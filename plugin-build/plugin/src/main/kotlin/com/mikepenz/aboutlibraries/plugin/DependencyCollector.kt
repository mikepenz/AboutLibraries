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
package com.mikepenz.aboutlibraries.plugin

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
    private val variant: String? = null
) {
    /**
     * Generates the project dependency report structure
     *
     * @param project this project
     * @return resolved set of dependencies, and the related versions
     */
    fun collect(project: Project): Map<String, HashSet<String>> {
        LOGGER.info("Collecting dependencies")

        val collected: MutableMap<String, HashSet<String>> = HashMap()
        project.configurations
            .filterNot { configuration ->
                configuration.shouldSkip()
            }
            .filter {
                val cn = it.name
                if (variant != null) {
                    if (!(cn.equals("${variant}CompileClasspath", true) || cn.equals("${variant}RuntimeClasspath", true))) {
                        false
                    } else {
                        LOGGER.info("Collecting dependencies for variant $variant from config: ${it.name}")
                        true
                    }
                } else {
                    LOGGER.info("Collecting dependencies from config: ${it.name}")
                    true
                }
            }
            .forEach { configuration ->
                // configuration.allDependencies.forEach {
                //     val identifier = "${it.group!!.trim()}:${it.name.trim()}"
                //     val versions = collected.getOrDefault(identifier, HashSet())
                //     versions.add(it.version!!.trim())
                //     collected[identifier] = versions
                // }

                val visitedDependencyNames = mutableSetOf<String>()
                configuration
                    .resolvedConfiguration
                    .lenientConfiguration
                    .allModuleDependencies
                    .getResolvedArtifacts(visitedDependencyNames)
                    .forEach {
                        val identifier = "${it.moduleVersion.id.group.trim()}:${it.name.trim()}"
                        val versions = collected.getOrDefault(identifier, HashSet())
                        versions.add(it.moduleVersion.id.version.trim())
                        collected[identifier] = versions
                    }
            }

        return collected
    }

    /**
     * Based on the gist by @eygraber https://gist.github.com/eygraber/482e9942d5812e9efa5ace016aac4197
     * Via https://github.com/google/play-services-plugins/blob/master/oss-licenses-plugin/src/main/groovy/com/google/android/gms/oss/licenses/plugin/LicensesTask.groovy
     */
    private fun Set<ResolvedDependency>.getResolvedArtifacts(
        visitedDependencyNames: MutableSet<String>
    ): Set<ResolvedArtifact> {
        val resolvedArtifacts = mutableSetOf<ResolvedArtifact>()
        for (resolvedDependency in this) {
            val name = resolvedDependency.name
            if (name !in visitedDependencyNames) {
                visitedDependencyNames += name

                try {
                    resolvedArtifacts += when (resolvedDependency.moduleVersion) {
                        "unspecified" ->
                            resolvedDependency.children.getResolvedArtifacts(
                                visitedDependencyNames = visitedDependencyNames
                            )

                        else -> resolvedDependency.allModuleArtifacts
                    }
                } catch (e: AmbiguousVariantSelectionException) {
                    LOGGER.info("Found ambiguous variant", e)
                }
            }
        }

        return resolvedArtifacts
    }

    internal fun Configuration.shouldSkip() =
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

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DependencyCollector::class.java)!!
    }
}