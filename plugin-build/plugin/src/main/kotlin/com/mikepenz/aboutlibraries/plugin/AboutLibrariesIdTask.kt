package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesIdTask : BaseAboutLibrariesTask() {
    // Disable fetching remote licenses for this task, not applicable
    @get:Input
    override val fetchRemoteLicense: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)

    // Force fetch remote funding all the time
    @get:Input
    override val fetchRemoteFunding: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)

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
