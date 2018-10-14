package com.mikepenz.aboutlibraries.sample;

import android.app.Application;

import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

/**
 * Created by mikepenz on 28.12.15.
 */
public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        //define an itemAnimator for our AboutLibs
        LibsConfiguration.Companion.getInstance().setItemAnimator(new SlideDownAlphaAnimator());
        //register our font
        Iconics.registerFont(new MaterialDesignIconic());
        super.onCreate();
    }
}
