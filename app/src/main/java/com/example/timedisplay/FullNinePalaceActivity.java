package com.example.timedisplay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FullNinePalaceActivity extends Activity {

    private FullNinePalacePanel fullNinePalacePanel;
    private TextView fullPageTitle;
    
    private TextView expBasic;
    private android.widget.TextView expDestiny;
    private TextView expZhifuZhishi;
    private TextView expGan;
    private TextView expSummary;
    private DetailedNinePalacePanel expTianDiPan;
    private DetailedNinePalacePanel expNineStars;
    private DetailedNinePalacePanel expEightDoors;
    private DetailedNinePalacePanel expGods;
    private TextView expDirection;
    private TextView expYiJi;
    private TextView expLife;
    private DetailedNinePalacePanel expPalaces;
    private TextView expTime;
    private TextView expOverall;
    private TextView destinyArrow;
    private boolean destinyExpanded = false;
    private TextView expTianDiPanDesc;
    private TextView expNineStarsDesc;
    private TextView expEightDoorsDesc;
    private TextView expGodsDesc;
    private TextView expPalacesDesc;

    private static final long UPDATE_INTERVAL = 60000;
    private Handler updateHandler;
    private Runnable updateRunnable;

    private static final String[] TIANGAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] DIZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static final String[] LIUJIAZI = {
        "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉",
        "甲戌", "乙亥", "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未",
        "甲申", "乙酉", "丙戌", "丁亥", "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳",
        "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥", "庚子", "辛丑", "壬寅", "癸卯",
        "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥", "壬子", "癸丑",
        "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉", "壬戌", "癸亥"
    };
    private static final String[] MONTH_ZHI_LIST = {"寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑"};
    private static final java.util.HashMap<String, String> WUHUDUN = new java.util.HashMap<String, String>();
    private static final java.util.HashMap<String, String> WUSHUDUN_MAP = new java.util.HashMap<String, String>();
    private static final java.util.HashMap<Integer, String> MONTH_ZHI_MAP = new java.util.HashMap<Integer, String>();

    static {
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

        MONTH_ZHI_MAP.put(1, "丑");
        MONTH_ZHI_MAP.put(2, "寅");
        MONTH_ZHI_MAP.put(3, "卯");
        MONTH_ZHI_MAP.put(4, "辰");
        MONTH_ZHI_MAP.put(5, "巳");
        MONTH_ZHI_MAP.put(6, "午");
        MONTH_ZHI_MAP.put(7, "未");
        MONTH_ZHI_MAP.put(8, "申");
        MONTH_ZHI_MAP.put(9, "酉");
        MONTH_ZHI_MAP.put(10, "戌");
        MONTH_ZHI_MAP.put(11, "亥");
        MONTH_ZHI_MAP.put(12, "子");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isRotationLocked = prefs.getBoolean("rotationLocked", false);
        int lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(lockedOrientation);
        }
        
        setContentView(R.layout.activity_full_nine_palace);

        fullNinePalacePanel = (FullNinePalacePanel) findViewById(R.id.fullNinePalacePanel);
        fullPageTitle = (TextView) findViewById(R.id.fullPageTitle);
        
        expBasic = (TextView) findViewById(R.id.expBasic);
        expZhifuZhishi = (TextView) findViewById(R.id.expZhifuZhishi);
        expGan = (TextView) findViewById(R.id.expGan);
        expSummary = (TextView) findViewById(R.id.expSummary);
        expTianDiPan = (DetailedNinePalacePanel) findViewById(R.id.expTianDiPan);
        expNineStars = (DetailedNinePalacePanel) findViewById(R.id.expNineStars);
        expEightDoors = (DetailedNinePalacePanel) findViewById(R.id.expEightDoors);
        expGods = (DetailedNinePalacePanel) findViewById(R.id.expGods);
        expDirection = (TextView) findViewById(R.id.expDirection);
        expYiJi = (TextView) findViewById(R.id.expYiJi);
        expLife = (TextView) findViewById(R.id.expLife);
        expPalaces = (DetailedNinePalacePanel) findViewById(R.id.expPalaces);
        expTime = (TextView) findViewById(R.id.expTime);
        expOverall = (TextView) findViewById(R.id.expOverall);
        expTianDiPanDesc = (TextView) findViewById(R.id.expTianDiPanDesc);
        expNineStarsDesc = (TextView) findViewById(R.id.expNineStarsDesc);
        expEightDoorsDesc = (TextView) findViewById(R.id.expEightDoorsDesc);
        expGodsDesc = (TextView) findViewById(R.id.expGodsDesc);
        expPalacesDesc = (TextView) findViewById(R.id.expPalacesDesc);
        
        expDestiny = (android.widget.TextView) findViewById(R.id.expDestiny);
        destinyArrow = (TextView) findViewById(R.id.destinyArrow);
        
        View destinyHeader = findViewById(R.id.destinyHeader);
        destinyHeader.setOnClickListener(v -> {
            destinyExpanded = !destinyExpanded;
            if (destinyExpanded) {
                expDestiny.setVisibility(View.VISIBLE);
                destinyArrow.setText("▼");
            } else {
                expDestiny.setVisibility(View.GONE);
                destinyArrow.setText("▶");
            }
        });

        updateHandler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateFullNinePalace();
                long nextUpdate = ((SystemClock.uptimeMillis() / UPDATE_INTERVAL) + 1) * UPDATE_INTERVAL;
                updateHandler.postAtTime(this, nextUpdate);
            }
        };

        updateFullNinePalace();
        long first = ((SystemClock.uptimeMillis() / UPDATE_INTERVAL) + 1) * UPDATE_INTERVAL;
        updateHandler.postAtTime(updateRunnable, first);
    }

    // 获取自定义时间（如果有）
    private Calendar getDisplayCalendar() {
        android.content.Intent intent = getIntent();
        if (intent != null && intent.hasExtra("custom_year")) {
            Calendar cal = Calendar.getInstance();
            cal.set(
                intent.getIntExtra("custom_year", cal.get(Calendar.YEAR)),
                intent.getIntExtra("custom_month", cal.get(Calendar.MONTH) + 1) - 1,
                intent.getIntExtra("custom_day", cal.get(Calendar.DAY_OF_MONTH)),
                intent.getIntExtra("custom_hour", cal.get(Calendar.HOUR_OF_DAY)),
                intent.getIntExtra("custom_minute", cal.get(Calendar.MINUTE)),
                0
            );
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        }
        return Calendar.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHandler.removeCallbacks(updateRunnable);
        updateFullNinePalace();
        long next = ((SystemClock.uptimeMillis() / UPDATE_INTERVAL) + 1) * UPDATE_INTERVAL;
        updateHandler.postAtTime(updateRunnable, next);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(updateRunnable);
    }

    private void updateFullNinePalace() {
        Calendar calendar = getDisplayCalendar();
        Date now = calendar.getTime();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String yearPillar = calculateYearPillar(year, month, day);
        String monthPillar = calculateMonthPillar(year, month, day, yearPillar.substring(0, 1));
        String dayPillar = calculateDayPillar(year, month, day);
        String timePillar = calculateTimePillar(hour, minute, dayPillar.substring(0, 1));

        // 根据节气确定阴阳遁和用局数
        String jieqi = getJieqi(year, month, day);
        
        // 计算落宫信息
        calculateAndSetPalaceData(yearPillar, monthPillar, dayPillar, timePillar, jieqi);

        // 更新各解读区域
        updateExplanation(yearPillar, monthPillar, dayPillar, timePillar, jieqi);
    }

    // 获取空亡
    private String getKongWang(String dayPillar) {
        String[] xunshouList = {"甲子", "甲戌", "甲申", "甲午", "甲辰", "甲寅"};
        java.util.Map<String, String> kongwangMap = new java.util.HashMap<>();
        kongwangMap.put("甲子", "戌亥");
        kongwangMap.put("甲戌", "申酉");
        kongwangMap.put("甲申", "午未");
        kongwangMap.put("甲午", "辰巳");
        kongwangMap.put("甲辰", "寅卯");
        kongwangMap.put("甲寅", "子丑");
        
        String dayGan = dayPillar.substring(0, 1);
        String dayZhi = dayPillar.substring(1, 2);
        String shiGanzhi = dayGan + dayZhi;
        int shiIndex = -1;
        for (int i = 0; i < LIUJIAZI.length; i++) {
            if (LIUJIAZI[i].equals(shiGanzhi)) {
                shiIndex = i;
                break;
            }
        }
        if (shiIndex == -1) shiIndex = 0;
        int xunIndex = shiIndex / 10;
        String xunshou = xunshouList[xunIndex];
        
        return kongwangMap.get(xunshou) != null ? kongwangMap.get(xunshou) : "--";
    }

    // 获取马星
    private String getMaXing(String dayPillar) {
        java.util.Map<String, String> maXingMap = new java.util.HashMap<>();
        maXingMap.put("申", "寅"); maXingMap.put("子", "午"); maXingMap.put("辰", "申");
        maXingMap.put("寅", "午"); maXingMap.put("午", "申"); maXingMap.put("戌", "子");
        maXingMap.put("巳", "亥"); maXingMap.put("酉", "巳"); maXingMap.put("丑", "酉");
        maXingMap.put("亥", "巳"); maXingMap.put("卯", "酉"); maXingMap.put("未", "亥");
        String dayZhi = dayPillar.substring(1, 2);
        return maXingMap.get(dayZhi) != null ? maXingMap.get(dayZhi) : "--";
    }

    private String calculateYearPillar(int year, int month, int day) {
        if (month < 2 || (month == 2 && day < 4)) {
            year = year - 1;
        }

        int baseYear = 1900;
        int baseIndex = 36;

        int yearDiff = year - baseYear;
        int yearIndex = (baseIndex + yearDiff) % 60;

        int yearGanIndex = yearIndex % 10;
        int yearZhiIndex = yearIndex % 12;

        String yearGan = TIANGAN[yearGanIndex];
        String yearZhi = DIZHI[yearZhiIndex];

        return yearGan + yearZhi;
    }

    private String getMonthZhi(int month, int day) {
        if (month == 2 && day >= 4) {
            return "寅";
        } else if (month == 2 && day < 4) {
            return "丑";
        }
        return MONTH_ZHI_MAP.get(month);
    }

    private String calculateMonthPillar(int year, int month, int day, String yearGan) {
        String monthZhi = getMonthZhi(month, day);
        String yinMonthGan = WUHUDUN.get(yearGan);
        if (yinMonthGan == null) {
            yinMonthGan = "丙";
        }
        int yinGanIndex = java.util.Arrays.asList(TIANGAN).indexOf(yinMonthGan);
        int monthZhiIndex = java.util.Arrays.asList(MONTH_ZHI_LIST).indexOf(monthZhi);
        int monthGanIndex = (yinGanIndex + monthZhiIndex) % 10;
        String monthGan = TIANGAN[monthGanIndex];
        return monthGan + monthZhi;
    }

    private String calculateDayPillar(int year, int month, int day) {
        try {
            java.util.Calendar targetCalendar = java.util.Calendar.getInstance();
            targetCalendar.set(year, month - 1, day);
            java.util.Calendar baseCalendar = java.util.Calendar.getInstance();
            baseCalendar.set(1900, 0, 1);
            long targetTime = targetCalendar.getTimeInMillis();
            long baseTime = baseCalendar.getTimeInMillis();
            long daysDiff = (targetTime - baseTime) / (1000 * 60 * 60 * 24);
            int baseGanzhiIndex = 10;
            int ganzhiIndex = (baseGanzhiIndex + (int)daysDiff) % 60;
            return LIUJIAZI[ganzhiIndex];
        } catch (Exception e) {
            e.printStackTrace();
            return "甲午";
        }
    }

    private String calculateTimePillar(int hour, int minute, String dayGan) {
        int adjustedHour = hour;
        if (minute >= 45) {
            adjustedHour = (hour + 1) % 24;
        }

        String hourZhi = "子";
        int hourZhiIndex = 0;
        String[][] shizhiTable = {
            {"子", "23", "1", "0"},
            {"丑", "1", "3", "1"},
            {"寅", "3", "5", "2"},
            {"卯", "5", "7", "3"},
            {"辰", "7", "9", "4"},
            {"巳", "9", "11", "5"},
            {"午", "11", "13", "6"},
            {"未", "13", "15", "7"},
            {"申", "15", "17", "8"},
            {"酉", "17", "19", "9"},
            {"戌", "19", "21", "10"},
            {"亥", "21", "23", "11"}
        };

        for (String[] entry : shizhiTable) {
            String zhi = entry[0];
            int start = Integer.parseInt(entry[1]);
            int end = Integer.parseInt(entry[2]);
            int index = Integer.parseInt(entry[3]);

            if (start <= end) {
                if (start <= adjustedHour && adjustedHour < end) {
                    hourZhi = zhi;
                    hourZhiIndex = index;
                    break;
                }
            } else {
                if (adjustedHour >= start || adjustedHour < end) {
                    hourZhi = zhi;
                    hourZhiIndex = index;
                    break;
                }
            }
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

    private boolean isYangDun(String monthZhi) {
        if (monthZhi == null) {
            return true;
        }
        int zhiIndex = java.util.Arrays.asList(DIZHI).indexOf(monthZhi);
        // 2-7月为阳遁，其他月份为阴遁
        // 寅(2)=1月, 卯(3)=2月, 辰(4)=3月, 巳(5)=4月, 午(6)=5月, 未(7)=6月, 申(8)=7月
        return zhiIndex >= 2 && zhiIndex <= 8; // 1月(寅)到7月(申)为阳遁
    }

    private int getJuShu(String monthZhi, boolean isYangDun) {
        if (monthZhi == null) {
            return 1;
        }
        int zhiIndex = java.util.Arrays.asList(DIZHI).indexOf(monthZhi);
        // 直接使用月份对应传统用局表
        int[] MONTH_JU = {1, 8, 1, 3, 4, 6, 9, 2, 9, 7, 6, 4};
        return MONTH_JU[zhiIndex];
    }

    private String[] calculateAndSetPalaceData(String yearPillar, String monthPillar, String dayPillar, String timePillar, String jieqi) {
        // 根据节气确定阴阳遁和用局数
        boolean isYangDun = isYangDunByJieqi(jieqi);
        int ju = getJuShuByJieqi(jieqi);

        String timeGan = timePillar != null && timePillar.length() >= 1 ? timePillar.substring(0, 1) : "甲";
        String timeZhi = timePillar != null && timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";
        String dayGan = dayPillar != null && dayPillar.length() >= 1 ? dayPillar.substring(0, 1) : "甲";

        // 1. 排地盘（根据局数和阴阳遁）
        int diPanJu = isYangDun ? ju : -ju;
        String[] diPanTianGan = arrangeDiPanTianGanStandard(diPanJu);

        // 2. 确定旬首、值符、值使
        Object[] xunShouInfo = getXunShouInfoStandard(timeGan, timeZhi);
        String xunShou = (String) xunShouInfo[0];
        String zhiFuStar = (String) xunShouInfo[1];
        String zhiShiDoor = (String) xunShouInfo[2];

        // 3. 值符落宫：时干在地盘的位置
        int zhiFuPalace = getShiGanPosition(diPanTianGan, timeGan);

        // 4. 值使落宫：从旬首宫位顺/逆数时支步数
        int xunShouPalace = getXunShouPalace(xunShou);
        int zhiShiPalace = getZhiShiPalace(xunShouPalace, timeZhi, isYangDun);

        // 5. 排九星
        String[] nineStars = arrangeNineStarsStandard(zhiFuStar, zhiFuPalace, isYangDun);

        // 6. 排八门
        String[] eightDoors = arrangeEightDoorsStandard(zhiShiDoor, zhiShiPalace, isYangDun);

        // 7. 排天盘
        String[] tianPanTianGan = arrangeTianPanTianGanStandard(diPanTianGan, timeGan, zhiFuPalace, isYangDun);

        // 8. 排八神
        String[] eightGods = arrangeEightGodsStandard(zhiFuPalace, isYangDun);

        // 9. 判断旺衰
        String[] wangCui = calculateWangCui(dayGan);

        String[] PALACE_NAMES = {"坎一", "坤二", "震三", "巽四", "中五", "乾六", "兑七", "艮八", "离九"};
        String[] DIRECTIONS = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};
        String[] GUA_SYMBOLS = {"☵", "☷", "☳", "☴", "", "☰", "☱", "☶", "☲"};
        String[] DIRECTION_SYMBOLS = {"↑", "↙", "→", "↘", "●", "↖", "←", "↗", "↓"};

        // 查找日干和时干的落宫
        int riGanPalace = -1;
        int shiGanPalace = -1;
        for (int i = 0; i < 9; i++) {
            if (tianPanTianGan[i].equals(dayGan)) {
                riGanPalace = i;
            }
            if (tianPanTianGan[i].equals(timeGan)) {
                shiGanPalace = i;
            }
        }

        String[][] palaceData = new String[9][4];
        String[] luckData = new String[9];
        for (int i = 0; i < 9; i++) {
            String star = nineStars[i];
            String door = eightDoors[i];
            String god = eightGods[i];
            String tianGan = tianPanTianGan[i];
            String diGan = diPanTianGan[i];
            String luck = getLuckSymbol(star, door, god, wangCui[i]);

            palaceData[i][0] = PALACE_NAMES[i] + " " + DIRECTION_SYMBOLS[i] + " " + DIRECTIONS[i];
            palaceData[i][1] = god + " " + star;
            palaceData[i][2] = (door != null && !door.isEmpty() ? door : " ") + " " + tianGan + "/" + diGan;
            palaceData[i][3] = luck + " " + wangCui[i];
            
            luckData[i] = luck;
        }

        fullNinePalacePanel.setPalaceData(palaceData);
        fullNinePalacePanel.setLuckData(luckData);
        fullNinePalacePanel.setBrightness(0.9f);

        // 返回落宫信息：[值符落宫, 值使落宫, 日干落宫, 时干落宫]
        String[] palaceInfo = new String[4];
        palaceInfo[0] = PALACE_NAMES[zhiFuPalace];
        palaceInfo[1] = PALACE_NAMES[zhiShiPalace];
        palaceInfo[2] = riGanPalace >= 0 ? PALACE_NAMES[riGanPalace] : "--";
        palaceInfo[3] = shiGanPalace >= 0 ? PALACE_NAMES[shiGanPalace] : "--";
        
        return palaceInfo;
    }

    // ==================== 标准算法方法 ====================
    
    // 根据节气判断阴阳遁
    private boolean isYangDunByJieqi(String jieqi) {
        String[] yangDunJieqi = {"冬至", "小寒", "大寒", "立春", "雨水", "惊蛰", 
                                "春分", "清明", "谷雨", "立夏", "小满", "芒种"};
        for (String jq : yangDunJieqi) {
            if (jq.equals(jieqi)) {
                return true;
            }
        }
        return false;
    }
    
    // 根据节气确定用局数
    private int getJuShuByJieqi(String jieqi) {
        java.util.Map<String, Integer> jieqiJuMap = new java.util.HashMap<>();
        jieqiJuMap.put("冬至", 1); jieqiJuMap.put("小寒", 2); jieqiJuMap.put("大寒", 3);
        jieqiJuMap.put("立春", 8); jieqiJuMap.put("雨水", 9); jieqiJuMap.put("惊蛰", 1);
        jieqiJuMap.put("春分", 3); jieqiJuMap.put("清明", 4); jieqiJuMap.put("谷雨", 5);
        jieqiJuMap.put("立夏", 4); jieqiJuMap.put("小满", 5); jieqiJuMap.put("芒种", 6);
        jieqiJuMap.put("夏至", 9); jieqiJuMap.put("小暑", 8); jieqiJuMap.put("大暑", 7);
        jieqiJuMap.put("立秋", 2); jieqiJuMap.put("处暑", 1); jieqiJuMap.put("白露", 9);
        jieqiJuMap.put("秋分", 7); jieqiJuMap.put("寒露", 6); jieqiJuMap.put("霜降", 5);
        jieqiJuMap.put("立冬", 6); jieqiJuMap.put("小雪", 5); jieqiJuMap.put("大雪", 4);
        return jieqiJuMap.getOrDefault(jieqi, 1);
    }
    
    // 获取节气
    private String getJieqi(int year, int month, int day) {
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
            
            if (startMonth == endMonth) {
                if (month == startMonth && day >= startDay && day <= endDay) {
                    return jieqiName;
                }
            } else {
                if ((month == startMonth && day >= startDay) ||
                    (month == endMonth && day <= endDay)) {
                    return jieqiName;
                }
            }
        }
        return "立春";
    }
    
    // 排地盘天干（标准算法）
    private String[] arrangeDiPanTianGanStandard(int ju) {
        String[] result = new String[9];
        String[] tianGanOrder = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
        
        if (ju > 0) {
            int startPos = ju - 1;
            for (int i = 0; i < 9; i++) {
                int pos = (startPos + i) % 9;
                result[pos] = tianGanOrder[i];
            }
        } else {
            int yinJu = -ju;
            int startPos = 9 - yinJu;
            for (int i = 0; i < 9; i++) {
                int pos = (startPos - i + 9) % 9;
                result[pos] = tianGanOrder[i];
            }
        }
        
        return result;
    }
    
    // 获取时干位置
    private int getShiGanPosition(String[] diPan, String shiGan) {
        for (int i = 0; i < 9; i++) {
            if (diPan[i].equals(shiGan)) {
                return i;
            }
        }
        return 0;
    }
    
    // 获取旬首宫位
    private int getXunShouPalace(String xunShou) {
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        map.put("甲子", 0); map.put("甲戌", 1); map.put("甲申", 2);
        map.put("甲午", 3); map.put("甲辰", 4); map.put("甲寅", 5);
        return map.getOrDefault(xunShou, 0);
    }
    
    // 计算值使落宫
    private int getZhiShiPalace(int xunShouPalace, String timeZhi, boolean isYangDun) {
        int zhiIndex = java.util.Arrays.asList(DIZHI).indexOf(timeZhi);
        int shiCheng = (zhiIndex + 1) % 12;
        if (shiCheng == 0) shiCheng = 12;
        
        if (isYangDun) {
            return (xunShouPalace + shiCheng - 1) % 9;
        } else {
            int result = (xunShouPalace - shiCheng + 1 + 9) % 9;
            return result < 0 ? result + 9 : result;
        }
    }
    
    // 获取旬首信息（标准算法）
    private Object[] getXunShouInfoStandard(String timeGan, String timeZhi) {
        String shiGanzhi = timeGan + timeZhi;
        int shiIdx = -1;
        for (int i = 0; i < LIUJIAZI.length; i++) {
            if (LIUJIAZI[i].equals(shiGanzhi)) {
                shiIdx = i;
                break;
            }
        }
        
        String[] xunshouList = {"甲子", "甲戌", "甲申", "甲午", "甲辰", "甲寅"};
        String xunShou = "甲子";
        if (shiIdx >= 0) {
            int xunIndex = shiIdx / 10;
            xunShou = xunshouList[xunIndex];
        }
        
        java.util.Map<String, String> zhiFuMap = new java.util.HashMap<>();
        zhiFuMap.put("甲子", "天蓬"); zhiFuMap.put("甲戌", "天芮"); zhiFuMap.put("甲申", "天冲");
        zhiFuMap.put("甲午", "天辅"); zhiFuMap.put("甲辰", "天禽"); zhiFuMap.put("甲寅", "天心");
        
        java.util.Map<String, String> zhiShiMap = new java.util.HashMap<>();
        zhiShiMap.put("甲子", "休"); zhiShiMap.put("甲戌", "生"); zhiShiMap.put("甲申", "伤");
        zhiShiMap.put("甲午", "杜"); zhiShiMap.put("甲辰", "景"); zhiShiMap.put("甲寅", "死");
        
        String zhiFuStar = zhiFuMap.getOrDefault(xunShou, "天蓬");
        String zhiShiDoor = zhiShiMap.getOrDefault(xunShou, "休");
        
        return new Object[]{xunShou, zhiFuStar, zhiShiDoor};
    }
    
    // 排九星（标准算法）
    private String[] arrangeNineStarsStandard(String zhiFuStar, int zhiFuPalace, boolean isYangDun) {
        String[] nineStars = new String[9];
        String[] jiuxingOrder = {"天蓬", "天芮", "天冲", "天辅", "天禽", "天心", "天柱", "天任", "天英"};
        
        int zhiFuIndex = -1;
        for (int i = 0; i < jiuxingOrder.length; i++) {
            if (jiuxingOrder[i].equals(zhiFuStar)) {
                zhiFuIndex = i;
                break;
            }
        }
        if (zhiFuIndex == -1) zhiFuIndex = 0;
        
        if (isYangDun) {
            for (int i = 0; i < 9; i++) {
                int pos = (zhiFuPalace + i) % 9;
                nineStars[pos] = jiuxingOrder[(zhiFuIndex + i) % 9];
            }
        } else {
            for (int i = 0; i < 9; i++) {
                int pos = (zhiFuPalace - i + 9) % 9;
                nineStars[pos] = jiuxingOrder[(zhiFuIndex + i) % 9];
            }
        }
        return nineStars;
    }
    
    // 排八门（标准算法）
    private String[] arrangeEightDoorsStandard(String zhiShiDoor, int zhiShiPalace, boolean isYangDun) {
        String[] eightDoors = new String[9];
        String[] bamenOrder = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
        
        int zhiShiIndex = -1;
        for (int i = 0; i < bamenOrder.length; i++) {
            if (bamenOrder[i].equals(zhiShiDoor)) {
                zhiShiIndex = i;
                break;
            }
        }
        if (zhiShiIndex == -1) zhiShiIndex = 0;
        
        int currentDoorIndex = zhiShiIndex;
        for (int i = 0; i < 9; i++) {
            int pos = isYangDun ? (zhiShiPalace + i) % 9 : (zhiShiPalace - i + 9) % 9;
            if (pos == 4) {
                eightDoors[pos] = "";
            } else {
                eightDoors[pos] = bamenOrder[currentDoorIndex];
                currentDoorIndex = (currentDoorIndex + 1) % 8;
            }
        }
        return eightDoors;
    }
    
    // 排天盘（标准算法）
    private String[] arrangeTianPanTianGanStandard(String[] diPan, String timeGan, int zhiFuPalace, boolean isYangDun) {
        String[] tianPan = new String[9];
        String[] tianGanOrder = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
        
        int shiGanIndex = -1;
        for (int i = 0; i < tianGanOrder.length; i++) {
            if (tianGanOrder[i].equals(timeGan)) {
                shiGanIndex = i;
                break;
            }
        }
        if (shiGanIndex == -1) shiGanIndex = 0;
        
        if (isYangDun) {
            for (int i = 0; i < 9; i++) {
                int pos = (zhiFuPalace + i) % 9;
                tianPan[pos] = tianGanOrder[(shiGanIndex + i) % 9];
            }
        } else {
            for (int i = 0; i < 9; i++) {
                int pos = (zhiFuPalace - i + 9) % 9;
                tianPan[pos] = tianGanOrder[(shiGanIndex + i) % 9];
            }
        }
        return tianPan;
    }
    
    // 排八神（标准算法）
    private String[] arrangeEightGodsStandard(int zhiFuPalace, boolean isYangDun) {
        String[] eightGods = new String[9];
        String[] yangShenOrder = {"值符", "螣蛇", "太阴", "六合", "白虎", "玄武", "九地", "九天"};
        String[] yinShenOrder = {"值符", "九天", "九地", "玄武", "白虎", "六合", "太阴", "螣蛇"};
        
        String[] bashenOrder = isYangDun ? yangShenOrder : yinShenOrder;
        
        if (isYangDun) {
            int currentGodIndex = 0;
            int pos = zhiFuPalace;
            while (currentGodIndex < 8) {
                if (pos != 4) {
                    eightGods[pos] = bashenOrder[currentGodIndex];
                    currentGodIndex++;
                }
                pos = (pos + 1) % 9;
            }
        } else {
            int currentGodIndex = 0;
            int pos = zhiFuPalace;
            while (currentGodIndex < 8) {
                if (pos != 4) {
                    eightGods[pos] = bashenOrder[currentGodIndex];
                    currentGodIndex++;
                }
                pos = (pos - 1 + 9) % 9;
            }
        }
        eightGods[4] = "";
        return eightGods;
    }
    
    // 计算旺衰
    private String[] calculateWangCui(String dayGan) {
        String[] wangCui = new String[9];
        String[] PALACE_WUXING = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
        String riGanWuXing = getWuXing(dayGan);
        
        for (int i = 0; i < 9; i++) {
            String gongWuXing = PALACE_WUXING[i];
            if (riGanWuXing.equals(gongWuXing)) {
                wangCui[i] = "旺";
            } else if (isSheng(gongWuXing, riGanWuXing)) {
                wangCui[i] = "相";
            } else if (isSheng(riGanWuXing, gongWuXing)) {
                wangCui[i] = "休";
            } else if (isKe(gongWuXing, riGanWuXing)) {
                wangCui[i] = "囚";
            } else if (isKe(riGanWuXing, gongWuXing)) {
                wangCui[i] = "死";
            } else {
                wangCui[i] = "平";
            }
        }
        return wangCui;
    }
    
    // 获取天干五行
    private String getWuXing(String gan) {
        if (gan == null) return "土";
        switch (gan) {
            case "甲": case "乙": return "木";
            case "丙": case "丁": return "火";
            case "戊": case "己": return "土";
            case "庚": case "辛": return "金";
            case "壬": case "癸": return "水";
            default: return "土";
        }
    }
    
    // 判断五行生克关系（a生b）
    private boolean isSheng(String a, String b) {
        return (a.equals("木") && b.equals("火")) ||
               (a.equals("火") && b.equals("土")) ||
               (a.equals("土") && b.equals("金")) ||
               (a.equals("金") && b.equals("水")) ||
               (a.equals("水") && b.equals("木"));
    }
    
    // 判断五行生克关系（a克b）
    private boolean isKe(String a, String b) {
        return (a.equals("木") && b.equals("土")) ||
               (a.equals("火") && b.equals("金")) ||
               (a.equals("土") && b.equals("水")) ||
               (a.equals("金") && b.equals("木")) ||
               (a.equals("水") && b.equals("火"));
    }

    private String getLuckSymbol(String star, String door, String god, String wangCui) {
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
        
        if (door != null && !door.isEmpty()) {
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
        
        if (god != null && !god.isEmpty()) {
            if (god.equals("值符")) {
                score += 3;
            } else if (god.equals("九天") || god.equals("太阴") || god.equals("六合")) {
                score += 2;
            } else if (god.equals("九地")) {
                score += 0;
            } else if (god.equals("螣蛇")) {
                score -= 1;
            } else if (god.equals("白虎") || god.equals("玄武")) {
                score -= 3;
            }
        }
        
        if (wangCui != null) {
            if (wangCui.equals("旺")) {
                score += 3;
            } else if (wangCui.equals("相")) {
                score += 2;
            } else if (wangCui.equals("休")) {
                score += 0;
            } else if (wangCui.equals("囚")) {
                score -= 2;
            } else if (wangCui.equals("死")) {
                score -= 3;
            }
        }
        
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
    
    private String getLuckSymbol(String star, String door) {
        return getLuckSymbol(star, door, null, null);
    }
    
    private String getStarLuckColor(String star) {
        if (star == null) return "#FFD700";
        switch (star) {
            case "天蓬": return "#90EE90";
            case "天任": return "#90EE90";
            case "天冲": return "#FF6B6B";
            case "天辅": return "#90EE90";
            case "天英": return "#FF6B6B";
            case "天芮": return "#FFA500";
            case "天柱": return "#FFD700";
            case "天心": return "#90EE90";
            case "天禽": return "#90EE90";
            default: return "#FFD700";
        }
    }
    
    private String getDoorLuckColor(String door) {
        if (door == null) return "#FFD700";
        switch (door) {
            case "开": return "#90EE90";
            case "休": return "#90EE90";
            case "生": return "#90EE90";
            case "伤": return "#FF6B6B";
            case "杜": return "#FFD700";
            case "景": return "#FFD700";
            case "死": return "#FF6B6B";
            case "惊": return "#FF6B6B";
            default: return "#FFD700";
        }
    }
    
    private String getRiShiRelationColor(String relation) {
        if (relation == null) return "#FFD700";
        if (relation.contains("生") || relation.contains("合") || relation.contains("比")) {
            return "#90EE90";
        } else if (relation.contains("克") || relation.contains("冲")) {
            return "#FF6B6B";
        } else {
            return "#FFD700";
        }
    }

    private void updateExplanation(String yearPillar, String monthPillar, String dayPillar, String timePillar, String jieqi) {
        boolean isYangDun = isYangDunByJieqi(jieqi);
        int ju = getJuShuByJieqi(jieqi);
        
        String timeGan = timePillar != null && timePillar.length() >= 1 ? timePillar.substring(0, 1) : "甲";
        String timeZhi = timePillar != null && timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";
        String dayGan = dayPillar != null && dayPillar.length() >= 1 ? dayPillar.substring(0, 1) : "甲";

        int diPanJu = isYangDun ? ju : -ju;
        String[] diPanTianGan = arrangeDiPanTianGanStandard(diPanJu);
        Object[] xunShouInfo = getXunShouInfoStandard(timeGan, timeZhi);
        String zhiFuStar = (String) xunShouInfo[1];
        String zhiShiDoor = (String) xunShouInfo[2];
        
        int zhiFuPalace = getShiGanPosition(diPanTianGan, timeGan);
        String[] nineStars = arrangeNineStarsStandard(zhiFuStar, zhiFuPalace, isYangDun);
        
        String xunShou = (String) xunShouInfo[0];
        int xunShouPalace = getXunShouPalace(xunShou);
        int zhiShiPalace = getZhiShiPalace(xunShouPalace, timeZhi, isYangDun);
        String[] eightDoors = arrangeEightDoorsStandard(zhiShiDoor, zhiShiPalace, isYangDun);
        String[] eightGods = arrangeEightGodsStandard(zhiFuPalace, isYangDun);
        String zhiFuGod = eightGods[zhiFuPalace];
        String[] wangCui = calculateWangCui(dayGan);
        
        String[] DIRECTIONS = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};
        String[] PALACE_NAMES = {"坎一", "坤二", "震三", "巽四", "中五", "乾六", "兑七", "艮八", "离九"};
        
        String kongWang = getKongWang(dayPillar);
        String maXing = getMaXing(dayPillar);
        String riGanWuXing = getWuXing(dayGan);
        String shiGanWuXing = getWuXing(timeGan);
        String dayZhi = dayPillar.substring(1, 2);
        
        String[] tianPan = arrangeTianPanTianGanStandard(diPanTianGan, timeGan, zhiFuPalace, isYangDun);
        
        int riGanPalace = -1;
        int shiGanPalace = -1;
        for (int i = 0; i < 9; i++) {
            if (tianPan[i].equals(dayGan)) {
                riGanPalace = i;
            }
            if (tianPan[i].equals(timeGan)) {
                shiGanPalace = i;
            }
        }
        
        StringBuilder sbBasic = new StringBuilder();
        sbBasic.append("【节气】").append(jieqi).append(" · ").append(isYangDun ? "阳遁" : "阴遁").append(ju).append("局\n\n");
        
        sbBasic.append("📆 旬首 ").append(xunShou).append(" · 空亡 ").append(kongWang).append("\n");
        sbBasic.append("   ").append(getKongWangExplanation(kongWang)).append("\n\n");
        
        sbBasic.append("🐴 马星 ").append(maXing).append("\n");
        sbBasic.append("   ").append(getMaXingExplanation(maXing)).append("\n\n");
        
        sbBasic.append("⚡ 旺衰判断\n");
        sbBasic.append("   日干").append(dayGan).append("属").append(riGanWuXing).append("，").append(getWangCuiDescription(wangCui, riGanPalace)).append("\n");
        expBasic.setText(sbBasic.toString());
        
        expSummary.setText(getQiMenSummary(yearPillar, monthPillar, dayPillar, timePillar, zhiFuStar, zhiShiDoor));
        
        expDestiny.setText(android.text.Html.fromHtml(getDestinyOverview(yearPillar, monthPillar, dayPillar, timePillar), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        StringBuilder sbZhifuZhishi = new StringBuilder();
        String zhiFuColor = getStarLuckColor(zhiFuStar);
        String zhiShiColor = getDoorLuckColor(zhiShiDoor);
        sbZhifuZhishi.append("值符：<font color='").append(zhiFuColor).append("'>").append(zhiFuStar).append("星</font>").append("落").append(PALACE_NAMES[zhiFuPalace]).append("<br/>");
        sbZhifuZhishi.append("值使：<font color='").append(zhiShiColor).append("'>").append(zhiShiDoor).append("门</font>").append("落").append(PALACE_NAMES[zhiShiPalace]);
        expZhifuZhishi.setText(android.text.Html.fromHtml(sbZhifuZhishi.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        StringBuilder sbGan = new StringBuilder();
        sbGan.append("日干 ").append(dayGan).append(dayZhi).append("(").append(riGanWuXing).append(")").append("落").append(riGanPalace >= 0 ? PALACE_NAMES[riGanPalace] : "--").append("<br/>");
        sbGan.append("时干 ").append(timeGan).append(timeZhi).append("(").append(shiGanWuXing).append(")").append("落").append(shiGanPalace >= 0 ? PALACE_NAMES[shiGanPalace] : "--").append("<br/>");
        String riShiRelation = getRiShiRelationship(dayGan, timeGan);
        String riShiColor = getRiShiRelationColor(riShiRelation);
        sbGan.append("日时关系：<font color='").append(riShiColor).append("'>").append(riShiRelation).append("</font>");
        expGan.setText(android.text.Html.fromHtml(sbGan.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        String[][] tianDiPanData = new String[9][2];
        String[][] nineStarsData = new String[9][2];
        String[][] eightDoorsData = new String[9][2];
        String[][] godsData = new String[9][2];
        String[] luckData = new String[9];
        
        for (int i = 0; i < 9; i++) {
            tianDiPanData[i][0] = PALACE_NAMES[i];
            tianDiPanData[i][1] = tianPan[i] + "/" + diPanTianGan[i];
            
            nineStarsData[i][0] = PALACE_NAMES[i];
            nineStarsData[i][1] = nineStars[i] + "星";
            
            String door = eightDoors[i] != null && !eightDoors[i].isEmpty() ? eightDoors[i] : "";
            eightDoorsData[i][0] = PALACE_NAMES[i];
            eightDoorsData[i][1] = door + "门";
            
            String god = eightGods[i] != null && !eightGods[i].isEmpty() ? eightGods[i] : "";
            godsData[i][0] = PALACE_NAMES[i];
            godsData[i][1] = god;
            
            luckData[i] = getLuckSymbol(nineStars[i], door, god, wangCui[i]);
        }
        
        expTianDiPan.setPalaceData(tianDiPanData);
        expTianDiPan.setLuckData(luckData);
        expNineStars.setPalaceData(nineStarsData);
        expNineStars.setLuckData(luckData);
        expEightDoors.setPalaceData(eightDoorsData);
        expEightDoors.setLuckData(luckData);
        expGods.setPalaceData(godsData);
        expGods.setLuckData(luckData);
        
        StringBuilder sbDirection = new StringBuilder();
        StringBuilder luckyDirections = new StringBuilder();
        StringBuilder unluckyDirections = new StringBuilder();
        StringBuilder neutralDirections = new StringBuilder();
        StringBuilder wangPositions = new StringBuilder();
        StringBuilder xiuPositions = new StringBuilder();
        StringBuilder qiuPositions = new StringBuilder();
        StringBuilder siPositions = new StringBuilder();
        StringBuilder xiangPositions = new StringBuilder();
        
        for (int i = 0; i < 9; i++) {
            String door = eightDoors[i];
            if (door == null || door.isEmpty()) continue;
            if (door.equals("开") || door.equals("休") || door.equals("生")) {
                if (luckyDirections.length() > 0) luckyDirections.append("、");
                luckyDirections.append(DIRECTIONS[i]);
            } else if (door.equals("死") || door.equals("惊") || door.equals("伤")) {
                if (unluckyDirections.length() > 0) unluckyDirections.append("、");
                unluckyDirections.append(DIRECTIONS[i]);
            } else {
                if (neutralDirections.length() > 0) neutralDirections.append("、");
                neutralDirections.append(DIRECTIONS[i]);
            }
            if (i != 4) {
                String wc = wangCui[i];
                if (wc.equals("旺")) {
                    if (wangPositions.length() > 0) wangPositions.append("、");
                    wangPositions.append(DIRECTIONS[i]);
                } else if (wc.equals("相")) {
                    if (xiangPositions.length() > 0) xiangPositions.append("、");
                    xiangPositions.append(DIRECTIONS[i]);
                } else if (wc.equals("休")) {
                    if (xiuPositions.length() > 0) xiuPositions.append("、");
                    xiuPositions.append(DIRECTIONS[i]);
                } else if (wc.equals("囚")) {
                    if (qiuPositions.length() > 0) qiuPositions.append("、");
                    qiuPositions.append(DIRECTIONS[i]);
                } else if (wc.equals("死")) {
                    if (siPositions.length() > 0) siPositions.append("、");
                    siPositions.append(DIRECTIONS[i]);
                }
            }
        }
        
        if (luckyDirections.length() > 0) sbDirection.append("✅ <font color='#90EE90'>吉方</font>：").append(luckyDirections).append("<br/>");
        if (neutralDirections.length() > 0) sbDirection.append("⚪ <font color='#FFD700'>平方</font>：").append(neutralDirections).append("<br/>");
        if (unluckyDirections.length() > 0) sbDirection.append("❌ <font color='#FF6B6B'>凶方</font>：").append(unluckyDirections).append("<br/>");
        
        sbDirection.append("<br/>");
        sbDirection.append("<b>📖 吉凶缘由：</b><br/>");
        for (int i = 0; i < 9; i++) {
            String door = eightDoors[i];
            if (door == null || door.isEmpty()) continue;
            
            String doorColor = "#FFD700";
            switch(door) {
                case "开": doorColor = "#90EE90"; break;
                case "休": doorColor = "#90EE90"; break;
                case "生": doorColor = "#90EE90"; break;
                case "伤": doorColor = "#FF6B6B"; break;
                case "杜": doorColor = "#FFD700"; break;
                case "景": doorColor = "#FFD700"; break;
                case "死": doorColor = "#FF6B6B"; break;
                case "惊": doorColor = "#FF6B6B"; break;
            }
            sbDirection.append("  ").append(DIRECTIONS[i]).append("方").append("<font color='").append(doorColor).append("'>").append(door).append("门</font>").append("：").append(getDoorDescription(door)).append("<br/>");
        }
        
        sbDirection.append("<br/>");
        sbDirection.append("<b>⚡ 旺相休囚死：</b><br/>");
        sbDirection.append("  <font color='#90EE90'>旺</font>：得时令之气，气势旺盛，百事顺遂<br/>");
        sbDirection.append("  <font color='#90EE90'>相</font>：得生扶之气，次吉，事多有成<br/>");
        sbDirection.append("  <font color='#FFD700'>休</font>：得休息之气，平和安宁，不宜进取<br/>");
        sbDirection.append("  <font color='#FFA500'>囚</font>：被克制之气，困顿受阻，事多不利<br/>");
        sbDirection.append("  <font color='#FF6B6B'>死</font>：得处死之气，衰败不利，诸事不宜<br/>");
        sbDirection.append("<br/>");
        if (wangPositions.length() > 0) sbDirection.append("🔥 <font color='#90EE90'>旺</font>方：").append(wangPositions).append("<br/>");
        if (xiangPositions.length() > 0) sbDirection.append("🌿 <font color='#90EE90'>相</font>方：").append(xiangPositions).append("<br/>");
        if (xiuPositions.length() > 0) sbDirection.append("😌 <font color='#FFD700'>休</font>方：").append(xiuPositions).append("<br/>");
        if (qiuPositions.length() > 0) sbDirection.append("🔒 <font color='#FFA500'>囚</font>方：").append(qiuPositions).append("<br/>");
        if (siPositions.length() > 0) sbDirection.append("💀 <font color='#FF6B6B'>死</font>方：").append(siPositions).append("</font>");
        expDirection.setText(android.text.Html.fromHtml(sbDirection.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        StringBuilder sbYiJi = new StringBuilder();
        sbYiJi.append("✅ 宜：");
        String[] yiItems = getYiActivitiesDetailed(zhiFuStar, zhiShiDoor);
        for (int i = 0; i < Math.min(yiItems.length, 5); i++) {
            if (i > 0) {
                sbYiJi.append("、");
            }
            sbYiJi.append(yiItems[i]);
        }
        sbYiJi.append("\n");
        sbYiJi.append("❌ 忌：");
        String[] jiItems = getJiActivitiesDetailed(zhiFuStar, zhiShiDoor);
        for (int i = 0; i < Math.min(jiItems.length, 5); i++) {
            if (i > 0) {
                sbYiJi.append("、");
            }
            sbYiJi.append(jiItems[i]);
        }
        expYiJi.setText(sbYiJi.toString());
        
        String riGanLuck = riGanPalace >= 0 ? luckData[riGanPalace] : "未知";
        String shiGanLuck = shiGanPalace >= 0 ? luckData[shiGanPalace] : "未知";
        
        StringBuilder sbLife = new StringBuilder();
        String doorPrefix = "值使" + zhiShiDoor + "门";
        sbLife.append(doorPrefix).append("\n");
        sbLife.append("👤 日干").append(dayGan).append("(").append(riGanWuXing).append(")").append("落").append(riGanPalace >= 0 ? PALACE_NAMES[riGanPalace] : "未知宫").append("(").append(riGanLuck).append(")\n");
        sbLife.append("⏰ 时干").append(timeGan).append("(").append(shiGanWuXing).append(")").append("落").append(shiGanPalace >= 0 ? PALACE_NAMES[shiGanPalace] : "未知宫").append("(").append(shiGanLuck).append(")\n");
        sbLife.append("🔗 日时关系：").append(getRiShiRelationship(dayGan, timeGan)).append("\n");
        sbLife.append("\n");
        
        sbLife.append("📋 生活提示\n\n");
        sbLife.append("💼 事业：").append(getCareerAdvice(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("💰 财运：").append(getWealthAdvice(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("💪 健康：").append(getHealthAdvice(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("💕 感情：").append(getRelationshipAdvice(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("📚 学业：").append(getStudyAdvice(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("🚗 出行：").append(getTravelAdvice(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("🍽️ 饮食：").append(getDietAdvice(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("👥 社交：").append(getSocialAdvice(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("🧘 心态：").append(getMindAdvice(zhiShiDoor, zhiFuStar)).append("\n");
        expLife.setText(sbLife.toString());
        
        String[][] palacesData = new String[9][3];
        for (int i = 0; i < 9; i++) {
            String doorText = eightDoors[i] != null && !eightDoors[i].isEmpty() ? eightDoors[i] + "门" : "";
            String godText = eightGods[i] != null && !eightGods[i].isEmpty() ? eightGods[i] : "";
            palacesData[i][0] = PALACE_NAMES[i];
            palacesData[i][1] = nineStars[i] + "星" + (doorText.isEmpty() ? "" : doorText);
            palacesData[i][2] = godText + tianPan[i] + "/" + diPanTianGan[i] + wangCui[i];
        }
        expPalaces.setPalaceData(palacesData);
        expPalaces.setLuckData(luckData);
        
        String[] palaceTips = new String[9];
        for (int i = 0; i < 9; i++) {
            palaceTips[i] = getPalaceTip(i, PALACE_NAMES[i], nineStars[i], eightDoors[i], eightGods[i]);
        }
        expPalaces.setPalaceTips(palaceTips);
        
        StringBuilder sbTime = new StringBuilder();
        sbTime.append(getShichenName(timeZhi)).append(" ").append(timeGan).append(timeZhi).append("(").append(shiGanWuXing).append(")\n");
        sbTime.append(getTimeFortune(timeZhi, zhiShiDoor, zhiFuStar));
        expTime.setText(sbTime.toString());
        
        expOverall.setText(getOverallAdviceSimple(isYangDun, ju, zhiFuStar, zhiShiDoor));
        
        if (expTianDiPanDesc != null) expTianDiPanDesc.setText(android.text.Html.fromHtml(getTianDiPanDesc(PALACE_NAMES, nineStars, PALACE_NAMES), android.text.Html.FROM_HTML_MODE_LEGACY));
        if (expNineStarsDesc != null) expNineStarsDesc.setText(android.text.Html.fromHtml(getNineStarsDesc(nineStars), android.text.Html.FROM_HTML_MODE_LEGACY));
        if (expEightDoorsDesc != null) expEightDoorsDesc.setText(android.text.Html.fromHtml(getEightDoorsDesc(eightDoors), android.text.Html.FROM_HTML_MODE_LEGACY));
        if (expGodsDesc != null) expGodsDesc.setText(android.text.Html.fromHtml(getEightGodsDesc(eightGods), android.text.Html.FROM_HTML_MODE_LEGACY));
        if (expPalacesDesc != null) expPalacesDesc.setText(android.text.Html.fromHtml(getPalacesDesc(PALACE_NAMES, nineStars, eightDoors, eightGods, luckData), android.text.Html.FROM_HTML_MODE_LEGACY));
    }
    
    private String getTianDiPanDesc(String[] palaces, String[] nineStars, String[] palaceNames) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>天盘代表天时，地盘代表地利，人盘为主事，神盘为助力。</font><br/><br/>");
        
        String[] directions = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};
        
        String[][] starDesc = {
            {"天蓬", "水·智，主求财、冒险，吉则财源广进，凶则破财"},
            {"天任", "土·信，主稳定、承载，吉则事业稳固，凶则阻滞"},
            {"天冲", "木·勇，主行动、争斗，吉则果断进取，凶则是非"},
            {"天辅", "木·仁，主贵人、文书，吉则金榜题名，凶则文书失误"},
            {"天英", "火·礼，主名声、光明，吉则声名远播，凶则火灾"},
            {"天芮", "土·信，主疾病、学习，吉则学业有成，凶则疾病缠身"},
            {"天柱", "金·义，主变革、决断，吉则贵人相助，凶则刑伤"},
            {"天心", "金·义，主谋略、医道，吉则计谋成功，凶则药石无功"},
            {"天禽", "土·信，主贵人、中和，吉则万事顺遂，凶则灾祸"}
        };
        
        String[][] palaceDesc = {
            {"坎", "水·北，主智、主险，对应肾脏、泌尿系统"},
            {"坤", "土·西南，主顺、主静，对应脾胃、消化系统"},
            {"震", "木·东，主动、主长，对应肝胆、神经系统"},
            {"巽", "木·东南，主风、主变，对应肝胆、呼吸系统"},
            {"中", "土·中，主和、主守，对应脾胃"},
            {"乾", "金·西北，主健、主尊，对应肺、大肠"},
            {"兑", "金·西，主悦、主泽，对应肺、呼吸系统"},
            {"艮", "土·东北，主止、主蓄，对应脾胃、消化系统"},
            {"离", "火·南，主明、主礼，对应心脏、眼睛"}
        };
        
        desc.append("<font color='#FFD700'><b>天盘（九星）</b></font><br/>");
        for (int i = 0; i < 9; i++) {
            if (!nineStars[i].isEmpty() && !nineStars[i].equals("--")) {
                String star = nineStars[i];
                String starInfo = "";
                for (String[] info : starDesc) {
                    if (info[0].equals(star)) {
                        starInfo = info[1];
                        break;
                    }
                }
                desc.append("<font color='#FFD700'>").append(star).append("星</font> ");
                desc.append("<font color='#98D8F0'>(").append(directions[i]).append(")</font> ");
                desc.append("<font color='#8899AA'>").append(starInfo).append("</font>");
                if (i < 8) desc.append("<br/>");
            }
        }
        desc.append("<br/><br/>");
        
        desc.append("<font color='#FFA500'><b>地盘（九宫）</b></font><br/>");
        for (int i = 0; i < 9; i++) {
            if (!palaceNames[i].isEmpty() && !palaceNames[i].equals("--")) {
                String palace = palaceNames[i].substring(0, 1);
                String palaceInfo = "";
                for (String[] info : palaceDesc) {
                    if (info[0].equals(palace)) {
                        palaceInfo = info[1];
                        break;
                    }
                }
                desc.append("<font color='#FFA500'>").append(palace).append("宫</font> ");
                desc.append("<font color='#98D8F0'>(").append(directions[i]).append(")</font> ");
                desc.append("<font color='#8899AA'>").append(palaceInfo).append("</font>");
                if (i < 8) desc.append("<br/>");
            }
        }
        
        return desc.toString();
    }
    
    private String getNineStarsDesc(String[] stars) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>九星主天时吉凶，决定事情发展趋势。</font><br/><br/>");
        
        String[][] starInfo = {
            {"天蓬", "吉星", "#90EE90", "水星。主智谋财禄，利经商冒险、求财投资；忌则水泛成灾、破财漂泊"},
            {"天任", "吉星", "#98FB98", "土星。主诚信稳定，利置业投资、稳固根基；忌则固执不化、阻滞不前"},
            {"天冲", "凶星", "#FF6B6B", "木星。主勇猛行动，利果断进取；忌则冲动争斗、是非口舌"},
            {"天辅", "吉星", "#ADFF2F", "木星。主文昌贵人，利考试求学、文书签约；忌则桃花泛滥、心神不定"},
            {"天英", "平星", "#FFD700", "火星。主名声光明，利求名展示；忌则火气过盛、急躁冒进"},
            {"天芮", "凶星", "#DC143C", "土星。主疾病障碍，利学习研究；忌则疾病缠身、久病不愈"},
            {"天柱", "平星", "#FFA500", "金星。主变革决断，利改革突破；忌则刑伤争斗、刚忆自用"},
            {"天心", "吉星", "#90EE90", "金星。主谋略医道，利策划管理、求医问药；忌则计谋落空、药石无功"},
            {"天禽", "吉星", "#98FB98", "土星。主中正贵人，利协调中和；忌则优柔寡断、错失良机"}
        };
        
        desc.append("<font color='#FFD700'><b>吉星</b></font><br/>");
        for (String[] info : starInfo) {
            if (info[1].equals("吉星")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("星</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }
        
        desc.append("<br/><font color='#FFD700'><b>平星</b></font><br/>");
        for (String[] info : starInfo) {
            if (info[1].equals("平星")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("星</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }
        
        desc.append("<br/><font color='#FF6B6B'><b>凶星</b></font><br/>");
        for (String[] info : starInfo) {
            if (info[1].equals("凶星")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("星</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }
        
        return desc.toString();
    }
    
    private String getEightDoorsDesc(String[] doors) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>八门主人事吉凶，决定行动成败。</font><br/><br/>");
        
        String[][] doorInfo = {
            {"休", "吉门", "#90EE90", "水门。宜休息养生、会客议事，百事皆宜，利见贵人"},
            {"生", "吉门", "#98FB98", "土门。最利求财开业、经商投资，谋事得利，八门之首"},
            {"伤", "凶门", "#FF6B6B", "木门。主损伤争斗，出行不利，易有伤灾官非，宜避之"},
            {"杜", "平门", "#FFD700", "木门。主闭塞隐藏，宜守不宜攻，适合隐藏守静、密谈"},
            {"景", "平门", "#FFA500", "火门。主名声文书，利考试求名，吉凶参半，宜谨慎行事"},
            {"死", "凶门", "#DC143C", "土门。主衰败丧事，百事不宜，宜镇不宜动"},
            {"惊", "凶门", "#FF6B6B", "金门。主惊恐怪异，虚惊口舌，官非诉讼，宜静不宜动"},
            {"开", "吉门", "#ADFF2F", "金门。主通达顺利，贵人相助，宜开业求职签约"}
        };
        
        desc.append("<font color='#FFD700'><b>吉门（宜行动、办事、求财）</b></font><br/>");
        for (String[] info : doorInfo) {
            if (info[1].equals("吉门")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("门</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }
        
        desc.append("<br/><font color='#FFD700'><b>平门（宜谨慎、待机）</b></font><br/>");
        for (String[] info : doorInfo) {
            if (info[1].equals("平门")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("门</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }
        
        desc.append("<br/><font color='#FF6B6B'><b>凶门（宜回避、守静）</b></font><br/>");
        for (String[] info : doorInfo) {
            if (info[1].equals("凶门")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("门</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }
        
        return desc.toString();
    }
    
    private String getEightGodsDesc(String[] gods) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>八神主外部环境与神秘力量影响。</font><br/><br/>");
        
        String[][] godInfo = {
            {"值符", "吉神", "#90EE90", "诸神之首。主尊贵权力，贵人相助，万事大吉"},
            {"螣蛇", "凶神", "#FF6B6B", "虚惊之神。主怪异缠绕，虚惊恐慌，宜放铜器化解"},
            {"太阴", "吉神", "#98FB98", "庇护之神。主暗中助力，贵人庇佑，宜密谋策划"},
            {"六合", "吉神", "#ADFF2F", "和合之神。主合作婚姻，交易和谈，人际和谐"},
            {"白虎", "凶神", "#DC143C", "杀伐之神。主血光灾祸，疾病争斗，宜静水化解"},
            {"玄武", "凶神", "#FF6B6B", "盗贼之神。主偷盗欺骗，暧昧小人，宜加强防范"},
            {"九地", "平神", "#FFD700", "稳固之神。主沉稳蓄势，保守守成，宜静不宜动"},
            {"九天", "吉神", "#90EE90", "升腾之神。主飞黄腾达，进取远行，气势磅礴"}
        };
        
        desc.append("<font color='#FFD700'><b>吉神（宜依靠、借助）</b></font><br/>");
        for (String[] info : godInfo) {
            if (info[1].equals("吉神")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }
        
        desc.append("<br/><font color='#FFD700'><b>平神（宜谨慎、待机）</b></font><br/>");
        for (String[] info : godInfo) {
            if (info[1].equals("平神")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }
        
        desc.append("<br/><font color='#FF6B6B'><b>凶神（宜回避、化解）</b></font><br/>");
        for (String[] info : godInfo) {
            if (info[1].equals("凶神")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }
        return desc.toString();
    }
    
    private String getPalacesDesc(String[] palaces, String[] stars, String[] doors, String[] gods, String[] lucks) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>各宫综合分析：</font><br/>");
        for (int i = 0; i < 9; i++) {
            String palace = palaces[i];
            String star = stars[i];
            String door = doors[i];
            String god = gods[i];
            String luck = lucks[i];
            desc.append("<font color='#98D8F0'>").append(palace).append("宫</font>");
            if (!star.isEmpty()) {
                desc.append("·<font color='#FFD700'>").append(star).append("星</font>");
            }
            if (door != null && !door.isEmpty()) {
                desc.append("·<font color='#90EE90'>").append(door).append("门</font>");
            }
            if (god != null && !god.isEmpty()) {
                desc.append("·<font color='#DDA0DD'>").append(god).append("</font>");
            }
            String luckColor = luck.contains("吉") ? "#90EE90" : (luck.contains("凶") ? "#FF6B6B" : "#8899AA");
            desc.append("(<font color='").append(luckColor).append("'>").append(luck).append("</font>)<br/>");
        }
        return desc.toString();
    }
    
    private String getZhifuAdvice(String star) {
        if (star == null) return "吉星高照，运势亨通";
        switch (star) {
            case "天蓬": return "值符天蓬星，智谋深远，宜策划谋略、把握先机，利求财冒险";
            case "天芮": return "值符天芮星，注意健康保养，防疾病侵扰，利学习研究";
            case "天冲": return "值符天冲星，行动需谨慎，防冲动误事，利果断进取";
            case "天辅": return "值符天辅星，贵人相助，宜把握机遇、借力成事，利学业考试";
            case "天禽": return "值符天禽星，中正平和，宜稳中求进，利协调沟通";
            case "天心": return "值符天心星，仁慈博爱，宜行善积德、求医问药，利策划管理";
            case "天柱": return "值符天柱星，刚直果断，宜当机立断、改革突破，利变革创新";
            case "天任": return "值符天任星，勤劳踏实，宜脚踏实地、稳固根基，利置业投资";
            case "天英": return "值符天英星，文明昌盛，宜学习进取、展示才华，利求名展示";
            default: return "吉星高照，运势亨通";
        }
    }
    
    private String getGodAdvice(String god) {
        if (god == null) return "神助之力，逢凶化吉";
        switch (god) {
            case "值符": return "值符临宫，统领全局，万事大吉，贵人相助，宜把握权势";
            case "螣蛇": return "螣蛇临宫，虚惊怪异，主惊恐缠绕，宜放铜器化解，保持冷静";
            case "太阴": return "太阴临宫，暗中助力，主贵人庇佑，宜密谋策划、暗中行事";
            case "六合": return "六合临宫，和合美满，主合作婚姻、交易和谈，宜主动沟通";
            case "白虎": return "白虎临宫，血光之灾，主杀伐疾病，宜静水化解，谨慎行事";
            case "玄武": return "玄武临宫，偷盗欺骗，主暧昧小人，宜加强防范、保管财物";
            case "九地": return "九地临宫，沉稳持重，主蓄势保守，宜静守待时、不宜冒进";
            case "九天": return "九天临宫，飞黄腾达，主升腾进取，宜主动出击、气势磅礴";
            default: return "神助之力，逢凶化吉";
        }
    }
    
    private String getZhishiAdvice(String door) {
        if (door == null) return "平稳发展";
        switch (door) {
            case "休": return "值使休门，宜休息养生、调整状态，蓄势待发，利会客议事";
            case "生": return "值使生门，宜开拓进取、求财创业，大展宏图，最吉之门";
            case "伤": return "值使伤门，宜谨慎行事，防破财损耗，避免出行争斗";
            case "杜": return "值使杜门，宜静不宜动，保守为上，适合隐藏密谈";
            case "景": return "值使景门，宜考试面试、展示才华，吉凶参半需谨慎";
            case "死": return "值使死门，宜保守谨慎，不宜进取，百事不宜";
            case "惊": return "值使惊门，宜防口舌是非，避免争执诉讼，宜静不宜动";
            case "开": return "值使开门，宜开门纳福、百事皆宜，利开业求职签约";
            default: return "平稳发展";
        }
    }
    
    private String getRiGanAdvice(String riGan) {
        if (riGan == null) return "审视自身，做出合适调整";
        switch (riGan) {
            case "甲": return "甲木为参天大树，主贵人、领袖。宜积极进取，发挥领导力。";
            case "乙": return "乙木为花草之木，主柔顺、仁慈。宜以柔克刚，耐心处事。";
            case "丙": return "丙火为太阳之火，主光明、热情。宜展现才华，积极向上。";
            case "丁": return "丁火为灯烛之火，主文明、细致。宜注重细节，精益求精。";
            case "戊": return "戊土为大地之土，主稳重、诚信。宜脚踏实地，诚实守信。";
            case "己": return "己土为田园之土，主包容、厚德。宜宽厚待人，积累福报。";
            case "庚": return "庚金为刀剑之金，主果断、刚毅。宜当机立断，勇往直前。";
            case "辛": return "辛金为首饰之金，主精致、细腻。宜注重品质，精益求精。";
            case "壬": return "壬水为江海之水，主智慧、流动。宜灵活变通，顺势而为。";
            case "癸": return "癸水为雨露之水，主聪明、神秘。宜低调行事，暗中谋划。";
            default: return "审视自身，做出合适调整";
        }
    }
    
    private String getShiGanAdvice(String shiGan) {
        if (shiGan == null) return "关注事情发展动向";
        switch (shiGan) {
            case "甲": return "甲木主事，主贵人相助，事情有望得到有力支持。";
            case "乙": return "乙木主事，主事情柔顺发展，需要耐心等待。";
            case "丙": return "丙火主事，主事情明朗，进展迅速，机遇显现。";
            case "丁": return "丁火主事，主事情需要细致处理，注重细节方能成功。";
            case "戊": return "戊土主事，主事情稳重推进，根基稳固，不易动摇。";
            case "己": return "己土主事，主事情需要包容忍耐，以柔克刚。";
            case "庚": return "庚金主事，主事情需要果断决策，勇往直前。";
            case "辛": return "辛金主事，主事情需要精益求精，注重品质。";
            case "壬": return "壬水主事，主事情变化多端，需要灵活应对。";
            case "癸": return "癸水主事，主事情暗藏玄机，需要谨慎分析。";
            default: return "关注事情发展动向";
        }
    }
    
    private String getRiShiRelationship(String riGan, String shiGan) {
        if (riGan.equals(shiGan)) {
            return "比和相助，事情容易达成，自身与事情协调一致";
        }
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土"); 
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        java.util.Map<String, String> ganWuxing = new java.util.HashMap<>();
        ganWuxing.put("甲", "木"); ganWuxing.put("乙", "木");
        ganWuxing.put("丙", "火"); ganWuxing.put("丁", "火");
        ganWuxing.put("戊", "土"); ganWuxing.put("己", "土");
        ganWuxing.put("庚", "金"); ganWuxing.put("辛", "金");
        ganWuxing.put("壬", "水"); ganWuxing.put("癸", "水");
        
        String riWuxing = ganWuxing.get(riGan);
        String shiWuxing = ganWuxing.get(shiGan);
        
        if (riWuxing != null && shiWuxing != null) {
            if (shengMap.get(riWuxing).equals(shiWuxing)) {
                return "日生时，自身生助事情，需付出努力方能成事";
            }
            if (shengMap.get(shiWuxing).equals(riWuxing)) {
                return "时生日，事情生助自身，事半功倍，易得帮助";
            }
        }
        return "日时关系一般，需努力争取，顺其自然";
    }
    
    private String getCareerAdvice(String door, String star) {
        if (door == null) return "谨慎行事";
        switch (door) {
            case "开": return "事业强劲，宜主动出击，把握良机";
            case "生": return "事业财运两旺，宜把握机遇，积极进取";
            case "休": return "宜休息调整，学习进修，规划未来";
            case "景": return "宜展示才华，积极表现，争取认可";
            case "伤": return "事业易损，宜稳守待时，防小人作祟";
            case "杜": return "事业受阻，宜静守待变，加强沟通";
            case "死": return "事业低迷，防受挫失机，宜守不宜攻";
            case "惊": return "防口舌是非，宜慎言慎行，低调处事";
            default: return "事业平稳，宜按部就班";
        }
    }
    
    private String getWealthAdvice(String door, String star) {
        if (door == null) return "谨慎理财";
        switch (door) {
            case "生": return "财运旺盛，宜积极求财，投资理财";
            case "开": return "财源广进，宜大胆尝试，创造财富";
            case "休": return "宜稳健理财，储蓄守财，不冒风险";
            case "景": return "财运一般，宜量力而行，见好就收";
            case "伤": return "防破财，不宜投资，守财为主";
            case "杜": return "财运受阻，宜静观其变，等待时机";
            case "死": return "财运低迷，守财为主，防破财";
            case "惊": return "防财务纠纷，不宜借贷，谨慎理财";
            default: return "财运平稳，宜稳健理财";
        }
    }
    
    private String getRelationshipAdvice(String door, String star) {
        if (door == null) return "谨慎交往";
        switch (door) {
            case "休": return "人际和谐，宜主动沟通，增进感情";
            case "生": return "感情运势佳，适合表白求婚";
            case "开": return "社交运势好，宜拓展人脉，结识贵人";
            case "景": return "宜展示魅力，积极社交";
            case "惊": return "防感情风波，宜冷静沟通";
            case "伤": return "感情易受伤，宜克制情绪";
            case "死": return "感情冷淡，宜反思修复";
            case "杜": return "沟通不畅，宜主动沟通，消除误会";
            default: return "感情平稳，宜顺其自然";
        }
    }
    
    private String getHealthAdvice(String door, String star) {
        if (door == null) return "注意保养";
        switch (door) {
            case "休": return "宜养生休息，劳逸结合，保证睡眠";
            case "生": return "身体健康，宜适度运动，增强体质";
            case "开": return "精力充沛，宜户外活动，保持活力";
            case "死": return "注意健康，防身体不适，定期检查";
            case "伤": return "防意外伤害，注意安全";
            case "景": return "防心火过旺，宜清淡饮食，避免熬夜";
            case "杜": return "防情绪郁结，宜放松心情";
            case "惊": return "防精神紧张，宜静心安神";
            default: return "身体平稳，宜保持良好习惯";
        }
    }
    
    private String getStudyAdvice(String door, String star) {
        if (door == null) return "勤奋学习";
        switch (door) {
            case "景": return "学习运势佳，宜刻苦钻研，把握良机";
            case "开": return "思维开阔，学习效率高，宜拓展知识";
            case "生": return "学业进步，适合备考复习，技能提升";
            case "休": return "宜静心学习，巩固知识，温故知新";
            case "杜": return "学习受阻，宜多思考实践，克服困难";
            case "伤": return "学习状态差，防半途而废，需坚持";
            case "死": return "学习低迷，宜调整心态，寻找方法";
            case "惊": return "防考试紧张，宜放松心态，沉着应对";
            default: return "学习平稳，宜循序渐进";
        }
    }
    
    private String getTravelAdvice(String door, String star) {
        if (door == null) return "谨慎出行";
        switch (door) {
            case "开": return "出行顺利，适合商务出差、旅游观光";
            case "休": return "宜休闲出行，适合度假放松、短途旅行";
            case "生": return "出行吉利，适合远足探险、求财出行";
            case "景": return "宜观光游览，适合拍照打卡、文化体验";
            case "惊": return "出行多波折，防交通延误，谨慎出行";
            case "伤": return "防出行意外，注意交通安全";
            case "死": return "不宜远行，在家为宜，防意外";
            case "杜": return "出行受阻，防路途不便，谨慎安排";
            default: return "出行平稳，注意安全";
        }
    }
    
    private String getDietAdvice(String door, String star) {
        if (door == null) return "饮食清淡";
        switch (door) {
            case "生": return "宜进补养生，营养均衡，适当进补";
            case "休": return "宜清淡饮食，素食调理，减轻肠胃负担";
            case "开": return "宜社交聚餐，商务宴请，朋友聚会";
            case "景": return "宜清热降火，清淡饮食，避免辛辣";
            case "死": return "饮食需谨慎，注意卫生，避免生冷";
            case "伤": return "防饮食损伤，节制饮食，不暴饮暴食";
            case "惊": return "防情绪性进食，保持规律饮食";
            case "杜": return "宜简单饮食，家常便饭，口味清淡";
            default: return "饮食宜规律，营养均衡";
        }
    }
    
    private String getSocialAdvice(String door, String star) {
        if (door == null) return "谨慎交友";
        switch (door) {
            case "开": return "社交运势佳，宜积极社交，广结善缘";
            case "休": return "人际和谐，适合维系旧友、家庭聚会";
            case "生": return "宜合作共赢，商务合作，团队协作";
            case "景": return "宜展示自我，参加活动，提升影响力";
            case "惊": return "防口舌是非，宜少言多行";
            case "伤": return "人际紧张，防朋友反目，宜低调";
            case "死": return "宜减少社交，不宜聚会应酬";
            case "杜": return "社交受阻，防沟通不畅，宜主动沟通";
            default: return "社交平稳，顺其自然";
        }
    }
    
    private String getMindAdvice(String door, String star) {
        if (door == null) return "保持平和";
        switch (door) {
            case "休": return "宜修身养性，放松身心，保持平和";
            case "生": return "心态积极，保持乐观，好运自来";
            case "开": return "宜开拓视野，勇于尝试，突破自我";
            case "景": return "宜保持热情，积极探索，追求美好";
            case "死": return "宜调整心态，面对困难，寻找转机";
            case "伤": return "宜保持冷静，克制冲动，三思后行";
            case "惊": return "宜减少焦虑，放松心情，相信自己";
            case "杜": return "宜保持耐心，静待时机";
            default: return "保持平常心，顺其自然";
        }
    }
    
    private String getCareerAdviceShort(String door, String star) {
        if (door == null) return "谨慎行事";
        switch (door) {
            case "开": return "事业运强，适合开拓";
            case "生": return "财运事业两旺";
            case "休": return "宜休整学习";
            case "景": return "宜展示才华";
            case "伤": return "防事业受损";
            case "杜": return "事业受阻";
            case "死": return "事业低迷";
            case "惊": return "防口舌是非";
            default: return "事业平稳";
        }
    }
    
    private String getWealthAdviceShort(String door, String star) {
        if (door == null) return "谨慎理财";
        switch (door) {
            case "生": return "财运旺盛";
            case "开": return "财源广进";
            case "休": return "宜稳健储蓄";
            case "景": return "财运一般";
            case "伤": return "防破财";
            case "杜": return "求财困难";
            case "死": return "不宜投资";
            case "惊": return "防财务纠纷";
            default: return "财运平稳";
        }
    }
    
    private String getHealthAdviceShort(String door, String star) {
        if (door == null) return "注意保养";
        switch (door) {
            case "休": return "宜养生休息";
            case "生": return "身体状态佳";
            case "开": return "精力充沛";
            case "景": return "宜户外活动";
            case "伤": return "防意外损伤";
            case "杜": return "宜静心调养";
            case "死": return "注意健康";
            case "惊": return "防精神紧张";
            default: return "健康平稳";
        }
    }
    
    private String getRelationshipAdviceShort(String door, String star) {
        if (door == null) return "谨慎交往";
        switch (door) {
            case "休": return "人际关系和谐";
            case "生": return "感情运势佳";
            case "开": return "社交运势好";
            case "景": return "宜展示魅力";
            case "惊": return "防感情风波";
            case "伤": return "感情易受伤";
            case "死": return "感情冷淡";
            case "杜": return "沟通不畅";
            default: return "感情平稳";
        }
    }
    
    private String getTravelAdviceShort(String door, String star) {
        if (door == null) return "谨慎出行";
        switch (door) {
            case "开": return "出行顺利";
            case "生": return "利于远行";
            case "休": return "宜短途出行";
            case "景": return "宜观光游览";
            case "伤": return "防交通意外";
            case "杜": return "出行受阻";
            case "死": return "不宜远行";
            case "惊": return "防旅途不安";
            default: return "出行平稳";
        }
    }
    
    private String getPalaceTip(int index, String palace, String star, String door, String god) {
        StringBuilder tip = new StringBuilder();
        
        switch (palace) {
            case "坎": tip.append("北方水"); break;
            case "坤": tip.append("西南土"); break;
            case "震": tip.append("东方木"); break;
            case "巽": tip.append("东南木"); break;
            case "中": tip.append("中央土"); break;
            case "乾": tip.append("西北金"); break;
            case "兑": tip.append("西方金"); break;
            case "艮": tip.append("东北土"); break;
            case "离": tip.append("南方火"); break;
        }
        
        tip.append("·");
        
        if (door != null && !door.isEmpty()) {
            switch (door) {
                case "开": tip.append("宜开拓"); break;
                case "生": tip.append("宜求财"); break;
                case "休": tip.append("宜休息"); break;
                case "景": tip.append("宜展示"); break;
                case "伤": tip.append("防损伤"); break;
                case "杜": tip.append("防阻塞"); break;
                case "死": tip.append("防衰败"); break;
                case "惊": tip.append("防口舌"); break;
            }
        } else {
            tip.append("平稳");
        }
        
        if (star != null && !star.isEmpty()) {
            tip.append("·");
            if (star.equals("天辅") || star.equals("天心")) {
                tip.append("吉");
            } else if (star.equals("天蓬") || star.equals("天任")) {
                tip.append("中");
            } else {
                tip.append("平");
            }
        }
        
        return tip.toString();
    }
    
    private String getStarMeaning(String star) {
        if (star == null) return "吉星高照";
        switch (star) {
            case "天蓬": return "智谋之星，利于策划谋略";
            case "天芮": return "病星，注意健康问题";
            case "天冲": return "冲动之星，行动需谨慎";
            case "天辅": return "辅佐之星，贵人相助";
            case "天禽": return "中正之星，稳定平和";
            case "天心": return "仁慈之星，利于医疗养生";
            case "天柱": return "刚直之星，利于决断";
            case "天任": return "任劳之星，勤劳得利";
            case "天英": return "文明之星，利于文教";
            default: return "吉星高照";
        }
    }
    
    private String getStarDetailedExplanation(String star) {
        if (star == null) return "";
        switch (star) {
            case "天蓬": return "【天蓬星】属水，北斗第一星\n特性：机智多谋，胆识过人\n吉：利谋划策略、市场开拓\n凶：防投机冒险、破财之灾\n建议：善用智慧，量力而行";
            case "天芮": return "【天芮星】属土，北斗第二星\n特性：病灾之星，主疾病\n吉：利求医问药、学习技艺\n凶：防身体损伤、疾病缠身\n建议：注重养生，谨慎行事";
            case "天冲": return "【天冲星】属木，北斗第三星\n特性：勇猛果敢，行动力强\n吉：利军事行动、快速决策\n凶：防冲动误事、争执冲突\n建议：三思后行，避免鲁莽";
            case "天辅": return "【天辅星】属木，北斗第四星\n特性：仁慈善良，贵人相助\n吉：利考试升学、文化教育\n凶：防轻信他人、犹豫不决\n建议：把握机遇，主动出击";
            case "天禽": return "【天禽星】属土，北斗第五星\n特性：中正平和，统领全局\n吉：利居中协调、团队管理\n凶：防优柔寡断、缺乏主见\n建议：保持中立，平衡各方";
            case "天心": return "【天心星】属金，北斗第六星\n特性：聪明智慧，善于谋划\n吉：利医疗健康、策略制定\n凶：防过于精明、猜忌多疑\n建议：心怀善念，广结善缘";
            case "天柱": return "【天柱星】属金，北斗第七星\n特性：刚直不阿，英勇果断\n吉：利司法诉讼、攻坚克难\n凶：防固执己见、树敌过多\n建议：灵活变通，团结协作";
            case "天任": return "【天任星】属土，北斗第八星\n特性：勤劳踏实，任劳任怨\n吉：利置业投资、稳步发展\n凶：防过于保守、错失良机\n建议：勤劳致富，适当进取";
            case "天英": return "【天英星】属火，北斗第九星\n特性：文明昌盛，才华出众\n吉：利文化创作、展示才华\n凶：防骄傲自满、口舌是非\n建议：低调行事，厚积薄发";
            default: return "";
        }
    }
    
    private String getDoorMeaning(String door) {
        if (door == null) return "中平之门";
        switch (door) {
            case "休": return "休息养生，利于休整";
            case "生": return "生发之气，大吉之门";
            case "伤": return "伤害损耗，需防破财";
            case "杜": return "阻塞不通，宜静不宜动";
            case "景": return "光明景象，利于考试面试";
            case "死": return "死气沉沉，诸事不宜";
            case "惊": return "惊恐不安，防口舌是非";
            case "开": return "开通顺利，百事皆宜";
            default: return "中平之门";
        }
    }
    
    private String getDoorDetailedExplanation(String door) {
        if (door == null) return "";
        switch (door) {
            case "休": return "【休门】属水，吉门\n特性：休养生息，恢复能量\n吉：利休息调养、学习思考\n凶：防过于懒散、不思进取\n建议：劳逸结合，蓄势待发";
            case "生": return "【生门】属土，吉门\n特性：生机勃勃，万物生长\n吉：利创业发展、投资理财\n凶：防贪得无厌、盲目扩张\n建议：稳扎稳打，步步为营";
            case "伤": return "【伤门】属木，凶门\n特性：伤害破坏，损耗财物\n吉：利捕猎渔猎、竞技比赛\n凶：防意外损伤、破财纠纷\n建议：谨慎行事，避免冒险";
            case "杜": return "【杜门】属木，平门\n特性：阻塞不通，隐藏躲避\n吉：利隐藏行踪、防守等待\n凶：防沟通受阻、孤立无援\n建议：静守待时，积蓄力量";
            case "景": return "【景门】属火，平门\n特性：光明灿烂，展示才华\n吉：利考试面试、宣传推广\n凶：防口舌是非、文书失误\n建议：言出必行，注重细节";
            case "死": return "【死门】属土，凶门\n特性：死气沉沉，缺乏生机\n吉：利丧葬事宜、终结旧业\n凶：防疾病死亡、诸事不顺\n建议：保守谨慎，不宜进取";
            case "惊": return "【惊门】属金，凶门\n特性：惊恐不安，口舌是非\n吉：利诉讼辩论、捕捉盗贼\n凶：防惊吓恐惧、谣言诽谤\n建议：镇定自若，谨言慎行";
            case "开": return "【开门】属金，吉门\n特性：开放通达，万事皆宜\n吉：利开业庆典、商务合作\n凶：防门户大开、泄露机密\n建议：把握机遇，大展宏图";
            default: return "";
        }
    }
    
    private String getYiActivities(String star, String door) {
        StringBuilder sb = new StringBuilder();
        if (door != null) {
            if (door.equals("开") || door.equals("生")) {
                sb.append("开业、求财、出行、");
            }
            if (door.equals("休")) {
                sb.append("休息、养生、学习、");
            }
        }
        if (star != null) {
            if (star.equals("天辅") || star.equals("天心")) {
                sb.append("求医、考试、面试、");
            }
            if (star.equals("天任")) {
                sb.append("工作、置业、合作、");
            }
        }
        sb.append("祈福、祭祀");
        return sb.toString();
    }
    
    private String getJiActivities(String star, String door) {
        StringBuilder sb = new StringBuilder();
        if (door != null) {
            if (door.equals("死") || door.equals("惊") || door.equals("伤")) {
                sb.append("远行、开业、签约、");
            }
        }
        if (star != null) {
            if (star.equals("天芮")) {
                sb.append("动土、手术、");
            }
            if (star.equals("天冲")) {
                sb.append("冲动决策、争执、");
            }
        }
        sb.append("诉讼、赌博");
        return sb.toString();
    }
    
    private String getTimeFortune(String timeZhi, String zhiShiDoor, String zhiFuStar) {
        if (timeZhi == null) return "时辰吉利";
        
        String timeInfo = "";
        switch (timeZhi) {
            case "子": timeInfo = "子时(23-1点): 阴气最重"; break;
            case "丑": timeInfo = "丑时(1-3点): 肝经当令"; break;
            case "寅": timeInfo = "寅时(3-5点): 肺经当令"; break;
            case "卯": timeInfo = "卯时(5-7点): 大肠经当令"; break;
            case "辰": timeInfo = "辰时(7-9点): 胃经当令"; break;
            case "巳": timeInfo = "巳时(9-11点): 脾经当令"; break;
            case "午": timeInfo = "午时(11-13点): 心经当令"; break;
            case "未": timeInfo = "未时(13-15点): 小肠经当令"; break;
            case "申": timeInfo = "申时(15-17点): 膀胱经当令"; break;
            case "酉": timeInfo = "酉时(17-19点): 肾经当令"; break;
            case "戌": timeInfo = "戌时(19-21点): 心包经当令"; break;
            case "亥": timeInfo = "亥时(21-23点): 三焦经当令"; break;
            default: timeInfo = "时辰";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(timeInfo);
        sb.append("\n值使").append(zhiShiDoor).append("门");
        if (zhiFuStar != null) sb.append(" 值符").append(zhiFuStar).append("星");
        sb.append("\n");
        
        String[] luckyDoors = {"开", "休", "生"};
        boolean isLuckyDoor = false;
        for (String d : luckyDoors) {
            if (d.equals(zhiShiDoor)) {
                isLuckyDoor = true;
                break;
            }
        }
        
        if (isLuckyDoor) {
            sb.append("吉门当值，运势佳，宜积极行动");
        } else {
            sb.append("平门/凶门当值，宜稳守待时");
        }
        
        return sb.toString();
    }
    
    private String getOverallAdvice(boolean isYangDun, int ju, String star, String door) {
        StringBuilder sb = new StringBuilder();
        if (isYangDun) {
            sb.append("当前为阳遁").append(ju).append("局，阳气上升，");
            sb.append("利于主动出击、开拓进取。\n");
        } else {
            sb.append("当前为阴遁").append(ju).append("局，阴气收敛，");
            sb.append("利于守成待时、稳中求进。\n");
        }
        
        String[] luckyStars = {"天辅", "天心", "天禽", "天任"};
        String[] luckyDoors = {"开", "休", "生"};
        boolean isLuckyStar = false, isLuckyDoor = false;
        if (star != null) {
            for (String s : luckyStars) if (s.equals(star)) isLuckyStar = true;
        }
        if (door != null) {
            for (String d : luckyDoors) if (d.equals(door)) isLuckyDoor = true;
        }
        
        if (isLuckyStar && isLuckyDoor) {
            sb.append("值符值使皆吉，今日运势极佳，把握机遇！");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("值符值使一吉，运势平稳，顺势而为。");
        } else {
            sb.append("值符值使欠佳，宜谨慎行事，趋吉避凶。");
        }
        
        return sb.toString();
    }

    
    private String getStarMeaningDetailed(String star) {
        if (star == null) return "吉星高照\n运势极佳";
        switch (star) {
            case "天蓬": return "智慧之星\n头脑聪明\n适合策划谋略";
            case "天芮": return "疾病之星\n身体较弱\n注意健康保养";
            case "天冲": return "冲动之星\n行动派\n需防冲动误事";
            case "天辅": return "辅佐之星\n贵人相助\n利人际交往";
            case "天禽": return "中正之星\n为人正直\n运势平稳";
            case "天心": return "仁慈之星\n有爱心\n利医疗养生";
            case "天柱": return "刚直之星\n性格刚强\n利于决断";
            case "天任": return "任劳之星\n勤奋努力\n财运不错";
            case "天英": return "文明之星\n利考试文化\n利创作";
            default: return "吉星高照\n运势极佳";
        }
    }
    
    private String getDoorMeaningDetailed(String door) {
        if (door == null) return "中平之门\n运势一般";
        switch (door) {
            case "休": return "休养生息\n利于休息疗养\n调整状态";
            case "生": return "生机勃勃\n大吉之门\n利创业发展";
            case "伤": return "伤害损耗\n破财之星\n需谨慎行事";
            case "杜": return "阻塞不通\n宜静不宜动\n避免冒险";
            case "景": return "光明景象\n利考试表演\n利展示";
            case "死": return "死气沉沉\n诸事不宜\n保守为上";
            case "惊": return "惊恐不安\n防口舌是非\n防纠纷";
            case "开": return "开放顺利\n大吉之门\n百事皆宜";
            default: return "中平之门\n运势一般";
        }
    }
    
    private String[] getYiActivitiesDetailed(String star, String door) {
        java.util.ArrayList<String> yiList = new java.util.ArrayList<>();
        
        if (door != null) {
            switch (door) {
                case "开":
                    yiList.add("开业剪彩"); yiList.add("商务洽谈"); yiList.add("签订合同");
                    yiList.add("投资理财"); yiList.add("拓展市场"); yiList.add("招聘面试");
                    yiList.add("公开演讲"); yiList.add("展示产品"); yiList.add("项目启动");
                    break;
                case "生":
                    yiList.add("投资理财"); yiList.add("置业购房"); yiList.add("储蓄存款");
                    yiList.add("合作共赢"); yiList.add("创业发展"); yiList.add("洽谈合作");
                    yiList.add("进货采购"); yiList.add("催收账款"); yiList.add("投资入股");
                    break;
                case "休":
                    yiList.add("休息疗养"); yiList.add("调养身体"); yiList.add("学习进修");
                    yiList.add("考试面试"); yiList.add("旅游度假"); yiList.add("约会交友");
                    yiList.add("家庭聚会"); yiList.add("拜访亲友"); yiList.add("静心冥想");
                    break;
                case "景":
                    yiList.add("文化教育"); yiList.add("考试培训"); yiList.add("展示才华");
                    yiList.add("社交应酬"); yiList.add("会议发言"); yiList.add("媒体采访");
                    yiList.add("艺术表演"); yiList.add("拍照摄影"); yiList.add("品牌宣传");
                    break;
                case "杜":
                    yiList.add("保守秘密"); yiList.add("隐藏行踪"); yiList.add("低调处事");
                    yiList.add("暗中调查"); yiList.add("内部沟通"); yiList.add("私下商议");
                    break;
                case "伤":
                    yiList.add("锻炼身体"); yiList.add("竞技比赛"); yiList.add("短途出行");
                    yiList.add("团队协作"); yiList.add("开拓进取"); yiList.add("灵活应变");
                    break;
                case "死":
                    yiList.add("清理整顿"); yiList.add("总结反思"); yiList.add("守财储蓄");
                    yiList.add("调整心态"); yiList.add("修复关系"); yiList.add("结束旧业");
                    break;
                case "惊":
                    yiList.add("法律咨询"); yiList.add("风险评估"); yiList.add("谨慎决策");
                    yiList.add("核实信息"); yiList.add("准备预案"); yiList.add("防患未然");
                    break;
            }
        }
        
        if (star != null) {
            switch (star) {
                case "天辅":
                    yiList.add("考试升学"); yiList.add("学术研究"); yiList.add("教育培训");
                    yiList.add("文化交流"); yiList.add("贵人相助"); yiList.add("合作共赢");
                    break;
                case "天心":
                    yiList.add("医疗养生"); yiList.add("求医问诊"); yiList.add("健康管理");
                    yiList.add("策略规划"); yiList.add("智慧决策"); yiList.add("技术研发");
                    break;
                case "天禽":
                    yiList.add("团队管理"); yiList.add("居中协调"); yiList.add("公正处事");
                    yiList.add("组织会议"); yiList.add("平衡各方"); yiList.add("统筹全局");
                    break;
                case "天任":
                    yiList.add("勤劳工作"); yiList.add("置业投资"); yiList.add("稳步发展");
                    yiList.add("踏实做事"); yiList.add("积累财富"); yiList.add("经营管理");
                    break;
                case "天蓬":
                    yiList.add("策划谋略"); yiList.add("市场开拓"); yiList.add("创新突破");
                    yiList.add("敢于尝试"); yiList.add("智慧理财"); yiList.add("商业谈判");
                    break;
                case "天冲":
                    yiList.add("果断行动"); yiList.add("快速决策"); yiList.add("执行力强");
                    yiList.add("体育竞技"); yiList.add("军事行动"); yiList.add("攻坚克难");
                    break;
                case "天芮":
                    yiList.add("学习技艺"); yiList.add("专业培训"); yiList.add("技能提升");
                    yiList.add("中医调理"); yiList.add("养生保健"); yiList.add("耐心学习");
                    break;
                case "天柱":
                    yiList.add("司法诉讼"); yiList.add("坚定立场"); yiList.add("主持公道");
                    yiList.add("权威发言"); yiList.add("攻坚克难"); yiList.add("捍卫权益");
                    break;
                case "天英":
                    yiList.add("文化创作"); yiList.add("艺术表演"); yiList.add("品牌宣传");
                    yiList.add("展示才华"); yiList.add("媒体曝光"); yiList.add("创意设计");
                    break;
            }
        }
        
        yiList.add("祭祀祈福");
        yiList.add("行善积德");
        yiList.add("拜访长辈");
        yiList.add("孝敬父母");
        
        return yiList.toArray(new String[0]);
    }
    
    private String[] getJiActivitiesDetailed(String star, String door) {
        java.util.ArrayList<String> jiList = new java.util.ArrayList<>();
        
        if (door != null) {
            switch (door) {
                case "死":
                    jiList.add("重大决策"); jiList.add("重要签约"); jiList.add("冒险投资");
                    jiList.add("远行迁徙"); jiList.add("结婚嫁娶"); jiList.add("开业开张");
                    jiList.add("动土施工"); jiList.add("破土安葬"); jiList.add("求医问药");
                    break;
                case "惊":
                    jiList.add("重大决策"); jiList.add("重要签约"); jiList.add("公开演讲");
                    jiList.add("与人争执"); jiList.add("诉讼纠纷"); jiList.add("传播谣言");
                    jiList.add("借贷担保"); jiList.add("轻信他人"); jiList.add("投资投机");
                    break;
                case "伤":
                    jiList.add("重大决策"); jiList.add("冒险投资"); jiList.add("远行迁徙");
                    jiList.add("与人争执"); jiList.add("动土施工"); jiList.add("高风险活动");
                    jiList.add("暴饮暴食"); jiList.add("冲动消费"); jiList.add("跳槽离职");
                    break;
                case "杜":
                    jiList.add("公开演讲"); jiList.add("展示才华"); jiList.add("社交聚会");
                    jiList.add("商业洽谈"); jiList.add("公开招标"); jiList.add("媒体曝光");
                    break;
                case "景":
                    jiList.add("冒险投资"); jiList.add("高风险活动"); jiList.add("过度娱乐");
                    jiList.add("酒后驾车"); jiList.add("熬夜通宵"); jiList.add("口舌之争");
                    break;
                case "开":
                    jiList.add("保守秘密"); jiList.add("隐藏行踪"); jiList.add("闭门造车");
                    break;
                case "休":
                    jiList.add("过度劳累"); jiList.add("剧烈运动"); jiList.add("冒险行动");
                    break;
                case "生":
                    jiList.add("铺张浪费"); jiList.add("盲目投资"); jiList.add("高风险投机");
                    break;
            }
        }
        
        if (star != null) {
            switch (star) {
                case "天芮":
                    jiList.add("动土施工"); jiList.add("外科手术"); jiList.add("暴饮暴食");
                    jiList.add("冒险活动"); jiList.add("带病工作"); jiList.add("忽视健康");
                    break;
                case "天冲":
                    jiList.add("冲动决策"); jiList.add("争执纠纷"); jiList.add("高风险投资");
                    jiList.add("鲁莽行动"); jiList.add("与人冲突"); jiList.add("过度竞争");
                    break;
                case "天柱":
                    jiList.add("固执己见"); jiList.add("盲目投资"); jiList.add("树敌过多");
                    jiList.add("刚愎自用"); jiList.add("与人对抗"); jiList.add("拒绝妥协");
                    break;
                case "天蓬":
                    jiList.add("投机冒险"); jiList.add("盲目跟风"); jiList.add("轻信谣言");
                    jiList.add("贪得无厌"); jiList.add("违法乱纪"); jiList.add("过度扩张");
                    break;
                case "天任":
                    jiList.add("过于保守"); jiList.add("错失良机"); jiList.add("犹豫不决");
                    jiList.add("固步自封"); jiList.add("不愿改变"); jiList.add("拖延懒散");
                    break;
                case "天辅":
                    jiList.add("轻信他人"); jiList.add("犹豫不决"); jiList.add("优柔寡断");
                    jiList.add("逃避现实"); jiList.add("过于理想化"); jiList.add("不切实际");
                    break;
                case "天心":
                    jiList.add("过于精明"); jiList.add("猜忌多疑"); jiList.add("算计他人");
                    jiList.add("冷漠无情"); jiList.add("缺乏信任"); jiList.add("过度分析");
                    break;
                case "天禽":
                    jiList.add("优柔寡断"); jiList.add("缺乏主见"); jiList.add("随波逐流");
                    jiList.add("立场不坚定"); jiList.add("调和过度"); jiList.add("错失良机");
                    break;
                case "天英":
                    jiList.add("骄傲自满"); jiList.add("自以为是"); jiList.add("炫耀张扬");
                    jiList.add("急功近利"); jiList.add("华而不实"); jiList.add("口出狂言");
                    break;
            }
        }
        
        jiList.add("赌博投机");
        jiList.add("诉讼纠纷");
        jiList.add("口舌是非");
        
        return jiList.toArray(new String[0]);
    }
    
    private String getShichenName(String timeZhi) {
        if (timeZhi == null) return "子时";
        switch (timeZhi) {
            case "子": return "子时 (23:00-01:00)";
            case "丑": return "丑时 (01:00-03:00)";
            case "寅": return "寅时 (03:00-05:00)";
            case "卯": return "卯时 (05:00-07:00)";
            case "辰": return "辰时 (07:00-09:00)";
            case "巳": return "巳时 (09:00-11:00)";
            case "午": return "午时 (11:00-13:00)";
            case "未": return "未时 (13:00-15:00)";
            case "申": return "申时 (15:00-17:00)";
            case "酉": return "酉时 (17:00-19:00)";
            case "戌": return "戌时 (19:00-21:00)";
            case "亥": return "亥时 (21:00-23:00)";
            default: return timeZhi + "时";
        }
    }
    
    private String getTimeFortuneDetailed(String timeZhi) {
        if (timeZhi == null) return "时辰吉利\n运势平稳";
        switch (timeZhi) {
            case "子": return "一阳初生\n阴气最盛\n宜静养安神\n利于思考规划\n忌：剧烈运动\n提示：适合冥想打坐";
            case "丑": return "丑土藏金\n肝经当令\n宜深度睡眠\n利于身体修复\n忌：熬夜通宵\n提示：保持充足睡眠";
            case "寅": return "肺经当令\n朝气蓬勃\n宜早起活动\n利于户外运动\n忌：赖床不起\n提示：早起迎曙光";
            case "卯": return "大肠经当令\n朝阳升起\n宜排便清肠\n利于排毒养颜\n忌：憋便忍尿\n提示：定时排便习惯";
            case "辰": return "胃经当令\n早餐时分\n宜进食营养\n利于消化吸收\n忌：不吃早餐\n提示：早餐要吃好";
            case "巳": return "脾经当令\n精力充沛\n宜勤奋工作\n利于脑力劳动\n忌：懒散拖延\n提示：黄金工作时段";
            case "午": return "心经当令\n阳气最盛\n宜适当休息\n利于午间小憩\n忌：剧烈运动\n提示：午睡养心护神";
            case "未": return "小肠经当令\n午餐消化\n宜清淡饮食\n利于营养吸收\n忌：午餐过饱\n提示：午餐适量为宜";
            case "申": return "膀胱经当令\n运动好时机\n宜适量运动\n利于体育锻炼\n忌：久坐不动\n提示：运动量力而行";
            case "酉": return "肾经当令\n日落时分\n宜休息放松\n利于养精蓄锐\n忌：剧烈运动\n提示：早睡养肾根本";
            case "戌": return "心包经当令\n夜幕降临\n宜放松娱乐\n利于社交活动\n忌：情绪低落\n提示：与亲友交流";
            case "亥": return "三焦经当令\n万物归宁\n宜准备休息\n利于按时入睡\n忌：熬夜加班\n提示：亥时入睡养生";
            default: return "时辰吉利\n运势平稳\n平安顺利";
        }
    }
    
    private String getOverallAdviceDetailed(boolean isYangDun, int ju, String star, String door, String dayGan) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("📊 命盘分析\n");
        sb.append("────────────\n\n");
        
        if (isYangDun) {
            sb.append("当前为阳遁").append(ju).append("局\n");
            sb.append("🔆 阳气旺盛，万物生长\n");
            sb.append("📈 利主动出击、开拓进取\n");
            sb.append("💪 建议：积极行动，把握机遇\n\n");
        } else {
            sb.append("当前为阴遁").append(ju).append("局\n");
            sb.append("🌙 阴气收敛，万物潜藏\n");
            sb.append("📉 利静心守成、稳扎稳打\n");
            sb.append("🧘 建议：沉稳低调，蓄势待发\n\n");
        }
        
        sb.append("⭐ 星门吉凶\n");
        sb.append("────────────\n\n");
        
        String[] luckyStars = {"天辅", "天心", "天禽", "天任"};
        String[] luckyDoors = {"开", "休", "生"};
        String[] neutralDoors = {"杜", "景"};
        
        boolean isLuckyStar = false, isLuckyDoor = false;
        boolean isNeutralDoor = false;
        
        if (star != null) {
            for (String s : luckyStars) if (s.equals(star)) isLuckyStar = true;
        }
        if (door != null) {
            for (String d : luckyDoors) if (d.equals(door)) isLuckyDoor = true;
            for (String d : neutralDoors) if (d.equals(door)) isNeutralDoor = true;
        }
        
        if (isLuckyStar && isLuckyDoor) {
            sb.append("★★★ 大吉 ★★★\n");
            sb.append("值符值使皆吉，今日运势极佳\n");
            sb.append("宜：把握机遇、大展宏图、积极进取\n\n");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("★★ 小吉 ★★\n");
            sb.append("星门一吉一平，运势良好\n");
            sb.append("宜：稳中求进、顺势而为、努力争取\n\n");
        } else if (isNeutralDoor) {
            sb.append("★ 平平 ★\n");
            sb.append("星门无大凶，运势一般\n");
            sb.append("宜：谨慎行事、守成待时、静观其变\n\n");
        } else {
            sb.append("⚠ 注意 ⚠\n");
            sb.append("星门欠佳，运势低迷\n");
            sb.append("宜：守不宜动、修身养性、规避风险\n\n");
        }
        
        sb.append("💡 综合建议\n");
        sb.append("────────────\n\n");
        sb.append("• ").append(getDayAdvice(star, door, dayGan)).append("\n\n");
        sb.append("• 保持良好心态，遇事冷静应对\n");
        sb.append("• 注重身心健康，劳逸结合\n");
        sb.append("• 多行善事，积累福报\n");
        sb.append("• 顺应天时，顺势而为\n");
        
        return sb.toString();
    }
    
    private String getOverallAdviceSimple(boolean isYangDun, int ju, String star, String door) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("📊 综合建议\n\n");
        
        sb.append("🔮 排盘格局\n");
        sb.append(isYangDun ? "☀️ 阳遁" : "🌙 阴遁").append(ju).append("局\n");
        sb.append("⭐ 值符：").append(star).append("星\n");
        sb.append("🚪 值使：").append(door).append("门\n");
        sb.append("\n");
        
        String[] luckyStars = {"天辅", "天心", "天禽", "天任"};
        String[] luckyDoors = {"开", "休", "生"};
        
        boolean isLuckyStar = false, isLuckyDoor = false;
        if (star != null) {
            for (String s : luckyStars) if (s.equals(star)) isLuckyStar = true;
        }
        if (door != null) {
            for (String d : luckyDoors) if (d.equals(door)) isLuckyDoor = true;
        }
        
        sb.append("📈 整体运势\n");
        if (isLuckyStar && isLuckyDoor) {
            sb.append("🏆 ★★★ 大吉 - 值符值使皆吉，今日运势极佳\n");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("👍 ★★ 小吉 - 值符值使一吉，运势平稳\n");
        } else {
            sb.append("⚡ ★ 平平 - 值符值使欠佳，宜谨慎行事\n");
        }
        sb.append("\n");
        
        sb.append("🎯 排盘特点分析\n\n");
        
        sb.append("✅ 优势\n");
        java.util.ArrayList<String> strengths = new java.util.ArrayList<>();
        if (isYangDun) {
            strengths.add("☀️ 阳气上升，利于主动出击");
        } else {
            strengths.add("🌙 阴气收敛，利于守成待时");
        }
        if (star != null) {
            switch (star) {
                case "天辅": strengths.add("👑 天辅星主贵人，易得相助"); strengths.add("📚 利于考试升学、文化教育"); break;
                case "天心": strengths.add("💡 天心星主智慧，善于谋划"); strengths.add("🏥 利于医疗健康、策略制定"); break;
                case "天禽": strengths.add("⚖️ 天禽星主中正，统领全局"); strengths.add("👥 利于团队管理、居中协调"); break;
                case "天任": strengths.add("💪 天任星主勤劳，稳扎稳打"); strengths.add("🏠 利于置业投资、稳步发展"); break;
                case "天蓬": strengths.add("🧠 天蓬星主谋略，机智多谋"); strengths.add("🌐 利于市场开拓、策划布局"); break;
                case "天冲": strengths.add("🚀 天冲星主动力，行动力强"); strengths.add("⚔️ 利于快速决策、攻坚克难"); break;
                case "天芮": strengths.add("📖 天芮星主学习，技艺提升"); strengths.add("🎓 利于专业培训、技能深造"); break;
                case "天柱": strengths.add("🔱 天柱星主刚直，决断有力"); strengths.add("⚖️ 利于司法诉讼、捍卫立场"); break;
                case "天英": strengths.add("🎨 天英星主文明，才华出众"); strengths.add("🌟 利于文化创作、展示才华"); break;
            }
        }
        if (door != null) {
            switch (door) {
                case "开": strengths.add("🎉 开门大吉，百事皆宜"); strengths.add("🤝 利于开业庆典、商务合作"); break;
                case "生": strengths.add("🌱 生门兴旺，万物生长"); strengths.add("💰 利于创业发展、投资理财"); break;
                case "休": strengths.add("😌 休门静养，恢复能量"); strengths.add("📖 利于休息调养、学习思考"); break;
                case "景": strengths.add("✨ 景门光明，展示才华"); strengths.add("🎤 利于考试面试、宣传推广"); break;
                case "杜": strengths.add("🔒 杜门隐蔽，防守待机"); strengths.add("💧 利于隐藏行踪、积蓄力量"); break;
                case "伤": strengths.add("⚡ 伤门活跃，灵活应变"); strengths.add("🏃 利于竞技比赛、开拓进取"); break;
                case "死": strengths.add("🔚 死门终结，清理整顿"); strengths.add("📝 利于总结反思、结束旧业"); break;
                case "惊": strengths.add("🔔 惊门警觉，防患未然"); strengths.add("📋 利于法律咨询、风险评估"); break;
            }
        }
        if (strengths.isEmpty()) {
            strengths.add("📊 运势平稳，可稳步推进");
        }
        for (int i = 0; i < strengths.size(); i++) {
            sb.append("  ").append(strengths.get(i)).append("\n");
        }
        sb.append("\n");
        
        sb.append("⚠️ 不足\n");
        java.util.ArrayList<String> weaknesses = new java.util.ArrayList<>();
        if (star != null) {
            switch (star) {
                case "天辅": weaknesses.add("❌ 防轻信他人、犹豫不决"); break;
                case "天心": weaknesses.add("❌ 防过于精明、猜忌多疑"); break;
                case "天禽": weaknesses.add("❌ 防优柔寡断、缺乏主见"); break;
                case "天任": weaknesses.add("❌ 防过于保守、错失良机"); break;
                case "天蓬": weaknesses.add("❌ 防投机冒险、贪得无厌"); break;
                case "天冲": weaknesses.add("❌ 防冲动误事、争执冲突"); break;
                case "天芮": weaknesses.add("❌ 防疾病缠身、体弱多病"); break;
                case "天柱": weaknesses.add("❌ 防固执己见、树敌过多"); break;
                case "天英": weaknesses.add("❌ 防骄傲自满、口舌是非"); break;
            }
        }
        if (door != null) {
            switch (door) {
                case "开": weaknesses.add("❌ 防门户大开、泄露机密"); break;
                case "生": weaknesses.add("❌ 防贪得无厌、盲目扩张"); break;
                case "休": weaknesses.add("❌ 防过于懒散、不思进取"); break;
                case "景": weaknesses.add("❌ 防口舌是非、文书失误"); break;
                case "杜": weaknesses.add("❌ 防沟通受阻、孤立无援"); break;
                case "伤": weaknesses.add("❌ 防意外损伤、破财纠纷"); break;
                case "死": weaknesses.add("❌ 防疾病死亡、诸事不顺"); break;
                case "惊": weaknesses.add("❌ 防惊吓恐惧、谣言诽谤"); break;
            }
        }
        if (weaknesses.isEmpty()) {
            weaknesses.add("✅ 暂无明显不足");
        }
        for (int i = 0; i < weaknesses.size(); i++) {
            sb.append("  ").append(weaknesses.get(i)).append("\n");
        }
        sb.append("\n");
        
        sb.append("⚖️ 平衡\n");
        java.util.ArrayList<String> balances = new java.util.ArrayList<>();
        if (isLuckyStar && isLuckyDoor) {
            balances.add("🎯 运势强劲，可乘胜追击，但需保持谦逊");
            balances.add("🛡️ 把握良机的同时，注意防范风险");
        } else if (isLuckyStar || isLuckyDoor) {
            balances.add("⚖️ 运势有吉有凶，宜扬长避短");
            balances.add("💪 发挥优势的同时，谨慎对待不足");
        } else {
            balances.add("🌱 运势欠佳，宜稳守待时");
            balances.add("📦 韬光养晦，积蓄力量，等待转机");
        }
        for (int i = 0; i < balances.size(); i++) {
            sb.append("  ").append(balances.get(i)).append("\n");
        }
        sb.append("\n");
        
        sb.append("💡 核心建议\n");
        if (isYangDun) {
            sb.append("☀️ 当前阳气上升，宜积极进取，把握机遇\n");
            if (isLuckyStar && isLuckyDoor) {
                sb.append("  🏆 吉星高照，贵人相助，今日是行动的最佳时机\n");
                sb.append("  💪 可大胆推进重要事项，成功概率高\n");
            } else {
                sb.append("  ⚡ 虽有吉象，但仍需谨慎行事\n");
                sb.append("  📊 建议稳步推进，不宜冒进\n");
            }
        } else {
            sb.append("🌙 当前阴气收敛，宜守成待时，稳中求进\n");
            if (isLuckyStar && isLuckyDoor) {
                sb.append("  👍 吉门吉星，运势不错，可谨慎进取\n");
                sb.append("  ⚖️ 注意把握分寸，避免过度扩张\n");
            } else {
                sb.append("  ⚡ 运势一般，宜静守待变\n");
                sb.append("  🛡️ 建议低调处事，避免争执\n");
            }
        }
        sb.append("\n");
        
        sb.append("📋 行事准则\n");
        if (door != null) {
            switch (door) {
                case "开": sb.append("🚀 大胆开创，积极进取，把握良机\n"); break;
                case "生": sb.append("🌱 稳扎稳打，步步为营，注重积累\n"); break;
                case "休": sb.append("😌 劳逸结合，养精蓄锐，厚积薄发\n"); break;
                case "景": sb.append("✨ 展示才华，注重细节，言出必行\n"); break;
                case "杜": sb.append("🔒 静守待时，积蓄力量，等待时机\n"); break;
                case "伤": sb.append("⚠️ 谨慎行事，避免冒险，防损破财\n"); break;
                case "死": sb.append("🛡️ 保守谨慎，不宜进取，清理整顿\n"); break;
                case "惊": sb.append("🔔 镇定自若，谨言慎行，防口舌是非\n"); break;
            }
        }
        
        return sb.toString();
    }
    
    private String getZhifuExplanation(String star) {
        if (star == null) return "值符为九星之主，统领全局";
        switch (star) {
            case "天蓬": return "值符天蓬星：智谋深远，主策划谋略。值此星者，善于谋划，把握先机";
            case "天芮": return "值符天芮星：主学习与疾病。值此星者，宜注重健康，刻苦学习";
            case "天冲": return "值符天冲星：行动力强，主果断进取。值此星者，宜快速决策，勇往直前";
            case "天辅": return "值符天辅星：贵人相助，主文化教育。值此星者，易得贵人扶持，利于考试升学";
            case "天禽": return "值符天禽星：中正平和，主统领协调。值此星者，宜居中协调，平衡各方";
            case "天心": return "值符天心星：智慧谋略，主医疗策划。值此星者，善于策划，利于医疗健康";
            case "天柱": return "值符天柱星：刚直果断，主变革决断。值此星者，宜当机立断，改革突破";
            case "天任": return "值符天任星：勤劳踏实，主稳步发展。值此星者，宜脚踏实地，稳扎稳打";
            case "天英": return "值符天英星：才华出众，主文化展示。值此星者，宜展示才华，追求卓越";
            default: return "值符为九星之主，统领全局";
        }
    }
    
    private String getZhishiExplanation(String door) {
        if (door == null) return "值使为八门之主，掌管人事";
        switch (door) {
            case "休": return "值使休门：主休息养生，恢复能量。宜调整状态，蓄势待发";
            case "生": return "值使生门：主生机勃勃，万物生长。宜开拓进取，求财创业";
            case "伤": return "值使伤门：主伤害损耗，破财纠纷。宜谨慎行事，避免冒险";
            case "杜": return "值使杜门：主阻塞不通，隐藏防守。宜静守待时，积蓄力量";
            case "景": return "值使景门：主光明展示，才华显露。宜考试面试，宣传推广";
            case "死": return "值使死门：主衰败终结，诸事不宜。宜保守谨慎，不宜进取";
            case "惊": return "值使惊门：主惊恐不安，口舌是非。宜镇定自若，谨言慎行";
            case "开": return "值使开门：主开放通达，百事皆宜。宜开业合作，大展宏图";
            default: return "值使为八门之主，掌管人事";
        }
    }
    
    private String getDoorDescription(String door) {
        if (door == null) return "";
        switch (door) {
            case "开": return "吉门，主事业顺利、贵人相助、财源广进，利于求职开业";
            case "休": return "吉门，主身心安宁、贵人扶持、百事顺遂，利于休息养生";
            case "生": return "吉门，主财运亨通、生意兴隆、身体健康，利于投资求财";
            case "伤": return "凶门，主损伤争斗、疾病破财、出行不利，忌出行办事";
            case "杜": return "平门，主闭塞阻滞、隐藏保守，适合静守不适合行动";
            case "景": return "平门，主名声远播、文书光明、才艺展现，利于考试宣传";
            case "死": return "凶门，主死亡丧事、疾病缠身、诸事不利，忌婚嫁出行";
            case "惊": return "凶门，主惊恐不安、口舌是非、官非诉讼，忌谈判签约";
            default: return "";
        }
    }
    
    private String getKongWangExplanation(String kongWang) {
        if (kongWang == null || kongWang.equals("--")) return "空亡指六甲旬中未出现的地支，主事体不实";
        String[] explanations = {
            "戌亥空：主孤独空虚，防情感缺失，宜充实内心",
            "申酉空：主金气不足，防肺呼吸系统，宜润肺养生",
            "午未空：主火气不足，防心血管系统，宜静心养神",
            "辰巳空：主土气不足，防脾胃消化系统，宜健脾养胃",
            "寅卯空：主木气不足，防肝胆神经系统，宜疏肝理气",
            "子丑空：主水气不足，防肾泌尿系统，宜温补肾阳"
        };
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("戌亥", explanations[0]);
        map.put("申酉", explanations[1]);
        map.put("午未", explanations[2]);
        map.put("辰巳", explanations[3]);
        map.put("寅卯", explanations[4]);
        map.put("子丑", explanations[5]);
        return map.getOrDefault(kongWang, "空亡指六甲旬中未出现的地支，主事体不实");
    }
    
    private String getMaXingExplanation(String maXing) {
        if (maXing == null || maXing.equals("--")) return "马星主奔波走动，变动迁移";
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("寅", "马星寅：主东方之动，利出行东方，宜积极进取");
        map.put("午", "马星午：主南方之动，利出行南方，宜热情开朗");
        map.put("申", "马星申：主西方之动，利出行西方，宜果断行动");
        map.put("亥", "马星亥：主北方之动，利出行北方，宜智慧变通");
        map.put("巳", "马星巳：主东南之动，利出行东南，宜灵活机动");
        map.put("酉", "马星酉：主西北之动，利出行西北，宜精细谋划");
        return map.getOrDefault(maXing, "马星主奔波走动，变动迁移");
    }
    
    private String getRiGanExplanation(String gan) {
        if (gan == null) return "日干代表自身，反映个人状态";
        switch (gan) {
            case "甲": return "甲木参天大树，主领袖气质，积极进取。宜发挥领导力，把握机遇";
            case "乙": return "乙木花草之木，主柔顺仁慈，以柔克刚。宜耐心处事，善于变通";
            case "丙": return "丙火太阳之火，主光明热情，积极向上。宜展现才华，追求卓越";
            case "丁": return "丁火灯烛之火，主文明细致，精益求精。宜注重细节，追求完美";
            case "戊": return "戊土大地之土，主稳重诚信，脚踏实地。宜诚实守信，稳步发展";
            case "己": return "己土田园之土，主包容厚德，积累福报。宜宽厚待人，善积功德";
            case "庚": return "庚金刀剑之金，主果断刚毅，勇往直前。宜当机立断，不畏艰难";
            case "辛": return "辛金首饰之金，主精致细腻，追求品质。宜注重品质，精益求精";
            case "壬": return "壬水江海之水，主智慧流动，灵活变通。宜顺势而为，善于应变";
            case "癸": return "癸水雨露之水，主聪明神秘，低调谋划。宜暗中谋划，厚积薄发";
            default: return "日干代表自身，反映个人状态";
        }
    }
    
    private String getShiGanExplanation(String gan) {
        if (gan == null) return "时干代表事情，反映事物发展";
        switch (gan) {
            case "甲": return "甲木主事：事情得贵人相助，发展顺利。宜积极推动，把握良机";
            case "乙": return "乙木主事：事情柔顺发展，需要耐心。宜循序渐进，静待时机";
            case "丙": return "丙火主事：事情明朗迅速，机遇显现。宜快速行动，把握机遇";
            case "丁": return "丁火主事：事情需要细致处理。宜注重细节，精益求精";
            case "戊": return "戊土主事：事情稳重推进，根基稳固。宜稳扎稳打，步步为营";
            case "己": return "己土主事：事情需要包容忍耐。宜以柔克刚，耐心等待";
            case "庚": return "庚金主事：事情需要果断决策。宜当机立断，勇往直前";
            case "辛": return "辛金主事：事情需要精益求精。宜注重品质，追求卓越";
            case "壬": return "壬水主事：事情变化多端。宜灵活应对，顺势而为";
            case "癸": return "癸水主事：事情暗藏玄机。宜谨慎分析，暗中谋划";
            default: return "时干代表事情，反映事物发展";
        }
    }
    
    private String getDayAdviceByAll(String star, String door, String god, String dayGan) {
        StringBuilder sb = new StringBuilder();
        
        // 根据门给出建议
        if (door != null) {
            switch (door) {
                case "开": sb.append("开门大吉 宜开创事业\n"); break;
                case "休": sb.append("休门利养 宜休息调整\n"); break;
                case "生": sb.append("生门兴旺 宜求财发展\n"); break;
                case "伤": sb.append("伤门有损 防破财受伤\n"); break;
                case "杜": sb.append("杜门闭塞 宜静守等待\n"); break;
                case "景": sb.append("景门光明 利考试展示\n"); break;
                case "死": sb.append("死门不利 诸事需谨慎\n"); break;
                case "惊": sb.append("惊门不安 防口舌是非\n"); break;
            }
        }
        
        // 根据神给出建议
        if (god != null) {
            switch (god) {
                case "值符": sb.append("值符贵人 利领导决策\n"); break;
                case "螣蛇": sb.append("螣蛇虚诈 防欺骗陷阱\n"); break;
                case "太阴": sb.append("太阴暗助 利隐秘行事\n"); break;
                case "六合": sb.append("六合和合 利合作婚姻\n"); break;
                case "白虎": sb.append("白虎凶险 谨慎防意外\n"); break;
                case "玄武": sb.append("玄武盗贼 防财物损失\n"); break;
                case "九地": sb.append("九地稳固 利根基建设\n"); break;
                case "九天": sb.append("九天高远 利开拓发展\n"); break;
            }
        }
        
        // 根据日干给出建议
        String wuxing = getWuXing(dayGan);
        sb.append("日干").append(dayGan).append("(").append(wuxing).append(") ");
        switch (wuxing) {
            case "木": sb.append("利东方 春季\n"); break;
            case "火": sb.append("利南方 夏季\n"); break;
            case "土": sb.append("利中央 四季\n"); break;
            case "金": sb.append("利西方 秋季\n"); break;
            case "水": sb.append("利北方 冬季\n"); break;
        }
        
        return sb.toString();
    }
    
    private String getDayAdvice(String star, String door, String dayGan) {
        if (star == null || door == null) return "运势平稳";
        
        if (door.equals("开") || door.equals("生")) {
            return "大吉之门\n适合开创事业";
        } else if (door.equals("休")) {
            return "休养之门\n适合学习进修";
        } else if (door.equals("死") || door.equals("惊") || door.equals("伤")) {
            return "凶险之门\n宜静不宜动";
        } else if (door.equals("景")) {
            return "光明之门\n适合展示才华";
        } else if (door.equals("杜")) {
            return "阻塞之门\n宜防守不宜进攻";
        }
        
        return "运势平稳";
    }
    
    private String getStarMeaningShort(String star) {
        if (star == null) return "吉星高照 运势不错";
        switch (star) {
            case "天蓬": return "智慧之星 聪明机智\n适合策划谋略";
            case "天芮": return "疾病之星 身体虚弱\n需注意健康";
            case "天冲": return "冲动之星 行动力强\n防冲动误事";
            case "天辅": return "辅佐之星 贵人相助\n利于交际";
            case "天禽": return "中正之星 为人正直\n运势平稳";
            case "天心": return "仁慈之星 有爱心\n利于医疗";
            case "天柱": return "刚直之星 性格刚强\n利于决断";
            case "天任": return "任劳之星 勤奋努力\n财运不错";
            case "天英": return "文明之星 利于考试\n利创作";
            default: return "吉星高照 运势不错";
        }
    }
    
    private String getDoorMeaningShort(String door) {
        if (door == null) return "中平之门 运势一般";
        switch (door) {
            case "休": return "休养生息 利于休息\n调整状态";
            case "生": return "生机勃勃 大吉之门\n利于发展";
            case "伤": return "伤害损耗 破财之星\n谨慎行事";
            case "杜": return "阻塞不通 宜静不宜动\n避免冒险";
            case "景": return "光明景象 利于考试\n利于展示";
            case "死": return "死气沉沉 诸事不宜\n保守为上";
            case "惊": return "惊恐不安 防口舌是非\n防纠纷";
            case "开": return "开放顺利 大吉之门\n百事皆宜";
            default: return "中平之门 运势一般";
        }
    }
    
    private String[] getYiActivitiesShort(String star, String door) {
        java.util.ArrayList<String> yiList = new java.util.ArrayList<>();
        
        yiList.add("开业创业  投资理财");
        yiList.add("商务洽谈  签订合同");
        yiList.add("学习进修  考试面试");
        yiList.add("求医问诊  医疗养生");
        yiList.add("祭祀祈福  行善积德");
        
        return yiList.toArray(new String[0]);
    }
    
    private String[] getJiActivitiesShort(String star, String door) {
        java.util.ArrayList<String> jiList = new java.util.ArrayList<>();
        
        jiList.add("重大决策  重要签约");
        jiList.add("冒险投资  远行迁徙");
        jiList.add("冲动决策  争执纠纷");
        jiList.add("赌博投机  诉讼纠纷");
        jiList.add("口舌是非  暴饮暴食");
        
        return jiList.toArray(new String[0]);
    }
    
    private String getPalaceCombinationAdvice(String star, String door, String god, String wangCui) {
        if (star == null || star.isEmpty()) return "信息不足";
        
        StringBuilder sb = new StringBuilder();
        
        boolean isLuckyStar = star.equals("天辅") || star.equals("天心") || star.equals("天禽") || star.equals("天任");
        boolean isLuckyDoor = door != null && !door.isEmpty() && (door.equals("开") || door.equals("休") || door.equals("生"));
        boolean isLuckyGod = god != null && (god.equals("值符") || god.equals("太阴") || god.equals("六合"));
        
        if (isLuckyStar && isLuckyDoor) {
            sb.append("吉上加吉，诸事顺遂，把握良机。");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("运势平稳，有贵人相助，需努力争取。");
        } else if (door != null && !door.isEmpty() && (door.equals("死") || door.equals("惊") || door.equals("伤"))) {
            sb.append("运势欠佳，宜守不宜攻，谨慎行事。");
        } else {
            sb.append("运势一般，稳中求进，顺其自然。");
        }
        
        if (god != null && !god.isEmpty()) {
            if (god.equals("值符")) sb.append(" 值符坐镇，贵人相助。");
            else if (god.equals("螣蛇")) sb.append(" 螣蛇缠绕，防虚惊怪异。");
            else if (god.equals("太阴")) sb.append(" 太阴庇佑，暗中助力。");
            else if (god.equals("六合")) sb.append(" 六合和合，利于合作。");
            else if (god.equals("白虎")) sb.append(" 白虎临门，防血光之灾。");
            else if (god.equals("玄武")) sb.append(" 玄武当道，防小人暗算。");
            else if (god.equals("九地")) sb.append(" 九地稳固，宜守成待时。");
            else if (god.equals("九天")) sb.append(" 九天升腾，利进取发展。");
        }
        
        if (wangCui != null) {
            if (wangCui.equals("旺")) sb.append(" 旺相之地，力量最强。");
            else if (wangCui.equals("相")) sb.append(" 得生助力，顺势而为。");
            else if (wangCui.equals("休")) sb.append(" 休养生息，不宜强求。");
            else if (wangCui.equals("囚")) sb.append(" 受克受制，困难重重。");
            else if (wangCui.equals("死")) sb.append(" 死气沉沉，宜避开。");
        }
        
        return sb.toString();
    }
    
    private String getTimeFortuneShort(String timeZhi) {
        if (timeZhi == null) return "时辰吉利 运势平稳";
        switch (timeZhi) {
            case "子": return "一阳初生 阴气最盛\n宜静养安神 利于思考";
            case "丑": return "丑土藏金 肝经当令\n宜深度睡眠 利修复";
            case "寅": return "肺经当令 朝气蓬勃\n宜早起活动 利运动";
            case "卯": return "大肠经当令 朝阳升起\n宜排便清肠 利排毒";
            case "辰": return "胃经当令 早餐时分\n宜进食营养 利消化";
            case "巳": return "脾经当令 精力充沛\n宜勤奋工作 利脑力";
            case "午": return "心经当令 阳气最盛\n宜适当休息 利午休";
            case "未": return "小肠经当令 午餐消化\n宜清淡饮食 利吸收";
            case "申": return "膀胱经当令 运动时机\n宜适量运动 利锻炼";
            case "酉": return "肾经当令 日落时分\n宜休息放松 利养精";
            case "戌": return "心包经当令 夜幕降临\n宜放松娱乐 利社交";
            case "亥": return "三焦经当令 万物归宁\n宜准备休息 利入睡";
            default: return "时辰吉利 运势平稳";
        }
    }
    
    private String getGodMeaningShort(String god) {
        if (god == null || god.isEmpty()) return "无神临宫";
        switch (god) {
            case "值符": return "领导贵人 大事可成";
            case "螣蛇": return "虚诈多变 小心陷阱";
            case "太阴": return "暗中助力 隐秘行事";
            case "六合": return "合作顺利 婚姻和谐";
            case "白虎": return "凶险压力 谨慎应对";
            case "玄武": return "盗贼欺骗 防范小人";
            case "九地": return "稳定持久 根基深厚";
            case "九天": return "高远发展 上升空间";
            default: return "神煞临宫";
        }
    }
    
    private String getOverallAdviceShort(boolean isYangDun, int ju, String star, String door, String god, String dayGan) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("📊 命盘分析:\n");
        
        if (isYangDun) {
            sb.append("阳遁").append(ju).append("局  阳气旺盛\n");
            sb.append("利主动出击 把握机遇\n\n");
        } else {
            sb.append("阴遁").append(ju).append("局  阴气收敛\n");
            sb.append("利静心守成 蓄势待发\n\n");
        }
        
        sb.append("⭐ 星门神吉凶:\n");
        
        String[] luckyStars = {"天辅", "天心", "天禽", "天任"};
        String[] luckyDoors = {"开", "休", "生"};
        String[] neutralDoors = {"杜", "景"};
        String[] luckyGods = {"值符", "太阴", "六合", "九地", "九天"};
        
        boolean isLuckyStar = false, isLuckyDoor = false, isLuckyGod = false;
        boolean isNeutralDoor = false;
        
        if (star != null) {
            for (String s : luckyStars) if (s.equals(star)) isLuckyStar = true;
        }
        if (door != null) {
            for (String d : luckyDoors) if (d.equals(door)) isLuckyDoor = true;
            for (String d : neutralDoors) if (d.equals(door)) isNeutralDoor = true;
        }
        if (god != null) {
            for (String g : luckyGods) if (g.equals(god)) isLuckyGod = true;
        }
        
        int luckyCount = (isLuckyStar ? 1 : 0) + (isLuckyDoor ? 1 : 0) + (isLuckyGod ? 1 : 0);
        
        if (luckyCount >= 3) {
            sb.append("★★★ 大吉 ★★★\n");
            sb.append("星门神皆吉 运势极佳\n把握机遇  积极行动\n\n");
        } else if (luckyCount == 2) {
            sb.append("★★ 小吉 ★★\n");
            sb.append("多数吉利 运势良好\n稳中求进  顺势而为\n\n");
        } else if (luckyCount == 1 || isNeutralDoor) {
            sb.append("★ 平平 ★\n");
            sb.append("运势一般 谨慎行事\n稳扎稳打  低调为宜\n\n");
        } else {
            sb.append("⚠ 注意 ⚠\n");
            sb.append("运势低迷 宜守不宜动\n趋吉避凶  化解不利\n\n");
        }
        
        sb.append("💡 综合建议:\n");
        sb.append(getDayAdviceByAll(star, door, god, dayGan));
        
        return sb.toString();
    }
    
    private String getRiGanAdviceSimple(String riGan) {
        if (riGan == null) return "审视自身，做出合适调整";
        switch (riGan) {
            case "甲": return "甲木参天，主贵人领袖，宜积极进取";
            case "乙": return "乙木花草，主柔顺仁慈，宜以柔克刚";
            case "丙": return "丙火太阳，主光明热情，宜展现才华";
            case "丁": return "丁火灯烛，主文明细致，宜注重细节";
            case "戊": return "戊土大地，主稳重诚信，宜脚踏实地";
            case "己": return "己土田园，主包容厚德，宜宽厚待人";
            case "庚": return "庚金刀剑，主果断刚毅，宜当机立断";
            case "辛": return "辛金首饰，主精致细腻，宜注重品质";
            case "壬": return "壬水江海，主智慧流动，宜灵活变通";
            case "癸": return "癸水雨露，主聪明神秘，宜低调谋划";
            default: return "审视自身，做出合适调整";
        }
    }
    
    private String getShiGanAdviceSimple(String shiGan) {
        if (shiGan == null) return "关注事情发展动向";
        switch (shiGan) {
            case "甲": return "贵人相助，事情有望得到有力支持";
            case "乙": return "事情柔顺发展，需要耐心等待";
            case "丙": return "事情明朗，进展迅速，机遇显现";
            case "丁": return "事情需细致处理，注重细节方能成功";
            case "戊": return "事情稳重推进，根基稳固，不易动摇";
            case "己": return "事情需包容忍耐，以柔克刚";
            case "庚": return "事情需果断决策，勇往直前";
            case "辛": return "事情需精益求精，注重品质";
            case "壬": return "事情变化多端，需要灵活应对";
            case "癸": return "事情暗藏玄机，需要谨慎分析";
            default: return "关注事情发展动向";
        }
    }
    
    private String getYiActivitiesConcise(String star, String door) {
        StringBuilder sb = new StringBuilder();
        if (door != null) {
            if (door.equals("开") || door.equals("生")) sb.append("开业创业、求财投资、");
            if (door.equals("休")) sb.append("休息养生、学习进修、");
            if (door.equals("景")) sb.append("考试面试、展示才华、");
        }
        if (star != null) {
            if (star.equals("天辅") || star.equals("天心")) sb.append("求医问诊、");
            if (star.equals("天任")) sb.append("工作置业、");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        else sb.append("祈福祭祀、拜访亲友");
        return sb.toString();
    }
    
    private String getJiActivitiesConcise(String star, String door) {
        StringBuilder sb = new StringBuilder();
        if (door != null) {
            if (door.equals("死") || door.equals("惊") || door.equals("伤")) sb.append("重大决策、冒险投资、");
        }
        if (star != null) {
            if (star.equals("天芮")) sb.append("动土手术、");
            if (star.equals("天冲")) sb.append("冲动决策、争执纠纷、");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        else sb.append("赌博投机、诉讼纠纷");
        return sb.toString();
    }
    
    private String getWangCuiDescription(String[] wangCui, int riGanPalace) {
        if (riGanPalace < 0 || riGanPalace >= wangCui.length) {
            return "旺衰状态不明";
        }
        String status = wangCui[riGanPalace];
        switch (status) {
            case "旺": return "落宫得令【旺】，气势旺盛，百事顺遂，力量最强";
            case "相": return "落宫得生【相】，次吉之象，得他人助力，事多有成";
            case "休": return "落宫休息【休】，平和安宁，不宜过分强求";
            case "囚": return "落宫受制【囚】，困顿受阻，事多不利，需谨慎";
            case "死": return "落宫处死【死】，衰败不利，诸事不宜，宜守不宜攻";
            default: return "旺衰状态不明";
        }
    }
    
    private String getPalaceKeyInfo(String star, String door, String wangCui) {
        if (!wangCui.equals("旺") && !wangCui.equals("死")) return null;
        
        StringBuilder sb = new StringBuilder();
        sb.append(star);
        if (!door.isEmpty()) sb.append(" ").append(door).append("门");
        sb.append(" ").append(wangCui);
        
        return sb.toString();
    }
    
    private String getTimeFortuneConcise(String timeZhi) {
        if (timeZhi == null) return "时辰吉利，运势平稳";
        switch (timeZhi) {
            case "子": return "一阳初生，宜静养安神";
            case "丑": return "肝经当令，宜深度睡眠";
            case "寅": return "肺经当令，宜早起活动";
            case "卯": return "大肠经当令，宜排便清肠";
            case "辰": return "胃经当令，宜进食营养";
            case "巳": return "脾经当令，宜勤奋工作";
            case "午": return "心经当令，宜适当休息";
            case "未": return "小肠经当令，宜清淡饮食";
            case "申": return "膀胱经当令，宜适量运动";
            case "酉": return "肾经当令，宜休息放松";
            case "戌": return "心包经当令，宜放松娱乐";
            case "亥": return "三焦经当令，宜准备休息";
            default: return "时辰吉利，运势平稳";
        }
    }
    
    private String getOverallAdviceConcise(boolean isYangDun, int ju, String star, String door, String god, String dayGan) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(isYangDun ? "阳遁" : "阴遁").append(ju).append("局\n");
        
        String[] luckyStars = {"天辅", "天心", "天禽", "天任"};
        String[] luckyDoors = {"开", "休", "生"};
        String[] neutralDoors = {"杜", "景"};
        
        boolean isLuckyStar = false, isLuckyDoor = false;
        if (star != null) for (String s : luckyStars) if (s.equals(star)) isLuckyStar = true;
        if (door != null) {
            for (String d : luckyDoors) if (d.equals(door)) isLuckyDoor = true;
        }
        
        if (isLuckyStar && isLuckyDoor) {
            sb.append("★★★ 大吉 ★★★\n");
            sb.append("星门皆吉，今日运势极佳\n把握机遇，积极行动");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("★★ 小吉 ★★\n");
            sb.append("星门一吉，运势平稳\n顺势而为，稳中求进");
        } else {
            sb.append("⚠ 注意 ⚠\n");
            sb.append("星门欠佳，宜守不宜动\n趋吉避凶，谨慎行事");
        }
        
        return sb.toString();
    }

    private String getFourPillarAnalysis(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        StringBuilder sb = new StringBuilder();
        
        String yearGan = yearPillar.substring(0, 1);
        String yearZhi = yearPillar.substring(1, 2);
        String monthGan = monthPillar.substring(0, 1);
        String monthZhi = monthPillar.substring(1, 2);
        String dayGan = dayPillar.substring(0, 1);
        String dayZhi = dayPillar.substring(1, 2);
        String timeGan = timePillar.substring(0, 1);
        String timeZhi = timePillar.substring(1, 2);
        
        String dayGanWuXing = getWuXing(dayGan);
        
        sb.append("<font color='#FFD700'><b>📊 四柱命理分析</b></font><br/><br/>");
        
        sb.append("<font color='#FFD700'><b>四柱排布：</b></font>").append(yearPillar).append(" ").append(monthPillar).append(" ").append(dayPillar).append(" ").append(timePillar).append("<br/><br/>");
        
        sb.append("<font color='#98D8F0'>【日主核心】</font><br/>");
        sb.append("日主 <font color='#FFD700'><b>").append(dayGan).append("</b></font> 属").append(dayGanWuXing).append("，").append(getGanDescription(dayGan)).append("<br/>");
        sb.append("<font color='#8899AA'>").append(getRiGanDetailedAnalysis(dayGan)).append("</font><br/><br/>");
        
        sb.append("<font color='#98D8F0'>【五行力量分析】</font><br/>");
        String[][] pillars = {{yearGan, yearZhi}, {monthGan, monthZhi}, {dayGan, dayZhi}, {timeGan, timeZhi}};
        String[] pillarNames = {"年", "月", "日", "时"};
        int shengCount = 0, keCount = 0, biCount = 0;
        
        for (int i = 0; i < pillars.length; i++) {
            String pGan = pillars[i][0];
            String pZhi = pillars[i][1];
            String pName = pillarNames[i];
            
            String pGanWuXing = getWuXing(pGan);
            String pZhiWuXing = getWuXing(pZhi);
            
            sb.append(pName).append("柱 ");
            sb.append(pGan).append("(").append(pGanWuXing).append(")");
            
            String ganRelation = "";
            if (pGanWuXing.equals(dayGanWuXing)) {
                ganRelation = "<font color='#C0C0C0'>比和</font>";
                if (i != 2) biCount++;
            } else if (isSheng(pGanWuXing, dayGanWuXing)) {
                ganRelation = "<font color='#90EE90'>生扶</font>";
                shengCount++;
            } else if (isKe(pGanWuXing, dayGanWuXing)) {
                ganRelation = "<font color='#FF6B6B'>克制</font>";
                keCount++;
            } else if (isSheng(dayGanWuXing, pGanWuXing)) {
                ganRelation = "<font color='#FFA500'>泄耗</font>";
                keCount++;
            }
            
            sb.append(ganRelation);
            
            if (i == 2) {
                sb.append(" · ").append(pZhi).append("(").append(pZhiWuXing).append(")");
                String zhiRelation = "";
                if (pZhiWuXing.equals(dayGanWuXing)) {
                    zhiRelation = "<font color='#C0C0C0'>比和</font>";
                } else if (isSheng(pZhiWuXing, dayGanWuXing)) {
                    zhiRelation = "<font color='#90EE90'>生扶</font>";
                    shengCount++;
                } else if (isKe(pZhiWuXing, dayGanWuXing)) {
                    zhiRelation = "<font color='#FF6B6B'>克制</font>";
                    keCount++;
                } else if (isSheng(dayGanWuXing, pZhiWuXing)) {
                    zhiRelation = "<font color='#FFA500'>泄耗</font>";
                    keCount++;
                }
                sb.append(zhiRelation);
            }
            
            sb.append("<br/>");
        }
        
        sb.append("<br/>");
        sb.append("<font color='#98D8F0'>【命局强弱判断】</font><br/>");
        sb.append("生扶之力：").append(shengCount).append(" · 克制之力：").append(keCount).append(" · 比和之力：").append(biCount).append("<br/>");
        
        if (shengCount + biCount > keCount) {
            sb.append("<font color='#90EE90'><b>身强</b></font>（生扶").append(shengCount).append("+比和").append(biCount).append(">克制").append(keCount).append("）<br/>");
            sb.append("<font color='#8899AA'>").append(getStrengthReason("强", dayGan, dayGanWuXing, shengCount, keCount, biCount)).append("</font><br/>");
            sb.append("<font color='#90EE90'>宜：</font>").append(getAdviceForStrength("强", dayGan, dayGanWuXing)).append("<br/>");
            sb.append("<font color='#FF6B6B'>忌：</font>").append(getAdviceForStrength("强忌", dayGan, dayGanWuXing)).append("<br/>");
        } else if (keCount > shengCount + biCount) {
            sb.append("<font color='#FF6B6B'><b>身弱</b></font>（克制").append(keCount).append(">生扶").append(shengCount).append("+比和").append(biCount).append("）<br/>");
            sb.append("<font color='#8899AA'>").append(getStrengthReason("弱", dayGan, dayGanWuXing, shengCount, keCount, biCount)).append("</font><br/>");
            sb.append("<font color='#90EE90'>宜：</font>").append(getAdviceForStrength("弱", dayGan, dayGanWuXing)).append("<br/>");
            sb.append("<font color='#FF6B6B'>忌：</font>").append(getAdviceForStrength("弱忌", dayGan, dayGanWuXing)).append("<br/>");
        } else {
            sb.append("<font color='#FFD700'><b>中和</b></font>（生扶").append(shengCount).append("+比和").append(biCount).append("=克制").append(keCount).append("）<br/>");
            sb.append("<font color='#8899AA'>原因：五行力量均衡，无明显偏旺偏衰，为最佳命局状态</font><br/>");
            sb.append("<font color='#FFD700'>宜：</font>顺势而为，把握良机，保持平衡发展<br/>");
            sb.append("<font color='#FFD700'>忌：</font>过度强求，偏废一方，打破平衡<br/>");
        }
        
        sb.append("<br/>");
        sb.append("<font color='#98D8F0'>【五行喜忌】</font><br/>");
        sb.append(getFiveElementXiJiDetailed(dayGan, dayGanWuXing, yearGan, yearZhi, monthGan, monthZhi, dayZhi, timeGan, timeZhi));
        
        return sb.toString();
    }

    private String getStrengthReason(String type, String dayGan, String dayGanWuXing, int sheng, int ke, int bi) {
        StringBuilder sb = new StringBuilder();
        if (type.equals("强")) {
            if (sheng > 0) {
                sb.append("四柱中有").append(sheng).append("个干支生扶日主").append(dayGan).append("(").append(dayGanWuXing).append(")");
            }
            if (bi > 0) {
                if (sb.length() > 0) sb.append("，");
                sb.append("有").append(bi).append("个干支与日主比和");
            }
            sb.append("，生扶力量充足，故").append(dayGan).append("日主气势强盛");
        } else {
            if (ke > 0) {
                sb.append("四柱中有").append(ke).append("个干支克制或泄耗日主").append(dayGan).append("(").append(dayGanWuXing).append(")");
            }
            if (sheng + bi == 0) {
                if (sb.length() > 0) sb.append("，");
                sb.append("而生扶之力不足");
            }
            sb.append("，克制力量过强，故").append(dayGan).append("日主气势衰弱");
        }
        return sb.toString();
    }
    
    private String getAdviceForStrength(String type, String dayGan, String dayGanWuXing) {
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        String xieHao = shengMap.get(dayGanWuXing);
        String keHao = keMap.get(dayGanWuXing);
        String shengHao = "";
        for (String key : shengMap.keySet()) {
            if (shengMap.get(key).equals(dayGanWuXing)) {
                shengHao = key;
                break;
            }
        }
        
        if (type.equals("强")) {
            return "泄耗（" + xieHao + "）、克制（" + keHao + "），创业开拓，发挥才华，注重变通，理财投资";
        } else if (type.equals("强忌")) {
            return "生扶（" + shengHao + "）、比和（" + dayGanWuXing + "），固执己见，过度自信，忽视风险";
        } else if (type.equals("弱")) {
            return "生扶（" + shengHao + "）、比和（" + dayGanWuXing + "），稳健守成，学习积累，结交贵人，增强自信";
        } else {
            return "泄耗（" + xieHao + "）、克制（" + keHao + "），过度消耗，承担过重，冒险投机";
        }
    }
    
    private String getFiveElementXiJiDetailed(String dayGan, String dayGanWuXing, String yearGan, String yearZhi, String monthGan, String monthZhi, String dayZhi, String timeGan, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        
        int count = 0;
        String[] allGans = {yearGan, monthGan, dayGan, timeGan};
        String[] allZhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        
        for (String gan : allGans) {
            if (getWuXing(gan).equals(dayGanWuXing)) count++;
        }
        for (String zhi : allZhis) {
            if (getWuXing(zhi).equals(dayGanWuXing)) count++;
        }
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        String xieHao = shengMap.get(dayGanWuXing);
        String keHao = keMap.get(dayGanWuXing);
        String shengHao = "";
        for (String key : shengMap.keySet()) {
            if (shengMap.get(key).equals(dayGanWuXing)) {
                shengHao = key;
                break;
            }
        }
        
        sb.append("日主").append(dayGan).append("属").append(dayGanWuXing).append("，");
        sb.append("四柱中").append(dayGanWuXing).append("出现").append(count).append("次");
        
        if (count >= 3) {
            sb.append("，<font color='#FF6B6B'>偏旺</font>");
            sb.append("（比劫多）");
            sb.append("<br/>");
            sb.append("<font color='#90EE90'>喜用神：</font>").append(xieHao).append("（泄秀）、").append(keHao).append("（制劫）");
            sb.append("<br/>");
            sb.append("<font color='#FF6B6B'>忌神：</font>").append(shengHao).append("（生身）、").append(dayGanWuXing).append("（比劫帮身）");
            sb.append("<br/>");
            sb.append("<font color='#8899AA'>原因：日主").append(dayGanWuXing).append("过旺，需要").append(xieHao).append("泄其秀气，").append(keHao).append("制其旺气，方能平衡");
        } else if (count <= 1) {
            sb.append("，<font color='#FF6B6B'>偏弱</font>");
            sb.append("（印比少）");
            sb.append("<br/>");
            sb.append("<font color='#90EE90'>喜用神：</font>").append(shengHao).append("（生扶）、").append(dayGanWuXing).append("（比劫帮身）");
            sb.append("<br/>");
            sb.append("<font color='#FF6B6B'>忌神：</font>").append(xieHao).append("（泄耗）、").append(keHao).append("（克制）");
            sb.append("<br/>");
            sb.append("<font color='#8899AA'>原因：日主").append(dayGanWuXing).append("偏弱，需要").append(shengHao).append("来生扶，").append(dayGanWuXing).append("同类来相助，方能增强气势");
        } else {
            sb.append("，<font color='#FFD700'>中和</font>");
            sb.append("（五行均衡）");
            sb.append("<br/>");
            sb.append("<font color='#FFD700'>喜用神：</font>视具体组合而定，无明显喜忌");
            sb.append("<br/>");
            sb.append("<font color='#8899AA'>原因：日主力量适中，五行不偏不倚，为贵命格局，宜顺势而为</font>");
        }
        
        return sb.toString();
    }
    
    private String getDestinyOverview(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        StringBuilder sb = new StringBuilder();
        
        String yearGan = yearPillar.substring(0, 1);
        String yearZhi = yearPillar.substring(1, 2);
        String monthGan = monthPillar.substring(0, 1);
        String monthZhi = monthPillar.substring(1, 2);
        String dayGan = dayPillar.substring(0, 1);
        String dayZhi = dayPillar.substring(1, 2);
        String timeGan = timePillar.substring(0, 1);
        String timeZhi = timePillar.substring(1, 2);
        
        String dayGanWuXing = getWuXing(dayGan);
        String[][] pillars = {{yearGan, yearZhi}, {monthGan, monthZhi}, {dayGan, dayZhi}, {timeGan, timeZhi}};
        String[] pillarNames = {"年柱", "月柱", "日柱", "时柱"};
        
        // ═══════════════════════════════════
        // 第一部分：专业命盘（专业人士参考）
        // ═══════════════════════════════════
        sb.append("<font color='#FF6B6B'><b>━━━ 专业命盘 ━━━</b></font><br/><br/>");
        
        // 1. 四柱排布 + 纳音 + 生肖
        sb.append("<font color='#FFD700'><b>◆ 四柱排布</b></font><br/>");
        sb.append(yearPillar).append("　").append(monthPillar).append("　").append(dayPillar).append("　").append(timePillar).append("<br/><br/>");
        
        sb.append("<font color='#FFD700'><b>◆ 纳音五行</b></font>　<font color='#8899AA'>（干支组合的意象属性，反映命局基调）</font><br/>");
        String nYear = getNayin(yearGan, yearZhi);
        String nMonth = getNayin(monthGan, monthZhi);
        String nDay = getNayin(dayGan, dayZhi);
        String nTime = getNayin(timeGan, timeZhi);
        sb.append("年·").append(nYear).append("　月·").append(nMonth).append("<br/>");
        sb.append("日·").append(nDay).append("　时·").append(nTime).append("<br/><br/>");
        
        sb.append("<font color='#FFD700'><b>◆ 生肖属相</b></font>　年支").append(yearZhi).append("·属").append(getZodiacNameFromZhi(yearZhi)).append("<br/><br/>");
        
        // 2. 日主核心分析
        sb.append("<font color='#FFD700'><b>◆ 日主核心</b></font>　<font color='#8899AA'>（日干代表命主本人，是命局核心）</font><br/>");
        sb.append("日主 <font color='#FFD700'><b>").append(dayGan).append("</b></font> 属<font color='#90EE90'><b>").append(dayGanWuXing).append("</b></font>，").append(getGanDescription(dayGan)).append("<br/>");
        sb.append("<font color='#8899AA'>").append(getRiGanDetailedAnalysis(dayGan)).append("</font><br/><br/>");
        
        // 十二长生
        String stageDay = getTwelveStage(dayGan, dayZhi);
        String stageTime = getTwelveStage(dayGan, timeZhi);
        String stageMonth = getTwelveStage(dayGan, monthZhi);
        sb.append("<font color='#FFD700'><b>◆ 十二长生</b></font>　<font color='#8899AA'>（日主在各柱地支所处的生命阶段）</font><br/>");
        sb.append("日主").append(dayGan).append("在日支").append(dayZhi).append("：<font color='#90EE90'><b>").append(stageDay).append("</b></font>").append(" — ").append(getTwelveStageExplanation(stageDay)).append("<br/>");
        sb.append("日主").append(dayGan).append("在时支").append(timeZhi).append("：").append(stageTime).append(" — ").append(getTwelveStageExplanation(stageTime)).append("<br/>");
        sb.append("日主").append(dayGan).append("在月支").append(monthZhi).append("（月令）：").append(stageMonth).append(" — ").append(getTwelveStageExplanation(stageMonth)).append("<br/><br/>");
        
        // 3. 五行力量分析
        sb.append("<font color='#FFD700'><b>◆ 五行力量分析</b></font>　<font color='#8899AA'>（各柱与日主的生克关系）</font><br/>");
        int shengCount = 0, keCount = 0, biCount = 0;
        
        for (int i = 0; i < pillars.length; i++) {
            String pGan = pillars[i][0];
            String pZhi = pillars[i][1];
            String pName = pillarNames[i];
            
            String pGanWuXing = getWuXing(pGan);
            String pZhiWuXing = getWuXing(pZhi);
            
            // 天干部分
            sb.append(pName).append("天干 ").append(pGan).append("(").append(pGanWuXing).append(")");
            if (pGanWuXing.equals(dayGanWuXing)) {
                sb.append("<font color='#C0C0C0'> ─ 比和</font>");
                if (i != 2) biCount++;
            } else if (isSheng(pGanWuXing, dayGanWuXing)) {
                sb.append("<font color='#90EE90'> → 生扶日主</font>");
                shengCount++;
            } else if (isKe(pGanWuXing, dayGanWuXing)) {
                sb.append("<font color='#FF6B6B'> → 克制日主</font>");
                keCount++;
            } else if (isSheng(dayGanWuXing, pGanWuXing)) {
                sb.append("<font color='#FFA500'> ← 被日主泄耗</font>");
                keCount++;
            } else if (isKe(dayGanWuXing, pGanWuXing)) {
                sb.append("<font color='#87CEEB'> ← 被日主所克</font>");
                keCount++;
            }
            sb.append("<br/>");
            
            // 日柱地支单独列出
            if (i == 2) {
                sb.append(pName).append("地支 ").append(pZhi).append("(").append(pZhiWuXing).append(")");
                if (pZhiWuXing.equals(dayGanWuXing)) {
                    sb.append("<font color='#C0C0C0'> ─ 比和</font>");
                } else if (isSheng(pZhiWuXing, dayGanWuXing)) {
                    sb.append("<font color='#90EE90'> → 生扶日主</font>");
                    shengCount++;
                } else if (isKe(pZhiWuXing, dayGanWuXing)) {
                    sb.append("<font color='#FF6B6B'> → 克制日主</font>");
                    keCount++;
                } else if (isSheng(dayGanWuXing, pZhiWuXing)) {
                    sb.append("<font color='#FFA500'> ← 被日主泄耗</font>");
                    keCount++;
                } else if (isKe(dayGanWuXing, pZhiWuXing)) {
                    sb.append("<font color='#87CEEB'> ← 被日主所克</font>");
                    keCount++;
                }
                sb.append("<br/>");
            }
        }
        sb.append("<br/>");
        
        // 4. 命局强弱判断
        sb.append("<font color='#FFD700'><b>◆ 命局强弱判断</b></font><br/>");
        sb.append("生扶之力：").append(shengCount).append("　克制之力：").append(keCount).append("　比和之力：").append(biCount).append("<br/>");
        
        int balance = shengCount + biCount - keCount;
        if (balance > 0) {
            sb.append("<font color='#90EE90'><b>日主身强</b></font>（生扶").append(shengCount).append("+比和").append(biCount).append("&gt;克制").append(keCount).append("）<br/>");
            sb.append("<font color='#8899AA'>解释：日主").append(dayGan).append("在命局中得到较多生扶和同类相助，气势旺盛、精力充沛，有较强的自我驱动力和抗压能力。</font><br/><br/>");
        } else if (balance < 0) {
            sb.append("<font color='#FF6B6B'><b>日主身弱</b></font>（克制").append(keCount).append("&gt;生扶").append(shengCount).append("+比和").append(biCount).append("）<br/>");
            sb.append("<font color='#8899AA'>解释：日主").append(dayGan).append("在命局中受到的克制和泄耗较多，气势偏弱，需要借助外界力量支持，更适合团队合作而非单打独斗。</font><br/><br/>");
        } else {
            sb.append("<font color='#FFD700'><b>日主中和</b></font>（生扶").append(shengCount).append("+比和").append(biCount).append("=克制").append(keCount).append("）<br/>");
            sb.append("<font color='#8899AA'>解释：五行力量均衡，是最理想的命局状态，适应能力强，能够根据环境灵活调整策略。</font><br/><br/>");
        }
        
        // 5. 五行喜忌
        sb.append("<font color='#FFD700'><b>◆ 五行喜忌</b></font><br/>");
        sb.append(getFiveElementXiJiDetailed(dayGan, dayGanWuXing, yearGan, yearZhi, monthGan, monthZhi, dayZhi, timeGan, timeZhi));
        sb.append("<br/><br/>");
        
        // 6. 十神全分析（天干）
        sb.append("<font color='#FFD700'><b>◆ 十神分析</b></font>　<font color='#8899AA'>（以日主为基准，看各天干与日主的十神关系）</font><br/>");
        String[] ganShen = getTenGods(dayGan, new String[]{yearGan, monthGan, dayGan, timeGan});
        String[] labelNames = {"年","月","日","时"};
        
        for (int i = 0; i < 4; i++) {
            String tenGod = ganShen[i];
            sb.append(labelNames[i]).append("干").append(pillars[i][0]).append("：<font color='#90EE90'><b>").append(tenGod).append("</b></font>");
            sb.append("（").append(getTenGodExplanation(tenGod)).append("）<br/>");
        }
        sb.append("<br/>");
        
        // 7. 干支生克关系
        sb.append("<font color='#FFD700'><b>◆ 干支关系</b></font>　<font color='#8899AA'>（每柱天干与地支的相互作用）</font><br/>");
        for (int i = 0; i < 4; i++) {
            sb.append(labelNames[i]).append("柱 ").append(pillars[i][0]).append(pillars[i][1]).append("：");
            sb.append(getGanZhiRelationship(pillars[i][0], pillars[i][1]));
            String sDesc = getSeasonDescription(pillars[i][1]);
            if (!sDesc.isEmpty()) sb.append(" · ").append(sDesc);
            sb.append("<br/>");
        }
        sb.append("<br/>");
        
        // ═══════════════════════════════════
        // 第二部分：通俗解读（普通人阅读）
        // ═══════════════════════════════════
        sb.append("<font color='#98D8F0'><b>━━━ 通俗解读 ━━━</b></font><br/><br/>");
        sb.append(getFourPillarComprehensiveAnalysis(yearGan, yearZhi, monthGan, monthZhi, dayGan, dayZhi, timeGan, timeZhi));
        
        return sb.toString();
    }
    
    private String getWuXingRelation(String wuxing1, String wuxing2) {
        if (wuxing1.equals(wuxing2)) return "<font color='#C0C0C0'>比和（同类相助）</font>";
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        if (shengMap.get(wuxing1).equals(wuxing2)) {
            return "<font color='#90EE90'>生扶（" + wuxing1 + "生" + wuxing2 + "）</font>";
        } else if (shengMap.get(wuxing2).equals(wuxing1)) {
            return "<font color='#FFA500'>泄耗（" + wuxing1 + "被" + wuxing2 + "生，泄气）</font>";
        } else if (keMap.get(wuxing1).equals(wuxing2)) {
            return "<font color='#FF6B6B'>克制（" + wuxing1 + "克" + wuxing2 + "）</font>";
        } else {
            return "<font color='#FF6B6B'>被克（" + wuxing1 + "被" + wuxing2 + "克）</font>";
        }
    }
    
    private String getSeasonDescription(String zhi) {
        switch(zhi) {
            case "寅": case "卯": case "辰": return "为春季，木气当旺";
            case "巳": case "午": case "未": return "为夏季，火气当旺";
            case "申": case "酉": case "戌": return "为秋季，金气当旺";
            case "亥": case "子": case "丑": return "为冬季，水气当旺";
            default: return "";
        }
    }
    
    // 判断天干阴阳：阳干 甲丙戊庚壬，阴干 乙丁己辛癸
    private boolean isYangGan(String gan) {
        return "甲".equals(gan) || "丙".equals(gan) || "戊".equals(gan) || "庚".equals(gan) || "壬".equals(gan);
    }
    
    private String[] getTenGods(String dayGan, String[] otherGans) {
        String[] result = new String[otherGans.length];
        for (int i = 0; i < otherGans.length; i++) {
            result[i] = getTenGodFull(dayGan, otherGans[i]);
        }
        return result;
    }
    
    // 完整十神（含阴阳区分）
    private String getTenGodFull(String dayGan, String gan) {
        String dayWuXing = getWuXing(dayGan);
        String ganWuXing = getWuXing(gan);
        boolean dayYang = isYangGan(dayGan);
        boolean ganYang = isYangGan(gan);
        boolean sameYinYang = (dayYang == ganYang);
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        if (ganWuXing.equals(dayWuXing)) return sameYinYang ? "比肩" : "劫财";
        if (shengMap.get(ganWuXing) != null && shengMap.get(ganWuXing).equals(dayWuXing)) return sameYinYang ? "偏印" : "正印";
        if (keMap.get(ganWuXing) != null && keMap.get(ganWuXing).equals(dayWuXing)) return sameYinYang ? "七杀" : "正官";
        if (shengMap.get(dayWuXing) != null && shengMap.get(dayWuXing).equals(ganWuXing)) return sameYinYang ? "食神" : "伤官";
        if (keMap.get(dayWuXing) != null && keMap.get(dayWuXing).equals(ganWuXing)) return sameYinYang ? "偏财" : "正财";
        
        return "比肩";
    }
    
    private String getTenGod(String dayGan, String gan) {
        return getTenGodFull(dayGan, gan);
    }
    
    // 十神通俗解释
    private String getTenGodExplanation(String tenGod) {
        switch (tenGod) {
            case "比肩": return "如兄弟姐妹，代表同辈助力、竞争关系、自我意识";
            case "劫财": return "如朋友伙伴，代表合作与争夺，性格豪爽但易冲动消费";
            case "正印": return "如母亲长辈，代表贵人扶持、学识智慧、仁慈包容";
            case "偏印": return "如继母师长，代表特殊才能、偏门学问、孤僻独特";
            case "食神": return "如子女晚辈，代表才华表达、口福享受、温和乐观";
            case "伤官": return "如才华外露，代表聪明机智、创造力强、不拘一格";
            case "正财": return "如正当收入，代表稳定财运、勤俭持家、务实可靠";
            case "偏财": return "如意外之财，代表投资理财、慷慨大方、人脉广泛";
            case "正官": return "如上级领导，代表纪律约束、名声地位、正直守信";
            case "七杀": return "如将军统帅，代表权威决断、事业心强、敢作敢为";
            default: return tenGod;
        }
    }
    
    // 纳音五行
    private String getNayin(String gan, String zhi) {
        // 六十甲子纳音表
        String[][] nayin = {
            {"甲子","乙丑","海中金"},{"丙寅","丁卯","炉中火"},{"戊辰","己巳","大林木"},
            {"庚午","辛未","路旁土"},{"壬申","癸酉","剑锋金"},{"甲戌","乙亥","山头火"},
            {"丙子","丁丑","涧下水"},{"戊寅","己卯","城头土"},{"庚辰","辛巳","白蜡金"},
            {"壬午","癸未","杨柳木"},{"甲申","乙酉","泉中水"},{"丙戌","丁亥","屋上土"},
            {"戊子","己丑","霹雳火"},{"庚寅","辛卯","松柏木"},{"壬辰","癸巳","长流水"},
            {"甲午","乙未","沙中金"},{"丙申","丁酉","山下火"},{"戊戌","己亥","平地木"},
            {"庚子","辛丑","壁上土"},{"壬寅","癸卯","金箔金"},{"甲辰","乙巳","覆灯火"},
            {"丙午","丁未","天河水"},{"戊申","己酉","大驿土"},{"庚戌","辛亥","钗钏金"},
            {"壬子","癸丑","桑柘木"},{"甲寅","乙卯","大溪水"},{"丙辰","丁巳","沙中土"},
            {"戊午","己未","天上火"},{"庚申","辛酉","石榴木"},{"壬戌","癸亥","大海水"}
        };
        String pillar = gan + zhi;
        for (String[] n : nayin) {
            if (n[0].equals(pillar) || n[1].equals(pillar)) return n[2];
        }
        return "未知";
    }
    
    // 生肖
    private String getZodiacNameFromZhi(String zhi) {
        String[] zodiacMap = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
        String[] zodiacName = {"鼠","牛","虎","兔","龙","蛇","马","羊","猴","鸡","狗","猪"};
        for (int i = 0; i < zodiacMap.length; i++) {
            if (zodiacMap[i].equals(zhi)) return zodiacName[i];
        }
        return zhi;
    }
    
    // 十二长生（阳干顺行，阴干逆行）
    private String getTwelveStage(String gan, String zhi) {
        String[] yangStages = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
        String[] yinStages = {"子","亥","戌","酉","申","未","午","巳","辰","卯","寅","丑"};
        // stages[11]="长生", stages[0]="沐浴" ... — 长生在数组末尾方便取模定位
        String[] stages = {"沐浴","冠带","临官","帝旺","衰","病","死","墓","绝","胎","养","长生"};
        
        if (isYangGan(gan)) {
            int startIdx;
            switch (gan) {
                case "甲": startIdx = 0; break;  // 甲长生在亥，亥在yangStages下标11，(0+11)%12=11→长生✓
                case "丙": startIdx = 9; break;  // 丙长生在寅，(9+2)%12=11→长生✓
                case "戊": startIdx = 9; break;  // 戊长生在寅，同丙
                case "庚": startIdx = 6; break;  // 庚长生在巳，(6+5)%12=11→长生✓
                case "壬": startIdx = 3; break;  // 壬长生在申，(3+8)%12=11→长生✓
                default: return "未知";
            }
            for (int i = 0; i < 12; i++) {
                if (yangStages[i].equals(zhi)) return stages[(startIdx + i) % 12];
            }
        } else {
            int startIdx;
            switch (gan) {
                case "乙": startIdx = 5; break;  // 乙长生在午，(5+6)%12=11→长生✓
                case "丁": startIdx = 8; break;  // 丁长生在酉，(8+3)%12=11→长生✓
                case "己": startIdx = 8; break;  // 己长生在酉，同丁
                case "辛": startIdx = 11; break; // 辛长生在子，(11+0)%12=11→长生✓
                case "癸": startIdx = 2; break;  // 癸长生在卯，(2+9)%12=11→长生✓
                default: return "未知";
            }
            for (int i = 0; i < 12; i++) {
                if (yinStages[i].equals(zhi)) return stages[(startIdx + i) % 12];
            }
        }
        return "未知";
    }
    
    // 十二长生通俗解释
    private String getTwelveStageExplanation(String stage) {
        switch (stage) {
            case "长生": return "如婴儿初生，充满生机希望，宜开始新计划";
            case "沐浴": return "如少年初长，敏感多变，桃花运旺但需防感情用事";
            case "冠带": return "如青年加冠，逐渐成熟，开始承担责任";
            case "临官": return "如人到壮年，事业有成，精力充沛，适合奋斗";
            case "帝旺": return "如人生巅峰，力量最强，但物极必反需谨慎";
            case "衰": return "如盛极而衰，需放慢节奏，保重身体";
            case "病": return "如人生低谷，需休养生息，注意健康";
            case "死": return "如冬眠之时，宜退守等待，不宜冲动";
            case "墓": return "如入库收藏，宜储蓄积累，保守稳健";
            case "绝": return "如种子入土，表面沉寂，实则孕育新生";
            case "胎": return "如母腹孕育，暗中筹划，等待时机";
            case "养": return "如胎儿成长，蓄势待发，培养实力";
            default: return "";
        }
    }
    
    // 纳音通俗解释
    private String getNayinExplanation(String nayin) {
        switch (nayin) {
            case "海中金": return "深海藏金，需经打磨方显价值，先苦后甜之象";
            case "炉中火": return "炉中烈火烧炼万物，热情进取之象";
            case "大林木": return "参天古木根基深厚，稳重持久之象";
            case "路旁土": return "道路尘土平凡踏实，务实可靠之象";
            case "剑锋金": return "宝剑锋芒锐利无比，果断刚毅之象";
            case "山头火": return "山头野火燎原之势，爆发力强但难持久";
            case "涧下水": return "山涧溪水清澈灵动，聪明灵活之象";
            case "城头土": return "城墙厚土坚固安稳，稳重护家之象";
            case "白蜡金": return "蜡中藏金精美细致，内秀外拙之象";
            case "杨柳木": return "杨柳依依柔韧随和，善交际适应力强";
            case "泉中水": return "泉水清澈甘甜滋养，智慧深藏之象";
            case "屋上土": return "屋瓦遮风挡雨，有责任感能保护他人";
            case "霹雳火": return "雷电之火迅猛有力，爆发力强性格刚烈";
            case "松柏木": return "松柏常青不畏严寒，坚韧不拔之象";
            case "长流水": return "长流不息源源不断，持续发展之象";
            case "沙中金": return "沙中淘金需耐心，后发有力之象";
            case "山下火": return "山下暗火不张扬，内热外冷之象";
            case "平地木": return "平地之木平凡生长，朴实自然之象";
            case "壁上土": return "壁间之土稳固可靠，守护家园之象";
            case "金箔金": return "金箔华美装饰性强，追求品质之象";
            case "覆灯火": return "灯烛之明温暖人心，文化传承之象";
            case "天河水": return "天河银汉浩瀚无边，胸怀宽广之象";
            case "大驿土": return "驿道通达四方，人脉广泛交际强";
            case "钗钏金": return "首饰之金精美华丽，审美高雅之象";
            case "桑柘木": return "桑树养蚕贡献于人，默默奉献之象";
            case "大溪水": return "大溪奔腾不拘小节，豪爽大气之象";
            case "沙中土": return "沙土松散需聚合力，宜团队合作";
            case "天上火": return "天上骄阳明照四方，光芒耀眼之象";
            case "石榴木": return "石榴多子多福，家运兴旺之象";
            case "大海水": return "大海浩瀚包容万物，胸襟宽广之象";
            default: return "";
        }
    }
    
    private String getFiveElementXiJi(String dayGan, String dayWuXing, String yearGan, String yearZhi, String monthGan, String monthZhi, String dayZhi, String timeGan, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        
        int count = 0;
        String[] allGans = {yearGan, monthGan, dayGan, timeGan};
        String[] allZhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        
        for (String gan : allGans) {
            if (getWuXing(gan).equals(dayWuXing)) count++;
        }
        for (String zhi : allZhis) {
            if (getWuXing(zhi).equals(dayWuXing)) count++;
        }
        
        sb.append("日主").append(dayGan).append("属").append(dayWuXing);
        if (count >= 3) {
            sb.append("<font color='#FF6B6B'>偏旺</font>，");
            sb.append("<font color='#90EE90'>喜</font>克制泄耗之五行，");
            sb.append("<font color='#FF6B6B'>忌</font>生扶比和之五行");
        } else if (count <= 1) {
            sb.append("<font color='#FF6B6B'>偏弱</font>，");
            sb.append("<font color='#90EE90'>喜</font>生扶比和之五行，");
            sb.append("<font color='#FF6B6B'>忌</font>克制泄耗之五行");
        } else {
            sb.append("<font color='#FFD700'>中和</font>，");
            sb.append("五行均衡，喜忌不明显，宜顺势而为");
        }
        
        return sb.toString();
    }
    
    private String getPersonalityAnalysis(String dayGan, String dayZhi) {
        StringBuilder sb = new StringBuilder();
        sb.append(getGanDescription(dayGan)).append(" · ").append(getZhiDescription(dayZhi));
        return sb.toString();
    }
    
    private String getFortuneAnalysis(String dayGan, String dayWuXing, String monthGan, String monthZhi) {
        StringBuilder sb = new StringBuilder();
        String monthWuXing = getWuXing(monthZhi);
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        if (monthWuXing.equals(dayWuXing)) {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，与日主").append(dayGan).append("比和，运势平稳");
        } else if (shengMap.get(monthWuXing).equals(dayWuXing)) {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，生扶日主").append(dayGan).append("，运势助力");
        } else if (keMap.get(monthWuXing).equals(dayWuXing)) {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，克制日主").append(dayGan).append("，运势压力");
        } else if (shengMap.get(dayWuXing).equals(monthWuXing)) {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，被日主").append(dayGan).append("生扶，运势消耗");
        } else {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，克制日主").append(dayGan).append("，运势受阻");
        }
        
        return sb.toString();
    }
    
    private String getPillarAnalysis(String gan, String zhi, String pillarType) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        String ganDesc = getGanDescription(gan);
        String zhiDesc = getZhiDescription(zhi);
        
        String relationship = getGanZhiRelationship(gan, zhi);
        
        return ganDesc + " · " + zhiDesc + " · " + relationship;
    }

    private String getGanDescription(String gan) {
        switch (gan) {
            case "甲": return "阳木·参天大树，主尊贵权威";
            case "乙": return "阴木·花草之木，主柔顺仁慈";
            case "丙": return "阳火·太阳之火，主光明热情";
            case "丁": return "阴火·灯烛之火，主文明细致";
            case "戊": return "阳土·大地之土，主稳重诚信";
            case "己": return "阴土·田园之土，主包容厚德";
            case "庚": return "阳金·刀剑之金，主果断刚毅";
            case "辛": return "阴金·首饰之金，主精致细腻";
            case "壬": return "阳水·江海之水，主智慧流动";
            case "癸": return "阴水·雨露之水，主聪明神秘";
            default: return gan + "·未知";
        }
    }

    private String getZhiDescription(String zhi) {
        String[] zodiac = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
        String[] zodiacMap = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        String zodiacName = "";
        for (int i = 0; i < zodiacMap.length; i++) {
            if (zodiacMap[i].equals(zhi)) {
                zodiacName = zodiac[i];
                break;
            }
        }
        
        switch (zhi) {
            case "子": return "鼠·水·北方·主智慧藏蓄";
            case "丑": return "牛·土·东北·主积蓄稳重";
            case "寅": return "虎·木·东北·主生发进取";
            case "卯": return "兔·木·东方·主生长繁荣";
            case "辰": return "龙·土·东南·主变化升腾";
            case "巳": return "蛇·火·东南·主温暖礼仪";
            case "午": return "马·火·南方·主旺盛显达";
            case "未": return "羊·土·西南·主收藏终结";
            case "申": return "猴·金·西南·主肃杀变革";
            case "酉": return "鸡·金·西方·主收敛收获";
            case "戌": return "狗·土·西北·主收藏防备";
            case "亥": return "猪·水·西北·主流动变化";
            default: return zhi + "·未知";
        }
    }

    private String getGanZhiRelationship(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        if (ganWuXing.equals(zhiWuXing)) {
            return "比和·相助";
        }
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        if (shengMap.get(ganWuXing).equals(zhiWuXing)) {
            return "天干生地支·泄秀";
        }
        if (shengMap.get(zhiWuXing).equals(ganWuXing)) {
            return "地支生天干·得助";
        }
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        if (keMap.get(ganWuXing).equals(zhiWuXing)) {
            return "天干克地支·制杀";
        }
        if (keMap.get(zhiWuXing).equals(ganWuXing)) {
            return "地支克天干·受制";
        }
        
        return "关系一般";
    }
    
    private String getRiGanDetailedAnalysis(String gan) {
        switch (gan) {
            case "甲": 
                return "甲木为参天大树，栋梁之材。性格正直、有领导力，具开创精神。宜在东方发展，适合从事管理、领导、林业等行业。";
            case "乙": 
                return "乙木为花草之木，柔顺多姿。性格温和、善于变通，具艺术天赋。宜在东方发展，适合从事艺术、教育、美容等行业。";
            case "丙": 
                return "丙火为太阳之火，光明照耀。性格热情、积极向上，具领导魅力。宜在南方发展，适合从事文化、能源、餐饮等行业。";
            case "丁": 
                return "丁火为灯烛之火，温暖光明。性格细腻、善于思考，具才华智慧。宜在南方发展，适合从事科技、文化、服务等行业。";
            case "戊": 
                return "戊土为大地之土，厚重沉稳。性格踏实、诚实守信，具包容精神。宜在中央发展，适合从事建筑、房地产、农业等行业。";
            case "己": 
                return "己土为田园之土，肥沃滋养。性格宽厚、乐于助人，具奉献精神。宜在中央发展，适合从事农业、医药、慈善等行业。";
            case "庚": 
                return "庚金为刀剑之金，锐利刚毅。性格果断、勇往直前，具决断力。宜在西方发展，适合从事金融、军事、金属等行业。";
            case "辛": 
                return "辛金为首饰之金，精致美丽。性格优雅、追求完美，具审美能力。宜在西方发展，适合从事珠宝、艺术、设计等行业。";
            case "壬": 
                return "壬水为江海之水，奔腾不息。性格聪明、善于变通，具商业头脑。宜在北方发展，适合从事商贸、水产、运输等行业。";
            case "癸": 
                return "癸水为雨露之水，润物无声。性格神秘、聪明睿智，具洞察力。宜在北方发展，适合从事学术、策划、保密等行业。";
            default: 
                return "日干代表自身，反映个人性格与天赋。";
        }
    }
    
    private String getFourPillarSummary(String yearGan, String yearZhi, String monthGan, String monthZhi,
                                        String dayGan, String dayZhi, String timeGan, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        
        String dayGanWuXing = getWuXing(dayGan);
        sb.append("日主五行：").append(dayGanWuXing).append("\n");
        
        int shengCount = 0, keCount = 0, biCount = 0;
        String[] pillars = {yearGan, yearZhi, monthGan, monthZhi, dayGan, dayZhi, timeGan, timeZhi};
        
        for (int i = 0; i < pillars.length; i += 2) {
            String pGan = pillars[i];
            String pZhi = pillars[i + 1];
            
            String pWuXing = getWuXing(pGan);
            if (pWuXing.equals(dayGanWuXing)) biCount++;
            
            java.util.Map<String, String> shengMap = new java.util.HashMap<>();
            shengMap.put("木", "火"); shengMap.put("火", "土");
            shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
            
            if (shengMap.get(pWuXing).equals(dayGanWuXing)) shengCount++;
            
            java.util.Map<String, String> keMap = new java.util.HashMap<>();
            keMap.put("木", "土"); keMap.put("火", "金");
            keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
            
            if (keMap.get(pWuXing).equals(dayGanWuXing)) keCount++;
        }
        
        sb.append("生扶：").append(shengCount).append("个 · 克制：").append(keCount).append("个 · 比和：").append(biCount).append("个\n");
        
        if (shengCount > keCount) {
            sb.append("身强·宜泄耗克制，适合创业发展");
        } else if (keCount > shengCount) {
            sb.append("身弱·宜生扶比和，适合稳健守成");
        } else {
            sb.append("中和·五行均衡，运势平稳顺畅");
        }
        
        return sb.toString();
    }
    
    private String getGanDetailedAnalysis(String gan) {
        switch (gan) {
            case "甲": return "阳木·参天大树，栋梁之才。主尊贵权威、领袖气质。性格正直刚强，有决断力，适合领导管理、政治、军事等行业。甲木得令则生机勃勃，失令则虽有志向但难以施展。";
            case "乙": return "阴木·花草之木，柔顺优美。主仁慈善良、多才多艺。性格温和细腻，善于协调，适合艺术、文化、教育等行业。乙木虽柔，但韧性十足，善于以柔克刚。";
            case "丙": return "阳火·太阳之火，光明普照。主热情开朗、名声远播。性格外向积极，充满活力，适合演艺、销售、公关等行业。丙火旺盛则光芒四射，失令则热情难持久。";
            case "丁": return "阴火·灯烛之火，柔和温暖。主文明细致、才华出众。性格文雅内敛，注重细节，适合艺术创作、手工艺、精密制造等行业。丁火虽小，但能照亮黑暗。";
            case "戊": return "阳土·大地之土，厚重沉稳。主稳重诚信、包容万物。性格踏实可靠，值得信赖，适合金融、房地产、仓储等行业。戊土得令则厚德载物，失令则固执保守。";
            case "己": return "阴土·田园之土，肥沃滋润。主包容厚德、善于耕耘。性格温和善良，善于照顾他人，适合农业、医疗、慈善等行业。己土虽柔，但能孕育万物。";
            case "庚": return "阳金·刀剑之金，锋利无比。主果断刚毅、权威正义。性格刚强果断，不畏困难，适合军事、法律、管理等行业。庚金得令则刚强有力，失令则锋芒毕露易伤人。";
            case "辛": return "阴金·首饰之金，精致秀丽。主细腻精致、审美高雅。性格细腻敏感，追求完美，适合艺术设计、珠宝、美容等行业。辛金虽柔，但精致典雅。";
            case "壬": return "阳水·江海之水，浩瀚无垠。主智慧流动、胸怀宽广。性格豁达开朗，善于变通，适合贸易、航海、水利等行业。壬水得令则奔腾不息，失令则泛滥成灾。";
            case "癸": return "阴水·雨露之水，润物无声。主聪明神秘、直觉敏锐。性格聪明伶俐，心思缜密，适合学术研究、科技研发、侦探等行业。癸水虽柔，但能渗透万物。";
            default: return gan + "·未知天干";
        }
    }
    
    private String getZhiDetailedAnalysis(String zhi) {
        String[] zodiac = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
        String[] zodiacMap = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        String zodiacName = "";
        for (int i = 0; i < zodiacMap.length; i++) {
            if (zodiacMap[i].equals(zhi)) {
                zodiacName = zodiac[i];
                break;
            }
        }
        
        switch (zhi) {
            case "子": return "鼠·水·北方。主智慧藏蓄，聪明伶俐。子水为阳水，象征万物之始源，具有开创之力。子时生人思维敏捷，善于谋划。";
            case "丑": return "牛·土·东北。主积蓄稳重，勤劳踏实。丑土为阴土，为金库，主财库积聚。丑时生人性格稳重，值得信赖。";
            case "寅": return "虎·木·东北。主生发进取，勇猛果敢。寅木为阳木，主万物生发，具有开创之力。寅时生人性格积极向上，勇于挑战。";
            case "卯": return "兔·木·东方。主生长繁荣，温和善良。卯木为阴木，主万物生长，生机勃勃。卯时生人性格温和，多才多艺。";
            case "辰": return "龙·土·东南。主变化升腾，神秘威严。辰土为阳土，为水库，主智慧谋略。辰时生人性格聪明，善于变通。";
            case "巳": return "蛇·火·东南。主温暖礼仪，神秘睿智。巳火为阴火，主文化教育。巳时生人性格文雅，富有才华。";
            case "午": return "马·火·南方。主旺盛显达，热情奔放。午火为阳火，主阳气最盛，事业辉煌。午时生人性格积极进取，事业心强。";
            case "未": return "羊·土·西南。主收藏终结，温顺善良。未土为阴土，主收获成果。未时生人性格温和，善于包容。";
            case "申": return "猴·金·西南。主肃杀变革，聪明机智。申金为阳金，主决断行动。申时生人性格聪明，反应敏捷。";
            case "酉": return "鸡·金·西方。主收敛收获，勤劳守信。酉金为阴金，主财富积累。酉时生人性格精明，善于理财。";
            case "戌": return "狗·土·西北。主收藏防备，忠诚可靠。戌土为阳土，主守护家园。戌时生人性格忠诚，责任感强。";
            case "亥": return "猪·水·西北。主流动变化，憨厚朴实。亥水为阴水，主智慧思辨。亥时生人性格聪明，思维敏捷。";
            default: return zhi + "·未知地支";
        }
    }
    
    private String getYearPillarMeaning(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        StringBuilder sb = new StringBuilder();
        sb.append("年柱代表祖上、家庭背景、早年运势。");
        
        if (ganWuXing.equals(zhiWuXing)) {
            sb.append(" 年柱比和，祖上根基稳固，家庭和睦。");
        } else if (isSheng(zhiWuXing, ganWuXing)) {
            sb.append(" 年支生年干，祖上福荫深厚，得长辈相助。");
        } else if (isSheng(ganWuXing, zhiWuXing)) {
            sb.append(" 年干生年支，自身奋发向上，光耀门楣。");
        } else if (isKe(zhiWuXing, ganWuXing)) {
            sb.append(" 年支克年干，早年压力较大，需靠自己努力。");
        } else if (isKe(ganWuXing, zhiWuXing)) {
            sb.append(" 年干克年支，自身能力强，可驾驭环境。");
        }
        
        return sb.toString();
    }
    
    private String getMonthPillarMeaning(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        StringBuilder sb = new StringBuilder();
        sb.append("月柱代表事业、学业、中年运势。");
        
        if (ganWuXing.equals(zhiWuXing)) {
            sb.append(" 月柱比和，事业稳定，学业有成。");
        } else if (isSheng(zhiWuXing, ganWuXing)) {
            sb.append(" 月支生月干，事业得助，贵人扶持。");
        } else if (isSheng(ganWuXing, zhiWuXing)) {
            sb.append(" 月干生月支，乐于助人，人脉广。");
        } else if (isKe(zhiWuXing, ganWuXing)) {
            sb.append(" 月支克月干，事业压力大，竞争激烈。");
        } else if (isKe(ganWuXing, zhiWuXing)) {
            sb.append(" 月干克月支，能力出众，可克服困难。");
        }
        
        return sb.toString();
    }
    
    private String getDayPillarMeaning(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        StringBuilder sb = new StringBuilder();
        sb.append("日柱代表自身、性格、婚姻家庭。");
        
        if (ganWuXing.equals(zhiWuXing)) {
            sb.append(" 日柱比和，性格坚韧，夫妻和睦。");
        } else if (isSheng(zhiWuXing, ganWuXing)) {
            sb.append(" 日支生日干，得配偶相助，家庭幸福。");
        } else if (isSheng(ganWuXing, zhiWuXing)) {
            sb.append(" 日干生日支，关爱家人，付出较多。");
        } else if (isKe(zhiWuXing, ganWuXing)) {
            sb.append(" 日支克日干，配偶强势，需相互包容。");
        } else if (isKe(ganWuXing, zhiWuXing)) {
            sb.append(" 日干克日支，自身主导，掌控家庭。");
        }
        
        return sb.toString();
    }
    
    private String getTimePillarMeaning(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        StringBuilder sb = new StringBuilder();
        sb.append("时柱代表晚年运势、子女、部属。");
        
        if (ganWuXing.equals(zhiWuXing)) {
            sb.append(" 时柱比和，晚年安逸，子女孝顺。");
        } else if (isSheng(zhiWuXing, ganWuXing)) {
            sb.append(" 时支生时干，子女得力，晚年享清福。");
        } else if (isSheng(ganWuXing, zhiWuXing)) {
            sb.append(" 时干生时支，关爱子女，付出心血。");
        } else if (isKe(zhiWuXing, ganWuXing)) {
            sb.append(" 时支克时干，晚年操心，子女叛逆。");
        } else if (isKe(ganWuXing, zhiWuXing)) {
            sb.append(" 时干克时支，管教严格，子女成才。");
        }
        
        return sb.toString();
    }
    
    private String getFourPillarComprehensiveAnalysis(String yearGan, String yearZhi, String monthGan, String monthZhi,
                                                      String dayGan, String dayZhi, String timeGan, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        
        String dayGanWuXing = getWuXing(dayGan);
        String dayZhiWuXing = getWuXing(dayZhi);
        String zodiac = getZodiacNameFromZhi(yearZhi);
        String dayZodiac = getZodiacNameFromZhi(dayZhi);
        String stageDay = getTwelveStage(dayGan, dayZhi);
        
        // 计算十神
        String yGanShen = getTenGodFull(dayGan, yearGan);
        String mGanShen = getTenGodFull(dayGan, monthGan);
        String tGanShen = getTenGodFull(dayGan, timeGan);
        
        // 计算身强身弱
        int shengCount = 0, keCount = 0, biCount = 0;
        String[][] pillars = {{yearGan, yearZhi}, {monthGan, monthZhi}, {dayGan, dayZhi}, {timeGan, timeZhi}};
        for (int i = 0; i < pillars.length; i++) {
            String pGanWuXing = getWuXing(pillars[i][0]);
            if (pGanWuXing.equals(dayGanWuXing)) { if (i != 2) biCount++; }
            else if (isSheng(pGanWuXing, dayGanWuXing)) shengCount++;
            else if (isKe(pGanWuXing, dayGanWuXing)) keCount++;
            else if (isSheng(dayGanWuXing, pGanWuXing)) keCount++;
            else if (isKe(dayGanWuXing, pGanWuXing)) keCount++;
        }
        
        boolean isStrong = (shengCount + biCount > keCount);
        boolean isWeak = (keCount > shengCount + biCount);
        boolean isBalance = (!isStrong && !isWeak);
        
        String shengHao = getShengWuXing(dayGanWuXing);
        String xieHao = ""; // 日主所生
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土"); shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        xieHao = shengMap.get(dayGanWuXing);
        
        // ════════════════════════════
        // 1. 整体性格
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>🌟 性格特点</b></font><br/>");
        sb.append(getPersonalityRich(dayGan, dayZhi, dayGanWuXing, isStrong, isWeak, isBalance));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 2. 事业运势
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>💼 事业运势</b></font><br/>");
        sb.append(getCareerAnalysisRich(dayGan, dayGanWuXing, monthGan, monthZhi, mGanShen, isStrong, isWeak));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 3. 财富运势
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>💰 财富运势</b></font><br/>");
        sb.append(getWealthAnalysisRich(dayGan, dayGanWuXing, pillars, isStrong, isWeak));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 4. 感情婚姻
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>💕 感情婚姻</b></font><br/>");
        sb.append(getRelationshipAnalysisRich(dayGan, dayZhi, dayGanWuXing, dayZhiWuXing, dayZodiac));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 5. 健康建议
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>🏥 健康建议</b></font><br/>");
        sb.append(getHealthAnalysisRich(dayGanWuXing, shengCount, keCount, biCount));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 6. 人际关系
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>🤝 人际关系</b></font><br/>");
        sb.append(getSocialAnalysisRich(dayGan, dayGanWuXing, yGanShen, mGanShen, tGanShen, yearGan, zodiac, isStrong, isWeak));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 7. 人生发展建议
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>🎯 人生发展建议</b></font><br/>");
        sb.append(getLifeAdviceRich(dayGan, dayGanWuXing, zodiac, shengHao, xieHao, isStrong, isWeak, isBalance));
        sb.append("<br/><br/>");
        
        sb.append("<font color='#8899AA'><i>※ 以上解读基于四柱命理分析，仅供参考。命运掌握在自己手中，积极努力、保持善良才是最好的风水。</i></font>");
        
        return sb.toString();
    }
    
    // ═══ 丰富的通俗解读方法 ═══
    
    private String getPersonalityRich(String dayGan, String dayZhi, String wuXing, boolean isStrong, boolean isWeak, boolean isBalance) {
        StringBuilder sb = new StringBuilder();
        String zodiac = getZodiacNameFromZhi(dayZhi);
        
        switch (wuXing) {
            case "木":
                sb.append("你如<font color='#90EE90'><b>乔木挺立</b></font>，生命力蓬勃，向上进取。");
                if (isStrong) sb.append("刚毅自信，有领袖气质，主意正、难动摇。忌刚愎自用，兼听则明。");
                else if (isWeak) sb.append("如藤附木，善借外力，温雅有礼，人缘佳。需在关键处坚持己见。");
                else sb.append("刚柔兼济，守原则而知权变。");
                break;
            case "火":
                sb.append("你如<font color='#FF6B6B'><b>明烛暖焰</b></font>，热情洋溢，走到哪里都自带光芒。");
                if (isStrong) sb.append("精力旺盛、雷厉风行，是天然的行动派。戒急躁冲动，三思后行。");
                else if (isWeak) sb.append("内热外敛，默默赋能他人。宜崭露头角，让才华被看见。");
                else sb.append("热情有度、有始有终，诚恳可交。");
                break;
            case "土":
                sb.append("你如<font color='#FFD700'><b>厚土载物</b></font>，踏实稳重，予人安全感。");
                if (isStrong) sb.append("敦厚诚信，朋友中最是靠得住。稳扎稳打，虽缓而实。偶尔尝试新鲜事物，会有意外之喜。");
                else if (isWeak) sb.append("心善好施，需警惕被人利用。宜设立底线，护住心神。");
                else sb.append("务实持重，有条不紊，稳步前行。");
                break;
            case "金":
                sb.append("你如<font color='#C0C0C0'><b>宝剑出匣</b></font>，头脑清晰，果敢利落。");
                if (isStrong) sb.append("刚正重义，处事爽利。藏锋守拙，过刚易折。");
                else if (isWeak) sb.append("细致求精，把控力强。勿求完美而自耗，该放则放。");
                else sb.append("理性果决而不失细腻，善决断也善执行。");
                break;
            case "水":
                sb.append("你如<font color='#87CEEB'><b>清泉灵动</b></font>，头脑灵活，随机应变。");
                if (isStrong) sb.append("智慧超群，洞察入微，总能觅得佳策。莫思虑过甚，简即是真。");
                else if (isWeak) sb.append("心思缜密，直觉敏锐，见地独到。当增自信，你之见解实有分量。");
                else sb.append("聪而不露，圆融处世，天生智囊。");
                break;
        }
        sb.append("<br/>日支").append(dayZhi).append("（属").append(zodiac).append("），主内，").append(getDayZhiPersonality(dayZhi)).append("。");
        return sb.toString();
    }
    
    private String getDayZhiPersonality(String zhi) {
        switch (zhi) {
            case "子": return "温柔细腻，重视家庭温暖";
            case "丑": return "忠诚踏实，对伴侣专一稳定";
            case "寅": return "自信独立，在家庭中也追求自我实现";
            case "卯": return "温和有艺术气质，家庭氛围和谐";
            case "辰": return "有魅力有担当，是家庭中的主心骨";
            case "巳": return "聪明体贴，善于经营家庭关系";
            case "午": return "热情浪漫，让家庭生活丰富多彩";
            case "未": return "温柔善良，是家庭中的润滑剂";
            case "申": return "机智灵活，能给家庭带来新鲜感";
            case "酉": return "精致讲究，注重家庭生活品质";
            case "戌": return "忠诚可靠，是家庭坚实后盾";
            case "亥": return "宽厚包容，家庭关系其乐融融";
            default: return "有独特个性";
        }
    }
    
    private String getCareerAnalysisRich(String dayGan, String wuXing, String monthGan, String monthZhi, String monthShen, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        String monthSeason = getSeasonDescription(monthZhi);
        
        switch (wuXing) {
            case "木": sb.append("天赋在<font color='#90EE90'><b>规划设计、文教领域</b></font>。"); break;
            case "火": sb.append("天赋在<font color='#FF6B6B'><b>创意表达、市场传播</b></font>。"); break;
            case "土": sb.append("天赋在<font color='#FFD700'><b>财务建筑、地产实业</b></font>。"); break;
            case "金": sb.append("天赋在<font color='#C0C0C0'><b>金融法律、技术研发</b></font>。"); break;
            case "水": sb.append("天赋在<font color='#87CEEB'><b>商贸流通、信息传媒</b></font>。"); break;
        }
        
        if (!monthSeason.isEmpty()) sb.append("月令").append(monthSeason).append("，");
        
        if (isStrong) {
            sb.append("身强可担重任，宜主攻开拓、登大平台任管理。");
            if (monthShen.equals("正官") || monthShen.equals("七杀")) sb.append("月有官杀，职场得位，管理有方。");
            if (monthShen.equals("正财") || monthShen.equals("偏财")) sb.append("商才卓越，善理财源。");
        } else if (isWeak) {
            sb.append("宜稳中求进，择良木而栖，好团队胜过单打独斗。");
            if (monthShen.equals("正印") || monthShen.equals("偏印")) sb.append("贵人运旺，长上提携，多向前辈请益。");
            if (monthShen.equals("比肩") || monthShen.equals("劫财")) sb.append("合作为上，得同道相助则事业顺遂。");
        } else {
            sb.append("适应力强，无论环境皆能出彩，宜做复合型人才。");
        }
        
        sb.append("<br/>月干十神：<font color='#90EE90'><b>").append(monthShen).append("</b></font>，主事业指引，").append(getTenGodExplanation(monthShen)).append("。");
        return sb.toString();
    }
    
    private String getWealthAnalysisRich(String dayGan, String wuXing, String[][] pillars, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        
        boolean hasZhengCai = false, hasPianCai = false;
        for (int i = 0; i < pillars.length; i++) {
            String shen = getTenGodFull(dayGan, pillars[i][0]);
            if (shen.equals("正财")) hasZhengCai = true;
            if (shen.equals("偏财")) hasPianCai = true;
        }
        
        switch (wuXing) {
            case "木": sb.append("财源在<font color='#90EE90'><b>土</b></font>（地产、农业、基建）。"); break;
            case "火": sb.append("财源在<font color='#FF6B6B'><b>金</b></font>（金融、科技、法律）。"); break;
            case "土": sb.append("财源在<font color='#87CEEB'><b>水</b></font>（商贸、物流、通讯）。"); break;
            case "金": sb.append("财源在<font color='#90EE90'><b>木</b></font>（文创、教育、医药）。"); break;
            case "水": sb.append("财源在<font color='#FF6B6B'><b>火</b></font>（餐饮、能源、娱乐）。"); break;
        }
        
        if (hasZhengCai) sb.append("命带<font color='#90EE90'><b>正财</b></font>，正业稳定积累，宜中长线布局。");
        if (hasPianCai) sb.append("命带<font color='#FFA500'><b>偏财</b></font>，有投资嗅觉，须控风险、莫孤注一掷。");
        
        if (isStrong) sb.append("身强能担财，积极开源、善理财，财富空间可观。");
        else if (isWeak) sb.append("身弱慎财，稳健为主，避高风险，寻良伴共理财更好。");
        else sb.append("财运平稳，量入为出，积微成著。");
        return sb.toString();
    }
    
    private String getRelationshipAnalysisRich(String dayGan, String dayZhi, String dayGanWuXing, String dayZhiWuXing, String dayZodiac) {
        StringBuilder sb = new StringBuilder();
        
        String ganZhiRel = getGanZhiRelationship(dayGan, dayZhi);
        
        sb.append("配偶宫").append(dayZhi).append("（属").append(dayZodiac).append("），");
        
        if (ganZhiRel.contains("比和")) {
            sb.append("与伴侣志趣相投、琴瑟相谐。唯太似易争，互让为贵。");
        } else if (ganZhiRel.contains("得助") || ganZhiRel.contains("生天干")) {
            sb.append("伴侣是人生贵人，温柔扶持、相得益彰，宜珍惜此缘。");
        } else if (ganZhiRel.contains("泄秀") || ganZhiRel.contains("天干生")) {
            sb.append("你付出为多，甘为家庭倾注心力。也须学接纳，情义两相衡。");
        } else if (ganZhiRel.contains("制杀") || ganZhiRel.contains("天干克")) {
            sb.append("你在家中主导，当不忘尊重，婚姻是两人之舞，非一人独唱。");
        } else if (ganZhiRel.contains("受制") || ganZhiRel.contains("克天干")) {
            sb.append("伴侣个性较强势，需多沟通理解，相敬如宾方长久。");
        }
        sb.append("<br/>良配：五行互补，性格").append(isYangGan(dayGan) ? "阴柔温润" : "刚健爽朗").append("者，彼此成就。");
        return sb.toString();
    }
    
    private String getHealthAnalysisRich(String wuXing, int shengCount, int keCount, int biCount) {
        StringBuilder sb = new StringBuilder();
        
        switch (wuXing) {
            case "木": sb.append("五行属木，护<font color='#90EE90'><b>肝胆筋腱</b></font>。多食青蔬、早睡养肝、舒展筋骨。春为养肝佳时。"); break;
            case "火": sb.append("五行属火，护<font color='#FF6B6B'><b>心脉血络</b></font>。勿过劳不妄怒，红食养心。夏宜清火。"); break;
            case "土": sb.append("五行属土，护<font color='#FFD700'><b>脾胃中宫</b></font>。三餐规律、忌生冷油腻，黄粟南瓜最益肠胃。"); break;
            case "金": sb.append("五行属金，护<font color='#C0C0C0'><b>肺与大肠</b></font>。多行深呼吸吐纳，白木耳百合养肺。秋重保暖。"); break;
            case "水": sb.append("五行属水，护<font color='#87CEEB'><b>肾元泌尿</b></font>。适量饮水，黑豆黑芝麻入肾。冬暖腰护体。"); break;
        }
        
        if (keCount > shengCount + biCount + 1) {
            sb.append("<br/>命局克伐较重，注意劳逸结合，定期体检为安。");
        }
        return sb.toString();
    }
    
    private String getSocialAnalysisRich(String dayGan, String wuXing, String yShen, String mShen, String tShen, String yearGan, String zodiac, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        
        switch (wuXing) {
            case "木": sb.append("待人真诚，如大树般予人依靠。"); break;
            case "火": sb.append("热情开朗，善带动气氛。"); break;
            case "土": sb.append("敦厚守信，人人信赖。"); break;
            case "金": sb.append("重义守信，口碑极佳。"); break;
            case "水": sb.append("善解人意，与各色人等皆能交融。"); break;
        }
        
        if (isStrong) sb.append("团体中往往为领袖，朋友有难第一个想到你。余力学人、亦顾自己。");
        else if (isWeak) sb.append("贵人缘佳，总有援手。多交良师益友，携手同进。");
        else sb.append("低调有度，恰到好处的存在感，让人如沐春风。");
        sb.append("<br/>属").append(zodiac).append("，有较好的适应力和独特魅力。");
        return sb.toString();
    }
    
    private String getLifeAdviceRich(String dayGan, String wuXing, String zodiac, String shengHao, String xieHao, boolean isStrong, boolean isWeak, boolean isBalance) {
        StringBuilder sb = new StringBuilder();
        
        if (isStrong) {
            sb.append("命局<font color='#90EE90'><b>身强气盛</b></font>，如良弓待发，找准方向方能致远。<br/>");
            sb.append("宜投向").append(xieHao).append("属行业，借力使力，刚柔相济。");
        } else if (isWeak) {
            sb.append("命局<font color='#FF6B6B'><b>身弱需扶</b></font>，如种待萌，需良土与天时。<br/>");
            sb.append("多结贵人，").append(shengHao).append("属之人与事是你的后盾；不疾不徐，学以致用。");
        } else {
            sb.append("命局<font color='#FFD700'><b>五行中和</b></font>，难得之格，如水流转，进退自如。<br/>");
            sb.append("保持开放应变，顺势而行，此刻便是黄金期。");
        }
        
        String[] zodiacCompat = getZodiacCompat(zodiac);
        sb.append("<br/>属").append(zodiac).append("，与").append(zodiacCompat[0]).append("、").append(zodiacCompat[1]).append("、").append(zodiacCompat[2]).append("最合。");
        sb.append(getDirectionAdvice(wuXing)).append("方位最旺。");
        return sb.toString();
    }
    
    private String[] getZodiacCompat(String zodiac) {
        java.util.Map<String, String[]> compat = new java.util.HashMap<>();
        compat.put("鼠", new String[]{"牛","龙","猴"});
        compat.put("牛", new String[]{"鼠","蛇","鸡"});
        compat.put("虎", new String[]{"猪","马","狗"});
        compat.put("兔", new String[]{"狗","猪","羊"});
        compat.put("龙", new String[]{"鸡","鼠","猴"});
        compat.put("蛇", new String[]{"猴","鸡","牛"});
        compat.put("马", new String[]{"羊","虎","狗"});
        compat.put("羊", new String[]{"马","兔","猪"});
        compat.put("猴", new String[]{"蛇","鼠","龙"});
        compat.put("鸡", new String[]{"龙","蛇","牛"});
        compat.put("狗", new String[]{"兔","虎","马"});
        compat.put("猪", new String[]{"虎","兔","羊"});
        return compat.getOrDefault(zodiac, new String[]{"—","—","—"});
    }
    
    private String getDirectionAdvice(String wuXing) {
        switch (wuXing) {
            case "木": return "东方";
            case "火": return "南方";
            case "土": return "中央";
            case "金": return "西方";
            case "水": return "北方";
            default: return "中央";
        }
    }
    
    private String getOppositeWuXing(String wuxing) {
        switch (wuxing) {
            case "木": return "金";
            case "火": return "水";
            case "土": return "木";
            case "金": return "火";
            case "水": return "土";
            default: return "土";
        }
    }
    
    private String getKeWuXing(String wuxing) {
        switch (wuxing) {
            case "木": return "土";
            case "火": return "金";
            case "土": return "水";
            case "金": return "木";
            case "水": return "火";
            default: return "土";
        }
    }
    
    private String getShengWuXing(String wuxing) {
        switch (wuxing) {
            case "木": return "水";
            case "火": return "木";
            case "土": return "火";
            case "金": return "土";
            case "水": return "金";
            default: return "土";
        }
    }
    
    private String getQiMenSummary(String yearPillar, String monthPillar, String dayPillar, String timePillar, String zhiFuStar, String zhiShiDoor) {
        String dayGan = dayPillar.substring(0, 1);
        String dayGanWuXing = getWuXing(dayGan);
        
        String[] luckyStars = {"天蓬", "天任", "天冲", "天辅", "天英", "天芮", "天柱", "天心", "天禽"};
        boolean isLucky = false;
        for (String ls : luckyStars) {
            if (ls.equals(zhiFuStar)) {
                isLucky = true;
                break;
            }
        }
        
        String[] luckyDoors = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
        boolean doorLucky = false;
        for (String ld : luckyDoors) {
            if (ld.equals(zhiShiDoor)) {
                doorLucky = true;
                break;
            }
        }
        
        String result = dayGanWuXing + "日" + dayGan + "，";
        result += isLucky ? "值符" + zhiFuStar + "星吉利，" : "值符" + zhiFuStar + "星一般，";
        result += doorLucky ? "值使" + zhiShiDoor + "门有利" : "值使" + zhiShiDoor + "门一般";
        return result;
    }
}
