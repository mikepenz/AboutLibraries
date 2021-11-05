package com.mikepenz.aboutlibraries.util

import android.content.Context
import com.mikepenz.aboutlibraries.Libs
import org.json.JSONArray
import org.json.JSONObject


fun Libs.Builder.withContext(ctx: Context): Libs.Builder {
    try {
        _stringData = ctx.resources.openRawResource(ctx.getRawResourceId("aboutlibraries")).bufferedReader().use { it.readText() }
    } catch (t: Throwable) {
        println("Could not retrieve libraries")
    }
    return this
}

internal fun Context.getRawResourceId(aString: String): Int {
    return resources.getIdentifier(aString, "raw", packageName)
}

internal fun <T> JSONArray?.forEachObject(block: JSONObject.() -> T): List<T> {
    this ?: return emptyList()
    val targetList = mutableListOf<T>()
    for (il in 0 until length()) {
        targetList.add(block.invoke(getJSONObject(il)))
    }
    return targetList
}

internal fun <T> JSONArray?.forEachString(block: String.() -> T): List<T> {
    this ?: return emptyList()
    val targetList = mutableListOf<T>()
    for (il in 0 until length()) {
        targetList.add(block.invoke(getString(il)))
    }
    return targetList
}

internal fun <T> JSONObject?.forEachObject(block: JSONObject.(key: String) -> T): List<T> {
    this ?: return emptyList()
    val targetList = mutableListOf<T>()
    keys().forEach {
        targetList.add(block.invoke(getJSONObject(it), it))
    }
    return targetList
}