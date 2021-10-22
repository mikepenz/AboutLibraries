package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.License
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class AboutLibrariesExportTask extends BaseAboutLibrariesTask {

    private String variant = null
    private Set<License> neededLicenses = new HashSet<License>()
    private Set<String> librariesWithoutLicenses = new HashSet<String>()
    private HashMap<String, HashSet<String>> unknownLicenses = new HashMap<String, HashSet<String>>()

    public void setVariant(String variant) {
        this.variant = variant
    }

    @Internal
    String getVariant() {
        return variant
    }

    @Internal
    Set<License> getNeededLicenses() {
        return neededLicenses
    }

    @Internal
    Set<String> getLibrariesWithoutLicenses() {
        return librariesWithoutLicenses
    }

    @Internal
    HashMap<String, HashSet<String>> getUnknownLicenses() {
        return unknownLicenses
    }

    @TaskAction
    public void action() throws IOException {
        final def collectedDependencies = readInCollectedDependencies()
        final def processor = new AboutLibrariesProcessor(dependencyHandler, collectedDependencies, configPath, exclusionPatterns, fetchRemoteLicense, includeAllLicenses, additionalLicenses, variant)
        final def libraries = processor.gatherDependencies()

        if (variant != null) {
            println ""
            println ""
            println "Variant: ${variant}"
        }

        println ""
        println ""
        println "LIBRARIES:"

        for (final library in libraries) {
            library.licenseIds.each { licenseId ->
                try {
                    neededLicenses.add(License.valueOf(licenseId))
                } catch (Exception ex) {
                    if (licenseId != null && licenseId != "") {
                        HashSet<String> libsWithMissing = unknownLicenses.getOrDefault(licenseId, new HashSet<String>())
                        libsWithMissing.add(library.artifactId)
                        unknownLicenses.put(licenseId, libsWithMissing)
                    } else {
                        librariesWithoutLicenses.add(library.artifactId)
                    }
                }
            }

            println "${library.libraryName};${library.artifactId};${library.licenseIds}"
        }

        println ""
        println ""
        println "LICENSES:"

        for (final license in neededLicenses) {
            println "${license.id};${license.fullName};${license.url}"
        }

        println ""
        println ""
        println "ARTIFACTS WITHOUT LICENSE:"
        for (final license in librariesWithoutLicenses) {
            println "${license}"
        }

        println ""
        println ""
        println "UNKNOWN LICENSES:"
        for (final entry in unknownLicenses) {
            println "${entry.key}"
            println "-- ${entry.value}"
        }
    }
}