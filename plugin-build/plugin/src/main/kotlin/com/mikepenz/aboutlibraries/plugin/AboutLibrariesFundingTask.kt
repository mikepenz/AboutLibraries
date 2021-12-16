package com.mikepenz.aboutlibraries.plugin

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesFundingTask : BaseAboutLibrariesTask() {

    @TaskAction
    fun action() {
        val result = createLibraryProcessor(readInCollectedDependencies()).gatherDependencies()
        println("Libraries offering funding options:")
        println()
        for (library in result.libraries.filter { it.funding.isNotEmpty() }) {
            println("${library.name} @ ${library.website ?: library.scm?.url ?: "no website"}")
            library.funding.forEach {
                println(" --> Sponsor via ${it.platform} @ ${it.url}")
            }
        }
    }
}