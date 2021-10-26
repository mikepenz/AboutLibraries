package com.mikepenz.aboutlibraries.plugin.mapping

import com.mikepenz.aboutlibraries.plugin.util.toMD5

/**
 * License class describing a license and its information
 */
data class License(
    val name: String,
    var url: String?,
    var year: String? = null,
    var remoteLicense: String? = null
) {
    val remoteLicenseHash: String?
        get() = remoteLicense?.toMD5()

    val spdxId: String?
        get() = resolveLicenseId(name, url)

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


/**
 * Ensures and applies fixes to the library names (shorten, ...)
 */
private fun resolveLicenseId(name: String, url: String?): String? {
    for (l: SpdxLicense in SpdxLicense.values()) {
        val matcher = l.customMatcher
        if (l.id.equals(name, true) || l.name.equals(name, true) || l.fullName.equals(name, true) || (matcher != null && matcher.invoke(name, url))) {
            return l.name
        }
    }
    return null
}