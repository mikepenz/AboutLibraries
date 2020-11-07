package com.mikepenz.aboutlibraries.plugin


import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
public class AboutLibrariesCleanTask extends DefaultTask {

    private File dependencies

    @OutputDirectory
    public File getDependencies() {
        return dependencies;
    }

    public void setDependencies(File dependencies) {
        this.dependencies = dependencies
    }

    @TaskAction
    public void action() throws IOException {
        if (dependencies.exists()) {
            dependencies.deleteDir()
        }
    }
}