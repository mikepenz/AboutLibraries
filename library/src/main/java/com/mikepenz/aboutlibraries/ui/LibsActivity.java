package com.mikepenz.aboutlibraries.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.R;

/**
 * Created by mikepenz on 04.06.14.
 */
public class LibsActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //set the theme
        boolean customTheme = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int themeId = bundle.getInt(Libs.BUNDLE_THEME, -1);
            if (themeId != -1) {
                customTheme = true;
                setTheme(themeId);
            }
        }
        if (!customTheme) {
            setTheme(R.style.Theme_AppCompat);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensource);

        String title = "";
        if (bundle != null) {
            title = bundle.getString(Libs.BUNDLE_TITLE);
        }

        LibsFragment fragment = new LibsFragment();
        fragment.setArguments(bundle);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            if (!customTheme) {
                ab.setBackgroundDrawable(null);
            }
            ab.setDisplayHomeAsUpEnabled(true);

            if (TextUtils.isEmpty(title)) {
                ab.setDisplayShowTitleEnabled(false);
            } else {
                ab.setDisplayShowTitleEnabled(true);
                ab.setTitle(title);
            }

            ab.setDisplayUseLogoEnabled(true);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                LibsActivity.this.finish();
                return true;
            }
            default:
                return false;
        }
    }
}
