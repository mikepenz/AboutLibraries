package com.mikepenz.aboutlibraries.ui.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.aboutlibraries.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


/**
 * Created by mikepenz on 28.12.15.
 */
public class LoaderItem extends AbstractItem<LoaderItem, LoaderItem.ViewHolder> {

    @Override
    public boolean isSelectable() {
        return false;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.loader_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.listloader_opensource;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
    }

    /**
     * This method returns the ViewHolder for our item, using the provided View.
     *
     * @param v
     * @return the ViewHolder for this Item
     */
    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View headerView) {
            super(headerView);
        }
    }
}
