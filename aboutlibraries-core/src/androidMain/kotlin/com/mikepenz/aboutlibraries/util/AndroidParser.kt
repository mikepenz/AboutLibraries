package com.mikepenz.aboutlibraries.util

import android.util.Log
import com.mikepenz.aboutlibraries.entity.*
import org.json.JSONObject

actual fun parseData(json: String): Result {
    try {
        val metaData = JSONObject(json)

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

            Library(
                getString("uniqueId"),
                optString("artifactVersion"),
                getString("name"),
                optString("description"),
                optString("website"),
                developers,
                organization,
                scm,
                libLicenses
            )
        }
        return Result(libraries, licenses)
    } catch (t: Throwable) {
        Log.e("aboutlibraries", "Failed to parse aboutlibraries.json: $t")
    }
    return Result(emptyList(), emptyList())
}

