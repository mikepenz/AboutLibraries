package com.mikepenz.aboutlibraries.plugin

import com.android.build.gradle.internal.ide.dependencies.ArtifactUtils
import com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils
import com.android.build.gradle.internal.ide.dependencies.ResolvedArtifact
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.VariantScopeImpl
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.result.ArtifactResult
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

@CacheableTask
public class AboutLibrariesTask extends DefaultTask {

    private File dependencies;
    private File outputFile;

    @OutputDirectory
    public File getDependencies() {
        return dependencies;
    }

    public void setDependencies(File dependencies) {
        this.dependencies = dependencies
        this.outputFile = new File(dependencies, "aboutlibraries.xml")
    }

    def gatherDependencies(def project) {
        def android = project.android
        def globalScope = android.globalScope
        def gradle = project.gradle

        def deps = new HashSet<ResolvedArtifact>()
        project.android.applicationVariants.all { variant ->
            def set = ArtifactUtils.getAllArtifacts(
                    new VariantScopeImpl(globalScope, new TransformManager(project, null, null), variant.variantData),
                    AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                    null,
                    BuildMappingUtils.computeBuildMapping(gradle)
            )

            println "${variant.name.capitalize()} all dependencies.size=${set.size()}"
            set.eachWithIndex { artifact, idx ->
                deps.add(artifact)
            }
        }

        // get all the componentIdentifiers from the artifacts
        def componentIdentifiers = new HashSet<ComponentIdentifier>()
        deps.eachWithIndex { artifact, idx ->
            def componentIdentifier = artifact.componentIdentifier
            componentIdentifiers.add(componentIdentifier)
            // log all dependencies
            // if (componentIdentifier.displayName.contains(':')) {
            //     println "${idx} : ${componentIdentifier.displayName}"
            // } else {
            //     println "${idx} -> ${artifact.artifactFile}"
            // }
        }


        def result = project.dependencies.createArtifactResolutionQuery()
                .forComponents(componentIdentifiers)
                .withArtifacts(MavenModule, MavenPomArtifact)
                .execute()

        outputFile.write("<resources>\n") // open
        for (component in result.resolvedComponents) {
            component.getArtifacts(MavenPomArtifact).each {
                // log the pom files content
                // println "POM file for ${component.id}: ${it.file.getText('UTF-8')}"
                writeDependency(component.id, it)
            }
        }
        outputFile.append("<string name=\"config_aboutLibraries_plugin\">yes</string>")
        outputFile.append("</resources>") // close
    }

    def writeDependency(ComponentIdentifier component, ArtifactResult artifact) {
        def artifactPom = new XmlSlurper().parseText(artifact.file.getText('UTF-8'))

        def uniqueId = fixIdentifier(artifactPom.groupId) + "__" + fixIdentifier(artifactPom.artifactId)
        // generate a unique ID for the library
        def author = fixAuthor(fixString(artifactPom.developers.developer.name))
        // get the author from the pom
        def authorWebsite = fixString(artifactPom.developers.developer.organizationUrl)
        // get the url for the author
        def libraryName = fixLibraryName(uniqueId, fixString(artifactPom.name))
        // get name of the library
        def libraryDescription = fixString(artifactPom.description) + "<br /><br />Artifact: ${artifactPom.groupId}:${artifactPom.artifactId}:${artifactPom.version}"
        // get the description of the library
        def libraryVersion = fixString(artifactPom.version) // get the version of the library
        def libraryWebsite = fixString(artifactPom.url) // get the url to the library
        def licenseId = resolveLicenseId(uniqueId, fixString(artifactPom.licenses.license.name), fixString(artifactPom.licenses.license.url))
        // get the url to the library
        def isOpenSource = fixString(artifactPom.url)
        def repositoryLink = fixString(artifactPom.scm.url)
        def libraryOwner = fixString(author)

        def delimiter = ""
        def customProperties = ""
        if (checkEmpty(libraryOwner)) {
            customProperties = delimiter + "owner" + customProperties
            delimiter = ","
        }

        if (!checkEmpty(libraryName)) {
            println "Could not get the name for ${uniqueId}, Skipping"
            return
        }

        outputFile.append("<string name=\"define_plu_${uniqueId}\">${customProperties}</string>")
        if (checkEmpty(author)) {
            outputFile.append("<string name=\"library_${uniqueId}_author\">${author}</string>")
        }
        if (checkEmpty(authorWebsite)) {
            outputFile.append("<string name=\"library_${uniqueId}_authorWebsite\">${authorWebsite}</string>")
        }
        outputFile.append("<string name=\"library_${uniqueId}_libraryName\">${libraryName}</string>")
        outputFile.append("<string name=\"library_${uniqueId}_libraryDescription\"><![CDATA[${libraryDescription}]]></string>")
        outputFile.append("<string name=\"library_${uniqueId}_libraryVersion\">${libraryVersion}</string>")
        if (checkEmpty(libraryWebsite)) {
            outputFile.append("<string name=\"library_${uniqueId}_libraryWebsiten\">${libraryWebsite}</string>")
        }
        if (checkEmpty(licenseId)) {
            outputFile.append("<string name=\"library_${uniqueId}_licenseId\">${licenseId}</string>")
        }
        if (checkEmpty(isOpenSource)) {
            outputFile.append("<string name=\"library_${uniqueId}_isOpenSource\">${isOpenSource}</string>")
        }
        if (checkEmpty(repositoryLink)) {
            outputFile.append("<string name=\"library_${uniqueId}_repositoryLink\">${repositoryLink}</string>")
        }
        if (checkEmpty(libraryOwner)) {
            outputFile.append("<string name=\"library_${uniqueId}_owner\">${libraryOwner}</string>")
        }
        outputFile.append("\n")
    }

    /**
     * Ensures no invalid chars stay in the identifier
     */
    private static def fixIdentifier(Object value) {
        return fixString(value).replace(".", "_").replace("-", "_")
    }

    /**
     * Ensures all characters necessary are escaped
     */
    private static def fixString(Object value) {
        if (value != null) {
            return value.toString().replace("\"", "\\\"").replace("'", "\\'")
        } else {
            return ""
        }
    }

    /**
     * Ensures the author name is not too long (for known options)
     */
    private static def fixAuthor(String value) {
        if (value == "The Android Open Source Project") {
            return "AOSP"
        } else {
            return value
        }
    }

    /**
     * Ensures and applies fixes to the library names (shorten, ...)
     */
    private static def fixLibraryName(String uniqueId, String value) {
        if (uniqueId == "androidx_savedstate__savedstate") {
            return "SavedState"
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
     * Ensures and applies fixes to the library names (shorten, ...)
     */
    private static def resolveLicenseId(String uniqueId, String name, String url) {
        if (name.contains("Apache") && url.endsWith("LICENSE-2.0.txt")) {
            return "apache_2_0"
        } else {
            return name
        }

        // todo add support for more libraries. need to figure out how they are defined in the various pom files!
    }

    /**
     * Checks if the given string is empty
     */
    private static def checkEmpty(String value) {
        return value != null && value != ""
    }

    @TaskAction
    public void action() throws IOException {
        gatherDependencies(project)
    }
}