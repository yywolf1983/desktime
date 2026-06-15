package com.example.timedisplay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
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
    private TextView stopwatchMillis;
    private LinearLayout lapListContainer;
    private TextView startStopButton;
    private TextView resetButton;
    private TextView lapButton;
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
    private int lastMillis = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isRotationLocked = prefs.getBoolean("rotationLocked", false);
        int lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(lockedOrientation);
        }
        
        setContentView(R.layout.activity_stopwatch);

        initViews();
        initButtonListeners();
        
        if (savedInstanceState != null) {
            elapsedTime = savedInstanceState.getLong("elapsedTime", 0);
            isRunning = savedInstanceState.getBoolean("isRunning", false);
            lastLapTime = savedInstanceState.getLong("lastLapTime", 0);
            lapCount = savedInstanceState.getInt("lapCount", 0);
            long[] lapTimesArray = savedInstanceState.getLongArray("lapTimes");
            if (lapTimesArray != null) {
                lapTimes.clear();
                for (long time : lapTimesArray) {
                    lapTimes.add(time);
                }
            }
            updateDisplay(elapsedTime);
            updateButtonStates();
            refreshLapList();
            if (isRunning) {
                startTime = SystemClock.elapsedRealtime() - elapsedTime;
                handler.postDelayed(updateRunnable, 10);
            }
        } else {
            updateDisplay(0);
            updateButtonStates();
        }
    }

    private void initViews() {
        handler = new Handler();

        stopwatchHour1 = findViewById(R.id.stopwatchHour1);
        stopwatchHour2 = findViewById(R.id.stopwatchHour2);
        stopwatchMinute1 = findViewById(R.id.stopwatchMinute1);
        stopwatchMinute2 = findViewById(R.id.stopwatchMinute2);
        stopwatchSecond1 = findViewById(R.id.stopwatchSecond1);
        stopwatchSecond2 = findViewById(R.id.stopwatchSecond2);
        stopwatchMillis = findViewById(R.id.stopwatchMillis);
        lapListContainer = findViewById(R.id.lapListContainer);
        startStopButton = findViewById(R.id.startStopButton);
        resetButton = findViewById(R.id.resetButton);
        lapButton = findViewById(R.id.lapButton);
        backButton = findViewById(R.id.backButton);

        float brightness = 0.9f;
        stopwatchHour1.setBrightness(brightness);
        stopwatchHour2.setBrightness(brightness);
        stopwatchMinute1.setBrightness(brightness);
        stopwatchMinute2.setBrightness(brightness);
        stopwatchSecond1.setBrightness(brightness);
        stopwatchSecond2.setBrightness(brightness);

        lastHour1 = -1;
        lastHour2 = -1;
        lastMinute1 = -1;
        lastMinute2 = -1;
        lastSecond1 = -1;
        lastSecond2 = -1;
        lastMillis = -1;
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

        if (lapButton != null) {
            lapButton.setOnClickListener(v -> lap());
        }
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
        lapCount = 0;
        lapTimes.clear();
        lapListContainer.removeAllViews();
        handler.removeCallbacks(updateRunnable);
        updateDisplay(0);
        updateButtonStates();
    }

    private void lap() {
        if (!isRunning) return;
        
        long currentElapsed = SystemClock.elapsedRealtime() - startTime;
        lapCount++;
        lapTimes.add(currentElapsed);
        
        addLapItem(lapCount, currentElapsed);
    }

    private void addLapItem(int lapNum, long lapTime) {
        refreshLapList();
    }

    private void refreshLapList() {
        lapListContainer.removeAllViews();
        
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2;
        int totalLaps = lapTimes.size();
        int rows = (int) Math.ceil((double) totalLaps / spanCount);
        
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setOrientation(LinearLayout.HORIZONTAL);
            
            for (int colIndex = 0; colIndex < spanCount; colIndex++) {
                int lapIndex = rowIndex * spanCount + colIndex;
                if (lapIndex < totalLaps) {
                    TextView lapItem = new TextView(this);
                    lapItem.setTextSize(14);
                    lapItem.setTextColor(getResources().getColor(R.color.text_primary));
                    lapItem.setPadding(4, 4, 4, 4);
                    lapItem.setGravity(android.view.Gravity.CENTER);
                    
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    lapItem.setLayoutParams(params);
                    
                    long lapTime = lapTimes.get(lapIndex);
                    int lapNum = lapIndex + 1;
                    
                    int hours = (int) (lapTime / 3600000);
                    int minutes = (int) ((lapTime % 3600000) / 60000);
                    int seconds = (int) ((lapTime % 60000) / 1000);
                    int millisPart = (int) ((lapTime % 1000) / 10);
                    
                    String timeStr;
                    if (hours > 0) {
                        timeStr = String.format("%d 分 %02d:%02d.%02d", hours, minutes, seconds, millisPart);
                    } else if (minutes > 0) {
                        timeStr = String.format("%02d:%02d.%02d", minutes, seconds, millisPart);
                    } else {
                        timeStr = String.format("%d.%02d", seconds, millisPart);
                    }
                    
                    lapItem.setText(String.format("第%d段: %s", lapNum, timeStr));
                    row.addView(lapItem);
                } else {
                    View emptyView = new View(this);
                    LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    emptyView.setLayoutParams(emptyParams);
                    row.addView(emptyView);
                }
            }
            
            lapListContainer.addView(row);
        }
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = SystemClock.elapsedRealtime() - startTime;
                updateDisplay(elapsedTime);
                handler.postDelayed(this, 10);
            }
        }
    };

    private void updateDisplay(long millis) {
        int hours = (int) (millis / 3600000);
        int minutes = (int) ((millis % 3600000) / 60000);
        int seconds = (int) ((millis % 60000) / 1000);
        int millisPart = (int) ((millis % 1000) / 10);

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
        if (lastMillis != millisPart) {
            if (stopwatchMillis != null) {
                stopwatchMillis.setText(String.format(".%02d", millisPart));
            }
            lastMillis = millisPart;
        }
    }

    private void updateButtonStates() {
        if (isRunning) {
            startStopButton.setText("停止");
            startStopButton.setTextColor(getResources().getColor(R.color.danger));
            resetButton.setEnabled(false);
            resetButton.setAlpha(0.5f);
            if (lapButton != null) {
                lapButton.setEnabled(true);
                lapButton.setAlpha(1.0f);
            }
        } else {
            startStopButton.setText("开始");
            startStopButton.setTextColor(getResources().getColor(R.color.gold));
            if (elapsedTime > 0) {
                resetButton.setEnabled(true);
                resetButton.setAlpha(1.0f);
            } else {
                resetButton.setEnabled(false);
                resetButton.setAlpha(0.5f);
            }
            if (lapButton != null) {
                lapButton.setEnabled(false);
                lapButton.setAlpha(0.5f);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("elapsedTime", elapsedTime);
        outState.putBoolean("isRunning", isRunning);
        outState.putLong("lastLapTime", lastLapTime);
        outState.putInt("lapCount", lapCount);
        outState.putLongArray("lapTimes", lapTimes.stream().mapToLong(Long::longValue).toArray());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        int layoutId = getResources().getIdentifier(
            "activity_stopwatch", "layout", getPackageName());
        setContentView(layoutId);
        
        initViews();
        initButtonListeners();
        updateDisplay(elapsedTime);
        updateButtonStates();
        refreshLapList();
        
        if (isRunning) {
            startTime = SystemClock.elapsedRealtime() - elapsedTime;
            handler.postDelayed(updateRunnable, 10);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}