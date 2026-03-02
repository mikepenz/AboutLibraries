@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.os.Build
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import com.mikepenz.aboutlibraries.R

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
 * Applies the light statusBar flag onto the activity
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun Activity?.applyLightSystemUi(view: View? = null, additionalFlags: Int = 0) {
    this ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = view?.systemUiVisibility ?: window.decorView.systemUiVisibility
        flags = flags or additionalFlags
        view?.systemUiVisibility = flags
        if (view == null) {
            window.decorView.systemUiVisibility = flags
        }

        val lightCtx = ContextThemeWrapper(this, com.google.android.material.R.style.Theme_MaterialComponents_Light)
        this.window.statusBarColor = lightCtx.getThemeColor(com.google.android.material.R.attr.colorSurface)
        this.window.navigationBarColor = lightCtx.getThemeColor(android.R.attr.colorBackground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.window.navigationBarDividerColor = lightCtx.getThemeColor(android.R.attr.colorControlHighlight)
        }
    }
}

/**
 * Clears the light statusBar flag
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun Activity?.applyDarkSystemUi(view: View? = null, additionalFlags: Int = 0) {
    this ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = view?.systemUiVisibility ?: window.decorView.systemUiVisibility
        flags = flags or additionalFlags
        view?.systemUiVisibility = flags
        if (view == null) {
            window.decorView.systemUiVisibility = flags
        }

        val darkCtx = ContextThemeWrapper(this, com.google.android.material.R.style.Theme_MaterialComponents)
        this.window.statusBarColor = darkCtx.getThemeColor(com.google.android.material.R.attr.colorSurface)
        this.window.navigationBarColor = darkCtx.getThemeColor(android.R.attr.colorBackground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.window.navigationBarDividerColor = darkCtx.getThemeColor(android.R.attr.colorControlHighlight)
        }
    }
}

/**
 * Applies the light statusBar flag onto the activity
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun Activity?.applyLightEdgeSystemUi(view: View? = null) {
    this ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        applyLightSystemUi(view, (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION))
        this.window.statusBarColor = getSupportColor(R.color.immersive_bars)
        this.window.navigationBarColor = getSupportColor(R.color.nav_bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.window.navigationBarDividerColor = getSupportColor(R.color.nav_bar)
        }
    }
}

/**
 * Clears the light statusBar flag
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun Activity?.applyDarkEdgeSystemUi(view: View? = null) {
    this ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        applyDarkSystemUi(view, (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION))

        this.window.statusBarColor = getSupportColor(R.color.dark_immersive_bars)

        this.window.navigationBarColor = getSupportColor(R.color.dark_nav_bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.window.navigationBarDividerColor = getSupportColor(R.color.dark_nav_bar)
        }
    }
}

/**
 * Applies the edge system UI based on the current phone setting
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun Activity?.applyEdgeSystemUi(view: View? = null) {
    this ?: return

    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> applyDarkEdgeSystemUi(view)
        else -> applyLightEdgeSystemUi(view)
    }
}

/**
 * a helper method to get the colors from the context
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun Context.getSupportColor(@ColorRes res: Int): Int {
    return ContextCompat.getColor(this, res)
}


/**
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
private fun recordInitialPaddingForView(view: View) = InitialPadding(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

/**
 * Util apply window inset extension method to simplify adjusting the insets properly
 * https://chris.banes.dev/2019/04/12/insets-listeners-to-layouts/
 *
 * Adjusted variant to allow setting padding automatically to the given view based on the gravity to apply to
 *
 * @hide
 */
@SuppressLint("ObsoleteSdkInt", "RtlHardcoded")
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun View.doOnApplySystemWindowInsets(vararg gravities: Int) {
    // Create a snapshot of the view's padding state
    val initialPadding = recordInitialPaddingForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding state
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
        setOnApplyWindowInsetsListener { v, insets ->
            gravities.forEach {
                when (it) {
                    Gravity.START, Gravity.LEFT -> {
                        v.updatePadding(left = initialPadding.left + insets.systemWindowInsetLeft)
                    }

                    Gravity.END, Gravity.RIGHT -> {
                        v.updatePadding(right = initialPadding.right + insets.systemWindowInsetRight)
                    }

                    Gravity.TOP -> {
                        v.updatePadding(top = initialPadding.top + insets.systemWindowInsetTop)
                    }

                    Gravity.BOTTOM -> {
                        v.updatePadding(bottom = initialPadding.bottom + insets.systemWindowInsetBottom)
                    }
                }
            }
            // Always return the insets, so that children can also use them
            insets
        }
        // request some insets
        requestApplyInsetsWhenAttached()
    }
}

/**
 * Fix potential issue with window insets
 * https://chris.banes.dev/2019/04/12/insets-listeners-to-layouts/
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal fun View.requestApplyInsetsWhenAttached() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
        if (isAttachedToWindow) {
            // We're already attached, just request as normal
            requestApplyInsets()
        } else {
            // We're not attached to the hierarchy, add a listener to
            // request when we are
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    v.removeOnAttachStateChangeListener(this)
                    v.requestApplyInsets()
                }

                override fun onViewDetachedFromWindow(v: View) = Unit
            })
        }
    }
}

internal fun <T> Context.resolveStyledValue(
    attrs: IntArray = R.styleable.AboutLibraries,
    defStyleAttr: Int = R.attr.aboutLibrariesStyle,
    defStyleRes: Int = R.style.AboutLibrariesStyle,
    resolver: (typedArray: TypedArray) -> T,
): T {
    val a = obtainStyledAttributes(null, attrs, defStyleAttr, defStyleRes)
    val value = resolver.invoke(a)
    a.recycle()
    return value
}


/**
 * a helper method to get the color from an attribute
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal fun Context.getThemeColor(@AttrRes attr: Int, @ColorInt def: Int = 0): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(attr, tv, true)) {
        if (tv.resourceId != 0) ResourcesCompat.getColor(resources, tv.resourceId, theme) else tv.data
    } else def
}
