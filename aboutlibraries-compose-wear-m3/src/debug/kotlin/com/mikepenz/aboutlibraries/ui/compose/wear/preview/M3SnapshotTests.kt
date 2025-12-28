package com.mikepenz.aboutlibraries.ui.compose.wear.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.mikepenz.aboutlibraries.ui.compose.wear.LibrariesContainer

@Composable
fun Theme(content: @Composable () -> Unit) = MaterialTheme(content = content)

@WearPreviewDevices
@Composable
fun PreviewLibraries() = Theme {
    Box {
        LibrariesContainer(libraries = fakeData, showDescription = false)
    }
}

@WearPreviewDevices
@Composable
fun PreviewLibrariesOff() = Theme {
    Box {
        LibrariesContainer(libraries = fakeData, showAuthor = false, showLicenseBadges = false, showVersion = false)
    }
}

@WearPreviewDevices
@Composable
fun PreviewLibraryRTL() = Theme {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box {
            LibrariesContainer(fakeData)
        }
    }
}
