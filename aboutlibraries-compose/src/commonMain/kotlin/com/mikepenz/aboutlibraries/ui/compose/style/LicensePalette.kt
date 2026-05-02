package com.mikepenz.aboutlibraries.ui.compose.style

import androidx.compose.ui.graphics.Color

/**
 * An ordered list of colours used as a cycling fallback for unknown license SPDX identifiers.
 *
 * When a [LicenseHueResolver] does not recognise a license ID it picks a colour at
 * `|hash(spdxId)| % palette.size`, so every unknown license always gets the same colour and
 * different unknown licenses are visually distinct. Supply any non-empty [List<Color>] — the
 * library ships four ready-made palettes below.
 */
typealias LicensePalette = List<Color>

/**
 * 12-colour palette for **light** surfaces at **normal** contrast (WCAG AA, ≥4.5:1 vs white).
 *
 * Hues spread evenly around the wheel (violet → blue → sky → teal → green → lime →
 * amber → orange → red → rose → fuchsia → slate) using Tailwind CSS 700-shade values.
 */
val LightNormalLicensePalette: LicensePalette = listOf(
    Color(0xFF6D28D9), // violet-700
    Color(0xFF1D4ED8), // blue-700
    Color(0xFF0369A1), // sky-700
    Color(0xFF0F766E), // teal-700
    Color(0xFF15803D), // green-700
    Color(0xFF4D7C0F), // lime-700
    Color(0xFFB45309), // amber-700
    Color(0xFFC2410C), // orange-700
    Color(0xFFB91C1C), // red-700
    Color(0xFFBE185D), // rose-700
    Color(0xFFA21CAF), // fuchsia-700
    Color(0xFF334155), // slate-700
)

/**
 * 12-colour palette for **light** surfaces at **high** contrast (WCAG AAA, ≥7:1 vs white).
 *
 * Same hue identities as [LightNormalLicensePalette] but darker (Tailwind 900 shades).
 */
val LightHighContrastLicensePalette: LicensePalette = listOf(
    Color(0xFF4C1D95), // violet-900
    Color(0xFF1E3A8A), // blue-900
    Color(0xFF0C4A6E), // sky-900
    Color(0xFF134E4A), // teal-900
    Color(0xFF14532D), // green-900
    Color(0xFF365314), // lime-900
    Color(0xFF78350F), // amber-900
    Color(0xFF7C2D12), // orange-900
    Color(0xFF7F1D1D), // red-900
    Color(0xFF881337), // rose-900
    Color(0xFF701A75), // fuchsia-900
    Color(0xFF0F172A), // slate-900
)

/**
 * 12-colour palette for **dark** surfaces at **normal** contrast (≥4.5:1 vs dark background).
 *
 * Pastel/bright tones (Tailwind 300 shades) that remain readable on dark surfaces.
 */
val DarkNormalLicensePalette: LicensePalette = listOf(
    Color(0xFFC4B5FD), // violet-300
    Color(0xFF93C5FD), // blue-300
    Color(0xFF7DD3FC), // sky-300
    Color(0xFF5EEAD4), // teal-300
    Color(0xFF86EFAC), // green-300
    Color(0xFFD9F99D), // lime-200  (slightly subdued vs lime-300)
    Color(0xFFFCD34D), // amber-300
    Color(0xFFFCA5A5), // red-300
    Color(0xFFFDA4AF), // rose-300
    Color(0xFFF9A8D4), // pink-300
    Color(0xFFF0ABFC), // fuchsia-300
    Color(0xFF94A3B8), // slate-400
)

/**
 * 12-colour palette for **dark** surfaces at **high** contrast (≥7:1 vs dark background).
 *
 * Near-white pastels (Tailwind 200 shades) for maximum readability on dark backgrounds.
 */
val DarkHighContrastLicensePalette: LicensePalette = listOf(
    Color(0xFFDDD6FE), // violet-200
    Color(0xFFBFDBFE), // blue-200
    Color(0xFFBAE6FD), // sky-200
    Color(0xFF99F6E4), // teal-200
    Color(0xFFBBF7D0), // green-200
    Color(0xFFD9F99D), // lime-200
    Color(0xFFFDE68A), // amber-200
    Color(0xFFFECACA), // red-200
    Color(0xFFFECDD3), // rose-200
    Color(0xFFFBCFE8), // pink-200
    Color(0xFFF5D0FE), // fuchsia-200
    Color(0xFFCBD5E1), // slate-200
)

/** Returns the appropriate default [LicensePalette] for the given surface and contrast settings. */
fun defaultLicensePalette(isDark: Boolean, contrastLevel: ContrastLevel = ContrastLevel.Normal): LicensePalette =
    when {
        isDark && contrastLevel == ContrastLevel.High -> DarkHighContrastLicensePalette
        isDark -> DarkNormalLicensePalette
        contrastLevel == ContrastLevel.High -> LightHighContrastLicensePalette
        else -> LightNormalLicensePalette
    }
