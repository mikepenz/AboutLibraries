package com.mikepenz.aboutlibraries.sample.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.sample.icon.Github

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBar(
    useDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    useV3: Boolean,
    onV3Toggle: (Boolean) -> Unit,
    onSettingsClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    if (useV3) {
        TopAppBar(
            title = { Text("AboutLibraries Compose Sample", maxLines = 1) },
            actions = {
                Switch(
                    checked = useDarkTheme,
                    onCheckedChange = onThemeToggle,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Switch(
                    checked = useV3,
                    onCheckedChange = onV3Toggle,
                    modifier = Modifier.padding(end = 16.dp)
                )
                IconButton(onClick = { uriHandler.openUri("https://github.com/mikepenz/AboutLibraries") }) {
                    Icon(
                        imageVector = Github,
                        contentDescription = "GitHub"
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        )
    } else {
        androidx.compose.material.TopAppBar(
            title = {
                androidx.compose.material.Text("AboutLibraries Compose Sample", maxLines = 1)
            },
            windowInsets = WindowInsets.safeContent.only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Top
            ),
            actions = {
                androidx.compose.material.Switch(
                    checked = useDarkTheme,
                    onCheckedChange = onThemeToggle,
                    modifier = Modifier.padding(end = 16.dp)
                )
                androidx.compose.material.Switch(
                    checked = useV3,
                    onCheckedChange = onV3Toggle,
                    modifier = Modifier.padding(end = 16.dp)
                )
                androidx.compose.material.IconButton(onClick = { uriHandler.openUri("https://github.com/mikepenz/AboutLibraries") }) {
                    androidx.compose.material.Icon(
                        imageVector = Github,
                        contentDescription = "GitHub"
                    )
                }
                androidx.compose.material.IconButton(onClick = onSettingsClick) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        )
    }
}
