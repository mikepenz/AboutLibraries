@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.sample

import android.os.Bundle
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.LibsActivity
import com.mikepenz.aboutlibraries.util.author
import java.io.Serializable


class CustomSortActivity : LibsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val builder = LibsBuilder()
            .withLibraryComparator(LibraryComparator())
            .withSearchEnabled(true)

        intent = builder.intent(this)
        super.onCreate(savedInstanceState)
    }


    private class LibraryComparator : Comparator<Library>, Serializable {

        override fun compare(lhs: Library, rhs: Library): Int {
            // Just to show you can sort however you might want to...
            var result = lhs.author.compareTo(rhs.author)
            if (result == 0) {
                // Backwards sort by lib name.
                result = rhs.name.compareTo(lhs.name)
            }
            return result
        }
    }
}
