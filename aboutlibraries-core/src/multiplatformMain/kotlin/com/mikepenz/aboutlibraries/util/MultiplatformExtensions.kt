package com.mikepenz.aboutlibraries.util

import kotlinx.serialization.json.*

internal fun JsonObject.getJSONObject(key: String): JsonObject {
    return getValue(key).jsonObject
}

internal fun JsonObject.optJSONObject(key: String): JsonObject? {
    return get(key)?.jsonObject
}

internal fun JsonObject.getJSONArray(key: String): JsonArray {
    return getValue(key).jsonArray
}

internal fun JsonObject.optJSONArray(key: String): JsonArray? {
    return get(key)?.jsonArray
}

internal fun JsonObject.getString(key: String): String {
    return getValue(key).jsonPrimitive.content
}

internal fun JsonObject.optString(key: String): String? {
    return get(key)?.jsonPrimitive?.contentOrNull
}

internal fun <T> JsonArray?.forEachObject(block: JsonObject.() -> T?): List<T> {
    this ?: return emptyList()
    val targetList = mutableListOf<T>()
    for (il in 0 until size) {
        val obj = block.invoke(get(il).jsonObject)
        if (obj != null) {
            targetList.add(obj)
        }
    }
    return targetList
}

internal fun <T> JsonArray?.forEachString(block: String.() -> T): List<T> {
    this ?: return emptyList()
    val targetList = mutableListOf<T>()
    for (il in 0 until size) {
        targetList.add(block.invoke(get(il).jsonPrimitive.content))
    }
    return targetList
}

internal fun <T> JsonObject?.forEachObject(block: JsonObject.(key: String) -> T): List<T> {
    this ?: return emptyList()
    val targetList = mutableListOf<T>()
    keys.forEach {
        targetList.add(block.invoke(get(it)!!.jsonObject, it))
    }
    return targetList
}