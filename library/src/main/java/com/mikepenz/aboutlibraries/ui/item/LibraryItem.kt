package com.mikepenz.aboutlibraries.ui.item

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.R
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.getThemeColorFromAttrOrRes
import com.mikepenz.fastadapter.items.AbstractItem


/**
 * Created by mikepenz on 28.12.15.
 */
class LibraryItem(private val library: Library, private val libsBuilder: LibsBuilder) : AbstractItem<LibraryItem, LibraryItem.ViewHolder>() {
    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override fun getType(): Int {
        return R.id.library_item_id
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override fun getLayoutRes(): Int {
        return R.layout.listitem_opensource
    }

    override fun isSelectable(): Boolean {
        return false
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
        super.bindView(holder, payloads)

        //ctx
        val ctx = holder.itemView.context

        //Set texts
        holder.libraryName.text = library.libraryName
        holder.libraryCreator.text = library.author
        if (TextUtils.isEmpty(library.libraryDescription)) {
            holder.libraryDescription.text = library.libraryDescription
        } else {
            holder.libraryDescription.text = Html.fromHtml(library.libraryDescription)
        }

        //Set License or Version Text
        if (TextUtils.isEmpty(library.libraryVersion) && library.license != null && library.license?.licenseName?.isEmpty() == true || (!libsBuilder.showVersion) && (!libsBuilder.showLicense)) {
            holder.libraryBottomDivider.visibility = View.GONE
            holder.libraryBottomContainer.visibility = View.GONE
        } else {
            holder.libraryBottomDivider.visibility = View.VISIBLE
            holder.libraryBottomContainer.visibility = View.VISIBLE

            if (!TextUtils.isEmpty(library.libraryVersion) && libsBuilder.showVersion) {
                holder.libraryVersion.text = library.libraryVersion
            } else {
                holder.libraryVersion.text = ""
            }
            if (library.license != null && library.license?.licenseName?.isNotEmpty() == true && libsBuilder.showLicense) {
                holder.libraryLicense.text = library.license?.licenseName
            } else {
                holder.libraryLicense.text = ""
            }
        }


        //Define onClickListener
        if (!TextUtils.isEmpty(library.authorWebsite)) {
            holder.libraryCreator.setOnClickListener { view ->
                val consumed = LibsConfiguration.instance.listener?.onLibraryAuthorClicked(view, library)
                        ?: false
                if (!consumed) {
                    openAuthorWebsite(ctx, library.authorWebsite)
                }
            }
            holder.libraryCreator.setOnLongClickListener { v ->
                var consumed = LibsConfiguration.instance.listener?.onLibraryAuthorLongClicked(v, library)
                        ?: false

                if (!consumed) {
                    openAuthorWebsite(ctx, library.authorWebsite)
                    consumed = true
                }
                consumed
            }
        } else {
            holder.libraryCreator.setOnTouchListener(null)
            holder.libraryCreator.setOnClickListener(null)
            holder.libraryCreator.setOnLongClickListener(null)
        }

        if (!TextUtils.isEmpty(library.libraryWebsite) || !TextUtils.isEmpty(library.repositoryLink)) {
            holder.libraryDescription.setOnClickListener { v ->
                val consumed = LibsConfiguration.instance.listener?.onLibraryContentClicked(v, library)
                        ?: false
                if (!consumed) {
                    openLibraryWebsite(ctx, if (library.libraryWebsite != null) library.libraryWebsite else library.repositoryLink)
                }
            }
            holder.libraryDescription.setOnLongClickListener { v ->
                var consumed = LibsConfiguration.instance.listener?.onLibraryContentLongClicked(v, library)
                        ?: false

                if (!consumed) {
                    openLibraryWebsite(ctx, if (library.libraryWebsite != null) library.libraryWebsite else library.repositoryLink)
                    consumed = true
                }
                consumed
            }
        } else {
            holder.libraryDescription.setOnTouchListener(null)
            holder.libraryDescription.setOnClickListener(null)
            holder.libraryDescription.setOnLongClickListener(null)
        }

        if (library.license != null && (library.license?.licenseWebsite?.isNotEmpty() == true || libsBuilder.showLicenseDialog)) {
            holder.libraryBottomContainer.setOnClickListener { view ->
                val consumed = LibsConfiguration.instance.listener?.onLibraryBottomClicked(view, library)
                        ?: false
                if (!consumed) {
                    openLicense(ctx, libsBuilder, library)
                }
            }
            holder.libraryBottomContainer.setOnLongClickListener { v ->
                var consumed = LibsConfiguration.instance.listener?.onLibraryBottomLongClicked(v, library)
                        ?: false
                if (!consumed) {
                    openLicense(ctx, libsBuilder, library)
                    consumed = true
                }
                consumed
            }
        } else {
            holder.libraryBottomContainer.setOnTouchListener(null)
            holder.libraryBottomContainer.setOnClickListener(null)
            holder.libraryBottomContainer.setOnLongClickListener(null)

        }

        //notify the libsRecyclerViewListener to allow modifications
        LibsConfiguration.instance.libsRecyclerViewListener?.onBindViewHolder(holder)
    }

    /**
     * helper method to open the author website
     *
     * @param ctx           Context for startActivity
     * @param authorWebsite Url to lib-website
     */
    private fun openAuthorWebsite(ctx: Context, authorWebsite: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authorWebsite))
            ctx.startActivity(browserIntent)
        } catch (ex: Exception) {
        }

    }

    /**
     * helper method to open the library website
     *
     * @param ctx            Context for startActivity
     * @param libraryWebsite Url to lib-website
     */
    private fun openLibraryWebsite(ctx: Context, libraryWebsite: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(libraryWebsite))
            ctx.startActivity(browserIntent)
        } catch (ex: Exception) {
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
            if (libsBuilder.showLicenseDialog && library.license?.licenseDescription?.isNotEmpty() == true) {
                val builder = AlertDialog.Builder(ctx)
                builder.setMessage(Html.fromHtml(library.license?.licenseDescription))
                builder.create().show()
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(library.license?.licenseWebsite))
                ctx.startActivity(browserIntent)
            }
        } catch (ex: Exception) {
        }

    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var card: MaterialCardView = itemView as MaterialCardView

        internal var libraryName: TextView = itemView.findViewById(R.id.libraryName) as TextView
        internal var libraryCreator: TextView = itemView.findViewById(R.id.libraryCreator) as TextView
        internal var libraryDescriptionDivider: View = itemView.findViewById(R.id.libraryDescriptionDivider)
        internal var libraryDescription: TextView = itemView.findViewById(R.id.libraryDescription) as TextView

        internal var libraryBottomDivider: View = itemView.findViewById(R.id.libraryBottomDivider)
        internal var libraryBottomContainer: View = itemView.findViewById(R.id.libraryBottomContainer)

        internal var libraryVersion: TextView = itemView.findViewById(R.id.libraryVersion) as TextView
        internal var libraryLicense: TextView = itemView.findViewById(R.id.libraryLicense) as TextView

        init {
            card.setCardBackgroundColor(itemView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_card, R.color.about_libraries_card))
            libraryName.setTextColor(itemView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_title_openSource, R.color.about_libraries_title_openSource))
            libraryCreator.setTextColor(itemView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource))
            libraryDescriptionDivider.setBackgroundColor(itemView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_dividerLight_openSource, R.color.about_libraries_dividerLight_openSource))
            libraryDescription.setTextColor(itemView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource))
            libraryBottomDivider.setBackgroundColor(itemView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_dividerLight_openSource, R.color.about_libraries_dividerLight_openSource))
            libraryVersion.setTextColor(itemView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource))
            libraryLicense.setTextColor(itemView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_text_openSource, R.color.about_libraries_text_openSource))
        }
    }
}
