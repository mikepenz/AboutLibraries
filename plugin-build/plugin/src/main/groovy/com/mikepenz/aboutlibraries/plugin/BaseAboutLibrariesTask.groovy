package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Pattern

abstract class BaseAboutLibrariesTask extends DefaultTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAboutLibrariesTask.class);

    @org.gradle.api.tasks.Optional
    @PathSensitive(value = PathSensitivity.RELATIVE)
    @InputDirectory
    File getConfigPath() {
        final String path = project.extensions.aboutLibraries.configPath
        if (path != null) {
            final File inputFile = new File(path)
            final File absoluteFile = new File(project.rootDir, path)
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
    final Boolean fetchRemoteLicense = project.extensions.aboutLibraries.fetchRemoteLicense

    @Input
    final Boolean getAsStringResource() {
        def value = project.extensions.aboutLibraries.asStringResource
        if (value == null) return true else return value
    }

    @Input
    final List<Pattern> exclusionPatterns = project.extensions.aboutLibraries.exclusionPatterns ?: new ArrayList<>()

    @Input
    final Boolean includeAllLicenses = project.extensions.aboutLibraries.includeAllLicenses

    @Input
    HashSet<String> getAdditionalLicenses() {
        HashSet<String> licenses = new HashSet<>()
        project.extensions.aboutLibraries.additionalLicenses.all {
            licenses.add(it.name)
        }
        return licenses
    }
}
