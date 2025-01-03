package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.model.writeToDisk
import com.mikepenz.aboutlibraries.plugin.util.forLicense
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory
import java.io.File
import java.util.Locale

@CacheableTask
abstract class AboutLibrariesTask : BaseAboutLibrariesTask() {
    @Input
    val strictMode = extension.strictMode

    @Input
    val outputFileName = extension.outputFileName

    @get:OutputDirectory
    val resultDirectory: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun action() {
        if (!resultDirectory.get().asFile.exists()) {
            resultDirectory.get().asFile.mkdirs() // verify output exists
        }

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

            val missingMapped = mutableMapOf<License, List<Library>>()
            val allowedLicensesMap = allowedLicensesMap.mapKeys { (key, _) -> key.lowercase(Locale.ENGLISH) }
            if (allowedLicensesMap.isNotEmpty()) {
                missing.forEach {
                    val id = it.spdxId?.lowercase(Locale.ENGLISH) ?: it.hash.lowercase(Locale.ENGLISH)
                    val name = it.name.lowercase(Locale.ENGLISH)

                    val libsForLicense = allowedLicensesMap[id] ?: allowedLicensesMap[name]
                    if (libsForLicense != null) {
                        val notAllowed = result.libraries.forLicense(it).filter { lib ->
                            !(libsForLicense.contains(lib.uniqueId) || libsForLicense.contains(lib.groupId) || libsForLicense.contains(lib.artifactId))
                        }
                        if (notAllowed.isNotEmpty()) {
                            missingMapped[it] = notAllowed
                        }
                    } else {
                        missingMapped[it] = result.libraries.forLicense(it)
                    }
                }
            } else {
                missing.forEach { missingMapped[it] = result.libraries.forLicense(it) }
            }


            if (missingMapped.isEmpty()) {
                LOGGER.info("No libraries detected using a license not allowed.")
            } else {
                val message = StringBuilder()
                repeat(2) {
                    message.appendLine("=======================================")
                }
                message.appendLine("Detected usage of not allowed licenses!")
                missingMapped.forEach { (license, libraries) ->
                    message.appendLine("-> License: ${license.name} | ${license.spdxId ?: "-"} (${license.url}), used by:")
                    libraries.forEach { lib -> message.appendLine("    ${lib.uniqueId}") }
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
        val combinedLibrariesOutputFile = resultDirectory.file(outputFileName).get().asFile
        result.writeToDisk(combinedLibrariesOutputFile, excludeFields, extension.prettyPrint)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesTask::class.java)
    }
}