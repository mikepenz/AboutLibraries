package com.mikepenz.aboutlibraries.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.R;
import com.mikepenz.aboutlibraries.util.Colors;

/**
 * Created by mikepenz on 04.06.14.
 */
public class LibsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //set the theme
        boolean customTheme = false;
        Libs.ActivityStyle activityStyle = Libs.ActivityStyle.DARK;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int themeId = bundle.getInt(Libs.BUNDLE_THEME, -1);
            if (themeId != -1) {
                customTheme = true;
                setTheme(themeId);
            }

            String style = bundle.getString(Libs.BUNDLE_STYLE);
            if (style != null) {
                activityStyle = Libs.ActivityStyle.valueOf(style);
            }
        }
        if (!customTheme) {
            if (activityStyle == Libs.ActivityStyle.DARK) {
                setTheme(R.style.AboutLibrariesTheme);
            } else if (activityStyle == Libs.ActivityStyle.LIGHT) {
                setTheme(R.style.AboutLibrariesTheme_Light);
            } else if (activityStyle == Libs.ActivityStyle.LIGHT_DARK_TOOLBAR) {
                setTheme(R.style.AboutLibrariesTheme_Light_DarkToolbar);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensource);
        String title = "";
        if (bundle != null && bundle.containsKey(Libs.BUNDLE_TITLE)) {
            title = bundle.getString(Libs.BUNDLE_TITLE);
        }
        LibsFragment fragment = new LibsFragment();
        fragment.setArguments(bundle);


        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //if we have a darkToolbar set the text white
        if (activityStyle == Libs.ActivityStyle.LIGHT_DARK_TOOLBAR) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setSubtitleTextColor(Color.WHITE);
        }
        setSupportActionBar(toolbar);
        //if we use the DarkToolbar style we have to handle the back arrow on our own too
        if (activityStyle == Libs.ActivityStyle.LIGHT_DARK_TOOLBAR && getSupportActionBar() != null) {
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        // Support ActionBar :D
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Set StatusBar Color by Code
            if (bundle != null && bundle.containsKey(Libs.BUNDLE_COLORS)) {
                Colors colors = (Colors) bundle.getSerializable(Libs.BUNDLE_COLORS);
                if (colors != null) {
                    ab.setBackgroundDrawable(new ColorDrawable(colors.appBarColor));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(colors.statusBarColor);
                    }
                } else {
                    ab.setBackgroundDrawable(null);
                }
            }

            // SetUp ActionBar
            ab.setDisplayHomeAsUpEnabled(true);
            if (TextUtils.isEmpty(title)) {
                ab.setDisplayShowTitleEnabled(false);
            } else {
                ab.setDisplayShowTitleEnabled(true);
                ab.setTitle(title);
            }
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
