package com.mikepenz.aboutlibraries.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

class AboutLibrariesExtension {
    String configPath
}

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
        def cleanupTask = project.tasks.create("aboutLibrariesClean", AboutLibrariesCleanTask)
        cleanupTask.description = "Cleans the generated data from the AboutLibraries plugin"
        cleanupTask.group = 'Build'
        cleanupTask.dependencies = project.file("$project.buildDir/generated/aboutlibraries/")
        // project.tasks.findByName("clean").dependsOn(cleanupTask)
        // doing a clean will regardless delete the dir containing the files

        // create tasks for different application variants
        project.android.applicationVariants.all { variant ->
            createAboutLibrariesTask(project, variant)
        }

        // task to output library names with ids for further actions
        AboutLibrariesIdTask taskId = project.tasks.create("findLibraries", AboutLibrariesIdTask)
        taskId.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
        taskId.group = 'Help'

        // task to output libraries, and their license in CSV format to the CLI
        AboutLibrariesExportTask exportTaskId = project.tasks.create("exportLibraries", AboutLibrariesExportTask)
        exportTaskId.description = "Writes all libraries and their license in CSV format to the CLI"
        exportTaskId.group = 'Help'
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
        AboutLibrariesTask generateTask = project.tasks.create("generateLibraryDefinitions${variant.name.capitalize()}", AboutLibrariesTask)
        generateTask.description = "Manually write meta data for the AboutLibraries plugin"
        generateTask.group = 'Build'
        generateTask.setDependencies(project.file("$project.buildDir/generated/aboutlibraries/${variant.name}/res/"))
        generateTask.setVariant(variant.name)

        // task to output libraries, and their license in CSV format to the CLI
        AboutLibrariesExportTask exportTaskId = project.tasks.create("exportLibraries${variant.name.capitalize()}", AboutLibrariesExportTask)
        exportTaskId.description = "Writes all libraries and their license in CSV format to the CLI"
        exportTaskId.group = 'Help'
        exportTaskId.setVariant(variant.name)
    }
}