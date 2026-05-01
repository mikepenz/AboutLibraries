package com.mikepenz.aboutlibraries.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.sample.AppTheme
import com.mikepenz.aboutlibraries.sample.sample.LicenseFilterBar
import com.mikepenz.aboutlibraries.sample.sample.LicenseFilterTab
import com.mikepenz.aboutlibraries.sample.sample.PillToggle
import com.mikepenz.aboutlibraries.sample.sample.SampleHeader
import com.mikepenz.aboutlibraries.sample.sample.SampleSettings
import com.mikepenz.aboutlibraries.sample.sample.Segmented
import com.mikepenz.aboutlibraries.sample.sample.SettingsPanel
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantColors
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultLibraryStrings
import com.mikepenz.aboutlibraries.ui.compose.style.defaultVariantDimensions
import com.mikepenz.aboutlibraries.ui.compose.style.defaultVariantPadding
import com.mikepenz.aboutlibraries.ui.compose.style.defaultVariantShapes
import com.mikepenz.aboutlibraries.ui.compose.style.librariesStyle
import com.mikepenz.aboutlibraries.ui.compose.variant.refined.LicenseTab
import com.mikepenz.aboutlibraries.ui.compose.variant.refined.RefinedHeader
import com.mikepenz.aboutlibraries.ui.compose.variant.traditional.TraditionalHeader

// Default accent = design's pink #E94B8E
private val Accent = Color(0xFFE94B8E)
private val DemoSettings = SampleSettings(darkTheme = true, accent = Accent)

// ── Icon badge helpers ────────────────────────────────────────────────────

@Composable
private fun FullAppIcon() {
    Box(
        modifier = Modifier.fillMaxSize().background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp),
        ),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = "A",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CompactAppIcon() {
    Box(
        modifier = Modifier.fillMaxSize().background(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(7.dp),
        ),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = "A",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ── Per-header styles (mirrors App.kt) ────────────────────────────────────

@Composable
private fun fullStyle() = LibraryDefaults.librariesStyle(
    colors = LibraryDefaults.m3VariantColors(
        headerBackground = MaterialTheme.colorScheme.surfaceContainer,
    ),
    textStyles = LibraryDefaults.m3VariantTextStyles(
        headerTitleTextStyle = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.2).sp,
        ),
    ),
    padding = remember {
        LibraryDefaults.defaultVariantPadding(
            headerPadding = PaddingValues(start = 22.dp, top = 18.dp, end = 22.dp, bottom = 16.dp),
        )
    },
    dimensions = remember {
        LibraryDefaults.defaultVariantDimensions(
            headerIconSize = 44.dp,
            searchHeight = 40.dp,
        )
    },
)

@Composable
private fun compactStyle() = LibraryDefaults.librariesStyle(
    colors = LibraryDefaults.m3VariantColors(
        headerBackground = MaterialTheme.colorScheme.surfaceContainerLow,
    ),
    textStyles = LibraryDefaults.m3VariantTextStyles(
        headerTitleTextStyle = MaterialTheme.typography.titleSmall.copy(
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp,
        ),
        headerTaglineTextStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
    ),
    padding = remember {
        LibraryDefaults.defaultVariantPadding(
            headerPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        )
    },
    dimensions = remember {
        LibraryDefaults.defaultVariantDimensions(
            headerIconSize = 28.dp,
            searchHeight = 30.dp,
        )
    },
    shapes = remember {
        LibraryDefaults.defaultVariantShapes(
            headerSearchShape = RoundedCornerShape(8.dp),
        )
    },
)

// ── Previews ──────────────────────────────────────────────────────────────

@Preview(name = "SampleHeader · desktop", widthDp = 928, heightDp = 80, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewSampleHeaderDesktop() = AppTheme(useV3 = true, useDarkTheme = true, accent = Accent) {
    SampleHeader(
        settings = DemoSettings,
        isMobile = false,
        onToggleTheme = {},
        onToggleHeader = {},
        onToggleVariant = {},
        onOpenGithub = {},
        onOpenSettings = {},
        appName = "AboutLibraries",
        appVersion = "11.2.0",
    )
}

@Preview(name = "SampleHeader · mobile", widthDp = 376, heightDp = 64, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewSampleHeaderMobile() = AppTheme(useV3 = true, useDarkTheme = true, accent = Accent) {
    SampleHeader(
        settings = DemoSettings,
        isMobile = true,
        onToggleTheme = {},
        onToggleHeader = {},
        onToggleVariant = {},
        onOpenGithub = null,
        onOpenSettings = {},
        appName = "AboutLibraries",
        appVersion = "11.2.0",
    )
}

@Preview(name = "TraditionalHeader (full) · dark", widthDp = 600, heightDp = 130, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalHeaderDark() = AppTheme(useV3 = true, useDarkTheme = true, accent = Accent) {
    TraditionalHeader(
        title = "AboutLibraries",
        tagline = "Open source acknowledgements",
        versionLabel = "v11.2.0",
        style = fullStyle(),
        strings = DefaultLibraryStrings,
        appIcon = { FullAppIcon() },
        onSearchChange = {},
    )
}

@Preview(name = "TraditionalHeader (full) · light", widthDp = 600, heightDp = 130, showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewTraditionalHeaderLight() = AppTheme(useV3 = true, useDarkTheme = false, accent = Accent) {
    TraditionalHeader(
        title = "AboutLibraries",
        tagline = "Open source acknowledgements",
        versionLabel = "v11.2.0",
        style = fullStyle(),
        strings = DefaultLibraryStrings,
        appIcon = { FullAppIcon() },
        onSearchChange = {},
    )
}

@Preview(name = "RefinedHeader (compact) · dark", widthDp = 600, heightDp = 66, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedHeaderDark() = AppTheme(useV3 = true, useDarkTheme = true, accent = Accent) {
    RefinedHeader(
        title = "AboutLibraries",
        subtitle = "v11.2.0 · 23 libraries",
        style = compactStyle(),
        strings = DefaultLibraryStrings,
        appIcon = { CompactAppIcon() },
        onSearchChange = {},
        inlineSearch = true,
    )
}

@Preview(name = "RefinedHeader (compact) · light", widthDp = 600, heightDp = 66, showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewRefinedHeaderLight() = AppTheme(useV3 = true, useDarkTheme = false, accent = Accent) {
    RefinedHeader(
        title = "AboutLibraries",
        subtitle = "v11.2.0 · 23 libraries",
        style = compactStyle(),
        strings = DefaultLibraryStrings,
        appIcon = { CompactAppIcon() },
        onSearchChange = {},
        inlineSearch = true,
    )
}

@Preview(name = "RefinedHeader (compact) + tabs · dark", widthDp = 600, heightDp = 100, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedHeaderWithTabsDark() = AppTheme(useV3 = true, useDarkTheme = true, accent = Accent) {
    RefinedHeader(
        title = "AboutLibraries",
        subtitle = "v11.2.0 · 23 libraries",
        style = compactStyle(),
        strings = DefaultLibraryStrings,
        appIcon = { CompactAppIcon() },
        onSearchChange = {},
        inlineSearch = true,
        tabs = listOf(
            LicenseTab(null, "All", 23),
            LicenseTab("Apache-2.0", "Apache 2.0", 18),
            LicenseTab("MIT", "MIT", 4),
            LicenseTab("BSD-3-Clause", "BSD 3-Clause", 1),
        ),
    )
}

@Preview(name = "LicenseFilterBar · dark", widthDp = 600, heightDp = 50, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewLicenseFilterBarDark() = AppTheme(useV3 = true, useDarkTheme = true, accent = Accent) {
    LicenseFilterBar(
        tabs = listOf(
            LicenseFilterTab(null, "All", 23),
            LicenseFilterTab("Apache-2.0", "Apache 2.0", 18),
            LicenseFilterTab("MIT", "MIT", 4),
            LicenseFilterTab("BSD-3-Clause", "BSD 3-Clause", 1),
        ),
        selectedSpdxId = "Apache-2.0",
        onSelect = {},
        isMobile = false,
    )
}

@Preview(name = "LicenseFilterBar · light", widthDp = 600, heightDp = 50, showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewLicenseFilterBarLight() = AppTheme(useV3 = true, useDarkTheme = false, accent = Accent) {
    LicenseFilterBar(
        tabs = listOf(
            LicenseFilterTab(null, "All", 23),
            LicenseFilterTab("Apache-2.0", "Apache 2.0", 18),
            LicenseFilterTab("MIT", "MIT", 4),
            LicenseFilterTab("BSD-3-Clause", "BSD 3-Clause", 1),
        ),
        selectedSpdxId = "Apache-2.0",
        onSelect = {},
        isMobile = false,
    )
}

@Preview(name = "PillToggle row", widthDp = 200, heightDp = 60, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewPillToggleRow() = AppTheme(useV3 = true, useDarkTheme = true, accent = Accent) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PillToggle(on = true, onToggle = {}, textIndicator = "A")
            PillToggle(on = false, onToggle = {}, textIndicator = "C")
            PillToggle(on = true, onToggle = {}, textIndicator = "H")
        }
    }
}

@Preview(name = "Segmented", widthDp = 320, heightDp = 60, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewSegmented() = AppTheme(useV3 = true, useDarkTheme = true, accent = Accent) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(16.dp),
    ) {
        Segmented(
            options = listOf("a" to "Traditional", "c" to "Refined"),
            selected = "a",
            onSelect = {},
        )
    }
}

@Preview(name = "SettingsPanel · desktop · dark", widthDp = 320, heightDp = 800, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewSettingsPanelDark() = AppTheme(useV3 = true, useDarkTheme = true, accent = Accent) {
    val settingsState = remember { mutableStateOf(DemoSettings) }
    SettingsPanel(
        settings = settingsState.value,
        onChange = { settingsState.value = it },
        onClose = {},
        isMobile = false,
        modifier = Modifier.fillMaxSize(),
    )
}

@Preview(name = "SettingsPanel · desktop · light", widthDp = 320, heightDp = 800, showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewSettingsPanelLight() = AppTheme(useV3 = true, useDarkTheme = false, accent = Accent) {
    val settingsState = remember { mutableStateOf(SampleSettings(darkTheme = false, accent = Accent)) }
    SettingsPanel(
        settings = settingsState.value,
        onChange = { settingsState.value = it },
        onClose = {},
        isMobile = false,
        modifier = Modifier.fillMaxSize(),
    )
}
