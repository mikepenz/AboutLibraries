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

        // task to output library names with ids for further actions
        final def collectTask = project.tasks.register("collectDependencies", AboutLibrariesCollectorTask) {
            it.description = "Collects dependencies to be used by the different AboutLibraries tasks"
            it.configure()
        }

        if (project.android.hasProperty("applicationVariants")) {
            project.android.applicationVariants.all {
                createAboutLibrariesTask(project, it, collectTask)
            }
        } else if (project.android.hasProperty("libraryVariants")) {
            project.android.libraryVariants.all {
                createAboutLibrariesTask(project, it, collectTask)
            }
        }

        // task to output library names with ids for further actions
        project.tasks.register("findLibraries", AboutLibrariesIdTask) {
            it.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
            it.group = 'Help'
            it.dependsOn(collectTask)
        }

        // task to output libraries, and their license in CSV format to the CLI
        project.tasks.register("exportLibraries", AboutLibrariesExportTask) {
            it.description = "Writes all libraries and their license in CSV format to the CLI"
            it.group = 'Help'
            it.dependsOn(collectTask)
        }
    }

    private static void createAboutLibrariesTask(Project project, def variant, def collectTask) {
        // task to write the general definitions information
        final AboutLibrariesTask task = project.tasks.create("prepareLibraryDefinitions${variant.name.capitalize()}", AboutLibrariesTask) {
            it.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
            it.group = 'Build'
            it.setDependencies(project.file("$project.buildDir/generated/aboutlibraries/${variant.name}/res/"))
            it.setVariant(variant.name)
            it.dependsOn(collectTask)
        }

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
            it.dependsOn(collectTask)
        }

        // task to output libraries, and their license in CSV format to the CLI
        project.tasks.register("exportLibraries${variant.name.capitalize()}", AboutLibrariesExportTask) {
            it.description = "Writes all libraries and their license in CSV format to the CLI"
            it.group = 'Help'
            it.setVariant(variant.name)
            it.dependsOn(collectTask)
        }

        // task to output libraries, their license in CSV format and source to a given location
        project.tasks.register("exportComplianceLibraries${variant.name.capitalize()}", AboutLibrariesExportComplianceTask) {
            it.description = "Writes all libraries with their source and their license in CSV format to the configured directory"
            it.group = 'Help'
            it.setVariant(variant.name)
            it.dependsOn(collectTask)
        }
    }
}