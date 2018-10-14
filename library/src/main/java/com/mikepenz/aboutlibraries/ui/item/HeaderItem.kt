package com.mikepenz.aboutlibraries.ui.item

import android.graphics.drawable.Drawable
import android.text.Html
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.R
import com.mikepenz.aboutlibraries.util.MovementCheck
import com.mikepenz.aboutlibraries.util.getThemeColorFromAttrOrRes
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.iconics.Iconics


/**
 * Created by mikepenz on 28.12.15.
 */
class HeaderItem(var libsBuilder: LibsBuilder) : AbstractItem<HeaderItem, HeaderItem.ViewHolder>() {
    private var aboutVersionCode: Int? = null
    private var aboutVersionName: String? = null
    private var aboutIcon: Drawable? = null

    fun withAboutVersionCode(aboutVersionCode: Int?): HeaderItem {
        this.aboutVersionCode = aboutVersionCode
        return this
    }

    fun withAboutVersionName(aboutVersionName: String?): HeaderItem {
        this.aboutVersionName = aboutVersionName
        return this
    }

    fun withAboutIcon(aboutIcon: Drawable?): HeaderItem {
        this.aboutIcon = aboutIcon
        return this
    }

    fun withLibsBuilder(libsBuilder: LibsBuilder): HeaderItem {
        this.libsBuilder = libsBuilder
        return this
    }

    override fun isSelectable(): Boolean {
        return false
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override fun getType(): Int {
        return R.id.header_item_id
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override fun getLayoutRes(): Int {
        return R.layout.listheader_opensource
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

        //Set the Icon or hide it
        if (libsBuilder.aboutShowIcon && aboutIcon != null) {
            holder.aboutIcon.setImageDrawable(aboutIcon)
            holder.aboutIcon.setOnClickListener {
                LibsConfiguration.instance.listener?.onIconClicked(it)
            }

            holder.aboutIcon.setOnLongClickListener { v -> LibsConfiguration.instance.listener != null && LibsConfiguration.instance.listener?.onIconLongClicked(v) ?: false }
        } else {
            holder.aboutIcon.visibility = View.GONE
        }

        //Set the description or hide it
        if (!TextUtils.isEmpty(libsBuilder.aboutAppName)) {
            holder.aboutAppName.text = libsBuilder.aboutAppName
        } else {
            holder.aboutAppName.visibility = View.GONE
        }

        // Reset aboutSpecial fields
        holder.aboutSpecialContainer.visibility = View.GONE
        holder.aboutSpecial1.visibility = View.GONE
        holder.aboutSpecial2.visibility = View.GONE
        holder.aboutSpecial3.visibility = View.GONE

        // set the values for the special fields
        if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial1) && (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial1Description) || LibsConfiguration.instance.listener != null)) {
            holder.aboutSpecial1.text = libsBuilder.aboutAppSpecial1
            Iconics.IconicsBuilder().ctx(ctx).on(holder.aboutSpecial1).build()
            holder.aboutSpecial1.visibility = View.VISIBLE
            holder.aboutSpecial1.setOnClickListener { v ->
                val consumed = LibsConfiguration.instance.listener?.onExtraClicked(v, Libs.SpecialButton.SPECIAL1)
                        ?: false

                if (!consumed && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial1Description)) {
                    try {
                        val alert = AlertDialog.Builder(ctx)
                                .setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial1Description))
                                .create()
                        alert.show()
                        val alertText = alert.findViewById<View>(android.R.id.message) as TextView?
                        if (alertText != null) {
                            alertText.movementMethod = LinkMovementMethod.getInstance()
                        }
                    } catch (ex: Exception) {
                    }

                }
            }
            holder.aboutSpecialContainer.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial2) && (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial2Description) || LibsConfiguration.instance.listener != null)) {
            holder.aboutSpecial2.text = libsBuilder.aboutAppSpecial2
            Iconics.IconicsBuilder().ctx(ctx).on(holder.aboutSpecial2).build()
            holder.aboutSpecial2.visibility = View.VISIBLE
            holder.aboutSpecial2.setOnClickListener { v ->
                val consumed = LibsConfiguration.instance.listener?.onExtraClicked(v, Libs.SpecialButton.SPECIAL2)
                        ?: false
                if (!consumed && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial2Description)) {
                    try {
                        val alert = AlertDialog.Builder(ctx)
                                .setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial2Description))
                                .create()
                        alert.show()
                        val alertText = alert.findViewById<View>(android.R.id.message) as TextView?
                        if (alertText != null) {
                            alertText.movementMethod = LinkMovementMethod.getInstance()
                        }
                    } catch (ex: Exception) {
                    }

                }
            }
            holder.aboutSpecialContainer.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial3) && (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial3Description) || LibsConfiguration.instance.listener != null)) {
            holder.aboutSpecial3.text = libsBuilder.aboutAppSpecial3
            Iconics.IconicsBuilder().ctx(ctx).on(holder.aboutSpecial3).build()
            holder.aboutSpecial3.visibility = View.VISIBLE
            holder.aboutSpecial3.setOnClickListener { v ->
                val consumed = LibsConfiguration.instance.listener?.onExtraClicked(v, Libs.SpecialButton.SPECIAL3)
                        ?: false

                if (!consumed && !TextUtils.isEmpty(libsBuilder.aboutAppSpecial3Description)) {
                    try {
                        val alert = AlertDialog.Builder(ctx)
                                .setMessage(Html.fromHtml(libsBuilder.aboutAppSpecial3Description))
                                .create()
                        alert.show()
                        val alertText = alert.findViewById<View>(android.R.id.message) as TextView?
                        if (alertText != null) {
                            alertText.movementMethod = LinkMovementMethod.getInstance()
                        }
                    } catch (ex: Exception) {
                    }

                }
            }
            holder.aboutSpecialContainer.visibility = View.VISIBLE
        }


        //set the Version or hide it
        if (libsBuilder.aboutVersionString.isNotEmpty())
            holder.aboutVersion.text = libsBuilder.aboutVersionString
        else {
            if (libsBuilder.aboutShowVersion) {
                holder.aboutVersion.text = ctx.getString(R.string.version) + " " + aboutVersionName + " (" + aboutVersionCode + ")"
            } else {
                if (libsBuilder.aboutShowVersionName) {
                    holder.aboutVersion.text = ctx.getString(R.string.version) + " " + aboutVersionName
                } else if (libsBuilder.aboutShowVersionCode) {
                    holder.aboutVersion.text = "${ctx.getString(R.string.version)} ${aboutVersionCode}"
                } else {
                    holder.aboutVersion.visibility = View.GONE
                }
            }
        }

        //Set the description or hide it
        if (!TextUtils.isEmpty(libsBuilder.aboutDescription)) {
            holder.aboutAppDescription.text = Html.fromHtml(libsBuilder.aboutDescription)
            Iconics.IconicsBuilder().ctx(ctx).on(holder.aboutAppDescription).build()
            holder.aboutAppDescription.movementMethod = MovementCheck.instance
        } else {
            holder.aboutAppDescription.visibility = View.GONE
        }

        //if there is no description or no icon and version number hide the divider
        if ((!libsBuilder.aboutShowIcon) && (!libsBuilder.aboutShowVersion) || TextUtils.isEmpty(libsBuilder.aboutDescription)) {
            holder.aboutDivider.visibility = View.GONE
        }

        //notify the libsRecyclerViewListener to allow modifications
        LibsConfiguration.instance.libsRecyclerViewListener?.onBindViewHolder(holder)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
        internal var aboutIcon: ImageView = headerView.findViewById(R.id.aboutIcon) as ImageView
        internal var aboutAppName: TextView = headerView.findViewById(R.id.aboutName) as TextView
        internal var aboutSpecialContainer: View = headerView.findViewById(R.id.aboutSpecialContainer)
        internal var aboutSpecial1: Button = headerView.findViewById(R.id.aboutSpecial1) as Button
        internal var aboutSpecial2: Button = headerView.findViewById(R.id.aboutSpecial2) as Button
        internal var aboutSpecial3: Button = headerView.findViewById(R.id.aboutSpecial3) as Button
        internal var aboutVersion: TextView = headerView.findViewById(R.id.aboutVersion) as TextView
        internal var aboutDivider: View = headerView.findViewById(R.id.aboutDivider)
        internal var aboutAppDescription: TextView = headerView.findViewById(R.id.aboutDescription) as TextView

        init {
            aboutAppName.setTextColor(headerView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_title_description, R.color.about_libraries_title_description))
            aboutVersion.setTextColor(headerView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_text_description, R.color.about_libraries_text_description))
            aboutDivider.setBackgroundColor(headerView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_divider_description, R.color.about_libraries_divider_description))
            aboutAppDescription.setTextColor(headerView.context.getThemeColorFromAttrOrRes(R.attr.about_libraries_text_description, R.color.about_libraries_text_description))
        }
    }
}
