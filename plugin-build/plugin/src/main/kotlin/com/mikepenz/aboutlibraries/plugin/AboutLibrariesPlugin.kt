package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.common
import org.slf4j.LoggerFactory

@Suppress("unused") // Public API for Gradle build scripts.
class AboutLibrariesPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (GradleVersion.current() < GradleVersion.version("7.0")) {
            project.logger.error("Gradle 7.0 or greater is required to apply this plugin.")
            return
        }

        // create the extension for the about libraries plugin
        val extension = project.extensions.create("aboutLibraries", AboutLibrariesExtension::class.java)
        extension.applyConvention()

        // task to output funding options for included libraries
        val fundLibrariesTask = project.tasks.register("fundLibraries", AboutLibrariesFundingTask::class.java) { task ->
            task.extension.set(extension)
            task.configureTask()
        }

        // task to fetch and export funding information for included libraries
        val exportFundingTask = project.tasks.register("exportFunding", AboutLibrariesExportFundingTask::class.java) { task ->
            task.extension.set(extension)
            task.configureTask()
        }

        // task to output library names with ids for further actions
        val findLibrariesTask = project.tasks.register("findLibraries", AboutLibrariesIdTask::class.java) { task ->
            task.extension.set(extension)
            task.configureTask()
        }

        // task to output libraries, and their license in CSV format to the CLI
        val exportLibrariesTask = project.tasks.register("exportLibraries", AboutLibrariesExportTask::class.java) { task ->
            task.extension.set(extension)
            task.configureTask()
        }

        // register a global task to generate library definitions
        val exportLibraryDefinitionsTask = project.tasks.register("exportLibraryDefinitions", AboutLibrariesTask::class.java) { task ->
            task.extension.set(extension)
            task.configureOutputFile()
            task.configureTask()
        }

        configureKotlinMultiplatformTasks(project, extension)

        if (extension.android.registerAndroidTasks.get()) {
            LOGGER.debug("Enabled Android task registration")
            configureAndroidTasks(project, extension)
        }
    }

    private fun configureKotlinMultiplatformTasks(project: Project, extension: AboutLibrariesExtension) {
        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
            val targets = kotlin.targets
            targets.configureEach { target ->
                if (target.platformType == common) return@configureEach // All common dependencies end up in platform targets.
                if (target.platformType == androidJvm) return@configureEach // handled by android logic.

                val suffix = target.name.replaceFirstChar { it.uppercase() }
                project.tasks.register("exportLibraryDefinitions${suffix}", AboutLibrariesTask::class.java) { task ->
                    task.extension.set(extension)
                    task.variant.set(target.name)
                    task.configureOutputFile()
                    task.configureTask()
                }
            }
        }
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(AboutLibrariesPlugin::class.java)
    }
}
