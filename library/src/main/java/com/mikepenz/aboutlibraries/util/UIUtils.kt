package com.mikepenz.aboutlibraries.util

import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

internal fun Context.getThemeColor(attr: Int): Int {
    val tv = TypedValue()
    return if (this.theme.resolveAttribute(attr, tv, true)) {
        if (tv.resourceId != 0) {
            ContextCompat.getColor(this, tv.resourceId)
        } else {
            tv.data
        }
    } else {
        0
    }
}

/**
 * helper method to get the color by attr (which is defined in the style) or by resource.
 *
 * @param ctx
 * @param attr
 * @param res
 * @return
 */
internal fun Context.getThemeColorFromAttrOrRes(attr: Int, res: Int): Int {
    var color = getThemeColor(attr)
    if (color == 0) {
        color = ContextCompat.getColor(this, res)
    }
    return color
}

/**
 * helper method to set the background depending on the android version
 *
 * @param v
 * @param drawableRes
 */
internal fun View.setBackground(drawableRes: Int) {
    ViewCompat.setBackground(this, ContextCompat.getDrawable(this.context, drawableRes))
}

/**
 * Returns the size in pixels of an attribute dimension
 *
 * @param context the context to get the resources from
 * @param attr    is the attribute dimension we want to know the size from
 * @return the size in pixels of an attribute dimension
 */
internal fun Context.getThemeAttributeDimensionSize(attr: Int): Int {
    var a: TypedArray? = null
    try {
        a = theme.obtainStyledAttributes(intArrayOf(attr))
        return a!!.getDimensionPixelSize(0, 0)
    } finally {
        a?.recycle()
    }
}

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
internal fun Context.convertDpToPixel(dp: Float): Float {
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi / 160f)
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px      A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
internal fun Context.convertPixelsToDp(px: Float): Float {
    val metrics = resources.displayMetrics
    return px / (metrics.densityDpi / 160f)
}