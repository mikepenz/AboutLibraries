package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.Scm
import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import java.net.URL

object LicenseUtil {
    private val remoteLicenseCache = HashMap<String, String>()

    /**
     * Fetch licenses from either the repository, or from spdx as txt format
     */
    fun fetchRemoteLicense(uniqueId: String, repositoryLink: Scm?, licenses: HashSet<License>) {
        val url = repositoryLink?.url
        if (url?.contains("github") == true) {
            // license is usually stored in a file called `LICENSE` on the main or dev branch of the project
            // https://raw.githubusercontent.com/mikepenz/FastAdapter/develop/LICENSE

            val variants = arrayOf(
                "/raw/develop/LICENSE",
                "/raw/develop/LICENSE.txt",
                "/raw/main/LICENSE",
                "/raw/main/LICENSE.txt",
                "/raw/master/LICENSE",
                "/raw/master/LICENSE.txt"
            )
            for (v in variants) {
                try {
                    var content: String?
                    val url = "${url}${v}"
                    if (remoteLicenseCache.containsKey(url)) {
                        content = remoteLicenseCache[url]
                    } else {
                        remoteLicenseCache[url] = ""
                        content = URL(url).readText()
                        remoteLicenseCache[url] = content
                    }
                    if (content?.isNotBlank() == true) {
                        licenses.add(License("Repo", url, null, content))
                    }
                    break
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
                        val url = enumLicense.getTxtUrl()
                        val singleLicense: String?
                        if (remoteLicenseCache.containsKey(url)) {
                            singleLicense = remoteLicenseCache[url]
                        } else {
                            remoteLicenseCache[url] = ""
                            // did not contain, put null, to not try again
                            singleLicense = URL(url).readText()
                            remoteLicenseCache[url] = singleLicense
                        }
                        if (singleLicense?.isNotBlank() == true) {
                            it.url = url
                            it.remoteLicense = singleLicense
                        }
                    }
                } catch (ignored: Throwable) {
                    // ignore
                }
            }
        }
    }
}