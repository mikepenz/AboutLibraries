package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory

import java.util.regex.Pattern

abstract class BaseAboutLibrariesTask extends DefaultTask {
    @InputDirectory
    File getConfigPath() {
        String path = project.extensions.aboutLibraries.configPath
        if (path != null) {
            return new File(path)
        } else {
            return null
        }
    }

    @Input
    final List<Pattern> exclusionPatterns = project.extensions.aboutLibraries.exclusionPatterns ?: new ArrayList<>()

    @Input
    final Boolean includeAllLicenses = project.extensions.aboutLibraries.includeAllLicenses ?: false

    @Input
    HashSet<String> getAdditionalLicenses() {
        HashSet<String> licenses = new HashSet<>()
        project.extensions.aboutLibraries.additionalLicenses.all {
            licenses.add(it.name)
        }
        return licenses
    }
}
