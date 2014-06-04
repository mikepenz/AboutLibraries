package com.tundem.aboutlibraries;

import android.content.Context;
import android.util.Log;

import com.tundem.aboutlibraries.entity.Library;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Libs {
    public static final String BUNDLE_LIBS = "ABOUT_LIBRARIES_LIBS";
    public static final String BUNDLE_FIELDS = "ABOUT_LIBRARIES_FIELDS";

    private static final String DEFINE_INT = "define_int_";
    private static final String DEFINE_EXT = "define_";

    private static Context ctx;
    private static Libs libs = null;

    private ArrayList<Library> internLibraries = new ArrayList<Library>();
    private ArrayList<Library> externLibraries = new ArrayList<Library>();

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

    private Library genLibrary(String libraryName) {
        libraryName = libraryName.replace("-", "_");

        try {
            Library lib = new Library();
            lib.setDefinedName(libraryName);
            lib.setAuthor(getStringResourceByName("libray_" + libraryName + "_author"));
            lib.setAuthorWebsite(getStringResourceByName("libray_" + libraryName + "_authorWebsite"));
            lib.setLibraryName(getStringResourceByName("libray_" + libraryName + "_libraryName"));
            lib.setLibraryDescription(getStringResourceByName("libray_" + libraryName + "_libraryDescription"));
            lib.setLibraryVersion(getStringResourceByName("libray_" + libraryName + "_libraryVersion"));
            lib.setLibraryWebsite(getStringResourceByName("libray_" + libraryName + "_libraryWebsite"));
            lib.setLicenseVersion(getStringResourceByName("libray_" + libraryName + "_licenseVersion"));
            lib.setLicenseContent(getStringResourceByName("libray_" + libraryName + "_licenseContent"));
            lib.setOpenSource(Boolean.valueOf(getStringResourceByName("libray_" + libraryName + "_isOpenSource")));
            lib.setRepositoryLink(getStringResourceByName("libray_" + libraryName + "_repositoryLink"));
            return lib;
        } catch (Exception ex) {
            Log.e("com.tundem.aboutlibraries", "Failed to generateLibrary from file: " + ex.toString());
            return null;
        }
    }

    private String getStringResourceByName(String aString) {
        String packageName = ctx.getPackageName();

        int resId = ctx.getResources().getIdentifier(aString, "string", packageName);
        if (resId == 0) {
            return "";
        } else {
            return ctx.getString(resId);
        }
    }
}
