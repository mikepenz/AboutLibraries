package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionBadges
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
    actionLabels: LibraryActionBadges,
    contentPadding: PaddingValues = style.padding.inlineDetailPadding,
    onActionClick: ((Library, LibraryActionKind) -> Boolean)? = null,
    onDialogRequest: ((Library) -> Unit)? = null,
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
            onLicenseContentRequest = onDialogRequest,
        )
    }
}

/**
 * Sheet body rendered inside a modal bottom sheet — title, meta line, description, license body
 * and action bar. The wrapping [LibraryDetailSheet] adapter (M3) supplies the sheet shell.
 *
 * Consumes the bottom + horizontal system-bar and display-cutout insets on its own content so the
 * sheet surface can render edge-to-edge (behind the gesture indicator, side nav bar, and notch)
 * while text stays clear of them. The top inset is intentionally excluded — the wrapping sheet's
 * drag handle owns it — so handle and content cover disjoint sides with no double padding. This
 * holds across portrait, landscape, and reverse-portrait, where the bars/cutout move between edges.
 * The inset scrolls with the content, so the last item can be scrolled fully clear of the bars.
 */
@Composable
fun LibrarySheetDetail(
    library: Library,
    actionMode: LibraryActionMode,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
    actionLabels: LibraryActionBadges,
    contentPadding: PaddingValues = style.padding.sheetPadding,
    onActionClick: ((Library, LibraryActionKind) -> Boolean)? = null,
) {
    val licenseContent = remember(library) { library.strippedLicenseContent.takeIf { it.isNotBlank() } }
    val onBg = style.colors.rowOnBackground.orFallback(Color.Black)
    val subtle = style.colors.rowSubtleContent.orFallback(onBg.copy(alpha = 0.6f))
    val sheetVariantBg = style.colors.sheetSurfaceVariant.orFallback(onBg.copy(alpha = 0.08f))

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val scrollModifier = if (constraints.hasBoundedHeight) Modifier.verticalScroll(rememberScrollState()) else Modifier
        Column(
            modifier = Modifier.fillMaxWidth().then(scrollModifier)
                .windowInsetsPadding(
                    WindowInsets.systemBars.union(WindowInsets.displayCutout)
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
                )
                .padding(contentPadding),
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

            LibraryActions(
                library = library,
                actionMode = actionMode,
                style = style,
                actionLabels = actionLabels,
                onActionClick = onActionClick,
            )

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
        }
    }
}
