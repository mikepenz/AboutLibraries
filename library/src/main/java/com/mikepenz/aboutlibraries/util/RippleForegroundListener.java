package com.mikepenz.aboutlibraries.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mikepenz on 16.04.15.
 */
public class RippleForegroundListener<T extends View> implements View.OnTouchListener {
    T view;

    public RippleForegroundListener setLayout(T view) {
        this.view = view;
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Convert to view coordinates. Assumes the host view is
        // a direct child and the view is not scrollable.
        float x = event.getX() + v.getLeft();
        float y = event.getY() + v.getTop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Simulate motion on the view.
            view.drawableHotspotChanged(x, y);
        }

        // Simulate pressed state on the view.
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                view.setPressed(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                view.setPressed(false);
                break;
        }

        // Pass all events through to the host view.
        return false;
    }
}
