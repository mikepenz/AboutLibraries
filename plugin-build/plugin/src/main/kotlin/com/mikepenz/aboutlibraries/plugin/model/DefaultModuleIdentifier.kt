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

/**
 * Copy from: https://github.com/gradle/gradle/blob/master/platforms/software/dependency-management/src/main/java/org/gradle/api/internal/artifacts/DefaultModuleIdentifier.java
 */
class DefaultModuleIdentifier private constructor(private val group: String, private val name: String) : ModuleIdentifier {
    private val hashCode = 31 * group.hashCode() xor name.hashCode()

    override fun getGroup(): String {
        return group
    }

    override fun getName(): String {
        return name
    }

    override fun toString(): String {
        return "$group:$name"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as DefaultModuleIdentifier
        return hashCode == that.hashCode &&
            group == that.group &&
            name == that.name
    }

    override fun hashCode(): Int {
        return hashCode
    }

    companion object {
        fun newId(group: String, name: String): ModuleIdentifier {
            return DefaultModuleIdentifier(group, name)
        }
    }
}
