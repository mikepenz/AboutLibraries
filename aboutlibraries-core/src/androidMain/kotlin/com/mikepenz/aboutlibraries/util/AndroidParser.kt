package com.mikepenz.aboutlibraries.util

import android.util.Log
import com.mikepenz.aboutlibraries.entity.*
import org.json.JSONObject

actual fun parseData(json: String, recoverable: Boolean): Result {
    try {
        val metaData = JSONObject(json)
        val firstItem: JSONObject = metaData.getJSONArray("libraries")[0] as JSONObject
        firstItem.remove("name")
        firstItem.remove("developers")

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
            val developers = optJSONArray("developers")?.forEachObject {
                Developer(optString("name"), optString("organisationUrl"))
            } ?: emptyList()
            val organization = optJSONObject("organization")?.let {
                Organization(it.getString("name"), it.optString("url"))
            }
            val scm = optJSONObject("scm")?.let {
                Scm(it.optString("connection"), it.optString("developerConnection"), it.optString("url"))
            }
            val funding = optJSONArray("funding").forEachObject {
                Funding(getString("platform"), getString("url"))
            }.toSet()
            val id = getString("uniqueId")
            Library(
                id,
                optString("artifactVersion"),
                if (recoverable) optString("name", id) else getString("name"),
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
        Log.e("AboutLibraries", "Failed to parse the meta data *.json file: $t")
    }
    return Result(emptyList(), emptyList())
}
