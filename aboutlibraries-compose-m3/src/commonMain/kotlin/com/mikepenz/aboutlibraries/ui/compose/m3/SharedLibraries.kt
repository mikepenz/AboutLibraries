package com.mikepenz.aboutlibraries.ui.compose.m3

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
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
import com.mikepenz.aboutlibraries.ui.compose.LibraryShapes
import com.mikepenz.aboutlibraries.ui.compose.LibraryTextStyles
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionKind
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryDetailMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesVariant
import com.mikepenz.aboutlibraries.ui.compose.variant.Libraries
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges
import com.mikepenz.aboutlibraries.ui.compose.style.ContrastLevel
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.VariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.defaultVariantDimensions
import com.mikepenz.aboutlibraries.ui.compose.style.defaultVariantPadding
import com.mikepenz.aboutlibraries.ui.compose.style.defaultVariantShapes
import com.mikepenz.aboutlibraries.ui.compose.style.librariesStyle
import com.mikepenz.aboutlibraries.ui.compose.m3.sheet.LibraryDetailSheet
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantColors
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryAuthor
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryDescription
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryFunding
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryLicense
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryName
import com.mikepenz.aboutlibraries.ui.compose.m3.component.DefaultLibraryVersion
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.ui.compose.util.strippedLicenseContent

/**
 * Displays all provided libraries in a simple list.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
    variant: LibrariesVariant = LibrariesVariant.Traditional,
    density: LibrariesDensity = LibrariesDensity.Cozy,
    detailMode: LibraryDetailMode = LibraryDetailMode.Inline,
    actionMode: LibraryActionMode = LibraryActionMode.Chips,
    variantColors: VariantColors? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
    onFundingClick: ((Funding) -> Unit)? = null,
    onActionClick: ((Library, LibraryActionKind) -> Unit)? = null,
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
    val libs = libraries?.libraries.orEmpty()
    val openDialog = remember { mutableStateOf<Library?>(null) }
    val openSheet = remember { mutableStateOf<Library?>(null) }

    val variantPadding = remember(variant) {
        when (variant) {
            LibrariesVariant.Traditional -> LibraryDefaults.defaultVariantPadding()
            LibrariesVariant.Refined -> LibraryDefaults.defaultVariantPadding(
                rowPaddingCozy = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                rowPaddingCompact = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                rowHorizontal = 16.dp,
            )
        }
    }
    val variantDimensions = remember(variant) {
        LibraryDefaults.defaultVariantDimensions(
            headerIconSize = if (variant == LibrariesVariant.Refined) 32.dp else 48.dp,
            searchHeight = if (variant == LibrariesVariant.Refined) 34.dp else 44.dp,
        )
    }
    val variantShapes = remember(variant) {
        when (variant) {
            LibrariesVariant.Traditional -> LibraryDefaults.defaultVariantShapes()
            LibrariesVariant.Refined -> LibraryDefaults.defaultVariantShapes(
                headerSearchShape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            )
        }
    }
    val style: LibrariesStyle = LibraryDefaults.librariesStyle(
        colors = variantColors ?: LibraryDefaults.m3VariantColors(),
        padding = variantPadding,
        dimensions = variantDimensions,
        textStyles = LibraryDefaults.m3VariantTextStyles(),
        shapes = variantShapes,
    )

    // Unused legacy slot lambdas — kept on the API for source compat. The new variant rows
    // render their own content via theme-agnostic primitives. Reference them so the IDE shows
    // the parameters as in-use until they are removed in a future major version.
    @Suppress("UNUSED_EXPRESSION") run { name; version; author; description; license; funding; actions; padding; dimensions; textStyles; shapes; typography; onFundingClick }

    Libraries(
        libraries = libs,
        style = style,
        modifier = modifier.background(colors.libraryBackgroundColor),
        variant = variant,
        density = density,
        detailMode = detailMode,
        actionMode = actionMode,
        badges = LibraryBadges(
            version = showVersion,
            author = showAuthor,
            description = showDescription,
            license = showLicenseBadges,
        ),
        contentPadding = contentPadding,
        state = lazyListState,
        header = header,
        divider = divider,
        footer = footer,
        onActionClick = onActionClick,
        onLibraryClick = { library ->
            if (onLibraryClick != null) {
                onLibraryClick(library)
                true
            } else false
        },
        onSheetRequest = { openSheet.value = it },
    )
    @Suppress("UNUSED_EXPRESSION") libraryModifier

    val dialogLibrary = openDialog.value
    if (dialogLibrary != null && licenseDialogBody != null) {
        LicenseDialog(
            library = dialogLibrary,
            colors = colors,
            padding = padding,
            confirmText = licenseDialogConfirmText,
            body = licenseDialogBody
        ) {
            openDialog.value = null
        }
    }

    val sheetLibrary = openSheet.value
    if (sheetLibrary != null) {
        LibraryDetailSheet(
            library = sheetLibrary,
            onDismiss = { openSheet.value = null },
            style = style,
            actionMode = actionMode,
            onActionClick = onActionClick,
        )
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
                color = colors.dialogBackgroundColor,
                contentColor = colors.dialogContentColor,
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