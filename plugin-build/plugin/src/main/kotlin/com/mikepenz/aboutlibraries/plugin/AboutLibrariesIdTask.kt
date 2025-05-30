package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesIdTask : BaseAboutLibrariesTask() {
    // Disable fetching remote licenses for this task, not applicable
    override val fetchRemoteLicense: Provider<Boolean?> = project.provider { false }

    // Force fetch remote funding all the time
    override val fetchRemoteFunding: Provider<Boolean?> = project.provider { false }

    override fun getDescription(): String = "Prints all retrieved variants and its libraries to the CLI."
    override fun getGroup(): String = "Help"

    @TaskAction
    fun action() {
        offlineMode.set(true) // Force offline mode

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