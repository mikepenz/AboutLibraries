package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.tasks.diagnostics.internal.ConfigurationDetails
import org.gradle.api.tasks.diagnostics.internal.DependencyReportRenderer
import org.gradle.api.tasks.diagnostics.internal.ProjectDetails
import org.gradle.api.tasks.diagnostics.internal.graph.nodes.RenderableDependency
import org.gradle.api.tasks.diagnostics.internal.graph.nodes.RenderableModuleResult
import org.gradle.initialization.BuildClientMetaData
import org.gradle.internal.logging.text.StyledTextOutput
import org.slf4j.LoggerFactory
import java.io.File

class DependencyReportCollector : DependencyReportRenderer {
    lateinit var dependencyCache: File
    var includePlatform: Boolean = false
    var filterVariants: Array<String> = emptyArray()


    private val mutableCollectContainer: MutableMap<String, MutableMap<String, MutableSet<String>>> =
        sortedMapOf(compareBy<String> { it })


    override fun setClientMetaData(clientMetaData: BuildClientMetaData) {
        //
    }

    override fun setOutput(textOutput: StyledTextOutput) {
        //
    }

    override fun setOutputFile(file: File) {
        //
    }

    override fun startProject(project: ProjectDetails) {
        //
    }

    override fun completeProject(project: ProjectDetails) {
        //
    }

    override fun complete() {
        val collectedContainer = CollectedContainer(mutableCollectContainer)
        dependencyCache.writeText(groovy.json.JsonOutput.toJson(collectedContainer))
    }

    override fun startConfiguration(configuration: ConfigurationDetails) {
        //
    }

    override fun render(configuration: ConfigurationDetails) {
        val cn = configuration.name
        val variant: String
        if (cn.endsWith("CompileClasspath", true)) {
            variant = cn.removeSuffix("CompileClasspath")
            if (filterVariants.isEmpty() || filterVariants.contains(variant)) {

            } else {
                LOGGER.error("SKIP VARIANT // ${configuration.name}")
                return
            }
            //
        } else if (configuration.name.endsWith("RuntimeClasspath", true)) {
            variant = cn.removeSuffix("RuntimeClasspath")
            if (filterVariants.isEmpty() || filterVariants.contains(variant)) {

            } else {
                LOGGER.error("SKIP VARIANT // ${configuration.name}")
                return
            }
            //
        } else {
            LOGGER.error("IGNORE // ${configuration.name}")
            return
        }

        if (configuration.isTest) {
            LOGGER.error("IGNORE isTest // ${configuration.name}")
            return
        }

        if (configuration.isCanBeResolved) {
            val result = configuration.resolutionResultRoot!!.get()
            val root = RenderableModuleResult(result)
            root.renderNow(variant)
        } else {
            val unresolvedResult = configuration.unresolvableResult
            if (unresolvedResult != null) {
                unresolvedResult.renderNow(variant)
            } else {
                LOGGER.debug("-- no unresolved result")
            }
        }
    }


    private fun RenderableDependency.renderNow(variant: String) {
        if (children.isEmpty()) {
            LOGGER.debug("-- variant (${variant}) has no dependencies")
            return
        }

        val visitedDependencyNames = mutableSetOf<Any>()
        renderNow(
            mutableCollectContainer.getOrPut(variant) { sortedMapOf(compareBy<String> { it }) },
            visitedDependencyNames
        )
    }

    private fun RenderableDependency.renderNow(
        variantSet: MutableMap<String, MutableSet<String>>,
        visitedDependencyNames: MutableSet<Any>,
    ) {
        children.onEach {
            if (!visitedDependencyNames.contains(it.id)) {
                visitedDependencyNames.add(it.id)
                
                val id = it.id
                if (id is ModuleComponentIdentifier) {
                    val identifier = "${id.group.trim()}:${id.module.trim()}"
                    val versions = variantSet.getOrPut(identifier) { LinkedHashSet() }
                    versions.add(id.version.trim())
                } else {
                    LOGGER.error("${it.id} // ${it.name}")
                }
                it.renderNow(variantSet, visitedDependencyNames)
            }
        }
    }

    override fun completeConfiguration(configuration: ConfigurationDetails) {
        //
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DependencyReportCollector::class.java)
    }
}

private val ConfigurationDetails.isTest
    get() = name.startsWith("test", ignoreCase = true) ||
            name.startsWith("androidTest", ignoreCase = true)