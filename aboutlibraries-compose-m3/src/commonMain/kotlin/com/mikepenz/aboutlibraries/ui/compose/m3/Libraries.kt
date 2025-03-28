package com.mikepenz.aboutlibraries.ui.compose.m3

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
import com.mikepenz.aboutlibraries.ui.compose.LibraryTextStyles
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries

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
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(),
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
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
        dimensions = dimensions,
        header = header,
        divider = divider,
        onLibraryClick = onLibraryClick,
        licenseDialogBody = { library ->
            Text(library.licenses.firstOrNull()?.licenseContent ?: "")
        }
    )
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
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(),
    textStyles: LibraryTextStyles = LibraryDefaults.libraryTextStyles(),
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
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
        dimensions = dimensions,
        textStyles = textStyles,
        header = header,
        divider = divider,
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
@Deprecated("Use rememberLibraries(libraries: ByteArray) instead", ReplaceWith("com.mikepenz.aboutlibraries.ui.compose.rememberLibraries(libraries)"))
@Composable
fun rememberLibraries(
    libraries: ByteArray,
) = rememberLibraries(libraries)

/**
 * Creates a State<Libs?> that holds the [Libs] as loaded by the [block].
 *
 * @see Libs
 */
@Deprecated("Use rememberLibraries(block: suspend () -> String) instead", ReplaceWith("com.mikepenz.aboutlibraries.ui.compose.rememberLibraries(block)"))
@Composable
fun rememberLibraries(
    block: suspend () -> String,
) = rememberLibraries(block)