package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.mapping.*
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.Parent
import org.apache.maven.model.Repository
import org.apache.maven.model.building.*
import org.apache.maven.model.resolution.ModelResolver
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.artifacts.result.ResolvedVariantResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category.*
import org.gradle.api.provider.Provider
import org.slf4j.LoggerFactory
import java.io.File

internal class DependencyCollector(
    private val includePlatform: Boolean = false,
) {
    private val handledLibraries = HashSet<String>()

    internal fun loadDependenciesFromConfiguration(
        project: Project,
        root: Provider<ResolvedComponentResult>,
    ): Provider<List<DependencyData>> {
        val dependencies = project.dependencies
        val configurations = project.configurations
        val pomInfos: Provider<List<DependencyData>> = root.map { root ->
            val directDependencies = loadDependencyCoordinates(root)
            val directPomFiles = directDependencies.fetchPomFiles(root.variants, dependencies, configurations)
            directPomFiles.getPomInfo(root.variants, dependencies, configurations)
        }
        return pomInfos
    }

    /** Loads dependency coordinates from the given root component. */
    private fun loadDependencyCoordinates(root: ResolvedComponentResult): Set<DependencyCoordinates> {
        val coordinates = mutableSetOf<DependencyCoordinates>()
        loadDependencyCoordinates(root, coordinates, mutableSetOf())
        return coordinates
    }

    /**
     * Loads dependency coordinates from the given root component.
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
     * Fetches the pom files for all dependencies.
     * Uses: https://maven.apache.org/ref/3.8.8/maven-model-builder/ to construct the pom model.
     *
     * Original Code is based on: https://github.com/cashapp/licensee/blob/1.13.0/src/main/kotlin/app/cash/licensee/dependencyGraph.kt#L42C14-L42C39
     * Copyright (C) 2021 Square, Inc.
     */
    private fun Iterable<DependencyCoordinatesWithPomFile>.getPomInfo(
        variants: List<ResolvedVariantResult>,
        dependencies: DependencyHandler,
        configurations: ConfigurationContainer,
    ): List<DependencyData> {
        val builder = DefaultModelBuilderFactory().newInstance()
        val resolver = object : ModelResolver {
            fun resolve(dependencyCoordinates: DependencyCoordinates): FileModelSource {
                return FileModelSource(setOf(dependencyCoordinates).fetchPomFiles(variants, dependencies, configurations).single().pomFile)
            }

            override fun resolveModel(groupId: String, artifactId: String, version: String): ModelSource2 = resolve(DependencyCoordinates(groupId, artifactId, version))
            override fun resolveModel(parent: Parent): ModelSource2 = resolve(DependencyCoordinates(parent.groupId, parent.artifactId, parent.version))
            override fun resolveModel(dependency: Dependency): ModelSource2 = resolve(DependencyCoordinates(dependency.groupId, dependency.artifactId, dependency.version))
            override fun addRepository(repository: Repository) {}
            override fun addRepository(repository: Repository, replace: Boolean) {}
            override fun newCopy(): ModelResolver = this
        }

        return mapNotNull { (coordinates, file) ->
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
            LOGGER.info("Could not get the name for ${uniqueId}! Fallback to2 '$uniqueId'")
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

    private fun <T> chooseValue(pom: Model, parentRawModel: List<Model>, block: (Model) -> T?): T? = pom.let(block) ?: parentRawModel.firstOrNull()?.let(block)

    /**
     * Fetches the pom files for all [ResolvedVariantResult]s.
     *
     * Original Code is based on: https://github.com/cashapp/licensee/blob/1.13.0/src/main/kotlin/app/cash/licensee/task.kt#L152
     * Copyright (C) 2021 Square, Inc.
     */
    private fun Set<DependencyCoordinates>.fetchPomFiles(
        variants: List<ResolvedVariantResult>,
        dependencies: DependencyHandler,
        configurations: ConfigurationContainer,
    ): List<DependencyCoordinatesWithPomFile> {
        fun Configuration.artifacts() = resolvedConfiguration.lenientConfiguration.allModuleDependencies.flatMap { it.allModuleArtifacts }

        val pomDependencies = map { dependencies.create(it.pomCoordinate()) }.toTypedArray()
        val withVariants = configurations.detachedConfiguration(*pomDependencies).apply {
            for (variant in variants) {
                attributes {
                    val variantAttrs = variant.attributes
                    for (attrs in variantAttrs.keySet()) {
                        @Suppress("UNCHECKED_CAST")
                        it.attribute(attrs as Attribute<Any>, variantAttrs.getAttribute(attrs)!!)
                    }
                }
            }
        }.artifacts()

        val withoutVariants = configurations.detachedConfiguration(*pomDependencies).artifacts()
        return (withVariants + withoutVariants).map {
            // Cast is safe because all resolved artifacts are pom files.
            val coordinates = (it.id.componentIdentifier as ModuleComponentIdentifier).toDependencyCoordinates()
            DependencyCoordinatesWithPomFile(coordinates, it.file)
        }.distinctBy { it.dependencyCoordinates }
    }

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

    /** Skip libraries which have a core dependency and we don't want it to show up more than necessary */
    private fun shouldSkip(uniqueId: String): Boolean {
        return handledLibraries.contains(uniqueId) || uniqueId == "com.mikepenz:aboutlibraries" || uniqueId == "com.mikepenz:aboutlibraries-definitions"
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DependencyCollector::class.java)!!
    }
}

private data class DependencyCoordinatesWithPomFile(
    val dependencyCoordinates: DependencyCoordinates,
    val pomFile: File,
)

internal data class DependencyCoordinates(
    val group: String,
    val artifact: String,
    val version: String,
) : java.io.Serializable {
    fun pomCoordinate() = "$group:$artifact:$version@pom"
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