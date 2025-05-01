package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.common
import org.slf4j.LoggerFactory
import java.util.Locale

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
        project.tasks.configure("fundLibraries", AboutLibrariesFundingTask::class.java) {
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

        if (extension.android.registerAndroidTasks.get()) {
            LOGGER.debug("Enabled Android task registration")
            configureAndroidTasks(project, extension)
        }
    }

    @Suppress("DEPRECATION")
    private fun configureKotlinMultiplatformTasks(project: Project, extension: AboutLibrariesExtension) {
        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
            val targets = kotlin.targets
            targets.configureEach { target ->
                if (target.platformType == common) return@configureEach // All common dependencies end up in platform targets.
                if (target.platformType == androidJvm) return@configureEach // handled by android logic.

                val suffix = target.name.capitalize(Locale.ENGLISH)
                project.tasks.configure("exportLibraryDefinitions${suffix}", AboutLibrariesTask::class.java) {
                    it.variant.set(target.name)
                    it.configureOutputFile()
                    it.configure()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun configureAndroidTasks(project: Project, extension: AboutLibrariesExtension) {
        project.pluginManager.withPlugin("com.android.application") {
            LOGGER.debug("Registering Android task for Application")
            val app = project.extensions.findByType(com.android.build.gradle.AppExtension::class.java)
            if (app != null) {
                app.applicationVariants.configureEach { variant -> configureAndroidTasks(project, extension, variant) }
            } else {
                LOGGER.warn("No Android AppExtension found. Skipping Android tasks registration. Please ensure your Android Gradle plugin is applied BEFORE the AboutLibraries plugin.")
            }
        }
        project.pluginManager.withPlugin("com.android.library") {
            LOGGER.debug("Registering Android task for Library")
            val lib = project.extensions.findByType(com.android.build.gradle.LibraryExtension::class.java)
            if (lib != null) {
                lib.libraryVariants.configureEach { variant -> configureAndroidTasks(project, extension, variant) }
            } else {
                LOGGER.warn("No Android LibraryExtension found. Skipping Android tasks registration. Please ensure your Android Gradle plugin is applied BEFORE the AboutLibraries plugin.")
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun configureAndroidTasks(project: Project, extension: AboutLibrariesExtension, variant: com.android.build.gradle.api.BaseVariant) {
        val variantName = variant.name.capitalize(Locale.ENGLISH)

        val resultsResDirectory = project.layout.buildDirectory.dir("generated/aboutLibraries/${variant.name}/res/")
        val resultsDirectory = resultsResDirectory.map { it.dir("raw/") }

        // task to write the general definitions information
        val task = project.tasks.configure("prepareLibraryDefinitions${variantName}", AboutLibrariesTask::class.java) {
            it.group = ""
            it.variant.set(variant.name)
            it.configureOutputFile(resultsDirectory.map { dir -> dir.file(extension.export.outputFileName.get()) })
            it.configure()
        }

        // This is necessary for backwards compatibility with versions of gradle that do not support this new API.
        try {
            variant.registerGeneratedResFolders(project.files(resultsResDirectory).builtBy(task))
            try {
                variant.mergeResourcesProvider.configure { it.dependsOn(task) }
            } catch (t: Throwable) {
                LOGGER.error("Couldn't register mergeResourcesProvider task dependency. This is a bug in AGP. Please report it to the Android team. ${t.message}", t)
                @Suppress("DEPRECATION") variant.mergeResources.dependsOn(task)
            }
        } catch (t: Throwable) {
            LOGGER.warn("Using deprecated API to register task, `registerGeneratedResFolders` is not supported by the environment. Upgrade your AGP version., ${t.message}", t)
            @Suppress("DEPRECATION")
            // noinspection EagerGradleConfiguration
            variant.registerResGeneratingTask(task.get(), resultsResDirectory.get().asFile)
        }

        // task to generate libraries, and their license into the build folder (not hooked to the build task)
        project.tasks.configure("exportLibraryDefinitions${variantName}", AboutLibrariesTask::class.java) {
            it.variant.set(variant.name)
            it.configureOutputFile(resultsDirectory.map { dir -> dir.file(extension.export.outputFileName.get()) })
            it.configure()
        }

        // backwards compatibility, to be removed in v13.0.0
        project.tasks.configure("generateLibraryDefinitions${variantName}", AboutLibrariesTask::class.java) {
            it.group = ""
            it.deprecated.set(true)
            it.variant.set(variant.name)
            it.configureOutputFile(resultsDirectory.map { dir -> dir.file(extension.export.outputFileName.get()) })
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

    /**
     * Configures a task with the given name and type. If a task with the same name already exists, it will be configured instead of created.
     * Copyright: https://github.com/cashapp/licensee/blob/99b162fb4bdba838ff1ce805a213002dd6c02827/src/main/kotlin/app/cash/licensee/plugin.kt#L239
     */
    private fun <T : Task> TaskContainer.configure(name: String, type: Class<T>, config: (T) -> Unit): TaskProvider<T> = if (name in names) {
        named(name, type, config)
    } else {
        register(name, type, config)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesPlugin::class.java)
    }
}