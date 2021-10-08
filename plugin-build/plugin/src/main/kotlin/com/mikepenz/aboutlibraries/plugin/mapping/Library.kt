package com.mikepenz.aboutlibraries.plugin.mapping

import java.io.File

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
        val isOpenSource: Boolean,
        val repositoryLink: String?,
        val libraryOwner: String?,
        val licenseIds: Set<String> = emptySet(),
        val licenseYear: String?,
        val remoteLicense: String?,
        val artifactFolder: File? = null
)