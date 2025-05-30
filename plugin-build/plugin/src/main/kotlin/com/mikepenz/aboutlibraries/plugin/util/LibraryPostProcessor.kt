package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.api.Api
import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import com.mikepenz.aboutlibraries.plugin.model.ResultContainer
import com.mikepenz.aboutlibraries.plugin.util.LicenseUtil.loadSpdxLicense
import com.mikepenz.aboutlibraries.plugin.util.parser.FundingReader
import com.mikepenz.aboutlibraries.plugin.util.parser.LibraryReader
import com.mikepenz.aboutlibraries.plugin.util.parser.LicenseReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.regex.Pattern

internal class LibraryPostProcessor(
    private val variantToDependencyData: Map<String, List<DependencyData>>,
    private val configFolder: File?,
    private val exclusionPatterns: Set<Pattern>,
    private val offlineMode: Boolean,
    private val fetchRemoteLicense: Boolean,
    private val fetchRemoteFunding: Boolean,
    additionalLicenses: Set<String>,
    private val duplicationMode: DuplicateMode,
    private val duplicationRule: DuplicateRule,
    private var variant: String? = null,
    private val mapLicensesToSpdx: Boolean = true,
    gitHubToken: String? = null,
) {
    private val additionalLicenses: MutableSet<String> = additionalLicenses.toMutableSet()

    private val api = Api.create(offlineMode, gitHubToken)

    fun process(): ResultContainer {
        val librariesList = ArrayList<Library>()
        val licensesMap = sortedMapOf<String, License>(compareBy { it })

        val variant = variant
        val dependencyDataForVariant = if (variant.isNullOrBlank()) {
            variantToDependencyData.flatMap { (_, dependencies) -> dependencies }.deduplicateDependencies() ?: emptySet()
        } else {
            variantToDependencyData[variant] ?: variantToDependencyData.flatMap { (configName, dependencies) ->
                // if we don't have an exact match, use all variants starting with
                val cleanedConfigName = configName.removeSuffix("CompileClasspath").removeSuffix("RuntimeClasspath")
                if (cleanedConfigName == variant) dependencies else emptyList()
            }.deduplicateDependencies()
        }

        if (dependencyDataForVariant != null) {
            dependencyDataForVariant.onEach { dependencyData ->
                if (exclusionPatterns.isEmpty() || !exclusionPatterns.any { pattern -> pattern.matcher(dependencyData.uniqueId).matches() }) {
                    val licenses = dependencyData.licenses.map { lic ->
                        if (mapLicensesToSpdx) {
                            // in case this can be tracked back to a spdx id use according hash, doing so will lower the size of the output
                            lic.internalHash = lic.spdxId
                        }
                        lic
                    }.toHashSet()

                    if (fetchRemoteLicense) {
                        api.fetchRemoteLicense(dependencyData.uniqueId, dependencyData.scm, licenses, mapLicensesToSpdx)
                    }

                    val funding = mutableSetOf<Funding>()
                    if (fetchRemoteFunding) {
                        api.fetchFunding(dependencyData.uniqueId, dependencyData.scm, funding)
                    }

                    val library = Library(
                        dependencyData.uniqueId,
                        dependencyData.artifactVersion,
                        fixLibraryName(dependencyData.uniqueId, dependencyData.name),
                        fixLibraryDescription(dependencyData.description),
                        dependencyData.website,
                        dependencyData.developers,
                        dependencyData.organization,
                        dependencyData.scm,
                        licenses.map { it.hash }.toSet(),
                        funding,
                        null,
                        dependencyData.artifactFolder,
                    )

                    licensesMap.putAll(licenses.associateBy { it.hash })
                    librariesList.add(library)
                } else {
                    LOGGER.debug("Excluding library ${dependencyData.uniqueId} due to exclusion patterns")
                }
            }
        } else {
            LOGGER.warn("No dependencies found for variant: $variant")
        }

        if (configFolder != null) {
            LicenseReader.readLicenses(configFolder).forEach { lic ->
                if (licensesMap.containsKey(lic.hash)) {
                    licensesMap[lic.hash]?.also { orgLic -> orgLic.merge(lic) }
                } else {
                    licensesMap[lic.hash] = lic
                }
            }

            // helper map to efficiently access libraries
            val librariesMap = librariesList.associateBy { it.uniqueId }.toMutableMap()
            LibraryReader.readLibraries(configFolder).takeIf { it.isNotEmpty() }?.also { customLibs ->
                customLibs.forEach { lib ->
                    /** Make sure we fetch any additional needed licenses */
                    fun Library.handleLicenses() {
                        this.licenses.forEach {
                            if (!licensesMap.containsKey(it)) {
                                additionalLicenses.add(it)
                            }
                        }
                    }

                    /** Merges this [Library] with the provided other [Library] */
                    fun Library.mergeWithCustom() {
                        this.merge(lib)
                        this.handleLicenses()
                    }

                    if (lib.uniqueId.endsWith("::regex")) {
                        val matchRegex = lib.uniqueId.replace("::regex", "").toRegex()
                        val matchedLibraries = librariesMap.filterKeys {
                            it.contains(matchRegex)
                        }
                        matchedLibraries.values.forEach { it.mergeWithCustom() }
                    } else {
                        if (librariesMap.containsKey(lib.uniqueId)) {
                            librariesMap[lib.uniqueId]?.mergeWithCustom()
                        } else {
                            lib.handleLicenses()
                            librariesList.add(lib)
                            librariesMap[lib.uniqueId] = lib
                        }
                    }
                }
            }

            FundingReader.readFunding(configFolder).forEach { (uniqueId, fundingSet) ->
                val library = librariesMap[uniqueId]
                if (library != null) {
                    library.funding = library.funding + fundingSet
                } else {
                    LOGGER.debug("No library found for provided funding information of library: $uniqueId")
                }
            }
        }

        if (additionalLicenses.isNotEmpty()) {
            // Include additional licenses explicitly requested.
            additionalLicenses.forEach { al ->
                val foundLicense = SpdxLicense.find(al)
                if (foundLicense != null && !licensesMap.containsKey(foundLicense.id)) {
                    licensesMap[foundLicense.id] = License(
                        foundLicense.fullName,
                        foundLicense.getUrl(),
                        null
                    )
                }
            }
        }

        // Download content for all licenses missing the content
        licensesMap.values.forEach {
            if (it.content.isNullOrBlank()) {
                if (!offlineMode) {
                    it.loadSpdxLicense(mapLicensesToSpdx)
                } else {
                    LOGGER.warn("--> `${it.name}` does not contain the license text and configuration is in OFFLINE MODE. Please provide manually with `name`: `${it.name}` and `hash`: `${it.hash}`")
                }
            }
        }

        return ResultContainer(
            librariesList.processDuplicates(duplicationMode, duplicationRule).sortedBy { it.uniqueId },
            licensesMap
        )
    }

    /**
     * Ensures and applies fixes to the library names (shorten, ...)
     */
    private fun fixLibraryName(uniqueId: String, value: String?): String {
        value ?: return ""
        return (if (value.startsWith("Android Support Library")) {
            value.replace("Android Support Library", "Support")
        } else if (value.startsWith("Android Support")) {
            value.replace("Android Support", "Support")
        } else if (value.startsWith("org.jetbrains.kotlin:")) {
            value.replace("org.jetbrains.kotlin:", "")
        } else if (value == "\${project.groupId}:\${project.artifactId}") {
            uniqueId
        } else {
            value
        }).trimIndent()
    }

    /**
     * Ensures and applies fixes to the library descriptions (remove 'null', ...)
     */
    private fun fixLibraryDescription(value: String?): String {
        return value?.takeIf { it != "null" }?.trimIndent() ?: ""
    }

    private fun List<DependencyData>.deduplicateDependencies() = groupBy {
        it.uniqueId
    }.map { (uniqueId, value) ->
        if (LOGGER.isInfoEnabled) LOGGER.info("Found multiple entries for $uniqueId, using first")
        if (LOGGER.isDebugEnabled) LOGGER.info("   Duplicates: ${value.joinToString(", ") { "${it.uniqueId}:${it.artifactVersion}" }}")
        value.first()
    }.takeIf {
        it.isNotEmpty()
    }?.toSet()

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(LibraryPostProcessor::class.java)
    }
}