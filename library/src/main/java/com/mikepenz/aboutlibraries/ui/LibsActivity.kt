package com.mikepenz.aboutlibraries.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.R
import com.mikepenz.aboutlibraries.util.Colors


/**
 * Created by mikepenz on 04.06.14.
 */
open class LibsActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        //set the theme
        var customTheme = false
        var activityStyle: Libs.ActivityStyle = Libs.ActivityStyle.DARK
        val bundle = intent.extras
        if (bundle != null) {
            val themeId = bundle.getInt(Libs.BUNDLE_THEME, -1)
            if (themeId != -1) {
                customTheme = true
                setTheme(themeId)
            }

            val style = bundle.getString(Libs.BUNDLE_STYLE)
            if (style != null) {
                activityStyle = Libs.ActivityStyle.valueOf(style)
            }
        }
        if (!customTheme) {
            if (activityStyle === Libs.ActivityStyle.DARK) {
                setTheme(R.style.AboutLibrariesTheme)
            } else if (activityStyle === Libs.ActivityStyle.LIGHT) {
                setTheme(R.style.AboutLibrariesTheme_Light)
            } else if (activityStyle === Libs.ActivityStyle.LIGHT_DARK_TOOLBAR) {
                setTheme(R.style.AboutLibrariesTheme_Light_DarkToolbar)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opensource)
        var title = ""
        if (bundle != null) {
            title = bundle.getString(Libs.BUNDLE_TITLE, "")
        }
        val fragment = LibsSupportFragment()
        fragment.arguments = bundle


        // Handle Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //if we have a darkToolbar set the text white
        if (activityStyle === Libs.ActivityStyle.LIGHT_DARK_TOOLBAR) {
            toolbar.setTitleTextColor(Color.WHITE)
            toolbar.setSubtitleTextColor(Color.WHITE)
        }
        setSupportActionBar(toolbar)

        // Support ActionBar :D
        val ab = supportActionBar
        if (ab != null) {
            // Set StatusBar Color by Code
            if (bundle != null && bundle.containsKey(Libs.BUNDLE_COLORS)) {
                val colors = bundle.getSerializable(Libs.BUNDLE_COLORS) as Colors
                if (colors != null) {
                    ab.setBackgroundDrawable(ColorDrawable(colors.appBarColor))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.statusBarColor = colors.statusBarColor
                    }
                } else {
                    ab.setBackgroundDrawable(null)
                }
            }

            // SetUp ActionBar
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setDisplayShowTitleEnabled(!TextUtils.isEmpty(title))
            ab.title = title
        }
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this@LibsActivity.finish()
                true
            }
            else -> false
        }
    }
}
