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

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class LicensesPlugin implements Plugin<Project> {
    void apply(Project project) {
        def getDependencies = project.tasks.create("getDependencies", com.mikepenz.aboutlibraries.plugin.DependencyTask)
        def dependencyOutput = new File(project.buildDir, "generated/dependencies")
        def generatedJson = new File(dependencyOutput, "dependencies.json")
        getDependencies.configurations = project.getConfigurations()
        getDependencies.outputDir = dependencyOutput
        getDependencies.outputFile = generatedJson

        def resourceOutput = new File(dependencyOutput, "/res")
        def outputDir = new File(resourceOutput, "/raw")


        project.android.applicationVariants.all { BaseVariant variant ->
            variant.preBuild.dependsOn(getDependencies)
        }

        def cleanupTask = project.tasks.create("licensesCleanUp", LicensesCleanUpTask)
        cleanupTask.dependencyFile = generatedJson
        cleanupTask.dependencyDir = dependencyOutput
        cleanupTask.licensesDir = outputDir

        project.tasks.findByName("clean").dependsOn(cleanupTask)
    }
}
