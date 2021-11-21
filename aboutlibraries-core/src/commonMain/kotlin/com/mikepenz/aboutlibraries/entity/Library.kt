package com.mikepenz.aboutlibraries.entity

/**
 * Describes a complete [Library] element, specifying important information about a used dependency.
 *
 * @param uniqueId describes this dependency (matches [artifactId] without version)
 * @param artifactVersion the version of the artifact used
 * @param name of the given dependency
 * @param description of the given dependency, may be empty.
 * @param website provided by the artifact `pom.xml`
 * @param developers list, including all listed devs according to the `pom` file
 * @param organization describing the creating org of for the dependency
 * @param scm information, linking to the repository hosting the source
 * @param licenses all identified licenses for this artifact
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
    /**
     * defines the [uniqueId]:[artifactVersion] combined
     */
    val artifactId: String
        get() = "${uniqueId}:${artifactVersion ?: ""}"

    /**
     * Returns `true` in cases this artifact is assumed to be open source (e..g. [scm].url is provided)
     */
    val openSource: Boolean
        get() = scm?.url?.isNotBlank() == true
}