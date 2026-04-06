@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.ui.item

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.R
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.util.license
import com.mikepenz.aboutlibraries.util.resolveStyledValue
import com.mikepenz.fastadapter.items.AbstractItem


/**
 * Created by mikepenz on 28.12.15.
 */
class SimpleLibraryItem(internal val library: Library, private val libsBuilder: LibsBuilder) : AbstractItem<SimpleLibraryItem.ViewHolder>() {
    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.library_simple_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.listitem_minimal_opensource

    override var isSelectable: Boolean
        get() = false
        set(value) {}

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        //ctx
        val ctx = holder.itemView.context

        //Set texts
        holder.libraryName.text = library.name

        if (library.license != null && (library.license?.url?.isNotEmpty() == true || libsBuilder.showLicenseDialog)) {
            holder.itemView.setOnClickListener { view ->
                val consumed = LibsConfiguration.listener?.onLibraryBottomClicked(view, library)
                    ?: false
                if (!consumed) {
                    openLicense(ctx, libsBuilder, library)
                }
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }


    /**
     * helper method to open the license dialog / or website
     *
     * @param ctx         Context for startActivity
     * @param libsBuilder
     * @param library
     */
    private fun openLicense(ctx: Context, libsBuilder: LibsBuilder, library: Library) {
        try {
            if (libsBuilder.showLicenseDialog && library.license?.licenseContent?.isNotEmpty() == true) {
                val builder = MaterialAlertDialogBuilder(ctx)
                builder.setMessage(HtmlCompat.fromHtml(library.license?.htmlReadyLicenseContent ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY))
                builder.create().show()
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(library.license?.url))
                ctx.startActivity(browserIntent)
            }
        } catch (ignored: Exception) {
            // ignored
        }
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var libraryName: TextView = itemView as TextView

        init {
            val ctx = itemView.context
            ctx.resolveStyledValue {
                libraryName.setTextColor(it.getColorStateList(R.styleable.AboutLibraries_aboutLibrariesOpenSourceTitle))
            }
        }
    }
}
