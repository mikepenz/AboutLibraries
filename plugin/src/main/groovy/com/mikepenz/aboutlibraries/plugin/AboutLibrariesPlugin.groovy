package com.mikepenz.aboutlibraries.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project

class AboutLibrariesPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.android.applicationVariants.all { variant ->
            handleVariantOpenFile(project, variant)
        }
    }

    static handleVariantOpenFile(Project project, def variant) {
        File outputFile = project.file("$project.buildDir/generated/aboutlibraries/res/${variant.name}/")
        outputFile.mkdirs()
        File resultFile = new File(outputFile, "aboutlibraries.xml")

        AboutLibrariesTask task = project.tasks.create("prepareLicenses${variant.name.capitalize()}", AboutLibrariesTask)
        task.description = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
        task.dependencies = outputFile

        // This is necessary for backwards compatibility with versions of gradle that do not support
        // this new API.
        if (variant.respondsTo("registerGeneratedResFolders")) {
            task.ext.generatedResFolders = project.files(resultFile).builtBy(task)
            variant.registerGeneratedResFolders(task.generatedResFolders)

            if (variant.hasProperty("mergeResourcesProvider")) {
                variant.mergeResourcesProvider.configure { dependsOn(task) }
            } else {
                //noinspection GrDeprecatedAPIUsage
                variant.mergeResources.dependsOn(task)
            }
        } else {
            //noinspection GrDeprecatedAPIUsage
            variant.registerResGeneratingTask(task, resultFile)
        }
    }
}