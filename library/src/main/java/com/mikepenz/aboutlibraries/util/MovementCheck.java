package com.mikepenz.aboutlibraries.util;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

public class MovementCheck extends LinkMovementMethod {

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        try {
            return super.onTouchEvent(widget, buffer, event);
        } catch (Exception ex) {
            return true;
        }
    }

}