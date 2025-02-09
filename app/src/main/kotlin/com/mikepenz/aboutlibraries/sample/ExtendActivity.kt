@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.sample

import android.os.Bundle
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.ui.LibsActivity


class ExtendActivity : LibsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        /*
        val intent = Intent()
        intent.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string::class.java.fields))
        intent.putExtra(Libs.BUNDLE_LIBS, arrayOf("activeandroid", "caldroid"))
        setIntent(intent)
        */
        intent = LibsBuilder()
            .withEdgeToEdge(true)
            .withSearchEnabled(true)
            .intent(this)
        super.onCreate(savedInstanceState)
    }
}
