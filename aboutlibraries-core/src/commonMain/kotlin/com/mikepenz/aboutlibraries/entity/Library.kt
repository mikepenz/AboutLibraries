package com.mikepenz.aboutlibraries.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
 * @param funding all identified funding opportunities for this artifact
 */
@Serializable
data class Library(
    @SerialName("uniqueId") val uniqueId: String,
    @SerialName("artifactVersion") val artifactVersion: String?,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("website") val website: String?,
    @SerialName("developers") val developers: List<Developer>,
    @SerialName("organization") val organization: Organization?,
    @SerialName("scm") val scm: Scm?,
    @SerialName("licenses") val licenses: Set<License> = emptySet(),
    @SerialName("funding") val funding: Set<Funding> = emptySet(),
    @SerialName("tag") val tag: String? = null,
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