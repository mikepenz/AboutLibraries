package com.mikepenz.aboutlibraries.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.mikepenz.aboutlibraries.sample.m2.M2AppTheme
import com.mikepenz.aboutlibraries.sample.m3.M3AppTheme

@Composable
fun AppTheme(
    useV3: Boolean,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    if (useV3) {
        M3AppTheme(useDarkTheme, content)
    } else {
        M2AppTheme(useDarkTheme, content)
    }
}