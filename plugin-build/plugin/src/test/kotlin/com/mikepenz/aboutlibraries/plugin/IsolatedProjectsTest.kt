package com.mikepenz.aboutlibraries.plugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.Properties

/**
 * Isolated Projects (https://blog.gradle.org/introducing-isolated-projects) is the strict-mode
 * evolution of the configuration cache: a project's configuration may not read or mutate the
 * state of any *other* project, and each project's configuration model is cached (and
 * invalidated) independently.
 *
 * [ConfigurationCacheTest] covers plain configuration-cache behaviour plus two smoke-level IP
 * runs. This suite is the dedicated IP guarantee, exercising the cases where the plugin could
 * realistically reach across a project boundary:
 *
 *  - project-to-project (`implementation(project(":lib"))`) dependency graphs,
 *  - all plugin-registered tasks in one invocation,
 *  - [AboutLibrariesExportComplianceTask], the only task that consults the *root* project
 *    directory (via `project.isolated`),
 *  - per-project cache invalidation — editing one module must not invalidate the other,
 *  - Kotlin Multiplatform per-target task registration.
 */
class IsolatedProjectsTest {

    @TempDir
    lateinit var projectDir: File

    private val kotlinVersion: String = System.getProperty("test.kotlin.version")
        ?: error("test.kotlin.version system property must be set by the test task")

    // region Scenarios

    /**
     * The core IP stressor: `:app` depends on `project(":lib")`, so resolving `:app`'s compile
     * classpath pulls in another project's artifacts. The plugin resolves configurations at
     * configuration time — it must do so without reaching into `:lib`'s mutable project state.
     *
     * Also asserts the plugin's existing behaviour of skipping project dependencies while still
     * collecting the external dependencies they contribute transitively.
     */
    @Test
    fun `project to project dependencies do not violate project isolation`() {
        writeSettings(":app", ":lib")
        writeIpProperties()
        File(projectDir, "lib").mkdirs()
        File(projectDir, "lib/build.gradle.kts").writeText(
            """
            plugins { id("java-library") }
            repositories { mavenCentral() }
            dependencies { api("org.slf4j:slf4j-api:2.0.16") }
            """.trimIndent()
        )
        File(projectDir, "app").mkdirs()
        File(projectDir, "app/build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            repositories { mavenCentral() }
            dependencies {
                implementation(project(":lib"))
                implementation("com.google.code.gson:gson:2.11.0")
            }
            aboutLibraries { offlineMode = true }
            """.trimIndent()
        )

        val result = runIsolated(":app:exportLibraryDefinitions")
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:exportLibraryDefinitions")?.outcome)

        val json = File(projectDir, "app/build/generated/aboutLibraries/aboutlibraries.json").readText()
        assertTrue(json.contains("com.google.code.gson:gson"), "direct dependency must be present")
        assertTrue(json.contains("org.slf4j:slf4j-api"), "external dependency exposed by :lib must be present")
        assertFalse(json.contains("\":lib\""), "the project dependency itself must not be exported")
    }

    /**
     * Every task the base plugin registers, run in a single invocation under IP. Task
     * *registration* alone can trip isolation (e.g. a convention resolved off the root project),
     * so this guards the whole registered surface, not just `exportLibraryDefinitions`.
     */
    @Test
    fun `all registered tasks run under project isolation`() {
        writeSettings(":app")
        writeIpProperties()
        File(projectDir, "app").mkdirs()
        // `exportFunding` requires a configured `collect.configPath` to write into.
        File(projectDir, "app/config").mkdirs()
        File(projectDir, "app/build.gradle.kts").writeText(
            """
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            repositories { mavenCentral() }
            dependencies { implementation("com.google.code.gson:gson:2.11.0") }
            aboutLibraries {
                offlineMode = true
                collect.configPath = file("config")
            }
            """.trimIndent()
        )

        val tasks = listOf("exportLibraryDefinitions", "findLibraries", "exportLibraries", "fundLibraries", "exportFunding")
        val result = runIsolated(*tasks.map { ":app:$it" }.toTypedArray())
        tasks.forEach {
            assertEquals(TaskOutcome.SUCCESS, result.task(":app:$it")?.outcome, "task :app:$it should succeed under IP")
        }
    }

    /**
     * [AboutLibrariesExportComplianceTask] resolves a user-supplied `aboutLibraries.exportPath`
     * against the **root** project directory. Under IP that lookup must go through
     * `project.isolated.rootProject`, never `project.rootProject`. The task is only auto-registered
     * for Android variants, so it is registered manually here to keep the test AGP-free.
     */
    @Test
    fun `export compliance task resolves root directory under project isolation`() {
        writeSettings(":app")
        writeIpProperties("aboutLibraries.exportPath=compliance-out")
        File(projectDir, "app").mkdirs()
        File(projectDir, "app/build.gradle.kts").writeText(
            """
            import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExportComplianceTask

            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            repositories { mavenCentral() }
            dependencies { implementation("com.google.code.gson:gson:2.11.0") }
            aboutLibraries { offlineMode = true }

            tasks.register("exportComplianceLibraries", AboutLibrariesExportComplianceTask::class.java) {
                configure()
            }
            """.trimIndent()
        )

        val result = runIsolated(":app:exportComplianceLibraries")
        assertEquals(TaskOutcome.SUCCESS, result.task(":app:exportComplianceLibraries")?.outcome)

        // `exportPath` is relative, so it must land under the ROOT project dir, not under :app.
        val exportCsv = File(projectDir, "compliance-out/export.csv")
        assertTrue(exportCsv.exists(), "export.csv must be written relative to the root project directory")
        assertTrue(exportCsv.readText().contains("gson"), "export.csv should list the collected libraries")
        assertFalse(
            File(projectDir, "app/compliance-out").exists(),
            "export path must not be resolved against the subproject directory"
        )
    }

    /**
     * The headline IP benefit: per-project configuration caching. Editing `:modb`'s build script
     * must invalidate only `:modb` — `:moda`'s cached configuration and task state must survive.
     */
    @Test
    fun `editing one module does not invalidate the other under project isolation`() {
        writeSettings(":moda", ":modb")
        writeIpProperties()
        File(projectDir, "moda").mkdirs()
        File(projectDir, "moda/build.gradle.kts").writeText(module("com.google.code.gson:gson:2.11.0"))
        File(projectDir, "modb").mkdirs()
        val modbBuild = File(projectDir, "modb/build.gradle.kts")
        modbBuild.writeText(module("org.slf4j:slf4j-api:2.0.16"))

        val first = runIsolated(":moda:exportLibraryDefinitions", ":modb:exportLibraryDefinitions")
        assertEquals(TaskOutcome.SUCCESS, first.task(":moda:exportLibraryDefinitions")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, first.task(":modb:exportLibraryDefinitions")?.outcome)

        // Change only :modb.
        modbBuild.writeText(module("org.slf4j:slf4j-api:2.0.17"))

        val second = runIsolated(":moda:exportLibraryDefinitions", ":modb:exportLibraryDefinitions")
        assertEquals(
            TaskOutcome.UP_TO_DATE, second.task(":moda:exportLibraryDefinitions")?.outcome,
            "untouched module must stay UP-TO-DATE; its cached project model must not be invalidated by a sibling edit"
        )
        assertEquals(
            TaskOutcome.SUCCESS, second.task(":modb:exportLibraryDefinitions")?.outcome,
            "edited module must re-run"
        )
        assertTrue(
            File(projectDir, "modb/build/generated/aboutLibraries/aboutlibraries.json").readText().contains("2.0.17"),
            "edited module output must reflect the new version"
        )
    }

    /**
     * KMP registers one `exportLibraryDefinitions<Target>` task per target via
     * `targets.configureEach {}`. Verifies that per-target registration and execution are
     * IP-clean, and that a target-specific task only reports that target's dependencies.
     */
    @Test
    fun `kotlin multiplatform per target tasks run under project isolation`() {
        File(projectDir, "settings.gradle.kts").writeText(
            """
            pluginManagement { repositories { gradlePluginPortal(); mavenCentral() } }
            dependencyResolutionManagement { repositories { mavenCentral() } }
            rootProject.name = "ip-kmp"
            include(":shared")
            """.trimIndent()
        )
        writeIpProperties()
        File(projectDir, "shared").mkdirs()
        File(projectDir, "shared/build.gradle.kts").writeText(
            """
            buildscript {
                repositories { mavenCentral(); gradlePluginPortal() }
                dependencies {
                    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
                    classpath(files($pluginClasspath))
                }
            }

            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            apply(plugin = "com.mikepenz.aboutlibraries.plugin")

            extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
                jvm()
                sourceSets.getByName("commonMain").dependencies {
                    implementation("com.google.code.gson:gson:2.11.0")
                }
                sourceSets.getByName("jvmMain").dependencies {
                    implementation("org.slf4j:slf4j-api:2.0.16")
                }
            }

            extensions.configure<com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension>("aboutLibraries") {
                offlineMode = true
            }
            """.trimIndent()
        )

        val result = runIsolated(":shared:exportLibraryDefinitionsJvm")
        assertEquals(TaskOutcome.SUCCESS, result.task(":shared:exportLibraryDefinitionsJvm")?.outcome)

        val json = File(projectDir, "shared/build/generated/aboutLibraries/aboutlibraries.json").readText()
        assertTrue(json.contains("com.google.code.gson:gson"), "commonMain dependency must be present")
        assertTrue(json.contains("org.slf4j:slf4j-api"), "jvmMain dependency must be present")
    }

    /**
     * A build that never runs an AboutLibraries task must still configure cleanly under IP —
     * plugin *application* (extension creation, task registration, KMP/Android hooks) is itself
     * a common source of isolation violations.
     */
    @Test
    fun `plugin application alone is isolation clean across many modules`() {
        writeSettings(":m1", ":m2", ":m3")
        writeIpProperties()
        listOf("m1", "m2", "m3").forEach { name ->
            File(projectDir, name).mkdirs()
            File(projectDir, "$name/build.gradle.kts").writeText(module("com.google.code.gson:gson:2.11.0"))
        }

        val result = runIsolated("help")
        assertEquals(TaskOutcome.SUCCESS, result.task(":help")?.outcome)
    }

    // endregion

    // region Helpers

    private fun module(dependency: String) =
        """
        plugins {
            id("java-library")
            id("com.mikepenz.aboutlibraries.plugin")
        }
        repositories { mavenCentral() }
        dependencies { implementation("$dependency") }
        aboutLibraries { offlineMode = true }
        """.trimIndent()

    private fun writeSettings(vararg modules: String) {
        File(projectDir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "ip-test"
            include(${modules.joinToString(", ") { "\"$it\"" }})
            """.trimIndent()
        )
    }

    private fun writeIpProperties(vararg extra: String) {
        File(projectDir, "gradle.properties").writeText(
            (
                listOf(
                    "org.gradle.unsafe.isolated-projects=true",
                    "org.gradle.configuration-cache=true",
                ) + extra
                ).joinToString("\n")
        )
    }

    /**
     * Runs [arguments] with Isolated Projects enabled (via `gradle.properties`) and asserts the
     * build reported no isolation/configuration-cache problems.
     *
     * A hard IP violation ("Project ':b' cannot access 'Project.tasks' functionality on another
     * project ':a'") fails the build, so `GradleRunner.build()` catches those on its own. The
     * extra assertions cover the two ways a build can stay green while IP silently does not
     * work: the property being unrecognised by the Gradle version under test, and problems that
     * only discard the cache entry.
     */
    private fun runIsolated(vararg arguments: String): BuildResult {
        @Suppress("WithPluginClasspathUsage")
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(*arguments, "--stacktrace")
            .withPluginClasspath()
            .build()

        assertTrue(
            result.output.contains("Isolated Projects is an incubating feature"),
            "Isolated Projects must actually be enabled — the property was not recognised by this Gradle version. Output: ${result.output}"
        )
        listOf(
            "Configuration cache entry discarded",
            "cannot access", // "Project ':b' cannot access 'Project.tasks' functionality on another project ':a'"
            "invocation of '", // "invocation of 'Task.project' at execution time"
        ).forEach { marker ->
            assertFalse(
                result.output.contains(marker, ignoreCase = true),
                "Project isolation violation ('$marker') reported for ${arguments.toList()}. Output: ${result.output}"
            )
        }
        return result
    }

    /** Plugin classpath rendered as `files(...)` entries, for builds that need a `buildscript` block. */
    private val pluginClasspath: String by lazy {
        val resource = javaClass.classLoader.getResource("plugin-under-test-metadata.properties")
            ?: error("plugin-under-test-metadata.properties not found on test classpath")
        val props = Properties().apply { resource.openStream().use { load(it) } }
        val cp = props.getProperty("implementation-classpath")
            ?: error("implementation-classpath missing from plugin-under-test-metadata.properties")
        cp.split(File.pathSeparator).joinToString(", ") { "files(\"${it.replace("\\", "\\\\")}\")" }
    }

    // endregion
}
