package com.mikepenz.aboutlibraries.ui.compose.m3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.DefaultLibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun LibrariesContainer(
    libraries: Libs?,
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
    onLibraryClick: ((Library) -> Unit)? = null,
    licenseDialogBody: (@Composable (Library) -> Unit)? = null,
    licenseDialogConfirmText: String = "OK",
) {
    val uriHandler = LocalUriHandler.current

    val libs = libraries?.libraries ?: persistentListOf()
    val openDialog = remember { mutableStateOf<Library?>(null) }

    Libraries(
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
        header = header,
        onLibraryClick = { library ->
            val license = library.licenses.firstOrNull()
            if (onLibraryClick != null) {
                onLibraryClick(library)
            } else if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                openDialog.value = library
            } else if (!license?.url.isNullOrBlank()) {
                license?.url?.also {
                    try {
                        uriHandler.openUri(it)
                    } catch (t: Throwable) {
                        println("Failed to open url: ${it}")
                    }
                }
            }
        },
    )

    val library = openDialog.value
    if (library != null && licenseDialogBody != null) {
        LicenseDialog(library, colors, licenseDialogConfirmText, body = licenseDialogBody) {
            openDialog.value = null
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LicenseDialog(
    library: Library,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    confirmText: String = "OK",
    body: @Composable (Library) -> Unit,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(),
        content = {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = colors.backgroundColor,
                contentColor = colors.contentColor
            ) {
                Column {
                    FlowRow(
                        modifier = Modifier.verticalScroll(scrollState).padding(8.dp).weight(1f)
                    ) {
                        body(library)
                    }
                    FlowRow(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colors.dialogConfirmButtonColor,
                            )
                        ) {
                            Text(confirmText)
                        }
                    }
                }
            }
        },
    )
}

/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun Libraries(
    libraries: ImmutableList<Library>,
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
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
        state = lazyListState,
        contentPadding = contentPadding
    ) {
        header?.invoke(this)
        libraryItems(
            libraries = libraries,
            showAuthor = showAuthor,
            showDescription = showDescription,
            showVersion = showVersion,
            showLicenseBadges = showLicenseBadges,
            colors = colors,
            padding = padding,
        ) { library ->
            val license = library.licenses.firstOrNull()
            if (onLibraryClick != null) {
                onLibraryClick.invoke(library)
            } else if (!license?.url.isNullOrBlank()) {
                license.url?.also {
                    try {
                        uriHandler.openUri(it)
                    } catch (t: Throwable) {
                        println("Failed to open url: ${it}")
                    }
                }
            }
        }
    }
}

internal inline fun LazyListScope.libraryItems(
    libraries: ImmutableList<Library>,
    showAuthor: Boolean = true,
    showDescription: Boolean = false,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors,
    padding: LibraryPadding,
    crossinline onLibraryClick: ((Library) -> Unit),
) {
    items(libraries) { library ->
        Library(
            library = library,
            showAuthor = showAuthor,
            showDescription = showDescription,
            showVersion = showVersion,
            showLicenseBadges = showLicenseBadges,
            colors = colors,
            padding = padding,
        ) {
            onLibraryClick.invoke(library)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Library(
    library: Library,
    showAuthor: Boolean = true,
    showDescription: Boolean = false,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    typography: Typography = MaterialTheme.typography,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.backgroundColor)
            .clickable { onClick.invoke() }
            .padding(padding.contentPadding),
        verticalArrangement = Arrangement.spacedBy(padding.verticalPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = library.name,
                modifier = Modifier
                    .padding(padding.namePadding)
                    .weight(1f),
                style = typography.titleLarge,
                color = colors.contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val version = library.artifactVersion
            if (version != null && showVersion) {
                Text(
                    version,
                    modifier = Modifier.padding(padding.versionPadding),
                    style = typography.bodyMedium,
                    color = colors.contentColor,
                    textAlign = TextAlign.Center
                )
            }
        }
        val author = library.author
        if (showAuthor && author.isNotBlank()) {
            Text(
                text = author,
                style = typography.bodyMedium,
                color = colors.contentColor
            )
        }
        val description = library.description
        if (showDescription && !description.isNullOrBlank()) {
            Text(
                text = description,
                style = typography.bodySmall,
                color = colors.contentColor
            )
        }
        if (showLicenseBadges && library.licenses.isNotEmpty()) {
            FlowRow {
                library.licenses.forEach {
                    Badge(
                        modifier = Modifier.padding(padding.badgePadding),
                        contentColor = colors.badgeContentColor,
                        containerColor = colors.badgeBackgroundColor
                    ) {
                        Text(
                            modifier = Modifier.padding(padding.badgeContentPadding),
                            text = it.name
                        )
                    }
                }
            }
        }
    }
}

/**
 * Creates a [LibraryColors] that represents the default colors used in
 * a [Library].
 *
 * @param backgroundColor the background color of this [Library]
 * @param contentColor the content color of this [Library]
 * @param badgeBackgroundColor the badge background color of this [Library]
 * @param badgeContentColor the badge content color of this [Library]
 * @param dialogConfirmButtonColor the dialog's confirm button color of this [Library]
 */
@Composable
fun LibraryDefaults.libraryColors(
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(backgroundColor),
    badgeBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    badgeContentColor: Color = contentColorFor(badgeBackgroundColor),
    dialogConfirmButtonColor: Color = MaterialTheme.colorScheme.primary,
): LibraryColors = DefaultLibraryColors(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    badgeBackgroundColor = badgeBackgroundColor,
    badgeContentColor = badgeContentColor,
    dialogConfirmButtonColor = dialogConfirmButtonColor,
)