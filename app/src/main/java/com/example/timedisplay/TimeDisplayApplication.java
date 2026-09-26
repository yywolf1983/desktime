package com.example.timedisplay;

import android.app.Application;
import android.app.Activity;
import android.os.Bundle;
import com.reggate.lib.RegGateConfig;

public class TimeDisplayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RegGateConfig.init(this).mainActivity(MainActivity.class).build();

        // 每次页面恢复(onResume)都强制应用方向锁定，作为兜底：
        // 进入子页面 / 从后台返回 / 系统重建 Activity 时，即使 onCreate 的锁定因异步落盘
        // 竞态未生效，这里也会重新纠正，避免“锁定后偶尔变竖屏/横屏”的偶发失效。
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                RotationLockUtil.apply(activity);
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {}

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}
        });
    }
}
