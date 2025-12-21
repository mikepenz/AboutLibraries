package com.mikepenz.aboutlibraries.sample.m2

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Green,
    primaryVariant = GreenDark,
    onPrimary = Color.White,
    secondary = Green,
    onSecondary = Color.White,
    error = Red200
)

private val LightColorPalette = lightColors(
    primary = Green,
    primaryVariant = GreenDark,
    onPrimary = Color.White,
    secondary = Green,
    secondaryVariant = GreenDark,
    onSecondary = Color.White,
    error = Red800
)

@Composable
fun M2AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}
