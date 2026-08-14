package com.mikepenz.aboutlibraries.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Verifies `aboutLibraries.library.mergePlatformArtifacts`: Kotlin Multiplatform artifacts reached
 * via a Gradle `available-at` redirect are reported under the root coordinate that was declared,
 * while artifacts genuinely published under a suffixed coordinate stay untouched.
 *
 * `androidx.collection:collection` is a KMP publication: on a JVM classpath it redirects to
 * `androidx.collection:collection-jvm`.
 */
class MergePlatformArtifactsFunctionalTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `root and platform artifact are both collected when merging is disabled`() {
        val json = runExport(mergePlatformArtifacts = false)

        // `duplicationMode = KEEP` (set by runExport) keeps both entries visible; with the default
        // MERGE they are collapsed onto whichever of the two the graph walk happened to reach first
        assertTrue(json.contains("\"androidx.collection:collection-jvm\""), "Expected platform artifact id. Output:\n$json")
        assertTrue(json.contains("\"androidx.collection:collection\""), "Expected redirect shell id. Output:\n$json")
    }

    @Test
    fun `platform artifacts are merged regardless of the order the graph is walked in`() {
        // `annotation-jvm` is declared first, so it is reached directly before the `annotation`
        // redirect shell that `collection` pulls in transitively. The merge must not depend on
        // which path reaches the platform artifact first.
        val json = runExport(
            mergePlatformArtifacts = true,
            dependencies = listOf("androidx.annotation:annotation-jvm:1.9.1", "androidx.collection:collection:1.5.0"),
        )

        assertTrue(json.contains("\"androidx.annotation:annotation\""), "Expected declared root id. Output:\n$json")
        assertFalse(json.contains("\"androidx.annotation:annotation-jvm\""), "Platform artifact id must be replaced. Output:\n$json")
    }

    @Test
    fun `platform artifacts are reported under the declared root coordinate when merging`() {
        val json = runExport(mergePlatformArtifacts = true)

        assertTrue(json.contains("\"androidx.collection:collection\""), "Expected declared root id. Output:\n$json")
        assertFalse(json.contains("\"androidx.collection:collection-jvm\""), "Platform artifact id must be replaced")
        // metadata still comes from the resolved artifact's POM
        assertTrue(json.contains("Standalone efficient collections."), "Description of the resolved artifact should be kept")
    }

    @Test
    fun `non-multiplatform dependency trees are byte-identical with and without merging`() {
        // guards against the detection widening beyond `available-at` redirects: a plain JVM tree
        // has no redirect shells at all, so enabling the option must be a no-op
        val dependencies = listOf("com.google.code.gson:gson:2.11.0", "org.slf4j:slf4j-api:2.0.16")

        val disabled = runExport(mergePlatformArtifacts = false, dependencies = dependencies)
        val enabled = runExport(mergePlatformArtifacts = true, dependencies = dependencies)

        assertEquals(disabled, enabled, "Merging must not alter output for non-KMP dependencies")
    }

    @Test
    fun `suffixed coordinates without a redirect are untouched when merging`() {
        // no KMP root module in the graph, so there is nothing to merge into — the `-jvm` suffix
        // must not be stripped by name
        val json = runExport(mergePlatformArtifacts = true, dependencies = listOf("androidx.annotation:annotation-jvm:1.9.1"))

        assertTrue(json.contains("\"androidx.annotation:annotation-jvm\""), "Suffixed id without redirect must be kept. Output:\n$json")
        assertFalse(json.contains("\"androidx.annotation:annotation\""), "Nothing should have been merged. Output:\n$json")
    }

    /**
     * The case the option exists for, and the only one a `java-library` project cannot show: a real
     * multiplatform *consumer*, where one declared dependency resolves to a different artifact per
     * target.
     *
     * Without merging, the default [DuplicateMode.MERGE] still collapses the three coordinates onto
     * one entry — but onto whichever the graph walk reached first. That survivor is named for a
     * single platform while standing for all of them, which `collect.includeTargets` makes plainly
     * visible: an entry called `collection-js` reporting that it is consumed by `jvm`.
     *
     * With merging the id is the declared root coordinate and the targets are the union, so the
     * name and the targets finally agree.
     */
    @Test
    fun `merged entry is named for the declared root and carries the union of its targets`() {
        val merged = extractEntry(runKmpExport(mergePlatformArtifacts = true), "androidx.collection:collection")
            ?: error("expected the declared root coordinate to be reported")
        assertEquals(setOf("js", "jvm"), targetsOf(merged) - "metadata", "Entry: $merged")

        val unmerged = runKmpExport(mergePlatformArtifacts = false)
        assertFalse(
            unmerged.contains("\"uniqueId\":\"androidx.collection:collection\","),
            "without merging the survivor is a platform artifact, not the root. Output:\n$unmerged"
        )
    }

    private fun targetsOf(entry: String): Set<String> =
        Regex("\"targets\":\\[(.*?)]").find(entry)?.groupValues?.get(1)
            ?.split(",")?.mapNotNull { it.trim().trim('"').takeIf(String::isNotEmpty) }?.toSet()
            ?: error("no `targets` field in entry: $entry")

    /** Slices out a single library object by `uniqueId` from the (non pretty-printed) output. */
    private fun extractEntry(json: String, uniqueId: String): String? {
        val keyIdx = json.indexOf("\"uniqueId\":\"$uniqueId\",")
        if (keyIdx < 0) return null
        var start = keyIdx
        while (start > 0 && json[start] != '{') start--
        var depth = 0
        for (end in start until json.length) {
            when (json[end]) {
                '{' -> depth++
                '}' -> if (--depth == 0) return json.substring(start, end + 1)
            }
        }
        return null
    }

    /**
     * A Kotlin Multiplatform project with two targets, so `androidx.collection:collection` resolves
     * to `collection-jvm` on one and `collection-js` on the other. `duplicationMode` is left at its
     * default here — the point is what a normal consumer sees.
     */
    private fun runKmpExport(mergePlatformArtifacts: Boolean): String {
        File(projectDir, "settings.gradle.kts").writeText(
            """
            pluginManagement { repositories { gradlePluginPortal(); mavenCentral(); google() } }
            rootProject.name = "kmp-consumer"
            """.trimIndent()
        )
        File(projectDir, "build.gradle.kts").writeText(
            """
            buildscript {
                repositories { mavenCentral(); google(); gradlePluginPortal() }
                dependencies {
                    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
                    classpath(files($pluginClasspath))
                }
            }

            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            apply(plugin = "com.mikepenz.aboutlibraries.plugin")

            repositories { mavenCentral(); google() }

            extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
                jvm()
                js { nodejs() }
                sourceSets.getByName("commonMain").dependencies {
                    implementation("androidx.collection:collection:1.5.0")
                }
            }

            extensions.configure<com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension>("aboutLibraries") {
                offlineMode = true
                collect { includeTargets = true }
                library { mergePlatformArtifacts = $mergePlatformArtifacts }
            }
            """.trimIndent()
        )

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)
        return File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json").readText()
    }

    private val kotlinVersion: String = System.getProperty("test.kotlin.version")
        ?: error("test.kotlin.version system property must be set by the test task")

    private val pluginClasspath: String by lazy {
        val resource = javaClass.classLoader.getResource("plugin-under-test-metadata.properties")
            ?: error("plugin-under-test-metadata.properties not found on test classpath")
        val props = java.util.Properties().apply { resource.openStream().use { load(it) } }
        val cp = props.getProperty("implementation-classpath")
            ?: error("implementation-classpath missing from plugin-under-test-metadata.properties")
        cp.split(File.pathSeparator).joinToString(", ") { "files(\"${it.replace("\\", "\\\\")}\")" }
    }

    private fun runExport(
        mergePlatformArtifacts: Boolean,
        dependencies: List<String> = listOf("androidx.collection:collection:1.5.0", "androidx.annotation:annotation-jvm:1.9.1"),
    ): String {
        File(projectDir, "settings.gradle.kts").writeText("""rootProject.name = "test-project"""")
        File(projectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }

            repositories {
                mavenCentral()
                google()
            }

            dependencies {
                ${dependencies.joinToString("\n                ") { "implementation(\"$it\")" }}
            }

            aboutLibraries {
                offlineMode = true
                library {
                    mergePlatformArtifacts = $mergePlatformArtifacts
                    duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.KEEP
                }
            }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
        assertTrue(outputFile.exists(), "Output file should be created")
        return outputFile.readText()
    }
}
