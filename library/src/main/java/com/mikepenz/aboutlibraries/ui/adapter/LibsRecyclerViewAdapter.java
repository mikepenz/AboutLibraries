package com.mikepenz.aboutlibraries.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import com.mikepenz.aboutlibraries.util.RippleForegroundListener;

import java.util.LinkedList;
import java.util.List;

public class LibsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context ctx;

    private List<Library> libs = new LinkedList<Library>();

    private boolean header = false;

    private Libs.Builder libsBuilder = null;
    private Integer aboutVersionCode;
    private String aboutVersionName;
    private Drawable aboutIcon;

    public LibsRecyclerViewAdapter(Context ctx, Libs.Builder libsBuilder) {
        this.ctx = ctx;

        this.libsBuilder = libsBuilder;
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
            if (libsBuilder.aboutShowIcon && aboutIcon != null) {
                holder.aboutIcon.setImageDrawable(aboutIcon);
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
            if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial1) && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial1Description)) {
                holder.aboutSpecial1.setText(libsBuilder.aboutAppSpecial1);
                holder.aboutSpecial1.setVisibility(View.VISIBLE);
                holder.aboutSpecial1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                            alert.setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial1Description));
                            alert.create().show();
                        } catch (Exception ex) {
                        }
                    }
                });
                holder.aboutSpecialContainer.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial2) && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial2Description)) {
                holder.aboutSpecial2.setText(libsBuilder.aboutAppSpecial2);
                holder.aboutSpecial2.setVisibility(View.VISIBLE);
                holder.aboutSpecial2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                            alert.setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial2Description));
                            alert.create().show();
                        } catch (Exception ex) {
                        }
                    }
                });
                holder.aboutSpecialContainer.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial3) && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial3Description)) {
                holder.aboutSpecial3.setText(libsBuilder.aboutAppSpecial3);
                holder.aboutSpecial3.setVisibility(View.VISIBLE);
                holder.aboutSpecial3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                            alert.setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial3Description));
                            alert.create().show();
                        } catch (Exception ex) {
                        }
                    }
                });
                holder.aboutSpecialContainer.setVisibility(View.VISIBLE);
            }


            //set the Version or hide it
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

            //Set the description or hide it
            if (!TextUtils.isEmpty(libsBuilder.aboutDescription)) {
                holder.aboutAppDescription.setText(Html.fromHtml(libsBuilder.aboutDescription));
                holder.aboutAppDescription.setMovementMethod(MovementCheck.getInstance());
            } else {
                holder.aboutAppDescription.setVisibility(View.GONE);
            }

            //if there is no description or no icon and version number hide the divider
            if (!libsBuilder.aboutShowIcon && !libsBuilder.aboutShowVersion || TextUtils.isEmpty(libsBuilder.aboutDescription)) {
                holder.aboutDivider.setVisibility(View.GONE);
            }
        } else if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;

            final Library library = getItem(position);

            RippleForegroundListener rippleForegroundListener = new RippleForegroundListener();
            rippleForegroundListener.setCardView((CardView) holder.itemView);

            //Set texts
            holder.libraryName.setText(library.getLibraryName());
            holder.libraryCreator.setText(library.getAuthor());
            if (TextUtils.isEmpty(library.getLibraryDescription())) {
                holder.libraryDescription.setText(library.getLibraryDescription());
            } else {
                holder.libraryDescription.setText(Html.fromHtml(library.getLibraryDescription()));
            }

            //Set License or Version Text
            if (TextUtils.isEmpty(library.getLibraryVersion()) && library.getLicense() != null && TextUtils.isEmpty(library.getLicense().getLicenseName()) || (!libsBuilder.showVersion && !libsBuilder.showLicense)) {
                holder.libraryBottomDivider.setVisibility(View.GONE);
                holder.libraryBottomContainer.setVisibility(View.GONE);
            } else {
                holder.libraryBottomDivider.setVisibility(View.VISIBLE);
                holder.libraryBottomContainer.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(library.getLibraryVersion()) && libsBuilder.showVersion) {
                    holder.libraryVersion.setText(library.getLibraryVersion());
                } else {
                    holder.libraryVersion.setText("");
                }
                if (library.getLicense() != null && !TextUtils.isEmpty(library.getLicense().getLicenseName()) && libsBuilder.showLicense) {
                    holder.libraryLicense.setText(library.getLicense().getLicenseName());
                } else {
                    holder.libraryLicense.setText("");
                }
            }


            //Define onClickListener
            if (!TextUtils.isEmpty(library.getAuthorWebsite())) {
                holder.libraryCreator.setOnTouchListener(rippleForegroundListener);
                holder.libraryCreator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAuthorWebsite(library.getAuthorWebsite());
                    }
                });
                holder.libraryCreator.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        openAuthorWebsite(library.getAuthorWebsite());
                        return true;
                    }
                });
            } else {
                holder.libraryCreator.setOnTouchListener(null);
                holder.libraryCreator.setOnClickListener(null);
                holder.libraryCreator.setOnLongClickListener(null);
            }

            if (!TextUtils.isEmpty(library.getLibraryWebsite())) {
                holder.libraryDescription.setOnTouchListener(rippleForegroundListener);
                holder.libraryDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLibraryWebsite(library.getLibraryWebsite());
                    }
                });
                holder.libraryDescription.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        openLibraryWebsite(library.getLibraryWebsite());
                        return true;
                    }
                });
            } else {
                holder.libraryDescription.setOnTouchListener(null);
                holder.libraryDescription.setOnClickListener(null);
                holder.libraryDescription.setOnLongClickListener(null);
            }

            if (library.getLicense() != null && !TextUtils.isEmpty((library.getLicense().getLicenseWebsite()))) {
                holder.libraryBottomContainer.setOnTouchListener(rippleForegroundListener);
                holder.libraryBottomContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openLicense(libsBuilder, library);
                    }
                });
                holder.libraryBottomContainer.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        openLicense(libsBuilder, library);
                        return true;
                    }
                });
            } else {
                holder.libraryBottomContainer.setOnTouchListener(null);
                holder.libraryBottomContainer.setOnClickListener(null);
                holder.libraryBottomContainer.setOnLongClickListener(null);

            }
        }
    }

    private void openAuthorWebsite(String authorWebsite) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorWebsite));
            ctx.startActivity(browserIntent);
        } catch (Exception ex) {
        }
    }

    private void openLibraryWebsite(String libraryWebsite) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(libraryWebsite));
            ctx.startActivity(browserIntent);
        } catch (Exception ex) {
        }
    }

    private void openLicense(Libs.Builder libsBuilder, Library library) {
        try {
            if (libsBuilder.showLicenseDialog && !TextUtils.isEmpty(library.getLicense().getLicenseDescription())) {
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

    public void setHeader(String aboutVersionName, Integer aboutVersionCode, Drawable aboutIcon) {
        this.header = true;
        this.libs.add(0, null);
        this.aboutVersionName = aboutVersionName;
        this.aboutVersionCode = aboutVersionCode;
        this.aboutIcon = aboutIcon;
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
