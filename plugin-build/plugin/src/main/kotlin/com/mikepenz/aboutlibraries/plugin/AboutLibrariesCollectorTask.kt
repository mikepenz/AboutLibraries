package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.util.DependencyReportCollector
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.diagnostics.DependencyReportTask
import org.gradle.api.tasks.diagnostics.internal.DependencyReportRenderer
import java.io.File

@CacheableTask
abstract class AboutLibrariesCollectorTask : DependencyReportTask() {

    @Internal
    protected val extension = project.extensions.getByName("aboutLibraries") as AboutLibrariesExtension

    @Input
    val includePlatform = extension.includePlatform

    @Input
    val filterVariants = extension.filterVariants

    @OutputFile
    val dependencyCache: File = File(File(project.buildDir, "generated/aboutLibraries/").also {
        it.mkdirs()
    }, "dependency_cache.json")

    fun setRenderer(renderer: DependencyReportCollector) {
        renderer.dependencyCache = dependencyCache
        renderer.includePlatform = includePlatform
        renderer.filterVariants = filterVariants
        setRenderer(renderer as DependencyReportRenderer)
    }
}