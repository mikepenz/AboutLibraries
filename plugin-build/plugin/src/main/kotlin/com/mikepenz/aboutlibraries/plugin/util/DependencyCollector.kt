package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.mapping.Developer
import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.Organization
import com.mikepenz.aboutlibraries.plugin.mapping.Scm
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.Parent
import org.apache.maven.model.Repository
import org.apache.maven.model.building.DefaultModelBuilderFactory
import org.apache.maven.model.building.DefaultModelBuildingRequest
import org.apache.maven.model.building.FileModelSource
import org.apache.maven.model.building.ModelBuildingRequest
import org.apache.maven.model.building.ModelSource2
import org.apache.maven.model.resolution.ModelResolver
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE
import org.gradle.api.attributes.Category.ENFORCED_PLATFORM
import org.gradle.api.attributes.Category.REGULAR_PLATFORM
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

internal class DependencyCollector(
    private val includePlatform: Boolean = false,
) {

    /**
     * Phase 1 (configuration time): extract the set of dependency coordinates from [root].
     *
     * This is called inside a `resolutionResult.rootComponent.map {}` provider chain so the
     * result is captured as a config-cache-serializable `Provider<List<DependencyCoordinates>>`.
     * No project-derived objects are retained after this call.
     */
    internal fun loadDependencyCoordinates(root: ResolvedComponentResult): Set<DependencyCoordinates> {
        val coordinates = mutableSetOf<DependencyCoordinates>()
        loadDependencyCoordinates(root, coordinates, mutableSetOf())
        return coordinates
    }

    /**
     * Phase 2 (execution time): parse POM metadata for the given [coordinates] using the
     * pre-resolved [pomFileMap] ("group:artifact:version" → local [File]).
     *
     * Parent POMs not present in [pomFileMap] are satisfied with a minimal synthetic POM so that
     * the Maven Model Builder can complete without triggering any network access.
     *
     * No [org.gradle.api.Project] or other project-derived objects are required here, making this
     * call fully config-cache and project-isolation safe.
     */
    internal fun loadDependenciesFromCoordinates(
        coordinates: Set<DependencyCoordinates>,
        pomFileMap: Map<String, File>,
    ): List<DependencyData> {
        return coordinates.getPomInfo(pomFileMap)
    }

    /**
     * Loads dependency coordinates from the given root component (recursive).
     * Original Code is based on: https://github.com/cashapp/licensee/blob/1.13.0/src/main/kotlin/app/cash/licensee/dependencyGraph.kt#L42C14-L42C39
     * Copyright (C) 2021 Square, Inc.
     */
    private fun loadDependencyCoordinates(
        root: ResolvedComponentResult,
        destination: MutableSet<DependencyCoordinates>,
        seen: MutableSet<ComponentIdentifier>,
        depth: Int = 1,
    ) {
        val id = root.id
        var ignoreSuffix: String? = null
        when {
            id is ProjectComponentIdentifier -> {
                ignoreSuffix = " skip project dependency" // Local dependency, do nothing.
            }

            root.isPlatform() -> {
                if (includePlatform) {
                    if (id is ModuleComponentIdentifier) {
                        destination += id.toDependencyCoordinates()
                    } else {
                        LOGGER.error("Unknown platform dependency: $id")
                        ignoreSuffix = " skip platform" // Platform (POM) dependency, do nothing.
                    }
                } else {
                    ignoreSuffix = " skip platform" // Platform (POM) dependency, do nothing.
                }
            }

            id is ModuleComponentIdentifier -> {
                if (id.group == "" && id.version == "") {
                    ignoreSuffix = " skip flat-dir dependency" // Assuming flat-dir repository dependency, do nothing.
                } else {
                    destination += id.toDependencyCoordinates()
                }
            }

            else -> error("Unknown dependency ${id::class.java}: $id")
        }

        if (LOGGER.isInfoEnabled) {
            LOGGER.info(
                buildString {
                    repeat(depth) { append("  ") }
                    append(id)
                    if (ignoreSuffix != null) append(ignoreSuffix)
                },
            )
        }

        for (dependency in root.dependencies) {
            if (dependency is ResolvedDependencyResult) {
                val selected = dependency.selected
                if (seen.add(selected.id)) {
                    loadDependencyCoordinates(
                        selected,
                        destination,
                        seen,
                        depth + 1,
                    )
                }
            }
        }
    }

    /**
     * Parses POM metadata for all coordinates using the pre-resolved [pomFileMap].
     *
     * Uses: https://maven.apache.org/ref/3.8.8/maven-model-builder/ to construct the pom model.
     * Parent POMs not present in [pomFileMap] fall back to a synthetic minimal POM.
     *
     * Original Code is based on: https://github.com/cashapp/licensee/blob/1.13.0/src/main/kotlin/app/cash/licensee/dependencyGraph.kt#L42C14-L42C39
     * Copyright (C) 2021 Square, Inc.
     */
    private fun Set<DependencyCoordinates>.getPomInfo(
        pomFileMap: Map<String, File>,
    ): List<DependencyData> {
        val builder = DefaultModelBuilderFactory().newInstance()
        val resolver = object : ModelResolver {
            fun resolve(coords: DependencyCoordinates): ModelSource2 {
                val file = pomFileMap[coords.cacheKey()]
                return if (file != null) FileModelSource(file) else syntheticModelSource(coords)
            }

            override fun resolveModel(groupId: String, artifactId: String, version: String): ModelSource2 =
                resolve(DependencyCoordinates(groupId, artifactId, version))

            override fun resolveModel(parent: Parent): ModelSource2 =
                resolve(DependencyCoordinates(parent.groupId, parent.artifactId, parent.version))

            override fun resolveModel(dependency: Dependency): ModelSource2 =
                resolve(DependencyCoordinates(dependency.groupId, dependency.artifactId, dependency.version))

            override fun addRepository(repository: Repository) {}
            override fun addRepository(repository: Repository, replace: Boolean) {}
            override fun newCopy(): ModelResolver = this
        }

        return mapNotNull { coordinates ->
            val file = pomFileMap[coordinates.cacheKey()] ?: run {
                LOGGER.debug("No POM file found for {}, skipping", coordinates.cacheKey())
                return@mapNotNull null
            }
            val req = DefaultModelBuildingRequest().apply {
                isProcessPlugins = false
                pomFile = file
                isTwoPhaseBuilding = true
                modelResolver = resolver
                validationLevel = ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL
            }
            val result = builder.build(req)
            loadLibraryFromPom(coordinates, result.effectiveModel) { modelId ->
                result.getRawModel(modelId)
            }
        }
    }

    private fun loadParentPoms(
        pom: Model,
        getRawModel: (String) -> Model?,
    ): List<Model> {
        val parentModels = mutableListOf<Model>()
        var currentModel: Model? = pom
        while (currentModel?.parent != null) {
            val parent = currentModel.parent
            if (LOGGER.isDebugEnabled) LOGGER.debug("--> ArtifactPom ParentPom for [{}:{}]:{}:{}", pom.groupId, pom.artifactId, parent.groupId, parent.artifactId)
            currentModel = getRawModel("${parent.groupId}:${parent.artifactId}:${parent.version}")?.also {
                parentModels.add(it)
            }
        }
        return parentModels
    }

    private fun loadLibraryFromPom(
        coordinates: DependencyCoordinates,
        pom: Model,
        getRawModel: (String) -> Model?,
    ): DependencyData? {
        val parentRawModel = loadParentPoms(pom, getRawModel)

        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("--> ArtifactPom for [{}:{}]:\n{}\n\n", pom.groupId, pom.artifactId, pom.pomFile.readText().trim())
        }

        val uniqueId = pom.groupId + ":" + pom.artifactId

        // check if we shall skip this specific uniqueId
        if (shouldSkip(uniqueId)) return null

        val libraryName = chooseStringValue(pom, parentRawModel) { it.name } ?: run {
            LOGGER.info("Could not get the name for ${uniqueId}! Fallback to '$uniqueId'")
            uniqueId
        }
        val libraryDescription = chooseStringValue(pom, parentRawModel) { it.description } ?: ""
        val artifactVersion = chooseStringValue(pom, parentRawModel) { it.version }
        if (artifactVersion.isNullOrBlank()) LOGGER.info("----> Failed to identify version for: $uniqueId")
        val libraryWebsite = chooseStringValue(pom, parentRawModel) { it.url }

        val licenses = chooseValue(pom, parentRawModel) { it.licenses }?.map {
            if (it.name == null) LOGGER.info("----> License name was null url: ${it.url} for: $uniqueId")
            License(it.name ?: "", it.url)
        }?.toHashSet() ?: emptySet()
        val scm = chooseValue(pom, parentRawModel) { it.scm }?.let { Scm(it.connection, it.developerConnection, it.url) }
        val developers = chooseValue(pom, parentRawModel) { it.developers }?.map { Developer(it.name, it.organizationUrl) }?.toHashSet()?.toList() ?: emptyList()
        val organization = chooseValue(pom, parentRawModel) { it.organization }?.let { Organization(it.name, it.url) }
        return DependencyData(
            dependencyCoordinates = coordinates,
            uniqueId = uniqueId,
            artifactVersion = artifactVersion,
            name = libraryName,
            description = libraryDescription,
            website = libraryWebsite,
            developers = developers,
            organization = organization,
            scm = scm,
            licenses = licenses,
            artifactFolder = pom.pomFile.parentFile?.parentFile,
        )
    }

    private fun chooseStringValue(pom: Model, parentRawModel: List<Model>, block: (Model) -> String?): String? {
        return chooseValue(pom, parentRawModel) { block(it).takeIf { v -> v?.isNotEmpty() == true } }
    }

    private fun <T> chooseValue(pom: Model, parentRawModel: List<Model>, block: (Model) -> T?): T? =
        pom.let(block) ?: parentRawModel.firstOrNull()?.let(block)

    private fun ModuleComponentIdentifier.toDependencyCoordinates() = DependencyCoordinates(group, module, version)

    private fun ResolvedComponentResult.isPlatform(): Boolean {
        val singleVariant = variants.singleOrNull() ?: return false
        val stringAttribute = Attribute.of(CATEGORY_ATTRIBUTE.name, String::class.java)  // https://github.com/gradle/gradle/issues/8854
        val category = singleVariant.attributes.getAttribute(stringAttribute) ?: return false
        return when (category) {
            ENFORCED_PLATFORM, REGULAR_PLATFORM -> true
            else -> false
        }
    }

    /** Skip the AboutLibraries library itself so it doesn't appear in its own output. */
    private fun shouldSkip(uniqueId: String): Boolean {
        return uniqueId == "com.mikepenz:aboutlibraries" || uniqueId == "com.mikepenz:aboutlibraries-definitions"
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DependencyCollector::class.java)!!

        /**
         * Returns a minimal valid POM [ModelSource2] for [coords] when the real POM file is not
         * available in the pre-fetched set (e.g. parent POMs that were not resolved as direct
         * dependencies).  This prevents the Maven Model Builder from attempting network access.
         */
        internal fun syntheticModelSource(coords: DependencyCoordinates): ModelSource2 {
            val xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>${coords.group}</groupId>
                  <artifactId>${coords.artifact}</artifactId>
                  <version>${coords.version}</version>
                </project>
            """.trimIndent()
            val bytes = xml.toByteArray(Charsets.UTF_8)
            val location = coords.cacheKey()
            return object : ModelSource2 {
                override fun getInputStream(): InputStream = ByteArrayInputStream(bytes)
                override fun getLocation(): String = location
                override fun getLocationURI(): java.net.URI = java.net.URI.create("synthetic:$location")
                override fun getRelatedSource(relPath: String): ModelSource2? = null
            }
        }
    }
}

internal data class DependencyCoordinates(
    val group: String,
    val artifact: String,
    val version: String,
) : java.io.Serializable {
    fun pomCoordinate() = "$group:$artifact:$version@pom"
    fun cacheKey() = "$group:$artifact:$version"
}

internal data class DependencyData(
    val dependencyCoordinates: DependencyCoordinates,
    var uniqueId: String,
    var artifactVersion: String?,
    var name: String?,
    var description: String?,
    var website: String?,
    var developers: List<Developer>,
    var organization: Organization?,
    var scm: Scm?,
    var licenses: Set<License> = emptySet(),
    var funding: Set<Funding> = emptySet(),
    var tag: String? = null,
    var artifactFolder: File? = null,
)
