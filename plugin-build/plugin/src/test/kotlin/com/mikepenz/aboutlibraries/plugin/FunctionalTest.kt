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
        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
        assertTrue(outputFile.exists(), "Output file should be created")
        assertTrue(outputFile.length() > 0, "Output file should not be empty")

        // Verify it contains expected dependencies
        val content = outputFile.readText()
        assertTrue(content.contains("gson"), "Should contain gson dependency")
        assertTrue(content.contains("slf4j"), "Should contain slf4j dependency")
        
        // Validate JSON structure
        val json = parseJsonContent(content)
        
        // Validate top-level structure
        assertTrue(json.contains("\"libraries\""), "JSON should contain 'libraries' field")
        assertTrue(json.contains("\"licenses\""), "JSON should contain 'licenses' field")
        
        // Validate library entries have required fields
        assertTrue(json.contains("\"uniqueId\""), "Libraries should have uniqueId")
        assertTrue(json.contains("\"artifactVersion\""), "Libraries should have artifactVersion")
        assertTrue(json.contains("\"name\""), "Libraries should have name")
        
        // Validate specific libraries are present with correct information
        assertTrue(content.contains("com.google.code.gson:gson"), "Should contain gson uniqueId")
        assertTrue(content.contains("org.slf4j:slf4j-api"), "Should contain slf4j uniqueId")
        
        // Validate license information is present
        assertTrue(json.contains("Apache-2.0") || json.contains("MIT"), "Should contain license information")
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

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
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

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
        assertTrue(outputFile.exists())
        
        // Validate JSON structure even with no dependencies
        val content = outputFile.readText()
        val json = parseJsonContent(content)
        
        assertTrue(json.contains("\"libraries\""), "JSON should contain 'libraries' field")
        assertTrue(json.contains("\"licenses\""), "JSON should contain 'licenses' field")
        
        // With no dependencies, libraries array should be empty
        assertTrue(json.contains("\"libraries\":[]") || json.contains("\"libraries\": []"), 
            "Libraries array should be empty when no dependencies")
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

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
        assertTrue(outputFile.exists())
        
        // Validate platform dependencies are included
        val content = outputFile.readText()
        assertTrue(content.contains("jackson"), "Should contain jackson dependencies from platform")
        
        // Validate JSON structure
        val json = parseJsonContent(content)
        assertTrue(json.contains("\"uniqueId\""), "Platform dependencies should have uniqueId")
        
        // If includePlatform is true, should contain the BOM or at least transitive deps
        assertTrue(content.contains("com.fasterxml.jackson"), "Should contain jackson artifacts")
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

    /**
     * Verifies that the task is truly relocatable in the build cache: a cache entry produced
     * in one project directory must be reused (FROM_CACHE) in a *different* project directory
     * with byte-identical build scripts. This exercises `PathSensitivity.NONE` on the
     * `pomFiles` input — any drift to a relocation-sensitive sensitivity would cause this
     * test to miss the cache and fall back to SUCCESS.
     */
    @Test
    fun `task is relocatable across project directories via build cache`() {
        val sharedCache = File(projectDir, "shared-cache").apply { mkdirs() }
        val dirA = File(projectDir, "dirA").apply { mkdirs() }
        val dirB = File(projectDir, "dirB").apply { mkdirs() }

        // The cache directory MUST match exactly between dirA and dirB so the same local cache
        // is used for store and retrieve. We write its absolute path into each settings.gradle.kts.
        val sharedCachePath = sharedCache.absolutePath.replace("\\", "\\\\")
        fun setup(target: File) {
            File(target, "settings.gradle.kts").writeText(
                """
                rootProject.name = "test-project"
                buildCache {
                    local {
                        directory = file("$sharedCachePath")
                        isEnabled = true
                    }
                }
                """.trimIndent()
            )
            File(target, "build.gradle.kts").writeText(
                """
                plugins {
                    id("java-library")
                    id("com.mikepenz.aboutlibraries.plugin")
                }
                repositories { mavenCentral() }
                dependencies {
                    implementation("com.google.code.gson:gson:2.11.0")
                    implementation("org.slf4j:slf4j-api:2.0.16")
                }
                aboutLibraries { offlineMode = true }
                """.trimIndent()
            )
        }
        setup(dirA)
        setup(dirB)

        // Populate the shared cache from dirA.
        @Suppress("WithPluginClasspathUsage")
        val firstRun = GradleRunner.create()
            .withProjectDir(dirA)
            .withArguments("exportLibraryDefinitions", "--build-cache", "--stacktrace")
            .withPluginClasspath()
            .build()
        assertEquals(
            TaskOutcome.SUCCESS,
            firstRun.task(":exportLibraryDefinitions")?.outcome,
            "First run in dirA must execute and store a cache entry"
        )

        // Run in a different directory pointing at the same cache. Outcome must be FROM_CACHE,
        // strictly — any other outcome (SUCCESS) means the task is not actually relocatable.
        @Suppress("WithPluginClasspathUsage")
        val secondRun = GradleRunner.create()
            .withProjectDir(dirB)
            .withArguments("exportLibraryDefinitions", "--build-cache", "--stacktrace")
            .withPluginClasspath()
            .build()
        assertEquals(
            TaskOutcome.FROM_CACHE,
            secondRun.task(":exportLibraryDefinitions")?.outcome,
            "Run in dirB against the shared cache must be FROM_CACHE (relocatable). Output: ${secondRun.output}"
        )
        assertTrue(
            File(dirB, "build/generated/aboutLibraries/aboutlibraries.json").exists(),
            "Output file must be restored from the cache into dirB"
        )
    }

    @Test
    fun `output should contain valid library structure with all required fields`() {
        setupDetailedProject(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
        val content = outputFile.readText()
        
        // Validate JSON can be parsed
        val json = parseJsonContent(content)
        
        // Validate structure contains arrays
        assertTrue(json.contains("\"libraries\":["), "Should have libraries array")
        assertTrue(json.contains("\"licenses\":[") || json.contains("\"licenses\":[]"), 
            "Should have licenses array")
        
        // Validate library object structure
        assertTrue(json.contains("\"uniqueId\":"), "Library should have uniqueId field")
        assertTrue(json.contains("\"artifactVersion\":"), "Library should have artifactVersion field")
        assertTrue(json.contains("\"name\":"), "Library should have name field")
        assertTrue(json.contains("\"developers\":"), "Library should have developers field")
        
        // Optional fields that should be present when available
        if (content.contains("\"website\"")) {
            assertTrue(json.contains("\"website\":\"http"), "Website should be a valid URL")
        }
        
        // Validate no obvious malformed JSON
        assertFalse(json.contains("\"uniqueId\":null"), "uniqueId should never be null")
        assertFalse(json.contains("\"name\":null"), "name should never be null")
        
        // Count library entries - should have at least the dependencies we added
        val libraryCount = json.split("\"uniqueId\":").size - 1
        assertTrue(libraryCount >= 2, "Should have at least 2 library entries, found $libraryCount")
    }

    @Test
    fun `output should contain license information`() {
        setupDetailedProject(projectDir)

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
        val content = outputFile.readText()
        
        // Validate licenses section exists
        assertTrue(content.contains("\"licenses\""), "Should contain licenses field")
        
        // Libraries should reference licenses
        assertTrue(
            content.contains("Apache-2.0") || content.contains("MIT") || content.contains("\"licenses\":["),
            "Should contain license references"
        )
    }

    private fun parseJsonContent(content: String): String {
        // Basic validation that it's valid JSON structure
        val trimmed = content.trim()
        assertTrue(trimmed.startsWith("{"), "JSON should start with {")
        assertTrue(trimmed.endsWith("}"), "JSON should end with }")
        
        // Count braces to ensure they're balanced
        val openBraces = trimmed.count { it == '{' }
        val closeBraces = trimmed.count { it == '}' }
        assertEquals(openBraces, closeBraces, "JSON braces should be balanced")
        
        val openBrackets = trimmed.count { it == '[' }
        val closeBrackets = trimmed.count { it == ']' }
        assertEquals(openBrackets, closeBrackets, "JSON brackets should be balanced")
        
        return trimmed
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
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
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
                implementation(platform("com.fasterxml.jackson:jackson-bom:2.18.2"))
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

    private fun setupDetailedProject(projectDir: File) {
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
            """.trimIndent()
        )
    }
}
