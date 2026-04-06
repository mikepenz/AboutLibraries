package com.mikepenz.aboutlibraries.ui.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.R
import com.mikepenz.fastadapter.items.AbstractItem


/**
 * Placeholder item while the libraries are loading.
 */
class LoaderItem : AbstractItem<LoaderItem.ViewHolder>() {

    override var isSelectable: Boolean
        get() = false
        set(value) {}

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.loader_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.listloader_opensource

    /**
     * This method returns the ViewHolder for our item, using the provided View.
     *
     * @param v
     * @return the ViewHolder for this Item
     */
    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView)
}
