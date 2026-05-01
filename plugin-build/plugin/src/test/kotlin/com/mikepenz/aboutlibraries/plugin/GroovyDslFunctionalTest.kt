package com.mikepenz.aboutlibraries.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Functional tests verifying the AboutLibraries plugin works correctly with Groovy DSL build scripts.
 *
 * Groovy's `DefaultGroovyMethods.collect(Object, Closure)` shadows the plugin's
 * `collect(Action<CollectorConfig>)` method, so the standard `collect { configPath = ... }`
 * syntax does NOT work. These tests verify working alternatives.
 *
 * See: https://github.com/mikepenz/AboutLibraries/issues/1328
 */
class GroovyDslFunctionalTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `groovy dsl - collect block closure syntax fails due to groovy collect shadowing`() {
        // This test documents the BROKEN syntax that users hit in issue #1328.
        // Groovy's built-in `collect(Closure)` on Object shadows the plugin method.
        setupGroovyProject(
            projectDir,
            """
            aboutLibraries {
                offlineMode = true
                collect {
                    configPath = file("config")
                }
            }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .buildAndFail()

        // The closure is interpreted by Groovy's collect, not the plugin's collect
        val output = result.output
        assertTrue(
            output.contains("MissingPropertyException") || output.contains("unknown property"),
            "Should fail because Groovy's built-in collect() shadows the plugin method. Output: $output"
        )
    }

    @Test
    fun `groovy dsl - getCollect() dot notation works`() {
        // RECOMMENDED workaround: use getCollect() to bypass Groovy's collect method
        setupGroovyProject(
            projectDir,
            """
            aboutLibraries {
                offlineMode = true
                getCollect().configPath = file("config")
                getCollect().fetchRemoteLicense = false
            }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)
    }

    @Test
    fun `groovy dsl - collect property accessor dot notation works`() {
        // Alternative: access the `collect` property directly (Groovy property access, not method call)
        setupGroovyProject(
            projectDir,
            """
            aboutLibraries {
                offlineMode = true
                collect.configPath = file("config")
                collect.fetchRemoteLicense = false
            }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)
    }

    @Test
    fun `groovy dsl - license and library blocks work normally`() {
        // `license` and `library` don't conflict with Groovy built-ins
        setupGroovyProject(
            projectDir,
            """
            import com.mikepenz.aboutlibraries.plugin.DuplicateMode
            import com.mikepenz.aboutlibraries.plugin.DuplicateRule
            import com.mikepenz.aboutlibraries.plugin.StrictMode

            aboutLibraries {
                offlineMode = true
                license {
                    strictMode = StrictMode.WARN
                    allowedLicenses.addAll("Apache-2.0", "MIT")
                }
                library {
                    duplicationMode = DuplicateMode.MERGE
                    duplicationRule = DuplicateRule.EXACT
                }
            }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)
    }

    @Test
    fun `groovy dsl - full configuration with getCollect workaround`() {
        // Full configuration example combining all blocks
        setupGroovyProject(
            projectDir,
            """
            import com.mikepenz.aboutlibraries.plugin.DuplicateMode
            import com.mikepenz.aboutlibraries.plugin.DuplicateRule
            import com.mikepenz.aboutlibraries.plugin.StrictMode

            aboutLibraries {
                offlineMode = true

                // Use getCollect() or collect. to configure the collector
                getCollect().configPath = file("config")
                getCollect().fetchRemoteLicense = false

                export {
                    prettyPrint = true
                }

                license {
                    strictMode = StrictMode.WARN
                    allowedLicenses.addAll("Apache-2.0", "MIT")
                }

                library {
                    duplicationMode = DuplicateMode.MERGE
                    duplicationRule = DuplicateRule.EXACT
                }
            }
            """.trimIndent()
        )

        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)
    }

    private fun setupGroovyProject(
        projectDir: File,
        aboutLibrariesBlock: String,
    ) {
        // Use Groovy DSL settings file
        File(projectDir, "settings.gradle").writeText(
            """
            rootProject.name = 'test-groovy-project'
            """.trimIndent()
        )

        // Create a config directory (for configPath tests)
        File(projectDir, "config").mkdirs()

        // Groovy DSL build file
        File(projectDir, "build.gradle").writeText(
            """
            plugins {
                id 'java-library'
                id 'com.mikepenz.aboutlibraries.plugin'
            }

            repositories {
                mavenCentral()
            }

            dependencies {
                implementation 'com.google.code.gson:gson:2.11.0'
                implementation 'org.slf4j:slf4j-api:2.0.16'
            }

            $aboutLibrariesBlock
            """.trimIndent()
        )
    }
}
