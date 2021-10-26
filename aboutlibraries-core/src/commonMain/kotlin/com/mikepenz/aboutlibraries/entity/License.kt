package com.mikepenz.aboutlibraries.entity

/**
 * License class describing a license and its information
 */
data class License(
    val name: String,
    val url: String?,
    val year: String? = null,
    val spdxId: String? = null,
    val licenseContent: String? = null,
    val hash: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as License

        if (hash != other.hash) return false

        return true
    }

    override fun hashCode(): Int {
        return hash.hashCode()
    }
}