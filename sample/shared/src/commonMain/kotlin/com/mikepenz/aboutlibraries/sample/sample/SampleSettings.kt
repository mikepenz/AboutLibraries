package com.mikepenz.aboutlibraries.sample.sample

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultLibraryActionBadges
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.DefaultLibraryBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges
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
    val useMaterial3: Boolean = true,
    val showHeader: Boolean = true,
    val headerStyle: HeaderStyle = HeaderStyle.Full,
    val headerPosition: HeaderPosition = HeaderPosition.Fixed,
    val showSearch: Boolean = true,
    val showTabsInHeader: Boolean = false,
    val showLicenseFilter: Boolean = false,
    val badges: LibraryBadges = LibraryBadges(description = true),
    val actionLabels: LibraryActionBadges = DefaultLibraryActionBadges,
    val darkTheme: Boolean = true,
    val highContrast: Boolean = false,
    val accent: Color = AccentSwatches[0],
)

/** Accent swatches that the user can pick from in the settings panel. */
val AccentSwatches: List<Color> = listOf(
    Color(0xFFC7FF1E), // lime
    Color(0xFF00FF6A), // green
    Color(0xFF00B9FF), // cyan
    Color(0xFFE94B8E), // pink
    Color(0xFF7C3AED), // purple
    Color(0xFFF59E0B), // amber
)
