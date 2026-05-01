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

    /**
     * Regression test for https://github.com/mikepenz/AboutLibraries/issues/1099
     *
     * `tasks.whenTaskAdded {}` forces eager realization of all lazily-registered tasks,
     * which means the plugin's `configure()` method (and its dependency resolution) runs
     * at registration time rather than at execution time. This test verifies that the plugin
     * still produces correct output under those conditions.
     */
    @Test
    fun `whenTaskAdded should not break library definitions generation`() {
        setupProjectWithWhenTaskAdded(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(
            org.gradle.testkit.runner.TaskOutcome.SUCCESS,
            result.task(":exportLibraryDefinitions")?.outcome,
            "Task should succeed even with whenTaskAdded present. Output: ${result.output}"
        )

        // Verify the output file exists and has content
        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
        assertTrue(outputFile.exists(), "Output file should be created")
        assertTrue(outputFile.length() > 0, "Output file should not be empty")

        // Verify it actually contains the expected dependencies
        val content = outputFile.readText()
        assertTrue(content.contains("gson"), "Should contain gson dependency")
        assertTrue(content.contains("slf4j"), "Should contain slf4j dependency")
    }

    /**
     * Verifies that dry-run does not trigger dependency resolution even with whenTaskAdded.
     */
    @Test
    fun `dry-run with whenTaskAdded should still NOT trigger task execution`() {
        setupProjectWithWhenTaskAdded(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--dry-run", "--stacktrace")
            .withPluginClasspath()
            .build()

        val output = result.output
        assertTrue(output.contains("SKIPPED"), "Should indicate dry run mode with SKIPPED tasks")
    }

    private fun setupProjectWithWhenTaskAdded(projectDir: File) {
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
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            }

            aboutLibraries {
                offlineMode = true
            }

            // Regression test for #1099: whenTaskAdded causes eager task realization
            tasks.whenTaskAdded {
                // This forces ALL lazily-registered tasks to be eagerly realized
                println("Task added: ${'$'}{this.name}")
            }
            """.trimIndent()
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
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
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
