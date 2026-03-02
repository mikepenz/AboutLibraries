package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library

/**
 * Displays all provided libraries in a simple list.
 */
@Deprecated("Use `LibrariesContainer` variant with `Libs` instead. Use `produceLibraries` to load the libraries.")
@Composable
fun LibrariesContainer(
    aboutLibsJson: String,
    modifier: Modifier = Modifier,
    libraryModifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showDescription: Boolean = false,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    showFundingBadges: Boolean = false,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(),
    textStyles: LibraryTextStyles = LibraryDefaults.libraryTextStyles(),
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
    onFundingClick: ((Funding) -> Unit)? = null,
) {
    val libs by produceLibraries(aboutLibsJson)
    LibrariesContainer(
        libraries = libs,
        modifier = modifier,
        libraryModifier = libraryModifier,
        lazyListState = lazyListState,
        contentPadding = contentPadding,
        showAuthor = showAuthor,
        showDescription = showDescription,
        showVersion = showVersion,
        showLicenseBadges = showLicenseBadges,
        showFundingBadges = showFundingBadges,
        colors = colors,
        padding = padding,
        dimensions = dimensions,
        textStyles = textStyles,
        header = header,
        divider = divider,
        footer = footer,
        onLibraryClick = onLibraryClick,
        onFundingClick = onFundingClick,
    )
}

/**
 * Displays all provided libraries in a simple list.
 */
@Deprecated("Use `LibrariesContainer` variant with `Libs` instead. Use `produceLibraries` to load the libraries.")
@Composable
fun LibrariesContainer(
    librariesBlock: () -> Libs,
    modifier: Modifier = Modifier,
    libraryModifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showDescription: Boolean = false,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    showFundingBadges: Boolean = false,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(),
    textStyles: LibraryTextStyles = LibraryDefaults.libraryTextStyles(),
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
    onFundingClick: ((Funding) -> Unit)? = null,
) {
    val libs = librariesBlock()

    LibrariesContainer(
        libraries = libs,
        modifier = modifier,
        libraryModifier = libraryModifier,
        lazyListState = lazyListState,
        contentPadding = contentPadding,
        showAuthor = showAuthor,
        showDescription = showDescription,
        showVersion = showVersion,
        showLicenseBadges = showLicenseBadges,
        showFundingBadges = showFundingBadges,
        colors = colors,
        padding = padding,
        dimensions = dimensions,
        textStyles = textStyles,
        header = header,
        divider = divider,
        footer = footer,
        onLibraryClick = onLibraryClick,
        onFundingClick = onFundingClick,
    )
}