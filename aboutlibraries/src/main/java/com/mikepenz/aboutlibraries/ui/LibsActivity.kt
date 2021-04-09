package com.mikepenz.aboutlibraries.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.Libs.Companion.BUNDLE_EDGE_TO_EDGE
import com.mikepenz.aboutlibraries.R
import com.mikepenz.aboutlibraries.util.applyEdgeSystemUi
import com.mikepenz.aboutlibraries.util.doOnApplySystemWindowInsets


/**
 * Created by mikepenz on 04.06.14.
 */
open class LibsActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var fragment: LibsSupportFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        //set the theme
        var edgeToEdge = false
        val bundle = intent.extras
        if (bundle != null) {
            edgeToEdge = bundle.getBoolean(BUNDLE_EDGE_TO_EDGE)
            if (edgeToEdge) {
                applyEdgeSystemUi()
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opensource)
        var title = ""
        if (bundle != null) {
            title = bundle.getString(Libs.BUNDLE_TITLE, "")
        }

        fragment = LibsSupportFragment().apply {
            arguments = bundle
        }

        // Handle Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Support ActionBar :D
        val ab = supportActionBar
        if (ab != null) {
            // SetUp ActionBar
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setDisplayShowTitleEnabled(title.isNotEmpty())
            ab.title = title
        }

        // apply insets
        toolbar.doOnApplySystemWindowInsets(Gravity.TOP, Gravity.START, Gravity.END)

        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val searchEnabled = intent.getBooleanExtra(Libs.BUNDLE_SEARCH_ENABLED, false)
        if (menu != null && searchEnabled) {
            menuInflater.inflate(R.menu.menu_aboutlibs, menu)
            val searchView = menu.findItem(R.id.action_menu_search).actionView as? SearchView
            val editText = searchView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText?.setTextColor(Color.WHITE)
            editText?.setHintTextColor(Color.WHITE)
            searchView?.maxWidth = Int.MAX_VALUE
            searchView?.setOnQueryTextListener(this)
        }

        return super.onCreateOptionsMenu(menu)
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        fragment.filter.filter(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        fragment.filter.filter(newText)
        return true
    }
}
