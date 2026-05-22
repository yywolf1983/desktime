package com.example.timedisplay;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JieqiData {

    public static final String[] SOLAR_TERMS = {
            "立春", "雨水", "惊蛰", "春分", "清明", "谷雨",
            "立夏", "小满", "芒种", "夏至", "小暑", "大暑",
            "立秋", "处暑", "白露", "秋分", "寒露", "霜降",
            "立冬", "小雪", "大雪", "冬至", "小寒", "大寒"
    };

    public static class JieqiInfo {
        public String name;
        public String phenomenon;
        public String hou1;
        public String hou2;
        public String hou3;
        public String tradition;
        public String english;

        public JieqiInfo(String name, String phenomenon, String hou1, String hou2, String hou3, String tradition, String english) {
            this.name = name;
            this.phenomenon = phenomenon;
            this.hou1 = hou1;
            this.hou2 = hou2;
            this.hou3 = hou3;
            this.tradition = tradition;
            this.english = english;
        }
    }

    private static final Map<String, JieqiInfo> JIEQI_MAP = new HashMap<>();

    static {
        JIEQI_MAP.put("立春", new JieqiInfo("立春", "东风解冻", "东风解冻", "蛰虫始振", "鱼陟负冰",
                "时令播种，迎接新生命。", "Spring begins"));
        JIEQI_MAP.put("雨水", new JieqiInfo("雨水", "雨水降临", "獭祭鱼", "候雁北", "草木萌动",
                "水润大地，适宜撒播种子。", "Rain starts falling"));
        JIEQI_MAP.put("惊蛰", new JieqiInfo("惊蛰", "万物苏醒", "桃始华", "仓庚鸣", "鹰化为鸠",
                "雷声唤醒沉睡的生命。", "Insects awaken"));
        JIEQI_MAP.put("春分", new JieqiInfo("春分", "春分时节", "玄鸟至", "雷乃发声", "始电",
                "白昼与黑夜长度相近，万象更新。", "Spring Equinox"));
        JIEQI_MAP.put("清明", new JieqiInfo("清明", "天朗气清", "桐始华", "田鼠化为鴽", "虹始见",
                "祭扫墓地，缅怀先人。", "Clear skies, bright days"));
        JIEQI_MAP.put("谷雨", new JieqiInfo("谷雨", "谷雨时节", "萍始生", "鸣鸠拂其羽", "戴胜降于桑",
                "谷物滋养之雨，蔬菜种植的最佳时节。", "Grain Rain"));
        JIEQI_MAP.put("立夏", new JieqiInfo("立夏", "夏季开始", "蝼蝈鸣", "蚯蚓出", "王瓜生",
                "太阳能量显著增强，万物生长。", "Start of Summer"));
        JIEQI_MAP.put("小满", new JieqiInfo("小满", "谷物饱满", "苦菜秀", "靡草死", "麦秋至",
                "稻穗初现丰盈之态，夏熟作物籽粒开始饱满。", "Grain Fullness"));
        JIEQI_MAP.put("芒种", new JieqiInfo("芒种", "芒种忙种", "螳螂生", "鵙始鸣", "反舌无声",
                "播种带芒的作物，收割早熟的农作。", "Glume Planting"));
        JIEQI_MAP.put("夏至", new JieqiInfo("夏至", "夏至节气", "鹿角解", "蜩始鸣", "半夏生",
                "一年中最长的一天，太阳能量达到顶峰。", "Summer Solstice"));
        JIEQI_MAP.put("小暑", new JieqiInfo("小暑", "小暑来临", "温风至", "蟋蟀居壁", "鹰始挚",
                "微热渐起，宜多进行降温活动。", "Minor Heat"));
        JIEQI_MAP.put("大暑", new JieqiInfo("大暑", "大暑酷热", "腐草为萤", "土润溽暑", "大雨时行",
                "酷热难耐的盛夏高温期，常伴有暴雨。", "Major Heat"));
        JIEQI_MAP.put("立秋", new JieqiInfo("立秋", "秋季开始", "凉风至", "白露降", "寒蝉鸣",
                "酷暑退却，秋高气爽的季节来临。", "Start of Autumn"));
        JIEQI_MAP.put("处暑", new JieqiInfo("处暑", "暑气消散", "鹰乃祭鸟", "天地始肃", "禾乃登",
                "热气消散之时，真正的秋凉尚未到来。", "End of Heat"));
        JIEQI_MAP.put("白露", new JieqiInfo("白露", "白露凝霜", "鸿雁来", "玄鸟归", "群鸟养羞",
                "清晨草叶上凝结出细微的白色露珠。", "White Dew"));
        JIEQI_MAP.put("秋分", new JieqiInfo("秋分", "秋分时节", "雷始收声", "蛰虫坯户", "水始涸",
                "秋季昼夜等长，气候转换的关键点。", "Autumn Equinox"));
        JIEQI_MAP.put("寒露", new JieqiInfo("寒露", "寒露降临", "鸿雁来宾", "雀入大水为蛤", "菊有黄华",
                "露水明显变冷，预示着冬季的临近。", "Cold Dew"));
        JIEQI_MAP.put("霜降", new JieqiInfo("霜降", "霜降时节", "豺乃祭兽", "草木黄落", "蛰虫咸俯",
                "初霜开始在植物上覆盖，气温骤降。", "Frost Descent"));
        JIEQI_MAP.put("立冬", new JieqiInfo("立冬", "冬季开始", "水始冰", "地始冻", "雉入大水为蜃",
                "冬季的正式开启，进入寒冷季节活动期。", "Start of Winter"));
        JIEQI_MAP.put("小雪", new JieqiInfo("小雪", "小雪飘飘", "虹藏不见", "天气上升地气下降", "闭塞而成冬",
                "轻微、温和的降雪开始出现。", "Minor Snow"));
        JIEQI_MAP.put("大雪", new JieqiInfo("大雪", "大雪纷飞", "鹖鴠不鸣", "虎始交", "荔挺出",
                "大量且显著的积雪开始落下。", "Major Snow"));
        JIEQI_MAP.put("冬至", new JieqiInfo("冬至", "冬至节气", "蚯蚓结", "麋角解", "水泉动",
                "一年中最短的一天，标志着白昼变长的转折点。", "Winter Solstice"));
        JIEQI_MAP.put("小寒", new JieqiInfo("小寒", "小寒寒冷", "雁北乡", "鹊始巢", "雉雊",
                "冬至后进入的极度严寒期。", "Minor Cold"));
        JIEQI_MAP.put("大寒", new JieqiInfo("大寒", "大寒酷寒", "鸡乳", "征鸟厉疾", "水泽腹坚",
                "全年中最冷的时期，深度的冰冻条件主导气候。", "Major Cold"));
    }

    public static JieqiInfo getJieqiInfo(String jieqiName) {
        return JIEQI_MAP.get(jieqiName);
    }

    public static int getJieqiIndex(String jieqiName) {
        for (int i = 0; i < SOLAR_TERMS.length; i++) {
            if (SOLAR_TERMS[i].equals(jieqiName)) {
                return i;
            }
        }
        return 0;
    }

    public static String getNextJieqi(String currentJieqi) {
        int index = getJieqiIndex(currentJieqi);
        return SOLAR_TERMS[(index + 1) % 24];
    }

    public static String getPrevJieqi(String currentJieqi) {
        int index = getJieqiIndex(currentJieqi);
        return SOLAR_TERMS[(index - 1 + 24) % 24];
    }

    public static String getCurrentJieqi(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        return getJieqi(year, month, day);
    }

    public static String getJieqi(int year, int month, int day) {
        int[] lichunDate = getJieqiDate(year, 0);
        if (month < lichunDate[1] || (month == lichunDate[1] && day < lichunDate[2])) {
            return "大寒";
        }
        
        for (int i = 0; i < SOLAR_TERMS.length; i++) {
            int[] jieqiDate = getJieqiDate(year, i);
            int jYear = jieqiDate[0];
            int jMonth = jieqiDate[1];
            int jDay = jieqiDate[2];
            
            int nextIndex = (i + 1) % SOLAR_TERMS.length;
            int[] nextJieqiDate = getJieqiDate(year, nextIndex);
            int nextYear = nextJieqiDate[0];
            int nextMonth = nextJieqiDate[1];
            int nextDay = nextJieqiDate[2];

            boolean afterJieqi = false;
            if (year > jYear) {
                afterJieqi = true;
            } else if (year == jYear) {
                if (month > jMonth) {
                    afterJieqi = true;
                } else if (month == jMonth && day >= jDay) {
                    afterJieqi = true;
                }
            }
            
            boolean beforeNextJieqi = false;
            if (year < nextYear) {
                beforeNextJieqi = true;
            } else if (year == nextYear) {
                if (month < nextMonth) {
                    beforeNextJieqi = true;
                } else if (month == nextMonth && day < nextDay) {
                    beforeNextJieqi = true;
                }
            }

            if (afterJieqi && beforeNextJieqi) {
                return SOLAR_TERMS[i];
            }
        }
        return "立春";
    }

    public static int[] getJieqiDate(int year, int jieqiIndex) {
        double[] year20 = {4.6295, 19.4599, 6.3826, 21.4155, 5.59, 20.88,
                           6.318, 21.86, 6.5, 22.2, 7.28, 23.65,
                           28.35, 23.95, 8.44, 23.822, 9.098, 24.218,
                           8.218, 23.08, 7.9, 22.6, 6.11, 20.84};
        
        double[] year21 = {3.87, 18.73, 5.63, 20.646, 4.81, 20.1,
                           5.52, 21.04, 5.678, 21.37, 7.108, 22.83,
                           7.5, 23.13, 7.646, 23.042, 8.318, 23.438,
                           7.438, 22.36, 7.18, 21.94, 5.4055, 20.12};

        int[] months = {2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7,
                        8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 1, 1};

        int calcYear = year;
        if (jieqiIndex > 21) {
            calcYear = year + 1;
        }

        int ydNum = calcYear % 100;
        double D = 0.2422;
        
        double[] solarTerms;
        if (calcYear >= 2100) {
            solarTerms = year21;
        } else if (calcYear >= 2000) {
            solarTerms = year21;
        } else {
            solarTerms = year20;
        }
        
        int day = (int) (ydNum * D + solarTerms[jieqiIndex]) - (int) ((ydNum - 1) / 4);
        
        int month = months[jieqiIndex];
        
        if (month == 1 && day > 31) {
            day = 31;
        } else if (month == 2) {
            if ((calcYear % 4 == 0 && calcYear % 100 != 0) || calcYear % 400 == 0) {
                if (day > 29) day = 29;
            } else {
                if (day > 28) day = 28;
            }
        } else if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
            day = 30;
        }

        return new int[]{calcYear, month, day};
    }

    public static int calculateDaysToNextJieqi(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        String currentJieqi = getCurrentJieqi(calendar);
        int currentIndex = getJieqiIndex(currentJieqi);
        int nextIndex = (currentIndex + 1) % 24;

        int[] nextJieqiDate = getJieqiDate(year, nextIndex);
        int targetYear = nextJieqiDate[0];
        int nextMonth = nextJieqiDate[1];
        int nextDay = nextJieqiDate[2];

        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.set(targetYear, nextMonth - 1, nextDay);
        
        long currentMillis = calendar.getTimeInMillis();
        long nextMillis = nextCalendar.getTimeInMillis();
        
        long diff = nextMillis - currentMillis;
        int days = (int) (diff / (1000 * 60 * 60 * 24));
        
        if (days < 0) {
            nextCalendar.set(targetYear + 1, nextMonth - 1, nextDay);
            diff = nextCalendar.getTimeInMillis() - currentMillis;
            days = (int) (diff / (1000 * 60 * 60 * 24));
        }
        
        return days;
    }
}
