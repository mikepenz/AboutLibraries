package com.mikepenz.aboutlibraries

import org.junit.Test
import kotlin.test.assertEquals

/**
 * The `targets` field is only written by the Gradle plugin when `collect.includeTargets` is
 * enabled, so the parser must handle both its presence and its absence within the same document.
 */
class TargetsParsingTest {

    @Test
    fun testTargetsParsing() {
        val result = Libs.Builder().withJson(json).build()

        assertEquals(2, result.libraries.size)

        val withTargets = result.libraries.first { it.uniqueId == "com.example:with-targets" }
        assertEquals(setOf("android", "iosArm64", "jvm"), withTargets.targets)

        val withoutTargets = result.libraries.first { it.uniqueId == "com.example:without-targets" }
        assertEquals(emptySet(), withoutTargets.targets)
    }

    /** The point of the field: narrowing the rendered list to what the running target links against. */
    @Test
    fun testFilteringByTarget() {
        val result = Libs.Builder().withJson(json).build()

        assertEquals(
            listOf("com.example:with-targets"),
            result.libraries.filter { "iosArm64" in it.targets }.map { it.uniqueId }
        )
    }

    private val json = """
        {
          "libraries": [
            {
              "uniqueId": "com.example:with-targets",
              "artifactVersion": "1.0.0",
              "name": "With Targets",
              "developers": [],
              "licenses": [],
              "funding": [],
              "targets": ["android", "iosArm64", "jvm"]
            },
            {
              "uniqueId": "com.example:without-targets",
              "artifactVersion": "1.0.0",
              "name": "Without Targets",
              "developers": [],
              "licenses": [],
              "funding": []
            }
          ],
          "licenses": {}
        }
    """.trimIndent()
}
