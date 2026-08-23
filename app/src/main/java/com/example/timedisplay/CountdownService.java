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
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;

public class CountdownService extends Service {
    private static final String CHANNEL_ID = "CountdownChannel";
    private static final int NOTIFICATION_ID = 2;

    public static final String ACTION_CD_START = "com.example.timedisplay.CD_START";
    public static final String ACTION_CD_PAUSE = "com.example.timedisplay.CD_PAUSE";
    public static final String ACTION_CD_RESET = "com.example.timedisplay.CD_RESET";
    public static final String ACTION_CD_RESUME = "com.example.timedisplay.CD_RESUME";
    public static final String ACTION_CD_UPDATE = "com.example.timedisplay.CD_UPDATE";

    public static final String EXTRA_DURATION = "duration";
    public static final String EXTRA_REMAINING = "remaining";
    public static final String EXTRA_TOTAL = "total";
    public static final String EXTRA_RUNNING = "running";
    public static final String EXTRA_FINISHED = "finished";

    private static final String CD_SPREFS = "CountdownServicePrefs";
    private static final String KEY_END = "endTime";
    private static final String KEY_TOTAL = "total";
    private static final String KEY_RUNNING = "running";

    private Handler handler;
    private long endTime = 0L;      // elapsedRealtime() 基准下的结束时刻
    private long totalMs = 0L;
    private long remainingMs = 0L;
    private boolean running = false;
    private boolean finished = false;

    private Ringtone ringtone;
    private Vibrator vibrator;
    private AudioManager audioManager;
    private int savedAlarmVolume = -1;

    private BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case ACTION_CD_START:
                    long dur = intent.getLongExtra(EXTRA_DURATION, 0L);
                    startCountdown(dur);
                    break;
                case ACTION_CD_PAUSE:
                    pauseCountdown();
                    break;
                case ACTION_CD_RESET:
                    resetCountdown();
                    break;
                case ACTION_CD_RESUME:
                    resumeCountdown();
                    break;
            }
        }
    };

    private Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            remainingMs = endTime - SystemClock.elapsedRealtime();
            if (remainingMs <= 0) {
                remainingMs = 0;
                running = false;
                finished = true;
                persistState();
                sendUpdate();
                updateNotification();
                onFinish();
                stopForeground(true);
                return;
            }
            sendUpdate();
            updateNotification();
            handler.postDelayed(this, 200);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        createNotificationChannel();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CD_START);
        filter.addAction(ACTION_CD_PAUSE);
        filter.addAction(ACTION_CD_RESET);
        registerReceiver(commandReceiver, filter);

        // 进程被杀后重建时，从持久化恢复状态
        SharedPreferences sp = getSharedPreferences(CD_SPREFS, MODE_PRIVATE);
        boolean wasRunning = sp.getBoolean(KEY_RUNNING, false);
        long end = sp.getLong(KEY_END, 0L);
        totalMs = sp.getLong(KEY_TOTAL, 0L);
        if (wasRunning && end > 0) {
            endTime = end;
            remainingMs = Math.max(0, endTime - SystemClock.elapsedRealtime());
            if (remainingMs <= 0) {
                // 进程在后台期间已到点：立即触发响铃
                running = false;
                finished = true;
                sendUpdate();
                onFinish();
            } else {
                startTicking();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return START_STICKY;
            switch (action) {
                case ACTION_CD_START:
                    startCountdown(intent.getLongExtra(EXTRA_DURATION, 0L));
                    break;
                case ACTION_CD_PAUSE:
                    pauseCountdown();
                    break;
                case ACTION_CD_RESET:
                    resetCountdown();
                    break;
                case ACTION_CD_RESUME:
                    resumeCountdown();
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
        handler.removeCallbacks(tickRunnable);
        stopRingtone();
        unregisterReceiver(commandReceiver);
    }

    private void startCountdown(long durationMs) {
        if (durationMs <= 0) return;
        if (running) {
            // 已运行则忽略（继续）
            return;
        }
        if (finished || remainingMs <= 0) {
            // 全新开始
            totalMs = durationMs;
            remainingMs = durationMs;
        }
        endTime = SystemClock.elapsedRealtime() + remainingMs;
        finished = false;
        running = true;
        persistState();
        startForegroundSafe();
        startTicking();
        sendUpdate();
    }

    private void pauseCountdown() {
        if (!running) return;
        remainingMs = endTime - SystemClock.elapsedRealtime();
        if (remainingMs < 0) remainingMs = 0;
        running = false;
        finished = false;
        persistState();
        handler.removeCallbacks(tickRunnable);
        stopForeground(true);
        sendUpdate();
    }

    private void resetCountdown() {
        running = false;
        finished = false;
        remainingMs = 0L;
        totalMs = 0L;
        endTime = 0L;
        persistState();
        handler.removeCallbacks(tickRunnable);
        stopForeground(true);
        stopRingtone();
        sendUpdate();
    }

    private void resumeCountdown() {
        if (running || finished) return;
        if (remainingMs <= 0) return;
        endTime = SystemClock.elapsedRealtime() + remainingMs;
        running = true;
        persistState();
        startForegroundSafe();
        startTicking();
        sendUpdate();
    }

    private void startTicking() {
        handler.removeCallbacks(tickRunnable);
        handler.postDelayed(tickRunnable, 200);
    }

    private void persistState() {
        SharedPreferences sp = getSharedPreferences(CD_SPREFS, MODE_PRIVATE);
        sp.edit()
                .putLong(KEY_END, endTime)
                .putLong(KEY_TOTAL, totalMs)
                .putBoolean(KEY_RUNNING, running)
                .apply();
    }

    private void sendUpdate() {
        Intent intent = new Intent(ACTION_CD_UPDATE);
        intent.putExtra(EXTRA_REMAINING, remainingMs);
        intent.putExtra(EXTRA_TOTAL, totalMs);
        intent.putExtra(EXTRA_RUNNING, running);
        intent.putExtra(EXTRA_FINISHED, finished);
        sendBroadcast(intent);
    }

    private void onFinish() {
        // 调高闹钟音量后播放，更响亮
        int maxVol = 0;
        if (audioManager != null) {
            try {
                maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                savedAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVol,
                        AudioManager.FLAG_PLAY_SOUND);
            } catch (Exception ignored) {
            }
        }
        try {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            if (ringtone == null) {
                ringtone = RingtoneManager.getRingtone(this, alarmUri);
            }
            if (ringtone != null && !ringtone.isPlaying()) {
                ringtone.play();
            }
        } catch (Exception ignored) {
        }
        if (vibrator != null) {
            try {
                vibrator.vibrate(new long[]{0, 600, 200, 600, 200, 600, 200, 600}, -1);
            } catch (Exception ignored) {
            }
        }
        handler.postDelayed(() -> {
            stopRingtone();
            if (audioManager != null && savedAlarmVolume >= 0) {
                try {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, savedAlarmVolume, 0);
                } catch (Exception ignored) {
                }
            }
        }, 8000);
    }

    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            try {
                ringtone.stop();
            } catch (Exception ignored) {
            }
        }
    }

    private void startForegroundSafe() {
        Notification n = buildNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, n);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "倒计时", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("倒计时通知");
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
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        return builder
                .setContentTitle("倒计时运行中")
                .setContentText(formatTime(remainingMs))
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

    private String formatTime(long ms) {
        int h = (int) (ms / 3600000);
        int m = (int) ((ms % 3600000) / 60000);
        int s = (int) ((ms % 60000) / 1000);
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
