package com.mikepenz.aboutlibraries.plugin.mapping

/**
 * Library class describing a library and its information
 */
data class Library(
        val uniqueId: String,
        val artifactId: String,
        val author: String?,
        val authorWebsite: String?,
        val libraryName: String?,
        val libraryDescription: String?,
        val libraryVersion: String?,
        val libraryWebsite: String?,
        val licenseIds: Set<String> = emptySet(),
        val isOpenSource: Boolean,
        val repositoryLink: String?,
        val libraryOwner: String?,
        val licenseYear: String?
)