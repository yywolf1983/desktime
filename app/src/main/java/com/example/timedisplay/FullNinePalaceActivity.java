package com.example.timedisplay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
    private TextView expZhifuZhishi;
    private TextView expGan;
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
        }

        fullNinePalacePanel.setPalaceData(palaceData);
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
            return (xunShouPalace - shiCheng + 1 + 9) % 9;
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
            if (star.equals("天辅") || star.equals("天心") || star.equals("天禽") || star.equals("天任")) {
                score += 2;
            } else if (star.equals("天蓬") || star.equals("天芮") || star.equals("天柱")) {
                score -= 2;
            } else if (star.equals("天英")) {
                score -= 1;
            } else if (star.equals("天冲")) {
                score += 0;
            }
        }
        
        if (door != null && !door.isEmpty()) {
            if (door.equals("开") || door.equals("休") || door.equals("生")) {
                score += 2;
            } else if (door.equals("死") || door.equals("伤") || door.equals("惊")) {
                score -= 2;
            } else if (door.equals("杜") || door.equals("景")) {
                score += 0;
            }
        }
        
        if (god != null && !god.isEmpty()) {
            if (god.equals("值符") || god.equals("太阴") || god.equals("六合") || god.equals("九天")) {
                score += 1;
            } else if (god.equals("螣蛇") || god.equals("白虎") || god.equals("玄武")) {
                score -= 1;
            } else if (god.equals("九地")) {
                score += 0;
            }
        }
        
        if (wangCui != null) {
            if (wangCui.equals("旺")) {
                score += 2;
            } else if (wangCui.equals("相")) {
                score += 1;
            } else if (wangCui.equals("休")) {
                score += 0;
            } else if (wangCui.equals("囚")) {
                score -= 1;
            } else if (wangCui.equals("死")) {
                score -= 2;
            }
        }
        
        if (score >= 3) {
            return "吉";
        } else if (score >= 1) {
            return "平吉";
        } else if (score >= -1) {
            return "平";
        } else if (score >= -3) {
            return "平凶";
        } else {
            return "凶";
        }
    }
    
    private String getLuckSymbol(String star, String door) {
        return getLuckSymbol(star, door, null, null);
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
        sbBasic.append(yearPillar).append(" ").append(monthPillar).append(" ").append(dayPillar).append(" ").append(timePillar).append("\n");
        sbBasic.append(jieqi).append(" ").append(isYangDun ? "阳" : "阴").append(ju).append("局\n");
        sbBasic.append("旬首:").append(xunShou).append(" 空亡:").append(kongWang).append("\n");
        sbBasic.append("马星:").append(maXing).append("\n");
        sbBasic.append("值符:").append(zhiFuStar).append("星").append("(").append(PALACE_NAMES[zhiFuPalace]).append(")").append("\n");
        sbBasic.append("值使:").append(zhiShiDoor).append("门").append("(").append(PALACE_NAMES[zhiShiPalace]).append(")");
        expBasic.setText(sbBasic.toString());
        
        StringBuilder sbZhifuZhishi = new StringBuilder();
        sbZhifuZhishi.append("值符：").append(zhiFuStar).append("星").append("落").append(PALACE_NAMES[zhiFuPalace]).append("\n");
        sbZhifuZhishi.append("值使：").append(zhiShiDoor).append("门").append("落").append(PALACE_NAMES[zhiShiPalace]);
        expZhifuZhishi.setText(sbZhifuZhishi.toString());
        
        StringBuilder sbGan = new StringBuilder();
        sbGan.append("日干").append(dayGan).append(dayZhi).append("(").append(riGanWuXing).append(")").append("落").append(riGanPalace >= 0 ? PALACE_NAMES[riGanPalace] : "--").append("\n");
        sbGan.append("时干").append(timeGan).append(timeZhi).append("(").append(shiGanWuXing).append(")").append("落").append(shiGanPalace >= 0 ? PALACE_NAMES[shiGanPalace] : "--").append("\n");
        sbGan.append("日时关系：").append(getRiShiRelationship(dayGan, timeGan));
        expGan.setText(sbGan.toString());
        
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
        
        if (luckyDirections.length() > 0) sbDirection.append("✅ 吉方：").append(luckyDirections).append("\n");
        if (neutralDirections.length() > 0) sbDirection.append("⚪ 平方：").append(neutralDirections).append("\n");
        if (unluckyDirections.length() > 0) sbDirection.append("❌ 凶方：").append(unluckyDirections).append("\n");
        sbDirection.append("\n");
        if (wangPositions.length() > 0) sbDirection.append("🔥 旺：").append(wangPositions).append("\n");
        if (xiangPositions.length() > 0) sbDirection.append("🌿 相：").append(xiangPositions).append("\n");
        if (xiuPositions.length() > 0) sbDirection.append("😌 休：").append(xiuPositions).append("\n");
        if (qiuPositions.length() > 0) sbDirection.append("🔒 囚：").append(qiuPositions).append("\n");
        if (siPositions.length() > 0) sbDirection.append("💀 死：").append(siPositions);
        expDirection.setText(sbDirection.toString());
        
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
        
        String riGanLuck = riGanPalace >= 0 ? luckData[riGanPalace] : "--";
        String shiGanLuck = shiGanPalace >= 0 ? luckData[shiGanPalace] : "--";
        
        StringBuilder sbLife = new StringBuilder();
        String doorPrefix = "值使" + zhiShiDoor + "门";
        sbLife.append(doorPrefix).append("\n");
        sbLife.append("👤 日干").append(dayGan).append("落").append(riGanPalace >= 0 ? PALACE_NAMES[riGanPalace] : "--").append("(").append(riGanLuck).append(")\n");
        sbLife.append("⏰ 时干").append(timeGan).append("落").append(shiGanPalace >= 0 ? PALACE_NAMES[shiGanPalace] : "--").append("(").append(shiGanLuck).append(")\n");
        sbLife.append("\n");
        
        sbLife.append("💼 ").append(getCareerAdviceShort(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("💰 ").append(getWealthAdviceShort(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("💪 ").append(getHealthAdviceShort(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("💕 ").append(getRelationshipAdviceShort(zhiShiDoor, zhiFuStar)).append("\n");
        sbLife.append("🚗 ").append(getTravelAdviceShort(zhiShiDoor, zhiFuStar));
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
        
        expTianDiPanDesc.setText(android.text.Html.fromHtml(getTianDiPanDesc(PALACE_NAMES, tianPan, diPanTianGan), android.text.Html.FROM_HTML_MODE_LEGACY));
        expNineStarsDesc.setText(android.text.Html.fromHtml(getNineStarsDesc(nineStars), android.text.Html.FROM_HTML_MODE_LEGACY));
        expEightDoorsDesc.setText(android.text.Html.fromHtml(getEightDoorsDesc(eightDoors), android.text.Html.FROM_HTML_MODE_LEGACY));
        expGodsDesc.setText(android.text.Html.fromHtml(getEightGodsDesc(eightGods), android.text.Html.FROM_HTML_MODE_LEGACY));
        expPalacesDesc.setText(android.text.Html.fromHtml(getPalacesDesc(PALACE_NAMES, nineStars, eightDoors, eightGods, luckData), android.text.Html.FROM_HTML_MODE_LEGACY));
    }
    
    private String getTianDiPanDesc(String[] palaces, String[] tianPan, String[] diPan) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>天盘代表天时，地盘代表地利。</font><br/>");
        for (int i = 0; i < 9; i++) {
            if (!tianPan[i].isEmpty() && !tianPan[i].equals("--")) {
                String palace = palaces[i];
                desc.append("<font color='#98D8F0'>").append(palace).append("宫</font>：");
                desc.append("<font color='#FFD700'>天").append(tianPan[i]).append("</font>/");
                desc.append("<font color='#FFA500'>地").append(diPan[i]).append("</font><br/>");
            }
        }
        return desc.toString();
    }
    
    private String getNineStarsDesc(String[] stars) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>九星主天时吉凶，决定事情发展趋势。</font><br/>");
        for (int i = 0; i < 9; i++) {
            if (!stars[i].isEmpty()) {
                String star = stars[i];
                String meaning = getStarMeaningShort(star);
                String starColor = "#FFD700";
                String meaningColor = (star.equals("天辅") || star.equals("天心") || star.equals("天禽")) ? "#90EE90" :
                                      (star.equals("天芮") || star.equals("天冲")) ? "#FF6B6B" : "#90EE90";
                desc.append("<font color='").append(starColor).append("'>").append(star).append("星</font>：");
                desc.append("<font color='").append(meaningColor).append("'>").append(meaning).append("</font><br/>");
            }
        }
        return desc.toString();
    }
    
    private String getEightDoorsDesc(String[] doors) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>八门主人事吉凶，决定行动成败。</font><br/>");
        for (int i = 0; i < 9; i++) {
            if (doors[i] != null && !doors[i].isEmpty()) {
                String door = doors[i];
                String meaning = getDoorMeaningShort(door);
                String doorColor = "#FFD700";
                String meaningColor;
                if (door.equals("开") || door.equals("生") || door.equals("休")) {
                    meaningColor = "#90EE90";
                } else if (door.equals("死") || door.equals("伤") || door.equals("惊")) {
                    meaningColor = "#FF6B6B";
                } else {
                    meaningColor = "#FFD700";
                }
                desc.append("<font color='").append(doorColor).append("'>").append(door).append("门</font>：");
                desc.append("<font color='").append(meaningColor).append("'>").append(meaning).append("</font><br/>");
            }
        }
        return desc.toString();
    }
    
    private String getEightGodsDesc(String[] gods) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>八神主外部环境与神秘力量影响。</font><br/>");
        for (int i = 0; i < 9; i++) {
            if (gods[i] != null && !gods[i].isEmpty()) {
                String god = gods[i];
                String meaning = getGodMeaningShort(god);
                String godColor = "#FFD700";
                String meaningColor;
                if (god.equals("值符") || god.equals("九天") || god.equals("太阴")) {
                    meaningColor = "#90EE90";
                } else if (god.equals("白虎") || god.equals("玄武") || god.equals("螣蛇")) {
                    meaningColor = "#FF6B6B";
                } else {
                    meaningColor = "#DDA0DD";
                }
                desc.append("<font color='").append(godColor).append("'>").append(god).append("</font>：");
                desc.append("<font color='").append(meaningColor).append("'>").append(meaning).append("</font><br/>");
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
            case "天蓬": return "智谋深远，宜策划谋略，把握先机";
            case "天芮": return "注意健康保养，防疾病侵扰";
            case "天冲": return "行动需谨慎，防冲动误事";
            case "天辅": return "贵人相助，宜把握机遇，借力成事";
            case "天禽": return "中正平和，宜稳中求进";
            case "天心": return "仁慈博爱，宜行善积德";
            case "天柱": return "刚直果断，宜当机立断";
            case "天任": return "勤劳踏实，宜脚踏实地";
            case "天英": return "文明昌盛，宜学习进取";
            default: return "吉星高照，运势亨通";
        }
    }
    
    private String getGodAdvice(String god) {
        if (god == null) return "神助之力，逢凶化吉";
        switch (god) {
            case "值符": return "统领全局，万事大吉，贵人相助";
            case "螣蛇": return "虚惊怪异，主惊恐、虚诈、缠绕";
            case "太阴": return "暗中助力，主隐秘、贵人、庇佑";
            case "六合": return "和合美满，主合作、婚姻、交易";
            case "白虎": return "血光之灾，主杀伐、疾病、争斗";
            case "玄武": return "偷盗欺骗，主暧昧、遗失、小人";
            case "九地": return "沉稳持重，主稳定、蓄势、保守";
            case "九天": return "飞黄腾达，主升腾、进取、贵人";
            default: return "神助之力，逢凶化吉";
        }
    }
    
    private String getZhishiAdvice(String door) {
        if (door == null) return "平稳发展";
        switch (door) {
            case "休": return "宜休息养生，调整状态，蓄势待发";
            case "生": return "宜开拓进取，求财创业，大展宏图";
            case "伤": return "宜谨慎行事，防破财损耗";
            case "杜": return "宜静不宜动，保守为上";
            case "景": return "宜考试面试，展示才华";
            case "死": return "宜保守谨慎，不宜进取";
            case "惊": return "宜防口舌是非，避免争执";
            case "开": return "宜开门纳福，百事皆宜";
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
        String base = "";
        switch (door) {
            case "开": base = "事业运势强劲。";
                       if (star.equals("天辅") || star.equals("天心")) base += "吉星高照，贵人相助，适合开创事业、拓展市场";
                       else if (star.equals("天蓬")) base += "天蓬星主谋略，适合策划布局、开拓新领域";
                       else if (star.equals("天任")) base += "天任星主稳重，适合稳步推进、厚积薄发";
                       else base += "宜主动出击，大胆尝试，把握良机";
                       break;
            case "生": base = "事业财运两旺。";
                       if (star.equals("天任")) base += "天任星主勤劳，适合置业投资、稳健发展";
                       else if (star.equals("天辅")) base += "吉星辅佐，适合合作共赢、借力发展";
                       else base += "宜把握机遇，积极进取，财源广进";
                       break;
            case "休": base = "宜休养生息。";
                       if (star.equals("天辅") || star.equals("天心")) base += "吉星守护，适合学习进修、规划未来";
                       else base += "宜调整状态，养精蓄锐，为下一步发展蓄力";
                       break;
            case "景": base = "宜展示才华。";
                       if (star.equals("天英")) base += "天英星主文明，适合文化创作、展示实力";
                       else if (star.equals("天辅")) base += "适合考试面试、汇报展示";
                       else base += "宜积极表现，争取认可，把握曝光机会";
                       break;
            case "伤": base = "事业易受损。";
                       if (star.equals("天冲")) base += "天冲星主动荡，防冲动决策、竞争失利";
                       else base += "宜稳守待时，避免冒险，防小人作祟";
                       break;
            case "杜": base = "事业受阻。";
                       if (star.equals("天芮")) base += "天芮星主病困，防沟通不畅、项目停滞";
                       else base += "宜静守待变，加强沟通，克服困难";
                       break;
            case "死": base = "事业低迷。";
                       base += "防事业受挫、机会丧失，宜守不宜攻";
                       break;
            case "惊": base = "防口舌是非。";
                       if (star.equals("天冲")) base += "天冲星主冲突，防争执纠纷、谣言中伤";
                       else base += "宜慎言慎行，低调处事，避免口舌";
                       break;
            default: base = "事业运势平稳，宜按部就班";
        }
        return base;
    }
    
    private String getWealthAdvice(String door, String star) {
        if (door == null) return "谨慎理财";
        String base = "";
        switch (door) {
            case "生": base = "财运旺盛。";
                       if (star.equals("天任")) base += "天任星主财库，适合稳健投资、置业增值";
                       else if (star.equals("天蓬")) base += "天蓬星主谋略，适合智慧理财、把握先机";
                       else base += "宜积极求财，投资理财，把握赚钱机会";
                       break;
            case "开": base = "财源广进。";
                       if (star.equals("天辅")) base += "吉星相助，适合开拓财源、创业致富";
                       else base += "宜大胆尝试，主动出击，创造财富";
                       break;
            case "休": base = "宜稳健理财。";
                       base += "不宜冒险投资，适合储蓄守财，稳健增长";
                       break;
            case "景": base = "财运一般。";
                       if (star.equals("天英")) base += "天英星主名气，适合品牌变现、知识付费";
                       else base += "宜量力而行，见好就收";
                       break;
            case "伤": base = "防破财。";
                       base += "不宜投资，防意外损耗，守财为主";
                       break;
            case "杜": base = "财运受阻。";
                       base += "求财困难，宜静观其变，等待时机";
                       break;
            case "死": base = "财运低迷。";
                       base += "不宜投资，守财为主，防破财之灾";
                       break;
            case "惊": base = "防财务纠纷。";
                       base += "不宜借贷，防合同纠纷，谨慎理财";
                       break;
            default: base = "财运平稳，宜稳健理财";
        }
        return base;
    }
    
    private String getRelationshipAdvice(String door, String star) {
        if (door == null) return "谨慎交往";
        String base = "";
        switch (door) {
            case "休": base = "人际关系和谐。";
                       if (star.equals("天辅")) base += "吉星相助，适合约会交友、感情升温";
                       else base += "宜主动沟通，增进感情";
                       break;
            case "生": base = "感情运势佳。";
                       base += "适合表白求婚、缔结良缘，感情顺遂";
                       break;
            case "开": base = "社交运势好。";
                       base += "适合拓展人脉、社交聚会，结识贵人";
                       break;
            case "景": base = "宜展示魅力。";
                       if (star.equals("天英")) base += "天英星主风采，适合展现自我、吸引异性";
                       else base += "宜积极社交，展示才华";
                       break;
            case "惊": base = "防感情风波。";
                       base += "防口舌争执、误会产生，宜冷静沟通";
                       break;
            case "伤": base = "感情易受伤。";
                       base += "防情感破裂、矛盾激化，宜克制情绪";
                       break;
            case "死": base = "感情冷淡。";
                       base += "不宜表白求婚，宜反思调整，修复关系";
                       break;
            case "杜": base = "沟通不畅。";
                       base += "防冷战隔阂，宜主动沟通，消除误会";
                       break;
            default: base = "感情运势平稳，宜顺其自然";
        }
        return base;
    }
    
    private String getHealthAdvice(String door, String star) {
        if (door == null) return "注意保养";
        String base = "";
        switch (door) {
            case "休": base = "宜养生休息。";
                       if (star.equals("天心")) base += "天心星主健康，适合调养身体、保健养生";
                       else base += "宜劳逸结合，保证睡眠，调养身心";
                       break;
            case "生": base = "身体健康。";
                       base += "身体状态良好，宜适度运动，增强体质";
                       break;
            case "开": base = "精力充沛。";
                       base += "宜户外活动，呼吸新鲜空气，保持活力";
                       break;
            case "死": base = "注意健康。";
                       if (star.equals("天芮")) base += "天芮星主疾病，防慢性病加重，及时就医";
                       else base += "防身体不适，注意保养，定期检查";
                       break;
            case "伤": base = "防意外伤害。";
                       if (star.equals("天冲")) base += "天冲星主动荡，防跌打损伤、意外事故";
                       else base += "注意安全，避免剧烈运动";
                       break;
            case "景": base = "防心火过旺。";
                       base += "宜清淡饮食，避免熬夜，保持平和";
                       break;
            case "杜": base = "防情绪郁结。";
                       base += "宜放松心情，避免压抑，适当宣泄";
                       break;
            case "惊": base = "防精神紧张。";
                       base += "防焦虑失眠，宜静心安神，放松身心";
                       break;
            default: base = "身体状态平稳，宜保持良好习惯";
        }
        return base;
    }
    
    private String getStudyAdvice(String door, String star) {
        if (door == null) return "勤奋学习";
        String base = "";
        switch (door) {
            case "景": base = "学习运势佳。";
                       if (star.equals("天辅")) base += "天辅星主智慧，适合考试冲刺、学术研究";
                       else if (star.equals("天英")) base += "天英星主文化，适合创作表达、才艺学习";
                       else base += "宜刻苦钻研，把握学习良机";
                       break;
            case "开": base = "思维开阔。";
                       base += "学习效率高，宜拓展知识面、突破瓶颈";
                       break;
            case "生": base = "学业进步。";
                       base += "适合备考复习、技能提升，进步明显";
                       break;
            case "休": base = "宜静心学习。";
                       base += "适合巩固知识、温故知新，心无旁骛";
                       break;
            case "杜": base = "学习受阻。";
                       base += "思维受限，宜多思考多实践，克服困难";
                       break;
            case "伤": base = "学习状态差。";
                       base += "注意力不集中，防半途而废，需坚持";
                       break;
            case "死": base = "学习低迷。";
                       base += "学习动力不足，宜调整心态，寻找方法";
                       break;
            case "惊": base = "防考试紧张。";
                       base += "防临场发挥失常，宜放松心态，沉着应对";
                       break;
            default: base = "学习状态平稳，宜循序渐进";
        }
        return base;
    }
    
    private String getTravelAdvice(String door, String star) {
        if (door == null) return "谨慎出行";
        String base = "";
        switch (door) {
            case "开": base = "出行顺利。";
                       base += "适合商务出差、旅游观光，诸事顺遂";
                       break;
            case "休": base = "宜休闲出行。";
                       base += "适合度假放松、短途旅行，身心愉悦";
                       break;
            case "生": base = "出行吉利。";
                       base += "适合远足探险、求财出行，收获满满";
                       break;
            case "景": base = "宜观光游览。";
                       if (star.equals("天英")) base += "天英星主风光，适合文化之旅、名胜游览";
                       else base += "适合拍照打卡、文化体验";
                       break;
            case "惊": base = "出行多波折。";
                       base += "防交通延误、意外事件，谨慎出行";
                       break;
            case "伤": base = "防出行意外。";
                       if (star.equals("天冲")) base += "天冲星主动荡，防交通事故、意外伤害";
                       else base += "注意交通安全，避免危险";
                       break;
            case "死": base = "不宜远行。";
                       base += "不宜长途旅行，在家为宜，防意外";
                       break;
            case "杜": base = "出行受阻。";
                       base += "防路途不便、计划变更，谨慎安排";
                       break;
            default: base = "出行运势平稳，注意安全";
        }
        return base;
    }
    
    private String getDietAdvice(String door, String star) {
        if (door == null) return "饮食清淡";
        String base = "";
        switch (door) {
            case "生": base = "宜进补养生。";
                       if (star.equals("天任")) base += "天任星主脾胃，适合滋补调理、增强体质";
                       else base += "宜营养均衡，适当进补";
                       break;
            case "休": base = "宜清淡饮食。";
                       base += "适合素食调理、养胃健脾，减轻肠胃负担";
                       break;
            case "开": base = "宜社交聚餐。";
                       base += "适合商务宴请、朋友聚会，增进感情";
                       break;
            case "景": base = "宜清热降火。";
                       base += "适合清淡饮食，避免辛辣，防上火";
                       break;
            case "死": base = "饮食需谨慎。";
                       if (star.equals("天芮")) base += "天芮星主疾病，防食物中毒、肠胃不适";
                       else base += "注意饮食卫生，避免生冷";
                       break;
            case "伤": base = "防饮食损伤。";
                       base += "防暴饮暴食、饮酒过量，节制饮食";
                       break;
            case "惊": base = "防情绪性进食。";
                       base += "避免因焦虑而暴饮暴食，保持规律";
                       break;
            case "杜": base = "宜简单饮食。";
                       base += "适合家常便饭，避免复杂口味";
                       break;
            default: base = "饮食宜规律，营养均衡";
        }
        return base;
    }
    
    private String getSocialAdvice(String door, String star) {
        if (door == null) return "谨慎交友";
        String base = "";
        switch (door) {
            case "开": base = "社交运势佳。";
                       if (star.equals("天辅")) base += "天辅星主贵人，适合结识高端人脉";
                       else base += "宜积极社交，广结善缘";
                       break;
            case "休": base = "人际关系和谐。";
                       base += "适合维系旧友、家庭聚会，氛围融洽";
                       break;
            case "生": base = "宜合作共赢。";
                       base += "适合商务合作、团队协作，互利互惠";
                       break;
            case "景": base = "宜展示自我。";
                       base += "适合参加活动、发表见解，提升影响力";
                       break;
            case "惊": base = "防口舌是非。";
                       if (star.equals("天冲")) base += "天冲星主冲突，防争执纠纷";
                       else base += "宜少言多行，避免议论他人";
                       break;
            case "伤": base = "人际关系紧张。";
                       base += "防朋友反目、团队矛盾，宜低调处事";
                       break;
            case "死": base = "宜减少社交。";
                       base += "不宜聚会应酬，防关系破裂";
                       break;
            case "杜": base = "社交受阻。";
                       base += "防沟通不畅、误会产生，宜主动沟通";
                       break;
            default: base = "社交运势平稳，顺其自然";
        }
        return base;
    }
    
    private String getMindAdvice(String door, String star) {
        if (door == null) return "保持平和心态";
        String base = "";
        switch (door) {
            case "休": base = "宜修身养性。";
                       if (star.equals("天辅")) base += "天辅星主修养，适合冥想静心、提升境界";
                       else base += "宜放松身心，保持平和";
                       break;
            case "生": base = "心态积极。";
                       base += "保持乐观向上，充满希望，好运自来";
                       break;
            case "开": base = "宜开拓视野。";
                       base += "勇于尝试新事物，突破自我，创造可能";
                       break;
            case "景": base = "宜保持热情。";
                       base += "保持好奇心，积极探索，追求美好";
                       break;
            case "死": base = "宜调整心态。";
                       base += "面对困难不气馁，积极寻找转机";
                       break;
            case "伤": base = "宜保持冷静。";
                       base += "克制冲动情绪，三思而后行";
                       break;
            case "惊": base = "宜减少焦虑。";
                       base += "放松心情，相信自己，不必过度担忧";
                       break;
            case "杜": base = "宜保持耐心。";
                       base += "静待时机，相信一切都会好起来";
                       break;
            default: base = "保持平常心，顺其自然";
        }
        return base;
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
        
        sb.append(isYangDun ? "阳" : "阴").append(ju).append("局\n");
        
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
            sb.append("★★★ 大吉\n");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("★★ 小吉\n");
        } else {
            sb.append("★ 平平\n");
        }
        
        sb.append("值符").append(star).append("  值使").append(door).append("\n");
        sb.append(getDayAdvice(star, door, null));
        
        return sb.toString();
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
}
