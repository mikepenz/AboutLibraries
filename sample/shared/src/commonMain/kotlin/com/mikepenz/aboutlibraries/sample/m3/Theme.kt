package com.mikepenz.aboutlibraries.sample.m3

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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

/** Near-black used for content on top of a light accent. */
private val OnAccentDark = Color(0xFF141414)

/**
 * Builds a dynamic ColorScheme derived from [accent], matching the design's
 * `m3Palette(accent, mode)` function:
 *   - Primary family: lerp-mixed from accent toward the dark/light base.
 *   - Surface family: ALL surface tones receive a small accent tint (3–7% in dark,
 *     2–5% in light), matching the design's `color-mix(in oklch, accent N%, base)`.
 */
private fun ColorScheme.withAccent(accent: Color, dark: Boolean): ColorScheme {
    // Content drawn on top of [accent]. Derived from the accent's own luminance rather than from
    // the theme: a light accent (lime #C7FF1E sits at ~0.84) needs dark content in *both* themes,
    // and hardcoding white here made the filled action unreadable. 0.179 is the WCAG crossover
    // where black and white contrast equally.
    val onAccent = if (accent.luminance() > 0.179f) OnAccentDark else Color.White
    val primaryContainer = if (dark) lerp(Color(0xFF141414), accent, 0.40f)
    else lerp(Color.White, accent, 0.25f)
    val onPrimaryContainer = if (dark) lerp(Color.White, accent, 0.15f)
    else lerp(Color.Black, accent, 0.40f)

    // Surface family — accent-tinted per design's `color-mix(in oklch, accent N%, base)`, over a
    // neutral #1a1a1a / #ffffff family. The tint percentages are deliberately low: they were tuned
    // for a mid-luminance accent, and a highly saturated one (lime) turns the upper surface tones
    // visibly olive at the original 3–7%. Keep the surfaces reading as neutral grey.
    val tint = if (dark) 0.02f else 0.015f
    val surface = lerp(if (dark) Color(0xFF1A1A1A) else Color(0xFFFFFFFF), accent, tint)
    val surfaceContainerLow = lerp(if (dark) Color(0xFF1F1F1F) else Color(0xFFFAFAFA), accent, tint)
    val surfaceContainer = lerp(if (dark) Color(0xFF232323) else Color(0xFFF4F4F4), accent, tint)
    val surfaceContainerHigh = lerp(if (dark) Color(0xFF2B2B2B) else Color(0xFFEEEEEE), accent, tint)
    val surfaceContainerHighest = lerp(if (dark) Color(0xFF363636) else Color(0xFFE7E7E7), accent, tint)

    return copy(
        primary = accent,
        onPrimary = onAccent,
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
        // The stock on-surface tones are warm/pink-tinted, which reads as mauve over the neutral
        // surfaces above. Keep them neutral so the accent stays the only hue in the UI.
        onSurface = if (dark) Color(0xFFEDEDED) else Color(0xFF1A1A1A),
        onBackground = if (dark) Color(0xFFEDEDED) else Color(0xFF1A1A1A),
        onSurfaceVariant = if (dark) Color(0xFFB4B4B4) else Color(0xFF4A4A4A),
        outline = if (dark) Color(0xFF8A8A8A) else Color(0xFF7A7A7A),
        outlineVariant = if (dark) Color(0xFF3A3A3A) else Color(0xFFD6D6D6),
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