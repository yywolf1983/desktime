package com.example.timedisplay;

import android.app.Application;
import com.reggate.lib.RegGateConfig;

public class TimeDisplayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RegGateConfig.init(this).mainActivity(MainActivity.class).build();
    }
}
