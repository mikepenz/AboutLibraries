package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesIdTask : BaseAboutLibrariesTask() {

    override fun getDescription(): String = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
    override fun getGroup(): String = "Help"

    @TaskAction
    fun action() {
        val libraries = libraries.get()
        val collectedDependencies = readInCollectedDependencies()
        collectedDependencies.dependencies.keys.forEach {
            println("variant: $it")
        }
        println("")
        println("")
        for (library in libraries) {
            println("${library.name} (${library.artifactVersion}) -> ${library.uniqueId}")
        }
    }
}