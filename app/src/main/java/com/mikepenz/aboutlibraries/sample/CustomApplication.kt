package com.mikepenz.aboutlibraries.sample

import android.app.Application

import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.iconics.Iconics
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic

/**
 * Created by mikepenz on 28.12.15.
 */
class CustomApplication : Application() {
    override fun onCreate() {
        //define an itemAnimator for our AboutLibs
        LibsConfiguration.instance.itemAnimator = SlideDownAlphaAnimator()
        //register our font
        Iconics.registerFont(MaterialDesignIconic())
        super.onCreate()
    }
}
