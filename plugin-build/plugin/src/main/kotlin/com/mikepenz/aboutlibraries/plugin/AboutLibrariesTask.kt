package com.mikepenz.aboutlibraries.plugin


import com.mikepenz.aboutlibraries.plugin.model.writeToDisk
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory
import java.io.File

@CacheableTask
abstract class AboutLibrariesTask : BaseAboutLibrariesTask() {

    @OutputDirectory
    lateinit var dependencies: File

    @Internal
    private lateinit var combinedLibrariesOutputFile: File
    private lateinit var outputRawFolder: File

    fun getCombinedLibrariesOutputFile(): File {
        return File(outputRawFolder, "aboutlibraries.json")
    }

    @OutputDirectory
    public fun getRawFolder(): File {
        return File(dependencies, "raw")
    }

    @TaskAction
    public fun action() {
        // ensure directories exist
        this.outputRawFolder = getRawFolder()
        this.combinedLibrariesOutputFile = getCombinedLibrariesOutputFile()

        val result = createLibraryProcessor().gatherDependencies()

        // write to disk
        result.writeToDisk(combinedLibrariesOutputFile)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesTask::class.java)
    }
}