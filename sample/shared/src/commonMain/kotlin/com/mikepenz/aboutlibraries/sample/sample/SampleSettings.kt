package com.mikepenz.aboutlibraries.sample.sample

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryDetailMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesVariant

enum class HeaderStyle { Full, Compact }
enum class HeaderPosition { Fixed, InList, Sticky }

@Immutable
data class SampleSettings(
    val variant: LibrariesVariant = LibrariesVariant.Traditional,
    val density: LibrariesDensity = LibrariesDensity.Cozy,
    val detailMode: LibraryDetailMode = LibraryDetailMode.Sheet,
    val actionMode: LibraryActionMode = LibraryActionMode.Chips,
    val showHeader: Boolean = true,
    val headerStyle: HeaderStyle = HeaderStyle.Full,
    val headerPosition: HeaderPosition = HeaderPosition.Fixed,
    val showSearch: Boolean = true,
    val showLicenseFilter: Boolean = false,
    val showVersion: Boolean = true,
    val showAuthor: Boolean = true,
    val showDescription: Boolean = true,
    val showLicense: Boolean = true,
    val darkTheme: Boolean = true,
    val highContrast: Boolean = false,
    val accent: Color = AccentSwatches[0],
)

/** Accent swatches that the user can pick from in the settings panel. */
val AccentSwatches: List<Color> = listOf(
    Color(0xFFE94B8E), // pink
    Color(0xFF1967D2), // blue
    Color(0xFF7C3AED), // purple
    Color(0xFF0EA5A4), // teal
    Color(0xFFF59E0B), // amber
    Color(0xFFEF4444), // red
)
