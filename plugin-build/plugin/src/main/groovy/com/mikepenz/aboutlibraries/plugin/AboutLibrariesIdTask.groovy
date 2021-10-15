package com.mikepenz.aboutlibraries.plugin


import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesIdTask extends BaseAboutLibrariesTask {

    AboutLibrariesIdTask() {
        loadCollectedDependencies()
    }

    @TaskAction
    public void action() throws IOException {
        loadCollectedDependenciesTask(variant)
        final def processor = new AboutLibrariesProcessor(dependencyHandler, collectedDependencies, configPath, exclusionPatterns, fetchRemoteLicense, includeAllLicenses, additionalLicenses)
        final def libraries = processor.gatherDependencies()

        for (final library in libraries) {
            println "${library.libraryName} (${library.libraryVersion}) -> ${library.uniqueId}"
        }
    }
}