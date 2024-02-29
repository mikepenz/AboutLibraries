package com.mikepenz.aboutlibraries.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the [Developer] defined in the `pom.xml` file.
 *
 * https://svn.apache.org/repos/infra/websites/production/maven/content/pom.html#Developers
 *
 * @param name of the developer
 * @param organisationUrl optional organisation url for the developer
 */
@Serializable
data class Developer(
    @SerialName("name") val name: String?,
    @SerialName("organisationUrl") val organisationUrl: String?
)