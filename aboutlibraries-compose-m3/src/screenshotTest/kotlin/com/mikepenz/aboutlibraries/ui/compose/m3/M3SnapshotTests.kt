package com.mikepenz.aboutlibraries.ui.compose.m3

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.LayoutDirection
import com.mikepenz.aboutlibraries.ui.compose.m3.data.fakeData

@Composable
fun Theme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) = MaterialTheme(if (isDarkTheme) darkColorScheme() else lightColorScheme()) { content() }

@PreviewLightDark
@Composable
fun PreviewLibraries() = Theme {
    Surface {
        Libraries(
            fakeData.libraries,
            showDescription = true
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewLibrariesOff() = Theme {
    Surface {
        Libraries(fakeData.libraries, showAuthor = false, showLicenseBadges = false)
    }
}

@PreviewLightDark
@Composable
fun PreviewLibrary() = Theme {
    Surface {
        Library(
            fakeData.libraries.first()
        ) {
            // on-click
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewLibraryRTL() = Theme {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface {
            Library(
                fakeData.libraries.first()
            ) {
                // on-click
            }
        }
    }
}
