package com.mikepenz.aboutlibraries.ui.item;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.R;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.util.RippleForegroundListener;
import com.mikepenz.aboutlibraries.util.UIUtils;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


/**
 * Created by mikepenz on 28.12.15.
 */
public class LibraryItem extends AbstractItem<LibraryItem, LibraryItem.ViewHolder> {
    private RippleForegroundListener rippleForegroundListener = new RippleForegroundListener(R.id.rippleForegroundListenerView);

    public Library library;

    public LibraryItem withLibrary(Library library) {
        this.library = library;
        return this;
    }


    public LibsBuilder libsBuilder;

    public LibraryItem withLibsBuilder(LibsBuilder libsBuilder) {
        this.libsBuilder = libsBuilder;
        return this;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.library_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.listitem_opensource;
    }

    @Override
    public boolean isSelectable() {
        return false;
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

        if (library.getLicense() != null && (!TextUtils.isEmpty(library.getLicense().getLicenseWebsite()) || libsBuilder.showLicenseDialog)) {
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

        //notify the libsRecyclerViewListener to allow modifications
        if (LibsConfiguration.getInstance().getLibsRecyclerViewListener() != null) {
            LibsConfiguration.getInstance().getLibsRecyclerViewListener().onBindViewHolder(holder);
        }
    }

    /**
     * helper method to open the author website
     *
     * @param ctx           Context for startActivity
     * @param authorWebsite Url to lib-website
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
     * @param ctx            Context for startActivity
     * @param libraryWebsite Url to lib-website
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
     * @param ctx         Context for startActivity
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
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */

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
