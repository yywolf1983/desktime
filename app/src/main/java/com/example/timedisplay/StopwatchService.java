package com.example.timedisplay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

public class StopwatchService extends Service {
    private static final String TAG = "StopwatchService";
    private static final String CHANNEL_ID = "StopwatchChannel";
    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_START = "com.example.timedisplay.STOPWATCH_START";
    public static final String ACTION_STOP = "com.example.timedisplay.STOPWATCH_STOP";
    public static final String ACTION_RESET = "com.example.timedisplay.STOPWATCH_RESET";
    public static final String ACTION_LAP = "com.example.timedisplay.STOPWATCH_LAP";
    public static final String ACTION_UPDATE = "com.example.timedisplay.STOPWATCH_UPDATE";

    public static final String EXTRA_ELAPSED_TIME = "elapsedTime";
    public static final String EXTRA_IS_RUNNING = "isRunning";
    public static final String EXTRA_LAP_TIMES = "lapTimes";

    private Handler handler;
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private boolean isRunning = false;
    private List<Long> lapTimes = new ArrayList<>();

    private BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            switch (action) {
                case ACTION_START:
                    startTimer();
                    break;
                case ACTION_STOP:
                    stopTimer();
                    break;
                case ACTION_RESET:
                    resetTimer();
                    break;
                case ACTION_LAP:
                    addLap();
                    break;
            }
        }
    };

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = SystemClock.elapsedRealtime() - startTime;
                sendUpdate();
                updateNotification();
                handler.postDelayed(this, 10);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        createNotificationChannel();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_START);
        filter.addAction(ACTION_STOP);
        filter.addAction(ACTION_RESET);
        filter.addAction(ACTION_LAP);
        registerReceiver(commandReceiver, filter,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ? Context.RECEIVER_NOT_EXPORTED : 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return START_STICKY;
            switch (action) {
                case ACTION_START:
                    if (!isRunning) {
                        startTimer();
                    }
                    break;
                case ACTION_STOP:
                    stopTimer();
                    break;
                case ACTION_RESET:
                    resetTimer();
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
        unregisterReceiver(commandReceiver);
    }

    private void startTimer() {
        if (isRunning) return;
        
        startTime = SystemClock.elapsedRealtime() - elapsedTime;
        isRunning = true;
        handler.postDelayed(updateRunnable, 10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, buildNotification());
        }
        sendUpdate();
    }

    private void stopTimer() {
        if (!isRunning) return;
        
        elapsedTime = SystemClock.elapsedRealtime() - startTime;
        isRunning = false;
        handler.removeCallbacks(updateRunnable);
        stopForeground(true);
        sendUpdate();
    }

    private void resetTimer() {
        isRunning = false;
        elapsedTime = 0L;
        lapTimes.clear();
        handler.removeCallbacks(updateRunnable);
        stopForeground(true);
        sendUpdate();
    }

    private void addLap() {
        if (!isRunning) return;
        lapTimes.add(elapsedTime);
        sendUpdate();
    }

    private void sendUpdate() {
        Intent intent = new Intent(ACTION_UPDATE);
        intent.putExtra(EXTRA_ELAPSED_TIME, elapsedTime);
        intent.putExtra(EXTRA_IS_RUNNING, isRunning);
        long[] lapArray = new long[lapTimes.size()];
        for (int i = 0; i < lapTimes.size(); i++) {
            lapArray[i] = lapTimes.get(i);
        }
        intent.putExtra(EXTRA_LAP_TIMES, lapArray);
        sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "秒表",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("秒表计时通知");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private Notification buildNotification() {
        Intent intent = new Intent(this, StopwatchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        return builder
                .setContentTitle("秒表运行中")
                .setContentText(formatTime(elapsedTime))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }

    private void updateNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification());
        }
    }

    private String formatTime(long millis) {
        int hours = (int) (millis / 3600000);
        int minutes = (int) ((millis % 3600000) / 60000);
        int seconds = (int) ((millis % 60000) / 1000);
        int millisPart = (int) ((millis % 1000) / 10);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, millisPart);
        } else {
            return String.format("%02d:%02d.%02d", minutes, seconds, millisPart);
        }
    }
}