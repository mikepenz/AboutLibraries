package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Android specific extension for the [AboutLibrariesPlugin]
 */
object AboutLibrariesPluginAndroidExtension {
    private val LOGGER = LoggerFactory.getLogger(AboutLibrariesPluginAndroidExtension::class.java)

    fun apply(project: Project, collectTask: TaskProvider<AboutLibrariesCollectorTask>) {
        try {
            val app = project.extensions.findByType(com.android.build.gradle.AppExtension::class.java)
            if (app != null) {
                app.applicationVariants.all {
                    createAboutLibrariesAndroidTasks(project, it, collectTask)
                }
            } else {
                val lib = project.extensions.findByType(com.android.build.gradle.LibraryExtension::class.java)
                lib?.libraryVariants?.all {
                    createAboutLibrariesAndroidTasks(project, it, collectTask)
                }
            }
        } catch (t: Throwable) {
            LOGGER.warn("Couldn't register Android related plugin tasks")
        }
    }

    @Suppress("DEPRECATION")
    private fun createAboutLibrariesAndroidTasks(project: Project, v: Any, collectTask: TaskProvider<*>) {
        val variant = (v as? com.android.build.gradle.api.BaseVariant) ?: return
        // task to write the general definitions information
        val task = project.tasks.create(
            "prepareLibraryDefinitions${variant.name.capitalize(Locale.ENGLISH)}",
            AboutLibrariesTask::class.java
        ) {
            it.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
            it.group = "Build"
            it.variant = variant.name
            it.resultDirectory = project.file("${project.buildDir}/generated/aboutLibraries/${variant.name}/res/raw/")
            it.dependsOn(collectTask)
        }

        // This is necessary for backwards compatibility with versions of gradle that do not support
        // this new API.
        try {
            variant.registerGeneratedResFolders(project.files(task.resultDirectory.parentFile).builtBy(task))
            try {
                variant.mergeResourcesProvider.configure { it.dependsOn(task) }
            } catch (t: Throwable) {
                @Suppress("DEPRECATION")
                variant.mergeResources.dependsOn(task)
            }
        } catch (t: Throwable) {
            @Suppress("DEPRECATION")
            variant.registerResGeneratingTask(task, task.resultDirectory.parentFile)
        }

        // task to generate libraries, and their license into the build folder (not hooked to the build task)
        project.tasks.register(
            "generateLibraryDefinitions${variant.name.capitalize(Locale.ENGLISH)}",
            AboutLibrariesTask::class.java
        ) {
            it.description = "Manually write meta data for the AboutLibraries plugin"
            it.group = "Build"
            it.variant = variant.name
            it.resultDirectory = project.file("${project.buildDir}/generated/aboutLibraries/${variant.name}/res/raw/")
            it.dependsOn(collectTask)
        }

        // task to output libraries, and their license in CSV format to the CLI
        project.tasks.register(
            "exportLibraries${variant.name.capitalize(Locale.ENGLISH)}",
            AboutLibrariesExportTask::class.java
        ) {
            it.description = "Writes all libraries and their license in CSV format to the CLI"
            it.group = "Help"
            it.variant = variant.name
            it.dependsOn(collectTask)
        }

        // task to output libraries, their license in CSV format and source to a given location
        project.tasks.register(
            "exportComplianceLibraries${variant.name.capitalize(Locale.ENGLISH)}",
            AboutLibrariesExportComplianceTask::class.java
        ) {
            it.description =
                "Writes all libraries with their source and their license in CSV format to the configured directory"
            it.group = "Help"
            it.variant = variant.name
            it.dependsOn(collectTask)
        }
    }
}