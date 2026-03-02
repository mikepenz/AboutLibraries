package com.mikepenz.aboutlibraries.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the [Organization] defined in the `pom.xml` file.
 *
 * https://svn.apache.org/repos/infra/websites/production/maven/content/pom.html#Organization
 *
 * @param name of the organisation
 * @param url optional url to the website of the defined organisation
 */
@Serializable
data class Organization(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String?,
)