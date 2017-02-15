package com.mikepenz.aboutlibraries.ui.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.R;
import com.mikepenz.aboutlibraries.util.MovementCheck;
import com.mikepenz.aboutlibraries.util.RippleForegroundListener;
import com.mikepenz.aboutlibraries.util.UIUtils;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.iconics.Iconics;

import java.util.List;


/**
 * Created by mikepenz on 28.12.15.
 */
public class LoaderItem extends AbstractItem<LoaderItem, LoaderItem.ViewHolder> {
    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

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
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    /**
     * our ViewHolder
     */

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public ViewHolder(View headerView) {
            super(headerView);

            //get the about this app views
            progressBar = (ProgressBar) headerView.findViewById(R.id.progressBar);
        }
    }
}
