package com.mikepenz.aboutlibraries.plugin.model

import org.gradle.api.tasks.Input
import java.io.Serializable

data class CollectedContainer(
    // Map<Variant, Map<Identifier, Set<Versions>>>
    @get:Input val dependencies: Map<String, Map<String, Set<String>>>
) : Serializable {
    /**
     * Retrieves the dependencies for a specific variant, if no variant is provided, will merge all found variants together.
     */
    fun dependenciesForVariant(variant: String? = null): Map<String, Set<String>> {
        if (variant != null) {
            return dependencies[variant] ?: run {
                println("Variant ($variant) was missing from dependencies, this should never happen")
                println("Available variants:")
                dependencies.keys.forEach {
                    println("-- $it")
                }
                emptyMap()
            }
        } else {
            val flattenedMap = mutableMapOf<String, HashSet<String>>()
            dependencies.forEach { (_, realDependencies) ->
                realDependencies.forEach { (identifier, versions) ->
                    val prevVersions = flattenedMap[identifier]
                    if (prevVersions == null) {
                        flattenedMap[identifier] = versions.toHashSet()
                    } else {
                        prevVersions.addAll(versions)
                    }
                }
            }
            return flattenedMap
        }
    }

    companion object {
        @JvmStatic
        fun from(parsed: Map<String, Map<String, List<String>>>): CollectedContainer {
            val target: MutableMap<String, MutableMap<String, Set<String>>> = mutableMapOf()
            parsed.forEach { (variant, deps) ->
                deps.forEach { (dep, versions) ->
                    target.getOrPut(variant) { mutableMapOf() }[dep] = versions.toSet()
                }
            }
            return CollectedContainer(target)
        }
    }
}