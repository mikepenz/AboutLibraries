/*
 * Copyright 2013 the original author or authors.
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
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

/**
 * Copy from https://github.com/gradle/gradle/blob/master/platforms/software/dependency-management/src/main/java/org/gradle/internal/component/external/model/DefaultModuleComponentIdentifier.java
 */
class DefaultModuleComponentIdentifier(module: ModuleIdentifier, version: String) : ModuleComponentIdentifier {
    private val moduleIdentifier: ModuleIdentifier
    private val version: String
    private val hashCode: Int

    init {
        checkNotNull(module.group) { "group cannot be null" }
        checkNotNull(module.name) { "name cannot be null" }

        this.moduleIdentifier = module
        this.version = version
        // Do NOT change the order of members used in hash code here, it's been empirically
        // tested to reduce the number of collisions on a large dependency graph (performance test)
        this.hashCode = 31 * version.hashCode() + module.hashCode()
    }

    override fun getDisplayName(): String {
        val group = moduleIdentifier.group
        val module = moduleIdentifier.name
        val builder = StringBuilder(group.length + module.length + version.length + 2)
        builder.append(group)
        builder.append(":")
        builder.append(module)
        builder.append(":")
        builder.append(version)
        return builder.toString()
    }

    override fun getGroup(): String {
        return moduleIdentifier.group
    }

    override fun getModule(): String {
        return moduleIdentifier.name
    }

    override fun getVersion(): String {
        return version
    }

    override fun getModuleIdentifier(): ModuleIdentifier {
        return moduleIdentifier
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as DefaultModuleComponentIdentifier
        if (hashCode != that.hashCode) return false
        if (moduleIdentifier != that.moduleIdentifier) return false
        return version == that.version
    }

    override fun hashCode(): Int {
        return hashCode
    }

    override fun toString(): String {
        return displayName
    }

    companion object {
        fun newId(moduleVersionIdentifier: ModuleVersionIdentifier): ModuleComponentIdentifier {
            return DefaultModuleComponentIdentifier(moduleVersionIdentifier.module, moduleVersionIdentifier.version)
        }
    }
}