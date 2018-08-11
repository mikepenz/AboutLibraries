package com.mikepenz.aboutlibraries;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.animation.LayoutAnimationController;

import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.item.HeaderItem;
import com.mikepenz.aboutlibraries.ui.item.LibraryItem;

/**
 * Created by mikepenz on 20.05.15.
 */
@SuppressWarnings("unused")
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
    private LibsListener mListener = null;

    public void setListener(LibsListener libsListener) {
        this.mListener = libsListener;
    }

    public LibsListener getListener() {
        return mListener;
    }

    public void removeListener() {
        this.mListener = null;
    }

    private LibsUIListener mUiListener = null;

    public LibsUIListener getUiListener() {
        return mUiListener;
    }

    public void setUiListener(LibsUIListener uiListener) {
        this.mUiListener = uiListener;
    }

    public void removeUiListener() {
        this.mUiListener = null;
    }


    private LibsRecyclerViewListener mRecyclerViewListener = null;

    public LibsRecyclerViewListener getLibsRecyclerViewListener() {
        return mRecyclerViewListener;
    }

    public void setLibsRecyclerViewListener(LibsRecyclerViewListener recyclerViewListener) {
        this.mRecyclerViewListener = recyclerViewListener;
    }

    public void removeLibsRecyclerViewListener() {
        this.mRecyclerViewListener = null;
    }


    private LayoutAnimationController mLayoutAnimationController = null;

    public LayoutAnimationController getLayoutAnimationController() {
        return mLayoutAnimationController;
    }

    public void setLayoutAnimationController(LayoutAnimationController layoutAnimationController) {
        this.mLayoutAnimationController = layoutAnimationController;
    }

    private RecyclerView.ItemAnimator mItemAnimator;

    public RecyclerView.ItemAnimator getItemAnimator() {
        return mItemAnimator;
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        this.mItemAnimator = itemAnimator;
    }

    private LibTaskCallback mLibTaskCallback;

    public LibTaskCallback getLibTaskCallback() {
        return mLibTaskCallback;
    }

    public void setLibTaskCallback(LibTaskCallback mLibTaskCallback) {
        this.mLibTaskCallback = mLibTaskCallback;
    }

    /**
     * helper to reset a current configuration
     * is only useful for the sample app
     */
    public void reset() {
        SINGLETON = null;
    }


    public interface LibsUIListener {
        /**
         * PreOnCreateView method called before the view was created
         *
         * @param view
         * @return
         */
        View preOnCreateView(View view);

        /**
         * PostOnCreateView method called after the view was created
         *
         * @param view
         * @return
         */
        View postOnCreateView(View view);
    }

    public interface LibsRecyclerViewListener {
        /**
         * OnBindHeaderViewHolder called after the headerView was filled inside the recyclerViews onBindViewHolder method
         *
         * @param headerViewHolder
         */
        void onBindViewHolder(HeaderItem.ViewHolder headerViewHolder);

        /**
         * onBindViewHolder called after the item view was filled inside the recyclerViews onBindViewHolder method
         *
         * @param viewHolder
         */
        void onBindViewHolder(LibraryItem.ViewHolder viewHolder);
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

    public static abstract class LibsRecyclerViewListenerImpl implements LibsRecyclerViewListener {
        @Override
        public void onBindViewHolder(HeaderItem.ViewHolder headerViewHolder) {
        }

        @Override
        public void onBindViewHolder(LibraryItem.ViewHolder holder) {
        }
    }

    public static abstract class LibsListenerImpl implements LibsListener {
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
