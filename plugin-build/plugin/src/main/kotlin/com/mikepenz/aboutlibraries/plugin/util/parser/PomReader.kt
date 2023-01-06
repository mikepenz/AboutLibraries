package com.mikepenz.aboutlibraries.plugin.util.parser

import com.mikepenz.aboutlibraries.plugin.mapping.Developer
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.Organization
import com.mikepenz.aboutlibraries.plugin.mapping.Scm
import org.apache.ivy.core.IvyPatternHelper
import org.apache.ivy.core.module.id.ModuleId
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.plugins.parser.m2.PomDependencyMgt
import org.apache.ivy.util.XMLHelper
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.IOException
import java.io.InputStream
import java.util.*

class PomReader(inputStream: InputStream) {
    private val properties: HashMap<String, String> = HashMap<String, String>()
    private var projectElement: Element? = null
    private var parentElement: Element? = null

    init {
        val source = InputSource(inputStream)
        try {
            val pomDomDoc = XMLHelper.parseToDom(source) { _: String?, systemId: String? ->
                if (systemId != null && systemId.endsWith("m2-entities.ent")) InputSource(
                    PomReader::class.java.getResourceAsStream("m2-entities.ent")
                ) else null
            }
            projectElement = pomDomDoc.documentElement.also {
                check(!(PROJECT != it.nodeName && MODEL != it.nodeName)) { "project must be the root tag" }
            }
            parentElement = getFirstChildElement(projectElement, PARENT)
        } finally {
            try {
                inputStream.close()
            } catch (var11: IOException) {
            }
        }
    }

    fun hasParent(): Boolean {
        return parentElement != null
    }

    val groupId: String?
        get() = getFirstChildText(projectElement, GROUP_ID).replaceProps() ?: parentGroupId

    val parentGroupId: String?
        get() = getFirstChildText(parentElement, GROUP_ID).replaceProps()

    val artifactId: String?
        get() = getFirstChildText(projectElement, ARTIFACT_ID).replaceProps()

    val parentArtifactId: String?
        get() = getFirstChildText(parentElement, ARTIFACT_ID).replaceProps()

    val version: String?
        get() = getFirstChildText(projectElement, VERSION).replaceProps()

    val parentVersion: String?
        get() = getFirstChildText(parentElement, VERSION).replaceProps()

    val packaging: String
        get() = getFirstChildText(projectElement, PACKAGING) ?: "jar"

    val homePage: String?
        get() = getFirstChildText(projectElement, HOMEPAGE)

    val name: String?
        get() = getFirstChildText(projectElement, NAME).replaceProps()?.trim()

    val description: String?
        get() = getFirstChildText(projectElement, DESCRIPTION).replaceProps()?.trim()

    val licenses: Array<License>
        get() {
            val licenses = getFirstChildElement(projectElement, LICENSES)
            if (licenses == null) {
                return emptyArray()
            } else {
                licenses.normalize()
                val lics: MutableList<License> = ArrayList<License>()
                val it = getAllChilds(licenses).iterator()
                while (true) {
                    var name: String?
                    var url: String?
                    do {
                        var license: Element
                        do {
                            if (!it.hasNext()) {
                                return lics.toTypedArray()
                            }
                            license = it.next() as Element
                        } while (LICENSE != license.nodeName)
                        name = getFirstChildText(license, LICENSE_NAME)
                        url = getFirstChildText(license, LICENSE_URL)
                    } while (name == null && url == null)
                    if (name == null) {
                        name = "Unknown License"
                    }
                    lics.add(License(name, url))
                }
            }
        }

    val scm: Scm?
        get() {
            val scm = getFirstChildElement(projectElement, SCM)
            return if (scm == null) {
                null
            } else {
                val url = getFirstChildText(scm, SCM_URL)
                val connection = getFirstChildText(scm, SCM_CONNECTION)
                val devConnection = getFirstChildText(scm, SCM_DEV_CONNECTION)
                Scm(connection, devConnection, url)
            }
        }

    val organization: Organization?
        get() {
            val organization = getFirstChildElement(projectElement, ORGANIZATION)
            return if (organization == null) {
                null
            } else {
                val name = getFirstChildText(organization, ORGANIZATION_NAME) ?: ""
                val url = getFirstChildText(organization, ORGANIZATION_URL)
                Organization(name, url)
            }
        }

    val relocation: ModuleRevisionId?
        get() {
            val distrMgt = getFirstChildElement(projectElement, DISTRIBUTION_MGT)
            val relocation = getFirstChildElement(distrMgt, RELOCATION)
            return if (relocation == null) {
                null
            } else {
                var relocGroupId = getFirstChildText(relocation, GROUP_ID)
                var relocArtId = getFirstChildText(relocation, ARTIFACT_ID)
                var relocVersion = getFirstChildText(relocation, VERSION)
                relocGroupId = relocGroupId ?: groupId
                relocArtId = relocArtId ?: artifactId
                relocVersion = relocVersion ?: version
                ModuleRevisionId.newInstance(relocGroupId, relocArtId, relocVersion)
            }
        }

    val dependencies: List<PomDependencyData>
        get() {
            val dependenciesElement = getFirstChildElement(projectElement, DEPENDENCIES)
            val dependencies: LinkedList<PomDependencyData> = LinkedList<PomDependencyData>()
            if (dependenciesElement != null) {
                val childs = dependenciesElement.childNodes
                for (i in 0 until childs.length) {
                    val node = childs.item(i)
                    if (node is Element && DEPENDENCY == node.getNodeName()) {
                        dependencies.add(PomDependencyData(node))
                    }
                }
            }
            return dependencies
        }

    val dependencyMgt: List<PomDependencyMgtElement>
        get() {
            var dependenciesElement = getFirstChildElement(projectElement, DEPENDENCY_MGT)
            dependenciesElement = getFirstChildElement(dependenciesElement, DEPENDENCIES)
            val dependencies: LinkedList<PomDependencyMgtElement> = LinkedList<PomDependencyMgtElement>()
            if (dependenciesElement != null) {
                val childs = dependenciesElement.childNodes
                for (i in 0 until childs.length) {
                    val node = childs.item(i)
                    if (node is Element && DEPENDENCY == node.getNodeName()) {
                        dependencies.add(PomDependencyMgtElement(node))
                    }
                }
            }
            return dependencies
        }

    val developers: List<Developer>
        get() {
            val developerElements = getFirstChildElement(projectElement, DEVELOPERS)
            val developers: LinkedList<Developer> = LinkedList<Developer>()
            if (developerElements != null) {
                val childs = developerElements.childNodes
                for (i in 0 until childs.length) {
                    val node = childs.item(i)
                    if (node is Element && DEVELOPER == node.getNodeName()) {
                        developers.add(PomDeveloper(node).toDeveloper())
                    }
                }
            }
            return developers
        }

    val plugins: List<PomPluginElement>
        get() {
            val plugins: LinkedList<PomPluginElement> = LinkedList<PomPluginElement>()
            val buildElement = getFirstChildElement(projectElement, "build")
            return if (buildElement == null) {
                plugins
            } else {
                val pluginsElement = getFirstChildElement(buildElement, PLUGINS)
                if (pluginsElement != null) {
                    val childs = pluginsElement.childNodes
                    for (i in 0 until childs.length) {
                        val node = childs.item(i)
                        if (node is Element && PLUGIN == node.getNodeName()) {
                            plugins.add(PomPluginElement(node))
                        }
                    }
                }
                plugins
            }
        }
    val pomProperties: Map<*, *>
        get() {
            val pomProperties: MutableMap<Any, Any> = HashMap<Any, Any>()
            val propsEl = getFirstChildElement(projectElement, PROPERTIES)
            propsEl?.normalize()
            val it = getAllChilds(propsEl).iterator()
            while (it.hasNext()) {
                val prop = it.next() as Element
                pomProperties[prop.nodeName] = getTextContent(prop)
            }
            return pomProperties
        }

    private fun String?.replaceProps(): String? {
        return if (this == null) null else IvyPatternHelper.substituteVariables(this, properties).trim { it <= ' ' }
    }

    inner class PomDependencyData internal constructor(private val depElement: Element?) : PomDependencyMgtElement(
        depElement
    ) {

        override fun getScope(): String = getFirstChildText(depElement, SCOPE).replaceProps() ?: ""

        val classifier: String?
            get() = getFirstChildText(depElement, CLASSIFIER).replaceProps()

        val type: String?
            get() = getFirstChildText(depElement, TYPE).replaceProps()

        val isOptional: Boolean
            get() {
                val e = getFirstChildElement(depElement, OPTIONAL)
                return e != null && "true".equals(getTextContent(e), ignoreCase = true)
            }
    }

    inner class PomDeveloper internal constructor(private val depElement: Element) {
        val name: String?
            get() = getFirstChildText(depElement, NAME).replaceProps()

        val organisationUrl: String?
            get() = getFirstChildText(depElement, DEVELOPER_ORG_URL).replaceProps()

        fun toDeveloper(): Developer = Developer(name, organisationUrl)
    }

    inner class PomPluginElement internal constructor(private val pluginElement: Element) : PomDependencyMgt {
        override fun getGroupId(): String = getFirstChildText(pluginElement, GROUP_ID).replaceProps() ?: ""

        override fun getArtifactId(): String = getFirstChildText(pluginElement, ARTIFACT_ID).replaceProps() ?: ""

        override fun getVersion(): String = getFirstChildText(pluginElement, VERSION).replaceProps() ?: ""

        override fun getScope(): String = ""

        override fun getExcludedModules(): List<ModuleId> = emptyList()
    }

    open inner class PomDependencyMgtElement internal constructor(private val depElement: Element?) : PomDependencyMgt {
        constructor(copyFrom: PomDependencyMgtElement) : this(copyFrom.depElement) {}

        override fun getGroupId(): String = getFirstChildText(depElement, GROUP_ID).replaceProps() ?: ""

        override fun getArtifactId(): String = getFirstChildText(depElement, ARTIFACT_ID).replaceProps() ?: ""

        override fun getVersion(): String = getFirstChildText(depElement, VERSION).replaceProps() ?: ""

        override fun getScope(): String = getFirstChildText(depElement, SCOPE).replaceProps() ?: ""

        override fun getExcludedModules(): List<ModuleId> {
            val exclusionsElement = getFirstChildElement(depElement, EXCLUSIONS)
            val exclusions: LinkedList<ModuleId> = LinkedList<ModuleId>()
            if (exclusionsElement != null) {
                val childs = exclusionsElement.childNodes
                for (i in 0 until childs.length) {
                    val node = childs.item(i)
                    if (node is Element && EXCLUSION == node.getNodeName()) {
                        val groupId = getFirstChildText(node, GROUP_ID)
                        val artifactId = getFirstChildText(node, ARTIFACT_ID)
                        if (groupId != null && artifactId != null) {
                            exclusions.add(ModuleId.newInstance(groupId, artifactId))
                        }
                    }
                }
            }
            return exclusions
        }
    }

    companion object {
        private const val PACKAGING = "packaging"
        private const val DEPENDENCY = "dependency"
        private const val DEPENDENCIES = "dependencies"
        private const val DEPENDENCY_MGT = "dependencyManagement"
        private const val DEVELOPER = "developer"
        private const val DEVELOPERS = "developers"
        private const val DEVELOPER_ORG_URL = "organizationUrl"
        private const val PROJECT = "project"
        private const val MODEL = "model"
        private const val GROUP_ID = "groupId"
        private const val ARTIFACT_ID = "artifactId"
        private const val VERSION = "version"
        private const val NAME = "name"
        private const val DESCRIPTION = "description"
        private const val HOMEPAGE = "url"
        private const val LICENSES = "licenses"
        private const val LICENSE = "license"
        private const val LICENSE_NAME = "name"
        private const val LICENSE_URL = "url"
        private const val SCM = "scm"
        private const val SCM_CONNECTION = "connection"
        private const val SCM_DEV_CONNECTION = "developerConnection"
        private const val SCM_URL = "url"
        private const val ORGANIZATION = "organization"
        private const val ORGANIZATION_NAME = "name"
        private const val ORGANIZATION_URL = "url"
        private const val PARENT = "parent"
        private const val SCOPE = "scope"
        private const val CLASSIFIER = "classifier"
        private const val OPTIONAL = "optional"
        private const val EXCLUSIONS = "exclusions"
        private const val EXCLUSION = "exclusion"
        private const val DISTRIBUTION_MGT = "distributionManagement"
        private const val RELOCATION = "relocation"
        private const val PROPERTIES = "properties"
        private const val PLUGINS = "plugins"
        private const val PLUGIN = "plugin"
        private const val TYPE = "type"

        private fun getTextContent(element: Element): String {
            val result = StringBuffer()
            val childNodes = element.childNodes
            var i = 0
            while (i < childNodes.length) {
                val child = childNodes.item(i)
                when (child.nodeType) {
                    3.toShort(), 4.toShort() -> {
                        result.append(child.nodeValue)
                        ++i
                    }
                    else -> ++i
                }
            }
            return result.toString()
        }

        private fun getFirstChildText(parentElem: Element?, name: String): String? {
            val node = getFirstChildElement(parentElem, name)
            return if (node != null) getTextContent(node) else null
        }

        private fun getFirstChildElement(parentElem: Element?, name: String): Element? {
            return if (parentElem == null) {
                null
            } else {
                val childs = parentElem.childNodes
                for (i in 0 until childs.length) {
                    val node = childs.item(i)
                    if (node is Element && name == node.getNodeName()) {
                        return node
                    }
                }
                null
            }
        }

        private fun getAllChilds(parent: Element?): List<*> {
            val r: MutableList<Element> = LinkedList<Element>()
            if (parent != null) {
                val childs = parent.childNodes
                for (i in 0 until childs.length) {
                    val node = childs.item(i)
                    if (node is Element) {
                        r.add(node)
                    }
                }
            }
            return r
        }
    }
}