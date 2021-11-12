package com.mikepenz.aboutlibraries.entity

/**
 * Describes the [Organization] defined in the `pom.xml` file.
 *
 * https://svn.apache.org/repos/infra/websites/production/maven/content/pom.html#Organization
 *
 * @param name of the organisation
 * @param url optional url to the website of the defined organisation
 */
data class Organization(
    val name: String,
    val url: String?
)