package com.mikepenz.aboutlibraries.entity

/**
 * Describes a complete [License] element.
 * Either retrieved from spdx or downloaded from the artifacts repo
 *
 * @param name of the given license
 * @param url linking to the hosted form of this license
 * @param year if available for this license (not contained in the `pom.xml`)
 * @param spdxId for this library, if it is a standard library available
 * @param licenseContent contains the whole license content as downloaded from the server
 * @param hash usually calculated to identify if a license is re-used and can be used for multiple artifacts
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