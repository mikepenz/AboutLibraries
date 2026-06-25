package com.mikepenz.aboutlibraries.screenshot.m3

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.mikepenz.aboutlibraries.screenshot.fakeLibraries
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantTextStyles
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
fun PreviewLibrariesCustomTextStyles() = Theme {
    Surface {
        LibrariesContainer(
            libraries = fakeLibraries,
            modifier = Modifier.width(360.dp),
            variantTextStyles = LibraryDefaults.m3VariantTextStyles(
                nameTextStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                versionTextStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
            ),
        )
    }
}

/**
 * Renders the M3 detail bottom sheet open, driving the stateless [LibrariesContainer] overload with a
 * fixed `sheetLibrary` and the `sheetState` exposed in #1411.
 *
 * A default `rememberModalBottomSheetState` starts `Hidden` and only animates to expanded, so Paparazzi
 * (which renders a single static frame) captures a blank sheet. Mirroring the Home Assistant Android
 * approach, we inject a `rememberStandardBottomSheetState(initialValue = Expanded)` so the sheet is already
 * laid out expanded when the frame is captured.
 */
@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun PreviewLibraryDetailSheet() = Theme {
    Surface {
        LibrariesContainer(
            libraries = fakeLibraries,
            dialogLibrary = null,
            sheetLibrary = fakeLibraries.libraries.first(),
            onDialogLibraryChange = {},
            onSheetLibraryChange = {},
            sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded),
            modifier = Modifier.fillMaxSize(),
        )
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
