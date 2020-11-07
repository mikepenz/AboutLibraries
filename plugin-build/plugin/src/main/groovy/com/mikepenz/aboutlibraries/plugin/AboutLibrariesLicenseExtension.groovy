package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Named

class AboutLibrariesLicenseExtension implements Named {

    final String name

    AboutLibrariesLicenseExtension(String name) {
        this.name = name
    }

    /**
     * The object's name.
     * <p>
     * Must be constant for the life of the object.
     *
     * @return The name. Never null.
     */
    @Override
    String getName() {
        return name
    }
}