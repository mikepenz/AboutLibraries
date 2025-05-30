package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_EXPORT_VARIANT
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_PREFIX
import com.mikepenz.aboutlibraries.plugin.util.DependencyCollector
import com.mikepenz.aboutlibraries.plugin.util.DependencyData
import com.mikepenz.aboutlibraries.plugin.util.LibraryPostProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

abstract class BaseAboutLibrariesTask : DefaultTask() {

    @get:Internal
    abstract val extension: Property<AboutLibrariesExtension>

    @get:Input
    abstract val collectAll: Property<Boolean>

    @get:Input
    abstract val includePlatform: Property<Boolean>

    @get:Input
    abstract val filterVariants: SetProperty<String>

    @get:Optional
    @get:Input
    abstract val variant: Property<String?>

    @get:Input
    abstract val exclusionPatterns: SetProperty<Pattern>

    @get:Input
    abstract val duplicationMode: Property<DuplicateMode>

    @get:Input
    abstract val duplicationRule: Property<DuplicateRule>

    @get:Input
    abstract val mapLicensesToSpdx: Property<Boolean>

    @get:Input
    abstract val allowedLicenses: SetProperty<String>

    @get:Input
    abstract val allowedLicensesMap: MapProperty<String, List<String>>

    @get:Input
    abstract val offlineMode: Property<Boolean>

    @get:Input
    abstract val fetchRemoteLicense: Property<Boolean>

    @get:Input
    abstract val fetchRemoteFunding: Property<Boolean>

    @get:Input
    abstract val additionalLicenses: SetProperty<String>

    @get:Input
    @get:Optional
    abstract val gitHubApiToken: Property<String>

    @get:Input
    abstract val excludeFields: SetProperty<String>

    @get:Input
    abstract val includeMetaData: Property<Boolean>

    @get:Input
    abstract val prettyPrint: Property<Boolean>

    @get:Optional
    @get:PathSensitive(value = PathSensitivity.RELATIVE)
    @get:InputDirectory
    abstract val configPath: DirectoryProperty

    @get:Internal
    internal abstract val variantToDependencyData: MapProperty<String, List<DependencyData>>

    @get:Internal
    abstract val configurations: SetProperty<Configuration>

    /**
     * Configure the task with values from the extension
     */
    open fun configureTask() {
        val ext = extension.get()

        // Set basic properties from extension
        collectAll.set(ext.collect.all)
        includePlatform.set(ext.collect.includePlatform)
        filterVariants.set(ext.collect.filterVariants)
        exclusionPatterns.set(ext.library.exclusionPatterns)
        duplicationMode.set(ext.library.duplicationMode)
        duplicationRule.set(ext.library.duplicationRule)
        mapLicensesToSpdx.set(ext.license.mapLicensesToSpdx)
        allowedLicenses.set(ext.license.allowedLicenses)
        allowedLicensesMap.set(ext.license.allowedLicensesMap)
        offlineMode.set(ext.offlineMode)
        additionalLicenses.set(ext.license.additionalLicenses)
        gitHubApiToken.set(ext.collect.gitHubApiToken)
        configPath.set(ext.collect.configPath)

        // Set derived properties
        fetchRemoteLicense.set(ext.collect.fetchRemoteLicense.map { it && !ext.offlineMode.get() })
        fetchRemoteFunding.set(ext.collect.fetchRemoteFunding.map { it && !ext.offlineMode.get() })

        // Configure export properties
        excludeFields.set(project.provider {
            val variantName = variant.getOrElse("")
            val config = ext.exports.findByName(variantName)
            config?.excludeFields?.orNull?.takeIf { it.isNotEmpty() } ?: ext.export.excludeFields.get()
        })

        if (!includeMetaData.isPresent) {
            includeMetaData.set(project.provider {
                val variantName = variant.getOrElse("")
                val config = ext.exports.findByName(variantName)
                config?.includeMetaData?.orNull ?: ext.export.includeMetaData.get()
            })
        }

        if (!prettyPrint.isPresent) {
            prettyPrint.set(project.provider {
                val variantName = variant.getOrElse("")
                val config = ext.exports.findByName(variantName)
                config?.prettyPrint?.orNull ?: ext.export.prettyPrint.get()
            })
        }

        if (!variant.isPresent) {
            variant.set(
                project.providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_VARIANT}").orElse(
                    project.providers.gradleProperty(PROP_EXPORT_VARIANT).orElse(
                        ext.export.variant
                    )
                )
            )
        }

        // Set configurations property
        configurations.set(project.configurations)

        // Call the original configure method to set up dependencies
        configure()
    }

    open fun configure() {
        val filter = filterVariants.get() + (variant.orNull?.let { arrayOf(it) } ?: emptyArray())

        val dependencies = configurations.get().filterNot { config ->
            config.shouldSkip()
        }.filter { config ->
            val cn = config.name
            if (collectAll.get()) {
                // collect configurations for the variants we are interested in
                if (filter.isEmpty() || filter.any { cn.contains(it) }) {
                    LOGGER.info("Collecting dependencies from config: $cn")
                    true
                } else {
                    LOGGER.info("Skipping config: $cn")
                    false
                }
            } else {
                if (cn.endsWith("CompileClasspath", true)) {
                    val variant = cn.removeSuffix("CompileClasspath")
                    if (filter.isEmpty() || filter.contains(variant)) {
                        LOGGER.info("Collecting dependencies for compile time variant $variant from config: $cn")
                        true
                    } else {
                        LOGGER.info("Skipping compile time variant $variant from config: $cn")
                        false
                    }
                } else if (cn.endsWith("RuntimeClasspath", true)) {
                    val variant = cn.removeSuffix("RuntimeClasspath")
                    if (filter.isEmpty() || filter.contains(variant)) {
                        LOGGER.info("Collecting dependencies for runtime variant $variant from config: $cn")
                        true
                    } else {
                        LOGGER.info("Skipping runtime variant $variant from config: $cn")
                        false
                    }
                } else {
                    LOGGER.debug("Skipping configuration $cn")
                    false
                }
            }
        }.associate { config ->
            config.name to DependencyCollector(includePlatform.get()).loadDependenciesFromConfiguration(project, config.incoming.resolutionResult.rootComponent)
        }

        variantToDependencyData.set(project.providers.provider {
            val target = mutableMapOf<String, List<DependencyData>>()
            dependencies.onEach { (name, result) -> target[name] = result.get() }
            target
        })
    }

    internal fun createLibraryPostProcessor(): LibraryPostProcessor {
        val configDirectory = configPath.orNull
        val realPath = if (configDirectory != null) {
            val file = configDirectory.asFile
            if (file.exists()) file else {
                LOGGER.warn("Couldn't find provided path in: '${file.absolutePath}'")
                null
            }
        } else null

        return LibraryPostProcessor(
            variantToDependencyData = variantToDependencyData.get(),
            configFolder = realPath,
            exclusionPatterns = exclusionPatterns.get(),
            offlineMode = offlineMode.get(),
            fetchRemoteLicense = fetchRemoteLicense.get(),
            fetchRemoteFunding = fetchRemoteFunding.get(),
            additionalLicenses = additionalLicenses.get(),
            duplicationMode = duplicationMode.get(),
            duplicationRule = duplicationRule.get(),
            variant = variant.orNull,
            mapLicensesToSpdx = mapLicensesToSpdx.get(),
            gitHubToken = gitHubApiToken.orNull
        )
    }

    /** Skip test and non resolvable configurations */
    private fun Configuration.shouldSkip() = !isCanBeResolved || isTest

    /**
     * Based on the gist by @eygraber https://gist.github.com/eygraber/482e9942d5812e9efa5ace016aac4197
     * Via https://github.com/google/play-services-plugins/blob/master/oss-licenses-plugin/src/main/groovy/com/google/android/gms/oss/licenses/plugin/LicensesTask.groovy
     */
    private val Configuration.isTest
        get() = name.startsWith("test", ignoreCase = true) ||
            name.startsWith("androidTest", ignoreCase = true) ||
            hierarchy.any { configurationHierarchy ->
                setOf("testCompile", "androidTestCompile").any { configurationHierarchy.name.contains(it, ignoreCase = true) }
            }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(BaseAboutLibrariesTask::class.java)!!
    }
}
