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
    public TextView ninePalaceExplanation;
    public TextView detailedInterpretation;
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
    private int lastHour = -1;
    private int lastMinute = -1;
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
        ninePalaceExplanation = findViewById(R.id.panExplanation);
        timeFortuneTextView = findViewById(R.id.timeFortuneTextView);
        panExplanation = findViewById(R.id.panExplanation);
        ninePalacePanel = (NinePalacePanel) findViewById(R.id.ninePalacePanel);
        mainLayout = findViewById(R.id.mainLayout);
        timeContainer = findViewById(R.id.timeContainer);
        rotationLockButton = findViewById(R.id.rotationLockButton);

        updateRotationLockButton();

        rotationLockButton.setOnClickListener(v -> toggleRotationLock());

        // 点击四柱跳转到罗盘页面
        fourPillarsTextView.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, LuoPanActivity.class);
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

        // 点击吉凶解释跳转到命理信息页面
        if (panExplanation != null) {
            panExplanation.setClickable(true);
            panExplanation.setFocusable(true);
            panExplanation.setOnClickListener(v -> {
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

        // 大时钟始终显示真实时间
        Date now = new Date();
        Calendar realCalendar = Calendar.getInstance();
        realCalendar.setTime(now);

        int currentHour = realCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = realCalendar.get(Calendar.MINUTE);

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
        Date fourPillarsDate = isCustomTime ? customCalendar.getTime() : now;
        if (minuteChanged || isCustomTime) {
            updateFourPillars(fourPillarsDate);
        }

        updateBackground();
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
        // 设置背景为深蓝色
        mainLayout.setBackgroundColor(0xFF0A0A14);

        // 设置七段数码管的亮度（降低亮度以节省电量）
        hour1TextView.setBrightness(0.7f);
        hour2TextView.setBrightness(0.7f);
        minute1TextView.setBrightness(0.7f);
        minute2TextView.setBrightness(0.7f);

        // 设置九宫格亮度
        ninePalacePanel.setBrightness(0.7f);

        // 设置日期和四柱文字颜色，使用新添加的颜色资源
        dateTextView.setTextColor(0xFF87CEEB); // sky_blue
        fourPillarsTextView.setTextColor(0xFFADD8E6); // light_blue
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
        String jieqi = getJieqi(year, month, day);
        
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
    
    // 月份到月支的映射
    private static final java.util.HashMap<Integer, String> MONTH_ZHI_MAP = new java.util.HashMap<Integer, String>();
    
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
        
        // 初始化月份到月支的映射
        MONTH_ZHI_MAP.put(1, "寅");
        MONTH_ZHI_MAP.put(2, "卯");
        MONTH_ZHI_MAP.put(3, "辰");
        MONTH_ZHI_MAP.put(4, "巳");
        MONTH_ZHI_MAP.put(5, "午");
        MONTH_ZHI_MAP.put(6, "未");
        MONTH_ZHI_MAP.put(7, "申");
        MONTH_ZHI_MAP.put(8, "酉");
        MONTH_ZHI_MAP.put(9, "戌");
        MONTH_ZHI_MAP.put(10, "亥");
        MONTH_ZHI_MAP.put(11, "子");
        MONTH_ZHI_MAP.put(12, "丑");
    }

    // 计算年柱
    private String calculateYearPillar(int year, int month, int day) {
        // 以立春为年分界
        if (month < 2 || (month == 2 && day < 4)) {
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
    
    // 获取月支
    private String getMonthZhi(int month, int day) {
        // 1月6日小寒前为子月，小寒后为丑月
        if (month == 1 && day < 6) {
            return "子";
        } else if (month == 1 && day >= 6) {
            return "丑";
        }
        // 2月4日立春后为寅月
        if (month == 2 && day >= 4) {
            return "寅";
        } else if (month == 2 && day < 4) {
            return "丑";
        }
        
        return MONTH_ZHI_MAP.get(month);
    }

    // 计算月柱
    private String calculateMonthPillar(int year, int month, int day, String yearGan) {
        String monthZhi = getMonthZhi(month, day);
        
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
            
            int luckScore = calculateOverallLuck(zhiFu, zhiShi);
            String luckText = getLuckText(luckScore);
            String simpleMeaning = getSimpleMeaning(zhiFu, zhiShi);
            String simpleAdvice = getSimpleAdvice(luckScore);
            
            android.text.SpannableStringBuilder sb = new android.text.SpannableStringBuilder();
            
            sb.append(luckText);
            sb.setSpan(new android.text.style.ForegroundColorSpan(getLuckColor(luckScore)), 0, luckText.length(), android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            
            sb.append("\n");
            
            sb.append(simpleMeaning);
            sb.setSpan(new android.text.style.ForegroundColorSpan(0xFF6B7280), sb.length() - simpleMeaning.length(), sb.length(), android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            
            sb.append(" · ");
            
            sb.append(simpleAdvice);
            sb.setSpan(new android.text.style.ForegroundColorSpan(0xFFB45309), sb.length() - simpleAdvice.length(), sb.length(), android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            
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
    
    private int calculateOverallLuck(String star, String door) {
        int score = 0;
        
        if (star != null) {
            if (star.equals("天辅") || star.equals("天心") || star.equals("天禽")) {
                score += 3;
            } else if (star.equals("天任")) {
                score += 2;
            } else if (star.equals("天冲")) {
                score += 1;
            } else if (star.equals("天英")) {
                score += 0;
            } else if (star.equals("天蓬") || star.equals("天芮") || star.equals("天柱")) {
                score -= 2;
            }
        }
        
        if (door != null) {
            if (door.equals("开") || door.equals("生")) {
                score += 3;
            } else if (door.equals("休")) {
                score += 2;
            } else if (door.equals("景")) {
                score += 1;
            } else if (door.equals("杜")) {
                score += 0;
            } else if (door.equals("惊")) {
                score -= 1;
            } else if (door.equals("伤") || door.equals("死")) {
                score -= 3;
            }
        }
        
        return score;
    }
    
    private String getLuckText(int score) {
        if (score >= 5) {
            return "大吉";
        } else if (score >= 3) {
            return "吉";
        } else if (score >= 1) {
            return "平吉";
        } else if (score >= -1) {
            return "平";
        } else if (score >= -3) {
            return "平凶";
        } else if (score >= -5) {
            return "凶";
        } else {
            return "大凶";
        }
    }
    
    private int getLuckColor(int score) {
        if (score >= 5) {
            return 0xFF22C55E;
        } else if (score >= 3) {
            return 0xFF34D399;
        } else if (score >= 1) {
            return 0xFF84CC16;
        } else if (score >= -1) {
            return 0xFF6B7280;
        } else if (score >= -3) {
            return 0xFFF97316;
        } else if (score >= -5) {
            return 0xFFEF4444;
        } else {
            return 0xFFDC2626;
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
    
    private String getStarMeaning(String star) {
        if (star == null) return "";
        if (star.equals("天辅")) return "天辅星(文曲星)主事，利学业考试、文书签约";
        if (star.equals("天心")) return "天心星(医星)主事，利求医问药、决策判断";
        if (star.equals("天禽")) return "天禽星(贵人星)主事，利合作谈判、求财办事";
        if (star.equals("天任")) return "天任星(稳重星)主事，利守成积累、房产置业";
        if (star.equals("天冲")) return "天冲星(行动星)主事，利变革突破、出行远行";
        if (star.equals("天英")) return "天英星(光明星)主事，利展示宣传、名声远播";
        if (star.equals("天蓬")) return "天蓬星(风险星)主事，宜防范风险、谨慎投资";
        if (star.equals("天芮")) return "天芮星(病符星)主事，宜关注健康、修身养性";
        if (star.equals("天柱")) return "天柱星(阻滞星)主事，宜防范小人、稳守为上";
        return "";
    }
    
    private String getDoorMeaning(String door) {
        if (door == null) return "";
        if (door.equals("开")) return "开门(吉门)主通达，宜开业、求职、签约";
        if (door.equals("休")) return "休门(吉门)主安宁，宜休养、会友、调理";
        if (door.equals("生")) return "生门(吉门)主财运，宜投资、经商、置业";
        if (door.equals("伤")) return "伤门(凶门)主损耗，忌出行、签约、争执";
        if (door.equals("杜")) return "杜门(平门)主闭塞，宜守静、隐藏、防守";
        if (door.equals("景")) return "景门(平门)主文书，宜策划、考试、展示";
        if (door.equals("死")) return "死门(凶门)主阻滞，百事不宜、静待时机";
        if (door.equals("惊")) return "惊门(凶门)主口舌，忌诉讼、争论、冒险";
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

    // 根据时间调整屏幕亮度
    private void adjustScreenBrightness() {
        try {
            // 获取Window实例
            android.view.Window window = getWindow();
            android.view.WindowManager.LayoutParams layoutParams = window.getAttributes();

            // 设置中等亮度，平衡显示效果和电池消耗
            layoutParams.screenBrightness = 0.5f;

            // 应用亮度设置
            window.setAttributes(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 兼容性方法，防止旧代码报错
    public void sendMessageToUpdateTime() {
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
    
    // 显示当前排盘的详细解读
    private void showDetailedInterpretation() {
        // 获取当前时间的四柱
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        
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
        
        // 生成详细解读
        String interpretation = generateDetailedInterpretation(yearPillar, monthPillar, dayPillar, timePillar);
        
        // 显示解读对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("奇门遁甲详细解读")
               .setMessage(interpretation)
               .setPositiveButton("确定", null)
               .show();
    }
    
    // 生成详细解读
    private String generateDetailedInterpretation(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        StringBuilder sb = new StringBuilder();
        
        // 基本信息（简要）
        sb.append("当前四柱: " + (yearPillar != null ? yearPillar : "未知") + " " + (monthPillar != null ? monthPillar : "未知") + " " + (dayPillar != null ? dayPillar : "未知") + " " + (timePillar != null ? timePillar : "未知") + "\n\n");
        
        // 阴阳遁和用局数
        String monthZhi = (monthPillar != null && monthPillar.length() >= 2) ? monthPillar.substring(1, 2) : "子";
        boolean isYangDun = isYangDun(monthZhi);
        int ju = getJuShu(monthZhi, isYangDun);
        
        sb.append("奇门盘: " + (isYangDun ? "阳遁" : "阴遁") + ju + "局\n\n");
        
        // 使用实际的奇门遁甲算法计算九宫信息
        String[] NINE_STARS = {"天蓬", "天芮", "天冲", "天辅", "天禽", "天心", "天柱", "天任", "天英"};
        String[] EIGHT_DOORS = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
        String[] DIRECTIONS = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};
        
        // 提取四柱的天干地支
        String timeGan = timePillar != null && timePillar.length() >= 1 ? timePillar.substring(0, 1) : "甲";
        String timeZhi = timePillar != null && timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";
        
        // 计算九宫格数据
        String[] diPanTianGan = arrangeDiPanTianGan(ju, isYangDun);
        int[] starPositions = arrangeNineStars(ju, isYangDun, timeGan);
        int[] doorPositions = arrangeEightDoors(ju, isYangDun, timeZhi);
        
        // 确定值符值使
        Object[] xunShouInfo = getXunShouInfo(timeGan, timeZhi);
        int zhiFuStarIndex = (int) xunShouInfo[2];
        int zhiShiDoorIndex = (int) xunShouInfo[3];
        
        // 找到值符星所在宫位
        int zhiFuPalace = -1;
        for (int i = 0; i < 9; i++) {
            if (starPositions[i] == zhiFuStarIndex) {
                zhiFuPalace = i;
                break;
            }
        }
        
        // 确保值符宫位有效
        if (zhiFuPalace == -1) {
            zhiFuPalace = 4; // 默认使用中五宫
        }
        
        // 排八神
        int[] godPositions = arrangeEightGods(zhiFuPalace, isYangDun, timeGan);
        
        // 找出吉门方位
        StringBuilder luckyDirections = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < 9; i++) {
            int doorIndex = doorPositions[i];
            String door = EIGHT_DOORS[doorIndex];
            if (door.equals("开") || door.equals("休") || door.equals("生")) {
                if (!first) {
                    luckyDirections.append("、");
                }
                luckyDirections.append(DIRECTIONS[i]);
                first = false;
            }
        }
        
        // 找出凶门方位
        StringBuilder unluckyDirections = new StringBuilder();
        first = true;
        for (int i = 0; i < 9; i++) {
            int doorIndex = doorPositions[i];
            String door = EIGHT_DOORS[doorIndex];
            if (door.equals("死") || door.equals("惊") || door.equals("伤")) {
                if (!first) {
                    unluckyDirections.append("、");
                }
                unluckyDirections.append(DIRECTIONS[i]);
                first = false;
            }
        }
        
        // 核心建议
        sb.append("【核心建议】\n");
        sb.append("━━━━━━━━━━━━━━━\n\n");
        
        // 方位建议
        sb.append("🧭 方位指导\n");
        if (luckyDirections.length() > 0) {
            sb.append("  吉方：" + luckyDirections.toString() + "，适宜开展重要活动\n");
        }
        if (unluckyDirections.length() > 0) {
            sb.append("  凶方：" + unluckyDirections.toString() + "，宜避开\n");
        }
        sb.append("\n");
        
        // 事项建议
        sb.append("📝 事项指导\n");
        sb.append("  值符星(" + NINE_STARS[zhiFuStarIndex] + ")：" + getStarMeaning(NINE_STARS[zhiFuStarIndex]) + "\n");
        sb.append("  值使门(" + EIGHT_DOORS[zhiShiDoorIndex] + ")：" + getDoorMeaning(EIGHT_DOORS[zhiShiDoorIndex]) + "\n\n");
        
        // 贵人提示
        sb.append("🌟 贵人方位\n");
        sb.append("  值符、值使所在方位易得贵人相助，办事可优先选择该方位。\n\n");
        
        // 总结
        sb.append("💡 综合提示\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        sb.append("  以上为奇门遁甲排盘的基本信息，具体应用需结合个人命理和实际情况。\n");
        sb.append("  吉方吉时可趋，凶方凶时宜避，顺势而为，灵活应对。\n");
        
        return sb.toString();
    }
    
    // 排地盘天干
    private String[] arrangeDiPanTianGan(int ju, boolean isYangDun) {
        String[] baseTianGan = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
        String[] result = new String[9];
        
        if (isYangDun) {
            // 阳遁：从用局数-1的位置开始
            int startIndex = ju - 1;
            for (int i = 0; i < 9; i++) {
                result[(startIndex + i) % 9] = baseTianGan[i];
            }
        } else {
            // 阴遁：从9-用局数的位置开始
            int startIndex = 9 - ju;
            for (int i = 0; i < 9; i++) {
                result[(startIndex + i) % 9] = baseTianGan[i];
            }
        }
        
        return result;
    }
    
    // 排九星
    private int[] arrangeNineStars(int ju, boolean isYangDun, String timeGan) {
        int[] baseStars = {0, 1, 2, 3, 4, 5, 6, 7, 8}; // 天蓬、天任、天冲、天辅、天英、天芮、天柱、天心、天禽
        int[] result = new int[9];
        
        if (isYangDun) {
            // 阳遁：顺排
            int startIndex = ju - 1;
            for (int i = 0; i < 9; i++) {
                result[(startIndex + i) % 9] = baseStars[i];
            }
        } else {
            // 阴遁：逆排
            int startIndex = 9 - ju;
            for (int i = 0; i < 9; i++) {
                result[(startIndex + i) % 9] = baseStars[8 - i];
            }
        }
        
        return result;
    }
    
    // 排八门
    private int[] arrangeEightDoors(int ju, boolean isYangDun, String timeZhi) {
        int[] baseDoors = {0, 1, 2, 3, 4, 5, 6, 7}; // 休、生、伤、杜、景、死、惊、开
        int[] result = new int[9];
        
        if (isYangDun) {
            // 阳遁：顺排
            int startIndex = ju - 1;
            for (int i = 0; i < 8; i++) {
                result[(startIndex + i) % 9] = baseDoors[i];
            }
        } else {
            // 阴遁：逆排
            int startIndex = 9 - ju;
            for (int i = 0; i < 8; i++) {
                result[(startIndex + i) % 9] = baseDoors[7 - i];
            }
        }
        
        // 中五宫借用坤二宫的门
        result[4] = result[1];
        
        return result;
    }
    
    // 获取旬首信息
    private Object[] getXunShouInfo(String timeGan, String timeZhi) {
        String[][] xunShouTable = {
            {"甲", "子", "戊", "0", "1"},
            {"甲", "戌", "戊", "0", "1"},
            {"甲", "申", "戊", "0", "1"},
            {"甲", "午", "戊", "0", "1"},
            {"甲", "辰", "戊", "0", "1"},
            {"甲", "寅", "戊", "0", "1"},
            {"乙", "丑", "己", "1", "2"},
            {"乙", "亥", "己", "1", "2"},
            {"乙", "酉", "己", "1", "2"},
            {"乙", "未", "己", "1", "2"},
            {"乙", "巳", "己", "1", "2"},
            {"乙", "卯", "己", "1", "2"},
            {"丙", "寅", "庚", "2", "3"},
            {"丙", "子", "庚", "2", "3"},
            {"丙", "戌", "庚", "2", "3"},
            {"丙", "申", "庚", "2", "3"},
            {"丙", "午", "庚", "2", "3"},
            {"丙", "辰", "庚", "2", "3"},
            {"丁", "卯", "辛", "3", "4"},
            {"丁", "丑", "辛", "3", "4"},
            {"丁", "亥", "辛", "3", "4"},
            {"丁", "酉", "辛", "3", "4"},
            {"丁", "未", "辛", "3", "4"},
            {"丁", "巳", "辛", "3", "4"},
            {"戊", "辰", "壬", "4", "5"},
            {"戊", "寅", "壬", "4", "5"},
            {"戊", "子", "壬", "4", "5"},
            {"戊", "戌", "壬", "4", "5"},
            {"戊", "申", "壬", "4", "5"},
            {"戊", "午", "壬", "4", "5"},
            {"己", "巳", "癸", "5", "6"},
            {"己", "卯", "癸", "5", "6"},
            {"己", "丑", "癸", "5", "6"},
            {"己", "亥", "癸", "5", "6"},
            {"己", "酉", "癸", "5", "6"},
            {"己", "未", "癸", "5", "6"},
            {"庚", "午", "丁", "6", "7"},
            {"庚", "辰", "丁", "6", "7"},
            {"庚", "寅", "丁", "6", "7"},
            {"庚", "子", "丁", "6", "7"},
            {"庚", "戌", "丁", "6", "7"},
            {"庚", "申", "丁", "6", "7"},
            {"辛", "未", "丙", "7", "0"},
            {"辛", "巳", "丙", "7", "0"},
            {"辛", "卯", "丙", "7", "0"},
            {"辛", "丑", "丙", "7", "0"},
            {"辛", "亥", "丙", "7", "0"},
            {"辛", "酉", "丙", "7", "0"},
            {"壬", "申", "乙", "8", "1"},
            {"壬", "午", "乙", "8", "1"},
            {"壬", "辰", "乙", "8", "1"},
            {"壬", "寅", "乙", "8", "1"},
            {"壬", "子", "乙", "8", "1"},
            {"壬", "戌", "乙", "8", "1"},
            {"癸", "酉", "甲", "8", "2"},
            {"癸", "未", "甲", "8", "2"},
            {"癸", "巳", "甲", "8", "2"},
            {"癸", "卯", "甲", "8", "2"},
            {"癸", "丑", "甲", "8", "2"},
            {"癸", "亥", "甲", "8", "2"}
        };
        
        for (String[] entry : xunShouTable) {
            if (entry[0].equals(timeGan) && entry[1].equals(timeZhi)) {
                return new Object[]{entry[2], entry[1], Integer.parseInt(entry[3]), Integer.parseInt(entry[4])};
            }
        }
        
        // 默认返回甲子旬
        return new Object[]{"戊", "子", 0, 1};
    }
    
    // 排八神
    private int[] arrangeEightGods(int zhiFuPalace, boolean isYangDun, String timeGan) {
        int[] baseGods = {0, 1, 2, 3, 4, 5, 6, 7}; // 值符、螣蛇、太阴、六合、白虎、玄武、九地、九天
        int[] result = new int[9];
        
        if (isYangDun) {
            // 阳遁：顺排
            for (int i = 0; i < 8; i++) {
                result[(zhiFuPalace + i) % 9] = baseGods[i];
            }
        } else {
            // 阴遁：逆排
            for (int i = 0; i < 8; i++) {
                result[(zhiFuPalace + i) % 9] = baseGods[7 - i];
            }
        }
        
        // 中五宫借用坤二宫的神
        result[4] = result[1];
        
        return result;
    }
    
    // 计算吉凶
    private String calculateLuck(String star, String door, String god) {
        // 吉星
        String[] luckyStars = {"天辅", "天心", "天禽", "天任"};
        // 吉门
        String[] luckyDoors = {"开", "休", "生"};
        // 吉神
        String[] luckyGods = {"值符", "太阴", "六合", "九天"};
        
        int score = 0;
        
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
        
        // 吉神加1分
        for (String luckyGod : luckyGods) {
            if (luckyGod.equals(god)) {
                score++;
                break;
            }
        }
        
        if (score >= 3) {
            return "大吉";
        } else if (score >= 2) {
            return "吉";
        } else if (score >= 1) {
            return "平";
        } else {
            return "凶";
        }
    }
    
    // 判断是否为阳遁
    private boolean isYangDun(String monthZhi) {
        if (monthZhi == null) {
            return true;
        }
        int zhiIndex = java.util.Arrays.asList(DIZHI).indexOf(monthZhi);
        return zhiIndex >= 1 && zhiIndex <= 6; // 2月(卯)到7月(申)为阳遁
    }
    
    // 获取用局数
    private int getJuShu(String monthZhi, boolean isYangDun) {
        if (monthZhi == null) {
            return 1;
        }
        int zhiIndex = java.util.Arrays.asList(DIZHI).indexOf(monthZhi);
        int month = (zhiIndex + 2) % 12 + 1; // 转换为1-12月
        
        // 直接使用月份对应传统用局表
        int[] MONTH_JU = {1, 8, 1, 3, 4, 6, 9, 2, 9, 7, 6, 4};
        return MONTH_JU[month - 1];
    }
    
    // 获取节气（基于日期）
    private String getJieqi(int year, int month, int day) {
        // 节气日期表（简化版，基于公历）
        // 每个节气的日期范围（月/日）
        String[][] jieqiDates = {
            {"立春", "2", "4", "2", "18"},
            {"雨水", "2", "19", "3", "5"},
            {"惊蛰", "3", "6", "3", "20"},
            {"春分", "3", "21", "4", "4"},
            {"清明", "4", "5", "4", "19"},
            {"谷雨", "4", "20", "5", "5"},
            {"立夏", "5", "6", "5", "20"},
            {"小满", "5", "21", "6", "5"},
            {"芒种", "6", "6", "6", "20"},
            {"夏至", "6", "21", "7", "6"},
            {"小暑", "7", "7", "7", "22"},
            {"大暑", "7", "23", "8", "7"},
            {"立秋", "8", "8", "8", "22"},
            {"处暑", "8", "23", "8", "31"},
            {"白露", "9", "1", "9", "16"},
            {"秋分", "9", "17", "10", "7"},
            {"寒露", "10", "8", "10", "23"},
            {"霜降", "10", "24", "11", "7"},
            {"立冬", "11", "8", "11", "22"},
            {"小雪", "11", "23", "12", "6"},
            {"大雪", "12", "7", "12", "21"},
            {"冬至", "12", "22", "1", "4"},
            {"小寒", "1", "5", "1", "19"},
            {"大寒", "1", "20", "2", "3"}
        };
        
        for (String[] jieqiEntry : jieqiDates) {
            String jieqiName = jieqiEntry[0];
            int startMonth = Integer.parseInt(jieqiEntry[1]);
            int startDay = Integer.parseInt(jieqiEntry[2]);
            int endMonth = Integer.parseInt(jieqiEntry[3]);
            int endDay = Integer.parseInt(jieqiEntry[4]);
            
            // 判断日期是否在节气范围内
            if (startMonth == endMonth) {
                if (month == startMonth && day >= startDay && day <= endDay) {
                    return jieqiName;
                }
            } else {
                // 跨月份的情况（如冬至跨12月和1月）
                if ((month == startMonth && day >= startDay) ||
                    (month == endMonth && day <= endDay)) {
                    return jieqiName;
                }
            }
        }
        
        // 默认返回立春
        return "立春";
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
            rotationLockButton.setTextColor(0x33FFFFFF);
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