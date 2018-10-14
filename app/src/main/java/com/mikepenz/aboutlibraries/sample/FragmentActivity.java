package com.mikepenz.aboutlibraries.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.LibTaskCallback;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

/**
 * Created by mikepenz on 04.06.14.
 */
public class FragmentActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new DrawerBuilder(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home"),
                        new PrimaryDrawerItem().withName(R.string.action_manifestactivity).withIdentifier(R.id.action_manifestactivity).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.action_extendactivity).withIdentifier(R.id.action_extendactivity).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.action_customsortactivity).withIdentifier(R.id.action_customsortactivity).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.action_opensource).withIdentifier(R.id.action_opensource).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        // Handle action bar item clicks here. The action bar will
                        // automatically handle clicks on the Home/Up button, so long
                        // as you specify a parent activity in AndroidManifest.xml.

                        long id = drawerItem.getIdentifier();
                        if (id == R.id.action_opensource) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikepenz/AboutLibraries"));
                            startActivity(browserIntent);
                        } else if (id == R.id.action_extendactivity) {
                            Intent intent = new Intent(getApplicationContext(), ExtendActivity.class);
                            startActivity(intent);
                        } else if (id == R.id.action_customsortactivity) {
                            Intent intent = new Intent(getApplicationContext(), CustomSortActivity.class);
                            startActivity(intent);
                        } else if (id == R.id.action_manifestactivity) {
                            new LibsBuilder()
                                    .withLibraries("crouton", "actionbarsherlock", "showcaseview", "glide")
                                    .withAutoDetect(false)
                                    .withLicenseShown(true)
                                    .withVersionShown(true)
                                    .withActivityTitle("Open Source")
                                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                    .withListener(libsListener)
                                    .withLibTaskCallback(libTaskCallback)
                                    .withUiListener(libsUIListener)
                                    .start(FragmentActivity.this);
                        }

                        return false;
                    }
                })
                .build();


        /*
        //NOTE: This is how you can modify a specific library definition during runtime
        HashMap<String, HashMap<String, String>> libsModification = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> modifyAboutLibraries = new HashMap<String, String>();
        modifyAboutLibraries.put("name", "_AboutLibraries");
        libsModification.put("aboutlibraries", modifyAboutLibraries);
        .withLibraryModification(libsModification);
        */

        LibsSupportFragment fragment = new LibsBuilder()
                .withVersionShown(false)
                .withLicenseShown(true)
                .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries")
                .supportFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

    }

    LibTaskCallback libTaskCallback = new LibTaskCallback() {
        @Override
        public void onLibTaskStarted() {
            Log.e("AboutLibraries", "started");
        }

        @Override
        public void onLibTaskFinished(ItemAdapter fastItemAdapter) {
            Log.e("AboutLibraries", "finished");
        }
    };

    LibsConfiguration.LibsUIListener libsUIListener = new LibsConfiguration.LibsUIListener() {
        @Override
        public View preOnCreateView(View view) {
            return view;
        }

        @Override
        public View postOnCreateView(View view) {
            return view;
        }
    };

    LibsConfiguration.LibsListener libsListener = new LibsConfiguration.LibsListener() {
        @Override
        public void onIconClicked(View v) {
            Toast.makeText(v.getContext(), "We are able to track this now ;)", Toast.LENGTH_LONG).show();
        }

        @Override
        public boolean onLibraryAuthorClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryContentClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryBottomClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onExtraClicked(View v, Libs.SpecialButton specialButton) {
            return false;
        }

        @Override
        public boolean onIconLongClicked(View v) {
            return false;
        }

        @Override
        public boolean onLibraryAuthorLongClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryContentLongClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryBottomLongClicked(View v, Library library) {
            return false;
        }
    };
}
