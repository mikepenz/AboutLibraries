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
import java.util.Locale
import java.util.TreeSet

internal class LibraryPostProcessor(
    private val variantToDependencyData: Map<String, List<DependencyData>>,
    private val configFolder: File?,
    private val exclusionPatterns: Set<String>,
    private val includeLicenses: Set<String>,
    private val offlineMode: Boolean,
    private val fetchRemoteLicense: Boolean,
    private val fetchRemoteFunding: Boolean,
    additionalLicenses: Set<String>,
    private val duplicationMode: DuplicateMode,
    private val duplicationRule: DuplicateRule,
    private var variant: String? = null,
    private val mapLicensesToSpdx: Boolean = true,
    gitHubToken: String? = null,
    private val includeTargets: Boolean = false,
    private val configToTarget: Map<String, String> = emptyMap(),
) {
    private val additionalLicenses: MutableSet<String> = additionalLicenses.toMutableSet()

    private val api = Api.create(offlineMode, gitHubToken)

    fun process(): ResultContainer {
        val librariesList = ArrayList<Library>()
        val licensesMap = sortedMapOf<String, License>(compareBy { it })

        // Compile regex patterns once per process() call; patterns are stored as strings
        // (java.util.regex.Pattern is not config-cache serializable, so we use String inputs).
        // Wrap any compilation failure so the user sees which pattern is invalid.
        val compiledPatterns = exclusionPatterns.map { pattern ->
            try {
                pattern.toRegex()
            } catch (e: java.util.regex.PatternSyntaxException) {
                throw IllegalArgumentException(
                    "Invalid regex in aboutLibraries.library.exclusionPatterns: \"$pattern\" — ${e.description}",
                    e
                )
            }
        }

        val lowercaseIncludeLicenses = if (includeLicenses.isNotEmpty()) {
            includeLicenses.mapTo(HashSet(includeLicenses.size)) { it.lowercase(Locale.ENGLISH) }
        } else emptySet()

        val variant = variant

        // The configurations contributing to this task's output. Retained separately from the
        // flattened + deduplicated dependency list below, because deduplication collapses entries
        // by `uniqueId` and would otherwise discard which configuration each dependency came from.
        //
        // Ordered runtime classpaths first: when compile and runtime resolve different versions of the
        // same module, `deduplicateDependencies` keeps the first entry, and the runtime resolution is
        // the version that actually ships.
        val selectedConfigs: Map<String, List<DependencyData>> = when {
            variant.isNullOrBlank() -> variantToDependencyData
            variantToDependencyData.containsKey(variant) -> mapOf(variant to variantToDependencyData.getValue(variant))
            // if we don't have an exact match, use all variants starting with
            else -> variantToDependencyData.filterKeys { configName ->
                configName.removeSuffix("CompileClasspath").removeSuffix("RuntimeClasspath") == variant
            }
        }.toSortedMap(compareBy({ !it.endsWith("RuntimeClasspath") }, { it }))

        val dependencyDataForVariant: Collection<DependencyData>? = if (variant.isNullOrBlank()) {
            selectedConfigs.flatMap { (_, dependencies) -> dependencies }.deduplicateDependencies() ?: emptySet()
        } else {
            // deduplicate as well on an exact variant hit: with `mergePlatformArtifacts` several
            // resolved artifacts can collapse onto the same root uniqueId within one configuration.
            // `deduplicateDependencies()` returns null for an empty result, so an exact hit with no
            // dependencies must not fall through to the prefix-match branch below.
            variantToDependencyData[variant]?.let { it.deduplicateDependencies() ?: emptySet() }
                ?: selectedConfigs.flatMap { (_, dependencies) -> dependencies }.deduplicateDependencies()
        }

        // uniqueId -> Kotlin target names consuming it. Sorted so the generated output is
        // byte-for-byte stable regardless of configuration iteration order.
        val targetsByUniqueId: Map<String, Set<String>> = if (includeTargets) {
            buildMap<String, TreeSet<String>> {
                // longest first, so `iosX64` wins over a hypothetical `ios` when both are declared
                val knownTargets = configToTarget.values.distinct().sortedByDescending { it.length }
                selectedConfigs.forEach { (configName, dependencies) ->
                    val target = configToTarget[configName] ?: configName.toFallbackTargetName(knownTargets)
                    if (target == null) {
                        LOGGER.info("No Kotlin target owns configuration $configName, omitting it from `targets`")
                        return@forEach
                    }
                    dependencies.forEach { dependency ->
                        getOrPut(dependency.uniqueId) { sortedSetOf() }.add(target)
                    }
                }
            }
        } else emptyMap()

        if (dependencyDataForVariant != null) {
            dependencyDataForVariant.onEach { dependencyData ->
                if (compiledPatterns.isEmpty() || !compiledPatterns.any { pattern -> pattern.matches(dependencyData.uniqueId) }) {
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

                    // License inclusion filter: skip libraries whose licenses don't match.
                    if (lowercaseIncludeLicenses.isNotEmpty()) {
                        val hasMatch = licenses.any { lic ->
                            val id = lic.spdxId?.lowercase(Locale.ENGLISH)
                            val name = lic.name.lowercase(Locale.ENGLISH)
                            val url = lic.url?.lowercase(Locale.ENGLISH)
                            lowercaseIncludeLicenses.contains(id) ||
                                lowercaseIncludeLicenses.contains(name) ||
                                (!url.isNullOrEmpty() && lowercaseIncludeLicenses.contains(url))
                        }
                        if (!hasMatch) {
                            LOGGER.debug("Excluding library ${dependencyData.uniqueId} due to license inclusion filter")
                            return@onEach
                        }
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
                        if (includeTargets) targetsByUniqueId[dependencyData.uniqueId] ?: emptySet() else null,
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
                    // never let an override materialize `targets` while the feature is disabled
                    if (!includeTargets) lib.targets = null

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
                            // config-only library, not part of any resolved configuration
                            if (includeTargets && lib.targets == null) lib.targets = emptySet()
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
            librariesList.processDuplicates(duplicationMode, duplicationRule, rootIds()).sortedBy { it.uniqueId },
            licensesMap
        )
    }

    /**
     * `uniqueId` of a resolved artifact → `uniqueId` of the module it is a platform artifact of.
     *
     * Built from the Gradle `available-at` redirects recorded during dependency collection, so the
     * duplicate handling merges exactly the artifacts of one Kotlin Multiplatform publication
     * (`androidx.collection:collection-jvm` → `androidx.collection:collection`) and never two
     * sibling modules that merely share their POM `name` and `description`.
     *
     * Empty for non-multiplatform graphs. Identity entries (a redirect shell mapping to itself)
     * are harmless — they are what an unlisted artifact falls back to anyway.
     */
    private fun rootIds(): Map<String, String> = buildMap {
        variantToDependencyData.values.forEach { dependencies ->
            dependencies.forEach { dependency ->
                val coordinates = dependency.dependencyCoordinates
                val rootModule = coordinates.rootModule ?: return@forEach
                put(dependency.uniqueId, "${coordinates.group}:$rootModule")
            }
        }
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

    /**
     * Target name for a configuration no Kotlin *compilation* claims directly.
     *
     * Covers the source-set level configurations a multiplatform project adds alongside the
     * compilation ones (`jvmMainCompileClasspath` next to `jvmCompileClasspath`), by folding them
     * into the [knownTargets] entry they are named after.
     *
     * `null` for anything else. A configuration that matches no declared target is not evidence of
     * an undeclared one — an Android-only or `java-library` project builds a single implicit
     * target, and naming it after its build variant (`debug`, `release`) would emit a value no
     * consumer could ever match against. That is what `export.variant` is for.
     */
    private fun String.toFallbackTargetName(knownTargets: List<String>): String? {
        // matched case-insensitively: an unprefixed config is `compileClasspath`, a prefixed one
        // `jvmMainCompileClasspath` — the same casing rule the configuration selection applies
        val stripped = when {
            endsWith("CompileClasspath", true) -> dropLast("CompileClasspath".length)
            endsWith("RuntimeClasspath", true) -> dropLast("RuntimeClasspath".length)
            else -> this
        }
        return knownTargets.firstOrNull { stripped.startsWith(it) }
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