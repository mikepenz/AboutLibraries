package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import com.mikepenz.aboutlibraries.plugin.util.LibrariesProcessor
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

abstract class BaseAboutLibrariesTask : DefaultTask() {
    private val LOGGER = LoggerFactory.getLogger(BaseAboutLibrariesTask::class.java)!!

    private val rootDir = project.rootDir

    @Internal
    protected val extension = project.extensions.findByType(AboutLibrariesExtension::class.java)!!

    @Optional
    @Input
    open var variant: Provider<String?> = project.provider { null }

    @Inject
    abstract fun getDependencyHandler(): DependencyHandler

    @InputFile
    @PathSensitive(value = PathSensitivity.RELATIVE)
    val dependencyCache: Provider<RegularFile> = project.layout.buildDirectory.file("generated/aboutLibraries/dependency_cache.json")

    @Optional
    @PathSensitive(value = PathSensitivity.RELATIVE)
    @InputDirectory
    fun getConfigPath(): File? {
        val path = extension.configPath
        if (path != null) {
            val inputFile = File(path)
            val absoluteFile = File(rootDir, path)
            if (inputFile.isAbsolute && inputFile.exists()) {
                return inputFile
            } else if (absoluteFile.exists()) {
                return absoluteFile
            } else {
                LOGGER.warn("Couldn't find provided path in: '${inputFile.absolutePath}' or '${absoluteFile.absolutePath}'")
            }
        }
        return null
    }

    @Input
    val exclusionPatterns = extension.exclusionPatterns

    @Input
    val includePlatform = extension.includePlatform

    @Input
    val duplicationMode = extension.duplicationMode

    @Input
    val duplicationRule = extension.duplicationRule

    @Input
    val mapLicensesToSpdx = extension.mapLicensesToSpdx

    @Input
    val allowedLicenses = extension.allowedLicenses

    @Input
    val allowedLicensesMap = extension.allowedLicensesMap

    @Input
    val offlineMode = extension.offlineMode

    @Input
    val fetchRemoteLicense = extension.fetchRemoteLicense && !offlineMode

    @Input
    val fetchRemoteFunding = extension.fetchRemoteFunding && !offlineMode

    @Input
    val additionalLicenses = extension.additionalLicenses.toHashSet()

    @Input
    @Optional
    val gitHubApiToken = extension.gitHubApiToken

    @Input
    val excludeFields = extension.excludeFields

    @Input
    val includeMetaData = extension.includeMetaData

    @Input
    val prettyPrint = extension.prettyPrint

    @Suppress("UNCHECKED_CAST")
    protected fun readInCollectedDependencies(): CollectedContainer {
        try {
            return CollectedContainer.from((JsonSlurper().parse(dependencyCache.get().asFile) as Map<String, *>)["dependencies"] as Map<String, Map<String, List<String>>>)
        } catch (t: Throwable) {
            throw IllegalStateException("Failed to parse the dependencyCache. Try to do a clean build", t)
        }
    }

    protected fun createLibraryProcessor(collectedContainer: CollectedContainer = readInCollectedDependencies()): LibrariesProcessor {
        return LibrariesProcessor(
            dependencyHandler = getDependencyHandler(),
            collectedDependencies = collectedContainer,
            configFolder = getConfigPath(),
            exclusionPatterns = exclusionPatterns,
            offlineMode = offlineMode,
            fetchRemoteLicense = fetchRemoteLicense,
            fetchRemoteFunding = fetchRemoteFunding,
            additionalLicenses = additionalLicenses,
            duplicationMode = duplicationMode,
            duplicationRule = duplicationRule,
            variant = variant.orNull,
            mapLicensesToSpdx = mapLicensesToSpdx,
            gitHubToken = gitHubApiToken
        )
    }
}
