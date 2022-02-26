package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.api.Api
import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import com.mikepenz.aboutlibraries.plugin.model.ResultContainer
import com.mikepenz.aboutlibraries.plugin.util.LicenseUtil.loadSpdxLicense
import com.mikepenz.aboutlibraries.plugin.util.PomLoader.resolvePomFile
import com.mikepenz.aboutlibraries.plugin.util.parser.LibraryReader
import com.mikepenz.aboutlibraries.plugin.util.parser.LicenseReader
import com.mikepenz.aboutlibraries.plugin.util.parser.PomReader
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.regex.Pattern

class LibrariesProcessor(
    private val dependencyHandler: DependencyHandler,
    private val collectedDependencies: CollectedContainer,
    private val configFolder: File?,
    private val exclusionPatterns: List<Pattern>,
    private val offlineMode: Boolean,
    private val fetchRemoteLicense: Boolean,
    private val fetchRemoteFunding: Boolean,
    private val additionalLicenses: HashSet<String>,
    private val duplicationMode: DuplicateMode,
    private val duplicationRule: DuplicateRule,
    private var variant: String? = null,
    gitHubToken: String? = null,
) {
    private val handledLibraries = HashSet<String>()

    private val api = Api.create(offlineMode, gitHubToken)

    fun gatherDependencies(): ResultContainer {
        val collectedDependencies = collectedDependencies.dependenciesForVariant(variant)
        LOGGER.info("All dependencies.size = ${collectedDependencies.size}")

        val librariesList = ArrayList<Library>()
        val licensesMap = HashMap<String, License>()
        for (dependency in collectedDependencies) {
            val groupArtifact = dependency.key.split(":")
            val version = dependency.value.first()
            val versionIdentifier = DefaultModuleVersionIdentifier.newId(groupArtifact[0], groupArtifact[1], version)
            val file = dependencyHandler.resolvePomFile(groupArtifact[0], versionIdentifier, false)
            if (file != null) {
                try {
                    parseDependency(file)?.let {
                        val (lib, licenses) = it
                        librariesList.add(lib)
                        licenses.forEach { lic ->
                            licensesMap[lic.hash] = lic
                        }
                    }
                } catch (ex: Throwable) {
                    LOGGER.error("--> Failed to write dependency information for: $groupArtifact", ex)
                }
            }
        }

        if (configFolder != null) {
            LicenseReader.readLicenses(configFolder).forEach { lic ->
                if (licensesMap.containsKey(lic.hash)) {
                    licensesMap[lic.hash]?.also { orgLic -> orgLic.merge(lic) }
                } else {
                    licensesMap[lic.hash] = lic
                }
            }

            LibraryReader.readLibraries(configFolder).takeIf { it.isNotEmpty() }?.also { customLibs ->
                val librariesMap = librariesList.associateBy { it.uniqueId }
                customLibs.forEach { lib ->
                    /**
                     * Merges this [Library] with the provided other [Library]
                     */
                    fun Library.mergeWithCustom() {
                        this.merge(lib)
                        // make sure we fetch any additionally needed licenses
                        this.licenses.forEach {
                            if (!licensesMap.containsKey(it)) {
                                additionalLicenses.add(it)
                            }
                        }
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
                            librariesList.add(lib)
                            // make sure we fetch any additionally needed licenses
                            lib.licenses.forEach {
                                if (!licensesMap.containsKey(it)) {
                                    additionalLicenses.add(it)
                                }
                            }
                        }
                    }
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
                    it.loadSpdxLicense()
                } else {
                    LOGGER.warn("--> `${it.name}` does not contain the license text and configuration is in OFFLINE MODE. Please provide manually with `name`: `${it.name}` and `hash`: `${it.hash}`")
                }
            }
        }

        return ResultContainer(librariesList.processDuplicates(duplicationMode, duplicationRule), licensesMap)
    }

    private fun parseDependency(artifactFile: File): Pair<Library, Set<License>>? {
        var artifactPomText = artifactFile.readText().trim()
        if (artifactPomText[0] != '<') {
            LOGGER.warn("--> ${artifactFile.path} contains a invalid character at the first position. Applying workaround.")
            artifactPomText = artifactPomText.substring(artifactPomText.indexOf('<'))
        }

        val pomReader = PomReader(artifactFile.inputStream())
        val uniqueId = pomReader.groupId + ":" + pomReader.artifactId

        for (pattern in exclusionPatterns) {
            if (pattern.matcher(uniqueId).matches()) {
                println("--> Skipping ${uniqueId}, matching exclusion pattern")
                return null
            }
        }

        LOGGER.debug("--> ArtifactPom for [{}:{}]:\n{}\n\n", pomReader.groupId, pomReader.artifactId, artifactPomText)

        // check if we shall skip this specific uniqueId
        if (shouldSkip(uniqueId)) {
            return null
        }

        // remember that we handled the library
        handledLibraries.add(uniqueId)

        // we also want to check if there are parent POMs with additional information
        var parentPomReader: PomReader? = null
        if (pomReader.hasParent()) {
            val parentGroupId = pomReader.parentGroupId
            val parentArtifactId = pomReader.parentArtifactId
            val parentVersion = pomReader.parentVersion

            if (parentGroupId != null && parentArtifactId != null && parentVersion != null) {
                val parentPomFile = dependencyHandler.resolvePomFile(
                    uniqueId,
                    DefaultModuleVersionIdentifier.newId(parentGroupId, parentArtifactId, parentVersion),
                    true
                )
                if (parentPomFile != null) {
                    val parentPomText = parentPomFile.readText()
                    LOGGER.debug("--> ArtifactPom ParentPom for [{}:{}]:\n{}\n\n", pomReader.groupId, pomReader.artifactId, parentPomText)
                    parentPomReader = PomReader(parentPomFile.inputStream())
                } else {
                    LOGGER.warn(
                        "--> ArtifactPom reports ParentPom for [{}:{}] but couldn't resolve it",
                        pomReader.groupId,
                        pomReader.artifactId
                    )
                }
            } else {
                LOGGER.info("--> Has parent pom, but misses info [{}:{}:{}]", parentGroupId, parentArtifactId, parentVersion)
            }
        } else {
            LOGGER.debug("--> No Artifact Parent Pom found for [{}:{}]", pomReader.groupId, pomReader.artifactId)
        }

        // get the url for the author
        var libraryName = fixLibraryName(uniqueId, chooseValue(uniqueId, "name", pomReader.name) { parentPomReader?.name } ?: "") // get name of the library
        val libraryDescription =
            fixLibraryDescription(chooseValue(uniqueId, "description", pomReader.description) { parentPomReader?.description } ?: "")

        val artifactVersion = chooseValue(uniqueId, "version", pomReader.version) { parentPomReader?.version } // get the version of the library
        if (artifactVersion.isNullOrBlank()) {
            LOGGER.info("----> Failed to identify version for: $uniqueId")
        }
        val libraryWebsite = chooseValue(uniqueId, "homePage", pomReader.homePage) { parentPomReader?.homePage } // get the url to the library

        // the list of licenses a lib may have
        val licenses = (chooseValue(uniqueId, "licenses", pomReader.licenses) { parentPomReader?.licenses })?.map {
            License(it.name, it.url).also { lic ->
                lic.internalHash = lic.spdxId // in case this can be tracked back to a spdx id use according hash
            }
        }?.toHashSet() ?: hashSetOf()

        val scm = chooseValue(uniqueId, "scm", pomReader.scm) { parentPomReader?.scm }
        if (fetchRemoteLicense) {
            api.fetchRemoteLicense(uniqueId, scm, licenses)
        }

        val funding = mutableSetOf<Funding>()
        if (fetchRemoteFunding) {
            api.fetchFunding(uniqueId, scm, funding)
        }

        if (libraryName.isBlank()) {
            LOGGER.info("Could not get the name for ${uniqueId}! Fallback to '$uniqueId'")
            libraryName = uniqueId
        }

        val developers = chooseValue(uniqueId, "developers", pomReader.developers) { parentPomReader?.developers } ?: emptyList()
        val organization = chooseValue(uniqueId, "organization", pomReader.organization) { parentPomReader?.organization }

        val library = Library(
            uniqueId,
            artifactVersion,
            libraryName,
            libraryDescription,
            libraryWebsite,
            developers,
            organization,
            scm,
            licenses.map { it.hash }.toSet(),
            funding,
            null,
            artifactFile.parentFile?.parentFile // artifactFile references the pom directly
        )

        LOGGER.debug("Adding library: {}", library)
        return library to licenses
    }

    /**
     * Ensures and applies fixes to the library names (shorten, ...)
     */
    private fun fixLibraryName(uniqueId: String, value: String): String {
        return (if (value.startsWith("Android Support Library")) {
            value.replace("Android Support Library", "Support")
        } else if (value.startsWith("Android Support")) {
            value.replace("Android Support", "Support")
        } else if (value.startsWith("org.jetbrains.kotlin:")) {
            value.replace("org.jetbrains.kotlin:", "")
        } else {
            value
        }).trimIndent()
    }

    /**
     * Ensures and applies fixes to the library descriptions (remove 'null', ...)
     */
    private fun fixLibraryDescription(value: String): String {
        return value.takeIf { it != "null" }?.trimIndent() ?: ""
    }

    /**
     * Skip libraries which have a core dependency and we don't want it to show up more than necessary
     */
    private fun shouldSkip(uniqueId: String): Boolean {
        return handledLibraries.contains(uniqueId) || uniqueId == "com.mikepenz:aboutlibraries" || uniqueId == "com.mikepenz:aboutlibraries-definitions"
    }

    companion object {
        internal val LOGGER: Logger = LoggerFactory.getLogger(LibrariesProcessor::class.java)
    }
}
