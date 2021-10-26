package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.Scm
import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

object LicenseUtil {
    private val LOGGER: Logger = LoggerFactory.getLogger(LicenseUtil::class.java)

    private val remoteLicenseCache = HashMap<String, String>()

    private fun loadLicenseCached(url: String): String? {
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

    /**
     * Fetch licenses from either the repository, or from spdx as txt format
     */
    fun fetchRemoteLicense(uniqueId: String, repositoryLink: Scm?, licenses: HashSet<License>) {
        val url = repositoryLink?.url
        if (url?.contains("github") == true) {
            // license is usually stored in a file called `LICENSE` on the main or dev branch of the project
            // https://raw.githubusercontent.com/mikepenz/FastAdapter/develop/LICENSE

            //https://api.github.com/repos/mikepenz/AboutLibraries/license
            fun discoverBase(url: String): Pair<String, String>? {
                val parts = url.split("/").filter { it.isNotBlank() }
                if (parts.size > 3) {
                    if (parts[parts.size - 3].contains("github")) {
                        return parts[parts.size - 2] to parts[parts.size - 1]
                    }
                }
                return null
            }

            discoverBase(url)?.let { base ->
                val (user, project) = base

                // TODO offer ability to provide PAT
                val licenseApi = "https://api.github.com/repos/$user/$project/license"
                try {
                    val licensesApiResult = JsonSlurper().parse(URL(licenseApi).readBytes()) as Map<String, *>
                    val rawLicense = licensesApiResult["download_url"] as String
                    val licenseInformation = licensesApiResult["license"] as Map<String, String>
                    val content: String? = loadLicenseCached(rawLicense)

                    if (content?.isNotBlank() == true) {
                        licenses.add(License(licenseInformation["name"]!!, url, null, content).also {
                            it.spdxId = licenseInformation["spdx_id"]
                        })
                    }
                } catch (ignored: Throwable) {
                    // ignore
                }
            }
        }

        if (licenses.isNotEmpty()) {
            licenses.forEach {
                try {
                    val spdxId = it.spdxId
                    if (spdxId != null) {
                        val enumLicense = SpdxLicense.valueOf(spdxId)
                        val licUrl = enumLicense.getTxtUrl()
                        val singleLicense: String? = loadLicenseCached(licUrl)
                        if (singleLicense?.isNotBlank() == true) {
                            it.name = enumLicense.fullName // TODO re-evaluate
                            it.url = licUrl
                            it.content = singleLicense
                        }
                    }
                } catch (ignored: Throwable) {
                    // ignore
                }
            }
        } else {
            LOGGER.info("Retrieved license (${licenses.firstOrNull()?.name}) via GitHub license API")
        }
    }
}