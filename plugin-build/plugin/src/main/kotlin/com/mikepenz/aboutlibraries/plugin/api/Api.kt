package com.mikepenz.aboutlibraries.plugin.api

import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.Scm

/**
 * API wrapper to cover different repo sources, expanding with information unavailable in the pom.xml file.
 */
class Api(
    gitHubToken: String? = null,
) {
    private val gitHubApi = GitHubApi(gitHubToken)

    /**
     * Identify api to use based on repository link.
     */
    private fun resolveApi(url: String?): IApi? {
        return if (url?.contains("github", true) == true) {
            gitHubApi
        } else {
            null
        }
    }

    /**
     * Fetches the remote license for the given repository, and attaches them to the existing set.
     */
    fun fetchRemoteLicense(uniqueId: String, repositoryLink: Scm?, licenses: HashSet<License>) {
        resolveApi(repositoryLink?.url)?.fetchRemoteLicense(uniqueId, repositoryLink, licenses)
    }

    /**
     * Fetch the funding for the given repository.
     */
    fun fetchFunding(uniqueId: String, repositoryLink: Scm?, funding: MutableSet<Funding>) {
        resolveApi(repositoryLink?.url)?.fetchFunding(uniqueId, repositoryLink, funding)
    }
}

/**
 * Interface to describe an API exposed to the plugin.
 */
internal interface IApi {

    /**
     * Fetches the remote license as stored in the repository and updates the already covered licenses.
     */
    fun fetchRemoteLicense(uniqueId: String, repositoryLink: Scm?, licenses: HashSet<License>)

    /**
     * Fetches the funding for the given repository.
     */
    fun fetchFunding(uniqueId: String, repositoryLink: Scm?, funding: MutableSet<Funding>)
}