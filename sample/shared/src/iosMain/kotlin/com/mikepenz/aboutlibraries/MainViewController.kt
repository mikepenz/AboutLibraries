package com.mikepenz.aboutlibraries

import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import com.mikepenz.aboutlibraries.sample.App
import com.mikepenz.aboutlibraries.sample.shared.resources.Res
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries

fun MainViewController() = ComposeUIViewController {
    val libraries by produceLibraries {
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }
    App(libs = libraries)
}
