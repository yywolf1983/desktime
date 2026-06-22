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
        public String hou1Desc;
        public String hou2Desc;
        public String hou3Desc;
        public String tradition;
        public String english;

        public JieqiInfo(String name, String phenomenon, String hou1, String hou2, String hou3, 
                         String hou1Desc, String hou2Desc, String hou3Desc, String tradition, String english) {
            this.name = name;
            this.phenomenon = phenomenon;
            this.hou1 = hou1;
            this.hou2 = hou2;
            this.hou3 = hou3;
            this.hou1Desc = hou1Desc;
            this.hou2Desc = hou2Desc;
            this.hou3Desc = hou3Desc;
            this.tradition = tradition;
            this.english = english;
        }
    }

    private static final Map<String, JieqiInfo> JIEQI_MAP = new HashMap<>();

    static {
        JIEQI_MAP.put("立春", new JieqiInfo("立春", "东风解冻", 
                "东风暖", "蛰虫振", "鱼陟冰",
                "阳气至而坚冰散，春风化冻", 
                "蛰居的虫类开始苏醒微动", 
                "阳气上升，鱼向上游近于冰下",
                "时令播种，迎接新生命。", "Spring begins"));
        JIEQI_MAP.put("雨水", new JieqiInfo("雨水", "雨水降临", 
                "獭祭鱼", "候雁北", "草木萌",
                "水獭捕鱼陈列岸边，如祭而后食", 
                "大雁自南向北迁徙归来", 
                "草木萌动发芽，正是耕种之时",
                "水润大地，适宜撒播种子。", "Rain starts falling"));
        JIEQI_MAP.put("惊蛰", new JieqiInfo("惊蛰", "万物苏醒", 
                "桃始华", "仓庚鸣", "鹰化鸠",
                "桃花盛开，阳气始盛", 
                "黄鹂鸣叫，春意盎然", 
                "鹰渐少而鸠渐多，古人谓鹰化为鸠",
                "雷声唤醒沉睡的生命。", "Insects awaken"));
        JIEQI_MAP.put("春分", new JieqiInfo("春分", "春分时节", 
                "玄鸟至", "雷发声", "始闪电",
                "燕子归来筑巢", 
                "阳气在阴内奋激而成雷声", 
                "阳气盛而光发，闪电始现",
                "白昼与黑夜长度相近，万象更新。", "Spring Equinox"));
        JIEQI_MAP.put("清明", new JieqiInfo("清明", "天朗气清", 
                "桐始华", "鼠化鴽", "虹始见",
                "梧桐树开花", 
                "阳气盛而田鼠化为鹌鹑", 
                "阴阳交而虹见，云薄漏日则现",
                "祭扫墓地，缅怀先人。", "Clear skies, bright days"));
        JIEQI_MAP.put("谷雨", new JieqiInfo("谷雨", "谷雨时节", 
                "萍始生", "鸠拂羽", "戴胜桑",
                "浮萍始生于水中", 
                "斑鸠梳理羽翼，农人始忙", 
                "戴胜鸟飞落桑树，示蚕妇养蚕",
                "谷物滋养之雨，蔬菜种植的最佳时节。", "Grain Rain"));
        JIEQI_MAP.put("立夏", new JieqiInfo("立夏", "夏季开始", 
                "蝼蝈鸣", "蚯蚓出", "王瓜生",
                "蝼蛄鸣叫，夏意渐浓", 
                "蚯蚓感阳气而出土", 
                "王瓜生长，赤花向阳",
                "太阳能量显著增强，万物生长。", "Start of Summer"));
        JIEQI_MAP.put("小满", new JieqiInfo("小满", "谷物饱满", 
                "苦菜秀", "靡草死", "麦秋至",
                "苦菜开花结实，味苦性寒", 
                "喜阴之草枯死，阳气旺盛", 
                "麦子成熟，虽为夏而有秋意",
                "稻穗初现丰盈之态，夏熟作物籽粒开始饱满。", "Grain Fullness"));
        JIEQI_MAP.put("芒种", new JieqiInfo("芒种", "芒种忙种", 
                "螳螂生", "鵙始鸣", "反舌默",
                "螳螂卵孵化而出", 
                "伯劳鸟开始鸣叫", 
                "百舌鸟停止鸣叫",
                "播种带芒的作物，收割早熟的农作。", "Glume Planting"));
        JIEQI_MAP.put("夏至", new JieqiInfo("夏至", "夏至节气", 
                "鹿角解", "蜩始鸣", "半夏生",
                "鹿角脱落，阳兽感阴", 
                "蝉始鸣叫，声闻四野", 
                "半夏草生，阳极而阴生",
                "一年中最长的一天，太阳能量达到顶峰。", "Summer Solstice"));
        JIEQI_MAP.put("小暑", new JieqiInfo("小暑", "小暑来临", 
                "温风至", "蟋蟀壁", "鹰始击",
                "温热之风遍及大地", 
                "蟋蟀羽翼未成，居壁避暑", 
                "鹰感阴气，始学搏击",
                "微热渐起，宜多进行降温活动。", "Minor Heat"));
        JIEQI_MAP.put("大暑", new JieqiInfo("大暑", "大暑酷热", 
                "腐草萤", "土润暑", "大雨行",
                "腐草化为萤火虫，幽类化为明类", 
                "土地湿润，天气闷热", 
                "大雨时常降临",
                "酷热难耐的盛夏高温期，常伴有暴雨。", "Major Heat"));
        JIEQI_MAP.put("立秋", new JieqiInfo("立秋", "秋季开始", 
                "凉风至", "白露降", "寒蝉鸣",
                "凉爽之风始至，暑气渐消", 
                "白露始降，天气转凉", 
                "寒蝉鸣叫，声凄而清",
                "酷暑退却，秋高气爽的季节来临。", "Start of Autumn"));
        JIEQI_MAP.put("处暑", new JieqiInfo("处暑", "暑气消散", 
                "鹰祭鸟", "天地肃", "禾乃登",
                "鹰捕鸟陈列，不敢先食，示报本", 
                "天地间始呈肃杀之气", 
                "五谷成熟，开始收获",
                "热气消散之时，真正的秋凉尚未到来。", "End of Heat"));
        JIEQI_MAP.put("白露", new JieqiInfo("白露", "白露凝霜", 
                "鸿雁来", "玄鸟归", "鸟养羞",
                "鸿雁自北南飞，大曰鸿小曰雁", 
                "燕子南飞归去", 
                "群鸟储备食物，以备冬月",
                "清晨草叶上凝结出细微的白色露珠。", "White Dew"));
        JIEQI_MAP.put("秋分", new JieqiInfo("秋分", "秋分时节", 
                "雷收声", "蛰虫户", "水始涸",
                "雷声渐收，阳气减弱", 
                "蛰虫用泥土封堵洞口", 
                "雨水减少，河川渐涸",
                "秋季昼夜等长，气候转换的关键点。", "Autumn Equinox"));
        JIEQI_MAP.put("寒露", new JieqiInfo("寒露", "寒露降临", 
                "鸿雁宾", "雀化蛤", "菊有华",
                "雁以仲秋先至者为主，季秋来者为宾", 
                "黄雀入海化为蛤蜊", 
                "草木皆华于阳，独菊华于阴",
                "露水明显变冷，预示着冬季的临近。", "Cold Dew"));
        JIEQI_MAP.put("霜降", new JieqiInfo("霜降", "霜降时节", 
                "豺祭兽", "草木落", "蛰虫俯",
                "豺狼捕兽陈列，以兽祭天", 
                "草木凋零，黄叶飘落", 
                "蛰虫皆垂头不动，入蛰冬眠",
                "初霜开始在植物上覆盖，气温骤降。", "Frost Descent"));
        JIEQI_MAP.put("立冬", new JieqiInfo("立冬", "冬季开始", 
                "水始冰", "地始冻", "雉化蜃",
                "河水开始结冰", 
                "土地开始冻结", 
                "野鸡入海化为大蛤",
                "冬季的正式开启，进入寒冷季节活动期。", "Start of Winter"));
        JIEQI_MAP.put("小雪", new JieqiInfo("小雪", "小雪飘飘", 
                "虹藏隐", "阴阳降", "闭塞冬",
                "阴盛阳伏，彩虹隐而不见", 
                "阳气上升，阴气下降", 
                "天地闭塞不通，万物休止成冬",
                "轻微、温和的降雪开始出现。", "Minor Snow"));
        JIEQI_MAP.put("大雪", new JieqiInfo("大雪", "大雪纷飞", 
                "鹖鴠默", "虎始交", "荔挺出",
                "鹖鴠鸟不再鸣叫，夜鸣求旦之鸟", 
                "老虎开始交配，感阳气而动", 
                "荔挺兰草破土而出",
                "大量且显著的积雪开始落下。", "Major Snow"));
        JIEQI_MAP.put("冬至", new JieqiInfo("冬至", "冬至节气", 
                "蚯蚓结", "麋角解", "水泉动",
                "蚯蚓蜷缩成结，感阳气将动", 
                "麋为阴兽，得阳气而角解", 
                "天一之阳所生水，阳生而动",
                "一年中最短的一天，标志着白昼变长的转折点。", "Winter Solstice"));
        JIEQI_MAP.put("小寒", new JieqiInfo("小寒", "小寒寒冷", 
                "雁北乡", "鹊始巢", "雉雊鸣",
                "大雁感知阳气，向北而行", 
                "喜鹊始筑巢穴，知来岁多风", 
                "野鸡鸣叫求偶，万物萌动",
                "冬至后进入的极度严寒期。", "Minor Cold"));
        JIEQI_MAP.put("大寒", new JieqiInfo("大寒", "大寒酷寒", 
                "鸡始乳", "征鸟疾", "水泽坚",
                "母鸡开始孵蛋，得阳气而卵生", 
                "鹰隼猛厉迅疾，捕食益凶", 
                "河水冻结至中心，冰厚数尺",
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
            solarTerms = year20;
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
