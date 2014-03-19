package com.tundem.aboutlibraries;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.tundem.aboutlibraries.entity.Library;

public class Libs {
	private static Context ctx;
	private static Libs libs = null;

	private ArrayList<Library> libraries = new ArrayList<Library>();

	private Libs() {
		Field[] fields = R.string.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().contains("define_")) {
				Library library = genLibrary(fields[i].getName().replace("define_", ""));
				if (library != null) {
					libraries.add(library);
				}
			}
		}
	}

	public static Libs getInstance(Context context) {
		ctx = context;
		if (libs == null) {
			libs = new Libs();
		}
		return libs;
	}

	/**
	 * Get all available Libraries
	 * 
	 * @return an ArrayList<Library> with all available libraries
	 */
	public ArrayList<Library> getLibraries() {
		return libraries;
	}

	/**
	 * Get a library by its name (the name must be equal)
	 * 
	 * @param libraryName
	 *            the name of the lib (NOT case sensitiv) or the real name of the lib (this is the name used for github)
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
	 * @param searchTerm
	 *            the term which is in the libs name (NOT case sensitiv) or the real name of the lib (this is the name used for github)
	 * @param limit
	 *            -1 for all results or > 0 for a limitted result
	 * @return an ArrayList<Library> with the found libraries
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
