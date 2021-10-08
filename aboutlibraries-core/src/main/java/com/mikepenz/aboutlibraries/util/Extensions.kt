package com.mikepenz.aboutlibraries.util

import org.json.JSONArray

internal fun JSONArray.toStringArray(): Array<String> {
    return (0 until length()).map { getString(it) }.toTypedArray()
}