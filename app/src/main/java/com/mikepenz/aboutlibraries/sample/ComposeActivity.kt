package com.mikepenz.aboutlibraries.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Update the system bars to be translucent
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
            }

            MainLayout()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainLayout() {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
    ) {
        ProvideWindowInsets {
            var showAuthor by remember { mutableStateOf(true) }
            var showVersion by remember { mutableStateOf(true) }
            var showLicenseBadges by remember { mutableStateOf(true) }

            Scaffold(
                topBar = {
                    // We use TopAppBar from accompanist-insets-ui which allows us to provide
                    // content padding matching the system bars insets.
                    TopAppBar(
                        title = { Text("Compose Sample") },
                        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.9f),
                        contentPadding = rememberInsetsPaddingValues(
                            LocalWindowInsets.current.statusBars,
                            applyBottom = false,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        actions = {
                            IconButton(onClick = { showAuthor = !showAuthor }) { Icon(Icons.Default.Person, "Author") }
                            IconButton(onClick = { showVersion = !showVersion }) { Icon(Icons.Default.Build, "Version") }
                            IconButton(onClick = { showLicenseBadges = !showLicenseBadges }) { Icon(Icons.Default.List, "Licenses") }
                        }
                    )
                },
            ) { contentPadding ->
                LibrariesContainer(
                    Modifier.fillMaxSize(),
                    contentPadding = rememberInsetsPaddingValues(
                        insets = LocalWindowInsets.current.systemBars,
                        additionalTop = contentPadding.calculateTopPadding(),
                        applyTop = false,
                        applyBottom = true
                    ),
                    showAuthor = showAuthor,
                    showVersion = showVersion,
                    showLicenseBadges = showLicenseBadges
                )
            }
        }
    }
}
