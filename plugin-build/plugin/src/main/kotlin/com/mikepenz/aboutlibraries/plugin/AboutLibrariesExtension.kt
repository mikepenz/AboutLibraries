package com.mikepenz.aboutlibraries.plugin

import java.util.regex.Pattern

@Suppress("unused") // Public API for Gradle build scripts.
abstract class AboutLibrariesExtension {

    /**
     * Adjusts the output file name for the generated meta data file.
     * Adjusting the file name will break the automatic discovery for supported platforms.
     * Ensure to use the respective APIs of the core module.
     *
     * ```
     * aboutLibraries {
     *   outputFileName = "aboutlibraries.json"
     * }
     * ```
     */
    var outputFileName: String = "aboutlibraries.json"

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
    var offlineMode: Boolean = false

    /**
     * Configures the creation and registration of the Android related tasks. Will automatically hook into the build process and create the `aboutlibraries.json` during build time.
     * If disabled use `exportLibraryDefinitions` manually to create the `.json` output.
     *
     * ```
     * aboutLibraries {
     *   registerAndroidTasks = true
     * }
     * ```
     *
     * For Android projects `./gradlew app:exportLibraryDefinitions -PaboutLibraries.exportPath=src/main/res/raw` leads to a similar manual output.
     * The resulting file can for example be added as part of the SCM.
     */
    var registerAndroidTasks: Boolean = true

    /**
     * The path to your directory containing additional libraries, and licenses to include in the generated data.
     * These can also be used to update library data, as identified by the `uniqueId`
     *
     * ```
     * aboutLibraries {
     *   configPath = "config"
     * }
     * ```
     */
    var configPath: String? = null

    /**
     * A list of patterns (matching on the library `uniqueId` ($groupId:$artifactId)) to exclude libraries.
     *
     * ```
     * aboutLibraries {
     *      exclusionPatterns = [
     *          ~"com\.company\..*"
     *      ]
     * }
     * ```
     */
    var exclusionPatterns: List<Pattern> = emptyList()

    /**
     * Enable the inclusion of platform dependencies in the report.
     * By default `platform` level `bom` specifications will be included in the report.
     *
     * > Gradle provides support for importing bill of materials (BOM) files, which are effectively .pom files that use <dependencyManagement> to control the dependency versions of direct and transitive dependencies. The BOM support in Gradle works similar to using <scope>import</scope> when depending on a BOM in Maven.
     *
     * ```
     * aboutLibraries {
     *      includePlatform = false
     * }
     * ```
     */
    var includePlatform: Boolean = true

    /**
     * Additional license descriptors to include in the generated `aboutlibs.json` file.
     *
     * Useful in case e.g. there's a license only used in an explicitly-added library.
     *
     * ```
     * aboutLibraries {
     *   additionalLicenses = arrayOf("mit", "mpl_2_0")
     * }
     * ```
     *
     * This API requires spdxId's to be provided. A full list is available here: https://spdx.org/licenses/
     */
    var additionalLicenses: Array<String> = emptyArray()

    /**
     * Enables an exceptional strictMode which will either log or crash the build in case non allowed licenses are detected.
     *
     * ```
     * aboutLibraries {
     *   strictMode = StrictMode.FAIL
     * }
     * ```
     */
    var strictMode = StrictMode.IGNORE

    /**
     * Defines the allowed licenses which will not result in warnings or failures depending on the [strictMode] configuration.
     *
     * ```
     * aboutLibraries {
     *   allowedLicenses = arrayOf("Apache-2.0", "mit")
     * }
     * ```
     *
     * This API requires spdxId's to be provided. A full list is available here: https://spdx.org/licenses/
     */
    var allowedLicenses: Array<String> = emptyArray()

    /**
     * Defines the allowed licenses for specific libraries which will not result in warnings or failures depending on the [strictMode] configuration.
     * This is useful if some dependencies have special licenses which are only used in testing and are accepted for this case.
     *
     * ```
     * aboutLibraries {
     *   allowedLicensesMap = mapOf("Apache-2.0" to arrayOf("libraryId"))
     * }
     * ```
     *
     * This API requires spdxId's to be provided. A full list is available here: https://spdx.org/licenses/
     */
    var allowedLicensesMap: Map<String, List<String>> = emptyMap()

    /**
     * Defines the plugins behavior in case of duplicates.
     * By default duplicates are kept, no duplicate discovery enabled.
     * Please check [duplicationRule] on the discovery rule.
     *
     * - [DuplicateMode.KEEP]
     * - [DuplicateMode.LINK]
     * - [DuplicateMode.MERGE]
     *
     * ```
     * aboutLibraries {
     *   duplicationRule = DuplicateMode.KEEP
     * }
     * ```
     *
     * @see duplicationRule
     */
    var duplicationMode = DuplicateMode.KEEP

    /**
     * Specifies which approach the plugin takes on detecting duplicates.
     *
     * - [DuplicateRule.EXACT]
     * - [DuplicateRule.SIMPLE]
     *
     * Please check [duplicationMode] on the mode for handling of duplicates.
     *
     * ```
     * aboutLibraries {
     *   duplicationRule = DuplicateRule.SIMPLE
     * }
     * ```
     *
     * @see duplicationMode
     */
    var duplicationRule = DuplicateRule.SIMPLE

    /**
     * Enable fetching of remote licenses.
     *
     * This will use the API for (supported) repository source hosts to fetch the source license information.
     *
     * Find special source hosts supported for this here: https://github.com/mikepenz/AboutLibraries#special-repository-support
     *
     * ```
     * aboutLibraries {
     *   fetchRemoteLicense = false
     * }
     * ```
     */
    var fetchRemoteLicense: Boolean = false

    /**
     * Enable fetching of remote funding information.
     * This will use the API for (supported) repository source hosts to fetch the funding information via the API.
     *
     * Find special source hosts supported for this here: https://github.com/mikepenz/AboutLibraries#special-repository-support
     *
     * ```
     * aboutLibraries {
     *   fetchRemoteFunding = false
     * }
     * ```
     */
    var fetchRemoteFunding: Boolean = false

    /**
     * An optional GitHub API token used to access the `license` endpoint provided by GitHub
     * - https://api.github.com/repos/mikepenz/AboutLibraries/license
     *
     * ```
     * aboutLibraries {
     *   gitHubApiToken = getLocalOrGlobalProperty("github.pat")
     * }
     * ```
     */
    var gitHubApiToken: String? = null

    /**
     * Defines fields which will be excluded during the serialisation of the metadata output file.
     *
     * Any field as included in the [com.mikepenz.aboutlibraries.plugin.mapping.Library] can theoretically be excluded.
     * ```
     * aboutLibraries {
     *   excludeFields = arrayOf("description", "tag")
     * }
     * ```
     */
    var excludeFields: Array<String> = emptyArray()

    /**
     * Enable pretty printing for the generated JSON metadata.
     *
     * ```
     * aboutLibraries {
     *   prettyPrint = true
     * }
     * ```
     */
    var prettyPrint: Boolean = false

    /**
     * Defines the variants to keep during the "collectDependencies" step.
     *
     * ```
     * aboutLibraries {
     *   filterVariants = arrayOf("debug")
     * }
     * ```
     */
    var filterVariants: Array<String> = emptyArray()
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