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
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class CountdownService extends Service {
    private static final String CHANNEL_ID = "CountdownChannel";
    private static final int NOTIFICATION_ID = 2;

    public static final String ACTION_CD_START = "com.example.timedisplay.CD_START";
    public static final String ACTION_CD_PAUSE = "com.example.timedisplay.CD_PAUSE";
    public static final String ACTION_CD_RESET = "com.example.timedisplay.CD_RESET";
    public static final String ACTION_CD_RESUME = "com.example.timedisplay.CD_RESUME";
    public static final String ACTION_CD_STOP_RING = "com.example.timedisplay.CD_STOP_RING";
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
    private static final String KEY_FINISHED = "finished";

    // 倒计时结束后的最长响铃/闪烁时长：用户未点击时 10 秒后自动停止
    private static final long MAX_RING_MS = 10_000L;

    private Handler handler;
    private long endTime = 0L;      // elapsedRealtime() 基准下的结束时刻
    private long totalMs = 0L;
    private long remainingMs = 0L;
    private boolean running = false;
    private boolean finished = false;
    private long finishedAtMs = 0L;      // 结束时刻（elapsedRealtime），用于 10 秒后自动停止响铃

    private Ringtone ringtone;
    private Vibrator vibrator;
    private AudioManager audioManager;
    private int savedAlarmVolume = -1;

    // 自定义蜂鸣声（AudioTrack 正弦波），避免系统铃声失真
    private AudioTrack beepTrack;
    private Thread beepThread;
    private volatile boolean beepRunning = false;

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
                case ACTION_CD_STOP_RING:
                    stopRingingCommand();
                    break;
            }
        }
    };

    private Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!running && !finished) return;
            if (finished) {
                // 结束阶段：持续广播，直到 10 秒后自动停止响铃并复位
                if (SystemClock.elapsedRealtime() >= finishedAtMs + 10000) {
                    stopRinging();
                    finished = false;
                    persistState();
                    sendUpdate();
                    stopForeground(true);
                    handler.removeCallbacks(this);
                    return;
                }
                sendUpdate();
                handler.postDelayed(this, 200);
                return;
            }
            remainingMs = endTime - SystemClock.elapsedRealtime();
            if (remainingMs <= 0) {
                remainingMs = 0;
                running = false;
                finished = true;
                finishedAtMs = SystemClock.elapsedRealtime();
                persistState();
                sendUpdate();
                updateNotification();
                onFinish();
                handler.postDelayed(this, 200);
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
        filter.addAction(ACTION_CD_STOP_RING);
        registerReceiver(commandReceiver, filter);

        // 进程被杀后重建时，从持久化恢复状态
        SharedPreferences sp = getSharedPreferences(CD_SPREFS, MODE_PRIVATE);
        boolean wasRunning = sp.getBoolean(KEY_RUNNING, false);
        boolean wasFinished = sp.getBoolean(KEY_FINISHED, false);
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
        } else if (wasFinished) {
            // 进程被杀时正在响铃：恢复前台并继续响铃，10 秒后自动复位
            running = false;
            finished = true;
            remainingMs = 0L;
            endTime = 0L;
            finishedAtMs = SystemClock.elapsedRealtime();
            sendUpdate();
            startForegroundSafe();
            updateNotification();
            onFinish();
            startTicking();
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
                case ACTION_CD_STOP_RING:
                    stopRingingCommand();
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
        stopBeep();
        beepRunning = false;
        if (beepThread != null) {
            try {
                beepThread.interrupt();
            } catch (Exception ignored) {
            }
            beepThread = null;
        }
        if (vibrator != null) {
            try {
                vibrator.cancel();
            } catch (Exception ignored) {
            }
        }
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
            // 若正处于结束响铃中，先停止旧响铃/震动
            if (finished) {
                stopBeep();
                stopRingtone();
                if (vibrator != null) {
                    try {
                        vibrator.cancel();
                    } catch (Exception ignored) {
                    }
                }
                if (audioManager != null && savedAlarmVolume >= 0) {
                    try {
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, savedAlarmVolume, 0);
                    } catch (Exception ignored) {
                    }
                }
            }
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
        finishedAtMs = 0L;
        remainingMs = 0L;
        totalMs = 0L;
        endTime = 0L;
        persistState();
        handler.removeCallbacks(autoStopRingRunnable);
        handler.removeCallbacks(tickRunnable);
        stopForeground(true);
        stopBeep();
        stopRingtone();
        if (vibrator != null) {
            try {
                vibrator.cancel();
            } catch (Exception ignored) {
            }
        }
        if (audioManager != null && savedAlarmVolume >= 0) {
            try {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, savedAlarmVolume, 0);
            } catch (Exception ignored) {
            }
        }
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
                .putBoolean(KEY_FINISHED, finished)
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
        persistState();

        // 主方案：循环播放提示音（AudioTrack 正弦波），直到用户点击停止
        // 若 AudioTrack 异常，内部 catch 会自动回退到系统铃声
        playBeepSequence();

        // 震动：循环震动，直到用户点击停止
        if (vibrator != null) {
            try {
                long[] pattern = new long[]{0, 300, 200, 300, 200, 500};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
                } else {
                    vibrator.vibrate(pattern, 0);
                }
            } catch (Exception ignored) {
            }
        }

        // 不自动停止：保持前台通知，等待用户点击“停止”（stopRinging）
    }

    private void tryPlayRingtone() {
        try {
            if (audioManager != null) {
                int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                savedAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVol,
                        AudioManager.FLAG_PLAY_SOUND);
            }
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
    }

    private void playBeepSequence() {
        if (beepRunning) return;
        beepRunning = true;
        beepThread = new Thread(() -> {
            android.media.AudioTrack track = null;
            try {
                int sampleRate = 44100;
                int bufSize = Math.max(
                        AudioTrack.getMinBufferSize(sampleRate,
                                AudioFormat.CHANNEL_OUT_MONO,
                                AudioFormat.ENCODING_PCM_16BIT),
                        sampleRate / 20) * 4;

                AudioAttributes attrs = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                track = new AudioTrack.Builder()
                        .setAudioAttributes(attrs)
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                        .setBufferSizeInBytes(bufSize)
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .build();

                // 三段音（C大调：C5, E5, G5），每段 ~320ms，间隔 ~180ms，循环播放直到用户停止
                int[] freqs = {523, 659, 784};
                int beepMs = 320;
                int gapMs = 180;

                track.play();
                while (beepRunning) {
                    for (int i = 0; i < freqs.length && beepRunning; i++) {
                        writeSine(track, sampleRate, freqs[i], beepMs);
                        if (i < freqs.length - 1 && beepRunning) {
                            Thread.sleep(gapMs);
                        }
                    }
                    if (beepRunning) {
                        Thread.sleep(400);
                    }
                }
                try {
                    track.stop();
                } catch (Exception ignored) {
                }
                track.release();
            } catch (Exception e) {
                // 回退到系统铃声（持续播放，直到用户停止）
                if (beepRunning) tryPlayRingtone();
            } finally {
                beepRunning = false;
                if (track != null) {
                    try { track.release(); } catch (Exception ignored) {
                    }
                }
            }
        });
        beepThread.setDaemon(true);
        beepThread.start();
    }

    /** 停止响铃命令入口：点击即立即取消响铃 */
    private void stopRingingCommand() {
        if (!finished) return;
        stopRinging();
    }

    /** 用户未点击时，10 秒后自动停止响铃 */
    private final Runnable autoStopRingRunnable = new Runnable() {
        @Override
        public void run() {
            if (finished) {
                stopRinging();
            }
        }
    };

    /** 真正停止响铃/震动/闪烁，回到空闲态 */
    private void stopRinging() {
        stopBeep();
        stopRingtone();
        if (vibrator != null) {
            try {
                vibrator.cancel();
            } catch (Exception ignored) {
            }
        }
        if (audioManager != null && savedAlarmVolume >= 0) {
            try {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, savedAlarmVolume, 0);
            } catch (Exception ignored) {
            }
        }
        // 清除结束状态，回到空闲
        finished = false;
        finishedAtMs = 0L;
        remainingMs = 0L;
        totalMs = 0L;
        endTime = 0L;
        persistState();
        handler.removeCallbacks(autoStopRingRunnable);
        stopForeground(true);
        sendUpdate();
        stopSelf();
    }

    private static void writeSine(AudioTrack track, int sampleRate, int freq, int durationMs) {
        if (track == null) return;
        int count = sampleRate * durationMs / 1000;
        short[] buf = new short[count];
        for (int i = 0; i < count; i++) {
            double t = (double) i / sampleRate;
            // 带淡入淡出，避免爆音
            double env = Math.min(1.0, Math.min(i, count - i) / (sampleRate * 0.02));
            buf[i] = (short) (Math.sin(2 * Math.PI * freq * t) * Short.MAX_VALUE * 0.35 * env);
        }
        track.write(buf, 0, count);
    }

    private void stopBeep() {
        beepRunning = false;
        if (beepTrack != null) {
            try {
                beepTrack.stop();
                beepTrack.release();
            } catch (Exception ignored) {
            }
            beepTrack = null;
        }
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
        String title = finished ? "倒计时结束" : "倒计时运行中";
        String text = finished ? "响铃中，点击停止" : formatTime(remainingMs);
        return builder
                .setContentTitle(title)
                .setContentText(text)
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
