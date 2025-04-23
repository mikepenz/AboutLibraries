package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_EXPORT_OUTPUT_PATH
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_EXPORT_PATH
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_PREFIX
import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.model.ResultContainer
import com.mikepenz.aboutlibraries.plugin.model.writeToDisk
import com.mikepenz.aboutlibraries.plugin.util.forLicense
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory
import java.util.Locale

@CacheableTask
abstract class AboutLibrariesTask : BaseAboutLibrariesTask() {
    @Input
    val strictMode = extension.license.strictMode

    @get:OutputFile
    protected abstract val outputFile: RegularFileProperty

    override fun getDescription(): String = "Writes the relevant meta data for the AboutLibraries plugin to display dependencies"
    override fun getGroup(): String = "Build"

    fun configureOutputFile(outputFile: Provider<RegularFile>? = null) {
        if (outputFile != null && outputFile.isPresent) {
            this.outputFile.set(outputFile)
        } else {
            val projectDirectory = project.layout.projectDirectory
            val buildDirectory = project.layout.buildDirectory

            @Suppress("DEPRECATION")
            val outputFileName = extension.export.outputFileName.get()
            val providers = project.providers
            this.outputFile.set(
                providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_PATH}").map { path -> projectDirectory.dir(path).file(outputFileName) }.orElse(
                    providers.gradleProperty(PROP_EXPORT_PATH).map { path -> projectDirectory.dir(path).file(outputFileName) }).orElse(
                    providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_OUTPUT_PATH}").map { path -> projectDirectory.file(path) }.orElse(
                        extension.export.outputPath.orElse(
                            buildDirectory.dir("generated/aboutLibraries/").map { it.file(outputFileName) }
                        )
                    )
                )
            )
        }
    }

    @TaskAction
    fun action() {
        val output = outputFile.get().asFile
        if (!output.parentFile.exists()) {
            output.parentFile.mkdirs() // verify output exists
        }

        val libraries = libraries.get()
        val licenses = licenses.get()

        // validate found licenses match expectation
        val allowedLicenses = allowedLicenses.getOrElse(emptySet()).map { it.lowercase(Locale.ENGLISH) }
        if (allowedLicenses.isNotEmpty() && strictMode.getOrElse(StrictMode.IGNORE) != StrictMode.IGNORE) {
            // detect all missing licenses
            val missing = mutableListOf<License>()
            licenses.values.forEach {
                val id = it.spdxId?.lowercase(Locale.ENGLISH) ?: it.hash.lowercase(Locale.ENGLISH)
                val name = it.name.lowercase(Locale.ENGLISH)
                val url = it.url?.lowercase(Locale.ENGLISH)
                if (!(allowedLicenses.contains(id) || allowedLicenses.contains(name) || (url?.isNotEmpty() == true && allowedLicenses.contains(url)))) {
                    missing.add(it)
                }
            }

            val missingMapped = mutableMapOf<License, List<Library>>()
            val allowedLicensesMap = allowedLicensesMap.get().mapKeys { (key, _) -> key.lowercase(Locale.ENGLISH) }
            if (allowedLicensesMap.isNotEmpty()) {
                missing.forEach {
                    val id = it.spdxId?.lowercase(Locale.ENGLISH) ?: it.hash.lowercase(Locale.ENGLISH)
                    val name = it.name.lowercase(Locale.ENGLISH)

                    val libsForLicense = allowedLicensesMap[id] ?: allowedLicensesMap[name]
                    if (libsForLicense != null) {
                        val notAllowed = libraries.forLicense(it).filter { lib ->
                            !(libsForLicense.contains(lib.uniqueId) || libsForLicense.contains(lib.groupId) || libsForLicense.contains(lib.artifactId))
                        }
                        if (notAllowed.isNotEmpty()) {
                            missingMapped[it] = notAllowed
                        }
                    } else {
                        missingMapped[it] = libraries.forLicense(it)
                    }
                }
            } else {
                missing.forEach { missingMapped[it] = libraries.forLicense(it) }
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
                if (strictMode.getOrElse(StrictMode.IGNORE) == StrictMode.FAIL) {
                    throw IllegalStateException(message.toString())
                } else {
                    LOGGER.warn(message.toString())
                }
            }
        }

        // write to disk
        ResultContainer(libraries, licenses).writeToDisk(output, includeMetaData.get(), excludeFields.get(), prettyPrint.get())
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesTask::class.java)
    }
}