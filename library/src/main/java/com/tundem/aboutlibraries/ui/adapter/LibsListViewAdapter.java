package com.tundem.aboutlibraries.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tundem.aboutlibraries.R;
import com.tundem.aboutlibraries.entity.Library;

import java.util.List;

public class LibsListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Library> libs;

    private boolean showLicense = false;
    private boolean showVersion = false;

    public LibsListViewAdapter(Context ctx, List<Library> libs, boolean showLicense, boolean showVersion) {
        this.libs = libs;
        this.inflater = LayoutInflater.from(ctx);

        this.showLicense = showLicense;
        this.showVersion = showVersion;
    }

    @Override
    public int getCount() {
        return libs.size();
    }

    @Override
    public Library getItem(int pos) {
        return libs.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final Library library = this.getItem(position);
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_opensource, null);

            holder = new ViewHolder();
            holder.libraryName = (TextView) convertView.findViewById(R.id.libraryName);
            holder.libraryCreator = (TextView) convertView.findViewById(R.id.libraryCreator);
            holder.libraryDescription = (TextView) convertView.findViewById(R.id.libraryDescription);

            holder.libraryBottomDivider = convertView.findViewById(R.id.libraryBottomDivider);
            holder.libraryBottomContainer = convertView.findViewById(R.id.libraryBottomContainer);

            holder.libraryVersion = (TextView) convertView.findViewById(R.id.libraryVersion);
            holder.libraryLicense = (TextView) convertView.findViewById(R.id.libraryLicense);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Set texts
        holder.libraryName.setText(library.getLibraryName());
        holder.libraryCreator.setText(library.getAuthor());
        holder.libraryDescription.setText(library.getLibraryDescription());

        //Set License or Version Text
        if (TextUtils.isEmpty(library.getLibraryVersion()) && TextUtils.isEmpty(library.getLicenseVersion()) || (!showVersion && !showLicense)) {
            holder.libraryBottomDivider.setVisibility(View.GONE);
            holder.libraryBottomContainer.setVisibility(View.GONE);
        } else {
            holder.libraryBottomDivider.setVisibility(View.VISIBLE);
            holder.libraryBottomContainer.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(library.getLibraryVersion()) && showVersion) {
                holder.libraryVersion.setText(library.getLibraryVersion());
            }
            if (!TextUtils.isEmpty(library.getLicenseVersion()) && showLicense) {
                holder.libraryLicense.setText(library.getLicenseVersion());
            }
        }

        //Define onClickListener
        if (!TextUtils.isEmpty(library.getAuthorWebsite())) {
            holder.libraryCreator.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getAuthorWebsite()));
                        parent.getContext().startActivity(browserIntent);
                    } catch (Exception ex) {
                    }
                }
            });
        }

        if (!TextUtils.isEmpty(library.getLibraryWebsite())) {
            holder.libraryDescription.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getLibraryWebsite()));
                        parent.getContext().startActivity(browserIntent);
                    } catch (Exception ex) {
                    }
                }
            });
        }

        if (!TextUtils.isEmpty((library.getLicenseLink()))) {
            holder.libraryBottomContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getLicenseLink()));
                        parent.getContext().startActivity(browserIntent);
                    } catch (Exception ex) {
                    }
                }
            });
        }

        int padding = parent.getResources().getDimensionPixelSize(R.dimen.card_padding_opensource);
        if (position + 1 == getCount()) {
            convertView.setPadding(padding, padding, padding, padding);
        } else {
            convertView.setPadding(padding, padding, padding, 0);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView libraryName;
        TextView libraryCreator;
        TextView libraryDescription;

        View libraryBottomDivider;
        View libraryBottomContainer;

        TextView libraryVersion;
        TextView libraryLicense;
    }
}
