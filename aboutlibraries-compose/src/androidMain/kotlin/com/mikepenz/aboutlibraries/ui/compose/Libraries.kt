package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.mikepenz.aboutlibraries.util.withContext

/**
 * Displays all provided libraries in a simple list.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LibrariesContainer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true
) {
    val libraries = remember { mutableStateOf<Libs?>(null) }
    val context = LocalContext.current
    LaunchedEffect(libraries) {
        libraries.value = Libs.Builder().withContext(context).build()
    }

    val libs = libraries.value?.libraries
    if (libs != null) {
        Libraries(
            libraries = libs,
            modifier,
            contentPadding,
            showAuthor,
            showVersion,
            showLicenseBadges
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
) {
    LazyColumn(modifier, contentPadding = contentPadding) {
        items(libraries) { library ->
            val openDialog = remember { mutableStateOf(false) }

            Library(library, showAuthor, showVersion, showLicenseBadges) {
                openDialog.value = true
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
                            .fillMaxSize()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = library.licenses.firstOrNull()?.licenseContent ?: "",
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

/*
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
 */