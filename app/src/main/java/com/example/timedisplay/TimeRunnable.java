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
                    // 首次运行立即更新
                    isFirstRun = false;
                }
                
                // 先更新，再睡1秒
                activity.sendMessageToUpdateTime();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}