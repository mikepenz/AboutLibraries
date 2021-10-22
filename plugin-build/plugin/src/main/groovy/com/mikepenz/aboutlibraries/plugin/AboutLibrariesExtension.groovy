package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject
import java.util.regex.Pattern

class AboutLibrariesExtension {

    @Inject
    public AboutLibrariesExtension(ObjectFactory objectFactory) {
        additionalLicenses = objectFactory.domainObjectContainer(AboutLibrariesLicenseExtension.class)
    }

    /**
     * The path to your directory containing {@code custom_*.prop} files.
     */
    String configPath

    /**
     * Fetch remote licenses
     *
     * Will try to fetch the LICENSE from the defined SCM if project is open source
     */
    boolean fetchRemoteLicense

    /**
     * Include all licenses
     *
     * Useful if you want to include all licenses available even if they weren't detected by this plugin
     */
    boolean includeAllLicenses

    /**
     * A list of patterns (matching on the library id) to exclude libraries.
     * This is helpful to exclude internal libraries or submodules.
     */
    List<Pattern> exclusionPatterns

    /**
     * Additional license names you want to include.
     *
     * Useful in case e.g. there's a license only used in an explicitly-added library.
     */
    NamedDomainObjectCollection<AboutLibrariesLicenseExtension> additionalLicenses
}
