package com.tundem.aboutlibraries.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    public LibsListViewAdapter(Context ctx, List<Library> libs) {
        this.libs = libs;
        this.inflater = LayoutInflater.from(ctx);
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
            holder.libraryVersion = (TextView) convertView.findViewById(R.id.libraryVersion);
            holder.libraryCreator = (TextView) convertView.findViewById(R.id.libraryCreator);
            holder.libraryDescription = (TextView) convertView.findViewById(R.id.libraryDescription);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.libraryName.setText(library.getLibraryName());
        holder.libraryVersion.setText(library.getLibraryVersion());
        holder.libraryCreator.setText(library.getAuthor());
        holder.libraryDescription.setText(library.getLibraryDescription());

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(library.getLibraryWebsite()));
                    parent.getContext().startActivity(browserIntent);
                } catch (Exception ex) {
                }
            }
        });

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
        TextView libraryVersion;
        TextView libraryCreator;
        TextView libraryDescription;
    }
}
