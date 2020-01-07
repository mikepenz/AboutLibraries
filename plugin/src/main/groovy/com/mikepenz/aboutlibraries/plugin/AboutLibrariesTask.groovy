package com.mikepenz.aboutlibraries.plugin

import com.android.build.gradle.internal.ide.dependencies.ArtifactUtils
import com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.VariantScopeImpl
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.result.ArtifactResolutionResult
import org.gradle.api.artifacts.result.ArtifactResult
import org.gradle.api.artifacts.result.ComponentArtifactsResult
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier
import org.gradle.api.tasks.*
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

@CacheableTask
public class AboutLibrariesTask extends DefaultTask {

    private File dependencies
    private File combinedLibrariesOutputFile
    private File outputValuesFolder
    private File outputRawFolder

    static Set<String> neededLicenses = new HashSet<String>()
    static Map<String, String> customLicenseMappings = new HashMap<String, String>()
    static Map<String, String> customNameMappings = new HashMap<String, String>()
    static Map<String, String> customEnchantMapping = new HashMap<String, String>()

    File getCombinedLibrariesOutputFile() {
        return new File(outputValuesFolder, "aboutlibraries.xml")
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

    @InputDirectory
    public void setDependencies(File dependencies) {
        this.dependencies = dependencies
    }

    def collectMappingDetails(targetMap, resourceName) {
        def customMappingText = getClass().getResource(resourceName).getText('UTF-8')
        customMappingText.eachLine {
            def splitMapping = it.split(':')
            targetMap.put(splitMapping[0], splitMapping[1])
        }
    }

    def collectMappingDetails() {
        collectMappingDetails(customLicenseMappings, '/static/custom_license_mappings.prop')
        collectMappingDetails(customNameMappings, '/static/custom_name_mappings.prop')
        collectMappingDetails(customEnchantMapping, '/static/custom_enchant_mapping.prop')
    }

    def gatherDependencies(def project) {
        // ensure directories exist
        this.outputValuesFolder = getValuesFolder()
        this.outputRawFolder = getRawFolder()
        this.combinedLibrariesOutputFile = getCombinedLibrariesOutputFile()

        // get all the componentIdentifiers from the artifacts
        def componentIdentifiers = new HashSet<ComponentIdentifier>()
        project.android.applicationVariants.all { variant ->
            ArtifactUtils.getAllArtifacts(
                    new VariantScopeImpl(project.android.globalScope, new TransformManager(project, null, null), variant.variantData),
                    AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                    null,
                    BuildMappingUtils.computeBuildMapping(project.gradle)
            ).eachWithIndex { artifact, idx ->
                // log all dependencies
                // println "${idx} : ${componentIdentifier.displayName}"
                componentIdentifiers.add(artifact.componentIdentifier)
            }
        }

        println "All dependencies.size=${componentIdentifiers.size()}"

        def result = project.dependencies.createArtifactResolutionQuery().forComponents(componentIdentifiers).withArtifacts(MavenModule, MavenPomArtifact).execute()
        if (componentIdentifiers.size() > 0) {
            collectMappingDetails()
        }

        def fileWriter = new FileWriter(combinedLibrariesOutputFile)
        def combinedLibrariesBuilder = new MarkupBuilder(fileWriter)
        combinedLibrariesBuilder.doubleQuotes = true
        combinedLibrariesBuilder.resources {
            for (component in result.resolvedComponents) {
                component.getArtifacts(MavenPomArtifact).each {
                    // log the pom files content
                    // println "POM file for ${component.id}: ${it.file.getText('UTF-8')}"
                    //writeDependency(component.id, it)
                    writeDependency(combinedLibrariesBuilder, component.id, it)
                }
            }
            string name: "config_aboutLibraries_plugin", "yes"
        }

        processNeededLicenses()
    }

    /**
     * Copy in the needed licenses to the relevant folder
     */
    def processNeededLicenses() {
        // now copy over all licenses
        for (String licenseId : neededLicenses) {
            try {
                def resultFile = new File(outputRawFolder, "license_${licenseId}.txt")
                if (!resultFile.exists()) {
                    def is = getClass().getResourceAsStream("/static/license_${licenseId}.txt")
                    resultFile.append(is)
                    is.close()
                }

                resultFile = new File(outputValuesFolder, "license_${licenseId}_strings.xml")
                if (!resultFile.exists()) {
                    def is = getClass().getResourceAsStream("/values/license_${licenseId}_strings.xml")
                    resultFile.append(is)
                    is.close()
                }
            } catch (Exception ex) {
                println("--> License not available: ${licenseId}")
            }
        }
    }

    def writeDependency(MarkupBuilder resources, ComponentIdentifier component, ArtifactResult artifact) {
        def artifactPom = new XmlSlurper(/* validating */ false, /* namespaceAware */ false).parseText(artifact.file.getText('UTF-8'))

        // the uniqueId
        def groupId = ifEmptyElse(artifactPom.groupId, artifactPom.parent.groupId)
        def uniqueId = fixIdentifier(groupId) + "__" + fixIdentifier(artifactPom.artifactId)

        // check if we shall skip this specific uniqueId
        if (shouldSkip(uniqueId)) {
            return
        }

        // we also want to check if there are parent POMs with additional information
        def parentPomFile = resolveParentPomFile(uniqueId, getParentFromPom(artifactPom))
        def parentPom = null
        if (parentPomFile != null) {
            parentPom = new XmlSlurper(/* validating */ false, /* namespaceAware */ false).parseText(parentPomFile.getText('UTF-8'))
        }

        def enchantedDefinition = null
        if (customEnchantMapping.containsKey(uniqueId)) {
            def enchantedDefinitionId = customEnchantMapping.get(uniqueId)
            try {
                enchantedDefinition = new XmlSlurper(/* validating */ false, /* namespaceAware */ false)
                        .parseText(getClass().getResource("/values/library_${enchantedDefinitionId}_strings.xml").getText('UTF-8'))
            } catch (Exception ex) {
                println("--> Enchanted file not available: ${enchantedDefinitionId}")
            }
        }

        // generate a unique ID for the library
        def author = fixAuthor(fixString(fixXmlSlurperArray(artifactPom.developers.developer.name)))
        if (!checkEmpty(author)) {
            // if no devs listed, use organisation
            author = fixString(artifactPom.organization.name)
        }
        if (!checkEmpty(author) && parentPom != null) { // fallback to parentPom if available
            author = fixAuthor(fixString(fixXmlSlurperArray(parentPom.developers.developer.name)))
            if (!checkEmpty(author)) {
                // if no devs listed, use organisation
                author = fixString(parentPom.organization.name)
            }
            println("----> Had to fallback to parent author for: ${uniqueId} -- result: ${author}")
        }
        // get the author from the pom
        def authorWebsite = fixString(fixXmlSlurperArray(artifactPom.developers.developer.organizationUrl))
        if (!checkEmpty(authorWebsite)) {
            // if no devs listed, use organisation
            authorWebsite = fixString(artifactPom.organization.url)
        }
        if (!checkEmpty(authorWebsite) && parentPom != null) { // fallback to parentPom if available
            authorWebsite = fixAuthor(fixString(fixXmlSlurperArray(parentPom.developers.developer.organizationUrl)))
            if (!checkEmpty(authorWebsite)) {
                // if no devs listed, use organisation
                authorWebsite = fixString(parentPom.organization.url)
            }
            println("----> Had to fallback to parent authorWebsite for: ${uniqueId} -- result: ${authorWebsite}")
        }
        // get the url for the author
        def libraryName = fixLibraryName(uniqueId, fixString(artifactPom.name))
        // get name of the library
        def libraryDescription = fixLibraryDescription(uniqueId, fixString(artifactPom.description))
        if (enchantedDefinition != null) {
            // enchant the library by the description of the available definition file
            libraryDescription = ifEmptyElse(enchantedDefinition.string.find { it.@name.toString().endsWith("_libraryDescription") }.toString(), libraryDescription)
        }
        if (!checkEmpty(libraryDescription) && parentPom != null) {
            // fallback to parentPom if available
            println("----> Had to fallback to parent description for: ${uniqueId}")
            libraryDescription = fixLibraryDescription(uniqueId, fixString(parentPom.description))
        }
        // get the description of the library
        def libraryVersion = fixString(artifactPom.version) // get the version of the library
        if (!checkEmpty(libraryVersion) && parentPom != null) {
            // fallback to parentPom if available
            libraryVersion = fixString(parentPom.version)
            println("----> Had to fallback to parent version for: ${uniqueId} -- result: ${libraryVersion}")
        }
        def libraryWebsite = fixString(artifactPom.url) // get the url to the library
        def licenseId = resolveLicenseId(uniqueId, fixString(artifactPom.licenses.license.name), fixString(artifactPom.licenses.license.url))
        if (!checkEmpty(licenseId) && parentPom != null) { // fallback to parentPom if available
            licenseId = resolveLicenseId(uniqueId, fixString(parentPom.licenses.license.name), fixString(parentPom.licenses.license.url))
            println("----> Had to fallback to parent licenseId for: ${uniqueId} -- result: ${licenseId}")
        }
        if (checkEmpty(licenseId)) {
            neededLicenses.add(licenseId) // remember the license we hit
        }
        // get the url to the library
        def isOpenSource = fixString(artifactPom.url)
        def repositoryLink = fixString(artifactPom.scm.url)
        def libraryOwner = fixString(author)

        def delimiter = ""
        def customProperties = ""
        if (checkEmpty(libraryOwner)) {
            customProperties = delimiter + customProperties + "owner"
            delimiter = ","
        }

        if (!checkEmpty(libraryName)) {
            println "Could not get the name for ${uniqueId}, Skipping"
            return
        }

        resources.string name: "define_plu_${uniqueId}", "${customProperties}"
        if (checkEmpty(author)) {
            resources.string name: "library_${uniqueId}_author", "${author}"
        }
        if (checkEmpty(authorWebsite)) {
            resources.string name: "library_${uniqueId}_authorWebsite", "${authorWebsite}"
        }
        resources.string name: "library_${uniqueId}_libraryName", "${libraryName}"
        resources.string(name: "library_${uniqueId}_libraryDescription") {
            mkp.yieldUnescaped("<![CDATA[${libraryDescription}]]>")
        }
        resources.string name: "library_${uniqueId}_libraryVersion", "${libraryVersion}"
        resources.string name: "library_${uniqueId}_libraryArtifactId", "${groupId}:${artifactPom.artifactId}:${artifactPom.version}"
        // the maven artifactId
        if (checkEmpty(libraryWebsite)) {
            resources.string name: "library_${uniqueId}_libraryWebsite", "${libraryWebsite}"
        }
        if (checkEmpty(licenseId)) {
            resources.string name: "library_${uniqueId}_licenseId", "${licenseId}"
        }
        if (checkEmpty(isOpenSource)) {
            resources.string name: "library_${uniqueId}_isOpenSource", "${isOpenSource}"
        }
        if (checkEmpty(repositoryLink)) {
            resources.string name: "library_${uniqueId}_repositoryLink", "${repositoryLink}"
        }
        if (checkEmpty(libraryOwner)) {
            resources.string name: "library_${uniqueId}_owner", "${libraryOwner}"
        }
    }

    /**
     * returns value1 if it is not empty otherwise value2
     */
    static def ifEmptyElse(def value1, def value2) {
        if (value1 != null && checkEmpty(value1.toString())) {
            return value1
        } else {
            return value2
        }
    }

    /**
     * Ensures no invalid chars stay in the identifier
     */
    static def fixIdentifier(Object value) {
        return fixString(value).replace(".", "_").replace("-", "_")
    }

    /**
     * Fix XmlSlurper array string
     */
    static def fixXmlSlurperArray(value) {
        if (value != null) {
            def delimiter = ""
            def resultString = ""
            for (item in value) {
                resultString = resultString + delimiter + item.toString()
                delimiter = ", "
            }
            return resultString
        } else {
            return null
        }
    }

    /**
     * Ensures all characters necessary are escaped
     */
    static def fixString(Object value) {
        if (value != null) {
            return value.toString().replace("\"", "\\\"").replace("'", "\\'")
        } else {
            return ""
        }
    }

    /**
     * Ensures the author name is not too long (for known options)
     */
    static def fixAuthor(String value) {
        if (value == "The Android Open Source Project") {
            return "AOSP"
        } else {
            return value
        }
    }

    /**
     * Ensures and applies fixes to the library names (shorten, ...)
     */
    static def fixLibraryName(String uniqueId, String value) {
        if (customNameMappings.containsKey(uniqueId)) {
            def customMapping = customNameMappings.get(uniqueId)
            println("--> Had to resolve name from custom mapping for: ${uniqueId} as ${customMapping}")
            return customMapping
        } else if (value.startsWith("Android Support Library")) {
            return value.replace("Android Support Library", "Support")
        } else if (value.startsWith("Android Support")) {
            return value.replace("Android Support", "Support")
        } else if (value.startsWith("org.jetbrains.kotlin:")) {
            return value.replace("org.jetbrains.kotlin:", "")
        } else {
            return value
        }
    }

    /**
     * Ensures and applies fixes to the library descriptions (remove 'null', ...)
     */
    static def fixLibraryDescription(String uniqueId, String value) {
        if (value == "null") {
            return ""
        } else {
            return value
        }
    }

    /**
     * Ensures and applies fixes to the library names (shorten, ...)
     */
    static def resolveLicenseId(String uniqueId, String name, String url) {
        if (customLicenseMappings.containsKey(uniqueId)) {
            def customMapping = customLicenseMappings.get(uniqueId)
            println("--> Had to resolve license from custom mapping for: ${uniqueId} as ${customMapping}")
            return customMapping
        } else if (name.contains("Apache") && url.endsWith("LICENSE-2.0.txt")) {
            return "apache_2_0"
        } else if (name.contains("MIT License")) {
            return "mit"
        } else if (name == "Android Software Development Kit License") {
            return "asdkl"
        } else if (name == "Eclipse Public License v2.0" || url == "https://www.eclipse.org/legal/epl-v20.html") {
            return "epl_2_0"
        } else if (name == "Crashlytics Terms of Service") {
            return "cts"
        } else if (name == "Fabric Software and Services Agreement") {
            return "fssa"
        } else {
            return name
        }
        // todo add support for more libraries. need to figure out how they are defined in the various pom files!
    }

    /**
     * Checks if the given string is empty
     */
    static def checkEmpty(String value) {
        return value != null && value != ""
    }

    /**
     * Skip libraries which have a core dependency and we don't want it to show up more than necessary
     */
    static def shouldSkip(String uniqueId) {
        return uniqueId == "com_mikepenz__aboutlibraries" || uniqueId == "com_mikepenz__aboutlibraries_definitions"
    }

    /**
     * Will convert some-thing-named to Some Thing Named
     */
    static def toProperNameString(String s) {
        String[] parts = s.split("-")
        String camelCaseString = ""
        for (String part : parts) {
            camelCaseString = camelCaseString + " " + toProperCase(part)
        }
        return camelCaseString
    }

    static def toProperCase(String s) {
        return s.substring(0, 1).toUpperCase(Locale.US) + s.substring(1).toLowerCase(Locale.US)
    }

    @TaskAction
    public void action() throws IOException {
        gatherDependencies(project)
    }

    /**
     * Looks in the pom if there is a parent we potentially could resolve
     *
     * Logic based on: https://github.com/ben-manes/gradle-versions-plugin
     */
    static ModuleVersionIdentifier getParentFromPom(pom) {
        def parent = pom.children().find { child -> child.name() == 'parent' }
        if (parent) {
            String groupId = parent.groupId
            String artifactId = parent.artifactId
            String version = parent.version
            if (groupId && artifactId && version) {
                return DefaultModuleVersionIdentifier.newId(groupId, artifactId, version)
            }
        }
        return null
    }

    /**
     * Tries to resolve the parent pom file given the id if possible
     *
     * Logic based on: https://github.com/ben-manes/gradle-versions-plugin
     */
    File resolveParentPomFile(uniqueId, ModuleVersionIdentifier id) {
        try {
            if (id == null) {
                return null
            }
            ArtifactResolutionResult resolutionResult = project.dependencies.createArtifactResolutionQuery()
                    .forComponents(DefaultModuleComponentIdentifier.newId(id))
                    .withArtifacts(MavenModule, MavenPomArtifact)
                    .execute()

            // size is 0 for gradle plugins, 1 for normal dependencies
            for (ComponentArtifactsResult result : resolutionResult.resolvedComponents) {
                // size should always be 1
                for (ArtifactResult artifact : result.getArtifacts(MavenPomArtifact)) {
                    if (artifact instanceof ResolvedArtifactResult) {
                        println "--> Retrieved parent POM for: ${uniqueId} from ${id.group}:${id.name}:${id.version}"
                        return ((ResolvedArtifactResult) artifact).file
                    }
                }
            }
            return null
        } catch (Exception e) {
            return null
        }
    }
}
