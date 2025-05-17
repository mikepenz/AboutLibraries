package com.mikepenz.aboutlibraries.ui.compose.m3

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.focusable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.DefaultLibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibrariesScaffold
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
import com.mikepenz.aboutlibraries.ui.compose.LibraryTextStyles
import com.mikepenz.aboutlibraries.ui.compose.m3.component.LibraryChip
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.ui.compose.util.strippedLicenseContent
import kotlinx.collections.immutable.persistentListOf


/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun LibrariesContainer(
    libraries: Libs?,
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
    licenseDialogBody: (@Composable (Library) -> Unit)? = { library -> LicenseDialogBody(library = library, colors = colors) },
    licenseDialogConfirmText: String = "OK",
) {
    val uriHandler = LocalUriHandler.current
    val typography = MaterialTheme.typography
    val libs = libraries?.libraries ?: persistentListOf()
    val openDialog = remember { mutableStateOf<Library?>(null) }

    LibrariesScaffold(
        libraries = libs,
        modifier = modifier,
        libraryModifier = libraryModifier,
        lazyListState = lazyListState,
        contentPadding = contentPadding,
        padding = padding,
        dimensions = dimensions,
        name = { libraryName ->
            Text(
                text = libraryName,
                style = textStyles.nameTextStyle ?: typography.titleLarge,
                color = colors.contentColor,
                maxLines = textStyles.nameMaxLines,
                overflow = textStyles.nameOverflow,
            )
        },
        version = { version ->
            if (showVersion) {
                LibraryChip(
                    modifier = Modifier.padding(padding.versionPadding.containerPadding),
                ) {
                    Text(
                        modifier = Modifier.padding(padding.versionPadding.contentPadding),
                        text = version,
                        style = textStyles.versionTextStyle ?: typography.bodyMedium,
                        maxLines = textStyles.versionMaxLines,
                        textAlign = TextAlign.Center,
                        overflow = textStyles.defaultOverflow,
                    )
                }
            }
        },
        author = { author ->
            if (showAuthor && author.isNotBlank()) {
                Text(
                    text = author,
                    style = textStyles.authorTextStyle ?: typography.bodyMedium,
                    color = colors.contentColor,
                    maxLines = textStyles.authorMaxLines,
                    overflow = textStyles.defaultOverflow,
                )
            }
        },
        description = { description ->
            if (showDescription) {
                Text(
                    text = description,
                    style = textStyles.descriptionTextStyle ?: typography.bodySmall,
                    color = colors.contentColor,
                    maxLines = textStyles.descriptionMaxLines,
                    overflow = textStyles.defaultOverflow,
                )
            }
        },
        license = { license ->
            if (showLicenseBadges) {
                LibraryChip(
                    modifier = Modifier.padding(padding.licensePadding.containerPadding),
                    contentColor = colors.badgeContentColor,
                    containerColor = colors.badgeBackgroundColor
                ) {
                    Text(
                        modifier = Modifier.padding(padding.licensePadding.contentPadding),
                        maxLines = 1,
                        text = license.name,
                        style = textStyles.licensesTextStyle ?: LocalTextStyle.current,
                        textAlign = TextAlign.Center,
                        overflow = textStyles.defaultOverflow,
                    )
                }
            }
        },
        funding = { funding ->
            if (showFundingBadges) {
                LibraryChip(
                    modifier = Modifier.padding(padding.fundingPadding.containerPadding),
                    onClick = {
                        if (onFundingClick != null) {
                            onFundingClick(funding)
                        } else {
                            try {
                                uriHandler.openUri(funding.url)
                            } catch (t: Throwable) {
                                println("Failed to open funding url: ${funding.url} // ${t.message}")
                            }
                        }
                    },
                    contentColor = colors.fundingBadgeContentColor,
                    containerColor = colors.fundingBadgeBackgroundColor
                ) {
                    Text(
                        modifier = Modifier.padding(padding.fundingPadding.contentPadding),
                        maxLines = 1,
                        text = funding.platform,
                        style = textStyles.fundingTextStyle ?: LocalTextStyle.current,
                        textAlign = TextAlign.Center,
                        overflow = textStyles.defaultOverflow,
                    )
                }
            }
        },
        header = header,
        divider = divider,
        footer = footer,
        onLibraryClick = { library ->
            val license = library.licenses.firstOrNull()
            if (onLibraryClick != null) {
                onLibraryClick(library)
                true
            } else if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                openDialog.value = library
                true
            } else false
        },
    )

    val library = openDialog.value
    if (library != null && licenseDialogBody != null) {
        LicenseDialog(
            library = library,
            colors = colors,
            confirmText = licenseDialogConfirmText,
            body = licenseDialogBody
        ) {
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
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .indication(interactionSource, LocalIndication.current)
                            .focusable(interactionSource = interactionSource)
                            .verticalScroll(scrollState)
                            .padding(8.dp)
                            .weight(1f)
                    ) {
                        body(library)
                    }
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colors.dialogConfirmButtonColor,
                        )
                    ) {
                        Text(confirmText)
                    }
                }
            }
        },
    )
}

@Composable
internal fun LicenseDialogBody(library: Library, colors: LibraryColors, modifier: Modifier = Modifier) {
    val license = remember(library) { library.strippedLicenseContent.takeIf { it.isNotEmpty() } }
    if (license != null) {
        Text(
            text = license,
            modifier = modifier,
            color = colors.contentColor
        )
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
 * @param fundingBadgeBackgroundColor the funding badge background color of this [Library]
 * @param fundingBadgeContentColor the funding badge content color of this [Library]
 * @param dialogConfirmButtonColor the dialog's confirm button color of this [Library]
 */
@Composable
fun LibraryDefaults.libraryColors(
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(backgroundColor),
    badgeBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    badgeContentColor: Color = contentColorFor(badgeBackgroundColor),
    fundingBadgeBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    fundingBadgeContentColor: Color = contentColorFor(fundingBadgeBackgroundColor),
    dialogConfirmButtonColor: Color = MaterialTheme.colorScheme.primary,
): LibraryColors = DefaultLibraryColors(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    badgeBackgroundColor = badgeBackgroundColor,
    badgeContentColor = badgeContentColor,
    fundingBadgeBackgroundColor = fundingBadgeBackgroundColor,
    fundingBadgeContentColor = fundingBadgeContentColor,
    dialogConfirmButtonColor = dialogConfirmButtonColor,
)