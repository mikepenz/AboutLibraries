package com.mikepenz.aboutlibraries.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Regression tests for the *content* of the generated `aboutlibraries.json`.
 *
 * These tests pin behaviour that the lazy-resolution refactor must preserve so we don't silently
 * regress fields that come from POM-inheritance, platform/BOM dependencies, deterministic ordering,
 * etc.
 */
class OutputCorrectnessTest {

    @TempDir
    lateinit var projectDir: File

    /**
     * gson's POM does not declare `<developers>` directly — the developer list is inherited from
     * `gson-parent`. If parent-POM resolution is broken, gson's `developers` array would be empty
     * in the output.
     */
    @Test
    fun `parent POM data is inherited (developers, scm, organization)`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
            """.trimIndent()
        )

        val result = run("exportLibraryDefinitions")
        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val content = readOutput()
        val gsonEntry = extractLibraryEntry(content, "com.google.code.gson:gson")
            ?: error("gson entry not found in output: $content")

        // The following all live in gson-parent, not in gson's own POM:
        assertTrue(
            gsonEntry.contains("\"organisationUrl\":\"https://www.google.com\"") ||
                gsonEntry.contains("\"organizationUrl\":\"https://www.google.com\""),
            "gson should inherit developers.organisationUrl from gson-parent. Got: $gsonEntry"
        )
        assertTrue(
            gsonEntry.contains("\"scm\""),
            "gson should inherit scm from gson-parent. Got: $gsonEntry"
        )
        assertTrue(
            gsonEntry.contains("github.com/google/gson"),
            "gson should inherit github URL from gson-parent. Got: $gsonEntry"
        )
    }

    /**
     * `failureaccess` and `guava` are pulled in by `guava` and use *different* parent POM versions
     * (`guava-parent:26.0-android` vs `guava-parent:33.3.1-jre`). Both parent versions need to be
     * resolved and applied — adding them both to a single detached configuration would let Gradle
     * silently dedupe to one version, dropping the other parent's metadata.
     */
    @Test
    fun `parent POMs resolve when multiple versions of the same parent are needed`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.guava:guava:33.3.1-jre")
            """.trimIndent()
        )

        val result = run("exportLibraryDefinitions")
        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val content = readOutput()
        val failureaccess = extractLibraryEntry(content, "com.google.guava:failureaccess")
            ?: error("failureaccess entry missing")

        // Data inherited from guava-parent:26.0-android (NOT 33.3.1-jre)
        assertTrue(
            failureaccess.contains("Apache-2.0"),
            "failureaccess should inherit Apache-2.0 license from its specific parent (26.0-android). " +
                "If only guava-parent:33.3.1-jre were resolved, the license would be missing. Got: $failureaccess"
        )
        assertTrue(
            failureaccess.contains("Kevin Bourrillion"),
            "failureaccess should inherit developer Kevin Bourrillion from guava-parent:26.0-android. " +
                "Got: $failureaccess"
        )
    }

    /**
     * With `includePlatform = true`, BOM dependencies must appear as their own library entries —
     * even though BOMs do not produce JAR artifacts and are easy to filter out accidentally.
     */
    @Test
    fun `platform BOM appears as a library entry when includePlatform is true`() {
        setupProject(
            projectDir,
            deps = """
                implementation(platform("com.fasterxml.jackson:jackson-bom:2.18.2"))
                implementation("com.fasterxml.jackson.core:jackson-databind")
            """.trimIndent(),
            extraConfig = """
                collect {
                    includePlatform = true
                }
            """.trimIndent()
        )

        val result = run("exportLibraryDefinitions")
        assertEquals(TaskOutcome.SUCCESS, result.task(":exportLibraryDefinitions")?.outcome)

        val content = readOutput()
        assertTrue(
            content.contains("\"uniqueId\":\"com.fasterxml.jackson:jackson-bom\""),
            "jackson-bom should appear as a library entry when includePlatform=true"
        )
        // The BOM also has its own POM metadata that must be inherited via parent walking.
        val bomEntry = extractLibraryEntry(content, "com.fasterxml.jackson:jackson-bom")
            ?: error("jackson-bom entry not found")
        assertTrue(
            bomEntry.contains("Jackson BOM") || bomEntry.contains("FasterXML"),
            "jackson-bom entry should contain its name/organization. Got: $bomEntry"
        )
    }

    /**
     * `includePlatform = false` (the default behaviour for many projects) must NOT include BOMs
     * even when they are declared as `platform(...)` dependencies.
     */
    @Test
    fun `platform BOM is excluded when includePlatform is false`() {
        setupProject(
            projectDir,
            deps = """
                implementation(platform("com.fasterxml.jackson:jackson-bom:2.18.2"))
                implementation("com.fasterxml.jackson.core:jackson-databind")
            """.trimIndent(),
            extraConfig = """
                collect {
                    includePlatform = false
                }
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()
        assertFalse(
            content.contains("\"uniqueId\":\"com.fasterxml.jackson:jackson-bom\""),
            "jackson-bom should not appear when includePlatform=false"
        )
        // jackson-databind itself (the actual library) should still be present.
        assertTrue(
            content.contains("\"uniqueId\":\"com.fasterxml.jackson.core:jackson-databind\""),
            "jackson-databind should still be present"
        )
    }

    /**
     * Sub-project (project()) dependencies must be skipped — only external Maven coordinates are
     * recorded in `aboutlibraries.json`.
     */
    @Test
    fun `subproject dependencies are skipped`() {
        File(projectDir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "root-project"
            include(":lib")
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
                implementation(project(":lib"))
                implementation("com.google.code.gson:gson:2.11.0")
            }
            aboutLibraries {
                offlineMode = true
            }
            """.trimIndent()
        )
        File(projectDir, "lib").mkdirs()
        File(projectDir, "lib/build.gradle.kts").writeText(
            """
            plugins { id("java-library") }
            repositories { mavenCentral() }
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()
        assertTrue(content.contains("com.google.code.gson:gson"), "gson should be present")
        assertFalse(content.contains("\"root-project\""), "root project should not appear")
        assertFalse(content.contains("\":lib\""), "subproject should not appear")
    }

    /**
     * Two consecutive runs from a clean state must produce byte-for-byte identical output. This
     * pins the deterministic ordering and protects against the `MapProperty` provider returning
     * different `Map` iteration orders between evaluations.
     */
    @Test
    fun `output is deterministic across runs`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
                implementation("com.google.guava:guava:33.3.1-jre")
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val first = readOutput()

        // Force a complete rebuild
        File(projectDir, "build").deleteRecursively()
        File(projectDir, ".gradle").deleteRecursively()

        run("exportLibraryDefinitions")
        val second = readOutput()

        assertEquals(
            first.length,
            second.length,
            "Output length differs between runs (first=${first.length}, second=${second.length})"
        )
        assertEquals(first, second, "Output bytes differ between runs")
    }

    /**
     * The output JSON must contain the exact dependency versions that were declared. This guards
     * against version-resolution conflicts (where Gradle picks a different version than declared)
     * silently changing the output.
     */
    @Test
    fun `output contains the exact declared versions`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()
        assertTrue(
            content.contains("\"artifactVersion\":\"2.11.0\""),
            "gson 2.11.0 version should be in output"
        )
        assertTrue(
            content.contains("\"artifactVersion\":\"2.0.16\""),
            "slf4j-api 2.0.16 version should be in output"
        )
    }

    /**
     * `exclusionPatterns` should remove matching libraries from the output.
     */
    @Test
    fun `exclusionPatterns removes matching libraries from output`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            """.trimIndent(),
            scriptHeader = """import java.util.regex.Pattern""",
            extraConfig = """
                library {
                    exclusionPatterns.add(Pattern.compile("com\\.google\\.code\\.gson.*"))
                }
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()
        assertFalse(
            content.contains("com.google.code.gson:gson"),
            "gson should be excluded by pattern"
        )
        assertTrue(
            content.contains("org.slf4j:slf4j-api"),
            "slf4j-api should still be present"
        )
    }

    /**
     * `compileOnly` dependencies must be collected — they live in `compileClasspath` only and are
     * easy to miss if a plugin only walks `runtimeClasspath`.
     */
    @Test
    fun `compileOnly dependencies are collected`() {
        setupProject(
            projectDir,
            deps = """
                compileOnly("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()
        assertTrue(content.contains("com.google.code.gson:gson"), "compileOnly gson should be present")
        assertTrue(content.contains("org.slf4j:slf4j-api"), "slf4j should also be present")
    }

    /**
     * `runtimeOnly` dependencies must be collected — they live in `runtimeClasspath` only.
     */
    @Test
    fun `runtimeOnly dependencies are collected`() {
        setupProject(
            projectDir,
            deps = """
                runtimeOnly("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()
        assertTrue(content.contains("com.google.code.gson:gson"), "runtimeOnly gson should be present")
    }

    /**
     * `api` dependencies must be collected — they live in both compile and runtime classpath.
     */
    @Test
    fun `api dependencies are collected`() {
        setupProject(
            projectDir,
            deps = """
                api("com.google.code.gson:gson:2.11.0")
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        assertTrue(readOutput().contains("com.google.code.gson:gson"))
    }

    /**
     * Verifies the JSON output contains every documented top-level field for libraries with full
     * POM data. This is the broad "is the output complete" smoke test.
     */
    @Test
    fun `output contains all expected library fields when POM is complete`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()

        // Top-level container
        assertTrue(content.contains("\"libraries\":["), "libraries array")
        assertTrue(content.contains("\"licenses\":"), "licenses field")

        // Per-library fields
        listOf(
            "\"uniqueId\":",
            "\"artifactVersion\":",
            "\"name\":",
            "\"description\":",
            "\"developers\":",
            "\"licenses\":",
            "\"funding\":",
        ).forEach {
            assertTrue(content.contains(it), "Output should contain field $it")
        }

        // Verify scm and website are present for at least one library (from parent POM inheritance)
        assertTrue(content.contains("\"scm\""), "At least one library should have scm")
        assertTrue(content.contains("\"website\""), "At least one library should have website")
    }

    /**
     * `exclusionPatterns` with regex must work for arbitrary patterns including alternation.
     */
    @Test
    fun `exclusionPatterns supports regex alternation`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            """.trimIndent(),
            scriptHeader = """import java.util.regex.Pattern""",
            extraConfig = """
                library {
                    exclusionPatterns.add(Pattern.compile("(com\\.google.*|org\\.slf4j.*)"))
                }
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()
        assertFalse(content.contains("com.google.code.gson:gson"), "gson should be excluded")
        assertFalse(content.contains("org.slf4j:slf4j-api"), "slf4j should be excluded")
    }

    /**
     * `exclusionPatterns.add(Pattern.compile(...))` and `.addAll(Pattern, ...)` must stay
     * compilable on the user-facing `SetProperty<Pattern>`, and the downstream task must still
     * filter correctly. The task derives a CC-safe `Provider<Set<String>>` over the extension
     * property via `toSerializedRegex`, which preserves supported `Pattern` flags and rejects
     * `Pattern.CANON_EQ`, so `Pattern` instances never reach the configuration cache.
     */
    @Test
    fun `exclusionPatterns accepts java util regex Pattern values`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
                implementation("com.squareup.okio:okio-jvm:3.9.0")
            """.trimIndent(),
            scriptHeader = """import java.util.regex.Pattern""",
            extraConfig = """
                library {
                    exclusionPatterns.add(Pattern.compile("com\\.google\\.code\\.gson.*"))
                    exclusionPatterns.addAll(
                        Pattern.compile("org\\.slf4j.*"),
                        Pattern.compile("com\\.squareup\\.okio.*"),
                    )
                }
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()
        assertFalse(
            content.contains("com.google.code.gson:gson"),
            "gson should be excluded by Pattern.add(...)"
        )
        assertFalse(
            content.contains("org.slf4j:slf4j-api"),
            "slf4j should be excluded by Pattern vararg addAll(...)"
        )
        assertFalse(
            content.contains("com.squareup.okio:okio"),
            "okio should be excluded by Pattern vararg addAll(...)"
        )
    }

    /**
     * The Kotlin DSL lazy-assignment form `exclusionPatterns = setOf(Pattern.compile(...))` —
     * rewritten by Gradle's Kotlin compiler plugin to `exclusionPatterns.set(setOf(...))` —
     * must compile and filter identically to the `.add(...)` form. Pinned here so future Gradle
     * or Kotlin DSL changes can't silently break the pre-14.0.1 assignment syntax.
     */
    @Test
    fun `exclusionPatterns supports Kotlin DSL lazy assignment with Pattern values`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            """.trimIndent(),
            scriptHeader = """import java.util.regex.Pattern""",
            extraConfig = """
                library {
                    exclusionPatterns = setOf(Pattern.compile("com\\.google\\.code\\.gson.*"))
                }
            """.trimIndent()
        )

        run("exportLibraryDefinitions")
        val content = readOutput()
        assertFalse(
            content.contains("com.google.code.gson:gson"),
            "gson should be excluded via `= setOf(Pattern.compile(...))`"
        )
        assertTrue(
            content.contains("org.slf4j:slf4j-api"),
            "slf4j-api should still be present"
        )
    }

    /**
     * Flags set on the original [java.util.regex.Pattern] (e.g. `CASE_INSENSITIVE`, `MULTILINE`,
     * `LITERAL`) must survive the real `--configuration-cache` serialize/deserialize cycle:
     * the task serialises each `Pattern` into an inline flag-prefixed string via
     * `toSerializedRegex` so downstream recompilation with `String.toRegex()` applies the same
     * flags. Without this, a pre-14.0.1 build using `Pattern.compile("...", CASE_INSENSITIVE)`
     * would silently change matching semantics after upgrade. This test runs with
     * `--configuration-cache` explicitly, asserts the CC entry is stored, and then verifies the
     * filtering reflects case-insensitive matching — proving flags survive the actual CC round
     * trip, not just the in-memory `Provider.map { }` chain.
     */
    @Test
    fun `exclusionPatterns preserves Pattern CASE_INSENSITIVE flag under configuration cache`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            """.trimIndent(),
            scriptHeader = """import java.util.regex.Pattern""",
            extraConfig = """
                library {
                    // Uppercase regex against lowercase uniqueId — only matches if
                    // CASE_INSENSITIVE is preserved through the CC store/restore cycle.
                    exclusionPatterns.add(Pattern.compile("COM\\.GOOGLE\\.CODE\\.GSON.*", Pattern.CASE_INSENSITIVE))
                }
            """.trimIndent()
        )

        val result = runWithCc("exportLibraryDefinitions")
        assertTrue(
            result.output.contains("Configuration cache entry stored"),
            "CC entry must be stored on first run. Output: ${result.output}"
        )
        val content = readOutput()
        assertFalse(
            content.contains("com.google.code.gson:gson"),
            "gson should be excluded by a CASE_INSENSITIVE Pattern — flag must survive the CC round trip"
        )
        assertTrue(
            content.contains("org.slf4j:slf4j-api"),
            "slf4j-api should still be present"
        )
    }

    /**
     * `Pattern.LITERAL` disables regex metacharacters, matching the pattern string literally.
     * After serialisation the task wraps the body in `Pattern.quote(...)`, so the Kotlin
     * `Regex` built downstream must treat the regex specials as literal characters. This test
     * runs with `--configuration-cache` explicitly so the assertion covers the real CC
     * serialize/deserialize cycle, not just the in-memory `Provider.map { }` chain. A uniqueId
     * containing no regex metacharacters like `com.google.code.gson:gson` does not exercise
     * LITERAL, so we use a pattern with a literal `.*` suffix that would otherwise match any
     * characters.
     */
    @Test
    fun `exclusionPatterns preserves Pattern LITERAL flag under configuration cache`() {
        setupProject(
            projectDir,
            deps = """
                implementation("com.google.code.gson:gson:2.11.0")
                implementation("org.slf4j:slf4j-api:2.0.16")
            """.trimIndent(),
            scriptHeader = """import java.util.regex.Pattern""",
            extraConfig = """
                library {
                    // Literal ".*" suffix — without LITERAL this would match everything; with
                    // LITERAL it must match nothing (no uniqueId ends in the literal string ".*").
                    exclusionPatterns.add(Pattern.compile("com.google.code.gson.*", Pattern.LITERAL))
                }
            """.trimIndent()
        )

        val result = runWithCc("exportLibraryDefinitions")
        assertTrue(
            result.output.contains("Configuration cache entry stored"),
            "CC entry must be stored on first run. Output: ${result.output}"
        )
        val content = readOutput()
        assertTrue(
            content.contains("com.google.code.gson:gson"),
            "gson must NOT be excluded: LITERAL flag must survive the CC round trip and prevent the pattern from matching anything"
        )
        assertTrue(
            content.contains("org.slf4j:slf4j-api"),
            "slf4j-api should still be present"
        )
    }

    // ----- helpers -----

    private fun run(vararg args: String) =
        @Suppress("WithPluginClasspathUsage")
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(*args, "--stacktrace")
            .withPluginClasspath()
            .build()

    private fun runWithCc(vararg args: String) =
        @Suppress("WithPluginClasspathUsage")
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(*args, "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

    private fun readOutput(): String =
        File(projectDir, "build/generated/aboutLibraries/aboutlibraries.json").readText()

    /**
     * Returns the JSON object substring for the library with the given uniqueId, or `null` if it
     * is not present. The plugin emits compact JSON so we walk braces manually.
     */
    private fun extractLibraryEntry(json: String, uniqueId: String): String? {
        val key = "\"uniqueId\":\"$uniqueId\""
        val keyIdx = json.indexOf(key)
        if (keyIdx < 0) return null
        // Walk backwards to find the opening { of this entry
        var start = keyIdx
        while (start > 0 && json[start] != '{') start--
        // Walk forwards counting braces to find the matching closing }
        var depth = 0
        var end = start
        while (end < json.length) {
            when (json[end]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) return json.substring(start, end + 1)
                }
            }
            end++
        }
        return null
    }

    private fun setupProject(
        projectDir: File,
        deps: String,
        extraConfig: String = "",
        scriptHeader: String = "",
    ) {
        File(projectDir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "test-project"
            """.trimIndent()
        )
        File(projectDir, "build.gradle.kts").writeText(
            """
            $scriptHeader
            plugins {
                id("java-library")
                id("com.mikepenz.aboutlibraries.plugin")
            }
            repositories { mavenCentral() }
            dependencies {
                $deps
            }
            aboutLibraries {
                offlineMode = true
                $extraConfig
            }
            """.trimIndent()
        )
    }
}
