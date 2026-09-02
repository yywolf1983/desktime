package com.example.timedisplay;

import java.util.Arrays;
import java.util.HashMap;

/**
 * 奇门遁甲时家转盘排盘算法（唯一权威实现）。
 *
 * 首页九宫（NinePalacePanel）与奇门遁甲详情页（FullNinePalaceActivity）都必须
 * 调用本类的 {@link #calculate} 来获取排盘结果，禁止在两处各自实现，以免出现
 * 阴阳遁、三元用局、地盘、天盘、旬首、旺衰等算法分歧导致「吉凶判断不一致」。
 *
 * 算法要点（统一定义）：
 *  - 用局：按节气 + 节气内第几天（三元上/中/下元）查表。
 *  - 地盘：阳遁自局数宫顺排戊己庚辛壬癸丁丙乙；阴遁自局数宫逆排戊乙丙丁癸壬辛庚己。
 *  - 旬首：时辰干支落于六十甲子哪一旬，其值符星/值使门 = 地盘该旬首宫的固定九星/八门（中宫寄坤）。
 *  - 天盘：随值符旋转，使「旬首六仪」（值符星携带之干）落值符宫。
 *  - 旺衰：以「宫五行」为用神、「节气月令五行」为时令判定旺相休囚死。
 *  - 吉凶：综合星、门、神、旺衰打分得到 大吉/吉/平吉/平/平凶/凶/大凶。
 */
public final class QiMenCalculator {

    // ===== 基础干支与排盘常量 =====
    public static final String[] TIANGAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    public static final String[] DIZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    public static final String[] LIUJIAZI = {
        "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉",
        "甲戌", "乙亥", "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未",
        "甲申", "乙酉", "丙戌", "丁亥", "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳",
        "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥", "庚子", "辛丑", "壬寅", "癸卯",
        "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥", "壬子", "癸丑",
        "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉", "壬戌", "癸亥"
    };

    // 定局：阳遁段 / 阴遁段节气序
    private static final String[] YANG_DUN_JIEQI = {"冬至","小寒","大寒","立春","雨水","惊蛰",
                                                   "春分","清明","谷雨","立夏","小满","芒种"};
    private static final String[] YIN_DUN_JIEQI  = {"夏至","小暑","大暑","立秋","处暑","白露",
                                                   "秋分","寒露","霜降","立冬","小雪","大雪"};
    // 三元局数表：[节气索引][上元/中元/下元]
    private static final int[][] YANG_DUN_JU = {
        {1,7,4},{2,8,5},{3,9,6},{8,5,2},{9,6,3},{1,7,4},
        {3,9,6},{4,1,7},{5,2,8},{4,1,7},{5,2,8},{6,3,9}
    };
    private static final int[][] YIN_DUN_JU = {
        {9,3,6},{8,2,5},{7,1,4},{2,5,8},{1,4,7},{9,3,6},
        {7,1,4},{6,9,3},{5,8,2},{6,9,3},{5,8,2},{4,7,1}
    };

    // 地盘九星（宫序：坎坤震巽中乾兑艮离）
    private static final String[] DI_PAN_STARS = {"天蓬","天芮","天冲","天辅","天禽","天心","天柱","天任","天英"};
    // 地盘八门（中宫无门寄坤）
    private static final String[] DI_PAN_DOORS = {"休","死","伤","杜","","开","惊","生","景"};
    // 六甲旬首遁于六仪
    private static final HashMap<String, String> XUNSHOU_GAN = new HashMap<String, String>();
    static {
        XUNSHOU_GAN.put("甲子", "戊"); XUNSHOU_GAN.put("甲戌", "己"); XUNSHOU_GAN.put("甲申", "庚");
        XUNSHOU_GAN.put("甲午", "辛"); XUNSHOU_GAN.put("甲辰", "壬"); XUNSHOU_GAN.put("甲寅", "癸");
    }

    // 节气常用起始（月,日）：与 JIEQI_NAMES 一一对应（仅用于 jieqi 推算，建议调用方统一使用 JieqiData）
    private static final int[][] JIEQI_START = {
        {2,4},{2,19},{3,6},{3,21},{4,5},{4,20},{5,6},{5,21},{6,6},{6,21},{7,7},{7,23},
        {8,8},{8,23},{9,8},{9,23},{10,8},{10,23},{11,7},{11,22},{12,7},{12,22},{1,6},{1,20}
    };
    private static final String[] JIEQI_NAMES = {"立春","雨水","惊蛰","春分","清明","谷雨","立夏","小满","芒种","夏至",
                                                "小暑","大暑","立秋","处暑","白露","秋分","寒露","霜降","立冬","小雪","大雪","冬至","小寒","大寒"};

    // 宫五行（宫序：坎坤震巽中乾兑艮离）
    private static final String[] PALACE_WUXING = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};

    private QiMenCalculator() {}

    /** 排盘结果：两页统一从该结构取数，保证吉凶判读完全一致。 */
    public static class Result {
        public String[] nineStars = new String[9];      // 九星
        public String[] eightDoors = new String[9];      // 八门（中宫为空串）
        public String[] eightGods = new String[9];      // 八神（中宫为空串）
        public String[] tianPanTianGan = new String[9];  // 天盘天干
        public String[] diPanTianGan = new String[9];   // 地盘天干
        public String[] wangCui = new String[9];        // 旺相休囚死
        public String[] luck = new String[9];           // 吉凶（大吉/吉/平吉/平/平凶/凶/大凶）

        public int zhiFuPalace;     // 值符落宫
        public int zhiShiPalace;    // 值使落宫
        public int riGanPalace;     // 日干落宫
        public int shiGanPalace;    // 时干落宫

        public String xunShou;      // 旬首
        public String xunShouGan;   // 旬首六仪
        public String zhiFuStar;    // 值符星
        public String zhiShiDoor;   // 值使门

        public int ju;              // 用局数（1..9，阳遁为正/阴遁在外部以负号表示）
        public boolean isYangDun;   // 阳遁/阴遁
        public String jieqi;        // 节气
    }

    /**
     * 计算奇门排盘。
     *
     * @param yearPillar/monthPillar/dayPillar/timePillar 四柱（如 "甲子"）
     * @param jieqi    节气名（建议由 JieqiData.getCurrentJieqi 取得，保证界面一致）
     * @param dayInJieqi 节气内第几天（1 基，1..15，建议由 JieqiData.getDaysIntoJieqi+1）
     */
    public static Result calculate(String yearPillar, String monthPillar,
                                   String dayPillar, String timePillar,
                                   String jieqi, int dayInJieqi) {
        Result r = new Result();
        r.jieqi = jieqi;
        r.ju = getJuShuByJieqi(jieqi, dayInJieqi);
        r.isYangDun = isYangDunByJieqi(jieqi);

        String timeGan = (timePillar != null && timePillar.length() >= 1) ? timePillar.substring(0, 1) : "甲";
        String timeZhi = (timePillar != null && timePillar.length() >= 2) ? timePillar.substring(1, 2) : "子";
        String dayGan  = (dayPillar != null && dayPillar.length() >= 1) ? dayPillar.substring(0, 1) : "甲";

        int diPanJu = r.isYangDun ? r.ju : -r.ju;
        r.diPanTianGan = arrangeDiPanTianGanStandard(diPanJu);

        Object[] xunShouInfo = getXunShouInfoStandard(timeGan, timeZhi, r.diPanTianGan);
        r.xunShou = (String) xunShouInfo[0];
        r.xunShouGan = (String) xunShouInfo[1];
        int xunShouPalace = (Integer) xunShouInfo[2];
        r.zhiFuStar = (String) xunShouInfo[3];
        r.zhiShiDoor = (String) xunShouInfo[4];

        // 值符随时干：时干为六甲(甲)时不入地盘，取其遁首六仪(旬首六仪)所在宫，即局数宫
        String zhiFuGan = "甲".equals(timeGan) ? r.xunShouGan : timeGan;
        r.zhiFuPalace = getShiGanPosition(r.diPanTianGan, zhiFuGan);
        r.zhiShiPalace = getZhiShiPalace(xunShouPalace, r.xunShou, timeZhi, r.isYangDun);

        r.nineStars = arrangeNineStarsStandard(r.zhiFuStar, r.zhiFuPalace, r.isYangDun);
        r.eightDoors = arrangeEightDoorsStandard(r.zhiShiDoor, r.zhiShiPalace, r.isYangDun);
        r.tianPanTianGan = arrangeTianPanTianGanStandard(r.diPanTianGan, r.xunShouGan, r.zhiFuPalace, r.isYangDun);
        r.eightGods = arrangeEightGodsStandard(r.zhiFuPalace, r.isYangDun);

        r.wangCui = calculateWangCui(jieqi);

        for (int i = 0; i < 9; i++) {
            r.luck[i] = getLuckSymbol(r.nineStars[i], r.eightDoors[i], r.eightGods[i], r.wangCui[i]);
        }

        r.riGanPalace = -1;
        r.shiGanPalace = -1;
        for (int i = 0; i < 9; i++) {
            if (r.tianPanTianGan[i].equals(dayGan)) r.riGanPalace = i;
            if (r.tianPanTianGan[i].equals(timeGan)) r.shiGanPalace = i;
        }
        return r;
    }

    // ===================== 阴阳遁 / 用局（三元） =====================

    public static boolean isYangDunByJieqi(String jieqi) {
        for (String jq : YANG_DUN_JIEQI) {
            if (jq.equals(jieqi)) return true;
        }
        return false;
    }

    /** 三元序号：0上元(第1-5日)、1中元(第6-10日)、2下元(第11-15日)，dayInJieqi 为 1 基。 */
    public static int getYuanIndex(int dayInJieqi) {
        if (dayInJieqi <= 5) return 0;
        if (dayInJieqi <= 10) return 1;
        return 2;
    }

    /** 根据节气与节气内第几天确定用局数（上中下三元）。 */
    public static int getJuShuByJieqi(String jieqi, int dayInJieqi) {
        int yuan = getYuanIndex(dayInJieqi);
        for (int i = 0; i < YANG_DUN_JIEQI.length; i++) {
            if (YANG_DUN_JIEQI[i].equals(jieqi)) return YANG_DUN_JU[i][yuan];
        }
        for (int i = 0; i < YIN_DUN_JIEQI.length; i++) {
            if (YIN_DUN_JIEQI[i].equals(jieqi)) return YIN_DUN_JU[i][yuan];
        }
        return 1;
    }

    // ===================== 地盘 / 天盘 =====================

    /** 排地盘三奇六仪（标准算法）。 */
    public static String[] arrangeDiPanTianGanStandard(int ju) {
        String[] result = new String[9];
        String[] yangOrder = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
        String[] yinOrder  = {"戊", "乙", "丙", "丁", "癸", "壬", "辛", "庚", "己"};

        if (ju > 0) {
            int startPos = ju - 1;
            for (int i = 0; i < 9; i++) {
                result[(startPos + i) % 9] = yangOrder[i];
            }
        } else {
            int startPos = (-ju) - 1;
            for (int i = 0; i < 9; i++) {
                result[(startPos - i + 9) % 9] = yinOrder[i];
            }
        }
        return result;
    }

    public static int getShiGanPosition(String[] diPan, String shiGan) {
        for (int i = 0; i < 9; i++) {
            if (diPan[i].equals(shiGan)) return i;
        }
        return 0;
    }

    /** 获取旬首宫位：六甲遁于六仪，旬首所在宫 = 对应六仪在「地盘」的宫位（随局数变化，不可写死）。 */
    public static int getXunShouPalace(String xunShou, String[] diPan) {
        String gan = XUNSHOU_GAN.get(xunShou);
        if (gan != null) {
            for (int i = 0; i < 9; i++) {
                if (gan.equals(diPan[i])) return i;
            }
        }
        return 0;
    }

    /**
     * 值使门落宫：从旬首本宫（地盘旬首六仪宫）起，按「时支与旬首地支的差值」顺（阳）/逆（阴）移动。
     * 注意：步数必须用「时支序数 − 旬首地支序数」(0..11)，而不能用「时支绝对序数 − 1」，
     * 否则只对甲子旬正确，甲戌/甲申/甲午/甲辰/甲寅旬（含其甲X时）会整体错位。
     * 例：甲戌时（甲戌旬，旬首地支=戌）→ 时支戌与旬首戌差 0 → 值使仍落旬首宫。
     */
    public static int getZhiShiPalace(int xunShouPalace, String xunShou, String timeZhi, boolean isYangDun) {
        int zhiIndex = Arrays.asList(DIZHI).indexOf(timeZhi);
        int shouZhiIndex = Arrays.asList(DIZHI).indexOf(xunShou.substring(1)); // 旬首地支
        int steps = (zhiIndex - shouZhiIndex + 12) % 12; // 与旬首地支的顺逆差值（0..11）

        if (isYangDun) {
            return (xunShouPalace + steps) % 9;
        } else {
            int result = (xunShouPalace - steps + 9) % 9;
            return result < 0 ? result + 9 : result;
        }
    }

    /**
     * 获取旬首信息。
     * 值符星 = 旬首所在宫的「地盘九星」；值使门 = 旬首所在宫的「地盘八门」（中宫寄坤）。
     * @return {旬首, 旬首六仪, 旬首宫, 值符星, 值使门}
     */
    public static Object[] getXunShouInfoStandard(String timeGan, String timeZhi, String[] diPan) {
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
            xunShou = xunshouList[shiIdx / 10];
        }

        String xunShouGan = XUNSHOU_GAN.get(xunShou);
        if (xunShouGan == null) xunShouGan = "戊";
        int xunShouPalace = getXunShouPalace(xunShou, diPan);

        String zhiFuStar = DI_PAN_STARS[xunShouPalace];
        String zhiShiDoor = DI_PAN_DOORS[xunShouPalace];
        if (zhiShiDoor == null || zhiShiDoor.isEmpty()) {
            zhiShiDoor = DI_PAN_DOORS[1]; // 中宫寄坤二宫
        }

        return new Object[]{xunShou, xunShouGan, xunShouPalace, zhiFuStar, zhiShiDoor};
    }

    // ===================== 九星 / 八门 / 八神 =====================

    public static String[] arrangeNineStarsStandard(String zhiFuStar, int zhiFuPalace, boolean isYangDun) {
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

    public static String[] arrangeEightDoorsStandard(String zhiShiDoor, int zhiShiPalace, boolean isYangDun) {
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

    /**
     * 排天盘三奇六仪：天盘随地盘整体旋转，使「旬首六仪」（值符星原本携带之干）落值符宫。
     * 例：阳遁一局己巳时，旬首甲子(戊)原在坎，值符落坤 → 天盘坤宫为戊，而非时干己。
     */
    public static String[] arrangeTianPanTianGanStandard(String[] diPan, String xunShouGan, int zhiFuPalace, boolean isYangDun) {
        String[] tianPan = new String[9];
        String[] order = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};

        int startIdx = 0;
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(xunShouGan)) { startIdx = i; break; }
        }

        if (isYangDun) {
            for (int i = 0; i < 9; i++) {
                tianPan[(zhiFuPalace + i) % 9] = order[(startIdx + i) % 9];
            }
        } else {
            for (int i = 0; i < 9; i++) {
                tianPan[(zhiFuPalace - i + 9) % 9] = order[(startIdx + i) % 9];
            }
        }
        return tianPan;
    }

    public static String[] arrangeEightGodsStandard(int zhiFuPalace, boolean isYangDun) {
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

    // ===================== 旺衰 / 吉凶 =====================

    /**
     * 计算各宫旺相休囚死：以「宫五行」为用神，「节气月令五行」为时令。
     * 用神=时令→旺；时令生用神→相；用神生时令→休；时令克用神→囚；用神克时令→死。
     */
    public static String[] calculateWangCui(String jieqi) {
        String[] wangCui = new String[9];
        String lingWuXing = getYueLingWuXing(jieqi);

        for (int i = 0; i < 9; i++) {
            String yongWuXing = PALACE_WUXING[i];
            if (yongWuXing.equals(lingWuXing)) {
                wangCui[i] = "旺";
            } else if (isSheng(lingWuXing, yongWuXing)) {
                wangCui[i] = "相";
            } else if (isSheng(yongWuXing, lingWuXing)) {
                wangCui[i] = "休";
            } else if (isKe(lingWuXing, yongWuXing)) {
                wangCui[i] = "囚";
            } else {
                wangCui[i] = "死";
            }
        }
        return wangCui;
    }

    /** 节气所属月令五行：寅卯木、巳午火、申酉金、亥子水、辰戌丑未土。 */
    public static String getYueLingWuXing(String jieqi) {
        if (jieqi == null) return "木";
        switch (jieqi) {
            case "立春": case "雨水": return "木";   // 寅月
            case "惊蛰": case "春分": return "木";   // 卯月
            case "清明": case "谷雨": return "土";   // 辰月
            case "立夏": case "小满": return "火";   // 巳月
            case "芒种": case "夏至": return "火";   // 午月
            case "小暑": case "大暑": return "土";   // 未月
            case "立秋": case "处暑": return "金";   // 申月
            case "白露": case "秋分": return "金";   // 酉月
            case "寒露": case "霜降": return "土";   // 戌月
            case "立冬": case "小雪": return "水";   // 亥月
            case "大雪": case "冬至": return "水";   // 子月
            case "小寒": case "大寒": return "土";   // 丑月
            default: return "木";
        }
    }

    public static String getWuXing(String ganOrZhi) {
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

    public static boolean isSheng(String a, String b) {
        return (a.equals("木") && b.equals("火")) ||
               (a.equals("火") && b.equals("土")) ||
               (a.equals("土") && b.equals("金")) ||
               (a.equals("金") && b.equals("水")) ||
               (a.equals("水") && b.equals("木"));
    }

    public static boolean isKe(String a, String b) {
        return (a.equals("木") && b.equals("土")) ||
               (a.equals("火") && b.equals("金")) ||
               (a.equals("土") && b.equals("水")) ||
               (a.equals("金") && b.equals("木")) ||
               (a.equals("水") && b.equals("火"));
    }

    public static String getLuckSymbol(String star, String door, String god, String wangCui) {
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

    public static String getLuckSymbol(String star, String door) {
        return getLuckSymbol(star, door, null, null);
    }
}
