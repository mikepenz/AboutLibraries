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
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.tasks.diagnostics.internal.graph.nodes.RenderableDependency
import org.gradle.api.tasks.diagnostics.internal.graph.nodes.RenderableModuleResult
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Based on https://raw.githubusercontent.com/gradle/gradle/master/subprojects/diagnostics/src/main/java/org/gradle/api/reporting/dependencies/internal/JsonProjectDependencyRenderer.java
 */
class DependencyCollector(
        private val variant: String? = null
) {

    private val processedDependency = mutableSetOf<Any>()

    /**
     * Generates the project dependency report structure
     *
     * @param project the project for which the report must be generated
     * @return the generated JSON, as a String
     */
    fun collect(project: Project): Map<String, HashSet<String>> {
        LOGGER.info("Collecting dependencies")
        return createConfigurations(project)
    }

    private fun createConfigurations(project: Project): Map<String, HashSet<String>> {
        val collected: MutableMap<String, HashSet<String>> = HashMap()
        val configurations: Iterable<Configuration> = getNonDeprecatedConfigurations(project)
        for (configuration in configurations) {
            if (canBeResolved(configuration)) {
                val cn = configuration.name
                if (!(cn.endsWith("CompileClasspath", true) || cn.endsWith("RuntimeClasspath", true))) {
                    // we are not keen to include compile time and runtime entries
                    continue
                }

                if (variant != null) {
                    if (!(cn.equals("${variant}CompileClasspath", true) || cn.equals("${variant}RuntimeClasspath", true))) {
                        // we are not keen to include compile time and runtime entries
                        continue
                    }

                    LOGGER.info("Collecting dependencies for variant $variant from config: ${configuration.name}")
                } else {
                    LOGGER.info("Collecting dependencies from config: ${configuration.name}")
                }

                val result = configuration.incoming.resolutionResult
                val root: RenderableDependency = RenderableModuleResult(result.root)

                try {
                    addChildDependencies(root.children, collected, configuration.name)
                } catch (ex: Throwable) {
                    LOGGER.error("Problem occurred while adding child dependencies", ex)
                }
            }
        }
        return collected
    }

    private fun addChildDependencies(childDependencies: Set<RenderableDependency>?, collected: MutableMap<String, HashSet<String>>, configurationName: String?) {
        childDependencies ?: return
        for (childDependency in childDependencies) {
            processedDependency.add(childDependency.id)
            if (childDependency.id is ModuleComponentIdentifier) {
                val id = childDependency.id as ModuleComponentIdentifier
                val versions = collected.getOrDefault(id.group + ":" + id.module, HashSet())
                versions.add(id.version)
                collected[id.group + ":" + id.module] = versions
            }
            // check children, but filter out references we already processed.
            addChildDependencies(childDependency.children.filter { !processedDependency.contains(it.id) }.toSet(), collected, configurationName)
        }
    }

    private fun getNonDeprecatedConfigurations(project: Project): List<Configuration> {
        val filteredConfigurations: MutableList<Configuration> = ArrayList()
        for (configuration in project.configurations) {
            filteredConfigurations.add(configuration)
        }
        return filteredConfigurations
    }

    private fun canBeResolved(configuration: Configuration): Boolean {
        return configuration.isCanBeResolved
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DependencyCollector::class.java)!!
    }
}