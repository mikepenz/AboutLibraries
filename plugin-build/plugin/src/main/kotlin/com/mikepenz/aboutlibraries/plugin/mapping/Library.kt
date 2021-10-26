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
    val developer: List<Developer>,
    val organization: Organization?,
    val scm: Scm?,
    val licenses: Set<License> = emptySet(),
    val artifactFolder: File? = null
) {
    val openSource: Boolean
        get() = scm?.url?.isNotBlank() == true
}