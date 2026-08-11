package com.example.timedisplay;

import java.util.HashMap;
import java.util.Map;

public class DestinyCalculator {

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
    
    // 判断五行生克关系（a生b）
    public static boolean isSheng(String a, String b) {
        return (a.equals("木") && b.equals("火")) ||
               (a.equals("火") && b.equals("土")) ||
               (a.equals("土") && b.equals("金")) ||
               (a.equals("金") && b.equals("水")) ||
               (a.equals("水") && b.equals("木"));
    }
    
    // 判断五行生克关系（a克b）
    public static boolean isKe(String a, String b) {
        return (a.equals("木") && b.equals("土")) ||
               (a.equals("火") && b.equals("金")) ||
               (a.equals("土") && b.equals("水")) ||
               (a.equals("金") && b.equals("木")) ||
               (a.equals("水") && b.equals("火"));
    }

    public static String getFiveElementXiJiDetailed(String dayGan, String dayGanWuXing, String yearGan, String yearZhi, String monthGan, String monthZhi, String dayZhi, String timeGan, String timeZhi) {
        StringBuilder sb = new StringBuilder();

        // 以下加权评分与命理页 calcStrength 完全一致的口径：得令、通根为重，个数法仅作补充
        int shengCount = 0, keCount = 0, biCount = 0;
        String[] gans = {yearGan, monthGan, dayGan, timeGan};
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        for (int i = 0; i < 4; i++) {
            String gwx = getWuXing(gans[i]);
            if (gwx.equals(dayGanWuXing)) {
                if (i != 2) biCount++;
            } else if (isSheng(gwx, dayGanWuXing)) {
                shengCount++;
            } else {
                keCount++;
            }
            if (i == 2) {
                String zwx = getWuXing(zhis[i]);
                if (!zwx.equals(dayGanWuXing)) {
                    if (isSheng(zwx, dayGanWuXing)) shengCount++;
                    else keCount++;
                }
            }
        }
        int score = shengCount + biCount - keCount;
        String monthZhiWx = getWuXing(monthZhi);
        if (monthZhiWx.equals(dayGanWuXing)) score += 3;
        else if (isSheng(monthZhiWx, dayGanWuXing)) score += 3;
        else if (isSheng(dayGanWuXing, monthZhiWx)) score -= 2;
        else if (isKe(monthZhiWx, dayGanWuXing)) score -= 3;
        else score -= 1;
        for (int i = 0; i < 4; i++) {
            if (i != 2 && getWuXing(zhis[i]).equals(dayGanWuXing)) score += 1;
        }
        boolean isStrong = score >= 4;
        boolean isWeak = score <= -2;

        // 五行本气出现次数（仅作补充参考）
        int count = 0;
        for (String g : gans) if (getWuXing(g).equals(dayGanWuXing)) count++;
        for (String z : zhis) if (getWuXing(z).equals(dayGanWuXing)) count++;

        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        String xieHao = shengMap.get(dayGanWuXing);   // 我生（泄秀）
        String keHao = keMap.get(dayGanWuXing);       // 我克（耗）
        String shengHao = "";
        for (String key : shengMap.keySet()) {
            if (shengMap.get(key).equals(dayGanWuXing)) {
                shengHao = key;
                break;
            }
        }

        sb.append("日主").append(dayGan).append("属").append(dayGanWuXing).append("，四柱本气出现").append(count).append("次；");

        if (isStrong) {
            sb.append("综合<font color='#E0593B'>身旺</font>（得令或通根有力）");
            sb.append("<br/>").append("<font color='#3FA34D'>喜用神：</font>").append(xieHao).append("（泄秀）、").append(keHao).append("（制劫）");
            sb.append("<br/>").append("<font color='#E0593B'>忌神：</font>").append(shengHao).append("（生身）、").append(dayGanWuXing).append("（比劫帮身）");
            sb.append("<br/>").append("<font color='#7C8C9C'>原因：日主").append(dayGanWuXing).append("偏旺，宜").append(xieHao).append("泄其秀气、").append(keHao).append("制其旺气以趋平衡");
        } else if (isWeak) {
            sb.append("综合<font color='#E0593B'>身弱</font>（失令或无根）");
            sb.append("<br/>").append("<font color='#3FA34D'>喜用神：</font>").append(shengHao).append("（生扶）、").append(dayGanWuXing).append("（比劫帮身）");
            sb.append("<br/>").append("<font color='#E0593B'>忌神：</font>").append(xieHao).append("（泄耗）、").append(keHao).append("（克制）");
            sb.append("<br/>").append("<font color='#7C8C9C'>原因：日主").append(dayGanWuXing).append("偏弱，宜").append(shengHao).append("来生扶、").append(dayGanWuXing).append("同类相助以增气势");
        } else {
            sb.append("综合<font color='#E6C46A'>中和</font>（得令失令相当）");
            sb.append("<br/>").append("<font color='#E6C46A'>喜用神：</font>视具体组合而定，无明显喜忌");
            sb.append("<br/>").append("<font color='#7C8C9C'>原因：日主力量适中，五行不偏不倚，宜顺势而为");
        }
        return sb.toString();
    }
    

    public static String getDestinyOverview(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        StringBuilder sb = new StringBuilder();
        
        String yearGan = yearPillar.substring(0, 1);
        String yearZhi = yearPillar.substring(1, 2);
        String monthGan = monthPillar.substring(0, 1);
        String monthZhi = monthPillar.substring(1, 2);
        String dayGan = dayPillar.substring(0, 1);
        String dayZhi = dayPillar.substring(1, 2);
        String timeGan = timePillar.substring(0, 1);
        String timeZhi = timePillar.substring(1, 2);
        
        String dayGanWuXing = getWuXing(dayGan);
        String[][] pillars = {{yearGan, yearZhi}, {monthGan, monthZhi}, {dayGan, dayZhi}, {timeGan, timeZhi}};
        String[] pillarNames = {"年柱", "月柱", "日柱", "时柱"};
        
        // ═══════════════════════════════════
        // 第一部分：专业命盘（专业人士参考）
        // ═══════════════════════════════════
        sb.append("<font color='#E0593B'><b>━━━ 专业命盘 ━━━</b></font><br/><br/>");
        
        // 1. 四柱排布 + 纳音 + 生肖
        sb.append("<font color='#D9A441'><b>◆ 四柱排布</b></font><br/>");
        sb.append(yearPillar).append("　").append(monthPillar).append("　").append(dayPillar).append("　").append(timePillar).append("<br/><br/>");
        
        sb.append("<font color='#D9A441'><b>◆ 纳音五行</b></font>　<font color='#7C8C9C'>（干支组合的意象属性，反映命局基调）</font><br/>");
        String nYear = getNayin(yearGan, yearZhi);
        String nMonth = getNayin(monthGan, monthZhi);
        String nDay = getNayin(dayGan, dayZhi);
        String nTime = getNayin(timeGan, timeZhi);
        sb.append("年·").append(nYear).append("　月·").append(nMonth).append("<br/>");
        sb.append("日·").append(nDay).append("　时·").append(nTime).append("<br/><br/>");
        
        sb.append("<font color='#D9A441'><b>◆ 生肖属相</b></font>　年支").append(yearZhi).append("·属").append(getZodiacNameFromZhi(yearZhi)).append("<br/><br/>");
        
        // 2. 日主核心分析
        sb.append("<font color='#D9A441'><b>◆ 日主核心</b></font>　<font color='#7C8C9C'>（日干代表命主本人，是命局核心）</font><br/>");
        sb.append("日主 <font color='#D9A441'><b>").append(dayGan).append("</b></font> 属<font color='#3FA34D'><b>").append(dayGanWuXing).append("</b></font>，").append(getGanDescription(dayGan)).append("<br/>");
        sb.append(getDayGanYueLingStatus(dayGan, monthZhi)).append("<br/>");
        sb.append("<font color='#7C8C9C'>").append(getRiGanDetailedAnalysis(dayGan)).append("</font><br/><br/>");
        
        // 十二长生
        String stageDay = getTwelveStage(dayGan, dayZhi);
        String stageTime = getTwelveStage(dayGan, timeZhi);
        String stageMonth = getTwelveStage(dayGan, monthZhi);
        sb.append("<font color='#D9A441'><b>◆ 十二长生</b></font>　<font color='#7C8C9C'>（日主在各柱地支所处的生命阶段）</font><br/>");
        sb.append("日主").append(dayGan).append("在日支").append(dayZhi).append("：<font color='#3FA34D'><b>").append(stageDay).append("</b></font>").append(" — ").append(getTwelveStageExplanation(stageDay)).append("<br/>");
        sb.append("日主").append(dayGan).append("在时支").append(timeZhi).append("：").append(stageTime).append(" — ").append(getTwelveStageExplanation(stageTime)).append("<br/>");
        sb.append("日主").append(dayGan).append("在月支").append(monthZhi).append("（月令）：").append(stageMonth).append(" — ").append(getTwelveStageExplanation(stageMonth)).append("<br/><br/>");
        
        // 3. 五行力量分析
        sb.append("<font color='#D9A441'><b>◆ 五行力量分析</b></font>　<font color='#7C8C9C'>（各柱与日主的生克关系）</font><br/>");
        int shengCount = 0, keCount = 0, biCount = 0;
        
        for (int i = 0; i < pillars.length; i++) {
            String pGan = pillars[i][0];
            String pZhi = pillars[i][1];
            String pName = pillarNames[i];
            
            String pGanWuXing = getWuXing(pGan);
            String pZhiWuXing = getWuXing(pZhi);
            
            // 天干部分
            sb.append(pName).append("天干 ").append(pGan).append("(").append(pGanWuXing).append(")");
            if (pGanWuXing.equals(dayGanWuXing)) {
                sb.append("<font color='#9AA7B8'> ─ 比和</font>");
                if (i != 2) biCount++;
            } else if (isSheng(pGanWuXing, dayGanWuXing)) {
                sb.append("<font color='#3FA34D'> → 生扶日主</font>");
                shengCount++;
            } else if (isKe(pGanWuXing, dayGanWuXing)) {
                sb.append("<font color='#E0593B'> → 克制日主</font>");
                keCount++;
            } else if (isSheng(dayGanWuXing, pGanWuXing)) {
                sb.append("<font color='#F3BA66'> ← 被日主泄耗</font>");
                keCount++;
            } else if (isKe(dayGanWuXing, pGanWuXing)) {
                sb.append("<font color='#3E87C2'> ← 被日主所克</font>");
                keCount++;
            }
            sb.append("<br/>");
            
            // 日柱地支单独列出
            if (i == 2) {
                sb.append(pName).append("地支 ").append(pZhi).append("(").append(pZhiWuXing).append(")");
                if (pZhiWuXing.equals(dayGanWuXing)) {
                    sb.append("<font color='#9AA7B8'> ─ 比和</font>");
                } else if (isSheng(pZhiWuXing, dayGanWuXing)) {
                    sb.append("<font color='#3FA34D'> → 生扶日主</font>");
                    shengCount++;
                } else if (isKe(pZhiWuXing, dayGanWuXing)) {
                    sb.append("<font color='#E0593B'> → 克制日主</font>");
                    keCount++;
                } else if (isSheng(dayGanWuXing, pZhiWuXing)) {
                    sb.append("<font color='#F3BA66'> ← 被日主泄耗</font>");
                    keCount++;
                } else if (isKe(dayGanWuXing, pZhiWuXing)) {
                    sb.append("<font color='#3E87C2'> ← 被日主所克</font>");
                    keCount++;
                }
                sb.append("<br/>");
            }
        }
        sb.append("<br/>");
        
        // 4. 命局强弱判断
        sb.append("<font color='#D9A441'><b>◆ 命局强弱判断</b></font><br/>");
        sb.append("生扶之力：").append(shengCount).append("　克制之力：").append(keCount).append("　比和之力：").append(biCount).append("<br/>");
        
        int balance = shengCount + biCount - keCount;
        if (balance > 0) {
            sb.append("<font color='#3FA34D'><b>日主身强</b></font>（生扶").append(shengCount).append("+比和").append(biCount).append("&gt;克制").append(keCount).append("）<br/>");
            sb.append("<font color='#7C8C9C'>解释：日主").append(dayGan).append("在命局中得到较多生扶和同类相助，气势旺盛、精力充沛，有较强的自我驱动力和抗压能力。</font><br/><br/>");
        } else if (balance < 0) {
            sb.append("<font color='#E0593B'><b>日主身弱</b></font>（克制").append(keCount).append("&gt;生扶").append(shengCount).append("+比和").append(biCount).append("）<br/>");
            sb.append("<font color='#7C8C9C'>解释：日主").append(dayGan).append("在命局中受到的克制和泄耗较多，气势偏弱，需要借助外界力量支持，更适合团队合作而非单打独斗。</font><br/><br/>");
        } else {
            sb.append("<font color='#D9A441'><b>日主中和</b></font>（生扶").append(shengCount).append("+比和").append(biCount).append("=克制").append(keCount).append("）<br/>");
            sb.append("<font color='#7C8C9C'>解释：五行力量均衡，是最理想的命局状态，适应能力强，能够根据环境灵活调整策略。</font><br/><br/>");
        }
        
        // 5. 五行喜忌
        sb.append("<font color='#D9A441'><b>◆ 五行喜忌</b></font><br/>");
        sb.append(getFiveElementXiJiDetailed(dayGan, dayGanWuXing, yearGan, yearZhi, monthGan, monthZhi, dayZhi, timeGan, timeZhi));
        sb.append("<br/><br/>");
        
        // 6. 十神全分析（天干）
        sb.append("<font color='#D9A441'><b>◆ 十神分析</b></font>　<font color='#7C8C9C'>（以日主为基准，看各天干与日主的十神关系）</font><br/>");
        String[] ganShen = getTenGods(dayGan, new String[]{yearGan, monthGan, dayGan, timeGan});
        String[] labelNames = {"年","月","日","时"};
        
        for (int i = 0; i < 4; i++) {
            String tenGod = ganShen[i];
            sb.append(labelNames[i]).append("干").append(pillars[i][0]).append("：<font color='#3FA34D'><b>").append(tenGod).append("</b></font>");
            sb.append("（").append(getTenGodExplanation(tenGod)).append("）<br/>");
        }
        sb.append("<br/>");
        
        // 7. 干支生克关系
        sb.append("<font color='#D9A441'><b>◆ 干支关系</b></font>　<font color='#7C8C9C'>（每柱天干与地支的相互作用）</font><br/>");
        for (int i = 0; i < 4; i++) {
            sb.append(labelNames[i]).append("柱 ").append(pillars[i][0]).append(pillars[i][1]).append("：");
            sb.append(getGanZhiRelationship(pillars[i][0], pillars[i][1]));
            String sDesc = getSeasonDescription(pillars[i][1]);
            if (!sDesc.isEmpty()) sb.append(" · ").append(sDesc);
            sb.append("<br/>");
        }
        sb.append("<br/>");
        
        // ═══════════════════════════════════
        // 第二部分：通俗解读（普通人阅读）
        // ═══════════════════════════════════
        sb.append("<font color='#98D8F0'><b>━━━ 通俗解读 ━━━</b></font><br/><br/>");
        sb.append(getFourPillarComprehensiveAnalysis(yearGan, yearZhi, monthGan, monthZhi, dayGan, dayZhi, timeGan, timeZhi));
        
        return sb.toString();
    }
    
    public static String getWuXingRelation(String wuxing1, String wuxing2) {
        if (wuxing1.equals(wuxing2)) return "<font color='#9AA7B8'>比和（同类相助）</font>";
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        if (shengMap.get(wuxing1).equals(wuxing2)) {
            return "<font color='#3FA34D'>生扶（" + wuxing1 + "生" + wuxing2 + "）</font>";
        } else if (shengMap.get(wuxing2).equals(wuxing1)) {
            return "<font color='#F3BA66'>泄耗（" + wuxing1 + "被" + wuxing2 + "生，泄气）</font>";
        } else if (keMap.get(wuxing1).equals(wuxing2)) {
            return "<font color='#E0593B'>克制（" + wuxing1 + "克" + wuxing2 + "）</font>";
        } else {
            return "<font color='#E0593B'>被克（" + wuxing1 + "被" + wuxing2 + "克）</font>";
        }
    }
    
    public static String getSeasonDescription(String zhi) {
        switch(zhi) {
            case "寅": case "卯": case "辰": return "为春季，木气当旺";
            case "巳": case "午": case "未": return "为夏季，火气当旺";
            case "申": case "酉": case "戌": return "为秋季，金气当旺";
            case "亥": case "子": case "丑": return "为冬季，水气当旺";
            default: return "";
        }
    }
    
    // 判断天干阴阳：阳干 甲丙戊庚壬，阴干 乙丁己辛癸
    public static boolean isYangGan(String gan) {
        return "甲".equals(gan) || "丙".equals(gan) || "戊".equals(gan) || "庚".equals(gan) || "壬".equals(gan);
    }
    
    public static String[] getTenGods(String dayGan, String[] otherGans) {
        String[] result = new String[otherGans.length];
        for (int i = 0; i < otherGans.length; i++) {
            result[i] = getTenGodFull(dayGan, otherGans[i]);
        }
        return result;
    }
    
    // 完整十神（含阴阳区分）
    public static String getTenGodFull(String dayGan, String gan) {
        String dayWuXing = getWuXing(dayGan);
        String ganWuXing = getWuXing(gan);
        boolean dayYang = isYangGan(dayGan);
        boolean ganYang = isYangGan(gan);
        boolean sameYinYang = (dayYang == ganYang);
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        if (ganWuXing.equals(dayWuXing)) return sameYinYang ? "比肩" : "劫财";
        if (shengMap.get(ganWuXing) != null && shengMap.get(ganWuXing).equals(dayWuXing)) return sameYinYang ? "偏印" : "正印";
        if (keMap.get(ganWuXing) != null && keMap.get(ganWuXing).equals(dayWuXing)) return sameYinYang ? "七杀" : "正官";
        if (shengMap.get(dayWuXing) != null && shengMap.get(dayWuXing).equals(ganWuXing)) return sameYinYang ? "食神" : "伤官";
        if (keMap.get(dayWuXing) != null && keMap.get(dayWuXing).equals(ganWuXing)) return sameYinYang ? "偏财" : "正财";
        
        return "比肩";
    }
    
    public static String getTenGod(String dayGan, String gan) {
        return getTenGodFull(dayGan, gan);
    }
    
    // 十神通俗解释
    public static String getTenGodExplanation(String tenGod) {
        switch (tenGod) {
            case "比肩": return "同辈相扶，互竞自强";
            case "劫财": return "共事争锋，慷慨耗财";
            case "正印": return "贵人庇佑，博学能容";
            case "偏印": return "异禀独擅，孤高自许";
            case "食神": return "才藻外宣，温厚安享";
            case "伤官": return "颖悟绝伦，才思纵横";
            case "正财": return "财源有常，勤俭笃实";
            case "偏财": return "理财有道，慷慨好施";
            case "正官": return "纲纪自持，端方守信";
            case "七杀": return "威权果决，敢作敢当";
            default: return tenGod;
        }
    }
    
    // 纳音五行
    public static String getNayin(String gan, String zhi) {
        // 六十甲子纳音表
        String[][] nayin = {
            {"甲子","乙丑","海中金"},{"丙寅","丁卯","炉中火"},{"戊辰","己巳","大林木"},
            {"庚午","辛未","路旁土"},{"壬申","癸酉","剑锋金"},{"甲戌","乙亥","山头火"},
            {"丙子","丁丑","涧下水"},{"戊寅","己卯","城头土"},{"庚辰","辛巳","白蜡金"},
            {"壬午","癸未","杨柳木"},{"甲申","乙酉","泉中水"},{"丙戌","丁亥","屋上土"},
            {"戊子","己丑","霹雳火"},{"庚寅","辛卯","松柏木"},{"壬辰","癸巳","长流水"},
            {"甲午","乙未","沙中金"},{"丙申","丁酉","山下火"},{"戊戌","己亥","平地木"},
            {"庚子","辛丑","壁上土"},{"壬寅","癸卯","金箔金"},{"甲辰","乙巳","覆灯火"},
            {"丙午","丁未","天河水"},{"戊申","己酉","大驿土"},{"庚戌","辛亥","钗钏金"},
            {"壬子","癸丑","桑柘木"},{"甲寅","乙卯","大溪水"},{"丙辰","丁巳","沙中土"},
            {"戊午","己未","天上火"},{"庚申","辛酉","石榴木"},{"壬戌","癸亥","大海水"}
        };
        String pillar = gan + zhi;
        for (String[] n : nayin) {
            if (n[0].equals(pillar) || n[1].equals(pillar)) return n[2];
        }
        return "未知";
    }
    
    // 生肖
    public static String getZodiacNameFromZhi(String zhi) {
        String[] zodiacMap = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
        String[] zodiacName = {"鼠","牛","虎","兔","龙","蛇","马","羊","猴","鸡","狗","猪"};
        for (int i = 0; i < zodiacMap.length; i++) {
            if (zodiacMap[i].equals(zhi)) return zodiacName[i];
        }
        return zhi;
    }

    public static String getZodiacEmoji(String zhi) {
        String[] zodiacMap = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
        String[] zodiacEmoji = {"🐭","🐮","🐯","🐰","🐲","🐍","🐴","🐑","🐵","🐔","🐶","🐷"};
        for (int i = 0; i < zodiacMap.length; i++) {
            if (zodiacMap[i].equals(zhi)) return zodiacEmoji[i];
        }
        return "🐲";
    }
    
    // 十二长生（阳干顺行，阴干逆行）
    public static String getTwelveStage(String gan, String zhi) {
        String[] yangStages = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
        String[] yinStages = {"子","亥","戌","酉","申","未","午","巳","辰","卯","寅","丑"};
        // stages[11]="长生", stages[0]="沐浴" ... — 长生在数组末尾方便取模定位
        String[] stages = {"沐浴","冠带","临官","帝旺","衰","病","死","墓","绝","胎","养","长生"};
        
        if (isYangGan(gan)) {
            int startIdx;
            switch (gan) {
                case "甲": startIdx = 0; break;  // 甲长生在亥，亥在yangStages下标11，(0+11)%12=11→长生✓
                case "丙": startIdx = 9; break;  // 丙长生在寅，(9+2)%12=11→长生✓
                case "戊": startIdx = 9; break;  // 戊长生在寅，同丙
                case "庚": startIdx = 6; break;  // 庚长生在巳，(6+5)%12=11→长生✓
                case "壬": startIdx = 3; break;  // 壬长生在申，(3+8)%12=11→长生✓
                default: return "未知";
            }
            for (int i = 0; i < 12; i++) {
                if (yangStages[i].equals(zhi)) return stages[(startIdx + i) % 12];
            }
        } else {
            int startIdx;
            switch (gan) {
                case "乙": startIdx = 5; break;  // 乙长生在午，(5+6)%12=11→长生✓
                case "丁": startIdx = 8; break;  // 丁长生在酉，(8+3)%12=11→长生✓
                case "己": startIdx = 8; break;  // 己长生在酉，同丁
                case "辛": startIdx = 11; break; // 辛长生在子，(11+0)%12=11→长生✓
                case "癸": startIdx = 2; break;  // 癸长生在卯，(2+9)%12=11→长生✓
                default: return "未知";
            }
            for (int i = 0; i < 12; i++) {
                if (yinStages[i].equals(zhi)) return stages[(startIdx + i) % 12];
            }
        }
        return "未知";
    }
    
    // 十二长生通俗解释
    public static String getTwelveStageExplanation(String stage) {
        switch (stage) {
            case "长生": return "生气方盛，宜图新";
            case "沐浴": return "易感多变，戒情牵";
            case "冠带": return "渐及成立，宜任事";
            case "临官": return "事业方隆，宜进取";
            case "帝旺": return "盛极当位，防满损";
            case "衰": return "气渐就衰，宜养元";
            case "病": return "宜静养，慎摄身";
            case "死": return "宜退守，俟运转";
            case "墓": return "宜敛藏，求稳毋妄";
            case "绝": return "伏处幽潜，含滋育新";
            case "胎": return "默运机缄，待时动";
            case "养": return "蓄力培基，俟勃发";
            default: return "";
        }
    }
    
    // 纳音通俗解释
    public static String getNayinExplanation(String nayin) {
        switch (nayin) {
            case "海中金": return "金沉于渊，待炼乃成";
            case "炉中火": return "火炽于炉，炼物成器";
            case "大林木": return "木植深根，历久弥坚";
            case "路旁土": return "土处通途，平实可信";
            case "剑锋金": return "金出锋锷，刚毅难犯";
            case "山头火": return "火起冈陵，骤发难久";
            case "涧下水": return "水行石涧，清驶而灵";
            case "城头土": return "土筑墉垣，崇固可守";
            case "白蜡金": return "金含脂泽，外朴内秀";
            case "杨柳木": return "木袅堤岸，因势而安";
            case "泉中水": return "水潜幽源，润物无声";
            case "屋上土": return "土覆栋宇，蔽雨庇人";
            case "霹雳火": return "火奋雷霆，性猛难制";
            case "松柏木": return "木挺寒岁，贞操不改";
            case "长流水": return "水逝不息，渐进日新";
            case "沙中金": return "金隐沙砾，淘漉乃见";
            case "山下火": return "火温岩阿，内蕴外敛";
            case "平地木": return "木生旷原，朴茂自然";
            case "壁上土": return "土垩垣墙，安居可托";
            case "金箔金": return "金施华饰，绚采尚精";
            case "覆灯火": return "灯火温帏，照夜传薪";
            case "天河水": return "水落自天，浩瀚无垠";
            case "大驿土": return "土接驰道，通达四方";
            case "钗钏金": return "金饰环佩，工巧妍丽";
            case "桑柘木": return "木荫蚕野，惠泽及物";
            case "大溪水": return "水奔溪壑，纵肆不羁";
            case "沙中土": return "土积丘墟，聚则成形";
            case "天上火": return "火丽于天，光焰烛远";
            case "石榴木": return "木实繁枝，家道乃兴";
            case "大海水": return "水纳百川，襟量弘深";
            default: return "";
        }
    }
    
    public static String getFiveElementXiJi(String dayGan, String dayWuXing, String yearGan, String yearZhi, String monthGan, String monthZhi, String dayZhi, String timeGan, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        
        int count = 0;
        String[] allGans = {yearGan, monthGan, dayGan, timeGan};
        String[] allZhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        
        for (String gan : allGans) {
            if (getWuXing(gan).equals(dayWuXing)) count++;
        }
        for (String zhi : allZhis) {
            if (getWuXing(zhi).equals(dayWuXing)) count++;
        }
        
        sb.append("日主").append(dayGan).append("属").append(dayWuXing);
        if (count >= 3) {
            sb.append("<font color='#E0593B'>偏旺</font>，");
            sb.append("<font color='#3FA34D'>喜</font>克泄耗之五行，");
            sb.append("<font color='#E0593B'>忌</font>生扶比和之五行");
        } else if (count <= 1) {
            sb.append("<font color='#E0593B'>偏弱</font>，");
            sb.append("<font color='#3FA34D'>喜</font>生扶比和之五行，");
            sb.append("<font color='#E0593B'>忌</font>克泄耗之五行");
        } else {
            sb.append("<font color='#D9A441'>中和</font>，");
            sb.append("五行停匀，喜忌不彰，宜顺时动");
        }
        
        return sb.toString();
    }
    
    public static String getPersonalityAnalysis(String dayGan, String dayZhi) {
        StringBuilder sb = new StringBuilder();
        sb.append(getGanDescription(dayGan)).append(" · ").append(getZhiDescription(dayZhi));
        return sb.toString();
    }
    
    public static String getFortuneAnalysis(String dayGan, String dayWuXing, String monthGan, String monthZhi) {
        StringBuilder sb = new StringBuilder();
        String monthWuXing = getWuXing(monthZhi);
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        if (monthWuXing.equals(dayWuXing)) {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，与日主").append(dayGan).append("比和，运势平稳");
        } else if (shengMap.get(monthWuXing).equals(dayWuXing)) {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，生扶日主").append(dayGan).append("，运势助力");
        } else if (keMap.get(monthWuXing).equals(dayWuXing)) {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，克制日主").append(dayGan).append("，运势压力");
        } else if (shengMap.get(dayWuXing).equals(monthWuXing)) {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，被日主").append(dayGan).append("生扶，运势消耗");
        } else {
            sb.append("当前月令").append(monthZhi).append("属").append(monthWuXing).append("，克制日主").append(dayGan).append("，运势受阻");
        }
        
        return sb.toString();
    }
    
    public static String getPillarAnalysis(String gan, String zhi, String pillarType) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        String ganDesc = getGanDescription(gan);
        String zhiDesc = getZhiDescription(zhi);
        
        String relationship = getGanZhiRelationship(gan, zhi);
        
        return ganDesc + " · " + zhiDesc + " · " + relationship;
    }

    public static String getGanDescription(String gan) {
        switch (gan) {
            case "甲": return "阳木·主尊贵权威";
            case "乙": return "阴木·主柔顺仁慈";
            case "丙": return "阳火·主光明热情";
            case "丁": return "阴火·主文明细致";
            case "戊": return "阳土·主稳重诚信";
            case "己": return "阴土·主包容厚德";
            case "庚": return "阳金·主果断刚毅";
            case "辛": return "阴金·主精致细腻";
            case "壬": return "阳水·主智慧流动";
            case "癸": return "阴水·主聪明神秘";
            default: return gan + "·未知";
        }
    }

    public static String getZhiDescription(String zhi) {
        String[] zodiac = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
        String[] zodiacMap = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        String zodiacName = "";
        for (int i = 0; i < zodiacMap.length; i++) {
            if (zodiacMap[i].equals(zhi)) {
                zodiacName = zodiac[i];
                break;
            }
        }
        
        switch (zhi) {
            case "子": return "鼠·水·北方·主智慧藏蓄";
            case "丑": return "牛·土·东北·主积蓄稳重";
            case "寅": return "虎·木·东北·主生发进取";
            case "卯": return "兔·木·东方·主生长繁荣";
            case "辰": return "龙·土·东南·主变化升腾";
            case "巳": return "蛇·火·东南·主温暖礼仪";
            case "午": return "马·火·南方·主旺盛显达";
            case "未": return "羊·土·西南·主收藏终结";
            case "申": return "猴·金·西南·主肃杀变革";
            case "酉": return "鸡·金·西方·主收敛收获";
            case "戌": return "狗·土·西北·主收藏防备";
            case "亥": return "猪·水·西北·主流动变化";
            default: return zhi + "·未知";
        }
    }

    public static String getGanZhiRelationship(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        if (ganWuXing.equals(zhiWuXing)) {
            return "比和·相助";
        }
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        if (shengMap.get(ganWuXing).equals(zhiWuXing)) {
            return "天干生地支·泄秀";
        }
        if (shengMap.get(zhiWuXing).equals(ganWuXing)) {
            return "地支生天干·得助";
        }
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        if (keMap.get(ganWuXing).equals(zhiWuXing)) {
            return "天干克地支·制杀";
        }
        if (keMap.get(zhiWuXing).equals(ganWuXing)) {
            return "地支克天干·受制";
        }
        
        return "关系一般";
    }
    
    public static String getRiGanDetailedAnalysis(String gan) {
        switch (gan) {
            case "甲":
                return "甲木日主，仁厚刚直，有雄才之量，具开创之风。利东方，宜司牧、将帅、林麓之业。";
            case "乙":
                return "乙木日主，温润善变，多艺而敏，具风雅之姿。利东方，宜文翰、庠序、容饰之业。";
            case "丙":
                return "丙火日主，热情朗曜，有领袖之威，声名易彰。利南方，宜文教、薪传、庖厨之业。";
            case "丁":
                return "丁火日主，细密善思，怀锦心之慧，才藻内蕴。利南方，宜机巧、文翰、服御之业。";
            case "戊":
                return "戊土日主，敦厚诚信，能容载万物，足可托付。利中央，宜版筑、田庐、仓廪之业。";
            case "己":
                return "己土日主，宽和乐施，有含弘之德，善育群生。利中央，宜农桑、医济、慈惠之业。";
            case "庚":
                return "庚金日主，果毅刚断，持正义之衡，临难不苟。利西方，宜泉布、戎政、百工之业。";
            case "辛":
                return "辛金日主，清雅好修，尚精微之美，品藻自高。利西方，宜珠玉、丹青、匠作之业。";
            case "壬":
                return "壬水日主，睿智通变，有江湖之量，善贾而知时。利北方，宜懋迁、舟楫、漕运之业。";
            case "癸":
                return "癸水日主，幽微多智，具玄览之明，深识隐机。利北方，宜稽古、运筹、秘计之业。";
            default:
                return "日干者，命主之身也，主性情材器所禀。";
        }
    }
    
    public static String getDayGanYueLingStatus(String dayGan, String monthZhi) {
        String dayWuXing = getWuXing(dayGan);
        String monthWuXing = getWuXing(monthZhi);
        
        if (dayWuXing.equals(monthWuXing)) {
            return "月令" + monthZhi + "属" + monthWuXing + "，与日主比和，<font color='#D9A441'><b>得令有力</b></font>";
        } else if (isSheng(monthWuXing, dayWuXing)) {
            return "月令" + monthZhi + "属" + monthWuXing + "，生扶日主，<font color='#3FA34D'><b>得令生助</b></font>";
        } else if (isSheng(dayWuXing, monthWuXing)) {
            return "月令" + monthZhi + "属" + monthWuXing + "，日主泄气，<font color='#F3BA66'><b>失令泄气</b></font>";
        } else if (isKe(monthWuXing, dayWuXing)) {
            return "月令" + monthZhi + "属" + monthWuXing + "，克制日主，<font color='#E0593B'><b>失令受制</b></font>";
        } else {
            return "月令" + monthZhi + "属" + monthWuXing + "，与日主无直接关系";
        }
    }
    
    public static String getFourPillarSummary(String yearGan, String yearZhi, String monthGan, String monthZhi,
                                        String dayGan, String dayZhi, String timeGan, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        
        String dayGanWuXing = getWuXing(dayGan);
        sb.append("日主五行：").append(dayGanWuXing).append("\n");
        
        int shengCount = 0, keCount = 0, biCount = 0;
        String[] pillars = {yearGan, yearZhi, monthGan, monthZhi, dayGan, dayZhi, timeGan, timeZhi};
        
        for (int i = 0; i < pillars.length; i += 2) {
            String pGan = pillars[i];
            String pZhi = pillars[i + 1];
            
            String pWuXing = getWuXing(pGan);
            if (pWuXing.equals(dayGanWuXing)) biCount++;
            
            java.util.Map<String, String> shengMap = new java.util.HashMap<>();
            shengMap.put("木", "火"); shengMap.put("火", "土");
            shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
            
            if (shengMap.get(pWuXing).equals(dayGanWuXing)) shengCount++;
            
            java.util.Map<String, String> keMap = new java.util.HashMap<>();
            keMap.put("木", "土"); keMap.put("火", "金");
            keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
            
            if (keMap.get(pWuXing).equals(dayGanWuXing)) keCount++;
        }
        
        sb.append("生扶：").append(shengCount).append("个 · 克制：").append(keCount).append("个 · 比和：").append(biCount).append("个\n");
        
        if (shengCount > keCount) {
            sb.append("身强·宜泄耗克制，适合创业发展");
        } else if (keCount > shengCount) {
            sb.append("身弱·宜生扶比和，适合稳健守成");
        } else {
            sb.append("中和·五行均衡，运势平稳顺畅");
        }
        
        return sb.toString();
    }
    
    public static String getGanDetailedAnalysis(String gan) {
        switch (gan) {
            case "甲": return "阳木·尊贵威重、领袖之姿。性刚直有断，宜秉钧、戎政。得令勃发，失令志存业隐。";
            case "乙": return "阴木·仁惠多能、风雅之质。性和柔善调，宜文藻、容饰。乙柔韧有守，以柔克刚。";
            case "丙": return "阳火·朗曜扬声、显达之象。性外朗充周，宜货殖、折冲。盛则光被四表，失令焰易歇。";
            case "丁": return "阴火·文明精微、才俊之秀。性内敛尚细，宜丹青、巧工。丁微能破暗烛幽。";
            case "戊": return "阳土·淳厚载物、信义之德。性沉稳可任，宜版筑、仓庾。得令厚德流光，失令执而不通。";
            case "己": return "阴土·含弘育物、慈惠之怀。性温良善育，宜农桑、医济。己柔能孳生万物。";
            case "庚": return "阳金·刚断任事、义烈之威。性果毅不挠，宜兵刑、纲纪。得令锋不可当，失令锐或伤。";
            case "辛": return "阴金·精粹尚美、清雅之鉴。性敏缜好修，宜金玉、匠意。辛柔粹然可玩。";
            case "壬": return "阳水·智流通变、汪洋之量。性豁朗善徙，宜懋迁、河渠。得令奔流不回，失令滥而失防。";
            case "癸": return "阴水·玄览幽微、睿哲之明。性黠慧多密，宜稽古、运帷。癸柔能浸润无间。";
            default: return gan + "·未知天干";
        }
    }
    
    public static String getZhiDetailedAnalysis(String zhi) {
        String[] zodiac = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
        String[] zodiacMap = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        String zodiacName = "";
        for (int i = 0; i < zodiacMap.length; i++) {
            if (zodiacMap[i].equals(zhi)) {
                zodiacName = zodiac[i];
                break;
            }
        }
        
        switch (zhi) {
            case "子": return "鼠·水·北方。主智慧藏蓄，聪明伶俐。子水为阳水，象征万物之始源，具有开创之力。子时生人思维敏捷，善于谋划。";
            case "丑": return "牛·土·东北。主积蓄稳重，勤劳踏实。丑土为阴土，为金库，主财库积聚。丑时生人性格稳重，值得信赖。";
            case "寅": return "虎·木·东北。主生发进取，勇猛果敢。寅木为阳木，主万物生发，具有开创之力。寅时生人性格积极向上，勇于挑战。";
            case "卯": return "兔·木·东方。主生长繁荣，温和善良。卯木为阴木，主万物生长，生机勃勃。卯时生人性格温和，多才多艺。";
            case "辰": return "龙·土·东南。主变化升腾，神秘威严。辰土为阳土，为水库，主智慧谋略。辰时生人性格聪明，善于变通。";
            case "巳": return "蛇·火·东南。主温暖礼仪，神秘睿智。巳火为阴火，主文化教育。巳时生人性格文雅，富有才华。";
            case "午": return "马·火·南方。主旺盛显达，热情奔放。午火为阳火，主阳气最盛，事业辉煌。午时生人性格积极进取，事业心强。";
            case "未": return "羊·土·西南。主收藏终结，温顺善良。未土为阴土，主收获成果。未时生人性格温和，善于包容。";
            case "申": return "猴·金·西南。主肃杀变革，聪明机智。申金为阳金，主决断行动。申时生人性格聪明，反应敏捷。";
            case "酉": return "鸡·金·西方。主收敛收获，勤劳守信。酉金为阴金，主财富积累。酉时生人性格精明，善于理财。";
            case "戌": return "狗·土·西北。主收藏防备，忠诚可靠。戌土为阳土，主守护家园。戌时生人性格忠诚，责任感强。";
            case "亥": return "猪·水·西北。主流动变化，憨厚朴实。亥水为阴水，主智慧思辨。亥时生人性格聪明，思维敏捷。";
            default: return zhi + "·未知地支";
        }
    }
    
    public static String getYearPillarMeaning(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        StringBuilder sb = new StringBuilder();
        sb.append("年柱代表祖上、家庭背景、早年运势。");
        
        if (ganWuXing.equals(zhiWuXing)) {
            sb.append(" 年柱比和，祖上根基稳固，家庭和睦。");
        } else if (isSheng(zhiWuXing, ganWuXing)) {
            sb.append(" 年支生年干，祖上福荫深厚，得长辈相助。");
        } else if (isSheng(ganWuXing, zhiWuXing)) {
            sb.append(" 年干生年支，自身奋发向上，光耀门楣。");
        } else if (isKe(zhiWuXing, ganWuXing)) {
            sb.append(" 年支克年干，早年压力较大，需靠自己努力。");
        } else if (isKe(ganWuXing, zhiWuXing)) {
            sb.append(" 年干克年支，自身能力强，可驾驭环境。");
        }
        
        return sb.toString();
    }
    
    public static String getMonthPillarMeaning(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        StringBuilder sb = new StringBuilder();
        sb.append("月柱代表事业、学业、中年运势。");
        
        if (ganWuXing.equals(zhiWuXing)) {
            sb.append(" 月柱比和，事业稳定，学业有成。");
        } else if (isSheng(zhiWuXing, ganWuXing)) {
            sb.append(" 月支生月干，事业得助，贵人扶持。");
        } else if (isSheng(ganWuXing, zhiWuXing)) {
            sb.append(" 月干生月支，乐于助人，人脉广。");
        } else if (isKe(zhiWuXing, ganWuXing)) {
            sb.append(" 月支克月干，事业压力大，竞争激烈。");
        } else if (isKe(ganWuXing, zhiWuXing)) {
            sb.append(" 月干克月支，能力出众，可克服困难。");
        }
        
        return sb.toString();
    }
    
    public static String getDayPillarMeaning(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        StringBuilder sb = new StringBuilder();
        sb.append("日柱代表自身、性格、婚姻家庭。");
        
        if (ganWuXing.equals(zhiWuXing)) {
            sb.append(" 日柱比和，性格坚韧，夫妻和睦。");
        } else if (isSheng(zhiWuXing, ganWuXing)) {
            sb.append(" 日支生日干，得配偶相助，家庭幸福。");
        } else if (isSheng(ganWuXing, zhiWuXing)) {
            sb.append(" 日干生日支，关爱家人，付出较多。");
        } else if (isKe(zhiWuXing, ganWuXing)) {
            sb.append(" 日支克日干，配偶强势，需相互包容。");
        } else if (isKe(ganWuXing, zhiWuXing)) {
            sb.append(" 日干克日支，自身主导，掌控家庭。");
        }
        
        return sb.toString();
    }
    
    public static String getTimePillarMeaning(String gan, String zhi) {
        String ganWuXing = getWuXing(gan);
        String zhiWuXing = getWuXing(zhi);
        
        StringBuilder sb = new StringBuilder();
        sb.append("时柱代表晚年运势、子女、部属。");
        
        if (ganWuXing.equals(zhiWuXing)) {
            sb.append(" 时柱比和，晚年安逸，子女孝顺。");
        } else if (isSheng(zhiWuXing, ganWuXing)) {
            sb.append(" 时支生时干，子女得力，晚年享清福。");
        } else if (isSheng(ganWuXing, zhiWuXing)) {
            sb.append(" 时干生时支，关爱子女，付出心血。");
        } else if (isKe(zhiWuXing, ganWuXing)) {
            sb.append(" 时支克时干，晚年操心，子女叛逆。");
        } else if (isKe(ganWuXing, zhiWuXing)) {
            sb.append(" 时干克时支，管教严格，子女成才。");
        }
        
        return sb.toString();
    }
    
    public static String getFourPillarComprehensiveAnalysis(String yearGan, String yearZhi, String monthGan, String monthZhi,
                                                      String dayGan, String dayZhi, String timeGan, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        
        String dayGanWuXing = getWuXing(dayGan);
        String dayZhiWuXing = getWuXing(dayZhi);
        String zodiac = getZodiacNameFromZhi(yearZhi);
        String dayZodiac = getZodiacNameFromZhi(dayZhi);
        String stageDay = getTwelveStage(dayGan, dayZhi);
        
        // 计算十神
        String yGanShen = getTenGodFull(dayGan, yearGan);
        String mGanShen = getTenGodFull(dayGan, monthGan);
        String tGanShen = getTenGodFull(dayGan, timeGan);
        
        // 计算身强身弱
        int shengCount = 0, keCount = 0, biCount = 0;
        String[][] pillars = {{yearGan, yearZhi}, {monthGan, monthZhi}, {dayGan, dayZhi}, {timeGan, timeZhi}};
        for (int i = 0; i < pillars.length; i++) {
            String pGanWuXing = getWuXing(pillars[i][0]);
            if (pGanWuXing.equals(dayGanWuXing)) { if (i != 2) biCount++; }
            else if (isSheng(pGanWuXing, dayGanWuXing)) shengCount++;
            else if (isKe(pGanWuXing, dayGanWuXing)) keCount++;
            else if (isSheng(dayGanWuXing, pGanWuXing)) keCount++;
            else if (isKe(dayGanWuXing, pGanWuXing)) keCount++;
        }
        
        boolean isStrong = (shengCount + biCount > keCount);
        boolean isWeak = (keCount > shengCount + biCount);
        boolean isBalance = (!isStrong && !isWeak);
        
        String shengHao = getShengWuXing(dayGanWuXing);
        String xieHao = ""; // 日主所生
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土"); shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        xieHao = shengMap.get(dayGanWuXing);
        
        // ════════════════════════════
        // 1. 整体性格
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>性格特点</b></font><br/>");
        sb.append(getPersonalityRich(dayGan, dayZhi, dayGanWuXing, isStrong, isWeak, isBalance));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 2. 事业运势
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>事业运势</b></font><br/>");
        sb.append(getCareerAnalysisRich(dayGan, dayGanWuXing, monthGan, monthZhi, mGanShen, isStrong, isWeak));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 3. 财富运势
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>财富运势</b></font><br/>");
        sb.append(getWealthAnalysisRich(dayGan, dayGanWuXing, pillars, isStrong, isWeak));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 4. 感情婚姻
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>感情婚姻</b></font><br/>");
        sb.append(getRelationshipAnalysisRich(dayGan, dayZhi, dayGanWuXing, dayZhiWuXing, dayZodiac, yearZhi, monthZhi, timeZhi));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 5. 健康建议
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>健康建议</b></font><br/>");
        sb.append(getHealthAnalysisRich(dayGanWuXing, shengCount, keCount, biCount));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 6. 人际关系
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>人际关系</b></font><br/>");
        sb.append(getSocialAnalysisRich(dayGan, dayGanWuXing, yGanShen, mGanShen, tGanShen, yearGan, zodiac, isStrong, isWeak, yearZhi, monthZhi, dayZhi, timeZhi));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 7. 人生发展建议
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>人生发展建议</b></font><br/>");
        sb.append(getLifeAdviceRich(dayGan, dayGanWuXing, zodiac, shengHao, xieHao, isStrong, isWeak, isBalance));
        sb.append("<br/><br/>");
        
        sb.append("<font color='#7C8C9C'><i>※ 以上解读基于四柱命理分析，仅供参考。命运掌握在自己手中，积极努力、保持善良才是最好的风水。</i></font>");
        
        return sb.toString();
    }
    
    // ═══ 丰富的通俗解读方法 ═══
    
    public static String getPersonalityRich(String dayGan, String dayZhi, String wuXing, boolean isStrong, boolean isWeak, boolean isBalance) {
        StringBuilder sb = new StringBuilder();
        String zodiac = getZodiacNameFromZhi(dayZhi);
        
        switch (wuXing) {
            case "木":
                sb.append("属<font color='#3FA34D'><b>木</b></font>，生发向上，志在进取。");
                if (isStrong) sb.append("刚毅自持，戒刚愎自用。");
                else if (isWeak) sb.append("善假于物，人缘攸宜。");
                else sb.append("刚柔相济，知权达变。");
                break;
            case "火":
                sb.append("属<font color='#E0593B'><b>火</b></font>，煦然有辉，情采外扬。");
                if (isStrong) sb.append("精力方盛，戒躁妄冲动。");
                else if (isWeak) sb.append("内温外敛，宜显所长。");
                else sb.append("热情有节，悃愊可交。");
                break;
            case "土":
                sb.append("属<font color='#D9A441'><b>土</b></font>，敦厚载物，沉稳可任。");
                if (isStrong) sb.append("淳信笃实，稳步以进。");
                else if (isWeak) sb.append("心善乐施，宜立分际。");
                else sb.append("务实持重，循序而行。");
                break;
            case "金":
                sb.append("属<font color='#9AA7B8'><b>金</b></font>，清刚明决，思虑精审。");
                if (isStrong) sb.append("刚正尚义，藏锋守拙。");
                else if (isWeak) sb.append("精微求粹，当放则放。");
                else sb.append("理性果毅，善断能行。");
                break;
            case "水":
                sb.append("属<font color='#3E87C2'><b>水</b></font>，智用不滞，变通随时。");
                if (isStrong) sb.append("睿识超群，毋过用其虑。");
                else if (isWeak) sb.append("思致缜密，当益其信。");
                else sb.append("聪而不炫，圆融处众。");
                break;
        }
        sb.append("<br/>日支").append(dayZhi).append("：主内，").append(getDayZhiPersonality(dayZhi)).append("。");
        sb.append("<br/><br/>").append(getProsAndCons(wuXing, isStrong, isWeak));
        return sb.toString();
    }
    
    public static String getDayZhiPersonality(String zhi) {
        switch (zhi) {
            case "子": return "温婉重和";
            case "丑": return "笃实专一";
            case "寅": return "独立自信";
            case "卯": return "温润雍和";
            case "辰": return "英爽有担";
            case "巳": return "慧黠体人";
            case "午": return "情热绚烂";
            case "未": return "柔善温良";
            case "申": return "机变通灵";
            case "酉": return "精雅尚品";
            case "戌": return "忠恳可托";
            case "亥": return "宽厚能和";
            default: return "禀性独异";
        }
    }
    
    public static String getCareerAnalysisRich(String dayGan, String wuXing, String monthGan, String monthZhi, String monthShen, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        String monthSeason = getSeasonDescription(monthZhi);
        
        switch (wuXing) {
            case "木": sb.append("天赋：<font color='#3FA34D'><b>规划设计、文教</b></font>。"); break;
            case "火": sb.append("天赋：<font color='#E0593B'><b>创意表达、市场</b></font>。"); break;
            case "土": sb.append("天赋：<font color='#D9A441'><b>财务建筑、地产</b></font>。"); break;
            case "金": sb.append("天赋：<font color='#9AA7B8'><b>金融法律、技术</b></font>。"); break;
            case "水": sb.append("天赋：<font color='#3E87C2'><b>商贸流通、传媒</b></font>。"); break;
        }
        
        if (!monthSeason.isEmpty()) sb.append("月令").append(monthSeason).append("，");
        
        if (isStrong) {
            sb.append("身强宜开拓任事。");
            if (monthShen.equals("正官") || monthShen.equals("七杀")) sb.append("月带官杀，职场得位。");
            if (monthShen.equals("正财") || monthShen.equals("偏财")) sb.append("商才卓然。");
        } else if (isWeak) {
            sb.append("身弱宜择木而栖。");
            if (monthShen.equals("正印") || monthShen.equals("偏印")) sb.append("贵人方隆，宜就前辈请益。");
            if (monthShen.equals("比肩") || monthShen.equals("劫财")) sb.append("合作为上，得同道之助。");
        } else {
            sb.append("中和宜为通才。");
        }
        
        sb.append("<br/>月干十神：<font color='#3FA34D'><b>").append(monthShen).append("</b></font>，主事业指引，").append(getTenGodExplanation(monthShen)).append("。");
        sb.append("<br/><br/>").append(getCareerDirectionDetail(wuXing, monthShen, isStrong, isWeak));
        return sb.toString();
    }
    
    public static String getWealthAnalysisRich(String dayGan, String wuXing, String[][] pillars, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();

        boolean hasZhengCai = false, hasPianCai = false;
        boolean hasBiJian = false, hasJieCai = false;
        for (int i = 0; i < pillars.length; i++) {
            String shen = getTenGodFull(dayGan, pillars[i][0]);
            if (shen.equals("正财")) hasZhengCai = true;
            if (shen.equals("偏财")) hasPianCai = true;
            if (shen.equals("比肩")) hasBiJian = true;
            if (shen.equals("劫财")) hasJieCai = true;
        }

        sb.append("<font color='#7C8C9C'>【财源方位】</font><br/>");
        switch (wuXing) {
            case "木": sb.append("财源：<font color='#3FA34D'><b>土</b></font>（地产·农业）。"); break;
            case "火": sb.append("财源：<font color='#E0593B'><b>金</b></font>（金融·科技）。"); break;
            case "土": sb.append("财源：<font color='#3E87C2'><b>水</b></font>（商贸·物流）。"); break;
            case "金": sb.append("财源：<font color='#3FA34D'><b>木</b></font>（文创·教育）。"); break;
            case "水": sb.append("财源：<font color='#E0593B'><b>火</b></font>（餐饮·能源）。"); break;
        }

        // 财星配置
        sb.append("<br/><br/><font color='#7C8C9C'>【财星配置】</font><br/>");
        if (hasZhengCai && hasPianCai) {
            sb.append("<font color='#3FA34D'><b>正财</b></font>＋<font color='#F3BA66'><b>偏财</b></font>，正业为本，投资为辅。<br/>");
        } else if (hasZhengCai) {
            sb.append("<font color='#3FA34D'><b>正财</b></font>：循序积累，宜长线布局。<br/>");
        } else if (hasPianCai) {
            sb.append("<font color='#F3BA66'><b>偏财</b></font>：善投机，控风险，见好即收。<br/>");
        } else {
            sb.append("<font color='#E0593B'>财星不显</font>：宜以一技立身。<br/>");
        }

        if (hasBiJian || hasJieCai) {
            sb.append("命带<font color='#F3BA66'>比肩/劫财</font>，财恐分夺，");
            if (isStrong) sb.append("身强可任，宜合伙共财。");
            else sb.append("身弱财来复去，宜防借贷之讼。");
            sb.append("<br/>");
        }

        sb.append("<br/><font color='#7C8C9C'>【担财能力】</font><br/>");
        if (isStrong) {
            sb.append("<font color='#3FA34D'><b>身强能担财</b></font>：宜积极开源。<br/>");
        } else if (isWeak) {
            sb.append("<font color='#E0593B'><b>身弱慎财</b></font>：宜稳健守成。<br/>");
        } else {
            sb.append("<font color='#E6C46A'><b>中和担财</b></font>：宜量入为出。<br/>");
        }

        // 求财方位与时机
        sb.append("<br/><font color='#7C8C9C'>【求财方位】</font><br/>");
        switch (wuXing) {
            case "木": sb.append("求财旺方：<font color='#3FA34D'><b>中央·东北·西南</b></font>。"); break;
            case "火": sb.append("求财旺方：<font color='#E0593B'><b>西方·西北</b></font>。"); break;
            case "土": sb.append("求财旺方：<font color='#3E87C2'><b>北方</b></font>。"); break;
            case "金": sb.append("求财旺方：<font color='#3FA34D'><b>东方·东南</b></font>。"); break;
            case "水": sb.append("求财旺方：<font color='#E0593B'><b>南方</b></font>。"); break;
        }
        sb.append("<br/><br/>").append(getWealthStyleDetail(wuXing, isStrong, isWeak));
        return sb.toString();
    }
    
    public static String getRelationshipAnalysisRich(String dayGan, String dayZhi, String dayGanWuXing, String dayZhiWuXing, String dayZodiac,
                                                       String yearZhi, String monthZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        
        String ganZhiRel = getGanZhiRelationship(dayGan, dayZhi);
        
        sb.append("配偶宫").append(dayZhi).append("（属").append(dayZodiac).append("），");
        
        if (ganZhiRel.contains("比和")) {
            sb.append("与伴侣志趣相侔，琴瑟相和，贵在相让。");
        } else if (ganZhiRel.contains("得助") || ganZhiRel.contains("生天干")) {
            sb.append("伴侣乃贵，温恭相扶，宜宝此缘。");
        } else if (ganZhiRel.contains("泄秀") || ganZhiRel.contains("天干生")) {
            sb.append("君多付出，亦当学受，情义均平。");
        } else if (ganZhiRel.contains("制杀") || ganZhiRel.contains("天干克")) {
            sb.append("君主于家，勿忘相敬，婚乃二人共舞。");
        } else if (ganZhiRel.contains("受制") || ganZhiRel.contains("克天干")) {
            sb.append("伴侣性刚，宜多通意相体，相敬乃久。");
        }
        sb.append("<br/>良配：五行相济，性").append(isYangGan(dayGan) ? "阴柔温润" : "刚健爽朗").append("者，彼此成全。");
        sb.append("<br/><br/>").append(getPeachBlossomAnalysis(yearZhi, monthZhi, dayZhi, timeZhi));
        return sb.toString();
    }
    
    public static String getHealthAnalysisRich(String wuXing, int shengCount, int keCount, int biCount) {
        StringBuilder sb = new StringBuilder();

        sb.append("<font color='#7C8C9C'>【脏腑对应】</font><br/>");
        switch (wuXing) {
            case "木": sb.append("属木主<font color='#3FA34D'><b>肝·胆·筋·目</b></font>，情绪易伤肝。<br/>"); break;
            case "火": sb.append("属火主<font color='#E0593B'><b>心·小肠·脉·舌</b></font>，过劳易伤心。<br/>"); break;
            case "土": sb.append("属土主<font color='#D9A441'><b>脾·胃·肉·口</b></font>，饮食不节伤脾。<br/>"); break;
            case "金": sb.append("属金主<font color='#9AA7B8'><b>肺·大肠·皮·鼻</b></font>，悲忧易伤肺。<br/>"); break;
            case "水": sb.append("属水主<font color='#3E87C2'><b>肾·膀胱·骨·耳</b></font>，惊恐过劳伤肾。<br/>"); break;
        }

        // 五行失衡专项预警
        sb.append("<br/><font color='#7C8C9C'>【失衡预警】</font><br/>");
        if (keCount > shengCount + biCount + 1) {
            sb.append("<font color='#E0593B'>克伐偏重</font>，正气逊弱。<br/>");
            switch (wuXing) {
                case "木": sb.append("肝气郁结，宜疏肝理气，戒熬夜动怒。"); break;
                case "火": sb.append("心气不足，宜养心安神，戒过劳亢奋。"); break;
                case "土": sb.append("脾胃虚弱，宜温补健运，戒生冷暴食。"); break;
                case "金": sb.append("肺气虚损，宜补肺固表，戒悲忧伤神。"); break;
                case "水": sb.append("肾元不固，宜温补肾阳，戒惊恐过劳。"); break;
            }
            sb.append("<br/>");
        } else if (shengCount + biCount > keCount + 1) {
            sb.append("<font color='#F3BA66'>生扶偏旺</font>，正气易壅。<br/>");
            switch (wuXing) {
                case "木": sb.append("肝木过亢，宜平肝清热，节酒辛。"); break;
                case "火": sb.append("心火偏亢，宜清心降火，少熬夜。"); break;
                case "土": sb.append("脾土壅滞，宜健脾化湿，节甘腻。"); break;
                case "金": sb.append("肺金过燥，宜润肺生津，多饮啜。"); break;
                case "水": sb.append("肾水泛溢，宜温阳化水，节咸寒。"); break;
            }
            sb.append("<br/>");
        } else {
            sb.append("<font color='#3FA34D'>五行停匀</font>，脏腑协和，宜谨日常之养。<br/>");
        }

        // 养生宜忌
        sb.append("<br/><font color='#7C8C9C'>【养生宜忌】</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("<font color='#3FA34D'>宜：</font>青蔬（菠·芹·猕猴桃）、酸养肝、晨舒、踏青<br/>");
                sb.append("<font color='#E0593B'>忌：</font>酒·怒·夜·久视<br/>");
                sb.append("<font color='#D9A441'>穴：</font>太冲·期门·行间"); break;
            case "火":
                sb.append("<font color='#3FA34D'>宜：</font>红食（枣·茄·豆）、苦清心、午休<br/>");
                sb.append("<font color='#E0593B'>忌：</font>怒·劳·辛·夜<br/>");
                sb.append("<font color='#D9A441'>穴：</font>神门·内关·心俞"); break;
            case "土":
                sb.append("<font color='#3FA34D'>宜：</font>黄食（瓜·米·薯）、甘健脾、定食细嚼<br/>");
                sb.append("<font color='#E0593B'>忌：</font>生冷腻·暴食·久坐<br/>");
                sb.append("<font color='#D9A441'>穴：</font>三里·中脘·脾俞"); break;
            case "金":
                sb.append("<font color='#3FA34D'>宜：</font>白食（耳·合·梨）、辛润肺、深呼吸<br/>");
                sb.append("<font color='#E0593B'>忌：</font>悲·燥·烟·寒吹<br/>");
                sb.append("<font color='#D9A441'>穴：</font>列缺·肺俞·迎香"); break;
            case "水":
                sb.append("<font color='#3FA34D'>宜：</font>黑食（豆·麻·米）、咸入肾、泡脚、太极<br/>");
                sb.append("<font color='#E0593B'>忌：</font>惊·劳·生冷·久立<br/>");
                sb.append("<font color='#D9A441'>穴：</font>涌泉·肾俞·太溪"); break;
        }

        sb.append("<br/><br/>").append(getSeasonHealthDetail(wuXing));
        return sb.toString();
    }
    
    public static String getSocialAnalysisRich(String dayGan, String wuXing, String yShen, String mShen, String tShen, String yearGan, String zodiac, boolean isStrong, boolean isWeak,
                                                 String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();

        sb.append("<font color='#7C8C9C'>【社交特质】</font><br/>");
        switch (wuXing) {
            case "木": sb.append("诚悫重情，持守准绳。<br/>"); break;
            case "火": sb.append("热忱善交，喜自表见。<br/>"); break;
            case "土": sb.append("敦厚守信，沉稳可托。<br/>"); break;
            case "金": sb.append("尚义重然，刚毅能断。<br/>"); break;
            case "水": sb.append("善体人意，圆转随时。<br/>"); break;
        }

        sb.append("<br/><font color='#7C8C9C'>【强弱影响】</font><br/>");
        if (isStrong) {
            sb.append("身强：宜兼听，毋专断。<br/>");
        } else if (isWeak) {
            sb.append("身弱：宜多交良师益友。<br/>");
        } else {
            sb.append("中和：进退有节。<br/>");
        }

        sb.append("<br/><font color='#7C8C9C'>【贵人方位】</font><br/>");
        sb.append(getDirectionAdvice(wuXing)).append("方最旺，广交宜趋此向。<br/>");
        switch (wuXing) {
            case "木": sb.append("合作首选：属<font color='#3FA34D'><b>水</b></font>与属<font color='#3FA34D'><b>木</b></font>之人。"); break;
            case "火": sb.append("合作首选：属<font color='#3FA34D'><b>木</b></font>与属<font color='#3FA34D'><b>火</b></font>之人。"); break;
            case "土": sb.append("合作首选：属<font color='#3FA34D'><b>火</b></font>与属<font color='#3FA34D'><b>土</b></font>之人。"); break;
            case "金": sb.append("合作首选：属<font color='#3FA34D'><b>土</b></font>与属<font color='#3FA34D'><b>金</b></font>之人。"); break;
            case "水": sb.append("合作首选：属<font color='#3FA34D'><b>金</b></font>与属<font color='#3FA34D'><b>水</b></font>之人。"); break;
        }

        sb.append("<br/>属").append(zodiac).append("，与三合局之人契合。");
        sb.append("<br/><br/>").append(getTianYiGuiRenAnalysis(dayGan, yearZhi, monthZhi, dayZhi, timeZhi));
        return sb.toString();
    }
    
    public static String getLifeAdviceRich(String dayGan, String wuXing, String zodiac, String shengHao, String xieHao, boolean isStrong, boolean isWeak, boolean isBalance) {
        StringBuilder sb = new StringBuilder();
        
        if (isStrong) {
            sb.append("<font color='#3FA34D'><b>身强气盛</b></font>，宜立定趋向。<br/>");
            sb.append("宜投").append(xieHao).append("属之业，刚柔相济。");
        } else if (isWeak) {
            sb.append("<font color='#E0593B'><b>身弱需扶</b></font>，宜多结贵人。<br/>");
            sb.append(shengHao).append("属之人与事，足为凭依。");
        } else {
            sb.append("<font color='#D9A441'><b>五行中和</b></font>，动静咸宜。<br/>");
            sb.append("顺时而行。");
        }
        
        String[] zodiacCompat = getZodiacCompat(zodiac);
        sb.append("<br/>属").append(zodiac).append("，与").append(zodiacCompat[0]).append("、").append(zodiacCompat[1]).append("、").append(zodiacCompat[2]).append("最契。");
        sb.append(getDirectionAdvice(wuXing)).append("方最旺。");
        sb.append("<br/><br/>").append(getKaiYunAdvice(wuXing, zodiac));
        return sb.toString();
    }
    
    public static String[] getZodiacCompat(String zodiac) {
        java.util.Map<String, String[]> compat = new java.util.HashMap<>();
        compat.put("鼠", new String[]{"牛","龙","猴"});
        compat.put("牛", new String[]{"鼠","蛇","鸡"});
        compat.put("虎", new String[]{"猪","马","狗"});
        compat.put("兔", new String[]{"狗","猪","羊"});
        compat.put("龙", new String[]{"鸡","鼠","猴"});
        compat.put("蛇", new String[]{"猴","鸡","牛"});
        compat.put("马", new String[]{"羊","虎","狗"});
        compat.put("羊", new String[]{"马","兔","猪"});
        compat.put("猴", new String[]{"蛇","鼠","龙"});
        compat.put("鸡", new String[]{"龙","蛇","牛"});
        compat.put("狗", new String[]{"兔","虎","马"});
        compat.put("猪", new String[]{"虎","兔","羊"});
        return compat.getOrDefault(zodiac, new String[]{"—","—","—"});
    }
    
    public static String getDirectionAdvice(String wuXing) {
        switch (wuXing) {
            case "木": return "东方";
            case "火": return "南方";
            case "土": return "中央";
            case "金": return "西方";
            case "水": return "北方";
            default: return "中央";
        }
    }
    
    public static String getOppositeWuXing(String wuxing) {
        switch (wuxing) {
            case "木": return "金";
            case "火": return "水";
            case "土": return "木";
            case "金": return "火";
            case "水": return "土";
            default: return "土";
        }
    }
    
    public static String getKeWuXing(String wuxing) {
        switch (wuxing) {
            case "木": return "土";
            case "火": return "金";
            case "土": return "水";
            case "金": return "木";
            case "水": return "火";
            default: return "土";
        }
    }
    
    public static String getShengWuXing(String wuxing) {
        switch (wuxing) {
            case "木": return "水";
            case "火": return "木";
            case "土": return "火";
            case "金": return "土";
            case "水": return "金";
            default: return "土";
        }
    }
    
    public static String getQiMenSummary(String yearPillar, String monthPillar, String dayPillar, String timePillar, String zhiFuStar, String zhiShiDoor) {
        String dayGan = dayPillar.substring(0, 1);
        String dayGanWuXing = getWuXing(dayGan);
        
        String[] luckyStars = {"天蓬", "天任", "天冲", "天辅", "天英", "天芮", "天柱", "天心", "天禽"};
        boolean isLucky = false;
        for (String ls : luckyStars) {
            if (ls.equals(zhiFuStar)) {
                isLucky = true;
                break;
            }
        }
        
        String[] luckyDoors = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
        boolean doorLucky = false;
        for (String ld : luckyDoors) {
            if (ld.equals(zhiShiDoor)) {
                doorLucky = true;
                break;
            }
        }
        
        String result = dayGanWuXing + "日" + dayGan + "，";
        result += isLucky ? "值符" + zhiFuStar + "星，吉。" : "值符" + zhiFuStar + "星，平。";
        result += doorLucky ? "值使" + zhiShiDoor + "门，利" : "值使" + zhiShiDoor + "门，平";
        return result;
    }

    // ═══════════════════════════════════
    // 新增：地支藏干
    // ═══════════════════════════════════
    private static final java.util.Map<String, String[]> HIDDEN_STEMS = new java.util.HashMap<>();
    static {
        HIDDEN_STEMS.put("子", new String[]{"癸"});
        HIDDEN_STEMS.put("丑", new String[]{"己", "癸", "辛"});
        HIDDEN_STEMS.put("寅", new String[]{"甲", "丙", "戊"});
        HIDDEN_STEMS.put("卯", new String[]{"乙"});
        HIDDEN_STEMS.put("辰", new String[]{"乙", "戊", "癸"});
        HIDDEN_STEMS.put("巳", new String[]{"丙", "戊", "庚"});
        HIDDEN_STEMS.put("午", new String[]{"丁", "己"});
        HIDDEN_STEMS.put("未", new String[]{"己", "丁", "乙"});
        HIDDEN_STEMS.put("申", new String[]{"庚", "戊", "壬"});
        HIDDEN_STEMS.put("酉", new String[]{"辛"});
        HIDDEN_STEMS.put("戌", new String[]{"戊", "辛", "丁"});
        HIDDEN_STEMS.put("亥", new String[]{"壬", "甲"});
    }

    private static final java.util.Map<String, String> HIDDEN_ROLE = new java.util.HashMap<>();
    static {
        HIDDEN_ROLE.put("子_癸", "本气");
        HIDDEN_ROLE.put("丑_己", "本气"); HIDDEN_ROLE.put("丑_癸", "中气"); HIDDEN_ROLE.put("丑_辛", "余气");
        HIDDEN_ROLE.put("寅_甲", "本气"); HIDDEN_ROLE.put("寅_丙", "中气"); HIDDEN_ROLE.put("寅_戊", "余气");
        HIDDEN_ROLE.put("卯_乙", "本气");
        HIDDEN_ROLE.put("辰_乙", "本气"); HIDDEN_ROLE.put("辰_戊", "中气"); HIDDEN_ROLE.put("辰_癸", "余气");
        HIDDEN_ROLE.put("巳_丙", "本气"); HIDDEN_ROLE.put("巳_戊", "中气"); HIDDEN_ROLE.put("巳_庚", "余气");
        HIDDEN_ROLE.put("午_丁", "本气"); HIDDEN_ROLE.put("午_己", "中气");
        HIDDEN_ROLE.put("未_己", "本气"); HIDDEN_ROLE.put("未_丁", "中气"); HIDDEN_ROLE.put("未_乙", "余气");
        HIDDEN_ROLE.put("申_庚", "本气"); HIDDEN_ROLE.put("申_戊", "中气"); HIDDEN_ROLE.put("申_壬", "余气");
        HIDDEN_ROLE.put("酉_辛", "本气");
        HIDDEN_ROLE.put("戌_戊", "本气"); HIDDEN_ROLE.put("戌_辛", "中气"); HIDDEN_ROLE.put("戌_丁", "余气");
        HIDDEN_ROLE.put("亥_壬", "本气"); HIDDEN_ROLE.put("亥_甲", "中气");
    }

    public static String[] getHiddenStems(String zhi) {
        String[] hs = HIDDEN_STEMS.get(zhi);
        return hs != null ? hs : new String[]{};
    }

    public static String getHiddenStemRole(String zhi, String stem) {
        String r = HIDDEN_ROLE.get(zhi + "_" + stem);
        return r != null ? r : "";
    }

    public static String getHiddenStemsRich(String dayGan, String yearGan, String monthGan, String timeGan,
                                             String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        String[] labels = {"年支", "月支", "日支", "时支"};
        String[] barColors = {"#E0593B", "#3FA34D", "#D9A441", "#3E87C2"};

        for (int i = 0; i < 4; i++) {
            String zhi = zhis[i];
            String label = labels[i];
            String color = barColors[i];
            String[] stems = getHiddenStems(zhi);
            sb.append("<font color='").append(color).append("'><b>").append(label).append(zhi).append("</b></font>：");
            for (int j = 0; j < stems.length; j++) {
                String stem = stems[j];
                String role = getHiddenStemRole(zhi, stem);
                String tenGod = getTenGodFull(dayGan, stem);
                String wuXing = getWuXing(stem);
                sb.append(stem).append(wuXing).append("(").append(role).append("·").append(tenGod).append(")");
                if (j < stems.length - 1) sb.append("、");
            }
            sb.append("<br/>");
        }
        sb.append("<br/><font color='#7C8C9C'><i>※ 本气为地支主气，中气余气为暗藏力量，藏干十神代表隐性特质</i></font>");
        sb.append("<br/><br/>").append(getHiddenStemTransparency(dayGan, yearGan, monthGan, timeGan, yearZhi, monthZhi, dayZhi, timeZhi));
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：命局格局
    // ═══════════════════════════════════
    private static String getMonthMainQi(String monthZhi) {
        String[] stems = getHiddenStems(monthZhi);
        return stems.length > 0 ? stems[0] : monthZhi;
    }

    public static String getPatternAnalysis(String dayGan, String monthZhi, String yearGan, String monthGan, String timeGan, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        String mainQi = getMonthMainQi(monthZhi);
        String patternShen = getTenGodFull(dayGan, mainQi);
        String patternName;

        switch (patternShen) {
            case "正官": patternName = "正官格"; break;
            case "七杀": patternName = "七杀格"; break;
            case "正财": patternName = "正财格"; break;
            case "偏财": patternName = "偏财格"; break;
            case "正印": patternName = "正印格"; break;
            case "偏印": patternName = "偏印格"; break;
            case "食神": patternName = "食神格"; break;
            case "伤官": patternName = "伤官格"; break;
            case "比肩": case "劫财": patternName = "建禄格"; break;
            default: patternName = "普通格";
        }

        sb.append("月令").append(monthZhi).append("藏<font color='#3FA34D'><b>").append(mainQi).append("</b></font>，取为<font color='#D9A441'><b>").append(patternName).append("</b></font>。<br/>");

        // 格局解释
        switch (patternShen) {
            case "正官":
                sb.append("正官格主贵气名声、重纪律。宜守法从政或入职大型机构，官星有力则仕途亨通。");
                if (isStrong && containsTenGod(new String[]{yearGan, monthGan, timeGan}, dayGan, "正印"))
                    sb.append("命带<font color='#3FA34D'><b>官印相生</b></font>，贵格也，德才兼备。");
                if (isWeak) sb.append("身弱官旺则压力较大，需印星化解。");
                break;
            case "七杀":
                sb.append("七杀格主权威果断、善执行。宜军警管理创业；杀有制则化权，无制多波折。");
                if (containsTenGod(new String[]{yearGan, monthGan, timeGan}, dayGan, "食神"))
                    sb.append("命带<font color='#3FA34D'><b>食神制杀</b></font>，以智勇取胜，成就非凡。");
                if (isWeak) sb.append("身弱遇杀则挑战重重，需印化杀或食制杀。");
                break;
            case "正财":
                sb.append("正财格主稳定财源、勤劳致富。宜金融、财务、实业经营，守正出奇则财运亨通。");
                if (isStrong) sb.append("身强能担财，财富积累可期。");
                if (isWeak) sb.append("身弱财旺反为财累，需谨慎理财。");
                break;
            case "偏财":
                sb.append("偏财格主意外之财、善投资。商业嗅觉灵敏，宜经商投资贸易，然需防冒进。");
                if (isStrong) sb.append("身强善用偏财，有富翁潜质，但要见好就收。");
                if (isWeak) sb.append("身弱不宜投机，应以稳健为主，见财莫贪。");
                break;
            case "正印":
                sb.append("正印格主学识、贵人、仁慈。宜教育、文化、学术研究，印星有力则学问深厚。");
                if (isStrong) sb.append("印旺身强则学问广博，有师者风范。");
                if (isWeak) sb.append("印绶护身，贵人运佳，读书进学是最佳途径。");
                break;
            case "偏印":
                sb.append("偏印格主特殊才能、偏门学问。思维独特，宜科研、玄学、技术专精，不走寻常路。");
                if (containsTenGod(new String[]{yearGan, monthGan, timeGan}, dayGan, "食神"))
                    sb.append("有<font color='#E0593B'>枭神夺食</font>之象，需注意心胸开阔，避免孤僻。");
                break;
            case "食神":
                sb.append("食神格主才华福气、尚享乐。多才多艺，宜艺术餐饮文创；心态乐观，福泽深厚。");
                if (isStrong) sb.append("食神泄秀，才华横溢，是天然的艺术家。");
                if (isWeak) sb.append("食神泄身太过则精力不足，需节制享乐。");
                break;
            case "伤官":
                sb.append("伤官格主聪明不羁、善创造。才华外露，宜创新行业，然锋芒易招是非。");
                if (containsTenGod(new String[]{yearGan, monthGan, timeGan}, dayGan, "正印"))
                    sb.append("命带<font color='#3FA34D'><b>伤官佩印</b></font>，才华与智慧并存，极为优秀。");
                if (isWeak) sb.append("伤官泄身过重，需养精蓄锐，莫贪多求快。");
                break;
            case "比肩": case "劫财":
                sb.append("建禄格主自强独立、重实干。靠自身打拼，宜自主创业或技术专精。");
                if (isStrong) sb.append("比劫林立，竞争意识强，适合需要拼搏的领域。防刚愎自用。");
                if (isWeak) sb.append("得月令之助，弱中转旺，合作共赢是最佳策略。");
                break;
            default:
                sb.append("格局清正，气场平和，宜顺势而为，发挥自身特长。");
        }

        // 是否破格提示
        sb.append("<br/><font color='#7C8C9C'>格局评语：</font>");
        if (isStrong && (patternShen.equals("正官") || patternShen.equals("七杀")))
            sb.append("身强官杀旺，威权在手之象，格局有力。");
        else if (isWeak && (patternShen.equals("正印") || patternShen.equals("偏印")))
            sb.append("印绶护身，弱中有靠，格局有情可原。");
        else if (isStrong && (patternShen.equals("食神") || patternShen.equals("伤官")))
            sb.append("身强食伤泄秀，才华得展，格局流通。");
        else
            sb.append("格局自成体系，顺其自然即可发挥最大优势。");

        sb.append("<br/><br/>").append(getYongShenHint(getWuXing(dayGan), monthZhi, isStrong, isWeak));
        return sb.toString();
    }

    private static boolean containsTenGod(String[] gans, String dayGan, String targetShen) {
        for (String gan : gans) {
            if (getTenGodFull(dayGan, gan).equals(targetShen)) return true;
        }
        return false;
    }

    // ═══════════════════════════════════
    // 新增：六亲分析
    // ═══════════════════════════════════
    public static String getSixRelativesRich(String dayGan, String yearGan, String yearZhi, String monthGan, String monthZhi, String dayZhi, String timeGan, String timeZhi) {
        StringBuilder sb = new StringBuilder();

        // 年柱 → 祖上/父母
        String yShen = getTenGodFull(dayGan, yearGan);
        String yZhiMainQi = getHiddenStems(yearZhi).length > 0 ? getHiddenStems(yearZhi)[0] : yearZhi;
        String yZhiShen = getTenGodFull(dayGan, yZhiMainQi);

        sb.append("<font color='#E0593B'><b>祖上 · 年柱</b></font> ").append(yearGan).append(yearZhi).append("<br/>");
        sb.append("年干").append(yearGan).append("为").append(yShen).append("：").append(getSixRelDesc(yShen, "祖上")).append("<br/>");
        sb.append("年支藏").append(yZhiMainQi).append("(").append(yZhiShen).append(")：").append(getSixRelDesc(yZhiShen, "祖荫")).append("<br/><br/>");

        // 月柱 → 父母/兄弟
        String mShen = getTenGodFull(dayGan, monthGan);
        String mZhiMainQi = getHiddenStems(monthZhi).length > 0 ? getHiddenStems(monthZhi)[0] : monthZhi;
        String mZhiShen = getTenGodFull(dayGan, mZhiMainQi);

        sb.append("<font color='#3FA34D'><b>父母 · 月柱</b></font> ").append(monthGan).append(monthZhi).append("<br/>");
        sb.append("月干").append(monthGan).append("为").append(mShen).append("：").append(getSixRelDesc(mShen, "父母")).append("<br/>");
        sb.append("月支藏").append(mZhiMainQi).append("(").append(mZhiShen).append(")：").append(getSixRelDesc(mZhiShen, "手足")).append("<br/><br/>");

        // 日支 → 配偶
        String dZhiMainQi = getHiddenStems(dayZhi).length > 0 ? getHiddenStems(dayZhi)[0] : dayZhi;
        String dZhiShen = getTenGodFull(dayGan, dZhiMainQi);
        String dayZodiac = getZodiacNameFromZhi(dayZhi);

        sb.append("<font color='#D9A441'><b>配偶 · 日支</b></font> ").append(dayZhi).append("<br/>");
        sb.append("配偶宫").append(dayZhi).append("（属").append(dayZodiac).append("）藏").append(dZhiMainQi).append("(").append(dZhiShen).append(")：").append(getSixRelDesc(dZhiShen, "配偶")).append("<br/>");
        sb.append(getSpouseDetail(dayZhi, dZhiShen)).append("<br/><br/>");

        // 时柱 → 子女
        String tShen = getTenGodFull(dayGan, timeGan);
        String tZhiMainQi = getHiddenStems(timeZhi).length > 0 ? getHiddenStems(timeZhi)[0] : timeZhi;
        String tZhiShen = getTenGodFull(dayGan, tZhiMainQi);

        sb.append("<font color='#3E87C2'><b>子女 · 时柱</b></font> ").append(timeGan).append(timeZhi).append("<br/>");
        sb.append("时干").append(timeGan).append("为").append(tShen).append("：").append(getSixRelDesc(tShen, "子女")).append("<br/>");
        sb.append("时支藏").append(tZhiMainQi).append("(").append(tZhiShen).append(")：").append(getSixRelDesc(tZhiShen, "晚辈")).append("<br/>");

        return sb.toString();
    }

    private static String getSixRelDesc(String tenGod, String role) {
        switch (tenGod) {
            case "正印": return role + "缘深，得长辈疼爱和贵人相助";
            case "偏印": return "与" + role + "缘分特殊，有独特的影响";
            case "比肩": return role + "助力强，关系平等互助";
            case "劫财": return role + "竞争与帮扶并存，关系复杂";
            case "食神": return role + "关系融洽，有温暖互动";
            case "伤官": return role + "关系中有独立精神，需理解包容";
            case "正财": return role + "关系稳定务实，有责任感";
            case "偏财": return role + "缘浅或特殊，有意外之互动";
            case "正官": return role + "有规矩有约束，受尊重或规范";
            case "七杀": return role + "关系中有压力和挑战，需耐心周旋";
            default: return role + "关系一般";
        }
    }

    private static String getSpouseDetail(String dayZhi, String dZhiShen) {
        switch (dZhiShen) {
            case "正官": return "配偶品行端正，有社会地位，是良配。";
            case "七杀": return "配偶性格强势有魄力，事业心强，但需多沟通。";
            case "正财": return "配偶务实可靠，持家有道，是生活好帮手。";
            case "偏财": return "配偶大方慷慨，善于理财，给生活带来惊喜。";
            case "正印": return "配偶温柔体贴，善解人意，家庭温暖。";
            case "偏印": return "配偶有独特个性，思想独立，需互相适应。";
            case "食神": return "配偶乐观开朗，生活有情趣，婚姻幸福。";
            case "伤官": return "配偶才华横溢但个性鲜明，需包容理解。";
            case "比肩": return "配偶与己志趣相投，如同志同道合之友。";
            case "劫财": return "配偶个性强势，夫妻间有竞争意识，宜以和为贵。";
            default: return "配偶关系需双方共同经营。";
        }
    }

    // ═══════════════════════════════════
    // 新增：合冲刑害分析
    // ═══════════════════════════════════
    public static String getBranchRelationsRich(String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        String[] labels = {"年支", "月支", "日支", "时支"};
        boolean hasRel = false;

        // 六合：子丑、寅亥、卯戌、辰酉、巳申、午未
        String[][] liuHe = {{"子","丑"},{"丑","子"},{"寅","亥"},{"亥","寅"},{"卯","戌"},{"戌","卯"},{"辰","酉"},{"酉","辰"},{"巳","申"},{"申","巳"},{"午","未"},{"未","午"}};

        // 六冲：子午、丑未、寅申、卯酉、辰戌、巳亥
        String[][] liuChong = {{"子","午"},{"午","子"},{"丑","未"},{"未","丑"},{"寅","申"},{"申","寅"},{"卯","酉"},{"酉","卯"},{"辰","戌"},{"戌","辰"},{"巳","亥"},{"亥","巳"}};

        // 三合局检测
        boolean hasYinWuXu = hasZhis(zhis, new String[]{"寅","午","戌"});
        boolean hasSiYouChou = hasZhis(zhis, new String[]{"巳","酉","丑"});
        boolean hasShenZiChen = hasZhis(zhis, new String[]{"申","子","辰"});
        boolean hasHaiMaoWei = hasZhis(zhis, new String[]{"亥","卯","未"});

        if (hasYinWuXu) { sb.append("<font color='#E0593B'><b>寅午戌三合火局</b></font> — 火气极旺，热情奔放，事业心强<br/>"); hasRel = true; }
        if (hasSiYouChou) { sb.append("<font color='#9AA7B8'><b>巳酉丑三合金局</b></font> — 金气凝聚，果断刚毅，财运佳<br/>"); hasRel = true; }
        if (hasShenZiChen) { sb.append("<font color='#3E87C2'><b>申子辰三合水局</b></font> — 水气流通，智慧过人，善变通<br/>"); hasRel = true; }
        if (hasHaiMaoWei) { sb.append("<font color='#3FA34D'><b>亥卯未三合木局</b></font> — 木气生发，仁慈宽厚，创造力强<br/>"); hasRel = true; }

        if (hasRel) sb.append("<br/>");

        // 六合与六冲
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                String z1 = zhis[i], z2 = zhis[j];

                // 六合
                for (String[] lh : liuHe) {
                    if (lh[0].equals(z1) && lh[1].equals(z2)) {
                        sb.append("").append(labels[i]).append(z1).append("与").append(labels[j]).append(z2).append("<font color='#3FA34D'><b>六合</b></font>");
                        sb.append(" — 关系和谐，互相吸引，有天然默契<br/>");
                        hasRel = true;
                    }
                }
                // 六冲
                for (String[] lc : liuChong) {
                    if (lc[0].equals(z1) && lc[1].equals(z2)) {
                        sb.append("").append(labels[i]).append(z1).append("与").append(labels[j]).append(z2).append("<font color='#E0593B'><b>六冲</b></font>");
                        sb.append(" — 对冲激荡，变化多端，宜以柔克刚<br/>");
                        hasRel = true;
                    }
                }
            }
        }

        // 自刑
        for (int i = 0; i < 4; i++) {
            String z = zhis[i];
            if (z.equals("辰") || z.equals("午") || z.equals("酉") || z.equals("亥")) {
                for (int j = i + 1; j < 4; j++) {
                    if (zhis[j].equals(z)) {
                        sb.append("").append(labels[i]).append("与").append(labels[j]).append("同为").append(z).append("，<font color='#F3BA66'><b>自刑</b></font> — 内心纠结，自我矛盾，需豁达<br/>");
                        hasRel = true;
                    }
                }
            }
        }

        if (!hasRel) {
            sb.append("<font color='#7C8C9C'>四支之间无明显的合冲刑害关系，气场独立平和。各柱各有轨迹，互不干扰，反而利于独立发展。</font>");
        }

        sb.append("<br/><font color='#7C8C9C'><i>※ 合则融洽助力，冲则动荡变化，刑则纠结烦恼。知晓关系，便能趋吉避凶</i></font>");
        sb.append("<br/><br/>").append(getHalfCombineAnalysis(yearZhi, monthZhi, dayZhi, timeZhi));
        sb.append("<br/>").append(getXiangHaiAnalysis(yearZhi, monthZhi, dayZhi, timeZhi));
        return sb.toString();
    }

    private static boolean hasZhis(String[] pillars, String[] needed) {
        int count = 0;
        for (String n : needed) {
            for (String p : pillars) {
                if (p.equals(n)) { count++; break; }
            }
        }
        return count >= 3;
    }

    // ═══════════════════════════════════
    // 新增：空亡分析
    // ═══════════════════════════════════
    private static final String[] XUN_LIST = {
        "甲子","甲戌","甲申","甲午","甲辰","甲寅"
    };
    private static final String[][] XUN_ZHI = {
        {"子","丑","寅","卯","辰","巳","午","未","申","酉"},    // 空戌亥
        {"戌","亥","子","丑","寅","卯","辰","巳","午","未"},    // 空申酉
        {"申","酉","戌","亥","子","丑","寅","卯","辰","巳"},    // 空午未
        {"午","未","申","酉","戌","亥","子","丑","寅","卯"},    // 空辰巳
        {"辰","巳","午","未","申","酉","戌","亥","子","丑"},    // 空寅卯
        {"寅","卯","辰","巳","午","未","申","酉","戌","亥"},    // 空子丑
    };
    private static final String[] VOID_RESULT = {
        "戌亥空", "申酉空", "午未空", "辰巳空", "寅卯空", "子丑空"
    };

    public static String getVoidAnalysisRich(String dayGan, String dayZhi, String yearZhi, String monthZhi, String dayZhiColumn, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        String dayPillar = dayGan + dayZhi;

        // 找出日柱所在的旬
        int xunIdx = -1;
        for (int i = 0; i < XUN_LIST.length; i++) {
            for (String z : XUN_ZHI[i]) {
                String xunPillar = XUN_LIST[i].charAt(0) + z;
                if (xunPillar.equals(dayPillar)) {
                    xunIdx = i;
                    break;
                }
            }
            if (xunIdx >= 0) break;
        }

        if (xunIdx >= 0) {
            String voidBranches = VOID_RESULT[xunIdx];
            String[] voidZhis = voidBranches.replace("空","").split("");
            String v1 = voidZhis[0], v2 = voidZhis[1];

            sb.append("日柱").append(dayPillar).append("属<font color='#3FA34D'>甲").append(XUN_LIST[xunIdx].substring(1)).append("旬</font>，");
            sb.append("空亡在<font color='#E0593B'>").append(v1).append("、").append(v2).append("</font>。<br/><br/>");

            // 检查各柱是否落空亡
            String[] zhis = {yearZhi, monthZhi, dayZhiColumn, timeZhi};
            String[] labels = {"年支", "月支", "日支", "时支"};
            String[] descs = {"祖上助力减弱，需自我奋斗", "父母缘分略薄，独立性强", "配偶助力减弱，婚姻需多经营", "子女缘分特殊，晚来得力"};
            boolean anyVoid = false;

            for (int i = 0; i < 4; i++) {
                if (zhis[i].equals(v1) || zhis[i].equals(v2)) {
                    sb.append("<font color='#F3BA66'>").append(labels[i]).append(zhis[i]).append("落空亡</font>：").append(descs[i]).append("<br/>");
                    anyVoid = true;
                }
            }

            if (!anyVoid) {
                sb.append("<font color='#3FA34D'>四柱地支均未落空亡</font>，命局根基稳固，六亲缘分正常。<br/>");
            }

            sb.append("<br/><font color='#7C8C9C'>空亡之解：</font>空亡并非坏事，表示该方面较为淡薄或独特。");
            sb.append("落空亡之柱若逢填实（大运流年遇之）则可应事，平时以平常心待之即可。");
        } else {
            sb.append("空亡推算需结合日柱与旬首，建议参考专业命理师判断。");
        }

        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：纳音详解
    // ═══════════════════════════════════
    public static String getNayinRich(String gan, String zhi) {
        String nayin = getNayin(gan, zhi);
        String expl = getNayinExplanation(nayin);
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#D9A441'><b>").append(gan).append(zhi).append(" · ").append(nayin).append("</b></font>");
        if (!expl.isEmpty()) sb.append(" — ").append(expl);
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：命宫推算
    // ═══════════════════════════════════
    public static String getMingGong(String yearGan, String monthZhi, String timeZhi) {
        String[] zhiArr = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
        int monthIdx = -1, timeIdx = -1;
        for (int i = 0; i < 12; i++) {
            if (zhiArr[i].equals(monthZhi)) monthIdx = i;
            if (zhiArr[i].equals(timeZhi)) timeIdx = i;
        }
        if (monthIdx < 0 || timeIdx < 0) return "未知";
        // 命宫地支 = (14 - monthIdx + timeIdx) % 12
        int mgIdx = (14 - monthIdx + timeIdx) % 12;
        String mgZhi = zhiArr[mgIdx];
        // 命宫天干用五虎遁（年干起月法）
        String[] ganArr = {"甲","乙","丙","丁","戊","己","庚","辛","壬","癸"};
        int ygIdx = -1;
        for (int i = 0; i < 10; i++) { if (ganArr[i].equals(yearGan)) { ygIdx = i; break; } }
        // 五虎遁：甲己起丙寅、乙庚起戊寅、丙辛起庚寅、丁壬起壬寅、戊癸起甲寅
        int startGan;
        if (ygIdx == 0 || ygIdx == 5) startGan = 2;      // 甲己→丙
        else if (ygIdx == 1 || ygIdx == 6) startGan = 4;  // 乙庚→戊
        else if (ygIdx == 2 || ygIdx == 7) startGan = 6;  // 丙辛→庚
        else if (ygIdx == 3 || ygIdx == 8) startGan = 8;  // 丁壬→壬
        else startGan = 0;                                  // 戊癸→甲
        int mgGanIdx = (startGan + mgIdx) % 10;
        String mgGan = ganArr[mgGanIdx];
        String mingGong = mgGan + mgZhi;
        StringBuilder sb = new StringBuilder();
        sb.append("命宫 <font color='#D9A441'><b>").append(mingGong).append("</b></font>，");
        sb.append("主后天运势倾向，代表一生福禄根基。");
        // 命宫五行
        String mgWx = getWuXing(mgGan);
        sb.append("命宫属").append(mgWx).append("，");
        java.util.Map<String, String> mgDesc = new java.util.HashMap<>();
        mgDesc.put("木", "活力充沛，开创性强");
        mgDesc.put("火", "热情积极，名声显达");
        mgDesc.put("土", "稳重踏实，根基深厚");
        mgDesc.put("金", "刚毅果断，财运亨通");
        mgDesc.put("水", "智慧灵动，变通力强");
        sb.append(mgDesc.getOrDefault(mgWx, "运势平顺"));
        sb.append("。");
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：十神组合吉凶
    // ═══════════════════════════════════
    public static String getTenGodComboAnalysis(String dayGan, String yearGan, String monthGan, String timeGan) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#7C8C9C'>十神组合：</font>");
        String yShen = getTenGodFull(dayGan, yearGan);
        String mShen = getTenGodFull(dayGan, monthGan);
        String tShen = getTenGodFull(dayGan, timeGan);
        String[] allShens = {yShen, mShen, tShen};
        boolean hasZhengGuan = false, hasQiSha = false, hasZhengYin = false, hasPianYin = false;
        boolean hasShiShen = false, hasShangGuan = false, hasZhengCai = false, hasPianCai = false;
        boolean hasBiJian = false, hasJieCai = false;
        for (String s : allShens) {
            switch (s) {
                case "正官": hasZhengGuan = true; break;
                case "七杀": hasQiSha = true; break;
                case "正印": hasZhengYin = true; break;
                case "偏印": hasPianYin = true; break;
                case "食神": hasShiShen = true; break;
                case "伤官": hasShangGuan = true; break;
                case "正财": hasZhengCai = true; break;
                case "偏财": hasPianCai = true; break;
                case "比肩": hasBiJian = true; break;
                case "劫财": hasJieCai = true; break;
            }
        }
        java.util.List<String> combos = new java.util.ArrayList<>();
        if (hasZhengGuan && (hasZhengYin || hasPianYin))
            combos.add("<font color='#3FA34D'><b>官印相生</b></font>（贵气流通，德才兼备）");
        if (hasQiSha && hasShiShen)
            combos.add("<font color='#3FA34D'><b>食神制杀</b></font>（以智取胜，化险为夷）");
        if (hasShangGuan && hasZhengYin)
            combos.add("<font color='#3FA34D'><b>伤官佩印</b></font>（才华与智慧并重）");
        if (hasShiShen && (hasZhengCai || hasPianCai))
            combos.add("<font color='#3FA34D'><b>食神生财</b></font>（才华变现，财源滚滚）");
        if (hasPianYin && hasShiShen)
            combos.add("<font color='#F3BA66'><b>枭神夺食</b></font>（思维独特，需防偏执）");
        if (hasZhengGuan && hasQiSha)
            combos.add("<font color='#F3BA66'><b>官杀混杂</b></font>（机遇与压力并存）");
        if (hasShangGuan && hasZhengGuan)
            combos.add("<font color='#E0593B'><b>伤官见官</b></font>（锋芒毕露，宜低调行事）");
        if (hasJieCai && hasZhengCai)
            combos.add("<font color='#F3BA66'><b>劫财夺财</b></font>（理财需谨慎，防破耗）");
        if (combos.isEmpty()) {
            sb.append("十神清正，无特殊组合，各安其位。");
        } else {
            sb.append(String.join(" · ", combos));
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：五行补益建议
    // ═══════════════════════════════════
    public static String getWuxingSupplementRich(String wuXing, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#7C8C9C'>五行补益：</font>");
        String targetWx;
        if (isStrong) {
            targetWx = getXieHaoWuXing(wuXing); // 泄秀
        } else if (isWeak) {
            targetWx = getShengWuXing(wuXing); // 生扶
        } else {
            targetWx = wuXing;
        }
        switch (targetWx) {
            case "木":
                sb.append("宜<font color='#3FA34D'><b>青绿</b></font>衣饰、东方绿植、绿色蔬果（菠菜芹菜绿豆），春季运势旺。"); break;
            case "火":
                sb.append("宜<font color='#E0593B'><b>红紫</b></font>衣饰、南方暖光、红色食物（红枣番茄红豆），夏季运势旺。"); break;
            case "土":
                sb.append("宜<font color='#D9A441'><b>黄棕</b></font>衣饰、中央稳守、黄色谷物（小米玉米南瓜），四季末旺。"); break;
            case "金":
                sb.append("宜<font color='#9AA7B8'><b>白金</b></font>衣饰、西方金属、白色食物（萝卜银耳百合），秋季运势旺。"); break;
            case "水":
                sb.append("宜<font color='#3E87C2'><b>黑蓝</b></font>衣饰、北方水景、黑色食物（黑豆芝麻海带），冬季运势旺。"); break;
        }
        return sb.toString();
    }

    private static String getXieHaoWuXing(String wx) {
        switch (wx) { case "木": return "火"; case "火": return "土"; case "土": return "金"; case "金": return "水"; case "水": return "木"; default: return "土"; }
    }

    // ═══════════════════════════════════
    // 新增：性格优缺点
    // ═══════════════════════════════════
    public static String getProsAndCons(String wuXing, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#7C8C9C'>个性详解：</font><br/>");
        switch (wuXing) {
            case "木":
                if (isStrong) {
                    sb.append("<font color='#3FA34D'>长：</font>仁厚刚直，有帅领才<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>执于己见，不纳善言");
                } else if (isWeak) {
                    sb.append("<font color='#3FA34D'>长：</font>柔韧随俗，善假于物<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>乏定见，易为人移");
                } else {
                    sb.append("<font color='#3FA34D'>长：</font>仁而有节，刚柔相济<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>失之平，鲜所表见");
                }
                break;
            case "火":
                if (isStrong) {
                    sb.append("<font color='#3FA34D'>长：</font>煦然有为，有感召姿<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>躁妄轻发，难竟其绪");
                } else if (isWeak) {
                    sb.append("<font color='#3FA34D'>长：</font>温润体人，善听能助<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>自信不足，才不克显");
                } else {
                    sb.append("<font color='#3FA34D'>长：</font>情采有度，温而不灼<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>敛抑过甚，真趣不畅");
                }
                break;
            case "土":
                if (isStrong) {
                    sb.append("<font color='#3FA34D'>长：</font>淳信可任，履实行<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>执而不通，应变稍迟");
                } else if (isWeak) {
                    sb.append("<font color='#3FA34D'>长：</font>敦善乐施，不较得丧<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>易见役于物，当立分际");
                } else {
                    sb.append("<font color='#3FA34D'>长：</font>稳而不滞，可任解趣<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>过于求安，坐失时会");
                }
                break;
            case "金":
                if (isStrong) {
                    sb.append("<font color='#3FA34D'>长：</font>刚毅重诺，断制如流<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>锋太露易折，当藏锋");
                } else if (isWeak) {
                    sb.append("<font color='#3FA34D'>长：</font>精微尚粹，品藻自高<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>过于案牍，求备自困");
                } else {
                    sb.append("<font color='#3FA34D'>长：</font>理情兼至，刚柔得中<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>失之刻，未免寡温");
                }
                break;
            case "水":
                if (isStrong) {
                    sb.append("<font color='#3FA34D'>长：</font>睿识善变，常得捷径<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>虑深难断，失之迟回");
                } else if (isWeak) {
                    sb.append("<font color='#3FA34D'>长：</font>直觉敏妙，洞察幽微<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>中无所主，情易摇荡");
                } else {
                    sb.append("<font color='#3FA34D'>长：</font>聪而不炫，明而能晦<br/>");
                    sb.append("<font color='#E0593B'>戒：</font>韬晦过甚，才见见掩");
                }
                break;
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：事业方向细化
    // ═══════════════════════════════════
    public static String getCareerDirectionDetail(String wuXing, String monthShen, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#7C8C9C'>发展指南：</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("宜：教育、文化、出版、园林、医药、环保、设计<br/>");
                isStrong = false; // dummy
                break;
            case "火":
                sb.append("宜：传媒、演艺、餐饮、能源、互联网、美业、公关<br/>");
                break;
            case "土":
                sb.append("宜：建筑、地产、金融、仓储、农业、顾问、行政<br/>");
                break;
            case "金":
                sb.append("宜：法律、金融、科技、机械、管理、审计、珠宝<br/>");
                break;
            case "水":
                sb.append("宜：商贸、物流、旅游、传媒、咨询、水产、信息<br/>");
                break;
        }
        if (isStrong) {
            sb.append("<font color='#3FA34D'>宜创业或秉钧之任</font>，能独任一面，宜择有为之基。");
        } else if (isWeak) {
            sb.append("<font color='#3E87C2'>宜专业或协作之职</font>，以一技之长取胜，良队胜良台。");
        } else {
            sb.append("<font color='#D9A441'>仕途之宜广</font>，创与就俱可，要在择其所好。");
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：理财风格
    // ═══════════════════════════════════
    public static String getWealthStyleDetail(String wuXing, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#7C8C9C'>理财建议：</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("理财之方：尚经略，重远势，不贪速获。<br/>");
                if (isStrong) sb.append("宜：股权、生植之业、教化之投。");
                else sb.append("宜：定投、储保、稳收之财。");
                break;
            case "火":
                sb.append("理财之方：任直觉，乘时而下注。<br/>");
                if (isStrong) sb.append("宜：短线、新兴之业、品牌之盟。");
                else sb.append("宜：分买毋追高，设止损之限。");
                break;
            case "土":
                sb.append("理财之方：持重守成，好实产，不信浮利。<br/>");
                if (isStrong) sb.append("宜：田宅、黄白、长债、实业。");
                else sb.append("宜：定储、逆回、固收之属。");
                break;
            case "金":
                sb.append("理财之方：精于综理，风控为先。<br/>");
                if (isStrong) sb.append("宜：衍生、量策、金类之业。");
                else sb.append("宜：分散、货基、短期之财。");
                break;
            case "水":
                sb.append("理财之方：变通灵敏，善察套利。<br/>");
                if (isStrong) sb.append("宜：跨海贸、漕运、信息之创。");
                else sb.append("宜：流动性强之财，务可随时变现。");
                break;
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：桃花星分析
    // ═══════════════════════════════════
    public static String getPeachBlossomAnalysis(String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#7C8C9C'>桃花运：</font>");
        // 子午卯酉为四正桃花
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        String[] labels = {"年", "月", "日", "时"};
        java.util.List<String> peachList = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String z = zhis[i];
            if (z.equals("子") || z.equals("午") || z.equals("卯") || z.equals("酉")) {
                String peachType = "";
                switch (z) {
                    case "子": peachType = "水桃花·灵动机敏"; break;
                    case "午": peachType = "火桃花·朗曜热忱"; break;
                    case "卯": peachType = "木桃花·温雅含章"; break;
                    case "酉": peachType = "金桃花·精丽可玩"; break;
                }
                peachList.add(labels[i] + "支" + z + "（" + peachType + "）");
            }
        }
        if (peachList.isEmpty()) {
            sb.append("命局无子午卯酉四正桃花，情致内敛，桃花虽不显而情实挚。");
        } else {
            sb.append("命带桃花星：").append(String.join("；", peachList));
            sb.append("。<br/>桃花较盛，异性缘佳，然当辨良缘与泛桃之异。");
            // 红鸾天喜简易提示
            String redLuan = getRedLuan(yearZhi);
            if (!redLuan.isEmpty()) sb.append("<br/>红鸾星在").append(redLuan).append("，主正缘婚期，值之则姻好可成。");
        }
        return sb.toString();
    }

    private static String getRedLuan(String yearZhi) {
        // 红鸾星：子→卯 丑→寅 寅→丑 卯→子 辰→亥 巳→戌 午→酉 未→申 申→未 酉→午 戌→巳 亥→辰
        String[] luanMap = {"子","卯","丑","寅","寅","丑","卯","子","辰","亥","巳","戌","午","酉","未","申","申","未","酉","午","戌","巳","亥","辰"};
        String[] zhiArr = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
        for (int i = 0; i < 12; i++) {
            if (zhiArr[i].equals(yearZhi)) return luanMap[i * 2] + luanMap[i * 2 + 1];
        }
        return "";
    }

    // ═══════════════════════════════════
    // 新增：天乙贵人
    // ═══════════════════════════════════
    public static String getTianYiGuiRenAnalysis(String dayGan, String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        String[] guiRenZhis = new String[0];
        switch (dayGan) {
            case "甲": case "戊": case "庚": guiRenZhis = new String[]{"丑","未"}; break;
            case "乙": case "己": guiRenZhis = new String[]{"子","申"}; break;
            case "丙": case "丁": guiRenZhis = new String[]{"亥","酉"}; break;
            case "壬": case "癸": guiRenZhis = new String[]{"卯","巳"}; break;
            case "辛": guiRenZhis = new String[]{"午","寅"}; break;
        }
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        String[] labels = {"年支", "月支", "日支", "时支"};
        java.util.List<String> found = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (String gr : guiRenZhis) {
                if (zhis[i].equals(gr)) found.add(labels[i] + zhis[i]);
            }
        }
        sb.append("<font color='#7C8C9C'>天乙贵人：</font>");
        if (found.isEmpty()) {
            sb.append("日主").append(dayGan).append("之天乙贵人为<font color='#D9A441'>").append(guiRenZhis[0]).append("、").append(guiRenZhis[1]).append("</font>，");
            sb.append("命局未遇，贵人多于流年大运显。宜多交属").append(getZodiacNameFromZhi(guiRenZhis[0])).append("、").append(getZodiacNameFromZhi(guiRenZhis[1])).append("者，可得匡助。");
        } else {
            sb.append("命带<font color='#3FA34D'><b>天乙贵人</b></font>（").append(String.join("、", found)).append("），临难有扶持，逢凶化吉。");
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：藏干透出分析
    // ═══════════════════════════════════
    public static String getHiddenStemTransparency(String dayGan, String yearGan, String monthGan, String timeGan,
                                                    String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        String[] ganArr = {yearGan, monthGan, dayGan, timeGan};
        String[] zhiArr = {yearZhi, monthZhi, dayZhi, timeZhi};
        String[] labels = {"年", "月", "日", "时"};
        sb.append("<font color='#7C8C9C'>透干分析：</font>");
        boolean anyTransparent = false;
        for (int i = 0; i < 4; i++) {
            String zhi = zhiArr[i];
            String[] hidden = getHiddenStems(zhi);
            for (int j = 0; j < 4; j++) {
                String gan = ganArr[j];
                for (String hs : hidden) {
                    if (hs.equals(gan) && i != j) {
                        if (!anyTransparent) sb.append("<br/>");
                        anyTransparent = true;
                        String role = getHiddenStemRole(zhi, hs);
                        String tenGod = getTenGodFull(dayGan, hs);
                        sb.append(labels[i]).append("支").append(zhi).append("藏").append(hs).append("（").append(role).append("·").append(tenGod).append("）");
                        sb.append("<font color='#3FA34D'><b>透于").append(labels[j]).append("干</b></font>").append(gan).append(" — 藏干发力，隐性特质外显为实际行动<br/>");
                    }
                }
            }
        }
        if (!anyTransparent) {
            sb.append("四柱藏干未直透天干，其潜蓄之力，待大运流年引动乃显。");
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：用神提示
    // ═══════════════════════════════════
    public static String getYongShenHint(String wuXing, String monthZhi, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#7C8C9C'>用神提示：</font>");
        if (isStrong) {
            String xie = getXieHaoWuXing(wuXing);
            String ke = getKeWuXing(wuXing);
            sb.append("身强宜<font color='#3FA34D'><b>泄</b></font>（").append(wuXing).append("生").append(xie).append("）或<font color='#3FA34D'><b>克</b></font>（").append(ke).append("克").append(wuXing).append("），");
            sb.append("用神取").append(xie).append("、").append(ke).append("。大运逢之则顺遂无碍。");
        } else if (isWeak) {
            String sheng = getShengWuXing(wuXing);
            sb.append("身弱宜<font color='#3FA34D'><b>生</b></font>（").append(sheng).append("生").append(wuXing).append("）或<font color='#3FA34D'><b>扶</b></font>（").append(wuXing).append("帮").append(wuXing).append("），");
            sb.append("用神取").append(sheng).append("、").append(wuXing).append("。大运逢之则得贵人相济。");
        } else {
            sb.append("命局中和，<font color='#D9A441'>顺势而为</font>即是用神，毋必补某一行。");
        }
        // 月令提示
        String monthWuXing = getWuXing(monthZhi);
        sb.append("<br/>月令属").append(monthWuXing).append("，");
        if (monthWuXing.equals(wuXing)) sb.append("得月令之气，禀赋深厚。");
        else if (isSheng(monthWuXing, wuXing)) sb.append("月令生扶日主，得时得令。");
        else if (isSheng(wuXing, monthWuXing)) sb.append("日主生月令，泄气宜补。");
        else sb.append("月令与日主各有所损益。");
        sb.append("<br/><font color='#7C8C9C'><small>用神者，调候扶抑之枢也</small></font>");
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：六害（相害）分析
    // ═══════════════════════════════════
    public static String getXiangHaiAnalysis(String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        String[][] xiangHai = {{"子","未"},{"未","子"},{"丑","午"},{"午","丑"},{"寅","巳"},{"巳","寅"},
                                {"卯","辰"},{"辰","卯"},{"申","亥"},{"亥","申"},{"酉","戌"},{"戌","酉"}};
        String[][] haiDesc = {{"子","未","子未相害·六亲缘疏，宜通情笃好"},
                              {"丑","午","丑午相害·性情相牾，宜相忍相涵"},
                              {"寅","巳","寅巳相害·暗相角力，宜防小人"},
                              {"卯","辰","卯辰相害·亲故失和，宜加宽厚"},
                              {"申","亥","申亥相害·情不通达，宜开诚相见"},
                              {"酉","戌","酉戌相害·多生口舌，宜慎言饬行"}};
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        String[] labels = {"年支", "月支", "日支", "时支"};
        boolean hasHai = false;
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                for (String[] hd : haiDesc) {
                    if ((zhis[i].equals(hd[0]) && zhis[j].equals(hd[1])) ||
                        (zhis[i].equals(hd[1]) && zhis[j].equals(hd[0]))) {
                        if (!hasHai) sb.append("<font color='#7C8C9C'>相害关系：</font>");
                        hasHai = true;
                        sb.append("<br/>").append(labels[i]).append(zhis[i]).append("与").append(labels[j]).append(zhis[j]).append("：");
                        sb.append("<font color='#F3BA66'>").append(hd[2]).append("</font>");
                    }
                }
            }
        }
        if (!hasHai) sb.append("<font color='#7C8C9C'>四支无相害</font>，人伦之际尚和。");
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：半合局/拱局分析
    // ═══════════════════════════════════
    public static String getHalfCombineAnalysis(String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        String[][][] halfHe = {
            {{"寅","午","寅午半合火局·火气渐旺"}}, {{"午","戌","午戌半合火局·火库待开"}},
            {{"巳","酉","巳酉半合金局·金气凝练"}}, {{"酉","丑","酉丑半合金局·金库藏锋"}},
            {{"申","子","申子半合水局·水势初成"}}, {{"子","辰","子辰半合水局·水库待启"}},
            {{"亥","卯","亥卯半合木局·木气萌发"}}, {{"卯","未","卯未半合木局·木库充盈"}}
        };
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        String[] labels = {"年支", "月支", "日支", "时支"};
        boolean hasHalf = false;
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                for (String[][] hh : halfHe) {
                    if ((zhis[i].equals(hh[0][0]) && zhis[j].equals(hh[0][1])) ||
                        (zhis[i].equals(hh[0][1]) && zhis[j].equals(hh[0][0]))) {
                        if (!hasHalf) sb.append("<font color='#7C8C9C'>半合/拱局：</font>");
                        hasHalf = true;
                        sb.append("<br/>").append(labels[i]).append(zhis[i]).append("与").append(labels[j]).append(zhis[j]);
                        sb.append(" <font color='#3E87C2'>").append(hh[0][2]).append("</font>");
                    }
                }
            }
        }
        if (!hasHalf) sb.append("<font color='#7C8C9C'>四支无半合</font>，气局自完。");
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：开运方法
    // ═══════════════════════════════════
    public static String getKaiYunAdvice(String wuXing, String zodiac) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#7C8C9C'>开运锦囊：</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("幸运色<font color='#3FA34D'>青·绿</font>｜数3·8｜绿松石/翡翠/木质｜旺春月<br/>"); break;
            case "火":
                sb.append("幸运色<font color='#E0593B'>红·紫</font>｜数2·7｜红玛瑙/紫晶/红宝｜旺夏月<br/>"); break;
            case "土":
                sb.append("幸运色<font color='#D9A441'>黄·棕</font>｜数5·0｜黄晶/蜜蜡/陶瓷｜旺四季末月<br/>"); break;
            case "金":
                sb.append("幸运色<font color='#9AA7B8'>白·银</font>｜数4·9｜白晶/银饰/白金｜旺秋月<br/>"); break;
            case "水":
                sb.append("幸运色<font color='#3E87C2'>黑·蓝</font>｜数1·6｜黑曜石/海蓝宝/黑晶｜旺冬月<br/>"); break;
        }
        sb.append("🌅 每日宜：收视返听，顺天时。<br/>");
        String[] compat = getZodiacCompat(zodiac);
        sb.append("贵人属相：").append(compat[0]).append("、").append(compat[1]).append("、").append(compat[2]);
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：四季养生
    // ═══════════════════════════════════
    public static String getSeasonHealthDetail(String wuXing) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#7C8C9C'>四季养生要点：</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("🌸 <font color='#3FA34D'>春：</font>养肝利胆，早卧早起，多伸舒以达木气<br/>");
                sb.append("☀ 夏：防木火升亢，戒怒节酒，宜菊茶饮之<br/>");
                sb.append("秋：金克乎木，宜护气道<br/>");
                sb.append("❄ 冬：藏精养木，早卧晚起，毋熬夜");
                break;
            case "火":
                sb.append("🌸 春：木生火，宜动以散其华<br/>");
                sb.append("☀ <font color='#E0593B'>夏：</font>养心安神，午憩为要，戒暴怒过劳<br/>");
                sb.append("秋：火气敛藏，宜调其情<br/>");
                sb.append("❄ 冬：水克火，宜温护心脑血脉");
                break;
            case "土":
                sb.append("🌸 春：木克土，宜调脾胃<br/>");
                sb.append("☀ 夏：火生土，化力强，然贵有常节<br/>");
                sb.append("<font color='#D9A441'>秋：</font>土生金而泄，宜补益养胃<br/>");
                sb.append("❄ 冬：脾胃为后天之本，四时皆宜温养");
                break;
            case "金":
                sb.append("🌸 春：金克木劳神，宜节其息<br/>");
                sb.append("☀ 夏：火克金，宜防上呼吸道之疾<br/>");
                sb.append("<font color='#9AA7B8'>秋：</font>润肺生津，多啖白物，深吸缓吐<br/>");
                sb.append("❄ 冬：金生水而泄，宜温裳防寒");
                break;
            case "水":
                sb.append("🌸 春：水生木泄气，宜稍补<br/>");
                sb.append("☀ 夏：水克火耗神，宜饮水节劳<br/>");
                sb.append("秋：金生水得助，正进补之良时<br/>");
                sb.append("❄ <font color='#3E87C2'>冬：</font>固肾培元，护腰膝，宜食玄色之物");
                break;
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：各柱富文本详情（含十神+藏干）
    // ═══════════════════════════════════
    public static String getPillarRichDetail(String dayGan, String gan, String zhi, String pillarLabel) {
        StringBuilder sb = new StringBuilder();
        String tenGod = getTenGodFull(dayGan, gan);
        String[] hidden = getHiddenStems(zhi);
        String zhiMainQi = hidden.length > 0 ? hidden[0] : zhi;
        String zhiShen = getTenGodFull(dayGan, zhiMainQi);
        sb.append(gan).append(zhi).append("｜干十神：<font color='#3FA34D'><b>").append(tenGod).append("</b></font>");
        sb.append("｜支藏：");
        for (int i = 0; i < hidden.length; i++) {
            String hTen = getTenGodFull(dayGan, hidden[i]);
            String role = getHiddenStemRole(zhi, hidden[i]);
            sb.append(hidden[i]).append("(").append(role).append("·").append(hTen).append(")");
            if (i < hidden.length - 1) sb.append("、");
        }
        sb.append("｜支主气十神：").append(zhiShen);
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：命局综合总评
    // ═══════════════════════════════════
    public static String getLevelAssessmentRich(String dayGan, String dayGanWuXing, String monthZhi, int shengCount, int keCount, int biCount,
                                                 String yearGan, String monthGan, String timeGan,
                                                 String yearZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        int totalSupport = shengCount + biCount;

        // 打分
        int score = 50;
        if (totalSupport > keCount + 2) score += 15;
        else if (totalSupport > keCount) score += 5;
        else if (keCount > totalSupport + 2) score -= 10;
        else score += 0;

        // 月令得令
        String monthWuXing = getWuXing(monthZhi);
        String mainQiWuXing = getWuXing(getMonthMainQi(monthZhi));
        if (monthWuXing.equals(dayGanWuXing) || mainQiWuXing.equals(dayGanWuXing)) score += 10;

        // 十神配置
        String mShen = getTenGodFull(dayGan, monthGan);
        if (mShen.equals("正官") || mShen.equals("正印") || mShen.equals("正财")) score += 5;
        if (mShen.equals("食神")) score += 3;

        // 三合局加分
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        if (hasZhis(zhis, new String[]{"寅","午","戌"}) || hasZhis(zhis, new String[]{"巳","酉","丑"})
         || hasZhis(zhis, new String[]{"申","子","辰"}) || hasZhis(zhis, new String[]{"亥","卯","未"})) score += 8;

        // 天干十神完整性
        String[] gans = {yearGan, monthGan, dayGan, timeGan};
        boolean hasWealth = false, hasOfficer = false, hasSeal = false, hasFood = false;
        for (String g : gans) {
            String s = getTenGodFull(dayGan, g);
            if (s.equals("正财") || s.equals("偏财")) hasWealth = true;
            if (s.equals("正官") || s.equals("七杀")) hasOfficer = true;
            if (s.equals("正印") || s.equals("偏印")) hasSeal = true;
            if (s.equals("食神") || s.equals("伤官")) hasFood = true;
        }
        int completeness = (hasWealth ? 1 : 0) + (hasOfficer ? 1 : 0) + (hasSeal ? 1 : 0) + (hasFood ? 1 : 0);
        score += completeness * 3;

        // 评级
        String level, levelColor, levelDesc;
        if (score >= 75) {
            level = "上等";
            levelColor = "#D9A441";
            levelDesc = "命局醇美，格局清峻。宝其天禀，乘时赴之。";
        } else if (score >= 60) {
            level = "中上";
            levelColor = "#3FA34D";
            levelDesc = "命局良善，所长昭然。扬长避短，循序可进。";
        } else if (score >= 45) {
            level = "中等";
            levelColor = "#3E87C2";
            levelDesc = "命局中平，长短相兼。勤学补拙，所趋得方。";
        } else if (score >= 30) {
            level = "中下";
            levelColor = "#F3BA66";
            levelDesc = "命局微有未足。宜假外助（贵人、问学），避重就轻。";
        } else {
            level = "下等";
            levelColor = "#E0593B";
            levelDesc = "命局偏弱，非命之蹇。逆境玉汝，后学可越。";
        }

        sb.append("<font color='").append(levelColor).append("'><b>").append(level).append(" · 综合得分 ").append(score).append("/100</b></font><br/><br/>");

        // 评分明细
        sb.append("<font color='#7C8C9C'>评分明细：</font><br/>");
        sb.append("五行力量 ").append(totalSupport > keCount ? "均衡" : "偏颇").append(" · ");
        sb.append("月令 ").append(monthWuXing.equals(dayGanWuXing) ? "得令+" : "一般").append(" · ");
        sb.append("十神 ").append(completeness >= 3 ? "齐全" : completeness >= 2 ? "较全" : "偏少").append(" · ");
        sb.append("合冲 ").append(hasZhis(zhis, new String[]{"寅","午","戌"}) || hasZhis(zhis, new String[]{"巳","酉","丑"}) ? "有三合" : "无三合");
        sb.append("<br/><br/>");

        sb.append("<font color='#D9A441'><b>总评：</b></font>").append(levelDesc);

        return sb.toString();
    }

    // ═══════════════════════════════════
    // 日期转四柱干支 + 当日宜忌
    // ═══════════════════════════════════

    private static final String[] TIANGAN_ARR = {"甲","乙","丙","丁","戊","己","庚","辛","壬","癸"};
    private static final String[] DIZHI_ARR = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
    private static final String[] LIUJIAZI_ARR = {
        "甲子","乙丑","丙寅","丁卯","戊辰","己巳","庚午","辛未","壬申","癸酉",
        "甲戌","乙亥","丙子","丁丑","戊寅","己卯","庚辰","辛巳","壬午","癸未",
        "甲申","乙酉","丙戌","丁亥","戊子","己丑","庚寅","辛卯","壬辰","癸巳",
        "甲午","乙未","丙申","丁酉","戊戌","己亥","庚子","辛丑","壬寅","癸卯",
        "甲辰","乙巳","丙午","丁未","戊申","己酉","庚戌","辛亥","壬子","癸丑",
        "甲寅","乙卯","丙辰","丁巳","戊午","己未","庚申","辛酉","壬戌","癸亥"
    };

    // 计算年柱（立春为界，这里按公历年份近似，以春节后当年干支计）
    public static String getYearPillar(int year) {
        int baseYear = 1900;
        int baseIndex = 36; // 庚子
        int yearDiff = year - baseYear;
        int idx = (baseIndex + yearDiff) % 60;
        if (idx < 0) idx += 60;
        return LIUJIAZI_ARR[idx];
    }

    // 计算月柱（按节气月，简化版：以公历月近似，用五虎遁）
    public static String getMonthPillar(int year, int month, int day) {
        String yearGan = getYearPillar(year).substring(0, 1);
        String monthZhi = getMonthZhiSimple(month, day);

        // 五虎遁
        Map<String, String> wuHuDun = new HashMap<>();
        wuHuDun.put("甲", "丙"); wuHuDun.put("己", "丙");
        wuHuDun.put("乙", "戊"); wuHuDun.put("庚", "戊");
        wuHuDun.put("丙", "庚"); wuHuDun.put("辛", "庚");
        wuHuDun.put("丁", "壬"); wuHuDun.put("壬", "壬");
        wuHuDun.put("戊", "甲"); wuHuDun.put("癸", "甲");

        String yinGan = wuHuDun.get(yearGan);
        if (yinGan == null) yinGan = "丙";

        int yinIdx = indexOf(TIANGAN_ARR, yinGan);
        int zhiIdx = indexOf(DIZHI_ARR, monthZhi);
        int ganIdx = (yinIdx + zhiIdx) % 10;
        return TIANGAN_ARR[ganIdx] + monthZhi;
    }

    private static String getMonthZhiSimple(int month, int day) {
        if (month == 1) return day < 6 ? "子" : "丑";
        if (month == 2) return day < 4 ? "丑" : "寅";
        Map<Integer, String> map = new HashMap<>();
        map.put(3, "卯"); map.put(4, "辰"); map.put(5, "巳");
        map.put(6, "午"); map.put(7, "未"); map.put(8, "申");
        map.put(9, "酉"); map.put(10, "戌"); map.put(11, "亥"); map.put(12, "子");
        return map.get(month);
    }

    // 计算日柱（1900年1月1日为甲戌日，索引10）
    // 采用整数儒略日算法求两日期之间的纯日历天数差，避免毫秒差在夏令时切换日
    // （1986-1991 中国曾实行夏时制）产生 ±1 天的误差，确保日柱准确。
    public static String getDayPillar(int year, int month, int day) {
        try {
            int daysDiff = julianDay(year, month, day) - julianDay(1900, 1, 1);
            int idx = (10 + daysDiff) % 60;
            if (idx < 0) idx += 60;
            return LIUJIAZI_ARR[idx];
        } catch (Exception e) {
            return "甲午";
        }
    }

    // 儒略日数（整数部分，正午为基准，此处取当日整数即可用于求差）
    private static int julianDay(int y, int m, int d) {
        if (m <= 2) { y -= 1; m += 12; }
        int a = y / 100;
        int b = 2 - a + a / 4;
        return (int) (365.25 * (y + 4716)) + (int) (30.6001 * (m + 1)) + d + b - 1524;
    }

    public static String getTimePillar(int hour, int minute, String dayGan) {
        String timeZhi;
        if (hour >= 23 || hour < 1) timeZhi = "子";
        else if (hour >= 1 && hour < 3) timeZhi = "丑";
        else if (hour >= 3 && hour < 5) timeZhi = "寅";
        else if (hour >= 5 && hour < 7) timeZhi = "卯";
        else if (hour >= 7 && hour < 9) timeZhi = "辰";
        else if (hour >= 9 && hour < 11) timeZhi = "巳";
        else if (hour >= 11 && hour < 13) timeZhi = "午";
        else if (hour >= 13 && hour < 15) timeZhi = "未";
        else if (hour >= 15 && hour < 17) timeZhi = "申";
        else if (hour >= 17 && hour < 19) timeZhi = "酉";
        else if (hour >= 19 && hour < 21) timeZhi = "戌";
        else timeZhi = "亥";

        Map<String, String> wuShuDun = new HashMap<>();
        wuShuDun.put("甲", "甲"); wuShuDun.put("己", "甲");
        wuShuDun.put("乙", "丙"); wuShuDun.put("庚", "丙");
        wuShuDun.put("丙", "戊"); wuShuDun.put("辛", "戊");
        wuShuDun.put("丁", "庚"); wuShuDun.put("壬", "庚");
        wuShuDun.put("戊", "壬"); wuShuDun.put("癸", "壬");

        String ziGan = wuShuDun.get(dayGan);
        if (ziGan == null) ziGan = "甲";
        int ziIdx = indexOf(TIANGAN_ARR, ziGan);
        int zhiIdx = indexOf(DIZHI_ARR, timeZhi);
        int ganIdx = (ziIdx + zhiIdx) % 10;
        return TIANGAN_ARR[ganIdx] + timeZhi;
    }

    private static int indexOf(String[] arr, String key) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(key)) return i;
        }
        return 0;
    }

    // 十二建除（以日支定建除）
    private static final String[] JIANCHU = {"建","除","满","平","定","执","破","危","成","收","开","闭"};

    public static String getJianChu(String dayZhi, String monthZhi) {
        int monthIdx = indexOf(DIZHI_ARR, monthZhi);
        int dayIdx = indexOf(DIZHI_ARR, dayZhi);
        int idx = (dayIdx - monthIdx + 12) % 12;
        return JIANCHU[idx];
    }

    // 当日宜忌（基于日干支、十二建除、黄历常用宜忌）
    public static String getDailyYiJi(int year, int month, int day) {
        StringBuilder sb = new StringBuilder();

        String dayPillar = getDayPillar(year, month, day);
        String dayGan = dayPillar.substring(0, 1);
        String dayZhi = dayPillar.substring(1, 2);
        String monthPillar = getMonthPillar(year, month, day);
        String monthZhi = monthPillar.substring(1, 2);
        String jianchu = getJianChu(dayZhi, monthZhi);
        String dayNaYin = getNayin(dayGan, dayZhi);
        String dayWuXing = getWuXing(dayGan);

        sb.append("<font color='#D9A441'><b>").append(year).append("年").append(month).append("月").append(day).append("日</b></font>");
        sb.append("　<font color='#3FA34D'><b>").append(dayPillar).append("日</b></font>");
        sb.append(" · ").append(dayNaYin);
        sb.append(" · 建除「<font color='#D9A441'><b>").append(jianchu).append("</b></font>」");
        sb.append("<br/>");

        // 宜
        sb.append("<br/><font color='#3FA34D'><b>宜：</b></font>");
        sb.append(getYiList(jianchu, dayZhi, dayGan, dayWuXing));
        sb.append("<br/>");

        // 忌
        sb.append("<br/><font color='#E0593B'><b>忌：</b></font>");
        sb.append(getJiList(jianchu, dayZhi, dayGan));
        sb.append("<br/>");

        // 通俗解读
        sb.append("<br/><font color='#7C8C9C'>");
        sb.append("「").append(jianchu).append("」日解读：").append(getJianChuExplanation(jianchu));
        sb.append("</font>");

        return sb.toString();
    }

    private static String getYiList(String jianchu, String dayZhi, String dayGan, String dayWuXing) {
        StringBuilder yi = new StringBuilder();
        switch (jianchu) {
            case "建": yi.append("出行、上任、签约、动土、求财、祈福"); break;
            case "除": yi.append("除服、疗病、解除、扫舍、求医、整容"); break;
            case "满": yi.append("祈福、进财、开光、开市、嫁娶、立券"); break;
            case "平": yi.append("修造、安床、交易、平治道涂、出行"); break;
            case "定": yi.append("祭祀、祈福、嫁娶、安床、置业、签约"); break;
            case "执": yi.append("捕捉、狩猎、修造、栽种、开市、纳财"); break;
            case "破": yi.append("求医、破屋坏垣、拆除、求医治病"); break;
            case "危": yi.append("祭祀、安床、祈福、登高、出行"); break;
            case "成": yi.append("嫁娶、开市、立券、入学、求职、出行"); break;
            case "收": yi.append("进财、纳畜、收藏、收官、嫁娶、祈福"); break;
            case "开": yi.append("开工、开业、出行、求学、搬家、动土"); break;
            case "闭": yi.append("安葬、筑堤、收藏、闭关、修仓"); break;
        }

        // 日支附加宜
        if (dayZhi.equals("子") || dayZhi.equals("午")) yi.append("、祈福");
        if (dayZhi.equals("卯") || dayZhi.equals("酉")) yi.append("、祭祀");
        if (dayZhi.equals("寅") || dayZhi.equals("申")) yi.append("、出行");
        if (dayZhi.equals("辰") || dayZhi.equals("戌")) yi.append("、修造");
        if (dayZhi.equals("巳") || dayZhi.equals("亥")) yi.append("、求谋");

        // 日干附加
        if (dayGan.equals("甲") || dayGan.equals("乙")) yi.append("、栽种");
        if (dayGan.equals("丙") || dayGan.equals("丁")) yi.append("、会友");
        if (dayGan.equals("戊") || dayGan.equals("己")) yi.append("、置业");
        if (dayGan.equals("庚") || dayGan.equals("辛")) yi.append("、交易");
        if (dayGan.equals("壬") || dayGan.equals("癸")) yi.append("、出游");

        return yi.toString();
    }

    private static String getJiList(String jianchu, String dayZhi, String dayGan) {
        StringBuilder ji = new StringBuilder();
        switch (jianchu) {
            case "建": ji.append("安葬、破土、大事不宜妄动"); break;
            case "除": ji.append("嫁娶、开市、出行、远行"); break;
            case "满": ji.append("服药、求医、打官司、下葬"); break;
            case "平": ji.append("诉讼、争执、动土、搬家"); break;
            case "定": ji.append("诉讼、出行、搬迁、求医"); break;
            case "执": ji.append("出行、搬迁、开市、投资"); break;
            case "破": ji.append("嫁娶、开市、出行、动土、祈福"); break;
            case "危": ji.append("登高、远行、冒险、投资"); break;
            case "成": ji.append("诉讼、打官司、词讼"); break;
            case "收": ji.append("出行、搬迁、动土、开仓"); break;
            case "开": ji.append("安葬、关闭、收藏、诉讼"); break;
            case "闭": ji.append("开市、出行、开工、嫁娶、动土"); break;
        }

        // 日支附加忌
        if (dayZhi.equals("子")) ji.append("、属马之人忌大事");
        if (dayZhi.equals("午")) ji.append("、属鼠之人忌大事");
        if (dayZhi.equals("卯")) ji.append("、属鸡之人忌大事");
        if (dayZhi.equals("酉")) ji.append("、属兔之人忌大事");

        return ji.toString();
    }

    private static String getJianChuExplanation(String jianchu) {
        switch (jianchu) {
            case "建": return "建日为一月之始，如月初建寅，万物生发。宜开创进取，但气势初起，忌过度扩张，稳扎稳打为上。";
            case "除": return "除日为除旧布新之日，如扫庭除秽。宜清理整顿、解决旧问题，但忌开启新局，先除旧再迎新。";
            case "满": return "满日为圆满充盈之日，如满月当空。宜求财祈福、成事立业，但满则溢，忌贪多无厌、骄傲自满。";
            case "平": return "平日为平稳中和之日，如水平流。宜守成持平、处理日常事务，不激不厉，稳步推进。";
            case "定": return "定日为安定稳固之日，如磐石落地。宜定计划、签合同、安床置业，但过于安定则难有突破。";
            case "执": return "执日为执掌控制之日，如持印掌权。宜坚持目标、执行计划，但须防固执己见、进退失据。";
            case "破": return "破日为冲破毁坏之日，如冰破春融。宜破旧立新、拆除旧物，忌谋新事、行大礼，先破后立之道。";
            case "危": return "危日为危难高耸之日，如临深渊。宜谨慎行事、居安思危，忌冒险激进，稳字当头可化险为夷。";
            case "成": return "成日为成就圆满之日，如功成名就。宜办大事、求结果，诸事易成，但成则思退，方保长久。";
            case "收": return "收日为收获收敛之日，如秋收冬藏。宜收官总结、纳财收藏，忌开创新局，宜收成不宜播种。";
            case "开": return "开日为开启舒展之日，如春暖花开。宜开工开业、出行求学，万象更新，运势开启，诸事可为。";
            case "闭": return "闭日为关闭收藏之日，如冬藏蛰居。宜闭关自省、收敛资源，忌大开大合，韬光养晦待时来。";
            default: return "十二建除为古代择日之法，以月建为基准，循环十二日，各有宜忌。";
        }
    }
}
