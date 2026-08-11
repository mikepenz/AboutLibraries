package com.mikepenz.aboutlibraries.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.Properties

/**
 * Verifies the plugin works under the strict AGP 10.0 behavior flags documented in
 * https://developer.android.com/build/releases/gradle-plugin-roadmap#agp-10:
 * `android.newDsl=true` (only the new DSL / Variant API interfaces exist) and
 * `android.builtInKotlin=true` (no `kotlin-android` opt-out).
 *
 * Covers the `com.android.application` and `com.android.library` paths of
 * [configureAndroidTasks], which are otherwise only exercised for
 * `com.android.kotlin.multiplatform.library` (see [KmpAndroidFunctionalTest]).
 */
class Agp10CompatibilityTest {

    @TempDir
    lateinit var projectDir: File

    private val agpVersion: String = System.getProperty("test.agp.version")
        ?: error("test.agp.version system property must be set by the test task")

    @Test
    fun `library module registers and runs variant tasks under AGP 10 behavior flags`() {
        setupAndroidProject(projectDir, "com.android.library", "com.android.build.api.dsl.LibraryExtension")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitionsRelease", "prepareLibraryDefinitionsRelease", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitionsRelease")?.outcome)
        // registered via variant.sources.res.addGeneratedSourceDirectory (Sources API)
        assertEquals(TaskOutcome.SUCCESS, result.task(":prepareLibraryDefinitionsRelease")?.outcome)

        val content = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json").readText()
        assertTrue(content.contains("com.google.code.gson:gson"), "Dependency should be collected. Output: $content")
    }

    @Test
    fun `application module registers and runs variant tasks under AGP 10 behavior flags`() {
        setupAndroidProject(projectDir, "com.android.application", "com.android.build.api.dsl.ApplicationExtension")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitionsRelease", "prepareLibraryDefinitionsRelease", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitionsRelease")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":prepareLibraryDefinitionsRelease")?.outcome)
    }

    private val pluginClasspath: String by lazy {
        val resource = javaClass.classLoader.getResource("plugin-under-test-metadata.properties")
            ?: error("plugin-under-test-metadata.properties not found on test classpath")
        val props = Properties().apply { resource.openStream().use { load(it) } }
        val cp = props.getProperty("implementation-classpath")
            ?: error("implementation-classpath missing from plugin-under-test-metadata.properties")
        cp.split(File.pathSeparator).joinToString(", ") { "files(\"${it.replace("\\", "\\\\")}\")" }
    }

    private fun setupAndroidProject(projectDir: File, androidPluginId: String, extensionType: String) {
        File(projectDir, "gradle.properties").writeText(
            """
            android.useAndroidX=true
            # enforce AGP 10.0 behavior while running on AGP 9.x
            android.newDsl=true
            android.builtInKotlin=true
            """.trimIndent()
        )

        File(projectDir, "settings.gradle.kts").writeText(
            """
            pluginManagement {
                repositories {
                    google()
                    gradlePluginPortal()
                    mavenCentral()
                }
            }
            dependencyResolutionManagement {
                repositories {
                    google()
                    mavenCentral()
                }
            }
            rootProject.name = "agp10-test"
            """.trimIndent()
        )

        File(projectDir, "src/main").mkdirs()
        File(projectDir, "src/main/AndroidManifest.xml").writeText("<manifest />")

        File(projectDir, "build.gradle.kts").writeText(
            """
            buildscript {
                repositories {
                    google()
                    mavenCentral()
                    gradlePluginPortal()
                }
                dependencies {
                    classpath("com.android.tools.build:gradle:$agpVersion")
                    classpath(files($pluginClasspath))
                }
            }

            apply(plugin = "$androidPluginId")
            apply(plugin = "com.mikepenz.aboutlibraries.plugin")

            extensions.configure<$extensionType>("android") {
                namespace = "com.mikepenz.aboutlibraries.agp10test"
                compileSdk = 35
                defaultConfig {
                    minSdk = 24
                }
            }

            dependencies {
                "implementation"("com.google.code.gson:gson:2.11.0")
            }

            extensions.configure<com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension>("aboutLibraries") {
                offlineMode = true
            }
            """.trimIndent()
        )
    }
}
