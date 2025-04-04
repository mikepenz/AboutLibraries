package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Project
import org.slf4j.LoggerFactory
import java.util.Locale

/**
 * Android specific extension for the [AboutLibrariesPlugin]
 */
object AboutLibrariesPluginAndroidExtension {
    private val LOGGER = LoggerFactory.getLogger(AboutLibrariesPluginAndroidExtension::class.java)

    fun apply(project: Project, extension: AboutLibrariesExtension) {
        try {
            val app = project.extensions.findByType(com.android.build.gradle.AppExtension::class.java)
            if (app != null) {
                app.applicationVariants.configureEach {
                    createAboutLibrariesAndroidTasks(project, extension, it)
                }
            } else {
                val lib = project.extensions.findByType(com.android.build.gradle.LibraryExtension::class.java)
                lib?.libraryVariants?.configureEach {
                    createAboutLibrariesAndroidTasks(project, extension, it)
                }
            }
        } catch (t: Throwable) {
            LOGGER.warn("Couldn't register Android related plugin tasks")
        }
    }

    @Suppress("DEPRECATION")
    private fun createAboutLibrariesAndroidTasks(project: Project, extension: AboutLibrariesExtension, v: Any) {
        val variant = (v as? com.android.build.gradle.api.BaseVariant) ?: return

        // task to output library names with ids for further actions
        val collectTask = project.tasks.register("collectDependencies${variant.name.capitalize(Locale.ENGLISH)}", AboutLibrariesCollectorTask::class.java) {
            it.variant.set(variant.name)
            // project.evaluationDependsOnChildren()
            it.configure()
        }

        val resultsResDirectory = project.layout.buildDirectory.dir("generated/aboutLibraries/${variant.name}/res/")
        val resultsDirectory = resultsResDirectory.map { it.dir("raw/") }

        // task to write the general definitions information
        val task = project.tasks.register(
            "prepareLibraryDefinitions${variant.name.capitalize(Locale.ENGLISH)}", AboutLibrariesTask::class.java
        ) {
            it.variant.set(variant.name)
            it.configureOutputFile(resultsDirectory.map { dir -> dir.file(extension.export.outputFileName.get()) })
            it.configure()
            it.dependsOn(collectTask)
        }

        // This is necessary for backwards compatibility with versions of gradle that do not support this new API.
        try {
            variant.registerGeneratedResFolders(project.files(resultsResDirectory).builtBy(task))
            try {
                variant.mergeResourcesProvider.configure { it.dependsOn(task) }
            } catch (t: Throwable) {
                LOGGER.error("Couldn't register mergeResourcesProvider task dependency. This is a bug in AGP. Please report it to the Android team. ${t.message}", t)
                @Suppress("DEPRECATION") variant.mergeResources.dependsOn(task)
            }
        } catch (t: Throwable) {
            LOGGER.warn(
                "Using deprecated API to register task, as new registerGeneratedResFolders was not supported by the current environment. Consider upgrading your AGP version., ${t.message}",
                t
            )
            @Suppress("DEPRECATION")
            // noinspection EagerGradleConfiguration
            variant.registerResGeneratingTask(task.get(), resultsResDirectory.get().asFile)
        }

        // task to generate libraries, and their license into the build folder (not hooked to the build task)
        project.tasks.register(
            "generateLibraryDefinitions${variant.name.capitalize(Locale.ENGLISH)}", AboutLibrariesTask::class.java
        ) {
            it.variant.set(variant.name)
            it.configureOutputFile(resultsDirectory.map { dir -> dir.file(extension.export.outputFileName.get()) })
            it.configure()
            it.dependsOn(collectTask)
        }

        // task to output libraries, and their license in CSV format to the CLI
        project.tasks.register(
            "exportLibraries${variant.name.capitalize(Locale.ENGLISH)}", AboutLibrariesExportTask::class.java
        ) {
            it.variant.set(variant.name)
            it.configure()
            it.dependsOn(collectTask)
        }

        // task to output libraries, their license in CSV format and source to a given location
        project.tasks.register(
            "exportComplianceLibraries${variant.name.capitalize(Locale.ENGLISH)}", AboutLibrariesExportComplianceTask::class.java
        ) {
            it.variant.set(variant.name)
            it.projectDirectory.set(project.layout.projectDirectory)
            it.configure()
            it.dependsOn(collectTask)
        }
    }
}