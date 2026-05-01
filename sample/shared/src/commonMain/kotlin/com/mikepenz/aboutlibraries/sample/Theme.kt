package com.mikepenz.aboutlibraries.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mikepenz.aboutlibraries.sample.m2.M2AppTheme
import com.mikepenz.aboutlibraries.sample.m3.M3AppTheme

@Composable
fun AppTheme(
    useV3: Boolean,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    accent: Color = Color.Unspecified,
    content: @Composable () -> Unit,
) {
    if (useV3) {
        M3AppTheme(useDarkTheme, accent, content)
    } else {
        M2AppTheme(useDarkTheme, content)
    }
}