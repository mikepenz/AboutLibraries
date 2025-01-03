/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mikepenz.aboutlibraries.plugin.model

import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.ModuleVersionIdentifier

/**
 * Copy from https://github.com/gradle/gradle/blob/master/platforms/software/dependency-management/src/main/java/org/gradle/api/internal/artifacts/DefaultModuleVersionIdentifier.java
 */
class DefaultModuleVersionIdentifier private constructor(
    group: String, name: String, private val version: String,
) : ModuleVersionIdentifier {
    private val id: ModuleIdentifier = DefaultModuleIdentifier.newId(group, name)
    private val hashCode: Int = 31 * id.hashCode() xor version.hashCode()

    override fun getGroup(): String {
        return id.group
    }

    override fun getName(): String {
        return id.name
    }

    override fun getVersion(): String {
        return version
    }

    override fun toString(): String {
        val group = id.group
        val module = id.name
        return "$group:$module:$version"
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other == null || other.javaClass != javaClass) {
            return false
        }
        val otherCast = other as DefaultModuleVersionIdentifier
        if (id != otherCast.id) {
            return false
        }
        return version == otherCast.version
    }

    override fun hashCode(): Int {
        return hashCode
    }

    override fun getModule(): ModuleIdentifier {
        return id
    }

    companion object {
        fun newId(group: String, name: String, version: String): ModuleVersionIdentifier {
            return DefaultModuleVersionIdentifier(group, name, version)
        }
    }
}

