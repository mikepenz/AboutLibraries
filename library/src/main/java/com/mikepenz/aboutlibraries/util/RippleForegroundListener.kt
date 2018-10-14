package com.mikepenz.aboutlibraries.util

import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import android.view.View


/**
 * Created by mikepenz on 16.04.15.
 */
class RippleForegroundListener
/**
 * @param rippleViewId the id of the view which contains the rippleDrawable
 */
(private var rippleViewId: Int = -1) : View.OnTouchListener {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        // Convert to view coordinates. Assumes the host view is
        // a direct child and the view is not scrollable.
        val x = event.x + v.left
        val y = event.y + v.top

        val rippleView = findRippleView(v) ?: return false
        //if we were not able to find the view to display the ripple on, continue.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Simulate motion on the view.
            rippleView.drawableHotspotChanged(x, y)
        }

        // Simulate pressed state on the view.
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> rippleView.isPressed = true
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> rippleView.isPressed = false
        }

        // Pass all events through to the host view.
        return false
    }

    fun findRippleView(view: View): View? {
        return if (view.id == rippleViewId) {
            view
        } else {
            if (view.parent is View) {
                findRippleView(view.parent as View)
            } else {
                null
            }
        }
    }
}