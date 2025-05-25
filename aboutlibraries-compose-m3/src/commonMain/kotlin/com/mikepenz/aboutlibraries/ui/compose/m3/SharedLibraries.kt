package com.mikepenz.aboutlibraries.ui.compose.m3

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.focusable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.ChipColors
import com.mikepenz.aboutlibraries.ui.compose.DefaultChipColors
import com.mikepenz.aboutlibraries.ui.compose.DefaultLibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibrariesScaffold
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
import com.mikepenz.aboutlibraries.ui.compose.LibraryShapes
import com.mikepenz.aboutlibraries.ui.compose.LibraryTextStyles
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryAuthor
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryDescription
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryFunding
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryLicense
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryName
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryVersion
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
    typography: Typography = MaterialTheme.typography,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(),
    textStyles: LibraryTextStyles = LibraryDefaults.libraryTextStyles(),
    shapes: LibraryShapes = LibraryDefaults.libraryShapes(),
    onLibraryClick: ((Library) -> Unit)? = null,
    onFundingClick: ((Funding) -> Unit)? = null,
    name: @Composable BoxScope.(name: String) -> Unit = { DefaultLibraryName(it, textStyles, colors, typography) },
    version: (@Composable BoxScope.(version: String) -> Unit)? = { version ->
        if (showVersion) DefaultLibraryVersion(version, textStyles, colors, typography, padding, dimensions, shapes)
    },
    author: (@Composable BoxScope.(authors: String) -> Unit)? = { author ->
        if (showAuthor && author.isNotBlank()) DefaultLibraryAuthor(author, textStyles, colors, typography)
    },
    description: (@Composable BoxScope.(description: String) -> Unit)? = { description ->
        if (showDescription) DefaultLibraryDescription(description, textStyles, colors, typography)
    },
    license: (@Composable FlowRowScope.(license: License) -> Unit)? = { license ->
        if (showLicenseBadges) DefaultLibraryLicense(license, textStyles, colors, padding, dimensions, shapes)
    },
    funding: (@Composable FlowRowScope.(funding: Funding) -> Unit)? = { funding ->
        if (showFundingBadges) DefaultLibraryFunding(funding, textStyles, colors, padding, dimensions, shapes, onFundingClick)
    },
    actions: (@Composable FlowRowScope.(library: Library) -> Unit)? = null,
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
    licenseDialogBody: (@Composable (Library, Modifier) -> Unit)? = { library, modifier -> LicenseDialogBody(library = library, colors = colors, modifier = modifier) },
    licenseDialogConfirmText: String = "OK",
) {
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
        name = name,
        version = version,
        author = author,
        description = description,
        license = license,
        funding = funding,
        actions = actions,
        header = header,
        divider = divider,
        footer = footer,
        onLibraryClick = { library ->
            if (onLibraryClick != null) {
                onLibraryClick(library)
                true
            } else {
                val license = library.licenses.firstOrNull()
                if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                    openDialog.value = library
                    true
                } else false
            }
        },
    )

    val library = openDialog.value
    if (library != null && licenseDialogBody != null) {
        LicenseDialog(
            library = library,
            colors = colors,
            padding = padding,
            confirmText = licenseDialogConfirmText,
            body = licenseDialogBody
        ) {
            openDialog.value = null
        }
    }
}

@Composable
fun LicenseDialog(
    library: Library,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    confirmText: String = "OK",
    body: @Composable (Library, Modifier) -> Unit,
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
                contentColor = colors.contentColor,
                modifier = Modifier.padding(8.dp),
            ) {
                Column {
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .indication(interactionSource, LocalIndication.current)
                            .focusable(interactionSource = interactionSource)
                            .weight(1f, fill = false)
                            .verticalScroll(scrollState)
                    ) {
                        body(library, Modifier.padding(padding.licenseDialogContentPadding))
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
expect fun LicenseDialogBody(library: Library, colors: LibraryColors, modifier: Modifier = Modifier)

@Composable
internal fun DefaultLicenseDialogBody(library: Library, colors: LibraryColors, modifier: Modifier = Modifier) {
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
 * @param dialogConfirmButtonColor the dialog's confirm button color of this [Library]
 */
@Deprecated("Use libraryColors() instead with `ChipColors` arguments.")
@Composable
fun LibraryDefaults.libraryColors(
    backgroundColor: Color,
    contentColor: Color = contentColorFor(backgroundColor),
    badgeBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    badgeContentColor: Color = contentColorFor(badgeBackgroundColor),
    dialogConfirmButtonColor: Color = MaterialTheme.colorScheme.primary,
): LibraryColors = libraryColors(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    versionChipColors = chipColors(containerColor = backgroundColor),
    licenseChipColors = chipColors(badgeBackgroundColor, badgeContentColor),
    dialogConfirmButtonColor = dialogConfirmButtonColor,
)

/**
 * Creates a [LibraryColors] that represents the default colors used in a [Library].
 *
 * @param backgroundColor the background color of this [Library]
 * @param contentColor the content color of this [Library]
 * @param versionChipColors the colors used for the version chip
 * @param licenseChipColors the colors used for the license chip
 * @param fundingChipColors the colors used for the funding chip
 * @param dialogConfirmButtonColor the dialog's confirm button color of this [Library]
 */
@Composable
fun LibraryDefaults.libraryColors(
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(backgroundColor),
    versionChipColors: ChipColors = chipColors(containerColor = backgroundColor),
    licenseChipColors: ChipColors = chipColors(),
    fundingChipColors: ChipColors = chipColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = contentColorFor(MaterialTheme.colorScheme.secondary),
    ),
    dialogConfirmButtonColor: Color = MaterialTheme.colorScheme.primary,
): LibraryColors = DefaultLibraryColors(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    versionChipColors = versionChipColors,
    licenseChipColors = licenseChipColors,
    fundingChipColors = fundingChipColors,
    dialogConfirmButtonColor = dialogConfirmButtonColor,
)

/**
 * Creates a [ChipColors] that represents the colors to use for a chip.
 */
@Composable
fun LibraryDefaults.chipColors(
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = contentColorFor(containerColor),
): ChipColors = DefaultChipColors(
    containerColor = containerColor,
    contentColor = contentColor,
)