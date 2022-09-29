package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.util.safeProp
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion
import org.slf4j.LoggerFactory
import java.util.*

@Suppress("unused") // Public API for Gradle build scripts.
class AboutLibrariesPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (GradleVersion.current() < GradleVersion.version("5.0")) {
            project.logger.error("Gradle 5.0 or greater is required to apply this plugin.")
            return
        }

        // create the config possible
        project.extensions.create("aboutLibraries", AboutLibrariesExtension::class.java)

        project.afterEvaluate {
            // task to output library names with ids for further actions
            val collectTask = project.tasks.register("collectDependencies", AboutLibrariesCollectorTask::class.java) {
                it.description = "Collects dependencies to be used by the different AboutLibraries tasks"
                if (project.experimentalCache) {
                    it.configure()
                }
            }

            // task to output funding options for included libraries
            project.tasks.register("fundLibraries", AboutLibrariesFundingTask::class.java) {
                it.description = "Outputs the funding options for used dependencies"
                it.group = "Help"
                it.dependsOn(collectTask)
            }

            // task to output library names with ids for further actions
            project.tasks.register("findLibraries", AboutLibrariesIdTask::class.java) {
                it.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
                it.group = "Help"
                it.dependsOn(collectTask)
            }

            // task to output libraries, and their license in CSV format to the CLI
            project.tasks.register("exportLibraries", AboutLibrariesExportTask::class.java) {
                it.description = "Writes all libraries and their license in CSV format to the CLI"
                it.group = "Help"
                it.dependsOn(collectTask)
            }

            // register a global task to generate library definitions
            project.tasks.create("exportLibraryDefinitions", AboutLibrariesTask::class.java) {
                it.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
                it.group = "Build"
                it.variant = project.safeProp("aboutLibraries.exportVariant") ?: project.safeProp("exportVariant")
                it.resultDirectory = project.file(
                    project.safeProp("aboutLibraries.exportPath") ?: project.safeProp("exportPath")
                    ?: "${project.buildDir}/generated/aboutLibraries/"
                )
                it.dependsOn(collectTask)
            }

            val extension = project.extensions.getByName("aboutLibraries") as AboutLibrariesExtension
            if (extension.registerAndroidTasks) {
                AboutLibrariesPluginAndroidExtension.apply(project, collectTask)
            }
        }
    }

    private val Project.experimentalCache: Boolean
        get() = hasProperty("org.gradle.unsafe.configuration-cache") &&
                property("org.gradle.unsafe.configuration-cache") == "true"


    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesPlugin::class.java)
    }
}