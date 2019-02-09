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
    protected Map<String, String> definitionMap = []

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

    protected void availableDefinitionFiles() {
        URL url = LicensesPlugin.class.getResource("/definitions")
        if (url != null) {
            String dirname = "definitions/"
            String path = url.getPath()
            String jarPath = path.substring(5, path.indexOf("!"))

            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))
            Enumeration<JarEntry> entries = jar.entries()
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement()
                String name = entry.getName()

                if (name.startsWith(dirname) && !dirname.equals(name)) {
                    String content = LicensesPlugin.class.getResource("/" + name).text
                    String classPath = content.substring(content.indexOf("_classPath") + 12)
                    classPath = classPath.substring(0, classPath.indexOf("<"))
                    definitionMap.put(classPath, name)
                }
            }
        }
    }

    protected void updateDependencyArtifacts() {
        availableDefinitionFiles()

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

    private static void copyFileUsingStream(File source, File dest) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (Exception ex) {
            System.out.println("Unable to copy file:" + ex.getMessage());
        } finally {
            try {
                is.close();
                os.close();
            } catch (Exception ex) {
            }
        }
    }
}
