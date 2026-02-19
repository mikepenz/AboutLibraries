package com.mikepenz.aboutlibraries.ui.compose.wear

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.wear.compose.foundation.lazy.ScalingLazyListItemScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.LocalContentColor
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TextButtonDefaults
import androidx.wear.compose.material3.contentColorFor
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.ChipColors
import com.mikepenz.aboutlibraries.ui.compose.DefaultChipColors
import com.mikepenz.aboutlibraries.ui.compose.DefaultLibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults.chipPadding
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
import com.mikepenz.aboutlibraries.ui.compose.LibraryShapes
import com.mikepenz.aboutlibraries.ui.compose.LibraryTextStyles
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.ui.compose.wear.component.WearDefaultLibraryAuthor
import com.mikepenz.aboutlibraries.ui.compose.wear.component.WearDefaultLibraryDescription
import com.mikepenz.aboutlibraries.ui.compose.wear.component.WearDefaultLibraryLicense
import com.mikepenz.aboutlibraries.ui.compose.wear.component.WearDefaultLibraryName
import com.mikepenz.aboutlibraries.ui.compose.wear.component.WearDefaultLibraryVersion
import com.mikepenz.aboutlibraries.ui.compose.wear.component.WearLibrariesScaffold

/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun LibrariesContainer(
    libraries: Libs?,
    modifier: Modifier = Modifier,
    libraryModifier: Modifier = Modifier,
    lazyListState: ScalingLazyListState = rememberScalingLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showDescription: Boolean = false,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    typography: androidx.wear.compose.material3.Typography = MaterialTheme.typography,
    colors: LibraryColors = LibraryDefaults.libraryColors(
        versionChipColors = LibraryDefaults.chipColors(contentColor = MaterialTheme.colorScheme.primary),
        licenseChipColors = LibraryDefaults.chipColors(contentColor = MaterialTheme.colorScheme.primary),
    ),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(
        versionPadding = chipPadding(contentPadding = PaddingValues(0.dp)),
        licensePadding = chipPadding(contentPadding = PaddingValues(0.dp))
    ),
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(
        itemSpacing = 4.dp
    ),
    textStyles: LibraryTextStyles = LibraryDefaults.libraryTextStyles(
        nameTextStyle = typography.titleMedium,
        authorTextStyle = typography.titleSmall,
        versionTextStyle = typography.titleSmall,
        descriptionTextStyle = typography.bodySmall,
        licensesTextStyle = typography.labelMedium,
    ),
    shapes: LibraryShapes = LibraryDefaults.libraryShapes(),
    onLibraryClick: ((Library) -> Unit)? = null,
    name: @Composable BoxScope.(name: String) -> Unit = { WearDefaultLibraryName(it, textStyles, colors, typography) },
    version: (@Composable BoxScope.(version: String) -> Unit)? = { version ->
        if (showVersion) WearDefaultLibraryVersion(version, textStyles, colors, typography, padding, dimensions, shapes)
    },
    author: (@Composable BoxScope.(authors: String) -> Unit)? = { author ->
        if (showAuthor && author.isNotBlank()) WearDefaultLibraryAuthor(author, textStyles, colors, typography)
    },
    description: (@Composable BoxScope.(description: String) -> Unit)? = { description ->
        if (showDescription) WearDefaultLibraryDescription(description, textStyles, colors, typography)
    },
    license: (@Composable FlowRowScope.(license: License) -> Unit)? = { license ->
        if (showLicenseBadges) WearDefaultLibraryLicense(license, textStyles, colors, padding, dimensions, shapes)
    },
    header: (ScalingLazyListScope.() -> Unit)? = null,
    divider: (@Composable ScalingLazyListItemScope.() -> Unit)? = null,
    footer: (ScalingLazyListScope.() -> Unit)? = null,
    licenseDialogBody: (@Composable (Library, Modifier) -> Unit)? = { library, modifier -> LicenseDialogBody(library = library, colors = colors, modifier = modifier) },
    licenseDialogConfirmText: String = "OK",
) {
    val libs = libraries?.libraries.orEmpty()
    val openDialog = remember { mutableStateOf<Library?>(null) }

    WearLibrariesScaffold(
        libraries = libs,
        modifier = modifier,
        libraryModifier = libraryModifier.background(colors.libraryBackgroundColor),
        lazyListState = lazyListState,
        contentPadding = contentPadding,
        padding = padding,
        dimensions = dimensions,
        name = name,
        version = version,
        author = author,
        description = description,
        license = license,
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
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(colors.dialogBackgroundColor, MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                CompositionLocalProvider(LocalContentColor provides colors.dialogContentColor) {
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
                            colors = TextButtonDefaults.textButtonColors(
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

@Composable
fun LicenseDialogBody(
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

/**
 * Creates a [LibraryColors] that represents the default colors used in a [Library].
 *
 * @param libraryBackgroundColor the background color of this [Library]
 * @param libraryContentColor the content color of this [Library]
 * @param versionChipColors the colors used for the version chip
 * @param licenseChipColors the colors used for the license chip
 * @param fundingChipColors the colors used for the funding chip
 * @param dialogBackgroundColor the dialog's background color of this [Library]
 * @param dialogContentColor the dialog's content color of this [Library]
 * @param dialogConfirmButtonColor the dialog's confirm button color of this [Library]
 */
@Composable
fun LibraryDefaults.libraryColors(
    libraryBackgroundColor: Color = MaterialTheme.colorScheme.background,
    libraryContentColor: Color = contentColorFor(libraryBackgroundColor),
    versionChipColors: ChipColors = chipColors(containerColor = libraryBackgroundColor),
    licenseChipColors: ChipColors = chipColors(),
    fundingChipColors: ChipColors = chipColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = contentColorFor(MaterialTheme.colorScheme.secondary),
    ),
    dialogBackgroundColor: Color = libraryBackgroundColor,
    dialogContentColor: Color = contentColorFor(dialogBackgroundColor),
    dialogConfirmButtonColor: Color = MaterialTheme.colorScheme.primary,
): LibraryColors = DefaultLibraryColors(
    libraryBackgroundColor = libraryBackgroundColor,
    libraryContentColor = libraryContentColor,
    versionChipColors = versionChipColors,
    licenseChipColors = licenseChipColors,
    fundingChipColors = fundingChipColors,
    dialogBackgroundColor = dialogBackgroundColor,
    dialogContentColor = dialogContentColor,
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