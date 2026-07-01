package com.example.timedisplay;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
        isStrong = (shengCount + biCount > keCount);
        isWeak = (keCount > shengCount + biCount);
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
        populatePillars();
        populateCore();
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

    private void populateCore() {
        // 日主大字
        setText(dayMasterLabel, dayGan);
        setText(dayMasterLabelL, dayGan + " · " + dayGanWuXing);

        // 日主五行标签
        String wxColor = getWuXingColor(dayGanWuXing);
        String wxEmoji = getWuXingEmoji(dayGanWuXing);
        setHtmlText(dayMasterWuXingLabel, colorSpan(dayGanWuXing, Color.parseColor(wxColor)));
        if (dayMasterIcon != null) dayMasterIcon.setText(wxEmoji);

        // 日支 + 生肖
        String dayZodiac = DestinyCalculator.getZodiacNameFromZhi(dayZhi);
        String dayZodiacEmoji = DestinyCalculator.getZodiacEmoji(dayZhi);
        setHtmlText(dayBranchLabel, "<font color='#FFD700'><b>" + dayZhi + "</b></font>");
        setText(dayBranchZodiac, dayZodiac);
        if (dayBranchIcon != null) dayBranchIcon.setText(dayZodiacEmoji);

        // 生肖
        String zodiac = DestinyCalculator.getZodiacNameFromZhi(yearZhi);
        String zodiacEmoji = DestinyCalculator.getZodiacEmoji(yearZhi);
        setText(zodiacLabel, zodiac);
        setText(zodiacYearLabel, "年支 " + yearZhi);
        if (zodiacIcon != null) zodiacIcon.setText(zodiacEmoji);
        setText(zodiacLabelL, zodiac);

        // 日支横屏
        setText(dayZhiLabelL, dayZhi + "（" + dayZodiac + "）");

        // 身强身弱 + 使用提示
        String strengthStr;
        int strengthColor;
        String strengthHintStr;
        if (isStrong) {
            strengthStr = "🔥 身强";
            strengthColor = Color.parseColor("#FF6B6B");
            strengthHintStr = "宜泄耗克制，适合创业发展";
        } else if (isWeak) {
            strengthStr = "💧 身弱";
            strengthColor = Color.parseColor("#87CEEB");
            strengthHintStr = "宜生扶比和，适合稳健守成";
        } else {
            strengthStr = "☯ 中和";
            strengthColor = Color.parseColor("#FFD700");
            strengthHintStr = "五行均衡，宜顺势而为";
        }
        setHtmlText(strengthTag, colorSpan(strengthStr, strengthColor));
        setText(strengthHint, strengthHintStr);
        setHtmlText(strengthTagL, colorSpan(strengthStr, strengthColor));

        // 日干详细解读
        String ganDesc = DestinyCalculator.getGanDescription(dayGan);
        String ganDetail = DestinyCalculator.getRiGanDetailedAnalysis(dayGan);
        setHtmlText(dayDescText, "<font color='#FFD700'><b>" + wxEmoji + " " + dayGan + "日主 · " + ganDesc + "</b></font><br/>"
                + "<font color='#8899AA'>" + ganDetail + "</font>");
        setText(dayDescTextL, ganDesc);

        // 纳音四柱 Grid
        String nYear = DestinyCalculator.getNayin(yearGan, yearZhi);
        String nMonth = DestinyCalculator.getNayin(monthGan, monthZhi);
        String nDay = DestinyCalculator.getNayin(dayGan, dayZhi);
        String nTime = DestinyCalculator.getNayin(timeGan, timeZhi);

        setHtmlText(nayinYearLabel, "<font color='#8899AA'>" + yearGan + yearZhi + "</font><br/>"
                + "<font color='#FFD700'>" + nYear + "</font>");
        setHtmlText(nayinMonthLabel, "<font color='#8899AA'>" + monthGan + monthZhi + "</font><br/>"
                + "<font color='#FFD700'>" + nMonth + "</font>");
        setHtmlText(nayinDayLabel, "<font color='#8899AA'>" + dayGan + dayZhi + "</font><br/>"
                + "<font color='#FFD700'>" + nDay + "</font>");
        setHtmlText(nayinTimeLabel, "<font color='#8899AA'>" + timeGan + timeZhi + "</font><br/>"
                + "<font color='#FFD700'>" + nTime + "</font>");

        // 纳音详细解读
        setHtmlText(nayinText, "日柱 <font color='#FFD700'><b>" + nDay + "</b></font> — "
                + DestinyCalculator.getNayinRich(dayGan, dayZhi));
        setText(nayinLabelL, nDay);

        // 命宫
        String mingGongFull = DestinyCalculator.getMingGong(yearGan, monthZhi, timeZhi);
        int commaIdx = mingGongFull.indexOf("，");
        String mingGongShort = commaIdx > 0 ? mingGongFull.substring(0, commaIdx) : mingGongFull;
        String mingGongDetail = commaIdx > 0 ? mingGongFull.substring(commaIdx + 1) : "";
        setHtmlText(mingGongValue, mingGongShort);
        setHtmlText(mingGongDesc, mingGongDetail + getMingGongPlain(yearGan, monthZhi, timeZhi));

        // 十二长生（三柱：日/月/时）
        String stageDay = DestinyCalculator.getTwelveStage(dayGan, dayZhi);
        String stageMonth = DestinyCalculator.getTwelveStage(dayGan, monthZhi);
        String stageTime = DestinyCalculator.getTwelveStage(dayGan, timeZhi);

        String csDayColor = getStageColor(stageDay);
        String csMonthColor = getStageColor(stageMonth);
        String csTimeColor = getStageColor(stageTime);

        setHtmlText(changshengDayLabel, "<font color='#8899AA'><small>日支</small></font><br/>"
                + "<font color='" + csDayColor + "'><b>" + stageDay + "</b></font>");
        setHtmlText(changshengMonthLabel, "<font color='#8899AA'><small>月令</small></font><br/>"
                + "<font color='" + csMonthColor + "'><b>" + stageMonth + "</b></font>");
        setHtmlText(changshengTimeLabel, "<font color='#8899AA'><small>时支</small></font><br/>"
                + "<font color='" + csTimeColor + "'><b>" + stageTime + "</b></font>");

        setHtmlText(changshengText, "日主「<font color='#FFD700'>" + dayGan + "</font>」在日支为 <font color='" + csDayColor + "'><b>" + stageDay + "</b></font> — " + DestinyCalculator.getTwelveStageExplanation(stageDay)
                + "<br/>在月令为 <font color='" + csMonthColor + "'><b>" + stageMonth + "</b></font> — " + DestinyCalculator.getTwelveStageExplanation(stageMonth)
                + "<br/>在时支为 <font color='" + csTimeColor + "'><b>" + stageTime + "</b></font> — " + DestinyCalculator.getTwelveStageExplanation(stageTime));
        setText(changshengLabelL, stageDay);

        // 五行小结（横屏用）
        String wuSum = dayGanWuXing + " · " + nDay;
        if (isStrong) wuSum += " · 旺";
        else if (isWeak) wuSum += " · 弱";
        else wuSum += " · 平";
        setText(wuxingSumL, wuSum);
    }

    private String getMingGongPlain(String yearGan, String monthZhi, String timeZhi) {
        String[] zhiArr = {"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};
        String[] mgDescMap = {"智慧灵动，变通力强","积累资源，根基稳固","活力充沛，开创性强",
                "繁荣发展，善于表达","变化升腾，格局宏大","礼仪周全，热情洋溢",
                "旺盛显达，名声远播","包容终结，收藏总结","变革创新，权威威严",
                "收敛收获，精明务实","防备收藏，忠诚可靠","流动变化，适应力强"};

        int monthIdx = -1, timeIdx = -1;
        for (int i = 0; i < 12; i++) {
            if (zhiArr[i].equals(monthZhi)) monthIdx = i;
            if (zhiArr[i].equals(timeZhi)) timeIdx = i;
        }
        if (monthIdx < 0 || timeIdx < 0) return "未知";

        int mgIdx = (14 - monthIdx + timeIdx) % 12;
        return "后天运势根基，" + mgDescMap[mgIdx];
    }

    private String getWuXingColor(String wx) {
        switch (wx) {
            case "木": return "#90EE90";
            case "火": return "#FF6B6B";
            case "土": return "#FFD700";
            case "金": return "#C0C0C0";
            case "水": return "#87CEEB";
            default: return "#FFD700";
        }
    }

    private String getWuXingEmoji(String wx) {
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
            return "#90EE90"; // 旺相 - green
        if (stage.equals("沐浴") || stage.equals("衰"))
            return "#FFD700"; // 中和 - gold
        if (stage.equals("病") || stage.equals("死") || stage.equals("墓") || stage.equals("绝"))
            return "#FF6B6B"; // 衰 - red
        return "#87CEEB"; // 胎养 - blue
    }

    private void populateTenGods() {
        StringBuilder sb = new StringBuilder();
        sb.append("年干 ").append(yearGan).append("：").append(coloredText(yGanShen, "#90EE90")).append("　");
        sb.append(DestinyCalculator.getTenGodExplanation(yGanShen)).append("<br/>");
        sb.append("月干 ").append(monthGan).append("：").append(coloredText(mGanShen, "#90EE90")).append("　");
        sb.append(DestinyCalculator.getTenGodExplanation(mGanShen)).append("<br/>");
        sb.append("日干 ").append(dayGan).append("：").append(coloredText("日元", "#FFD700")).append("　命主自身<br/>");
        sb.append("时干 ").append(timeGan).append("：").append(coloredText(tGanShen, "#90EE90")).append("　");
        sb.append(DestinyCalculator.getTenGodExplanation(tGanShen)).append("<br/><br/>");
        sb.append(DestinyCalculator.getTenGodComboAnalysis(dayGan, yearGan, monthGan, timeGan));
        setHtmlText(tenGodsText, sb.toString());
    }

    private void populateWuxingPower() {
        // 五行力量分布
        int[] counts = new int[]{0, 0, 0, 0, 0}; // 木火土金水
        String[] names = {"木", "火", "土", "金", "水"};
        String[] colors = {"#90EE90", "#FF6B6B", "#FFD700", "#C0C0C0", "#87CEEB"};
        String[] emoji = {"🌿", "🔥", "🗻", "⚔️", "💧"};

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
            sb.append(emoji[i]).append(" <font color='").append(colors[i]).append("'><b>").append(names[i]).append("</b></font>  ");
            int bars = (int)((float)counts[i] / maxCount * 10);
            for (int j = 0; j < bars; j++) sb.append("█");
            sb.append(" ").append(counts[i]);
            if (names[i].equals(dayGanWuXing)) sb.append(" <font color='#FFD700'><b>★日主</b></font>");
            sb.append("<br/>");
        }

        sb.append("<br/>生扶: ").append(shengCount + biCount).append("　克泄: ").append(keCount).append("　");
        if (isStrong) sb.append(coloredText("→ 身强", "#FF6B6B"));
        else if (isWeak) sb.append(coloredText("→ 身弱", "#87CEEB"));
        else sb.append(coloredText("→ 中和", "#FFD700"));

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
        String dayZodiac = DestinyCalculator.getZodiacNameFromZhi(dayZhi);
        String dayZhiWuXing = DestinyCalculator.getWuXing(dayZhi);
        String rel = DestinyCalculator.getRelationshipAnalysisRich(dayGan, dayZhi, dayGanWuXing, dayZhiWuXing, dayZodiac, yearZhi, monthZhi, timeZhi);
        setHtmlText(relationshipText, rel);
    }

    private void populateHealth() {
        String health = DestinyCalculator.getHealthAnalysisRich(dayGanWuXing, shengCount, keCount, biCount);
        setHtmlText(healthText, health);
    }

    private void populateSocial() {
        String zodiac = DestinyCalculator.getZodiacNameFromZhi(yearZhi);
        String social = DestinyCalculator.getSocialAnalysisRich(dayGan, dayGanWuXing, yGanShen, mGanShen, tGanShen, yearGan, zodiac, isStrong, isWeak, yearZhi, monthZhi, dayZhi, timeZhi);
        setHtmlText(socialText, social);
    }

    private void populatePillarsDetail() {
        StringBuilder sb = new StringBuilder();

        sb.append("<font color='#FFD700'><b>年柱</b></font> ").append(yearPillar).append("<br/>");
        sb.append(DestinyCalculator.getYearPillarMeaning(yearGan, yearZhi)).append("<br/>");
        sb.append("<font color='#8899AA'>").append(DestinyCalculator.getPillarRichDetail(dayGan, yearGan, yearZhi, "年")).append("</font><br/><br/>");

        sb.append("<font color='#FFD700'><b>月柱</b></font> ").append(monthPillar).append("<br/>");
        sb.append(DestinyCalculator.getMonthPillarMeaning(monthGan, monthZhi)).append("<br/>");
        sb.append("<font color='#8899AA'>").append(DestinyCalculator.getPillarRichDetail(dayGan, monthGan, monthZhi, "月")).append("</font><br/><br/>");

        sb.append("<font color='#FFD700'><b>日柱</b></font> ").append(dayPillar).append("<br/>");
        sb.append(DestinyCalculator.getDayPillarMeaning(dayGan, dayZhi)).append("<br/>");
        sb.append("<font color='#8899AA'>").append(DestinyCalculator.getPillarRichDetail(dayGan, dayGan, dayZhi, "日")).append("</font><br/><br/>");

        sb.append("<font color='#FFD700'><b>时柱</b></font> ").append(timePillar).append("<br/>");
        sb.append(DestinyCalculator.getTimePillarMeaning(timeGan, timeZhi)).append("<br/>");
        sb.append("<font color='#8899AA'>").append(DestinyCalculator.getPillarRichDetail(dayGan, timeGan, timeZhi, "时")).append("</font>");

        setHtmlText(pillarsDetailText, sb.toString());
    }

    private void populateLifeAdvice() {
        String shengHao = DestinyCalculator.getShengWuXing(dayGanWuXing);
        String xieHao = getXieHao();
        String zodiac = DestinyCalculator.getZodiacNameFromZhi(yearZhi);
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
