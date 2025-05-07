package com.mikepenz.aboutlibraries.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.mikepenz.aboutlibraries.plugin.util.configure
import org.gradle.api.Project

internal fun configureAndroidTasks(project: Project, extension: AboutLibrariesExtension) {
    project.pluginManager.withPlugin("com.android.application") {
        AboutLibrariesPlugin.LOGGER.debug("Registering Android task for Application")
        val app = project.extensions.findByType(AppExtension::class.java)
        if (app != null) {
            app.applicationVariants.configureEach { variant -> configureAndroidTasks(project, extension, variant) }
        } else {
            AboutLibrariesPlugin.LOGGER.warn("No Android AppExtension found. Skipping Android tasks registration. Please ensure your Android Gradle plugin is applied BEFORE the AboutLibraries plugin.")
        }
    }
    project.pluginManager.withPlugin("com.android.library") {
        AboutLibrariesPlugin.LOGGER.debug("Registering Android task for Library")
        val lib = project.extensions.findByType(LibraryExtension::class.java)
        if (lib != null) {
            lib.libraryVariants.configureEach { variant -> configureAndroidTasks(project, extension, variant) }
        } else {
            AboutLibrariesPlugin.LOGGER.warn("No Android LibraryExtension found. Skipping Android tasks registration. Please ensure your Android Gradle plugin is applied BEFORE the AboutLibraries plugin.")
        }
    }
}

private fun configureAndroidTasks(project: Project, extension: AboutLibrariesExtension, @Suppress("DEPRECATION") variant: com.android.build.gradle.api.BaseVariant) {
    val variantName = variant.name.replaceFirstChar { it.uppercase() }

    val resultsResDirectory = project.layout.buildDirectory.dir("generated/aboutLibraries/${variant.name}/res/")
    val resultsDirectory = resultsResDirectory.map { it.dir("raw/") }

    // task to write the general definitions information
    val task = project.tasks.configure("prepareLibraryDefinitions${variantName}", AboutLibrariesTask::class.java) {
        it.group = ""
        it.variant.set(variant.name)
        it.configureOutputFile(resultsDirectory.map { dir ->
            @Suppress("DEPRECATION")
            dir.file(extension.export.outputFileName.get())
        })
        it.configure()
    }

    // This is necessary for backwards compatibility with versions of gradle that do not support this new API.
    try {
        variant.registerGeneratedResFolders(project.files(resultsResDirectory).builtBy(task))
        try {
            variant.mergeResourcesProvider.configure { it.dependsOn(task) }
        } catch (t: Throwable) {
            AboutLibrariesPlugin.LOGGER.error(
                "Couldn't register mergeResourcesProvider task dependency. This is a bug in AGP. Please report it to the Android team. ${t.message}",
                t
            )
            @Suppress("DEPRECATION") variant.mergeResources.dependsOn(task)
        }
    } catch (t: Throwable) {
        AboutLibrariesPlugin.LOGGER.warn(
            "Using deprecated API to register task, `registerGeneratedResFolders` is not supported by the environment. Upgrade your AGP version., ${t.message}",
            t
        )
        @Suppress("DEPRECATION")
        // noinspection EagerGradleConfiguration
        variant.registerResGeneratingTask(task.get(), resultsResDirectory.get().asFile)
    }

    // task to generate libraries, and their license into the build folder (not hooked to the build task)
    project.tasks.configure("exportLibraryDefinitions${variantName}", AboutLibrariesTask::class.java) {
        it.variant.set(variant.name)
        it.configureOutputFile(resultsDirectory.map { dir ->
            @Suppress("DEPRECATION")
            dir.file(extension.export.outputFileName.get())
        })
        it.configure()
    }

    // backwards compatibility, to be removed in v13.0.0
    project.tasks.configure("generateLibraryDefinitions${variantName}", AboutLibrariesTask::class.java) {
        it.group = ""
        it.deprecated.set(true)
        it.variant.set(variant.name)
        it.configureOutputFile(resultsDirectory.map { dir ->
            @Suppress("DEPRECATION")
            dir.file(extension.export.outputFileName.get())
        })
        it.configure()
    }

    // task to output libraries, and their license in CSV format to the CLI
    project.tasks.configure("exportLibraries${variantName}", AboutLibrariesExportTask::class.java) {
        it.variant.set(variant.name)
        it.configure()
    }

    // task to output libraries, their license in CSV format and source to a given location
    project.tasks.configure("exportComplianceLibraries${variantName}", AboutLibrariesExportComplianceTask::class.java) {
        it.variant.set(variant.name)
        it.projectDirectory.set(project.layout.projectDirectory)
        it.configure()
    }
}