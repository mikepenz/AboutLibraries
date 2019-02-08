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

class ArtifactInfo {
    private String group
    private String name
    private String version
    private String full

    ArtifactInfo(String group, String name, String full, String version) {
        this.group = group
        this.name = name
        this.version = version
        this.full = full
    }

    String getGroup() {
        return group
    }

    String getName() {
        return name
    }

    String getFull() {
        return full
    }

    String getVersion() {
        return version
    }
}
