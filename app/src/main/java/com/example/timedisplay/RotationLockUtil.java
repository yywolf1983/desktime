package com.example.timedisplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

/**
 * 全局方向锁定工具：读取 SharedPreferences 中保存的锁定状态并应用到指定 Activity。
 * 与各 Activity 中“onCreate 时应用一次”的逻辑共用同一份状态，作为兜底防止偶发失效。
 */
public final class RotationLockUtil {
    private static final String PREFS = "Settings";

    private RotationLockUtil() {}

    /** 读取全局方向锁定状态并应用到指定 Activity。未锁定时不做任何事（交由系统 fullSensor）。 */
    public static void apply(Activity activity) {
        if (activity == null) return;
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean isRotationLocked = prefs.getBoolean("rotationLocked", false);
        int lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            activity.setRequestedOrientation(lockedOrientation);
        }
    }
}
