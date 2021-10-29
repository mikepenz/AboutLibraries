package com.mikepenz.aboutlibraries.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
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

        // task to output library names with ids for further actions
        val collectTask = project.tasks.register("collectDependencies", AboutLibrariesCollectorTask::class.java) {
            it.description = "Collects dependencies to be used by the different AboutLibraries tasks"
            it.configure()
        }

        try {
            val app = project.extensions.findByType(AppExtension::class.java)
            if (app != null) {
                app.applicationVariants.all {
                    createAboutLibrariesAndroidTasks(project, it, collectTask)
                }
            } else {
                val lib = project.extensions.findByType(LibraryExtension::class.java)
                lib?.libraryVariants?.all {
                    createAboutLibrariesAndroidTasks(project, it, collectTask)
                }
            }
        } catch (t: Throwable) {
            LOGGER.info("Couldn't register Android related plugin tasks")
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
    }

    @Suppress("DEPRECATION")
    private fun createAboutLibrariesAndroidTasks(project: Project, variant: BaseVariant, collectTask: TaskProvider<*>) {
        // task to write the general definitions information
        val task = project.tasks.create("prepareLibraryDefinitions${variant.name.capitalize(Locale.ENGLISH)}", AboutLibrariesTask::class.java) {
            it.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
            it.group = "Build"
            it.dependencies = project.file("${project.buildDir}/generated/aboutlibraries/${variant.name}/res/")
            it.variant = variant.name
            it.dependsOn(collectTask)
        }


        // This is necessary for backwards compatibility with versions of gradle that do not support
        // this new API.
        try {
            variant.registerGeneratedResFolders(project.files(task.dependencies).builtBy(task))
            try {
                variant.mergeResourcesProvider.configure { it.dependsOn(task) }
            } catch (t: Throwable) {
                @Suppress("DEPRECATION")
                variant.mergeResources.dependsOn(task)
            }
        } catch (t: Throwable) {
            @Suppress("DEPRECATION")
            variant.registerResGeneratingTask(task, task.dependencies)
        }

        // task to generate libraries, and their license into the build folder (not hooked to the build task)
        project.tasks.register("generateLibraryDefinitions${variant.name.capitalize(Locale.ENGLISH)}", AboutLibrariesTask::class.java) {
            it.description = "Manually write meta data for the AboutLibraries plugin"
            it.group = "Build"
            it.dependencies = project.file("${project.buildDir}/generated/aboutlibraries/${variant.name}/res/")
            it.variant = variant.name
            it.dependsOn(collectTask)
        }

        // task to output libraries, and their license in CSV format to the CLI
        project.tasks.register("exportLibraries${variant.name.capitalize(Locale.ENGLISH)}", AboutLibrariesExportTask::class.java) {
            it.description = "Writes all libraries and their license in CSV format to the CLI"
            it.group = "Help"
            it.variant = variant.name
            it.dependsOn(collectTask)
        }

        // task to output libraries, their license in CSV format and source to a given location
        project.tasks.register("exportComplianceLibraries${variant.name.capitalize(Locale.ENGLISH)}", AboutLibrariesExportComplianceTask::class.java) {
            it.description = "Writes all libraries with their source and their license in CSV format to the configured directory"
            it.group = "Help"
            it.variant = variant.name
            it.dependsOn(collectTask)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesPlugin::class.java)
    }
}