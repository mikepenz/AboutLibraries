package com.mikepenz.aboutlibraries.plugin.mapping

/**
 * License class describing a license and its information
 */
data class License(
    val name: String,
    var url: String,
    var year: String? = null,
    var distribution: String? = null,
    var remoteLicense: String? = null,
    var spdxId: String? = null
) {
    constructor(lic: SpdxLicense) : this(lic.fullName, lic.getUrl(), spdxId = lic.id)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as License

        if (name != other.name) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }
}