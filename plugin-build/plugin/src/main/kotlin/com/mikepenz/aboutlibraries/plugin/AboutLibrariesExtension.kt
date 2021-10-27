package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.model.ObjectFactory
import java.util.regex.Pattern
import javax.inject.Inject

abstract class AboutLibrariesExtension @Inject constructor(objectFactory: ObjectFactory) {

    /**
     * The path to your directory containing {@code custom_*.prop} files.
     */
    var configPath: String? = null

    /**
     * Fetch remote licenses
     *
     * Will try to fetch the LICENSE from the defined SCM if project is open source
     */
    var fetchRemoteLicense: Boolean? = null

    /**
     * Include all licenses
     *
     * Useful if you want to include all licenses available even if they weren't detected by this plugin
     */
    var includeAllLicenses: Boolean? = null

    /**
     * A list of patterns (matching on the library id) to exclude libraries.
     * This is helpful to exclude internal libraries or submodules.
     */
    var exclusionPatterns: List<Pattern> = emptyList()

    /**
     * Additional license names you want to include.
     *
     * Useful in case e.g. there's a license only used in an explicitly-added library.
     */
    var additionalLicenses: NamedDomainObjectCollection<AboutLibrariesLicenseExtension>

    /**
     * An optional GitHub API token used to access the `license` endpoint provided by GitHub
     * - https://api.github.com/repos/mikepenz/AboutLibraries/license
     */
    var gitHubApiToken: String? = null

    init {
        additionalLicenses = objectFactory.domainObjectContainer(AboutLibrariesLicenseExtension::class.java)
    }
}
