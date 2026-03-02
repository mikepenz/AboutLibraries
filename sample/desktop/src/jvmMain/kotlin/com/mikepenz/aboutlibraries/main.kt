package com.mikepenz.aboutlibraries

import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mikepenz.aboutlibraries.sample.App
import com.mikepenz.aboutlibraries.sample.desktop.resources.Res
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "AboutLibraries Sample") {
        val libraries by produceLibraries {
            Res.readBytes("files/aboutlibraries.json").decodeToString()
        }
        App(libraries)
    }
}
