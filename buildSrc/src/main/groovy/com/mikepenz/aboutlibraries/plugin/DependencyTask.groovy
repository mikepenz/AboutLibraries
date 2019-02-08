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

        throw new RuntimeException(LicensesPlugin.class.getResource( '/definitions/library_actionbarpulltorefresh_strings.xml' ).text)

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

    private void initOutput() {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }
}
