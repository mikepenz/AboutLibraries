package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import java.util.regex.Pattern

abstract class BaseAboutLibrariesTask extends DefaultTask {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseAboutLibrariesTask.class);

    private def rootDir = project.rootDir
    private def extension = project.extensions.aboutLibraries

    @Inject
    abstract DependencyHandler getDependencyHandler()

    @InputFile
    protected File dependencyCache = new File(project.buildDir, "generated/aboutLibraries/dependency_cache.json")

    @org.gradle.api.tasks.Optional
    @PathSensitive(value = PathSensitivity.RELATIVE)
    @InputDirectory
    File getConfigPath() {
        final String path = extension.configPath
        if (path != null) {
            final File inputFile = new File(path)
            final File absoluteFile = new File(rootDir, path)
            if (inputFile.isAbsolute() && inputFile.exists()) {
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
    final List<Pattern> exclusionPatterns = extension.exclusionPatterns ?: new ArrayList<>()

    @Input
    final Boolean includeAllLicenses = extension.includeAllLicenses

    @Input
    HashSet<String> getAdditionalLicenses() {
        final HashSet<String> licenses = new HashSet<>()
        extension.additionalLicenses.all {
            licenses.add(it.name)
        }
        return licenses
    }

    @Internal
    protected CollectedContainer readInCollectedDependencies() {
        try {
            return CollectedContainer.from(new JsonSlurper().parse(dependencyCache).get("dependencies"))
        } catch (final Throwable t) {
            throw new IllegalStateException("Failed to parse the dependencyCache. Try to do a clean build", t)
        }
    }
}
