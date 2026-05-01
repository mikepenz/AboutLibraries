package com.mikepenz.aboutlibraries.sample

import android.app.Application
import com.google.android.material.color.DynamicColors

class CustomApplication : Application() {
    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()
    }
}
