package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion
import org.slf4j.LoggerFactory

@Suppress("unused") // Public API for Gradle build scripts.
class AboutLibrariesPluginAndroid : Plugin<Project> {

    override fun apply(project: Project) {
        if (GradleVersion.current() < GradleVersion.version("8.8")) {
            project.logger.error("Gradle 8.8 or greater is required to apply this plugin.")
            return
        }

        try {
            Class.forName("com.android.build.api.variant.AndroidComponentsExtension")
        } catch (t: Throwable) {
            project.logger.error("Android Gradle Plugin 7.0.0 or greater is required to apply this plugin.")
            return
        }

        // create the extension for the about libraries plugin
        val extension = project.extensions.findByType(AboutLibrariesExtension::class.java) ?: project.extensions.create("aboutLibraries", AboutLibrariesExtension::class.java)
        extension.applyConvention()

        LOGGER.debug("Enabled Android task registration")
        configureAndroidTasks(project, extension)
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(AboutLibrariesPluginAndroid::class.java)
    }
}
