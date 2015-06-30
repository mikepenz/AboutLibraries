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
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.R;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.util.MovementCheck;
import com.mikepenz.aboutlibraries.util.RippleForegroundListener;
import com.mikepenz.aboutlibraries.util.UIUtils;

import java.util.LinkedList;
import java.util.List;

public class LibsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private RippleForegroundListener rippleForegroundListener = new RippleForegroundListener(R.id.rippleForegroundListenerView);

    private List<Library> libs = new LinkedList<Library>();

    private boolean header = false;

    private LibsBuilder libsBuilder = null;
    private Integer aboutVersionCode;
    private String aboutVersionName;
    private Drawable aboutIcon;

    public LibsRecyclerViewAdapter(LibsBuilder libsBuilder) {
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
        final Context ctx = viewHolder.itemView.getContext();
        if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            //Set the Icon or hide it
            if (libsBuilder.aboutShowIcon && aboutIcon != null) {
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
            if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial1) && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial1Description)) {
                holder.aboutSpecial1.setText(libsBuilder.aboutAppSpecial1);
                holder.aboutSpecial1.setVisibility(View.VISIBLE);
                holder.aboutSpecial1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean consumed = false;
                        if (LibsConfiguration.getInstance().getListener() != null) {
                            consumed = LibsConfiguration.getInstance().getListener().onExtraClicked(v, Libs.SpecialButton.SPECIAL1);
                        }

                        if (!consumed) {
                            try {
                                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                alert.setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial1Description));
                                alert.create().show();
                            } catch (Exception ex) {
                            }
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
                        boolean consumed = false;
                        if (LibsConfiguration.getInstance().getListener() != null) {
                            consumed = LibsConfiguration.getInstance().getListener().onExtraClicked(v, Libs.SpecialButton.SPECIAL2);
                        }

                        if (!consumed) {
                            try {
                                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                alert.setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial2Description));
                                alert.create().show();
                            } catch (Exception ex) {
                            }
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
                        boolean consumed = false;
                        if (LibsConfiguration.getInstance().getListener() != null) {
                            consumed = LibsConfiguration.getInstance().getListener().onExtraClicked(v, Libs.SpecialButton.SPECIAL3);
                        }

                        if (!consumed) {
                            try {
                                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                alert.setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial3Description));
                                alert.create().show();
                            } catch (Exception ex) {
                            }
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
                        boolean consumed = false;
                        if (LibsConfiguration.getInstance().getListener() != null) {
                            consumed = LibsConfiguration.getInstance().getListener().onLibraryAuthorClicked(view, library);
                        }

                        if (!consumed) {
                            openAuthorWebsite(ctx, library.getAuthorWebsite());
                        }
                    }
                });
                holder.libraryCreator.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        boolean consumed = false;
                        if (LibsConfiguration.getInstance().getListener() != null) {
                            consumed = LibsConfiguration.getInstance().getListener().onLibraryAuthorLongClicked(v, library);
                        }

                        if (!consumed) {
                            openAuthorWebsite(ctx, library.getAuthorWebsite());
                            consumed = true;
                        }
                        return consumed;
                    }
                });
            } else {
                holder.libraryCreator.setOnTouchListener(null);
                holder.libraryCreator.setOnClickListener(null);
                holder.libraryCreator.setOnLongClickListener(null);
            }

            if (!TextUtils.isEmpty(library.getLibraryWebsite()) || !TextUtils.isEmpty(library.getRepositoryLink())) {
                holder.libraryDescription.setOnTouchListener(rippleForegroundListener);
                holder.libraryDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean consumed = false;
                        if (LibsConfiguration.getInstance().getListener() != null) {
                            consumed = LibsConfiguration.getInstance().getListener().onLibraryContentClicked(v, library);
                        }

                        if (!consumed) {
                            openLibraryWebsite(ctx, library.getLibraryWebsite() != null ? library.getLibraryWebsite() : library.getRepositoryLink());
                        }
                    }
                });
                holder.libraryDescription.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        boolean consumed = false;
                        if (LibsConfiguration.getInstance().getListener() != null) {
                            consumed = LibsConfiguration.getInstance().getListener().onLibraryContentLongClicked(v, library);
                        }

                        if (!consumed) {
                            openLibraryWebsite(ctx, library.getLibraryWebsite() != null ? library.getLibraryWebsite() : library.getRepositoryLink());
                            consumed = true;
                        }
                        return consumed;
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
                        boolean consumed = false;
                        if (LibsConfiguration.getInstance().getListener() != null) {
                            consumed = LibsConfiguration.getInstance().getListener().onLibraryBottomClicked(view, library);
                        }

                        if (!consumed) {
                            openLicense(ctx, libsBuilder, library);
                        }
                    }
                });
                holder.libraryBottomContainer.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        boolean consumed = false;
                        if (LibsConfiguration.getInstance().getListener() != null) {
                            consumed = LibsConfiguration.getInstance().getListener().onLibraryBottomLongClicked(v, library);
                        }

                        if (!consumed) {
                            openLicense(ctx, libsBuilder, library);
                            consumed = true;
                        }
                        return consumed;
                    }
                });
            } else {
                holder.libraryBottomContainer.setOnTouchListener(null);
                holder.libraryBottomContainer.setOnClickListener(null);
                holder.libraryBottomContainer.setOnLongClickListener(null);

            }
        }
    }

    /**
     * helper method to open the author website
     *
     * @param ctx
     * @param authorWebsite
     */
    private void openAuthorWebsite(Context ctx, String authorWebsite) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorWebsite));
            ctx.startActivity(browserIntent);
        } catch (Exception ex) {
        }
    }

    /**
     * helper method to open the library website
     *
     * @param ctx
     * @param libraryWebsite
     */
    private void openLibraryWebsite(Context ctx, String libraryWebsite) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(libraryWebsite));
            ctx.startActivity(browserIntent);
        } catch (Exception ex) {
        }
    }

    /**
     * helper method to open the license dialog / or website
     *
     * @param ctx
     * @param libsBuilder
     * @param library
     */
    private void openLicense(Context ctx, LibsBuilder libsBuilder, Library library) {
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
            aboutAppName.setTextColor(UIUtils.getThemeColorFromAttrOrRes(headerView.getContext(), R.attr.about_libraries_title_openSource, R.color.about_libraries_title_openSource));
            aboutSpecialContainer = headerView.findViewById(R.id.aboutSpecialContainer);
            aboutSpecial1 = (Button) headerView.findViewById(R.id.aboutSpecial1);
            aboutSpecial2 = (Button) headerView.findViewById(R.id.aboutSpecial2);
            aboutSpecial3 = (Button) headerView.findViewById(R.id.aboutSpecial3);
            aboutVersion = (TextView) headerView.findViewById(R.id.aboutVersion);
            aboutVersion.setTextColor(UIUtils.getThemeColorFromAttrOrRes(headerView.getContext(), R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource));
            aboutDivider = headerView.findViewById(R.id.aboutDivider);
            aboutDivider.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(headerView.getContext(), R.attr.about_libraries_dividerDark_openSource, R.color.about_libraries_dividerDark_openSource));
            aboutAppDescription = (TextView) headerView.findViewById(R.id.aboutDescription);
            aboutAppDescription.setTextColor(UIUtils.getThemeColorFromAttrOrRes(headerView.getContext(), R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;

        TextView libraryName;
        TextView libraryCreator;
        View libraryDescriptionDivider;
        TextView libraryDescription;

        View libraryBottomDivider;
        View libraryBottomContainer;

        TextView libraryVersion;
        TextView libraryLicense;

        public ViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView;
            card.setCardBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), R.attr.about_libraries_card, R.color.about_libraries_card));

            libraryName = (TextView) itemView.findViewById(R.id.libraryName);
            libraryName.setTextColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), R.attr.about_libraries_title_openSource, R.color.about_libraries_title_openSource));
            libraryCreator = (TextView) itemView.findViewById(R.id.libraryCreator);
            libraryCreator.setTextColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource));
            libraryDescriptionDivider = itemView.findViewById(R.id.libraryDescriptionDivider);
            libraryDescriptionDivider.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), R.attr.about_libraries_dividerLight_openSource, R.color.about_libraries_dividerLight_openSource));
            libraryDescription = (TextView) itemView.findViewById(R.id.libraryDescription);
            libraryDescription.setTextColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource));

            libraryBottomDivider = itemView.findViewById(R.id.libraryBottomDivider);
            libraryBottomDivider.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), R.attr.about_libraries_dividerLight_openSource, R.color.about_libraries_dividerLight_openSource));
            libraryBottomContainer = itemView.findViewById(R.id.libraryBottomContainer);

            libraryVersion = (TextView) itemView.findViewById(R.id.libraryVersion);
            libraryVersion.setTextColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource));
            libraryLicense = (TextView) itemView.findViewById(R.id.libraryLicense);
            libraryLicense.setTextColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource));

        }

    }
}
