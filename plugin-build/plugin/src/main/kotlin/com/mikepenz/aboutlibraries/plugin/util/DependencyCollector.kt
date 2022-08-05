/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.internal.component.AmbiguousVariantSelectionException
import org.slf4j.LoggerFactory

/**
 * Based on https://raw.githubusercontent.com/gradle/gradle/master/subprojects/diagnostics/src/main/java/org/gradle/api/reporting/dependencies/internal/JsonProjectDependencyRenderer.java
 */
class DependencyCollector(
    private val includePlatform: Boolean = false,
) {
    /**
     * Generates the project dependency report structure
     *
     * @param project this project
     * @return resolved set of dependencies, and the related versions
     */
    fun collect(project: Project): CollectedContainer {
        LOGGER.info("Collecting dependencies")

        val mutableCollectContainer: MutableMap<String, MutableMap<String, MutableSet<String>>> = mutableMapOf()

        project.configurations
            .filterNot { configuration ->
                configuration.shouldSkip()
            }
            .mapNotNull {
                val cn = it.name
                // collect configurations for the variants we are interested in

                if (cn.endsWith("CompileClasspath", true)) {
                    val variant = cn.removeSuffix("CompileClasspath")
                    LOGGER.info("Collecting dependencies for compile time variant $variant from config: ${it.name}")
                    return@mapNotNull variant to it
                } else if (cn.endsWith("RuntimeClasspath", true)) {
                    val variant = cn.removeSuffix("RuntimeClasspath")
                    LOGGER.info("Collecting dependencies for runtime variant $variant from config: ${it.name}")
                    return@mapNotNull variant to it
                }

                null
            }
            .forEach { (variant, configuration) ->
                val variantSet = mutableCollectContainer.getOrPut(variant) { mutableMapOf() }
                val visitedDependencyNames = mutableSetOf<String>()
                configuration
                    .resolvedConfiguration
                    .lenientConfiguration
                    .allModuleDependencies
                    .getResolvedArtifacts(visitedDependencyNames)
                    .forEach { resArtifact ->
                        val identifier = "${resArtifact.moduleVersion.id.group.trim()}:${resArtifact.name.trim()}"
                        val versions = variantSet.getOrPut(identifier) { HashSet() }
                        versions.add(resArtifact.moduleVersion.id.version.trim())
                    }
            }
        return CollectedContainer(mutableCollectContainer)
    }

    /**
     * Based on the gist by @eygraber https://gist.github.com/eygraber/482e9942d5812e9efa5ace016aac4197
     * Via https://github.com/google/play-services-plugins/blob/master/oss-licenses-plugin/src/main/groovy/com/google/android/gms/oss/licenses/plugin/LicensesTask.groovy
     */
    private fun Set<ResolvedDependency>.getResolvedArtifacts(
        visitedDependencyNames: MutableSet<String>,
    ): Set<ResolvedArtifact> {
        val resolvedArtifacts = mutableSetOf<ResolvedArtifact>()
        for (resolvedDependency in this) {
            val name = resolvedDependency.name
            if (name !in visitedDependencyNames) {
                visitedDependencyNames += name

                try {
                    resolvedArtifacts += when {
                        resolvedDependency.moduleVersion == "unspecified" -> {
                            resolvedDependency.children.getResolvedArtifacts(
                                visitedDependencyNames = visitedDependencyNames
                            )
                        }

                        includePlatform && resolvedDependency.isPlatform -> {
                            setOf(resolvedDependency.toResolvedBomArtifact())
                        }

                        else -> resolvedDependency.allModuleArtifacts
                    }
                } catch (e: AmbiguousVariantSelectionException) {
                    LOGGER.info("Found ambiguous variant - $resolvedDependency", e)
                }
            }
        }

        return resolvedArtifacts
    }

    private fun Configuration.shouldSkip() =
        !isCanBeResolved || isTest

    /**
     * Based on the gist by @eygraber https://gist.github.com/eygraber/482e9942d5812e9efa5ace016aac4197
     * Via https://github.com/google/play-services-plugins/blob/master/oss-licenses-plugin/src/main/groovy/com/google/android/gms/oss/licenses/plugin/LicensesTask.groovy
     */
    private val testCompile = setOf("testCompile", "androidTestCompile")
    private val Configuration.isTest
        get() = name.contains("test", ignoreCase = true) ||
                name.contains("androidTest", ignoreCase = true) ||
                hierarchy.any { configurationHierarchy ->
                    testCompile.any { configurationHierarchy.name.contains(it, ignoreCase = true) }
                }

    private val platform = "platform"

    private val ResolvedDependency.isPlatform
        get() = configuration.contains(platform)

    private companion object {
        private val LOGGER = LoggerFactory.getLogger(DependencyCollector::class.java)!!
    }
}