package com.mikepenz.aboutlibraries.plugin.model

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.util.PartialObjectConverter
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
    val qualifiedFieldNames = mutableSetOf(
        "${Library::class.qualifiedName}.${Library::artifactId.name}",
        "${Library::class.qualifiedName}.${Library::groupId.name}",
        "${Library::class.qualifiedName}.${Library::artifactFolder.name}"
    )
    val unqualifiedFieldNames = mutableSetOf<String>()
    excludeFields.forEach { excludedField ->
        if (excludedField.startsWith("com.mikepenz.aboutlibraries.plugin")) {
            qualifiedFieldNames.add(excludedField)
        } else {
            unqualifiedFieldNames.add(excludedField)
        }
    }
    val jsonGenerator = JsonGenerator.Options()
        .excludeNulls()
        .excludeFieldsByName(unqualifiedFieldNames)
        .addConverter(PartialObjectConverter(qualifiedFieldNames))
        .build()
    PrintWriter(OutputStreamWriter(outputFile.outputStream(), StandardCharsets.UTF_8), true).use {
        it.write(jsonGenerator.toJson(this).let { json -> if (prettyPrint) JsonOutput.prettyPrint(json) else json })
    }
}