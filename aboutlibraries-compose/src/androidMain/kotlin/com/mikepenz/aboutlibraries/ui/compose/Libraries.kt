package com.mikepenz.aboutlibraries.ui.compose

import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.data.fakeData
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.util.withContext

/**
 * Displays all provided libraries in a simple list.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LibrariesContainer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    librariesBlock: (Context) -> Libs = { context ->
        Libs.Builder().withContext(context).build()
    },
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    onLibraryClick: ((Library) -> Unit)? = null
) {
    val libraries = remember { mutableStateOf<Libs?>(null) }

    val context = LocalContext.current
    LaunchedEffect(libraries) {
        libraries.value = librariesBlock.invoke(context)
    }

    val libs = libraries.value?.libraries
    if (libs != null) {
        Libraries(
            libraries = libs,
            modifier,
            contentPadding,
            showAuthor,
            showVersion,
            showLicenseBadges,
            onLibraryClick
        )
    }
}

/**
 * Displays all provided libraries in a simple list.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Libraries(
    libraries: List<Library>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    onLibraryClick: ((Library) -> Unit)? = null
) {
    LazyColumn(modifier, contentPadding = contentPadding) {
        items(libraries) { library ->
            val openDialog = remember { mutableStateOf(false) }

            Library(library, showAuthor, showVersion, showLicenseBadges) {
                if (onLibraryClick != null) {
                    onLibraryClick.invoke(library)
                } else {
                    openDialog.value = true
                }
            }

            if (openDialog.value) {
                val scrollState = rememberScrollState()
                Dialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .verticalScroll(scrollState)
                            .fillMaxSize(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colors.surface,
                        contentColor = contentColorFor(MaterialTheme.colors.surface),
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            HtmlText(
                                library.licenses.firstOrNull()?.htmlReadyLicenseContent ?: ""
                            )
                            TextButton(
                                onClick = { openDialog.value = false },
                                modifier = Modifier.align(End)
                            ) {
                                Text(stringResource(id = R.string.aboutlibs_ok))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context) },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
    )
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