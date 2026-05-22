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
        long next = ((SystemClock.uptimeMillis() / 1000) + 1) * 1000;
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
        String[] DIRECTION_SYMBOLS = {"↑", "↙", "→", "↘", "●", "↖", "←", "↗", "↓"};

        String[][] palaceData = new String[9][2];
        for (int i = 0; i < 9; i++) {
            String star = nineStars[i];
            String door = eightDoors[i];
            String god = eightGods[i];

            String luck = getLuckSymbol(star, door);
            String directionSymbol = DIRECTION_SYMBOLS[i];
            String palaceName = PALACE_NAMES[i] + " " + directionSymbol + " " + DIRECTIONS[i];

            palaceData[i][0] = palaceName;
            palaceData[i][1] = god + " " + star + "\n" + (door != null && !door.isEmpty() ? door : " ") + " " + luck + " " + wangCui[i];
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
            // 阴遁：顺序与阳遁相同，只改变方向
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
        String dayGan = dayPillar != null && dayPillar.length() >= 1 ? dayPillar.substring(0, 1) : "甲";

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
        String[] eightGods = arrangeEightGodsStandard(zhiFuPalace, isYangDun);
        String[] wangCui = calculateWangCui(dayGan);
        
        String[] DIRECTIONS = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};
        
        sb.append("🔮 当前运势解读 🔮\n\n");
        
        sb.append("【📅 今日信息】\n");
        sb.append("年柱 ").append(yearPillar).append(" 月柱 ").append(monthPillar).append("\n");
        sb.append("日柱 ").append(dayPillar).append(" 时柱 ").append(timePillar).append("\n");
        sb.append("节气 ").append(jieqi).append("  遁局 ").append(isYangDun ? "阳" : "阴").append(ju).append("局\n");
        sb.append("旬首 ").append(xunShou).append("\n\n");
        
        sb.append("【🌟 值符值使】\n");
        sb.append("值符 ").append(zhiFuStar).append("\n");
        sb.append(getStarMeaningShort(zhiFuStar)).append("\n");
        sb.append("值使 ").append(zhiShiDoor).append("\n");
        sb.append(getDoorMeaningShort(zhiShiDoor)).append("\n\n");
        
        StringBuilder luckyDirections = new StringBuilder();
        StringBuilder unluckyDirections = new StringBuilder();
        StringBuilder neutralDirections = new StringBuilder();
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
        }
        
        sb.append("【🧭 方位吉凶】\n");
        if (luckyDirections.length() > 0) {
            sb.append("✅ 吉：").append(luckyDirections.toString()).append("\n");
            sb.append("宜求财交易  宜商务谈判\n");
            sb.append("宜出行远行  宜开业创业\n\n");
        }
        if (neutralDirections.length() > 0) {
            sb.append("⚪ 平：").append(neutralDirections.toString()).append("\n");
            sb.append("宜日常活动  宜文书处理\n");
            sb.append("宜人际沟通  宜普通往来\n\n");
        }
        if (unluckyDirections.length() > 0) {
            sb.append("❌ 凶：").append(unluckyDirections.toString()).append("\n");
            sb.append("忌重要决策  忌签约投资\n");
            sb.append("忌远行迁徙  忌重大行动\n\n");
        }
        
        // 旺衰分析
        sb.append("【📈 旺衰分析】\n");
        String dayGanWuXing = getWuXing(dayGan);
        sb.append("日干 ").append(dayGan).append("(").append(dayGanWuXing).append(")\n");
        StringBuilder wangPositions = new StringBuilder();
        StringBuilder xiuPositions = new StringBuilder();
        StringBuilder qiuPositions = new StringBuilder();
        StringBuilder siPositions = new StringBuilder();
        StringBuilder xiangPositions = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (i == 4) continue; // 跳过中宫
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
        if (wangPositions.length() > 0) sb.append("旺位：").append(wangPositions).append(" 利于行动\n");
        if (xiangPositions.length() > 0) sb.append("相位：").append(xiangPositions).append(" 得助力\n");
        if (xiuPositions.length() > 0) sb.append("休位：").append(xiuPositions).append(" 宜休息\n");
        if (qiuPositions.length() > 0) sb.append("囚位：").append(qiuPositions).append(" 受制约\n");
        if (siPositions.length() > 0) sb.append("死位：").append(siPositions).append(" 宜避开\n");
        sb.append("\n");
        
        sb.append("【📋 今日宜忌】\n");
        sb.append("宜:\n");
        String[] yiItems = getYiActivitiesShort(zhiFuStar, zhiShiDoor);
        for (String item : yiItems) {
            sb.append("• ").append(item).append("\n");
        }
        sb.append("\n忌:\n");
        String[] jiItems = getJiActivitiesShort(zhiFuStar, zhiShiDoor);
        for (String item : jiItems) {
            sb.append("• ").append(item).append("\n");
        }
        sb.append("\n");
        
        // 八神分析
        sb.append("【🔮 八神分布】\n");
        String zhiFuGod = eightGods[zhiFuPalace];
        sb.append("值符宫八神：").append(zhiFuGod != null ? zhiFuGod : "无").append("\n");
        sb.append(getGodMeaningShort(zhiFuGod)).append("\n\n");
        
        sb.append("【⏰ 时辰运势】\n");
        sb.append(getShichenName(timeZhi)).append("\n");
        sb.append(getTimeFortuneShort(timeZhi)).append("\n\n");
        
        sb.append("【🌈 综合建议】\n");
        sb.append(getOverallAdviceShort(isYangDun, ju, zhiFuStar, zhiShiDoor, zhiFuGod, dayGan));
        
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
            if (door.equals("开") || door.equals("生")) {
                yiList.add("开业创业");
                yiList.add("商务洽谈");
                yiList.add("签订合同");
                yiList.add("投资理财");
            }
            if (door.equals("休")) {
                yiList.add("休息疗养");
                yiList.add("调养身体");
                yiList.add("学习进修");
                yiList.add("考试面试");
            }
            if (door.equals("景")) {
                yiList.add("文化教育");
                yiList.add("考试培训");
                yiList.add("展示才华");
                yiList.add("社交应酬");
            }
        }
        
        if (star != null) {
            if (star.equals("天辅") || star.equals("天心")) {
                yiList.add("求医问诊");
                yiList.add("医疗养生");
                yiList.add("贵人相助");
            }
            if (star.equals("天任") || star.equals("天蓬")) {
                yiList.add("勤奋工作");
                yiList.add("创业发展");
                yiList.add("开拓市场");
            }
            if (star.equals("天英")) {
                yiList.add("文化创作");
                yiList.add("艺术表演");
                yiList.add("品牌宣传");
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
            if (door.equals("死") || door.equals("惊") || door.equals("伤")) {
                jiList.add("重大决策");
                jiList.add("重要签约");
                jiList.add("冒险投资");
                jiList.add("远行迁徙");
            }
        }
        
        if (star != null) {
            if (star.equals("天芮")) {
                jiList.add("动土施工");
                jiList.add("外科手术");
                jiList.add("暴饮暴食");
            }
            if (star.equals("天冲")) {
                jiList.add("冲动决策");
                jiList.add("争执纠纷");
                jiList.add("高风险投资");
            }
            if (star.equals("天柱")) {
                jiList.add("固执己见");
                jiList.add("盲目投资");
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
        
        sb.append("📊 命盘分析:\n");
        
        if (isYangDun) {
            sb.append("当前为阳遁").append(ju).append("局\n");
            sb.append("阳气旺盛\n利主动出击\n建议：积极行动\n把握机遇\n\n");
        } else {
            sb.append("当前为阴遁").append(ju).append("局\n");
            sb.append("阴气收敛\n利静心守成\n建议：沉稳低调\n蓄势待发\n\n");
        }
        
        sb.append("⭐ 星门吉凶:\n");
        
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
            sb.append("值符值使皆吉\n今日运势极佳\n宜把握机遇\n\n");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("★★ 小吉 ★★\n");
            sb.append("星门一吉一平\n运势良好\n稳中求进\n\n");
        } else if (isNeutralDoor) {
            sb.append("★ 平平 ★\n");
            sb.append("星门无大凶\n运势一般\n谨慎行事\n\n");
        } else {
            sb.append("⚠ 注意 ⚠\n");
            sb.append("星门欠佳\n运势低迷\n宜守不宜动\n\n");
        }
        
        sb.append("💡 综合建议:\n");
        sb.append("• ").append(getDayAdvice(star, door, dayGan)).append("\n");
        sb.append("• 注意调理身心\n");
        sb.append("• 宜在东方活动\n");
        sb.append("• 避开西方北方\n");
        
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
}
