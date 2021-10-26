package com.mikepenz.aboutlibraries.entity

/**
 * Library class describing a library and its information
 */
data class Library(
    val uniqueId: String,
    val artifactVersion: String?,
    val name: String,
    val description: String?,
    val website: String?,
    val developers: List<Developer>,
    val organization: Organization?,
    val scm: Scm?,
    val licenses: Set<License> = emptySet()
) {
    val artifactId: String
        get() = "${uniqueId}:${artifactVersion ?: ""}"

    val openSource: Boolean
        get() = scm?.url?.isNotBlank() == true
}