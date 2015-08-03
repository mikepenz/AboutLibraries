package com.mikepenz.aboutlibraries.util;

import android.content.Context;

import com.mikepenz.aboutlibraries.Libs;

/**
 * Created by mikepenz on 03.08.15.
 */
public class GenericsUtil {

    public static String[] getFields(Context ctx) {
        Class rClass = resolveRClass(ctx.getPackageName());

        if (rClass != null) {
            for (Class c : rClass.getClasses()) {
                if (c.getName().endsWith("string")) {
                    return Libs.toStringArray(c.getFields());
                }
            }
        }
        return new String[0];
    }

    /**
     * a helper class to resolve the correct R Class for the package
     *
     * @param packageName
     * @return
     */
    private static Class resolveRClass(String packageName) {
        try {
            return Class.forName(packageName + ".R");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return Class.forName(packageName.replace(".debug", "") + ".R");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return Class.forName(packageName.replace(".release", "") + ".R");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
