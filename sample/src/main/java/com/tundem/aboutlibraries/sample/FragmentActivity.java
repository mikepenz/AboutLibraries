package com.tundem.aboutlibraries.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.R;
import com.tundem.aboutlibraries.ui.LibsFragment;

/**
 * Created by mikepenz on 04.06.14.
 */
public class FragmentActivity extends android.support.v4.app.FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opensource);

        Bundle bundle = new Bundle();
        bundle.putStringArray(Libs.BUNDLE_FIELDS, Libs.toStringArray(com.tundem.aboutlibraries.sample.R.string.class.getFields()));
        bundle.putStringArray(Libs.BUNDLE_LIBS, new String[]{"crouton"});

        LibsFragment fragment = new LibsFragment();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }
}
