/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mikepenz.aboutlibraries.plugin

import groovy.json.JsonBuilder
import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * This task does the following:
 * First it finds all dependencies that meet all the requirements below:
 * 1. Can be resolved.
 * 2. Not test compile.
 * Then it finds all the artifacts associated with the dependencies.
 * Finally it generates a json file that contains the information about these
 * artifacts.
 */
class DependencyTask extends DefaultTask {
    protected Set<String> artifactSet = []
    protected Set<ArtifactInfo> artifactInfos = []
    private static final String TEST_PREFIX = "test"
    private static final String ANDROID_TEST_PREFIX = "androidTest"
    private static final Set<String> TEST_COMPILE = ["testCompile",
                                                     "androidTestCompile"]
    @Input
    public ConfigurationContainer configurations

    @OutputDirectory
    public File outputDir

    @OutputFile
    public File outputFile

    @TaskAction
    void action() {
        initOutput()
        updateDependencyArtifacts()

        if (outputFile.exists() && checkArtifactSet(outputFile)) {
            return
        }

        outputFile.newWriter()
        outputFile.write(new JsonBuilder(artifactInfos).toPrettyString())
    }

    /**
     * Checks if current artifact set is the same as the artifact set in the
     * json file
     * @param file
     * @return true if artifactSet is the same as the json file,
     * false otherwise
     */
    protected boolean checkArtifactSet(File file) {
        try {
            def previousArtifacts = new JsonSlurper().parse(file)
            for (entry in previousArtifacts) {
                String key = "${entry.full}"
                if (artifactSet.contains(key)) {
                    artifactSet.remove(key)
                } else {
                    return false
                }
            }
            return artifactSet.isEmpty()
        } catch (JsonException exception) {
            return false
        }
    }

    protected void updateDependencyArtifacts() {
        for (Configuration configuration : configurations) {
            configuration.dependencies.findAll { dependency ->
                String group = dependency.group
                String name = dependency.name
                String version = dependency.version
                String artifact_key = "${dependency.group}:${dependency.name}:${version}"

                if (group != null && name != null) {
                    if (artifactSet.contains(artifact_key)) {
                        dependency
                    }

                    artifactSet.add(artifact_key)
                    artifactInfos.add(new ArtifactInfo(group, name,
                            artifact_key,
                            version))
                }
            }
        }
    }

    /**
     * Checks if the configuration can be resolved. isCanBeResolved is added to
     * gradle Api since 3.3. For the previous version, all the configurations
     * can be resolved.
     * @param configuration
     * @return true if configuration can be resolved or the api is lower than
     * 3.3, otherwise false.
     */
    protected boolean canBeResolved(Configuration configuration) {
        return configuration.metaClass.respondsTo(configuration,
                "isCanBeResolved") ? configuration.isCanBeResolved() : true
    }

    /**
     * Checks if the configuration is from test.
     * @param configuration
     * @return true if configuration is a test configuration or its parent
     * configurations are either testCompile or androidTestCompile, otherwise
     * false.
     */
    protected boolean isTest(Configuration configuration) {
        boolean isTestConfiguration = (
                configuration.name.startsWith(TEST_PREFIX) ||
                        configuration.name.startsWith(ANDROID_TEST_PREFIX))
        configuration.hierarchy.each {
            isTestConfiguration |= TEST_COMPILE.contains(it.name)
        }
        return isTestConfiguration
    }

    private void initOutput() {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }
}
