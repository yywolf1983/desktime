package com.example.timedisplay;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
                updateHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };

        updateFullNinePalace();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHandler.removeCallbacks(updateRunnable);
        updateHandler.post(updateRunnable);
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

        String monthZhi = (monthPillar != null && monthPillar.length() >= 2) ? monthPillar.substring(1, 2) : "子";
        boolean isYangDun = isYangDun(monthZhi);
        int ju = getJuShu(monthZhi, isYangDun);
        fullPageDunType.setText(isYangDun ? "阳遁" : "阴遁");
        fullPagePanelInfo.setText(ju + "局");

        calculateAndSetPalaceData(yearPillar, monthPillar, dayPillar, timePillar);

        fullPageExplanation.setText(generateExplanation(yearPillar, monthPillar, dayPillar, timePillar));
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

    private void calculateAndSetPalaceData(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        String monthZhi = (monthPillar != null && monthPillar.length() >= 2) ? monthPillar.substring(1, 2) : "子";
        boolean isYangDun = isYangDun(monthZhi);
        int ju = getJuShu(monthZhi, isYangDun);

        String timeGan = timePillar != null && timePillar.length() >= 1 ? timePillar.substring(0, 1) : "甲";
        String timeZhi = timePillar != null && timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";

        String[] diPanTianGan = arrangeDiPanTianGan(ju, isYangDun);
        String[] nineStars = arrangeNineStars(ju, isYangDun, timeGan);
        String[] eightDoors = arrangeEightDoors(ju, isYangDun, timeZhi);
        Object[] xunShouInfo = getXunShouInfo(timeGan, timeZhi);
        String zhiFuStar = (String) xunShouInfo[2];
        String zhiShiDoor = (String) xunShouInfo[3];

        int zhiFuPalace = -1;
        for (int i = 0; i < 9; i++) {
            if (nineStars[i].equals(zhiFuStar)) {
                zhiFuPalace = i;
                break;
            }
        }
        if (zhiFuPalace == -1) {
            zhiFuPalace = 4;
        }

        String[] tianPanTianGan = arrangeTianPanTianGan(diPanTianGan, timeGan, isYangDun, nineStars, zhiFuPalace);
        String[] eightGods = arrangeEightGods(zhiFuPalace, isYangDun, timeGan);

        String[] NINE_STARS = {"天蓬", "天芮", "天冲", "天辅", "天禽", "天心", "天柱", "天任", "天英"};
        String[] EIGHT_DOORS = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
        String[] EIGHT_GODS = {"值符", "螣蛇", "太阴", "六合", "白虎", "玄武", "九地", "九天"};
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

    private String[] arrangeDiPanTianGan(int ju, boolean isYangDun) {
        String[] diPan = new String[9];
        String[] tianGanOrder = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
        
        // 确定起始宫位（用局数减1，因为数组从0开始）
        int startPalace = ju - 1;
        
        if (isYangDun) {
            // 阳遁：顺时针排列
            for (int i = 0; i < 9; i++) {
                int palace = (startPalace + i) % 9;
                diPan[palace] = tianGanOrder[i];
            }
        } else {
            // 阴遁：逆时针排列
            for (int i = 0; i < 9; i++) {
                int palace = (startPalace - i + 9) % 9;
                diPan[palace] = tianGanOrder[i];
            }
        }
        
        return diPan;
    }

    private String[] arrangeNineStars(int ju, boolean isYangDun, String timeGan) {
        String[] nineStars = new String[9];
        
        // 传统九星顺序（与qimen.py一致：天蓬、天芮、天冲、天辅、天禽、天心、天柱、天任、天英）
        String[] jiuxingOrder = {"天蓬", "天芮", "天冲", "天辅", "天禽", "天心", "天柱", "天任", "天英"};
        
        // 按用局数确定起始宫位
        int startGong = ju - 1;
        
        // 阳遁顺排，阴遁逆排
        if (isYangDun) {
            for (int i = 0; i < 9; i++) {
                int gongPos = (startGong + i) % 9;
                nineStars[gongPos] = jiuxingOrder[i];
            }
        } else {
            for (int i = 0; i < 9; i++) {
                int gongPos = (startGong - i + 9) % 9;
                nineStars[gongPos] = jiuxingOrder[i];
            }
        }
        
        return nineStars;
    }

    private String[] arrangeEightDoors(int ju, boolean isYangDun, String timeZhi) {
        String[] eightDoors = new String[9];
        
        // 传统八门顺序
        String[] bamenOrder = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
        
        // 时支对应当起始门
        java.util.Map<String, Integer> shizhiMenMap = new java.util.HashMap<>();
        shizhiMenMap.put("子", 0);
        shizhiMenMap.put("丑", 1);
        shizhiMenMap.put("寅", 2);
        shizhiMenMap.put("卯", 3);
        shizhiMenMap.put("辰", 4);
        shizhiMenMap.put("巳", 5);
        shizhiMenMap.put("午", 6);
        shizhiMenMap.put("未", 7);
        shizhiMenMap.put("申", 0);
        shizhiMenMap.put("酉", 1);
        shizhiMenMap.put("戌", 2);
        shizhiMenMap.put("亥", 3);
        
        int startDoor = shizhiMenMap.getOrDefault(timeZhi, 0);
        
        // 计算八门位置
        for (int i = 0; i < 9; i++) {
            if (i == 4) { // 中五宫
                eightDoors[i] = "生"; // 传统规则：中五宫借用生门
                continue;
            }
            
            int doorIndex;
            if (isYangDun) {
                // 阳遁顺排
                doorIndex = (startDoor + i) % 8;
            } else {
                // 阴遁逆排
                doorIndex = (startDoor - i) % 8;
                if (doorIndex < 0) {
                    doorIndex += 8;
                }
            }
            eightDoors[i] = bamenOrder[doorIndex];
        }
        
        return eightDoors;
    }

    private Object[] getXunShouInfo(String timeGan, String timeZhi) {
        String[][] xunShouTable = {
            {"甲", "子", "戊", "天蓬", "休"},
            {"甲", "戌", "戊", "天蓬", "休"},
            {"甲", "申", "戊", "天蓬", "休"},
            {"甲", "午", "戊", "天蓬", "休"},
            {"甲", "辰", "戊", "天蓬", "休"},
            {"甲", "寅", "戊", "天蓬", "休"},
            {"乙", "丑", "己", "天芮", "生"},
            {"乙", "亥", "己", "天芮", "生"},
            {"乙", "酉", "己", "天芮", "生"},
            {"乙", "未", "己", "天芮", "生"},
            {"乙", "巳", "己", "天芮", "生"},
            {"乙", "卯", "己", "天芮", "生"},
            {"丙", "寅", "庚", "天冲", "伤"},
            {"丙", "子", "庚", "天冲", "伤"},
            {"丙", "戌", "庚", "天冲", "伤"},
            {"丙", "申", "庚", "天冲", "伤"},
            {"丙", "午", "庚", "天冲", "伤"},
            {"丙", "辰", "庚", "天冲", "伤"},
            {"丁", "卯", "辛", "天辅", "杜"},
            {"丁", "丑", "辛", "天辅", "杜"},
            {"丁", "亥", "辛", "天辅", "杜"},
            {"丁", "酉", "辛", "天辅", "杜"},
            {"丁", "未", "辛", "天辅", "杜"},
            {"丁", "巳", "辛", "天辅", "杜"},
            {"戊", "辰", "壬", "天禽", "景"},
            {"戊", "寅", "壬", "天禽", "景"},
            {"戊", "子", "壬", "天禽", "景"},
            {"戊", "戌", "壬", "天禽", "景"},
            {"戊", "申", "壬", "天禽", "景"},
            {"戊", "午", "壬", "天禽", "景"},
            {"己", "巳", "癸", "天心", "死"},
            {"己", "卯", "癸", "天心", "死"},
            {"己", "丑", "癸", "天心", "死"},
            {"己", "亥", "癸", "天心", "死"},
            {"己", "酉", "癸", "天心", "死"},
            {"己", "未", "癸", "天心", "死"},
            {"庚", "午", "丁", "天柱", "惊"},
            {"庚", "辰", "丁", "天柱", "惊"},
            {"庚", "寅", "丁", "天柱", "惊"},
            {"庚", "子", "丁", "天柱", "惊"},
            {"庚", "戌", "丁", "天柱", "惊"},
            {"庚", "申", "丁", "天柱", "惊"},
            {"辛", "未", "丙", "天任", "开"},
            {"辛", "巳", "丙", "天任", "开"},
            {"辛", "卯", "丙", "天任", "开"},
            {"辛", "丑", "丙", "天任", "开"},
            {"辛", "亥", "丙", "天任", "开"},
            {"辛", "酉", "丙", "天任", "开"},
            {"壬", "申", "乙", "天英", "休"},
            {"壬", "午", "乙", "天英", "休"},
            {"壬", "辰", "乙", "天英", "休"},
            {"壬", "寅", "乙", "天英", "休"},
            {"壬", "子", "乙", "天英", "休"},
            {"壬", "戌", "乙", "天英", "休"},
            {"癸", "酉", "甲", "天英", "生"},
            {"癸", "未", "甲", "天英", "生"},
            {"癸", "巳", "甲", "天英", "生"},
            {"癸", "卯", "甲", "天英", "生"},
            {"癸", "丑", "甲", "天英", "生"},
            {"癸", "亥", "甲", "天英", "生"}
        };

        for (String[] entry : xunShouTable) {
            if (entry[0].equals(timeGan) && entry[1].equals(timeZhi)) {
                return new Object[]{entry[2], entry[1], entry[3], entry[4]};
            }
        }

        return new Object[]{"戊", "子", "天蓬", "休"};
    }

    private String[] arrangeEightGods(int zhiFuPalace, boolean isYangDun, String timeGan) {
        String[] eightGods = new String[9];
        
        // 八神顺序：值符、螣蛇、太阴、六合、白虎、玄武、九地、九天
        String[] bashenOrder = {"值符", "螣蛇", "太阴", "六合", "白虎", "玄武", "九地", "九天"};
        
        for (int i = 0; i < 9; i++) {
            int currentPos;
            if (isYangDun) {
                // 阳遁顺排八神
                currentPos = (zhiFuPalace + i) % 9;
            } else {
                // 阴遁逆排八神
                currentPos = (zhiFuPalace - i) % 9;
                if (currentPos < 0) {
                    currentPos += 9;
                }
            }
            eightGods[currentPos] = bashenOrder[i % 8];
        }
        
        return eightGods;
    }

    private String[] arrangeTianPanTianGan(String[] diPanTianGan, String timeGan, boolean isYangDun, String[] nineStars, int zhiFuPalace) {
        String[] tianPanTianGan = new String[9];
        
        // 找到时干在地盘上的位置
        int shiGanGong = -1;
        for (int i = 0; i < 9; i++) {
            if (diPanTianGan[i].equals(timeGan)) {
                shiGanGong = i;
                break;
            }
        }
        
        // 如果没找到时干在地盘的位置，默认在坎宫
        if (shiGanGong == -1) {
            shiGanGong = 0;
        }
        
        // 计算转动量：从值符当前位置转到时干宫位的偏移量
        int rotation = shiGanGong - zhiFuPalace;
        
        // 转动天盘（九星带动天干）
        for (int i = 0; i < 9; i++) {
            int sourceIdx;
            if (isYangDun) {
                // 阳遁顺转
                sourceIdx = (i - rotation) % 9;
                if (sourceIdx < 0) {
                    sourceIdx += 9;
                }
            } else {
                // 阴遁逆转
                sourceIdx = (i + rotation) % 9;
                if (sourceIdx < 0) {
                    sourceIdx += 9;
                }
            }
            tianPanTianGan[i] = diPanTianGan[sourceIdx];
        }
        
        return tianPanTianGan;
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

    private String generateExplanation(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        StringBuilder sb = new StringBuilder();
        
        String monthZhi = (monthPillar != null && monthPillar.length() >= 2) ? monthPillar.substring(1, 2) : "子";
        boolean isYangDun = isYangDun(monthZhi);
        int ju = getJuShu(monthZhi, isYangDun);
        
        String timeGan = timePillar != null && timePillar.length() >= 1 ? timePillar.substring(0, 1) : "甲";
        String timeZhi = timePillar != null && timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";
        
        String[] nineStars = arrangeNineStars(ju, isYangDun, timeGan);
        String[] eightDoors = arrangeEightDoors(ju, isYangDun, timeZhi);
        Object[] xunShouInfo = getXunShouInfo(timeGan, timeZhi);
        String zhiFuStar = (String) xunShouInfo[2];
        String zhiShiDoor = (String) xunShouInfo[3];
        
        String[] NINE_STARS = {"天蓬", "天芮", "天冲", "天辅", "天禽", "天心", "天柱", "天任", "天英"};
        String[] EIGHT_DOORS = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
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
            if (door == null) continue;
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
