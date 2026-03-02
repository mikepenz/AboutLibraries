package com.mikepenz.aboutlibraries.plugin.api

import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.Scm
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * API wrapper to cover different repo sources, expanding with information unavailable in the pom.xml file.
 */
class Api private constructor(
    gitHubToken: String? = null,
) : IApi {
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
    override fun fetchRemoteLicense(uniqueId: String, repositoryLink: Scm?, licenses: HashSet<License>, mapLicensesToSpdx: Boolean) {
        resolveApi(repositoryLink?.url)?.fetchRemoteLicense(uniqueId, repositoryLink, licenses, mapLicensesToSpdx)
    }

    /**
     * Fetch the funding for the given repository.
     */
    override fun fetchFunding(uniqueId: String, repositoryLink: Scm?, funding: MutableSet<Funding>) {
        resolveApi(repositoryLink?.url)?.fetchFunding(uniqueId, repositoryLink, funding)
    }


    /**
     * NoOp API for `offline mode` usage
     */
    internal class NoOpApi internal constructor() : IApi

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Api::class.java)

        /**
         * Creates a new [IApi] instance, to be used by the plugin
         */
        internal fun create(offlineMode: Boolean, gitHubToken: String? = null): IApi {
            return if (offlineMode) {
                LOGGER.info("Plugin running in `offlineMode`. Use no-op API.")
                NoOpApi()
            } else {
                Api(gitHubToken)
            }
        }
    }
}

/**
 * Interface to describe an API exposed to the plugin.
 */
internal interface IApi {

    /**
     * Fetches the remote license as stored in the repository and updates the already covered licenses.
     */
    fun fetchRemoteLicense(uniqueId: String, repositoryLink: Scm?, licenses: HashSet<License>, mapLicensesToSpdx: Boolean = true) {}

    /**
     * Fetches the funding for the given repository.
     */
    fun fetchFunding(uniqueId: String, repositoryLink: Scm?, funding: MutableSet<Funding>) {}
}