package com.mikepenz.aboutlibraries.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo


internal fun Context.getPackageInfo(): PackageInfo? = try {
    packageManager.getPackageInfo(packageName, 0)
} catch (ex: Exception) {
    null
}

internal fun Context.getApplicationInfo(): ApplicationInfo? = try {
    packageManager.getApplicationInfo(packageName, 0)
} catch (ex: Exception) {
    null
}


internal fun Context.getRawResourceId(aString: String): Int {
    return resources.getIdentifier(aString, "raw", packageName)
}

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
 *
 * @param libs
 * @param value
 * @param resName
 * @return
 */
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
 *
 * @param libs
 * @param value
 * @param resName
 * @return
 */
internal fun Context.extractStringBundleOrResource(value: String?, resName: String): String? =
        value ?: getStringResourceByName(resName).takeIf { it.isNotEmpty() }