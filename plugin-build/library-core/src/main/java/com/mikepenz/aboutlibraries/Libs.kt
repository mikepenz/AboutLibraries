@file:JvmName("Libs")

package com.mikepenz.aboutlibraries

import android.content.Context
import android.util.Log
import com.mikepenz.aboutlibraries.detector.Detect
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.*
import java.util.*
import kotlin.collections.ArrayList

class Libs(
        context: Context,
        fields: Array<String> = context.getFields(),
        libraryEnchantments: Map<String, String> = emptyMap()
) {

    private var usedGradlePlugin = false
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
        val foundInternalLibraryIdentifiers = ArrayList<String>()
        val foundExternalLibraryIdentifiers = ArrayList<String>()
        val foundPluginLibraryIdentifiers = ArrayList<String>()

        for (field in fields) {
            when {
                field.startsWith(DEFINE_LICENSE) -> foundLicenseIdentifiers.add(field.replace(DEFINE_LICENSE, ""))
                field.startsWith(DEFINE_INT) -> foundInternalLibraryIdentifiers.add(field.replace(DEFINE_INT, ""))
                field.startsWith(DEFINE_PLUGIN) -> foundPluginLibraryIdentifiers.add(field.replace(DEFINE_PLUGIN, ""))
                field.startsWith(DEFINE_EXT) -> foundExternalLibraryIdentifiers.add(field.replace(DEFINE_EXT, ""))
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
            usedGradlePlugin = true

            val enchantWithKey = libraryEnchantments[pluginLibraryIdentifier] ?: continue
            val enchantWith = genLibrary(context, enchantWithKey) ?: continue
            library.enchantBy(enchantWith)
        }

        // if we used the gradle plugin to resolve libraries, only use those
        if (foundPluginLibraryIdentifiers.isEmpty()) {
            //add internal libs
            for (internalIdentifier in foundInternalLibraryIdentifiers) {
                val library = genLibrary(context, internalIdentifier) ?: continue
                library.isInternal = true
                internLibraries.add(library)
            }

            //add external libs
            for (externalIdentifier in foundExternalLibraryIdentifiers) {
                val library = genLibrary(context, externalIdentifier) ?: continue
                library.isInternal = false
                externLibraries.add(library)
            }
        }
    }

    /**
     * This will summarize all libraries and eliminate duplicates
     *
     * @param internalLibraries    the String[] with the internalLibraries (if set manual)
     * @param excludeLibraries     the String[] with the libs to be excluded
     * @param autoDetect           defines if the libraries should be resolved by their classpath (if possible)
     * @param checkCachedDetection defines if we should check the cached autodetected libraries (per version) (default: enabled)
     * @param sort                 defines if the array should be sorted
     * @return the summarized list of included Libraries
     */
    fun prepareLibraries(ctx: Context, internalLibraries: Array<out String> = emptyArray(), excludeLibraries: Array<out String> = emptyArray(), autoDetect: Boolean = true, checkCachedDetection: Boolean = true, sort: Boolean = true): ArrayList<Library> {
        val isExcluding = excludeLibraries.isNotEmpty()
        val libraries = HashMap<String, Library>()
        val resultLibraries = ArrayList<Library>()

        if (!usedGradlePlugin && autoDetect) {
            val autoDetected = getAutoDetectedLibraries(ctx, checkCachedDetection)
            resultLibraries.addAll(autoDetected)

            if (isExcluding) {
                for (lib in autoDetected) {
                    libraries[lib.definedName] = lib
                }
            }
        }

        //Add all external libraries
        val extern = getExternLibraries()
        resultLibraries.addAll(extern)

        if (isExcluding) {
            for (lib in extern) {
                libraries[lib.definedName] = lib
            }
        }

        //Now add all libs which do not contain the info file, but are in the AboutLibraries lib
        if (internalLibraries.isNotEmpty()) {
            for (internalLibrary in internalLibraries) {
                val lib = getLibrary(internalLibrary) ?: continue
                resultLibraries.add(lib)
                libraries[lib.definedName] = lib
            }
        }

        //remove libraries which should be excluded
        if (isExcluding) {
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
     * Get all autoDetected Libraries
     *
     * @param ctx                  the current context
     * @param checkCachedDetection defines if we should check the cached autodetected libraries (per version) (default: enabled)
     * @return an ArrayList Library with all found libs by their classpath
     */
    fun getAutoDetectedLibraries(ctx: Context, checkCachedDetection: Boolean): List<Library> {
        val pi = ctx.getPackageInfo()
        val sharedPreferences = ctx.getSharedPreferences("aboutLibraries", Context.MODE_PRIVATE)
        val lastCacheVersion = sharedPreferences.getInt("versionCode", -1)
        val isCacheUpToDate = pi != null && lastCacheVersion == pi.versionCode

        if (checkCachedDetection) { //Retrieve from cache if up to date
            if (pi != null && isCacheUpToDate) {
                val autoDetectedLibraries = sharedPreferences.getString("autoDetectedLibraries", "")?.split(DELIMITER.toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()

                if (autoDetectedLibraries?.isNotEmpty() == true) {
                    val libraries = ArrayList<Library>(autoDetectedLibraries.size)
                    for (autoDetectedLibrary in autoDetectedLibraries) {
                        val lib = getLibrary(autoDetectedLibrary) ?: continue
                        libraries.add(lib)
                    }
                    return libraries
                }
            }
        }

        val libraries = Detect.detect(ctx, libraries)
        if (pi != null && !isCacheUpToDate) { //Update cache
            val autoDetectedLibrariesPref = StringBuilder()

            for (lib in libraries) {
                autoDetectedLibrariesPref.append(DELIMITER).append(lib.definedName)
            }

            sharedPreferences.edit()
                    .putInt("versionCode", pi.versionCode)
                    .putString("autoDetectedLibraries", autoDetectedLibrariesPref.toString())
                    .apply()
        }

        return libraries
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
     * @param searchTerm
     * @param idOnly
     * @param limit
     * @return
     */
    fun findInInternalLibrary(searchTerm: String, idOnly: Boolean, limit: Int): List<Library> {
        return find(getInternLibraries(), searchTerm, idOnly, limit)
    }

    /**
     * @param searchTerm
     * @param idOnly
     * @param limit
     * @return
     */
    fun findInExternalLibrary(searchTerm: String, idOnly: Boolean, limit: Int): List<Library> {
        return find(getExternLibraries(), searchTerm, idOnly, limit)
    }

    /**
     * @param libraries
     * @param searchTerm
     * @param idOnly
     * @param limit
     * @return
     */
    private fun find(libraries: List<Library>, searchTerm: String, idOnly: Boolean, limit: Int): List<Library> {
        val localLibs = ArrayList<Library>()

        var count = 0
        for (library in libraries) {
            if (idOnly) {
                if (library.definedName.contains(searchTerm, true)) {
                    localLibs.add(library)
                    count += 1

                    if (limit != -1 && limit < count) {
                        break
                    }
                }
            } else {
                if (library.libraryName.contains(searchTerm, true) || library.definedName.contains(searchTerm, true)) {
                    localLibs.add(library)
                    count += 1

                    if (limit != -1 && limit < count) {
                        break
                    }
                }
            }
        }

        return localLibs
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

        val customVariablesString = sequenceOf(DEFINE_EXT, DEFINE_INT, DEFINE_PLUGIN)
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
        private const val DEFINE_INT = "define_int_"
        private const val DEFINE_PLUGIN = "define_plu_"
        internal const val DEFINE_EXT = "define_"

        private const val DELIMITER = ";"

        fun classFields(rClass: Class<*>): Array<String> = rClass.fields.toStringArray()
    }
}
