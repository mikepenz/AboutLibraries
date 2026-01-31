package com.mikepenz.aboutlibraries.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Tests to verify configuration cache compatibility of the AboutLibraries plugin.
 * Configuration cache is a Gradle feature that caches the result of the configuration phase,
 * significantly improving build performance for subsequent builds.
 */
class ConfigurationCacheTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `plugin should work with configuration cache`() {
        // Setup a simple project
        setupProject(projectDir)

        // First build - populate configuration cache
        @Suppress("WithPluginClasspathUsage")
        val firstRun = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertTrue(firstRun.output.contains("Configuration cache entry stored"))
        assertEquals(TaskOutcome.SUCCESS, firstRun.task(":exportLibraryDefinitions")?.outcome)

        // Second build - should reuse configuration cache
        @Suppress("WithPluginClasspathUsage")
        val secondRun = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertTrue(secondRun.output.contains("Configuration cache entry reused"))
        assertEquals(TaskOutcome.UP_TO_DATE, secondRun.task(":exportLibraryDefinitions")?.outcome)
    }

    @Test
    fun `plugin should not resolve configurations during configuration phase`() {
        setupProject(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("tasks", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        // Running 'tasks' should not trigger dependency resolution
        assertTrue(result.output.contains("Configuration cache entry stored"))
    }

    @Test
    fun `configuration cache should work with multiple variants`() {
        setupProjectWithMultipleVariants(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val firstRun = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertTrue(firstRun.output.contains("Configuration cache entry stored"))

        @Suppress("WithPluginClasspathUsage")
        val secondRun = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertTrue(secondRun.output.contains("Configuration cache entry reused"))
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

    private fun setupProjectWithMultipleVariants(projectDir: File) {
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
            
            configurations {
                create("customConfig")
            }
            
            dependencies {
                implementation("com.google.code.gson:gson:2.10.1")
                "customConfig"("junit:junit:4.13.2")
                testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
            }
            
            aboutLibraries {
                offlineMode = true
            }
            """.trimIndent()
        )
    }
}
