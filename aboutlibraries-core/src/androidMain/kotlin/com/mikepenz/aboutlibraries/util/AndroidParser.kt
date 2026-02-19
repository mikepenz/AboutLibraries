package com.mikepenz.aboutlibraries.util

import android.util.Log
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.entity.Organization
import com.mikepenz.aboutlibraries.entity.Scm
import org.json.JSONObject

actual fun parseData(json: String): Result {
    try {
        val metaData = JSONObject(json)

        val licenses = metaData.getJSONObject("licenses").forEachObject { key ->
            License(
                getString("name"),
                optStringOrNull("url"),
                optStringOrNull("year"),
                optStringOrNull("spdxId"),
                optStringOrNull("content"),
                key
            )
        }
        val mappedLicenses = licenses.associateBy { it.hash }
        val libraries = metaData.getJSONArray("libraries").forEachObject {
            val libLicenses =
                optJSONArray("licenses").forEachString { mappedLicenses[this] }.mapNotNull { it }
                    .toHashSet()
            val developers = optJSONArray("developers")?.forEachObject {
                Developer(optStringOrNull("name"), optStringOrNull("organisationUrl"))
            } ?: emptyList()
            val organization = optJSONObject("organization")?.let {
                Organization(it.optString("name") ?: "", it.optStringOrNull("url"))
            }
            val scm = optJSONObject("scm")?.let {
                Scm(
                    it.optStringOrNull("connection"),
                    it.optStringOrNull("developerConnection"),
                    it.optStringOrNull("url")
                )
            }
            val funding = optJSONArray("funding").forEachObject {
                Funding(getString("platform"), getString("url"))
            }.toSet()
            val id = getString("uniqueId")
            Library(
                id,
                optStringOrNull("artifactVersion"),
                optString("name", id),
                optStringOrNull("description"),
                optStringOrNull("website"),
                developers,
                organization,
                scm,
                libLicenses,
                funding,
                optStringOrNull("tag")
            )
        }
        return Result(libraries, licenses)
    } catch (t: Throwable) {
        Log.e("AboutLibraries", "Failed to parse the meta data *.json file: $t")
    }
    return Result(emptyList(), emptyList())
}

private fun JSONObject.optStringOrNull(name: String): String? {
    val value = opt(name)
    return if (value == null || value == JSONObject.NULL) null else value.toString()
}
