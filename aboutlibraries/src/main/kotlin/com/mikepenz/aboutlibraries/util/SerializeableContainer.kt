package com.mikepenz.aboutlibraries.util

import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.entity.Organization
import com.mikepenz.aboutlibraries.entity.Scm
import java.io.Serializable

internal fun Libs.toSerializable() = SerializableLibs(
    libraries.map { lib ->
        SerializableLibrary(
            lib.uniqueId,
            lib.artifactVersion,
            lib.name,
            lib.description,
            lib.website,
            lib.developers.map { SerializableDeveloper(it.name, it.organisationUrl) },
            lib.organization?.let { SerializableOrganization(it.name, it.url) },
            lib.scm?.let { SerializableScm(it.connection, it.developerConnection, it.url) },
            lib.licenses.map {
                SerializableLicense(it.name, it.url, it.year, it.spdxId, it.licenseContent, it.hash)
            }.toSet(),
            lib.funding.map { SerializableFunding(it.platform, it.url) }.toSet(),
            lib.tag
        )
    },
    licenses.map {
        SerializableLicense(it.name, it.url, it.year, it.spdxId, it.licenseContent, it.hash)
    }.toSet()
)

internal fun SerializableLibs.toLibs() = Libs(
    libraries.map { lib ->
        Library(
            lib.uniqueId,
            lib.artifactVersion,
            lib.name,
            lib.description,
            lib.website,
            lib.developers.map { Developer(it.name, it.organisationUrl) },
            lib.organization?.let { Organization(it.name ?: "", it.url) },
            lib.scm?.let { Scm(it.connection, it.developerConnection, it.url) },
            lib.licenses.map {
                License(it.name, it.url, it.year, it.spdxId, it.licenseContent, it.hash)
            }.toSet(),
            lib.funding.map { Funding(it.platform, it.url) }.toSet(),
            lib.tag
        )
    },
    licenses.map {
        License(it.name, it.url, it.year, it.spdxId, it.licenseContent, it.hash)
    }.toSet()
)

internal data class SerializableLibs(
    val libraries: List<SerializableLibrary>,
    val licenses: Set<SerializableLicense>,
) : Serializable

internal data class SerializableLibrary(
    val uniqueId: String,
    val artifactVersion: String?,
    val name: String,
    val description: String?,
    val website: String?,
    val developers: List<SerializableDeveloper>,
    val organization: SerializableOrganization?,
    val scm: SerializableScm?,
    val licenses: Set<SerializableLicense> = emptySet(),
    val funding: Set<SerializableFunding> = emptySet(),
    val tag: String? = null,
) : Serializable

internal data class SerializableDeveloper(
    val name: String?,
    val organisationUrl: String?,
) : Serializable

internal data class SerializableFunding(
    val platform: String,
    val url: String,
) : Serializable

internal data class SerializableLicense(
    val name: String,
    val url: String?,
    val year: String? = null,
    val spdxId: String? = null,
    val licenseContent: String? = null,
    val hash: String,
) : Serializable

internal data class SerializableOrganization(
    val name: String?,
    val url: String?,
) : Serializable

internal data class SerializableScm(
    val connection: String?,
    val developerConnection: String?,
    val url: String?,
) : Serializable