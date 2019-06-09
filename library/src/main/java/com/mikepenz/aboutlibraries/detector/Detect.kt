package com.mikepenz.aboutlibraries.detector

import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import com.mikepenz.aboutlibraries.entity.Library
import java.util.*


/**
 * Created by mikepenz on 08.09.14.
 * Original concept of detecting libraries with their classPath by Michael Carrano
 * More details can be found here: https://github.com/michaelcarrano/detective-droid
 */
object Detect {
    fun detect(mCtx: Context, libraries: List<Library>): List<Library> {
        val foundLibraries = ArrayList<Library>()
        // Loop through known libraries
        for (library in libraries) {
            if (!TextUtils.isEmpty(library.classPath)) {
                try {
                    val ctx = mCtx.createPackageContext(mCtx.packageName,
                            Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
                    Class.forName(library.classPath, false, ctx.classLoader) // throws an exception if the class does not exist

                    // Detected a library!!!
                    foundLibraries.add(library)
                } catch (e: ClassNotFoundException) {
                    //e.printStackTrace();
                } catch (e: PackageManager.NameNotFoundException) {
                    //e.printStackTrace();
                }

            }
        }
        // Only return AppSource if app has a library
        //return libraries.size() > 0 ? new AppSource(pkg, libraries) : null;

        return foundLibraries
    }
}
