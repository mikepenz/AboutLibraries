package com.mikepenz.aboutlibraries.ui.compose.util

import androidx.compose.runtime.Immutable
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlin.jvm.JvmInline

@JvmInline
@Immutable
value class StableLibrary(val library: Library)

val Library.stable get() = StableLibrary(this)

val List<Library>.stable get() = map { it.stable }.toImmutableList()

@JvmInline
@Immutable
value class StableLicense(val license: License)

val License.stable get() = StableLicense(this)

val Set<License>.stable get() = map { it.stable }.toImmutableSet()

@Immutable
class StableLibs(val libraries: ImmutableList<StableLibrary>, val licenses: ImmutableSet<StableLicense>)

val Libs.stable get() = StableLibs(this.libraries.stable, this.licenses.stable)

val Library.author: String
    get() = developers.takeIf { it.isNotEmpty() }?.map { it.name }?.joinToString(", ") ?: organization?.name ?: ""

val License.htmlReadyLicenseContent: String?
    get() = licenseContent?.replace("\n", "<br />")

val License.strippedLicenseContent: String?
    get() = licenseContent?.replace("<br />", "\n")?.replace("<br/>", "\n")
