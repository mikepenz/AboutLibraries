package com.mikepenz.aboutlibraries.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.sample.m3.AppTheme
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(libs: Libs?) {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text("AboutLibraries Compose Desktop Sample", maxLines = 1)
                })
            }
        ) {
            LibrariesContainer(
                libraries = libs,
                modifier = Modifier.fillMaxSize(),
                contentPadding = it,
                showFundingBadges = false,
                // divider = { Divider(modifier = Modifier.fillMaxWidth()) }
            )
        }
    }
}