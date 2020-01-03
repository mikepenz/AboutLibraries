package com.mikepenz.aboutlibraries.plugin

import com.android.build.gradle.internal.ide.dependencies.ArtifactUtils
import com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.VariantScopeImpl
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.Parent
import org.apache.maven.model.Repository
import org.apache.maven.model.building.*
import org.apache.maven.model.resolution.InvalidRepositoryException
import org.apache.maven.model.resolution.ModelResolver
import org.apache.maven.model.resolution.UnresolvableModelException
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

@CacheableTask
public class AboutLibrariesIdTask extends DefaultTask {

    public Map<String, Object> systemProperties = System.getProperties()

    def gatherDependencies(def project) {
        def android = project.android
        def globalScope = android.globalScope
        def gradle = project.gradle

        def componentIdentifiers = new HashSet<ComponentIdentifier>()
        project.android.applicationVariants.all { variant ->
            // get all the componentIdentifiers from the artifacts
            ArtifactUtils.getAllArtifacts(
                    new VariantScopeImpl(globalScope, new TransformManager(project, null, null), variant.variantData),
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
            component.getArtifacts(MavenModule).each {
                println it.file.getText('UTF-8')
            }
            component.getArtifacts(MavenPomArtifact).each {

                // force the parent POMs and BOMs to be downloaded and copied
                try {
                    DefaultModelBuildingRequest request = new DefaultModelBuildingRequest();
                    request.setModelResolver(new ModelResolver() {
                        @Override
                        public ModelSource2 resolveModel(String groupId, String artifactId, String version) throws UnresolvableModelException {
                            String notation = groupId + ":" + artifactId + ":" + version + "@pom";
                            org.gradle.api.artifacts.Dependency dependency = project.getDependencies().create(notation);
                            Configuration configuration = project.getConfigurations().detachedConfiguration(dependency);
                            try {
                                File file = configuration.getFiles().iterator().next();
                                return new SimpleModelSource(new FileInputStream(file));
                            } catch (Exception e) {
                                throw new UnresolvableModelException(e, groupId, artifactId, version);
                            }
                        }

                        @Override
                        public ModelSource2 resolveModel(Dependency dependency) throws UnresolvableModelException {
                            return resolveModel(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
                        }

                        @Override
                        public ModelSource2 resolveModel(Parent parent) throws UnresolvableModelException {
                            return resolveModel(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
                        }

                        @Override
                        public void addRepository(Repository repository) throws InvalidRepositoryException {
                            // do nothing
                        }

                        @Override
                        public void addRepository(Repository repository, boolean bool) throws InvalidRepositoryException {
                            // do nothing
                        }

                        @Override
                        public ModelResolver newCopy() {
                            return this; // do nothing
                        }
                    });
                    request.setModelSource(new SimpleModelSource(new FileInputStream(it.file)));
                    request.setSystemProperties(System.getProperties());

                    DefaultModelBuilder modelBuilder = new DefaultModelBuilderFactory().newInstance();
                    //modelBuilder.setModelInterpolator(new ProjectPropertiesModelInterpolator(project));

                    ModelBuildingResult result2 = modelBuilder.build(request);
                    Model model = result2.getEffectiveModel();
                    if (model == null) {
                        //break;
                    }
                    org.apache.maven.model.DependencyManagement dependencyManagement = model.getDependencyManagement();
                    if (dependencyManagement == null) {
                        //break;
                    }
                    for (Dependency d : dependencyManagement.getDependencies()) {
                        println d.type
                        //recommendations.put(d.getGroupId() + ":" + d.getArtifactId(), d.getVersion());
                    }
                } catch (Exception e) {
                    logger.error("Error resolving ${it.file}", e)
                }

                println it.file
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

    static class SimpleModelSource implements ModelSource2 {
        InputStream input;

        public SimpleModelSource(InputStream input) {
            this.input = input;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return input;
        }

        @Override
        public String getLocation() {
            return null;
        }

        @Override
        public ModelSource2 getRelatedSource(String relPath) {
            return null;
        }

        @Override
        public URI getLocationURI() {
            return null;
        }
    }
}

