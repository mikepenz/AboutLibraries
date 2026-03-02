package com.mikepenz.aboutlibraries.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Performance tests to verify that the plugin minimizes configuration-time overhead.
 * These tests ensure that dependency resolution happens during task execution, not configuration.
 */
class PerformanceTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `configuration phase should be fast for help task`() {
        setupLargeProject(projectDir)

        // Measure configuration time by running 'help' task which doesn't execute our tasks
        val startTime = System.currentTimeMillis()
        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("help", "--stacktrace")
            .withPluginClasspath()
            .build()
        val duration = System.currentTimeMillis() - startTime

        assertEquals(TaskOutcome.SUCCESS, result.task(":help")?.outcome)

        // Configuration should be fast - under 10 seconds even with many dependencies
        println("Configuration time: ${duration}ms")
        assertTrue(duration < 10000, "Configuration took too long: ${duration}ms")
    }

    @Test
    fun `configuration phase should not download POM files`() {
        setupProject(projectDir)

        // Run a task that doesn't execute our plugin's tasks
        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("tasks", "--info", "--stacktrace")
            .withPluginClasspath()
            .build()

        // Should not see POM downloads during configuration
        // The string "Download " indicates Gradle is downloading artifacts
        val downloadCount = result.output.lines().count { it.contains("Download ") && it.contains("pom") }
        assertEquals(0, downloadCount, "POM files should not be downloaded during configuration phase")
    }

    @Test
    fun `subsequent builds should be instant with up-to-date tasks`() {
        setupProject(projectDir)

        // First build
        @Suppress("WithPluginClasspathUsage")
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        // Second build should be instant
        val startTime = System.currentTimeMillis()
        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()
        val duration = System.currentTimeMillis() - startTime

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(":exportLibraryDefinitions")?.outcome)

        println("Up-to-date check time: ${duration}ms")
        assertTrue(duration < 5000, "Up-to-date check took too long: ${duration}ms")
    }

    private fun setupProject(projectDir: File) {
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
            """.trimIndent()
        )
    }

    private fun setupLargeProject(projectDir: File) {
        File(projectDir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "large-project"
            """.trimIndent()
        )

        // Create a project with many dependencies to stress-test configuration performance
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
                // Multiple popular libraries to simulate real-world project
                implementation("com.google.code.gson:gson:2.10.1")
                implementation("org.slf4j:slf4j-api:2.0.9")
                implementation("com.squareup.okhttp3:okhttp:4.12.0")
                implementation("com.google.guava:guava:32.1.3-jre")
                implementation("org.apache.commons:commons-lang3:3.13.0")
                implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                testImplementation("junit:junit:4.13.2")
                testImplementation("org.mockito:mockito-core:5.7.0")
            }
            
            aboutLibraries {
                offlineMode = true
            }
            """.trimIndent()
        )
    }
}
