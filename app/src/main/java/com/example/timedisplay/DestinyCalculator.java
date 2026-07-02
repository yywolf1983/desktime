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
        
        int count = 0;
        String[] allGans = {yearGan, monthGan, dayGan, timeGan};
        String[] allZhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        
        for (String gan : allGans) {
            if (getWuXing(gan).equals(dayGanWuXing)) count++;
        }
        for (String zhi : allZhis) {
            if (getWuXing(zhi).equals(dayGanWuXing)) count++;
        }
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        String xieHao = shengMap.get(dayGanWuXing);
        String keHao = keMap.get(dayGanWuXing);
        String shengHao = "";
        for (String key : shengMap.keySet()) {
            if (shengMap.get(key).equals(dayGanWuXing)) {
                shengHao = key;
                break;
            }
        }
        
        sb.append("日主").append(dayGan).append("属").append(dayGanWuXing).append("，");
        sb.append("四柱中").append(dayGanWuXing).append("出现").append(count).append("次");
        
        if (count >= 3) {
            sb.append("，<font color='#FF6B6B'>偏旺</font>");
            sb.append("（比劫多）");
            sb.append("<br/>");
            sb.append("<font color='#90EE90'>喜用神：</font>").append(xieHao).append("（泄秀）、").append(keHao).append("（制劫）");
            sb.append("<br/>");
            sb.append("<font color='#FF6B6B'>忌神：</font>").append(shengHao).append("（生身）、").append(dayGanWuXing).append("（比劫帮身）");
            sb.append("<br/>");
            sb.append("<font color='#8899AA'>原因：日主").append(dayGanWuXing).append("过旺，需要").append(xieHao).append("泄其秀气，").append(keHao).append("制其旺气，方能平衡");
        } else if (count <= 1) {
            sb.append("，<font color='#FF6B6B'>偏弱</font>");
            sb.append("（印比少）");
            sb.append("<br/>");
            sb.append("<font color='#90EE90'>喜用神：</font>").append(shengHao).append("（生扶）、").append(dayGanWuXing).append("（比劫帮身）");
            sb.append("<br/>");
            sb.append("<font color='#FF6B6B'>忌神：</font>").append(xieHao).append("（泄耗）、").append(keHao).append("（克制）");
            sb.append("<br/>");
            sb.append("<font color='#8899AA'>原因：日主").append(dayGanWuXing).append("偏弱，需要").append(shengHao).append("来生扶，").append(dayGanWuXing).append("同类来相助，方能增强气势");
        } else {
            sb.append("，<font color='#FFD700'>中和</font>");
            sb.append("（五行均衡）");
            sb.append("<br/>");
            sb.append("<font color='#FFD700'>喜用神：</font>视具体组合而定，无明显喜忌");
            sb.append("<br/>");
            sb.append("<font color='#8899AA'>原因：日主力量适中，五行不偏不倚，为贵命格局，宜顺势而为</font>");
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
        sb.append("<font color='#FF6B6B'><b>━━━ 专业命盘 ━━━</b></font><br/><br/>");
        
        // 1. 四柱排布 + 纳音 + 生肖
        sb.append("<font color='#FFD700'><b>◆ 四柱排布</b></font><br/>");
        sb.append(yearPillar).append("　").append(monthPillar).append("　").append(dayPillar).append("　").append(timePillar).append("<br/><br/>");
        
        sb.append("<font color='#FFD700'><b>◆ 纳音五行</b></font>　<font color='#8899AA'>（干支组合的意象属性，反映命局基调）</font><br/>");
        String nYear = getNayin(yearGan, yearZhi);
        String nMonth = getNayin(monthGan, monthZhi);
        String nDay = getNayin(dayGan, dayZhi);
        String nTime = getNayin(timeGan, timeZhi);
        sb.append("年·").append(nYear).append("　月·").append(nMonth).append("<br/>");
        sb.append("日·").append(nDay).append("　时·").append(nTime).append("<br/><br/>");
        
        sb.append("<font color='#FFD700'><b>◆ 生肖属相</b></font>　年支").append(yearZhi).append("·属").append(getZodiacNameFromZhi(yearZhi)).append("<br/><br/>");
        
        // 2. 日主核心分析
        sb.append("<font color='#FFD700'><b>◆ 日主核心</b></font>　<font color='#8899AA'>（日干代表命主本人，是命局核心）</font><br/>");
        sb.append("日主 <font color='#FFD700'><b>").append(dayGan).append("</b></font> 属<font color='#90EE90'><b>").append(dayGanWuXing).append("</b></font>，").append(getGanDescription(dayGan)).append("<br/>");
        sb.append("<font color='#8899AA'>").append(getRiGanDetailedAnalysis(dayGan)).append("</font><br/><br/>");
        
        // 十二长生
        String stageDay = getTwelveStage(dayGan, dayZhi);
        String stageTime = getTwelveStage(dayGan, timeZhi);
        String stageMonth = getTwelveStage(dayGan, monthZhi);
        sb.append("<font color='#FFD700'><b>◆ 十二长生</b></font>　<font color='#8899AA'>（日主在各柱地支所处的生命阶段）</font><br/>");
        sb.append("日主").append(dayGan).append("在日支").append(dayZhi).append("：<font color='#90EE90'><b>").append(stageDay).append("</b></font>").append(" — ").append(getTwelveStageExplanation(stageDay)).append("<br/>");
        sb.append("日主").append(dayGan).append("在时支").append(timeZhi).append("：").append(stageTime).append(" — ").append(getTwelveStageExplanation(stageTime)).append("<br/>");
        sb.append("日主").append(dayGan).append("在月支").append(monthZhi).append("（月令）：").append(stageMonth).append(" — ").append(getTwelveStageExplanation(stageMonth)).append("<br/><br/>");
        
        // 3. 五行力量分析
        sb.append("<font color='#FFD700'><b>◆ 五行力量分析</b></font>　<font color='#8899AA'>（各柱与日主的生克关系）</font><br/>");
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
                sb.append("<font color='#C0C0C0'> ─ 比和</font>");
                if (i != 2) biCount++;
            } else if (isSheng(pGanWuXing, dayGanWuXing)) {
                sb.append("<font color='#90EE90'> → 生扶日主</font>");
                shengCount++;
            } else if (isKe(pGanWuXing, dayGanWuXing)) {
                sb.append("<font color='#FF6B6B'> → 克制日主</font>");
                keCount++;
            } else if (isSheng(dayGanWuXing, pGanWuXing)) {
                sb.append("<font color='#FFA500'> ← 被日主泄耗</font>");
                keCount++;
            } else if (isKe(dayGanWuXing, pGanWuXing)) {
                sb.append("<font color='#87CEEB'> ← 被日主所克</font>");
                keCount++;
            }
            sb.append("<br/>");
            
            // 日柱地支单独列出
            if (i == 2) {
                sb.append(pName).append("地支 ").append(pZhi).append("(").append(pZhiWuXing).append(")");
                if (pZhiWuXing.equals(dayGanWuXing)) {
                    sb.append("<font color='#C0C0C0'> ─ 比和</font>");
                } else if (isSheng(pZhiWuXing, dayGanWuXing)) {
                    sb.append("<font color='#90EE90'> → 生扶日主</font>");
                    shengCount++;
                } else if (isKe(pZhiWuXing, dayGanWuXing)) {
                    sb.append("<font color='#FF6B6B'> → 克制日主</font>");
                    keCount++;
                } else if (isSheng(dayGanWuXing, pZhiWuXing)) {
                    sb.append("<font color='#FFA500'> ← 被日主泄耗</font>");
                    keCount++;
                } else if (isKe(dayGanWuXing, pZhiWuXing)) {
                    sb.append("<font color='#87CEEB'> ← 被日主所克</font>");
                    keCount++;
                }
                sb.append("<br/>");
            }
        }
        sb.append("<br/>");
        
        // 4. 命局强弱判断
        sb.append("<font color='#FFD700'><b>◆ 命局强弱判断</b></font><br/>");
        sb.append("生扶之力：").append(shengCount).append("　克制之力：").append(keCount).append("　比和之力：").append(biCount).append("<br/>");
        
        int balance = shengCount + biCount - keCount;
        if (balance > 0) {
            sb.append("<font color='#90EE90'><b>日主身强</b></font>（生扶").append(shengCount).append("+比和").append(biCount).append("&gt;克制").append(keCount).append("）<br/>");
            sb.append("<font color='#8899AA'>解释：日主").append(dayGan).append("在命局中得到较多生扶和同类相助，气势旺盛、精力充沛，有较强的自我驱动力和抗压能力。</font><br/><br/>");
        } else if (balance < 0) {
            sb.append("<font color='#FF6B6B'><b>日主身弱</b></font>（克制").append(keCount).append("&gt;生扶").append(shengCount).append("+比和").append(biCount).append("）<br/>");
            sb.append("<font color='#8899AA'>解释：日主").append(dayGan).append("在命局中受到的克制和泄耗较多，气势偏弱，需要借助外界力量支持，更适合团队合作而非单打独斗。</font><br/><br/>");
        } else {
            sb.append("<font color='#FFD700'><b>日主中和</b></font>（生扶").append(shengCount).append("+比和").append(biCount).append("=克制").append(keCount).append("）<br/>");
            sb.append("<font color='#8899AA'>解释：五行力量均衡，是最理想的命局状态，适应能力强，能够根据环境灵活调整策略。</font><br/><br/>");
        }
        
        // 5. 五行喜忌
        sb.append("<font color='#FFD700'><b>◆ 五行喜忌</b></font><br/>");
        sb.append(getFiveElementXiJiDetailed(dayGan, dayGanWuXing, yearGan, yearZhi, monthGan, monthZhi, dayZhi, timeGan, timeZhi));
        sb.append("<br/><br/>");
        
        // 6. 十神全分析（天干）
        sb.append("<font color='#FFD700'><b>◆ 十神分析</b></font>　<font color='#8899AA'>（以日主为基准，看各天干与日主的十神关系）</font><br/>");
        String[] ganShen = getTenGods(dayGan, new String[]{yearGan, monthGan, dayGan, timeGan});
        String[] labelNames = {"年","月","日","时"};
        
        for (int i = 0; i < 4; i++) {
            String tenGod = ganShen[i];
            sb.append(labelNames[i]).append("干").append(pillars[i][0]).append("：<font color='#90EE90'><b>").append(tenGod).append("</b></font>");
            sb.append("（").append(getTenGodExplanation(tenGod)).append("）<br/>");
        }
        sb.append("<br/>");
        
        // 7. 干支生克关系
        sb.append("<font color='#FFD700'><b>◆ 干支关系</b></font>　<font color='#8899AA'>（每柱天干与地支的相互作用）</font><br/>");
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
        if (wuxing1.equals(wuxing2)) return "<font color='#C0C0C0'>比和（同类相助）</font>";
        
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        
        if (shengMap.get(wuxing1).equals(wuxing2)) {
            return "<font color='#90EE90'>生扶（" + wuxing1 + "生" + wuxing2 + "）</font>";
        } else if (shengMap.get(wuxing2).equals(wuxing1)) {
            return "<font color='#FFA500'>泄耗（" + wuxing1 + "被" + wuxing2 + "生，泄气）</font>";
        } else if (keMap.get(wuxing1).equals(wuxing2)) {
            return "<font color='#FF6B6B'>克制（" + wuxing1 + "克" + wuxing2 + "）</font>";
        } else {
            return "<font color='#FF6B6B'>被克（" + wuxing1 + "被" + wuxing2 + "克）</font>";
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
            case "比肩": return "如兄弟姐妹，代表同辈助力、竞争关系、自我意识";
            case "劫财": return "如朋友伙伴，代表合作与争夺，性格豪爽但易冲动消费";
            case "正印": return "如母亲长辈，代表贵人扶持、学识智慧、仁慈包容";
            case "偏印": return "如继母师长，代表特殊才能、偏门学问、孤僻独特";
            case "食神": return "如子女晚辈，代表才华表达、口福享受、温和乐观";
            case "伤官": return "如才华外露，代表聪明机智、创造力强、不拘一格";
            case "正财": return "如正当收入，代表稳定财运、勤俭持家、务实可靠";
            case "偏财": return "如意外之财，代表投资理财、慷慨大方、人脉广泛";
            case "正官": return "如上级领导，代表纪律约束、名声地位、正直守信";
            case "七杀": return "如将军统帅，代表权威决断、事业心强、敢作敢为";
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
            case "长生": return "如婴儿初生，充满生机希望，宜开始新计划";
            case "沐浴": return "如少年初长，敏感多变，桃花运旺但需防感情用事";
            case "冠带": return "如青年加冠，逐渐成熟，开始承担责任";
            case "临官": return "如人到壮年，事业有成，精力充沛，适合奋斗";
            case "帝旺": return "如人生巅峰，力量最强，但物极必反需谨慎";
            case "衰": return "如盛极而衰，需放慢节奏，保重身体";
            case "病": return "如人生低谷，需休养生息，注意健康";
            case "死": return "如冬眠之时，宜退守等待，不宜冲动";
            case "墓": return "如入库收藏，宜储蓄积累，保守稳健";
            case "绝": return "如种子入土，表面沉寂，实则孕育新生";
            case "胎": return "如母腹孕育，暗中筹划，等待时机";
            case "养": return "如胎儿成长，蓄势待发，培养实力";
            default: return "";
        }
    }
    
    // 纳音通俗解释
    public static String getNayinExplanation(String nayin) {
        switch (nayin) {
            case "海中金": return "深海藏金，需经打磨方显价值，先苦后甜之象";
            case "炉中火": return "炉中烈火烧炼万物，热情进取之象";
            case "大林木": return "参天古木根基深厚，稳重持久之象";
            case "路旁土": return "道路尘土平凡踏实，务实可靠之象";
            case "剑锋金": return "宝剑锋芒锐利无比，果断刚毅之象";
            case "山头火": return "山头野火燎原之势，爆发力强但难持久";
            case "涧下水": return "山涧溪水清澈灵动，聪明灵活之象";
            case "城头土": return "城墙厚土坚固安稳，稳重护家之象";
            case "白蜡金": return "蜡中藏金精美细致，内秀外拙之象";
            case "杨柳木": return "杨柳依依柔韧随和，善交际适应力强";
            case "泉中水": return "泉水清澈甘甜滋养，智慧深藏之象";
            case "屋上土": return "屋瓦遮风挡雨，有责任感能保护他人";
            case "霹雳火": return "雷电之火迅猛有力，爆发力强性格刚烈";
            case "松柏木": return "松柏常青不畏严寒，坚韧不拔之象";
            case "长流水": return "长流不息源源不断，持续发展之象";
            case "沙中金": return "沙中淘金需耐心，后发有力之象";
            case "山下火": return "山下暗火不张扬，内热外冷之象";
            case "平地木": return "平地之木平凡生长，朴实自然之象";
            case "壁上土": return "壁间之土稳固可靠，守护家园之象";
            case "金箔金": return "金箔华美装饰性强，追求品质之象";
            case "覆灯火": return "灯烛之明温暖人心，文化传承之象";
            case "天河水": return "天河银汉浩瀚无边，胸怀宽广之象";
            case "大驿土": return "驿道通达四方，人脉广泛交际强";
            case "钗钏金": return "首饰之金精美华丽，审美高雅之象";
            case "桑柘木": return "桑树养蚕贡献于人，默默奉献之象";
            case "大溪水": return "大溪奔腾不拘小节，豪爽大气之象";
            case "沙中土": return "沙土松散需聚合力，宜团队合作";
            case "天上火": return "天上骄阳明照四方，光芒耀眼之象";
            case "石榴木": return "石榴多子多福，家运兴旺之象";
            case "大海水": return "大海浩瀚包容万物，胸襟宽广之象";
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
            sb.append("<font color='#FF6B6B'>偏旺</font>，");
            sb.append("<font color='#90EE90'>喜</font>克制泄耗之五行，");
            sb.append("<font color='#FF6B6B'>忌</font>生扶比和之五行");
        } else if (count <= 1) {
            sb.append("<font color='#FF6B6B'>偏弱</font>，");
            sb.append("<font color='#90EE90'>喜</font>生扶比和之五行，");
            sb.append("<font color='#FF6B6B'>忌</font>克制泄耗之五行");
        } else {
            sb.append("<font color='#FFD700'>中和</font>，");
            sb.append("五行均衡，喜忌不明显，宜顺势而为");
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
            case "甲": return "阳木·参天大树，主尊贵权威";
            case "乙": return "阴木·花草之木，主柔顺仁慈";
            case "丙": return "阳火·太阳之火，主光明热情";
            case "丁": return "阴火·灯烛之火，主文明细致";
            case "戊": return "阳土·大地之土，主稳重诚信";
            case "己": return "阴土·田园之土，主包容厚德";
            case "庚": return "阳金·刀剑之金，主果断刚毅";
            case "辛": return "阴金·首饰之金，主精致细腻";
            case "壬": return "阳水·江海之水，主智慧流动";
            case "癸": return "阴水·雨露之水，主聪明神秘";
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
                return "甲木为参天大树，栋梁之材。性格正直、有领导力，具开创精神。宜在东方发展，适合从事管理、领导、林业等行业。";
            case "乙": 
                return "乙木为花草之木，柔顺多姿。性格温和、善于变通，具艺术天赋。宜在东方发展，适合从事艺术、教育、美容等行业。";
            case "丙": 
                return "丙火为太阳之火，光明照耀。性格热情、积极向上，具领导魅力。宜在南方发展，适合从事文化、能源、餐饮等行业。";
            case "丁": 
                return "丁火为灯烛之火，温暖光明。性格细腻、善于思考，具才华智慧。宜在南方发展，适合从事科技、文化、服务等行业。";
            case "戊": 
                return "戊土为大地之土，厚重沉稳。性格踏实、诚实守信，具包容精神。宜在中央发展，适合从事建筑、房地产、农业等行业。";
            case "己": 
                return "己土为田园之土，肥沃滋养。性格宽厚、乐于助人，具奉献精神。宜在中央发展，适合从事农业、医药、慈善等行业。";
            case "庚": 
                return "庚金为刀剑之金，锐利刚毅。性格果断、勇往直前，具决断力。宜在西方发展，适合从事金融、军事、金属等行业。";
            case "辛": 
                return "辛金为首饰之金，精致美丽。性格优雅、追求完美，具审美能力。宜在西方发展，适合从事珠宝、艺术、设计等行业。";
            case "壬": 
                return "壬水为江海之水，奔腾不息。性格聪明、善于变通，具商业头脑。宜在北方发展，适合从事商贸、水产、运输等行业。";
            case "癸": 
                return "癸水为雨露之水，润物无声。性格神秘、聪明睿智，具洞察力。宜在北方发展，适合从事学术、策划、保密等行业。";
            default: 
                return "日干代表自身，反映个人性格与天赋。";
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
            case "甲": return "阳木·参天大树，栋梁之才。主尊贵权威、领袖气质。性格正直刚强，有决断力，适合领导管理、政治、军事等行业。甲木得令则生机勃勃，失令则虽有志向但难以施展。";
            case "乙": return "阴木·花草之木，柔顺优美。主仁慈善良、多才多艺。性格温和细腻，善于协调，适合艺术、文化、教育等行业。乙木虽柔，但韧性十足，善于以柔克刚。";
            case "丙": return "阳火·太阳之火，光明普照。主热情开朗、名声远播。性格外向积极，充满活力，适合演艺、销售、公关等行业。丙火旺盛则光芒四射，失令则热情难持久。";
            case "丁": return "阴火·灯烛之火，柔和温暖。主文明细致、才华出众。性格文雅内敛，注重细节，适合艺术创作、手工艺、精密制造等行业。丁火虽小，但能照亮黑暗。";
            case "戊": return "阳土·大地之土，厚重沉稳。主稳重诚信、包容万物。性格踏实可靠，值得信赖，适合金融、房地产、仓储等行业。戊土得令则厚德载物，失令则固执保守。";
            case "己": return "阴土·田园之土，肥沃滋润。主包容厚德、善于耕耘。性格温和善良，善于照顾他人，适合农业、医疗、慈善等行业。己土虽柔，但能孕育万物。";
            case "庚": return "阳金·刀剑之金，锋利无比。主果断刚毅、权威正义。性格刚强果断，不畏困难，适合军事、法律、管理等行业。庚金得令则刚强有力，失令则锋芒毕露易伤人。";
            case "辛": return "阴金·首饰之金，精致秀丽。主细腻精致、审美高雅。性格细腻敏感，追求完美，适合艺术设计、珠宝、美容等行业。辛金虽柔，但精致典雅。";
            case "壬": return "阳水·江海之水，浩瀚无垠。主智慧流动、胸怀宽广。性格豁达开朗，善于变通，适合贸易、航海、水利等行业。壬水得令则奔腾不息，失令则泛滥成灾。";
            case "癸": return "阴水·雨露之水，润物无声。主聪明神秘、直觉敏锐。性格聪明伶俐，心思缜密，适合学术研究、科技研发、侦探等行业。癸水虽柔，但能渗透万物。";
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
        sb.append("<font color='#98D8F0'><b>🌟 性格特点</b></font><br/>");
        sb.append(getPersonalityRich(dayGan, dayZhi, dayGanWuXing, isStrong, isWeak, isBalance));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 2. 事业运势
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>💼 事业运势</b></font><br/>");
        sb.append(getCareerAnalysisRich(dayGan, dayGanWuXing, monthGan, monthZhi, mGanShen, isStrong, isWeak));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 3. 财富运势
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>💰 财富运势</b></font><br/>");
        sb.append(getWealthAnalysisRich(dayGan, dayGanWuXing, pillars, isStrong, isWeak));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 4. 感情婚姻
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>💕 感情婚姻</b></font><br/>");
        sb.append(getRelationshipAnalysisRich(dayGan, dayZhi, dayGanWuXing, dayZhiWuXing, dayZodiac, yearZhi, monthZhi, timeZhi));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 5. 健康建议
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>🏥 健康建议</b></font><br/>");
        sb.append(getHealthAnalysisRich(dayGanWuXing, shengCount, keCount, biCount));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 6. 人际关系
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>🤝 人际关系</b></font><br/>");
        sb.append(getSocialAnalysisRich(dayGan, dayGanWuXing, yGanShen, mGanShen, tGanShen, yearGan, zodiac, isStrong, isWeak, yearZhi, monthZhi, dayZhi, timeZhi));
        sb.append("<br/>");
        
        // ════════════════════════════
        // 7. 人生发展建议
        // ════════════════════════════
        sb.append("<font color='#98D8F0'><b>🎯 人生发展建议</b></font><br/>");
        sb.append(getLifeAdviceRich(dayGan, dayGanWuXing, zodiac, shengHao, xieHao, isStrong, isWeak, isBalance));
        sb.append("<br/><br/>");
        
        sb.append("<font color='#8899AA'><i>※ 以上解读基于四柱命理分析，仅供参考。命运掌握在自己手中，积极努力、保持善良才是最好的风水。</i></font>");
        
        return sb.toString();
    }
    
    // ═══ 丰富的通俗解读方法 ═══
    
    public static String getPersonalityRich(String dayGan, String dayZhi, String wuXing, boolean isStrong, boolean isWeak, boolean isBalance) {
        StringBuilder sb = new StringBuilder();
        String zodiac = getZodiacNameFromZhi(dayZhi);
        
        switch (wuXing) {
            case "木":
                sb.append("你如<font color='#90EE90'><b>乔木挺立</b></font>，生命力蓬勃，向上进取。");
                if (isStrong) sb.append("刚毅自信，有领袖气质，主意正、难动摇。忌刚愎自用，兼听则明。");
                else if (isWeak) sb.append("如藤附木，善借外力，温雅有礼，人缘佳。需在关键处坚持己见。");
                else sb.append("刚柔兼济，守原则而知权变。");
                break;
            case "火":
                sb.append("你如<font color='#FF6B6B'><b>明烛暖焰</b></font>，热情洋溢，走到哪里都自带光芒。");
                if (isStrong) sb.append("精力旺盛、雷厉风行，是天然的行动派。戒急躁冲动，三思后行。");
                else if (isWeak) sb.append("内热外敛，默默赋能他人。宜崭露头角，让才华被看见。");
                else sb.append("热情有度、有始有终，诚恳可交。");
                break;
            case "土":
                sb.append("你如<font color='#FFD700'><b>厚土载物</b></font>，踏实稳重，予人安全感。");
                if (isStrong) sb.append("敦厚诚信，朋友中最是靠得住。稳扎稳打，虽缓而实。偶尔尝试新鲜事物，会有意外之喜。");
                else if (isWeak) sb.append("心善好施，需警惕被人利用。宜设立底线，护住心神。");
                else sb.append("务实持重，有条不紊，稳步前行。");
                break;
            case "金":
                sb.append("你如<font color='#C0C0C0'><b>宝剑出匣</b></font>，头脑清晰，果敢利落。");
                if (isStrong) sb.append("刚正重义，处事爽利。藏锋守拙，过刚易折。");
                else if (isWeak) sb.append("细致求精，把控力强。勿求完美而自耗，该放则放。");
                else sb.append("理性果决而不失细腻，善决断也善执行。");
                break;
            case "水":
                sb.append("你如<font color='#87CEEB'><b>清泉灵动</b></font>，头脑灵活，随机应变。");
                if (isStrong) sb.append("智慧超群，洞察入微，总能觅得佳策。莫思虑过甚，简即是真。");
                else if (isWeak) sb.append("心思缜密，直觉敏锐，见地独到。当增自信，你之见解实有分量。");
                else sb.append("聪而不露，圆融处世，天生智囊。");
                break;
        }
        sb.append("<br/>日支").append(dayZhi).append("（属").append(zodiac).append("），主内，").append(getDayZhiPersonality(dayZhi)).append("。");
        sb.append("<br/><br/>").append(getProsAndCons(wuXing, isStrong, isWeak));
        return sb.toString();
    }
    
    public static String getDayZhiPersonality(String zhi) {
        switch (zhi) {
            case "子": return "温柔细腻，重视家庭温暖";
            case "丑": return "忠诚踏实，对伴侣专一稳定";
            case "寅": return "自信独立，在家庭中也追求自我实现";
            case "卯": return "温和有艺术气质，家庭氛围和谐";
            case "辰": return "有魅力有担当，是家庭中的主心骨";
            case "巳": return "聪明体贴，善于经营家庭关系";
            case "午": return "热情浪漫，让家庭生活丰富多彩";
            case "未": return "温柔善良，是家庭中的润滑剂";
            case "申": return "机智灵活，能给家庭带来新鲜感";
            case "酉": return "精致讲究，注重家庭生活品质";
            case "戌": return "忠诚可靠，是家庭坚实后盾";
            case "亥": return "宽厚包容，家庭关系其乐融融";
            default: return "有独特个性";
        }
    }
    
    public static String getCareerAnalysisRich(String dayGan, String wuXing, String monthGan, String monthZhi, String monthShen, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        String monthSeason = getSeasonDescription(monthZhi);
        
        switch (wuXing) {
            case "木": sb.append("天赋在<font color='#90EE90'><b>规划设计、文教领域</b></font>。"); break;
            case "火": sb.append("天赋在<font color='#FF6B6B'><b>创意表达、市场传播</b></font>。"); break;
            case "土": sb.append("天赋在<font color='#FFD700'><b>财务建筑、地产实业</b></font>。"); break;
            case "金": sb.append("天赋在<font color='#C0C0C0'><b>金融法律、技术研发</b></font>。"); break;
            case "水": sb.append("天赋在<font color='#87CEEB'><b>商贸流通、信息传媒</b></font>。"); break;
        }
        
        if (!monthSeason.isEmpty()) sb.append("月令").append(monthSeason).append("，");
        
        if (isStrong) {
            sb.append("身强可担重任，宜主攻开拓、登大平台任管理。");
            if (monthShen.equals("正官") || monthShen.equals("七杀")) sb.append("月有官杀，职场得位，管理有方。");
            if (monthShen.equals("正财") || monthShen.equals("偏财")) sb.append("商才卓越，善理财源。");
        } else if (isWeak) {
            sb.append("宜稳中求进，择良木而栖，好团队胜过单打独斗。");
            if (monthShen.equals("正印") || monthShen.equals("偏印")) sb.append("贵人运旺，长上提携，多向前辈请益。");
            if (monthShen.equals("比肩") || monthShen.equals("劫财")) sb.append("合作为上，得同道相助则事业顺遂。");
        } else {
            sb.append("适应力强，无论环境皆能出彩，宜做复合型人才。");
        }
        
        sb.append("<br/>月干十神：<font color='#90EE90'><b>").append(monthShen).append("</b></font>，主事业指引，").append(getTenGodExplanation(monthShen)).append("。");
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

        sb.append("<font color='#8899AA'>【财源方位】</font><br/>");
        switch (wuXing) {
            case "木": sb.append("日主属木，我克者为财，财源在<font color='#90EE90'><b>土</b></font>（地产·农业·基建·陶瓷）。"); break;
            case "火": sb.append("日主属火，我克者为财，财源在<font color='#FF6B6B'><b>金</b></font>（金融·科技·法律·珠宝）。"); break;
            case "土": sb.append("日主属土，我克者为财，财源在<font color='#87CEEB'><b>水</b></font>（商贸·物流·通讯·旅游）。"); break;
            case "金": sb.append("日主属金，我克者为财，财源在<font color='#90EE90'><b>木</b></font>（文创·教育·医药·林业）。"); break;
            case "水": sb.append("日主属水，我克者为财，财源在<font color='#FF6B6B'><b>火</b></font>（餐饮·能源·娱乐·传媒）。"); break;
        }
        sb.append("<br/><font color='#8899AA'>通俗说：「财」就是你能掌控的东西，木克土得土之财，就像种树能改良土壤、收获果实。</font><br/>");

        // 财星配置
        sb.append("<br/><font color='#8899AA'>【财星配置】</font><br/>");
        if (hasZhengCai && hasPianCai) {
            sb.append("命带<font color='#90EE90'><b>正财</b></font>＋<font color='#FFA500'><b>偏财</b></font>，正偏财俱全，既有稳定正业，又有投资嗅觉。");
            sb.append("宜正业为主、投资为辅，七分稳三分搏，方能源源不绝。<br/>");
        } else if (hasZhengCai) {
            sb.append("命带<font color='#90EE90'><b>正财</b></font>，财来有道，正业稳定积累。");
            sb.append("宜中长线布局，靠工资薪酬·稳健经营致富，不贪偏门横财。<br/>");
        } else if (hasPianCai) {
            sb.append("命带<font color='#FFA500'><b>偏财</b></font>，有投资嗅觉与经商头脑。");
            sb.append("须控风险、莫孤注一掷，赚快钱也要留退路，「见好就收」是秘诀。<br/>");
        } else {
            sb.append("命局<font color='#FF6B6B'>财星不显</font>，财运多靠技能·才华·人脉间接转化。");
            sb.append("宜以专长立身，食神伤官生财，「凭本事吃饭」一样富足。<br/>");
        }

        if (hasBiJian || hasJieCai) {
            sb.append("命带<font color='#FFA500'>比肩/劫财</font>，财易被分夺，");
            if (isStrong) sb.append("但身强可抗，反主合伙求财、团队致富之象。");
            else sb.append("身弱则财来财去，宜避免与人合资、谨防借贷纠纷。");
            sb.append("<br/>");
        }

        // 身强弱与担财
        sb.append("<br/><font color='#8899AA'>【担财能力】</font><br/>");
        if (isStrong) {
            sb.append("<font color='#90EE90'><b>身强能担财</b></font>：如大力士举鼎，财富空间可观，积极开源、善理财。");
            sb.append("<br/><font color='#8899AA'>通俗说：身体好才能扛大财，你有能力驾驭大项目、大投资，但也要防「贪多嚼不烂」。</font><br/>");
        } else if (isWeak) {
            sb.append("<font color='#FF6B6B'><b>身弱慎财</b></font>：如体弱者负重，财多反成累赘，稳健为主。");
            sb.append("<br/><font color='#8899AA'>通俗说：力气小扛不动大箱，莫眼红他人暴富，寻良伴共理财、借团队之力更稳当。</font><br/>");
        } else {
            sb.append("<font color='#FFD700'><b>中和担财</b></font>：财运平稳，量入为出，积微成著。");
            sb.append("<br/><font color='#8899AA'>通俗说：不贪不躁，细水长流，适合自己的才是最好的。</font><br/>");
        }

        // 求财方位与时机
        sb.append("<br/><font color='#8899AA'>【求财方位】</font><br/>");
        switch (wuXing) {
            case "木": sb.append("求财旺方：<font color='#90EE90'><b>中央·东北·西南</b></font>（土方），此方向求财最为顺遂。"); break;
            case "火": sb.append("求财旺方：<font color='#FF6B6B'><b>西方·西北</b></font>（金方），此方向求财最为顺遂。"); break;
            case "土": sb.append("求财旺方：<font color='#87CEEB'><b>北方</b></font>（水方），此方向求财最为顺遂。"); break;
            case "金": sb.append("求财旺方：<font color='#90EE90'><b>东方·东南</b></font>（木方），此方向求财最为顺遂。"); break;
            case "水": sb.append("求财旺方：<font color='#FF6B6B'><b>南方</b></font>（火方），此方向求财最为顺遂。"); break;
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
            sb.append("与伴侣志趣相投、琴瑟相谐。唯太似易争，互让为贵。");
        } else if (ganZhiRel.contains("得助") || ganZhiRel.contains("生天干")) {
            sb.append("伴侣是人生贵人，温柔扶持、相得益彰，宜珍惜此缘。");
        } else if (ganZhiRel.contains("泄秀") || ganZhiRel.contains("天干生")) {
            sb.append("你付出为多，甘为家庭倾注心力。也须学接纳，情义两相衡。");
        } else if (ganZhiRel.contains("制杀") || ganZhiRel.contains("天干克")) {
            sb.append("你在家中主导，当不忘尊重，婚姻是两人之舞，非一人独唱。");
        } else if (ganZhiRel.contains("受制") || ganZhiRel.contains("克天干")) {
            sb.append("伴侣个性较强势，需多沟通理解，相敬如宾方长久。");
        }
        sb.append("<br/>良配：五行互补，性格").append(isYangGan(dayGan) ? "阴柔温润" : "刚健爽朗").append("者，彼此成就。");
        sb.append("<br/><br/>").append(getPeachBlossomAnalysis(yearZhi, monthZhi, dayZhi, timeZhi));
        return sb.toString();
    }
    
    public static String getHealthAnalysisRich(String wuXing, int shengCount, int keCount, int biCount) {
        StringBuilder sb = new StringBuilder();

        sb.append("<font color='#8899AA'>【脏腑对应】</font><br/>");
        switch (wuXing) {
            case "木": sb.append("日主属木，主司<font color='#90EE90'><b>肝·胆·筋·目</b></font>。肝为将军之官，主疏泄藏血，情绪起伏最易伤肝。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你的身体就像一棵树，最怕「憋」和「怒」，气血要像树枝一样舒展通畅才健康。</font><br/>"); break;
            case "火": sb.append("日主属火，主司<font color='#FF6B6B'><b>心·小肠·脉·舌</b></font>。心为君主之官，主神明血脉，过劳与情绪激动最伤心。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你的身体像一团火，最怕「熬」和「急」，心要像烛火一样平稳跳动才安宁。</font><br/>"); break;
            case "土": sb.append("日主属土，主司<font color='#FFD700'><b>脾·胃·肉·口</b></font>。脾胃为后天之本，气血生化之源，饮食不节最伤脾。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你的身体像一片田地，最怕「撑」和「凉」，脾胃要像和面一样揉得匀才养人。</font><br/>"); break;
            case "金": sb.append("日主属金，主司<font color='#C0C0C0'><b>肺·大肠·皮·鼻</b></font>。肺为相傅之官，主气司呼吸，悲忧过度最伤肺。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你的身体像一把钟，最怕「悲」和「燥」，呼吸要像秋风一样清肃通畅才润泽。</font><br/>"); break;
            case "水": sb.append("日主属水，主司<font color='#87CEEB'><b>肾·膀胱·骨·耳</b></font>。肾为先天之本，藏精主骨生髓，惊恐与过劳最伤肾。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你的身体像一口井，最怕「冻」和「怕」，肾气要像泉水一样源源不断才充沛。</font><br/>"); break;
        }

        // 五行失衡专项预警
        sb.append("<br/><font color='#8899AA'>【失衡预警】</font><br/>");
        if (keCount > shengCount + biCount + 1) {
            sb.append("<font color='#FF6B6B'>⚠ 命局克伐较重</font>（生扶").append(shengCount).append("+比和").append(biCount)
                    .append("＜克泄").append(keCount).append("），正气偏弱，易感外邪。<br/>");
            switch (wuXing) {
                case "木": sb.append("肝气郁结风险高，易怒、胸闷、女性经乳胀痛；宜疏肝理气，忌熬夜动怒。"); break;
                case "火": sb.append("心气不足风险高，心悸、失眠、面色无华；宜养心安神，忌过劳亢奋。"); break;
                case "土": sb.append("脾胃虚弱风险高，消化不良、食欲差、四肢乏力；宜温补脾胃，忌生冷暴食。"); break;
                case "金": sb.append("肺气虚损风险高，易感冒、气短、皮肤干燥；宜补肺固表，忌悲忧伤神。"); break;
                case "水": sb.append("肾元不固风险高，腰膝酸软、畏寒、夜尿频多；宜温补肾阳，忌惊恐劳累。"); break;
            }
            sb.append("<br/>");
        } else if (shengCount + biCount > keCount + 1) {
            sb.append("<font color='#FFA500'>⚠ 命局生扶较旺</font>（生扶").append(shengCount).append("+比和").append(biCount)
                    .append("＞克泄").append(keCount).append("），正气虽足但易壅滞。<br/>");
            switch (wuXing) {
                case "木": sb.append("肝木过旺易化火，头痛目赤、急躁易怒；宜平肝清热，少酒少辛。"); break;
                case "火": sb.append("心火偏亢易扰神，口舌生疮、心烦失眠；宜清心降火，少熬夜。"); break;
                case "土": sb.append("脾土壅滞易生湿，身体困重、口中黏腻；宜健脾化湿，少甜腻。"); break;
                case "金": sb.append("肺金过燥易伤津，干咳少痰、皮肤起屑；宜润肺生津，多饮水。"); break;
                case "水": sb.append("肾水泛溢易伤阳，下肢浮肿、畏寒怕冷；宜温阳化水，少咸寒。"); break;
            }
            sb.append("<br/>");
        } else {
            sb.append("<font color='#90EE90'>✓ 五行力量基本均衡</font>，脏腑协调，健康基础良好，注意日常保养即可。<br/>");
        }

        // 养生宜忌
        sb.append("<br/><font color='#8899AA'>【养生宜忌】</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("<font color='#90EE90'>宜：</font>青色食物（菠菜·芹菜·猕猴桃）、酸味入肝、晨起舒展拉伸、踏青散步<br/>");
                sb.append("<font color='#FF6B6B'>忌：</font>过量饮酒、长期憋怒、熬夜伤肝、久视伤目<br/>");
                sb.append("<font color='#FFD700'>穴位：</font>太冲·期门·行间，常按可疏肝理气"); break;
            case "火":
                sb.append("<font color='#90EE90'>宜：</font>红色食物（红枣·番茄·红豆）、苦味入心、午时小憩、静心冥想<br/>");
                sb.append("<font color='#FF6B6B'>忌：</font>暴怒激动、过度劳累、辛辣燥热、熬夜耗神<br/>");
                sb.append("<font color='#FFD700'>穴位：</font>神门·内关·心俞，常按可养心安神"); break;
            case "土":
                sb.append("<font color='#90EE90'>宜：</font>黄色食物（南瓜·小米·土豆）、甘味入脾、三餐定时、细嚼慢咽<br/>");
                sb.append("<font color='#FF6B6B'>忌：</font>生冷油腻、暴饮暴食、思虑过度、久坐伤肉<br/>");
                sb.append("<font color='#FFD700'>穴位：</font>足三里·中脘·脾俞，常按可健脾和胃"); break;
            case "金":
                sb.append("<font color='#90EE90'>宜：</font>白色食物（银耳·百合·雪梨）、辛味入肺、深呼吸吐纳、有氧运动<br/>");
                sb.append("<font color='#FF6B6B'>忌：</font>悲忧伤肺、干燥环境、吸烟、寒凉直吹<br/>");
                sb.append("<font color='#FFD700'>穴位：</font>列缺·肺俞·迎香，常按可宣肺利气"); break;
            case "水":
                sb.append("<font color='#90EE90'>宜：</font>黑色食物（黑豆·黑芝麻·紫米）、咸味入肾、泡脚暖腰、太极八段锦<br/>");
                sb.append("<font color='#FF6B6B'>忌：</font>惊恐伤肾、过度房劳、寒凉生冷、久立伤骨<br/>");
                sb.append("<font color='#FFD700'>穴位：</font>涌泉·肾俞·太溪，常按可固肾培元"); break;
        }

        sb.append("<br/><br/>").append(getSeasonHealthDetail(wuXing));
        return sb.toString();
    }
    
    public static String getSocialAnalysisRich(String dayGan, String wuXing, String yShen, String mShen, String tShen, String yearGan, String zodiac, boolean isStrong, boolean isWeak,
                                                 String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();

        sb.append("<font color='#8899AA'>【社交特质】</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("待人真诚，如<font color='#90EE90'><b>大树</b></font>般予人依靠，重情义、讲原则。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你是朋友圈里的「老好人」，乐于庇护他人，但有时过于固执己见。</font><br/>");
                break;
            case "火":
                sb.append("热情开朗，如<font color='#FF6B6B'><b>明烛</b></font>般带动气氛，善交际、爱表现。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你是聚会中的「气氛组」，光芒四射，但有时过于急躁冲动。</font><br/>");
                break;
            case "土":
                sb.append("敦厚守信，如<font color='#FFD700'><b>大地</b></font>般人人信赖，稳重可靠、重承诺。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你是朋友眼中的「定海神针」，踏实靠谱，但有时过于保守慢热。</font><br/>");
                break;
            case "金":
                sb.append("重义守信，如<font color='#C0C0C0'><b>金石</b></font>般口碑极佳，刚毅果决、讲规矩。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你是圈中的「正义使者」，爱憎分明，但有时过于刚直易得罪人。</font><br/>");
                break;
            case "水":
                sb.append("善解人意，如<font color='#87CEEB'><b>清泉</b></font>般与各色人等皆能交融，圆融变通。<br/>");
                sb.append("<font color='#8899AA'>通俗说：你是人脉里的「润滑剂」，八面玲珑，但有时过于圆滑失原则。</font><br/>");
                break;
        }

        sb.append("<br/><font color='#8899AA'>【强弱影响】</font><br/>");
        if (isStrong) {
            sb.append("身强主<font color='#90EE90'><b>领袖型</b></font>社交：团体中往往为领袖，朋友有难第一个想到你。");
            sb.append("宜学会「兼听则明」，勿因刚强而独断，多留余地给他人，方能聚人聚心。<br/>");
        } else if (isWeak) {
            sb.append("身弱主<font color='#87CEEB'><b>贵人型</b></font>社交：贵人缘佳，总有援手。");
            sb.append("宜多交良师益友，借助团队力量成就自己，「独木难成林」是你的写照。<br/>");
        } else {
            sb.append("中和主<font color='#FFD700'><b>中庸型</b></font>社交：低调有度，恰到好处的存在感，让人如沐春风。");
            sb.append("进退有度，既能融入群体又保独立，是难得的社交高手。<br/>");
        }

        // 合作贵人方位
        sb.append("<br/><font color='#8899AA'>【贵人方位】</font><br/>");
        sb.append(getDirectionAdvice(wuXing)).append("方位最旺，往此方向求贤访友、拓展人脉，多有意外之喜。<br/>");
        switch (wuXing) {
            case "木": sb.append("合作首选：属<font color='#90EE90'><b>水</b></font>之人（生扶你）与属<font color='#90EE90'><b>木</b></font>之人（同类相助）。"); break;
            case "火": sb.append("合作首选：属<font color='#90EE90'><b>木</b></font>之人（生扶你）与属<font color='#90EE90'><b>火</b></font>之人（同类相助）。"); break;
            case "土": sb.append("合作首选：属<font color='#90EE90'><b>火</b></font>之人（生扶你）与属<font color='#90EE90'><b>土</b></font>之人（同类相助）。"); break;
            case "金": sb.append("合作首选：属<font color='#90EE90'><b>土</b></font>之人（生扶你）与属<font color='#90EE90'><b>金</b></font>之人（同类相助）。"); break;
            case "水": sb.append("合作首选：属<font color='#90EE90'><b>金</b></font>之人（生扶你）与属<font color='#90EE90'><b>水</b></font>之人（同类相助）。"); break;
        }

        sb.append("<br/>属").append(zodiac).append("，与三合局之人最为契合，有较好的适应力和独特魅力。");
        sb.append("<br/><br/>").append(getTianYiGuiRenAnalysis(dayGan, yearZhi, monthZhi, dayZhi, timeZhi));
        return sb.toString();
    }
    
    public static String getLifeAdviceRich(String dayGan, String wuXing, String zodiac, String shengHao, String xieHao, boolean isStrong, boolean isWeak, boolean isBalance) {
        StringBuilder sb = new StringBuilder();
        
        if (isStrong) {
            sb.append("命局<font color='#90EE90'><b>身强气盛</b></font>，如良弓待发，找准方向方能致远。<br/>");
            sb.append("宜投向").append(xieHao).append("属行业，借力使力，刚柔相济。");
        } else if (isWeak) {
            sb.append("命局<font color='#FF6B6B'><b>身弱需扶</b></font>，如种待萌，需良土与天时。<br/>");
            sb.append("多结贵人，").append(shengHao).append("属之人与事是你的后盾；不疾不徐，学以致用。");
        } else {
            sb.append("命局<font color='#FFD700'><b>五行中和</b></font>，难得之格，如水流转，进退自如。<br/>");
            sb.append("保持开放应变，顺势而行，此刻便是黄金期。");
        }
        
        String[] zodiacCompat = getZodiacCompat(zodiac);
        sb.append("<br/>属").append(zodiac).append("，与").append(zodiacCompat[0]).append("、").append(zodiacCompat[1]).append("、").append(zodiacCompat[2]).append("最合。");
        sb.append(getDirectionAdvice(wuXing)).append("方位最旺。");
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
        result += isLucky ? "值符" + zhiFuStar + "星吉利，" : "值符" + zhiFuStar + "星一般，";
        result += doorLucky ? "值使" + zhiShiDoor + "门有利" : "值使" + zhiShiDoor + "门一般";
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
        String[] barColors = {"#FF6B6B", "#90EE90", "#FFD700", "#87CEEB"};

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
        sb.append("<br/><font color='#8899AA'><i>※ 本气为地支主气，中气余气为暗藏力量，藏干十神代表隐性特质</i></font>");
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

        sb.append("月令").append(monthZhi).append("藏<font color='#90EE90'><b>").append(mainQi).append("</b></font>，取为<font color='#FFD700'><b>").append(patternName).append("</b></font>。<br/>");

        // 格局解释
        switch (patternShen) {
            case "正官":
                sb.append("正官格主贵气、名声、纪律。宜遵纪守法，从政或大型机构发展，官星有力则仕途亨通。");
                if (isStrong && containsTenGod(new String[]{yearGan, monthGan, timeGan}, dayGan, "正印"))
                    sb.append("命带<font color='#90EE90'><b>官印相生</b></font>，贵格也，德才兼备。");
                if (isWeak) sb.append("身弱官旺则压力较大，需印星化解。");
                break;
            case "七杀":
                sb.append("七杀格主权威、果断、执行力。宜军警、管理、创业。杀星有制则化权，无制则多波折。");
                if (containsTenGod(new String[]{yearGan, monthGan, timeGan}, dayGan, "食神"))
                    sb.append("命带<font color='#90EE90'><b>食神制杀</b></font>，以智勇取胜，成就非凡。");
                if (isWeak) sb.append("身弱遇杀则挑战重重，需印化杀或食制杀。");
                break;
            case "正财":
                sb.append("正财格主稳定财源、勤劳致富。宜金融、财务、实业经营，守正出奇则财运亨通。");
                if (isStrong) sb.append("身强能担财，财富积累可期。");
                if (isWeak) sb.append("身弱财旺反为财累，需谨慎理财。");
                break;
            case "偏财":
                sb.append("偏财格主意外之财、投资天赋。商业嗅觉灵敏，宜经商、投资、贸易等行业，但需防冒进。");
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
                    sb.append("有<font color='#FF6B6B'>枭神夺食</font>之象，需注意心胸开阔，避免孤僻。");
                break;
            case "食神":
                sb.append("食神格主才华、福气、享乐。多才多艺，宜艺术、餐饮、文化创意行业。心态乐观，一生福泽深厚。");
                if (isStrong) sb.append("食神泄秀，才华横溢，是天然的艺术家。");
                if (isWeak) sb.append("食神泄身太过则精力不足，需节制享乐。");
                break;
            case "伤官":
                sb.append("伤官格主聪明、不拘一格、创造力强。才华外露，宜创新型行业，但锋芒易招是非。");
                if (containsTenGod(new String[]{yearGan, monthGan, timeGan}, dayGan, "正印"))
                    sb.append("命带<font color='#90EE90'><b>伤官佩印</b></font>，才华与智慧并存，极为优秀。");
                if (isWeak) sb.append("伤官泄身过重，需养精蓄锐，莫贪多求快。");
                break;
            case "比肩": case "劫财":
                sb.append("建禄格主自强、独立、实干。靠自身努力打拼，宜自主创业或技术专精。");
                if (isStrong) sb.append("比劫林立，竞争意识强，适合需要拼搏的领域。防刚愎自用。");
                if (isWeak) sb.append("得月令之助，弱中转旺，合作共赢是最佳策略。");
                break;
            default:
                sb.append("格局清正，气场平和，宜顺势而为，发挥自身特长。");
        }

        // 是否破格提示
        sb.append("<br/><font color='#8899AA'>格局评语：</font>");
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

        sb.append("<font color='#FF6B6B'><b>祖上 · 年柱</b></font> ").append(yearGan).append(yearZhi).append("<br/>");
        sb.append("年干").append(yearGan).append("为").append(yShen).append("：").append(getSixRelDesc(yShen, "祖上")).append("<br/>");
        sb.append("年支藏").append(yZhiMainQi).append("(").append(yZhiShen).append(")：").append(getSixRelDesc(yZhiShen, "祖荫")).append("<br/><br/>");

        // 月柱 → 父母/兄弟
        String mShen = getTenGodFull(dayGan, monthGan);
        String mZhiMainQi = getHiddenStems(monthZhi).length > 0 ? getHiddenStems(monthZhi)[0] : monthZhi;
        String mZhiShen = getTenGodFull(dayGan, mZhiMainQi);

        sb.append("<font color='#90EE90'><b>父母 · 月柱</b></font> ").append(monthGan).append(monthZhi).append("<br/>");
        sb.append("月干").append(monthGan).append("为").append(mShen).append("：").append(getSixRelDesc(mShen, "父母")).append("<br/>");
        sb.append("月支藏").append(mZhiMainQi).append("(").append(mZhiShen).append(")：").append(getSixRelDesc(mZhiShen, "手足")).append("<br/><br/>");

        // 日支 → 配偶
        String dZhiMainQi = getHiddenStems(dayZhi).length > 0 ? getHiddenStems(dayZhi)[0] : dayZhi;
        String dZhiShen = getTenGodFull(dayGan, dZhiMainQi);
        String dayZodiac = getZodiacNameFromZhi(dayZhi);

        sb.append("<font color='#FFD700'><b>配偶 · 日支</b></font> ").append(dayZhi).append("<br/>");
        sb.append("配偶宫").append(dayZhi).append("（属").append(dayZodiac).append("）藏").append(dZhiMainQi).append("(").append(dZhiShen).append(")：").append(getSixRelDesc(dZhiShen, "配偶")).append("<br/>");
        sb.append(getSpouseDetail(dayZhi, dZhiShen)).append("<br/><br/>");

        // 时柱 → 子女
        String tShen = getTenGodFull(dayGan, timeGan);
        String tZhiMainQi = getHiddenStems(timeZhi).length > 0 ? getHiddenStems(timeZhi)[0] : timeZhi;
        String tZhiShen = getTenGodFull(dayGan, tZhiMainQi);

        sb.append("<font color='#87CEEB'><b>子女 · 时柱</b></font> ").append(timeGan).append(timeZhi).append("<br/>");
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

        if (hasYinWuXu) { sb.append("🔥 <font color='#FF6B6B'><b>寅午戌三合火局</b></font> — 火气极旺，热情奔放，事业心强<br/>"); hasRel = true; }
        if (hasSiYouChou) { sb.append("⚔ <font color='#C0C0C0'><b>巳酉丑三合金局</b></font> — 金气凝聚，果断刚毅，财运佳<br/>"); hasRel = true; }
        if (hasShenZiChen) { sb.append("💧 <font color='#87CEEB'><b>申子辰三合水局</b></font> — 水气流通，智慧过人，善变通<br/>"); hasRel = true; }
        if (hasHaiMaoWei) { sb.append("🌿 <font color='#90EE90'><b>亥卯未三合木局</b></font> — 木气生发，仁慈宽厚，创造力强<br/>"); hasRel = true; }

        if (hasRel) sb.append("<br/>");

        // 六合与六冲
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                String z1 = zhis[i], z2 = zhis[j];

                // 六合
                for (String[] lh : liuHe) {
                    if (lh[0].equals(z1) && lh[1].equals(z2)) {
                        sb.append("🤝 ").append(labels[i]).append(z1).append("与").append(labels[j]).append(z2).append("<font color='#90EE90'><b>六合</b></font>");
                        sb.append(" — 关系和谐，互相吸引，有天然默契<br/>");
                        hasRel = true;
                    }
                }
                // 六冲
                for (String[] lc : liuChong) {
                    if (lc[0].equals(z1) && lc[1].equals(z2)) {
                        sb.append("⚡ ").append(labels[i]).append(z1).append("与").append(labels[j]).append(z2).append("<font color='#FF6B6B'><b>六冲</b></font>");
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
                        sb.append("⚠ ").append(labels[i]).append("与").append(labels[j]).append("同为").append(z).append("，<font color='#FFA500'><b>自刑</b></font> — 内心纠结，自我矛盾，需豁达<br/>");
                        hasRel = true;
                    }
                }
            }
        }

        if (!hasRel) {
            sb.append("<font color='#8899AA'>四支之间无明显的合冲刑害关系，气场独立平和。各柱各有轨迹，互不干扰，反而利于独立发展。</font>");
        }

        sb.append("<br/><font color='#8899AA'><i>※ 合则融洽助力，冲则动荡变化，刑则纠结烦恼。知晓关系，便能趋吉避凶</i></font>");
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

            sb.append("日柱").append(dayPillar).append("属<font color='#90EE90'>甲").append(XUN_LIST[xunIdx].substring(1)).append("旬</font>，");
            sb.append("空亡在<font color='#FF6B6B'>").append(v1).append("、").append(v2).append("</font>。<br/><br/>");

            // 检查各柱是否落空亡
            String[] zhis = {yearZhi, monthZhi, dayZhiColumn, timeZhi};
            String[] labels = {"年支", "月支", "日支", "时支"};
            String[] descs = {"祖上助力减弱，需自我奋斗", "父母缘分略薄，独立性强", "配偶助力减弱，婚姻需多经营", "子女缘分特殊，晚来得力"};
            boolean anyVoid = false;

            for (int i = 0; i < 4; i++) {
                if (zhis[i].equals(v1) || zhis[i].equals(v2)) {
                    sb.append("<font color='#FFA500'>").append(labels[i]).append(zhis[i]).append("落空亡</font>：").append(descs[i]).append("<br/>");
                    anyVoid = true;
                }
            }

            if (!anyVoid) {
                sb.append("<font color='#90EE90'>四柱地支均未落空亡</font>，命局根基稳固，六亲缘分正常。<br/>");
            }

            sb.append("<br/><font color='#8899AA'>空亡之解：</font>空亡并非坏事，表示该方面较为淡薄或独特。");
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
        sb.append("<font color='#FFD700'><b>").append(gan).append(zhi).append(" · ").append(nayin).append("</b></font>");
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
        sb.append("命宫 <font color='#FFD700'><b>").append(mingGong).append("</b></font>，");
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
        sb.append("<font color='#8899AA'>十神组合：</font>");
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
            combos.add("<font color='#90EE90'><b>官印相生</b></font>（贵气流通，德才兼备）");
        if (hasQiSha && hasShiShen)
            combos.add("<font color='#90EE90'><b>食神制杀</b></font>（以智取胜，化险为夷）");
        if (hasShangGuan && hasZhengYin)
            combos.add("<font color='#90EE90'><b>伤官佩印</b></font>（才华与智慧并重）");
        if (hasShiShen && (hasZhengCai || hasPianCai))
            combos.add("<font color='#90EE90'><b>食神生财</b></font>（才华变现，财源滚滚）");
        if (hasPianYin && hasShiShen)
            combos.add("<font color='#FFA500'><b>枭神夺食</b></font>（思维独特，需防偏执）");
        if (hasZhengGuan && hasQiSha)
            combos.add("<font color='#FFA500'><b>官杀混杂</b></font>（机遇与压力并存）");
        if (hasShangGuan && hasZhengGuan)
            combos.add("<font color='#FF6B6B'><b>伤官见官</b></font>（锋芒毕露，宜低调行事）");
        if (hasJieCai && hasZhengCai)
            combos.add("<font color='#FFA500'><b>劫财夺财</b></font>（理财需谨慎，防破耗）");
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
        sb.append("<font color='#8899AA'>五行补益：</font>");
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
                sb.append("宜<font color='#90EE90'><b>青绿色系</b></font>衣着饰品，");
                sb.append("<font color='#90EE90'><b>东方</b></font>发展或摆放绿植，");
                sb.append("多食绿色蔬果（菠菜、芹菜、绿豆），");
                sb.append("春季是运势旺盛期。");
                break;
            case "火":
                sb.append("宜<font color='#FF6B6B'><b>红紫色系</b></font>衣着饰品，");
                sb.append("<font color='#FF6B6B'><b>南方</b></font>发展或使用暖色灯光，");
                sb.append("多食红色食物（红枣、番茄、红豆），");
                sb.append("夏季是运势旺盛期。");
                break;
            case "土":
                sb.append("宜<font color='#FFD700'><b>黄棕色系</b></font>衣着饰品，");
                sb.append("<font color='#FFD700'><b>中央/本地</b></font>发展稳守，");
                sb.append("多食黄色谷物（小米、玉米、南瓜），");
                sb.append("四季末（辰戌丑未月）运势较旺。");
                break;
            case "金":
                sb.append("宜<font color='#C0C0C0'><b>白金银色系</b></font>衣着饰品，");
                sb.append("<font color='#C0C0C0'><b>西方</b></font>发展或佩戴金属饰品，");
                sb.append("多食白色食物（白萝卜、银耳、百合），");
                sb.append("秋季是运势旺盛期。");
                break;
            case "水":
                sb.append("宜<font color='#87CEEB'><b>黑蓝色系</b></font>衣着饰品，");
                sb.append("<font color='#87CEEB'><b>北方</b></font>发展或摆放水景，");
                sb.append("多食黑色食物（黑豆、黑芝麻、海带），");
                sb.append("冬季是运势旺盛期。");
                break;
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
        sb.append("<font color='#8899AA'>个性详解：</font><br/>");
        switch (wuXing) {
            case "木":
                if (isStrong) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>仁爱正直，有领导力，目标感强，勇于开拓<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>有时固执己见，不听劝告，一根筋走到底");
                } else if (isWeak) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>柔韧随和，善借外力，人缘好，适应力强<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>缺乏主见，优柔寡断，易被他人左右");
                } else {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>仁而有节，刚柔并济，既有原则又知变通<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>中庸之道有时显得缺乏鲜明个性");
                }
                break;
            case "火":
                if (isStrong) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>热情奔放，行动力强，感染力十足，是天生的领袖<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>急躁冲动，三分钟热度，易半途而废");
                } else if (isWeak) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>温暖细腻，善解人意，是优秀的倾听者和支持者<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>缺乏自信，不敢展示才华，容易被人忽略");
                } else {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>热情有度，温暖而不灼人，有始有终<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>有时过于克制，压抑真实情感");
                }
                break;
            case "土":
                if (isStrong) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>诚信可靠，脚踏实地，是团队的中流砥柱<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>保守固执，缺乏变通，遇事反应慢半拍");
                } else if (isWeak) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>敦厚善良，乐于助人，不计较得失<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>易受人利用，缺乏边界感，需学会拒绝");
                } else {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>稳重而不失灵活，可靠又知情趣<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>有时过分求稳，错失良机");
                }
                break;
            case "金":
                if (isStrong) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>刚毅果断，义气重诺，执行力一流<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>锋芒太露，刚极易折，需学会藏锋守拙");
                } else if (isWeak) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>细腻精致，追求品质，审美出众<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>过于计较细节，完美主义导致内耗");
                } else {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>理性与感性兼备，刚柔适度<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>有时过于理性，显得不够热情");
                }
                break;
            case "水":
                if (isStrong) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>智慧超群，灵活善变，总能找到最佳路径<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>思虑过重，易陷入过度分析而行动迟缓");
                } else if (isWeak) {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>直觉敏锐，心思缜密，洞察力过人<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>缺乏安全感，情绪波动大，需增强自信");
                } else {
                    sb.append("<font color='#90EE90'>✓ 优点：</font>聪而不露，心思通透，大智若愚<br/>");
                    sb.append("<font color='#FF6B6B'>⚠ 注意：</font>有时过于低调，才华被埋没");
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
        sb.append("<font color='#8899AA'>发展指南：</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("🏢 宜：教育、文化、出版、园林、医药、环保、设计<br/>");
                isStrong = false; // dummy
                break;
            case "火":
                sb.append("🏢 宜：传媒、演艺、餐饮、能源、互联网、美业、公关<br/>");
                break;
            case "土":
                sb.append("🏢 宜：建筑、地产、金融、仓储、农业、顾问、行政<br/>");
                break;
            case "金":
                sb.append("🏢 宜：法律、金融、科技、机械、管理、审计、珠宝<br/>");
                break;
            case "水":
                sb.append("🏢 宜：商贸、物流、旅游、传媒、咨询、水产、信息<br/>");
                break;
        }
        if (isStrong) {
            sb.append("🎯 <font color='#90EE90'>适合创业或管理岗</font>，能独当一面，建议选择有发展空间的平台。");
        } else if (isWeak) {
            sb.append("🎯 <font color='#87CEEB'>适合专业型或协作型岗位</font>，以技术专精取胜，好团队胜过好平台。");
        } else {
            sb.append("🎯 <font color='#FFD700'>职场适应力强</font>，创业或就业皆宜，关键在于选择自己真正热爱的方向。");
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：理财风格
    // ═══════════════════════════════════
    public static String getWealthStyleDetail(String wuXing, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#8899AA'>理财建议：</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("💡 理财风格：战略性投资，看长期趋势，不贪图快钱。<br/>");
                if (isStrong) sb.append("适合：股权投资、绿色产业、教育培训产业投资。");
                else sb.append("适合：定投基金、储蓄型保险、稳健型理财产品。");
                break;
            case "火":
                sb.append("💡 理财风格：凭直觉行动，行情好时敢于下注。<br/>");
                if (isStrong) sb.append("适合：股票短线、新兴行业投资、品牌加盟。");
                else sb.append("适合：分批买入、避免追高、设置止损线。");
                break;
            case "土":
                sb.append("💡 理财风格：保守稳健，偏好实体资产，不轻信高回报承诺。<br/>");
                if (isStrong) sb.append("适合：房地产、黄金、长期国债、实业投资。");
                else sb.append("适合：定期储蓄、国债逆回购、保守型固收产品。");
                break;
            case "金":
                sb.append("💡 理财风格：精打细算，风险控制意识强。<br/>");
                if (isStrong) sb.append("适合：金融衍生品、量化投资、金属相关产业。");
                else sb.append("适合：分散投资、货币基金、短期理财。");
                break;
            case "水":
                sb.append("💡 理财风格：灵活多变，善于发现套利机会。<br/>");
                if (isStrong) sb.append("适合：跨境电商、物流投资、信息产业创投。");
                else sb.append("适合：流动性强的理财产品，确保随时可变现。");
                break;
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：桃花星分析
    // ═══════════════════════════════════
    public static String getPeachBlossomAnalysis(String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#8899AA'>桃花运：</font>");
        // 子午卯酉为四正桃花
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        String[] labels = {"年", "月", "日", "时"};
        java.util.List<String> peachList = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String z = zhis[i];
            if (z.equals("子") || z.equals("午") || z.equals("卯") || z.equals("酉")) {
                String peachType = "";
                switch (z) {
                    case "子": peachType = "水桃花·灵动机智，异性缘来自智慧魅力"; break;
                    case "午": peachType = "火桃花·热情奔放，异性缘来自阳光自信"; break;
                    case "卯": peachType = "木桃花·温柔典雅，异性缘来自优雅气质"; break;
                    case "酉": peachType = "金桃花·精致靓丽，异性缘来自外在魅力"; break;
                }
                peachList.add(labels[i] + "支" + z + "（" + peachType + "）");
            }
        }
        if (peachList.isEmpty()) {
            sb.append("命局中无子午卯酉四正桃花，感情较为含蓄内敛，桃花运不显但情感真挚。");
        } else {
            sb.append("命带桃花星：").append(String.join("；", peachList));
            sb.append("。<br/>桃花运较旺，异性缘佳，但需分辨是良缘还是烂桃花。");
            // 红鸾天喜简易提示
            String redLuan = getRedLuan(yearZhi);
            if (!redLuan.isEmpty()) sb.append("<br/>红鸾星在").append(redLuan).append("，主正缘婚期，遇之则婚姻可成。");
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
        sb.append("<font color='#8899AA'>天乙贵人：</font>");
        if (found.isEmpty()) {
            sb.append("日主").append(dayGan).append("之天乙贵人为<font color='#FFD700'>").append(guiRenZhis[0]).append("、").append(guiRenZhis[1]).append("</font>，");
            sb.append("命局中未见，贵人多在流年大运中显现。多与属").append(getZodiacNameFromZhi(guiRenZhis[0])).append("、").append(getZodiacNameFromZhi(guiRenZhis[1])).append("之人交往可得贵人助力。");
        } else {
            sb.append("命带<font color='#90EE90'><b>天乙贵人</b></font>（").append(String.join("、", found)).append("），");
            sb.append("天生贵人运旺，遇困难总有贵人相助，逢凶化吉之命。");
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
        sb.append("<font color='#8899AA'>透干分析：</font>");
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
                        sb.append("<font color='#90EE90'><b>透于").append(labels[j]).append("干</b></font>").append(gan).append(" — 藏干发力，隐性特质外显为实际行动<br/>");
                    }
                }
            }
        }
        if (!anyTransparent) {
            sb.append("四柱中藏干未直接透出天干，隐性力量需大运流年引动方显。");
        }
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：用神提示
    // ═══════════════════════════════════
    public static String getYongShenHint(String wuXing, String monthZhi, boolean isStrong, boolean isWeak) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#8899AA'>用神提示：</font>");
        if (isStrong) {
            String xie = getXieHaoWuXing(wuXing);
            String ke = getKeWuXing(wuXing);
            sb.append("身强宜<font color='#90EE90'><b>泄</b></font>（").append(wuXing).append("生").append(xie).append("）或<font color='#90EE90'><b>克</b></font>（").append(ke).append("克").append(wuXing).append("），");
            sb.append("用神取").append(xie).append("、").append(ke).append("。大运逢之则顺风顺水。");
        } else if (isWeak) {
            String sheng = getShengWuXing(wuXing);
            sb.append("身弱宜<font color='#90EE90'><b>生</b></font>（").append(sheng).append("生").append(wuXing).append("）或<font color='#90EE90'><b>扶</b></font>（").append(wuXing).append("帮").append(wuXing).append("），");
            sb.append("用神取").append(sheng).append("、").append(wuXing).append("。大运逢之则得贵人相助。");
        } else {
            sb.append("命局中和，<font color='#FFD700'>顺势而为</font>即是用神，不必刻意补某一五行。");
        }
        // 月令提示
        String monthWuXing = getWuXing(monthZhi);
        sb.append("<br/>月令属").append(monthWuXing).append("，");
        if (monthWuXing.equals(wuXing)) sb.append("得月令之气，先天禀赋深厚。");
        else if (isSheng(monthWuXing, wuXing)) sb.append("月令生扶日主，得时得令。");
        else if (isSheng(wuXing, monthWuXing)) sb.append("日主生月令，泄气需补。");
        else sb.append("月令与日主各有得失。");
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：六害（相害）分析
    // ═══════════════════════════════════
    public static String getXiangHaiAnalysis(String yearZhi, String monthZhi, String dayZhi, String timeZhi) {
        StringBuilder sb = new StringBuilder();
        String[][] xiangHai = {{"子","未"},{"未","子"},{"丑","午"},{"午","丑"},{"寅","巳"},{"巳","寅"},
                                {"卯","辰"},{"辰","卯"},{"申","亥"},{"亥","申"},{"酉","戌"},{"戌","酉"}};
        String[][] haiDesc = {{"子","未","子未相害·六亲缘薄，需多沟通增进感情"},
                              {"丑","午","丑午相害·脾气不合，需相互包容忍让"},
                              {"寅","巳","寅巳相害·暗中较劲，防小人暗算"},
                              {"卯","辰","卯辰相害·亲友失和，需多一份宽容"},
                              {"申","亥","申亥相害·沟通不畅，宜开诚布公"},
                              {"酉","戌","酉戌相害·口舌是非，宜谨言慎行"}};
        String[] zhis = {yearZhi, monthZhi, dayZhi, timeZhi};
        String[] labels = {"年支", "月支", "日支", "时支"};
        boolean hasHai = false;
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                for (String[] hd : haiDesc) {
                    if ((zhis[i].equals(hd[0]) && zhis[j].equals(hd[1])) ||
                        (zhis[i].equals(hd[1]) && zhis[j].equals(hd[0]))) {
                        if (!hasHai) sb.append("<font color='#8899AA'>相害关系：</font>");
                        hasHai = true;
                        sb.append("<br/>⚠ ").append(labels[i]).append(zhis[i]).append("与").append(labels[j]).append(zhis[j]).append("：");
                        sb.append("<font color='#FFA500'>").append(hd[2]).append("</font>");
                    }
                }
            }
        }
        if (!hasHai) sb.append("<font color='#8899AA'>四支无相害</font>，人际关系较为和谐。");
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
                        if (!hasHalf) sb.append("<font color='#8899AA'>半合/拱局：</font>");
                        hasHalf = true;
                        sb.append("<br/>").append(labels[i]).append(zhis[i]).append("与").append(labels[j]).append(zhis[j]);
                        sb.append(" <font color='#87CEEB'>").append(hh[0][2]).append("</font>");
                    }
                }
            }
        }
        if (!hasHalf) sb.append("<font color='#8899AA'>四支无半合</font>，气场独立完整。");
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：开运方法
    // ═══════════════════════════════════
    public static String getKaiYunAdvice(String wuXing, String zodiac) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#8899AA'>开运锦囊：</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("🎨 幸运色：<font color='#90EE90'>青色、绿色</font>　🔢 幸运数：3、8<br/>");
                sb.append("💎 佩戴：绿松石、翡翠、木质饰品<br/>");
                sb.append("📅 旺月：春季（寅卯辰月）<br/>");
                break;
            case "火":
                sb.append("🎨 幸运色：<font color='#FF6B6B'>红色、紫色</font>　🔢 幸运数：2、7<br/>");
                sb.append("💎 佩戴：红玛瑙、紫水晶、红宝石<br/>");
                sb.append("📅 旺月：夏季（巳午未月）<br/>");
                break;
            case "土":
                sb.append("🎨 幸运色：<font color='#FFD700'>黄色、棕色</font>　🔢 幸运数：5、0<br/>");
                sb.append("💎 佩戴：黄水晶、蜜蜡、陶瓷饰品<br/>");
                sb.append("📅 旺月：四季末（辰未戌丑月）<br/>");
                break;
            case "金":
                sb.append("🎨 幸运色：<font color='#C0C0C0'>白色、银色</font>　🔢 幸运数：4、9<br/>");
                sb.append("💎 佩戴：白水晶、银饰、白金饰品<br/>");
                sb.append("📅 旺月：秋季（申酉戌月）<br/>");
                break;
            case "水":
                sb.append("🎨 幸运色：<font color='#87CEEB'>黑色、蓝色</font>　🔢 幸运数：1、6<br/>");
                sb.append("💎 佩戴：黑曜石、海蓝宝、黑水晶<br/>");
                sb.append("📅 旺月：冬季（亥子丑月）<br/>");
                break;
        }
        sb.append("🌅 每日宜：保持心平气和，顺应自然节奏。<br/>");
        String[] compat = getZodiacCompat(zodiac);
        sb.append("🤝 贵人属相：").append(compat[0]).append("、").append(compat[1]).append("、").append(compat[2]);
        return sb.toString();
    }

    // ═══════════════════════════════════
    // 新增：四季养生
    // ═══════════════════════════════════
    public static String getSeasonHealthDetail(String wuXing) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#8899AA'>四季养生要点：</font><br/>");
        switch (wuXing) {
            case "木":
                sb.append("🌸 <font color='#90EE90'>春：</font>养肝护胆，早睡早起，多拉伸舒展<br/>");
                sb.append("☀ 夏：防肝火过旺，少怒少酒，多饮菊花茶<br/>");
                sb.append("🍂 秋：肺金克木，注意呼吸道保养<br/>");
                sb.append("❄ 冬：藏精养肝，早卧晚起，少熬夜");
                break;
            case "火":
                sb.append("🌸 春：木生火旺，宜多运动散发能量<br/>");
                sb.append("☀ <font color='#FF6B6B'>夏：</font>养心安神，午休为要，忌暴怒过劳<br/>");
                sb.append("🍂 秋：火气收敛，注意情绪调节<br/>");
                sb.append("❄ 冬：水克火，注意保暖护心脑血管");
                break;
            case "土":
                sb.append("🌸 春：木克土，注意脾胃调理<br/>");
                sb.append("☀ 夏：火生土，消化力强，但仍需规律饮食<br/>");
                sb.append("🍂 <font color='#FFD700'>秋：</font>土生金泄气，宜进补养胃<br/>");
                sb.append("❄ 冬：脾胃为后天之本，四季皆需温养");
                break;
            case "金":
                sb.append("🌸 春：金克木劳神，注意休息<br/>");
                sb.append("☀ 夏：火克金，防呼吸道感染<br/>");
                sb.append("🍂 <font color='#C0C0C0'>秋：</font>养肺润燥，多食白色食物，深呼吸吐纳<br/>");
                sb.append("❄ 冬：金生水泄气，注意保暖防寒");
                break;
            case "水":
                sb.append("🌸 春：水生木泄气，适当进补<br/>");
                sb.append("☀ 夏：水克火耗神，注意补水休息<br/>");
                sb.append("🍂 秋：金生水得助，是进补好时机<br/>");
                sb.append("❄ <font color='#87CEEB'>冬：</font>养肾固元，注意腰膝保暖，宜食黑色食物");
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
        sb.append(gan).append(zhi).append("｜干十神：<font color='#90EE90'><b>").append(tenGod).append("</b></font>");
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
            levelColor = "#FFD700";
            levelDesc = "命局优良，五行流通有情，格局层次较高。建议珍惜天赋，把握机遇，必有大成。";
        } else if (score >= 60) {
            level = "中上";
            levelColor = "#90EE90";
            levelDesc = "命局较好，有明确的优势领域。扬长避短，持续深耕，循序渐进可达中上之局。";
        } else if (score >= 45) {
            level = "中等";
            levelColor = "#87CEEB";
            levelDesc = "命局中等，有优点也有不足。关键在于后天努力和方向选择，勤能补拙，后来居上可期。";
        } else if (score >= 30) {
            level = "中下";
            levelColor = "#FFA500";
            levelDesc = "命局略显不足，有需要补足之处。建议借助外力（贵人、学习、团队），避重就轻方为上策。";
        } else {
            level = "下等";
            levelColor = "#FF6B6B";
            levelDesc = "命局较弱，但不代表命运不好。英雄不问出处，逆境出豪杰，后天努力可突破先天限制。";
        }

        sb.append("<font color='").append(levelColor).append("'><b>").append(level).append(" · 综合得分 ").append(score).append("/100</b></font><br/><br/>");

        // 评分明细
        sb.append("<font color='#8899AA'>评分明细：</font><br/>");
        sb.append("五行力量 ").append(totalSupport > keCount ? "均衡" : "偏颇").append(" · ");
        sb.append("月令 ").append(monthWuXing.equals(dayGanWuXing) ? "得令+" : "一般").append(" · ");
        sb.append("十神 ").append(completeness >= 3 ? "齐全" : completeness >= 2 ? "较全" : "偏少").append(" · ");
        sb.append("合冲 ").append(hasZhis(zhis, new String[]{"寅","午","戌"}) || hasZhis(zhis, new String[]{"巳","酉","丑"}) ? "有三合" : "无三合");
        sb.append("<br/><br/>");

        sb.append("<font color='#FFD700'><b>总评：</b></font>").append(levelDesc);

        return sb.toString();
    }
}
