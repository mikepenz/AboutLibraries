package com.mikepenz.aboutlibraries.util

import android.content.Context
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun Context.getStringResourceByName(aString: String): String {
    val resId = resources.getIdentifier(aString, "string", packageName)
    return if (resId == 0) {
        ""
    } else {
        getString(resId)
    }
}

/**
 * Helper to extract a boolean from a bundle or resource
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun Context.extractBooleanBundleOrResource(value: Boolean?, resName: String): Boolean? {
    var result: Boolean? = null
    if (value != null) {
        result = value
    } else {
        val descriptionShowVersion = getStringResourceByName(resName)
        if (descriptionShowVersion.isNotEmpty()) {
            try {
                result = java.lang.Boolean.parseBoolean(descriptionShowVersion)
            } catch (ignored: Exception) {
                // ignored
            }
        }
    }
    return result
}

/**
 * Helper to extract a string from a bundle or resource
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun Context.extractStringBundleOrResource(value: String?, resName: String): String? =
    value ?: getStringResourceByName(resName).takeIf { it.isNotEmpty() }