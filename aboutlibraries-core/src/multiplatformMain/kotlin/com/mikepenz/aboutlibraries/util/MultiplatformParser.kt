package com.mikepenz.aboutlibraries.util

import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.entity.Organization
import com.mikepenz.aboutlibraries.entity.Scm
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
            val libLicenses =
                optJSONArray("licenses").forEachString { mappedLicenses[this] }.mapNotNull { it }
                    .toHashSet()
            val developers = optJSONArray("developers")?.forEachObject {
                Developer(optString("name"), optString("organisationUrl"))
            } ?: emptyList()
            val organization = optJSONObject("organization")?.let {
                Organization(it.optString("name") ?: "", it.optString("url"))
            }
            val scm = optJSONObject("scm")?.let {
                Scm(
                    it.optString("connection"),
                    it.optString("developerConnection"),
                    it.optString("url")
                )
            }
            val funding = optJSONArray("funding").forEachObject {
                Funding(getString("platform"), getString("url"))
            }.toSet()

            val id = getString("uniqueId")
            Library(
                id,
                optString("artifactVersion"),
                optString("name") ?: id,
                optString("description"),
                optString("website"),
                developers,
                organization,
                scm,
                libLicenses,
                funding,
                optString("tag")
            )
        }
        return Result(libraries, licenses)
    } catch (t: Throwable) {
        println("Failed to parse the meta data *.json file: $t")
    }
    return Result(emptyList(), emptyList())
}
