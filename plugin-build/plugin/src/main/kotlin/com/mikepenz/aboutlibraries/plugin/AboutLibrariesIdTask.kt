package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesIdTask : BaseAboutLibrariesTask() {

    @TaskAction
    fun action() {
        val result = createLibraryProcessor().gatherDependencies()
        for (library in result.libraries) {
            println("${library.name} (${library.artifactVersion}) -> ${library.uniqueId}")
        }
    }
}