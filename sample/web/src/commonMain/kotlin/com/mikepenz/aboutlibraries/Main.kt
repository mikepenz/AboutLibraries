package com.mikepenz.aboutlibraries

import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.mikepenz.aboutlibraries.sample.App
import com.mikepenz.aboutlibraries.sample.web.resources.Res
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        val libraries by produceLibraries {
            Res.readBytes("files/aboutlibraries.json").decodeToString()
        }
        App(libs = libraries)
    }
}
