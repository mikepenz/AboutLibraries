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
 * Verifies the plugin resolves dependencies on a Kotlin Multiplatform module that declares
 * an Android target via AGP 9's `com.android.kotlin.multiplatform.library` plugin. Regression
 * coverage for "android targets are not handled by the KMP plugin".
 */
class KmpAndroidFunctionalTest {

    @TempDir
    lateinit var projectDir: File

    private val kotlinVersion: String = System.getProperty("test.kotlin.version")
        ?: error("test.kotlin.version system property must be set by the test task")
    private val agpVersion: String = System.getProperty("test.agp.version")
        ?: error("test.agp.version system property must be set by the test task")

    @Test
    fun `plugin registers an android-specific exportLibraryDefinitions task in KMP module`() {
        setupKmpAndroidProject(projectDir)

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("tasks", "--all", "--stacktrace")
            .build()

        val androidTaskRegex = Regex("exportLibraryDefinitions(Android\\w*|Debug|Release)")
        assertTrue(
            androidTaskRegex.containsMatchIn(result.output),
            "Expected an android-specific exportLibraryDefinitions* task to be registered. Tasks output:\n${result.output}"
        )
    }

    @Test
    fun `android-specific exportLibraryDefinitions task runs without requiring outputFile`() {
        setupKmpAndroidProject(projectDir)

        // discover the android-specific task name (e.g. exportLibraryDefinitionsAndroid)
        val tasksResult = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("tasks", "--all", "--stacktrace")
            .build()
        val taskName = Regex("exportLibraryDefinitions(Android\\w*|Debug|Release)")
            .find(tasksResult.output)?.value
            ?: error("Expected an android-specific exportLibraryDefinitions* task. Tasks output:\n${tasksResult.output}")

        // regression: without configureOutputFile() this fails with "outputFile Value not set"
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(taskName, "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")?.outcome)

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
        assertTrue(outputFile.exists(), "Output file should be created by :$taskName")
    }

    @Test
    fun `plugin resolves android-target dependencies in KMP module`() {
        setupKmpAndroidProject(projectDir)

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("exportLibraryDefinitions", "--stacktrace")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val outputFile = File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json")
        assertTrue(outputFile.exists(), "Output file should be created")

        val content = outputFile.readText()
        assertTrue(
            content.contains("androidx.annotation:annotation"),
            "Android-target dependency (androidx.annotation) should be present. Output: $content"
        )
        assertTrue(
            content.contains("com.google.code.gson:gson"),
            "commonMain dependency (gson) should be present. Output: $content"
        )
    }

    private val pluginClasspath: String by lazy {
        val resource = javaClass.classLoader.getResource("plugin-under-test-metadata.properties")
            ?: error("plugin-under-test-metadata.properties not found on test classpath")
        val props = Properties().apply { resource.openStream().use { load(it) } }
        val cp = props.getProperty("implementation-classpath")
            ?: error("implementation-classpath missing from plugin-under-test-metadata.properties")
        cp.split(File.pathSeparator).joinToString(", ") { "files(\"${it.replace("\\", "\\\\")}\")" }
    }

    private fun setupKmpAndroidProject(projectDir: File) {
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
            rootProject.name = "kmp-android-test"
            """.trimIndent()
        )

        File(projectDir, "build.gradle.kts").writeText(
            """
            buildscript {
                repositories {
                    google()
                    mavenCentral()
                    gradlePluginPortal()
                }
                dependencies {
                    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
                    classpath("com.android.tools.build:gradle:$agpVersion")
                    classpath(files($pluginClasspath))
                }
            }

            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            apply(plugin = "com.android.kotlin.multiplatform.library")
            apply(plugin = "com.mikepenz.aboutlibraries.plugin")

            extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
                jvm()
                (this as org.gradle.api.plugins.ExtensionAware).extensions
                    .configure<com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension>("android") {
                        namespace = "com.mikepenz.aboutlibraries.kmpandroidtest"
                        compileSdk = 35
                        minSdk = 24
                    }
                sourceSets.getByName("commonMain").dependencies {
                    implementation("com.google.code.gson:gson:2.11.0")
                }
                sourceSets.getByName("androidMain").dependencies {
                    implementation("androidx.annotation:annotation:1.9.1")
                }
            }

            extensions.configure<com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension>("aboutLibraries") {
                offlineMode = true
            }
            """.trimIndent()
        )
    }
}
