@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.sample

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.sample.legacy.R
import com.mikepenz.aboutlibraries.sample.legacy.databinding.ActivityFragmentBinding
import com.mikepenz.aboutlibraries.util.withContext

/**
 * Created by mikepenz on 04.06.14.
 */
class FragmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        // Remove line to test RTL support
        // window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        // Handle Toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val fragment = LibsBuilder()
            .withVersionShown(true)
            .withLicenseShown(true)
            .withLicenseDialog(true)
            .supportFragment()

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()

        // Showcase to use the library meta information without the UI module
        Libs.Builder().withContext(this).build().libraries
            .forEach {
                Log.d("AboutLibraries", it.name)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button press
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
