package com.mikepenz.aboutlibraries.detector;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.mikepenz.aboutlibraries.entity.Library;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikepenz on 08.09.14.
 * Original concept of detecting libraries with their classPath by Michael Carrano
 * More details can be found here: https://github.com/michaelcarrano/detective-droid
 */
public class Detect {
    public static List<Library> detect(Context mCtx, List<Library> libraries) {
        ArrayList<Library> foundLibraries = new ArrayList<Library>();
        // Loop through known libraries
        for (Library library : libraries) {
            if (!TextUtils.isEmpty(library.getClassPath())) {
                try {
                    Context ctx = mCtx.createPackageContext(mCtx.getPackageName(),
                            Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
                    Class<?> clazz = Class.forName(library.getClassPath(), false, ctx.getClassLoader());

                    // Detected a library!!!
                    if (clazz != null) {
                        foundLibraries.add(library);
                    }
                } catch (ClassNotFoundException e) {
                    //e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    //e.printStackTrace();
                }
            }
        }
        // Only return AppSource if app has a library
        //return libraries.size() > 0 ? new AppSource(pkg, libraries) : null;

        return foundLibraries;
    }
}
