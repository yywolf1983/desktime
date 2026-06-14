package com.example.timedisplay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class LuoPanActivity extends Activity {
    private LuoPanView luoPanView;

    private TextView directionInfo;
    private TextView mountainInfo;
    private TextView wuxingInfo;
    private TextView shierZhixingInfo;
    private TextView tianganInfo;
    private TextView baguaInfo;
    private TextView chaoXiangInfo;
    private TextView nayinInfo;
    private TextView shenshaInfo;
    private TextView jixiongInfo;
    private TextView diguiInfo;
    private TextView luopanTips;
    private TextView zhaoxiangAnalysis;
    private TextView shuishaAnalysis;
    private DirectionNinePalaceView bazhaiNinePalace;
    private DirectionNinePalaceView jiuxingNinePalace;
    private DirectionNinePalaceView bamenNinePalace;
    private TextView buildingAdvice;
    private TextView mingliInfo;
    private TextView summaryInfo;
    
    private float currentRotation = 0;
    private float lastAngle = 0;
    private boolean isRotating = false;
    
    private static final String KEY_ROTATION = "current_rotation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isRotationLocked = prefs.getBoolean("rotationLocked", false);
        int lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(lockedOrientation);
        }
        
        setContentView(R.layout.activity_luo_pan);
        
        luoPanView = findViewById(R.id.luoPanView);

        directionInfo = findViewById(R.id.directionInfo);
        mountainInfo = findViewById(R.id.mountainInfo);
        wuxingInfo = findViewById(R.id.wuxingInfo);
        shierZhixingInfo = findViewById(R.id.shierZhixingInfo);
        tianganInfo = findViewById(R.id.tianganInfo);
        baguaInfo = findViewById(R.id.baguaInfo);
        chaoXiangInfo = findViewById(R.id.chaoXiangInfo);
        nayinInfo = findViewById(R.id.nayinInfo);
        shenshaInfo = findViewById(R.id.shenshaInfo);
        jixiongInfo = findViewById(R.id.jixiongInfo);
        diguiInfo = findViewById(R.id.diguiInfo);
        luopanTips = findViewById(R.id.luopanTips);
        zhaoxiangAnalysis = findViewById(R.id.zhaoxiangAnalysis);
        shuishaAnalysis = findViewById(R.id.shuishaAnalysis);
        bazhaiNinePalace = findViewById(R.id.bazhaiNinePalace);
        jiuxingNinePalace = findViewById(R.id.jiuxingNinePalace);
        bamenNinePalace = findViewById(R.id.bamenNinePalace);
        buildingAdvice = findViewById(R.id.buildingAdvice);
        mingliInfo = findViewById(R.id.mingliInfo);
        summaryInfo = findViewById(R.id.summaryInfo);
        
        if (savedInstanceState != null) {
            currentRotation = savedInstanceState.getFloat(KEY_ROTATION, 0);
            luoPanView.setRotation(currentRotation);
        }
        
        updateInfo();
        setupTouchListener();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(KEY_ROTATION, currentRotation);
    }
    
    private void updateInfo() {
        String mountain = luoPanView.getCurrentMountain();
        String direction = luoPanView.getCurrentDirection();
        String bagua = getGuaFromShan(mountain);
        String chaoXiang = getChaoXiang(mountain);
        
        directionInfo.setText(direction);
        mountainInfo.setText("坐山: " + mountain);
        wuxingInfo.setText("五行: " + getWuxing(mountain));
        shierZhixingInfo.setText("地支: " + getShierZhi(mountain));
        tianganInfo.setText("天干: " + getTiangan(mountain));
        baguaInfo.setText("八卦: " + bagua);
        chaoXiangInfo.setText("朝向: " + chaoXiang);
        nayinInfo.setText("纳音: " + getNayin(mountain));
        shenshaInfo.setText("神煞: " + getShensha(mountain));
        jixiongInfo.setText("吉凶: " + getJixiong(mountain));
        diguiInfo.setText("归藏: " + getGuiZang(mountain));
        luopanTips.setText(getMountainTip(mountain));
        zhaoxiangAnalysis.setText(getZhaoxiangAnalysis(mountain, chaoXiang));
        shuishaAnalysis.setText(getShuiShaAnalysis(mountain));
        
        String[] currentDirections = getCurrentDirections();
        if (bazhaiNinePalace != null) {
            setupBazhaiNinePalace(bagua, currentDirections);
        }
        if (jiuxingNinePalace != null) {
            setupJiuxingNinePalace(bagua, currentDirections);
        }
        if (bamenNinePalace != null) {
            setupBamenNinePalace(bagua, currentDirections);
        }
        
        buildingAdvice.setText(getBuildingAdvice(mountain));
        mingliInfo.setText(getMingLiInfo(mountain));
        summaryInfo.setText(getSummary(mountain, bagua, chaoXiang));
    }
    
    private String[] getCurrentDirections() {
        float rotation = luoPanView.getRotationValue();
        int offset = Math.round((-rotation % 360 + 360) % 360 / 45f) % 8;
        
        String[] baseDirs = {"北", "东北", "东", "东南", "南", "西南", "西", "西北"};
        String[] result = new String[9];
        
        result[0] = baseDirs[(0 + offset) % 8];
        result[1] = baseDirs[(5 + offset) % 8];
        result[2] = baseDirs[(2 + offset) % 8];
        result[3] = baseDirs[(3 + offset) % 8];
        result[4] = "";
        result[5] = baseDirs[(7 + offset) % 8];
        result[6] = baseDirs[(6 + offset) % 8];
        result[7] = baseDirs[(1 + offset) % 8];
        result[8] = baseDirs[(4 + offset) % 8];
        
        return result;
    }
    
    private String getWuxing(String mountain) {
        String[] wuxingMap = {
            "水", "水", "水", "土", "土", "木",
            "木", "木", "木", "土", "木", "火",
            "火", "火", "火", "土", "土", "金",
            "金", "金", "金", "土", "金", "水"
        };
        String[] mountains = {
            "壬", "子", "癸", "丑", "艮", "寅",
            "甲", "卯", "乙", "辰", "巽", "巳",
            "丙", "午", "丁", "未", "坤", "申",
            "庚", "酉", "辛", "戌", "乾", "亥"
        };
        
        for (int i = 0; i < mountains.length; i++) {
            if (mountains[i].equals(mountain)) {
                return wuxingMap[i];
            }
        }
        return "";
    }
    
    private String getShierZhi(String mountain) {
        String[] zhiMap = {
            "", "子", "", "丑", "", "寅",
            "", "卯", "", "辰", "", "巳",
            "", "午", "", "未", "", "申",
            "", "酉", "", "戌", "", "亥"
        };
        String[] mountains = {
            "壬", "子", "癸", "丑", "艮", "寅",
            "甲", "卯", "乙", "辰", "巽", "巳",
            "丙", "午", "丁", "未", "坤", "申",
            "庚", "酉", "辛", "戌", "乾", "亥"
        };
        
        for (int i = 0; i < mountains.length; i++) {
            if (mountains[i].equals(mountain) && !zhiMap[i].isEmpty()) {
                return zhiMap[i];
            }
        }
        return "—";
    }
    
    private String getTiangan(String mountain) {
        String[] ganMap = {
            "壬", "", "癸", "", "", "",
            "甲", "", "乙", "", "", "",
            "丙", "", "丁", "", "", "",
            "庚", "", "辛", "", "", ""
        };
        String[] mountains = {
            "壬", "子", "癸", "丑", "艮", "寅",
            "甲", "卯", "乙", "辰", "巽", "巳",
            "丙", "午", "丁", "未", "坤", "申",
            "庚", "酉", "辛", "戌", "乾", "亥"
        };
        
        for (int i = 0; i < mountains.length; i++) {
            if (mountains[i].equals(mountain) && !ganMap[i].isEmpty()) {
                return ganMap[i];
            }
        }
        return "—";
    }
    
    private String getGuaFromShan(String shan) {
        String[][] shanGuaMap = {
            {"子", "坎"}, {"癸", "坎"}, {"壬", "坎"},
            {"丑", "艮"}, {"艮", "艮"}, {"寅", "艮"},
            {"甲", "震"}, {"卯", "震"}, {"乙", "震"},
            {"辰", "巽"}, {"巽", "巽"}, {"巳", "巽"},
            {"丙", "离"}, {"午", "离"}, {"丁", "离"},
            {"未", "坤"}, {"坤", "坤"}, {"申", "坤"},
            {"庚", "兑"}, {"酉", "兑"}, {"辛", "兑"},
            {"戌", "乾"}, {"乾", "乾"}, {"亥", "乾"}
        };
        
        for (String[] entry : shanGuaMap) {
            if (entry[0].equals(shan)) {
                return entry[1];
            }
        }
        return "坎";
    }
    
    private String getChaoXiang(String zuoShan) {
        String[][] zuoChaoMap = {
            {"壬", "丙"}, {"子", "午"}, {"癸", "丁"},
            {"丑", "未"}, {"艮", "坤"}, {"寅", "申"},
            {"甲", "庚"}, {"卯", "酉"}, {"乙", "辛"},
            {"辰", "戌"}, {"巽", "乾"}, {"巳", "亥"},
            {"丙", "壬"}, {"午", "子"}, {"丁", "癸"},
            {"未", "丑"}, {"坤", "艮"}, {"申", "寅"},
            {"庚", "甲"}, {"酉", "卯"}, {"辛", "乙"},
            {"戌", "辰"}, {"乾", "巽"}, {"亥", "巳"}
        };
        
        for (String[] entry : zuoChaoMap) {
            if (entry[0].equals(zuoShan)) {
                return entry[1];
            }
        }
        return "午";
    }
    
    private String getNayin(String mountain) {
        String[] nayinMap = {
            "桑柘木", "海中金", "桑柘木", "海中金", "炉中火", "炉中火",
            "大溪水", "大溪水", "大溪水", "沙中土", "沙中土", "沙中土",
            "天河水", "天河水", "天河水", "路旁土", "路旁土", "剑锋金",
            "石榴木", "剑锋金", "石榴木", "山头火", "山头火", "山头火"
        };
        String[] mountains = {
            "壬", "子", "癸", "丑", "艮", "寅",
            "甲", "卯", "乙", "辰", "巽", "巳",
            "丙", "午", "丁", "未", "坤", "申",
            "庚", "酉", "辛", "戌", "乾", "亥"
        };
        
        for (int i = 0; i < mountains.length; i++) {
            if (mountains[i].equals(mountain)) {
                return nayinMap[i];
            }
        }
        return "";
    }
    
    private String getShensha(String mountain) {
        String[] shenshaMap = {
            "天德、月德", "阳刃", "天德合", "天德合", "月德", "月德合",
            "月德", "桃花", "天德", "天德合", "天德", "天德",
            "月德", "桃花", "天德、月德", "天德合", "天德", "天德合",
            "月德", "桃花", "月德合", "天德合", "天德", "天德"
        };
        String[] mountains = {
            "壬", "子", "癸", "丑", "艮", "寅",
            "甲", "卯", "乙", "辰", "巽", "巳",
            "丙", "午", "丁", "未", "坤", "申",
            "庚", "酉", "辛", "戌", "乾", "亥"
        };
        
        for (int i = 0; i < mountains.length; i++) {
            if (mountains[i].equals(mountain)) {
                return shenshaMap[i];
            }
        }
        return "";
    }
    
    private String getJixiong(String mountain) {
        String wuxing = getWuxing(mountain);
        String nayin = getNayin(mountain);
        String shensha = getShensha(mountain);
        
        int score = 0;
        
        if (shensha.contains("天德") || shensha.contains("月德")) {
            score += 2;
        }
        if (shensha.contains("桃花")) {
            score += 1;
        }
        if (shensha.contains("阳刃")) {
            score -= 1;
        }
        
        if (wuxing.equals("土")) {
            score += 1;
        }
        
        if (nayin.contains("金")) {
            score += 1;
        }
        
        if (score >= 3) {
            return "大吉";
        } else if (score >= 2) {
            return "吉";
        } else if (score >= 0) {
            return "平";
        } else {
            return "凶";
        }
    }
    
    private String getGuiZang(String mountain) {
        String wuxing = getWuxing(mountain);
        String[] guiZangMap = {
            "水", "火", "木", "金", "土"
        };
        String[] wuxingKeys = {"水", "火", "木", "金", "土"};
        
        for (int i = 0; i < wuxingKeys.length; i++) {
            if (wuxingKeys[i].equals(wuxing)) {
                return guiZangMap[i];
            }
        }
        return wuxing;
    }
    
    private String getMountainTip(String mountain) {
        String[] tips = {
            "壬: 北方阳水，江河湖海，智巧聪明",
            "子: 正北水位，阴阳交界，万物根基",
            "癸: 北方阴水，雨露泉涧，柔美细腻",
            "丑: 东北金库，湿土藏金，孕育之机",
            "艮: 东北艮位，少男之位，山陵高岗",
            "寅: 东北木位，阳木生发，参天大树",
            "甲: 东方阳木，栋梁之才，阳气初升",
            "卯: 正东木位，日出之所，生机盎然",
            "乙: 东方阴木，花草藤萝，柔和之气",
            "辰: 东南水库，湿土蓄水，蓄藏之地",
            "巽: 东南巽位，长女之位，风林草木",
            "巳: 东南火位，阴火初盛，初夏之气",
            "丙: 南方阳火，太阳烈火，阳气盛极",
            "午: 正南火位，日中之位，鼎盛辉煌",
            "丁: 南方阴火，灯烛星光，温暖柔和",
            "未: 西南木库，燥土藏木，滋养之地",
            "坤: 西南坤位，老母之位，大地母亲",
            "申: 西南金位，阳金萧杀，刀剑兵器",
            "庚: 西方阳金，金银铜铁，萧杀之气",
            "酉: 正西金位，日落之所，月照秋凉",
            "辛: 西方阴金，珠玉宝石，柔和之金",
            "戌: 西北火库，燥土藏火，收藏之地",
            "乾: 西北乾位，老父之位，天门高天",
            "亥: 西北水位，阴水天门，长生之地"
        };
        
        String[] mountains = {
            "壬", "子", "癸", "丑", "艮", "寅",
            "甲", "卯", "乙", "辰", "巽", "巳",
            "丙", "午", "丁", "未", "坤", "申",
            "庚", "酉", "辛", "戌", "乾", "亥"
        };
        
        for (int i = 0; i < mountains.length; i++) {
            if (mountains[i].equals(mountain)) {
                return tips[i];
            }
        }
        return "";
    }
    
    private String getZhaoxiangAnalysis(String zuoShan, String chaoXiang) {
        String zuoDir = getDirectionFromShan(zuoShan);
        String chaoDir = getDirectionFromShan(chaoXiang);
        
        String[][] zuoChaoTable = {
            {"子", "午", "【坐北向南】坐子向午，北方壬癸水来，南方丙丁火照，水火既济，主富贵双全，人丁兴旺，事业昌盛"},
            {"午", "子", "【坐南向北】坐午向子，南方丙丁火来，北方壬癸水照，火水未济，宜用五行调和，子孙昌盛，家道兴隆"},
            {"卯", "酉", "【坐东向西】坐卯向酉，东方甲乙木来，西方庚辛金照，金木相克，需用土通关，财源广进，百事亨通"},
            {"酉", "卯", "【坐西向东】坐酉向卯，西方庚辛金来，东方甲乙木照，金克木，需用火化解，家道兴隆，富贵绵长"},
            {"辰", "戌", "【坐东南向西北】坐辰向戌，东南水库来，西北火库照，水土相混，需木疏通，百事亨通，丁财两旺"},
            {"戌", "辰", "【坐西北向东南】坐戌向辰，西北火库来，东南水库照，火土相生，主丁财两旺，富贵绵长，福寿安康"},
            {"丑", "未", "【坐东北向西南】坐丑向未，东北金库来，西南木库照，土土相助，主根基稳固，子孙满堂，福禄寿全"},
            {"未", "丑", "【坐西南向东北】坐未向丑，西南木库来，东北金库照，土金相生，主贵气临门，福禄寿全，吉祥如意"},
            {"艮", "坤", "【坐东北向西南】坐艮向坤，东北少男位，西南老母位，阴阳相配，人丁兴旺，福寿安康，家宅康宁"},
            {"坤", "艮", "【坐西南向东北】坐坤向艮，西南老母位，东北少男位，阴阳得位，福寿绵长，家宅康宁，子孙满堂"},
            {"巽", "乾", "【坐东南向西北】坐巽向乾，东南长女位，西北老父位，风天小畜，贵人相助，事业发达，富贵荣华"},
            {"乾", "巽", "【坐西北向东南】坐乾向巽，西北老父位，东南长女位，天风姤，家道兴隆，富贵荣华，声名远播"},
            {"寅", "申", "【坐东北向西南】坐寅向申，东北阳木，西南阳金，金木相战，需水调和，财源广进，事业有成"},
            {"申", "寅", "【坐西南向东北】坐申向寅，西南阳金，东北阳木，金伐木，需火制金，官运亨通，步步高升"},
            {"巳", "亥", "【坐东南向西北】坐巳向亥，东南阴火，西北阴水，水火相激，需土通关，家宅安宁，百事顺遂"},
            {"亥", "巳", "【坐西北向东南】坐亥向巳，西北阴水，东南阴火，水克火，需木通关，丁财两旺，家道荣昌"},
            {"甲", "庚", "【坐东向西】坐甲向庚，东方阳木，西方阳金，金克木，宜用火泄金，文运昌盛，学业有成"},
            {"庚", "甲", "【坐西向东】坐庚向甲，西方阳金，东方阳木，金强木弱，宜用水化金，武运亨通，权势显赫"},
            {"乙", "辛", "【坐东向西】坐乙向辛，东方阴木，西方阴金，金克木，宜用土生金，财源茂盛，富贵双全"},
            {"辛", "乙", "【坐西向东】坐辛向乙，西方阴金，东方阴木，金多木少，宜用木助，人丁兴旺，福寿安康"},
            {"丙", "壬", "【坐南向北】坐丙向壬，南方阳火，北方阳水，水克火，宜用木生火，官贵临门，步步高升"},
            {"壬", "丙", "【坐北向南】坐壬向丙，北方阳水，南方阳火，水火既济，富贵双全，声名远播，福禄寿全"},
            {"丁", "癸", "【坐南向北】坐丁向癸，南方阴火，北方阴水，水克火，宜用土止水，福寿绵长，家道荣昌"},
            {"癸", "丁", "【坐北向南】坐癸向丁，北方阴水，南方阴火，水火相济，丁财两旺，家道荣昌，富贵双全"}
        };
        
        for (String[] entry : zuoChaoTable) {
            if (entry[0].equals(zuoShan) && entry[1].equals(chaoXiang)) {
                return entry[2];
            }
        }
        
        return "【坐" + zuoDir + "向" + chaoDir + "】坐" + zuoShan + "向" + chaoXiang + "，需结合具体地形分析";
    }
    
    private String getDirectionFromShan(String shan) {
        String[][] shanDirMap = {
            {"子", "北"}, {"癸", "北"}, {"壬", "北"},
            {"丑", "东北"}, {"艮", "东北"}, {"寅", "东北"},
            {"甲", "东"}, {"卯", "东"}, {"乙", "东"},
            {"辰", "东南"}, {"巽", "东南"}, {"巳", "东南"},
            {"丙", "南"}, {"午", "南"}, {"丁", "南"},
            {"未", "西南"}, {"坤", "西南"}, {"申", "西南"},
            {"庚", "西"}, {"酉", "西"}, {"辛", "西"},
            {"戌", "西北"}, {"乾", "西北"}, {"亥", "西北"}
        };
        
        for (String[] entry : shanDirMap) {
            if (entry[0].equals(shan)) {
                return entry[1];
            }
        }
        return "";
    }
    
    private String getShuiShaAnalysis(String zuoShan) {
        String zuoDir = getDirectionFromShan(zuoShan);
        
        String[][] shuiShaTable = {
            {"子", "【坐北】北方(坎)宜有水，南方(离)宜有山，东方(震)青龙宜高耸，西方(兑)白虎宜柔顺，明堂开阔主富贵，后山高耸主丁旺"},
            {"丑", "【坐东北】东北(艮)宜有山，西南(坤)宜有水，艮方砂秀主贵，坤方水旺财，龙虎相配丁财旺，水口紧锁富贵长"},
            {"寅", "【坐东北】东北(艮)宜有水，西南(坤)宜有山，水来青龙方，砂见白虎位，山水相映福寿全，明堂端正子孙贤"},
            {"卯", "【坐东】东方(震)宜有水，西方(兑)宜有山，青龙得水主贵，白虎有砂旺财，山环水绕出英豪，玉带缠腰富贵来"},
            {"辰", "【坐东南】东南(巽)宜有山，西北(乾)宜有水，水库得砂主富，天门见水主贵，水砂相配主康宁，文昌得位出贤能"},
            {"巳", "【坐东南】东南(巽)宜有水，西北(乾)宜有山，巽方水来主秀，乾方砂起主贵，秀气临门出贤才，贵人相助步步高"},
            {"午", "【坐南】南方(离)宜有水，北方(坎)宜有山，朱雀得水主文，玄武有山主寿，文武双全福禄长，贵人相助万事昌"},
            {"未", "【坐西南】西南(坤)宜有山，东北(艮)宜有水，坤方砂厚主富，艮方水秀主贵，厚砂秀水出贵人，家宅康宁福满堂"},
            {"申", "【坐西南】西南(坤)宜有水，东北(艮)宜有山，坤方水来主财，艮方砂起主丁，财丁两旺家道兴，子孙昌盛代代荣"},
            {"酉", "【坐西】西方(兑)宜有水，东方(震)宜有山，白虎得水主富，青龙有砂主贵，金水相生富贵来，家道兴隆传万代"},
            {"戌", "【坐西北】西北(乾)宜有山，东南(巽)宜有水，火库得砂主贵，巽方水来主秀，贵秀两全福寿臻，金榜题名耀门庭"},
            {"亥", "【坐西北】西北(乾)宜有水，东南(巽)宜有山，天门得水主智，地户有砂主贵，智慧显贵代代传，书香门第出状元"},
            {"壬", "【坐北】北方(坎)宜有山，南方(离)宜有水，阳水得砂主贵，丙丁见水主富，贵富双全大吉昌，子孙满堂福寿长"},
            {"癸", "【坐北】北方(坎)宜有水，南方(离)宜有山，阴水得水主智，午位有砂主贵，智慧显贵出英才，金榜题名乐开怀"},
            {"甲", "【坐东】东方(震)宜有山，西方(兑)宜有水，阳木得砂主贵，庚辛见水主富，贵富兼全福禄寿，家宅康宁万事休"},
            {"乙", "【坐东】东方(震)宜有水，西方(兑)宜有山，阴木得水主秀，酉位有砂主贵，秀丽显贵子孙贤，福禄寿喜满人间"},
            {"丙", "【坐南】南方(离)宜有山，北方(坎)宜有水，阳火得砂主贵，壬癸见水主富，贵富双全福满堂，子孙昌盛万年长"},
            {"丁", "【坐南】南方(离)宜有水，北方(坎)宜有山，阴火得水主文，子位有砂主贵，文贵双全书香第，世代书香传不息"},
            {"庚", "【坐西】西方(兑)宜有山，东方(震)宜有水，阳金得砂主贵，甲乙见水主富，贵富并臻吉祥地，家宅兴旺永无比"},
            {"辛", "【坐西】西方(兑)宜有水，东方(震)宜有山，阴金得水主秀，卯位有砂主贵，秀美显贵富贵家，福禄寿喜人人夸"},
            {"艮", "【坐东北】东北(艮)宜有山，西南(坤)宜有水，少男得位主丁，老母见水主财，丁财两旺吉祥宅，子孙昌盛传万代"},
            {"坤", "【坐西南】西南(坤)宜有山，东北(艮)宜有水，老母得位主富，少男见水主贵，富贵双全安乐窝，子孙满堂福气多"},
            {"巽", "【坐东南】东南(巽)宜有山，西北(乾)宜有水，长女得位主秀，老父见水主智，秀智双全吉祥地，家宅康宁万事吉"},
            {"乾", "【坐西北】西北(乾)宜有山，东南(巽)宜有水，老父得位主贵，长女见水主文，贵文双全书香门，世代书香传子孙"}
        };
        
        for (String[] entry : shuiShaTable) {
            if (entry[0].equals(zuoShan)) {
                return entry[1];
            }
        }
        
        return "【坐" + zuoDir + "】砂水判断需结合具体地形";
    }
    
    private String getBaZhaiAnalysis(String bagua) {
        String[][] baZhaiTable = {
            {"坎", "【坐北】吉方：生气在东南(巽)，天医在东方(震)，延年在东北(艮)，伏位在北方(坎)。凶方：祸害在西北(乾)，六煞在西方(兑)，五鬼在南方(离)，绝命在西南(坤)"},
            {"离", "【坐南】吉方：生气在西北(乾)，天医在西方(兑)，延年在东南(巽)，伏位在南方(离)。凶方：祸害在东北(艮)，六煞在东方(震)，五鬼在北方(坎)，绝命在西南(坤)"},
            {"震", "【坐东】吉方：生气在南方(离)，天医在西南(坤)，延年在西方(兑)，伏位在东方(震)。凶方：祸害在东南(巽)，六煞在北方(坎)，五鬼在西北(乾)，绝命在东北(艮)"},
            {"兑", "【坐西】吉方：生气在东北(艮)，天医在北方(坎)，延年在西南(坤)，伏位在西方(兑)。凶方：祸害在东方(震)，六煞在南方(离)，五鬼在东南(巽)，绝命在西北(乾)"},
            {"巽", "【坐东南】吉方：生气在北方(坎)，天医在东北(艮)，延年在南方(离)，伏位在东南(巽)。凶方：祸害在西南(坤)，六煞在西北(乾)，五鬼在西方(兑)，绝命在东方(震)"},
            {"艮", "【坐东北】吉方：生气在西方(兑)，天医在东南(巽)，延年在北方(坎)，伏位在东北(艮)。凶方：祸害在南方(离)，六煞在西南(坤)，五鬼在东方(震)，绝命在西北(乾)"},
            {"坤", "【坐西南】吉方：生气在东方(震)，天医在南方(离)，延年在西方(兑)，伏位在西南(坤)。凶方：祸害在北方(坎)，六煞在东北(艮)，五鬼在西北(乾)，绝命在东南(巽)"},
            {"乾", "【坐西北】吉方：生气在南方(离)，天医在东方(震)，延年在东南(巽)，伏位在西北(乾)。凶方：祸害在西方(兑)，六煞在北方(坎)，五鬼在西南(坤)，绝命在东北(艮)"}
        };
        
        for (String[] entry : baZhaiTable) {
            if (entry[0].equals(bagua)) {
                return entry[1];
            }
        }
        
        return "八宅吉凶方位需结合具体布局";
    }
    
    private String getJiuXingAnalysis(String bagua) {
        String[][] jiuXingTable = {
            {"坎", "【坐北】一白贪狼星在北方(坎)，二黑巨门星在西南(坤)，三碧禄存星在东方(震)，四绿文曲星在东南(巽)，五黄廉贞星在中宫，六白武曲星在西北(乾)，七赤破军星在西方(兑)，八白左辅星在东北(艮)，九紫右弼星在南方(离)"},
            {"离", "【坐南】九紫右弼星在南方(离)，一白贪狼星在北方(坎)，二黑巨门星在西南(坤)，三碧禄存星在东方(震)，四绿文曲星在东南(巽)，五黄廉贞星在中宫，六白武曲星在西北(乾)，七赤破军星在西方(兑)，八白左辅星在东北(艮)"},
            {"震", "【坐东】三碧禄存星在东方(震)，四绿文曲星在东南(巽)，五黄廉贞星在中宫，六白武曲星在西北(乾)，七赤破军星在西方(兑)，八白左辅星在东北(艮)，九紫右弼星在南方(离)，一白贪狼星在北方(坎)，二黑巨门星在西南(坤)"},
            {"兑", "【坐西】七赤破军星在西方(兑)，八白左辅星在东北(艮)，九紫右弼星在南方(离)，一白贪狼星在北方(坎)，二黑巨门星在西南(坤)，三碧禄存星在东方(震)，四绿文曲星在东南(巽)，五黄廉贞星在中宫，六白武曲星在西北(乾)"},
            {"巽", "【坐东南】四绿文曲星在东南(巽)，五黄廉贞星在中宫，六白武曲星在西北(乾)，七赤破军星在西方(兑)，八白左辅星在东北(艮)，九紫右弼星在南方(离)，一白贪狼星在北方(坎)，二黑巨门星在西南(坤)，三碧禄存星在东方(震)"},
            {"艮", "【坐东北】八白左辅星在东北(艮)，九紫右弼星在南方(离)，一白贪狼星在北方(坎)，二黑巨门星在西南(坤)，三碧禄存星在东方(震)，四绿文曲星在东南(巽)，五黄廉贞星在中宫，六白武曲星在西北(乾)，七赤破军星在西方(兑)"},
            {"坤", "【坐西南】二黑巨门星在西南(坤)，三碧禄存星在东方(震)，四绿文曲星在东南(巽)，五黄廉贞星在中宫，六白武曲星在西北(乾)，七赤破军星在西方(兑)，八白左辅星在东北(艮)，九紫右弼星在南方(离)，一白贪狼星在北方(坎)"},
            {"乾", "【坐西北】六白武曲星在西北(乾)，七赤破军星在西方(兑)，八白左辅星在东北(艮)，九紫右弼星在南方(离)，一白贪狼星在北方(坎)，二黑巨门星在西南(坤)，三碧禄存星在东方(震)，四绿文曲星在东南(巽)，五黄廉贞星在中宫"}
        };
        
        for (String[] entry : jiuXingTable) {
            if (entry[0].equals(bagua)) {
                return entry[1];
            }
        }
        
        return "九星飞宫需结合具体布局";
    }
    
    private String getBaMenAnalysis(String bagua) {
        String[][] baMenTable = {
            {"坎", "【坐北】吉门：休门在北方(坎)，生门在东北(艮)，开门在西北(乾)。平门：景门在南方(离)，杜门在东南(巽)。凶门：伤门在东方(震)，惊门在西方(兑)，死门在西南(坤)"},
            {"离", "【坐南】吉门：开门在西北(乾)，休门在北方(坎)，生门在东北(艮)。平门：杜门在东南(巽)，景门在南方(离)。凶门：伤门在东方(震)，惊门在西方(兑)，死门在西南(坤)"},
            {"震", "【坐东】吉门：生门在东北(艮)，开门在西北(乾)，休门在北方(坎)。平门：杜门在东南(巽)，景门在南方(离)。凶门：伤门在东方(震)，惊门在西方(兑)，死门在西南(坤)"},
            {"兑", "【坐西】吉门：休门在北方(坎)，生门在东北(艮)，开门在西北(乾)。平门：景门在南方(离)，杜门在东南(巽)。凶门：伤门在东方(震)，惊门在西方(兑)，死门在西南(坤)"},
            {"巽", "【坐东南】吉门：开门在西北(乾)，休门在北方(坎)，生门在东北(艮)。平门：景门在南方(离)，杜门在东南(巽)。凶门：伤门在东方(震)，惊门在西方(兑)，死门在西南(坤)"},
            {"艮", "【坐东北】吉门：休门在北方(坎)，生门在东北(艮)，开门在西北(乾)。平门：景门在南方(离)，杜门在东南(巽)。凶门：伤门在东方(震)，惊门在西方(兑)，死门在西南(坤)"},
            {"坤", "【坐西南】吉门：开门在西北(乾)，休门在北方(坎)，生门在东北(艮)。平门：杜门在东南(巽)，景门在南方(离)。凶门：伤门在东方(震)，惊门在西方(兑)，死门在西南(坤)"},
            {"乾", "【坐西北】吉门：开门在西北(乾)，休门在北方(坎)，生门在东北(艮)。平门：景门在南方(离)，杜门在东南(巽)。凶门：伤门在东方(震)，惊门在西方(兑)，死门在西南(坤)"}
        };
        
        for (String[] entry : baMenTable) {
            if (entry[0].equals(bagua)) {
                return entry[1];
            }
        }
        
        return "八门遁法需结合具体布局";
    }
    
    private String getBuildingAdvice(String zuoShan) {
        String[][] adviceTable = {
            {"子", "宜坐北朝南，大门宜开南方或东南方，忌开北方；主卧宜设在东方或南方，利于人丁；书房宜设在东南方，文昌得位"},
            {"丑", "宜坐东北朝西南，大门宜开西南或南方，忌开东北；客厅宜宽敞，聚气生财；财位宜设在西南，坤方得地"},
            {"寅", "宜坐东北朝西南，大门宜开南方或东方，忌开西南；书房宜设在东方，文昌得位；贵人位在北方，水来生木"},
            {"卯", "宜坐东朝西，大门宜开南方或北方，忌开西方；厨房宜设在东南方，火生土旺；桃花位在南方，人缘旺盛"},
            {"辰", "宜坐东南朝西北，大门宜开北方或东方，忌开西北；财位宜设在北方，水旺财聚；文昌位在东方，木火通明"},
            {"巳", "宜坐东南朝西北，大门宜开南方或北方，忌开西北；卧室宜设在南方，阳光充足；事业位在东方，木来生火"},
            {"午", "宜坐南朝北，大门宜开北方或东南方，忌开南方；神位宜设在北方，敬天祭祖；文昌位在西方，金白水清"},
            {"未", "宜坐西南朝东北，大门宜开东北或北方，忌开西南；花园宜设在南方，生机勃勃；贵人位在北方，天一生水"},
            {"申", "宜坐西南朝东北，大门宜开北方或西方，忌开东北；库房宜设在西北方，金生水旺；财位在西方，金白水清"},
            {"酉", "宜坐西朝东，大门宜开东方或南方，忌开东方；财位宜设在南方，火生土旺；贵人位在东方，木来克土"},
            {"戌", "宜坐西北朝东南，大门宜开东南或南方，忌开西北；老人房宜设在东北方，艮位得宜；文昌位在东方，水木清华"},
            {"亥", "宜坐西北朝东南，大门宜开东南或东方，忌开东南；儿童房宜设在东方，震位主长；贵人位在南方，火来暖水"},
            {"壬", "宜坐北朝南偏东，大门宜开南方，忌开北方；文昌位在东南，利于学业；财位在西方，金白水清"},
            {"癸", "宜坐北朝南偏西，大门宜开南方，忌开北方；财位在西南，坤方得地；桃花位在南方，人缘旺盛"},
            {"甲", "宜坐东朝西偏北，大门宜开南方，忌开西方；贵人位在北方，水来生木；文昌位在南方，木火通明"},
            {"乙", "宜坐东朝西偏南，大门宜开南方，忌开西方；桃花位在南方，人缘旺盛；财位在西方，金克木"},
            {"丙", "宜坐南朝北偏东，大门宜开北方，忌开南方；事业位在东方，木来生火；文昌位在北方，水火既济"},
            {"丁", "宜坐南朝北偏西，大门宜开北方，忌开南方；文昌位在西方，金白水清；贵人位在东方，木来生火"},
            {"庚", "宜坐西朝东偏北，大门宜开东方，忌开东方；财位在北方，金水相生；文昌位在南方，火克金"},
            {"辛", "宜坐西朝东偏南，大门宜开东方，忌开东方；贵人位在南方，火来炼金；桃花位在北方，金生水"},
            {"艮", "宜坐东北朝西南，大门宜开南方，忌开东北；子孙位在东方，震为长男；财位在西方，土生金"},
            {"坤", "宜坐西南朝东北，大门宜开北方，忌开西南；财位在北方，天一生水；子孙位在东方，木克土"},
            {"巽", "宜坐东南朝西北，大门宜开北方，忌开西北；文昌位在北方，水木清华；贵人位在南方，火来暖木"},
            {"乾", "宜坐西北朝东南，大门宜开东南，忌开西北；长辈房宜设在西北，乾为老父；财位在北方，金生水"}
        };
        
        for (String[] entry : adviceTable) {
            if (entry[0].equals(zuoShan)) {
                return entry[1];
            }
        }
        
        return "请结合实际地形和周边环境进行布局设计";
    }
    
    private String getMingLiInfo(String mountain) {
        String[][] mingLiTable = {
            {"子", "子年生人属鼠，五行属水，性格聪明伶俐，机智灵活，善于社交，处事圆滑，富有创造力，适应能力强，财运亨通"},
            {"丑", "丑年生人属牛，五行属土，性格稳重踏实，勤劳肯干，诚实守信，意志坚强，耐力持久，财运稳定，福禄寿全"},
            {"寅", "寅年生人属虎，五行属木，性格勇猛果敢，热情开朗，富有正义感，行动力强，领导能力出众，事业有成"},
            {"卯", "卯年生人属兔，五行属木，性格温柔善良，聪明机智，心思细腻，富有艺术天赋，人缘极佳，福禄绵长"},
            {"辰", "辰年生人属龙，五行属土，性格刚毅果断，富有魄力，自信心强，领导力出众，事业辉煌，富贵荣华"},
            {"巳", "巳年生人属蛇，五行属火，性格智慧机敏，直觉敏锐，善于思考，富有魅力，财运亨通，福寿安康"},
            {"午", "午年生人属马，五行属火，性格热情奔放，乐观开朗，行动力强，追求自由，事业有成，福禄寿喜"},
            {"未", "未年生人属羊，五行属土，性格温和善良，富有同情心，艺术天赋高，家庭观念强，福禄双全"},
            {"申", "申年生人属猴，五行属金，性格聪明伶俐，反应敏捷，善于变通，富有创新精神，财运旺盛"},
            {"酉", "酉年生人属鸡，五行属金，性格勤奋努力，精明能干，追求完美，口才出众，事业成功"},
            {"戌", "戌年生人属狗，五行属土，性格忠诚正直，勇敢正义，责任心强，重情重义，福禄寿全"},
            {"亥", "亥年生人属猪，五行属水，性格善良淳朴，乐观豁达，待人真诚，财运亨通，福寿绵长"},
            {"壬", "壬年生人五行属水，性格聪明睿智，思维敏捷，善于谋划，财运亨通，事业有成"},
            {"癸", "癸年生人五行属水，性格温柔细腻，富有智慧，善于思考，艺术天赋高，福寿安康"},
            {"甲", "甲年生人五行属木，性格刚毅果断，行动力强，领导能力出众，事业辉煌"},
            {"乙", "乙年生人五行属木，性格温柔善良，富有艺术天赋，心思细腻，人缘极佳"},
            {"丙", "丙年生人五行属火，性格热情奔放，自信心强，富有魄力，事业有成"},
            {"丁", "丁年生人五行属火，性格温柔体贴，富有智慧，艺术天赋高，福禄双全"},
            {"庚", "庚年生人五行属金，性格刚毅果断，正义感强，事业成功，财运旺盛"},
            {"辛", "辛年生人五行属金，性格温柔善良，精明能干，追求完美，福禄寿全"},
            {"艮", "艮年生人五行属土，性格稳重踏实，勤劳肯干，意志坚强，福禄绵长"},
            {"坤", "坤年生人五行属土，性格温柔善良，富有同情心，家庭观念强，福寿安康"},
            {"巽", "巽年生人五行属木，性格智慧机敏，善于变通，富有创新精神，事业有成"},
            {"乾", "乾年生人五行属金，性格刚毅果断，自信心强，领导力出众，富贵荣华"}
        };
        
        for (String[] entry : mingLiTable) {
            if (entry[0].equals(mountain)) {
                return entry[1];
            }
        }
        
        return "命理分析需结合具体年份";
    }
    
    private SpannableString getSummary(String mountain, String bagua, String chaoXiang) {
        String zuoDir = getDirectionFromShan(mountain);
        String chaoDir = getDirectionFromShan(chaoXiang);
        String wuxing = getWuxing(mountain);
        String nayin = getNayin(mountain);
        
        StringBuilder sb = new StringBuilder();
        SpannableString result;
        
        sb.append("【基本信息】\n");
        sb.append("坐山：").append(mountain).append(" 五行：").append(wuxing).append(" 纳音：").append(nayin).append("\n");
        sb.append("朝向：").append(chaoXiang).append(" 坐").append(zuoDir).append("向").append(chaoDir).append("\n");
        sb.append("卦象：").append(bagua).append("卦\n");
        sb.append("\n");
        
        String[][] luckyPos = {
            {"坎", "东南(巽)生气、东方(震)天医、东北(艮)延年"},
            {"离", "西北(乾)生气、西方(兑)天医、东南(巽)延年"},
            {"震", "南方(离)生气、西南(坤)天医、西方(兑)延年"},
            {"兑", "东北(艮)生气、北方(坎)天医、西南(坤)延年"},
            {"巽", "北方(坎)生气、东北(艮)天医、南方(离)延年"},
            {"艮", "西方(兑)生气、东南(巽)天医、北方(坎)延年"},
            {"坤", "东方(震)生气、南方(离)天医、西方(兑)延年"},
            {"乾", "南方(离)生气、东方(震)天医、东南(巽)延年"}
        };
        
        String[][] badPos = {
            {"坎", "南方(离)五鬼、西南(坤)绝命、西北(乾)祸害、西方(兑)六煞"},
            {"离", "北方(坎)五鬼、西南(坤)绝命、东北(艮)祸害、东方(震)六煞"},
            {"震", "西北(乾)五鬼、东北(艮)绝命、东南(巽)祸害、北方(坎)六煞"},
            {"兑", "东南(巽)五鬼、西北(乾)绝命、东方(震)祸害、南方(离)六煞"},
            {"巽", "西方(兑)五鬼、东方(震)绝命、西南(坤)祸害、西北(乾)六煞"},
            {"艮", "东方(震)五鬼、西北(乾)绝命、南方(离)祸害、西南(坤)六煞"},
            {"坤", "西北(乾)五鬼、东南(巽)绝命、北方(坎)祸害、东北(艮)六煞"},
            {"乾", "西南(坤)五鬼、东北(艮)绝命、西方(兑)祸害、北方(坎)六煞"}
        };
        
        String lucky = "";
        String bad = "";
        for (String[] entry : luckyPos) {
            if (entry[0].equals(bagua)) {
                lucky = entry[1];
                break;
            }
        }
        for (String[] entry : badPos) {
            if (entry[0].equals(bagua)) {
                bad = entry[1];
                break;
            }
        }
        
        sb.append("【吉位】").append(lucky).append("\n");
        sb.append("\n");
        
        sb.append("【凶位】").append(bad).append("\n");
        sb.append("\n");
        
        String[][] advice = {
            {"坎", "大门宜开东南或东方，主卧宜设东方或北方，书房宜设东南方"},
            {"离", "大门宜开西北或西方，神位宜设北方，文昌位在西方，财位在东方"},
            {"震", "大门宜开南方或西方，厨房宜设东南，桃花位在南方，财位在北方"},
            {"兑", "大门宜开东北或北方，财位宜设南方，贵人位在东方，文昌位在东南"},
            {"巽", "大门宜开北方或东北，财位宜设北方，文昌位在北方，贵人位在南方"},
            {"艮", "大门宜开西方或东南，子孙位在东方，财位在西方，老人房宜设东北"},
            {"坤", "大门宜开东方或南方，财位宜设北方，子孙位在东方，花园宜设南方"},
            {"乾", "大门宜开南方或东南，长辈房宜设西北，财位在北方，书房宜设东南"}
        };
        
        String adviceText = "";
        for (String[] entry : advice) {
            if (entry[0].equals(bagua)) {
                adviceText = entry[1];
                break;
            }
        }
        
        sb.append("【布局建议】\n");
        sb.append(adviceText).append("\n");
        sb.append("\n");
        
        String[][] wuxingTips = {
            {"坎", "北方宜有水景，南方宜有山峦"},
            {"离", "南方宜开阔，北方宜靠山"},
            {"震", "东方宜流水，西方宜靠山"},
            {"兑", "西方宜水景，东方宜靠山"},
            {"巽", "东南宜树林，西北宜水景"},
            {"艮", "东北宜山峦，西南宜水景"},
            {"坤", "西南宜厚土，东北宜水景"},
            {"乾", "西北宜高楼，东南宜水景"}
        };
        
        String wuxingTip = "";
        for (String[] entry : wuxingTips) {
            if (entry[0].equals(bagua)) {
                wuxingTip = entry[1];
                break;
            }
        }
        
        sb.append("【砂水提示】\n");
        sb.append(wuxingTip).append("\n");
        sb.append("\n");
        
        String[][] fortune = {
            {"坎", "水旺主智，财运亨通，宜经商"},
            {"离", "火旺主礼，事业兴旺，宜仕途"},
            {"震", "木旺主仁，人丁兴旺，宜求学"},
            {"兑", "金旺主义，财运旺盛，宜理财"},
            {"巽", "木旺主风，贵人相助，宜发展"},
            {"艮", "土旺主信，根基稳固，宜置业"},
            {"坤", "土旺主顺，家宅康宁，宜安居"},
            {"乾", "金旺主健，贵人提携，宜创业"}
        };
        
        String fortuneText = "";
        for (String[] entry : fortune) {
            if (entry[0].equals(bagua)) {
                fortuneText = entry[1];
                break;
            }
        }
        
        sb.append("【运势总评】\n");
        sb.append("坐").append(mountain).append("(").append(zuoDir).append(")向").append(chaoXiang).append("(").append(chaoDir).append(")，");
        sb.append(wuxing).append("行得位，").append(bagua).append("卦得气。");
        sb.append("\n");
        sb.append(fortuneText);
        sb.append("\n");
        sb.append("吉位宜开门、设卧室、做书房；凶位宜作厨房、储物间或卫生间。");
        
        result = new SpannableString(sb.toString());
        
        int idx = 0;
        
        idx = sb.indexOf("【基本信息】");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#FFD700")), idx, idx + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("坐山：");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#87CEEB")), idx + 3, idx + 3 + mountain.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("五行：");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#00CED1")), idx + 3, idx + 3 + wuxing.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("纳音：");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#00CED1")), idx + 3, idx + 3 + nayin.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("朝向：");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#87CEEB")), idx + 3, idx + 3 + chaoXiang.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("卦象：");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#DDA0DD")), idx + 3, idx + 3 + bagua.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("【吉位】");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#90EE90")), idx, idx + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf(lucky);
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#90EE90")), idx, idx + lucky.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("【凶位】");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6B6B")), idx, idx + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf(bad);
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6B6B")), idx, idx + bad.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("【布局建议】");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#FFD700")), idx, idx + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("【砂水提示】");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#FFD700")), idx, idx + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("【运势总评】");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#FFD700")), idx, idx + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf(fortuneText);
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#FFD700")), idx, idx + fortuneText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("吉位宜开门");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#90EE90")), idx, idx + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        idx = sb.indexOf("凶位宜作厨房");
        if (idx >= 0) result.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6B6B")), idx, idx + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        return result;
    }
    
    private void setupBazhaiNinePalace(String bagua, String[] directions) {
        String[][] bazhaiData = new String[9][3];
        String[] bazhaiLuck = new String[9];
        
        String[] basePalaceNames = {"坎一宫", "坤二宫", "震三宫", "巽四宫", "中五宫", "乾六宫", "兑七宫", "艮八宫", "离九宫"};
        String[] dirToPalace = {"坎一宫", "艮八宫", "震三宫", "巽四宫", "离九宫", "坤二宫", "兑七宫", "乾六宫"};
        
        String[][] baZhaiTable = {
            {"坎", "生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命"},
            {"离", "生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命"},
            {"震", "生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命"},
            {"兑", "生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命"},
            {"巽", "生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命"},
            {"艮", "生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命"},
            {"坤", "生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命"},
            {"乾", "生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命"}
        };
        
        String[][] baZhaiPositions = {
            {"坎", "巽", "震", "艮", "坎", "乾", "兑", "离", "坤"},
            {"离", "乾", "兑", "巽", "离", "艮", "震", "坎", "坤"},
            {"震", "离", "坤", "兑", "震", "巽", "坎", "乾", "艮"},
            {"兑", "艮", "坎", "坤", "兑", "震", "离", "巽", "乾"},
            {"巽", "坎", "艮", "离", "巽", "坤", "乾", "兑", "震"},
            {"艮", "兑", "巽", "坎", "艮", "离", "坤", "震", "乾"},
            {"坤", "震", "离", "兑", "坤", "坎", "艮", "乾", "巽"},
            {"乾", "离", "震", "巽", "乾", "兑", "坎", "坤", "艮"}
        };
        
        int[] baguaIndex = new int[9];
        for (int i = 0; i < 8; i++) {
            if (baZhaiTable[i][0].equals(bagua)) {
                for (int j = 0; j < 9; j++) {
                    String pos = baZhaiPositions[i][j];
                    switch (pos) {
                        case "坎": baguaIndex[j] = 0; break;
                        case "坤": baguaIndex[j] = 1; break;
                        case "震": baguaIndex[j] = 2; break;
                        case "巽": baguaIndex[j] = 3; break;
                        case "中": baguaIndex[j] = 4; break;
                        case "乾": baguaIndex[j] = 5; break;
                        case "兑": baguaIndex[j] = 6; break;
                        case "艮": baguaIndex[j] = 7; break;
                        case "离": baguaIndex[j] = 8; break;
                    }
                }
                break;
            }
        }
        
        String[] names = {"生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命", ""};
        String[] meanings = {"主财", "主寿", "主贵", "主稳", "主病", "主灾", "主祸", "主绝", ""};
        String[] positions = {"开门/卧室", "卧室/书房", "卧室/老人房", "储物/休息", "厕所/厨房", "储物/杂物", "厨房/厕所", "厕所/储物", ""};
        
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                bazhaiData[i][0] = "中五宫";
            } else {
                String dir = directions[i];
                int dirIdx = getDirectionIndex(dir);
                if (dirIdx >= 0 && dirIdx < 8) {
                    bazhaiData[i][0] = dirToPalace[dirIdx];
                } else {
                    bazhaiData[i][0] = basePalaceNames[i];
                }
            }
            bazhaiData[i][1] = "";
            bazhaiData[i][2] = "";
            bazhaiLuck[i] = "平";
        }
        
        for (int i = 0; i < 8; i++) {
            int idx = baguaIndex[i];
            bazhaiData[idx][1] = names[i] + " " + meanings[i];
            bazhaiData[idx][2] = positions[i];
            if (i < 4) {
                bazhaiLuck[idx] = i == 0 ? "大吉" : "吉";
            } else {
                bazhaiLuck[idx] = i == 6 ? "大凶" : "凶";
            }
        }
        
        bazhaiNinePalace.setPalaceData(bazhaiData);
        bazhaiNinePalace.setLuckData(bazhaiLuck);
        bazhaiNinePalace.setDirections(directions);
    }
    
    private int getDirectionIndex(String dir) {
        String[] dirs = {"北", "东北", "东", "东南", "南", "西南", "西", "西北"};
        for (int i = 0; i < dirs.length; i++) {
            if (dirs[i].equals(dir)) {
                return i;
            }
        }
        return -1;
    }
    
    private void setupJiuxingNinePalace(String bagua, String[] directions) {
        String[][] jiuxingData = new String[9][3];
        String[] jiuxingLuck = new String[9];
        
        String[] dirToPalace = {"坎一宫", "艮八宫", "震三宫", "巽四宫", "离九宫", "坤二宫", "兑七宫", "乾六宫"};
        String[] stars = {"一白贪狼", "二黑巨门", "三碧禄存", "四绿文曲", "五黄廉贞", "六白武曲", "七赤破军", "八白左辅", "九紫右弼"};
        String[] lucks = {"大吉", "大凶", "凶", "吉", "大凶", "吉", "凶", "吉", "吉"};
        String[] meanings = {"主财", "主病", "主争", "主文", "主灾", "主官", "主盗", "主富", "主贵"};
        String[] wuxing = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
        
        int[][] jiuxingPositions = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8},
            {8, 0, 1, 2, 3, 4, 5, 6, 7},
            {2, 3, 4, 5, 6, 7, 8, 0, 1},
            {6, 7, 8, 0, 1, 2, 3, 4, 5},
            {3, 4, 5, 6, 7, 8, 0, 1, 2},
            {7, 8, 0, 1, 2, 3, 4, 5, 6},
            {1, 2, 3, 4, 5, 6, 7, 8, 0},
            {5, 6, 7, 8, 0, 1, 2, 3, 4}
        };
        
        int baguaIdx = 0;
        String[] baguaNames = {"坎", "离", "震", "兑", "巽", "艮", "坤", "乾"};
        for (int i = 0; i < 8; i++) {
            if (baguaNames[i].equals(bagua)) {
                baguaIdx = i;
                break;
            }
        }
        
        for (int i = 0; i < 9; i++) {
            int idx = jiuxingPositions[baguaIdx][i];
            if (i == 4) {
                jiuxingData[i][0] = "中五宫";
            } else {
                String dir = directions[i];
                int dirIdx = getDirectionIndex(dir);
                if (dirIdx >= 0 && dirIdx < 8) {
                    jiuxingData[i][0] = dirToPalace[dirIdx];
                } else {
                    jiuxingData[i][0] = "";
                }
            }
            jiuxingData[i][1] = stars[idx] + "(" + wuxing[idx] + ")";
            jiuxingData[i][2] = meanings[idx];
            jiuxingLuck[i] = lucks[idx];
        }
        
        jiuxingNinePalace.setPalaceData(jiuxingData);
        jiuxingNinePalace.setLuckData(jiuxingLuck);
        jiuxingNinePalace.setDirections(directions);
    }
    
    private void setupBamenNinePalace(String bagua, String[] directions) {
        String[][] bamenData = new String[9][3];
        String[] bamenLuck = new String[9];
        
        String[] dirToPalace = {"坎一宫", "艮八宫", "震三宫", "巽四宫", "离九宫", "坤二宫", "兑七宫", "乾六宫"};
        String[] doors = {"休门", "生门", "伤门", "杜门", "景门", "死门", "惊门", "开门", ""};
        String[] lucks = {"吉", "吉", "凶", "平", "平", "凶", "凶", "吉", "平"};
        String[] meanings = {"主休", "主财", "主伤", "主闭", "主景", "主死", "主惊", "主开", ""};
        
        int[][] bamenPositions = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8},
            {4, 5, 6, 7, 0, 1, 2, 3, 8},
            {2, 3, 4, 5, 6, 7, 0, 1, 8},
            {6, 7, 0, 1, 2, 3, 4, 5, 8},
            {3, 4, 5, 6, 7, 0, 1, 2, 8},
            {1, 2, 3, 4, 5, 6, 7, 0, 8},
            {5, 6, 7, 0, 1, 2, 3, 4, 8},
            {7, 0, 1, 2, 3, 4, 5, 6, 8}
        };
        
        int baguaIdx = 0;
        String[] baguaNames = {"坎", "离", "震", "兑", "巽", "艮", "坤", "乾"};
        for (int i = 0; i < 8; i++) {
            if (baguaNames[i].equals(bagua)) {
                baguaIdx = i;
                break;
            }
        }
        
        for (int i = 0; i < 9; i++) {
            int idx = bamenPositions[baguaIdx][i];
            if (i == 4) {
                bamenData[i][0] = "中五宫";
            } else {
                String dir = directions[i];
                int dirIdx = getDirectionIndex(dir);
                if (dirIdx >= 0 && dirIdx < 8) {
                    bamenData[i][0] = dirToPalace[dirIdx];
                } else {
                    bamenData[i][0] = "";
                }
            }
            bamenData[i][1] = doors[idx];
            bamenData[i][2] = meanings[idx];
            bamenLuck[i] = lucks[idx];
        }
        
        bamenNinePalace.setPalaceData(bamenData);
        bamenNinePalace.setLuckData(bamenLuck);
        bamenNinePalace.setDirections(directions);
    }
    
    private void setupTouchListener() {
        luoPanView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float centerX = v.getWidth() / 2f;
                float centerY = v.getHeight() / 2f;
                
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastAngle = getAngle(event.getX(), event.getY(), centerX, centerY);
                        isRotating = true;
                        return true;
                        
                    case MotionEvent.ACTION_MOVE:
                        if (isRotating) {
                            float currentAngle = getAngle(event.getX(), event.getY(), centerX, centerY);
                            float deltaAngle = currentAngle - lastAngle;
                            
                            if (deltaAngle > 180) deltaAngle -= 360;
                            if (deltaAngle < -180) deltaAngle += 360;
                            
                            rotate(deltaAngle);
                            lastAngle = currentAngle;
                        }
                        return true;
                        
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isRotating = false;
                        return true;
                }
                return false;
            }
        });
    }
    
    private float getAngle(float x, float y, float centerX, float centerY) {
        return (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
    }
    
    private void rotate(float degrees) {
        currentRotation += degrees;
        if (currentRotation >= 360) {
            currentRotation -= 360;
        } else if (currentRotation < 0) {
            currentRotation += 360;
        }
        
        luoPanView.setRotation(currentRotation);
        updateInfo();
    }
}