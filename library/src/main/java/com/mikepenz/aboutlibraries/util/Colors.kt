package com.mikepenz.aboutlibraries.util

import android.os.Parcel
import java.io.Serializable


/**
 * Created by Mike Penz.
 */
class Colors : Serializable {
    var appBarColor: Int = 0
    var statusBarColor: Int = 0

    constructor(toolbarColor: Int, statusBarColor: Int) {
        this.appBarColor = toolbarColor
        this.statusBarColor = statusBarColor
    }

    private constructor(`in`: Parcel) {
        this.appBarColor = `in`.readInt()
        this.statusBarColor = `in`.readInt()
    }
}
