package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.util.AboutLibrariesProcessor
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesIdTask extends BaseAboutLibrariesTask {
    @TaskAction
    public void action() throws IOException {
        final def collectedDependencies = readInCollectedDependencies()
        final def processor = new AboutLibrariesProcessor(dependencyHandler, collectedDependencies, configPath, exclusionPatterns, fetchRemoteLicense)
        final def libraries = processor.gatherDependencies()
        for (final library in libraries) {
            println "${library.libraryName} (${library.artifactVersion}) -> ${library.uniqueId}"
        }
    }
}