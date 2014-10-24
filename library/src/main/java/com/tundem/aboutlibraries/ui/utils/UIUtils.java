package com.tundem.aboutlibraries.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by mikepenz on 15.03.14.
 */
public class UIUtils {

    private static UIUtils singleton;

    private boolean initDone = false;

    private Context c;
    private int accentColor;
    private int accentSecondaryColor;
    private boolean translucentStatusBar;
    private boolean marginStatusBar;
    private boolean translucentNavigationBar;
    private boolean marginNavigationBar;

    private UIUtils() {
    }

    public static UIUtils getInstance() {
        if (singleton == null) {
            singleton = new UIUtils();
        }
        return singleton;
    }

    public static UIUtils init(Context c, int accentColor, int accentSecondaryColor, boolean translucentStatusBar, boolean marginStatusBar, boolean translucentNavigationBar, boolean marginNavigationBar) {
        getInstance().c = c;
        getInstance().accentColor = accentColor;
        getInstance().accentSecondaryColor = accentSecondaryColor;
        getInstance().translucentStatusBar = translucentStatusBar;
        getInstance().marginStatusBar = marginStatusBar;
        getInstance().translucentNavigationBar = translucentNavigationBar;
        getInstance().marginNavigationBar = marginNavigationBar;

        getInstance().initDone = true;

        return getInstance();
    }

    public void initActivity(Activity act) {
        //set background color
        act.getWindow().getDecorView().setBackgroundColor(accentColor);

        if (translucentStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //set translucent statusBar
            act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

        if (translucentNavigationBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //set translucent navigation
            act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public FrameLayout.LayoutParams handleTranslucentDecorMargins(FrameLayout.LayoutParams layoutParams, Rect insets) {
        layoutParams.setMargins(0, insets.top, 0, insets.bottom);
        return layoutParams;
    }

    public int getActionStatusBarHeight() {
        return getStatusBarHeight() + getActionBarHeight();
    }

    public int getActionBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TypedValue tv = new TypedValue();
            if (c.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                return TypedValue.complexToDimensionPixelSize(tv.data, c.getResources().getDisplayMetrics());
            }
        }
        return 0;
    }

    public int getStatusBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resourceId = c.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return c.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    public int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resourceId = c.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return c.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    /*
     * GETTER UND SETTER!!
     */
    public int getAccentColor() {
        return accentColor;
    }

    public int getAccentSecondaryColor() {
        return accentSecondaryColor;
    }

    public boolean isTranslucentStatusBar() {
        return translucentStatusBar;
    }

    public boolean isTranslucentNavigationBar() {
        return translucentNavigationBar;
    }

    public boolean isMarginStatusBar() {
        return marginStatusBar;
    }

    public boolean isMarginNavigationBar() {
        return marginNavigationBar;
    }
}
