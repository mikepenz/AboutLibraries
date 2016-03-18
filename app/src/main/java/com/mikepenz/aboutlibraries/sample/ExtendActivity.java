package com.mikepenz.aboutlibraries.sample;

import android.os.Bundle;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsActivity;


public class ExtendActivity extends LibsActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*
        Intent intent = new Intent();
        intent.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
        intent.putExtra(Libs.BUNDLE_LIBS, new String[]{"activeandroid", "caldroid"});
        setIntent(intent);
        */

        setIntent(new LibsBuilder().withLibraries("activeandroid", "caldroid").withActivityTheme(R.style.MaterialDrawerTheme).intent(this));
        super.onCreate(savedInstanceState);
    }
}
