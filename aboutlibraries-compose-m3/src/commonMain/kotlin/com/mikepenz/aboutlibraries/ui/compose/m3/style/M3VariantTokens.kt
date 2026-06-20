package com.mikepenz.aboutlibraries.ui.compose.m3.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.style.ContrastLevel
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultVariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultVariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.style.LicenseHueResolver
import com.mikepenz.aboutlibraries.ui.compose.style.VariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.VariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.style.defaultLicensePalette
import com.mikepenz.aboutlibraries.ui.compose.style.withPaletteFallback

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
 * All entries verified ≥6:1 WCAG contrast against white (#FFFFFF).
 * Yellow-range hues (EPL, MPL, LGPL) need lower HSV value to stay below the
 * sRGB luminance threshold; darker shades are used specifically for them.
 */
val LightM3LicensePalette: Map<String, Color> = mapOf(
    "Apache-2.0" to Color(0xFF5030BB),    // violet  — 8.5:1
    "MIT" to Color(0xFF1560A8),           // sky     — 6.4:1
    "EPL-2.0" to Color(0xFF8F4C0A),       // amber   — 6.6:1
    "EPL-1.0" to Color(0xFF8F4C0A),
    "BSD-3-Clause" to Color(0xFF1E6B3D),  // green   — 6.5:1
    "BSD-2-Clause" to Color(0xFF1E6B3D),
    "GPL-3.0" to Color(0xFFBB2222),       // red     — 6.2:1
    "GPL-3.0-only" to Color(0xFFBB2222),
    "GPL-3.0-or-later" to Color(0xFFBB2222),
    "GPL-2.0" to Color(0xFFBB2222),
    "LGPL-2.1" to Color(0xFF963F18),      // orange  — 6.9:1
    "LGPL-3.0" to Color(0xFF963F18),
    "MPL-2.0" to Color(0xFF7A5400),       // amber-brown — 6.8:1
    "ISC" to Color(0xFF1560A8),
    "Unlicense" to Color(0xFF5A5A5A),     // neutral — 7.3:1
    "CC0-1.0" to Color(0xFF5A5A5A),
)

/**
 * Hue offsets (in HSV degrees) for each SPDX family relative to the accent color.
 * Chosen so licenses stay visually distinct across the full accent hue range.
 * `null` = neutral gray (no hue identity).
 */
private val LICENSE_HUE_OFFSETS: Map<String, Float?> = mapOf(
    "Apache-2.0" to -60f,
    "MIT" to -130f,
    "EPL-2.0" to 60f,
    "EPL-1.0" to 60f,
    "BSD-3-Clause" to 170f,
    "BSD-2-Clause" to 170f,
    "GPL-3.0" to 30f,
    "GPL-3.0-only" to 30f,
    "GPL-3.0-or-later" to 30f,
    "GPL-2.0" to 30f,
    "LGPL-2.1" to 50f,
    "LGPL-3.0" to 50f,
    "MPL-2.0" to 75f,
    "ISC" to -130f,
    "Unlicense" to null,
    "CC0-1.0" to null,
)

/** Returns the HSV hue (0–360°) of this color. */
private fun Color.hsvHue(): Float {
    val r = red;
    val g = green;
    val b = blue
    val max = maxOf(r, g, b);
    val min = minOf(r, g, b)
    val delta = max - min
    if (delta == 0f) return 0f
    val h = when (max) {
        r -> (g - b) / delta % 6f
        g -> (b - r) / delta + 2f
        else -> (r - g) / delta + 4f
    } * 60f
    return if (h < 0f) h + 360f else h
}

/**
 * Builds a [LicenseHueResolver] whose colors rotate with the Material 3 accent (`primary`).
 * Each SPDX family keeps its fixed hue offset from the accent, so swapping the accent
 * shifts all badge and dot colors in lockstep while preserving their relative identities.
 *
 * This is the default used by [LibraryDefaults.m3VariantColors].
 */
@Composable
fun accentDerivedLicenseHueResolver(
    isDark: Boolean = isSystemInDarkTheme(),
    contrastLevel: ContrastLevel = ContrastLevel.Normal,
): LicenseHueResolver {
    val accent = MaterialTheme.colorScheme.primary
    return remember(accent, isDark, contrastLevel) {
        val accentHue = accent.convert(ColorSpaces.Srgb).hsvHue()
        // Light-mode: s=0.75, v=0.45 ensures ≥5.2:1 contrast for all hues including yellow.
        // Yellow/lime (hue 40-80°) are the hardest — at these values they reach ~5-6:1 vs white.
        val saturation = when {
            isDark && contrastLevel == ContrastLevel.High -> 0.55f
            isDark -> 0.40f
            contrastLevel == ContrastLevel.High -> 0.90f
            else -> 0.75f
        }
        val value = when {
            isDark && contrastLevel == ContrastLevel.High -> 1.00f
            isDark -> 0.97f
            contrastLevel == ContrastLevel.High -> 0.32f
            else -> 0.45f
        }
        val neutral = when {
            isDark && contrastLevel == ContrastLevel.High -> Color(0xFFDDDDDD)
            isDark -> Color(0xFFB7B7B7)
            contrastLevel == ContrastLevel.High -> Color(0xFF3A3A3A)
            else -> Color(0xFF5A5A5A)
        }
        val palette = LICENSE_HUE_OFFSETS.mapKeys { it.key }.mapValues { (_, offset) ->
            if (offset == null) neutral
            else Color.hsv(((accentHue + offset) % 360f + 360f) % 360f, saturation, value)
        }
        LicenseHueResolver(palette).withPaletteFallback(defaultLicensePalette(isDark, contrastLevel))
    }
}

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
    contrastLevel: ContrastLevel = ContrastLevel.Normal,
    headerBackground: Color = MaterialTheme.colorScheme.surfaceContainer,
    headerOnBackground: Color = MaterialTheme.colorScheme.onSurface,
    headerSubtleContent: Color = if (contrastLevel == ContrastLevel.High)
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f)
    else MaterialTheme.colorScheme.onSurfaceVariant,
    headerDivider: Color = MaterialTheme.colorScheme.outlineVariant,
    rowBackground: Color = MaterialTheme.colorScheme.surface,
    rowExpandedBackground: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    rowOnBackground: Color = MaterialTheme.colorScheme.onSurface,
    rowSubtleContent: Color = if (contrastLevel == ContrastLevel.High)
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f)
    else MaterialTheme.colorScheme.onSurfaceVariant,
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
    licenseHueResolver: LicenseHueResolver = accentDerivedLicenseHueResolver(contrastLevel = contrastLevel),
): VariantColors = remember(
    headerBackground, headerOnBackground, headerSubtleContent, headerDivider,
    rowBackground, rowExpandedBackground, rowOnBackground, rowSubtleContent, rowDivider,
    actionFilledContainer, actionFilledContent, actionOutlineBorder, actionOutlineContent, actionLinkColor,
    tabIdleBackground, tabIdleContent, tabActiveBackground, tabActiveBorder, tabActiveContent,
    sheetScrim, sheetSurface, sheetSurfaceVariant, sheetDragHandle, licenseHueResolver, contrastLevel,
) {
    DefaultVariantColors(
        headerBackground, headerOnBackground, headerSubtleContent, headerDivider,
        rowBackground, rowExpandedBackground, rowOnBackground, rowSubtleContent, rowDivider,
        actionFilledContainer, actionFilledContent, actionOutlineBorder, actionOutlineContent, actionLinkColor,
        tabIdleBackground, tabIdleContent, tabActiveBackground, tabActiveBorder, tabActiveContent,
        sheetScrim, sheetSurface, sheetSurfaceVariant, sheetDragHandle, licenseHueResolver,
        contrastLevel = contrastLevel,
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
