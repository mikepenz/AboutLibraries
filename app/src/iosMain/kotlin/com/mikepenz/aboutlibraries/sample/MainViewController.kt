package com.mikepenz.aboutlibraries.sample

import aboutlibraries.app.generated.resources.Res
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries

fun MainViewController() = ComposeUIViewController {
    SampleTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("AboutLibraries iOS Sample") }) }
        ) {
            val libraries by rememberLibraries {
                Res.readBytes("files/aboutlibraries.json").decodeToString()
            }
            LibrariesContainer(
                libraries,
                Modifier.fillMaxSize(),
            )
        }
    }
}