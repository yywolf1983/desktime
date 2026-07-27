package com.example.timedisplay;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Surface;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int REQUEST_PERMISSIONS_CODE = 1001;

    public SevenSegmentDisplay hour1TextView;
    public SevenSegmentDisplay hour2TextView;
    public SevenSegmentDisplay minute1TextView;
    public SevenSegmentDisplay minute2TextView;
    public TextView dateTextView;
    public TextView weekdayTextView;
    public TextView jieqiTextView;
    public TextView fourPillarsTextView;
    public TextView timeFortuneTextView;
    public TextView panExplanation;
    public NinePalacePanel ninePalacePanel;
    private android.view.ViewGroup mainLayout;
    private LinearLayout timeContainer;
    private android.os.Handler handler;
    private Runnable timeRunnable;
    private TextView copyButton;
    private TextView rotationLockButton;

    // 自定义时间状态（用于排盘）
    private boolean isCustomTime = false;
    private Calendar customCalendar = null;
    private TextView resetTimeButton;

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

        requestPermissionsIfNeeded();

        hour1TextView = (SevenSegmentDisplay) findViewById(R.id.hour1TextView);
        hour2TextView = (SevenSegmentDisplay) findViewById(R.id.hour2TextView);
        minute1TextView = (SevenSegmentDisplay) findViewById(R.id.minute1TextView);
        minute2TextView = (SevenSegmentDisplay) findViewById(R.id.minute2TextView);
        dateTextView = findViewById(R.id.dateTextView);
        weekdayTextView = findViewById(R.id.weekdayTextView);
        jieqiTextView = findViewById(R.id.jieqiTextView);
        resetTimeButton = findViewById(R.id.resetTimeButton);
        fourPillarsTextView = findViewById(R.id.fourPillarsTextView);
        copyButton = findViewById(R.id.copyButton);
        timeFortuneTextView = findViewById(R.id.timeFortuneTextView);
        panExplanation = findViewById(R.id.panExplanation);
        ninePalacePanel = (NinePalacePanel) findViewById(R.id.ninePalacePanel);
        mainLayout = findViewById(R.id.mainLayout);

        // 背景/亮度只需初始化时设置一次，避免每秒触发九宫格整屏重绘
        updateBackground();
        timeContainer = findViewById(R.id.timeContainer);
        rotationLockButton = findViewById(R.id.rotationLockButton);

        updateRotationLockButton();

        rotationLockButton.setOnClickListener(v -> toggleRotationLock());

        // 点击四柱跳转到命理解读页面
        fourPillarsTextView.setOnClickListener(v -> {
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
        });
        fourPillarsTextView.setClickable(true);
        fourPillarsTextView.setFocusable(true);

        // 点击日期弹出日期时间选择器
        if (dateTextView != null) {
            dateTextView.setOnClickListener(v -> showDateTimePicker());
        }

        // 点击返回按钮恢复当前时间
        if (resetTimeButton != null) {
            resetTimeButton.setOnClickListener(v -> resetToCurrentTime());
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
        ninePalacePanel.setOnClickListener(v -> {
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
        });

        // 点击吉凶解释跳转到罗盘页面
        if (panExplanation != null) {
            panExplanation.setClickable(true);
            panExplanation.setFocusable(true);
            panExplanation.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, LuoPanActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        
        // 为时辰运势添加点击事件监听器，点击时显示五运六气
        timeFortuneTextView.setOnClickListener(v -> {
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
        });
        
        // 设置时辰运势TextView可点击
        timeFortuneTextView.setClickable(true);
        timeFortuneTextView.setFocusable(true);

        // 为节气TextView添加点击事件监听器，点击时跳转到节气详情页面
        if (jieqiTextView != null) {
            jieqiTextView.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, JieqiActivity.class);
                    String jieqi = jieqiTextView.getText().toString().replace("·", "").trim();
                    intent.putExtra("jieqi", jieqi);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

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
        if (isCustomTime) {
            dateString = "✎ " + dateString;
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
        if (weekdayTextView != null) {
            weekdayTextView.setText(weekdayString);
        }

        // 更新节气显示
        String jieqi = JieqiData.getCurrentJieqi(displayCalendar);
        if (jieqiTextView != null) {
            jieqiTextView.setText(jieqi);
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

        // 更新时辰运势
        String timeZhi = timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";
        timeFortuneTextView.setText(getTimeFortune(timeZhi));

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
            // 创建目标日期
            java.util.Calendar targetCalendar = java.util.Calendar.getInstance();
            targetCalendar.set(year, month - 1, day);
            
            // 创建基准日期（1900年1月1日为甲戌日）
            java.util.Calendar baseCalendar = java.util.Calendar.getInstance();
            baseCalendar.set(1900, 0, 1);
            
            // 计算与基准日期的天数差
            long targetTime = targetCalendar.getTimeInMillis();
            long baseTime = baseCalendar.getTimeInMillis();
            long daysDiff = (targetTime - baseTime) / (1000 * 60 * 60 * 24);
            
            // 计算干支索引（1900年1月1日为甲戌日，索引为10）
            int baseGanzhiIndex = 10;
            int ganzhiIndex = (baseGanzhiIndex + (int)daysDiff) % 60;
            if (ganzhiIndex < 0) ganzhiIndex += 60;
            
            // 直接从60甲子数组中取
            return LIUJIAZI[ganzhiIndex];
            
        } catch (Exception e) {
            e.printStackTrace();
            return "甲午"; // 默认值
        }
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
            case "天辅": return new String[]{"文星主事", "利学业签约、文化教育"};
            case "天心": return new String[]{"医星临身", "利求医决策、智慧判断"};
            case "天禽": return new String[]{"贵人得位", "利合作谈判、求财办事"};
            case "天任": return new String[]{"土德厚实", "利守成积累、房产置业"};
            case "天蓬": return new String[]{"水势涌动", "宜防范风险、谨慎投资"};
            case "天冲": return new String[]{"雷厉风行", "利变革突破、出行远行"};
            case "天芮": return new String[]{"病符主事", "宜关注健康、修身养性"};
            case "天柱": return new String[]{"金气肃杀", "宜防小人、稳守为上"};
            case "天英": return new String[]{"火性炎上", "利宣传展示、名声远播"};
            default: return new String[]{"星辰待辨", "需结合全盘分析"};
        }
    }
    
    private String[] getZhiShiPhrases(String door) {
        if (door == null) return new String[]{"门位不清", "需结合全盘分析"};
        switch (door) {
            case "开": return new String[]{"开门通达", "宜开业求职、签约合作"};
            case "休": return new String[]{"休门安宁", "宜休养调理、会友社交"};
            case "生": return new String[]{"生门得位", "宜投资理财、经商置业"};
            case "伤": return new String[]{"伤门临位", "忌签约出行、防损失争端"};
            case "杜": return new String[]{"杜门闭塞", "宜守静隐藏、不宜冒进"};
            case "景": return new String[]{"景门秀丽", "宜策划考试、展示宣传"};
            case "死": return new String[]{"死门当值", "百事不宜、静待时机"};
            case "惊": return new String[]{"惊门不安", "忌诉讼争论、防口舌是非"};
            default: return new String[]{"门气不稳", "需谨慎行事"};
        }
    }
    
    // 更新九宫格解释
    private void updateNinePalaceExplanation(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        if (panExplanation != null && ninePalacePanel != null) {
            String zhiFu = ninePalacePanel.getCopyZhiFu();
            String zhiShi = ninePalacePanel.getCopyZhiShi();
            
                        // 与九宫配色同源：取值符所在宫的吉凶等级
            String luckLabel = ninePalacePanel.getZhiFuPalaceLuck();
            String simpleMeaning = getSimpleMeaning(zhiFu, zhiShi);
            String simpleAdvice = getSimpleAdviceByLabel(luckLabel);

            android.text.SpannableStringBuilder sb = new android.text.SpannableStringBuilder();
            
            sb.append(simpleMeaning);
            sb.setSpan(new android.text.style.ForegroundColorSpan(getResources().getColor(R.color.explanation_gray)), 0, sb.length(), android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            
            sb.append(" · ");
            
            sb.append(simpleAdvice);
            sb.setSpan(new android.text.style.ForegroundColorSpan(getResources().getColor(R.color.jieqi_orange)), sb.length() - simpleAdvice.length(), sb.length(), android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            
            panExplanation.setText(sb);
        }
    }
    
    private String[] getGeneralPhrases(String star, String door) {
        boolean isGoodStar = isGoodStar(star);
        boolean isGoodDoor = isGoodDoor(door);
        
        if (isGoodStar && isGoodDoor) {
            return new String[]{"气场旺盛", "万事通达", "宜积极进取"};
        } else if (isGoodStar && !isGoodDoor) {
            return new String[]{"星吉门平", "宜守待时", "择机而动"};
        } else if (!isGoodStar && isGoodDoor) {
            return new String[]{"门吉星平", "借力而为", "稳步推进"};
        } else {
            return new String[]{"气场偏弱", "宜静不宜动", "谨慎行事"};
        }
    }
    
    private String getStarDescription(String star) {
        if (star == null) return "";
        if (star.equals("天辅")) return "文曲·利学业";
        if (star.equals("天心")) return "医星·利决策";
        if (star.equals("天禽")) return "贵人·利合作";
        if (star.equals("天任")) return "稳重·利守成";
        if (star.equals("天冲")) return "行动·利变革";
        if (star.equals("天英")) return "光明·利名声";
        if (star.equals("天蓬")) return "暗涌·防风险";
        if (star.equals("天芮")) return "病符·注意健康";
        if (star.equals("天柱")) return "阻滞·防小人";
        return "";
    }
    
    private String getDoorDescription(String door) {
        if (door == null) return "";
        if (door.equals("开")) return "开门·万事通";
        if (door.equals("休")) return "休门·宜调养";
        if (door.equals("生")) return "生门·财运旺";
        if (door.equals("伤")) return "伤门·防损耗";
        if (door.equals("杜")) return "杜门·宜守静";
        if (door.equals("景")) return "景门·利策划";
        if (door.equals("死")) return "死门·百事忌";
        if (door.equals("惊")) return "惊门·防口舌";
        return "";
    }
    
    private String getLuckReason(String star, String door) {
        if (star == null) star = "";
        if (door == null) door = "";
        
        boolean isGoodStar = isGoodStar(star);
        boolean isGoodDoor = isGoodDoor(door);
        
        String starDesc = "";
        if (star.equals("天辅")) starDesc = "文星主事，利学业文书";
        else if (star.equals("天心")) starDesc = "医星高照，利决策判断";
        else if (star.equals("天禽")) starDesc = "贵人星临，利合作求财";
        else if (star.equals("天任")) starDesc = "稳重踏实，利守成积累";
        else if (star.equals("天冲")) starDesc = "行动力强，利变革突破";
        else if (star.equals("天英")) starDesc = "光明磊落，利名声展示";
        else if (star.equals("天蓬")) starDesc = "暗藏风险，宜谨慎防范";
        else if (star.equals("天芮")) starDesc = "病符临位，宜关注健康";
        else if (star.equals("天柱")) starDesc = "阻力较大，宜稳不宜进";
        
        String doorDesc = "";
        if (door.equals("开")) doorDesc = "开门通达，诸事可成";
        else if (door.equals("休")) doorDesc = "休门安宁，宜养精蓄锐";
        else if (door.equals("生")) doorDesc = "生门得位，财运亨通";
        else if (door.equals("伤")) doorDesc = "伤门临位，防损失争端";
        else if (door.equals("杜")) doorDesc = "杜门闭塞，宜守不宜攻";
        else if (door.equals("景")) doorDesc = "景门秀丽，适合策划";
        else if (door.equals("死")) doorDesc = "死门当值，百事不宜";
        else if (door.equals("惊")) doorDesc = "惊门不安，谨防口舌";
        
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
            return "星门俱佳，时机成熟，可全力推进重要事务";
        } else if (score >= 3) {
            return "吉星照临，门位得力，适合启动新计划";
        } else if (score >= 1) {
            return "星门尚可，有小幅助力，宜循序渐进";
        } else if (score >= -1) {
            return "星门平淡，无大碍亦无大利，保持现状即可";
        } else if (score >= -3) {
            return "星门偏弱，外部阻力较大，宜暂缓行动";
        } else if (score >= -5) {
            return "星门不利，不宜冒进，韬光养晦为上策";
        } else {
            return "星门俱凶，万事皆忌，务必静守待变";
        }
    }
    
    private String getSimpleMeaning(String star, String door) {
        String starMean = "";
        if (star != null) {
            if (star.equals("天辅")) starMean = "文星主事，利学业文书";
            else if (star.equals("天心")) starMean = "医星高照，利决策判断";
            else if (star.equals("天禽")) starMean = "贵人星临，利合作求财";
            else if (star.equals("天任")) starMean = "稳重踏实，利守成积累";
            else if (star.equals("天冲")) starMean = "行动力强，利变革突破";
            else if (star.equals("天英")) starMean = "光明磊落，利名声展示";
            else if (star.equals("天蓬")) starMean = "暗藏风险，宜谨慎防范";
            else if (star.equals("天芮")) starMean = "病符临位，宜关注健康";
            else if (star.equals("天柱")) starMean = "阻力较大，宜稳不宜进";
            else starMean = "星位平和";
        }
        
        String doorMean = "";
        if (door != null) {
            if (door.equals("开")) doorMean = "开门通达，诸事可成";
            else if (door.equals("生")) doorMean = "生门得位，财运亨通";
            else if (door.equals("休")) doorMean = "休门安宁，宜养精蓄锐";
            else if (door.equals("景")) doorMean = "景门秀丽，适合策划展示";
            else if (door.equals("杜")) doorMean = "杜门闭塞，宜守不宜攻";
            else if (door.equals("惊")) doorMean = "惊门不安，谨防口舌是非";
            else if (door.equals("伤")) doorMean = "伤门临位，防损失争端";
            else if (door.equals("死")) doorMean = "死门当值，百事不宜";
            else doorMean = "门位平和";
        }
        
        return starMean + "｜" + doorMean;
    }
    
    private String getSimpleAdvice(int score) {
        if (score >= 5) {
            return "天时地利，宜大胆进取";
        } else if (score >= 3) {
            return "运势良好，宜推进计划";
        } else if (score >= 1) {
            return "小有助力，宜稳步前行";
        } else if (score >= -1) {
            return "运势平平，宜守不宜攻";
        } else if (score >= -3) {
            return "阻力渐显，宜谨慎观望";
        } else if (score >= -5) {
            return "时运不济，宜以静制动";
        } else {
            return "诸事不宜，静待时机转变";
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

    @Override
    protected void onPause() {
        super.onPause();
        // 应用进入后台时，停止时间更新
        handler.removeCallbacks(timeRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDateTime();
        handler.removeCallbacks(timeRunnable);
        handler.postDelayed(timeRunnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 应用销毁时，清理资源
        handler.removeCallbacksAndMessages(null);
    }
    
    private String getTimeFortune(String timeZhi) {
        if (timeZhi == null) return "时辰吉利";
        switch (timeZhi) {
            case "子": return "子时(23-01) 胆经当令 · 宜安睡养阳，忌熬夜";
            case "丑": return "丑时(01-03) 肝经当令 · 宜深睡排毒，忌饮酒";
            case "寅": return "寅时(03-05) 肺经当令 · 宜熟睡养肺，忌剧烈";
            case "卯": return "卯时(05-07) 大肠经当令 · 宜起床排便，饮温水";
            case "辰": return "辰时(07-09) 胃经当令 · 宜吃早餐，营养吸收最佳";
            case "巳": return "巳时(09-11) 脾经当令 · 宜工作学习，精力最旺";
            case "午": return "午时(11-13) 心经当令 · 宜小憩养心，忌过劳";
            case "未": return "未时(13-15) 小肠经当令 · 宜午休后放松";
            case "申": return "申时(15-17) 膀胱经当令 · 宜运动饮水，排毒佳期";
            case "酉": return "酉时(17-19) 肾经当令 · 宜静养收藏，忌过劳";
            case "戌": return "戌时(19-21) 心包经当令 · 宜愉悦休闲，忌忧愁";
            case "亥": return "亥时(21-23) 三焦经当令 · 宜泡脚安眠，百脉通";
            default: return "时辰吉利";
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
            if (isRotationLocked) {
                rotationLockButton.setText("🔓");
            } else {
                rotationLockButton.setText("🔒");
            }
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