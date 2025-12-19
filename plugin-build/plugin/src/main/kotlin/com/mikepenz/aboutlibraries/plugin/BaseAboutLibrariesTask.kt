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
import org.gradle.api.tasks.*
import org.slf4j.LoggerFactory

abstract class BaseAboutLibrariesTask : DefaultTask() {

    @Internal
    protected val extension = project.extensions.findByType(AboutLibrariesExtension::class.java)!!

    @Input
    val collectAll = extension.collect.all

    @Input
    val includeTestVariants = extension.collect.includeTestVariants

    @Input
    val includePlatform = extension.collect.includePlatform

    @Input
    val filterVariants = extension.collect.filterVariants

    @get:Optional
    @get:Input
    abstract val variant: Property<String>

    @Input
    val requireLicense = extension.library.requireLicense

    @Input
    val exclusionPatterns = extension.library.exclusionPatterns

    @Input
    val duplicationMode = extension.library.duplicationMode

    @Input
    val duplicationRule = extension.library.duplicationRule

    @Input
    val mapLicensesToSpdx = extension.license.mapLicensesToSpdx

    @Input
    val allowedLicenses = extension.license.allowedLicenses

    @Input
    val allowedLicensesMap = extension.license.allowedLicensesMap

    @Input
    open val offlineMode = extension.offlineMode

    @Suppress("HasPlatformType")
    @Input
    open val fetchRemoteLicense = extension.collect.fetchRemoteLicense.map { it && !offlineMode.get() }

    @Suppress("HasPlatformType")
    @Input
    open val fetchRemoteFunding = extension.collect.fetchRemoteFunding.map { it && !offlineMode.get() }

    @Input
    val additionalLicenses = extension.license.additionalLicenses

    @Input
    @Optional
    val gitHubApiToken = extension.collect.gitHubApiToken

    @get:Input
    abstract val excludeFields: SetProperty<String>

    @get:Input
    abstract val includeMetaData: Property<Boolean>

    @get:Input
    abstract val prettyPrint: Property<Boolean>

    @Optional
    @PathSensitive(value = PathSensitivity.RELATIVE)
    @InputDirectory
    val configPath: DirectoryProperty = extension.collect.configPath

    @get:Internal
    internal abstract val variantToDependencyData: MapProperty<String, List<DependencyData>>

    open fun configure() {
        excludeFields.set(project.provider {
            val config = extension.exports.findByName(variant.getOrElse(""))
            config?.excludeFields?.orNull?.takeIf { it.isNotEmpty() } ?: extension.export.excludeFields.get()
        })

        if (!includeMetaData.isPresent) {
            includeMetaData.set(project.provider {
                val config = extension.exports.findByName(variant.getOrElse(""))
                config?.includeMetaData?.orNull ?: extension.export.includeMetaData.get()
            })
        }

        if (!prettyPrint.isPresent) {
            prettyPrint.set(project.provider {
                val config = extension.exports.findByName(variant.getOrElse(""))
                config?.prettyPrint?.orNull ?: extension.export.prettyPrint.get()
            })
        }

        if (!variant.isPresent) {
            variant.set(
                project.providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_VARIANT}").orElse(
                    project.providers.gradleProperty(PROP_EXPORT_VARIANT).orElse(
                        extension.export.variant
                    )
                )
            )
        }

        val filter = filterVariants.get() + (variant.orNull?.let { arrayOf(it) } ?: emptyArray())

        val dependencies = project.configurations.filterNot { config ->
            config.shouldSkip(includeTestVariants.get())
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
            config.name to DependencyCollector(includePlatform.get())
                .loadDependenciesFromConfiguration(project, config.incoming.resolutionResult.rootComponent)
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
    private fun Configuration.shouldSkip(includeTestVariants: Boolean) = !isCanBeResolved || (!includeTestVariants && isTest)

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
