package com.example.timedisplay;

import android.app.Activity;
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
    private TextView fullPageFourPillars;
    private TextView fullPagePanelInfo;
    private TextView fullPageDunType;
    private TextView fullPageExplanation;

    private static final long UPDATE_INTERVAL = 1000;
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
        setContentView(R.layout.activity_full_nine_palace);

        fullNinePalacePanel = (FullNinePalacePanel) findViewById(R.id.fullNinePalacePanel);
        fullPageTitle = (TextView) findViewById(R.id.fullPageTitle);
        fullPageFourPillars = (TextView) findViewById(R.id.fullPageFourPillars);
        fullPagePanelInfo = (TextView) findViewById(R.id.fullPagePanelInfo);
        fullPageDunType = (TextView) findViewById(R.id.fullPageDunType);
        fullPageExplanation = (TextView) findViewById(R.id.fullPageExplanation);

        updateHandler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateFullNinePalace();
                long nextSecond = ((SystemClock.uptimeMillis() / 1000) + 1) * 1000;
                updateHandler.postAtTime(this, nextSecond);
            }
        };

        updateFullNinePalace();
        long first = ((SystemClock.uptimeMillis() / 1000) + 1) * 1000;
        updateHandler.postAtTime(updateRunnable, first);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHandler.removeCallbacks(updateRunnable);
        updateFullNinePalace();
        long next = ((SystemClock.uptimeMillis() / 1000) + 1) * 1000;
        updateHandler.postAtTime(updateRunnable, next);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(updateRunnable);
    }

    private void updateFullNinePalace() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String yearPillar = calculateYearPillar(year, month, day);
        String monthPillar = calculateMonthPillar(year, month, day, yearPillar.substring(0, 1));
        String dayPillar = calculateDayPillar(year, month, day);
        String timePillar = calculateTimePillar(hour, minute, dayPillar.substring(0, 1));

        String fourPillarsText = yearPillar + " " + monthPillar + " " + dayPillar + " " + timePillar;
        fullPageFourPillars.setText(fourPillarsText);

        // 根据节气确定阴阳遁和用局数
        String jieqi = getJieqi(year, month, day);
        boolean isYangDun = isYangDunByJieqi(jieqi);
        int ju = getJuShuByJieqi(jieqi);
        
        fullPageDunType.setText(isYangDun ? "阳遁" : "阴遁");
        fullPagePanelInfo.setText(ju + "局");

        calculateAndSetPalaceData(yearPillar, monthPillar, dayPillar, timePillar, jieqi);

        fullPageExplanation.setText(generateExplanation(yearPillar, monthPillar, dayPillar, timePillar, jieqi));
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

    private void calculateAndSetPalaceData(String yearPillar, String monthPillar, String dayPillar, String timePillar, String jieqi) {
        // 根据节气确定阴阳遁和用局数
        boolean isYangDun = isYangDunByJieqi(jieqi);
        int ju = getJuShuByJieqi(jieqi);

        String timeGan = timePillar != null && timePillar.length() >= 1 ? timePillar.substring(0, 1) : "甲";
        String timeZhi = timePillar != null && timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";
        String dayGan = dayPillar != null && dayPillar.length() >= 1 ? dayPillar.substring(0, 1) : "甲";

        // 1. 排地盘（固定顺序）
        String[] diPanTianGan = arrangeDiPanTianGanStandard();

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

        String[] PALACE_NAMES = {"坎", "坤", "震", "巽", "中", "乾", "兑", "艮", "离"};
        String[] DIRECTIONS = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};
        String[] GUA_SYMBOLS = {"☵", "☷", "☳", "☴", "", "☰", "☱", "☶", "☲"};
        String[] DIRECTION_SYMBOLS = {"⬆", "⤢", "➡", "↘", "●", "↖", "⬅", "↗", "⬇"};

        String[][] palaceData = new String[9][2];
        for (int i = 0; i < 9; i++) {
            String star = nineStars[i];
            String door = eightDoors[i];
            String god = eightGods[i];
            String tianGan = tianPanTianGan[i];
            String diGan = diPanTianGan[i];

            String luck = getLuckSymbol(star, door);
            String palaceName = PALACE_NAMES[i] + DIRECTIONS[i];

            palaceData[i][0] = palaceName;
            palaceData[i][1] = star + door + "\n" + tianGan + " " + luck + " " + GUA_SYMBOLS[i] + DIRECTION_SYMBOLS[i];
        }

        fullNinePalacePanel.setPalaceData(palaceData);
        fullNinePalacePanel.setBrightness(0.9f);
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
    private String[] arrangeDiPanTianGanStandard() {
        return new String[]{"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
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
        String[] bashenOrder = {"值符", "螣蛇", "太阴", "六合", "白虎", "玄武", "九地", "九天"};
        
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
            String[] bashenOrderYin = {"值符", "九天", "九地", "玄武", "白虎", "六合", "太阴", "螣蛇"};
            int currentGodIndex = 0;
            int pos = zhiFuPalace;
            while (currentGodIndex < 8) {
                if (pos != 4) {
                    eightGods[pos] = bashenOrderYin[currentGodIndex];
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
        java.util.Arrays.fill(wangCui, "旺相");
        return wangCui;
    }

    private String getLuckSymbol(String star, String door, String god) {
        // 简单的吉凶判断算法
        // 吉星：天辅、天心、天禽、天任
        // 吉门：开、休、生
        boolean isLuckyStar = (star.equals("天辅") || star.equals("天心") || star.equals("天禽") || star.equals("天任"));
        boolean isLuckyDoor = (door.equals("开") || door.equals("休") || door.equals("生"));
        
        if (isLuckyStar && isLuckyDoor) {
            return "吉";
        } else if (isLuckyStar || isLuckyDoor) {
            return "平";
        } else {
            return "凶";
        }
    }
    
    private String getLuckSymbol(String star, String door) {
        // 简单的吉凶判断算法
        // 吉星：天辅、天心、天禽、天任
        // 吉门：开、休、生
        boolean isLuckyStar = (star.equals("天辅") || star.equals("天心") || star.equals("天禽") || star.equals("天任"));
        boolean isLuckyDoor = (door.equals("开") || door.equals("休") || door.equals("生"));
        
        if (isLuckyStar && isLuckyDoor) {
            return "吉";
        } else if (isLuckyStar || isLuckyDoor) {
            return "平";
        } else {
            return "凶";
        }
    }

    private String generateExplanation(String yearPillar, String monthPillar, String dayPillar, String timePillar, String jieqi) {
        StringBuilder sb = new StringBuilder();
        
        boolean isYangDun = isYangDunByJieqi(jieqi);
        int ju = getJuShuByJieqi(jieqi);
        
        String timeGan = timePillar != null && timePillar.length() >= 1 ? timePillar.substring(0, 1) : "甲";
        String timeZhi = timePillar != null && timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";
        
        String[] diPanTianGan = arrangeDiPanTianGanStandard();
        Object[] xunShouInfo = getXunShouInfoStandard(timeGan, timeZhi);
        String zhiFuStar = (String) xunShouInfo[1];
        String zhiShiDoor = (String) xunShouInfo[2];
        
        int zhiFuPalace = getShiGanPosition(diPanTianGan, timeGan);
        String[] nineStars = arrangeNineStarsStandard(zhiFuStar, zhiFuPalace, isYangDun);
        
        String xunShou = (String) xunShouInfo[0];
        int xunShouPalace = getXunShouPalace(xunShou);
        int zhiShiPalace = getZhiShiPalace(xunShouPalace, timeZhi, isYangDun);
        String[] eightDoors = arrangeEightDoorsStandard(zhiShiDoor, zhiShiPalace, isYangDun);
        
        String[] DIRECTIONS = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};
        
        sb.append("🔮 当前运势解读 🔮\n\n");
        
        sb.append("【值符值使】\n");
        sb.append("值符星: ").append(zhiFuStar).append(" - ");
        sb.append(getStarMeaning(zhiFuStar)).append("\n");
        sb.append("值使门: ").append(zhiShiDoor).append(" - ");
        sb.append(getDoorMeaning(zhiShiDoor)).append("\n\n");
        
        StringBuilder luckyDirections = new StringBuilder();
        StringBuilder unluckyDirections = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            String door = eightDoors[i];
            if (door == null || door.isEmpty()) continue;
            if (door.equals("开") || door.equals("休") || door.equals("生")) {
                if (luckyDirections.length() > 0) luckyDirections.append("、");
                luckyDirections.append(DIRECTIONS[i]);
            }
            if (door.equals("死") || door.equals("惊") || door.equals("伤")) {
                if (unluckyDirections.length() > 0) unluckyDirections.append("、");
                unluckyDirections.append(DIRECTIONS[i]);
            }
        }
        
        sb.append("【方位吉凶】\n");
        if (luckyDirections.length() > 0) {
            sb.append("✓ 吉方: ").append(luckyDirections.toString()).append("\n");
            sb.append("  适宜: 求财、谈判、出行、开业\n");
        }
        if (unluckyDirections.length() > 0) {
            sb.append("✗ 凶方: ").append(unluckyDirections.toString()).append("\n");
            sb.append("  宜避: 重要决策、远行、投资\n");
        }
        sb.append("\n");
        
        sb.append("【今日宜忌】\n");
        sb.append("宜: ").append(getYiActivities(zhiFuStar, zhiShiDoor)).append("\n");
        sb.append("忌: ").append(getJiActivities(zhiFuStar, zhiShiDoor)).append("\n\n");
        
        sb.append("【时辰运势】\n");
        sb.append(getTimeFortune(timeZhi)).append("\n\n");
        
        sb.append("【综合建议】\n");
        sb.append(getOverallAdvice(isYangDun, ju, zhiFuStar, zhiShiDoor));
        
        return sb.toString();
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
    
    private String getTimeFortune(String timeZhi) {
        if (timeZhi == null) return "时辰吉利";
        switch (timeZhi) {
            case "子": return "子时(23-1点): 阴气最重，宜静养安神";
            case "丑": return "丑时(1-3点): 肝经当令，深度睡眠";
            case "寅": return "寅时(3-5点): 肺经当令，宜早起";
            case "卯": return "卯时(5-7点): 大肠经当令，宜排便";
            case "辰": return "辰时(7-9点): 胃经当令，宜进食";
            case "巳": return "巳时(9-11点): 脾经当令，精力充沛";
            case "午": return "午时(11-13点): 心经当令，宜午休";
            case "未": return "未时(13-15点): 小肠经当令，宜消化";
            case "申": return "申时(15-17点): 膀胱经当令，宜运动";
            case "酉": return "酉时(17-19点): 肾经当令，宜休息";
            case "戌": return "戌时(19-21点): 心包经当令，宜放松";
            case "亥": return "亥时(21-23点): 三焦经当令，宜入睡";
            default: return "时辰吉利";
        }
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
}
