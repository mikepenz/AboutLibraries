package com.mikepenz.aboutlibraries.ui.adapter;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.R;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.util.MovementCheck;

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
    private int returnCode = Libs.INVALID_RESULT_CODE;

    private boolean header = false;
    private String aboutAppName;
    private String aboutSpecial1;
    private String aboutSpecial1Description;
    private String aboutSpecial2;
    private String aboutSpecial2Description;
    private String aboutSpecial3;
    private String aboutSpecial3Description;
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

            //Set the description or hide it
            if (!TextUtils.isEmpty(aboutAppName)) {
                holder.aboutAppName.setText(aboutAppName);
            } else {
                holder.aboutAppName.setVisibility(View.GONE);
            }

            // Reset aboutSpecial fields
            holder.aboutSpecialContainer.setVisibility(View.GONE);
            holder.aboutSpecial1.setVisibility(View.GONE);
            holder.aboutSpecial2.setVisibility(View.GONE);
            holder.aboutSpecial3.setVisibility(View.GONE);

            // set the values for the special fields
            if (!TextUtils.isEmpty(aboutSpecial1) && !TextUtils.isEmpty(aboutSpecial1Description)) {
                holder.aboutSpecial1.setText(aboutSpecial1);
                holder.aboutSpecial1.setVisibility(View.VISIBLE);
                holder.aboutSpecial1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                            builder.setMessage(Html.fromHtml(aboutSpecial1Description));
                            builder.create().show();
                        } catch (Exception ex) {
                        }
                    }
                });
                holder.aboutSpecialContainer.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(aboutSpecial2) && !TextUtils.isEmpty(aboutSpecial2Description)) {
                holder.aboutSpecial2.setText(aboutSpecial2);
                holder.aboutSpecial2.setVisibility(View.VISIBLE);
                holder.aboutSpecial2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                            builder.setMessage(Html.fromHtml(aboutSpecial2Description));
                            builder.create().show();
                        } catch (Exception ex) {
                        }
                    }
                });
                holder.aboutSpecialContainer.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(aboutSpecial3) && !TextUtils.isEmpty(aboutSpecial3Description)) {
                holder.aboutSpecial3.setText(aboutSpecial3);
                holder.aboutSpecial3.setVisibility(View.VISIBLE);
                holder.aboutSpecial3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                            builder.setMessage(Html.fromHtml(aboutSpecial3Description));
                            builder.create().show();
                        } catch (Exception ex) {
                        }
                    }
                });
                holder.aboutSpecialContainer.setVisibility(View.VISIBLE);
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
                holder.aboutAppDescription.setMovementMethod(MovementCheck.getInstance());
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
                } else {
                    holder.libraryVersion.setText("");
                }
                if (library.getLicense() != null && !TextUtils.isEmpty(library.getLicense().getLicenseName()) && showLicense) {
                    holder.libraryLicense.setText(library.getLicense().getLicenseName());
                } else {
                    holder.libraryLicense.setText("");
                }
            }

            //Define onClickListener
            if (!TextUtils.isEmpty(library.getAuthorWebsite())) {
                holder.libraryCreator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (validReturnCode() && ctx instanceof Activity) {
                            Intent intent = new Intent();
                            intent.setData(Uri.parse(library.getAuthorWebsite()));
                            ((Activity) ctx).setResult(Activity.RESULT_OK, intent);
                            ((Activity) ctx).finish();
                        } else {
                            try {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getAuthorWebsite()));
                                ctx.startActivity(browserIntent);
                            } catch (Exception ex) {
                            }
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
                        if (validReturnCode() && ctx instanceof Activity) {
                            Intent intent = new Intent();
                            intent.setData(Uri.parse(library.getLibraryWebsite()));
                            ((Activity) ctx).setResult(Activity.RESULT_OK, intent);
                            ((Activity) ctx).finish();
                        } else {
                            try {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getLibraryWebsite()));
                                ctx.startActivity(browserIntent);
                            } catch (Exception ex) {
                            }
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
                            } else if (validReturnCode() && ctx instanceof Activity) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse(library.getLicense().getLicenseWebsite()));
                                ((Activity) ctx).setResult(Activity.RESULT_OK, intent);
                                ((Activity) ctx).finish();
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

    private boolean validReturnCode() {
        return returnCode != Libs.INVALID_RESULT_CODE;
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

    public void setHeader(String aboutAppName, String aboutDescription, String aboutSpecial1, String aboutSpecial1Description, String aboutSpecial2, String aboutSpecial2Description, String aboutSpecial3, String aboutSpecial3Description, String aboutVersionName, Integer aboutVersionCode, Boolean aboutShowVersion, Boolean aboutShowVersionName, Boolean aboutShowVersionCode, Drawable aboutIcon, boolean aboutShowIcon) {
        this.header = true;
        this.libs.add(0, null);
        this.aboutAppName = aboutAppName;
        this.aboutDescription = aboutDescription;
        this.aboutSpecial1 = aboutSpecial1;
        this.aboutSpecial1Description = aboutSpecial1Description;
        this.aboutSpecial2 = aboutSpecial2;
        this.aboutSpecial2Description = aboutSpecial2Description;
        this.aboutSpecial3 = aboutSpecial3;
        this.aboutSpecial3Description = aboutSpecial3Description;
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

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView aboutIcon;
        TextView aboutAppName;
        View aboutSpecialContainer;
        Button aboutSpecial1;
        Button aboutSpecial2;
        Button aboutSpecial3;
        TextView aboutVersion;
        View aboutDivider;
        TextView aboutAppDescription;

        public HeaderViewHolder(View headerView) {
            super(headerView);

            //get the about this app views
            aboutIcon = (ImageView) headerView.findViewById(R.id.aboutIcon);
            aboutAppName = (TextView) headerView.findViewById(R.id.aboutName);
            aboutSpecialContainer = headerView.findViewById(R.id.aboutSpecialContainer);
            aboutSpecial1 = (Button) headerView.findViewById(R.id.aboutSpecial1);
            aboutSpecial2 = (Button) headerView.findViewById(R.id.aboutSpecial2);
            aboutSpecial3 = (Button) headerView.findViewById(R.id.aboutSpecial3);
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
