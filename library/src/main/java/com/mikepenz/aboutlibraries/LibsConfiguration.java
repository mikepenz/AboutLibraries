package com.mikepenz.aboutlibraries;

import android.view.View;
import android.view.animation.LayoutAnimationController;

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

    public LibsListener getListener() {
        return listener;
    }

    public void removeListener() {
        this.listener = null;
    }


    private LibsUIListener uiListener = null;

    public LibsUIListener getUiListener() {
        return uiListener;
    }

    public void setUiListener(LibsUIListener uiListener) {
        this.uiListener = uiListener;
    }

    public void removeUiListener() {
        this.uiListener = null;
    }


    private LayoutAnimationController layoutAnimationController = null;

    public LayoutAnimationController getLayoutAnimationController() {
        return layoutAnimationController;
    }

    public void setLayoutAnimationController(LayoutAnimationController layoutAnimationController) {
        this.layoutAnimationController = layoutAnimationController;
    }

    /**
     * helper to reset a current configuration
     * is only useful for the sample app
     */
    public void reset() {
        SINGLETON = null;
    }


    public interface LibsUIListener {
        View preOnCreateView(View view);

        View postOnCreateView(View view);
    }

    public interface LibsListener {
        /**
         * onClick listener if the icon of the AboutAppSection is clicked
         *
         * @param v
         */
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

        /**
         * onClick listener if the icon of the AboutAppSection is clicked
         *
         * @param v true if consumed and no further action is required
         */
        boolean onIconLongClicked(View v);

        /**
         * onClick listener if the Author of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        boolean onLibraryAuthorLongClicked(View v, Library library);


        /**
         * onClick listener if the Content of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        boolean onLibraryContentLongClicked(View v, Library library);


        /**
         * onClick listener if the Bottom of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        boolean onLibraryBottomLongClicked(View v, Library library);
    }

    public abstract class LibsListenerImpl implements LibsListener {
        @Override
        public void onIconClicked(View v) {

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
            return true;
        }

        @Override
        public boolean onLibraryAuthorLongClicked(View v, Library library) {
            return true;
        }

        @Override
        public boolean onLibraryContentLongClicked(View v, Library library) {
            return true;
        }

        @Override
        public boolean onLibraryBottomLongClicked(View v, Library library) {
            return true;
        }
    }
}
