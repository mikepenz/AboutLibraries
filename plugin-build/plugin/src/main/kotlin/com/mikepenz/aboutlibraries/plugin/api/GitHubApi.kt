package com.mikepenz.aboutlibraries.plugin.api

import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.Scm
import com.mikepenz.aboutlibraries.plugin.util.LicenseUtil.findSameSpdx
import com.mikepenz.aboutlibraries.plugin.util.LicenseUtil.loadLicenseCached
import com.mikepenz.aboutlibraries.plugin.util.LicenseUtil.loadSpdxLicense
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.OutputStreamWriter
import java.net.URL

internal class GitHubApi(
    private val gitHubToken: String? = null,
) : IApi {
    private var rateLimit: Int = 0

    init {
        rateLimit = availableGitHubRateLimit()
    }

    /** check if there is still available rate quota for the gitHub API */
    @Suppress("UNCHECKED_CAST")
    private fun availableGitHubRateLimit(): Int {
        return try {
            val connection = URL("${GITHUB_API}rate_limit").openConnection()
            if (gitHubToken?.isNotBlank() == true) {
                connection.setRequestProperty("Authorization", "token $gitHubToken")
            }
            val rateLimit = JsonSlurper().parse(connection.getInputStream().readBytes()) as Map<String, *>
            val limit = (rateLimit["rate"] as Map<String, *>)["remaining"] as Int
            if (limit == 0) {
                LOGGER.warn("GitHub `rate_limit` exhausted. Won't be able to use the GitHub API. Please check if the token is provided, or enable `offlineMode`.")
            }
            limit
        } catch (t: Throwable) {
            LOGGER.error("Could not retrieve `rate_limit`. Please check if the token is provided. (${t.message})")
            0
        }
    }

    /**
     * Fetch licenses from either the repository, or from spdx as txt format
     */
    @Suppress("UNCHECKED_CAST")
    override fun fetchRemoteLicense(uniqueId: String, repositoryLink: Scm?, licenses: HashSet<License>, mapLicensesToSpdx: Boolean) {
        val url = repositoryLink?.url
        if (rateLimit > 0 && url?.contains("github") == true) {
            LOGGER.debug("Remaining GitHub rate limit: $rateLimit")
            val calledGitHub = getLicenseFromGitHubAPI(uniqueId, url, licenses)

            if (licenses.isNotEmpty()) {
                licenses.forEach {
                    if (it.spdxId != null && it.content == null) {
                        it.loadSpdxLicense(mapLicensesToSpdx)
                    }
                }
            } else {
                LOGGER.info("Retrieved license (${licenses.firstOrNull()?.name}) via GitHub license API")
            }

            if (calledGitHub) {
                // update remaining rate limit
                updateAndCheckRateLimit()
            }
        }
    }

    /**
     * Call the GitHub API to retrieve the license of a project
     */
    @Suppress("UNCHECKED_CAST")
    private fun getLicenseFromGitHubAPI(uniqueId: String, url: String, licenses: HashSet<License>): Boolean {
        // license is usually stored in a file called `LICENSE` on the main or dev branch of the project
        // https://raw.githubusercontent.com/mikepenz/FastAdapter/develop/LICENSE
        var calledGitHub = false
        discoverBase(url)?.let { (user, project) ->
            val licenseApi = "${GITHUB_API}repos/$user/$project/license"
            try {
                val connection = URL(licenseApi).openConnection()
                if (!gitHubToken.isNullOrBlank()) {
                    connection.setRequestProperty("Authorization", "token $gitHubToken")
                }
                val licensesApiResult = try {
                    JsonSlurper().parse(connection.getInputStream().readBytes()) as Map<String, *>
                } catch (t: Throwable) {
                    // no license available for repo
                    return calledGitHub
                }
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
            } catch (t: Throwable) {
                LOGGER.warn("Could not fetch license for $user/$project - ($uniqueId)", t)
            }
        }
        return calledGitHub
    }


    /**
     * Call the GitHub API to retrieve the funding of a project
     */
    @Suppress("UNCHECKED_CAST")
    override fun fetchFunding(uniqueId: String, repositoryLink: Scm?, funding: MutableSet<Funding>) {
        val url = repositoryLink?.url ?: return
        // {"query": "query { repository(name: \"openssl\", owner: \"openssl\") { fundingLinks { platform url } } }" }
        discoverBase(url)?.let { (user, project) ->
            val cacheKey = "$user/$project"
            if (remoteFundingCache.containsKey(cacheKey)) {
                remoteFundingCache[cacheKey]?.forEach {
                    funding.add(it)
                }
                return
            }

            if (rateLimit <= 0) {
                return
            }

            try {
                val connection = URL("${GITHUB_API}graphql").openConnection()
                if (!gitHubToken.isNullOrBlank()) {
                    connection.setRequestProperty("Authorization", "token $gitHubToken")
                }
                connection.doOutput = true
                OutputStreamWriter(connection.getOutputStream()).use {
                    it.write(
                        """
                    {"query": "query { repository(name: \"${project}\", owner: \"${user}\") { fundingLinks { platform url } } }" }
                    """.trimIndent()
                    )
                    it.flush()
                }

                val localFunding = mutableSetOf<Funding>()
                val fundingResult = JsonSlurper()
                    .parse(connection.getInputStream().readBytes()) as Map<String, Map<String, Map<String, ArrayList<Map<String, String>>>>>

                fundingResult["data"]?.get("repository")?.get("fundingLinks")?.forEach {
                    val platform = it["platform"]
                    val fundingUrl = it["url"]
                    if (platform != null && fundingUrl != null) {
                        localFunding.add(Funding(platform, fundingUrl))
                    }
                }

                // store in cache
                remoteFundingCache[cacheKey] = localFunding
                funding.addAll(localFunding)

                // update rate limit
                updateAndCheckRateLimit()
            } catch (t: Throwable) {
                LOGGER.warn("Could not fetch funding for $user/$project - ($uniqueId)", t)
            }
        }

    }

    /**
     * Retrieves username (or org) and repository from the repository url
     *
     * https://github.com/mikepenz/AboutLibraries
     */
    private fun discoverBase(url: String): Pair<String, String>? {
        val parts = url.split("/").filter { it.isNotBlank() }
        if (parts.size > 3) {
            if (parts[parts.size - 3].contains("github")) {
                return parts[parts.size - 2] to parts[parts.size - 1]
            }
        }
        return null
    }

    /**
     * Updates the [rateLimit] and logs error in case of the limit being exhausted.
     */
    private fun updateAndCheckRateLimit() {
        // update remaining rate limit
        rateLimit -= 1
        if (rateLimit <= 0) {
            LOGGER.warn("GitHub `rate_limit` exhausted. The plugin be able to use the GitHub API.")
        }
    }

    private companion object {
        private const val GITHUB_API = "https://api.github.com/"

        private val LOGGER: Logger = LoggerFactory.getLogger(GitHubApi::class.java)

        private val remoteFundingCache = HashMap<String, Set<Funding>>()
    }
}