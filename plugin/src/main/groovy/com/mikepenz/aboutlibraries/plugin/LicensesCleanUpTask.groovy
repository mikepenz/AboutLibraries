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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Task to clean up the generated dependency.json, third_party_licenses and
 * third_party_license_metadata files.
 */
class LicensesCleanUpTask extends DefaultTask {
    @Input
    public File dependencyFile

    @Input
    public File dependencyDir

    @Input
    public File metadataFile

    @Input
    public File licensesDir

    @TaskAction
    void action() {
        if (dependencyFile.exists()) {
            dependencyFile.delete()
        }

        if (dependencyDir.isDirectory() && dependencyDir.list().length == 0) {
            dependencyDir.delete()
        }

        if (metadataFile.exists()) {
            metadataFile.delete()
        }

        if (licensesDir.isDirectory() && licensesDir.list().length == 0) {
            licensesDir.delete()
        }
    }
}
