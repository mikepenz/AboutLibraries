package com.mikepenz.aboutlibraries.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo


fun Context.getPackageInfo(): PackageInfo? {
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

fun Context.getApplicationInfo(): ApplicationInfo? {
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