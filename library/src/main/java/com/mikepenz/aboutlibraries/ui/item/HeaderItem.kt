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
import com.mikepenz.aboutlibraries.util.getSupportColor
import com.mikepenz.aboutlibraries.util.getThemeColor
import com.mikepenz.aboutlibraries.util.resolveStyledValue
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * Created by mikepenz on 28.12.15.
 */
class HeaderItem(var libsBuilder: LibsBuilder) : AbstractItem<HeaderItem.ViewHolder>() {
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

    override var isSelectable: Boolean
        get() = false
        set(value) {}

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.header_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.listheader_opensource

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        //ctx
        val ctx = holder.itemView.context

        //Set the Icon or hide it
        if (libsBuilder.aboutShowIcon && aboutIcon != null) {
            holder.aboutIcon.setImageDrawable(aboutIcon)
            holder.aboutIcon.setOnClickListener {
                LibsConfiguration.listener?.onIconClicked(it)
            }

            holder.aboutIcon.setOnLongClickListener { v -> LibsConfiguration.listener != null && LibsConfiguration.listener?.onIconLongClicked(v) ?: false }
        } else {
            holder.aboutIcon.visibility = View.GONE
        }

        //Set the description or hide it
        if (!libsBuilder.aboutAppName.isNullOrEmpty()) {
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
        if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial1) && (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial1Description) || LibsConfiguration.listener != null)) {
            holder.aboutSpecial1.text = libsBuilder.aboutAppSpecial1
            LibsConfiguration.postTextAction?.invoke(holder.aboutSpecial1)
            holder.aboutSpecial1.visibility = View.VISIBLE
            holder.aboutSpecial1.setOnClickListener { v ->
                val consumed = LibsConfiguration.listener?.onExtraClicked(v, Libs.SpecialButton.SPECIAL1)
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
                    } catch (ignored: Exception) {
                        // ignored
                    }
                }
            }
            holder.aboutSpecialContainer.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial2) && (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial2Description) || LibsConfiguration.listener != null)) {
            holder.aboutSpecial2.text = libsBuilder.aboutAppSpecial2
            LibsConfiguration.postTextAction?.invoke(holder.aboutSpecial2)
            holder.aboutSpecial2.visibility = View.VISIBLE
            holder.aboutSpecial2.setOnClickListener { v ->
                val consumed = LibsConfiguration.listener?.onExtraClicked(v, Libs.SpecialButton.SPECIAL2)
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
                    } catch (ignored: Exception) {
                        // ignored
                    }
                }
            }
            holder.aboutSpecialContainer.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial3) && (!TextUtils.isEmpty(libsBuilder.aboutAppSpecial3Description) || LibsConfiguration.listener != null)) {
            holder.aboutSpecial3.text = libsBuilder.aboutAppSpecial3
            LibsConfiguration.postTextAction?.invoke(holder.aboutSpecial3)
            holder.aboutSpecial3.visibility = View.VISIBLE
            holder.aboutSpecial3.setOnClickListener { v ->
                val consumed = LibsConfiguration.listener?.onExtraClicked(v, Libs.SpecialButton.SPECIAL3)
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
                    } catch (ignored: Exception) {
                        // ignored
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
                holder.aboutVersion.text = "${ctx.getString(R.string.version)} $aboutVersionName ($aboutVersionCode)"
            } else {
                when {
                    libsBuilder.aboutShowVersionName -> holder.aboutVersion.text = "${ctx.getString(R.string.version)} $aboutVersionName"
                    libsBuilder.aboutShowVersionCode -> holder.aboutVersion.text = "${ctx.getString(R.string.version)} $aboutVersionCode"
                    else -> holder.aboutVersion.visibility = View.GONE
                }
            }
        }

        //Set the description or hide it
        if (!libsBuilder.aboutDescription.isNullOrEmpty()) {
            holder.aboutAppDescription.text = Html.fromHtml(libsBuilder.aboutDescription)
            LibsConfiguration.postTextAction?.invoke(holder.aboutAppDescription)
            holder.aboutAppDescription.movementMethod = MovementCheck.instance
        } else {
            holder.aboutAppDescription.visibility = View.GONE
        }

        //if there is no description or no icon and version number hide the divider
        if ((!libsBuilder.aboutShowIcon) && (!libsBuilder.aboutShowVersion) || TextUtils.isEmpty(libsBuilder.aboutDescription)) {
            holder.aboutDivider.visibility = View.GONE
        }

        //notify the libsRecyclerViewListener to allow modifications
        LibsConfiguration.libsRecyclerViewListener?.onBindViewHolder(holder)
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
            val ctx = itemView.context
            ctx.resolveStyledValue {
                aboutAppName.setTextColor(it.getColorStateList(R.styleable.AboutLibraries_aboutLibrariesDescriptionTitle))
                aboutVersion.setTextColor(it.getColorStateList(R.styleable.AboutLibraries_aboutLibrariesDescriptionText))
                aboutAppDescription.setTextColor(it.getColorStateList(R.styleable.AboutLibraries_aboutLibrariesDescriptionText))
                aboutDivider.setBackgroundColor(it.getColor(R.styleable.AboutLibraries_aboutLibrariesDescriptionDivider, ctx.getThemeColor(R.attr.aboutLibrariesDescriptionDivider, ctx.getSupportColor(R.color.about_libraries_dividerLight_openSource))))
                aboutSpecial1.setTextColor(it.getColorStateList(R.styleable.AboutLibraries_aboutLibrariesSpecialButtonText))
                aboutSpecial2.setTextColor(it.getColorStateList(R.styleable.AboutLibraries_aboutLibrariesSpecialButtonText))
                aboutSpecial3.setTextColor(it.getColorStateList(R.styleable.AboutLibraries_aboutLibrariesSpecialButtonText))
            }
        }
    }
}