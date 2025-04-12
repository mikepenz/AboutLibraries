package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.model.DefaultModuleComponentIdentifier
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object PomLoader {
    private val LOGGER: Logger = LoggerFactory.getLogger(PomLoader::class.java)

    /**
     * Tries to resolve the pom file given the id if possible
     *
     * Logic based on: https://github.com/ben-manes/gradle-versions-plugin
     */
    fun DependencyHandler.resolvePomFile(
        uniqueId: String?,
        id: ModuleVersionIdentifier,
        parent: Boolean,
        prefix: String = "",
    ): File? {
        try {
            LOGGER.debug("Attempting to resolve POM file for uniqueId={}, ModuleVersionIdentifier id={}", uniqueId, id);
            val resolutionResult = createArtifactResolutionQuery()
                .forComponents(DefaultModuleComponentIdentifier.newId(id))
                .withArtifacts(MavenModule::class.java, MavenPomArtifact::class.java)
                .execute()

            if (resolutionResult.resolvedComponents.isEmpty()) {
                LOGGER.info("{}--> Retrieved no components for: {}", prefix, id)
            }

            // size is 0 for gradle plugins, 1 for normal dependencies
            for (r in resolutionResult.resolvedComponents) {
                LOGGER.debug("Processing component artifact result {}", r);
                // size should always be 1
                for (artifact in r.getArtifacts(MavenPomArtifact::class.java)) {
                    LOGGER.debug("Processing artifact result {}", artifact);
                    // todo identify if that ever has more than 1
                    if (artifact is ResolvedArtifactResult) {
                        if (parent) {
                            LOGGER.info("${prefix}--> Retrieved POM for: $uniqueId from ${id.group}:${id.name}:${id.version}")
                        }
                        return artifact.file
                    }
                }
            }
            return null
        } catch (e: Exception) {
            LOGGER.error("Could not load pom file", e)
            return null
        }
    }

}