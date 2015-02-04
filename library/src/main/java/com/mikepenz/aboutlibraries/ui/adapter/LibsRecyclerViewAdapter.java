package com.mikepenz.aboutlibraries.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.R;
import com.mikepenz.aboutlibraries.entity.Library;

import java.util.LinkedList;
import java.util.List;

public class LibsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context ctx;

    private List<Library> libs = new LinkedList<Library>();

    private boolean showLicense = false;
    private boolean showLicenseDialog = true;
    private boolean showVersion = false;

    private boolean header = false;
    private Boolean aboutShowVersion;
    private Boolean aboutShowVersionName;
    private Boolean aboutShowVersionCode;
    private Integer aboutVersionCode;
    private String aboutVersionName;
    private String aboutDescription;
    private boolean aboutShowIcon;
    private Drawable aboutIcon;

    public LibsRecyclerViewAdapter(Context ctx, boolean showLicense, boolean showLicenseDialog, boolean showVersion) {
        this.ctx = ctx;

        this.showLicense = showLicense;
        this.showLicenseDialog = showLicenseDialog;
        this.showVersion = showVersion;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listheader_opensource, viewGroup, false);
            return new HeaderViewHolder(v);
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_opensource, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            //Set the Icon or hide it
            if (aboutShowIcon && aboutIcon != null) {
                holder.aboutIcon.setImageDrawable(aboutIcon);
            } else {
                holder.aboutIcon.setVisibility(View.GONE);
            }

            //set the Version or hide it
            if (aboutShowVersion != null && aboutShowVersion) {
                holder.aboutVersion.setText(ctx.getString(R.string.version) + " " + aboutVersionName + " (" + aboutVersionCode + ")");
            } else {
                if (aboutShowVersionName != null && aboutShowVersionName) {
                    holder.aboutVersion.setText(ctx.getString(R.string.version) + " " + aboutVersionName);
                } else if (aboutShowVersionCode != null && aboutShowVersionCode) {
                    holder.aboutVersion.setText(ctx.getString(R.string.version) + " " + aboutVersionCode);
                } else {
                    holder.aboutVersion.setVisibility(View.GONE);
                }
            }

            //Set the description or hide it
            if (!TextUtils.isEmpty(aboutDescription)) {
                holder.aboutAppDescription.setText(Html.fromHtml(aboutDescription));
            } else {
                holder.aboutAppDescription.setVisibility(View.GONE);
            }

            //if there is no description or no icon and version number hide the divider
            if (!aboutShowIcon && !aboutShowVersion || TextUtils.isEmpty(aboutDescription)) {
                holder.aboutDivider.setVisibility(View.GONE);
            }
        } else if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;

            final Library library = getItem(position);

            //Set texts
            holder.libraryName.setText(library.getLibraryName());
            holder.libraryCreator.setText(library.getAuthor());
            if (TextUtils.isEmpty(library.getLibraryDescription())) {
                holder.libraryDescription.setText(library.getLibraryDescription());
            } else {
                holder.libraryDescription.setText(Html.fromHtml(library.getLibraryDescription()));
            }

            //Set License or Version Text
            if (TextUtils.isEmpty(library.getLibraryVersion()) && library.getLicense() != null && TextUtils.isEmpty(library.getLicense().getLicenseName()) || (!showVersion && !showLicense)) {
                holder.libraryBottomDivider.setVisibility(View.GONE);
                holder.libraryBottomContainer.setVisibility(View.GONE);
            } else {
                holder.libraryBottomDivider.setVisibility(View.VISIBLE);
                holder.libraryBottomContainer.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(library.getLibraryVersion()) && showVersion) {
                    holder.libraryVersion.setText(library.getLibraryVersion());
                }
                if (library.getLicense() != null && !TextUtils.isEmpty(library.getLicense().getLicenseName()) && showLicense) {
                    holder.libraryLicense.setText(library.getLicense().getLicenseName());
                }
            }

            //Define onClickListener
            if (!TextUtils.isEmpty(library.getAuthorWebsite())) {
                holder.libraryCreator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getAuthorWebsite()));
                            ctx.startActivity(browserIntent);
                        } catch (Exception ex) {
                        }
                    }
                });
            } else {
                holder.libraryCreator.setOnClickListener(null);
            }

            if (!TextUtils.isEmpty(library.getLibraryWebsite())) {
                holder.libraryDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getLibraryWebsite()));
                            ctx.startActivity(browserIntent);
                        } catch (Exception ex) {
                        }
                    }
                });
            } else {
                holder.libraryDescription.setOnClickListener(null);
            }

            if (library.getLicense() != null && !TextUtils.isEmpty((library.getLicense().getLicenseWebsite()))) {
                holder.libraryBottomContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (showLicenseDialog && !TextUtils.isEmpty(library.getLicense().getLicenseDescription())) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                builder.setMessage(Html.fromHtml(library.getLicense().getLicenseDescription()));
                                builder.create().show();
                            } else {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getLicense().getLicenseWebsite()));
                                ctx.startActivity(browserIntent);
                            }
                        } catch (Exception ex) {
                        }
                    }
                });
            } else {
                holder.libraryBottomContainer.setOnClickListener(null);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0 && header) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return libs == null ? 0 : libs.size();
    }

    public Library getItem(int pos) {
        return libs.get(pos);
    }

    public long getItemId(int pos) {
        return pos;
    }

    public void setLibs(List<Library> libs) {
        this.libs = libs;
        this.notifyItemRangeInserted(0, libs.size() - 1);
    }

    public void addLibs(List<Library> libs) {
        this.libs.addAll(libs);
    }

    public void setHeader(String aboutDescription, String aboutVersionName, Integer aboutVersionCode, Boolean aboutShowVersion, Boolean aboutShowVersionName, Boolean aboutShowVersionCode, Drawable aboutIcon, boolean aboutShowIcon) {
        this.header = true;
        this.libs.add(0, null);
        this.aboutDescription = aboutDescription;
        this.aboutVersionName = aboutVersionName;
        this.aboutVersionCode = aboutVersionCode;
        this.aboutShowVersion = aboutShowVersion;
        this.aboutShowVersionName = aboutShowVersionName;
        this.aboutShowVersionCode = aboutShowVersionCode;
        this.aboutIcon = aboutIcon;
        this.aboutShowIcon = aboutShowIcon;
        this.notifyItemInserted(0);
    }

    public void deleteHeader() {
        if (header) {
            if (this.libs.size() > 0) {
                this.libs.remove(0);
            }
        }
        this.header = false;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView aboutIcon;
        TextView aboutVersion;
        View aboutDivider;
        TextView aboutAppDescription;

        public HeaderViewHolder(View headerView) {
            super(headerView);

            //get the about this app views
            aboutIcon = (ImageView) headerView.findViewById(R.id.aboutIcon);
            aboutVersion = (TextView) headerView.findViewById(R.id.aboutVersion);
            aboutDivider = headerView.findViewById(R.id.aboutDivider);
            aboutAppDescription = (TextView) headerView.findViewById(R.id.aboutDescription);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View card;

        TextView libraryName;
        TextView libraryCreator;
        TextView libraryDescription;

        View libraryBottomDivider;
        View libraryBottomContainer;

        TextView libraryVersion;
        TextView libraryLicense;

        public ViewHolder(View itemView) {
            super(itemView);
            card = itemView;

            libraryName = (TextView) itemView.findViewById(R.id.libraryName);
            libraryCreator = (TextView) itemView.findViewById(R.id.libraryCreator);
            libraryDescription = (TextView) itemView.findViewById(R.id.libraryDescription);

            libraryBottomDivider = itemView.findViewById(R.id.libraryBottomDivider);
            libraryBottomContainer = itemView.findViewById(R.id.libraryBottomContainer);

            libraryVersion = (TextView) itemView.findViewById(R.id.libraryVersion);
            libraryLicense = (TextView) itemView.findViewById(R.id.libraryLicense);
        }

    }
}
