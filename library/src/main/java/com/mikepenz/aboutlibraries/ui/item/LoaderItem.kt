package com.mikepenz.aboutlibraries.ui.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.R
import com.mikepenz.fastadapter.items.AbstractItem


/**
 * Created by mikepenz on 28.12.15.
 */
class LoaderItem : AbstractItem<LoaderItem, LoaderItem.ViewHolder>() {

    override fun isSelectable(): Boolean {
        return false
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override fun getType(): Int {
        return R.id.loader_item_id
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override fun getLayoutRes(): Int {
        return R.layout.listloader_opensource
    }

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
