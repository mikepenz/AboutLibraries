package com.mikepenz.aboutlibraries.util

import com.mikepenz.aboutlibraries.entity.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

actual fun parseData(json: String): Result {
    try {
        val metaData = Json.parseToJsonElement(json).jsonObject

        val licenses = metaData.getJSONObject("licenses").forEachObject { key ->
            License(
                getString("name"),
                optString("url"),
                optString("year"),
                optString("spdxId"),
                optString("content"),
                key
            )
        }
        val mappedLicenses = licenses.associateBy { it.hash }
        val libraries = metaData.getJSONArray("libraries").forEachObject {
            val libLicenses = optJSONArray("licenses").forEachString { mappedLicenses[this] }.mapNotNull { it }.toHashSet()
            val developers = optJSONArray("developers").forEachObject {
                Developer(optString("name"), optString("organisationUrl"))
            }
            val organization = optJSONObject("organization")?.let {
                Organization(it.getString("name"), it.optString("url"))
            }
            val scm = optJSONObject("scm")?.let {
                Scm(it.optString("connection"), it.optString("developerConnection"), it.optString("url"))
            }
            val funding = optJSONArray("funding").forEachObject {
                Funding(getString("platform"), getString("url"))
            }.toSet()

            Library(
                getString("uniqueId"),
                optString("artifactVersion"),
                getString("name"),
                optString("description"),
                optString("website"),
                developers,
                organization,
                scm,
                libLicenses,
                funding
            )
        }
        return Result(libraries, licenses)
    } catch (t: Throwable) {
        println("Failed to parse aboutlibraries.json: $t")
    }
    return Result(emptyList(), emptyList())
}

