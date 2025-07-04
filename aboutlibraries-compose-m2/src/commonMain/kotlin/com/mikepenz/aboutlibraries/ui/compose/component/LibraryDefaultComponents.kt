package com.mikepenz.aboutlibraries.ui.compose.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
import com.mikepenz.aboutlibraries.ui.compose.LibraryShapes
import com.mikepenz.aboutlibraries.ui.compose.LibraryTextStyles

internal val DefaultLibraryName: @Composable BoxScope.(name: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: androidx.compose.material.Typography) -> Unit =
    { libraryName, textStyles, colors, typography ->
        Text(
            text = libraryName,
            style = textStyles.nameTextStyle ?: typography.h6,
            color = colors.libraryContentColor,
            maxLines = textStyles.nameMaxLines,
            overflow = textStyles.nameOverflow,
        )
    }

internal val DefaultLibraryVersion: @Composable BoxScope.(version: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: androidx.compose.material.Typography, padding: LibraryPadding, dimensions: LibraryDimensions, shapes: LibraryShapes) -> Unit =
    { version, textStyles, colors, typography, padding, dimensions, shapes ->
        LibraryChip(
            modifier = Modifier.padding(padding.versionPadding.containerPadding),
            minHeight = dimensions.chipMinHeight,
            backgroundColor = colors.versionChipColors.containerColor,
            contentColor = colors.versionChipColors.contentColor,
            shape = shapes.chipShape,
        ) {
            Text(
                modifier = Modifier.padding(padding.versionPadding.contentPadding),
                text = version,
                style = textStyles.versionTextStyle ?: typography.body2,
                maxLines = textStyles.versionMaxLines,
                textAlign = TextAlign.Center,
                overflow = textStyles.defaultOverflow,
            )
        }
    }

internal val DefaultLibraryAuthor: @Composable BoxScope.(author: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: androidx.compose.material.Typography) -> Unit =
    { author, textStyles, colors, typography ->
        Text(
            text = author,
            style = textStyles.authorTextStyle ?: typography.body2,
            color = colors.libraryContentColor,
            maxLines = textStyles.authorMaxLines,
            overflow = textStyles.defaultOverflow,
        )
    }

internal val DefaultLibraryDescription: @Composable BoxScope.(description: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: androidx.compose.material.Typography) -> Unit =
    { description, textStyles, colors, typography ->
        Text(
            text = description,
            style = textStyles.descriptionTextStyle ?: typography.caption,
            color = colors.libraryContentColor,
            maxLines = textStyles.descriptionMaxLines,
            overflow = textStyles.defaultOverflow,
        )
    }

internal val DefaultLibraryLicense: @Composable FlowRowScope.(license: License, textStyles: LibraryTextStyles, colors: LibraryColors, padding: LibraryPadding, dimensions: LibraryDimensions, shapes: LibraryShapes) -> Unit =
    { license, textStyles, colors, padding, dimensions, shapes ->
        LibraryChip(
            modifier = Modifier.padding(padding.licensePadding.containerPadding),
            minHeight = dimensions.chipMinHeight,
            backgroundColor = colors.licenseChipColors.containerColor,
            contentColor = colors.licenseChipColors.contentColor,
            shape = shapes.chipShape,
        ) {
            Text(
                modifier = Modifier.padding(padding.licensePadding.contentPadding),
                text = license.name,
                style = textStyles.licensesTextStyle ?: LocalTextStyle.current,
                textAlign = TextAlign.Center,
                overflow = textStyles.defaultOverflow,
                maxLines = 1,
            )
        }
    }

internal val DefaultLibraryFunding: @Composable FlowRowScope.(funding: Funding, textStyles: LibraryTextStyles, colors: LibraryColors, padding: LibraryPadding, dimensions: LibraryDimensions, shapes: LibraryShapes, onFundingClick: ((Funding) -> Unit)?) -> Unit =
    { funding, textStyles, colors, padding, dimensions, shapes, onFundingClick ->
        val uriHandler = LocalUriHandler.current
        LibraryChip(
            modifier = Modifier.padding(padding.fundingPadding.containerPadding).pointerHoverIcon(PointerIcon.Hand),
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
            minHeight = dimensions.chipMinHeight,
            backgroundColor = colors.fundingChipColors.containerColor,
            contentColor = colors.fundingChipColors.contentColor,
            shape = shapes.chipShape,
        ) {
            Text(
                modifier = Modifier.padding(padding.fundingPadding.contentPadding),
                text = funding.platform,
                style = textStyles.fundingTextStyle ?: LocalTextStyle.current,
                textAlign = TextAlign.Center,
                overflow = textStyles.defaultOverflow,
                maxLines = 1,
            )
        }
    }
