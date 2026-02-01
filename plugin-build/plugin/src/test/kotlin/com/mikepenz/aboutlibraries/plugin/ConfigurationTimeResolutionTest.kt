package com.mikepenz.aboutlibraries.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Test to verify that dependency resolution happens ONLY during task execution,
 * not during configuration phase.
 */
class ConfigurationTimeResolutionTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `dry-run should NOT trigger dependency resolution`() {
        setupInstrumentedProject(projectDir)

        // Run with --dry-run - this ONLY runs configuration phase
        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--dry-run", "--stacktrace")
            .withPluginClasspath()
            .build()

        val output = result.output

        // Verify dry-run mode - tasks should be SKIPPED, not executed
        assertTrue(output.contains("SKIPPED"), "Should indicate dry run mode with SKIPPED tasks")

        // Should NOT see resolution messages during dry-run
        assertFalse(
            output.contains("Collecting dependencies"),
            "Should NOT collect dependencies during configuration (dry-run)"
        )
    }

    @Test
    fun `tasks command should NOT trigger dependency resolution`() {
        setupInstrumentedProject(projectDir)

        // Running 'tasks' should only do configuration, no execution
        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("tasks", "--stacktrace")
            .withPluginClasspath()
            .build()

        val output = result.output

        // Should see the task listed but not executed
        assertTrue(output.contains("exportLibraryDefinitions"))

        // Should NOT see "Collecting dependencies" log during tasks command
        assertFalse(
            output.contains("Collecting dependencies"),
            "Should NOT collect dependencies when listing tasks"
        )
    }

    @Test
    fun `configuration cache should work without problems`() {
        setupInstrumentedProject(projectDir)

        // First run with configuration cache
        @Suppress("WithPluginClasspathUsage")
        val firstRun = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertTrue(firstRun.output.contains("Configuration cache entry stored"))

        // Second run should reuse cache
        @Suppress("WithPluginClasspathUsage")
        val secondRun = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertTrue(secondRun.output.contains("Configuration cache entry reused"))

        // Should NOT have configuration cache problems
        assertFalse(
            secondRun.output.contains("configuration cache problems"),
            "Should not have configuration cache problems"
        )
    }

    private fun setupInstrumentedProject(projectDir: File) {
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
                offlineMode = true
            }
            
            // Log when configuration happens
            println("==> CONFIGURATION PHASE for project: ${'$'}name")
            """.trimIndent()
        )
    }
}
