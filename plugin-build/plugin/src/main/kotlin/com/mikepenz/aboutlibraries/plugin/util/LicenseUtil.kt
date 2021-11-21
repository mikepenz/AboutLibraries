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

    /** check if there is still available rate quota for the gitHub API */
    @Suppress("UNCHECKED_CAST")
    fun availableGitHubRateLimit(gitHubToken: String? = null): Int {
        return try {
            val connection = URL("https://api.github.com/rate_limit").openConnection()
            if (gitHubToken?.isNotBlank() == true) {
                connection.setRequestProperty("Authorization", "token $gitHubToken")
            }
            val rateLimit = JsonSlurper().parse(connection.getInputStream().readBytes()) as Map<String, *>
            (rateLimit["rate"] as Map<String, *>)["remaining"] as Int
        } catch (t: Throwable) {
            0
        }
    }

    /**
     * Fetch licenses from either the repository, or from spdx as txt format
     */
    @Suppress("UNCHECKED_CAST")
    fun fetchRemoteLicense(uniqueId: String, repositoryLink: Scm?, licenses: HashSet<License>, gitHubApiRateLimit: Int, gitHubToken: String? = null): Int {
        val url = repositoryLink?.url
        var calledGitHub = false
        if (gitHubApiRateLimit > 0 && url?.contains("github") == true) {
            LOGGER.debug("Remaining GitHub rate limit: $gitHubApiRateLimit")
            calledGitHub = getLicenseFromGitHubAPI(url, licenses, gitHubToken)
        }
        if (licenses.isNotEmpty()) {
            licenses.forEach {
                if (it.spdxId != null && it.content == null) {
                    it.loadSpdxLicense()
                }
            }
        } else {
            LOGGER.info("Retrieved license (${licenses.firstOrNull()?.name}) via GitHub license API")
        }

        return if (calledGitHub) {
            gitHubApiRateLimit - 1
        } else {
            gitHubApiRateLimit
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

    /**
     * Call the GitHub API to retrieve the license of a project
     */
    @Suppress("UNCHECKED_CAST")
    private fun getLicenseFromGitHubAPI(url: String, licenses: HashSet<License>, gitHubToken: String? = null): Boolean {
        // license is usually stored in a file called `LICENSE` on the main or dev branch of the project
        // https://raw.githubusercontent.com/mikepenz/FastAdapter/develop/LICENSE

        var calledGitHub = false

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
                val connection = URL(licenseApi).openConnection()
                if (!gitHubToken.isNullOrBlank()) {
                    connection.setRequestProperty("Authorization", "token $gitHubToken")
                }
                val licensesApiResult = JsonSlurper().parse(connection.getInputStream().readBytes()) as Map<String, *>
                calledGitHub = true

                val rawLicense = licensesApiResult["download_url"] as String
                val licenseInformation = licensesApiResult["license"] as Map<String, String>
                val content: String? = loadLicenseCached(rawLicense)

                if (content?.isNotBlank() == true) {
                    val spdxId = licenseInformation["spdx_id"]
                    if (!spdxId.isNullOrBlank()) {
                        val hasSame = licenses.findSameSpdx(spdxId)
                        if (hasSame != null) {
                            licenses.remove(hasSame)
                            LOGGER.debug("Replace POM license with REPO library")
                        }
                    }
                    licenses.add(License(licenseInformation["name"]!!, rawLicense, null, content).also {
                        it.spdxId = spdxId
                    })
                }
            } catch (ignored: Throwable) {
                // ignore
            }
        }
        return calledGitHub
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