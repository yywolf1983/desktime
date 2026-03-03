package com.example.timedisplay;

public class TimeRunnable implements Runnable {
    private MainActivity activity;

    public TimeRunnable(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 等待1秒
                Thread.sleep(1000);
                // 发送消息给MainActivity，请求更新时间
                activity.sendMessageToUpdateTime();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}