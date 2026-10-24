package com.example.timedisplay;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.WindowManager;

/**
 * 全局屏幕策略控制器。
 *
 * 通过 Application 级 ActivityLifecycleCallbacks 接管应用内所有界面（含第三方库界面），
 * 不依赖特定 Activity 的继承关系：
 * - 充电（插电 / 满电）时：阻止系统熄屏，屏幕常亮（FLAG_KEEP_SCREEN_ON）。
 * - 未充电时：跟随系统默认熄屏策略，不干预（移除常亮标记，交由系统处理）。
 *
 * 说明：普通应用无系统签名权限，无法调用 PowerManager.goToSleep() 做真正系统休眠；
 * 此处“阻止熄屏”以 FLAG_KEEP_SCREEN_ON 实现，未充电时则交还系统默认策略。
 */
public class ScreenKeepOnController {

    private static ScreenKeepOnController sInstance;

    private Application application;
    private Activity currentActivity;
    private boolean isPlugged = false;
    private boolean initialized = false;

    public static ScreenKeepOnController getInstance() {
        if (sInstance == null) {
            sInstance = new ScreenKeepOnController();
        }
        return sInstance;
    }

    public void init(Application app) {
        if (initialized) return;
        initialized = true;
        this.application = app;

        // 充电状态监听（ACTION_BATTERY_CHANGED 为粘性广播，注册即拿到当前状态）
        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updatePlugged(intent);
            }
        };
        Intent sticky = app.registerReceiver(
                batteryReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ? Context.RECEIVER_NOT_EXPORTED : 0);
        updatePlugged(sticky);

        // 接管所有界面的生命周期
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity;
                applyPolicy();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity == currentActivity) {
                    currentActivity = null;
                }
            }

            @Override
            public void onActivityCreated(Activity activity, android.os.Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, android.os.Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}
        });
    }

    /** 依据电池广播更新充电状态，并在状态变化时切换屏幕策略 */
    private void updatePlugged(Intent intent) {
        if (intent == null) return;
        int status = intent.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1);
        boolean charging = status == android.os.BatteryManager.BATTERY_STATUS_CHARGING
                || status == android.os.BatteryManager.BATTERY_STATUS_FULL;
        if (charging != isPlugged) {
            isPlugged = charging;
            applyPolicy();
        }
    }

    /** 依据充电状态应用屏幕策略（作用于当前前台界面） */
    private void applyPolicy() {
        Activity a = currentActivity;
        if (a == null || a.getWindow() == null) return;
        if (isPlugged) {
            // 充电：阻止系统熄屏，屏幕常亮
            a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            // 未充电：跟随系统默认熄屏策略，移除常亮标记（交还系统处理）
            a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
