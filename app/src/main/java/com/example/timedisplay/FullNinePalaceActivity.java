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

    private TextView expTianDiPanDesc;
    private TextView expNineStarsDesc;
    private TextView expEightDoorsDesc;
    private TextView expGodsDesc;
    private TextView expPalacesDesc;
    private TextView expPaipanYiju;

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
        expPaipanYiju = (TextView) findViewById(R.id.expPaipanYiju);
        
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
        if (month == 1 && day < 6) {
            return "子";
        } else if (month == 1 && day >= 6) {
            return "丑";
        }
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
    private String getWuXing(String ganOrZhi) {
        if (ganOrZhi == null) return "土";
        switch (ganOrZhi) {
            // 天干五行
            case "甲": case "乙": return "木";
            case "丙": case "丁": return "火";
            case "戊": case "己": return "土";
            case "庚": case "辛": return "金";
            case "壬": case "癸": return "水";
            // 地支五行
            case "子": return "水";
            case "丑": case "未": return "土";
            case "寅": case "卯": return "木";
            case "辰": case "戌": return "土";
            case "巳": case "午": return "火";
            case "申": case "酉": return "金";
            case "亥": return "水";
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
        if (star == null) return "#E6C46A";
        switch (star) {
            case "天蓬": return "#3FA34D";
            case "天任": return "#3FA34D";
            case "天冲": return "#E0593B";
            case "天辅": return "#3FA34D";
            case "天英": return "#E0593B";
            case "天芮": return "#F3BA66";
            case "天柱": return "#E6C46A";
            case "天心": return "#3FA34D";
            case "天禽": return "#3FA34D";
            default: return "#E6C46A";
        }
    }
    
    private String getDoorLuckColor(String door) {
        if (door == null) return "#E6C46A";
        switch (door) {
            case "开": return "#3FA34D";
            case "休": return "#3FA34D";
            case "生": return "#3FA34D";
            case "伤": return "#E0593B";
            case "杜": return "#E6C46A";
            case "景": return "#E6C46A";
            case "死": return "#E0593B";
            case "惊": return "#E0593B";
            default: return "#E6C46A";
        }
    }
    
    private String getRiShiRelationColor(String relation) {
        if (relation == null) return "#E6C46A";
        if (relation.contains("生") || relation.contains("合") || relation.contains("比")) {
            return "#3FA34D";
        } else if (relation.contains("克") || relation.contains("冲")) {
            return "#E0593B";
        } else {
            return "#E6C46A";
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
        sbBasic.append("<font color='#CCB866'><b>").append(jieqi).append(" · ").append(isYangDun ? "阳遁" : "阴遁").append(ju).append("局</b></font><br/><br/>");
        
        sbBasic.append("📆 旬首 <font color='#E6C46A'><b>").append(xunShou).append("</b></font> · 空亡 <font color='#F3BA66'><b>").append(kongWang).append("</b></font><br/>");
        sbBasic.append("<font color='#7C8C9C'>").append(getKongWangExplanation(kongWang)).append("</font><br/><br/>");
        
        sbBasic.append("🐴 马星 <font color='#E6C46A'><b>").append(maXing).append("</b></font><br/>");
        sbBasic.append("<font color='#7C8C9C'>").append(getMaXingExplanation(maXing)).append("</font><br/><br/>");
        
        sbBasic.append("⚡ <font color='#CCB866'><b>旺衰判断</b></font><br/>");
        sbBasic.append("<font color='#7C8C9C'>").append(getWangCuiDescription(wangCui, riGanPalace)).append("</font>");
        expBasic.setText(android.text.Html.fromHtml(sbBasic.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        if (expSummary != null) {
            expSummary.setText(getQiMenSummary(yearPillar, monthPillar, dayPillar, timePillar, zhiFuStar, zhiShiDoor, isYangDun, ju));
        }
        
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
        
        if (luckyDirections.length() > 0) sbDirection.append("✅ <font color='#3FA34D'>吉方</font>：").append(luckyDirections).append("<br/>");
        if (neutralDirections.length() > 0) sbDirection.append("⚪ <font color='#E6C46A'>平方</font>：").append(neutralDirections).append("<br/>");
        if (unluckyDirections.length() > 0) sbDirection.append("❌ <font color='#E0593B'>凶方</font>：").append(unluckyDirections).append("<br/>");

        sbDirection.append("<br/>");
        sbDirection.append("<b>⚡ 旺相休囚死分布：</b><br/>");
        // 紧凑汇总形式，省去5行解释
        if (wangPositions.length() > 0) sbDirection.append("🔥 <font color='#3FA34D'>旺</font>：").append(wangPositions).append("（得时·百事顺）<br/>");
        if (xiangPositions.length() > 0) sbDirection.append("🌿 <font color='#3FA34D'>相</font>：").append(xiangPositions).append("（得生·次吉）<br/>");
        if (xiuPositions.length() > 0) sbDirection.append("😌 <font color='#E6C46A'>休</font>：").append(xiuPositions).append("（休息·宜静）<br/>");
        if (qiuPositions.length() > 0) sbDirection.append("🔒 <font color='#F3BA66'>囚</font>：").append(qiuPositions).append("（受克·不利）<br/>");
        if (siPositions.length() > 0) sbDirection.append("💀 <font color='#E0593B'>死</font>：").append(siPositions).append("（处死·衰败）</font>");
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
        sbLife.append("💼 <font color='#CCB866'><b>事业学业</b></font>：").append(getCareerAdvice(zhiShiDoor, zhiFuStar)).append(" ").append(getStudyAdvice(zhiShiDoor, zhiFuStar)).append("<br/>");
        sbLife.append("💰 <font color='#CCB866'><b>财运</b></font>：").append(getWealthAdvice(zhiShiDoor, zhiFuStar)).append("<br/>");
        sbLife.append("💪 <font color='#CCB866'><b>健康饮食</b></font>：").append(getHealthAdvice(zhiShiDoor, zhiFuStar)).append(" ").append(getDietAdvice(zhiShiDoor, zhiFuStar)).append("<br/>");
        sbLife.append("💕 <font color='#CCB866'><b>感情人际</b></font>：").append(getRelationshipAdvice(zhiShiDoor, zhiFuStar)).append(" ").append(getSocialAdvice(zhiShiDoor, zhiFuStar)).append("<br/>");
        sbLife.append("🚗 <font color='#CCB866'><b>出行</b></font>：").append(getTravelAdvice(zhiShiDoor, zhiFuStar)).append("<br/>");
        sbLife.append("🧘 <font color='#CCB866'><b>心态</b></font>：").append(getMindAdvice(zhiShiDoor, zhiFuStar));
        expLife.setText(android.text.Html.fromHtml(sbLife.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
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
        
        if (expPaipanYiju != null) {
            expPaipanYiju.setText(android.text.Html.fromHtml(getPaipanYiju(yearPillar, monthPillar, dayPillar, timePillar, jieqi, isYangDun, ju, xunShou, zhiFuStar, zhiShiDoor, zhiFuPalace, zhiShiPalace), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
    }
    
    private String getTianDiPanDesc(String[] palaces, String[] nineStars, String[] palaceNames) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#7C8C9C'>天盘主天时，地盘主地利，天地交合定吉凶。</font><br/><br/>");

        String[] directions = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};

        String[][] palaceDesc = {
            {"坎", "水·北，主智主险，对应肾、泌尿"},
            {"坤", "土·西南，主顺主静，对应脾胃"},
            {"震", "木·东，主动主长，对应肝胆"},
            {"巽", "木·东南，主风主变，对应肝胆"},
            {"中", "土·中，主和主守，对应脾胃"},
            {"乾", "金·西北，主健主尊，对应肺、大肠"},
            {"兑", "金·西，主悦主泽，对应肺"},
            {"艮", "土·东北，主止主蓄，对应脾胃"},
            {"离", "火·南，主明主礼，对应心、眼"}
        };

        desc.append("<font color='#F3BA66'><b>地盘（九宫）</b></font><br/>");
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
                desc.append("<font color='#F3BA66'>").append(palace).append("宫</font> ");
                desc.append("<font color='#98D8F0'>(").append(directions[i]).append(")</font> ");
                desc.append("<font color='#7C8C9C'>").append(palaceInfo).append("</font>");
                if (i < 8) desc.append("<br/>");
            }
        }

        return desc.toString();
    }
    
    private String getNineStarsDesc(String[] stars) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#7C8C9C'>九星主天时吉凶</font><br/><br/>");

        String[][] starInfo = {
            {"天蓬", "吉星", "#3FA34D", "水星·智谋，利投资；忌破财"},
            {"天任", "吉星", "#98FB98", "土星·诚信，利置业；忌阻滞"},
            {"天冲", "凶星", "#E0593B", "木星·勇猛，利进取；忌冲动"},
            {"天辅", "吉星", "#ADFF2F", "木星·文昌，利考试；忌桃花"},
            {"天英", "平星", "#E6C46A", "火星·名声，利求名；忌急躁"},
            {"天芮", "凶星", "#DC143C", "土星·疾病，利学习；忌久病"},
            {"天柱", "平星", "#F3BA66", "金星·变革，利改革；忌争斗"},
            {"天心", "吉星", "#3FA34D", "金星·谋略，利策划；忌药石"},
            {"天禽", "吉星", "#98FB98", "土星·中正，利协调；忌优柔"}
        };

        desc.append("<font color='#E6C46A'><b>吉星</b></font><br/>");
        for (String[] info : starInfo) {
            if (info[1].equals("吉星")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("星</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }

        desc.append("<br/><font color='#E6C46A'><b>平星</b></font><br/>");
        for (String[] info : starInfo) {
            if (info[1].equals("平星")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("星</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }

        desc.append("<br/><font color='#E0593B'><b>凶星</b></font><br/>");
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
        desc.append("<font color='#7C8C9C'>八门主人事吉凶</font><br/><br/>");

        String[][] doorInfo = {
            {"休", "吉门", "#3FA34D", "水门·休养，百事皆宜"},
            {"生", "吉门", "#98FB98", "土门·求财，谋事得利"},
            {"伤", "凶门", "#E0593B", "木门·损伤，出行不利"},
            {"杜", "平门", "#E6C46A", "木门·闭塞，宜守不宜攻"},
            {"景", "平门", "#F3BA66", "火门·名声，利考试求名"},
            {"死", "凶门", "#DC143C", "土门·衰败，百事不宜"},
            {"惊", "凶门", "#E0593B", "金门·惊恐，官非诉讼"},
            {"开", "吉门", "#ADFF2F", "金门·通达，贵人相助"}
        };

        desc.append("<font color='#E6C46A'><b>吉门</b></font><br/>");
        for (String[] info : doorInfo) {
            if (info[1].equals("吉门")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("门</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }

        desc.append("<br/><font color='#E6C46A'><b>平门</b></font><br/>");
        for (String[] info : doorInfo) {
            if (info[1].equals("平门")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("门</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }

        desc.append("<br/><font color='#E0593B'><b>凶门</b></font><br/>");
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
        desc.append("<font color='#7C8C9C'>八神主外部影响</font><br/><br/>");

        String[][] godInfo = {
            {"值符", "吉神", "#3FA34D", "尊贵权力，贵人相助"},
            {"螣蛇", "凶神", "#E0593B", "怪异缠绕，虚惊恐慌"},
            {"太阴", "吉神", "#98FB98", "暗中助力，贵人庇佑"},
            {"六合", "吉神", "#ADFF2F", "合作婚姻，交易和谈"},
            {"白虎", "凶神", "#DC143C", "血光灾祸，疾病争斗"},
            {"玄武", "凶神", "#E0593B", "偷盗欺骗，暧昧小人"},
            {"九地", "平神", "#E6C46A", "沉稳蓄势，保守守成"},
            {"九天", "吉神", "#3FA34D", "飞黄腾达，进取远行"}
        };

        desc.append("<font color='#E6C46A'><b>吉神</b></font><br/>");
        for (String[] info : godInfo) {
            if (info[1].equals("吉神")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }

        desc.append("<br/><font color='#E6C46A'><b>平神</b></font><br/>");
        for (String[] info : godInfo) {
            if (info[1].equals("平神")) {
                desc.append("<font color='").append(info[2]).append("'>").append(info[0]).append("</font> ");
                desc.append("<font color='#98D8F0'>").append(info[3]).append("</font><br/>");
            }
        }

        desc.append("<br/><font color='#E0593B'><b>凶神</b></font><br/>");
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
        desc.append("<font color='#7C8C9C'>九宫组合定吉凶</font><br/>");
        for (int i = 0; i < 9; i++) {
            String palace = palaces[i];
            String star = stars[i];
            String door = doors[i];
            String god = gods[i];
            String luck = lucks[i];
            desc.append("<font color='#98D8F0'>").append(palace).append("</font>");
            if (!star.isEmpty()) {
                desc.append("·<font color='#E6C46A'>").append(star).append("星</font>");
            }
            if (door != null && !door.isEmpty()) {
                desc.append("·<font color='#3FA34D'>").append(door).append("门</font>");
            }
            if (god != null && !god.isEmpty()) {
                desc.append("·<font color='#DDA0DD'>").append(god).append("</font>");
            }
            String luckColor = luck.contains("吉") ? "#3FA34D" : (luck.contains("凶") ? "#E0593B" : "#7C8C9C");
            desc.append(" → <font color='").append(luckColor).append("'>").append(luck).append("</font><br/>");
        }
        return desc.toString();
    }
    
    private String getRiShiRelationship(String riGan, String shiGan) {
        if (riGan.equals(shiGan)) {
            return "比和相助，事易成，主客协调";
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
                return "日生时，我生事，须奋勉方成";
            }
            if (shengMap.get(shiWuxing).equals(riWuxing)) {
                return "时生日，事生我，事半功倍易得助";
            }
        }
        return "日时平平，宜勉力，顺其自然";
    }
    
    private String getCareerAdvice(String door, String star) {
        if (door == null) return "谨慎行事";
        switch (door) {
            case "开": return "宜主动出击，把握良机";
            case "生": return "宜把握机遇，积极进取";
            case "休": return "宜休息调整，学习进修";
            case "景": return "宜展示才华，积极表现";
            case "伤": return "宜稳守待时，防小人";
            case "杜": return "宜静守待变，加强沟通";
            case "死": return "防受挫失机，宜守不宜攻";
            case "惊": return "宜慎言慎行，低调处事";
            default: return "宜按部就班";
        }
    }
    
    private String getWealthAdvice(String door, String star) {
        if (door == null) return "谨慎理财";
        switch (door) {
            case "生": return "宜积极求财，投资理财";
            case "开": return "宜大胆尝试，创造财富";
            case "休": return "宜稳健理财，储蓄守财";
            case "景": return "宜量力而行，见好就收";
            case "伤": return "不宜投资，守财为主";
            case "杜": return "宜静观其变，等待时机";
            case "死": return "守财为主，防破财";
            case "惊": return "不宜借贷，谨慎理财";
            default: return "宜稳健理财";
        }
    }
    
    private String getRelationshipAdvice(String door, String star) {
        if (door == null) return "谨慎交往";
        switch (door) {
            case "休": return "宜主动沟通，增进感情";
            case "生": return "适合表白求婚";
            case "开": return "宜拓展人脉，结识贵人";
            case "景": return "宜展示魅力，积极社交";
            case "惊": return "防感情风波，冷静沟通";
            case "伤": return "宜克制情绪";
            case "死": return "宜反思修复";
            case "杜": return "宜主动沟通，消除误会";
            default: return "宜顺其自然";
        }
    }
    
    private String getHealthAdvice(String door, String star) {
        if (door == null) return "注意保养";
        switch (door) {
            case "休": return "宜养生休息，劳逸结合";
            case "生": return "宜适度运动，增强体质";
            case "开": return "宜户外活动，保持活力";
            case "死": return "防身体不适，定期检查";
            case "伤": return "防意外伤害，注意安全";
            case "景": return "宜清淡饮食，避免熬夜";
            case "杜": return "防情绪郁结，放松心情";
            case "惊": return "防精神紧张，静心安神";
            default: return "宜保持良好习惯";
        }
    }
    
    private String getStudyAdvice(String door, String star) {
        if (door == null) return "勤奋学习";
        switch (door) {
            case "景": return "宜刻苦钻研，把握良机";
            case "开": return "宜拓展知识，提高效率";
            case "生": return "适合备考复习，技能提升";
            case "休": return "宜静心学习，温故知新";
            case "杜": return "宜多思考实践，克服困难";
            case "伤": return "需坚持，防半途而废";
            case "死": return "宜调整心态，寻找方法";
            case "惊": return "宜放松心态，沉着应对";
            default: return "宜循序渐进";
        }
    }
    
    private String getTravelAdvice(String door, String star) {
        if (door == null) return "谨慎出行";
        switch (door) {
            case "开": return "出行顺利，适合出差旅游";
            case "休": return "宜休闲出行，度假放松";
            case "生": return "出行吉利，适合远足求财";
            case "景": return "宜观光游览，文化体验";
            case "惊": return "防交通延误，谨慎出行";
            case "伤": return "防出行意外，注意安全";
            case "死": return "不宜远行，在家为宜";
            case "杜": return "出行受阻，谨慎安排";
            default: return "出行平稳，注意安全";
        }
    }
    
    private String getDietAdvice(String door, String star) {
        if (door == null) return "饮食清淡";
        switch (door) {
            case "生": return "宜进补养生，营养均衡";
            case "休": return "宜清淡饮食，素食调理";
            case "开": return "宜社交聚餐，朋友聚会";
            case "景": return "宜清热降火，清淡饮食";
            case "死": return "饮食需谨慎，注意卫生";
            case "伤": return "节制饮食，不暴饮暴食";
            case "惊": return "保持规律饮食";
            case "杜": return "宜简单饮食，家常便饭";
            default: return "饮食宜规律，营养均衡";
        }
    }
    
    private String getSocialAdvice(String door, String star) {
        if (door == null) return "谨慎交友";
        switch (door) {
            case "开": return "宜积极社交，广结善缘";
            case "休": return "适合维系旧友，家庭聚会";
            case "生": return "宜合作共赢，团队协作";
            case "景": return "宜展示自我，参加活动";
            case "惊": return "防口舌是非，少言多行";
            case "伤": return "人际紧张，宜低调";
            case "死": return "宜减少社交，不宜聚会";
            case "杜": return "社交受阻，宜主动沟通";
            default: return "社交平稳，顺其自然";
        }
    }
    
    private String getMindAdvice(String door, String star) {
        if (door == null) return "保持平和";
        switch (door) {
            case "休": return "宜修身养性，保持平和";
            case "生": return "心态积极，保持乐观";
            case "开": return "宜开拓视野，突破自我";
            case "景": return "宜保持热情，积极探索";
            case "死": return "宜调整心态，寻找转机";
            case "伤": return "宜保持冷静，克制冲动";
            case "惊": return "宜减少焦虑，放松心情";
            case "杜": return "宜保持耐心，静待时机";
            default: return "保持平常心，顺其自然";
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
    
    private String[] getYiActivitiesDetailed(String star, String door) {
        java.util.ArrayList<String> yiList = new java.util.ArrayList<>();

        if (door != null) {
            switch (door) {
                case "开":
                    yiList.add("开业"); yiList.add("洽谈"); yiList.add("签约");
                    yiList.add("投资"); yiList.add("拓市");
                    break;
                case "生":
                    yiList.add("投资"); yiList.add("置业"); yiList.add("储蓄");
                    yiList.add("合作"); yiList.add("创业");
                    break;
                case "休":
                    yiList.add("休养"); yiList.add("学习"); yiList.add("考试");
                    yiList.add("旅游"); yiList.add("进修");
                    break;
                case "景":
                    yiList.add("文教"); yiList.add("考试"); yiList.add("展示");
                    yiList.add("社交"); yiList.add("会议");
                    break;
                case "杜":
                    yiList.add("保密"); yiList.add("隐匿"); yiList.add("低调");
                    yiList.add("暗查"); yiList.add("内通");
                    break;
                case "伤":
                    yiList.add("健身"); yiList.add("竞技"); yiList.add("出行");
                    yiList.add("协作"); yiList.add("开拓");
                    break;
                case "死":
                    yiList.add("清理"); yiList.add("反思"); yiList.add("守财");
                    yiList.add("调心"); yiList.add("修复");
                    break;
                case "惊":
                    yiList.add("咨询"); yiList.add("评估"); yiList.add("慎决");
                    yiList.add("核实"); yiList.add("预案");
                    break;
            }
        }

        yiList.add("祭祀");
        yiList.add("祈福");
        yiList.add("行善");
        yiList.add("敬老");

        return yiList.toArray(new String[0]);
    }
    
    private String[] getJiActivitiesDetailed(String star, String door) {
        java.util.ArrayList<String> jiList = new java.util.ArrayList<>();

        if (door != null) {
            switch (door) {
                case "死":
                    jiList.add("决策"); jiList.add("签约"); jiList.add("投资");
                    jiList.add("远行"); jiList.add("嫁娶");
                    break;
                case "惊":
                    jiList.add("决策"); jiList.add("签约"); jiList.add("演讲");
                    jiList.add("争执"); jiList.add("诉讼");
                    break;
                case "伤":
                    jiList.add("决策"); jiList.add("投资"); jiList.add("远行");
                    jiList.add("争执"); jiList.add("动土");
                    break;
                case "杜":
                    jiList.add("演讲"); jiList.add("展示"); jiList.add("社交");
                    jiList.add("洽谈"); jiList.add("招标");
                    break;
                case "景":
                    jiList.add("投资"); jiList.add("高风险"); jiList.add("娱乐");
                    jiList.add("酒驾"); jiList.add("熬夜");
                    break;
                case "开":
                    jiList.add("保密"); jiList.add("隐匿"); jiList.add("闭关");
                    break;
                case "休":
                    jiList.add("过劳"); jiList.add("剧动"); jiList.add("冒险");
                    break;
                case "生":
                    jiList.add("浪费"); jiList.add("盲投"); jiList.add("投机");
                    break;
            }
        }

        jiList.add("赌博");
        jiList.add("诉讼");
        jiList.add("口舌");

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
    
    private String getOverallAdviceSimple(boolean isYangDun, int ju, String star, String door) {
        StringBuilder sb = new StringBuilder();

        sb.append("📊 综合建议\n\n");

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
            sb.append("🏆 ★★★ 大吉 · 值符值使皆吉，运势极佳\n");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("👍 ★★ 小吉 - 值符值使一吉，运势平稳\n");
        } else {
            sb.append("⚡ ★ 平平 - 值符值使欠佳，宜谨慎行事\n");
        }
        sb.append("\n");

        sb.append("📋 行事准则\n");
        if (door != null) {
            switch (door) {
                case "开": sb.append("🚀 大胆开创，把握良机\n"); break;
                case "生": sb.append("🌱 稳扎稳打，注重积累\n"); break;
                case "休": sb.append("😌 劳逸结合，养精蓄锐\n"); break;
                case "景": sb.append("✨ 展示才华，言出必行\n"); break;
                case "杜": sb.append("🔒 静守待时，蓄力待发\n"); break;
                case "伤": sb.append("⚠️ 谨慎行事，防损破财\n"); break;
                case "死": sb.append("🛡️ 保守谨慎，清理整顿\n"); break;
                case "惊": sb.append("🔔 镇定自若，防口舌是非\n"); break;
            }
        }

        return sb.toString();
    }

    private String getKongWangExplanation(String kongWang) {
        if (kongWang == null || kongWang.equals("--")) return "空亡主事体不实";
        String[] explanations = {
            "主空虚，防情感，宜充实",
            "主金弱，防肺疾，宜润肺",
            "主火弱，防心疾，宜静心",
            "主土弱，防脾胃，宜健脾",
            "主木弱，防肝胆，宜疏肝",
            "主水弱，防肾虚，宜补肾"
        };
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("戌亥", explanations[0]);
        map.put("申酉", explanations[1]);
        map.put("午未", explanations[2]);
        map.put("辰巳", explanations[3]);
        map.put("寅卯", explanations[4]);
        map.put("子丑", explanations[5]);
        return map.getOrDefault(kongWang, "空亡主事体不实");
    }
    
    private String getMaXingExplanation(String maXing) {
        if (maXing == null || maXing.equals("--")) return "马星主奔波变动";
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("寅", "主动，利出行东方");
        map.put("午", "主动，利出行南方");
        map.put("申", "主动，利出行西方");
        map.put("亥", "主动，利出行北方");
        map.put("巳", "主动，利出行东南");
        map.put("酉", "主动，利出行西北");
        return map.getOrDefault(maXing, "马星主奔波变动");
    }
    
    private String getWangCuiDescription(String[] wangCui, int riGanPalace) {
        if (riGanPalace < 0 || riGanPalace >= wangCui.length) {
            return "旺衰状态不明";
        }
        String status = wangCui[riGanPalace];
        switch (status) {
            case "旺": return "气势最旺，诸事顺遂";
            case "相": return "次吉，多助，事易成";
            case "休": return "平和安宁，不宜强求";
            case "囚": return "困顿受阻，事多不利";
            case "死": return "衰败不利，宜守不宜攻";
            default: return "旺衰状态不明";
        }
    }
    
    private String getQiMenSummary(String yearPillar, String monthPillar, String dayPillar, String timePillar, String zhiFuStar, String zhiShiDoor, boolean isYangDun, int ju) {
        String dayGan = dayPillar.substring(0, 1);
        String dayGanWuXing = getWuXing(dayGan);

        String[] luckyStars = {"天辅", "天心", "天禽", "天任"};
        String[] luckyDoors = {"开", "休", "生"};
        boolean isLuckyStar = false, isLuckyDoor = false;
        if (zhiFuStar != null) {
            for (String s : luckyStars) if (s.equals(zhiFuStar)) isLuckyStar = true;
        }
        if (zhiShiDoor != null) {
            for (String d : luckyDoors) if (d.equals(zhiShiDoor)) isLuckyDoor = true;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(dayGanWuXing).append("日").append(dayGan).append(" · ");
        sb.append(isYangDun ? "阳遁" : "阴遁").append(ju).append("局\n");
        sb.append("值符").append(zhiFuStar).append("星").append(isLuckyStar ? "吉" : "平");
        sb.append(" · 值使").append(zhiShiDoor).append("门").append(isLuckyDoor ? "吉" : "平").append("\n");
        sb.append("整体格局：");
        if (isLuckyStar && isLuckyDoor) {
            sb.append("★★★ 大吉");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("★★ 小吉");
        } else {
            sb.append("★ 平平");
        }
        return sb.toString();
    }
    
    private String getPaipanYiju(String yearPillar, String monthPillar, String dayPillar, String timePillar,
                                  String jieqi, boolean isYangDun, int ju,
                                  String xunShou, String zhiFuStar, String zhiShiDoor,
                                  int zhiFuPalace, int zhiShiPalace) {
        String[] PALACE_NAMES = {"坎一", "坤二", "震三", "巽四", "中五", "乾六", "兑七", "艮八", "离九"};
        
        String getGoldColor = "<font color='#E6C46A'>";
        String getWoodColor = "<font color='#3FA34D'>";
        String getFireColor = "<font color='#E0593B'>";
        String getEarthColor = "<font color='#DEB887'>";
        String getMetalColor = "<font color='#9AA7B8'>";
        String getWaterColor = "<font color='#3E87C2'>";
        String getTitleColor = "<font color='#CCB866'>";
        String getClose = "</font>";
        
        String dunType = isYangDun ? "阳遁" : "阴遁";
        String dunColor = isYangDun ? getWoodColor : getMetalColor;
        String shunNi = isYangDun ? "顺" : "逆";
        
        StringBuilder sb = new StringBuilder();
        
        // 1. 四柱
        sb.append(getTitleColor).append("<b>【四柱】</b>").append(getClose).append("<br/>");
        sb.append("年 ").append(getGoldColor).append(yearPillar).append(getClose)
          .append("　月 ").append(getGoldColor).append(monthPillar).append(getClose)
          .append("　日 ").append(getGoldColor).append(dayPillar).append(getClose)
          .append("　时 ").append(getGoldColor).append(timePillar).append(getClose).append("<br/><br/>");
        
        // 2. 节气局数
        sb.append(getTitleColor).append("<b>【局法】</b>").append(getClose).append("<br/>");
        sb.append("节气：").append(getGoldColor).append(jieqi).append(getClose)
          .append("　").append(dunColor).append(dunType).append(ju).append("局").append(getClose)
          .append("　").append(shunNi).append("行").append("<br/><br/>");
        
        // 3. 旬首值符值使
        sb.append(getTitleColor).append("<b>【值使】</b>").append(getClose).append("<br/>");
        sb.append("旬首：").append(getGoldColor).append(xunShou).append(getClose).append("<br/>");
        sb.append("值符：").append(getFireColor).append(zhiFuStar).append("星").append(getClose)
          .append("　落").append(PALACE_NAMES[zhiFuPalace]).append("宫").append("<br/>");
        sb.append("值使：").append(getWoodColor).append(zhiShiDoor).append("门").append(getClose)
          .append("　落").append(PALACE_NAMES[zhiShiPalace]).append("宫").append("<br/><br/>");
        
        // 4. 三奇六仪
        sb.append(getTitleColor).append("<b>【三奇六仪】</b>").append(getClose).append("<br/>");
        sb.append("三奇：").append(getFireColor).append("丙").append(getClose)
          .append(getWoodColor).append("乙").append(getClose)
          .append(getWaterColor).append("丁").append(getClose).append("<br/>");
        sb.append("六仪：").append(getEarthColor).append("戊己庚辛壬癸").append(getClose).append("<br/>");
        sb.append("顺序：").append(shunNi).append("排 戊己庚辛壬癸丁丙乙").append("<br/><br/>");
        
        // 5. 九星
        sb.append(getTitleColor).append("<b>【九星】</b>").append(getClose).append("<br/>");
        sb.append("值符 ").append(getFireColor).append(zhiFuStar).append(getClose)
          .append(" 落").append(PALACE_NAMES[zhiFuPalace]).append("，")
          .append(shunNi).append("布：蓬芮冲辅禽心柱任英").append("<br/><br/>");
        
        // 6. 八门
        sb.append(getTitleColor).append("<b>【八门】</b>").append(getClose).append("<br/>");
        sb.append("值使 ").append(getWoodColor).append(zhiShiDoor).append(getClose)
          .append(" 落").append(PALACE_NAMES[zhiShiPalace]).append("，")
          .append(shunNi).append("排：休生伤杜景死惊开").append("<br/><br/>");
        
        // 7. 八神
        sb.append(getTitleColor).append("<b>【八神】</b>").append(getClose).append("<br/>");
        sb.append("值符落").append(PALACE_NAMES[zhiFuPalace]).append("，")
          .append(shunNi).append("布：符蛇阴合白玄地天").append("<br/><br/>");
        
        // 8. 排盘关键
        sb.append(getTitleColor).append("<b>【关键】</b>").append(getClose).append("<br/>");
        sb.append(dunColor).append(dunType).append(shunNi).append("行").append(getClose)
          .append("　值符星随时干　值使门随时支<br/>");
        sb.append("天盘主动　地盘主静　星门神仪合断吉凶");
        
        return sb.toString();
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

