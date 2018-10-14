@file:JvmName("Libs")

package com.mikepenz.aboutlibraries

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.mikepenz.aboutlibraries.detector.Detect
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.getFields
import com.mikepenz.aboutlibraries.util.getPackageInfo
import java.util.*
import kotlin.collections.ArrayList

public class Libs {

    private val internLibraries = ArrayList<Library>()
    private val externLibraries = ArrayList<Library>()
    private val licenses = ArrayList<License>()

    /**
     * Get all available Libraries
     *
     * @return an ArrayList Library with all available Libraries
     */
    val libraries: ArrayList<Library>
        get() {
            val libs = ArrayList<Library>()
            libs.addAll(getInternLibraries())
            libs.addAll(getExternLibraries())
            return libs
        }

    enum class LibraryFields {
        AUTHOR_NAME,
        AUTHOR_WEBSITE,
        LIBRARY_NAME,
        LIBRARY_DESCRIPTION,
        LIBRARY_VERSION,
        LIBRARY_WEBSITE,
        LIBRARY_OPEN_SOURCE,
        LIBRARY_REPOSITORY_LINK,
        LIBRARY_CLASSPATH,
        LICENSE_NAME,
        LICENSE_SHORT_DESCRIPTION,
        LICENSE_DESCRIPTION,
        LICENSE_WEBSITE
    }

    enum class ActivityStyle {
        LIGHT,
        DARK,
        LIGHT_DARK_TOOLBAR
    }

    enum class SpecialButton {
        SPECIAL1,
        SPECIAL2,
        SPECIAL3
    }

    constructor(context: Context) {
        init(context, context.getFields())
    }

    constructor(context: Context, fields: Array<String>?) {
        init(context, fields)
    }

    /**
     * init method
     *
     * @param fields
     */
    private fun init(ctx: Context, fields: Array<String>?) {
        val foundLicenseIdentifiers = ArrayList<String>()
        val foundInternalLibraryIdentifiers = ArrayList<String>()
        val foundExternalLibraryIdentifiers = ArrayList<String>()

        if (fields != null) {
            for (field in fields) {
                if (field.startsWith(DEFINE_LICENSE)) {
                    foundLicenseIdentifiers.add(field.replace(DEFINE_LICENSE, ""))
                } else if (field.startsWith(DEFINE_INT)) {
                    foundInternalLibraryIdentifiers.add(field.replace(DEFINE_INT, ""))
                } else if (field.startsWith(DEFINE_EXT)) {
                    foundExternalLibraryIdentifiers.add(field.replace(DEFINE_EXT, ""))
                }
            }
        }

        // add licenses
        // this has to happen first as the licenses need to be initialized before the libraries are read in
        for (licenseIdentifier in foundLicenseIdentifiers) {
            val license = genLicense(ctx, licenseIdentifier)
            if (license != null) {
                licenses.add(license)
            }
        }
        //add internal libs
        for (internalIdentifier in foundInternalLibraryIdentifiers) {
            val library = genLibrary(ctx, internalIdentifier)
            if (library != null) {
                library.isInternal = true
                internLibraries.add(library)
            }
        }

        //add external libs
        for (externalIdentifier in foundExternalLibraryIdentifiers) {
            val library = genLibrary(ctx, externalIdentifier)
            if (library != null) {
                library.isInternal = false
                externLibraries.add(library)
            }
        }
    }

    /**
     * This will summarize all libraries and elimate duplicates
     *
     * @param internalLibraries    the String[] with the internalLibraries (if set manual)
     * @param excludeLibraries     the String[] with the libs to be excluded
     * @param autoDetect           defines if the libraries should be resolved by their classpath (if possible)
     * @param checkCachedDetection defines if we should check the cached autodetected libraries (per version) (default: enabled)
     * @param sort                 defines if the array should be sorted
     * @return the summarized list of included Libraries
     */
    fun prepareLibraries(ctx: Context, internalLibraries: Array<out String>, excludeLibraries: Array<out String>, autoDetect: Boolean, checkCachedDetection: Boolean, sort: Boolean): ArrayList<Library> {
        val isExcluding = excludeLibraries.isNotEmpty()
        val libraries = HashMap<String, Library>()
        val resultLibraries = ArrayList<Library>()

        if (autoDetect) {
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

        //Now add all libs which do not contains the info file, but are in the AboutLibraries lib
        if (internalLibraries != null) {
            for (internalLibrary in internalLibraries) {
                val lib = getLibrary(internalLibrary)
                if (lib != null) {
                    resultLibraries.add(lib)
                    libraries[lib.definedName] = lib
                }
            }
        }

        //remove libraries which should be excluded
        if (isExcluding) {
            for (excludeLibrary in excludeLibraries) {
                val lib = libraries[excludeLibrary]
                if (lib != null) {
                    resultLibraries.remove(lib)
                }
            }
        }

        if (sort) {
            Collections.sort(resultLibraries)
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

        if (checkCachedDetection) {//Retrieve from cache if up to date
            if (pi != null && isCacheUpToDate) {
                val autoDetectedLibraries = sharedPreferences.getString("autoDetectedLibraries", "")?.split(DELIMITER.toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()

                if (autoDetectedLibraries?.isNotEmpty() == true) {
                    val libraries = ArrayList<Library>(autoDetectedLibraries.size)
                    for (autoDetectedLibrary in autoDetectedLibraries) {
                        val lib = getLibrary(autoDetectedLibrary)
                        if (lib != null) libraries.add(lib)
                    }
                    return libraries
                }
            }
        }

        val libraries = Detect.detect(ctx, libraries)
        if (pi != null && !isCacheUpToDate) {//Update cache
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
     * @param libraryName the name of the lib (NOT case sensitiv) or the real name of the lib (this is the name used for github)
     * @return the found library or null
     */
    fun getLibrary(libraryName: String): Library? {
        for (library in libraries) {
            if (library.libraryName.toLowerCase() == libraryName.toLowerCase()) {
                return library
            } else if (library.definedName.toLowerCase() == libraryName.toLowerCase()) {
                return library
            }
        }
        return null
    }

    /**
     * Find a library by a searchTerm (Limit the results if there are more than one)
     *
     * @param searchTerm the term which is in the libs name (NOT case sensitiv) or the real name of the lib (this is the name used for github)
     * @param limit      -1 for all results or smaller 0 for a limitted result
     * @return an ArrayList Library with the found internLibraries
     */
    fun findLibrary(searchTerm: String, limit: Int): ArrayList<Library> {
        return find(libraries, searchTerm, false, limit)
    }

    /**
     * @param searchTerm
     * @param idOnly
     * @param limit
     * @return
     */
    fun findInInternalLibrary(searchTerm: String, idOnly: Boolean, limit: Int): ArrayList<Library> {
        return find(getInternLibraries(), searchTerm, idOnly, limit)
    }

    /**
     * @param searchTerm
     * @param idOnly
     * @param limit
     * @return
     */
    fun findInExternalLibrary(searchTerm: String, idOnly: Boolean, limit: Int): ArrayList<Library> {
        return find(getExternLibraries(), searchTerm, idOnly, limit)
    }

    /**
     * @param libraries
     * @param searchTerm
     * @param idOnly
     * @param limit
     * @return
     */
    private fun find(libraries: ArrayList<Library>, searchTerm: String, idOnly: Boolean, limit: Int): ArrayList<Library> {
        val localLibs = ArrayList<Library>()

        var count = 0
        for (library in libraries) {
            if (idOnly) {
                if (library.definedName.toLowerCase().contains(searchTerm.toLowerCase())) {
                    localLibs.add(library)
                    count = count + 1

                    if (limit != -1 && limit < count) {
                        break
                    }
                }
            } else {
                if (library.libraryName.toLowerCase().contains(searchTerm.toLowerCase()) || library.definedName.toLowerCase().contains(searchTerm.toLowerCase())) {
                    localLibs.add(library)
                    count = count + 1

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
            if (license.licenseName.toLowerCase() == licenseName.toLowerCase()) {
                return license
            } else if (license.definedName.toLowerCase() == licenseName.toLowerCase()) {
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
        var licenseName = licenseName
        licenseName = licenseName.replace("-", "_")

        return try {
            License(
                    licenseName,
                    getStringResourceByName(ctx, "license_" + licenseName + "_licenseName"),
                    getStringResourceByName(ctx, "license_" + licenseName + "_licenseWebsite"),
                    getStringResourceByName(ctx, "license_" + licenseName + "_licenseShortDescription"),
                    getStringResourceByName(ctx, "license_" + licenseName + "_licenseDescription")
            )
        } catch (ex: Exception) {
            Log.e("aboutlibraries", "Failed to generateLicense from file: " + ex.toString())
            null
        }

    }

    /**
     * @param libraryName
     * @return
     */
    private fun genLibrary(ctx: Context, libraryName: String): Library? {
        var libraryName = libraryName
        libraryName = libraryName.replace("-", "_")

        try {
            val lib = Library(definedName = libraryName, libraryName = getStringResourceByName(ctx, "library_" + libraryName + "_libraryName"))

            //Get custom vars to insert into defined areas
            val customVariables = getCustomVariables(ctx, libraryName)

            lib.author = getStringResourceByName(ctx, "library_" + libraryName + "_author")
            lib.authorWebsite = getStringResourceByName(ctx, "library_" + libraryName + "_authorWebsite")
            lib.libraryDescription = insertVariables(getStringResourceByName(ctx, "library_" + libraryName + "_libraryDescription"), customVariables)
            lib.libraryVersion = getStringResourceByName(ctx, "library_" + libraryName + "_libraryVersion")
            lib.libraryWebsite = getStringResourceByName(ctx, "library_" + libraryName + "_libraryWebsite")

            val licenseId = getStringResourceByName(ctx, "library_" + libraryName + "_licenseId")
            if (TextUtils.isEmpty(licenseId)) {
                val license = License("",
                        getStringResourceByName(ctx, "library_" + libraryName + "_licenseVersion"),
                        getStringResourceByName(ctx, "library_" + libraryName + "_licenseLink"),
                        insertVariables(getStringResourceByName(ctx, "library_" + libraryName + "_licenseContent"), customVariables),
                        insertVariables(getStringResourceByName(ctx, "library_" + libraryName + "_licenseContent"), customVariables)
                )
                lib.license = license
            } else {
                var license = getLicense(licenseId)
                if (license != null) {
                    license = license.copy()
                    license.licenseShortDescription = insertVariables(license.licenseShortDescription, customVariables)
                    license.licenseDescription = insertVariables(license.licenseDescription, customVariables)
                    lib.license = license
                }
            }

            lib.isOpenSource = java.lang.Boolean.valueOf(getStringResourceByName(ctx, "library_" + libraryName + "_isOpenSource"))
            lib.repositoryLink = getStringResourceByName(ctx, "library_" + libraryName + "_repositoryLink")

            lib.classPath = getStringResourceByName(ctx, "library_" + libraryName + "_classPath")

            return if (TextUtils.isEmpty(lib.libraryName) && TextUtils.isEmpty(lib.libraryDescription)) {
                null
            } else lib

        } catch (ex: Exception) {
            Log.e("aboutlibraries", "Failed to generateLibrary from file: " + ex.toString())
            return null
        }

    }

    /**
     * @param libraryName
     * @return
     */
    fun getCustomVariables(ctx: Context, libraryName: String): HashMap<String, String> {
        val customVariables = HashMap<String, String>()

        var customVariablesString = getStringResourceByName(ctx, DEFINE_EXT + libraryName)
        if (TextUtils.isEmpty(customVariablesString)) {
            customVariablesString = getStringResourceByName(ctx, DEFINE_INT + libraryName)
        }

        if (!TextUtils.isEmpty(customVariablesString)) {
            val customVariableArray = customVariablesString.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (customVariableArray.isNotEmpty()) {
                for (customVariableKey in customVariableArray) {
                    val customVariableContent = getStringResourceByName(ctx, "library_" + libraryName + "_" + customVariableKey)
                    if (!TextUtils.isEmpty(customVariableContent)) {
                        customVariables[customVariableKey] = customVariableContent
                    }
                }
            }
        }

        return customVariables
    }

    fun insertVariables(insertInto: String, variables: HashMap<String, String>): String {
        var insertInto = insertInto
        for ((key, value) in variables) {
            if (!TextUtils.isEmpty(value)) {
                insertInto = insertInto.replace("<<<" + key.toUpperCase() + ">>>", value)
            }
        }

        //remove the placeholder chars so the license is shown correct
        insertInto = insertInto.replace("<<<", "")
        insertInto = insertInto.replace(">>>", "")

        return insertInto
    }

    fun getStringResourceByName(ctx: Context, aString: String): String {
        val packageName = ctx.packageName

        val resId = ctx.resources.getIdentifier(aString, "string", packageName)
        return if (resId == 0) {
            ""
        } else {
            ctx.getString(resId)
        }
    }


    /**
     * @param modifications
     */
    fun modifyLibraries(modifications: HashMap<String, HashMap<String, String>>?) {
        if (modifications != null) {
            for ((key1, value1) in modifications) {
                var foundLibs: ArrayList<Library>? = findInExternalLibrary(key1, true, 1)
                if (foundLibs == null || foundLibs.size == 0) {
                    foundLibs = findInInternalLibrary(key1, true, 1)
                }

                if (foundLibs != null && foundLibs.size == 1) {
                    val lib = foundLibs[0]
                    for ((key2, value) in value1) {
                        val key = key2.toUpperCase()

                        if (key == LibraryFields.AUTHOR_NAME.name) {
                            lib.author = value
                        } else if (key == LibraryFields.AUTHOR_WEBSITE.name) {
                            lib.authorWebsite = value
                        } else if (key == LibraryFields.LIBRARY_NAME.name) {
                            lib.libraryName = value
                        } else if (key == LibraryFields.LIBRARY_DESCRIPTION.name) {
                            lib.libraryDescription = value
                        } else if (key == LibraryFields.LIBRARY_VERSION.name) {
                            lib.libraryVersion = value
                        } else if (key == LibraryFields.LIBRARY_WEBSITE.name) {
                            lib.libraryWebsite = value
                        } else if (key == LibraryFields.LIBRARY_OPEN_SOURCE.name) {
                            lib.isOpenSource = java.lang.Boolean.parseBoolean(value)
                        } else if (key == LibraryFields.LIBRARY_REPOSITORY_LINK.name) {
                            lib.repositoryLink = value
                        } else if (key == LibraryFields.LIBRARY_CLASSPATH.name) {
                            //note this can be set but won't probably work for autodetect
                            lib.classPath = value
                        } else if (key == LibraryFields.LICENSE_NAME.name) {
                            if (lib.license == null) {
                                lib.license = License("", "", "", "", "")
                            }
                            lib.license?.licenseName = value
                        } else if (key == LibraryFields.LICENSE_SHORT_DESCRIPTION.name) {
                            if (lib.license == null) {
                                lib.license = License("", "", "", "", "")
                            }
                            lib.license?.licenseShortDescription = value
                        } else if (key == LibraryFields.LICENSE_DESCRIPTION.name) {
                            if (lib.license == null) {
                                lib.license = License("", "", "", "", "")
                            }
                            lib.license?.licenseDescription = value
                        } else if (key == LibraryFields.LICENSE_WEBSITE.name) {
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

        val BUNDLE_THEME = "ABOUT_LIBRARIES_THEME"
        val BUNDLE_TITLE = "ABOUT_LIBRARIES_TITLE"
        val BUNDLE_STYLE = "ABOUT_LIBRARIES_STYLE"
        val BUNDLE_COLORS = "ABOUT_COLOR"

        private val DEFINE_LICENSE = "define_license_"
        private val DEFINE_INT = "define_int_"
        internal val DEFINE_EXT = "define_"

        private val DELIMITER = ";"
    }
}