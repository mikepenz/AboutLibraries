package com.mikepenz.aboutlibraries.sample;

import android.os.Bundle;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

import java.io.Serializable;
import java.util.Comparator;


public class CustomSortActivity extends LibsActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        LibsBuilder builder = new LibsBuilder()
            .withFields(R.string.class.getFields())
            .withLibraries("crouton, actionbarsherlock", "showcaseview")
            .withActivityTheme(R.style.MaterialDrawerTheme)
            .withLibraryComparator(new LibraryComparator());

        setIntent(builder.intent(this));
        super.onCreate(savedInstanceState);
    }


    private static class LibraryComparator implements Comparator<Library>, Serializable {

        @Override
        public int compare(Library lhs, Library rhs) {
            // Just to show you can sort however you might want to...
            int result = lhs.getAuthor().compareTo(rhs.getAuthor());
            if (result == 0) {
                // Backwards sort by lib name.
                result = rhs.getLibraryName().compareTo(lhs.getLibraryName());
            }
            return result;
        }
    }
}
