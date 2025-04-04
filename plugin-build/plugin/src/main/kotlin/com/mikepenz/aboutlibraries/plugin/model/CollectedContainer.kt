package com.mikepenz.aboutlibraries.plugin.model

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable

data class CollectedContainer(
    val dependencies: Map<String, Map<String, Set<String>>>,
) : Serializable {
    /**
     * Retrieves the dependencies for a specific variant, if no variant is provided, will merge all found variants together.
     */
    fun dependenciesForVariant(variant: String? = null): Map<String, Set<String>> {
        if (variant != null) {
            return dependencies[variant] ?: run {
                LOGGER.warn("Variant ($variant) was missing from dependencies, this should never happen")
                LOGGER.warn("Available variants:")
                dependencies.keys.forEach {
                    LOGGER.warn("-- $it")
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
        internal val LOGGER: Logger = LoggerFactory.getLogger(CollectedContainer::class.java)

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