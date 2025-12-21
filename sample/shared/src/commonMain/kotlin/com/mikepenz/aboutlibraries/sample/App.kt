package com.mikepenz.aboutlibraries.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.sample.components.ToggleableSetting
import com.mikepenz.aboutlibraries.sample.components.TopAppBar
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(libs: Libs?) {
    val isSystemInDarkMode = isSystemInDarkTheme()
    var darkMode by remember { mutableStateOf(isSystemInDarkMode) }
    var useV3 by remember { mutableStateOf(true) }
    var showAuthor by remember { mutableStateOf(true) }
    var showDescription by remember { mutableStateOf(false) }
    var showVersion by remember { mutableStateOf(true) }
    var showLicenseBadges by remember { mutableStateOf(true) }
    var showHeader by remember { mutableStateOf(false) }
    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val scope = rememberCoroutineScope()


    AppTheme(useV3 = useV3, useDarkTheme = darkMode) {
        BottomDrawer(
            gesturesEnabled = bottomDrawerState.isOpen,
            drawerState = bottomDrawerState,
            drawerContent = {
                Column(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                ) {
                    ToggleableSetting(
                        useV3 = useV3,
                        title = "Show Author",
                        icon = Icons.Default.Person,
                        enabled = showAuthor,
                        onToggled = { showAuthor = it },
                    )
                    ToggleableSetting(
                        useV3 = useV3,
                        title = "Show Description",
                        icon = Icons.Default.Person,
                        enabled = showDescription,
                        onToggled = { showDescription = it },
                    )
                    ToggleableSetting(
                        useV3 = useV3,
                        title = "Show Version",
                        icon = Icons.Default.Build,
                        enabled = showVersion,
                        onToggled = { showVersion = it },
                    )
                    ToggleableSetting(
                        useV3 = useV3,
                        title = "Show License Badges",
                        icon = Icons.AutoMirrored.Filled.List,
                        enabled = showLicenseBadges,
                        onToggled = { showLicenseBadges = it },
                    )
                    ToggleableSetting(
                        useV3 = useV3,
                        title = "Show Header",
                        icon = Icons.Default.Info,
                        enabled = showHeader,
                        onToggled = { showHeader = it },
                    )
                }
            }, content = {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            useDarkTheme = darkMode,
                            onThemeToggle = { darkMode = it },
                            useV3 = useV3,
                            onV3Toggle = { useV3 = it },
                            onSettingsClick = { scope.launch { bottomDrawerState.open() } },
                        )
                    }
                ) {
                    if (useV3) {
                        LibrariesContainer(
                            libraries = libs,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = it,
                            showAuthor = showAuthor,
                            showDescription = showDescription,
                            showVersion = showVersion,
                            showLicenseBadges = showLicenseBadges,
                            header = {
                                if (showHeader) {
                                    stickyHeader {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface)
                                                .padding(vertical = 25.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Text("ExampleHeader")
                                        }
                                    }
                                }
                            }
                        )
                    } else {
                        com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer(
                            libraries = libs,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = it,
                            showAuthor = showAuthor,
                            showDescription = showDescription,
                            showVersion = showVersion,
                            showLicenseBadges = showLicenseBadges,
                            header = {
                                if (showHeader) {
                                    stickyHeader {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface)
                                                .padding(vertical = 25.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Text("ExampleHeader")
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}
