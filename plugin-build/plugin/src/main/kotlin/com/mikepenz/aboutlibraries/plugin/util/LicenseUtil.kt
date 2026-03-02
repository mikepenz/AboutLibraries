package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

object LicenseUtil {
    private val LOGGER: Logger = LoggerFactory.getLogger(LicenseUtil::class.java)

    private val remoteLicenseCache = HashMap<String, String>()

    internal fun loadLicenseCached(url: String): String? {
        return try {
            if (remoteLicenseCache.containsKey(url)) {
                remoteLicenseCache[url]?.takeIf { it.isNotBlank() }
            } else {
                remoteLicenseCache[url] = ""
                URL(url).readText().let {
                    val trimmed = it.trimIndent() // cleanup empty lines before and after, and any indent.
                    remoteLicenseCache[url] = trimmed
                    trimmed
                }
            }
        } catch (t: Throwable) {
            null
        }
    }

    fun License.loadSpdxLicense(mapToSpdx: Boolean = true) {
        val spdxId = spdxId ?: return
        try {
            val enumLicense = SpdxLicense.find(spdxId)
            if (enumLicense != null) {
                val singleLicense: String? = loadLicenseCached(enumLicense.getTxtUrl()) ?: loadLicenseCached(enumLicense.getFallbackTxtUrl())
                if (singleLicense?.isNotBlank() == true) {
                    if (mapToSpdx || name.isBlank()) {
                        name = enumLicense.fullName
                    }
                    if (mapToSpdx || url.isNullOrBlank()) {
                        url = enumLicense.getUrl()
                    }
                    content = singleLicense
                }
            } else {
                LOGGER.info("`spdxId` did not match any known SpdxLicense: $spdxId")
            }
        } catch (t: Throwable) {
            LOGGER.debug("Could not load the license content", t)
        }
    }

    fun HashSet<License>.findSameSpdx(spdxId: String?): License? {
        spdxId ?: return null
        return firstOrNull { it.spdxId == spdxId }
    }
}

fun License.merge(with: License) {
    val orgLic = this
    with.name.takeIf { it.isNotBlank() }?.also { orgLic.name = it }
    with.url?.takeIf { it.isNotBlank() }?.also { orgLic.url = it }
    with.year?.takeIf { it.isNotBlank() }?.also { orgLic.year = it }
    with.content?.takeIf { it.isNotBlank() }?.also { orgLic.content = it }
}