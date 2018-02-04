package com.mikepenz.aboutlibraries.ui.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.R;
import com.mikepenz.aboutlibraries.util.MovementCheck;
import com.mikepenz.aboutlibraries.util.RippleForegroundListener;
import com.mikepenz.aboutlibraries.util.UIUtils;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.Iconics;

import java.util.List;


/**
 * Created by mikepenz on 28.12.15.
 */
public class HeaderItem extends AbstractItem<HeaderItem, HeaderItem.ViewHolder> {
    private Integer aboutVersionCode;
    private String aboutVersionName;
    private Drawable aboutIcon;

    public HeaderItem withAboutVersionCode(Integer aboutVersionCode) {
        this.aboutVersionCode = aboutVersionCode;
        return this;
    }

    public HeaderItem withAboutVersionName(String aboutVersionName) {
        this.aboutVersionName = aboutVersionName;
        return this;
    }

    public HeaderItem withAboutIcon(Drawable aboutIcon) {
        this.aboutIcon = aboutIcon;
        return this;
    }

    public LibsBuilder libsBuilder;

    public HeaderItem withLibsBuilder(LibsBuilder libsBuilder) {
        this.libsBuilder = libsBuilder;
        return this;
    }

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
        return R.id.header_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.listheader_opensource;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        //ctx
        final Context ctx = holder.itemView.getContext();

        //Set the Icon or hide it
        if (libsBuilder.aboutShowIcon != null && libsBuilder.aboutShowIcon && aboutIcon != null) {
            holder.aboutIcon.setImageDrawable(aboutIcon);
            holder.aboutIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LibsConfiguration.getInstance().getListener() != null) {
                        LibsConfiguration.getInstance().getListener().onIconClicked(v);
                    }
                }
            });

            holder.aboutIcon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return LibsConfiguration.getInstance().getListener() != null && LibsConfiguration.getInstance().getListener().onIconLongClicked(v);

                }
            });
        } else {
            holder.aboutIcon.setVisibility(View.GONE);
        }

        //Set the description or hide it
        if (!TextUtils.isEmpty(libsBuilder.aboutAppName)) {
            holder.aboutAppName.setText(libsBuilder.aboutAppName);
        } else {
            holder.aboutAppName.setVisibility(View.GONE);
        }

        // Reset aboutSpecial fields
        holder.aboutSpecialContainer.setVisibility(View.GONE);
        holder.aboutSpecial1.setVisibility(View.GONE);
        holder.aboutSpecial2.setVisibility(View.GONE);
        holder.aboutSpecial3.setVisibility(View.GONE);

        // set the values for the special fields
        if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial1) && (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial1Description) || LibsConfiguration.getInstance().getListener() != null)) {
            holder.aboutSpecial1.setText(libsBuilder.aboutAppSpecial1);
            new Iconics.IconicsBuilder().ctx(ctx).on(holder.aboutSpecial1).build();
            holder.aboutSpecial1.setVisibility(View.VISIBLE);
            holder.aboutSpecial1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean consumed = false;
                    if (LibsConfiguration.getInstance().getListener() != null) {
                        consumed = LibsConfiguration.getInstance().getListener().onExtraClicked(v, Libs.SpecialButton.SPECIAL1);
                    }

                    if (!consumed && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial1Description)) {
                        try {
                            AlertDialog alert = new AlertDialog.Builder(ctx)
                                .setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial1Description))
                                .create();
                            alert.show();
                            TextView alertText = (TextView) alert.findViewById(android.R.id.message);
                            if (alertText != null) {
                                alertText.setMovementMethod(LinkMovementMethod.getInstance());
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            });
            holder.aboutSpecialContainer.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial2) && (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial2Description) || LibsConfiguration.getInstance().getListener() != null)) {
            holder.aboutSpecial2.setText(libsBuilder.aboutAppSpecial2);
            new Iconics.IconicsBuilder().ctx(ctx).on(holder.aboutSpecial2).build();
            holder.aboutSpecial2.setVisibility(View.VISIBLE);
            holder.aboutSpecial2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean consumed = false;
                    if (LibsConfiguration.getInstance().getListener() != null) {
                        consumed = LibsConfiguration.getInstance().getListener().onExtraClicked(v, Libs.SpecialButton.SPECIAL2);
                    }

                    if (!consumed && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial2Description)) {
                        try {
                            AlertDialog alert = new AlertDialog.Builder(ctx)
                                .setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial2Description))
                                .create();
                            alert.show();
                            TextView alertText = (TextView) alert.findViewById(android.R.id.message);
                            if (alertText != null) {
                                alertText.setMovementMethod(LinkMovementMethod.getInstance());
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            });
            holder.aboutSpecialContainer.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial3) && (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial3Description) || LibsConfiguration.getInstance().getListener() != null)) {
            holder.aboutSpecial3.setText(libsBuilder.aboutAppSpecial3);
            new Iconics.IconicsBuilder().ctx(ctx).on(holder.aboutSpecial3).build();
            holder.aboutSpecial3.setVisibility(View.VISIBLE);
            holder.aboutSpecial3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean consumed = false;
                    if (LibsConfiguration.getInstance().getListener() != null) {
                        consumed = LibsConfiguration.getInstance().getListener().onExtraClicked(v, Libs.SpecialButton.SPECIAL3);
                    }

                    if (!consumed && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial3Description)) {
                        try {
                            AlertDialog alert = new AlertDialog.Builder(ctx)
                                .setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial3Description))
                                .create();
                            alert.show();
                            TextView alertText = (TextView) alert.findViewById(android.R.id.message);
                            if (alertText != null) {
                                alertText.setMovementMethod(LinkMovementMethod.getInstance());
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            });
            holder.aboutSpecialContainer.setVisibility(View.VISIBLE);
        }


        //set the Version or hide it
        if (libsBuilder.aboutVersionString != null)
            holder.aboutVersion.setText(libsBuilder.aboutVersionString);
        else {
            if (libsBuilder.aboutShowVersion != null && libsBuilder.aboutShowVersion) {
                holder.aboutVersion.setText(ctx.getString(R.string.version) + " " + aboutVersionName + " (" + aboutVersionCode + ")");
            } else {
                if (libsBuilder.aboutShowVersionName != null && libsBuilder.aboutShowVersionName) {
                    holder.aboutVersion.setText(ctx.getString(R.string.version) + " " + aboutVersionName);
                } else if (libsBuilder.aboutShowVersionCode != null && libsBuilder.aboutShowVersionCode) {
                    holder.aboutVersion.setText(ctx.getString(R.string.version) + " " + aboutVersionCode);
                } else {
                    holder.aboutVersion.setVisibility(View.GONE);
                }
            }
        }

        //Set the description or hide it
        if (!TextUtils.isEmpty(libsBuilder.aboutDescription)) {
            holder.aboutAppDescription.setText(Html.fromHtml(libsBuilder.aboutDescription));
            new Iconics.IconicsBuilder().ctx(ctx).on(holder.aboutAppDescription).build();
            holder.aboutAppDescription.setMovementMethod(MovementCheck.getInstance());
        } else {
            holder.aboutAppDescription.setVisibility(View.GONE);
        }

        //if there is no description or no icon and version number hide the divider
        if (!libsBuilder.aboutShowIcon && !libsBuilder.aboutShowVersion || TextUtils.isEmpty(libsBuilder.aboutDescription)) {
            holder.aboutDivider.setVisibility(View.GONE);
        }

        //notify the libsRecyclerViewListener to allow modifications
        if (LibsConfiguration.getInstance().getLibsRecyclerViewListener() != null) {
            LibsConfiguration.getInstance().getLibsRecyclerViewListener().onBindViewHolder(holder);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView aboutIcon;
        TextView aboutAppName;
        View aboutSpecialContainer;
        Button aboutSpecial1;
        Button aboutSpecial2;
        Button aboutSpecial3;
        TextView aboutVersion;
        View aboutDivider;
        TextView aboutAppDescription;

        public ViewHolder(View headerView) {
            super(headerView);

            //get the about this app views
            aboutIcon = (ImageView) headerView.findViewById(R.id.aboutIcon);
            aboutAppName = (TextView) headerView.findViewById(R.id.aboutName);
            aboutAppName.setTextColor(UIUtils.getThemeColorFromAttrOrRes(headerView.getContext(), R.attr.about_libraries_title_description, R.color.about_libraries_title_description));
            aboutSpecialContainer = headerView.findViewById(R.id.aboutSpecialContainer);
            aboutSpecial1 = (Button) headerView.findViewById(R.id.aboutSpecial1);
            aboutSpecial2 = (Button) headerView.findViewById(R.id.aboutSpecial2);
            aboutSpecial3 = (Button) headerView.findViewById(R.id.aboutSpecial3);
            aboutVersion = (TextView) headerView.findViewById(R.id.aboutVersion);
            aboutVersion.setTextColor(UIUtils.getThemeColorFromAttrOrRes(headerView.getContext(), R.attr.about_libraries_text_description, R.color.about_libraries_text_description));
            aboutDivider = headerView.findViewById(R.id.aboutDivider);
            aboutDivider.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(headerView.getContext(), R.attr.about_libraries_divider_description, R.color.about_libraries_divider_description));
            aboutAppDescription = (TextView) headerView.findViewById(R.id.aboutDescription);
            aboutAppDescription.setTextColor(UIUtils.getThemeColorFromAttrOrRes(headerView.getContext(), R.attr.about_libraries_text_description, R.color.about_libraries_text_description));
        }
    }
}
