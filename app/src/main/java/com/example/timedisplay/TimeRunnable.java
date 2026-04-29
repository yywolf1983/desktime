package com.example.timedisplay;

public class TimeRunnable implements Runnable {
    private MainActivity activity;
    private volatile boolean running = true;
    private boolean isFirstRun = true;

    public TimeRunnable(MainActivity activity) {
        this.activity = activity;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (isFirstRun) {
                    // 首次运行立即更新，然后每秒更新一次
                    isFirstRun = false;
                    activity.sendMessageToUpdateTime();
                    Thread.sleep(1000);
                } else {
                    // 每秒更新一次
                    Thread.sleep(1000);
                    if (running) {
                        activity.sendMessageToUpdateTime();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}