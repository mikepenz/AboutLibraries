package com.mikepenz.aboutlibraries;

import android.view.View;

import com.mikepenz.aboutlibraries.entity.Library;

/**
 * Created by mikepenz on 20.05.15.
 */
public class LibsConfiguration {

    private static LibsConfiguration SINGLETON = null;

    private LibsConfiguration() {
    }

    public static LibsConfiguration getInstance() {
        if (SINGLETON == null) {
            SINGLETON = new LibsConfiguration();
        }
        return SINGLETON;
    }


    /**
     * LOGIC FOR THE LISTENER
     */
    private LibsListener listener = null;

    public void setListener(LibsListener libsListener) {
        this.listener = libsListener;
    }

    public void removeListener() {
        this.listener = null;
    }

    public LibsListener getListener() {
        return listener;
    }

    /**
     * helper to reset a current configuration
     * is only useful for the sample app
     */
    public void reset() {
        SINGLETON = null;
    }

    public interface LibsListener {
        void onIconClicked(View v);

        /**
         * onClick listener if the Author of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        boolean onLibraryAuthorClicked(View v, Library library);


        /**
         * onClick listener if the Content of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        boolean onLibraryContentClicked(View v, Library library);


        /**
         * onClick listener if the Bottom of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        boolean onLibraryBottomClicked(View v, Library library);


        /**
         * onClick listener for one of the three special buttons
         *
         * @param v
         * @param specialButton
         * @return true if consumed and no further action is required
         */
        boolean onExtraClicked(View v, Libs.SpecialButton specialButton);
    }
}
