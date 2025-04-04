package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion
import org.slf4j.LoggerFactory

@Suppress("unused") // Public API for Gradle build scripts.
class AboutLibrariesPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (GradleVersion.current() < GradleVersion.version("7.0")) {
            project.logger.error("Gradle 7.0 or greater is required to apply this plugin.")
            return
        }

        // create the extension for the about libraries plugin
        val extension = project.extensions.create("aboutLibraries", AboutLibrariesExtension::class.java)
        extension.applyConvention()

        // task to output funding options for included libraries
        project.tasks.register("fundLibraries", AboutLibrariesFundingTask::class.java) {
            it.configure()
        }

        // task to output library names with ids for further actions
        project.tasks.register("findLibraries", AboutLibrariesIdTask::class.java) {
            it.configure()
        }

        // task to output libraries, and their license in CSV format to the CLI
        project.tasks.register("exportLibraries", AboutLibrariesExportTask::class.java) {
            it.configure()
        }

        // register a global task to generate library definitions
        project.tasks.register("exportLibraryDefinitions", AboutLibrariesTask::class.java) {
            it.configureOutputFile()
            it.configure()
        }

        if (extension.android.registerAndroidTasks.getOrElse(true)) {
            AboutLibrariesPluginAndroidExtension.apply(project, extension)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesPlugin::class.java)
    }
}