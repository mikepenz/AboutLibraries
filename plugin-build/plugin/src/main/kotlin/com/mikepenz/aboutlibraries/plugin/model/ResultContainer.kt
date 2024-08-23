package com.mikepenz.aboutlibraries.plugin.model

import com.mikepenz.aboutlibraries.plugin.mapping.*
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
    val allowedExclusionQualifiers = setOf(
        ResultContainer::class.simpleName,
        Library::class.simpleName,
        Developer::class.simpleName,
        Organization::class.simpleName,
        Funding::class.simpleName,
        Scm::class.simpleName,
        License::class.simpleName,
        MetaData::class.simpleName,
    )
    val excludedQualifiedFieldNames = mutableSetOf(
        "${Library::class.simpleName}.${Library::artifactId.name}",
        "${Library::class.simpleName}.${Library::groupId.name}",
        "${Library::class.simpleName}.${Library::artifactFolder.name}"
    )
    val excludedUnqualifiedFieldNames = mutableSetOf<String>()
    excludeFields.forEach { excludedField ->
        val segments = excludedField.split(".")
        if (segments.size == 2 && allowedExclusionQualifiers.contains(segments.first())) {
            excludedQualifiedFieldNames.add(excludedField)
        } else {
            excludedUnqualifiedFieldNames.add(excludedField)
        }
    }
    val jsonGenerator = JsonGenerator.Options()
        .excludeNulls()
        .excludeFieldsByName(excludedUnqualifiedFieldNames)
        .addConverter(PartialObjectConverter(excludedQualifiedFieldNames))
        .build()
    PrintWriter(OutputStreamWriter(outputFile.outputStream(), StandardCharsets.UTF_8), true).use {
        it.write(jsonGenerator.toJson(this).let { json -> if (prettyPrint) JsonOutput.prettyPrint(json) else json })
    }
}