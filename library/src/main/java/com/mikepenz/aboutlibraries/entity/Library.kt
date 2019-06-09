package com.mikepenz.aboutlibraries.entity


data class Library(
        var definedName: String,
        var isInternal: Boolean = false,

        var libraryName: String,
        var author: String = "",
        var authorWebsite: String = "",
        var libraryDescription: String = "",
        var libraryVersion: String = "",
        var libraryWebsite: String = "",
        var license: License? = null,

        var isOpenSource: Boolean = true,
        var repositoryLink: String = "",

        var classPath: String = ""
) : Comparable<Library> {

    override fun compareTo(other: Library): Int {
        return libraryName.compareTo(other.libraryName, ignoreCase = true)
    }
}
