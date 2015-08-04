package com.mikepenz.aboutlibraries.util;

import android.content.Context;
import android.text.TextUtils;

import com.mikepenz.aboutlibraries.Libs;

/**
 * Created by mikepenz on 03.08.15.
 */
public class GenericsUtil {

    /**
     * a helper to get the string fields from the R class
     *
     * @param ctx
     * @return
     */
    public static String[] getFields(Context ctx) {
        Class rStringClass = resolveRClass(ctx.getPackageName());
        if (rStringClass != null) {
            return Libs.toStringArray(rStringClass.getFields());
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
        do {
            try {
                return Class.forName(packageName + ".R$string");
            } catch (ClassNotFoundException e) {
                packageName = packageName.contains(".") ? packageName.substring(0, packageName.lastIndexOf('.')) : "";
            }
        } while (!TextUtils.isEmpty(packageName));

        return null;
    }
}
