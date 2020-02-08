package com.mikepenz.aboutlibraries.plugin

import com.android.build.gradle.internal.ide.dependencies.ArtifactUtils
import com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.VariantScopeImpl
import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.result.ArtifactResolutionResult
import org.gradle.api.artifacts.result.ArtifactResult
import org.gradle.api.artifacts.result.ComponentArtifactsResult
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

class AboutLibrariesProcessor {
    private File configFolder

    Set<String> handledLibraries = new HashSet<String>()

    Map<String, String> customLicenseMappings = new HashMap<String, String>()
    Map<String, String> customLicenseYearMappings = new HashMap<String, String>()
    Map<String, String> customNameMappings = new HashMap<String, String>()
    Map<String, String> customEnchantMapping = new HashMap<String, String>()

    def collectMappingDetails(targetMap, resourceName) {
        def customMappingText = getClass().getResource("/static/${resourceName}").getText('UTF-8')
        customMappingText.eachLine {
            def splitMapping = it.split(':')
            targetMap.put(splitMapping[0], splitMapping[1])
        }

        if (configFolder != null) {
            try {
                def target = new File(configFolder, "${resourceName}")
                if (target.exists()) {
                    customMappingText = target.getText('UTF-8')
                    customMappingText.eachLine {
                        def splitMapping = it.split(':')
                        targetMap.put(splitMapping[0], splitMapping[1])
                    }
                    println "Read custom mapping file from: ${target.absolutePath}"
                }
            } catch (Exception ex) {
                // ignored
            }
        }
    }

    def collectMappingDetails() {
        collectMappingDetails(customLicenseMappings, 'custom_license_mappings.prop')
        collectMappingDetails(customLicenseYearMappings, 'custom_license_year_mappings.prop')
        collectMappingDetails(customNameMappings, 'custom_name_mappings.prop')
        collectMappingDetails(customEnchantMapping, 'custom_enchant_mapping.prop')
    }

    def gatherDependencies(def project) {
        def extension = project.extensions.aboutLibraries
        if (extension.configPath != null) {
            configFolder = new File(extension.configPath)
        }

        // get all the componentIdentifiers from the artifacts
        def componentIdentifiers = new HashSet<ComponentIdentifier>()
        project.android.applicationVariants.all { variant ->
            def variantScopeImpl = null
            try {
                // 4.0.0-alpha09
                variantScopeImpl = new VariantScopeImpl(project.android.globalScope, new TransformManager(project, null, null), variant.variantData.getVariantDslInfo(), variant.variantData.getType())
                variantScopeImpl.setVariantData(variant.variantData)
            } catch (Exception ex) {
                // pre 4.0.0-alpha09
                variantScopeImpl = new VariantScopeImpl(project.android.globalScope, new TransformManager(project, null, null), variant.variantData)
            }

            ArtifactUtils.getAllArtifacts(
                    variantScopeImpl,
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

        def librariesList = new ArrayList<Library>()
        for (component in result.resolvedComponents) {
            component.getArtifacts(MavenPomArtifact).each {
                // log the pom files content
                // println "POM file for ${component.id}: ${it.file.getText('UTF-8')}"
                writeDependency(librariesList, component.id, it)
            }
        }
        return librariesList
    }

    def writeDependency(List<Library> libraries, ComponentIdentifier component, ArtifactResult artifact) {
        def artifactPom = new XmlSlurper(/* validating */ false, /* namespaceAware */ false).parseText(artifact.file.getText('UTF-8'))

        // the uniqueId
        def groupId = ifEmptyElse(artifactPom.groupId, artifactPom.parent.groupId)
        def uniqueId = fixIdentifier(groupId) + "__" + fixIdentifier(artifactPom.artifactId)

        // check if we shall skip this specific uniqueId
        if (shouldSkip(uniqueId)) {
            return
        }

        // remember that we handled the library
        handledLibraries.add(uniqueId)

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
        if (!isNotEmpty(author)) {
            // if no devs listed, use organisation
            author = fixString(artifactPom.organization.name)
        }
        if (!isNotEmpty(author) && parentPom != null) { // fallback to parentPom if available
            author = fixAuthor(fixString(fixXmlSlurperArray(parentPom.developers.developer.name)))
            if (!isNotEmpty(author)) {
                // if no devs listed, use organisation
                author = fixString(parentPom.organization.name)
            }
            if (isNotEmpty(author)) {
                println("----> Had to fallback to parent author for: ${uniqueId} -- result: ${author}")
            }
        }
        // get the author from the pom
        def authorWebsite = fixString(fixXmlSlurperArray(artifactPom.developers.developer.organizationUrl))
        if (!isNotEmpty(authorWebsite)) {
            // if no devs listed, use organisation
            authorWebsite = fixString(artifactPom.organization.url)
        }
        if (!isNotEmpty(authorWebsite) && parentPom != null) { // fallback to parentPom if available
            authorWebsite = fixAuthor(fixString(fixXmlSlurperArray(parentPom.developers.developer.organizationUrl)))
            if (!isNotEmpty(authorWebsite)) {
                // if no devs listed, use organisation
                authorWebsite = fixString(parentPom.organization.url)
            }
            if (isNotEmpty(authorWebsite)) {
                println("----> Had to fallback to parent authorWebsite for: ${uniqueId} -- result: ${authorWebsite}")
            }
        }
        // get the url for the author
        def libraryName = fixLibraryName(uniqueId, fixString(artifactPom.name))
        // get name of the library
        def libraryDescription = fixLibraryDescription(uniqueId, fixString(artifactPom.description))
        if (enchantedDefinition != null) {
            // enchant the library by the description of the available definition file
            libraryDescription = ifEmptyElse(enchantedDefinition.string.find { it.@name.toString().endsWith("_libraryDescription") }.toString(), libraryDescription)
        }
        if (!isNotEmpty(libraryDescription) && parentPom != null) {
            // fallback to parentPom if available
            println("----> Had to fallback to parent description for: ${uniqueId}")
            libraryDescription = fixLibraryDescription(uniqueId, fixString(parentPom.description))
        }
        // get the description of the library
        def libraryVersion = fixString(artifactPom.version) // get the version of the library
        if (!isNotEmpty(libraryVersion) && parentPom != null) {
            // fallback to parentPom if available
            libraryVersion = fixString(parentPom.version)
            if (isNotEmpty(libraryVersion)) {
                println("----> Had to fallback to parent version for: ${uniqueId} -- result: ${libraryVersion}")
            }
        }
        def libraryWebsite = fixString(artifactPom.url) // get the url to the library
        def licenseId = resolveLicenseId(uniqueId, fixString(artifactPom.licenses.license.name), fixString(artifactPom.licenses.license.url))
        if (!isNotEmpty(licenseId) && parentPom != null) { // fallback to parentPom if available
            licenseId = resolveLicenseId(uniqueId, fixString(parentPom.licenses.license.name), fixString(parentPom.licenses.license.url))
            if (isNotEmpty(licenseId)) {
                println("----> Had to fallback to parent licenseId for: ${uniqueId} -- result: ${licenseId}")
            }
        }
        // get the url to the library
        def repositoryLink = fixString(artifactPom.scm.url)
        def isOpenSource = isNotEmpty(repositoryLink)
        // assume if we have a link it is open source, may not always be accurate!
        def libraryOwner = fixString(author)

        // the license year
        def licenseYear = resolveLicenseYear(uniqueId, repositoryLink)

        if (!isNotEmpty(libraryName)) {
            println "Could not get the name for ${uniqueId}, Skipping"
            return
        }

        libraries.add(
                new Library(
                        uniqueId,
                        "${groupId}:${artifactPom.artifactId}:${artifactPom.version}",
                        author,
                        authorWebsite,
                        libraryName,
                        libraryDescription,
                        libraryVersion,
                        libraryWebsite,
                        licenseId,
                        isOpenSource,
                        repositoryLink,
                        libraryOwner,
                        licenseYear
                )
        )
    }

    /**
     * returns value1 if it is not empty otherwise value2
     */
    static def ifEmptyElse(def value1, def value2) {
        if (value1 != null && isNotEmpty(value1.toString())) {
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
            return value.toString()
                    .replace("\\", "")
                    .replace("\"", "\\\"")
                    .replace("'", "\\'")
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
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
    def fixLibraryName(String uniqueId, String value) {
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
    def resolveLicenseId(String uniqueId, String name, String url) {
        if (customLicenseMappings.containsKey(uniqueId)) {
            def customMapping = customLicenseMappings.get(uniqueId)
            println("--> Had to resolve license from custom mapping for: ${uniqueId} as ${customMapping}")
            return customMapping
        } else {
            for (License l : License.values()) {
                def matcher = l.customMatcher
                if (l.id.equalsIgnoreCase(name) || l.name().equalsIgnoreCase(name) || l.fullName.equalsIgnoreCase(name) || (matcher != null && matcher.invoke(name, url))) {
                    if (l.aboutLibsId != null) {
                        return l.aboutLibsId
                    } else {
                        return l.id
                    }
                }
            }
        }
        return name
    }

    def resolveLicenseYear(String uniqueId, String repositoryLink) {
        if (customLicenseYearMappings.containsKey(uniqueId)) {
            def customMapping = customLicenseYearMappings.get(uniqueId)
            println("--> Had to resolve license year custom mapping for: ${uniqueId} as ${customMapping}")
            return customMapping
        } else {
            // TODO resolve via custom pom rule? try to resolve via git repo?
        }
        return ""
    }

    /**
     * Checks if the given string is empty.
     * Returns true if it is NOT empty
     */
    static def isNotEmpty(String value) {
        return value != null && value != ""
    }

    /**
     * Skip libraries which have a core dependency and we don't want it to show up more than necessary
     */
    def shouldSkip(String uniqueId) {
        return handledLibraries.contains(uniqueId) || uniqueId == "com_mikepenz__aboutlibraries" || uniqueId == "com_mikepenz__aboutlibraries_definitions"
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