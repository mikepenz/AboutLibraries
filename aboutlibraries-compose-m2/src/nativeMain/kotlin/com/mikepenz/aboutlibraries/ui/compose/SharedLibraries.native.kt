package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.entity.Library

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