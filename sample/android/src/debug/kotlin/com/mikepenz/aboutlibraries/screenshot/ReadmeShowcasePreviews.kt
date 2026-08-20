package com.mikepenz.aboutlibraries.screenshot

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.sample.AppTheme
import com.mikepenz.aboutlibraries.sample.sample.AccentSwatches
import com.mikepenz.aboutlibraries.sample.sample.SampleSettings
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantColors
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.style.librariesStyle
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesVariant
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActions
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrarySheetDetail

/**
 * Previews backing the README "Showcase" section.
 *
 * These deliberately do *not* reuse [VariantPreviews]: those pin a bare `AppTheme` with no accent,
 * which skips `withAccent(..)` and renders the stock M3 purple scheme on an untinted backdrop —
 * a different color scheme than the sample app ever shows. Everything here goes through the same
 * theme and the same `LibrariesContainer` arguments as `App`, so the README cannot show a look the
 * app does not produce.
 */
private val Defaults = SampleSettings()

@Composable
private fun ShowcaseFrame(
    background: @Composable () -> Color = { MaterialTheme.colorScheme.surface },
    content: @Composable () -> Unit,
) = AppTheme(useV3 = true, useDarkTheme = isSystemInDarkTheme(), accent = AccentSwatches[0]) {
    Surface(color = background()) { content() }
}

@Composable
private fun showcaseStyle() = LibraryDefaults.librariesStyle(
    colors = LibraryDefaults.m3VariantColors(),
    textStyles = LibraryDefaults.m3VariantTextStyles(),
)

/** Mirrors the `M3LibrariesContainer(..)` call in `App`, varying only the knob under test. */
@Composable
private fun ShowcaseList(
    variant: LibrariesVariant = Defaults.variant,
    density: LibrariesDensity = Defaults.density,
    height: Int = 300,
) = ShowcaseFrame {
    LibrariesContainer(
        libraries = fakeLibraries,
        modifier = Modifier.width(360.dp).height(height.dp),
        badges = Defaults.badges,
        actionLabels = Defaults.actionLabels,
        variant = variant,
        density = density,
        detailMode = Defaults.detailMode,
        actionMode = Defaults.actionMode,
        variantColors = LibraryDefaults.m3VariantColors(),
    )
}

@PreviewLightDark
@Composable
fun PreviewShowcaseVariantRefined() = ShowcaseList(variant = LibrariesVariant.Refined)

@PreviewLightDark
@Composable
fun PreviewShowcaseVariantTraditional() = ShowcaseList(variant = LibrariesVariant.Traditional)

@PreviewLightDark
@Composable
fun PreviewShowcaseDensityCompact() = ShowcaseList(density = LibrariesDensity.Compact)

@PreviewLightDark
@Composable
fun PreviewShowcaseDensityCozy() = ShowcaseList(density = LibrariesDensity.Cozy)

/**
 * Action modes only render on an expanded row, which a static container preview cannot reach —
 * so these show the actions strip on its own, in the same theme as the lists above.
 */
@Composable
private fun ShowcaseActions(mode: LibraryActionMode) = ShowcaseFrame {
    LibraryActions(
        fakeLibraries.libraries.first(),
        mode,
        showcaseStyle(),
        actionLabels = Defaults.actionLabels,
        modifier = Modifier.fillMaxWidth().width(360.dp).padding(16.dp),
    )
}

@PreviewLightDark
@Composable
fun PreviewShowcaseActionsChips() = ShowcaseActions(LibraryActionMode.Chips)

@PreviewLightDark
@Composable
fun PreviewShowcaseActionsIcons() = ShowcaseActions(LibraryActionMode.Icons)

/**
  * Detail-sheet body. Sits on `surfaceContainerHigh` because that is the sheet surface the
 * container itself uses ([LibraryDefaults.m3VariantColors]'s `sheetSurface`).
 */
@PreviewLightDark
@Composable
fun PreviewShowcaseDetailSheet() = ShowcaseFrame(background = { MaterialTheme.colorScheme.surfaceContainerHigh }) {
    LibrarySheetDetail(
        fakeLibraries.libraries.first(),
        Defaults.actionMode,
        showcaseStyle(),
        actionLabels = Defaults.actionLabels,
        modifier = Modifier.width(360.dp),
    )
}
