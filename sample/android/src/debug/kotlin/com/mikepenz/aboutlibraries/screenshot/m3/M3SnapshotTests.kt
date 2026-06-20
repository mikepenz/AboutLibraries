package com.mikepenz.aboutlibraries.screenshot.m3

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.screenshot.fakeLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges

@Composable
fun Theme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) = MaterialTheme(if (isDarkTheme) darkColorScheme() else lightColorScheme()) { content() }

@PreviewLightDark
@Composable
fun PreviewLibraries() = Theme {
    Surface {
        LibrariesContainer(libraries = fakeLibraries, badges = LibraryBadges(description = true), modifier = Modifier.width(360.dp))
    }
}

@PreviewLightDark
@Composable
fun PreviewLibrariesOff() = Theme {
    Surface {
        LibrariesContainer(libraries = fakeLibraries, badges = LibraryBadges(author = false, license = false), modifier = Modifier.width(360.dp))
    }
}

@PreviewLightDark
@Composable
fun PreviewLibraryRTL() = Theme {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface {
            LibrariesContainer(fakeLibraries, modifier = Modifier.width(360.dp))
        }
    }
}
