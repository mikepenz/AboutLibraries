package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import com.mikepenz.aboutlibraries.plugin.util.LibrariesProcessor
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesExportTask : BaseAboutLibrariesTask() {

    @Internal
    var neededLicenses = HashSet<SpdxLicense>()

    @Internal
    var librariesWithoutLicenses = HashSet<String>()

    @Internal
    private var unknownLicenses = HashMap<String, HashSet<String>>()

    @TaskAction
    fun action() {
        val collectedDependencies = readInCollectedDependencies()
        val processor = LibrariesProcessor(getDependencyHandler(), collectedDependencies, getConfigPath(), exclusionPatterns, fetchRemoteLicense, variant)
        val result = processor.gatherDependencies()

        if (variant != null) {
            println("")
            println("")
            println("Variant: $variant")
        }

        println("")
        println("")
        println("LIBRARIES:")

        for (library in result.libraries) {
            val fullLicenses = library.licenses.mapNotNull { result.licenses[it] }
            fullLicenses.map { it.spdxId ?: it.name }.forEach { licenseId ->
                try {
                    neededLicenses.add(SpdxLicense.valueOf(licenseId))
                } catch (ex: Throwable) {
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