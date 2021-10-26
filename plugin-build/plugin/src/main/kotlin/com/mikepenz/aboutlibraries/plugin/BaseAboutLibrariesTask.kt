package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
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
    private val extension = project.extensions.getByName("aboutLibraries") as AboutLibrariesExtension

    @Internal
    var variant: String? = null

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
    val fetchRemoteLicense = extension.fetchRemoteLicense ?: false

    @Input
    val exclusionPatterns = extension.exclusionPatterns ?: emptyList()

    @Input
    val includeAllLicenses = extension.includeAllLicenses ?: false

    @Input
    fun getAdditionalLicenses(): HashSet<String> {
        return extension.additionalLicenses.map { it.name }.toHashSet()
    }

    @Internal
    protected fun readInCollectedDependencies(): CollectedContainer {
        try {
            return CollectedContainer.from((JsonSlurper().parse(dependencyCache) as Map<String, *>)["dependencies"] as Map<String, Map<String, List<String>>>)
        } catch (t: Throwable) {
            throw IllegalStateException("Failed to parse the dependencyCache. Try to do a clean build", t)
        }
    }
}
