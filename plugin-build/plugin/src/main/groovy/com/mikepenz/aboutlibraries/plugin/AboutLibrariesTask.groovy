package com.mikepenz.aboutlibraries.plugin


import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import groovy.json.JsonGenerator
import groovy.xml.MarkupBuilder
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets

@CacheableTask
abstract class AboutLibrariesTask extends BaseAboutLibrariesTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AboutLibrariesTask.class);

    @Internal
    Set<String> neededLicenses = new HashSet<String>()

    private String variant
    private File dependencies

    @Internal
    private File combinedLibrariesOutputFile
    private File outputValuesFolder
    private File outputRawFolder

    File getCombinedLibrariesOutputFile() {
        return new File(outputRawFolder, "aboutlibraries.json")
    }

    @OutputDirectory
    public File getValuesFolder() {
        return new File(dependencies, "values")
    }

    @OutputDirectory
    public File getRawFolder() {
        return new File(dependencies, "raw")
    }

    @OutputDirectory
    public File getDependencies() {
        return dependencies
    }

    public void setDependencies(File dependencies) {
        this.dependencies = dependencies
    }

    public void setVariant(String variant) {
        this.variant = variant
    }

    /**
     * Loos inside the *.jar and tries to find a license file to include in the apk
     */
    def tryToFindAndWriteLibrary(def licenseId) {
        try {
            LOGGER.debug("--> Try load library with ID {}", licenseId)
            def successfulXml = false
            def resultFile = new File(outputValuesFolder, "license_${licenseId}_strings.xml")
            resultFile.delete()
            def is = getClass().getResourceAsStream("/values/license_${licenseId}_strings.xml")
            if (is != null) {
                resultFile.append(is)
                is.close()
                successfulXml = true
            } else {
                LOGGER.debug("--> File did not exist {}", getClass().getResource("values/license_${licenseId}_strings.xml"))
            }

            resultFile = new File(outputRawFolder, "license_${licenseId}.txt")
            resultFile.delete()
            is = getClass().getResourceAsStream("/static/license_${licenseId}.txt")
            if (is != null) {
                resultFile.append(is)
                is.close()
            }

            return successfulXml
        } catch (Exception ex) {
            println("--> License not available: ${licenseId}")
        }
        return false
    }

    /**
     * Creates the additional RAW files with remote licenses if available, otherwise inclues the license from the available definitions
     */
    def handleLicenses(License license) {
        if (license.remoteLicense == null || license.remoteLicense.isBlank()) {
            if (license.spdxId != null) {
                neededLicenses.add(license.spdxId) // remember the license we hit
            }
        } else {
            def resultFile = new File(getRawFolder(), "license_${license.remoteLicense.md5()}.txt")
            if (!resultFile.exists()) {
                resultFile.append(license.remoteLicense, "UTF-8")
            }
        }
    }

    /**
     * Copy in the needed licenses to the relevant folder
     */
    def processNeededLicenses() {
        // now copy over all licenses
        for (String licenseId : neededLicenses) {
            try {
                def enumLicense = SpdxLicense.valueOf(licenseId)

                // try to find and write license by aboutLibsId
                if (!tryToFindAndWriteLibrary(enumLicense.aboutLibsId)) {
                    // try to find and write license by id
                    if (!tryToFindAndWriteLibrary(enumLicense.id)) {
                        // license was not available generate the url license template
                        def resultFile = new File(outputValuesFolder, "license_${licenseId.toLowerCase(Locale.US)}_strings.xml")
                        def printWriter = new PrintWriter(new OutputStreamWriter(resultFile.newOutputStream(), StandardCharsets.UTF_8), true)
                        def licenseBuilder = new MarkupBuilder(printWriter)
                        licenseBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
                        licenseBuilder.doubleQuotes = true
                        licenseBuilder.resources {
                            string name: "define_license_${licenseId}", translatable: 'false', ""
                            string name: "license_${licenseId}_licenseName", translatable: 'false', "${enumLicense.fullName}"
                            string name: "license_${licenseId}_licenseWebsite", translatable: 'false', "${enumLicense.getUrl()}"
                        }
                        printWriter.close()
                    }
                }
            } catch (Exception ex) {
                try {
                    if (!tryToFindAndWriteLibrary(licenseId)) {
                        println("--> License not available: ${licenseId}")
                    }
                } catch (Exception ex2) {
                    println("--> License not available: ${licenseId}")
                }
            }
        }
    }

    /**
     * Checks if the given string is empty.
     * Returns true if it is NOT empty
     */
    static def isNotEmpty(String value) {
        return value != null && value != ""
    }

    @TaskAction
    public void action() throws IOException {
        // ensure directories exist
        this.outputValuesFolder = getValuesFolder()
        this.outputRawFolder = getRawFolder()
        this.combinedLibrariesOutputFile = getCombinedLibrariesOutputFile()

        final def collectedDependencies = readInCollectedDependencies()
        final def processor = new AboutLibrariesProcessor(getDependencyHandler(), collectedDependencies, configPath, exclusionPatterns, fetchRemoteLicense, includeAllLicenses, additionalLicenses, variant)
        final def libraries = processor.gatherDependencies()

        if (processor.includeAllLicenses) {
            // Include all licenses
            neededLicenses.addAll(SpdxLicense.values())
        } else {
            // Include additional licenses explicitly requested.
            processor.additionalLicenses.each { final al ->
                final def foundLicense = SpdxLicense.values().find { final li ->
                    li.name().equalsIgnoreCase(al) || li.id.equalsIgnoreCase(al)
                }
                if (foundLicense != null) {
                    neededLicenses.add(foundLicense.name())
                }
            }
        }

        final JsonGenerator jsonGenerator = new JsonGenerator.Options().excludeNulls().excludeFieldsByName("artifactFolder").build();
        final def printWriter = new PrintWriter(new OutputStreamWriter(combinedLibrariesOutputFile.newOutputStream(), StandardCharsets.UTF_8), true)
        printWriter.write(jsonGenerator.toJson(libraries))
        printWriter.close()

        libraries.forEach { final lib ->
            lib.licenses.forEach { final lic ->
                handleLicenses(lic)
            }
        }

        processNeededLicenses()
    }
}