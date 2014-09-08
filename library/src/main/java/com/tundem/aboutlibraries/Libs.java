package com.tundem.aboutlibraries;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.util.Log;

import com.tundem.aboutlibraries.detector.Detect;
import com.tundem.aboutlibraries.entity.Library;
import com.tundem.aboutlibraries.entity.License;
import com.tundem.aboutlibraries.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Libs {
    public static final String BUNDLE_FIELDS = "ABOUT_LIBRARIES_FIELDS";
    public static final String BUNDLE_LIBS = "ABOUT_LIBRARIES_LIBS";
    public static final String BUNDLE_EXCLUDE_LIBS = "ABOUT_LIBRARIES_EXCLUDE_LIBS";
    public static final String BUNDLE_AUTODETECT = "ABOUT_LIBRARIES_AUTODETECT";
    public static final String BUNDLE_SORT = "ABOUT_LIBRARIES_SORT";

    public static final String BUNDLE_LICENSE = "ABOUT_LIBRARIES_LICENSE";
    public static final String BUNDLE_LICENSE_DIALOG = "ABOUT_LIBRARIES_LICENSE_DIALOG";
    public static final String BUNDLE_VERSION = "ABOUT_LIBRARIES_VERSION";

    public static final String BUNDLE_THEME = "ABOUT_LIBRARIES_THEME";
    public static final String BUNDLE_TITLE = "ABOUT_LIBRARIES_TITLE";
    public static final String BUNDLE_ACCENT_COLOR = "ABOUT_LIBRARIES_ACCENT_COLOR";
    public static final String BUNDLE_TRANSLUCENT_DECOR = "ABOUT_LIBRARIES_TRANSLUCENT_DECOR";

    private static final String DEFINE_LICENSE = "define_license_";
    private static final String DEFINE_INT = "define_int_";
    private static final String DEFINE_EXT = "define_";

    private static Context ctx;
    private static Libs libs = null;

    private ArrayList<Library> internLibraries = new ArrayList<Library>();
    private ArrayList<Library> externLibraries = new ArrayList<Library>();
    private ArrayList<License> licenses = new ArrayList<License>();

    private Libs() {
        String[] fields = toStringArray(R.string.class.getFields());
        init(fields);
    }

    private Libs(String[] fields) {
        init(fields);
    }

    /**
     * init method
     *
     * @param fields
     */
    private void init(String[] fields) {
        ArrayList<String> foundLicenseIdentifiers = new ArrayList<String>();
        ArrayList<String> foundInternalLibraryIdentifiers = new ArrayList<String>();
        ArrayList<String> foundExternalLibraryIdentifiers = new ArrayList<String>();

        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].startsWith(DEFINE_LICENSE)) {
                    foundLicenseIdentifiers.add(fields[i].replace(DEFINE_LICENSE, ""));
                } else if (fields[i].startsWith(DEFINE_INT)) {
                    foundInternalLibraryIdentifiers.add(fields[i].replace(DEFINE_INT, ""));
                } else if (fields[i].startsWith(DEFINE_EXT)) {
                    foundExternalLibraryIdentifiers.add(fields[i].replace(DEFINE_EXT, ""));
                }
            }
        }

        //add licenses
        for (String licenseIdentifier : foundLicenseIdentifiers) {
            License license = genLicense(licenseIdentifier);
            if (license != null) {
                licenses.add(license);
            }
        }
        //add internal libs
        for (String internalIdentifier : foundInternalLibraryIdentifiers) {
            Library library = genLibrary(internalIdentifier);
            if (library != null) {
                library.setInternal(true);
                internLibraries.add(library);
            }
        }

        //add external libs
        for (String externalIdentifier : foundExternalLibraryIdentifiers) {
            Library library = genLibrary(externalIdentifier);
            if (library != null) {
                library.setInternal(false);
                externLibraries.add(library);
            }
        }
    }


    /**
     * A helper method to get a String[] out of a fieldArray
     *
     * @param fields R.strings.class.getFields()
     * @return a String[] with the string ids we need
     */
    public static String[] toStringArray(Field[] fields) {
        ArrayList<String> fieldArray = new ArrayList<String>();
        for (Field field : fields) {
            if (field.getName().contains(DEFINE_EXT)) {
                fieldArray.add(field.getName());
            }
        }
        return fieldArray.toArray(new String[fieldArray.size()]);
    }

    /**
     * Creates a new Instance of Libs. This only includes internal internLibraries
     *
     * @param context
     * @return
     */
    public static Libs getInstance(Context context) {
        ctx = context;
        if (libs == null) {
            libs = new Libs();
        }
        return libs;
    }

    /**
     * Creates a new Instance of Libs. This give fields[] also contains the ressources of any lib you've used
     * Retrieve the fields[] by calling following method: Field[] fields = R.string.class.getFields();
     *
     * @param context
     * @param fields
     * @return
     */
    public static Libs getInstance(Context context, String[] fields) {
        ctx = context;
        if (libs == null) {
            libs = new Libs(fields);
        }
        return libs;
    }


    /**
     * This will summarize all libraries and elimate duplicates
     *
     * @param internalLibraries the String[] with the internalLibraries (if set manual)
     * @param excludeLibraries  the String[] with the libs to be excluded
     * @param autoDetect        defines if the libraries should be resolved by their classpath (if possible)
     * @param sort              defines if the array should be sorted
     * @return the summarized list of included Libraries
     */
    public ArrayList<Library> prepareLibraries(String[] internalLibraries, String[] excludeLibraries, boolean autoDetect, boolean sort) {
        HashMap<String, Library> libraries = new HashMap<String, Library>();

        if (autoDetect) {
            for (Library lib : libs.getAutoDetectedLibraries()) {
                libraries.put(lib.getDefinedName(), lib);
            }
        }

        //Add all external libraries
        for (Library lib : libs.getExternLibraries()) {
            libraries.put(lib.getDefinedName(), lib);
        }

        //Now add all libs which do not contains the info file, but are in the AboutLibraries lib
        if (internalLibraries != null) {
            for (String internalLibrary : internalLibraries) {
                Library lib = libs.getLibrary(internalLibrary);
                if (lib != null) {
                    libraries.put(lib.getDefinedName(), lib);
                }
            }
        }

        ArrayList<Library> resultLibraries = new ArrayList<Library>(libraries.values());

        //remove libraries which should be excluded
        if (excludeLibraries != null) {
            List<Library> libsToRemove = new ArrayList<Library>();
            for (String excludeLibrary : excludeLibraries) {
                for (Library library : resultLibraries) {
                    if (library.getDefinedName().equals(excludeLibrary)) {
                        libsToRemove.add(library);
                        break;
                    }
                }
            }
            for (Library libToRemove : libsToRemove) {
                resultLibraries.remove(libToRemove);
            }
        }

        if (sort) {
            Collections.sort(resultLibraries);
        }
        return resultLibraries;
    }

    /**
     * Get all autoDetected Libraries
     *
     * @return an ArrayList<Library> with all found libs by their classpath
     */
    public ArrayList<Library> getAutoDetectedLibraries() {
        ArrayList<Library> libraries = new ArrayList<Library>();

        PackageInfo pi = Util.getPackageInfo(ctx);
        if (pi != null) {
            String[] autoDetectedLibraries = ctx.getSharedPreferences("aboutLibraries_" + pi.versionCode, Context.MODE_PRIVATE).getString("autoDetectedLibraries", "").split(";");

            if (autoDetectedLibraries.length > 0) {
                for (String autoDetectedLibrary : autoDetectedLibraries) {
                    Library lib = getLibrary(autoDetectedLibrary);
                    if (lib != null) {
                        libraries.add(lib);
                    }
                }
            }
        }

        if (libraries.size() == 0) {
            String delimiter = "";
            String autoDetectedLibrariesPref = "";
            for (Library lib : Detect.detect(ctx, getLibraries())) {
                libraries.add(lib);

                autoDetectedLibrariesPref = autoDetectedLibrariesPref + delimiter + lib.getDefinedName();
                delimiter = ";";
            }

            if (pi != null) {
                ctx.getSharedPreferences("aboutLibraries_" + pi.versionCode, Context.MODE_PRIVATE).edit().putString("autoDetectedLibraries", autoDetectedLibrariesPref).commit();
            }
        }

        return libraries;
    }

    /**
     * Get all intern available Libraries
     *
     * @return an ArrayList<Library> with all available internLibraries
     */
    public ArrayList<Library> getInternLibraries() {
        return new ArrayList<Library>(internLibraries);
    }

    /**
     * Get all extern available Libraries
     *
     * @return an ArrayList<Library> with all available externLibraries
     */
    public ArrayList<Library> getExternLibraries() {
        return new ArrayList<Library>(externLibraries);
    }

    /**
     * Get all available licenses
     *
     * @return an ArrayLIst<License> with all available Licenses
     */
    public ArrayList<License> getLicenses() {
        return new ArrayList<License>(licenses);
    }

    /**
     * Get all available Libraries
     *
     * @return an ArrayList<Library> with all available Libraries
     */
    public ArrayList<Library> getLibraries() {
        ArrayList<Library> libs = new ArrayList<Library>();
        libs.addAll(getInternLibraries());
        libs.addAll(getExternLibraries());
        return libs;
    }

    /**
     * Get a library by its name (the name must be equal)
     *
     * @param libraryName the name of the lib (NOT case sensitiv) or the real name of the lib (this is the name used for github)
     * @return the found library or null
     */
    public Library getLibrary(String libraryName) {
        for (Library library : getLibraries()) {
            if (library.getLibraryName().toLowerCase().equals(libraryName.toLowerCase())) {
                return library;
            } else if (library.getDefinedName().toLowerCase().equals(libraryName.toLowerCase())) {
                return library;
            }
        }
        return null;
    }

    /**
     * Find a library by a searchTerm (Limit the results if there are more than one)
     *
     * @param searchTerm the term which is in the libs name (NOT case sensitiv) or the real name of the lib (this is the name used for github)
     * @param limit      -1 for all results or > 0 for a limitted result
     * @return an ArrayList<Library> with the found internLibraries
     */
    public ArrayList<Library> findLibrary(String searchTerm, int limit) {
        ArrayList<Library> localLibs = new ArrayList<Library>();

        int count = 0;
        for (Library library : getLibraries()) {
            if (library.getLibraryName().toLowerCase().contains(searchTerm.toLowerCase()) || library.getDefinedName().toLowerCase().contains(searchTerm.toLowerCase())) {
                localLibs.add(library);
                count = count + 1;

                if (limit != -1 && limit < count) {
                    break;
                }
            }
        }

        return localLibs;
    }


    public License getLicense(String licenseName) {
        for (License license : getLicenses()) {
            if (license.getLicenseName().toLowerCase().equals(licenseName.toLowerCase())) {
                return license;
            } else if (license.getDefinedName().toLowerCase().equals(licenseName.toLowerCase())) {
                return license;
            }
        }
        return null;
    }

    private License genLicense(String licenseName) {
        licenseName = licenseName.replace("-", "_");

        try {
            License lic = new License();
            lic.setDefinedName(licenseName);
            lic.setLicenseName(getStringResourceByName("license_" + licenseName + "_licenseName"));
            lic.setLicenseWebsite(getStringResourceByName("license_" + licenseName + "_licenseWebsite"));
            lic.setLicenseShortDescription(getStringResourceByName("license_" + licenseName + "_licenseShortDescription"));
            lic.setLicenseDescription(getStringResourceByName("license_" + licenseName + "_licenseDescription"));
            return lic;
        } catch (Exception ex) {
            Log.e("com.tundem.aboutlibraries", "Failed to generateLicense from file: " + ex.toString());
            return null;
        }
    }

    private Library genLibrary(String libraryName) {
        libraryName = libraryName.replace("-", "_");

        try {
            Library lib = new Library();

            //Get custom vars to insert into defined areas
            HashMap<String, String> customVariables = getCustomVariables(libraryName);

            lib.setDefinedName(libraryName);
            lib.setAuthor(getStringResourceByName("library_" + libraryName + "_author"));
            lib.setAuthorWebsite(getStringResourceByName("library_" + libraryName + "_authorWebsite"));
            lib.setLibraryName(getStringResourceByName("library_" + libraryName + "_libraryName"));
            lib.setLibraryDescription(insertVariables(getStringResourceByName("library_" + libraryName + "_libraryDescription"), customVariables));
            lib.setLibraryVersion(getStringResourceByName("library_" + libraryName + "_libraryVersion"));
            lib.setLibraryWebsite(getStringResourceByName("library_" + libraryName + "_libraryWebsite"));

            String licenseId = getStringResourceByName("library_" + libraryName + "_licenseId");
            if (TextUtils.isEmpty(licenseId)) {
                License license = new License();
                license.setLicenseName(getStringResourceByName("library_" + libraryName + "_licenseVersion"));
                license.setLicenseWebsite(getStringResourceByName("library_" + libraryName + "_licenseLink"));
                license.setLicenseShortDescription(insertVariables(getStringResourceByName("library_" + libraryName + "_licenseContent"), customVariables));
                lib.setLicense(license);
            } else {
                License license = getLicense(licenseId);
                if (license != null) {
                    license = license.copy();
                    license.setLicenseShortDescription(insertVariables(license.getLicenseShortDescription(), customVariables));
                    license.setLicenseDescription(insertVariables(license.getLicenseDescription(), customVariables));
                    lib.setLicense(license);
                }
            }

            lib.setOpenSource(Boolean.valueOf(getStringResourceByName("library_" + libraryName + "_isOpenSource")));
            lib.setRepositoryLink(getStringResourceByName("library_" + libraryName + "_repositoryLink"));

            lib.setClassPath(getStringResourceByName("library_" + libraryName + "_classPath"));

            if (TextUtils.isEmpty(lib.getLibraryName()) && TextUtils.isEmpty(lib.getLibraryDescription())) {
                return null;
            }

            return lib;
        } catch (Exception ex) {
            Log.e("com.tundem.aboutlibraries", "Failed to generateLibrary from file: " + ex.toString());
            return null;
        }
    }

    public HashMap<String, String> getCustomVariables(String libraryName) {
        HashMap<String, String> customVariables = new HashMap<String, String>();

        String customVariablesString = getStringResourceByName(DEFINE_EXT + libraryName);
        if (TextUtils.isEmpty(customVariablesString)) {
            customVariablesString = getStringResourceByName(DEFINE_INT + libraryName);
        }

        if (!TextUtils.isEmpty(customVariablesString)) {
            String[] customVariableArray = customVariablesString.split(";");
            if (customVariableArray.length > 0) {
                for (String customVariableKey : customVariableArray) {
                    String customVariableContent = getStringResourceByName("library_" + libraryName + "_" + customVariableKey);
                    if (!TextUtils.isEmpty(customVariableContent)) {
                        customVariables.put(customVariableKey, customVariableContent);
                    }
                }
            }
        }

        return customVariables;
    }

    public String insertVariables(String insertInto, HashMap<String, String> variables) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            insertInto = insertInto.replace("<<<" + entry.getKey().toUpperCase() + ">>>", entry.getValue());
        }
        return insertInto;
    }

    public String getStringResourceByName(String aString) {
        String packageName = ctx.getPackageName();

        int resId = ctx.getResources().getIdentifier(aString, "string", packageName);
        if (resId == 0) {
            return "";
        } else {
            return ctx.getString(resId);
        }
    }
}
