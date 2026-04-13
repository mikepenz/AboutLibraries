package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_EXPORT_VARIANT
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_PREFIX
import com.mikepenz.aboutlibraries.plugin.util.DependencyCollector
import com.mikepenz.aboutlibraries.plugin.util.DependencyCoordinates
import com.mikepenz.aboutlibraries.plugin.util.DependencyData
import com.mikepenz.aboutlibraries.plugin.util.LibraryPostProcessor
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.work.DisableCachingByDefault
import org.slf4j.LoggerFactory
import java.io.File

@DisableCachingByDefault(because = "Abstract base task; concrete subclasses opt in via @CacheableTask")
abstract class BaseAboutLibrariesTask : DefaultTask() {

    /**
     * Getter-only accessor for the project extension. Implemented as a `get()` (not a backing
     * field) so the extension instance is not serialized into the configuration cache. Each
     * call resolves it fresh from `project.extensions`, which is only safe during the
     * configuration phase — every consumer below is field-initializer or [configure] scoped.
     */
    @get:Internal
    protected val extension: AboutLibrariesExtension
        get() = project.extensions.getByType(AboutLibrariesExtension::class.java)

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
    open val offlineMode: Property<Boolean> = extension.offlineMode

    @Input
    open val fetchRemoteLicense: Provider<Boolean> = extension.collect.fetchRemoteLicense.map { it && !offlineMode.get() }

    @Input
    open val fetchRemoteFunding: Provider<Boolean> = extension.collect.fetchRemoteFunding.map { it && !offlineMode.get() }

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
     * Maps configuration name → list of [DependencyCoordinates] for all external module
     * dependencies in that configuration.
     *
     * Stored as a typed `MapProperty<String, List<DependencyCoordinates>>` (rather than a
     * pipe-encoded `MapProperty<String, String>`) so the value is consumed without any
     * string parsing at execution time. [DependencyCoordinates] is `java.io.Serializable`,
     * which is all the configuration cache requires.
     *
     * Marked `@Input` so the task build-cache key reflects which dependency versions are
     * present — a version change produces a different value and triggers a cache miss.
     */
    @get:Input
    internal abstract val configToCoordinates: MapProperty<String, List<DependencyCoordinates>>

    /**
     * Maps "group:artifact:version" → absolute path of the resolved POM file for every external
     * module dependency (including transitively discovered parent POMs).
     *
     * Populated eagerly during [configure] via detached configurations + parent POM walking.
     * Eager evaluation guarantees no `Configuration` instances are captured into provider
     * closures and serialized into the configuration cache.
     *
     * Approach mirrors google/play-services-plugins#365 (`oss-licenses-plugin`), which
     * pre-resolves artifact-to-file mappings at configuration time and exposes them as a lazy
     * task property.
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

        // Single-pass dependency-graph walk: compute per-configuration coordinates AND the
        // union set used for POM resolution in one traversal per configuration. The previous
        // implementation walked the graph twice (once for `configToCoordinateKeys`, once
        // inside `resolvePomFiles`).
        val capturedIncludePlatform = includePlatform.get()
        val collector = DependencyCollector(capturedIncludePlatform)
        val perConfigCoords = LinkedHashMap<String, List<DependencyCoordinates>>(selectedConfigs.size)
        val unionCoords = LinkedHashSet<DependencyCoordinates>()
        for (config in selectedConfigs) {
            val root = config.incoming.resolutionResult.rootComponent.get()
            val coords = collector.loadDependencyCoordinates(root)
            perConfigCoords[config.name] = coords.toList()
            unionCoords.addAll(coords)
        }
        configToCoordinates.set(perConfigCoords)

        // Resolve POM files eagerly at configuration time. Eager resolution avoids capturing
        // any `Configuration` references inside a `project.provider {}` closure (which would
        // otherwise live in the configuration-cache state until store time). All Gradle API
        // access happens here, in [configure], which itself runs lazily via the
        // `tasks.named { … }` task-realization mechanism.
        val resolvedPomMap = if (unionCoords.isEmpty()) emptyMap() else resolvePomFiles(unionCoords)
        pomFileMap.set(resolvedPomMap)

        // Register the actual POM files as @InputFiles for content-addressed UP-TO-DATE tracking.
        pomFiles.from(resolvedPomMap.values.map { File(it) })
        pomFiles.finalizeValueOnRead()
    }

    /**
     * Resolves POM files for every coordinate in [initialCoordinates], including parent POMs
     * reachable via the `<parent>` element.
     *
     * Detached configurations are batched in **version slots** to keep their count bounded by
     * the maximum number of conflicting versions per `group:artifact`, rather than the total
     * coordinate count. Slot 0 contains the first version of each `g:a`, slot 1 the second,
     * and so on. Within a slot every coordinate is unique by `g:a`, so Gradle's conflict
     * resolver cannot silently discard versions.
     *
     * Phase 1 fetches the initial union of coordinates collected from every configuration.
     * Phase 2 repeatedly drains the parent-POM queue and fetches each level in slot batches
     * until no new parents are discovered.
     */
    private fun resolvePomFiles(initialCoordinates: Set<DependencyCoordinates>): Map<String, String> {
        val pomMap = LinkedHashMap<String, String>(initialCoordinates.size * 2)
        val parentsToFetch = ArrayDeque<DependencyCoordinates>()
        val attempted = HashSet<String>(initialCoordinates.size * 2)

        // Phase 1: initial coordinates.
        fetchInVersionSlots(initialCoordinates, pomMap, parentsToFetch, attempted)

        // Phase 2: parent POMs, drained one level at a time so parents-of-parents land in
        // the next iteration. Each level is itself batched into version slots.
        while (parentsToFetch.isNotEmpty()) {
            val level = ArrayList<DependencyCoordinates>(parentsToFetch.size)
            while (parentsToFetch.isNotEmpty()) level.add(parentsToFetch.removeFirst())
            fetchInVersionSlots(level, pomMap, parentsToFetch, attempted)
        }

        return pomMap
    }

    /**
     * Partitions [coordinates] by `group:artifact` and fetches them in version slots: each
     * slot becomes a single detached configuration containing at most one version per
     * `group:artifact`, so Gradle's conflict resolver leaves every requested version intact.
     */
    private fun fetchInVersionSlots(
        coordinates: Collection<DependencyCoordinates>,
        pomMap: MutableMap<String, String>,
        parentsToFetch: ArrayDeque<DependencyCoordinates>,
        attempted: MutableSet<String>,
    ) {
        if (coordinates.isEmpty()) return
        val byGa = LinkedHashMap<String, ArrayList<DependencyCoordinates>>()
        for (coord in coordinates) {
            if (coord.cacheKey() in attempted) continue
            byGa.getOrPut("${coord.group}:${coord.artifact}") { ArrayList(1) }.add(coord)
        }
        if (byGa.isEmpty()) return
        val maxSlots = byGa.values.maxOf { it.size }
        for (slot in 0 until maxSlots) {
            val batch = ArrayList<DependencyCoordinates>(byGa.size)
            for (versions in byGa.values) {
                if (slot < versions.size) batch.add(versions[slot])
            }
            if (batch.isNotEmpty()) fetchPomBatch(batch, pomMap, parentsToFetch, attempted)
        }
    }

    /**
     * Resolves the [batch] of POM coordinates into a single detached configuration, populating
     * [pomMap] with `<group:artifact:version> → absolute path` entries and queueing any newly
     * discovered parent coordinates onto [parentsToFetch].
     *
     * The caller guarantees [batch] contains at most one version per `group:artifact`.
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
            // `@pom` deps don't pull transitives, but disabling explicitly skips graph-build.
            isTransitive = false
        }

        try {
            val artifacts = detached.incoming.artifactView { view -> view.lenient(true) }.artifacts
            for (artifact in artifacts) {
                val id = artifact.id.componentIdentifier as? ModuleComponentIdentifier ?: continue
                val key = "${id.group}:${id.module}:${id.version}"
                if (pomMap.containsKey(key)) continue
                val file = artifact.file
                pomMap[key] = file.absolutePath

                // Parse the POM to discover the parent and queue it for the next level.
                val parent = readParent(file) ?: continue
                val parentCoords = DependencyCoordinates(parent.first, parent.second, parent.third)
                if (parentCoords.cacheKey() !in attempted) {
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
     * Both [configToCoordinates] and [pomFileMap] are task properties whose values were
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

        val resolvedPerConfigCoords: Map<String, List<DependencyCoordinates>> = configToCoordinates.get()
        val resolvedPomFileMap: Map<String, File> = pomFileMap.get().mapValues { (_, path) -> File(path) }

        // Parse each unique coordinate exactly once across all configurations. Without this,
        // overlapping classpaths (e.g. compile + runtime sharing the same deps) would re-run
        // the Maven Model Builder for each occurrence — a measurable execution-time cost on
        // larger projects.
        val allCoords: Set<DependencyCoordinates> = resolvedPerConfigCoords.values.flatten().toSet()
        val parsedByCoord: Map<DependencyCoordinates, DependencyData> = DependencyCollector(includePlatform.get())
            .loadDependenciesFromCoordinates(allCoords, resolvedPomFileMap)
            .associateBy { it.dependencyCoordinates }
        val variantToDependencyData = resolvedPerConfigCoords.mapValues { (_, coords) ->
            coords.mapNotNull { parsedByCoord[it] }
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
     * Determines whether a configuration is a test configuration.
     *
     * Combines three independent checks so all real-world Gradle/AGP/KMP test classpath naming
     * conventions are caught:
     *
     *  1. `name.startsWith("test", ignoreCase)` — plain JVM (`testCompileClasspath`,
     *     `testRuntimeClasspath`) and any variant beginning with "test".
     *  2. `name.contains("Test")` (case-sensitive) — camelCase test configs that don't start
     *     with "test", including KMP source sets (`jvmTestCompileClasspath`,
     *     `desktopTestRuntimeClasspath`, `iosTestCompileClasspath`, …) and Android variants
     *     (`debugAndroidTestCompileClasspath`, `releaseUnitTestRuntimeClasspath`,
     *     `androidUnitTestRuntimeClasspath`, …). Case-sensitive matching avoids false positives
     *     on unrelated names like `attestation` or `latest`.
     *  3. Hierarchy traversal — safety net for non-standard configs that extend `testCompile`
     *     or `androidTestCompile` without having "test" in their own name. This forces some
     *     configuration realisation, but it is necessary for correctness on edge cases.
     *
     * Based on the gist by @eygraber https://gist.github.com/eygraber/482e9942d5812e9efa5ace016aac4197
     * Via https://github.com/google/play-services-plugins/blob/master/oss-licenses-plugin/src/main/groovy/com/google/android/gms/oss/licenses/plugin/LicensesTask.groovy
     */
    private val Configuration.isTest
        get() = name.startsWith("test", ignoreCase = true) ||
            name.contains("Test") ||
            hierarchy.any { parent ->
                parent.name.contains("testCompile", ignoreCase = true) ||
                    parent.name.contains("androidTestCompile", ignoreCase = true)
            }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BaseAboutLibrariesTask::class.java)!!
    }
}
