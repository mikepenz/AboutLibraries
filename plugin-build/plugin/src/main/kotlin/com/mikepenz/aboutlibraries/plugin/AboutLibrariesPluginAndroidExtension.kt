package com.mikepenz.aboutlibraries.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.mikepenz.aboutlibraries.plugin.util.configure
import org.gradle.api.Project

internal fun configureAndroidTasks(
    project: Project,
    extension: AboutLibrariesExtension,
    block: (Project, AboutLibrariesExtension, com.android.build.api.variant.Variant) -> Unit = ::configureAndroidTasks,
) {
    project.pluginManager.withPlugin("com.android.application") {
        AboutLibrariesPlugin.LOGGER.debug("Registering Android task for Application")

        project.extensions.configure(AndroidComponentsExtension::class.java) {
            it.onVariants { variant ->
                block(project, extension, variant)
            }
        }
    }
    project.pluginManager.withPlugin("com.android.library") {
        AboutLibrariesPlugin.LOGGER.debug("Registering Android task for Library")
        project.extensions.configure(AndroidComponentsExtension::class.java) {
            it.onVariants { variant ->
                block(project, extension, variant)
            }
        }
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