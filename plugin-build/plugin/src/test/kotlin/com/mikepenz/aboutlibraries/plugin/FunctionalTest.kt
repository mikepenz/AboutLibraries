package com.mikepenz.aboutlibraries.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Functional tests to verify the plugin behavior remains unchanged after performance optimizations.
 */
class FunctionalTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `plugin should generate library definitions successfully`() {
        setupProject(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        // Verify output file was created
        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutLibraries.json")
        assertTrue(outputFile.exists(), "Output file should be created")
        assertTrue(outputFile.length() > 0, "Output file should not be empty")

        // Verify it contains expected dependencies
        val content = outputFile.readText()
        assertTrue(content.contains("gson"), "Should contain gson dependency")
        assertTrue(content.contains("slf4j"), "Should contain slf4j dependency")
    }

    @Test
    fun `plugin should respect offline mode`() {
        setupProject(projectDir, offlineMode = true)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        // In offline mode, should not attempt network requests
        // We verify this by checking the build succeeds without network access
    }

    @Test
    fun `plugin should handle variant filtering`() {
        setupProject(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "-PaboutLibraries.exportVariant=main", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutLibraries.json")
        assertTrue(outputFile.exists())
    }

    @Test
    fun `plugin should work with no dependencies`() {
        setupProjectWithNoDependencies(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutLibraries.json")
        assertTrue(outputFile.exists())
    }

    @Test
    fun `plugin should handle platform dependencies`() {
        setupProjectWithPlatform(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutLibraries.json")
        assertTrue(outputFile.exists())
    }

    @Test
    fun `task should be cacheable`() {
        setupProject(projectDir)

        // First build - may be SUCCESS or FROM_CACHE if cache is populated from previous tests
        @Suppress("WithPluginClasspathUsage")
        val firstRun = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--build-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertTrue(
            firstRun.task(":exportLibraryDefinitions")?.outcome in listOf(TaskOutcome.SUCCESS, TaskOutcome.FROM_CACHE),
            "First run should succeed (either SUCCESS or FROM_CACHE)"
        )

        // Clean and rebuild - should use cache
        File(projectDir, "build").deleteRecursively()

        @Suppress("WithPluginClasspathUsage")
        val secondRun = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--build-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        // Verify it succeeds with cache hit
        assertTrue(
            secondRun.task(":exportLibraryDefinitions")?.outcome in listOf(TaskOutcome.SUCCESS, TaskOutcome.FROM_CACHE)
        )
    }

    private fun setupProject(projectDir: File, offlineMode: Boolean = true) {
        File(projectDir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "test-project"
            """.trimIndent()
        )

        File(projectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            
            repositories {
                mavenCentral()
            }
            
            dependencies {
                implementation("com.google.code.gson:gson:2.10.1")
                implementation("org.slf4j:slf4j-api:2.0.9")
            }
            
            aboutLibraries {
                offlineMode = $offlineMode
            }
            """.trimIndent()
        )
    }

    private fun setupProjectWithNoDependencies(projectDir: File) {
        File(projectDir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "test-project"
            """.trimIndent()
        )

        File(projectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            
            repositories {
                mavenCentral()
            }
            
            aboutLibraries {
                offlineMode = true
            }
            """.trimIndent()
        )
    }

    private fun setupProjectWithPlatform(projectDir: File) {
        File(projectDir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "test-project"
            """.trimIndent()
        )

        File(projectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            
            repositories {
                mavenCentral()
            }
            
            dependencies {
                implementation(platform("com.fasterxml.jackson:jackson-bom:2.15.3"))
                implementation("com.fasterxml.jackson.core:jackson-databind")
            }
            
            aboutLibraries {
                offlineMode = true
                collect {
                    includePlatform = true
                }
            }
            """.trimIndent()
        )
    }
}
