package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_EXPORT_VARIANT
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_PREFIX
import com.mikepenz.aboutlibraries.plugin.util.DependencyCollector
import com.mikepenz.aboutlibraries.plugin.util.DependencyCoordinates
import com.mikepenz.aboutlibraries.plugin.util.LibraryPostProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.slf4j.LoggerFactory
import java.io.File

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

    /**
     * Names of the Gradle configurations selected during [configure].
     */
    @get:Internal
    internal abstract val configurationNames: ListProperty<String>

    /**
     * Maps configuration name → pipe-separated "group:artifact:version" coordinate keys.
     *
     * Marked `@Input` so the task build-cache key reflects which dependency versions are present.
     * A version change produces a different value and triggers a cache miss.
     *
     * Populated lazily via `resolutionResult.rootComponent.map {}` — fully config-cache safe.
     */
    @get:Input
    internal abstract val configToCoordinateKeys: MapProperty<String, String>

    /**
     * Maps "group:artifact:version" → absolute path of the resolved POM file for every external
     * module dependency (including transitively discovered parent POMs).
     *
     * Populated at configuration time via detached configurations with `@pom` notation, wrapped
     * in a `project.provider {}` so the actual resolution is deferred until Gradle realises this
     * property. The resolution is evaluated at configuration time (during config-cache store),
     * so no `Project` access happens at execution time.
     *
     * Approach mirrors google/play-services-plugins#365 (`oss-licenses-plugin`), which pre-resolves
     * artifact-to-file mappings at configuration time and exposes them as a lazy task property.
     *
     * Marked `@Internal` because the actual file content is tracked via [pomFiles]; this map
     * only stores the coordinate→path lookup for execution-time use.
     */
    @get:Internal
    internal abstract val pomFileMap: MapProperty<String, String>

    /**
     * Resolved POM files for all external module dependencies.
     *
     * This is the proper Gradle input declaration for content-addressed UP-TO-DATE tracking:
     * if any POM file's content changes (or new POMs are added/removed) the task will be
     * re-executed. Path-sensitivity is `NONE` so cache keys are portable across machines.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val pomFiles: ConfigurableFileCollection

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

        val selectedConfigs = project.configurations.filterNot { config ->
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
        }

        configurationNames.set(selectedConfigs.map { it.name })

        val capturedIncludePlatform = includePlatform.get()
        for (config in selectedConfigs) {
            // Per-config coordinate keys: resolved lazily from the resolution result.
            // Encoded as "|"-separated "group:artifact:version" strings so the MapProperty value
            // type stays a plain String — fully config-cache serializable.
            val coordKeysProvider = config.incoming.resolutionResult.rootComponent.map { root ->
                DependencyCollector(capturedIncludePlatform)
                    .loadDependencyCoordinates(root)
                    .map { it.cacheKey() }
                    .sorted()
                    .joinToString("|")
            }
            configToCoordinateKeys.put(config.name, coordKeysProvider)
        }

        // Populate pomFileMap via detached configurations + parent POM walking, wrapped in
        // `project.provider {}`. The provider is evaluated at configuration time (when the value
        // is realised during config-cache store), so all project access happens during the
        // configuration phase — never at task execution time.
        //
        // This pattern mirrors google/play-services-plugins#365 (the oss-licenses-plugin), which
        // uses the same approach for config-cache-compatible POM resolution.
        pomFileMap.putAll(project.provider { resolvePomFiles(selectedConfigs) })

        // Register the actual POM files as @InputFiles for content-addressed UP-TO-DATE tracking.
        // Derive from `pomFileMap` (rather than re-running `resolvePomFiles`) so the heavy
        // resolution happens exactly once.  `MapProperty.map {}` shares the underlying value with
        // any other consumer of the same property.
        pomFiles.from(pomFileMap.map { map -> map.values.map { path -> File(path) } })
        pomFiles.finalizeValueOnRead()
    }

    /**
     * Resolves POM files for every external module dependency in [configs], including parent
     * POMs reachable via the `<parent>` element.
     *
     * Approach (similar to the OLD detached-configuration pattern but evaluated at configuration
     * time so it remains config-cache safe):
     *
     *  1. Walk every [configs] resolution result and gather module coordinates with
     *     [DependencyCollector.loadDependencyCoordinates] (this also picks up platform / BOM
     *     dependencies, which are otherwise filtered out by attribute-typed artifact views).
     *
     *  2. Fetch each coordinate's POM via a detached configuration using `@pom` notation. This is
     *     the only Gradle API that reliably returns POM files for all module types (regular libs,
     *     platforms, BOMs, classifier deps).
     *
     *  3. Parse each fetched POM with [MavenXpp3Reader], collect parent coordinates, queue any
     *     unseen parents, and repeat steps 2-3 until the queue is empty. This pre-resolves the
     *     full parent-POM hierarchy so the execution-time Maven Model Builder can produce the
     *     same library metadata (developers, organization, scm, …) the OLD code produced via
     *     its on-demand `ModelResolver`.
     */
    private fun resolvePomFiles(configs: List<Configuration>): Map<String, String> {
        val capturedIncludePlatform = includePlatform.get()

        // Phase 1: collect coordinates for every resolved external dependency.
        val initialCoordinates = mutableSetOf<DependencyCoordinates>()
        for (config in configs) {
            val root = config.incoming.resolutionResult.rootComponent.get()
            DependencyCollector(capturedIncludePlatform)
                .loadDependencyCoordinates(root)
                .forEach { initialCoordinates.add(it) }
        }
        if (initialCoordinates.isEmpty()) return emptyMap()

        val pomMap = mutableMapOf<String, String>()
        val parentsToFetch = ArrayDeque<DependencyCoordinates>()
        val attempted = mutableSetOf<String>()

        // Phase 2: fetch the initial coordinates. Coordinates are aggregated across multiple
        // configurations (e.g. debugCompileClasspath + releaseRuntimeClasspath), which can
        // legitimately contribute the same `group:artifact` at different versions. Adding all
        // versions to a single detached configuration would let Gradle's conflict resolver
        // pick one version and silently discard the others. Partition by `group:artifact`:
        // batch all g:a that have a single version (the common case — fast path), and fetch any
        // g:a with multiple versions one coordinate at a time (same approach as Phase 3).
        val byGa = initialCoordinates.groupBy { "${it.group}:${it.artifact}" }
        val (uniqueGa, conflictingGa) = byGa.values.partition { it.size == 1 }
        fetchPomBatch(uniqueGa.flatten(), pomMap, parentsToFetch, attempted)
        conflictingGa.flatten().forEach { coord ->
            fetchPomBatch(listOf(coord), pomMap, parentsToFetch, attempted)
        }

        // Phase 3: fetch parent POMs ONE AT A TIME. Parents from different dependencies can
        // legitimately reference different versions of the same group:artifact (e.g.
        // guava-parent:33.3.1-jre vs guava-parent:26.0-android). Adding both to a single detached
        // configuration causes Gradle to silently pick one and discard the other, so each parent
        // gets its own detached configuration.
        while (parentsToFetch.isNotEmpty()) {
            val coord = parentsToFetch.removeFirst()
            if (!attempted.add(coord.cacheKey())) continue
            fetchPomBatch(listOf(coord), pomMap, parentsToFetch, attempted)
        }

        return pomMap
    }

    /**
     * Resolves the [batch] of POM coordinates into a single detached configuration, populating
     * [pomMap] with `<group:artifact:version> → absolute path` entries and queueing any newly
     * discovered parent coordinates onto [parentsToFetch].
     */
    private fun fetchPomBatch(
        batch: List<DependencyCoordinates>,
        pomMap: MutableMap<String, String>,
        parentsToFetch: ArrayDeque<DependencyCoordinates>,
        attempted: MutableSet<String>,
    ) {
        if (batch.isEmpty()) return
        // Mark every coordinate in this batch as attempted so we don't requeue them on failure.
        batch.forEach { attempted.add(it.cacheKey()) }

        val pomDeps = batch.map { project.dependencies.create("${it.group}:${it.artifact}:${it.version}@pom") }.toTypedArray()
        val detached = project.configurations.detachedConfiguration(*pomDeps).apply {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        try {
            val artifacts = detached.incoming.artifactView { view -> view.lenient(true) }.artifacts
            for (artifact in artifacts) {
                val id = artifact.id.componentIdentifier as? ModuleComponentIdentifier ?: continue
                val key = "${id.group}:${id.module}:${id.version}"
                if (pomMap.containsKey(key)) continue
                val file = artifact.file
                pomMap[key] = file.absolutePath

                // Parse the POM to discover the parent and queue it for individual fetching.
                val parent = readParent(file) ?: continue
                val parentCoords = DependencyCoordinates(parent.first, parent.second, parent.third)
                if (!attempted.contains(parentCoords.cacheKey())) {
                    parentsToFetch.add(parentCoords)
                }
            }
        } catch (e: Exception) {
            LOGGER.warn("Failed to resolve POM batch (${batch.joinToString { it.cacheKey() }}); some library metadata may be incomplete", e)
        }
    }

    /** Returns the parent (groupId, artifactId, version) of [pomFile], or null if absent. */
    private fun readParent(pomFile: File): Triple<String, String, String>? {
        return try {
            val model = pomFile.inputStream().use { MavenXpp3Reader().read(it) }
            val p = model.parent ?: return null
            if (p.groupId.isNullOrEmpty() || p.artifactId.isNullOrEmpty() || p.version.isNullOrEmpty()) null
            else Triple(p.groupId, p.artifactId, p.version)
        } catch (e: Exception) {
            LOGGER.debug("Failed to parse POM ${pomFile.name}: ${e.message}")
            null
        }
    }

    /**
     * Builds a [LibraryPostProcessor] using only serialized task properties — no [project] access.
     *
     * Both [configToCoordinateKeys] and [pomFileMap] are `@Input` properties whose values were
     * computed at configuration time. At execution time we simply read them, making the method
     * fully compatible with Gradle's configuration cache and project-isolation requirements.
     */
    internal fun createLibraryPostProcessor(): LibraryPostProcessor {
        val configDirectory = configPath.orNull
        val realPath = if (configDirectory != null) {
            val file = configDirectory.asFile
            if (file.exists()) file else {
                LOGGER.warn("Couldn't find provided path in: '${file.absolutePath}'")
                null
            }
        } else null

        if (LOGGER.isDebugEnabled) LOGGER.debug("==> ABOUTLIBRARIES: Resolving dependency data at execution time")

        val resolvedKeyMap: Map<String, String> = configToCoordinateKeys.get()
        val resolvedPomFileMap: Map<String, File> = pomFileMap.get().mapValues { (_, path) -> File(path) }

        val variantToDependencyData = resolvedKeyMap.mapValues { (_, coordKeysStr) ->
            val coords = coordKeysStr.split("|").filter { it.isNotEmpty() }.mapNotNull { key ->
                val parts = key.split(":")
                if (parts.size == 3) DependencyCoordinates(parts[0], parts[1], parts[2]) else null
            }.toSet()
            DependencyCollector(includePlatform.get())
                .loadDependenciesFromCoordinates(coords, resolvedPomFileMap)
        }

        if (LOGGER.isDebugEnabled) LOGGER.debug("==> ABOUTLIBRARIES: Dependency resolution complete")

        return LibraryPostProcessor(
            variantToDependencyData = variantToDependencyData,
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

    /** Skip test and non-resolvable configurations */
    private fun Configuration.shouldSkip(includeTestVariants: Boolean) = !isCanBeResolved || (!includeTestVariants && isTest)

    /**
     * Determines whether a configuration is a test configuration by its name.
     *
     * Previously this traversed `config.hierarchy` to catch configurations that extend
     * `testCompile`/`androidTestCompile` without having "test" in their own name. That traversal
     * was removed because it forced configuration graph realisation. We instead match by name,
     * covering both JVM (`testCompileClasspath`, `testRuntimeClasspath`) and Android variant
     * naming (`debugAndroidTestCompileClasspath`, `releaseUnitTestRuntimeClasspath`, etc.).
     */
    private val Configuration.isTest
        get() = name.startsWith("test", ignoreCase = true) ||
            name.contains("androidTest", ignoreCase = true) ||
            name.contains("unitTest", ignoreCase = true)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BaseAboutLibrariesTask::class.java)!!
    }
}
