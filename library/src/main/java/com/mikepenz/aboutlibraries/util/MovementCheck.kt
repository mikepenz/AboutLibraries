package com.mikepenz.aboutlibraries.util

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView


class MovementCheck : LinkMovementMethod() {

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        return try {
            super.onTouchEvent(widget, buffer, event)
        } catch (ex: Exception) {
            true
        }

    }

    private object Holder { val INSTANCE = MovementCheck() }

    companion object {
        val instance: MovementCheck by lazy { Holder.INSTANCE }
    }
}