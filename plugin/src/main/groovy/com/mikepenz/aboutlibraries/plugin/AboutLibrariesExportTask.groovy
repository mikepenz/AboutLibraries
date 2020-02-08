package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.License
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
public class AboutLibrariesExportTask extends DefaultTask {
    Set<License> neededLicenses = new HashSet<License>()
    Set<String> unknownLicenses = new HashSet<String>()

    def gatherDependencies(def project) {
        def libraries = new AboutLibrariesProcessor().gatherDependencies(project)

        println ""
        println ""
        println "LIBRARIES:"

        for (final library in libraries) {
            try {
                neededLicenses.add(License.valueOf(library.licenseId))
            } catch (Exception ex) {
                if (library.licenseId != null && library.licenseId != "") {
                    unknownLicenses.add(library.licenseId)
                } else {
                    unknownLicenses.add(library.artifactId)
                }
            }
            println "${library.libraryName};${library.artifactId};${library.licenseId}"
        }

        println ""
        println ""
        println "LICENSES:"

        for (final license in neededLicenses) {
            println "${license.id};${license.fullName};${license.url}"
        }

        println ""
        println ""
        println "UNKNOWN LICENSES / ARTIFACTS WITHOUT LICENSE:"
        for (final license in unknownLicenses) {
            println "${license}"
        }
    }

    @TaskAction
    public void action() throws IOException {
        gatherDependencies(project)
    }
}