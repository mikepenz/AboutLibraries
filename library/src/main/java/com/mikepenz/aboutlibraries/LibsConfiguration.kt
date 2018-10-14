@file:JvmName("LibsConfiguration")

package com.mikepenz.aboutlibraries

import android.view.View
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.item.HeaderItem
import com.mikepenz.aboutlibraries.ui.item.LibraryItem

/**
 * Created by mikepenz on 20.05.15.
 */
class LibsConfiguration private constructor() {


    /**
     * LOGIC FOR THE LISTENER
     */
    var listener: LibsListener? = null

    var uiListener: LibsUIListener? = null


    var libsRecyclerViewListener: LibsRecyclerViewListener? = null


    var layoutAnimationController: LayoutAnimationController? = null

    var itemAnimator: RecyclerView.ItemAnimator? = null

    var libTaskCallback: LibTaskCallback? = null

    fun removeListener() {
        this.listener = null
    }

    fun removeUiListener() {
        this.uiListener = null
    }

    fun removeLibsRecyclerViewListener() {
        this.libsRecyclerViewListener = null
    }

    interface LibsUIListener {
        /**
         * PreOnCreateView method called before the view was created
         *
         * @param view
         * @return
         */
        fun preOnCreateView(view: View): View

        /**
         * PostOnCreateView method called after the view was created
         *
         * @param view
         * @return
         */
        fun postOnCreateView(view: View): View
    }

    interface LibsRecyclerViewListener {
        /**
         * OnBindHeaderViewHolder called after the headerView was filled inside the recyclerViews onBindViewHolder method
         *
         * @param headerViewHolder
         */
        fun onBindViewHolder(headerViewHolder: HeaderItem.ViewHolder)

        /**
         * onBindViewHolder called after the item view was filled inside the recyclerViews onBindViewHolder method
         *
         * @param viewHolder
         */
        fun onBindViewHolder(viewHolder: LibraryItem.ViewHolder)
    }

    interface LibsListener {
        /**
         * onClick listener if the icon of the AboutAppSection is clicked
         *
         * @param v
         */
        fun onIconClicked(v: View)

        /**
         * onClick listener if the Author of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        fun onLibraryAuthorClicked(v: View, library: Library): Boolean


        /**
         * onClick listener if the Content of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        fun onLibraryContentClicked(v: View, library: Library): Boolean


        /**
         * onClick listener if the Bottom of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        fun onLibraryBottomClicked(v: View, library: Library): Boolean


        /**
         * onClick listener for one of the three special buttons
         *
         * @param v
         * @param specialButton
         * @return true if consumed and no further action is required
         */
        fun onExtraClicked(v: View, specialButton: Libs.SpecialButton): Boolean

        /**
         * onClick listener if the icon of the AboutAppSection is clicked
         *
         * @param v true if consumed and no further action is required
         */
        fun onIconLongClicked(v: View): Boolean

        /**
         * onClick listener if the Author of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        fun onLibraryAuthorLongClicked(v: View, library: Library): Boolean


        /**
         * onClick listener if the Content of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        fun onLibraryContentLongClicked(v: View, library: Library): Boolean


        /**
         * onClick listener if the Bottom of a Library is clicked
         *
         * @param v
         * @param library
         * @return true if consumed and no further action is required
         */
        fun onLibraryBottomLongClicked(v: View, library: Library): Boolean
    }

    abstract class LibsRecyclerViewListenerImpl : LibsRecyclerViewListener {
        override fun onBindViewHolder(headerViewHolder: HeaderItem.ViewHolder) {}

        override fun onBindViewHolder(holder: LibraryItem.ViewHolder) {}
    }

    abstract class LibsListenerImpl : LibsListener {
        override fun onIconClicked(v: View) {

        }

        override fun onLibraryAuthorClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryContentClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryBottomClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onExtraClicked(v: View, specialButton: Libs.SpecialButton): Boolean {
            return false
        }

        override fun onIconLongClicked(v: View): Boolean {
            return true
        }

        override fun onLibraryAuthorLongClicked(v: View, library: Library): Boolean {
            return true
        }

        override fun onLibraryContentLongClicked(v: View, library: Library): Boolean {
            return true
        }

        override fun onLibraryBottomLongClicked(v: View, library: Library): Boolean {
            return true
        }
    }

    private object Holder { val INSTANCE = LibsConfiguration() }

    companion object {
        val instance: LibsConfiguration by lazy { Holder.INSTANCE }
    }
}
