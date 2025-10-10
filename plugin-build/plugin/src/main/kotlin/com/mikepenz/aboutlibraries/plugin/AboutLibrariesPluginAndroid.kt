package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.util.configure
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion
import org.slf4j.LoggerFactory

@Suppress("unused") // Public API for Gradle build scripts.
class AboutLibrariesPluginAndroid : Plugin<Project> {

    override fun apply(project: Project) {
        if (GradleVersion.current() < GradleVersion.version("8.8")) {
            project.logger.error("Gradle 8.8 or greater is required to apply this plugin.")
            return
        }

        // create the extension for the about libraries plugin
        val extension = project.extensions.findByType(AboutLibrariesExtension::class.java) ?: project.extensions.create("aboutLibraries", AboutLibrariesExtension::class.java)
        extension.applyConvention()

        LOGGER.debug("Enabled Android task registration")
        configureAndroidTasks(project, extension, ::configureAndroidResourceTasks)
    }

    private fun configureAndroidResourceTasks(project: Project, extension: AboutLibrariesExtension, @Suppress("DEPRECATION") variant: com.android.build.gradle.api.BaseVariant) {
        val variantName = variant.name.replaceFirstChar { it.uppercase() }

        val resultsResDirectory = project.layout.buildDirectory.dir("generated/aboutLibraries/${variant.name}/res/")
        val resultsDirectory = resultsResDirectory.map { it.dir("raw/") }

        // task to write the general definitions information
        val task = project.tasks.configure("prepareLibraryDefinitions${variantName}", AboutLibrariesTask::class.java) {
            it.group = ""
            it.variant.set(variant.name)
            it.configureOutputFile(resultsDirectory.map { dir ->
                @Suppress("DEPRECATION")
                dir.file(extension.export.outputFileName.get())
            })
            it.configure()
        }

        // This is necessary for backwards compatibility with versions of gradle that do not support this new API.
        try {
            variant.registerGeneratedResFolders(project.files(resultsResDirectory).builtBy(task))
            try {
                variant.mergeResourcesProvider.configure { it.dependsOn(task) }
            } catch (t: Throwable) {
                AboutLibrariesPlugin.LOGGER.error(
                    "Couldn't register mergeResourcesProvider task dependency. This is a bug in AGP. Please report it to the Android team. ${t.message}",
                    t
                )
                @Suppress("DEPRECATION") variant.mergeResources.dependsOn(task)
            }
        } catch (t: Throwable) {
            AboutLibrariesPlugin.LOGGER.warn(
                "Using deprecated API to register task, `registerGeneratedResFolders` is not supported by the environment. Upgrade your AGP version., ${t.message}",
                t
            )
            @Suppress("DEPRECATION")
            // noinspection EagerGradleConfiguration
            variant.registerResGeneratingTask(task.get(), resultsResDirectory.get().asFile)
        }
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(AboutLibrariesPluginAndroid::class.java)
    }
}
