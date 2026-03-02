package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesExportTask : BaseAboutLibrariesTask() {

    override fun getDescription(): String = "Writes all libraries and their licenses in CSV format to the CLI.${variant.orNull?.let { " Filtered by variant: '$it'." } ?: ""}"
    override fun getGroup(): String = "Help"

    @TaskAction
    fun action() {
        val neededLicenses = HashSet<SpdxLicense>()
        val librariesWithoutLicenses = HashSet<String>()
        val unknownLicenses = HashMap<String, HashSet<String>>()

        val result = createLibraryPostProcessor().process()
        val libraries = result.libraries
        val licenses = result.licenses

        val variant = variant.orNull
        if (variant != null) {
            println("")
            println("")
            println("Variant: $variant")
        }

        println("")
        println("")
        println("LIBRARIES:")

        for (library in libraries) {
            val fullLicenses = library.licenses.mapNotNull { licenses[it] }
            fullLicenses.map { it.spdxId ?: it.name }.forEach { licenseId ->
                try {
                    neededLicenses.add(SpdxLicense.getById(licenseId))
                } catch (_: Throwable) {
                    if (licenseId != "") {
                        val libsWithMissing = unknownLicenses.getOrDefault(licenseId, HashSet())
                        libsWithMissing.add(library.artifactId)
                        unknownLicenses[licenseId] = libsWithMissing
                    } else {
                        librariesWithoutLicenses.add(library.artifactId)
                    }
                }
            }

            println("${library.name};${library.artifactId};${fullLicenses.joinToString(",") { it.spdxId ?: it.name }}")
        }

        println("")
        println("")
        println("LICENSES:")

        for (license in neededLicenses) {
            println("${license.id};${license.fullName};${license.getUrl()}")
        }

        println("")
        println("")
        println("ARTIFACTS WITHOUT LICENSE:")
        for (license in librariesWithoutLicenses) {
            println(license)
        }

        println("")
        println("")
        println("UNKNOWN LICENSES:")
        for (entry in unknownLicenses) {
            println(entry.key)
            println("-- ${entry.value}")
        }
    }
}