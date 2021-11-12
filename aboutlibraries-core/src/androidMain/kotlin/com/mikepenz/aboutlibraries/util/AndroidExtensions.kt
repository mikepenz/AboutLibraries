package com.mikepenz.aboutlibraries.util

import android.content.Context
import com.mikepenz.aboutlibraries.Libs
import org.json.JSONArray
import org.json.JSONObject

/**
 * Attach the generated library definition data as [ByteArray]
 *
 * @param byteArray containing the information
 */
fun Libs.Builder.withJson(byteArray: ByteArray): Libs.Builder {
    return withJson(byteArray.toString(kotlin.text.Charsets.UTF_8))
}

/**
 * Auto discover the generated library definition data by the default name and location
 * `res/raw/aboutlibraries.json`
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