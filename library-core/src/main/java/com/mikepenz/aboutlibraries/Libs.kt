@file:JvmName("Libs")

package com.mikepenz.aboutlibraries

import android.content.Context
import android.util.Log
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.getFields
import com.mikepenz.aboutlibraries.util.getRawResourceId
import com.mikepenz.aboutlibraries.util.getStringResourceByName
import com.mikepenz.aboutlibraries.util.toStringArray
import java.util.*
import kotlin.collections.ArrayList

class Libs(
        context: Context,
        fields: Array<String> = context.getFields(),
        libraryEnchantments: Map<String, String> = emptyMap()
) {

    private val internLibraries = mutableListOf<Library>()
    private val externLibraries = mutableListOf<Library>()
    private val licenses = mutableListOf<License>()

    /**
     * Get all available Libraries
     *
     * @return an ArrayList Library with all available Libraries
     */
    val libraries: List<Library>
        get() {
            val libs = mutableListOf<Library>()
            libs.addAll(getInternLibraries())
            libs.addAll(getExternLibraries())
            return libs
        }

    /**
     * Keys to handle specific fields describing a Library.
     */
    enum class LibraryFields {
        AUTHOR_NAME,
        AUTHOR_WEBSITE,
        LIBRARY_NAME,
        LIBRARY_DESCRIPTION,
        LIBRARY_VERSION,
        LIBRARY_ARTIFACT_ID,
        LIBRARY_WEBSITE,
        LIBRARY_OPEN_SOURCE,
        LIBRARY_REPOSITORY_LINK,
        LIBRARY_CLASSPATH,

        @Deprecated("Note this only will work for libraries with a single license, otherwise only the first is modified")
        LICENSE_NAME,

        @Deprecated("Note this only will work for libraries with a single license, otherwise only the first is modified")
        LICENSE_SHORT_DESCRIPTION,

        @Deprecated("Note this only will work for libraries with a single license, otherwise only the first is modified")
        LICENSE_DESCRIPTION,

        @Deprecated("Note this only will work for libraries with a single license, otherwise only the first is modified")
        LICENSE_WEBSITE
    }

    enum class SpecialButton {
        SPECIAL1,
        SPECIAL2,
        SPECIAL3
    }

    /**
     * init method
     */
    init {
        val foundLicenseIdentifiers = ArrayList<String>()
        val foundPluginLibraryIdentifiers = ArrayList<String>()

        for (field in fields) {
            when {
                field.startsWith(DEFINE_LICENSE) -> foundLicenseIdentifiers.add(field.replace(DEFINE_LICENSE, ""))
                field.startsWith(DEFINE_PLUGIN) -> foundPluginLibraryIdentifiers.add(field.replace(DEFINE_PLUGIN, ""))
            }
        }

        // add licenses
        // this has to happen first as the licenses need to be initialized before the libraries are read in
        for (licenseIdentifier in foundLicenseIdentifiers) {
            val license = genLicense(context, licenseIdentifier) ?: continue
            licenses.add(license)
        }

        //add plugin libs
        for (pluginLibraryIdentifier in foundPluginLibraryIdentifiers) {
            val library = genLibrary(context, pluginLibraryIdentifier) ?: continue
            library.isInternal = false
            library.isPlugin = true
            externLibraries.add(library)

            val enchantWithKey = libraryEnchantments[pluginLibraryIdentifier] ?: continue
            val enchantWith = genLibrary(context, enchantWithKey) ?: continue
            library.enchantBy(enchantWith)
        }
    }

    /**
     * This will summarize libraries, exclude requested libs, and sort the list
     *
     * @param excludeLibraries     the String[] with the libs to be excluded
     * @param sort                 defines if the array should be sorted
     * @return the summarized list of included Libraries
     */
    fun prepareLibraries(excludeLibraries: Array<out String> = emptyArray(), sort: Boolean = true): ArrayList<Library> {
        val libraries = HashMap<String, Library>()
        val resultLibraries = ArrayList<Library>()

        //Add all external libraries
        val extern = getExternLibraries()
        resultLibraries.addAll(extern)

        if (excludeLibraries.isNotEmpty()) {
            for (lib in extern) {
                libraries[lib.definedName] = lib
            }

            //remove libraries which should be excluded
            for (excludeLibrary in excludeLibraries) {
                val lib = libraries[excludeLibrary] ?: continue
                resultLibraries.remove(lib)
            }
        }

        if (sort) {
            resultLibraries.sort()
        }
        return resultLibraries
    }

    /**
     * Get all intern available Libraries
     *
     * @return an ArrayList Library with all available internLibraries
     */
    fun getInternLibraries(): ArrayList<Library> {
        return ArrayList(internLibraries)
    }

    /**
     * Get all extern available Libraries
     *
     * @return an ArrayList Library  with all available externLibraries
     */
    fun getExternLibraries(): ArrayList<Library> {
        return ArrayList(externLibraries)
    }

    /**
     * Get all available licenses
     *
     * @return an ArrayLIst License  with all available Licenses
     */
    fun getLicenses(): ArrayList<License> {
        return ArrayList(licenses)
    }

    /**
     * Get a library by its name (the name must be equal)
     *
     * @param libraryName the name of the lib (NOT case sensitive) or the real name of the lib (this is the name used for github)
     * @return the found library or null
     */
    fun getLibrary(libraryName: String): Library? {
        for (library in libraries) {
            if (library.libraryName.equals(libraryName, true)) {
                return library
            } else if (library.definedName.equals(libraryName, true)) {
                return library
            }
        }
        return null
    }

    /**
     * Find a library by a searchTerm (Limit the results if there are more than one)
     *
     * @param searchTerm the term which is in the libs name (NOT case sensitive) or the real name of the lib (this is the name used for github)
     * @param limit      -1 for all results or smaller 0 for a limited result
     * @return an ArrayList Library with the found internLibraries
     */
    fun findLibrary(searchTerm: String, limit: Int): List<Library> {
        return find(libraries, searchTerm, false, limit)
    }

    /**
     * Tries to find a library given a [searchTerm] from the internal libraries.
     * If [limit] is set to 1, an equal definedName match would be tried to match, fallback to search in lib name or definedName.
     */
    fun findInInternalLibrary(searchTerm: String, idOnly: Boolean, limit: Int): List<Library> {
        return find(getInternLibraries(), searchTerm, idOnly, limit)
    }

    /**
     * Tries to find a library given a [searchTerm] from the external libraries.
     * If [limit] is set to 1, an equal definedName match would be tried to match, fallback to search in lib name or definedName.
     */
    fun findInExternalLibrary(searchTerm: String, idOnly: Boolean, limit: Int): List<Library> {
        return find(getExternLibraries(), searchTerm, idOnly, limit)
    }

    /**
     * Tries to find a library given a [searchTerm] from the given [libraries].
     * If [limit] is set to 1, an equal definedName match would be tried to match, fallback to search in lib name or definedName.
     */
    private fun find(libraries: List<Library>, searchTerm: String, idOnly: Boolean, limit: Int): List<Library> {
        /** special optimization to return a equal id match in case we only want max 1 elements */
        if (limit == 1) {
            libraries.firstOrNull { it.definedName.equals(searchTerm, true) }?.let {
                return listOf(it)
            }
        }

        /** no equal match found, try to find alternative matches instead */
        val matchFunction = if (idOnly) {
            { library: Library -> library.definedName.contains(searchTerm, true) }
        } else {
            { library: Library -> library.libraryName.contains(searchTerm, true) || library.definedName.contains(searchTerm, true) }
        }
        return libraries.filter(matchFunction).take(limit)
    }


    /**
     * @param licenseName
     * @return
     */
    fun getLicense(licenseName: String): License? {
        for (license in getLicenses()) {
            if (license.licenseName.equals(licenseName, true)) {
                return license
            } else if (license.definedName.equals(licenseName, true)) {
                return license
            }
        }
        return null
    }

    /**
     * @param licenseName
     * @return
     */
    private fun genLicense(ctx: Context, licenseName: String): License? {
        val license = licenseName.replace("-", "_")
        return try {
            var licenseDescription = ctx.getStringResourceByName("license_" + license + "_licenseDescription")
            if (licenseDescription.startsWith("raw:")) {
                licenseDescription = ctx.resources.openRawResource(ctx.getRawResourceId(licenseDescription.removePrefix("raw:"))).bufferedReader().use { it.readText() }
            }

            License(
                    license,
                    ctx.getStringResourceByName("license_" + license + "_licenseName"),
                    ctx.getStringResourceByName("license_" + license + "_licenseWebsite"),
                    ctx.getStringResourceByName("license_" + license + "_licenseShortDescription"),
                    licenseDescription
            )
        } catch (ex: Exception) {
            Log.e("aboutlibraries", "Failed to generateLicense from file: $ex")
            null
        }
    }

    /**
     * @param libraryName
     * @return
     */
    private fun genLibrary(ctx: Context, libraryName: String): Library? {
        val name = libraryName.replace("-", "_")

        try {
            val lib = Library(definedName = name, libraryName = ctx.getStringResourceByName("library_" + name + "_libraryName"))

            //Get custom vars to insert into defined areas
            val customVariables = getCustomVariables(ctx, name)

            lib.author = ctx.getStringResourceByName("library_" + name + "_author")
            lib.authorWebsite = ctx.getStringResourceByName("library_" + name + "_authorWebsite")
            lib.libraryDescription = insertVariables(ctx.getStringResourceByName("library_" + name + "_libraryDescription"), customVariables)
            lib.libraryVersion = ctx.getStringResourceByName("library_" + name + "_libraryVersion")
            lib.libraryArtifactId = ctx.getStringResourceByName("library_" + name + "_libraryArtifactId")
            lib.libraryWebsite = ctx.getStringResourceByName("library_" + name + "_libraryWebsite")

            val licenseIds = ctx.getStringResourceByName("library_" + name + "_licenseIds")
            val legacyLicenseId = ctx.getStringResourceByName("library_" + name + "_licenseId")
            if (licenseIds.isBlank() && legacyLicenseId.isBlank()) {
                val license = License("",
                        ctx.getStringResourceByName("library_" + name + "_licenseVersion"),
                        ctx.getStringResourceByName("library_" + name + "_licenseLink"),
                        insertVariables(ctx.getStringResourceByName("library_" + name + "_licenseContent"), customVariables),
                        insertVariables(ctx.getStringResourceByName("library_" + name + "_licenseContent"), customVariables)
                )
                lib.licenses = setOf(license)
            } else {
                val licenses = mutableSetOf<License>()
                (if (licenseIds.isBlank()) listOf(legacyLicenseId) else licenseIds.split(",")).onEach { licenseId ->
                    var license = getLicense(licenseId)
                    if (license != null) {
                        license = license.copy()
                        license.licenseShortDescription = insertVariables(license.licenseShortDescription, customVariables)
                        license.licenseDescription = insertVariables(license.licenseDescription, customVariables)
                        licenses.add(license)
                    } else {
                        licenses.add(License("", licenseId, "", "", ""))
                    }
                }
                lib.licenses = licenses
            }

            lib.isOpenSource = java.lang.Boolean.valueOf(ctx.getStringResourceByName("library_" + name + "_isOpenSource"))
            lib.repositoryLink = ctx.getStringResourceByName("library_" + name + "_repositoryLink")

            lib.classPath = ctx.getStringResourceByName("library_" + name + "_classPath")

            return if (lib.libraryName.isBlank() && lib.libraryDescription.isBlank()) {
                null
            } else {
                lib
            }
        } catch (ex: Exception) {
            Log.e("aboutlibraries", "Failed to generateLibrary from file: $ex")
            return null
        }
    }

    /**
     * @param libraryName
     * @return
     */
    fun getCustomVariables(ctx: Context, libraryName: String): HashMap<String, String> {
        val customVariables = HashMap<String, String>()

        val customVariablesString = sequenceOf(DEFINE_PLUGIN)
                .map { ctx.getStringResourceByName("$it$libraryName") }
                .filter { it.isNotBlank() }
                .firstOrNull()
                ?: ""

        if (customVariablesString.isNotEmpty()) {
            val customVariableArray = customVariablesString.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (customVariableArray.isNotEmpty()) {
                for (customVariableKey in customVariableArray) {
                    val customVariableContent = ctx.getStringResourceByName("library_" + libraryName + "_" + customVariableKey)
                    if (customVariableContent.isNotEmpty()) {
                        customVariables[customVariableKey] = customVariableContent
                    }
                }
            }
        }

        return customVariables
    }

    fun insertVariables(insertIntoVar: String, variables: HashMap<String, String>): String {
        var insertInto = insertIntoVar
        for ((key, value) in variables) {
            if (value.isNotEmpty()) {
                insertInto = insertInto.replace("<<<" + key.toUpperCase(Locale.US) + ">>>", value)
            }
        }

        //remove the placeholder chars so the license is shown correct
        insertInto = insertInto.replace("<<<", "")
        insertInto = insertInto.replace(">>>", "")

        return insertInto
    }

    /**
     * @param modifications
     */
    fun modifyLibraries(modifications: HashMap<String, HashMap<String, String>>?) {
        modifications ?: return
        for ((key1, value1) in modifications) {
            var foundLibs: List<Library>? = findInExternalLibrary(key1, true, 1)
            if (foundLibs == null || foundLibs.isEmpty()) {
                foundLibs = findInInternalLibrary(key1, true, 1)
            }

            if (foundLibs.size == 1) {
                val lib = foundLibs[0]
                for ((key2, value) in value1) {
                    @Suppress("DEPRECATION")
                    when (key2.toUpperCase(Locale.US)) {
                        LibraryFields.AUTHOR_NAME.name -> {
                            lib.author = value
                        }
                        LibraryFields.AUTHOR_WEBSITE.name -> {
                            lib.authorWebsite = value
                        }
                        LibraryFields.LIBRARY_NAME.name -> {
                            lib.libraryName = value
                        }
                        LibraryFields.LIBRARY_DESCRIPTION.name -> {
                            lib.libraryDescription = value
                        }
                        LibraryFields.LIBRARY_VERSION.name -> {
                            lib.libraryVersion = value
                        }
                        LibraryFields.LIBRARY_ARTIFACT_ID.name -> {
                            lib.libraryArtifactId = value
                        }
                        LibraryFields.LIBRARY_WEBSITE.name -> {
                            lib.libraryWebsite = value
                        }
                        LibraryFields.LIBRARY_OPEN_SOURCE.name -> {
                            lib.isOpenSource = java.lang.Boolean.parseBoolean(value)
                        }
                        LibraryFields.LIBRARY_REPOSITORY_LINK.name -> {
                            lib.repositoryLink = value
                        }
                        LibraryFields.LIBRARY_CLASSPATH.name -> {
                            //note this can be set but won't probably work for autodetect
                            lib.classPath = value
                        }
                        LibraryFields.LICENSE_NAME.name -> {
                            if (lib.license == null) {
                                lib.license = License("", "", "", "", "")
                            }
                            lib.license?.licenseName = value
                        }
                        LibraryFields.LICENSE_SHORT_DESCRIPTION.name -> {
                            if (lib.license == null) {
                                lib.license = License("", "", "", "", "")
                            }
                            lib.license?.licenseShortDescription = value
                        }
                        LibraryFields.LICENSE_DESCRIPTION.name -> {
                            if (lib.license == null) {
                                lib.license = License("", "", "", "", "")
                            }
                            lib.license?.licenseDescription = value
                        }
                        LibraryFields.LICENSE_WEBSITE.name -> {
                            if (lib.license == null) {
                                lib.license = License("", "", "", "", "")
                            }
                            lib.license?.licenseWebsite = value
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val BUNDLE_TITLE = "ABOUT_LIBRARIES_TITLE"
        const val BUNDLE_EDGE_TO_EDGE = "ABOUT_LIBRARIES_EDGE_TO_EDGE"

        private const val DEFINE_LICENSE = "define_license_"
        private const val DEFINE_PLUGIN = "define_plu_"
        internal const val DEFINE_EXT = "define_"

        fun classFields(rClass: Class<*>): Array<String> = rClass.fields.toStringArray()
    }
}
