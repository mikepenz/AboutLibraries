package com.mikepenz.aboutlibraries.plugin


import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
public class AboutLibrariesIdTask extends BaseAboutLibrariesTask {

    def gatherDependencies(def project) {
        def libraries = new AboutLibrariesProcessor().gatherDependencies(project, configPath, exclusionPatterns, fetchRemoteLicense, includeAllLicenses, additionalLicenses)

        for (final library in libraries) {
            println "${library.libraryName} (${library.libraryVersion}) -> ${library.uniqueId}"
        }
    }

    @TaskAction
    public void action() throws IOException {
        gatherDependencies(project)
    }
}