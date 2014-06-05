package com.tundem.aboutlibraries.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

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

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
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
