package com.mikepenz.aboutlibraries.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project

class AboutLibrariesExtension {
    String configPath
}

class AboutLibrariesPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // create the config possible
        project.extensions.create('aboutLibraries', AboutLibrariesExtension)

        File outputFile = project.file("$project.buildDir/generated/aboutlibraries/res/")

        // task for cleaning
        def cleanupTask = project.tasks.create("aboutLibrariesClean", AboutLibrariesCleanTask)
        cleanupTask.description = "Cleans the generated data from the AboutLibraries plugin"
        cleanupTask.dependencies = outputFile
        project.tasks.findByName("clean").dependsOn(cleanupTask)

        // task to write the general definitions information
        AboutLibrariesTask task = project.tasks.create("prepareLibraryDefinitions", AboutLibrariesTask)
        task.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
        task.setDependencies(outputFile)

        project.android.applicationVariants.all { variant ->
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
        }

        // task to output library names with ids for further actions
        AboutLibrariesIdTask taskId = project.tasks.create("findLibraries", AboutLibrariesIdTask)
        taskId.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"

        // task to output libraries and their license in CSV format to the CLI
        AboutLibrariesExportTask exportTaskId = project.tasks.create("exportLibraries", AboutLibrariesExportTask)
        exportTaskId.description = "Writes all libraries and their license in CSV format to the CLI"
    }
}