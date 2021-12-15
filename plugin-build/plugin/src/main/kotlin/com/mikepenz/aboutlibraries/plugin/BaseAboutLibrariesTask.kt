package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import com.mikepenz.aboutlibraries.plugin.util.LibrariesProcessor
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.tasks.*
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

abstract class BaseAboutLibrariesTask : DefaultTask() {
    private val LOGGER = LoggerFactory.getLogger(BaseAboutLibrariesTask::class.java)!!

    private val rootDir = project.rootDir

    @Internal
    protected val extension = project.extensions.getByName("aboutLibraries") as AboutLibrariesExtension

    @Internal
    open var variant: String? = null

    @Inject
    abstract fun getDependencyHandler(): DependencyHandler

    @InputFile
    @PathSensitive(value = PathSensitivity.RELATIVE)
    protected val dependencyCache = File(project.buildDir, "generated/aboutLibraries/dependency_cache.json")

    @org.gradle.api.tasks.Optional
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
    val duplicationMode = extension.duplicationMode

    @Input
    val duplicationRule = extension.duplicationRule

    @Input
    val allowedLicenses = extension.allowedLicenses

    @Input
    val offlineMode = extension.offlineMode

    @Input
    val fetchRemoteLicense = extension.fetchRemoteLicense && !offlineMode

    @Input
    @org.gradle.api.tasks.Optional
    val gitHubApiToken = extension.gitHubApiToken

    @Input
    fun getAdditionalLicenses(): HashSet<String> {
        return extension.additionalLicenses.map { it.name }.toHashSet()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun readInCollectedDependencies(): CollectedContainer {
        try {
            return CollectedContainer.from((JsonSlurper().parse(dependencyCache) as Map<String, *>)["dependencies"] as Map<String, Map<String, List<String>>>)
        } catch (t: Throwable) {
            throw IllegalStateException("Failed to parse the dependencyCache. Try to do a clean build", t)
        }
    }

    protected fun createLibraryProcessor(collectedContainer: CollectedContainer = readInCollectedDependencies()): LibrariesProcessor {
        return LibrariesProcessor(
            getDependencyHandler(),
            collectedContainer,
            getConfigPath(),
            exclusionPatterns,
            offlineMode,
            fetchRemoteLicense,
            getAdditionalLicenses(),
            duplicationMode,
            duplicationRule,
            variant,
            gitHubApiToken
        )
    }
}
