package com.mikepenz.aboutlibraries.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the [Scm] defined in the `pom.xml` file.
 *
 * https://svn.apache.org/repos/infra/websites/production/maven/content/pom.html#SCM
 *
 * @param connection describing the source connection
 * @param developerConnection optionally describing the developer connection
 * @param url optionally linking to the hosted form of this artifact
 */
@Serializable
data class Scm(
    @SerialName("connection") val connection: String?,
    @SerialName("developerConnection") val developerConnection: String?,
    @SerialName("url") val url: String?
)