package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_EXPORT_VARIANT
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_PREFIX
import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import com.mikepenz.aboutlibraries.plugin.util.DependencyCollector
import com.mikepenz.aboutlibraries.plugin.util.LibrariesProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.slf4j.LoggerFactory
import javax.inject.Inject

abstract class BaseAboutLibrariesTask : DefaultTask() {

    @Internal
    protected val extension = project.extensions.findByType(AboutLibrariesExtension::class.java)!!

    @get:Input
    val projectName: String = project.name

    @Input
    val includePlatform = extension.collect.includePlatform

    @Input
    val filterVariants = extension.collect.filterVariants

    @get:Optional
    @get:Input
    abstract val variant: Property<String?>

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
    abstract val dependencies: MapProperty<String, Map<String, Set<String>>>

    @get:Internal
    abstract val libraries: ListProperty<Library>

    @get:Internal
    abstract val licenses: MapProperty<String, License>

    open fun configure() {
        if (!variant.isPresent) {
            variant.set(
                project.providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_VARIANT}").orElse(
                    project.providers.gradleProperty(PROP_EXPORT_VARIANT).orElse(
                        extension.export.exportVariant
                    )
                )
            )
        }

        val variant = variant.orNull
        val collectedContainer = DependencyCollector(
            includePlatform.get(),
            filterVariants.get() + (variant?.let { arrayOf(it) } ?: emptyArray()),
        ).collect(project)

        // keep dependencies
        dependencies.set(collectedContainer.dependencies)

        val resultContainer = createLibraryProcessor(collectedContainer).gatherDependencies()

        LOGGER.info("Collected ${resultContainer.libraries.size} libraries and ${resultContainer.licenses.size} licenses")

        libraries.set(resultContainer.libraries)
        licenses.set(resultContainer.licenses)
    }

    private fun createLibraryProcessor(collectedContainer: CollectedContainer): LibrariesProcessor {
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
