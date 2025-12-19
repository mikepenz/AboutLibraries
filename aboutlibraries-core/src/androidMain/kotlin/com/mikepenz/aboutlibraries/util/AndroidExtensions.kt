package com.mikepenz.aboutlibraries.util

import android.content.Context
import android.util.Log
import com.mikepenz.aboutlibraries.Libs
import org.json.JSONArray
import org.json.JSONObject

/**
 * Attach the generated library definition data as [ByteArray]
 *
 * @param byteArray containing the information
 */
fun Libs.Builder.withJson(byteArray: ByteArray): Libs.Builder {
    return withJson(byteArray.toString(Charsets.UTF_8))
}

/**
 * Auto-discover the generated library definition data by the default name and location
 * `res/raw/aboutlibraries.json`
 *
 * Please remember to disable resource shrinking when using this API.
 * https://developer.android.com/topic/performance/app-optimization/customize-which-resources-to-keep
 *
 * ```
 * <?xml version="1.0" encoding="utf-8"?>
 * <resources xmlns:tools="http://schemas.android.com/tools"
 *     tools:keep="@raw/aboutlibraries" />
 * ```
 *
 * @param ctx context used to retrieve the resource
 */
fun Libs.Builder.withContext(ctx: Context): Libs.Builder {
    return withJson(ctx, ctx.getRawResourceId("aboutlibraries"))
}

/**
 * Attach the generated library definition data as resource file, with the given id.
 *
 * @param ctx context used to retrieve the resource
 * @param rawResId used to retrieve the file
 */
fun Libs.Builder.withJson(ctx: Context, rawResId: Int): Libs.Builder {
    try {
        withJson(ctx.resources.openRawResource(rawResId).bufferedReader().use { it.readText() })
    } catch (t: Throwable) {
        Log.e(
            "AboutLibraries", """
            Unable to retrieve library information given the `raw` resource identifier. 
            Please make sure either the gradle plugin is properly set up, or the file is manually provided. 
        """.trimIndent()
        )
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
        val obj = block.invoke(getJSONObject(il))
        if (obj != null) {
            targetList.add(obj)
        }
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
