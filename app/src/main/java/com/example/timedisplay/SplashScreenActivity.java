package com.example.timedisplay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import java.util.concurrent.atomic.AtomicBoolean;

public class SplashScreenActivity extends Activity {

    private static final int SPLASH_DELAY = 1000;

    // 进程内只做一次「启动页 → 门禁」跳转。
    // 启动期 Activity 可能因配置变更（透明状态栏/导航栏、旋转等）被系统重建，
    // 重建若重跑 onCreate 会再次展示启动页并重启定时器，在部分机型上表现为
    // “启动页面多次展示 / 黑屏”。用此标志确保跳转仅发生一次，重建的实例直接继续。
    private static final AtomicBoolean sHandedOff = new AtomicBoolean(false);

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isRotationLocked = prefs.getBoolean("rotationLocked", false);
        int lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(lockedOrientation);
        }

        // 已经被（重建前的）实例触发过跳转：直接把门禁拉到前台并结束本次启动页，不再展示。
        if (sHandedOff.get()) {
            Intent intent = new Intent(SplashScreenActivity.this, com.reggate.lib.RegistrationGateActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handOff();
            }
        }, SPLASH_DELAY);
    }

    private void handOff() {
        if (sHandedOff.compareAndSet(false, true)) {
            Intent intent = new Intent(SplashScreenActivity.this, com.reggate.lib.RegistrationGateActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
