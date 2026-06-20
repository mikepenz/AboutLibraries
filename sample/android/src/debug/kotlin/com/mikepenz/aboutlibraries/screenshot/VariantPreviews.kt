package com.mikepenz.aboutlibraries.screenshot

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.entity.Scm
import com.mikepenz.aboutlibraries.sample.AppTheme
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantColors
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.style.ContrastLevel
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultLibraryActionBadges
import com.mikepenz.aboutlibraries.ui.compose.style.librariesStyle
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActions
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryDetailMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesVariant
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryInlineDetail
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryRow
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrarySheetDetail
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryListScaffold
import com.mikepenz.aboutlibraries.ui.compose.variant.refined.RefinedRow
import com.mikepenz.aboutlibraries.ui.compose.variant.traditional.TraditionalRow

private val SampleLibrary: Library = Library(
    uniqueId = "com.mikepenz:aboutlibraries",
    artifactVersion = "11.2.0",
    name = "AboutLibraries",
    description = "Automatically collects dependencies and licenses and provides a themed Compose component to show them to the user.",
    website = "https://github.com/mikepenz/AboutLibraries",
    developers = listOf(
        com.mikepenz.aboutlibraries.entity.Developer(
            name = "Mike Penz",
            organisationUrl = "https://mikepenz.dev",
        ),
    ),
    organization = null,
    scm = Scm(
        url = "https://github.com/mikepenz/AboutLibraries",
        connection = null,
        developerConnection = null,
    ),
    licenses = setOf(
        License(name = "Apache 2.0", url = null, year = null, spdxId = "Apache-2.0", licenseContent = null, hash = "apl"),
    ),
    funding = setOf(Funding(platform = "GitHub Sponsors", url = "https://github.com/sponsors/mikepenz")),
)

private val MitLibrary: Library = SampleLibrary.copy(
    uniqueId = "com.example:mit-lib",
    name = "MIT Sample Lib",
    artifactVersion = "1.0.0",
    licenses = setOf(License("MIT", null, null, "MIT", null, "mit")),
    description = "An example library released under the MIT license — used to show hue variation in the row.",
)

@Composable
private fun WithStyle(content: @Composable (LibrariesStyle) -> Unit) {
    val style = LibraryDefaults.librariesStyle(
        colors = LibraryDefaults.m3VariantColors(),
        textStyles = LibraryDefaults.m3VariantTextStyles(),
    )
    content(style)
}

private val AllBadges = LibraryBadges(version = true, author = true, description = true, license = true)
private val MinimalBadges = LibraryBadges(version = true, author = false, description = false, license = false)

// ── Traditional row matrix ────────────────────────────────────────────────────

@Preview(name = "Traditional · Cozy · collapsed", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalCozyCollapsed() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
        }
    }
}

@Preview(name = "Traditional · Compact · collapsed", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalCompactCollapsed() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Compact, badges = AllBadges, style = style)
        }
    }
}

@Preview(name = "Traditional · Cozy · minimal badges", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalCozyMinimal() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = MinimalBadges, style = style)
        }
    }
}

@Preview(name = "Traditional · Inline expanded · Chips", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalExpandedChips() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                TraditionalRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    LibraryActions(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
                }
            }
        }
    }
}

@Preview(name = "Traditional · Inline expanded · Icons", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalExpandedIcons() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                TraditionalRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    LibraryActions(SampleLibrary, LibraryActionMode.Icons, style, actionLabels = DefaultLibraryActionBadges)
                }
            }
        }
    }
}

@Preview(name = "Traditional · Inline expanded · Links", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalExpandedLinks() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                TraditionalRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    LibraryActions(SampleLibrary, LibraryActionMode.Links, style, actionLabels = DefaultLibraryActionBadges)
                }
            }
        }
    }
}

// ── Refined row matrix ────────────────────────────────────────────────────────

@Preview(name = "Refined · Cozy · collapsed", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedCozyCollapsed() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            RefinedRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
        }
    }
}

@Preview(name = "Refined · Compact · collapsed", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedCompactCollapsed() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            RefinedRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Compact, badges = AllBadges, style = style)
        }
    }
}

@Preview(name = "Refined · Inline expanded · Links", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedExpandedLinks() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                LibraryInlineDetail(SampleLibrary, LibraryActionMode.Links, style, actionLabels = DefaultLibraryActionBadges)
            }
        }
    }
}

@Preview(name = "Refined · Inline expanded · Chips", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedExpandedChips() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                LibraryInlineDetail(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
            }
        }
    }
}

@Preview(name = "Refined · Inline expanded · Icons", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedExpandedIcons() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                LibraryInlineDetail(SampleLibrary, LibraryActionMode.Icons, style, actionLabels = DefaultLibraryActionBadges)
            }
        }
    }
}

// ── Stacked rows (mixed licenses, shows hue resolver) ─────────────────────────

@Preview(name = "Refined · stacked rows · mixed licenses", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedStack() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                RefinedRow(MitLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
            }
        }
    }
}

// ── Detail sheet body (action mode matrix) ────────────────────────────────────

@Preview(name = "Sheet body · Chips", showBackground = true, backgroundColor = 0xFF272529)
@Composable
fun PreviewSheetChips() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
        }
    }
}

@Preview(name = "Sheet body · Icons", showBackground = true, backgroundColor = 0xFF272529)
@Composable
fun PreviewSheetIcons() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Icons, style, actionLabels = DefaultLibraryActionBadges)
        }
    }
}

@Preview(name = "Sheet body · Links", showBackground = true, backgroundColor = 0xFF272529)
@Composable
fun PreviewSheetLinks() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Links, style, actionLabels = DefaultLibraryActionBadges)
        }
    }
}

// ── Standalone LibraryActions previews ────────────────────────────────────────

@Preview(name = "Actions · Chips", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewActionsChips() = AppTheme(useV3 = true, useDarkTheme = true) {
    WithStyle { style ->
        Box(Modifier.width(360.dp).padding(16.dp).background(MaterialTheme.colorScheme.surface)) {
            LibraryActions(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
        }
    }
}

@Preview(name = "Actions · Icons", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewActionsIcons() = AppTheme(useV3 = true, useDarkTheme = true) {
    WithStyle { style ->
        Box(Modifier.width(360.dp).padding(16.dp).background(MaterialTheme.colorScheme.surface)) {
            LibraryActions(SampleLibrary, LibraryActionMode.Icons, style, actionLabels = DefaultLibraryActionBadges)
        }
    }
}

@Preview(name = "Actions · Links", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewActionsLinks() = AppTheme(useV3 = true, useDarkTheme = true) {
    WithStyle { style ->
        Box(Modifier.width(360.dp).padding(16.dp).background(MaterialTheme.colorScheme.surface)) {
            LibraryActions(SampleLibrary, LibraryActionMode.Links, style, actionLabels = DefaultLibraryActionBadges)
        }
    }
}

// ── Light theme — shows accent-derived badge + dot colors ────────────────────

@Preview(name = "Traditional · light · badge colors", showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewTraditionalLightBadgeColors() = AppTheme(useV3 = true, useDarkTheme = false) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                TraditionalRow(MitLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
            }
        }
    }
}

@Preview(name = "Refined · light · badge colors", showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewRefinedLightBadgeColors() = AppTheme(useV3 = true, useDarkTheme = false) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                RefinedRow(MitLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
            }
        }
    }
}

// ── LibraryListScaffold inline-expand (expanded end state) ───────────────────

@Preview(name = "Scaffold · Traditional · inline expanded", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewScaffoldTraditionalInlineExpanded() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).height(320.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            LibraryListScaffold(
                libraries = listOf(MitLibrary, SampleLibrary),
                expandedLibraryId = SampleLibrary.uniqueId,
                onExpandedLibraryIdChange = {},
                row = { _, library, expanded, toggle ->
                    Column {
                        TraditionalRow(library, expanded, toggle, LibrariesDensity.Cozy, AllBadges, style)
                        if (expanded) {
                            LibraryInlineDetail(library, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
                        }
                    }
                },
                detailMode = LibraryDetailMode.Inline,
            )
        }
    }
}

@Preview(name = "Scaffold · Refined · inline expanded", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewScaffoldRefinedInlineExpanded() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).height(280.dp).background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            LibraryListScaffold(
                libraries = listOf(MitLibrary, SampleLibrary),
                expandedLibraryId = SampleLibrary.uniqueId,
                onExpandedLibraryIdChange = {},
                row = { _, library, expanded, toggle ->
                    Column {
                        RefinedRow(library, expanded, toggle, LibrariesDensity.Cozy, AllBadges, style)
                        if (expanded) {
                            LibraryInlineDetail(library, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
                        }
                    }
                },
                detailMode = LibraryDetailMode.Inline,
            )
        }
    }
}

// ── Sheet detail — reordered (description + actions first, license last) ──────

@Preview(name = "Sheet body · reordered · dark", showBackground = true, backgroundColor = 0xFF272529)
@Composable
fun PreviewSheetReordered() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
        }
    }
}

@Preview(name = "Sheet body · reordered · light", showBackground = true, backgroundColor = 0xFFECE6F0)
@Composable
fun PreviewSheetReorderedLight() = AppTheme(useV3 = true, useDarkTheme = false) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
        }
    }
}

// ── High contrast ──────────────────────────────────────────────────────────

@Preview(name = "Traditional · high contrast · dark", showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalHighContrastDark() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        val style = LibraryDefaults.librariesStyle(
            colors = LibraryDefaults.m3VariantColors(contrastLevel = ContrastLevel.High),
            textStyles = LibraryDefaults.m3VariantTextStyles(),
        )
        Column {
            TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
            TraditionalRow(MitLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
        }
    }
}

// ── LibraryRow (default item renderer) ───────────────────────────────────────
// Covers the public `LibraryRow` slot composable: the per-item wrapper (item animation +
// expanded background), variant dispatch, inline-detail expansion, and the `modifier` hook.
//
// `LibraryRow` is a `LazyItemScope` extension. A real `LazyColumn` fills the canvas height (and
// collapses width) under the screenshot scanner's `RenderingMode.SHRINK`, producing narrow,
// over-tall captures. Hosting it in a fixed-width `Box` via a no-op `LazyItemScope` keeps the
// capture sized to the row content (item-placement animation is inert in a static snapshot).

private object PreviewLazyItemScope : LazyItemScope {
    override fun Modifier.fillParentMaxSize(fraction: Float) = this
    override fun Modifier.fillParentMaxWidth(fraction: Float) = this
    override fun Modifier.fillParentMaxHeight(fraction: Float) = this
    override fun Modifier.animateItem(
        fadeInSpec: FiniteAnimationSpec<Float>?,
        placementSpec: FiniteAnimationSpec<IntOffset>?,
        fadeOutSpec: FiniteAnimationSpec<Float>?,
    ) = this
}

@Composable
private fun RowHost(
    dark: Boolean = true,
    content: @Composable LazyItemScope.(LibrariesStyle) -> Unit,
) = AppTheme(useV3 = true, useDarkTheme = dark) {
    WithStyle { style ->
        Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
            with(PreviewLazyItemScope) { content(style) }
        }
    }
}

@Preview(name = "LibraryRow · Traditional · collapsed", showBackground = true)
@Composable
fun PreviewLibraryRowTraditionalCollapsed() = RowHost { style ->
    LibraryRow(
        library = SampleLibrary,
        expanded = false,
        onToggle = {},
        style = style,
        variant = LibrariesVariant.Traditional,
        badges = AllBadges,
    )
}

@Preview(name = "LibraryRow · Traditional · expanded (bg + inline)", showBackground = true)
@Composable
fun PreviewLibraryRowTraditionalExpanded() = RowHost { style ->
    LibraryRow(
        library = SampleLibrary,
        expanded = true,
        onToggle = {},
        style = style,
        variant = LibrariesVariant.Traditional,
        badges = AllBadges,
        expandedBackground = MaterialTheme.colorScheme.surfaceContainerHigh,
        inlineDetail = { library ->
            Box(Modifier.padding(style.padding.inlineActionsPadding)) {
                LibraryActions(library, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
            }
        },
    )
}

@Preview(name = "LibraryRow · Refined · collapsed", showBackground = true)
@Composable
fun PreviewLibraryRowRefinedCollapsed() = RowHost { style ->
    LibraryRow(
        library = SampleLibrary,
        expanded = false,
        onToggle = {},
        style = style,
        variant = LibrariesVariant.Refined,
        badges = AllBadges,
    )
}

@Preview(name = "LibraryRow · Refined · expanded (bg + inline)", showBackground = true)
@Composable
fun PreviewLibraryRowRefinedExpanded() = RowHost { style ->
    LibraryRow(
        library = SampleLibrary,
        expanded = true,
        onToggle = {},
        style = style,
        variant = LibrariesVariant.Refined,
        badges = AllBadges,
        expandedBackground = MaterialTheme.colorScheme.surfaceContainerHigh,
        inlineDetail = { library ->
            LibraryInlineDetail(library, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionBadges)
        },
    )
}

@Preview(name = "LibraryRow · modifier override (padding)", showBackground = true)
@Composable
fun PreviewLibraryRowModifierOverride() = RowHost { style ->
    LibraryRow(
        library = SampleLibrary,
        expanded = false,
        onToggle = {},
        style = style,
        modifier = Modifier.padding(horizontal = 24.dp),
        variant = LibrariesVariant.Traditional,
        badges = AllBadges,
    )
}

@Preview(name = "Traditional · high contrast · light", showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewTraditionalHighContrastLight() = AppTheme(useV3 = true, useDarkTheme = false) {
    Box(Modifier.width(360.dp).background(MaterialTheme.colorScheme.surface)) {
        val style = LibraryDefaults.librariesStyle(
            colors = LibraryDefaults.m3VariantColors(contrastLevel = ContrastLevel.High),
            textStyles = LibraryDefaults.m3VariantTextStyles(),
        )
        Column {
            TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
            TraditionalRow(MitLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
        }
    }
}
