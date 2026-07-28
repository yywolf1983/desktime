package com.example.timedisplay;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class DestinyActivity extends android.app.Activity {

    // 竖屏
    private TextView yearPillarView, monthPillarView, dayPillarView, timePillarView;
    private TextView dayMasterLabel, dayMasterWuXingLabel, dayMasterIcon, dayBranchLabel, dayBranchZodiac, dayBranchIcon;
    private TextView zodiacLabel, zodiacYearLabel, zodiacIcon, strengthTag, strengthHint, dayDescText;
    private TextView nayinYearLabel, nayinMonthLabel, nayinDayLabel, nayinTimeLabel;
    private TextView nayinText, mingGongValue, mingGongDesc;
    private TextView changshengDayLabel, changshengMonthLabel, changshengTimeLabel, changshengText;
    private TextView tenGodsText, wuxingPowerText, xijiText;
    private TextView personalityText, careerText, wealthText, relationshipText, healthText, socialText;
    private TextView pillarsDetailText, lifeAdviceText;
    private TextView yearWuXing, monthWuXing, dayWuXing, timeWuXing;
    private TextView hiddenStemsText, patternText, sixRelText, branchRelText, levelText;

    // 横屏
    private TextView fourPillarsLabelL, dayMasterLabelL, dayDescTextL, strengthTagL;
    private TextView zodiacLabelL, nayinLabelL, dayZhiLabelL, changshengLabelL, wuxingSumL;

    private String yearPillar, monthPillar, dayPillar, timePillar;
    private String yearGan, yearZhi, monthGan, monthZhi, dayGan, dayZhi, timeGan, timeZhi;
    private String dayGanWuXing;
    private String[][] pillars;
    private String yGanShen, mGanShen, tGanShen;
    private int shengCount, keCount, biCount;
    private boolean isStrong, isWeak, isBalance;
    // 生肖缓存（一次计算，多处复用，避免重复调用）
    private String zodiac, dayZodiac, zodiacEmoji, dayZodiacEmoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isRotationLocked = prefs.getBoolean("rotationLocked", false);
        int lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(lockedOrientation);
        }

        setContentView(R.layout.activity_destiny);

        // 获取四柱
        yearPillar = defaultIfNull(getIntent().getStringExtra("yearPillar"), "甲子");
        monthPillar = defaultIfNull(getIntent().getStringExtra("monthPillar"), "甲子");
        dayPillar = defaultIfNull(getIntent().getStringExtra("dayPillar"), "甲子");
        timePillar = defaultIfNull(getIntent().getStringExtra("timePillar"), "甲子");

        yearGan = yearPillar.substring(0, 1);
        yearZhi = yearPillar.substring(1, 2);
        monthGan = monthPillar.substring(0, 1);
        monthZhi = monthPillar.substring(1, 2);
        dayGan = dayPillar.substring(0, 1);
        dayZhi = dayPillar.substring(1, 2);
        timeGan = timePillar.substring(0, 1);
        timeZhi = timePillar.substring(1, 2);

        dayGanWuXing = DestinyCalculator.getWuXing(dayGan);
        pillars = new String[][]{{yearGan, yearZhi}, {monthGan, monthZhi}, {dayGan, dayZhi}, {timeGan, timeZhi}};

        // 计算身强身弱
        calcStrength();

        // 十神
        yGanShen = DestinyCalculator.getTenGodFull(dayGan, yearGan);
        mGanShen = DestinyCalculator.getTenGodFull(dayGan, monthGan);
        tGanShen = DestinyCalculator.getTenGodFull(dayGan, timeGan);

        // 初始化视图并填充数据
        initViews();
        populateAll();
    }

    private String defaultIfNull(String s, String def) {
        return s != null ? s : def;
    }

    private void calcStrength() {
        shengCount = 0;
        keCount = 0;
        biCount = 0;
        for (int i = 0; i < pillars.length; i++) {
            String pGanWuXing = DestinyCalculator.getWuXing(pillars[i][0]);
            String pZhiWuXing = DestinyCalculator.getWuXing(pillars[i][1]);
            if (pGanWuXing.equals(dayGanWuXing)) { if (i != 2) biCount++; }
            else if (DestinyCalculator.isSheng(pGanWuXing, dayGanWuXing)) shengCount++;
            else keCount++;

            if (i == 2) {
                if (pZhiWuXing.equals(dayGanWuXing)) { /* 日支不算 */ }
                else if (DestinyCalculator.isSheng(pZhiWuXing, dayGanWuXing)) shengCount++;
                else keCount++;
            }
        }
        // 加权评分：月令（得令/失令）权重最高，地支通根次之；个数法仅用于上方展示
        int score = shengCount + biCount - keCount;
        String monthZhiWx = DestinyCalculator.getWuXing(monthZhi);
        if (monthZhiWx.equals(dayGanWuXing)) score += 3;                  // 月令比和，得令
        else if (DestinyCalculator.isSheng(monthZhiWx, dayGanWuXing)) score += 3; // 月令生我
        else if (DestinyCalculator.isSheng(dayGanWuXing, monthZhiWx)) score -= 2; // 我生月令（泄）
        else if (DestinyCalculator.isKe(monthZhiWx, dayGanWuXing)) score -= 3;    // 月令克我
        else score -= 1;                                                  // 我克月令
        // 地支通根：年/时支有日主同五行之根
        for (int i = 0; i < pillars.length; i++) {
            if (i == 2) continue;
            String pZhiWx = DestinyCalculator.getWuXing(pillars[i][1]);
            if (pZhiWx.equals(dayGanWuXing)) score += 1;
        }
        isStrong = (score >= 4);
        isWeak = (score <= -2);
        isBalance = (!isStrong && !isWeak);
    }

    private void initViews() {
        // 竖屏视图
        yearPillarView = findViewById(R.id.destinyYearPillar);
        monthPillarView = findViewById(R.id.destinyMonthPillar);
        dayPillarView = findViewById(R.id.destinyDayPillar);
        timePillarView = findViewById(R.id.destinyTimePillar);
        dayMasterLabel = findViewById(R.id.dayMasterLabel);
        dayMasterWuXingLabel = findViewById(R.id.dayMasterWuXingLabel);
        dayMasterIcon = findViewById(R.id.dayMasterIcon);
        dayBranchLabel = findViewById(R.id.dayBranchLabel);
        dayBranchZodiac = findViewById(R.id.dayBranchZodiac);
        dayBranchIcon = findViewById(R.id.dayBranchIcon);
        zodiacLabel = findViewById(R.id.zodiacLabel);
        zodiacYearLabel = findViewById(R.id.zodiacYearLabel);
        zodiacIcon = findViewById(R.id.zodiacIcon);
        strengthTag = findViewById(R.id.strengthTag);
        strengthHint = findViewById(R.id.strengthHint);
        dayDescText = findViewById(R.id.dayDescText);
        nayinYearLabel = findViewById(R.id.nayinYearLabel);
        nayinMonthLabel = findViewById(R.id.nayinMonthLabel);
        nayinDayLabel = findViewById(R.id.nayinDayLabel);
        nayinTimeLabel = findViewById(R.id.nayinTimeLabel);
        nayinText = findViewById(R.id.nayinText);
        mingGongValue = findViewById(R.id.mingGongValue);
        mingGongDesc = findViewById(R.id.mingGongDesc);
        changshengDayLabel = findViewById(R.id.changshengDayLabel);
        changshengMonthLabel = findViewById(R.id.changshengMonthLabel);
        changshengTimeLabel = findViewById(R.id.changshengTimeLabel);
        changshengText = findViewById(R.id.changshengText);
        tenGodsText = findViewById(R.id.tenGodsText);
        wuxingPowerText = findViewById(R.id.wuxingPowerText);
        xijiText = findViewById(R.id.xijiText);
        personalityText = findViewById(R.id.personalityText);
        careerText = findViewById(R.id.careerText);
        wealthText = findViewById(R.id.wealthText);
        relationshipText = findViewById(R.id.relationshipText);
        healthText = findViewById(R.id.healthText);
        socialText = findViewById(R.id.socialText);
        pillarsDetailText = findViewById(R.id.pillarsDetailText);
        lifeAdviceText = findViewById(R.id.lifeAdviceText);
        yearWuXing = findViewById(R.id.yearWuXing);
        monthWuXing = findViewById(R.id.monthWuXing);
        dayWuXing = findViewById(R.id.dayWuXing);
        timeWuXing = findViewById(R.id.timeWuXing);

        hiddenStemsText = findViewById(R.id.hiddenStemsText);
        patternText = findViewById(R.id.patternText);
        sixRelText = findViewById(R.id.sixRelText);
        branchRelText = findViewById(R.id.branchRelText);
        levelText = findViewById(R.id.levelText);

        // 横屏视图
        fourPillarsLabelL = findViewById(R.id.fourPillarsLabel);
        dayMasterLabelL = findViewById(R.id.dayMasterLabelL);
        dayDescTextL = findViewById(R.id.dayDescTextL);
        strengthTagL = findViewById(R.id.strengthTagL);
        zodiacLabelL = findViewById(R.id.zodiacLabelL);
        nayinLabelL = findViewById(R.id.nayinLabelL);
        dayZhiLabelL = findViewById(R.id.dayZhiLabelL);
        changshengLabelL = findViewById(R.id.changshengLabelL);
        wuxingSumL = findViewById(R.id.wuxingSumL);
    }

    private void populateAll() {
        // 生肖缓存（一次计算，多处复用）
        zodiac = DestinyCalculator.getZodiacNameFromZhi(yearZhi);
        dayZodiac = DestinyCalculator.getZodiacNameFromZhi(dayZhi);
        zodiacEmoji = DestinyCalculator.getZodiacEmoji(yearZhi);
        dayZodiacEmoji = DestinyCalculator.getZodiacEmoji(dayZhi);

        populatePillars();
        populateDayMaster();
        populateDayBranch();
        populateZodiac();
        populateStrength();
        populateDayDesc();
        populateNayin();
        populateMingGong();
        populateChangsheng();
        populateTenGods();
        populateWuxingPower();
        populatePersonality();
        populateCareer();
        populateWealth();
        populateRelationship();
        populateHealth();
        populateSocial();
        populatePillarsDetail();
        populateHiddenStems();
        populatePattern();
        populateSixRelatives();
        populateBranchRelations();
        populateLifeAdvice();
        populateLevelAssessment();
    }

    private void populatePillars() {
        setText(yearPillarView, yearPillar);
        setText(monthPillarView, monthPillar);
        setText(dayPillarView, dayPillar);
        setText(timePillarView, timePillar);

        if (fourPillarsLabelL != null) {
            fourPillarsLabelL.setText(yearPillar + "　" + monthPillar + "　" + dayPillar + "　" + timePillar);
        }

        // 五行标签
        setText(yearWuXing, getGanZhiWuXing(yearGan, yearZhi));
        setText(monthWuXing, getGanZhiWuXing(monthGan, monthZhi));
        setText(dayWuXing, getGanZhiWuXing(dayGan, dayZhi));
        setText(timeWuXing, getGanZhiWuXing(timeGan, timeZhi));
    }

    private String getGanZhiWuXing(String gan, String zhi) {
        String gwx = DestinyCalculator.getWuXing(gan);
        String zwx = DestinyCalculator.getWuXing(zhi);
        return gan + gwx + " " + zhi + zwx;
    }

    // ──── populateCore 拆分（8 个子方法）────

    private void populateDayMaster() {
        String wxEmoji = getWuXingEmoji(dayGanWuXing);
        setText(dayMasterLabel, dayGan);
        setText(dayMasterWuXingLabel, dayGan + " · " + dayGanWuXing);
        if (dayMasterIcon != null) dayMasterIcon.setText(wxEmoji);
    }

    private void populateDayBranch() {
        // 日支 + 生肖
        setHtmlText(dayBranchLabel, "<font color='#E6C46A'><b>" + dayZhi + "</b></font>");
        setText(dayBranchZodiac, dayZodiac);
        if (dayBranchIcon != null) dayBranchIcon.setText(dayZodiacEmoji);
        setText(dayZhiLabelL, dayZhi + "（" + dayZodiac + "）");
    }

    private void populateZodiac() {
        // 生肖
        setText(zodiacLabel, zodiac);
        setText(zodiacYearLabel, "年支 " + yearZhi);
        if (zodiacIcon != null) zodiacIcon.setText(zodiacEmoji);
        setText(zodiacLabelL, zodiac);
    }

    private void populateStrength() {
        String strengthStr;
        int strengthColor;
        String strengthHintStr;
        if (isStrong) {
            strengthStr = "身强";
            strengthColor = Color.parseColor("#E0593B");
            strengthHintStr = "生扶" + (shengCount + biCount) + "·克泄" + keCount + " → 宜泄耗";
        } else if (isWeak) {
            strengthStr = "身弱";
            strengthColor = Color.parseColor("#3E87C2");
            strengthHintStr = "生扶" + (shengCount + biCount) + "·克泄" + keCount + " → 宜生扶";
        } else {
            strengthStr = "中和";
            strengthColor = Color.parseColor("#E6C46A");
            strengthHintStr = "生扶" + (shengCount + biCount) + "·克泄" + keCount + " → 均衡";
        }
        setHtmlText(strengthTag, colorSpan(strengthStr, strengthColor));
        setText(strengthHint, strengthHintStr);
        setHtmlText(strengthTagL, colorSpan(strengthStr, strengthColor));
    }

    private void populateDayDesc() {
        // 日干详细解读（五行属性+象征详述在此展示）
        String ganDesc = DestinyCalculator.getGanDescription(dayGan);
        String ganDetail = DestinyCalculator.getRiGanDetailedAnalysis(dayGan);
        String wxEmoji = getWuXingEmoji(dayGanWuXing);
        setHtmlText(dayDescText, "<font color='#E6C46A'><b>" + wxEmoji + " " + dayGan + "日主 · " + ganDesc + "</b></font><br/>"
                + "<font color='#7C8C9C'>" + ganDetail + "</font>");
        setText(dayDescTextL, ganDesc);
    }

    private void populateNayin() {
        // 纳音四柱 Grid（循环化）
        String[] gzArr = {yearGan + yearZhi, monthGan + monthZhi, dayGan + dayZhi, timeGan + timeZhi};
        String[] nayinArr = {
            DestinyCalculator.getNayin(yearGan, yearZhi),
            DestinyCalculator.getNayin(monthGan, monthZhi),
            DestinyCalculator.getNayin(dayGan, dayZhi),
            DestinyCalculator.getNayin(timeGan, timeZhi)
        };
        TextView[] nayinLabels = {nayinYearLabel, nayinMonthLabel, nayinDayLabel, nayinTimeLabel};
        for (int i = 0; i < 4; i++) {
            setHtmlText(nayinLabels[i], "<font color='#7C8C9C'>" + gzArr[i] + "</font><br/>"
                    + "<font color='#E6C46A'>" + nayinArr[i] + "</font>");
        }

        // 日柱纳音详细解读（不复述 nayinDayLabel 已显示的纳音名，直接给含义+象征）
        String nDay = nayinArr[2];
        String nayinExpl = DestinyCalculator.getNayinExplanation(nDay);
        setHtmlText(nayinText, "日柱纳音解读：<br/>" + (nayinExpl.isEmpty() ? nDay : nayinExpl));
        setText(nayinLabelL, nDay);

        // 五行小结（横屏用，身强弱判据已多处展示，此处仅留五行+纳音）
        setText(wuxingSumL, dayGanWuXing + " · " + nDay);
    }

    private void populateMingGong() {
        // 命宫（单套解读，融合通俗释义，删除同义叠加段）
        String mingGongFull = DestinyCalculator.getMingGong(yearGan, monthZhi, timeZhi);
        int commaIdx = mingGongFull.indexOf("，");
        String mingGongShort = commaIdx > 0 ? mingGongFull.substring(0, commaIdx) : mingGongFull;
        String mingGongDetail = commaIdx > 0 ? mingGongFull.substring(commaIdx + 1) : "";
        setHtmlText(mingGongValue, mingGongShort);
        setHtmlText(mingGongDesc, mingGongDetail);
    }

    private void populateChangsheng() {
        // 十二长生（三柱：日/月/时）
        String stageDay = DestinyCalculator.getTwelveStage(dayGan, dayZhi);
        String stageMonth = DestinyCalculator.getTwelveStage(dayGan, monthZhi);
        String stageTime = DestinyCalculator.getTwelveStage(dayGan, timeZhi);

        String[] csStages = {stageDay, stageMonth, stageTime};
        String[] csColors = {getStageColor(stageDay), getStageColor(stageMonth), getStageColor(stageTime)};
        String[] csNames = {"日支", "月令", "时支"};
        TextView[] csLabels = {changshengDayLabel, changshengMonthLabel, changshengTimeLabel};

        for (int i = 0; i < 3; i++) {
            setHtmlText(csLabels[i], "<font color='#7C8C9C'><small>" + csNames[i] + "</small></font><br/>"
                    + "<font color='" + csColors[i] + "'><b>" + csStages[i] + "</b></font>");
        }

        // 长生解读（仅展示解释，不复述阶段名，颜色与上方标签呼应）
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (i > 0) sb.append("<br/>");
            sb.append(csNames[i]).append("：<font color='").append(csColors[i]).append("'>")
              .append(DestinyCalculator.getTwelveStageExplanation(csStages[i])).append("</font>");
        }
        setHtmlText(changshengText, sb.toString());
        setText(changshengLabelL, stageDay);
    }

    // ──── 五行 emoji/颜色映射（静态统一）────

    private static String getWuXingColor(String wx) {
        switch (wx) {
            case "木": return "#3FA34D";
            case "火": return "#E0593B";
            case "土": return "#D9A441";
            case "金": return "#9AA7B8";
            case "水": return "#3E87C2";
            default: return "#D9A441";
        }
    }

    private static String getWuXingEmoji(String wx) {
        switch (wx) {
            case "木": return "🌿";
            case "火": return "🔥";
            case "土": return "🗻";
            case "金": return "⚔️";
            case "水": return "💧";
            default: return "☯";
        }
    }

    private String getStageColor(String stage) {
        if (stage.equals("长生") || stage.equals("冠带") || stage.equals("临官") || stage.equals("帝旺"))
            return "#3FA34D"; // 旺相 - 木绿
        if (stage.equals("沐浴") || stage.equals("衰"))
            return "#E6C46A"; // 中和 - 金
        if (stage.equals("病") || stage.equals("死") || stage.equals("墓") || stage.equals("绝"))
            return "#E0593B"; // 衰 - 火红
        return "#3E87C2"; // 胎养 - 水蓝
    }

    private void populateTenGods() {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#9AA7B8'><small>十神＝日主与其他干支的十种关系，通俗讲就是「你与外界（亲人、事业、钱财）的互动模式」。</small></font><br/><br/>");
        sb.append("年干 ").append(yearGan).append("：").append(coloredText(yGanShen, "#3FA34D")).append("　");
        sb.append(DestinyCalculator.getTenGodExplanation(yGanShen)).append("<br/>");
        sb.append("月干 ").append(monthGan).append("：").append(coloredText(mGanShen, "#3FA34D")).append("　");
        sb.append(DestinyCalculator.getTenGodExplanation(mGanShen)).append("<br/>");
        sb.append("日干 ").append(dayGan).append("：").append(coloredText("日元", "#E6C46A")).append("　命主自身<br/>");
        sb.append("时干 ").append(timeGan).append("：").append(coloredText(tGanShen, "#3FA34D")).append("　");
        sb.append(DestinyCalculator.getTenGodExplanation(tGanShen)).append("<br/><br/>");
        sb.append(DestinyCalculator.getTenGodComboAnalysis(dayGan, yearGan, monthGan, timeGan));
        setHtmlText(tenGodsText, sb.toString());
    }

    private void populateWuxingPower() {
        // 五行力量分布（颜色/emoji 统一引用静态映射，避免重复定义）
        int[] counts = new int[]{0, 0, 0, 0, 0}; // 木火土金水
        String[] names = {"木", "火", "土", "金", "水"};

        for (int i = 0; i < pillars.length; i++) {
            for (int j = 0; j < 2; j++) {
                String w = DestinyCalculator.getWuXing(pillars[i][j]);
                for (int k = 0; k < names.length; k++) {
                    if (names[k].equals(w)) counts[k]++;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        int maxCount = 1;
        for (int c : counts) if (c > maxCount) maxCount = c;

        for (int i = 0; i < 5; i++) {
            sb.append(getWuXingEmoji(names[i])).append(" <font color='").append(getWuXingColor(names[i]))
              .append("'><b>").append(names[i]).append("</b></font>  ");
            int bars = (int)((float)counts[i] / maxCount * 10);
            for (int j = 0; j < bars; j++) sb.append("█");
            sb.append(" ").append(counts[i]);
            if (names[i].equals(dayGanWuXing)) sb.append(" <font color='#E6C46A'><b>★日主</b></font>");
            sb.append("<br/>");
        }

        sb.append("<br/>生扶: ").append(shengCount + biCount).append("　克泄: ").append(keCount).append("　");
        if (isStrong) sb.append(coloredText("→ 身强", "#E0593B"));
        else if (isWeak) sb.append(coloredText("→ 身弱", "#3E87C2"));
        else sb.append(coloredText("→ 中和", "#E6C46A"));
        sb.append("<br/><font color='#9AA7B8'><small>通俗说：身强＝自身能量足，宜克泄耗；身弱＝底气不足，宜生扶比助；中和＝顺其自然最好。</small></font>");

        setHtmlText(wuxingPowerText, sb.toString());

        // 喜用神
        String xijiStr = DestinyCalculator.getFiveElementXiJiDetailed(dayGan, dayGanWuXing, yearGan, yearZhi, monthGan, monthZhi, dayZhi, timeGan, timeZhi);
        xijiStr += "<br/><br/>" + DestinyCalculator.getWuxingSupplementRich(dayGanWuXing, isStrong, isWeak);
        setHtmlText(xijiText, xijiStr);
    }

    private void populatePersonality() {
        String pers = DestinyCalculator.getPersonalityRich(dayGan, dayZhi, dayGanWuXing, isStrong, isWeak, isBalance);
        setHtmlText(personalityText, pers);
    }

    private void populateCareer() {
        String career = DestinyCalculator.getCareerAnalysisRich(dayGan, dayGanWuXing, monthGan, monthZhi, mGanShen, isStrong, isWeak);
        setHtmlText(careerText, career);
    }

    private void populateWealth() {
        String wealth = DestinyCalculator.getWealthAnalysisRich(dayGan, dayGanWuXing, pillars, isStrong, isWeak);
        setHtmlText(wealthText, wealth);
    }

    private void populateRelationship() {
        String dayZhiWuXing = DestinyCalculator.getWuXing(dayZhi);
        String rel = DestinyCalculator.getRelationshipAnalysisRich(dayGan, dayZhi, dayGanWuXing, dayZhiWuXing, dayZodiac, yearZhi, monthZhi, timeZhi);
        setHtmlText(relationshipText, rel);
    }

    private void populateHealth() {
        String health = DestinyCalculator.getHealthAnalysisRich(dayGanWuXing, shengCount, keCount, biCount);
        setHtmlText(healthText, health);
    }

    private void populateSocial() {
        String social = DestinyCalculator.getSocialAnalysisRich(dayGan, dayGanWuXing, yGanShen, mGanShen, tGanShen, yearGan, zodiac, isStrong, isWeak, yearZhi, monthZhi, dayZhi, timeZhi);
        setHtmlText(socialText, social);
    }

    private void populatePillarsDetail() {
        // 四柱详断（循环化，结构相同仅参数不同）
        String[] names = {"年", "月", "日", "时"};
        String[] pillarStrs = {yearPillar, monthPillar, dayPillar, timePillar};
        String[] meanings = {
            DestinyCalculator.getYearPillarMeaning(yearGan, yearZhi),
            DestinyCalculator.getMonthPillarMeaning(monthGan, monthZhi),
            DestinyCalculator.getDayPillarMeaning(dayGan, dayZhi),
            DestinyCalculator.getTimePillarMeaning(timeGan, timeZhi)
        };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append("<font color='#E6C46A'><b>").append(names[i]).append("柱</b></font> ").append(pillarStrs[i]).append("<br/>");
            sb.append(meanings[i]).append("<br/>");
            sb.append("<font color='#7C8C9C'>").append(DestinyCalculator.getPillarRichDetail(dayGan, pillars[i][0], pillars[i][1], names[i])).append("</font>");
            if (i < 3) sb.append("<br/><br/>");
        }
        setHtmlText(pillarsDetailText, sb.toString());
    }

    private void populateLifeAdvice() {
        String shengHao = DestinyCalculator.getShengWuXing(dayGanWuXing);
        String xieHao = getXieHao();
        String advice = DestinyCalculator.getLifeAdviceRich(dayGan, dayGanWuXing, zodiac, shengHao, xieHao, isStrong, isWeak, isBalance);
        setHtmlText(lifeAdviceText, advice);
    }

    private void populateHiddenStems() {
        String hs = DestinyCalculator.getHiddenStemsRich(dayGan, yearGan, monthGan, timeGan, yearZhi, monthZhi, dayZhi, timeZhi);
        setHtmlText(hiddenStemsText, hs);
    }

    private void populatePattern() {
        String pattern = DestinyCalculator.getPatternAnalysis(dayGan, monthZhi, yearGan, monthGan, timeGan, isStrong, isWeak);
        setHtmlText(patternText, pattern);
    }

    private void populateSixRelatives() {
        String sixRel = DestinyCalculator.getSixRelativesRich(dayGan, yearGan, yearZhi, monthGan, monthZhi, dayZhi, timeGan, timeZhi);
        setHtmlText(sixRelText, sixRel);
    }

    private void populateBranchRelations() {
        String br = DestinyCalculator.getBranchRelationsRich(yearZhi, monthZhi, dayZhi, timeZhi);
        setHtmlText(branchRelText, br);
    }

    private void populateLevelAssessment() {
        String level = DestinyCalculator.getLevelAssessmentRich(dayGan, dayGanWuXing, monthZhi,
                shengCount, keCount, biCount, yearGan, monthGan, timeGan, yearZhi, dayZhi, timeZhi);
        setHtmlText(levelText, level);
    }

    private String getXieHao() {
        switch (dayGanWuXing) {
            case "木": return "火";
            case "火": return "土";
            case "土": return "金";
            case "金": return "水";
            case "水": return "木";
            default: return "土";
        }
    }

    // ──── 工具方法 ────

    private void setText(TextView tv, String text) {
        if (tv != null) tv.setText(text);
    }

    private void setHtmlText(TextView tv, String html) {
        if (tv != null) {
            tv.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        }
    }

    private String colorSpan(String text, int color) {
        return "<font color='" + String.format("#%06X", 0xFFFFFF & color) + "'>" + text + "</font>";
    }

    private String coloredText(String text, String color) {
        return "<font color='" + color + "'><b>" + text + "</b></font>";
    }
}
