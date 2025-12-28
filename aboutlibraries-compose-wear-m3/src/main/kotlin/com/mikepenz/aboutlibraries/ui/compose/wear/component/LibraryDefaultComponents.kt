package com.mikepenz.aboutlibraries.ui.compose.wear.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material3.LocalContentColor
import androidx.wear.compose.material3.LocalTextStyle
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.Typography
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
import com.mikepenz.aboutlibraries.ui.compose.LibraryShapes
import com.mikepenz.aboutlibraries.ui.compose.LibraryTextStyles

internal val WearDefaultLibraryName: @Composable BoxScope.(name: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: Typography) -> Unit =
    { libraryName, textStyles, colors, typography ->
        Text(
            text = libraryName,
            style = textStyles.nameTextStyle ?: LocalTextStyle.current,
            color = colors.libraryContentColor,
            maxLines = textStyles.nameMaxLines,
            overflow = textStyles.nameOverflow,
        )
    }

internal val WearDefaultLibraryVersion: @Composable BoxScope.(version: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: Typography, padding: LibraryPadding, dimensions: LibraryDimensions, shapes: LibraryShapes) -> Unit =
    { version, textStyles, colors, typography, padding, dimensions, shapes ->
        CompositionLocalProvider(
            LocalContentColor provides colors.versionChipColors.contentColor,
        ) {
            Text(
                modifier = Modifier.padding(padding.versionPadding.contentPadding),
                text = version,
                style = textStyles.versionTextStyle ?: LocalTextStyle.current,
                maxLines = textStyles.versionMaxLines,
                textAlign = TextAlign.Center,
                overflow = textStyles.defaultOverflow,
            )
        }
    }

internal val WearDefaultLibraryAuthor: @Composable BoxScope.(author: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: Typography) -> Unit =
    { author, textStyles, colors, typography ->
        Text(
            text = author,
            style = textStyles.authorTextStyle ?: LocalTextStyle.current,
            color = colors.libraryContentColor,
            maxLines = textStyles.authorMaxLines,
            overflow = textStyles.defaultOverflow,
        )
    }

internal val WearDefaultLibraryDescription: @Composable BoxScope.(description: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: Typography) -> Unit =
    { description, textStyles, colors, typography ->
        Text(
            text = description,
            style = textStyles.descriptionTextStyle ?: LocalTextStyle.current,
            color = colors.libraryContentColor,
            maxLines = textStyles.descriptionMaxLines,
            overflow = textStyles.defaultOverflow,
        )
    }

internal val WearDefaultLibraryLicense: @Composable FlowRowScope.(license: License, textStyles: LibraryTextStyles, colors: LibraryColors, padding: LibraryPadding, dimensions: LibraryDimensions, shapes: LibraryShapes) -> Unit =
    { license, textStyles, colors, padding, dimensions, shapes ->
        CompositionLocalProvider(
            LocalContentColor provides colors.licenseChipColors.contentColor,
        ) {
            Text(
                modifier = Modifier
                    .padding(padding.licensePadding.containerPadding)
                    .padding(padding.licensePadding.contentPadding),
                maxLines = 1,
                text = license.name,
                style = textStyles.licensesTextStyle ?: LocalTextStyle.current,
                textAlign = TextAlign.Center,
                overflow = textStyles.defaultOverflow,
            )
        }
    }
