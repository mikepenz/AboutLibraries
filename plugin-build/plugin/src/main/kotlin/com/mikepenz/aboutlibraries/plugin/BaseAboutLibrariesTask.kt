package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import com.mikepenz.aboutlibraries.plugin.util.LibrariesProcessor
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.slf4j.LoggerFactory
import javax.inject.Inject

abstract class BaseAboutLibrariesTask : DefaultTask() {

    @Internal
    protected val extension = project.extensions.findByType(AboutLibrariesExtension::class.java)!!

    @get:Optional
    @get:Input
    abstract val variant: Property<String?>

    @get:InputFile
    @get:PathSensitive(value = PathSensitivity.RELATIVE)
    abstract val dependencyCache: RegularFileProperty

    @Input
    val exclusionPatterns = extension.library.exclusionPatterns

    @Input
    val duplicationMode = extension.library.duplicationMode

    @Input
    val duplicationRule = extension.library.duplicationRule

    @Input
    val mapLicensesToSpdx = extension.license.mapLicensesToSpdx

    @Input
    val allowedLicenses = extension.license.allowedLicenses

    @Input
    val allowedLicensesMap = extension.license.allowedLicensesMap

    @Input
    val offlineMode = extension.offlineMode

    @Input
    val fetchRemoteLicense = extension.collect.fetchRemoteLicense.map { it && !offlineMode.getOrElse(false) }.orElse(false)

    @Input
    val fetchRemoteFunding = extension.collect.fetchRemoteFunding.map { it && !offlineMode.getOrElse(false) }

    @Input
    val additionalLicenses = extension.license.additionalLicenses

    @Input
    @Optional
    val gitHubApiToken = extension.collect.gitHubApiToken

    @Input
    val excludeFields = extension.export.excludeFields

    @Input
    val includeMetaData = extension.export.includeMetaData

    @Input
    val prettyPrint = extension.export.prettyPrint

    @Inject
    abstract fun getDependencyHandler(): DependencyHandler

    @Optional
    @PathSensitive(value = PathSensitivity.RELATIVE)
    @InputDirectory
    val configPath: DirectoryProperty = extension.collect.configPath

    @get:Internal
    abstract val libraries: ListProperty<Library>

    @get:Internal
    abstract val licenses: MapProperty<String, License>

    open fun configure() {
        variant.orElse(
            project.providers.gradleProperty("aboutLibraries.exportVariant").orElse(
                project.providers.gradleProperty("exportVariant").orElse(
                    extension.export.exportVariant
                )
            )
        )

        val variant = variant.orNull
        dependencyCache.set(
            if (variant == null) {
                project.layout.buildDirectory.file("generated/aboutLibraries/dependency_cache.json")
            } else {
                project.layout.buildDirectory.file("generated/aboutLibraries/$variant/dependency_cache.json")
            }
        )

        val resultContainer = createLibraryProcessor().gatherDependencies()

        LOGGER.info("Collected ${resultContainer.libraries.size} libraries and ${resultContainer.licenses.size} licenses")

        libraries.set(resultContainer.libraries)
        licenses.set(resultContainer.licenses)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun readInCollectedDependencies(): CollectedContainer {
        try {
            val parsedMap = JsonSlurper().parse(dependencyCache.get().asFile) as Map<String, *>
            val dependencies = if (parsedMap.contains("dependencies")) {
                parsedMap["dependencies"] as Map<String, Map<String, List<String>>>
            } else {
                LOGGER.warn("No dependencies found in the cache file. Please check your setup.")
                emptyMap()
            }

            return CollectedContainer.from(dependencies)
        } catch (t: Throwable) {
            throw IllegalStateException("Failed to parse the dependencyCache. Try to do a clean build. (${dependencyCache.orNull?.asFile?.absolutePath ?: "-"})", t)
        }
    }

    private fun createLibraryProcessor(collectedContainer: CollectedContainer = readInCollectedDependencies()): LibrariesProcessor {
        val configDirectory = configPath.orNull
        val realPath = if (configDirectory != null) {
            val file = configDirectory.asFile
            if (file.exists()) file else {
                LOGGER.warn("Couldn't find provided path in: '${file.absolutePath}'")
                null
            }
        } else null

        return LibrariesProcessor(
            dependencyHandler = getDependencyHandler(),
            collectedDependencies = collectedContainer,
            configFolder = realPath,
            exclusionPatterns = exclusionPatterns.getOrElse(emptySet()),
            offlineMode = offlineMode.getOrElse(false),
            fetchRemoteLicense = fetchRemoteLicense.get(),
            fetchRemoteFunding = fetchRemoteFunding.get(),
            additionalLicenses = additionalLicenses.get(),
            duplicationMode = duplicationMode.get(),
            duplicationRule = duplicationRule.get(),
            variant = variant.orNull,
            mapLicensesToSpdx = mapLicensesToSpdx.get(),
            gitHubToken = gitHubApiToken.orNull
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BaseAboutLibrariesTask::class.java)!!
    }
}
