package com.example.timedisplay;

import android.os.Handler;
import android.os.Message;

public class TimeHandler extends Handler {
    private MainActivity activity;

    public TimeHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 1) {
            activity.updateDateTime();
        }
    }
}