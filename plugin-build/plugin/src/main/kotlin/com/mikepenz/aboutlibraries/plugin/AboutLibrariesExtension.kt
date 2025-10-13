package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import java.util.regex.Pattern
import javax.inject.Inject

abstract class AboutLibrariesExtension {

    @get:Nested
    abstract val collect: CollectorConfig

    fun collect(action: Action<CollectorConfig>) {
        action.execute(collect)
    }

    @get:Nested
    abstract val export: ExportConfig

    fun export(action: Action<ExportConfig>) {
        action.execute(export)
    }

    @get:Nested
    abstract val exports: NamedDomainObjectContainer<ExportConfig>

    fun exports(action: Action<NamedDomainObjectContainer<ExportConfig>>) {
        action.execute(exports)
    }

    @get:Nested
    abstract val library: LibraryConfig

    fun library(action: Action<LibraryConfig>) {
        action.execute(library)
    }

    @get:Nested
    abstract val license: LicenseConfig

    fun license(action: Action<LicenseConfig>) {
        action.execute(license)
    }

    @Deprecated("Removed, no-op - Use the aboutlibsAndroidPlugin instead.")
    @get:Nested
    abstract val android: AndroidConfig

    @Deprecated("Removed, no-op - Use the aboutlibsAndroidPlugin instead.")
    fun android(action: Action<AndroidConfig>) {
        action.execute(android)
    }

    /**
     * Disables any remote checking of licenses.
     * Please note that this will also disable the download of the LICENSE text from `https://spdx.org/licenses/`.
     * It will be required to provide license content manually.
     *
     * ```
     * aboutLibraries {
     *   offlineMode = false
     * }
     * ```
     */
    @get:Optional
    abstract val offlineMode: Property<Boolean>

    /**
     * Helper API to apply the default convention for the extension.
     */
    fun applyConvention() {
        offlineMode.convention(false)
        collect {
            it.all.convention(false)
            it.includeTestVariants.convention(false)
            it.includePlatform.convention(true)
            it.fetchRemoteLicense.convention(false)
            it.fetchRemoteFunding.convention(false)
            it.filterVariants.convention(emptySet())
        }
        export {
            // it.variant.convention("") (mp) intentionally not set, we use it as `orNull` in places
            it.includeMetaData.convention(false)
            it.excludeFields.convention(emptySet())
            it.prettyPrint.convention(false)
            @Suppress("DEPRECATION")
            it.outputFileName.convention(DEFAULT_OUTPUT_NAME)
        }
        library {
            it.requireLicense.convention(false)
            it.exclusionPatterns.convention(emptySet())
            it.duplicationMode.convention(DuplicateMode.MERGE)
            it.duplicationRule.convention(DuplicateRule.EXACT)
        }
        license {
            it.mapLicensesToSpdx.convention(true)
            it.allowedLicenses.convention(emptySet())
            it.allowedLicensesMap.convention(emptyMap())
            it.additionalLicenses.convention(emptySet())
            it.strictMode.convention(StrictMode.IGNORE)
        }
        android {
            it.registerAndroidTasks.convention(true)
        }
    }

    companion object {
        private const val DEFAULT_OUTPUT_NAME = "aboutlibraries.json"
        internal const val PROP_PREFIX = "aboutLibraries."
        internal const val PROP_EXPORT_VARIANT = "exportVariant"
        internal const val PROP_EXPORT_PATH = "exportPath"
        internal const val PROP_EXPORT_ARTIFACT_GROUPS = "artifactGroups"

        @Deprecated("Use `PROP_EXPORT_OUTPUT_FILE` instead")
        internal const val PROP_EXPORT_OUTPUT_PATH = "outputPath"
        internal const val PROP_EXPORT_OUTPUT_FILE = "outputFile"
    }
}

@Deprecated("Removed, no-op - Use the aboutlibsAndroidPlugin instead.")
abstract class AndroidConfig @Inject constructor() {

    /**
     * Configures the creation and registration of the Android related tasks. Will automatically hook into the build process and create the `aboutlibraries.json` during build time.
     * If disabled use `exportLibraryDefinitions` manually to create the `.json` output.
     *
     * ```
     * aboutLibraries {
     *   android {
     *      registerAndroidTasks = true
     *   }
     * }
     * ```
     *
     * The resulting file can for example be added as part of the SCM.
     */
    @Deprecated("Removed, no-op - Use the aboutlibsAndroidPlugin instead.")
    @get:Optional
    abstract val registerAndroidTasks: Property<Boolean> // = true
}

abstract class CollectorConfig @Inject constructor() {

    /**
     * The path to your directory containing additional libraries, and licenses to include in the generated data.
     * These can also be used to update library data, as identified by the `uniqueId`.
     *
     * The path is relative to the module directory. (Not project root)
     *
     * ```
     * aboutLibraries {
     *   collect {
     *      configPath = "config"
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val configPath: DirectoryProperty

    /**
     * Enabling this will collect all configurations found in the project, skipping the usual `runtime` and `compile` filters.
     * This will still filter based on the provided filter, and also by default skip test configurations (this can be changed via `includeTestVariants = true`).
     *
     * ```
     * aboutLibraries {
     *   collect {
     *      all = false
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val all: Property<Boolean>

    /**
     * Enable the inclusion of test variants in the report.
     * By default `test` variants are excluded in the report.
     *
     * ```
     * aboutLibraries {
     *   collect {
     *      includeTestVariants = false
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val includeTestVariants: Property<Boolean>

    /**
     * Enable the inclusion of platform dependencies in the report.
     * By default `platform` level `bom` specifications will be included in the report.
     *
     * > Gradle provides support for importing bill of materials (BOM) files, which are effectively .pom files that use <dependencyManagement> to control the dependency versions of direct and transitive dependencies. The BOM support in Gradle works similar to using <scope>import</scope> when depending on a BOM in Maven.
     *
     * ```
     * aboutLibraries {
     *   collect {
     *      includePlatform = false
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val includePlatform: Property<Boolean>

    /**
     * Enable fetching of remote licenses.
     *
     * This will use the API for (supported) repository source hosts to fetch the source license information.
     *
     * Find special source hosts supported for this here: https://github.com/mikepenz/AboutLibraries#special-repository-support
     *
     * ```
     * aboutLibraries {
     *   collect {
     *      fetchRemoteLicense = false
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val fetchRemoteLicense: Property<Boolean>

    /**
     * Enable fetching of remote funding information.
     * This will use the API for (supported) repository source hosts to fetch the funding information via the API.
     *
     * Find special source hosts supported for this here: https://github.com/mikepenz/AboutLibraries#special-repository-support
     *
     * ```
     * aboutLibraries {
     *   collect {
     *      fetchRemoteFunding = false
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val fetchRemoteFunding: Property<Boolean>

    /**
     * An optional GitHub API token used to access the `license` endpoint provided by GitHub
     * - https://api.github.com/repos/mikepenz/AboutLibraries/license
     *
     * ```
     * aboutLibraries {
     *   collect {
     *      gitHubApiToken = property("github.pat")
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val gitHubApiToken: Property<String>

    /**
     * Defines the variants to keep during the "collectDependencies" step.
     *
     * ```
     * aboutLibraries {
     *   collect {
     *      filterVariants.addAll("debug")
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val filterVariants: SetProperty<String>
}

abstract class ExportConfig @Inject constructor(val name: String = "") {

    /**
     * The full path (file path, including file name) at which the generated metadata file will be stored.
     *
     * This path is relative to the modules project directory.
     *
     * This setting specifies both the path and file name for the generated metadata file.
     * Adjusting this will break the automatic discovery for supported platforms (Android).
     *
     * This can also be overwritten with the `-PaboutLibraries.outputFile` command line argument.
     *
     * ```
     * aboutLibraries {
     *   export {
     *      outputFile = "src/commonMain/composeResources/files/aboutlibraries.json"
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val outputFile: RegularFileProperty

    /** Alias for [outputFile] */
    @get:Optional
    val outputPath: RegularFileProperty get() = outputFile

    /**
     * The output file name for the generated meta data file.
     * Adjusting the file name will break the automatic discovery for supported platforms.
     *
     * Note: This API has no effect if `outputFile` is used. (unless the property is passed)
     * ```
     * aboutLibraries {
     *   export {
     *      outputFileName = "aboutlibraries.json"
     *   }
     * }
     * ```
     */
    @Deprecated("Use `outputFile` instead, which is the full path including file name")
    @get:Optional
    abstract val outputFileName: Property<String>

    /**
     * The default export variant to use for this module.
     * Can be overwritten with the `-PaboutLibraries.exportVariant` command line argument.
     *
     * ```
     * aboutLibraries {
     *   export {
     *      variant = "jvm"
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val variant: Property<String>

    /** Alias for [variant] */
    @get:Optional
    val exportVariant: Property<String> get() = variant

    /**
     * Enable the inclusion of generated MetaData.
     * Warning: This includes the generated date, making the build non-reproducible.
     *
     * ```
     * aboutLibraries {
     *   export {
     *      includeMetaData = true
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val includeMetaData: Property<Boolean>

    /**
     * Defines fields which will be excluded during the serialisation of the metadata output file.
     *
     * It is possible to qualify the field names by specifying the class name (e.g. "License.name").
     * Permissible qualifiers are "ResultContainer", "Library", "Developer", "Organization", "Funding", "Scm",
     * "License" and "MetaData".
     * Unqualified field names (e.g. "description") are applied to the entire output.
     *
     * ```
     * aboutLibraries {
     *   export {
     *      excludeFields.addAll"License.name", "ResultContainer.metadata", "description", "tag")
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val excludeFields: SetProperty<String>

    /**
     * Enable pretty printing for the generated JSON metadata.
     *
     * ```
     * aboutLibraries {
     *   export {
     *      prettyPrint = true
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val prettyPrint: Property<Boolean>
}

abstract class LibraryConfig @Inject constructor() {

    /**
     * Warn or fail the build if a library does not have license information attached.
     * The specific behavior depends on the `StrictMode` as configured in the license block.
     *
     * ```
     * aboutLibraries {
     *   library {
     *      requireLicense = true
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val requireLicense: Property<Boolean>

    /**
     * A list of patterns (matching on the library `uniqueId` ($groupId:$artifactId)) to exclude libraries.
     *
     * ```
     * aboutLibraries {
     *   library {
     *      exclusionPatterns.addAll(Pattern.compile("com\.company\..*")))
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val exclusionPatterns: SetProperty<Pattern>

    /**
     * Defines the plugins behavior in case of duplicates.
     * By default duplicates are merged.
     * Please check [duplicationRule] on the discovery rule.
     *
     * - [DuplicateMode.KEEP]
     * - [DuplicateMode.LINK]
     * - [DuplicateMode.MERGE]
     *
     * ```
     * aboutLibraries {
     *   library {
     *      duplicationRule = DuplicateMode.KEEP
     *   }
     * }
     * ```
     *
     * @see duplicationRule
     */
    @get:Optional
    abstract val duplicationMode: Property<DuplicateMode>

    /**
     * Specifies which approach the plugin takes on detecting duplicates.
     *
     * - [DuplicateRule.GROUP]
     * - [DuplicateRule.EXACT]
     * - [DuplicateRule.SIMPLE]
     *
     * Please check [duplicationMode] on the mode for handling of duplicates.
     *
     * ```
     * aboutLibraries {
     *   library {
     *      duplicationRule = DuplicateRule.EXACT
     *   }
     * }
     * ```
     *
     * @see duplicationMode
     */
    @get:Optional
    abstract val duplicationRule: Property<DuplicateRule>
}

abstract class LicenseConfig @Inject constructor() {

    /**
     * Defines if licenses are mapped to SPDX identifiers.
     * This lowers the meta data file size, however in case of modified license content, might loose the original license information - and instead use standard SPDX information, based on SPDX license id defined.
     *
     * ```
     * aboutLibraries {
     *   license {
     *      mapLicensesToSpdx = false
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val mapLicensesToSpdx: Property<Boolean>

    /**
     * Defines the allowed licenses which will not result in warnings or failures depending on the [strictMode] configuration.
     *
     * ```
     * aboutLibraries {
     *   license {
     *      allowedLicenses.addAll("Apache-2.0", "mit")
     *   }
     * }
     * ```
     *
     * This API requires spdxId's to be provided. A full list is available here: https://spdx.org/licenses/
     */
    @get:Optional
    abstract val allowedLicenses: SetProperty<String>

    /**
     * Defines the allowed licenses for specific libraries which will not result in warnings or failures depending on the [strictMode] configuration.
     * This is useful if some dependencies have special licenses which are only used in testing and are accepted for this case.
     *
     * ```
     * aboutLibraries {
     *   license {
     *      allowedLicensesMap = mapOf("Apache-2.0" to arrayOf("libraryId"))
     *   }
     * }
     * ```
     *
     * This API requires spdxId's to be provided. A full list is available here: https://spdx.org/licenses/
     */
    @get:Optional
    abstract val allowedLicensesMap: MapProperty<String, List<String>>

    /**
     * Additional license descriptors to include in the generated `aboutlibs.json` file.
     *
     * Useful in case e.g. there's a license only used in an explicitly-added library.
     *
     * ```
     * aboutLibraries {
     *   license {
     *      additionalLicenses.addAll("mit", "mpl_2_0")
     *   }
     * }
     * ```
     *
     * This API requires spdxId's to be provided. A full list is available here: https://spdx.org/licenses/
     */
    @get:Optional
    abstract val additionalLicenses: SetProperty<String>

    /**
     * Enables an exceptional strictMode which will either log or crash the build in case non allowed licenses are detected.
     *
     * ```
     * aboutLibraries {
     *   license {
     *      strictMode = StrictMode.FAIL
     *   }
     * }
     * ```
     */
    @get:Optional
    abstract val strictMode: Property<StrictMode>
}


enum class StrictMode {
    /** fails the build if a non allowed license is found */
    FAIL,

    /** writes a warning message to the log */
    WARN,

    /** no action */
    IGNORE,
}

enum class DuplicateRule {
    /** groupId and license are equal */
    GROUP,

    /** groupId and title are equal */
    SIMPLE,

    /** groupId, title and description are equal */
    EXACT,
}

enum class DuplicateMode {
    /** no action, no duplicate checking */
    KEEP,

    /** duplicates are being merged */
    MERGE,

    /** duplicates get linked to each other */
    LINK,
}