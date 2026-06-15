package com.example.timedisplay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
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

    private long elapsedTime = 0L;
    private boolean isRunning = false;
    private List<Long> lapTimes = new ArrayList<>();

    private int lastHour1 = -1;
    private int lastHour2 = -1;
    private int lastMinute1 = -1;
    private int lastMinute2 = -1;
    private int lastSecond1 = -1;
    private int lastSecond2 = -1;
    private int lastMillis = -1;

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (StopwatchService.ACTION_UPDATE.equals(intent.getAction())) {
                elapsedTime = intent.getLongExtra(StopwatchService.EXTRA_ELAPSED_TIME, 0);
                isRunning = intent.getBooleanExtra(StopwatchService.EXTRA_IS_RUNNING, false);
                
                long[] lapArray = intent.getLongExtra(StopwatchService.EXTRA_LAP_TIMES, null);
                if (lapArray != null) {
                    lapTimes.clear();
                    for (long time : lapArray) {
                        lapTimes.add(time);
                    }
                }
                
                updateDisplay(elapsedTime);
                updateButtonStates();
                refreshLapList();
            }
        }
    };

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
        updateDisplay(0);
        updateButtonStates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(StopwatchService.ACTION_UPDATE);
        registerReceiver(updateReceiver, filter);
        
        Intent intent = new Intent(this, StopwatchService.class);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateReceiver);
        
        if (isRunning) {
            Intent intent = new Intent(StopwatchService.ACTION_START);
            sendBroadcast(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isRunning) {
            stopService(new Intent(this, StopwatchService.class));
        }
    }

    private void initViews() {
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
                sendCommand(StopwatchService.ACTION_STOP);
            } else {
                sendCommand(StopwatchService.ACTION_START);
            }
        });

        resetButton.setOnClickListener(v -> sendCommand(StopwatchService.ACTION_RESET));

        if (lapButton != null) {
            lapButton.setOnClickListener(v -> sendCommand(StopwatchService.ACTION_LAP));
        }
    }

    private void sendCommand(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
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
    }
}