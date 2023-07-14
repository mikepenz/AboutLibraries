package com.mikepenz.aboutlibraries.ui.compose.util

import androidx.compose.runtime.Immutable
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import kotlinx.collections.immutable.toImmutableList
import kotlin.jvm.JvmInline

@JvmInline
@Immutable
value class StableLibrary(val library: Library)

val Library.stable get() = StableLibrary(this)

val List<Library>.stable get() = map { it.stable }.toImmutableList()

val Library.author: String
    get() = developers.takeIf { it.isNotEmpty() }?.map { it.name }?.joinToString(", ") ?: organization?.name ?: ""

val License.htmlReadyLicenseContent: String?
    get() = licenseContent?.replace("\n", "<br />")

val License.strippedLicenseContent: String?
    get() = licenseContent?.replace("<br />", "\n")?.replace("<br/>", "\n")
