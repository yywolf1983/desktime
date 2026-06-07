package com.example.timedisplay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
    private TextView bazhaiAnalysis;
    private TextView jiuxingAnalysis;
    private TextView bamenAnalysis;
    private TextView buildingAdvice;
    private TextView mingliInfo;
    
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
        bazhaiAnalysis = findViewById(R.id.bazhaiAnalysis);
        jiuxingAnalysis = findViewById(R.id.jiuxingAnalysis);
        bamenAnalysis = findViewById(R.id.bamenAnalysis);
        buildingAdvice = findViewById(R.id.buildingAdvice);
        mingliInfo = findViewById(R.id.mingliInfo);
        
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
        bazhaiAnalysis.setText(getBaZhaiAnalysis(bagua));
        jiuxingAnalysis.setText(getJiuXingAnalysis(bagua));
        bamenAnalysis.setText(getBaMenAnalysis(bagua));
        buildingAdvice.setText(getBuildingAdvice(mountain));
        mingliInfo.setText(getMingLiInfo(mountain));
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
            "桑柘木", "桑柘木", "桑柘木", "桑柘木", "大溪水", "大溪水",
            "大溪水", "大溪水", "覆灯火", "覆灯火", "覆灯火", "覆灯火",
            "天河水", "天河水", "天河水", "天河水", "山下火", "山下火",
            "山下火", "山下火", "钗钏金", "钗钏金", "钗钏金", "钗钏金"
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
            "天德、月德", "天德、月德", "天德、月德", "天德合", "天德合", "天德合",
            "月德", "月德", "月德", "天德", "天德", "天德",
            "天德、月德", "天德、月德", "天德、月德", "天德合", "天德合", "天德合",
            "月德", "月德", "月德", "天德", "天德", "天德"
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
        String[] jixiongMap = {
            "吉", "吉", "吉", "吉", "吉", "吉",
            "吉", "吉", "吉", "吉", "吉", "吉",
            "吉", "吉", "吉", "吉", "吉", "吉",
            "吉", "吉", "吉", "吉", "吉", "吉"
        };
        String[] mountains = {
            "壬", "子", "癸", "丑", "艮", "寅",
            "甲", "卯", "乙", "辰", "巽", "巳",
            "丙", "午", "丁", "未", "坤", "申",
            "庚", "酉", "辛", "戌", "乾", "亥"
        };
        
        for (int i = 0; i < mountains.length; i++) {
            if (mountains[i].equals(mountain)) {
                return jixiongMap[i];
            }
        }
        return "";
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
        String[][] zuoChaoTable = {
            {"子", "午", "坐子向午，北方壬癸水来，南方丙丁火照，水火既济，主富贵双全，人丁兴旺，事业昌盛"},
            {"午", "子", "坐午向子，南方丙丁火来，北方壬癸水照，火水未济，宜用五行调和，子孙昌盛，家道兴隆"},
            {"卯", "酉", "坐卯向酉，东方甲乙木来，西方庚辛金照，金木相克，需用土通关，财源广进，百事亨通"},
            {"酉", "卯", "坐酉向卯，西方庚辛金来，东方甲乙木照，金克木，需用火化解，家道兴隆，富贵绵长"},
            {"辰", "戌", "坐辰向戌，东南水库来，西北火库照，水土相混，需木疏通，百事亨通，丁财两旺"},
            {"戌", "辰", "坐戌向辰，西北火库来，东南水库照，火土相生，主丁财两旺，富贵绵长，福寿安康"},
            {"丑", "未", "坐丑向未，东北金库来，西南木库照，土土相助，主根基稳固，子孙满堂，福禄寿全"},
            {"未", "丑", "坐未向丑，西南木库来，东北金库照，土金相生，主贵气临门，福禄寿全，吉祥如意"},
            {"艮", "坤", "坐艮向坤，东北少男位，西南老母位，阴阳相配，人丁兴旺，福寿安康，家宅康宁"},
            {"坤", "艮", "坐坤向艮，西南老母位，东北少男位，阴阳得位，福寿绵长，家宅康宁，子孙满堂"},
            {"巽", "乾", "坐巽向乾，东南长女位，西北老父位，风天小畜，贵人相助，事业发达，富贵荣华"},
            {"乾", "巽", "坐乾向巽，西北老父位，东南长女位，天风姤，家道兴隆，富贵荣华，声名远播"},
            {"寅", "申", "坐寅向申，东北阳木，西南阳金，金木相战，需水调和，财源广进，事业有成"},
            {"申", "寅", "坐申向寅，西南阳金，东北阳木，金伐木，需火制金，官运亨通，步步高升"},
            {"巳", "亥", "坐巳向亥，东南阴火，西北阴水，水火相激，需土通关，家宅安宁，百事顺遂"},
            {"亥", "巳", "坐亥向巳，西北阴水，东南阴火，水克火，需木通关，丁财两旺，家道荣昌"},
            {"甲", "庚", "坐甲向庚，东方阳木，西方阳金，金克木，宜用火泄金，文运昌盛，学业有成"},
            {"庚", "甲", "坐庚向甲，西方阳金，东方阳木，金强木弱，宜用水化金，武运亨通，权势显赫"},
            {"乙", "辛", "坐乙向辛，东方阴木，西方阴金，金克木，宜用土生金，财源茂盛，富贵双全"},
            {"辛", "乙", "坐辛向乙，西方阴金，东方阴木，金多木少，宜用木助，人丁兴旺，福寿安康"},
            {"丙", "壬", "坐丙向壬，南方阳火，北方阳水，水克火，宜用木生火，官贵临门，步步高升"},
            {"壬", "丙", "坐壬向丙，北方阳水，南方阳火，水火既济，富贵双全，声名远播，福禄寿全"},
            {"丁", "癸", "坐丁向癸，南方阴火，北方阴水，水克火，宜用土止水，福寿绵长，家道荣昌"},
            {"癸", "丁", "坐癸向丁，北方阴水，南方阴火，水火相济，丁财两旺，家道荣昌，富贵双全"}
        };
        
        for (String[] entry : zuoChaoTable) {
            if (entry[0].equals(zuoShan) && entry[1].equals(chaoXiang)) {
                return entry[2];
            }
        }
        
        return "坐" + zuoShan + "向" + chaoXiang + "，需结合具体地形分析";
    }
    
    private String getShuiShaAnalysis(String zuoShan) {
        String[][] shuiShaTable = {
            {"子", "北方宜有水，南方宜有山，东方青龙宜高耸，西方白虎宜柔顺，明堂开阔主富贵，后山高耸主丁旺"},
            {"丑", "东北宜有山，西南宜有水，艮方砂秀主贵，坤方水旺财，龙虎相配丁财旺，水口紧锁富贵长"},
            {"寅", "东北宜有水，西南宜有山，水来青龙方，砂见白虎位，山水相映福寿全，明堂端正子孙贤"},
            {"卯", "东方宜有水，西方宜有山，青龙得水主贵，白虎有砂旺财，山环水绕出英豪，玉带缠腰富贵来"},
            {"辰", "东南宜有山，西北宜有水，水库得砂主富，天门见水主贵，水砂相配主康宁，文昌得位出贤能"},
            {"巳", "东南宜有水，西北宜有山，巽方水来主秀，乾方砂起主贵，秀气临门出贤才，贵人相助步步高"},
            {"午", "南方宜有水，北方宜有山，朱雀得水主文，玄武有山主寿，文武双全福禄长，贵人相助万事昌"},
            {"未", "西南宜有山，东北宜有水，坤方砂厚主富，艮方水秀主贵，厚砂秀水出贵人，家宅康宁福满堂"},
            {"申", "西南宜有水，东北宜有山，坤方水来主财，艮方砂起主丁，财丁两旺家道兴，子孙昌盛代代荣"},
            {"酉", "西方宜有水，东方宜有山，白虎得水主富，青龙有砂主贵，金水相生富贵来，家道兴隆传万代"},
            {"戌", "西北宜有山，东南宜有水，火库得砂主贵，巽方水来主秀，贵秀两全福寿臻，金榜题名耀门庭"},
            {"亥", "西北宜有水，东南宜有山，天门得水主智，地户有砂主贵，智慧显贵代代传，书香门第出状元"},
            {"壬", "北方宜有山，南方宜有水，阳水得砂主贵，丙丁见水主富，贵富双全大吉昌，子孙满堂福寿长"},
            {"癸", "北方宜有水，南方宜有山，阴水得水主智，午位有砂主贵，智慧显贵出英才，金榜题名乐开怀"},
            {"甲", "东方宜有山，西方宜有水，阳木得砂主贵，庚辛见水主富，贵富兼全福禄寿，家宅康宁万事休"},
            {"乙", "东方宜有水，西方宜有山，阴木得水主秀，酉位有砂主贵，秀丽显贵子孙贤，福禄寿喜满人间"},
            {"丙", "南方宜有山，北方宜有水，阳火得砂主贵，壬癸见水主富，贵富双全福满堂，子孙昌盛万年长"},
            {"丁", "南方宜有水，北方宜有山，阴火得水主文，子位有砂主贵，文贵双全书香第，世代书香传不息"},
            {"庚", "西方宜有山，东方宜有水，阳金得砂主贵，甲乙见水主富，贵富并臻吉祥地，家宅兴旺永无比"},
            {"辛", "西方宜有水，东方宜有山，阴金得水主秀，卯位有砂主贵，秀美显贵富贵家，福禄寿喜人人夸"},
            {"艮", "东北宜有山，西南宜有水，少男得位主丁，老母见水主财，丁财两旺吉祥宅，子孙昌盛传万代"},
            {"坤", "西南宜有山，东北宜有水，老母得位主富，少男见水主贵，富贵双全安乐窝，子孙满堂福气多"},
            {"巽", "东南宜有山，西北宜有水，长女得位主秀，老父见水主智，秀智双全吉祥地，家宅康宁万事吉"},
            {"乾", "西北宜有山，东南宜有水，老父得位主贵，长女见水主文，贵文双全书香门，世代书香传子孙"}
        };
        
        for (String[] entry : shuiShaTable) {
            if (entry[0].equals(zuoShan)) {
                return entry[1];
            }
        }
        
        return "";
    }
    
    private String getBaZhaiAnalysis(String bagua) {
        String[][] baZhaiTable = {
            {"坎", "生气在巽，天医在震，延年在艮，伏位在坎，祸害在乾，六煞在兑，五鬼在离，绝命在坤"},
            {"离", "生气在乾，天医在兑，延年在巽，伏位在离，祸害在艮，六煞在震，五鬼在坎，绝命在坤"},
            {"震", "生气在离，天医在坤，延年在兑，伏位在震，祸害在巽，六煞在坎，五鬼在乾，绝命在艮"},
            {"兑", "生气在艮，天医在坎，延年在坤，伏位在兑，祸害在震，六煞在离，五鬼在巽，绝命在乾"},
            {"巽", "生气在坎，天医在艮，延年在离，伏位在巽，祸害在坤，六煞在乾，五鬼在兑，绝命在震"},
            {"艮", "生气在兑，天医在巽，延年在坎，伏位在艮，祸害在离，六煞在坤，五鬼在震，绝命在乾"},
            {"坤", "生气在震，天医在离，延年在兑，伏位在坤，祸害在坎，六煞在艮，五鬼在乾，绝命在巽"},
            {"乾", "生气在离，天医在震，延年在巽，伏位在乾，祸害在兑，六煞在坎，五鬼在坤，绝命在艮"}
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
            {"坎", "一白贪狼星在坎宫，二黑巨门星在坤宫，三碧禄存星在震宫，四绿文曲星在巽宫，五黄廉贞星在中宫，六白武曲星在乾宫，七赤破军星在兑宫，八白左辅星在艮宫，九紫右弼星在离宫"},
            {"离", "九紫右弼星在离宫，一白贪狼星在坎宫，二黑巨门星在坤宫，三碧禄存星在震宫，四绿文曲星在巽宫，五黄廉贞星在中宫，六白武曲星在乾宫，七赤破军星在兑宫，八白左辅星在艮宫"},
            {"震", "三碧禄存星在震宫，四绿文曲星在巽宫，五黄廉贞星在中宫，六白武曲星在乾宫，七赤破军星在兑宫，八白左辅星在艮宫，九紫右弼星在离宫，一白贪狼星在坎宫，二黑巨门星在坤宫"},
            {"兑", "七赤破军星在兑宫，八白左辅星在艮宫，九紫右弼星在离宫，一白贪狼星在坎宫，二黑巨门星在坤宫，三碧禄存星在震宫，四绿文曲星在巽宫，五黄廉贞星在中宫，六白武曲星在乾宫"},
            {"巽", "四绿文曲星在巽宫，五黄廉贞星在中宫，六白武曲星在乾宫，七赤破军星在兑宫，八白左辅星在艮宫，九紫右弼星在离宫，一白贪狼星在坎宫，二黑巨门星在坤宫，三碧禄存星在震宫"},
            {"艮", "八白左辅星在艮宫，九紫右弼星在离宫，一白贪狼星在坎宫，二黑巨门星在坤宫，三碧禄存星在震宫，四绿文曲星在巽宫，五黄廉贞星在中宫，六白武曲星在乾宫，七赤破军星在兑宫"},
            {"坤", "二黑巨门星在坤宫，三碧禄存星在震宫，四绿文曲星在巽宫，五黄廉贞星在中宫，六白武曲星在乾宫，七赤破军星在兑宫，八白左辅星在艮宫，九紫右弼星在离宫，一白贪狼星在坎宫"},
            {"乾", "六白武曲星在乾宫，七赤破军星在兑宫，八白左辅星在艮宫，九紫右弼星在离宫，一白贪狼星在坎宫，二黑巨门星在坤宫，三碧禄存星在震宫，四绿文曲星在巽宫，五黄廉贞星在中宫"}
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
            {"坎", "休门在坎，生门在艮，伤门在震，杜门在巽，景门在离，死门在坤，惊门在兑，开门在乾"},
            {"离", "景门在离，死门在坤，惊门在兑，开门在乾，休门在坎，生门在艮，伤门在震，杜门在巽"},
            {"震", "伤门在震，杜门在巽，景门在离，死门在坤，惊门在兑，开门在乾，休门在坎，生门在艮"},
            {"兑", "惊门在兑，开门在乾，休门在坎，生门在艮，伤门在震，杜门在巽，景门在离，死门在坤"},
            {"巽", "杜门在巽，景门在离，死门在坤，惊门在兑，开门在乾，休门在坎，生门在艮，伤门在震"},
            {"艮", "生门在艮，伤门在震，杜门在巽，景门在离，死门在坤，惊门在兑，开门在乾，休门在坎"},
            {"坤", "死门在坤，惊门在兑，开门在乾，休门在坎，生门在艮，伤门在震，杜门在巽，景门在离"},
            {"乾", "开门在乾，休门在坎，生门在艮，伤门在震，杜门在巽，景门在离，死门在坤，惊门在兑"}
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