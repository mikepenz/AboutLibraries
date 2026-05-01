package com.mikepenz.aboutlibraries.ui.compose.m3.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultVariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultVariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.style.LicenseHueResolver
import com.mikepenz.aboutlibraries.ui.compose.style.VariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.VariantTextStyles

/**
 * Static SPDX → hex color palette derived from the design handoff (`m3.jsx`).
 *
 * Values are picked to read against both light and dark Material 3 surfaces; tweak per app
 * by passing a custom [LicenseHueResolver] to the `Libraries(...)` entry.
 */
val DefaultM3LicensePalette: Map<String, Color> = mapOf(
    "Apache-2.0" to Color(0xFFB69CFF),    // violet
    "MIT" to Color(0xFF7AC0FF),           // sky
    "EPL-2.0" to Color(0xFFE4A56A),       // amber-orange
    "EPL-1.0" to Color(0xFFE4A56A),
    "BSD-3-Clause" to Color(0xFF8AD4A4),  // green
    "BSD-2-Clause" to Color(0xFF8AD4A4),
    "GPL-3.0" to Color(0xFFFF8E8E),       // red
    "GPL-3.0-only" to Color(0xFFFF8E8E),
    "GPL-3.0-or-later" to Color(0xFFFF8E8E),
    "GPL-2.0" to Color(0xFFFF8E8E),
    "LGPL-2.1" to Color(0xFFFFB088),
    "LGPL-3.0" to Color(0xFFFFB088),
    "MPL-2.0" to Color(0xFFFFD27A),
    "ISC" to Color(0xFF7AC0FF),
    "Unlicense" to Color(0xFFB7B7B7),
    "CC0-1.0" to Color(0xFFB7B7B7),
)

/** Singleton resolver over [DefaultM3LicensePalette] — referenced by all M3 token defaults. */
val DefaultM3LicenseHueResolver: LicenseHueResolver = LicenseHueResolver(DefaultM3LicensePalette)

/** Convenience factory for building a custom-palette resolver in M3 styling. */
fun m3LicenseHueResolver(palette: Map<String, Color> = DefaultM3LicensePalette): LicenseHueResolver =
    if (palette === DefaultM3LicensePalette) DefaultM3LicenseHueResolver else LicenseHueResolver(palette)

/**
 * Builds Material 3 defaults for the variant color tokens.
 */
@Composable
fun LibraryDefaults.m3VariantColors(
    headerBackground: Color = MaterialTheme.colorScheme.surfaceContainer,
    headerOnBackground: Color = MaterialTheme.colorScheme.onSurface,
    headerSubtleContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    headerDivider: Color = MaterialTheme.colorScheme.outlineVariant,
    rowBackground: Color = MaterialTheme.colorScheme.surface,
    rowExpandedBackground: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    rowOnBackground: Color = MaterialTheme.colorScheme.onSurface,
    rowSubtleContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    rowDivider: Color = MaterialTheme.colorScheme.outlineVariant,
    actionFilledContainer: Color = MaterialTheme.colorScheme.primary,
    actionFilledContent: Color = MaterialTheme.colorScheme.onPrimary,
    actionOutlineBorder: Color = MaterialTheme.colorScheme.outline,
    actionOutlineContent: Color = MaterialTheme.colorScheme.onSurface,
    actionLinkColor: Color = MaterialTheme.colorScheme.primary,
    tabIdleBackground: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    tabIdleContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    // design: color-mix(primary 22%, surfaceContainer) — matches sample-app.jsx LicenseFilterBar
    tabActiveBackground: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
        .compositeOver(MaterialTheme.colorScheme.surfaceContainer),
    tabActiveBorder: Color = MaterialTheme.colorScheme.primary,
    tabActiveContent: Color = MaterialTheme.colorScheme.primary,
    sheetScrim: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f),
    sheetSurface: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    sheetSurfaceVariant: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    sheetDragHandle: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    licenseHueResolver: LicenseHueResolver = DefaultM3LicenseHueResolver,
): VariantColors = remember(
    headerBackground, headerOnBackground, headerSubtleContent, headerDivider,
    rowBackground, rowExpandedBackground, rowOnBackground, rowSubtleContent, rowDivider,
    actionFilledContainer, actionFilledContent, actionOutlineBorder, actionOutlineContent, actionLinkColor,
    tabIdleBackground, tabIdleContent, tabActiveBackground, tabActiveBorder, tabActiveContent,
    sheetScrim, sheetSurface, sheetSurfaceVariant, sheetDragHandle, licenseHueResolver,
) {
    DefaultVariantColors(
        headerBackground, headerOnBackground, headerSubtleContent, headerDivider,
        rowBackground, rowExpandedBackground, rowOnBackground, rowSubtleContent, rowDivider,
        actionFilledContainer, actionFilledContent, actionOutlineBorder, actionOutlineContent, actionLinkColor,
        tabIdleBackground, tabIdleContent, tabActiveBackground, tabActiveBorder, tabActiveContent,
        sheetScrim, sheetSurface, sheetSurfaceVariant, sheetDragHandle, licenseHueResolver,
    )
}

/**
 * Builds Material 3 typography defaults for the variant text styles.
 */
@Composable
fun LibraryDefaults.m3VariantTextStyles(
    nameTextStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 1.2.em,
    ),
    authorTextStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = 11.sp,
        lineHeight = 1.25.em,
    ),
    versionTextStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
    licenseTextStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
    ),
    descriptionTextStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = 12.sp,
        lineHeight = 1.5.em,
    ),
    headerTitleTextStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.2).sp,
        lineHeight = 1.1.em,
    ),
    headerTaglineTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
    tabTextStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
    ),
    tabCountTextStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
    sheetTitleTextStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeightStyle = LineHeightStyle.Default,
    ),
    sheetMetaTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
    sheetBodyTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 14.sp,
        lineHeight = 1.5.em,
    ),
    actionLinkTextStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    ),
    actionChipTextStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    ),
): VariantTextStyles = remember(
    nameTextStyle, authorTextStyle, versionTextStyle, licenseTextStyle, descriptionTextStyle,
    headerTitleTextStyle, headerTaglineTextStyle,
    tabTextStyle, tabCountTextStyle,
    sheetTitleTextStyle, sheetMetaTextStyle, sheetBodyTextStyle,
    actionLinkTextStyle, actionChipTextStyle,
) {
    DefaultVariantTextStyles(
        nameTextStyle, authorTextStyle, versionTextStyle, licenseTextStyle, descriptionTextStyle,
        headerTitleTextStyle, headerTaglineTextStyle,
        tabTextStyle, tabCountTextStyle,
        sheetTitleTextStyle, sheetMetaTextStyle, sheetBodyTextStyle,
        actionLinkTextStyle, actionChipTextStyle,
    )
}
