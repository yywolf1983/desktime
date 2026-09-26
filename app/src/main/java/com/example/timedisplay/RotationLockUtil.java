package com.example.timedisplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

/**
 * 全局方向控制：屏幕始终固定为已保存的方向（横屏或竖屏），不自动跟随系统传感器。
 * 由首页“旋转”按钮手动在横屏/竖屏之间切换并固定（旋转后锁死，除非再次点击）。
 */
public final class RotationLockUtil {
    private static final String PREFS = "Settings";
    private static final String KEY = "lockedOrientation";
    private static final int DEFAULT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    private RotationLockUtil() {}

    /** 当前固定方向：横屏或竖屏，默认竖屏。 */
    public static int getOrientation(Context c) {
        SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return p.getInt(KEY, DEFAULT);
    }

    /** 始终按保存的方向固定屏幕，不跟随系统传感器。 */
    public static void apply(Activity activity) {
        if (activity == null) return;
        activity.setRequestedOrientation(getOrientation(activity));
    }

    /** 横竖屏互相切换并固定保存，返回切换后的方向。 */
    public static int toggle(Activity activity) {
        int next = getOrientation(activity) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        save(activity, next);
        activity.setRequestedOrientation(next);
        return next;
    }

    public static void save(Context c, int orientation) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY, orientation).commit();
    }
}
