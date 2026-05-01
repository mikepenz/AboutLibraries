package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionLabels
import com.mikepenz.aboutlibraries.ui.compose.style.orFallback
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.mikepenz.aboutlibraries.ui.compose.util.strippedLicenseContent

/**
 * Inline body rendered beneath an expanded row — description (when present) and the action bar.
 * No title or sheet padding — that's the [LibrarySheetDetail] composable's job.
 */
@Composable
fun LibraryInlineDetail(
    library: Library,
    actionMode: LibraryActionMode,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
    actionLabels: LibraryActionLabels,
    contentPadding: PaddingValues = style.padding.inlineDetailPadding,
    onActionClick: ((Library, LibraryActionKind) -> Unit)? = null,
) {
    val onBg = style.colors.rowOnBackground.orFallback(Color.Black)

    Column(
        modifier = modifier.fillMaxWidth().padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val description = library.description
        if (!description.isNullOrBlank()) {
            BasicText(text = description, style = style.textStyles.descriptionTextStyle.copy(color = onBg))
        }
        LibraryActions(
            library = library,
            actionMode = actionMode,
            style = style,
            actionLabels = actionLabels,
            onActionClick = onActionClick,
        )
    }
}

/**
 * Sheet body rendered inside a modal bottom sheet — title, meta line, description, license body
 * and action bar. The wrapping [LibraryDetailSheet] adapter (M3) supplies the sheet shell.
 */
@Composable
fun LibrarySheetDetail(
    library: Library,
    actionMode: LibraryActionMode,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
    actionLabels: LibraryActionLabels,
    contentPadding: PaddingValues = style.padding.sheetPadding,
    onActionClick: ((Library, LibraryActionKind) -> Unit)? = null,
) {
    val licenseContent = remember(library) { library.strippedLicenseContent.takeIf { it.isNotBlank() } }
    val onBg = style.colors.rowOnBackground.orFallback(Color.Black)
    val subtle = style.colors.rowSubtleContent.orFallback(onBg.copy(alpha = 0.6f))
    val sheetVariantBg = style.colors.sheetSurfaceVariant.orFallback(onBg.copy(alpha = 0.08f))

    Column(
        modifier = modifier.fillMaxWidth().padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicText(text = library.name, style = style.textStyles.sheetTitleTextStyle.copy(color = onBg))

        val author = library.author
        val version = library.artifactVersion
        if (author.isNotBlank() || !version.isNullOrBlank()) {
            val meta = remember(author, version) {
                listOfNotNull(author.takeIf { it.isNotBlank() }, version).joinToString(" · ")
            }
            BasicText(text = meta, style = style.textStyles.sheetMetaTextStyle.copy(color = subtle))
        }

        val description = library.description
        if (!description.isNullOrBlank()) {
            BasicText(text = description, style = style.textStyles.sheetBodyTextStyle.copy(color = onBg))
        }

        if (licenseContent != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(style.shapes.sheetLicenseShape)
                    .background(sheetVariantBg)
                    .padding(14.dp),
            ) {
                BasicText(text = licenseContent, style = style.textStyles.sheetBodyTextStyle.copy(color = subtle))
            }
        }

        LibraryActions(
            library = library,
            actionMode = actionMode,
            style = style,
            actionLabels = actionLabels,
            onActionClick = onActionClick,
        )
    }
}
