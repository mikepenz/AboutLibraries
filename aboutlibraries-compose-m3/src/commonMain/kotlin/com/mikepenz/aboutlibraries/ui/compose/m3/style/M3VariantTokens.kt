package com.mikepenz.aboutlibraries.ui.compose.m3.style

import androidx.compose.foundation.isSystemInDarkTheme
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
 * SPDX → color palette for **dark** surfaces.
 *
 * Colors are pastel-toned (high value, low-medium saturation) so they read as text on a
 * dark surface and produce a subtle tinted badge when used at 15% alpha.
 * Hue identity matches the design handoff's `LICENSE_HUES` (OKLCH angles converted to HSV).
 */
val DarkM3LicensePalette: Map<String, Color> = mapOf(
    "Apache-2.0" to Color(0xFFB69CFF),    // violet  — HSV 270°
    "MIT" to Color(0xFF7AC0FF),           // sky     — HSV 207°
    "EPL-2.0" to Color(0xFFE4A56A),       // amber   — HSV  30°
    "EPL-1.0" to Color(0xFFE4A56A),
    "BSD-3-Clause" to Color(0xFF8AD4A4),  // green   — HSV 143°
    "BSD-2-Clause" to Color(0xFF8AD4A4),
    "GPL-3.0" to Color(0xFFFF8E8E),       // red     — HSV   0°
    "GPL-3.0-only" to Color(0xFFFF8E8E),
    "GPL-3.0-or-later" to Color(0xFFFF8E8E),
    "GPL-2.0" to Color(0xFFFF8E8E),
    "LGPL-2.1" to Color(0xFFFFB088),      // orange  — HSV  20°
    "LGPL-3.0" to Color(0xFFFFB088),
    "MPL-2.0" to Color(0xFFFFD27A),       // yellow  — HSV  44°
    "ISC" to Color(0xFF7AC0FF),
    "Unlicense" to Color(0xFFB7B7B7),
    "CC0-1.0" to Color(0xFFB7B7B7),
)

/**
 * SPDX → color palette for **light** surfaces.
 *
 * Same hue identities as [DarkM3LicensePalette] but deeper/more saturated so they maintain
 * 4.5:1+ contrast as text on a light surface and still produce a legible tinted badge at 15%.
 */
val LightM3LicensePalette: Map<String, Color> = mapOf(
    "Apache-2.0" to Color(0xFF5C35CC),    // violet  — HSV 258° s=74 v=80
    "MIT" to Color(0xFF1A6DB5),           // sky     — HSV 208° s=85 v=71
    "EPL-2.0" to Color(0xFFB06010),       // amber   — HSV  32° s=91 v=69
    "EPL-1.0" to Color(0xFFB06010),
    "BSD-3-Clause" to Color(0xFF28834E),  // green   — HSV 143° s=69 v=51
    "BSD-2-Clause" to Color(0xFF28834E),
    "GPL-3.0" to Color(0xFFCC2828),       // red     — HSV   0° s=81 v=80
    "GPL-3.0-only" to Color(0xFFCC2828),
    "GPL-3.0-or-later" to Color(0xFFCC2828),
    "GPL-2.0" to Color(0xFFCC2828),
    "LGPL-2.1" to Color(0xFFB85020),      // orange  — HSV  20° s=83 v=72
    "LGPL-3.0" to Color(0xFFB85020),
    "MPL-2.0" to Color(0xFF9E7200),       // yellow  — HSV  43° s=100 v=62
    "ISC" to Color(0xFF1A6DB5),
    "Unlicense" to Color(0xFF666666),
    "CC0-1.0" to Color(0xFF666666),
)

/** @suppress kept for source compatibility — prefer [DarkM3LicensePalette] for explicit dark theming. */
val DefaultM3LicensePalette: Map<String, Color> get() = DarkM3LicensePalette

/** Singleton resolver over [DarkM3LicensePalette]. */
val DefaultM3LicenseHueResolver: LicenseHueResolver = LicenseHueResolver(DarkM3LicensePalette)

/** Convenience factory for building a custom-palette resolver in M3 styling. */
fun m3LicenseHueResolver(palette: Map<String, Color> = DarkM3LicensePalette): LicenseHueResolver =
    if (palette === DarkM3LicensePalette) DefaultM3LicenseHueResolver else LicenseHueResolver(palette)

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
    licenseHueResolver: LicenseHueResolver = if (isSystemInDarkTheme()) DefaultM3LicenseHueResolver
        else remember { LicenseHueResolver(LightM3LicensePalette) },
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
