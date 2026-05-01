package com.mikepenz.aboutlibraries.ui.compose.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

/**
 * Resolves an accent [Color] for a given license SPDX identifier (e.g. `Apache-2.0`).
 *
 * Used by the Refined variant to color the per-row license dot, the inline license label,
 * and the license tab strip in the header. The core defines only the resolver interface;
 * adapter modules supply concrete maps with hex values appropriate for the design.
 *
 * Returning `null` means the license should fall back to the row's default subtle content color.
 */
@Stable
fun interface LicenseHueResolver {
    fun colorFor(spdxId: String?): Color?

    companion object {
        /** Resolver that always returns null — every license falls back to the default content color. */
        val None: LicenseHueResolver = LicenseHueResolver { null }
    }
}

/** Build a case-insensitive [LicenseHueResolver] backed by the supplied SPDX → [Color] map. */
fun LicenseHueResolver(palette: Map<String, Color>): LicenseHueResolver =
    MapLicenseHueResolver(palette)

@Immutable
internal class MapLicenseHueResolver(palette: Map<String, Color>) : LicenseHueResolver {
    private val normalized: Map<String, Color> = palette.mapKeys { it.key.lowercase() }
    override fun colorFor(spdxId: String?): Color? {
        if (spdxId.isNullOrBlank()) return null
        return normalized[spdxId.lowercase()]
    }
}
