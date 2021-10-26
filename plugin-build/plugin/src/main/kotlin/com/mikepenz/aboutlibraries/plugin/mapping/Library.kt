package com.mikepenz.aboutlibraries.plugin.mapping

import java.io.File

/**
 * Library class describing a library and its information
 */
data class Library(
    val uniqueId: String,
    val artifactVersion: String?,
    val name: String?,
    val description: String?,
    val website: String?,
    val developers: List<Developer>,
    val organization: Organization?,
    val scm: Scm?,
    val licenses: Set<String> = emptySet(),
    val artifactFolder: File? = null
) {
    val artifactId: String
        get() = "${uniqueId}:${artifactVersion ?: ""}"
}