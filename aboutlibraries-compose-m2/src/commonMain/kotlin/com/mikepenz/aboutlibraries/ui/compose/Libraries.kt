package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun LibrariesContainer(
    aboutLibsJson: String,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showDescription: Boolean = false,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    itemContentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    itemSpacing: Dp = LibraryDefaults.LibraryItemSpacing,
    header: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    val libs = Libs.Builder().withJson(aboutLibsJson).build()
    LibrariesContainer(
        libs,
        modifier = modifier,
        lazyListState = lazyListState,
        contentPadding = contentPadding,
        showAuthor = showAuthor,
        showDescription = showDescription,
        showVersion = showVersion,
        showLicenseBadges = showLicenseBadges,
        colors = colors,
        padding = padding,
        itemContentPadding = itemContentPadding,
        itemSpacing = itemSpacing,
        header = header,
        onLibraryClick = onLibraryClick,
        licenseDialogBody = { library ->
            Text(library.licenses.firstOrNull()?.licenseContent ?: "")
        })
}

/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun LibrariesContainer(
    librariesBlock: () -> Libs,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showDescription: Boolean = false,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    itemContentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    itemSpacing: Dp = LibraryDefaults.LibraryItemSpacing,
    header: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    val libs = librariesBlock()

    LibrariesContainer(
        libraries = libs,
        modifier = modifier,
        lazyListState = lazyListState,
        contentPadding = contentPadding,
        showAuthor = showAuthor,
        showDescription = showDescription,
        showVersion = showVersion,
        showLicenseBadges = showLicenseBadges,
        colors = colors,
        padding = padding,
        itemContentPadding = itemContentPadding,
        itemSpacing = itemSpacing,
        header = header,
        onLibraryClick = onLibraryClick,
        licenseDialogBody = { library ->
            Text(library.licenses.firstOrNull()?.licenseContent ?: "")
        }
    )
}

/**
 * Creates a State<Libs?> that holds the [Libs] as loaded by the [libraries].
 *
 * @see Libs
 */
@Composable
fun rememberLibraries(
    libraries: ByteArray,
): State<Libs?> = rememberLibraries {
    libraries.decodeToString()
}

/**
 * Creates a State<Libs?> that holds the [Libs] as loaded by the [block].
 *
 * @see Libs
 */
@Composable
fun rememberLibraries(
    block: suspend () -> String,
): State<Libs?> {
    return produceState<Libs?>(initialValue = null) {
        value = withContext(Dispatchers.Default) {
            Libs.Builder().withJson(block()).build()
        }
    }
}
