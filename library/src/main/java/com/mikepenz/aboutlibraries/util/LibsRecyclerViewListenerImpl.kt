package com.mikepenz.aboutlibraries.util

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.ui.item.HeaderItem

abstract class LibsRecyclerViewListenerImpl : LibsConfiguration.LibsRecyclerViewListener {
    override fun onBindViewHolder(headerViewHolder: HeaderItem.ViewHolder) {}

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder) {}
}