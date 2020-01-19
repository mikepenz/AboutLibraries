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
        var license: License? = null,

        var isOpenSource: Boolean = true,
        var repositoryLink: String = "",

        var classPath: String = ""
) : Comparable<Library> {

    override fun compareTo(other: Library): Int {
        return libraryName.compareTo(other.libraryName, ignoreCase = true)
    }

    private fun ifNotEmpty(receiver: String): String? {
        return if (receiver.isEmpty()) null else receiver
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
        license = enchantWith.license ?: license
        isOpenSource = enchantWith.isOpenSource
        repositoryLink = ifNotEmpty(enchantWith.repositoryLink) ?: repositoryLink
    }
}
