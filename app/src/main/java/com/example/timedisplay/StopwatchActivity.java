package com.example.timedisplay;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StopwatchActivity extends Activity {
    private SevenSegmentDisplay stopwatchHour1;
    private SevenSegmentDisplay stopwatchHour2;
    private SevenSegmentDisplay stopwatchMinute1;
    private SevenSegmentDisplay stopwatchMinute2;
    private SevenSegmentDisplay stopwatchSecond1;
    private SevenSegmentDisplay stopwatchSecond2;
    private LinearLayout lapListContainer;
    private TextView startStopButton;
    private TextView resetButton;
    private TextView backButton;

    private Handler handler;
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private long lastLapTime = 0L;
    private boolean isRunning = false;
    private int lapCount = 0;
    private List<Long> lapTimes = new ArrayList<>();

    private int lastHour1 = -1;
    private int lastHour2 = -1;
    private int lastMinute1 = -1;
    private int lastMinute2 = -1;
    private int lastSecond1 = -1;
    private int lastSecond2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        initViews();
        initButtonListeners();
        updateDisplay(0);
        updateButtonStates();
    }

    private void initViews() {
        handler = new Handler();

        stopwatchHour1 = findViewById(R.id.stopwatchHour1);
        stopwatchHour2 = findViewById(R.id.stopwatchHour2);
        stopwatchMinute1 = findViewById(R.id.stopwatchMinute1);
        stopwatchMinute2 = findViewById(R.id.stopwatchMinute2);
        stopwatchSecond1 = findViewById(R.id.stopwatchSecond1);
        stopwatchSecond2 = findViewById(R.id.stopwatchSecond2);
        lapListContainer = findViewById(R.id.lapListContainer);
        startStopButton = findViewById(R.id.startStopButton);
        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backButton);

        float brightness = 0.9f;
        stopwatchHour1.setBrightness(brightness);
        stopwatchHour2.setBrightness(brightness);
        stopwatchMinute1.setBrightness(brightness);
        stopwatchMinute2.setBrightness(brightness);
        stopwatchSecond1.setBrightness(brightness);
        stopwatchSecond2.setBrightness(brightness);
    }

    private void initButtonListeners() {
        backButton.setOnClickListener(v -> finish());

        startStopButton.setOnClickListener(v -> {
            if (isRunning) {
                stop();
            } else {
                start();
            }
        });

        resetButton.setOnClickListener(v -> reset());
    }

    private void start() {
        startTime = SystemClock.elapsedRealtime() - elapsedTime;
        isRunning = true;
        handler.postDelayed(updateRunnable, 10);
        updateButtonStates();
    }

    private void stop() {
        elapsedTime = SystemClock.elapsedRealtime() - startTime;
        isRunning = false;
        handler.removeCallbacks(updateRunnable);
        updateButtonStates();
    }

    private void reset() {
        isRunning = false;
        elapsedTime = 0L;
        lastLapTime = 0L;
        lapCount = 0;
        lapTimes.clear();
        lapListContainer.removeAllViews();
        handler.removeCallbacks(updateRunnable);
        updateDisplay(0);
        updateButtonStates();
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long currentElapsed = SystemClock.elapsedRealtime() - startTime;
                updateDisplay(currentElapsed);
                handler.postDelayed(this, 10);
            }
        }
    };

    private void updateDisplay(long millis) {
        int hours = (int) (millis / 3600000);
        int minutes = (int) ((millis % 3600000) / 60000);
        int seconds = (int) ((millis % 60000) / 1000);

        int hour1 = hours / 10;
        int hour2 = hours % 10;
        int minute1 = minutes / 10;
        int minute2 = minutes % 10;
        int second1 = seconds / 10;
        int second2 = seconds % 10;

        if (lastHour1 != hour1) {
            stopwatchHour1.setDigit(hour1);
            lastHour1 = hour1;
        }
        if (lastHour2 != hour2) {
            stopwatchHour2.setDigit(hour2);
            lastHour2 = hour2;
        }
        if (lastMinute1 != minute1) {
            stopwatchMinute1.setDigit(minute1);
            lastMinute1 = minute1;
        }
        if (lastMinute2 != minute2) {
            stopwatchMinute2.setDigit(minute2);
            lastMinute2 = minute2;
        }
        if (lastSecond1 != second1) {
            stopwatchSecond1.setDigit(second1);
            lastSecond1 = second1;
        }
        if (lastSecond2 != second2) {
            stopwatchSecond2.setDigit(second2);
            lastSecond2 = second2;
        }
    }

    private void updateButtonStates() {
        if (isRunning) {
            startStopButton.setText("停止");
            startStopButton.setTextColor(0xFF0A0A14);
            resetButton.setEnabled(false);
            resetButton.setAlpha(0.5f);
        } else {
            startStopButton.setText("开始");
            startStopButton.setTextColor(0xFF0A0A14);
            if (elapsedTime > 0) {
                resetButton.setEnabled(true);
                resetButton.setAlpha(1.0f);
            } else {
                resetButton.setEnabled(false);
                resetButton.setAlpha(0.5f);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}