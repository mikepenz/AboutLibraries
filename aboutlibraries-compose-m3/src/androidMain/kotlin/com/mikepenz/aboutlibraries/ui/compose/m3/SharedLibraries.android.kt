package com.mikepenz.aboutlibraries.ui.compose.m3

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent

@Composable
actual fun LicenseDialogBody(
    library: Library,
    colors: LibraryColors,
    modifier: Modifier,
) {
    val license = remember(library) {
        library.htmlReadyLicenseContent
            .takeIf { it.isNotEmpty() }
            ?.let { AnnotatedString.fromHtml(it) }
    }
    if (license != null) {
        Text(
            text = license,
            modifier = modifier,
            color = colors.dialogContentColor
        )
    }
}