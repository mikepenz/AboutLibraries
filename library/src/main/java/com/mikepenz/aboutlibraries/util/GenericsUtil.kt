package com.mikepenz.aboutlibraries.util

import android.content.Context
import android.text.TextUtils
import com.mikepenz.aboutlibraries.Libs
import java.lang.reflect.Field

/**
 * A helper method to get a String[] out of a fieldArray
 *
 * @param fields R.strings.class.getFields()
 * @return a String[] with the string ids we need
 */
internal fun Array<Field>.toStringArray(): Array<String> {
    val fieldArray = ArrayList<String>()
    for (field in this) {
        if (field.name.contains(Libs.DEFINE_EXT)) {
            fieldArray.add(field.name)
        }
    }
    return fieldArray.toTypedArray()
}

/**
 * a helper to get the string fields from the R class
 *
 * @param ctx
 * @return
 */
internal fun Context.getFields(): Array<String> {
    val rStringClass = resolveRClass(this.packageName)
    return rStringClass?.fields?.toStringArray() ?: emptyArray()
}

/**
 * a helper class to resolve the correct R Class for the package
 *
 * @param packageName
 * @return
 */
internal fun resolveRClass(packageName: String): Class<*>? {
    var packageName = packageName
    do {
        try {
            return Class.forName("$packageName.R\$string")
        } catch (e: ClassNotFoundException) {
            packageName = if (packageName.contains(".")) packageName.substring(0, packageName.lastIndexOf('.')) else ""
        }

    } while (!TextUtils.isEmpty(packageName))

    return null
}