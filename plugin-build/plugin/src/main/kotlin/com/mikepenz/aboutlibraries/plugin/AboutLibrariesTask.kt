package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_EXPORT_OUTPUT_FILE
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
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory
import java.util.Locale

@CacheableTask
abstract class AboutLibrariesTask : BaseAboutLibrariesTask() {
    @get:Input
    val strictMode = project.objects.property(StrictMode::class.java)
        .convention(project.provider { extension.get().license.strictMode.get() })

    @get:OutputFile
    protected abstract val outputFile: RegularFileProperty

    @get:Optional
    @get:Input
    abstract val deprecated: Property<Boolean>

    override fun getDescription(): String = "Exports dependency meta data from the current module.${variant.orNull?.let { " Filtered by variant: '$it'." } ?: ""}"
    override fun getGroup(): String = super.group ?: org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP

    fun configureOutputFile(outputFile: Provider<RegularFile>? = null) {
        if (outputFile != null && outputFile.isPresent) {
            this.outputFile.set(outputFile)
        } else {
            val projectDirectory = project.layout.projectDirectory
            val buildDirectory = project.layout.buildDirectory

            @Suppress("DEPRECATION")
            val fileNameProvider = project.provider {
                val ext = extension.get()
                val config = ext.exports.findByName(variant.getOrElse(""))
                config?.outputFileName?.orNull ?: ext.export.outputFileName.get()
            }

            val outputFileProvider = project.provider {
                val ext = extension.get()
                val config = ext.exports.findByName(variant.getOrElse(""))
                config?.outputFile?.orNull ?: ext.export.outputFile.orNull
            }

            val providers = project.providers

            // Chain of fallbacks for output file location
            @Suppress("DEPRECATION") val outputFile = providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_OUTPUT_FILE}")
                .map { path -> projectDirectory.file(path) }
                .orElse(
                    providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_OUTPUT_PATH}")
                    .map { path -> projectDirectory.file(path) }
                    .orElse(
                        providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_PATH}")
                        .flatMap { path ->
                            fileNameProvider.map { fileName -> projectDirectory.dir(path).file(fileName) }
                        }
                        .orElse(
                            providers.gradleProperty(PROP_EXPORT_PATH)
                            .flatMap { path ->
                                fileNameProvider.map { fileName -> projectDirectory.dir(path).file(fileName) }
                            }
                            .orElse(
                                outputFileProvider
                                    .orElse(fileNameProvider.map { fileName ->
                                        buildDirectory.dir("generated/aboutLibraries/").get().file(fileName)
                                    })
                            )
                        )
                    )
                )

            @Suppress("DEPRECATION")
            this.outputFile.set(outputFile)
        }
    }

    @TaskAction
    fun action() {
        if (deprecated.isPresent && deprecated.get()) {
            LOGGER.warn("`generateLibraryDefinitions${variant.orElse("").get()}` is deprecated. Please use `exportLibraryDefinitions${variant.orElse("").get()}` instead.")
        }

        val output = outputFile.get().asFile
        if (!output.parentFile.exists()) {
            output.parentFile.mkdirs() // verify output exists
        }

        val postProcessedLibraryData = createLibraryPostProcessor().process()
        val libraries = postProcessedLibraryData.libraries
        val licenses = postProcessedLibraryData.licenses

        // validate found licenses match expectation
        val allowedLicenses = allowedLicenses.get().map { it.lowercase(Locale.ENGLISH) }
        if (allowedLicenses.isNotEmpty() && strictMode.get() != StrictMode.IGNORE) {
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
                if (strictMode.get() == StrictMode.FAIL) {
                    throw IllegalStateException(message.toString())
                } else {
                    LOGGER.warn(message.toString())
                }
            }
        }

        // write to disk
        ResultContainer(libraries, licenses).writeToDisk(
            outputFile = output,
            includeMetaData = includeMetaData.get(),
            excludeFields = excludeFields.get(),
            prettyPrint = prettyPrint.get()
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesTask::class.java)
    }
}
