package com.tundem.aboutlibraries;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tundem.aboutlibraries.entity.Library;
import com.tundem.aboutlibraries.entity.License;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Libs {
    public static final String BUNDLE_LIBS = "ABOUT_LIBRARIES_LIBS";
    public static final String BUNDLE_FIELDS = "ABOUT_LIBRARIES_FIELDS";

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
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].contains(DEFINE_LICENSE)) {
                    License license = genLicense(fields[i].replace(DEFINE_LICENSE, ""));
                    if (license != null) {
                        licenses.add(license);
                    }
                }
            }
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].contains(DEFINE_LICENSE)) {
                    continue;
                }
                if (fields[i].contains(DEFINE_INT)) {
                    Library library = genLibrary(fields[i].replace(DEFINE_INT, ""));
                    if (library != null) {
                        library.setInternal(true);
                        internLibraries.add(library);
                    }
                } else if (fields[i].contains(DEFINE_EXT)) {
                    Library library = genLibrary(fields[i].replace(DEFINE_EXT, ""));
                    if (library != null) {
                        library.setInternal(false);
                        externLibraries.add(library);
                    }
                }
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
            lib.setDefinedName(libraryName);
            lib.setAuthor(getStringResourceByName("library_" + libraryName + "_author"));
            lib.setAuthorWebsite(getStringResourceByName("library_" + libraryName + "_authorWebsite"));
            lib.setLibraryName(getStringResourceByName("library_" + libraryName + "_libraryName"));
            lib.setLibraryDescription(getStringResourceByName("library_" + libraryName + "_libraryDescription"));
            lib.setLibraryVersion(getStringResourceByName("library_" + libraryName + "_libraryVersion"));
            lib.setLibraryWebsite(getStringResourceByName("library_" + libraryName + "_libraryWebsite"));

            String licenseId = getStringResourceByName("library_" + libraryName + "_licenseId");
            if (TextUtils.isEmpty(licenseId)) {
                License license = new License();
                license.setLicenseName(getStringResourceByName("library_" + libraryName + "_licenseVersion"));
                license.setLicenseWebsite(getStringResourceByName("library_" + libraryName + "_licenseLink"));
                license.setLicenseShortDescription(getStringResourceByName("library_" + libraryName + "_licenseContent"));
                lib.setLicense(license);
            } else {
                lib.setLicense(getLicense(licenseId));
            }

            lib.setOpenSource(Boolean.valueOf(getStringResourceByName("library_" + libraryName + "_isOpenSource")));
            lib.setRepositoryLink(getStringResourceByName("library_" + libraryName + "_repositoryLink"));

            if (TextUtils.isEmpty(lib.getLibraryName()) && TextUtils.isEmpty(lib.getLibraryDescription())) {
                return null;
            }

            return lib;
        } catch (Exception ex) {
            Log.e("com.tundem.aboutlibraries", "Failed to generateLibrary from file: " + ex.toString());
            return null;
        }
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
