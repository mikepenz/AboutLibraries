package com.mikepenz.aboutlibraries.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.text.TextUtils


internal fun Context.getPackageInfo(): PackageInfo? {
    //get the packageManager to load and read some values :D
    val pm = this.packageManager
    //get the packageName
    val packageName = this.packageName
    //Try to load the applicationInfo
    var packageInfo: PackageInfo? = null
    try {
        packageInfo = pm.getPackageInfo(packageName, 0)
    } catch (ex: Exception) {
    }

    return packageInfo
}

internal fun Context.getApplicationInfo(): ApplicationInfo? {
    //get the packageManager to load and read some values :D
    val pm = this.packageManager
    //get the packageName
    val packageName = this.packageName
    //Try to load the applicationInfo
    var appInfo: ApplicationInfo? = null
    try {
        appInfo = pm.getApplicationInfo(packageName, 0)
    } catch (ex: Exception) {
    }

    return appInfo
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
        if (!TextUtils.isEmpty(descriptionShowVersion)) {
            try {
                result = java.lang.Boolean.parseBoolean(descriptionShowVersion)
            } catch (ex: Exception) {
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
internal fun Context.extractStringBundleOrResource(value: String?, resName: String): String? {
    var result: String? = null
    if (value != null) {
        result = value
    } else {
        val descriptionShowVersion = getStringResourceByName(resName)
        if (!TextUtils.isEmpty(descriptionShowVersion)) {
            result = descriptionShowVersion
        }
    }
    return result
}