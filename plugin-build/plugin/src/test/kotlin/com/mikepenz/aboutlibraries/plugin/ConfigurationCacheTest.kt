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

    /**
     * Verifies that the configuration cache survives across builds even when the plugin needs to
     * resolve parent POMs with overlapping group:artifact but different versions
     * (e.g. `guava-parent:33.3.1-jre` and `guava-parent:26.0-android`).
     */
    @Test
    fun `configuration cache works with multi-version parent POMs`() {
        File(projectDir, "settings.gradle.kts").writeText("""rootProject.name = "test-project"""")
        File(projectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            repositories { mavenCentral() }
            dependencies {
                // guava pulls failureaccess (whose parent is guava-parent:26.0-android)
                // and itself uses guava-parent:33.3.1-jre — both must resolve.
                implementation("com.google.guava:guava:33.3.1-jre")
            }
            aboutLibraries { offlineMode = true }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val first = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()
        assertTrue(first.output.contains("Configuration cache entry stored"))
        assertEquals(TaskOutcome.SUCCESS, first.task(":exportLibraryDefinitions")?.outcome)

        val firstOutput = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json").readText()
        assertTrue(firstOutput.contains("Kevin Bourrillion"), "Should inherit dev from guava-parent")

        @Suppress("WithPluginClasspathUsage")
        val second = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()
        assertTrue(second.output.contains("Configuration cache entry reused"))
        assertEquals(TaskOutcome.UP_TO_DATE, second.task(":exportLibraryDefinitions")?.outcome)
    }

    /**
     * Project isolation is the strict-mode evolution of configuration cache. Tasks must not
     * reach across project boundaries at execution time. This test enables both
     * `--configuration-cache` and `org.gradle.unsafe.isolated-projects=true` and confirms the
     * plugin's task graph is registered without isolation problems.
     */
    @Test
    fun `plugin should work with project isolation enabled`() {
        File(projectDir, "settings.gradle.kts").writeText("""rootProject.name = "test-project"""")
        File(projectDir, "gradle.properties").writeText(
            """
            org.gradle.unsafe.isolated-projects=true
            org.gradle.configuration-cache=true
            """.trimIndent()
        )
        File(projectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            repositories { mavenCentral() }
            dependencies {
                implementation("com.google.code.gson:gson:2.11.0")
            }
            aboutLibraries { offlineMode = true }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val first = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()
        assertEquals(TaskOutcome.SUCCESS, first.task(":exportLibraryDefinitions")?.outcome)
        // Project isolation should NOT discard the cache entry due to plugin code.
        assertFalse(
            first.output.contains("Configuration cache entry discarded"),
            "Plugin must not cause project-isolation problems. Output: ${first.output}"
        )

        // Second run reuses the cache.
        @Suppress("WithPluginClasspathUsage")
        val second = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()
        assertTrue(
            second.output.contains("Configuration cache entry reused") ||
                second.output.contains("Reusing configuration cache"),
            "Cache should be reused on second run. Output: ${second.output}"
        )
    }

    /**
     * Regression test: when two configurations contribute the SAME `group:artifact` at different
     * versions (e.g. `commons-io:2.11.0` in one config and `commons-io:2.16.1` in another),
     * batching all coordinates into a single detached configuration would let Gradle's conflict
     * resolver pick one version and silently drop the POM for the other, leaving the dropped
     * version with synthetic (metadata-less) data. The plugin must fetch each conflicting
     * `group:artifact` version individually so both POMs are resolved.
     */
    @Test
    fun `multi-version same artifact across configurations both resolve`() {
        File(projectDir, "settings.gradle.kts").writeText("""rootProject.name = "test-project"""")
        File(projectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            repositories { mavenCentral() }
            configurations {
                create("variantA") { isCanBeResolved = true; isCanBeConsumed = false }
                create("variantB") { isCanBeResolved = true; isCanBeConsumed = false }
            }
            dependencies {
                "variantA"("commons-io:commons-io:2.11.0")
                "variantB"("commons-io:commons-io:2.16.1")
            }
            aboutLibraries {
                collect.all = true
                offlineMode = true
            }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)
        assertFalse(
            result.output.contains("Failed to resolve POM batch"),
            "No POM batch should fail; both versions must be fetched individually. Output: ${result.output}"
        )

        // The output should contain commons-io with proper metadata inherited from its real POM
        // (Apache organization), not the synthetic fallback. Synthetic POMs have no organization.
        val output = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json").readText()
        assertTrue(output.contains("commons-io:commons-io"), "commons-io should be in the output")
        assertTrue(output.contains("Apache", ignoreCase = true), "commons-io metadata (Apache) should be inherited from its real POM, not synthetic")
    }

    /**
     * Regression test: Android variant test configurations are named with the variant prefix
     * (e.g. `debugAndroidTestCompileClasspath`, `releaseUnitTestCompileClasspath`) and do NOT
     * start with `test` or `androidTest`. The `isTest` heuristic must catch these so they are
     * excluded by default (when `includeTestVariants = false`).
     */
    @Test
    fun `android variant test configurations are excluded by default`() {
        File(projectDir, "settings.gradle.kts").writeText("""rootProject.name = "test-project"""")
        File(projectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            repositories { mavenCentral() }
            configurations {
                // Mimic AGP-generated test classpaths: these end in `CompileClasspath` so they
                // would otherwise be picked up by the default (non-collect-all) filter.
                create("debugAndroidTestCompileClasspath") { isCanBeResolved = true; isCanBeConsumed = false }
                create("releaseUnitTestCompileClasspath") { isCanBeResolved = true; isCanBeConsumed = false }
            }
            dependencies {
                implementation("com.google.code.gson:gson:2.11.0")
                "debugAndroidTestCompileClasspath"("junit:junit:4.13.2")
                "releaseUnitTestCompileClasspath"("org.mockito:mockito-core:5.12.0")
            }
            aboutLibraries { offlineMode = true }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)
        val output = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json").readText()
        assertTrue(output.contains("com.google.code.gson:gson"), "Production dep should be present")
        assertFalse(output.contains("junit:junit"), "junit from debugAndroidTest classpath must NOT leak into output")
        assertFalse(output.contains("org.mockito:mockito-core"), "mockito from releaseUnitTest classpath must NOT leak into output")
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
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
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
                implementation("com.google.code.gson:gson:2.11.0")
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
