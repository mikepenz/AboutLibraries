package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.mapping.Library
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LibraryUtilTest {

    private fun library(
        uniqueId: String,
        name: String,
        description: String = "Material You dynamic color",
        licenses: Set<String> = setOf("Apache-2.0"),
    ) = Library(
        uniqueId = uniqueId,
        artifactVersion = "5.0.0",
        name = name,
        description = description,
        website = null,
        developers = emptyList(),
        organization = null,
        scm = null,
        licenses = licenses,
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
    fun `KEEP reports every coordinate untouched`() {
        val libraries = listOf(
            library("androidx.collection:collection", "collection"),
            library("androidx.collection:collection-jvm", "collection"),
        )

        val result = libraries.processDuplicates(DuplicateMode.KEEP, DuplicateRule.EXACT)

        assertEquals(libraries.map { it.uniqueId }, result.map { it.uniqueId })
        assertEquals(listOf(null, null), result.map { it.associated })
    }

    @Test
    fun `LINK keeps every coordinate and cross-references the others`() {
        val libraries = listOf(
            library("androidx.collection:collection", "collection"),
            library("androidx.collection:collection-jvm", "collection"),
            library("androidx.collection:collection-js", "collection"),
        )

        val result = libraries.processDuplicates(DuplicateMode.LINK, DuplicateRule.EXACT)

        assertEquals(libraries.map { it.uniqueId }, result.map { it.uniqueId }, "LINK must not drop anything")
        // a library is associated to its siblings, never to itself
        assertEquals(
            listOf(
                setOf("androidx.collection:collection-jvm", "androidx.collection:collection-js"),
                setOf("androidx.collection:collection", "androidx.collection:collection-js"),
                setOf("androidx.collection:collection", "androidx.collection:collection-jvm"),
            ),
            result.map { it.associated?.toSet() },
        )
    }

    @Test
    fun `LINK leaves a library without siblings unassociated`() {
        val libraries = listOf(
            library("androidx.collection:collection", "collection"),
            library("com.google.code.gson:gson", "Gson"),
        )

        val result = libraries.processDuplicates(DuplicateMode.LINK, DuplicateRule.EXACT)

        assertEquals(listOf(null, null), result.map { it.associated })
    }

    /** [DuplicateRule.SIMPLE] matches on group + name, so a differing description must not split. */
    @Test
    fun `SIMPLE ignores the description EXACT distinguishes on`() {
        val libraries = listOf(
            library("androidx.collection:collection", "collection", description = "Standalone efficient collections."),
            library("androidx.collection:collection-jvm", "collection", description = "Collections, but for the JVM."),
        )

        assertEquals(
            listOf("androidx.collection:collection"),
            libraries.processDuplicates(DuplicateMode.MERGE, DuplicateRule.SIMPLE).map { it.uniqueId },
        )
        assertEquals(
            libraries.map { it.uniqueId },
            libraries.processDuplicates(DuplicateMode.MERGE, DuplicateRule.EXACT).map { it.uniqueId },
            "differing descriptions are distinct under EXACT",
        )
    }

    /** [DuplicateRule.GROUP] matches on group + licenses alone, ignoring name and description. */
    @Test
    fun `GROUP matches on licenses regardless of name`() {
        val libraries = listOf(
            library("androidx.collection:collection", "collection", description = "Collections"),
            library("androidx.collection:collection-jvm", "Collection for JVM", description = "Collections for the JVM"),
            library("androidx.collection:collection-ktx", "Collection KTX", licenses = setOf("MIT")),
        )

        val result = libraries.processDuplicates(DuplicateMode.MERGE, DuplicateRule.GROUP)

        assertEquals(
            setOf("androidx.collection:collection", "androidx.collection:collection-ktx"),
            result.map { it.uniqueId }.toSet(),
            "the two Apache-2.0 artifacts merge, the MIT one stays on its own",
        )
    }

    /**
     * The coarser rules match far more libraries, so the module-id clustering that keeps sibling
     * modules apart has to hold for them too — group + licenses alone would otherwise collapse a
     * whole group onto one entry.
     */
    @Test
    fun `GROUP does not merge unrelated modules sharing a license`() {
        val libraries = listOf(
            library("com.materialkolor:material-kolor", "MaterialKolor"),
            library("com.materialkolor:material-color-utilities", "MaterialKolor"),
        )

        val result = libraries.processDuplicates(DuplicateMode.MERGE, DuplicateRule.GROUP)

        assertEquals(libraries.map { it.uniqueId }.toSet(), result.map { it.uniqueId }.toSet())
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
