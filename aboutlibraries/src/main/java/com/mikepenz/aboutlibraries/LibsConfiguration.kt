@file:JvmName("LibsConfiguration")

package com.mikepenz.aboutlibraries

import android.view.View
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.item.HeaderItem
import com.mikepenz.fastadapter.IItem

/**
 * Util class to modify behavior of the
 */
object LibsConfiguration {
    /** LOGIC FOR THE LISTENER*/
    var listener: LibsListener? = null

    /** Intercept the UI and allow to modify it */
    var uiListener: LibsUIListener? = null

    /** Interceptor allowing creating custom items for the list */
    var libsItemInterceptor: ((Library, LibsBuilder) -> IItem<*>)? = null

    var libsRecyclerViewListener: LibsRecyclerViewListener? = null

    var layoutAnimationController: LayoutAnimationController? = null

    var itemAnimator: RecyclerView.ItemAnimator? = null

    var libTaskCallback: LibTaskCallback? = null

    /** Allow to adjust text manually, format to HTML, ...*/
    var postTextAction: ((TextView) -> Unit)? = null

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
        fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder)
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
        fun onExtraClicked(v: View, specialButton: LibsBuilder.SpecialButton): Boolean

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
}
