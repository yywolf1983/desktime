package com.example.timedisplay;

import android.view.MotionEvent;
import android.view.View;

public class TouchListener implements View.OnTouchListener {
    private MainActivity activity;

    public TouchListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 触摸屏幕时唤醒设备
            activity.wakeUpDevice();
            return true;
        }
        return false;
    }
}