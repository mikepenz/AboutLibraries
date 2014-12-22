package com.mikepenz.aboutlibraries.sample;

import android.os.Bundle;

import com.mikepenz.aboutlibraries.Libs;
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

        setIntent(new Libs.Builder().withFields(R.string.class.getFields()).withLibraries("activeandroid", "caldroid").intent(this));


        super.onCreate(savedInstanceState);
    }
}
