package com.mikepenz.aboutlibraries.entity

/**
 * Describes the [Scm] defined in the `pom.xml` file.
 *
 * https://svn.apache.org/repos/infra/websites/production/maven/content/pom.html#SCM
 *
 * @param connection describing the source connection
 * @param developerConnection optionally describing the developer connection
 * @param url optionally linking to the hosted form of this artifact
 */
data class Scm(
    val connection: String?,
    val developerConnection: String?,
    val url: String?
)