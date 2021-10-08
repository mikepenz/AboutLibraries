package com.mikepenz.aboutlibraries.entity


data class Library(
    var definedName: String,
    var isInternal: Boolean = false,
    var isPlugin: Boolean = false,

    var libraryName: String,
    var author: String = "",
    var authorWebsite: String = "",
    var libraryDescription: String = "",
    var libraryVersion: String = "",
    var libraryArtifactId: String = "",
    var libraryWebsite: String = "",
    var licenses: Set<License>? = null,

    var isOpenSource: Boolean = true,
    var repositoryLink: String = "",

    var classPath: String = ""
) : Comparable<Library> {

    @Deprecated("Note. AboutLibraries v8.3.0 now supports multiple licenses per Library", ReplaceWith("licenses.firstOrNull()"))
    var license: License?
        get() = licenses?.firstOrNull()
        set(value) {
            licenses = setOf(value ?: License("", "", "", "", ""))
        }

    override fun compareTo(other: Library): Int {
        return libraryName.compareTo(other.libraryName, ignoreCase = true)
    }

    private fun ifNotEmpty(receiver: String): String? {
        return receiver.ifEmpty { null }
    }

    /**
     * transfers the information from one to the other, if set
     */
    fun enchantBy(enchantWith: Library) {
        libraryName = ifNotEmpty(enchantWith.libraryName) ?: libraryName
        author = ifNotEmpty(enchantWith.author) ?: author
        authorWebsite = ifNotEmpty(enchantWith.authorWebsite) ?: authorWebsite
        libraryDescription = ifNotEmpty(enchantWith.libraryDescription) ?: libraryDescription
        libraryVersion = ifNotEmpty(enchantWith.libraryVersion) ?: libraryVersion
        libraryArtifactId = ifNotEmpty(enchantWith.libraryArtifactId) ?: libraryArtifactId
        libraryWebsite = ifNotEmpty(enchantWith.libraryWebsite) ?: libraryWebsite
        licenses = enchantWith.licenses ?: licenses
        isOpenSource = enchantWith.isOpenSource
        repositoryLink = ifNotEmpty(enchantWith.repositoryLink) ?: repositoryLink
    }
}
