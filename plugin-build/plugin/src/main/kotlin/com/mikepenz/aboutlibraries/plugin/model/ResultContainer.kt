package com.mikepenz.aboutlibraries.plugin.model

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import groovy.json.JsonGenerator
import java.io.File
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

data class ResultContainer(
    val libraries: List<Library>,
    val licenses: Map<String, License>
)

fun ResultContainer.writeToDisk(outputFile: File) {
    val jsonGenerator = JsonGenerator.Options().excludeNulls().excludeFieldsByName(
        "artifactId", "artifactFolder"
    ).build()
    PrintWriter(OutputStreamWriter(outputFile.outputStream(), StandardCharsets.UTF_8), true).use {
        it.write(jsonGenerator.toJson(this))
    }
}