package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.model.ObjectFactory
import java.util.regex.Pattern
import javax.inject.Inject

@Suppress("unused") // Public API for Gradle build scripts.
abstract class AboutLibrariesExtension @Inject constructor(objectFactory: ObjectFactory) {

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
     * Additional license descriptors to include in the generated `aboutlibs.json` file.
     *
     * Useful in case e.g. there's a license only used in an explicitly-added library.
     *
     * ```
     * aboutLibraries {
     *   additionalLicenses {
     *      mit
     *      mpl_2_0
     *   }
     * }
     * ```
     *
     * This API requires spdxId's to be provided. A full list is available here: https://spdx.org/licenses/
     */
    var additionalLicenses: NamedDomainObjectCollection<AboutLibrariesLicenseExtension>

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
    var allowedLicenses: List<String> = emptyList()

    /**
     * Enable fetching of remote licenses.
     * This will use the GitHub license API to fetch the defined library as specified in the projects repository.
     *
     * ```
     * aboutLibraries {
     *   fetchRemoteLicense = true
     * }
     * ```
     */
    var fetchRemoteLicense: Boolean? = null

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

    init {
        additionalLicenses = objectFactory.domainObjectContainer(AboutLibrariesLicenseExtension::class.java)
    }


    enum class StrictMode {
        FAIL,
        WARN,
        IGNORE,
    }
}
