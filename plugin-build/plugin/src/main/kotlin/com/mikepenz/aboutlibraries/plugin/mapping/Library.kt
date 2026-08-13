package com.mikepenz.aboutlibraries.plugin.mapping

import java.io.File

/**
 * Library class describing a library and its information
 */
data class Library(
    var uniqueId: String,
    var artifactVersion: String?,
    var name: String?,
    var description: String?,
    var website: String?,
    var developers: List<Developer>,
    var organization: Organization?,
    var scm: Scm?,
    var licenses: Set<String> = emptySet(),
    var funding: Set<Funding> = emptySet(),
    var tag: String? = null,
    /**
     * Names of the Gradle configurations this library was resolved from (e.g. `androidCompileClasspath`).
     *
     * `null` unless `collect.includeVariants` is enabled — the JSON writer excludes nulls, so the
     * field is omitted from the output entirely when the feature is off.
     */
    var variants: Set<String>? = null,
    var artifactFolder: File? = null,
) {
    val artifactId: String
        get() = "${uniqueId}:${artifactVersion ?: ""}"

    val groupId: String
        get() = uniqueId.split(":").first()

    /** references all associated libraries, matched by the duplicateRule */
    var associated: List<String>? = null
}