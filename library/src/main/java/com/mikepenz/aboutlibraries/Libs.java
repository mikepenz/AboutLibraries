package com.mikepenz.aboutlibraries;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.mikepenz.aboutlibraries.detector.Detect;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.entity.License;
import com.mikepenz.aboutlibraries.ui.LibsActivity;
import com.mikepenz.aboutlibraries.ui.LibsFragment;
import com.mikepenz.aboutlibraries.ui.adapter.LibsRecyclerViewAdapter;
import com.mikepenz.aboutlibraries.util.Colors;
import com.mikepenz.aboutlibraries.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Libs {
    public static enum LibraryFields {
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


    public static final String BUNDLE_FIELDS = "ABOUT_LIBRARIES_FIELDS";
    public static final String BUNDLE_LIBS = "ABOUT_LIBRARIES_LIBS";
    public static final String BUNDLE_EXCLUDE_LIBS = "ABOUT_LIBRARIES_EXCLUDE_LIBS";
    public static final String BUNDLE_AUTODETECT = "ABOUT_LIBRARIES_AUTODETECT";
    public static final String BUNDLE_SORT = "ABOUT_LIBRARIES_SORT";
    public static final String BUNDLE_ANIMATE = "ABOUT_LIBRARIES_ANIMATE";

    public static final String BUNDLE_RESULT_CODE = "BUNDLE_RESULT_CODE";
    public static final int INVALID_RESULT_CODE = -1;

    public static final String BUNDLE_LICENSE = "ABOUT_LIBRARIES_LICENSE";
    public static final String BUNDLE_LICENSE_DIALOG = "ABOUT_LIBRARIES_LICENSE_DIALOG";
    public static final String BUNDLE_VERSION = "ABOUT_LIBRARIES_VERSION";

    public static final String BUNDLE_LIBS_MODIFICATION = "ABOUT_LIBRARIES_LIBS_MODIFICATION";

    public static final String BUNDLE_THEME = "ABOUT_LIBRARIES_THEME";
    public static final String BUNDLE_TITLE = "ABOUT_LIBRARIES_TITLE";
    public static final String BUNDLE_COLORS = "ABOUT_COLOR";

    public static final String BUNDLE_APP_ABOUT_ICON = "ABOUT_LIBRARIES_APP_ABOUT_ICON";
    public static final String BUNDLE_APP_ABOUT_NAME = "ABOUT_LIBRARIES_APP_ABOUT_NAME";
    public static final String BUNDLE_APP_ABOUT_VERSION = "ABOUT_LIBRARIES_APP_ABOUT_VERSION";
    public static final String BUNDLE_APP_ABOUT_VERSION_NAME = "ABOUT_LIBRARIES_APP_ABOUT_VERSION_NAME";
    public static final String BUNDLE_APP_ABOUT_VERSION_CODE = "ABOUT_LIBRARIES_APP_ABOUT_VERSION_CODE";
    public static final String BUNDLE_APP_ABOUT_DESCRIPTION = "ABOUT_LIBRARIES_APP_ABOUT_DESCRIPTION";

    public static final String BUNDLE_APP_ABOUT_SPECIAL1 = "ABOUT_LIBRARIES_APP_ABOUT_SPECIAL1";
    public static final String BUNDLE_APP_ABOUT_SPECIAL1_DESCRIPTION = "ABOUT_LIBRARIES_APP_ABOUT_SPECIAL1_DESCRIPTION";
    public static final String BUNDLE_APP_ABOUT_SPECIAL2 = "ABOUT_LIBRARIES_APP_ABOUT_SPECIAL2";
    public static final String BUNDLE_APP_ABOUT_SPECIAL2_DESCRIPTION = "ABOUT_LIBRARIES_APP_ABOUT_SPECIAL2_DESCRIPTION";
    public static final String BUNDLE_APP_ABOUT_SPECIAL3 = "ABOUT_LIBRARIES_APP_ABOUT_SPECIAL3";
    public static final String BUNDLE_APP_ABOUT_SPECIAL3_DESCRIPTION = "ABOUT_LIBRARIES_APP_ABOUT_SPECIAL3_DESCRIPTION";

    private static final String DEFINE_LICENSE = "define_license_";
    private static final String DEFINE_INT = "define_int_";
    private static final String DEFINE_EXT = "define_";

    private Context ctx;

    private ArrayList<Library> internLibraries = new ArrayList<Library>();
    private ArrayList<Library> externLibraries = new ArrayList<Library>();
    private ArrayList<License> licenses = new ArrayList<License>();

    public Libs(Context context) {
        ctx = context;
        String[] fields = toStringArray(R.string.class.getFields());
        init(fields);
    }

    public Libs(Context context, String[] fields) {
        ctx = context;
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
            for (Library lib : getAutoDetectedLibraries()) {
                libraries.put(lib.getDefinedName(), lib);
            }
        }

        //Add all external libraries
        for (Library lib : getExternLibraries()) {
            libraries.put(lib.getDefinedName(), lib);
        }

        //Now add all libs which do not contains the info file, but are in the AboutLibraries lib
        if (internalLibraries != null) {
            for (String internalLibrary : internalLibraries) {
                Library lib = getLibrary(internalLibrary);
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
     * @return an ArrayList Library with all found libs by their classpath
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
     * @return an ArrayList Library with all available internLibraries
     */
    public ArrayList<Library> getInternLibraries() {
        return new ArrayList<Library>(internLibraries);
    }

    /**
     * Get all extern available Libraries
     *
     * @return an ArrayList Library  with all available externLibraries
     */
    public ArrayList<Library> getExternLibraries() {
        return new ArrayList<Library>(externLibraries);
    }

    /**
     * Get all available licenses
     *
     * @return an ArrayLIst License  with all available Licenses
     */
    public ArrayList<License> getLicenses() {
        return new ArrayList<License>(licenses);
    }

    /**
     * Get all available Libraries
     *
     * @return an ArrayList Library with all available Libraries
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
     * @param limit      -1 for all results or smaller 0 for a limitted result
     * @return an ArrayList Library with the found internLibraries
     */
    public ArrayList<Library> findLibrary(String searchTerm, int limit) {
        return find(getLibraries(), searchTerm, false, limit);
    }

    /**
     * @param searchTerm
     * @param idOnly
     * @param limit
     * @return
     */
    public ArrayList<Library> findInInternalLibrary(String searchTerm, boolean idOnly, int limit) {
        return find(getInternLibraries(), searchTerm, idOnly, limit);
    }

    /**
     * @param searchTerm
     * @param idOnly
     * @param limit
     * @return
     */
    public ArrayList<Library> findInExternalLibrary(String searchTerm, boolean idOnly, int limit) {
        return find(getExternLibraries(), searchTerm, idOnly, limit);
    }

    /**
     * @param libraries
     * @param searchTerm
     * @param idOnly
     * @param limit
     * @return
     */
    private ArrayList<Library> find(ArrayList<Library> libraries, String searchTerm, boolean idOnly, int limit) {
        ArrayList<Library> localLibs = new ArrayList<Library>();

        int count = 0;
        for (Library library : libraries) {
            if (idOnly) {
                if (library.getDefinedName().toLowerCase().contains(searchTerm.toLowerCase())) {
                    localLibs.add(library);
                    count = count + 1;

                    if (limit != -1 && limit < count) {
                        break;
                    }
                }
            } else {
                if (library.getLibraryName().toLowerCase().contains(searchTerm.toLowerCase()) || library.getDefinedName().toLowerCase().contains(searchTerm.toLowerCase())) {
                    localLibs.add(library);
                    count = count + 1;

                    if (limit != -1 && limit < count) {
                        break;
                    }
                }
            }
        }

        return localLibs;
    }


    /**
     * @param licenseName
     * @return
     */
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

    /**
     * @param licenseName
     * @return
     */
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
            Log.e("aboutlibraries", "Failed to generateLicense from file: " + ex.toString());
            return null;
        }
    }

    /**
     * @param libraryName
     * @return
     */
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
            Log.e("aboutlibraries", "Failed to generateLibrary from file: " + ex.toString());
            return null;
        }
    }

    /**
     * @param libraryName
     * @return
     */
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
            if (!TextUtils.isEmpty(entry.getValue())) {
                insertInto = insertInto.replace("<<<" + entry.getKey().toUpperCase() + ">>>", entry.getValue());
            }
        }

        //remove the placeholder chars so the license is shown correct
        insertInto = insertInto.replace("<<<", "");
        insertInto = insertInto.replace(">>>", "");

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


    /**
     * @param modifications
     */
    public void modifyLibraries(HashMap<String, HashMap<String, String>> modifications) {
        if (modifications != null) {
            for (Map.Entry<String, HashMap<String, String>> entry : modifications.entrySet()) {
                ArrayList<Library> foundLibs = findInExternalLibrary(entry.getKey(), true, 1);
                if (foundLibs == null || foundLibs.size() == 0) {
                    foundLibs = findInInternalLibrary(entry.getKey(), true, 1);
                }

                if (foundLibs != null && foundLibs.size() == 1) {
                    Library lib = foundLibs.get(0);
                    for (Map.Entry<String, String> modification : entry.getValue().entrySet()) {
                        String key = modification.getKey().toUpperCase();
                        String value = modification.getValue();

                        if (key.equals(LibraryFields.AUTHOR_NAME.name())) {
                            lib.setAuthor(value);
                        } else if (key.equals(LibraryFields.AUTHOR_WEBSITE.name())) {
                            lib.setAuthorWebsite(value);
                        } else if (key.equals(LibraryFields.LIBRARY_NAME.name())) {
                            lib.setLibraryName(value);
                        } else if (key.equals(LibraryFields.LIBRARY_DESCRIPTION.name())) {
                            lib.setLibraryDescription(value);
                        } else if (key.equals(LibraryFields.LIBRARY_VERSION.name())) {
                            lib.setLibraryVersion(value);
                        } else if (key.equals(LibraryFields.LIBRARY_WEBSITE.name())) {
                            lib.setLibraryWebsite(value);
                        } else if (key.equals(LibraryFields.LIBRARY_OPEN_SOURCE.name())) {
                            lib.setOpenSource(Boolean.parseBoolean(value));
                        } else if (key.equals(LibraryFields.LIBRARY_REPOSITORY_LINK.name())) {
                            lib.setRepositoryLink(value);
                        } else if (key.equals(LibraryFields.LIBRARY_CLASSPATH.name())) {
                            //note this can be set but won't probably work for autodetect
                            lib.setClassPath(value);
                        } else if (key.equals(LibraryFields.LICENSE_NAME.name())) {
                            if (lib.getLicense() == null) {
                                lib.setLicense(new License());
                            }
                            lib.getLicense().setLicenseName(value);
                        } else if (key.equals(LibraryFields.LICENSE_SHORT_DESCRIPTION.name())) {
                            if (lib.getLicense() == null) {
                                lib.setLicense(new License());
                            }
                            lib.getLicense().setLicenseShortDescription(value);
                        } else if (key.equals(LibraryFields.LICENSE_DESCRIPTION.name())) {
                            if (lib.getLicense() == null) {
                                lib.setLicense(new License());
                            }
                            lib.getLicense().setLicenseDescription(value);
                        } else if (key.equals(LibraryFields.LICENSE_WEBSITE.name())) {
                            if (lib.getLicense() == null) {
                                lib.setLicense(new License());
                            }
                            lib.getLicense().setLicenseWebsite(value);
                        }
                    }
                }
            }
        }
    }


    public static class Builder {
        private String[] fields = null;
        private String[] internalLibraries = null;
        private String[] excludeLibraries = null;

        private boolean autoDetect = true;
        private boolean sort = true;
        private boolean animate = true;

        private boolean showLicense = false;
        private boolean showLicenseDialog = true;
        private boolean showVersion = false;

        private Boolean aboutShowIcon = null;
        private String aboutAppName = null;
        private Boolean aboutShowVersion = null;
        private String aboutDescription = null;
        private Boolean aboutShowVersionName = false;
        private Boolean aboutShowVersionCode = false;

        private String aboutAppSpecial1 = null;
        private String aboutAppSpecial1Description = null;
        private String aboutAppSpecial2 = null;
        private String aboutAppSpecial2Description = null;
        private String aboutAppSpecial3 = null;
        private String aboutAppSpecial3Description = null;

        private int activityTheme = -1;
        private String activityTitle = null;
        private Colors activityColor = null;

        private HashMap<String, HashMap<String, String>> libraryModification = null;

        public Builder() {
        }

        /**
         * Builder method to pass the R.string.class.getFields() array to the fragment/activity so we can also include all ressources which are within libraries or your app.
         *
         * @param fields R.string.class.getFields()
         * @return this
         */
        public Builder withFields(Field[] fields) {
            return withFields(Libs.toStringArray(fields));
        }

        /**
         * Builder method to pass the Libs.toStringArray(R.string.class.getFields()) array to the fragment/activity so we can also include all ressources which are within libraries or your app.
         *
         * @param fields Libs.toStringArray(R.string.class.getFields())
         * @return this
         */
        public Builder withFields(String... fields) {
            this.fields = fields;
            return this;
        }

        /**
         * Builder method to pass manual libraries (libs which are not autoDetected)
         *
         * @param libraries the identifiers of the manual added libraries
         * @return this
         */
        public Builder withLibraries(String... libraries) {
            this.internalLibraries = libraries;
            return this;
        }

        /**
         * Builder method to exclude specific libraries
         *
         * @param excludeLibraries the identifiers of the libraries which should be excluded
         * @return this
         */
        public Builder withExcludedLibraries(String... excludeLibraries) {
            this.excludeLibraries = excludeLibraries;
            return this;
        }

        /**
         * Builder method to disable autoDetect (default: enabled)
         *
         * @param autoDetect enabled or disabled
         * @return this
         */
        public Builder withAutoDetect(boolean autoDetect) {
            this.autoDetect = autoDetect;
            return this;
        }

        /**
         * Builder method to disable sort (default: enabled)
         *
         * @param sort enabled or disabled
         * @return this
         */
        public Builder withSortEnabled(boolean sort) {
            this.sort = sort;
            return this;
        }

        /**
         * Builder method to disable animations (default: enabled)
         *
         * @param animate enabled or disabled
         * @return this
         */
        public Builder withAnimations(boolean animate) {
            this.animate = animate;
            return this;
        }

        /**
         * Builder method to enable the license display (default: disabled)
         *
         * @param showLicense enabled or disabled
         * @return this
         */
        public Builder withLicenseShown(boolean showLicense) {
            this.showLicense = showLicense;
            return this;
        }

        /**
         * Builder method to disable the license display as dialog (default: enabled)
         *
         * @param showLicenseDialog enabled or disabled
         * @return this
         */
        public Builder withLicenseDialog(boolean showLicenseDialog) {
            this.showLicenseDialog = showLicenseDialog;
            return this;
        }

        /**
         * Builder method to hide the version number (default: enabled)
         *
         * @param showVersion enabled or disabled
         * @return this
         */
        public Builder withVersionShown(boolean showVersion) {
            this.showVersion = showVersion;
            return this;
        }

        /**
         * Builder method to enable the display of the application icon as about this app view
         *
         * @param aboutShowIcon enabled or disabled
         * @return this
         */
        public Builder withAboutIconShown(boolean aboutShowIcon) {
            this.aboutShowIcon = aboutShowIcon;
            return this;
        }

        /**
         * Builder method to enable the display of the application version name and code as about this app view
         *
         * @param aboutShowVersion enabled or disabled
         * @return this
         */
        public Builder withAboutVersionShown(boolean aboutShowVersion) {
            this.aboutShowVersion = aboutShowVersion;
            this.aboutShowVersionName = aboutShowVersion;
            this.aboutShowVersionCode = aboutShowVersion;
            return this;
        }

        /**
         * Builder method to enable the display of the application version name as about this app view
         *
         * @param aboutShowVersion
         * @return
         */
        public Builder withAboutVersionShownName(boolean aboutShowVersion) {
            this.aboutShowVersionName = aboutShowVersion;
            return this;
        }

        /**
         * Builder method to enable the display of the application version code as about this app view
         *
         * @param aboutShowVersion
         * @return this
         */
        public Builder withAboutVersionShownCode(boolean aboutShowVersion) {
            this.aboutShowVersionCode = aboutShowVersion;
            return this;
        }

        /**
         * Builder method to enable the display and set the text of the application name in the about this app view
         *
         * @param aboutAppName the name of this application
         * @return this
         */
        public Builder withAboutAppName(String aboutAppName) {
            this.aboutAppName = aboutAppName;
            return this;
        }

        /**
         * Builder method to enable the display and set the text of the application description as about this app view
         *
         * @param aboutDescription the description of this application
         * @return this
         */
        public Builder withAboutDescription(String aboutDescription) {
            this.aboutDescription = aboutDescription;
            return this;
        }

        /**
         * @param aboutAppSpecial1 the special button text
         * @return this
         */
        public Builder withAboutSpecial1(String aboutAppSpecial1) {
            this.aboutAppSpecial1 = aboutAppSpecial1;
            return this;
        }

        /**
         * @param aboutAppSpecial1Description the special dialog text
         * @return this
         */
        public Builder withAboutSpecial1Description(String aboutAppSpecial1Description) {
            this.aboutAppSpecial1Description = aboutAppSpecial1Description;
            return this;
        }

        /**
         * @param aboutAppSpecial2 the special button text
         * @return this
         */
        public Builder withAboutSpecial2(String aboutAppSpecial2) {
            this.aboutAppSpecial1 = aboutAppSpecial1;
            return this;
        }

        /**
         * @param aboutAppSpecial2Description the special dialog text
         * @return this
         */
        public Builder withAboutSpecial2Description(String aboutAppSpecial2Description) {
            this.aboutAppSpecial2Description = aboutAppSpecial2Description;
            return this;
        }

        /**
         * @param aboutAppSpecial3 the special button text
         * @return this
         */
        public Builder withAboutSpecial3(String aboutAppSpecial3) {
            this.aboutAppSpecial3 = aboutAppSpecial3;
            return this;
        }

        /**
         * @param aboutAppSpecial3Description the special dialog text
         * @return this
         */
        public Builder withAboutSpecial3Description(String aboutAppSpecial3Description) {
            this.aboutAppSpecial3Description = aboutAppSpecial3Description;
            return this;
        }

        /**
         * Builder method to set the activity theme
         *
         * @param activityTheme as example R.theme.AppTheme (just for the activity)
         * @return this
         */
        public Builder withActivityTheme(int activityTheme) {
            this.activityTheme = activityTheme;
            return this;
        }

        /**
         * Builder method to set the ActivityTitle
         *
         * @param activityTitle the activity title (just for the activity)
         * @return this
         */
        public Builder withActivityTitle(String activityTitle) {
            this.activityTitle = activityTitle;
            return this;
        }

        /**
         * Builder method to set the ActivityColor
         *
         * @param activityColor the activity color (just for the activity)
         * @return this
         */
        public Builder withActivityColor(Colors activityColor) {
            this.activityColor = activityColor;
            return this;
        }

        /**
         * Builder method to modify specific libraries. NOTE: This will overwrite any modifications with the helper methods
         *
         * @param libraryModification an HashMap identified by libraryID containing an HashMap with the modifications identified by elementID.
         * @return this
         */
        public Builder withLibraryModification(HashMap<String, HashMap<String, String>> libraryModification) {
            this.libraryModification = libraryModification;
            return this;
        }

        /**
         * Builder helper method to set modifications for specific libraries
         *
         * @param library           the library to be modified
         * @param modificationKey   the identifier for the specific modification
         * @param modificationValue the value for the specific modification
         * @return this
         */
        public Builder withLibraryModification(String library, LibraryFields modificationKey, String modificationValue) {
            if (this.libraryModification == null) {
                this.libraryModification = new HashMap<String, HashMap<String, String>>();
            }

            if (!libraryModification.containsKey(library)) {
                libraryModification.put(library, new HashMap<String, String>());
            }

            libraryModification.get(library).put(modificationKey.name(), modificationValue);

            return this;
        }

        /*
         * START OF THE FINAL METHODS
         */

        private void preCheck() {
            if (fields == null) {
                Log.w("AboutLibraries", "Have you missed to call withFields(R.string.class.getFields())? - autoDetect won't work - https://github.com/mikepenz/AboutLibraries/wiki/HOWTO:-Fragment");
            }
        }

        /**
         * builder to build an adapter out of the given information ;D
         *
         * @param context the current context
         * @return a LibsRecyclerViewAdapter with the libraries
         */
        public LibsRecyclerViewAdapter adapter(Context context) {
            Libs libs;
            if (fields == null) {
                libs = new Libs(context);
            } else {
                libs = new Libs(context, fields);
            }

            //apply modifications
            libs.modifyLibraries(libraryModification);

            //fetch the libraries and sort if a comparator was set
            ArrayList<Library> libraries = libs.prepareLibraries(internalLibraries, excludeLibraries, autoDetect, sort);

            //prepare adapter
            LibsRecyclerViewAdapter adapter = new LibsRecyclerViewAdapter(context, showLicense, showLicenseDialog, showVersion);
            adapter.addLibs(libraries);
            return adapter;
        }

        /**
         * builder to build an adapter out of the given information ;D
         * Get result at onActivityForResult()
         *
         * @param context the current context
         * @return a LibsRecyclerViewAdapter with the libraries
         */
        public LibsRecyclerViewAdapter adapter(Context context, int returnCode) {
            LibsRecyclerViewAdapter adapter = adapter(context);
            adapter.setReturnCode(returnCode);
            return adapter;
        }


        /**
         * intent() method to build and create the intent with the set params
         *
         * @return the intent to start the activity
         */
        public Intent intent(Context ctx) {
            preCheck();

            Intent i = new Intent(ctx, LibsActivity.class);
            i.putExtra(Libs.BUNDLE_FIELDS, this.fields);
            i.putExtra(Libs.BUNDLE_LIBS, this.internalLibraries);
            i.putExtra(Libs.BUNDLE_EXCLUDE_LIBS, this.excludeLibraries);

            i.putExtra(Libs.BUNDLE_AUTODETECT, this.autoDetect);
            i.putExtra(Libs.BUNDLE_SORT, this.sort);
            i.putExtra(Libs.BUNDLE_ANIMATE, this.animate);

            i.putExtra(Libs.BUNDLE_LICENSE, this.showLicense);
            i.putExtra(Libs.BUNDLE_LICENSE_DIALOG, this.showLicenseDialog);
            i.putExtra(Libs.BUNDLE_VERSION, this.showVersion);

            if (this.libraryModification != null) {
                i.putExtra(Libs.BUNDLE_LIBS_MODIFICATION, this.libraryModification);
            }

            if (this.aboutShowIcon != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_ICON, this.aboutShowIcon);
            }
            if (this.aboutAppName != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_NAME, this.aboutAppName);
            }
            if (this.aboutShowVersion != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_VERSION, this.aboutShowVersion);
            }
            if (this.aboutShowVersionName != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_VERSION_NAME, this.aboutShowVersionName);
            }
            if (this.aboutShowVersionCode != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_VERSION_CODE, this.aboutShowVersionCode);
            }
            if (this.aboutDescription != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_DESCRIPTION, this.aboutDescription);
            }
            if (this.aboutAppSpecial1 != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_SPECIAL1, this.aboutAppSpecial1);
            }
            if (this.aboutAppSpecial1Description != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_SPECIAL1_DESCRIPTION, this.aboutAppSpecial1Description);
            }
            if (this.aboutAppSpecial2 != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_SPECIAL2, this.aboutAppSpecial2);
            }
            if (this.aboutAppSpecial2Description != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_SPECIAL2_DESCRIPTION, this.aboutAppSpecial2Description);
            }
            if (this.aboutAppSpecial3 != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_SPECIAL3, this.aboutAppSpecial3);
            }
            if (this.aboutAppSpecial3Description != null) {
                i.putExtra(Libs.BUNDLE_APP_ABOUT_SPECIAL3_DESCRIPTION, this.aboutAppSpecial3Description);
            }

            i.putExtra(Libs.BUNDLE_THEME, this.activityTheme);
            if (this.activityTitle != null) {
                i.putExtra(Libs.BUNDLE_TITLE, this.activityTitle);
            }
            if (this.activityColor != null) {
                i.putExtra(Libs.BUNDLE_COLORS, this.activityColor);
            }

            return i;
        }

        /**
         * intent() method to build and create the intent with the set params
         *
         * @return the intent to start the activity
         */
        public Intent intent(Context ctx, int result) {
            Intent intent = intent(ctx);
            Bundle bundle = intent.getExtras();
            bundle.putInt(Libs.BUNDLE_RESULT_CODE, result);
            return intent;
        }

        /**
         * start() method to start the application and get result at onActivityForResult()
         */
        public void start(Activity act, int result) {
            Intent i = intent(act, result);
            act.startActivityForResult(i, result);
        }

        /**
         * start() method to start the application
         */
        public void start(Context ctx) {
            Intent i = intent(ctx);
            ctx.startActivity(i);
        }

        /**
         * activity() method to start the application
         */
        public void activity(Context ctx) {
            start(ctx);
        }

        /**
         * bundle() method to build and create the bundle with the set params
         *
         * @return the bundle to create the fragment
         */
        public Bundle bundle() {
            preCheck();

            Bundle bundle = new Bundle();
            bundle.putStringArray(Libs.BUNDLE_FIELDS, this.fields);
            bundle.putStringArray(Libs.BUNDLE_LIBS, this.internalLibraries);
            bundle.putStringArray(Libs.BUNDLE_EXCLUDE_LIBS, this.excludeLibraries);

            bundle.putBoolean(Libs.BUNDLE_AUTODETECT, this.autoDetect);
            bundle.putBoolean(Libs.BUNDLE_SORT, this.sort);
            bundle.putBoolean(Libs.BUNDLE_ANIMATE, this.animate);

            bundle.putBoolean(Libs.BUNDLE_LICENSE, this.showLicense);
            bundle.putBoolean(Libs.BUNDLE_LICENSE_DIALOG, this.showLicenseDialog);
            bundle.putBoolean(Libs.BUNDLE_VERSION, this.showVersion);

            if (this.libraryModification != null) {
                bundle.putSerializable(Libs.BUNDLE_LIBS_MODIFICATION, this.libraryModification);
            }

            if (this.aboutShowIcon != null) {
                bundle.putBoolean(Libs.BUNDLE_APP_ABOUT_ICON, this.aboutShowIcon);
            }
            if (this.aboutAppName != null) {
                bundle.putString(Libs.BUNDLE_APP_ABOUT_NAME, this.aboutAppName);
            }
            if (this.aboutShowVersion != null) {
                bundle.putBoolean(Libs.BUNDLE_APP_ABOUT_VERSION, this.aboutShowVersion);
            }
            if (this.aboutShowVersionName != null) {
                bundle.putBoolean(Libs.BUNDLE_APP_ABOUT_VERSION_NAME, this.aboutShowVersionName);
            }
            if (this.aboutShowVersionCode != null) {
                bundle.putBoolean(Libs.BUNDLE_APP_ABOUT_VERSION_CODE, this.aboutShowVersionCode);
            }
            if (this.aboutDescription != null) {
                bundle.putString(Libs.BUNDLE_APP_ABOUT_DESCRIPTION, this.aboutDescription);
            }
            if (this.aboutAppSpecial1 != null) {
                bundle.putString(Libs.BUNDLE_APP_ABOUT_SPECIAL1, this.aboutAppSpecial1);
            }
            if (this.aboutAppSpecial1Description != null) {
                bundle.putString(Libs.BUNDLE_APP_ABOUT_SPECIAL1_DESCRIPTION, this.aboutAppSpecial1Description);
            }
            if (this.aboutAppSpecial2 != null) {
                bundle.putString(Libs.BUNDLE_APP_ABOUT_SPECIAL2, this.aboutAppSpecial2);
            }
            if (this.aboutAppSpecial2Description != null) {
                bundle.putString(Libs.BUNDLE_APP_ABOUT_SPECIAL2_DESCRIPTION, this.aboutAppSpecial2Description);
            }
            if (this.aboutAppSpecial3 != null) {
                bundle.putString(Libs.BUNDLE_APP_ABOUT_SPECIAL3, this.aboutAppSpecial3);
            }
            if (this.aboutAppSpecial3Description != null) {
                bundle.putString(Libs.BUNDLE_APP_ABOUT_SPECIAL3_DESCRIPTION, this.aboutAppSpecial3Description);
            }

            return bundle;
        }

        /**
         * fragment() method to build and create the fragment with the set params
         *
         * @return the fragment to set in your application
         */
        public LibsFragment fragment() {
            Bundle bundle = bundle();

            LibsFragment fragment = new LibsFragment();
            fragment.setArguments(bundle);

            return fragment;
        }
    }
}