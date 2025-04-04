package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import com.mikepenz.aboutlibraries.plugin.util.DependencyCollector
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

/**
 * A Gradle task to collect library dependencies and cache them in a JSON file.
 */
@CacheableTask
abstract class AboutLibrariesCollectorTask : DefaultTask() {

    override fun getDescription(): String = "Collects dependencies to be used by the different AboutLibraries tasks"

    @Internal
    protected val extension = project.extensions.findByType(AboutLibrariesExtension::class.java)!!

    @get:Input
    val projectName: String = project.name

    @Input
    val includePlatform = extension.collect.includePlatform

    @Input
    val filterVariants = extension.collect.filterVariants

    @Optional
    @Input
    var variant: Provider<String?> = project.providers.gradleProperty("aboutLibraries.exportVariant").orElse(
        project.providers.gradleProperty("exportVariant").orElse(
            extension.export.exportVariant
        )
    )

    /** holds the collected set of dependencies*/
    @get:Input
    abstract val dependencies: MapProperty<String, Map<String, Set<String>>>

    @OutputFile
    val dependencyCache: Provider<RegularFile> = project.provider {
        val variant = variant.orNull
        if (variant == null) {
            project.layout.buildDirectory.file("generated/aboutLibraries/dependency_cache.json").orNull
        } else {
            project.layout.buildDirectory.file("generated/aboutLibraries/$variant/dependency_cache.json").orNull
        }
    }

    fun configure() {
        dependencies.set(
            DependencyCollector(
                includePlatform.get(),
                filterVariants.get() + (variant.orNull?.let { arrayOf(it) } ?: emptyArray()),
            ).collect(project)
        )
    }

    @TaskAction
    fun action() {
        LOGGER.info("Collecting for: $projectName")

        val cacheOutput = dependencyCache.get().asFile
        cacheOutput.parentFile.mkdirs()
        cacheOutput.writeText(groovy.json.JsonOutput.toJson(CollectedContainer(dependencies.get())))

        LOGGER.debug("Collected dependencies for: $projectName to ${cacheOutput.absolutePath}")
    }

    private companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesCollectorTask::class.java)!!
    }
}