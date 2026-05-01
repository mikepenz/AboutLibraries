package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.runtime.Immutable

/** Visual treatment for the libraries list. */
enum class LibrariesVariant {
    /** Vertical Material 3 list: title + trailing version, author, description, license chips. */
    Traditional,

    /** Dense two-line table-like row: license dot + name + version + chevron, author · license-label. */
    Refined,
}

/** Vertical density of each library row. */
enum class LibrariesDensity { Cozy, Compact }

/** How a library's detail content is presented when the user opens an item. */
enum class LibraryDetailMode {
    /** No expanded detail — clicks delegate to the click handler only. */
    None,

    /** Expand inline below the row. */
    Inline,

    /** Open in a modal bottom sheet — caller renders the sheet via the `onSheetRequest` callback. */
    Sheet,
}

/** How action affordances (source / website / sponsor / view license) render. */
enum class LibraryActionMode {
    /** Pill-shaped action chips (default for Traditional). */
    Chips,

    /** Compact icon buttons. */
    Icons,

    /** Underlined text links (default for Refined inline expansion). */
    Links,
}

/** Kinds of action affordances surfaced for a library. */
enum class LibraryActionKind { Source, Website, Sponsor, License }

/** Per-row badge / metadata visibility configuration. */
@Immutable
data class LibraryBadges(
    val version: Boolean = true,
    val author: Boolean = true,
    val description: Boolean = false,
    val license: Boolean = true,
)

/** Singleton default — used as the parameter default to avoid per-recomposition allocations. */
val DefaultLibraryBadges: LibraryBadges = LibraryBadges()
