package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

class AboutLibrariesPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (GradleVersion.current() < GradleVersion.version("5.0")) {
            project.logger.error("Gradle 5.0 or greater is required to apply this plugin.")
            return
        }

        // create the config possible
        project.extensions.create('aboutLibraries', AboutLibrariesExtension)

        // task for cleaning
        project.tasks.register("aboutLibrariesClean", AboutLibrariesCleanTask) {
            it.description = "Cleans the generated data from the AboutLibraries plugin"
            it.group = 'Build'
            it.dependencies = project.file("$project.buildDir/generated/aboutlibraries/")
        }
        // project.tasks.findByName("clean").dependsOn(cleanupTask)
        // doing a clean will regardless delete the dir containing the files

        // create tasks for different application variants
        if (project.android.hasProperty("applicationVariants")) {
            project.android.applicationVariants.all { final variant ->
                createAboutLibrariesTask(project, variant)
            }
        } else {
            project.android.libraryVariants.all { final variant ->
                createAboutLibrariesTask(project, variant)
            }
        }

        // task to output library names with ids for further actions
        project.tasks.register("findLibraries", AboutLibrariesIdTask) {
            it.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
            it.group = 'Help'
        }

        // task to output libraries, and their license in CSV format to the CLI
        project.tasks.register("exportLibraries", AboutLibrariesExportTask) {
            it.description = "Writes all libraries and their license in CSV format to the CLI"
            it.group = 'Help'
        }
    }

    private static void createAboutLibrariesTask(Project project, def variant) {
        // task to write the general definitions information
        AboutLibrariesTask task = project.tasks.create("prepareLibraryDefinitions${variant.name.capitalize()}", AboutLibrariesTask)
        task.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
        task.group = 'Build'
        task.setDependencies(project.file("$project.buildDir/generated/aboutlibraries/${variant.name}/res/"))
        task.setVariant(variant.name)

        // This is necessary for backwards compatibility with versions of gradle that do not support
        // this new API.
        if (variant.hasProperty("preBuildProvider")) {
            variant.preBuildProvider.configure { dependsOn(task) }
        } else {
            //noinspection GrDeprecatedAPIUsage
            variant.preBuild.dependsOn(task)
        }

        // This is necessary for backwards compatibility with versions of gradle that do not support
        // this new API.
        if (variant.respondsTo("registerGeneratedResFolders")) {
            task.ext.generatedResFolders = project.files(task.getDependencies()).builtBy(task)
            variant.registerGeneratedResFolders(task.generatedResFolders)

            if (variant.hasProperty("mergeResourcesProvider")) {
                variant.mergeResourcesProvider.configure { dependsOn(task) }
            } else {
                //noinspection GrDeprecatedAPIUsage
                variant.mergeResources.dependsOn(task)
            }
        } else {
            //noinspection GrDeprecatedAPIUsage
            variant.registerResGeneratingTask(task, task.getDependencies())
        }

        // task to generate libraries, and their license into the build folder (not hooked to the build task)
        project.tasks.register("generateLibraryDefinitions${variant.name.capitalize()}", AboutLibrariesTask) {
            it.description = "Manually write meta data for the AboutLibraries plugin"
            it.group = 'Build'
            it.setDependencies(project.file("$project.buildDir/generated/aboutlibraries/${variant.name}/res/"))
            it.setVariant(variant.name)
        }

        // task to output libraries, and their license in CSV format to the CLI
        project.tasks.register("exportLibraries${variant.name.capitalize()}", AboutLibrariesExportTask) {
            it.description = "Writes all libraries and their license in CSV format to the CLI"
            it.group = 'Help'
            it.setVariant(variant.name)
        }

        // task to output libraries, their license in CSV format and source to a given location
        project.tasks.register("exportComplianceLibraries${variant.name.capitalize()}", AboutLibrariesExportComplianceTask) {
            it.description = "Writes all libraries with their source and their license in CSV format to the configured directory"
            it.group = 'Help'
            it.setVariant(variant.name)
        }
    }
}