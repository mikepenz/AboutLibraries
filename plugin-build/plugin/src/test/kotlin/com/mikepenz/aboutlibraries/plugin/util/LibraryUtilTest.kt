package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.mapping.Library
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LibraryUtilTest {

    private fun library(uniqueId: String, name: String, description: String = "Material You dynamic color") = Library(
        uniqueId = uniqueId,
        artifactVersion = "5.0.0",
        name = name,
        description = description,
        website = null,
        developers = emptyList(),
        organization = null,
        scm = null,
    )

    /**
     * https://github.com/mikepenz/AboutLibraries/issues/1430 — sibling modules of one project share
     * the POM `name` and `description`, which made every duplicate rule consider them equal. Only
     * the platform artifacts of the *same* module may be merged.
     */
    @Test
    fun `sibling modules sharing name and description are not merged`() {
        val libraries = listOf(
            library("com.materialkolor:material-kolor", "MaterialKolor"),
            library("com.materialkolor:material-kolor-jvm", "MaterialKolor"),
            library("com.materialkolor:material-color-utilities", "MaterialKolor"),
            library("com.materialkolor:material-color-utilities-jvm", "MaterialKolor"),
        )

        val result = libraries.processDuplicates(DuplicateMode.MERGE, DuplicateRule.EXACT)

        assertEquals(
            setOf("com.materialkolor:material-kolor", "com.materialkolor:material-color-utilities"),
            result.map { it.uniqueId }.toSet(),
        )
    }

    @Test
    fun `platform artifacts of the same module are still merged`() {
        val libraries = listOf(
            library("androidx.collection:collection-jvm", "collection"),
            library("androidx.collection:collection", "collection"),
            library("androidx.collection:collection-js", "collection"),
        )

        val result = libraries.processDuplicates(DuplicateMode.MERGE, DuplicateRule.EXACT)

        assertEquals(listOf("androidx.collection:collection"), result.map { it.uniqueId })
    }

    @Test
    fun `native and web platform artifacts are merged too`() {
        val libraries = listOf(
            library("androidx.collection:collection", "collection"),
            library("androidx.collection:collection-iossimulatorarm64", "collection"),
            library("androidx.collection:collection-linuxx64", "collection"),
            library("androidx.collection:collection-wasm-js", "collection"),
            library("androidx.collection:collection-jvmstubs", "collection"),
            library("androidx.collection:collection-desktop", "collection"),
        )

        val result = libraries.processDuplicates(DuplicateMode.MERGE, DuplicateRule.EXACT)

        assertEquals(listOf("androidx.collection:collection"), result.map { it.uniqueId })
    }

    /**
     * A sibling module whose id happens to start with a shorter module's id is not a platform
     * artifact of it — only a known Kotlin target suffix makes one.
     */
    @Test
    fun `a shorter sibling module does not absorb the ones it prefixes`() {
        val libraries = listOf(
            library("com.foo:android", "Foo"),
            library("com.foo:android-core", "Foo"),
            library("com.foo:android-core-jvm", "Foo"),
            library("com.foo:android-extra", "Foo"),
            library("com.foo:android-extra-jvm", "Foo"),
        )

        val result = libraries.processDuplicates(DuplicateMode.MERGE, DuplicateRule.EXACT)

        assertEquals(
            setOf("com.foo:android", "com.foo:android-core", "com.foo:android-extra"),
            result.map { it.uniqueId }.toSet(),
            "each module keeps its own entry, absorbing only its own platform artifact",
        )
    }

    @Test
    fun `a non-target suffix is not treated as a platform artifact`() {
        val libraries = listOf(
            library("androidx.core:core", "Core"),
            library("androidx.core:core-ktx", "Core"),
        )

        val result = libraries.processDuplicates(DuplicateMode.MERGE, DuplicateRule.EXACT)

        assertEquals(libraries.map { it.uniqueId }.toSet(), result.map { it.uniqueId }.toSet())
    }
}
