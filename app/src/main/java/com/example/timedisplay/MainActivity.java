package com.example.timedisplay;

import android.app.Activity;
import android.graphics.Color;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.Surface;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.widget.ImageView;

public class MainActivity extends Activity {

    private static final int REQUEST_PERMISSIONS_CODE = 1001;
    private static final int REQUEST_AUSPICIOUS = 1002;

    public SevenSegmentDisplay hour1TextView;
    public SevenSegmentDisplay hour2TextView;
    public SevenSegmentDisplay minute1TextView;
    public SevenSegmentDisplay minute2TextView;
    public TextView dateTextView;
    public TextView weekdayTextView;
    public TextView jieqiTextView;
    // 当前节气纯名称（不含首页显示的「候 / 日」后缀），供打开节气页时传参
    private String currentJieqiName;
    public TextView fourPillarsTextView;
    public TextView timeFortuneTextView;
    public TextView panExplanation;
    public TextView panStarTextView;
    public NinePalacePanel ninePalacePanel;
    private android.widget.TextView ninePalaceChevron;
    private android.view.ViewGroup mainLayout;
    private LinearLayout timeContainer;
    private android.os.Handler handler;
    private Runnable timeRunnable;
    private TextView copyButton;
    private TextView rotationLockButton;

    // 顶部图标行容器：锁 / 电池 / 倒计时 / 功能入口 统一水平排列
    private LinearLayout topBar;
    // 顶部所有图标统一尺寸（px），保证视觉一致
    private int iconSize;

    // 电池电量显示（纯代码动态创建，不依赖任何 XML 资源，避免资源合并冲突）
    private LinearLayout batteryContainer;
    private View batteryIcon;
    private TextView batteryPercentTextView;
    private BroadcastReceiver batteryReceiver;

    // 倒计时入口（电量右侧）
    private LinearLayout countdownEntryContainer;
    private TextView countdownEntryText;
    private ImageView countdownEntryIcon;
    private CountdownReceiver countdownReceiver;
    private boolean cdActive = false;
    private boolean cdRunning = false;
    private boolean cdFinished = false;
    private long cdRemainingMs = 0L;
    private long cdTotalMs = 0L;

    // 自定义时间状态（用于排盘）
    private boolean isCustomTime = false;
    private Calendar customCalendar = null;
    private TextView resetTimeButton;
    private TextView auspiciousButton;

    // 横竖屏锁定状态
    private boolean isRotationLocked = false;
    private int lockedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

    // 上一次的时间值，用于比较哪些部分发生了变化
    private int lastHour1 = -1;
    private int lastHour2 = -1;
    private int lastMinute1 = -1;
    private int lastMinute2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        loadRotationLockState();

        setContentView(R.layout.activity_main);

        // 沉浸式全屏：隐藏系统状态栏/导航栏，让顶部图标行真正贴到屏幕最顶边
        hideSystemBars();

        requestPermissionsIfNeeded();

        hour1TextView = (SevenSegmentDisplay) findViewById(R.id.hour1TextView);
        hour2TextView = (SevenSegmentDisplay) findViewById(R.id.hour2TextView);
        minute1TextView = (SevenSegmentDisplay) findViewById(R.id.minute1TextView);
        minute2TextView = (SevenSegmentDisplay) findViewById(R.id.minute2TextView);
        dateTextView = findViewById(R.id.dateTextView);
        weekdayTextView = findViewById(R.id.weekdayTextView);
        jieqiTextView = findViewById(R.id.jieqiTextView);
        resetTimeButton = findViewById(R.id.resetTimeButton);
        auspiciousButton = findViewById(R.id.auspiciousButton);
        fourPillarsTextView = findViewById(R.id.fourPillarsTextView);
        copyButton = findViewById(R.id.copyButton);
        timeFortuneTextView = findViewById(R.id.timeFortuneTextView);
        panExplanation = findViewById(R.id.panExplanation);
        panStarTextView = findViewById(R.id.panStarTextView);
        ninePalacePanel = (NinePalacePanel) findViewById(R.id.ninePalacePanel);
        ninePalaceChevron = (android.widget.TextView) findViewById(R.id.ninePalaceChevron);
        mainLayout = findViewById(R.id.mainLayout);

        // 电池电量显示（动态创建，不新增 XML 资源）
        initBatteryView();
        initBatteryMonitor();

        // 倒计时入口（电量右侧）
        initCountdownEntry();

        // 功能入口（与电池/倒计时同行的紧凑胶囊按钮）
        initFeatureEntries();

        // 背景/亮度只需初始化时设置一次，避免每秒触发九宫格整屏重绘
        updateBackground();
        timeContainer = findViewById(R.id.timeContainer);

        updateRotationLockButton();

        rotationLockButton.setOnClickListener(v -> toggleRotationLock());

        // 命理解读改由首页顶部入口胶囊按钮进入，此处不再绑定点击

        // 点击日期弹出日期时间选择器
        if (dateTextView != null) {
            dateTextView.setOnClickListener(v -> showDateTimePicker());
        }

        // 点击返回按钮恢复当前时间
        if (resetTimeButton != null) {
            resetTimeButton.setOnClickListener(v -> resetToCurrentTime());
        }

        // 点击"择"按钮进入吉日查询页
        if (auspiciousButton != null) {
            auspiciousButton.setOnClickListener(v -> openAuspiciousDay());
        }

        // 点击时间容器跳转到秒表页面
        timeContainer.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, StopwatchActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 为九宫格添加点击事件监听器，点击时显示详细解读
        View.OnClickListener openFullNinePalace = v -> {
            try {
                Intent intent = new Intent(MainActivity.this, FullNinePalaceActivity.class);
                if (isCustomTime && customCalendar != null) {
                    intent.putExtra("custom_year", customCalendar.get(Calendar.YEAR));
                    intent.putExtra("custom_month", customCalendar.get(Calendar.MONTH) + 1);
                    intent.putExtra("custom_day", customCalendar.get(Calendar.DAY_OF_MONTH));
                    intent.putExtra("custom_hour", customCalendar.get(Calendar.HOUR_OF_DAY));
                    intent.putExtra("custom_minute", customCalendar.get(Calendar.MINUTE));
                }
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        ninePalacePanel.setOnClickListener(openFullNinePalace);
        ninePalaceChevron.setOnClickListener(openFullNinePalace);

        // 罗盘解释点击改由首页顶部入口胶囊按钮进入

        // 五运六气改由首页顶部入口胶囊按钮进入
        // 节气详情改由首页顶部入口胶囊按钮进入

        // 点击复制按钮复制排盘信息
        if (copyButton != null) {
            copyButton.setOnClickListener(v -> copyPaiPanInfo());
        }

        // 初始化Handler
        handler = new android.os.Handler(android.os.Looper.getMainLooper());
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 1000);
            }
        };

        mainLayout.setOnTouchListener(new TouchListener(this));

        updateDateTime();
        handler.postDelayed(timeRunnable, 1000);
    }

    // 唤醒设备的方法
    public void wakeUpDevice() {
        try {
            // 获取PowerManager实例
            android.os.PowerManager powerManager = (android.os.PowerManager) getSystemService(POWER_SERVICE);
            boolean isScreenOn = powerManager.isInteractive();

            if (!isScreenOn) {
                // 使用PARTIAL_WAKE_LOCK唤醒CPU，但不保持屏幕常亮
                android.os.PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                        android.os.PowerManager.PARTIAL_WAKE_LOCK,
                        "TimeDisplay:WakeLock");
                wakeLock.acquire(500); // 保持唤醒0.5秒钟，足够处理触摸事件
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_PERMISSIONS_CODE
                );
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.SCHEDULE_EXACT_ALARM},
                        REQUEST_PERMISSIONS_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // 权限被拒绝，可以显示提示或继续正常运行（功能可能受限）
                }
            }
        }
    }

    // 公开方法，以便TimeHandler类可以访问它
    public void updateDateTime() {
        if (hour1TextView == null || dateTextView == null) return;

        // 大时钟：自定义时间时跟随 displayCalendar，否则显示真实时间
        Date now = new Date();
        Calendar realCalendar = Calendar.getInstance();
        realCalendar.setTime(now);
        Calendar activeCalendar = (isCustomTime && customCalendar != null) ? customCalendar : realCalendar;

        int currentHour = activeCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = activeCalendar.get(Calendar.MINUTE);

        int currentHour1 = currentHour / 10;
        int currentHour2 = currentHour % 10;
        int currentMinute1 = currentMinute / 10;
        int currentMinute2 = currentMinute % 10;

        // 日期显示：如果自定义时间则显示自定义日期，否则显示当前日期
        Calendar displayCalendar = isCustomTime ? customCalendar : realCalendar;
        Date displayDate = displayCalendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEEE", Locale.CHINA);
        String dateString = dateFormat.format(displayDate);
        String weekdayString = weekdayFormat.format(displayDate);
        int dateColor;
        if (isCustomTime) {
            // 自定义时间：仅以高亮橙金色区分，去掉前缀文字，避免突兀
            dateColor = Color.parseColor("#FFC55A");
        } else {
            dateColor = getResources().getColor(R.color.date_blue);
        }

        boolean minuteChanged = (lastMinute1 != currentMinute1) || (lastMinute2 != currentMinute2);

        if (lastHour1 != currentHour1) {
            animateTimeChange(hour1TextView, currentHour1);
            lastHour1 = currentHour1;
        }

        if (lastHour2 != currentHour2) {
            animateTimeChange(hour2TextView, currentHour2);
            lastHour2 = currentHour2;
        }

        if (lastMinute1 != currentMinute1) {
            animateTimeChange(minute1TextView, currentMinute1);
            lastMinute1 = currentMinute1;
        }

        if (lastMinute2 != currentMinute2) {
            animateTimeChange(minute2TextView, currentMinute2);
            lastMinute2 = currentMinute2;
        }

        dateTextView.setText(dateString);
        dateTextView.setTextColor(dateColor);
        if (weekdayTextView != null) {
            weekdayTextView.setText(weekdayString);
            weekdayTextView.setTextColor(dateColor);
        }

        // 更新节气显示：附「第几候」与候内第几天的丁正记号（一 / 丁 / 上 / 止 / 正）
        String jieqi = JieqiData.getCurrentJieqi(displayCalendar);
        currentJieqiName = jieqi;
        if (jieqiTextView != null) {
            int daysIntoJieqi = JieqiData.getDaysIntoJieqi(displayCalendar, jieqi);
            String hou = JieqiData.getHouName(daysIntoJieqi);
            String dayMark = JieqiData.getDayMark(daysIntoJieqi);
            if (hou.isEmpty() || dayMark.isEmpty()) {
                jieqiTextView.setText(jieqi);
            } else {
                jieqiTextView.setText(jieqi + " · " + hou + " · " + dayMark);
            }
        }

        // 四柱排盘：使用自定义时间（如有）或当前时间
        // 仅在分钟变化（含首次）时重算，避免自定义时间下每秒重复计算
        Date fourPillarsDate = isCustomTime ? customCalendar.getTime() : now;
        if (minuteChanged) {
            updateFourPillars(fourPillarsDate);
        }
    }

    // 对单个时间部分应用七段管风格的切换动画
    private void animateTimeChange(final SevenSegmentDisplay display, final int newDigit) {
        if (display == null) return;

        try {
            display.setDigit(newDigit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 显示日期时间选择器
    private void showDateTimePicker() {
        Calendar current = isCustomTime ? customCalendar : Calendar.getInstance();

        new CustomDateTimePickerDialog(this, current, (year, month, day, hour, minute) -> {
            customCalendar = Calendar.getInstance();
            customCalendar.set(year, month - 1, day, hour, minute, 0);
            customCalendar.set(Calendar.MILLISECOND, 0);
            isCustomTime = true;
            if (resetTimeButton != null) {
                resetTimeButton.setVisibility(View.VISIBLE);
            }
            // 立即更新四柱和排盘
            updateFourPillars(customCalendar.getTime());
            updateDateTime();
        }).show();
    }

    // 恢复当前时间
    private void resetToCurrentTime() {
        isCustomTime = false;
        customCalendar = null;
        if (resetTimeButton != null) {
            resetTimeButton.setVisibility(View.GONE);
        }
        // 立即更新四柱和排盘
        updateFourPillars(new Date());
        updateDateTime();
    }

    // 进入吉日查询页（以当前显示日期为基准，向后推 180 天）
    private void openAuspiciousDay() {
        try {
            Intent intent = new Intent(MainActivity.this, AuspiciousDayActivity.class);
            Calendar base = (isCustomTime && customCalendar != null) ? customCalendar : Calendar.getInstance();
            intent.putExtra("base_year", base.get(Calendar.YEAR));
            intent.putExtra("base_month", base.get(Calendar.MONTH) + 1);
            intent.putExtra("base_day", base.get(Calendar.DAY_OF_MONTH));
            startActivityForResult(intent, REQUEST_AUSPICIOUS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== 首页功能入口（与电池/倒计时同一行的紧凑胶囊按钮，动态创建） =====
    private LinearLayout featureEntryBar;

    private void initFeatureEntries() {
        featureEntryBar = new LinearLayout(this);
        featureEntryBar.setOrientation(LinearLayout.HORIZONTAL);
        featureEntryBar.setGravity(android.view.Gravity.CENTER_VERTICAL);

        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(0x26FFFFFF);
        bg.setCornerRadius(dpToPx(16));
        featureEntryBar.setBackgroundDrawable(bg);
        featureEntryBar.setElevation(dpToPx(8));
        featureEntryBar.setPadding(dpToPx(6), 0, dpToPx(6), 0);

        addFeatureButton(R.drawable.ic_jieqi, "节气", v -> openJieqi());
        addFeatureDivider();
        addFeatureButton(R.drawable.ic_wuyun, "五运六气", v -> openWuyun());
        addFeatureDivider();
        addFeatureButton(R.drawable.ic_destiny, "命理", v -> openDestiny());
        addFeatureDivider();
        addFeatureButton(R.drawable.ic_luopan, "罗盘", v -> {
            try {
                startActivity(new Intent(MainActivity.this, LuoPanActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = dpToPx(8);
        featureEntryBar.setLayoutParams(lp);
        topBar.addView(featureEntryBar);
    }

    private void addFeatureButton(int iconRes, String desc, android.view.View.OnClickListener listener) {
        ImageView btn = new ImageView(this);
        btn.setContentDescription(desc);
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btn.setClickable(true);
        btn.setFocusable(true);
        btn.setPadding(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3));
        android.util.TypedValue out = new android.util.TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, out, true);
        if (out.resourceId != 0) btn.setBackgroundResource(out.resourceId);
        try {
            android.graphics.drawable.Drawable d = getResources().getDrawable(iconRes, getTheme());
            if (d != null) {
                d = d.mutate();
                d.setTint(0xFFFFD27F);
                btn.setImageDrawable(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        btn.setOnClickListener(listener);
        featureEntryBar.addView(btn, new LinearLayout.LayoutParams(iconSize, iconSize));
    }

    private void addFeatureDivider() {
        View d = new View(this);
        LinearLayout.LayoutParams dl = new LinearLayout.LayoutParams(dpToPx(6), dpToPx(14));
        dl.gravity = android.view.Gravity.CENTER_VERTICAL;
        d.setLayoutParams(dl);
        d.setBackgroundColor(0x00000000);
        featureEntryBar.addView(d);
    }





    private void openJieqi() {
        try {
            Intent intent = new Intent(MainActivity.this, JieqiActivity.class);
            // 首页显示文本形如「立秋 · 初候 · 一」，改为直接传计算好的纯节气名，
            // 避免解析残留「初候 一」导致节气名匹配失败、页面文字被清空
            String jieqi = (currentJieqiName != null && !currentJieqiName.isEmpty())
                    ? currentJieqiName
                    : JieqiData.getCurrentJieqi(Calendar.getInstance());
            intent.putExtra("jieqi", jieqi);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDestiny() {
        try {
            Intent intent = new Intent(MainActivity.this, DestinyActivity.class);
            String fourPillars = fourPillarsTextView.getText().toString();
            String[] pillars = fourPillars.split("\\s+");
            if (pillars.length >= 4) {
                intent.putExtra("yearPillar", pillars[0]);
                intent.putExtra("monthPillar", pillars[1]);
                intent.putExtra("dayPillar", pillars[2]);
                intent.putExtra("timePillar", pillars[3]);
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openWuyun() {
        try {
            Intent intent = new Intent(MainActivity.this, WuyunLiuqiActivity.class);
            String fourPillars = fourPillarsTextView.getText().toString();
            String[] pillars = fourPillars.split("\\s+");
            if (pillars.length >= 4) {
                intent.putExtra("year_pillar", pillars[0]);
                intent.putExtra("month_pillar", pillars[1]);
                intent.putExtra("day_pillar", pillars[2]);
                intent.putExtra("time_pillar", pillars[3]);
            }
            if (isCustomTime && customCalendar != null) {
                intent.putExtra("custom_year", customCalendar.get(Calendar.YEAR));
                intent.putExtra("custom_month", customCalendar.get(Calendar.MONTH) + 1);
                intent.putExtra("custom_day", customCalendar.get(Calendar.DAY_OF_MONTH));
                intent.putExtra("custom_hour", customCalendar.get(Calendar.HOUR_OF_DAY));
                intent.putExtra("custom_minute", customCalendar.get(Calendar.MINUTE));
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 复制排盘信息到剪贴板 - 标准排盘文本格式
    private void copyPaiPanInfo() {
        StringBuilder sb = new StringBuilder();
        
        // 日期信息
        Calendar displayCalendar = isCustomTime ? customCalendar : Calendar.getInstance();
        Date displayDate = displayCalendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        
        // 四柱信息
        String fourPillars = fourPillarsTextView.getText().toString();
        
        // 节气和局数
        String jieqi = ninePalacePanel.getCopyJieqi();
        int ju = ninePalacePanel.getCopyJu();
        boolean isYangDun = ninePalacePanel.getCopyIsYangDun();
        
        // 旬首、值符、值使
        String xunShou = ninePalacePanel.getCopyXunShou();
        String zhiFu = ninePalacePanel.getCopyZhiFu();
        String zhiShi = ninePalacePanel.getCopyZhiShi();
        
        // 时辰
        String timeFortune = timeFortuneTextView.getText().toString();
        String shichen = "";
        if (timeFortune != null && timeFortune.length() >= 2) {
            shichen = timeFortune.substring(0, 2); // 取"子时"等前两字
        }
        
        // === 组装标准排盘文本 ===
        sb.append("【奇门遁甲排盘】\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        sb.append("时间：").append(dateFormat.format(displayDate));
        sb.append(" ").append(timeFormat.format(displayDate));
        if (!shichen.isEmpty()) sb.append(" ").append(shichen);
        sb.append("\n");
        sb.append("四柱：").append(fourPillars).append("\n");
        sb.append("节气：").append(jieqi);
        sb.append("  ").append(isYangDun ? "阳遁" : "阴遁").append(ju).append("局");
        sb.append("\n");
        sb.append("旬首：").append(xunShou);
        sb.append("  值符：").append(zhiFu).append("星");
        sb.append("  值使：").append(zhiShi).append("门");
        sb.append("\n\n");
        
        // 九宫排盘
        sb.append("【九宫排盘】\n");
        sb.append("─────────────\n");
        sb.append(ninePalacePanel.getCopyText());
        
        String copyText = sb.toString();
        
        // 复制到剪贴板
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("排盘信息", copyText);
        clipboard.setPrimaryClip(clip);
        
        // 显示提示
        android.widget.Toast.makeText(this, "已复制排盘信息", android.widget.Toast.LENGTH_SHORT).show();
    }

    // 更新背景为深蓝色，保持沉稳风格
    private void updateBackground() {
        // 背景与文字颜色统一走资源，避免硬编码覆盖 XML 主题
        mainLayout.setBackgroundColor(getResources().getColor(R.color.bg_dark));
        dateTextView.setTextColor(getResources().getColor(R.color.date_blue));
        fourPillarsTextView.setTextColor(getResources().getColor(R.color.gold_faint));

        // 设置七段数码管的亮度（降低亮度以节省电量）
        hour1TextView.setBrightness(0.7f);
        hour2TextView.setBrightness(0.7f);
        minute1TextView.setBrightness(0.7f);
        minute2TextView.setBrightness(0.7f);

        // 设置九宫格亮度
        ninePalacePanel.setBrightness(0.7f);
    }

    // 更新四柱显示
    private void updateFourPillars(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 转换为1-12
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        // 计算四柱
        String yearPillar = calculateYearPillar(year, month, day);
        String monthPillar = calculateMonthPillar(year, month, day, yearPillar.substring(0, 1));
        
        int calcYear = year;
        int calcMonth = month;
        int calcDay = day;
        if (hour >= 23) {
            calcDay++;
            if (calcDay > getDaysInMonth(calcYear, calcMonth)) {
                calcDay = 1;
                calcMonth++;
                if (calcMonth > 12) {
                    calcMonth = 1;
                    calcYear++;
                }
            }
        }
        String dayPillar = calculateDayPillar(calcYear, calcMonth, calcDay);
        String timePillar = calculateTimePillar(hour, minute, dayPillar.substring(0, 1));
        
        // 格式化四柱显示
        String fourPillars = yearPillar + " " + monthPillar + " " + dayPillar + " " + timePillar;
        fourPillarsTextView.setText(fourPillars);

        // 更新时辰运势：上行：时辰+当令，下行：宜xxx（两行分别显示，避免低分辨率挤在一行）
        String timeZhi = timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";
        String fortune = getTimeFortune(timeZhi);
        if (fortune != null) {
            fortune = fortune.replace(" · ", "\n");
        }
        timeFortuneTextView.setText(fortune);

        // 获取当前节气
        // 统一使用 JieqiData 计算节气，与界面显示的节气保持一致
        String jieqi = JieqiData.getCurrentJieqi(calendar);
        
        // 更新奇门排盘（传入节气参数）
        ninePalacePanel.calculateQiMenPanel(yearPillar, monthPillar, dayPillar, timePillar, jieqi);

        // 更新九宫格解释
        updateNinePalaceExplanation(yearPillar, monthPillar, dayPillar, timePillar);
    }

    // 天干数组
    private static final String[] TIANGAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    // 地支数组
    private static final String[] DIZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    // 60甲子数组
    private static final String[] LIUJIAZI = {
        "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉",
        "甲戌", "乙亥", "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未",
        "甲申", "乙酉", "丙戌", "丁亥", "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳",
        "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥", "庚子", "辛丑", "壬寅", "癸卯",
        "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥", "壬子", "癸丑",
        "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉", "壬戌", "癸亥"
    };
    // 月支表
    private static final String[] MONTH_ZHI_LIST = {"寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑"};
    // 五虎遁诀
    private static final java.util.HashMap<String, String> WUHUDUN = new java.util.HashMap<String, String>();
    // 日上起时法
    private static final java.util.HashMap<String, String> WUSHUDUN_MAP = new java.util.HashMap<String, String>();
    
    // 静态初始化块
    static {
        // 初始化五虎遁诀
        WUHUDUN.put("甲", "丙");
        WUHUDUN.put("己", "丙");
        WUHUDUN.put("乙", "戊");
        WUHUDUN.put("庚", "戊");
        WUHUDUN.put("丙", "庚");
        WUHUDUN.put("辛", "庚");
        WUHUDUN.put("丁", "壬");
        WUHUDUN.put("壬", "壬");
        WUHUDUN.put("戊", "甲");
        WUHUDUN.put("癸", "甲");
        
        // 初始化日上起时法
        WUSHUDUN_MAP.put("甲", "甲");
        WUSHUDUN_MAP.put("己", "甲");
        WUSHUDUN_MAP.put("乙", "丙");
        WUSHUDUN_MAP.put("庚", "丙");
        WUSHUDUN_MAP.put("丙", "戊");
        WUSHUDUN_MAP.put("辛", "戊");
        WUSHUDUN_MAP.put("丁", "庚");
        WUSHUDUN_MAP.put("壬", "庚");
        WUSHUDUN_MAP.put("戊", "壬");
        WUSHUDUN_MAP.put("癸", "壬");
        
    }

    // 计算年柱（以立春为年分界，使用节气数据精确判定）
    private String calculateYearPillar(int year, int month, int day) {
        // 立春前的节气为小寒、大寒，此时八字年份仍属上一年
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day, 12, 0, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        String jieqi = JieqiData.getCurrentJieqi(cal);
        if ("小寒".equals(jieqi) || "大寒".equals(jieqi)) {
            year = year - 1;
        }
        
        // 计算年干支（1900年为庚子年）
        int baseYear = 1900; // 庚子年
        int baseIndex = 36;  // 庚子年在60甲子中的索引
        
        int yearDiff = year - baseYear;
        int yearIndex = (baseIndex + yearDiff) % 60;
        
        int yearGanIndex = yearIndex % 10;
        int yearZhiIndex = yearIndex % 12;
        
        String yearGan = TIANGAN[yearGanIndex];
        String yearZhi = DIZHI[yearZhiIndex];
        
        return yearGan + yearZhi;
    }
    
    // 获取月支（节月：以二十四节气为分界）
    private String getMonthZhi(int year, int month, int day) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day, 12, 0, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        String jieqi = JieqiData.getCurrentJieqi(cal);
        switch (jieqi) {
            case "立春":
            case "雨水":  return "寅";
            case "惊蛰":
            case "春分":  return "卯";
            case "清明":
            case "谷雨":  return "辰";
            case "立夏":
            case "小满":  return "巳";
            case "芒种":
            case "夏至":  return "午";
            case "小暑":
            case "大暑":  return "未";
            case "立秋":
            case "处暑":  return "申";
            case "白露":
            case "秋分":  return "酉";
            case "寒露":
            case "霜降":  return "戌";
            case "立冬":
            case "小雪":  return "亥";
            case "大雪":
            case "冬至":  return "子";
            case "小寒":
            case "大寒":  return "丑";
            default:       return "寅";
        }
    }

    // 计算月柱
    private String calculateMonthPillar(int year, int month, int day, String yearGan) {
        String monthZhi = getMonthZhi(year, month, day);
        
        // 使用五虎遁诀计算月干
        String yinMonthGan = WUHUDUN.get(yearGan);
        if (yinMonthGan == null) {
            yinMonthGan = "丙";
        }
        int yinGanIndex = java.util.Arrays.asList(TIANGAN).indexOf(yinMonthGan);
        
        // 计算月支对应的偏移量
        int monthZhiIndex = java.util.Arrays.asList(MONTH_ZHI_LIST).indexOf(monthZhi);
        int monthGanIndex = (yinGanIndex + monthZhiIndex) % 10;
        String monthGan = TIANGAN[monthGanIndex];
        
        return monthGan + monthZhi;
    }

    // 计算日柱
    private String calculateDayPillar(int year, int month, int day) {
        try {
            // 采用整数儒略日算法求两日期之间的纯日历天数差，避免毫秒差在夏令时
            // 切换日（1986-1991 中国曾实行夏时制）产生 ±1 天的误差，确保日柱准确。
            int daysDiff = julianDay(year, month, day) - julianDay(1900, 1, 1);
            int baseGanzhiIndex = 10; // 1900年1月1日为甲戌日
            int ganzhiIndex = (baseGanzhiIndex + daysDiff) % 60;
            if (ganzhiIndex < 0) ganzhiIndex += 60;
            return LIUJIAZI[ganzhiIndex];
        } catch (Exception e) {
            e.printStackTrace();
            return "甲午"; // 默认值
        }
    }

    // 儒略日数（整数部分，用于求两日期之间的整数天数差）
    private static int julianDay(int y, int m, int d) {
        if (m <= 2) { y -= 1; m += 12; }
        int a = y / 100;
        int b = 2 - a + a / 4;
        return (int) (365.25 * (y + 4716)) + (int) (30.6001 * (m + 1)) + d + b - 1524;
    }

    // 计算时柱（带日柱天干）
    private String calculateTimePillar(int hour, int minute, String dayGan) {
        String hourZhi = "子";
        int hourZhiIndex = 0;
        
        if (hour >= 23 || hour < 1) {
            hourZhi = "子";
            hourZhiIndex = 0;
        } else if (hour >= 1 && hour < 3) {
            hourZhi = "丑";
            hourZhiIndex = 1;
        } else if (hour >= 3 && hour < 5) {
            hourZhi = "寅";
            hourZhiIndex = 2;
        } else if (hour >= 5 && hour < 7) {
            hourZhi = "卯";
            hourZhiIndex = 3;
        } else if (hour >= 7 && hour < 9) {
            hourZhi = "辰";
            hourZhiIndex = 4;
        } else if (hour >= 9 && hour < 11) {
            hourZhi = "巳";
            hourZhiIndex = 5;
        } else if (hour >= 11 && hour < 13) {
            hourZhi = "午";
            hourZhiIndex = 6;
        } else if (hour >= 13 && hour < 15) {
            hourZhi = "未";
            hourZhiIndex = 7;
        } else if (hour >= 15 && hour < 17) {
            hourZhi = "申";
            hourZhiIndex = 8;
        } else if (hour >= 17 && hour < 19) {
            hourZhi = "酉";
            hourZhiIndex = 9;
        } else if (hour >= 19 && hour < 21) {
            hourZhi = "戌";
            hourZhiIndex = 10;
        } else {
            hourZhi = "亥";
            hourZhiIndex = 11;
        }
        
        String startGan = WUSHUDUN_MAP.get(dayGan);
        if (startGan == null) {
            startGan = "甲";
        }
        int startGanIndex = java.util.Arrays.asList(TIANGAN).indexOf(startGan);
        
        int hourGanIndex = (startGanIndex + hourZhiIndex) % 10;
        String hourGan = TIANGAN[hourGanIndex];
        
        return hourGan + hourZhi;
    }

    private String[] getZhiFuPhrases(String star) {
        if (star == null) return new String[]{"星位不明", "需结合全盘分析"};
        switch (star) {
            case "天辅": return new String[]{"文星", "利修文进学"};
            case "天心": return new String[]{"医星", "利决疑疗疾"};
            case "天禽": return new String[]{"贵星", "利交协作事"};
            case "天任": return new String[]{"厚土", "利守成置业"};
            case "天蓬": return new String[]{"暗流", "宜慎防险陷"};
            case "天冲": return new String[]{"奋发", "利革故鼎新"};
            case "天芮": return new String[]{"病符", "宜摄养慎疾"};
            case "天柱": return new String[]{"肃杀", "宜防谗守稳"};
            case "天英": return new String[]{"炎上", "利扬名显声"};
            default: return new String[]{"星位不明", "合盘参看"};
        }
    }
    
    private String[] getZhiShiPhrases(String door) {
        if (door == null) return new String[]{"门位不清", "需结合全盘分析"};
        switch (door) {
            case "开": return new String[]{"开门", "宜启业缔约"};
            case "休": return new String[]{"休门", "宜养静会友"};
            case "生": return new String[]{"生门", "宜经商置业"};
            case "伤": return new String[]{"伤门", "忌行防损"};
            case "杜": return new String[]{"杜门", "宜守静潜藏"};
            case "景": return new String[]{"景门", "宜谋成显扬"};
            case "死": return new String[]{"死门", "百事俱忌"};
            case "惊": return new String[]{"惊门", "忌讼防非"};
            default: return new String[]{"门气不稳", "宜慎行"};
        }
    }
    
    // 更新九宫格解释
    private void updateNinePalaceExplanation(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        if (panExplanation != null && panStarTextView != null && ninePalacePanel != null) {
            String zhiFu = ninePalacePanel.getCopyZhiFu();
            String zhiShi = ninePalacePanel.getCopyZhiShi();

            String luckLabel = ninePalacePanel.getZhiFuPalaceLuck();
            String simpleMeaning = getSimpleMeaning(zhiFu, zhiShi);
            String simpleAdvice = getSimpleAdviceByLabel(luckLabel);

            // 第二行：值符星义（灰色）
            panStarTextView.setText(simpleMeaning);

            // 第三行：门义/建议（橙色），由首页图标按钮进入罗盘解析
            panExplanation.setText(simpleAdvice);
        }
    }
    
    private String[] getGeneralPhrases(String star, String door) {
        boolean isGoodStar = isGoodStar(star);
        boolean isGoodDoor = isGoodDoor(door);
        
        if (isGoodStar && isGoodDoor) {
            return new String[]{"气旺", "诸事亨", "宜进取"};
        } else if (isGoodStar && !isGoodDoor) {
            return new String[]{"星吉门平", "宜待时", "择机动"};
        } else if (!isGoodStar && isGoodDoor) {
            return new String[]{"门吉星平", "宜借力", "稳推行"};
        } else {
            return new String[]{"气弱", "宜静守", "慎行事"};
        }
    }
    
    private String getStarDescription(String star) {
        if (star == null) return "";
        if (star.equals("天辅")) return "文星·利修文进学";
        if (star.equals("天心")) return "医星·利决疑疾";
        if (star.equals("天禽")) return "贵星·利交协作事";
        if (star.equals("天任")) return "厚土·利守成安土";
        if (star.equals("天冲")) return "奋发·利革故鼎新";
        if (star.equals("天英")) return "炎上·利扬名显声";
        if (star.equals("天蓬")) return "暗流·宜慎防险陷";
        if (star.equals("天芮")) return "病符·宜摄养慎疾";
        if (star.equals("天柱")) return "肃杀·宜防谗守稳";
        return "";
    }
    
    private String getDoorDescription(String door) {
        if (door == null) return "";
        if (door.equals("开")) return "开门·诸事亨通";
        if (door.equals("休")) return "休门·宜养静蓄锐";
        if (door.equals("生")) return "生门·财源畅茂";
        if (door.equals("伤")) return "伤门·宜防损耗";
        if (door.equals("杜")) return "杜门·宜静守无言";
        if (door.equals("景")) return "景门·宜谋成显扬";
        if (door.equals("死")) return "死门·百事俱忌";
        if (door.equals("惊")) return "惊门·宜防口舌非";
        return "";
    }
    
    private String getLuckReason(String star, String door) {
        if (star == null) star = "";
        if (door == null) door = "";
        
        boolean isGoodStar = isGoodStar(star);
        boolean isGoodDoor = isGoodDoor(door);
        
        String starDesc = "";
        if (star.equals("天辅")) starDesc = "文星，利修文进学";
        else if (star.equals("天心")) starDesc = "医星，利决疑疾";
        else if (star.equals("天禽")) starDesc = "贵星，利交协作事";
        else if (star.equals("天任")) starDesc = "厚土，利守成安土";
        else if (star.equals("天冲")) starDesc = "奋发，利革故鼎新";
        else if (star.equals("天英")) starDesc = "炎上，利扬名显声";
        else if (star.equals("天蓬")) starDesc = "暗流，宜慎防险陷";
        else if (star.equals("天芮")) starDesc = "病符，宜摄养慎疾";
        else if (star.equals("天柱")) starDesc = "肃杀，宜防谗守稳";
        
        String doorDesc = "";
        if (door.equals("开")) doorDesc = "开门，诸事亨通";
        else if (door.equals("休")) doorDesc = "休门，宜养静蓄锐";
        else if (door.equals("生")) doorDesc = "生门，财源畅茂";
        else if (door.equals("伤")) doorDesc = "伤门，防损耗争端";
        else if (door.equals("杜")) doorDesc = "杜门，宜静守无言";
        else if (door.equals("景")) doorDesc = "景门，宜谋成显扬";
        else if (door.equals("死")) doorDesc = "死门，百事俱忌";
        else if (door.equals("惊")) doorDesc = "惊门，宜防口舌非";
        
        if (isGoodStar && isGoodDoor) {
            return starDesc + "，" + doorDesc;
        } else if (isGoodStar && !isGoodDoor) {
            return starDesc + "，但" + doorDesc;
        } else if (!isGoodStar && isGoodDoor) {
            return doorDesc + "，但" + starDesc;
        } else {
            return starDesc + "，" + doorDesc;
        }
    }
    
    private String getSimpleAdviceByLabel(String label) {
        int score;
        switch (label) {
            case "大吉": score = 7; break;
            case "吉":   score = 5; break;
            case "平吉": score = 3; break;
            case "平":   score = 1; break;
            case "平凶": score = -2; break;
            case "凶":   score = -4; break;
            case "大凶": score = -7; break;
            default:     score = 0;
        }
        return getLuckAdvice(score);
    }

    private String getLuckAdvice(int score) {
        if (score >= 5) {
            return "星门俱吉，可倾力举事";
        } else if (score >= 3) {
            return "吉星照临，宜启新猷";
        } else if (score >= 1) {
            return "星门尚可，宜循序进";
        } else if (score >= -1) {
            return "星门夷平，守常毋妄动";
        } else if (score >= -3) {
            return "星门渐衰，宜缓行观变";
        } else if (score >= -5) {
            return "星门乖违，宜韬光守晦";
        } else {
            return "星门俱凶，宜静守俟时";
        }
    }
    
    private String getSimpleMeaning(String star, String door) {
        String starMean = "";
        if (star != null) {
            if (star.equals("天辅")) starMean = "文星，利修文进学";
            else if (star.equals("天心")) starMean = "医星，利决疑疾";
            else if (star.equals("天禽")) starMean = "贵星，利交协作事";
            else if (star.equals("天任")) starMean = "厚土，利守成安土";
            else if (star.equals("天冲")) starMean = "奋发，利革故鼎新";
            else if (star.equals("天英")) starMean = "炎上，利扬名显声";
            else if (star.equals("天蓬")) starMean = "暗流，宜慎防险陷";
            else if (star.equals("天芮")) starMean = "病符，宜摄养慎疾";
            else if (star.equals("天柱")) starMean = "肃杀，宜防谗守稳";
            else starMean = "星位夷平";
        }
        
        String doorMean = "";
        if (door != null) {
            if (door.equals("开")) doorMean = "开门，诸事亨通";
            else if (door.equals("生")) doorMean = "生门，财源畅茂";
            else if (door.equals("休")) doorMean = "休门，宜养静蓄锐";
            else if (door.equals("景")) doorMean = "景门，宜谋成显扬";
            else if (door.equals("杜")) doorMean = "杜门，宜静守无言";
            else if (door.equals("惊")) doorMean = "惊门，宜防口舌非";
            else if (door.equals("伤")) doorMean = "伤门，防损耗争端";
            else if (door.equals("死")) doorMean = "死门，百事俱忌";
            else doorMean = "门位夷平";
        }
        
        return starMean + "｜" + doorMean;
    }
    
    private String getSimpleAdvice(int score) {
        if (score >= 5) {
            return "天时地利兼得，宜奋迅以进";
        } else if (score >= 3) {
            return "运数方隆，宜推展宏图";
        } else if (score >= 1) {
            return "小有匡助，宜稳步前行";
        } else if (score >= -1) {
            return "运数夷平，宜守不宜攻";
        } else if (score >= -3) {
            return "阻力渐彰，宜慎守观望";
        } else if (score >= -5) {
            return "时运不济，宜以静制动";
        } else {
            return "诸事宜戒，静俟时移";
        }
    }
    
    private boolean isGoodStar(String star) {
        if (star == null) return true;
        return star.equals("天辅") || star.equals("天心") || star.equals("天禽") || 
               star.equals("天任") || star.equals("天英");
    }
    
    private boolean isGoodDoor(String door) {
        if (door == null) return true;
        return door.equals("开") || door.equals("休") || door.equals("生") || door.equals("景");
    }
    
    // 获取门名称
    private String getDoorName(int doorIndex) {
        String[] EIGHT_DOORS = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
        if (doorIndex >= 0 && doorIndex < EIGHT_DOORS.length) {
            return EIGHT_DOORS[doorIndex];
        }
        return "未知";
    }
    
    // 基于九宫格星门组合计算吉凶分数
    private int calculateLuckScore(String[][] palaceData) {
        // 吉星：天辅、天心、天禽、天任
        String[] luckyStars = {"天辅", "天心", "天禽", "天任"};
        // 吉门：开、休、生
        String[] luckyDoors = {"开", "休", "生"};
        
        int score = 0;
        
        // 计算每个宫位的吉凶分数
        for (int i = 0; i < 9; i++) {
            String star = palaceData[i][0];
            String door = palaceData[i][1];
            
            // 吉星加1分
            for (String luckyStar : luckyStars) {
                if (luckyStar.equals(star)) {
                    score++;
                    break;
                }
            }
            
            // 吉门加1分
            for (String luckyDoor : luckyDoors) {
                if (luckyDoor.equals(door)) {
                    score++;
                    break;
                }
            }
        }
        
        return score;
    }

    /**
     * 沉浸式全屏：隐藏系统状态栏与导航栏，让顶部图标行真正贴到屏幕最顶边。
     * Android R(11) 及以上用 WindowInsetsController；旧版本用 SYSTEM_UI_FLAG。
     */
    private void hideSystemBars() {
        // FLAG_FULLSCREEN 是各 ROM 最普遍尊重的隐藏状态栏方式
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // 传统 systemUiVisibility 标志：对所有 API 都有效，与上面叠加更稳
        // LAYOUT_FULLSCREEN 让内容从 y=0 铺到状态栏下方（保证"贴顶"，无论状态栏能否隐藏）
        int legacyFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 关闭 decor 自动适配系统窗，让内容真正铺到 y=0（含状态栏/挖孔区）
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }
        getWindow().getDecorView().setSystemUiVisibility(legacyFlags);

        // 挖孔/刘海屏：允许内容延伸到短边挖孔区。
        // DEFAULT 模式下，全屏窗口会被系统整条下移避让，空出一条与状态栏等高的区域；
        // 该区域由 windowBackground 填充，所以颜色与页面背景一致（看不到黑条），
        // 但内容实际仍被顶下去了 safeInsetTop（约 24dp+）。
        // 设成 SHORT_EDGES 可回收这块空间，让图标行真正到顶。
        // 无挖孔的机器此设置没有任何副作用。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }

    /**
     * 顶部图标行的顶部内边距 = 挖孔/刘海安全区高度 + 基础微距（6dp）。
     * - 有挖孔的机器：安全区高度即避开挖孔所需距离，图标行整体让开，不会被孔压住；
     *   由于窗口已设 SHORT_EDGES，背景仍铺满整屏，不会出现那条与背景同色的空带。
     * - 无挖孔的机器：安全区高度为 0，只剩 6dp 基础微距，与传统全屏机贴顶效果一致。
     */
    private void applyTopBarSafePadding(final View topBarContainer) {
        final int basePadding = dpToPx(6);
        // 挖孔安全区预留比例：1.0 = 完全避开挖孔；小于 1 可让图标行更靠上。
        // 挖孔多在屏幕横向居中、而图标行左对齐，按比例收紧一般不会压到图标。
        final float reserveRatio = 0.6f;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            topBarContainer.setPadding(topBarContainer.getPaddingLeft(), basePadding,
                    topBarContainer.getPaddingRight(), topBarContainer.getPaddingBottom());
            return;
        }
        final View decor = getWindow().getDecorView();
        decor.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int safeTop = 0;
                android.view.DisplayCutout cutout = insets.getDisplayCutout();
                if (cutout != null) {
                    safeTop = Math.round(cutout.getSafeInsetTop() * reserveRatio);
                }
                topBarContainer.setPadding(topBarContainer.getPaddingLeft(),
                        basePadding + safeTop,
                        topBarContainer.getPaddingRight(),
                        topBarContainer.getPaddingBottom());
                // 继续走默认分发，保证其它 fitsSystemWindows 的视图仍正常处理 insets
                return v.onApplyWindowInsets(insets);
            }
        });
        decor.requestApplyInsets();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timeRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemBars();
        updateDateTime();
        handler.removeCallbacks(timeRunnable);
        handler.postDelayed(timeRunnable, 1000);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemBars();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUSPICIOUS && resultCode == RESULT_OK && data != null) {
            int y = data.getIntExtra("sel_year", -1);
            int m = data.getIntExtra("sel_month", -1);
            int d = data.getIntExtra("sel_day", -1);
            if (y > 0 && m > 0 && d > 0) {
                customCalendar = Calendar.getInstance();
                customCalendar.set(y, m - 1, d, 12, 0, 0);
                customCalendar.set(Calendar.MILLISECOND, 0);
                isCustomTime = true;
                if (resetTimeButton != null) {
                    resetTimeButton.setVisibility(View.VISIBLE);
                }
                updateFourPillars(customCalendar.getTime());
                updateDateTime();
                android.widget.Toast.makeText(this,
                        "已切换至 " + y + "年" + m + "月" + d + "日",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 应用销毁时，清理资源
        handler.removeCallbacksAndMessages(null);
        if (batteryReceiver != null) {
            try {
                unregisterReceiver(batteryReceiver);
            } catch (Exception ignored) {
            }
            batteryReceiver = null;
        }
        unregisterCountdownReceiver();
    }

    // 动态创建电池电量视图（右上角、锁按钮左侧），不引用任何 XML/drawable 资源
    private void initBatteryView() {
        // 顶部图标行容器：锁 / 电池 / 倒计时 / 功能入口 统一水平排列、垂直居中，避免错位与相互覆盖
        topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams topLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // 顶部间距统一由 topBarContainer 的 paddingTop 控制，此处不再叠加 margin，
        // 避免多层偏移叠加把图标顶下去
        topLp.topMargin = 0;
        topBar.setLayoutParams(topLp);

        // 功能入口 / 倒计时 图标尺寸；锁屏按钮尺寸独立（见 lockSize），电池图也独立
        iconSize = dpToPx(24);

        batteryContainer = new LinearLayout(this);
        batteryContainer.setOrientation(LinearLayout.HORIZONTAL);
        batteryContainer.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // 圆角半透明胶囊背景（用 GradientDrawable 画，避免依赖资源文件）
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(0x26FFFFFF);              // 白色半透明
        bg.setCornerRadius(dpToPx(16));
        batteryContainer.setBackgroundDrawable(bg);
        batteryContainer.setElevation(dpToPx(8));

        batteryContainer.setPadding(dpToPx(8), dpToPx(3), dpToPx(10), dpToPx(3));

        // 锁屏按钮：独立图标，作为首行第一个元素（与其它图标同尺寸）
        // 锁屏按钮独立尺寸，不跟随 iconSize；注意它已是像素值，不能再套 dpToPx
        final int lockSize = dpToPx(22);
        rotationLockButton = new TextView(this);
        rotationLockButton.setText("🔓");
        rotationLockButton.setTextSize(18);
        rotationLockButton.setTextColor(getResources().getColor(R.color.lock_icon));
        rotationLockButton.setGravity(android.view.Gravity.CENTER);
        rotationLockButton.setBackgroundResource(R.drawable.btn_round_transparent);
        rotationLockButton.setClickable(true);
        rotationLockButton.setFocusable(true);
        rotationLockButton.setContentDescription("锁定/解锁横竖屏");
        // iconSize 已是像素值，此处不能再套 dpToPx（否则会二次换算，
        // 让锁屏按钮背景圈被放大约 density 倍，并把整行高度撑高）
        LinearLayout.LayoutParams lockLp = new LinearLayout.LayoutParams(lockSize, lockSize);
        rotationLockButton.setLayoutParams(lockLp);
        topBar.addView(rotationLockButton);

        // 电池胶囊：与锁之间留 8dp 间距，随电量可见性显示/隐藏
        LinearLayout.LayoutParams batLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        batLp.leftMargin = dpToPx(8);
        batteryContainer.setLayoutParams(batLp);
        batteryContainer.setVisibility(View.GONE);
        topBar.addView(batteryContainer);

        // 图标行挂到布局里的 topBarContainer（竖屏在内容上方、横屏在左半屏顶部），
        // 时间自然排在它下方；横竖屏都不存在 contentLayout 缺失导致图标丢失的问题
        ViewGroup topBarContainer = findViewById(R.id.topBarContainer);
        if (topBarContainer != null) {
            if (topBar.getParent() != null) {
                ((ViewGroup) topBar.getParent()).removeView(topBar);
            }
            topBarContainer.addView(topBar);
            applyTopBarSafePadding(topBarContainer);
        }

        batteryIcon = new BatteryView(this);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(
                dpToPx(24), dpToPx(14));
        iconLp.rightMargin = dpToPx(5);
        batteryIcon.setLayoutParams(iconLp);

        batteryPercentTextView = new TextView(this);
        batteryPercentTextView.setText("--%");
        batteryPercentTextView.setTextSize(11);
        batteryPercentTextView.setTextColor(0xFFFFD27F);   // 金色调，与首页整体风格一致
        batteryPercentTextView.setIncludeFontPadding(false);
        batteryPercentTextView.setTypeface(android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.NORMAL));

        batteryContainer.addView(batteryIcon);
        batteryContainer.addView(batteryPercentTextView);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // 电池电量监控：用粘性广播获取实时电量，并按电量/充电状态美化为图标与配色
    private void initBatteryMonitor() {
        batteryReceiver = new android.content.BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, android.content.Intent intent) {
                updateBattery(intent);
            }
        };
        // 注册后立即用当前粘性广播刷新一次
        android.content.Intent current = registerReceiver(
                batteryReceiver,
                new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED),
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
                        ? android.content.Context.RECEIVER_NOT_EXPORTED : 0);
        updateBattery(current);
    }

    private void updateBattery(android.content.Intent intent) {
        if (intent == null || batteryContainer == null) return;
        int level = intent.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1);
        int status = intent.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1);
        if (level < 0 || scale <= 0) {
            batteryContainer.setVisibility(View.GONE);
            return;
        }
        int percent = (int) Math.round(100.0 * level / scale);
        boolean charging = status == android.os.BatteryManager.BATTERY_STATUS_CHARGING
                || status == android.os.BatteryManager.BATTERY_STATUS_FULL;

        batteryContainer.setVisibility(View.VISIBLE);
        batteryPercentTextView.setText(percent + "%");

        // 颜色与首页整体一致（金色），仅充电时图标显示闪电
        int gold = 0xFFFFD27F;
        batteryPercentTextView.setTextColor(gold);
        if (batteryIcon instanceof BatteryView) {
            ((BatteryView) batteryIcon).setLevel(percent, charging, gold);
        }
    }

    // 纯代码绘制的电池图标（外壳 + 正极头 + 内部电量填充 + 充电闪电），零资源依赖
    private static class BatteryView extends View {
        private int level = 0;
        private boolean charging = false;
        private int color = 0xFF3DDC84;
        private final android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);

        public BatteryView(android.content.Context context) {
            super(context);
        }

        void setLevel(int level, boolean charging, int color) {
            this.level = level;
            this.charging = charging;
            this.color = color;
            invalidate();
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            if (w <= 0 || h <= 0) return;

            float capW = w * 0.12f;             // 正极头宽度
            float bodyW = w - capW;
            float stroke = Math.max(1.5f, h * 0.10f);
            float radius = h * 0.18f;

            // 外壳
            android.graphics.RectF body = new android.graphics.RectF(0, 0, bodyW, h);
            paint.setStyle(android.graphics.Paint.Style.STROKE);
            paint.setColor(0xCCFFFFFF);
            paint.setStrokeWidth(stroke);
            canvas.drawRoundRect(body, radius, radius, paint);

            // 正极头
            float capH = h * 0.45f;
            float capTop = (h - capH) / 2;
            android.graphics.RectF cap = new android.graphics.RectF(bodyW - stroke * 0.5f, capTop, w, capTop + capH);
            paint.setStyle(android.graphics.Paint.Style.FILL);
            paint.setColor(0xCCFFFFFF);
            canvas.drawRoundRect(cap, stroke, stroke, paint);

            // 内部电量填充
            float pad = stroke * 1.6f;
            float fillH = h - pad * 2;
            float fillMaxW = bodyW - pad * 2;
            float fillW = fillMaxW * Math.max(0, Math.min(100, level)) / 100f;
            if (fillW > 0) {
                android.graphics.RectF fill = new android.graphics.RectF(pad, pad, pad + fillW, pad + fillH);
                paint.setColor(color);
                canvas.drawRoundRect(fill, radius * 0.6f, radius * 0.6f, paint);
            }

            // 充电闪电
            if (charging) {
                paint.setColor(0xFFFFFFFF);
                paint.setStyle(android.graphics.Paint.Style.FILL);
                float cx = bodyW / 2;
                float cy = h / 2;
                float s = h * 0.32f;
                android.graphics.Path bolt = new android.graphics.Path();
                bolt.moveTo(cx - s * 0.15f, cy - s);
                bolt.lineTo(cx - s * 0.55f, cy + s * 0.15f);
                bolt.lineTo(cx - s * 0.05f, cy + s * 0.15f);
                bolt.lineTo(cx + s * 0.15f, cy + s);
                bolt.lineTo(cx + s * 0.55f, cy - s * 0.15f);
                bolt.lineTo(cx + s * 0.05f, cy - s * 0.15f);
                bolt.close();
                canvas.drawPath(bolt, paint);
            }
        }
    }

    // ===== 倒计时入口（电量右侧，动态创建，零资源依赖） =====
    private void initCountdownEntry() {
        countdownEntryContainer = new LinearLayout(this);
        countdownEntryContainer.setOrientation(LinearLayout.HORIZONTAL);
        countdownEntryContainer.setGravity(android.view.Gravity.CENTER_VERTICAL);

        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(0x26FFFFFF);
        bg.setCornerRadius(dpToPx(16));
        countdownEntryContainer.setBackgroundDrawable(bg);
        countdownEntryContainer.setElevation(dpToPx(8));
        countdownEntryContainer.setPadding(dpToPx(8), 0, dpToPx(10), 0);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = dpToPx(8);
        countdownEntryContainer.setLayoutParams(lp);

        countdownEntryText = new TextView(this);
        countdownEntryText.setTextSize(11);
        countdownEntryText.setTextColor(0xFFFFD27F);
        countdownEntryText.setIncludeFontPadding(false);
        countdownEntryText.setTypeface(android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.NORMAL));

        // 倒计时图标（小号矢量图标，与功能入口风格统一、尺寸更小）
        countdownEntryIcon = new ImageView(this);
        countdownEntryIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        countdownEntryIcon.setPadding(0, 0, dpToPx(4), 0);
        try {
            android.graphics.drawable.Drawable cd = getResources().getDrawable(R.drawable.ic_countdown, getTheme());
            if (cd != null) {
                cd = cd.mutate();
                cd.setTint(0xFFFFD27F);
                countdownEntryIcon.setImageDrawable(cd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LinearLayout.LayoutParams cdilp = new LinearLayout.LayoutParams(iconSize, iconSize);
        cdilp.gravity = android.view.Gravity.CENTER_VERTICAL;
        countdownEntryIcon.setLayoutParams(cdilp);

        countdownEntryContainer.addView(countdownEntryIcon);
        countdownEntryContainer.addView(countdownEntryText);

        // 始终作为入口显示（而非无倒计时时隐藏）
        countdownEntryContainer.setVisibility(View.VISIBLE);

        // 点击：倒计时结束响铃中点击可停止；其他情况跳转到倒计时页
        countdownEntryContainer.setOnClickListener(v -> {
            try {
                if (cdFinished) {
                    // 结束响铃中：点击立即停止（否则 10 秒后自动停止）
                    sendBroadcast(new Intent(CountdownService.ACTION_CD_STOP_RING));
                    return;
                }
                Intent intent = new Intent(MainActivity.this, StopwatchActivity.class);
                intent.putExtra(StopwatchActivity.EXTRA_OPEN_COUNTDOWN, true);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        countdownEntryContainer.setClickable(true);
        countdownEntryContainer.setFocusable(true);

        topBar.addView(countdownEntryContainer);

        // 注册接收 CountdownService 更新
        countdownReceiver = new CountdownReceiver();
        IntentFilter filter = new IntentFilter(CountdownService.ACTION_CD_UPDATE);
        registerReceiver(countdownReceiver, filter,
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
                        ? android.content.Context.RECEIVER_NOT_EXPORTED : 0);

        // 首次打开时主动请求一次状态
        requestCountdownState();

    }

    private void requestCountdownState() {
        try {
            android.content.SharedPreferences sp = getSharedPreferences("CountdownServicePrefs", MODE_PRIVATE);
            boolean wasRunning = sp.getBoolean("running", false);
            long total = sp.getLong("total", 0L);
            long end = sp.getLong("endTime", 0L);
            long remain = 0L;
            boolean finished = false;
            if (wasRunning && end > 0) {
                remain = Math.max(0L, end - android.os.SystemClock.elapsedRealtime());
                if (remain <= 0) {
                    remain = 0L;
                    finished = true;
                    wasRunning = false;
                }
            } else if (end > 0 && total > 0) {
                remain = total;
            }
            updateCountdownEntryUi(remain, total, wasRunning, finished);
        } catch (Exception ignored) {
        }
    }

    private void updateCountdownEntryUi(long remaining, long total, boolean running, boolean finished) {
        if (countdownEntryContainer == null || countdownEntryText == null) return;

        cdRemainingMs = remaining;
        cdTotalMs = total;
        cdRunning = running;
        cdFinished = finished;
        cdActive = total > 0 || running || finished || remaining > 0;

        // 状态切换时先复位文字动画，避免残留闪烁
        countdownEntryText.clearAnimation();
        countdownEntryText.setAlpha(1.0f);

        int eyeCatching = 0xFF00E5FF;   // 醒目青色，深底高对比
        int gold = 0xFFFFD27F;
        int warn = 0xFFFF6B6B;
        int dim = 0xFFB8B8B8;

        if (finished) {
            // 已结束：仅文字闪烁红色（背景容器不闪），便于点击停止
            countdownEntryContainer.setVisibility(View.VISIBLE);
            countdownEntryText.setText("倒计时结束");
            countdownEntryText.setTextSize(12);
            countdownEntryText.setTextColor(warn);
            AlphaAnimation blink = new AlphaAnimation(1.0f, 0.2f);
            blink.setDuration(500);
            blink.setRepeatMode(Animation.REVERSE);
            blink.setRepeatCount(Animation.INFINITE);
            countdownEntryText.startAnimation(blink);
        } else if (running) {
            long ms = Math.max(0, remaining);
            int h = (int) (ms / 3600000);
            int m = (int) ((ms % 3600000) / 60000);
            int s = (int) ((ms % 60000) / 1000);
            String text;
            if (h > 0) {
                text = String.format("%d:%02d:%02d", h, m, s);
            } else {
                text = String.format("%02d:%02d", m, s);
            }
            countdownEntryContainer.setVisibility(View.VISIBLE);
            countdownEntryText.setText(text);
            countdownEntryText.setTextSize(14);
            countdownEntryText.setTextColor(eyeCatching);
        } else if (total > 0) {
            // 有历史但未运行：显示总时长图标
            long ms = Math.max(0, total);
            int h = (int) (ms / 3600000);
            int m = (int) ((ms % 3600000) / 60000);
            int s = (int) ((ms % 60000) / 1000);
            String text;
            if (h > 0) {
                text = String.format("%d:%02d:%02d", h, m, s);
            } else {
                text = String.format("%02d:%02d", m, s);
            }
            countdownEntryContainer.setVisibility(View.VISIBLE);
            countdownEntryText.setText(text);
            countdownEntryText.setTextSize(12);
            countdownEntryText.setTextColor(dim);
        } else {
            // 完全无倒计时：显示入口图标（点击可设置）
            countdownEntryContainer.setVisibility(View.VISIBLE);
            countdownEntryText.setText("");
            countdownEntryText.setTextSize(12);
            countdownEntryText.setTextColor(dim);
        }
    }

    private class CountdownReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (CountdownService.ACTION_CD_UPDATE.equals(action)) {
                long remaining = intent.getLongExtra(CountdownService.EXTRA_REMAINING, 0L);
                long total = intent.getLongExtra(CountdownService.EXTRA_TOTAL, 0L);
                boolean running = intent.getBooleanExtra(CountdownService.EXTRA_RUNNING, false);
                boolean finished = intent.getBooleanExtra(CountdownService.EXTRA_FINISHED, false);
                updateCountdownEntryUi(remaining, total, running, finished);
            }
        }
    }

    private void unregisterCountdownReceiver() {
        if (countdownReceiver != null) {
            try {
                unregisterReceiver(countdownReceiver);
            } catch (Exception ignored) {
            }
            countdownReceiver = null;
        }
    }



    
    private String getTimeFortune(String timeZhi) {
        if (timeZhi == null) return "时辰平吉";
        switch (timeZhi) {
            case "子": return "子时(二三至一) 胆经司令 · 宜安卧养阳，戒夜不寐";
            case "丑": return "丑时(一至三) 肝经司令 · 宜沉眠排毒，戒饮酒浆";
            case "寅": return "寅时(三至五) 肺经司令 · 宜酣眠润肺，戒妄作劳";
            case "卯": return "卯时(五至七) 大肠司令 · 宜晨起更衣，温汤徐饮";
            case "辰": return "辰时(七至九) 胃经司令 · 宜进朝膳，纳谷最良";
            case "巳": return "巳时(九至十一) 脾经司令 · 宜勤事研习，神气最旺";
            case "午": return "午时(十一至十三) 心经司令 · 宜小憩养心，戒过劳形";
            case "未": return "未时(十三至十五) 小肠司令 · 宜午憩稍安，舒体养元";
            case "申": return "申时(十五至十七) 膀胱司令 · 宜运动饮水，排毒良机";
            case "酉": return "酉时(十七至十九) 肾经司令 · 宜静养藏精，戒过劳损";
            case "戌": return "戌时(十九至廿一) 心包司令 · 宜怡情悦性，戒忧思结";
            case "亥": return "亥时(廿一至廿三) 三焦司令 · 宜濯足安眠，百脉乃通";
            default: return "时辰平吉";
        }
    }

    private void loadRotationLockState() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        isRotationLocked = prefs.getBoolean("rotationLocked", false);
        lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(lockedOrientation);
        }
    }

    private void saveRotationLockState() {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putBoolean("rotationLocked", isRotationLocked);
        editor.putInt("lockedOrientation", lockedOrientation);
        editor.apply();
    }

    private void toggleRotationLock() {
        if (isRotationLocked) {
            isRotationLocked = false;
            lockedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            android.widget.Toast.makeText(this, "已解锁横竖屏", android.widget.Toast.LENGTH_SHORT).show();
        } else {
            isRotationLocked = true;
            int currentRotation = getWindowManager().getDefaultDisplay().getRotation();
            switch (currentRotation) {
                case Surface.ROTATION_0:
                    lockedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    lockedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    lockedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    lockedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    lockedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
            setRequestedOrientation(lockedOrientation);
            android.widget.Toast.makeText(this, "已锁定当前方向", android.widget.Toast.LENGTH_SHORT).show();
        }
        saveRotationLockState();
        updateRotationLockButton();
    }

    private void updateRotationLockButton() {
        if (rotationLockButton != null) {
            rotationLockButton.setText(isRotationLocked ? "🔒" : "🔓");
            rotationLockButton.setTextColor(getResources().getColor(R.color.lock_icon));
        }
    }
    
    private int getDaysInMonth(int year, int month) {
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = daysInMonth[month - 1];
        if (month == 2 && isLeapYear(year)) {
            days = 29;
        }
        return days;
    }
    
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}