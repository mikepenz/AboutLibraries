package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Named

abstract class AboutLibrariesLicenseExtension(name: String) : Named {
    private val _name = name

    /**
     * The object's name.
     * <p>
     * Must be constant for the life of the object.
     *
     * @return The name. Never null.
     */
    override fun getName(): String {
        return _name
    }
}