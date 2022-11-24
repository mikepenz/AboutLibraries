package com.mikepenz.aboutlibraries.ui.compose

import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.data.fakeData
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun LibrariesContainer(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    librariesBlock: (Context) -> Libs = { context ->
        Libs.Builder().withContext(context).build()
    },
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    itemContentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    header: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    val context = LocalContext.current
    val libraries = produceState<Libs?>(null) {
        value = withContext(Dispatchers.IO) {
            librariesBlock(context)
        }
    }

    val libs = libraries.value?.libraries
    if (libs != null) {
        val openDialog = remember { mutableStateOf<Library?>(null) }

        Libraries(
            libraries = libs,
            modifier = modifier,
            lazyListState = lazyListState,
            contentPadding = contentPadding,
            showAuthor = showAuthor,
            showVersion = showVersion,
            showLicenseBadges = showLicenseBadges,
            colors = colors,
            padding = padding,
            itemContentPadding = itemContentPadding,
            header = header,
            onLibraryClick = { library ->
                if (onLibraryClick != null) {
                    onLibraryClick(library)
                } else if (!library.licenses.firstOrNull()?.htmlReadyLicenseContent.isNullOrBlank()) {
                    openDialog.value = library
                }
            },
        )

        val library = openDialog.value
        if (library != null) {
            LicenseDialog(library = library, colors) {
                openDialog.value = null
            }
        }
    }
}

@Composable
fun LicenseDialog(
    library: Library,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()
    AlertDialog(
        backgroundColor = colors.backgroundColor,
        contentColor = colors.contentColor,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = android.R.string.ok))
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
            ) {
                HtmlText(
                    html = library.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty(),
                    color = colors.contentColor,
                )
            }
        },
    )
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    color: Color = LibraryDefaults.libraryColors().contentColor
) {
    AndroidView(modifier = modifier, factory = { context ->
        TextView(context).apply {
            setTextColor(color.toArgb())
        }
    }, update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) })
}

@Preview("Library items (Default)")
@Composable
fun PreviewLibraries() {
    MaterialTheme {
        Surface {
            Libraries(fakeData.libraries)
        }
    }
}


@Preview("Library items (Off)")
@Composable
fun PreviewLibrariesOff() {
    MaterialTheme {
        Surface {
            Libraries(fakeData.libraries, showAuthor = false, showLicenseBadges = false)
        }
    }
}

@Preview("Library item")
@Composable
fun PreviewLibrary() {
    MaterialTheme {
        Surface {
            Library(
                fakeData.libraries.first()
            ) {
                // on-click
            }
        }
    }
}
