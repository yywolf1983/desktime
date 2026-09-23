package com.example.timedisplay;

import java.util.Calendar;

/**
 * 全局共享的“当前生效时间”状态。
 * MainActivity 修改时间（日期时间选择器 / 吉日查询）后写入此处，
 * 其它页面（如二十四节气页）读取，使它们跟随首页的时间修改。
 */
public final class TimeState {
    private static Calendar customCalendar = null;
    private static boolean isCustomTime = false;

    private TimeState() {}

    public static void setCustomCalendar(Calendar cal) {
        customCalendar = cal != null ? (Calendar) cal.clone() : null;
        isCustomTime = customCalendar != null;
    }

    public static void clear() {
        customCalendar = null;
        isCustomTime = false;
    }

    public static boolean isCustomTime() {
        return isCustomTime;
    }

    public static Calendar getActiveCalendar() {
        if (isCustomTime && customCalendar != null) {
            return (Calendar) customCalendar.clone();
        }
        return Calendar.getInstance();
    }
}
