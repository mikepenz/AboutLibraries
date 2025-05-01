package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesIdTask : BaseAboutLibrariesTask() {

    override fun getDescription(): String = "Prints all retrieved variants and its libraries to the CLI."
    override fun getGroup(): String = "Help"

    @TaskAction
    fun action() {
        val result = createLibraryPostProcessor().process()
        val libraries = result.libraries
        variantToDependencyData.get().keys.forEach {
            println("variant: $it")
        }
        println("")
        println("")
        for (library in libraries) {
            println("${library.name} (${library.artifactVersion}) -> ${library.uniqueId}")
        }
    }
}