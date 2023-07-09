package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.model.writeToDisk
import com.mikepenz.aboutlibraries.plugin.util.forLicense
import org.gradle.api.tasks.*
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

@CacheableTask
abstract class AboutLibrariesTask : BaseAboutLibrariesTask() {
    @Input
    val strictMode = extension.strictMode

    @Input
    val outputFileName = extension.outputFileName

    @OutputDirectory
    var resultDirectory: File = project.file("${project.buildDir}/generated/aboutLibraries/res/")
        set(value) {
            field = value
            combinedLibrariesOutputFile = File(resultDirectory, outputFileName)
        }

    @OutputFile
    var combinedLibrariesOutputFile = File(resultDirectory, outputFileName)

    @TaskAction
    public fun action() {
        val result = createLibraryProcessor().gatherDependencies()

        // validate found licenses match expectation
        val allowedLicenses = allowedLicenses.map { it.lowercase(Locale.ENGLISH) }
        if (allowedLicenses.isNotEmpty() && strictMode != StrictMode.IGNORE) {
            // detect all missing licenses
            val missing = mutableListOf<License>()
            result.licenses.values.forEach {
                val id = it.spdxId?.lowercase(Locale.ENGLISH) ?: it.hash.lowercase(Locale.ENGLISH)
                val name = it.name.lowercase(Locale.ENGLISH)
                val url = it.url?.lowercase(Locale.ENGLISH)
                if (!(allowedLicenses.contains(id)
                            || allowedLicenses.contains(name)
                            || (url?.isNotEmpty() == true && allowedLicenses.contains(url)))
                ) {
                    missing.add(it)
                }
            }

            if (missing.isEmpty()) {
                LOGGER.info("No libraries detected using a license not allowed.")
            } else {
                val message = StringBuilder()
                repeat(2) {
                    message.appendLine("=======================================")
                }
                message.appendLine("Detected usage of not allowed licenses!")
                missing.forEach {
                    message.appendLine("-> License: ${it.name} | ${it.spdxId ?: "-"} (${it.url}), used by:")
                    result.libraries.forLicense(it).forEach { lib ->
                        message.appendLine("    ${lib.uniqueId}")
                    }
                }
                repeat(2) {
                    message.appendLine("=======================================")
                }
                if (strictMode == StrictMode.FAIL) {
                    throw IllegalStateException(message.toString())
                } else {
                    LOGGER.warn(message.toString())
                }
            }
        }

        // write to disk
        result.writeToDisk(combinedLibrariesOutputFile, excludeFields, extension.prettyPrint)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesTask::class.java)
    }
}