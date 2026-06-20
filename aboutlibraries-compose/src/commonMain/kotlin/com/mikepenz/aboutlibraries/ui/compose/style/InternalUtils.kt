package com.mikepenz.aboutlibraries.ui.compose.style

import androidx.compose.ui.graphics.Color

internal fun Color.orFallback(fallback: Color): Color =
    if (this == Color.Unspecified) fallback else this
