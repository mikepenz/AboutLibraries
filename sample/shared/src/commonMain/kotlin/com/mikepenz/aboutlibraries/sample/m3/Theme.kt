package com.mikepenz.aboutlibraries.sample.m3

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

/**
 * Builds a dynamic ColorScheme derived from [accent], matching the design's
 * `m3Palette(accent, mode)` function:
 *   - Primary family: lerp-mixed from accent toward the dark/light base.
 *   - Surface family: ALL surface tones receive a small accent tint (3–7% in dark,
 *     2–5% in light), matching the design's `color-mix(in oklch, accent N%, base)`.
 */
private fun ColorScheme.withAccent(accent: Color, dark: Boolean): ColorScheme {
    // Primary family
    val onPrimary = if (dark) Color(0xFF1B0C1A) else Color.White
    val primaryContainer = if (dark) lerp(Color(0xFF1B0C1A), accent, 0.40f)
    else lerp(Color.White, accent, 0.25f)
    val onPrimaryContainer = if (dark) lerp(Color.White, accent, 0.15f)
    else lerp(Color.Black, accent, 0.40f)

    // Surface family — accent-tinted per design's `color-mix(in oklch, accent N%, base)`
    // dark bases from #141218 family; light bases from #fef7ff family
    val surface = lerp(
        if (dark) Color(0xFF141218) else Color(0xFFFEF7FF), accent, if (dark) 0.03f else 0.02f,
    )
    val surfaceContainer = lerp(
        if (dark) Color(0xFF1D1B20) else Color(0xFFF3EDF7), accent, if (dark) 0.05f else 0.03f,
    )
    val surfaceContainerLow = lerp(
        if (dark) Color(0xFF1A181D) else Color(0xFFF7F2FA), accent, if (dark) 0.04f else 0.02f,
    )
    val surfaceContainerHigh = lerp(
        if (dark) Color(0xFF272529) else Color(0xFFECE6F0), accent, if (dark) 0.06f else 0.04f,
    )
    val surfaceContainerHighest = lerp(
        if (dark) Color(0xFF322F35) else Color(0xFFE6E0E9), accent, if (dark) 0.07f else 0.05f,
    )

    return copy(
        primary = accent,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        surfaceTint = accent,
        inversePrimary = if (dark) accent else lerp(Color.White, accent, 0.85f),
        surface = surface,
        background = surface,
        surfaceContainer = surfaceContainer,
        surfaceContainerLow = surfaceContainerLow,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest,
    )
}

@Composable
fun M3AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    accent: Color = Color.Unspecified,
    content: @Composable () -> Unit,
) {
    val base = if (!useDarkTheme) LightColors else DarkColors
    val colors = if (accent != Color.Unspecified) base.withAccent(accent, useDarkTheme) else base

    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}