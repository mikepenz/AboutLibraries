package com.mikepenz.aboutlibraries.ui.compose.m3

import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors

@androidx.compose.runtime.Composable
actual fun LicenseDialogBody(
    library: Library,
    colors: LibraryColors,
    modifier: Modifier,
) = DefaultLicenseDialogBody(
    library = library,
    colors = colors,
    modifier = modifier,
)