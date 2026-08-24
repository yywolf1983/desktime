package com.example.timedisplay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StopwatchActivity extends Activity {
    public static final String EXTRA_OPEN_COUNTDOWN = "open_countdown";

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

                long[] lapArray = intent.getLongArrayExtra(StopwatchService.EXTRA_LAP_TIMES);
                if (lapArray != null) {
                    lapTimes.clear();
                    for (long time : lapArray) {
                        lapTimes.add(time);
                    }
                }

                updateDisplay(elapsedTime);
                updateButtonStates();
                refreshLapList();
            } else if (CountdownService.ACTION_CD_UPDATE.equals(intent.getAction())) {
                cdRemainingMs = intent.getLongExtra(CountdownService.EXTRA_REMAINING, 0L);
                cdTotalMs = intent.getLongExtra(CountdownService.EXTRA_TOTAL, 0L);
                cdRunning = intent.getBooleanExtra(CountdownService.EXTRA_RUNNING, false);
                cdFinished = intent.getBooleanExtra(CountdownService.EXTRA_FINISHED, false);
                if (cdFinished) {
                    cdRunning = false;
                }
                updateCdDisplay();
                updateCdButtons();
            }
        }
    };

    // ===== 倒计时模块 =====
    private LinearLayout countdownPanel;
    private LinearLayout stopwatchPanel;
    private TextView tabStopwatch;
    private TextView tabCountdown;
    private boolean showingCountdown = false;

    private int cdHour = 0;
    private int cdMinute = 5;
    private int cdSecond = 0;
    private TextView cdHourText;
    private TextView cdMinuteText;
    private TextView cdSecondText;
    private TextView cdStartButton;
    private TextView cdResetButton;
    private TextView cdSavePresetButton;
    private LinearLayout cdPresetContainer;

    private long cdRemainingMs = 0L;       // 剩余毫秒
    private long cdTotalMs = 0L;           // 总时长（用于显示进度）
    private boolean cdRunning = false;
    private boolean cdFinished = false;    // 是否已结束（响铃/闪烁中）

    private static final String CD_PREFS = "CountdownPrefs";
    private static final String KEY_PRESETS = "presets";
    private static final String KEY_LAST_H = "lastH";
    private static final String KEY_LAST_M = "lastM";
    private static final String KEY_LAST_S = "lastS";

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
        initCountdown();

        // 根据启动参数决定初始显示倒计时还是秒表
        if (getIntent() != null && getIntent().getBooleanExtra(EXTRA_OPEN_COUNTDOWN, false)) {
            showingCountdown = true;
        }
        if (showingCountdown) {
            showCountdown();
        } else {
            showStopwatch();
        }

        updateDisplay(0);
        updateButtonStates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(StopwatchService.ACTION_UPDATE);
        filter.addAction(CountdownService.ACTION_CD_UPDATE);
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

        stopwatchPanel = findViewById(R.id.stopwatchPanel);
        countdownPanel = findViewById(R.id.countdownPanel);
        tabStopwatch = findViewById(R.id.tabStopwatch);
        tabCountdown = findViewById(R.id.tabCountdown);

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

        tabStopwatch.setOnClickListener(v -> showStopwatch());
        tabCountdown.setOnClickListener(v -> showCountdown());
    }

    private void sendCommand(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void showStopwatch() {
        showingCountdown = false;
        stopwatchPanel.setVisibility(View.VISIBLE);
        countdownPanel.setVisibility(View.GONE);
        tabStopwatch.setTextColor(getResources().getColor(R.color.gold));
        tabCountdown.setTextColor(getResources().getColor(R.color.text_primary));
    }

    private void showCountdown() {
        showingCountdown = true;
        stopwatchPanel.setVisibility(View.GONE);
        countdownPanel.setVisibility(View.VISIBLE);
        tabStopwatch.setTextColor(getResources().getColor(R.color.text_primary));
        tabCountdown.setTextColor(getResources().getColor(R.color.gold));
    }

    // 分段列表缓存：仅内容变化时重建，避免每帧重排导致上下跳动
    private int lastLapCount = -1;
    private List<String> lastLapStrings = new ArrayList<>();

    private void refreshLapList() {
        int totalLaps = lapTimes.size();
        List<String> current = new ArrayList<>();
        // 顺序：第1段在最上，依次向下排列
        for (int i = 0; i < totalLaps; i++) {
            long lapTime = lapTimes.get(i);
            int hours = (int) (lapTime / 3600000);
            int minutes = (int) ((lapTime % 3600000) / 60000);
            int seconds = (int) ((lapTime % 60000) / 1000);
            int millisPart = (int) ((lapTime % 1000) / 10);
            String timeStr;
            if (hours > 0) {
                timeStr = String.format("%d 时 %02d:%02d.%02d", hours, minutes, seconds, millisPart);
            } else if (minutes > 0) {
                timeStr = String.format("%02d:%02d.%02d", minutes, seconds, millisPart);
            } else {
                timeStr = String.format("%d.%02d", seconds, millisPart);
            }
            current.add(String.format("第%d段: %s", i + 1, timeStr));
        }
        // 数量或内容未变则不重建
        if (totalLaps == lastLapCount && current.equals(lastLapStrings)) {
            return;
        }
        lastLapCount = totalLaps;
        lastLapStrings = current;

        lapListContainer.removeAllViews();

        int spanCount = 2;
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
                    lapItem.setText(current.get(lapIndex));
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
        initCountdown();
        updateDisplay(elapsedTime);
        updateButtonStates();
        refreshLapList();
        // 保持当前 Tab 状态（用字段判断，因为 setContentView 后 XML 默认回到秒表）
        if (showingCountdown) {
            showCountdown();
        } else {
            showStopwatch();
        }
        updateCdDisplay();
        updateCdButtons();
        refreshPresetList();
    }

    // ===================== 倒计时实现 =====================

    private void initCountdown() {
        // 读取上次设置
        SharedPreferences cd = getSharedPreferences(CD_PREFS, MODE_PRIVATE);
        int h = cd.getInt(KEY_LAST_H, 0);
        int m = cd.getInt(KEY_LAST_M, 5);
        int s = cd.getInt(KEY_LAST_S, 0);

        // 当前选中值（由点击时间弹出的选择器设置）
        cdHour = h;
        cdMinute = m;
        cdSecond = s;

        // 倒计时显示：一行等宽金色 HH:MM:SS，时/分/秒 各自可单独点击
        LinearLayout timeRow = new LinearLayout(this);
        timeRow.setOrientation(LinearLayout.HORIZONTAL);
        timeRow.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams timeLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeLp.setMargins(0, 14, 0, 0);
        timeRow.setLayoutParams(timeLp);
        timeRow.setPadding(4, 4, 4, 4);

        cdHourText = makeTimeSeg("时");
        cdMinuteText = makeTimeSeg("分");
        cdSecondText = makeTimeSeg("秒");

        TextView c1 = makeColon();
        TextView c2 = makeColon();

        timeRow.addView(cdHourText);
        timeRow.addView(c1);
        timeRow.addView(cdMinuteText);
        timeRow.addView(c2);
        timeRow.addView(cdSecondText);

        // 控制按钮行
        cdStartButton = makeCtrlButton("开始", R.color.gold);
        cdResetButton = makeCtrlButton("重置", R.color.text_primary);
        cdSavePresetButton = makeCtrlButton("存为预设", R.color.text_primary);

        LinearLayout ctrlRow = new LinearLayout(this);
        ctrlRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ctrlRow.setOrientation(LinearLayout.HORIZONTAL);
        ctrlRow.setGravity(android.view.Gravity.CENTER);
        ctrlRow.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams ctrlLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        ctrlLp.setMarginStart(6);
        ctrlLp.setMarginEnd(6);
        cdStartButton.setLayoutParams(ctrlLp);
        cdResetButton.setLayoutParams(ctrlLp);
        cdSavePresetButton.setLayoutParams(ctrlLp);
        ctrlRow.addView(cdStartButton);
        ctrlRow.addView(cdResetButton);
        ctrlRow.addView(cdSavePresetButton);

        // 预设列表标题
        TextView presetTitle = new TextView(this);
        presetTitle.setText("常用预设（一键开启）");
        presetTitle.setTextSize(14);
        presetTitle.setTextColor(getResources().getColor(R.color.text_primary));
        presetTitle.setGravity(android.view.Gravity.CENTER);
        presetTitle.setPadding(8, 16, 8, 8);
        LinearLayout.LayoutParams preLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        presetTitle.setLayoutParams(preLp);

        // 预设列表容器（由根 ScrollView 统一滚动，不再嵌套 ScrollView）
        cdPresetContainer = new LinearLayout(this);
        cdPresetContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        cdPresetContainer.setOrientation(LinearLayout.VERTICAL);
        cdPresetContainer.setPadding(8, 4, 8, 4);

        // 组装布局：横屏左右分栏，竖屏上下堆叠
        boolean landscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (landscape) {
            // 横屏：倒计时面板整体垂直居中
            countdownPanel.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            countdownPanel.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // 左侧：设置 + 显示 + 控制
            LinearLayout leftCol = new LinearLayout(this);
            leftCol.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            leftCol.setOrientation(LinearLayout.VERTICAL);
            leftCol.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            leftCol.setPadding(10, 4, 10, 4);
            leftCol.addView(timeRow);
            leftCol.addView(ctrlRow);

            // 右侧：预设列表
            LinearLayout rightCol = new LinearLayout(this);
            rightCol.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            rightCol.setOrientation(LinearLayout.VERTICAL);
            rightCol.setPadding(10, 4, 10, 4);
            rightCol.addView(presetTitle);
            rightCol.addView(cdPresetContainer);

            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.addView(leftCol);
            row.addView(rightCol);
            countdownPanel.addView(row);
        } else {
            countdownPanel.addView(timeRow);
            countdownPanel.addView(ctrlRow);
            countdownPanel.addView(presetTitle);
            countdownPanel.addView(cdPresetContainer);
        }

        // 按钮事件
        cdStartButton.setOnClickListener(v -> onCdStartClick());
        cdResetButton.setOnClickListener(v -> onCdResetClick());
        cdSavePresetButton.setOnClickListener(v -> onCdSavePreset());

        // 时 / 分 / 秒 各段卡片已绑定单独点击（见 makeTimeSeg）

        // 初始状态
        cdRemainingMs = getPickerMs();
        cdTotalMs = cdRemainingMs;
        updateCdDisplay();
        updateCdButtons();
        refreshPresetList();
    }

    private TextView makeCtrlButton(String text, int colorRes) {
        TextView btn = new TextView(this);
        btn.setText(text);
        btn.setTextSize(14);
        btn.setTextColor(getResources().getColor(colorRes));
        btn.setGravity(android.view.Gravity.CENTER);
        btn.setPadding(8, 14, 8, 14);
        btn.setBackgroundResource(R.drawable.btn_round_transparent);
        btn.setClickable(true);
        return btn;
    }

    private long getPickerMs() {
        return (cdHour * 3600L + cdMinute * 60L + cdSecond) * 1000L;
    }

    private void saveLastSetting() {
        SharedPreferences cd = getSharedPreferences(CD_PREFS, MODE_PRIVATE);
        cd.edit()
                .putInt(KEY_LAST_H, cdHour)
                .putInt(KEY_LAST_M, cdMinute)
                .putInt(KEY_LAST_S, cdSecond)
                .apply();
    }

    /** 构建倒计时大显示里的一段（时/分/秒）：等宽金色数字，各自可单独点击 */
    private TextView makeTimeSeg(String name) {
        TextView num = new TextView(this);
        num.setTextSize(46);
        num.setTypeface(android.graphics.Typeface.MONOSPACE);
        num.setTextColor(getResources().getColor(R.color.time_gold));
        num.setGravity(android.view.Gravity.CENTER);
        num.setPadding(10, 2, 10, 2);
        num.setClickable(true);
        num.setContentDescription("设置" + name);
        num.setOnClickListener(v -> {
            int unit = name.equals("时") ? 0 : (name.equals("分") ? 1 : 2);
            showUnitPicker(unit);
        });
        return num;
    }

    /** 倒计时显示中的冒号分隔符 */
    private TextView makeColon() {
        TextView c = new TextView(this);
        c.setText(":");
        c.setTextSize(40);
        c.setTypeface(android.graphics.Typeface.MONOSPACE);
        c.setTextColor(getResources().getColor(R.color.text_primary));
        c.setGravity(android.view.Gravity.CENTER);
        return c;
    }

    /** 点击某个时间单位（0=时 1=分 2=秒）单独弹出数字选择，无标题、只有确定/取消 */
    private void showUnitPicker(int unit) {
        if (cdRunning) return;
        int gold = getResources().getColor(R.color.gold);
        int dark = getResources().getColor(R.color.bg_dark);
        int textPri = getResources().getColor(R.color.text_primary);
        int cardLight = getResources().getColor(R.color.bg_card_light);
        final int[] maxArr = {23, 59, 59};
        final int[] colsArr = {4, 5, 5};
        int max = maxArr[unit];
        int cols = colsArr[unit];
        final int[] sel = {unit == 0 ? cdHour : (unit == 1 ? cdMinute : cdSecond)};

        android.graphics.drawable.GradientDrawable selBg =
                new android.graphics.drawable.GradientDrawable();
        selBg.setColor(gold);
        selBg.setCornerRadius(16f);
        android.graphics.drawable.GradientDrawable cellBg =
                new android.graphics.drawable.GradientDrawable();
        cellBg.setColor(cardLight);
        cellBg.setCornerRadius(16f);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(dark);

        // 顶部紧凑栏：左侧单位提示 + 右侧小而紧凑的确定/取消
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        topBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        topBar.setPadding(18, 12, 12, 12);

        String unitName = unit == 0 ? "设置小时" : (unit == 1 ? "设置分钟" : "设置秒");
        TextView title = new TextView(this);
        title.setText(unitName);
        title.setTextSize(16);
        title.setTypeface(android.graphics.Typeface.MONOSPACE);
        title.setTextColor(textPri);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        title.setLayoutParams(titleLp);
        topBar.addView(title);

        android.widget.Button neg = new android.widget.Button(this);
        neg.setText("取消");
        neg.setTextSize(14);
        neg.setTypeface(android.graphics.Typeface.MONOSPACE);
        neg.setTextColor(textPri);
        neg.setBackgroundResource(R.drawable.btn_dialog_negative);
        neg.setPadding(22, 8, 22, 8);
        neg.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        topBar.addView(neg);

        android.widget.Button pos = new android.widget.Button(this);
        pos.setText("确定");
        pos.setTextSize(14);
        pos.setTypeface(android.graphics.Typeface.MONOSPACE);
        pos.setTextColor(gold);
        pos.setBackgroundResource(R.drawable.btn_dialog_positive);
        pos.setPadding(22, 8, 22, 8);
        LinearLayout.LayoutParams posLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        posLp.setMarginStart(10);
        pos.setLayoutParams(posLp);
        topBar.addView(pos);

        root.addView(topBar);

        // 网格区：尽量撑满剩余空间
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        sv.setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_INSET);
        sv.setFillViewport(false);

        GridLayout g = new GridLayout(this);
        g.setColumnCount(cols);
        g.setRowCount((int) Math.ceil((max + 1) / (float) cols));
        g.setPadding(10, 10, 10, 10);
        g.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        final TextView[] cells = new TextView[max + 1];
        for (int i = 0; i <= max; i++) {
            final int val = i;
            TextView cell = new TextView(this);
            cell.setText(String.format("%02d", i));
            cell.setTextSize(26);
            cell.setGravity(android.view.Gravity.CENTER);
            GridLayout.LayoutParams gp = new GridLayout.LayoutParams();
            gp.width = 0;
            gp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            gp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            gp.setMargins(8, 8, 8, 8);
            cell.setLayoutParams(gp);
            cell.setPadding(8, 18, 8, 18);
            cell.setBackgroundDrawable(cellBg.getConstantState().newDrawable());
            cell.setTextColor(textPri);
            cell.setClickable(true);
            cell.setOnClickListener(v -> {
                sel[0] = val;
                for (TextView c : cells) {
                    if (c != null) {
                        c.setBackgroundDrawable(cellBg.getConstantState().newDrawable());
                        c.setTextColor(textPri);
                    }
                }
                cell.setBackgroundDrawable(selBg.getConstantState().newDrawable());
                cell.setTextColor(dark);
            });
            cells[i] = cell;
            g.addView(cell);
        }
        // 默认高亮当前值
        TextView cur = cells[sel[0]];
        if (cur != null) {
            cur.setBackgroundDrawable(selBg.getConstantState().newDrawable());
            cur.setTextColor(dark);
        }
        sv.addView(g);
        root.addView(sv);

        final android.app.Dialog dialog = new android.app.Dialog(this, R.style.PickerTextStyle);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.setOnShowListener(d -> {
            android.view.Window w = dialog.getWindow();
            if (w != null) {
                w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                w.getDecorView().setBackgroundColor(dark);
                w.getDecorView().setSystemUiVisibility(
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }
        });

        pos.setOnClickListener(v -> {
            if (cdRunning) {
                dialog.dismiss();
                return;
            }
            if (unit == 0) cdHour = sel[0];
            else if (unit == 1) cdMinute = sel[0];
            else cdSecond = sel[0];
            cdRemainingMs = getPickerMs();
            cdTotalMs = cdRemainingMs;
            saveLastSetting();
            updateCdDisplay();
            dialog.dismiss();
        });
        neg.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void onCdStartClick() {
        if (cdFinished) {
            // 倒计时结束：点击立即停止响铃/闪烁（否则 10 秒后自动停止）
            sendCdCommand(CountdownService.ACTION_CD_STOP_RING, 0L);
        } else if (cdRunning) {
            // 暂停
            sendCdCommand(CountdownService.ACTION_CD_PAUSE, 0L);
        } else if (cdTotalMs > 0 && cdRemainingMs > 0 && cdRemainingMs < cdTotalMs) {
            // 暂停后继续（沿用剩余时长）
            sendCdCommand(CountdownService.ACTION_CD_RESUME, 0L);
        } else {
            // 全新开始
            long dur = getPickerMs();
            if (dur <= 0) return; // 未设置时长
            cdRemainingMs = dur;
            cdTotalMs = dur;
            saveLastSetting();
            sendCdCommand(CountdownService.ACTION_CD_START, dur);
        }
    }

    private void onCdResetClick() {
        sendCdCommand(CountdownService.ACTION_CD_RESET, 0L);
        cdRemainingMs = getPickerMs();
        cdTotalMs = cdRemainingMs;
        updateCdDisplay();
        updateCdButtons();
    }

    private void sendCdCommand(String action, long duration) {
        if (CountdownService.ACTION_CD_STOP_RING.equals(action)) {
            // 停止响铃用广播即可，避免再次触发前台服务启动
            sendBroadcast(new Intent(CountdownService.ACTION_CD_STOP_RING));
            return;
        }
        Intent intent = new Intent(this, CountdownService.class);
        intent.setAction(action);
        if (action.equals(CountdownService.ACTION_CD_START)) {
            intent.putExtra(CountdownService.EXTRA_DURATION, duration);
        }
        // 确保服务已启动（前台）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void onCdSavePreset() {
        long ms = getPickerMs();
        if (ms <= 0) return;
        int h = cdHour;
        int m = cdMinute;
        int s = cdSecond;
        String label = String.format("%02d:%02d:%02d", h, m, s);
        List<Preset> presets = loadPresets();
        // 每种时间只存一种：已存在相同时长则不重复添加
        for (Preset p : presets) {
            if (p.label.equals(label)) {
                return;
            }
        }
        presets.add(new Preset(label, h, m, s));
        savePresets(presets);
        refreshPresetList();
    }

    private void updateCdDisplay() {
        if (cdHourText == null) return;
        long ms = Math.max(0, cdRemainingMs);
        int h = (int) (ms / 3600000);
        int m = (int) ((ms % 3600000) / 60000);
        int s = (int) ((ms % 60000) / 1000);
        cdHourText.setText(String.format("%02d", h));
        cdMinuteText.setText(String.format("%02d", m));
        cdSecondText.setText(String.format("%02d", s));
        if (cdFinished) {
            // 结束：红色并持续闪烁，提醒用户点击停止
            int warn = getResources().getColor(R.color.danger);
            cdHourText.setTextColor(warn);
            cdMinuteText.setTextColor(warn);
            cdSecondText.setTextColor(warn);
            startCdFinishBlink();
        } else {
            int gold = getResources().getColor(R.color.time_gold);
            cdHourText.setTextColor(gold);
            cdMinuteText.setTextColor(gold);
            cdSecondText.setTextColor(gold);
            stopCdFinishBlink();
        }
    }

    /** 结束状态：时/分/秒数字循环闪烁 */
    private void startCdFinishBlink() {
        startBlink(cdHourText);
        startBlink(cdMinuteText);
        startBlink(cdSecondText);
    }

    private void startBlink(TextView tv) {
        if (tv == null) return;
        if (tv.getAnimation() != null
                && tv.getAnimation() instanceof AlphaAnimation
                && tv.getAnimation().getRepeatCount() == Animation.INFINITE) {
            return; // 已在闪烁
        }
        AlphaAnimation blink = new AlphaAnimation(1.0f, 0.15f);
        blink.setDuration(450);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
        tv.startAnimation(blink);
    }

    private void stopCdFinishBlink() {
        clearBlink(cdHourText);
        clearBlink(cdMinuteText);
        clearBlink(cdSecondText);
    }

    private void clearBlink(TextView tv) {
        if (tv == null) return;
        if (tv.getAnimation() != null) {
            tv.clearAnimation();
            tv.setAlpha(1.0f);
        }
    }

    private void updateCdButtons() {
        if (cdStartButton == null) return;
        if (cdFinished) {
            // 倒计时结束：点击停止响铃/闪烁
            cdStartButton.setText("停止响铃");
            cdStartButton.setTextColor(getResources().getColor(R.color.danger));
        } else if (cdRunning) {
            cdStartButton.setText("暂停");
            cdStartButton.setTextColor(getResources().getColor(R.color.danger));
        } else if (cdRemainingMs > 0 && cdRemainingMs < cdTotalMs) {
            cdStartButton.setText("继续");
            cdStartButton.setTextColor(getResources().getColor(R.color.gold));
        } else {
            cdStartButton.setText("开始");
            cdStartButton.setTextColor(getResources().getColor(R.color.gold));
        }
    }

    // ===== 预设持久化 =====
    private static class Preset {
        String label;
        int h, m, s;
        Preset(String label, int h, int m, int s) {
            this.label = label;
            this.h = h;
            this.m = m;
            this.s = s;
        }
    }

    private List<Preset> loadPresets() {
        SharedPreferences cd = getSharedPreferences(CD_PREFS, MODE_PRIVATE);
        String raw = cd.getString(KEY_PRESETS, "");
        List<Preset> list = new ArrayList<>();
        if (raw == null || raw.isEmpty()) return list;
        // 格式: label|h,m,s;label|h,m,s
        for (String part : raw.split(";")) {
            if (part.isEmpty()) continue;
            String[] kv = part.split("\\|");
            if (kv.length < 2) continue;
            String[] hms = kv[1].split(",");
            if (hms.length < 3) continue;
            try {
                list.add(new Preset(kv[0],
                        Integer.parseInt(hms[0]),
                        Integer.parseInt(hms[1]),
                        Integer.parseInt(hms[2])));
            } catch (NumberFormatException ignored) {
            }
        }
        return list;
    }

    private void savePresets(List<Preset> presets) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < presets.size(); i++) {
            Preset p = presets.get(i);
            if (i > 0) sb.append(";");
            sb.append(p.label).append("|").append(p.h).append(",").append(p.m).append(",").append(p.s);
        }
        SharedPreferences cd = getSharedPreferences(CD_PREFS, MODE_PRIVATE);
        cd.edit().putString(KEY_PRESETS, sb.toString()).apply();
    }

    private void refreshPresetList() {
        if (cdPresetContainer == null) return;
        cdPresetContainer.removeAllViews();
        List<Preset> presets = loadPresets();
        if (presets.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("暂无预设，设置时间后点「存为预设」");
            empty.setTextSize(13);
            empty.setTextColor(getResources().getColor(R.color.text_primary));
            empty.setAlpha(0.6f);
            empty.setGravity(android.view.Gravity.CENTER);
            empty.setPadding(8, 12, 8, 12);
            empty.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            cdPresetContainer.addView(empty);
            return;
        }
        for (Preset p : presets) {
            LinearLayout row = new LinearLayout(this);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowLp.setMargins(0, 4, 0, 4);
            row.setLayoutParams(rowLp);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(18, 16, 18, 16);
            row.setBackgroundResource(R.drawable.card_background);
            row.setClickable(true);

            TextView name = new TextView(this);
            name.setText(p.label);
            name.setTextSize(22);
            name.setTypeface(android.graphics.Typeface.MONOSPACE);
            name.setTextColor(getResources().getColor(R.color.gold));
            LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            name.setLayoutParams(nameLp);

            TextView tag = new TextView(this);
            tag.setText("点击开始");
            tag.setTextSize(12);
            tag.setTextColor(getResources().getColor(R.color.gold_faint));
            tag.setPadding(8, 0, 0, 0);

            final Preset fp = p;
            // 点击：直接开始倒计时
            row.setOnClickListener(v -> startPreset(fp));
            // 长按：确认后删除
            row.setOnLongClickListener(v -> {
                confirmDeletePreset(fp);
                return true;
            });

            row.addView(name);
            row.addView(tag);
            cdPresetContainer.addView(row);
        }
    }

    private void startPreset(Preset p) {
        cdHour = p.h;
        cdMinute = p.m;
        cdSecond = p.s;
        saveLastSetting();
        updateCdDisplay();
        // 自动开始：先重置再通知服务以该预设时长启动
        sendCdCommand(CountdownService.ACTION_CD_RESET, 0L);
        sendCdCommand(CountdownService.ACTION_CD_START, getPickerMs());
    }

    private void confirmDeletePreset(Preset p) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("删除预设")
                .setMessage("确定删除预设 " + p.label + " ？")
                .setPositiveButton("删除", (dialog, which) -> {
                    List<Preset> presets = loadPresets();
                    presets.removeIf(x -> x.h == p.h && x.m == p.m && x.s == p.s && x.label.equals(p.label));
                    savePresets(presets);
                    refreshPresetList();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
