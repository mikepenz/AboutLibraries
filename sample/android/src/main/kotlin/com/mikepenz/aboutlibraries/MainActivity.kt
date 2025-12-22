package com.mikepenz.aboutlibraries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import com.mikepenz.aboutlibraries.sample.App
import com.mikepenz.aboutlibraries.sample.R
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val libraries by produceLibraries(R.raw.aboutlibraries)
            App(libs = libraries)
        }
    }
}
