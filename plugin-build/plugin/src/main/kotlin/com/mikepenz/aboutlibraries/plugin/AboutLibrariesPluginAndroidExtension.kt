package com.mikepenz.aboutlibraries.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.mikepenz.aboutlibraries.plugin.util.configure
import org.gradle.api.Project

internal fun configureAndroidTasks(
    project: Project,
    extension: AboutLibrariesExtension,
    block: (Project, AboutLibrariesExtension, @Suppress("DEPRECATION") com.android.build.gradle.api.BaseVariant) -> Unit = ::configureAndroidTasks,
    blockNew: (Project, AboutLibrariesExtension, com.android.build.api.variant.Variant) -> Unit = ::configureAndroidTasks,
) {
    project.pluginManager.withPlugin("com.android.application") {
        AboutLibrariesPlugin.LOGGER.debug("Registering Android task for Application")

        val newApp = project.extensions.findByType(AndroidComponentsExtension::class.java)
        if (newApp != null) {
            project.extensions.configure(AndroidComponentsExtension::class.java) {
                it.onVariants { variant ->
                    blockNew(project, extension, variant)
                }
            }
        } else {
            val app = project.extensions.findByType(AppExtension::class.java)
            if (app != null) {
                app.applicationVariants.configureEach { variant -> block(project, extension, variant) }
            } else {
                AboutLibrariesPlugin.LOGGER.warn("No Android AppExtension found. Skipping Android tasks registration. Please ensure your Android Gradle plugin is applied BEFORE the AboutLibraries plugin.")
            }
        }
    }
    project.pluginManager.withPlugin("com.android.library") {
        AboutLibrariesPlugin.LOGGER.debug("Registering Android task for Library")

        val newLib = project.extensions.findByType(AndroidComponentsExtension::class.java)
        if (newLib != null) {
            project.extensions.configure(AndroidComponentsExtension::class.java) {
                it.onVariants { variant ->
                    blockNew(project, extension, variant)
                }
            }
        } else {
            val lib = project.extensions.findByType(LibraryExtension::class.java)
            if (lib != null) {
                lib.libraryVariants.configureEach { variant -> block(project, extension, variant) }
            } else {
                AboutLibrariesPlugin.LOGGER.warn("No Android LibraryExtension found. Skipping Android tasks registration. Please ensure your Android Gradle plugin is applied BEFORE the AboutLibraries plugin.")
            }
        }
    }
}

private fun configureAndroidTasks(project: Project, extension: AboutLibrariesExtension, @Suppress("DEPRECATION") variant: com.android.build.gradle.api.BaseVariant) {
    val variantName = variant.name.replaceFirstChar { it.uppercase() }

    val resultsResDirectory = project.layout.buildDirectory.dir("generated/aboutLibraries/${variant.name}/res/")
    val resultsDirectory = resultsResDirectory.map { it.dir("raw/") }

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

private fun configureAndroidTasks(project: Project, extension: AboutLibrariesExtension, variant: com.android.build.api.variant.Variant) {
    val variantName = variant.name.replaceFirstChar { it.uppercase() }
    val resultsResDirectory = project.layout.buildDirectory.dir("generated/aboutLibraries/${variant.name}/res/")
    val resultsDirectory = resultsResDirectory.map { it.dir("raw/") }

    // task to write the general definitions information
    val task = project.tasks.configure("prepareLibraryDefinitions${variantName}", AboutLibrariesTask::class.java) {
        it.group = ""
        it.variant.set(variant.name)
        it.outputDirectory.set(resultsResDirectory)
        it.configureOutputFile(resultsDirectory.map { dir ->
            @Suppress("DEPRECATION")
            dir.file(extension.export.outputFileName.get())
        })
        it.configure()
    }

    variant.sources.res?.addGeneratedSourceDirectory(task) {
        it.outputDirectory
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