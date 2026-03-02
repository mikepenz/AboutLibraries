package com.mikepenz.aboutlibraries.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the [Funding] as defined by the dependency.
 * This is only supported for projects hosted for dependencies hosted on: https://github.com/mikepenz/AboutLibraries#special-repository-support
 * Or can be manually supplied.
 *
 * @param platform name of the platform allowing to fund the project
 * @param url url pointing towards the location to fund the project
 */
@Serializable
data class Funding(
    @SerialName("platform") val platform: String,
    @SerialName("url") val url: String
)