package com.mikepenz.aboutlibraries

import org.junit.Test
import kotlin.test.assertEquals

/**
 * The `variants` field is only written by the Gradle plugin when `collect.includeVariants` is
 * enabled, so the parser must handle both its presence and its absence within the same document.
 */
class VariantsParsingTest {

    @Test
    fun testVariantsParsing() {
        val result = Libs.Builder().withJson(json).build()

        assertEquals(2, result.libraries.size)

        val withVariants = result.libraries.first { it.uniqueId == "com.example:with-variants" }
        assertEquals(
            setOf("androidCompileClasspath", "androidRuntimeClasspath"),
            withVariants.variants
        )

        val withoutVariants = result.libraries.first { it.uniqueId == "com.example:without-variants" }
        assertEquals(emptySet(), withoutVariants.variants)
    }

    private val json = """
        {
          "libraries": [
            {
              "uniqueId": "com.example:with-variants",
              "artifactVersion": "1.0.0",
              "name": "With Variants",
              "developers": [],
              "licenses": [],
              "funding": [],
              "variants": ["androidCompileClasspath", "androidRuntimeClasspath"]
            },
            {
              "uniqueId": "com.example:without-variants",
              "artifactVersion": "1.0.0",
              "name": "Without Variants",
              "developers": [],
              "licenses": [],
              "funding": []
            }
          ],
          "licenses": {}
        }
    """.trimIndent()
}
