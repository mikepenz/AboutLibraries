package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import com.mikepenz.aboutlibraries.plugin.util.DependencyCollector
import com.mikepenz.aboutlibraries.plugin.util.LibrariesProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.tasks.*
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

abstract class BaseAboutLibrariesTask : DefaultTask() {
    private val LOGGER = LoggerFactory.getLogger(BaseAboutLibrariesTask::class.java)!!

    private val rootDir = project.rootDir

    /** holds the collected set of dependencies*/
    @Internal
    protected lateinit var collectedDependencies: CollectedContainer

    @Internal
    protected val extension = project.extensions.getByName("aboutLibraries") as AboutLibrariesExtension

    @Internal
    open var variant: String? = null

    @Inject
    abstract fun getDependencyHandler(): DependencyHandler

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
    val fetchRemoteLicense = extension.fetchRemoteLicense

    @Input
    @org.gradle.api.tasks.Optional
    val gitHubApiToken = extension.gitHubApiToken

    @Input
    fun getAdditionalLicenses(): HashSet<String> {
        return extension.additionalLicenses.map { it.name }.toHashSet()
    }

    @Internal
    @Suppress("UNCHECKED_CAST")
    protected fun readInCollectedDependencies(): CollectedContainer {
        if (!::collectedDependencies.isInitialized) {
            configure()
        }
        return collectedDependencies
        //try {
        //    return CollectedContainer.from((JsonSlurper().parse(dependencyCache) as Map<String, *>)["dependencies"] as Map<String, Map<String, List<String>>>)
        //} catch (t: Throwable) {
        //    throw IllegalStateException("Failed to parse the dependencyCache. Try to do a clean build", t)
        //}
    }

    @Internal
    protected fun createLibraryProcessor(collectedContainer: CollectedContainer = readInCollectedDependencies()): LibrariesProcessor {
        return LibrariesProcessor(
            getDependencyHandler(),
            collectedContainer,
            getConfigPath(),
            exclusionPatterns,
            fetchRemoteLicense,
            getAdditionalLicenses(),
            duplicationMode,
            duplicationRule,
            variant,
            gitHubApiToken
        )
    }

    /**
     * Collect the dependencies via the available configurations for the current project
     */
    @Internal
    fun configure() {
        collectedDependencies = DependencyCollector().collect(project)
    }
}
