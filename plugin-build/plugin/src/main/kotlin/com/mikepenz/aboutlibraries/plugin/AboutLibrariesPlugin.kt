package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.util.configure
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
        if (GradleVersion.current() < GradleVersion.version("8.8")) {
            project.logger.error("Gradle 8.8 or greater is required to apply this plugin.")
            return
        }

        // create the extension for the about libraries plugin
        val extension = project.extensions.create("aboutLibraries", AboutLibrariesExtension::class.java)
        extension.applyConvention()

        // task to output funding options for included libraries
        project.tasks.configure("fundLibraries", AboutLibrariesFundingTask::class.java) {
            it.configure()
        }

        // task to fetch and export funding information for included libraries
        project.tasks.configure("exportFunding", AboutLibrariesExportFundingTask::class.java) {
            it.configure()
        }

        // task to output library names with ids for further actions
        project.tasks.configure("findLibraries", AboutLibrariesIdTask::class.java) {
            it.configure()
        }

        // task to output libraries, and their license in CSV format to the CLI
        project.tasks.configure("exportLibraries", AboutLibrariesExportTask::class.java) {
            it.configure()
        }

        // register a global task to generate library definitions
        project.tasks.configure("exportLibraryDefinitions", AboutLibrariesTask::class.java) {
            it.configureOutputFile()
            it.configure()
        }

        configureKotlinMultiplatformTasks(project, extension)
        configureAndroidTasks(project, extension)
    }

    private fun configureKotlinMultiplatformTasks(project: Project, extension: AboutLibrariesExtension) {
        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
            val targets = kotlin.targets
            targets.configureEach { target ->
                if (target.platformType == common) return@configureEach // All common dependencies end up in platform targets.
                if (target.platformType == androidJvm) return@configureEach // handled by android logic.

                val suffix = target.name.replaceFirstChar { it.uppercase() }
                project.tasks.configure("exportLibraryDefinitions${suffix}", AboutLibrariesTask::class.java) {
                    it.variant.set(target.name)
                    it.configureOutputFile()
                    it.configure()
                }
            }
        }
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(AboutLibrariesPlugin::class.java)
    }
}
