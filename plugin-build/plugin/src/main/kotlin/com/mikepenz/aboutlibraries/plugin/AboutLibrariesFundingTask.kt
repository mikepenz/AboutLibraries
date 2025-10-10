package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesFundingTask : BaseAboutLibrariesTask() {

    // Disable fetching remote licenses for this task, not applicable
    override val fetchRemoteLicense: Provider<Boolean> = project.provider { false }

    override fun getDescription(): String = "Outputs the funding options for used dependencies"
    override fun getGroup(): String = "Help"

    @TaskAction
    fun action() {
        val result = createLibraryPostProcessor().process()
        val libraries = result.libraries
        println("Libraries offering funding options:")
        println()
        for (library in libraries.filter { it.funding.isNotEmpty() }) {
            println("${library.name} @ ${library.website ?: library.scm?.url ?: "no website"}")
            library.funding.forEach {
                println(" --> Sponsor via ${it.platform} @ ${it.url}")
            }
        }
    }
}