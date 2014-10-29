package com.tundem.aboutlibraries.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.ui.LibsActivity;
import com.tundem.aboutlibraries.ui.LibsFragment;

/**
 * Created by mikepenz on 04.06.14.
 */
public class FragmentActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opensource);

        Bundle bundle = new Bundle();
        bundle.putStringArray(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
        bundle.putStringArray(Libs.BUNDLE_LIBS, new String[]{"crouton", "activeandroid", "actionbarsherlock", "showcaseview"});

        bundle.putBoolean(Libs.BUNDLE_VERSION, true);
        bundle.putBoolean(Libs.BUNDLE_LICENSE, true);

        LibsFragment fragment = new LibsFragment();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.tundem.aboutlibraries.sample.R.menu.fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == com.tundem.aboutlibraries.sample.R.id.action_opensource) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikepenz/AboutLibraries"));
            startActivity(browserIntent);
            return true;
        } else if (id == com.tundem.aboutlibraries.sample.R.id.action_extendactivity) {
            Intent i = new Intent(getApplicationContext(), ExtendActivity.class);
            startActivity(i);
        } else if (id == com.tundem.aboutlibraries.sample.R.id.action_customactivity) {
            Intent i = new Intent(getApplicationContext(), CustomActivity.class);
            startActivity(i);
        } else if (id == com.tundem.aboutlibraries.sample.R.id.action_manifestactivity) {
            Intent i = new Intent(getApplicationContext(), LibsActivity.class);
            i.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
            i.putExtra(Libs.BUNDLE_LIBS, new String[]{"crouton", "actionbarsherlock", "showcaseview"});
            i.putExtra(Libs.BUNDLE_AUTODETECT, true);

            i.putExtra(Libs.BUNDLE_VERSION, true);
            i.putExtra(Libs.BUNDLE_LICENSE, true);

            i.putExtra(Libs.BUNDLE_TITLE, "Open Source");
            i.putExtra(Libs.BUNDLE_THEME, R.style.AppTheme);

            /*
            //INFO you can set the about app text with these extra data too
            i.putExtra(Libs.BUNDLE_APP_ABOUT_ICON, true);
            i.putExtra(Libs.BUNDLE_APP_ABOUT_VERSION, true);
            i.putExtra(Libs.BUNDLE_APP_ABOUT_DESCRIPTION, "This is a small sample which can be set in the about my app description file.<br /><b>You can style this with html markup :D</b>");
            */

            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
