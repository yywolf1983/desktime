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
        JIEQI_MAP.put("立春", new JieqiInfo("立春", "东风解冻，万物复苏", 
                "东风暖", "蛰虫振", "鱼陟冰",
                "阳气初升，冰雪消融，春风送暖", 
                "蛰居的虫类感知阳气，开始苏醒微动", 
                "阳气上升，鱼由水底游近冰面",
                "春季伊始，宜疏肝养阳、早睡早起，多食辛甘发散之品。", "Spring begins"));
        JIEQI_MAP.put("雨水", new JieqiInfo("雨水", "春雨降临，润泽大地", 
                "獭祭鱼", "候雁北", "草木萌",
                "水獭捕鱼陈列岸边，如祭而后食", 
                "大雁感知时令，自南向北迁徙", 
                "春雨润泽，草木萌动发芽",
                "雨水时节，宜健脾祛湿、注意保暖，多食粥汤养生。", "Rain starts falling"));
        JIEQI_MAP.put("惊蛰", new JieqiInfo("惊蛰", "春雷始鸣，蛰虫惊醒", 
                "桃始华", "仓庚鸣", "鹰化鸠",
                "桃花盛开，满树粉红，阳气始盛", 
                "黄鹂鸣叫，春意盎然", 
                "鹰渐少而鸠渐多，古人谓鹰化为鸠",
                "惊蛰时节，宜养肝护脾、防春瘟，多食梨润肺。", "Insects awaken"));
        JIEQI_MAP.put("春分", new JieqiInfo("春分", "昼夜等分，阴阳平衡", 
                "玄鸟至", "雷发声", "始闪电",
                "燕子从南方归来，开始筑巢", 
                "阳气奋动于阴中，奋激而成雷声", 
                "阳气盛极而光发，闪电始现",
                "春分时节，宜调和阴阳、平肝养血，保持情绪平稳。", "Spring Equinox"));
        JIEQI_MAP.put("清明", new JieqiInfo("清明", "天清气朗，万物皆明", 
                "桐始华", "鼠化鴽", "虹始见",
                "梧桐树开花，白花如雪", 
                "阳气盛而田鼠化为鹌鹑，阴消阳长", 
                "阴阳交感，雨后彩虹初现",
                "清明时节，宜踏青赏花、祭扫先人，养肝护目。", "Clear skies, bright days"));
        JIEQI_MAP.put("谷雨", new JieqiInfo("谷雨", "雨生百谷，春将尽矣", 
                "萍始生", "鸠拂羽", "戴胜桑",
                "浮萍始生于水中，水面渐绿", 
                "斑鸠梳理羽翼，农人忙于播种", 
                "戴胜鸟飞落桑树，提示蚕妇养蚕",
                "谷雨时节，宜祛湿健脾、防过敏，多食薏米山药。", "Grain Rain"));
        JIEQI_MAP.put("立夏", new JieqiInfo("立夏", "夏季开始，万物繁茂", 
                "螻蝈鸣", "蚯蚓出", "王瓜生",
                "螻蛄鸣叫，夏意渐浓，阳气旺盛", 
                "蚯蚓感阳气而翻土出土", 
                "王瓜藤蔓生长，赤花向阳",
                "立夏时节，宜养心安神、清淡饮食，多食红色食物。", "Start of Summer"));
        JIEQI_MAP.put("小满", new JieqiInfo("小满", "麦类灌浆，籽粒初满", 
                "苦菜秀", "靡草死", "麦秋至",
                "苦菜开花结实，味苦性寒，可清热", 
                "喜阴之草感阳气而枯死", 
                "麦子灌浆饱满，夏熟作物将熟",
                "小满时节，宜清热祛湿、健脾养胃，多食苦瓜清淡之品。", "Grain Fullness"));
        JIEQI_MAP.put("芒种", new JieqiInfo("芒种", "有芒之种当播，忙种也", 
                "螳螂生", "鵙始鸣", "反舌默",
                "螳螂卵感阴气而孵化而出", 
                "伯劳鸟开始鸣叫，声如流水", 
                "百舌鸟感阴气而停止鸣叫",
                "芒种时节，宜防暑祛湿、劳逸结合，多食瓜果消暑。", "Glume Planting"));
        JIEQI_MAP.put("夏至", new JieqiInfo("夏至", "日最长，阳极阴生", 
                "鹿角解", "蝉始鸣", "半夏生",
                "鹿角脱落，鹿为阳兽，感阴气而角解", 
                "蝉始鸣叫，声闻四野，盛夏之音", 
                "半夏草生，阳极而阴生，阴阳转换",
                "夏至时节，宜养心护阳、避暑生津，多食绿豆瓜果。", "Summer Solstice"));
        JIEQI_MAP.put("小暑", new JieqiInfo("小暑", "暑气渐盛，尚未极热", 
                "温风至", "蟋蟀壁", "鹰始击",
                "温热之风遍及大地，暑气蒸腾", 
                "蟋蟀居壁避暑，羽翼未成", 
                "鹰感阴气，始学搏击长空",
                "小暑时节，宜清热解暑、养心护阴，多食莲子百合。", "Minor Heat"));
        JIEQI_MAP.put("大暑", new JieqiInfo("大暑", "一年最热，酷暑难当", 
                "腐草萤", "土润暑", "大雨行",
                "腐草化为萤火虫，暗夜流光", 
                "土地湿润蒸腾，天气闷热如蒸笼", 
                "大雨时常降临，暑湿交加",
                "大暑时节，宜清热解暑、益气生津，多食西瓜绿豆汤。", "Major Heat"));
        JIEQI_MAP.put("立秋", new JieqiInfo("立秋", "秋季开始，凉风始至", 
                "凉风至", "白露降", "寒蝉鸣",
                "凉爽之风始至，暑气渐消，秋意初现", 
                "清晨白露始降，天气渐凉", 
                "寒蝉鸣叫，声凄而清，秋声渐起",
                "立秋时节，宜润肺生津、早睡早起，多食白色食物。", "Start of Autumn"));
        JIEQI_MAP.put("处暑", new JieqiInfo("处暑", "暑气终止，秋凉渐至", 
                "鹰祭鸟", "天地肃", "禾乃登",
                "鹰捕鸟陈列，如祭而后食，示报本", 
                "天地间始呈肃杀之气，万物渐收", 
                "五谷成熟，开始秋收",
                "处暑时节，宜养阴润肺、防秋燥，多食梨银耳。", "End of Heat"));
        JIEQI_MAP.put("白露", new JieqiInfo("白露", "露凝而白，秋意渐浓", 
                "鸿雁来", "玄鸟归", "鸟养羞",
                "鸿雁感知秋气，自北向南飞", 
                "燕子南飞归去，秋去冬来", 
                "群鸟储备食物，以备冬月之需",
                "白露时节，宜润肺防燥、注意保暖，多食蜂蜜百合。", "White Dew"));
        JIEQI_MAP.put("秋分", new JieqiInfo("秋分", "昼夜等分，阴阳相半", 
                "雷收声", "蛰虫户", "水始涸",
                "雷声渐收，阳气渐弱，阴气渐盛", 
                "蛰虫用泥土封堵洞口，准备冬眠", 
                "雨水渐少，河川水位渐涸",
                "秋分时节，宜养阴润肺、调和脾胃，多食芝麻核桃。", "Autumn Equinox"));
        JIEQI_MAP.put("寒露", new JieqiInfo("寒露", "露气寒冷，将凝为霜", 
                "鸿雁宾", "雀化蛤", "菊有华",
                "鸿雁南飞，后至者为宾", 
                "古人谓黄雀入海化为蛤蜊，实为隐藏不出", 
                "百花凋零，唯菊花傲霜而开",
                "寒露时节，宜养阴防燥、防寒保暖，多食芝麻糯米。", "Cold Dew"));
        JIEQI_MAP.put("霜降", new JieqiInfo("霜降", "初霜降临，草木凋零", 
                "豺祭兽", "草木落", "蛰虫俯",
                "豺狼捕兽陈列，以兽祭天，示杀伐之气", 
                "草木凋零，黄叶飘落，秋将尽矣", 
                "蛰虫皆垂头不动，准备入冬",
                "霜降时节，宜补脾胃、防寒气，多食牛肉栗子。", "Frost Descent"));
        JIEQI_MAP.put("立冬", new JieqiInfo("立冬", "冬季开始，万物收藏", 
                "水始冰", "地始冻", "雉化蜃",
                "河水开始结冰，水面初凝", 
                "土地开始冻结，寒气入地", 
                "古人谓野鸡入海化为大蛤，实为隐藏不见",
                "立冬时节，宜温补肾阳、早睡晚起，多食羊肉栗子。", "Start of Winter"));
        JIEQI_MAP.put("小雪", new JieqiInfo("小雪", "天气渐冷，雪花初落", 
                "虹藏隐", "阴阳降", "闭塞冬",
                "阴盛阳伏，彩虹隐而不见", 
                "天气上升，地气下降，天地闭塞", 
                "天地闭塞不通，万物休止入冬",
                "小雪时节，宜温阳散寒、防忧郁，多食温热滋补之品。", "Minor Snow"));
        JIEQI_MAP.put("大雪", new JieqiInfo("大雪", "大雪纷飞，仲冬开始", 
                "鹖鴠默", "虎始交", "荔挺出",
                "鹖鴠鸟不再鸣叫，感阴气之极", 
                "老虎感阳气而开始交配", 
                "荔挺兰草感阳气而破土而出",
                "大雪时节，宜温补养藏、防寒保暖，多食羊肉生姜。", "Major Snow"));
        JIEQI_MAP.put("冬至", new JieqiInfo("冬至", "阴极阳生，白昼渐长", 
                "蚯蚓结", "麋角解", "水泉动",
                "蚯蚓蜷缩成结，感阳气将动而未出", 
                "麋为阴兽，感阳气而角解落", 
                "天一之阳生，泉水开始流动",
                "冬至时节，宜温补养藏、静养安神，多食饺子汤圆。", "Winter Solstice"));
        JIEQI_MAP.put("小寒", new JieqiInfo("小寒", "寒气至极，尚未大寒", 
                "雁北乡", "鹊始巢", "雉雊鸣",
                "大雁感知阳气萌动，开始向北迁徙", 
                "喜鹊感知阳气，开始筑巢备春", 
                "野鸡鸣叫求偶，阳气渐动",
                "小寒时节，宜温补防寒、养护肾气，多食羊肉核桃。", "Minor Cold"));
        JIEQI_MAP.put("大寒", new JieqiInfo("大寒", "一年最冷，寒至极矣", 
                "鸡始乳", "征鸟疾", "水泽坚",
                "母鸡感阳气开始孵蛋，孕育新生", 
                "鹰隼猛厉迅疾，捕食益凶，为越冬蓄力", 
                "河水冻结至中心，冰厚数尺，寒气凛冽",
                "大寒时节，宜温补脾肾、防寒保暖，多食羊肉当归。", "Major Cold"));
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
        return getJieqi(year, month, day, 12);
    }

    // 带 hour 的重载：节气采用公历日取整（寿星公式固有精度），这里以正午 12:00 为界细化“当日临界”归属，
    // 将原来“整日 ±1 天”的误差收敛为以正午为界的近似，使节气名/倒计时在临界日更贴合真实时刻。
    //
    // 注意：小寒、大寒在每年1月，在节气年顺序中位于冬至之后、立春之前，跨日历年。
    // getJieqiDate(year, 22/23) 返回的是 year+1 年1月的日期（下一节气年的小寒、大寒），
    // 因此当年1月的小寒、大寒需用 getJieqiDate(year-1, 22/23) 获取。
    // 旧实现“早于立春直接返回大寒”忽略了1月初的小寒期，且 i=23 循环用当年立春作为下一节气，
    // 导致1月初到立春前这段日期返回的节气错误。这里改为先单独判断1月、2月初边界，再循环立春到冬至。
    public static String getJieqi(int year, int month, int day, int hour) {
        long now = toMillis(year, month, day, hour);

        // 当年1月的小寒、大寒：用 getJieqiDate(year-1, 22/23) 获取
        int[] xiaohan = getJieqiDate(year - 1, 22);   // [year, 1, ...]
        int[] dahan = getJieqiDate(year - 1, 23);     // [year, 1, ...]
        int[] lichun = getJieqiDate(year, 0);         // [year, 2, ...]

        // 在小寒之前，实际属于上一年的冬至期
        if (now < toMillis(xiaohan[0], xiaohan[1], xiaohan[2], 12)) {
            return "冬至";
        }
        // 小寒 <= now < 大寒
        if (now < toMillis(dahan[0], dahan[1], dahan[2], 12)) {
            return "小寒";
        }
        // 大寒 <= now < 立春
        if (now < toMillis(lichun[0], lichun[1], lichun[2], 12)) {
            return "大寒";
        }

        // 立春及以后：从立春(i=0)到冬至(i=21)循环
        for (int i = 0; i <= 21; i++) {
            int[] jieqiDate = getJieqiDate(year, i);
            int[] nextJieqiDate;
            if (i < 21) {
                nextJieqiDate = getJieqiDate(year, i + 1);
            } else {
                // 冬至(i=21)之后的下一个节气是下一年的小寒
                nextJieqiDate = getJieqiDate(year, 22);   // [year+1, 1, ...]
            }
            long jieqiTime = toMillis(jieqiDate[0], jieqiDate[1], jieqiDate[2], 12);
            long nextJieqiTime = toMillis(nextJieqiDate[0], nextJieqiDate[1], nextJieqiDate[2], 12);
            if (now >= jieqiTime && now < nextJieqiTime) {
                return SOLAR_TERMS[i];
            }
        }
        return "冬至";
    }

    // 将(year, month, day, hour)转换成毫秒时间戳，用于节气日期比较
    private static long toMillis(int year, int month, int day, int hour) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, hour, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    // 根据日历日期上下文，获取小寒、大寒在日历年中的具体日期。
    // 小寒、大寒在1月：若当前在立春之前(1月、2月初)，取当年1月日期；否则取下一年1月日期。
    // 用于 calculateDaysToNextJieqi、JieqiActivity 中需要“按当前日期上下文”获取节气日期的场景。
    public static int[] getJieqiDateByContext(int year, int month, int day, int jieqiIndex) {
        if (jieqiIndex == 22 || jieqiIndex == 23) {
            int[] lichun = getJieqiDate(year, 0);
            boolean beforeLichun = (month < lichun[1]) || (month == lichun[1] && day < lichun[2]);
            if (beforeLichun) {
                // 当前在立春之前，小寒、大寒是当年1月(即 getJieqiDate(year-1, idx))
                return getJieqiDate(year - 1, jieqiIndex);
            } else {
                // 当前在立春及之后，小寒、大寒是下一年1月(即 getJieqiDate(year, idx))
                return getJieqiDate(year, jieqiIndex);
            }
        }
        return getJieqiDate(year, jieqiIndex);
    }

    public static int[] getJieqiDate(int year, int jieqiIndex) {
        // 寿星公式节气常数(year20=20世纪, year21=21世纪);立秋常数原误为28.35,实际为8.35
        double[] year20 = {4.6295, 19.4599, 6.3826, 21.4155, 5.59, 20.88,
                           6.318, 21.86, 6.5, 22.2, 7.28, 23.65,
                           8.35, 23.95, 8.44, 23.822, 9.098, 24.218,
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

        // 2000年及之后用21世纪常数(year21),2000年之前用20世纪常数(year20),与 web/app.js 一致
        double[] solarTerms = (calcYear >= 2000) ? year21 : year20;
        
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
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String currentJieqi = getCurrentJieqi(calendar);
        int currentIndex = getJieqiIndex(currentJieqi);
        int nextIndex = (currentIndex + 1) % 24;

        // 根据当前日期上下文获取下一个节气的日期(处理小寒、大寒的跨年)
        // 旧实现直接用 getJieqiDate(year, nextIndex),对 nextIndex=22/23(小寒/大寒)
        // 会取 year+1 年1月的日期,在1月初(实际下个节气是当年1月的大寒)时算出跨年错误的天数。
        int[] nextJieqiDate = getJieqiDateByContext(year, month, day, nextIndex);
        long nextJieqiTime = toMillis(nextJieqiDate[0], nextJieqiDate[1], nextJieqiDate[2], 12);
        long now = toMillis(year, month, day, hour);

        long diff = nextJieqiTime - now;
        int days = (int) (diff / (1000L * 60 * 60 * 24));

        // 兜底:若仍为负(理论上不应发生),根据节气索引调整年份重算
        if (days < 0) {
            int[] alt;
            if (nextIndex == 22 || nextIndex == 23) {
                int[] lichun = getJieqiDate(year, 0);
                boolean beforeLichun = (month < lichun[1]) || (month == lichun[1] && day < lichun[2]);
                alt = beforeLichun ? getJieqiDate(year, nextIndex) : getJieqiDate(year - 1, nextIndex);
            } else {
                alt = getJieqiDate(year + 1, nextIndex);
            }
            nextJieqiTime = toMillis(alt[0], alt[1], alt[2], 12);
            diff = nextJieqiTime - now;
            days = (int) (diff / (1000L * 60 * 60 * 24));
        }

        return days;
    }
}
