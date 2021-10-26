package com.mikepenz.aboutlibraries.plugin.mapping

import groovy.json.JsonGenerator
import java.io.File
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

/**
 * Library class describing a library and its information
 */
data class Library(
    val uniqueId: String,
    val artifactVersion: String?,
    val name: String?,
    val description: String?,
    val website: String?,
    val developer: List<Developer>,
    val organization: Organization?,
    val scm: Scm?,
    val licenses: Set<License> = emptySet(),
    val artifactFolder: File? = null
) {
    val artifactId: String
        get() = "${uniqueId}:${artifactVersion ?: ""}"

    val openSource: Boolean
        get() = scm?.url?.isNotBlank() == true
}

fun List<Library>.writeToDisk(outputFile: File) {
    val jsonGenerator = JsonGenerator.Options().excludeNulls().excludeFieldsByName(
        "artifactId", "artifactFolder", "remoteLicense"
    ).build()
    val printWriter = PrintWriter(OutputStreamWriter(outputFile.outputStream(), StandardCharsets.UTF_8), true)
    printWriter.write(jsonGenerator.toJson(this))
    printWriter.close()
}