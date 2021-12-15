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
                remoteLicenseCache[url]
            } else {
                remoteLicenseCache[url] = ""
                URL(url).readText().also {
                    remoteLicenseCache[url] = it
                }
            }
        } catch (t: Throwable) {
            null
        }
    }

    fun License.loadSpdxLicense() {
        val spdxId = spdxId ?: return
        try {
            val enumLicense = SpdxLicense.find(spdxId)
            if (enumLicense != null) {
                val licUrl = enumLicense.getTxtUrl()
                val singleLicense: String? = loadLicenseCached(licUrl)
                if (singleLicense?.isNotBlank() == true) {
                    name = enumLicense.fullName
                    url = enumLicense.getUrl()
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