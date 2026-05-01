package com.mikepenz.aboutlibraries.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.entity.Scm
import com.mikepenz.aboutlibraries.sample.AppTheme
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantColors
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultLibraryActionLabels
import com.mikepenz.aboutlibraries.ui.compose.style.librariesStyle
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActions
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryDetailMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryInlineDetail
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

@Preview(name = "Traditional · Cozy · collapsed", widthDp = 600, heightDp = 130, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalCozyCollapsed() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
        }
    }
}

@Preview(name = "Traditional · Compact · collapsed", widthDp = 600, heightDp = 110, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalCompactCollapsed() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Compact, badges = AllBadges, style = style)
        }
    }
}

@Preview(name = "Traditional · Cozy · minimal badges", widthDp = 600, heightDp = 80, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalCozyMinimal() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = MinimalBadges, style = style)
        }
    }
}

@Preview(name = "Traditional · Inline expanded · Chips", widthDp = 600, heightDp = 260, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalExpandedChips() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                TraditionalRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    LibraryActions(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionLabels)
                }
            }
        }
    }
}

@Preview(name = "Traditional · Inline expanded · Icons", widthDp = 600, heightDp = 260, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalExpandedIcons() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                TraditionalRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    LibraryActions(SampleLibrary, LibraryActionMode.Icons, style, actionLabels = DefaultLibraryActionLabels)
                }
            }
        }
    }
}

@Preview(name = "Traditional · Inline expanded · Links", widthDp = 600, heightDp = 260, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewTraditionalExpandedLinks() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                TraditionalRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    LibraryActions(SampleLibrary, LibraryActionMode.Links, style, actionLabels = DefaultLibraryActionLabels)
                }
            }
        }
    }
}

// ── Refined row matrix ────────────────────────────────────────────────────────

@Preview(name = "Refined · Cozy · collapsed", widthDp = 600, heightDp = 80, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedCozyCollapsed() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            RefinedRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
        }
    }
}

@Preview(name = "Refined · Compact · collapsed", widthDp = 600, heightDp = 70, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedCompactCollapsed() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            RefinedRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Compact, badges = AllBadges, style = style)
        }
    }
}

@Preview(name = "Refined · Inline expanded · Links", widthDp = 600, heightDp = 200, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedExpandedLinks() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                LibraryInlineDetail(SampleLibrary, LibraryActionMode.Links, style, actionLabels = DefaultLibraryActionLabels)
            }
        }
    }
}

@Preview(name = "Refined · Inline expanded · Chips", widthDp = 600, heightDp = 200, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedExpandedChips() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                LibraryInlineDetail(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionLabels)
            }
        }
    }
}

@Preview(name = "Refined · Inline expanded · Icons", widthDp = 600, heightDp = 200, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedExpandedIcons() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = true, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                LibraryInlineDetail(SampleLibrary, LibraryActionMode.Icons, style, actionLabels = DefaultLibraryActionLabels)
            }
        }
    }
}

// ── Stacked rows (mixed licenses, shows hue resolver) ─────────────────────────

@Preview(name = "Refined · stacked rows · mixed licenses", widthDp = 600, heightDp = 200, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewRefinedStack() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                RefinedRow(MitLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
            }
        }
    }
}

// ── Detail sheet body (action mode matrix) ────────────────────────────────────

@Preview(name = "Sheet body · Chips", widthDp = 400, heightDp = 380, showBackground = true, backgroundColor = 0xFF272529)
@Composable
fun PreviewSheetChips() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionLabels)
        }
    }
}

@Preview(name = "Sheet body · Icons", widthDp = 400, heightDp = 380, showBackground = true, backgroundColor = 0xFF272529)
@Composable
fun PreviewSheetIcons() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Icons, style, actionLabels = DefaultLibraryActionLabels)
        }
    }
}

@Preview(name = "Sheet body · Links", widthDp = 400, heightDp = 380, showBackground = true, backgroundColor = 0xFF272529)
@Composable
fun PreviewSheetLinks() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Links, style, actionLabels = DefaultLibraryActionLabels)
        }
    }
}

// ── Standalone LibraryActions previews ────────────────────────────────────────

@Preview(name = "Actions · Chips", widthDp = 500, heightDp = 60, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewActionsChips() = AppTheme(useV3 = true, useDarkTheme = true) {
    WithStyle { style ->
        Box(Modifier.fillMaxWidth().padding(16.dp).background(MaterialTheme.colorScheme.surface)) {
            LibraryActions(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionLabels)
        }
    }
}

@Preview(name = "Actions · Icons", widthDp = 500, heightDp = 60, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewActionsIcons() = AppTheme(useV3 = true, useDarkTheme = true) {
    WithStyle { style ->
        Box(Modifier.fillMaxWidth().padding(16.dp).background(MaterialTheme.colorScheme.surface)) {
            LibraryActions(SampleLibrary, LibraryActionMode.Icons, style, actionLabels = DefaultLibraryActionLabels)
        }
    }
}

@Preview(name = "Actions · Links", widthDp = 500, heightDp = 60, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewActionsLinks() = AppTheme(useV3 = true, useDarkTheme = true) {
    WithStyle { style ->
        Box(Modifier.fillMaxWidth().padding(16.dp).background(MaterialTheme.colorScheme.surface)) {
            LibraryActions(SampleLibrary, LibraryActionMode.Links, style, actionLabels = DefaultLibraryActionLabels)
        }
    }
}

// ── Light theme — shows accent-derived badge + dot colors ────────────────────

@Preview(name = "Traditional · light · badge colors", widthDp = 600, heightDp = 130, showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewTraditionalLightBadgeColors() = AppTheme(useV3 = true, useDarkTheme = false) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                TraditionalRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                TraditionalRow(MitLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
            }
        }
    }
}

@Preview(name = "Refined · light · badge colors", widthDp = 600, heightDp = 160, showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewRefinedLightBadgeColors() = AppTheme(useV3 = true, useDarkTheme = false) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            Column {
                RefinedRow(SampleLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
                RefinedRow(MitLibrary, expanded = false, onToggle = {}, density = LibrariesDensity.Cozy, badges = AllBadges, style = style)
            }
        }
    }
}

// ── LibraryListScaffold inline-expand (expanded end state) ───────────────────

@Preview(name = "Scaffold · Traditional · inline expanded", widthDp = 600, heightDp = 320, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewScaffoldTraditionalInlineExpanded() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            LibraryListScaffold(
                libraries = listOf(MitLibrary, SampleLibrary),
                expandedLibraryId = SampleLibrary.uniqueId,
                onExpandedLibraryIdChange = {},
                row = { library, expanded, toggle ->
                    TraditionalRow(library, expanded, toggle, LibrariesDensity.Cozy, AllBadges, style)
                },
                detailMode = LibraryDetailMode.Inline,
                inlineDetail = { library ->
                    LibraryInlineDetail(library, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionLabels)
                },
            )
        }
    }
}

@Preview(name = "Scaffold · Refined · inline expanded", widthDp = 600, heightDp = 280, showBackground = true, backgroundColor = 0xFF141218)
@Composable
fun PreviewScaffoldRefinedInlineExpanded() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        WithStyle { style ->
            LibraryListScaffold(
                libraries = listOf(MitLibrary, SampleLibrary),
                expandedLibraryId = SampleLibrary.uniqueId,
                onExpandedLibraryIdChange = {},
                row = { library, expanded, toggle ->
                    RefinedRow(library, expanded, toggle, LibrariesDensity.Cozy, AllBadges, style)
                },
                detailMode = LibraryDetailMode.Inline,
                inlineDetail = { library ->
                    LibraryInlineDetail(library, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionLabels)
                },
            )
        }
    }
}

// ── Sheet detail — reordered (description + actions first, license last) ──────

@Preview(name = "Sheet body · reordered · dark", widthDp = 400, heightDp = 420, showBackground = true, backgroundColor = 0xFF272529)
@Composable
fun PreviewSheetReordered() = AppTheme(useV3 = true, useDarkTheme = true) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionLabels)
        }
    }
}

@Preview(name = "Sheet body · reordered · light", widthDp = 400, heightDp = 420, showBackground = true, backgroundColor = 0xFFECE6F0)
@Composable
fun PreviewSheetReorderedLight() = AppTheme(useV3 = true, useDarkTheme = false) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        WithStyle { style ->
            LibrarySheetDetail(SampleLibrary, LibraryActionMode.Chips, style, actionLabels = DefaultLibraryActionLabels)
        }
    }
}
