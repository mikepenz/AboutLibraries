package com.mikepenz.aboutlibraries.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.ui.LibsFragment;

/**
 * Created by mikepenz on 04.06.14.
 */
public class FragmentActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opensource);

        LibsFragment fragment = new Libs.Builder()
                .withFields(R.string.class.getFields())
                .withLibraries("crouton", "activeandroid", "actionbarsherlock", "showcaseview")
                .withVersionShown(false)
                .withLicenseShown(false)
                .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries")
                .fragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.mikepenz.aboutlibraries.sample.R.menu.fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == com.mikepenz.aboutlibraries.sample.R.id.action_opensource) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikepenz/AboutLibraries"));
            startActivity(browserIntent);
            return true;
        } else if (id == com.mikepenz.aboutlibraries.sample.R.id.action_extendactivity) {
            Intent i = new Intent(getApplicationContext(), ExtendActivity.class);
            startActivity(i);
        } else if (id == com.mikepenz.aboutlibraries.sample.R.id.action_customactivity) {
            Intent i = new Intent(getApplicationContext(), CustomActivity.class);
            startActivity(i);
        } else if (id == com.mikepenz.aboutlibraries.sample.R.id.action_manifestactivity) {
            new Libs.Builder()
                    .withFields(R.string.class.getFields())
                    .withLibraries("crouton, actionbarsherlock", "showcaseview")
                    .withAutoDetect(true)
                    .withLicenseShown(true)
                    .withVersionShown(true)
                    .withActivityTitle("Open Source")
                    .withActivityTheme(R.style.AppTheme)
                    .start(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
