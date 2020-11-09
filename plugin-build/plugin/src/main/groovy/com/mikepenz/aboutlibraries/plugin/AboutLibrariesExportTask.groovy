package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.License
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
public class AboutLibrariesExportTask extends DefaultTask {

    private String variant = null
    private Set<License> neededLicenses = new HashSet<License>()
    private Set<String> librariesWithoutLicenses = new HashSet<String>()
    private HashMap<String, HashSet<String>> unknownLicenses = new HashMap<String, HashSet<String>>()

    public void setVariant(String variant) {
        this.variant = variant
    }

    String getVariant() {
        return variant
    }

    Set<License> getNeededLicenses() {
        return neededLicenses
    }

    Set<String> getLibrariesWithoutLicenses() {
        return librariesWithoutLicenses
    }

    HashMap<String, HashSet<String>> getUnknownLicenses() {
        return unknownLicenses
    }

    def gatherDependencies(def project) {
        def libraries = new AboutLibrariesProcessor().gatherDependencies(project, variant)

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

    @TaskAction
    public void action() throws IOException {
        gatherDependencies(project)
    }
}