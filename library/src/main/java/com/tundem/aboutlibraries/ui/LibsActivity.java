package com.tundem.aboutlibraries.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.tundem.aboutlibraries.R;

/**
 * Created by mikepenz on 04.06.14.
 */
public class LibsActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opensource);

        Bundle bundle = getIntent().getExtras();
        LibsFragment fragment = new LibsFragment();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }
}
