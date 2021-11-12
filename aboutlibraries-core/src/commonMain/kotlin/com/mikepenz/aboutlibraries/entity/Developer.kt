package com.mikepenz.aboutlibraries.entity

/**
 * Describes the [Developer] defined in the `pom.xml` file.
 *
 * https://svn.apache.org/repos/infra/websites/production/maven/content/pom.html#Developers
 *
 * @param name of the developer
 * @param organisationUrl optional organisation url for the developer
 */
data class Developer(
    val name: String?,
    val organisationUrl: String?
)