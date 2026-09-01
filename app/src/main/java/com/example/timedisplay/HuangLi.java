package com.example.timedisplay;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 黄历（万年历）推算工具：基于四柱中日柱干支与月支（节月）推算建除十二神，
 * 再映射每日宜忌。用于吉日查询页面的择日判定。
 */
public class HuangLi {

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
    // 建除十二神（顺序固定，建=0）
    public static final String[] JIANCHU = {"建", "除", "满", "平", "定", "执", "破", "危", "成", "收", "开", "闭"};

    // 每日宜忌表（传统择日建除十二神通用歌诀）
    private static final HashMap<String, String[]> YI_MAP = new HashMap<>();
    private static final HashMap<String, String[]> JI_MAP = new HashMap<>();

    static {
        YI_MAP.put("建", new String[]{"出行", "祈福", "求嗣", "动土", "上梁", "开市", "修造", "安门"});
        JI_MAP.put("建", new String[]{"开仓", "出货财", "入宅", "安葬", "移徙"});

        YI_MAP.put("除", new String[]{"祭祀", "祈福", "解除", "疗病", "出行", "移徙", "嫁娶", "扫舍"});
        JI_MAP.put("除", new String[]{"求官", "上任", "远行"});

        YI_MAP.put("满", new String[]{"祭祀", "祈福", "开市", "交易", "立券", "出行", "嫁娶", "安床"});
        JI_MAP.put("满", new String[]{"动土", "安葬", "修造", "求医"});

        YI_MAP.put("平", new String[]{"修造", "嫁娶", "移徙", "出行", "动土", "安葬", "安床", "纳畜"});
        JI_MAP.put("平", new String[]{"词讼", "酝酿", "种植"});

        YI_MAP.put("定", new String[]{"祭祀", "祈福", "嫁娶", "造屋", "入学", "纳财", "安床", "作灶"});
        JI_MAP.put("定", new String[]{"词讼", "出行", "医疗", "上任"});

        YI_MAP.put("执", new String[]{"建屋", "修造", "嫁娶", "捕捉", "收购", "纳财", "安门"});
        JI_MAP.put("执", new String[]{"开市", "移徙", "出行", "求医"});

        YI_MAP.put("破", new String[]{"破屋坏垣", "求医", "治病", "解除", "拆除"});
        JI_MAP.put("破", new String[]{"嫁娶", "出行", "动土", "安葬", "开市", "移徙", "修造"});

        YI_MAP.put("危", new String[]{"安床", "祭祀", "祈福", "捕捉", "安产"});
        JI_MAP.put("危", new String[]{"登高", "出行", "移徙", "动土", "嫁娶"});

        YI_MAP.put("成", new String[]{"嫁娶", "开市", "入学", "安床", "动土", "出行", "安葬", "修造", "纳财", "订盟"});
        JI_MAP.put("成", new String[]{"词讼", "诉讼"});

        YI_MAP.put("收", new String[]{"收购", "纳财", "嫁娶", "入仓", "捕捉", "纳畜"});
        JI_MAP.put("收", new String[]{"放债", "出行", "安葬", "开市"});

        YI_MAP.put("开", new String[]{"开市", "嫁娶", "祭祀", "祈福", "入学", "动土", "出行", "安床", "修造"});
        JI_MAP.put("开", new String[]{"安葬", "放债"});

        YI_MAP.put("闭", new String[]{"筑堤", "安葬", "补垣", "纳畜", "塞穴"});
        JI_MAP.put("闭", new String[]{"开市", "出行", "求医", "动土", "嫁娶"});
    }

    // 儒略日数（整数部分，用于求两日期之间的整数天数差）
    public static int julianDay(int y, int m, int d) {
        if (m <= 2) {
            y -= 1;
            m += 12;
        }
        int a = y / 100;
        int b = 2 - a + a / 4;
        return (int) (365.25 * (y + 4716)) + (int) (30.6001 * (m + 1)) + d + b - 1524;
    }

    // 计算日柱（干支）
    public static String getDayPillar(int y, int m, int d) {
        try {
            int daysDiff = julianDay(y, m, d) - julianDay(1900, 1, 1);
            int baseGanzhiIndex = 10; // 1900年1月1日为甲戌日
            int ganzhiIndex = (baseGanzhiIndex + daysDiff) % 60;
            if (ganzhiIndex < 0) ganzhiIndex += 60;
            return LIUJIAZI[ganzhiIndex];
        } catch (Exception e) {
            return "甲午";
        }
    }

    // 计算月支（节月：以二十四节气为分界），复用 JieqiData 节气判定
    public static String getMonthZhi(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, 12, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        String jieqi = JieqiData.getCurrentJieqi(c);
        switch (jieqi) {
            case "立春":
            case "雨水":  return "寅";
            case "惊蛰":
            case "春分":  return "卯";
            case "清明":
            case "谷雨":  return "辰";
            case "立夏":
            case "小满":  return "巳";
            case "芒种":
            case "夏至":  return "午";
            case "小暑":
            case "大暑":  return "未";
            case "立秋":
            case "处暑":  return "申";
            case "白露":
            case "秋分":  return "酉";
            case "寒露":
            case "霜降":  return "戌";
            case "立冬":
            case "小雪":  return "亥";
            case "大雪":
            case "冬至":  return "子";
            case "小寒":
            case "大寒":  return "丑";
            default:       return "寅";
        }
    }

    // 由日柱与月支推算建除十二神
    public static String getJianChu(String dayPillar, String monthZhi) {
        int dayZhiIdx = Arrays.asList(DIZHI).indexOf(dayPillar.substring(1, 2));
        int monthZhiIdx = Arrays.asList(DIZHI).indexOf(monthZhi);
        if (dayZhiIdx < 0 || monthZhiIdx < 0) return "建";
        int idx = (dayZhiIdx - monthZhiIdx + 12) % 12;
        return JIANCHU[idx];
    }

    public static String[] getYi(String jianChu) {
        String[] r = YI_MAP.get(jianChu);
        return r == null ? new String[]{} : r;
    }

    public static String[] getJi(String jianChu) {
        String[] r = JI_MAP.get(jianChu);
        return r == null ? new String[]{} : r;
    }

    /** 把宜/忌数组合并为“宜：a、b”形式 */
    public static String formatYiJi(String[] items, String prefix) {
        if (items == null || items.length == 0) return prefix + "—";
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < items.length; i++) {
            sb.append(items[i]);
            if (i < items.length - 1) sb.append("、");
        }
        return sb.toString();
    }
}
