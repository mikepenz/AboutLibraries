package com.mikepenz.aboutlibraries.util

import android.content.Context
import androidx.annotation.RestrictTo
import com.mikepenz.aboutlibraries.Libs
import java.lang.reflect.Field

/**
 * A helper method to get a String[] out of a fieldArray
 *
 * @param fields R.strings.class.getFields()
 * @return a String[] with the string ids we need
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
fun Array<Field>.toStringArray(): Array<String> =
        map { it.name }.filter { it.contains(Libs.DEFINE_EXT) }.toTypedArray()

/**
 * a helper to get the string fields from the R class
 *
 * @param ctx
 * @return
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
fun Context.getFields(): Array<String> =
        resolveRClass(packageName)?.fields?.toStringArray() ?: emptyArray()

/**
 * a helper class to resolve the correct R Class for the package
 *
 * @param packageName
 * @return
 */
internal fun resolveRClass(pn: String): Class<*>? {
    var packageName = pn
    do {
        try {
            return Class.forName("$packageName.R\$string")
        } catch (e: ClassNotFoundException) {
            packageName = if (packageName.contains(".")) packageName.substring(0, packageName.lastIndexOf('.')) else ""
        }
    } while (packageName.isNotEmpty())

    return null
}