package com.mikepenz.aboutlibraries.plugin.model

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import groovy.json.JsonGenerator
import groovy.json.JsonOutput
import java.io.File
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

data class ResultContainer(
    val libraries: List<Library>,
    val licenses: Map<String, License>,
) {
    val metadata: MetaData = MetaData()
}

class MetaData(
    val generated: String = DateTimeFormatter.ISO_DATE_TIME
        .withZone(ZoneOffset.UTC)
        .format(Calendar.getInstance().toInstant()),
)

fun ResultContainer.writeToDisk(outputFile: File, excludeFields: Array<String>, prettyPrint: Boolean) {
    val fieldNames = mutableListOf("artifactId", "groupId", "artifactFolder").also {
        it.addAll(excludeFields)
    }
    val jsonGenerator = JsonGenerator.Options().excludeNulls().excludeFieldsByName(fieldNames).build()
    PrintWriter(OutputStreamWriter(outputFile.outputStream(), StandardCharsets.UTF_8), true).use {
        it.write(jsonGenerator.toJson(this).let { json -> if (prettyPrint) JsonOutput.prettyPrint(json) else json })
    }
}