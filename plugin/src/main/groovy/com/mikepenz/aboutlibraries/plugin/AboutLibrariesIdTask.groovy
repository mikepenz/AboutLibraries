package com.mikepenz.aboutlibraries.plugin


import com.android.build.gradle.internal.ide.dependencies.ArtifactUtils
import com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.VariantScopeImpl
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

@CacheableTask
public class AboutLibrariesIdTask extends DefaultTask {

    def gatherDependencies(def project) {
        def android = project.android
        def globalScope = android.globalScope
        def gradle = project.gradle

        def componentIdentifiers = new HashSet<ComponentIdentifier>()
        project.android.applicationVariants.all { variant ->
            def variantScopeImpl = null
            try {
                // 4.0.0-alpha09
                variantScopeImpl = new VariantScopeImpl(project.android.globalScope, new TransformManager(project, null, null), variant.variantData.getVariantDslInfo(), variant.variantData.getType())
                variantScopeImpl.setVariantData(variant.variantData)
            } catch (Exception ex) {
                // pre 4.0.0-alpha09
                variantScopeImpl = new VariantScopeImpl(project.android.globalScope, new TransformManager(project, null, null), variant.variantData)
            }

            ArtifactUtils.getAllArtifacts(
                    variantScopeImpl,
                    AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                    null,
                    BuildMappingUtils.computeBuildMapping(gradle)
            ).each { artifact ->
                componentIdentifiers.add(artifact.componentIdentifier)
            }
        }

        println "All dependencies.size=${componentIdentifiers.size()}"

        def result = project.dependencies.createArtifactResolutionQuery()
                .forComponents(componentIdentifiers)
                .withArtifacts(MavenModule, MavenPomArtifact)
                .execute()

        for (component in result.resolvedComponents) {
            component.getArtifacts(MavenPomArtifact).each {
                def artifactPom = new XmlSlurper().parseText(it.file.getText('UTF-8'))
                def uniqueId = fixIdentifier(artifactPom.groupId) + "__" + fixIdentifier(artifactPom.artifactId)
                def libraryName = fixString(artifactPom.name)
                println "${libraryName} (${artifactPom.version}) -> ${uniqueId}"
            }
        }
    }

    /**
     * Ensures no invalid chars stay in the identifier
     */
    static def fixIdentifier(value) {
        return fixString(value).replace(".", "_").replace("-", "_")
    }

    /**
     * Ensures all characters necessary are escaped
     */
    static def fixString(value) {
        if (value != null) {
            return value.toString().replace("\"", "\\\"").replace("'", "\\'")
        } else {
            return ""
        }
    }

    @TaskAction
    public void action() throws IOException {
        gatherDependencies(project)
    }
}