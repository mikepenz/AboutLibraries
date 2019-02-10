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
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry
import java.util.jar.JarFile

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
    protected Map<String, String> definitionMap = new HashMap<>()

    @Input
    public Project project
    @Input
    public ConfigurationContainer configurations

    @OutputDirectory
    public File outputDir
    public File outputDirRes

    @OutputFile
    public File outputFile

    @TaskAction
    void action() {
        outputDirRes = new File(outputDir, "res")

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
            final def previousArtifacts = new JsonSlurper().parse(file)
            for (final entry in previousArtifacts) {
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

    protected void availableDefinitionFiles() {
        final URL url = LicensesPlugin.class.getResource("/definitions")
        if (url != null) {
            final String dirname = "definitions/"
            final String path = url.getPath()
            final String jarPath = path.substring(5, path.indexOf("!"))

            final JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))
            final Enumeration<JarEntry> entries = jar.entries()
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement()
                String name = entry.getName()

                if (name.startsWith(dirname) && !dirname.equals(name)) {
                    String content = LicensesPlugin.class.getResource("/" + name).text
                    String classPath = content.substring(content.indexOf("_artifactId") + 13)
                    classPath = classPath.substring(0, classPath.indexOf("<"))
                    definitionMap.put(classPath, name)
                }
            }
        }
    }

    protected void updateDependencyArtifacts() {
        availableDefinitionFiles()

        for (final Configuration configuration : configurations) {
            configuration.incoming.dependencies.each { final dependency ->
                final String group = dependency.group
                final String name = dependency.name
                final String version = dependency.version
                final String artifact_key = "${dependency.group}:${dependency.name}"

                if (group != null && name != null) {
                    if (!artifactSet.contains(artifact_key)) {
                        artifactSet.add(artifact_key)
                        artifactInfos.add(new ArtifactInfo(group, name, artifact_key, version))

                        if (definitionMap.containsKey(artifact_key)) {
                            final String fileName = definitionMap.get(artifact_key)
                            final String content = LicensesPlugin.class.getResource("/" + fileName).text
                            try {
                                final BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputDirRes, new File(fileName).getName())))
                                out.write(content)
                                out.close()
                            } catch (IOException e) {
                                System.out.println("Exception " + e)
                            }
                        }
                    }
                }
            }
        }
    }

    private void initOutput() {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        if (!outputDirRes.exists()) {
            outputDirRes.mkdirs()
        }
    }
}
