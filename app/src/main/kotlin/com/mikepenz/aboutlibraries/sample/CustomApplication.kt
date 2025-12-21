@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.sample

import android.app.Application
import com.google.android.material.color.DynamicColors

import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.iconics.Iconics
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.itemanimators.SlideDownAlphaAnimator

/**
 * Created by mikepenz on 28.12.15.
 */
class CustomApplication : Application() {
    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)

        //define an itemAnimator for our AboutLibs
        LibsConfiguration.itemAnimator = SlideDownAlphaAnimator()

        //register our font
        Iconics.registerFont(MaterialDesignIconic)
        super.onCreate()
    }
}
