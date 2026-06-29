package com.example.timedisplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class LuoPanActivity extends Activity implements SensorEventListener {
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
    private TextView bazhaiDesc;
    private TextView jiuxingDesc;
    private TextView bamenDesc;
    private TextView buildingAdvice;
    private TextView mingliInfo;
    private TextView summaryInfo;
    
    private float currentRotation = 0;
    private float lastAngle = 0;
    private boolean isRotating = false;
    private boolean autoMode = true;
    private boolean hasCompass = false;
    
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] accelerometerValues = new float[3];
    private float[] magnetometerValues = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    
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
        bazhaiDesc = findViewById(R.id.bazhaiDesc);
        jiuxingDesc = findViewById(R.id.jiuxingDesc);
        bamenDesc = findViewById(R.id.bamenDesc);
        buildingAdvice = findViewById(R.id.buildingAdvice);
        mingliInfo = findViewById(R.id.mingliInfo);
        summaryInfo = findViewById(R.id.summaryInfo);
        
        if (savedInstanceState != null) {
            currentRotation = savedInstanceState.getFloat(KEY_ROTATION, 0);
            luoPanView.setRotation(currentRotation);
        }
        
        updateInfo();
        setupTouchListener();
        setupSensor();
    }
    
    private void setupSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (accelerometer != null && magnetometer != null) {
                hasCompass = true;
            }
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(KEY_ROTATION, currentRotation);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (hasCompass && autoMode) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!autoMode) return;
        
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerValues, 0, 3);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerValues, 0, 3);
        }
        
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues);
        SensorManager.getOrientation(rotationMatrix, orientation);
        
        float azimuth = (float) Math.toDegrees(orientation[0]);
        if (azimuth < 0) {
            azimuth += 360;
        }
        
        currentRotation = -azimuth;
        luoPanView.setRotation(currentRotation);
        updateInfo();
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
        
        if (bazhaiDesc != null) {
            bazhaiDesc.setText(android.text.Html.fromHtml(getBazhaiDesc(bagua), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        if (jiuxingDesc != null) {
            jiuxingDesc.setText(android.text.Html.fromHtml(getJiuxingDesc(bagua), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        if (bamenDesc != null) {
            bamenDesc.setText(android.text.Html.fromHtml(getBamenDesc(bagua), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        
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
            "桑柘木", "海中金", "涧下水", "涧下水", "壁上土", "炉中火",
            "大溪水", "大溪水", "炉中火", "覆灯火", "白蜡金", "大林木",
            "天河水", "天上火", "路旁土", "天河水", "路旁土", "泉中水",
            "松柏木", "石榴木", "山下火", "山头火", "钗钏金", "山头火"
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
            "天德", "阳刃", "玉堂", "天德合", "月德", "天医",
            "月德", "桃花", "天德", "华盖", "太阴", "天乙",
            "天德", "桃花", "玉堂", "天德合", "月德", "天医",
            "月德", "桃花", "玉堂", "华盖", "太阴", "天乙"
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
        String[][] guiZangMap = {
            {"壬", "坤"}, {"子", "坤"}, {"癸", "艮"},
            {"丑", "坤"}, {"艮", "坎"}, {"寅", "艮"},
            {"甲", "乾"}, {"卯", "坤"}, {"乙", "巽"},
            {"辰", "巽"}, {"巽", "震"}, {"巳", "巽"},
            {"丙", "离"}, {"午", "离"}, {"丁", "兑"},
            {"未", "兑"}, {"坤", "艮"}, {"申", "兑"},
            {"庚", "乾"}, {"酉", "坎"}, {"辛", "巽"},
            {"戌", "乾"}, {"乾", "坎"}, {"亥", "巽"}
        };
        
        for (String[] entry : guiZangMap) {
            if (entry[0].equals(mountain)) {
                return entry[1];
            }
        }
        return "";
    }
    
    private String getMountainTip(String mountain) {
        String[] tips = {
            "壬: 北方阳水，如江河湖海，主智巧变通。坐此山宜向南开门，纳火气调和，忌北方开敞泄水气。",
            "子: 正北水位，阴阳交界之处，万物根基所在。坐此山气场沉稳，宜作主卧，忌南方正对尖角。",
            "癸: 北方阴水，如雨露泉涧，主柔美细腻。坐此山利文昌学业，宜设书房，忌北方堆杂物阻水气。",
            "丑: 东北金库，湿土藏金，孕育生机之机。坐此山利财运积蓄，宜设财务室，忌东北方动土破气。",
            "艮: 东北艮位，少男之位，山陵高岗。坐此山主丁旺，宜设子孙房，忌东北开门艮土闭塞。",
            "寅: 东北木位，阳木生发，如参天大树。坐此山朝气蓬勃，利事业开拓，忌西南冲射伐木气。",
            "甲: 东方阳木，栋梁之才，阳气初升。坐此山利领导决策，宜设办公室，忌西方金气克伐木身。",
            "卯: 正东木位，日出之所，生机盎然。坐此山家运旺盛，利长子发展，忌西方开门金克木损。",
            "乙: 东方阴木，花草藤萝，柔和之气。坐此山利人缘桃花，宜设客厅，忌西方煞气破坏柔和。",
            "辰: 东南水库，湿土蓄水，蓄藏之地。坐此山利财库积蓄，宜设储物空间，忌西北开门泄库气。",
            "巽: 东南巽位，长女之位，风林草木。坐此山利文昌名声，宜设书房阳台，忌西北金气断木根。",
            "巳: 东南火位，阴火初盛，初夏之气。坐此山利文秀之才，宜设卧室书房，忌西北开门水火激。",
            "丙: 南方阳火，太阳烈火，阳气盛极。坐此山声名显赫，利仕途官运，忌北方水气直冲火位。",
            "午: 正南火位，日中之位，鼎盛辉煌。坐此山阳气最旺，利事业巅峰，忌南方开门火气过泄。",
            "丁: 南方阴火，灯烛星光，温暖柔和。坐此山利文思创意，宜设工作室，忌南方火气过盛失眠。",
            "未: 西南木库，燥土藏木，滋养之地。坐此山利家宅安稳，宜设花园，忌西南开门土气壅塞。",
            "坤: 西南坤位，老母之位，大地母亲。坐此山利母运家宅，宜设长辈房，忌西南开挖破地气。",
            "申: 西南金位，阳金萧杀，刀剑兵器。坐此山利武职权威，宜设客厅库房，忌东北土气困金。",
            "庚: 西方阳金，金银铜铁，萧杀之气。坐此山利财权并得，宜设财务室，忌西方开门金气过泄。",
            "酉: 正西金位，日落之所，月照秋凉。坐此山利艺术才华，宜设工作室，忌西方开门口舌是非。",
            "辛: 西方阴金，珠玉宝石，柔和之金。坐此山利秀贵之名，宜设卧室书房，忌西方金气过盛忧郁。",
            "戌: 西北火库，燥土藏火，收藏之地。坐此山利功名积蓄，宜设老人房，忌西北开门火库气泄。",
            "乾: 西北乾位，老父之位，天门高天。坐此山利权威地位，宜设长辈房办公室，忌西北开挖泄天气。",
            "亥: 西北水位，阴水天门，长生之地。坐此山利智慧长寿，宜设书房卧室，忌西北金水过盛寒湿。"
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
    
    private String getBazhaiDesc(String bagua) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>八宅以坐山为伏位，将住宅分为八方位。</font><br/><br/>");
        
        String[][] bazhaiData = {
            {"坎", "巽", "震", "艮", "乾", "兑", "离", "坤"},
            {"离", "乾", "兑", "巽", "艮", "震", "坎", "坤"},
            {"震", "离", "坤", "兑", "巽", "坎", "乾", "艮"},
            {"兑", "艮", "坎", "坤", "震", "离", "巽", "乾"},
            {"巽", "坎", "艮", "离", "坤", "乾", "兑", "震"},
            {"艮", "兑", "巽", "坎", "离", "坤", "震", "乾"},
            {"坤", "震", "离", "兑", "坎", "艮", "乾", "巽"},
            {"乾", "离", "震", "巽", "兑", "坎", "坤", "艮"}
        };
        
        String[] baguaNames = {"坎", "离", "震", "兑", "巽", "艮", "坤", "乾"};
        String[] names = {"生气", "天医", "延年", "伏位", "祸害", "六煞", "五鬼", "绝命"};
        String[] luckColors = {"#90EE90", "#98FB98", "#ADFF2F", "#FFD700", "#FFA07A", "#FF8C00", "#FF6347", "#DC143C"};
        String[] baguaDirs = {"北", "南", "东", "西", "东南", "东北", "西南", "西北"};
        String[] bazhaiExplain = {
            "最旺之位，主财运人丁，宜开大门、设主卧、办公",
            "次吉之位，主健康长寿，宜设老人房、书房、餐厅",
            "中吉之位，主事业贵人，宜设办公室、客厅",
            "平稳之位，主家宅安宁，宜设客厅、玄关、储物",
            "小凶之位，主口舌疾病，宜作厨房、卫生间压煞",
            "次凶之位，主官非桃花劫，宜作储物间、洗衣房",
            "大凶之位，主火灾盗匪，宜作杂物间、厕所镇煞",
            "极凶之位，主绝嗣大祸，宜作车库、仓库镇压"
        };
        
        int idx = 0;
        for (int i = 0; i < 8; i++) {
            if (baguaNames[i].equals(bagua)) {
                idx = i;
                break;
            }
        }
        
        desc.append("<font color='#FFD700'><b>吉位</b></font><br/>");
        for (int i = 0; i < 4; i++) {
            String pos = bazhaiData[idx][i];
            String name = names[i];
            int posIdx = getBaguaIndex(pos);
            desc.append("<font color='").append(luckColors[i]).append("'>").append(name).append("</font> ");
            desc.append("<font color='#98D8F0'>(").append(baguaDirs[posIdx]).append(")</font> ");
            desc.append("<font color='#8899AA'>").append(bazhaiExplain[i]).append("</font>");
            if (i < 3) desc.append("<br/>");
        }
        desc.append("<br/><br/>");
        
        desc.append("<font color='#FF6B6B'><b>凶位</b></font><br/>");
        for (int i = 4; i < 8; i++) {
            String pos = bazhaiData[idx][i];
            String name = names[i];
            int posIdx = getBaguaIndex(pos);
            desc.append("<font color='").append(luckColors[i]).append("'>").append(name).append("</font> ");
            desc.append("<font color='#98D8F0'>(").append(baguaDirs[posIdx]).append(")</font> ");
            desc.append("<font color='#8899AA'>").append(bazhaiExplain[i]).append("</font>");
            if (i < 7) desc.append("<br/>");
        }
        
        return desc.toString();
    }
    
    private int getBaguaIndex(String bagua) {
        String[] baguaNames = {"坎", "离", "震", "兑", "巽", "艮", "坤", "乾"};
        for (int i = 0; i < baguaNames.length; i++) {
            if (baguaNames[i].equals(bagua)) {
                return i;
            }
        }
        return 0;
    }
    
    private String getJiuxingDesc(String bagua) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>九星按洛书顺序飞布九宫。</font><br/><br/>");
        
        String[] stars = {"一白贪狼", "二黑巨门", "三碧禄存", "四绿文曲", "五黄廉贞", "六白武曲", "七赤破军", "八白左辅", "九紫右弼"};
        String[] luckColors = {"#90EE90", "#DC143C", "#FF6347", "#98FB98", "#DC143C", "#ADFF2F", "#FF6347", "#98FB98", "#ADFF2F"};
        String[] wuxing = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
        String[] starExplain = {
            "财星。吉利经商考试、求名投资；忌则水泛成灾，漂泊不定",
            "病星。凶主疾病灾祸、脾胃皮肤；宜静不宜动，可放置铜器化解",
            "是非星。凶主争斗官非、口舌纠纷；宜静守，可放红色物品泄木气",
            "文昌星。吉利考试写作、升职求学；忌则桃花泛滥、心神不定",
            "灾煞星。大凶，最宜压制，易招横祸血光；宜放铜铃、五帝钱化解",
            "官星。吉利从政升职、诉讼维权；忌则刚愎自用、孤高寡合",
            "破败星。凶主盗贼破财、火灾口舌；宜放静水化解金煞之气",
            "财星。吉利置业经商、投资理财；忌则贪财好利、田宅纠纷",
            "喜庆星。吉利婚嫁添丁、名声远扬；忌则虚荣浮华、喜事落空"
        };
        
        int[][] jiuxingPositions = {
            {3, 6, 2, 0, 4, 5, 7, 1, 8},
            {3, 1, 7, 6, 4, 2, 8, 0, 5},
            {3, 8, 0, 1, 4, 7, 5, 6, 2},
            {3, 5, 6, 7, 4, 8, 2, 0, 1},
            {3, 0, 8, 5, 4, 2, 1, 7, 6},
            {3, 6, 7, 8, 4, 0, 1, 2, 5},
            {3, 2, 1, 0, 4, 8, 5, 7, 6},
            {3, 5, 1, 2, 4, 7, 6, 8, 0}
        };
        
        String[] baguaNames = {"坎", "离", "震", "兑", "巽", "艮", "坤", "乾"};
        String[] directions = {"北", "东北", "东", "东南", "中", "西北", "西", "西南", "南"};
        
        int idx = 0;
        for (int i = 0; i < 8; i++) {
            if (baguaNames[i].equals(bagua)) {
                idx = i;
                break;
            }
        }
        
        desc.append("<font color='#FFD700'><b>吉星</b></font><br/>");
        for (int i = 0; i < 9; i++) {
            int starIdx = jiuxingPositions[idx][i];
            String star = stars[starIdx];
            if (starIdx == 0 || starIdx == 3 || starIdx == 5 || starIdx == 7 || starIdx == 8) {
                desc.append("<font color='").append(luckColors[starIdx]).append("'>").append(star).append("</font> ");
                desc.append("<font color='#98D8F0'>(").append(directions[i]).append("·").append(wuxing[starIdx]).append(")</font> ");
                desc.append("<font color='#8899AA'>").append(starExplain[starIdx]).append("</font>");
                desc.append("<br/>");
            }
        }
        
        desc.append("<br/><font color='#FF6B6B'><b>凶星</b></font><br/>");
        for (int i = 0; i < 9; i++) {
            int starIdx = jiuxingPositions[idx][i];
            String star = stars[starIdx];
            if (starIdx == 1 || starIdx == 2 || starIdx == 4 || starIdx == 6) {
                desc.append("<font color='").append(luckColors[starIdx]).append("'>").append(star).append("</font> ");
                desc.append("<font color='#98D8F0'>(").append(directions[i]).append("·").append(wuxing[starIdx]).append(")</font> ");
                desc.append("<font color='#8899AA'>").append(starExplain[starIdx]).append("</font>");
                desc.append("<br/>");
            }
        }
        
        return desc.toString();
    }
    
    private String getBamenDesc(String bagua) {
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#8899AA'>八门随九星飞布，休生开为吉门，死伤惊为凶门，杜景为平门。</font><br/><br/>");
        
        String[] doors = {"休门", "生门", "伤门", "杜门", "景门", "死门", "惊门", "开门"};
        String[] luckColors = {"#98FB98", "#90EE90", "#FF6347", "#FFD700", "#FFD700", "#DC143C", "#FF6347", "#ADFF2F"};
        String[] doorExplain = {
            "吉门。宜休息养身、会客议事，百事皆宜，利见贵人",
            "大吉门。最利求财开业、经商投资，谋事得利，八门之首",
            "凶门。主损伤争斗、出行不利，易有伤灾官非，宜避之",
            "平门。主闭塞隐藏，宜守不宜攻，适合隐藏守静、密谈",
            "平门。主名声文书，利考试求名，吉凶参半，宜谨慎行事",
            "大凶门。主衰败丧事，百事不宜，宜镇不宜动",
            "凶门。主惊恐怪异，虚惊口舌，官非诉讼，宜静不宜动",
            "吉门。主通达顺利，贵人相助，宜开业求职签约"
        };
        
        int[][] bamenPositions = {
            {0, 1, 2, 3, 4, 5, 6, 7},
            {4, 5, 6, 7, 0, 1, 2, 3},
            {2, 3, 4, 5, 6, 7, 0, 1},
            {6, 7, 0, 1, 2, 3, 4, 5},
            {3, 4, 5, 6, 7, 0, 1, 2},
            {1, 2, 3, 4, 5, 6, 7, 0},
            {5, 6, 7, 0, 1, 2, 3, 4},
            {7, 0, 1, 2, 3, 4, 5, 6}
        };
        
        String[] baguaNames = {"坎", "离", "震", "兑", "巽", "艮", "坤", "乾"};
        String[] directions = {"北", "西南", "东", "东南", "西北", "西", "东北", "南"};
        
        int idx = 0;
        for (int i = 0; i < 8; i++) {
            if (baguaNames[i].equals(bagua)) {
                idx = i;
                break;
            }
        }
        
        desc.append("<font color='#FFD700'><b>吉门</b></font><br/>");
        for (int i = 0; i < 8; i++) {
            int doorIdx = bamenPositions[idx][i];
            String door = doors[doorIdx];
            if (doorIdx == 0 || doorIdx == 1 || doorIdx == 7) {
                desc.append("<font color='").append(luckColors[doorIdx]).append("'>").append(door).append("</font> ");
                desc.append("<font color='#98D8F0'>(").append(directions[i]).append(")</font> ");
                desc.append("<font color='#8899AA'>").append(doorExplain[doorIdx]).append("</font>");
                desc.append("<br/>");
            }
        }
        
        desc.append("<br/><font color='#FFD700'><b>平门</b></font><br/>");
        for (int i = 0; i < 8; i++) {
            int doorIdx = bamenPositions[idx][i];
            String door = doors[doorIdx];
            if (doorIdx == 3 || doorIdx == 4) {
                desc.append("<font color='").append(luckColors[doorIdx]).append("'>").append(door).append("</font> ");
                desc.append("<font color='#98D8F0'>(").append(directions[i]).append(")</font> ");
                desc.append("<font color='#8899AA'>").append(doorExplain[doorIdx]).append("</font>");
                desc.append("<br/>");
            }
        }
        
        desc.append("<br/><font color='#FF6B6B'><b>凶门</b></font><br/>");
        for (int i = 0; i < 8; i++) {
            int doorIdx = bamenPositions[idx][i];
            String door = doors[doorIdx];
            if (doorIdx == 2 || doorIdx == 5 || doorIdx == 6) {
                desc.append("<font color='").append(luckColors[doorIdx]).append("'>").append(door).append("</font> ");
                desc.append("<font color='#98D8F0'>(").append(directions[i]).append(")</font> ");
                desc.append("<font color='#8899AA'>").append(doorExplain[doorIdx]).append("</font>");
                desc.append("<br/>");
            }
        }
        
        return desc.toString();
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
            {"离", "【坐南】吉门：休门在南方(离)，生门在东南(巽)，开门在东方(震)。平门：景门在北方(坎)，杜门在东北(艮)。凶门：伤门在西北(乾)，惊门在西方(兑)，死门在西南(坤)"},
            {"震", "【坐东】吉门：休门在东方(震)，生门在北方(坎)，开门在东北(艮)。平门：景门在南方(离)，杜门在东南(巽)。凶门：伤门在西方(兑)，惊门在西南(坤)，死门在西北(乾)"},
            {"兑", "【坐西】吉门：休门在西方(兑)，生门在西北(乾)，开门在西南(坤)。平门：景门在北方(坎)，杜门在东北(艮)。凶门：伤门在东方(震)，惊门在东南(巽)，死门在南方(离)"},
            {"巽", "【坐东南】吉门：休门在东南(巽)，生门在南方(离)，开门在西南(坤)。平门：景门在西方(兑)，杜门在西北(乾)。凶门：伤门在东北(艮)，惊门在北方(坎)，死门在东方(震)"},
            {"艮", "【坐东北】吉门：休门在东北(艮)，生门在西北(乾)，开门在北方(坎)。平门：景门在东方(震)，杜门在东南(巽)。凶门：伤门在南方(离)，惊门在西南(坤)，死门在西方(兑)"},
            {"坤", "【坐西南】吉门：休门在西南(坤)，生门在西方(兑)，开门在西北(乾)。平门：景门在北方(坎)，杜门在东北(艮)。凶门：伤门在东方(震)，惊门在东南(巽)，死门在南方(离)"},
            {"乾", "【坐西北】吉门：休门在西北(乾)，生门在西方(兑)，开门在西南(坤)。平门：景门在南方(离)，杜门在东南(巽)。凶门：伤门在东方(震)，惊门在东北(艮)，死门在北方(坎)"}
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
            {"子", "【大门】宜开南方(离)或东南(巽)，忌开北方(坎)。南方纳阳光之气，东南为文昌方。\n【主卧】宜设东方(震)或南方(离)，东方主长子，南方主阳气旺盛。\n【书房】宜设东南(巽)，文昌星位，利于学业。\n【厨房】宜设东南(巽)或南方(离)，木火相生。\n【禁忌】北方不宜开门，坎水泄气，主家运衰退。"},
            {"丑", "【大门】宜开西南(坤)或南方(离)，忌开东北(艮)。西南为坤土，聚气生财。\n【客厅】宜宽敞明亮，聚气生财，主家庭和睦。\n【财位】宜设在西南(坤)，坤方得地，财运亨通。\n【老人房】宜设东北(艮)，艮为少男，主健康长寿。\n【禁忌】东北不宜开门，艮土闭塞，主诸事不顺。"},
            {"寅", "【大门】宜开南方(离)或东方(震)，忌开西南(坤)。南方火生土，东方木旺。\n【书房】宜设东方(震)，文昌得位，学业有成。\n【贵人位】北方(坎)，水来生木，贵人相助。\n【儿童房】宜设东方(震)，震为长男，生机勃勃。\n【禁忌】西南不宜开门，坤土克木，主阻滞。"},
            {"卯", "【大门】宜开南方(离)或北方(坎)，忌开西方(兑)。南方火生木，北方水生木。\n【厨房】宜设东南(巽)，火生土旺，家庭和睦。\n【桃花位】南方(离)，人缘旺盛，婚姻美满。\n【玄关】宜设东方(震)，紫气东来，吉祥如意。\n【禁忌】西方不宜开门，金克木，主口舌是非。"},
            {"辰", "【大门】宜开北方(坎)或东方(震)，忌开西北(乾)。北方水旺财聚，东方木火通明。\n【财位】宜设在北方(坎)，水旺财聚，财源广进。\n【文昌位】东方(震)，木火通明，学业有成。\n【书房】宜设东南(巽)，文风昌盛。\n【禁忌】西北不宜开门，乾金克木，主事业不顺。"},
            {"巳", "【大门】宜开南方(离)或北方(坎)，忌开西北(乾)。南方火旺，北方水济。\n【卧室】宜设南方(离)，阳光充足，身体健康。\n【事业位】东方(震)，木来生火，事业兴旺。\n【阳台】宜设南方(离)，采光良好。\n【禁忌】西北不宜开门，乾金泄火，主财运不佳。"},
            {"午", "【大门】宜开北方(坎)或东南(巽)，忌开南方(离)。北方水既济，东南木生火。\n【神位】宜设在北方(坎)，敬天祭祖，福禄寿全。\n【文昌位】西方(兑)，金白水清，学业有成。\n【书房】宜设西方(兑)，文风鼎盛。\n【禁忌】南方不宜开门，离火过旺，主家宅不安。"},
            {"未", "【大门】宜开东北(艮)或北方(坎)，忌开西南(坤)。东北艮土，北方水旺。\n【花园】宜设在南方(离)，生机勃勃，人丁兴旺。\n【贵人位】北方(坎)，天一生水，贵人相助。\n【储物间】宜设西南(坤)，藏风聚气。\n【禁忌】西南不宜开门，坤土过重，主阻滞。"},
            {"申", "【大门】宜开北方(坎)或西方(兑)，忌开东北(艮)。北方水旺财，西方金旺。\n【库房】宜设在西北(乾)，金生水旺，财运亨通。\n【财位】西方(兑)，金白水清，财源广进。\n【书房】宜设东方(震)，木火通明。\n【禁忌】东北不宜开门，艮土生金过盛，主压力大。"},
            {"酉", "【大门】宜开东方(震)或南方(离)，忌开西方(兑)。东方木克土，南方火克金。\n【财位】宜设在南方(离)，火生土旺，财运亨通。\n【贵人位】东方(震)，木来克土，贵人扶持。\n【厨房】宜设南方(离)，火克金，家庭和睦。\n【禁忌】西方不宜开门，金气过盛，主口舌是非。"},
            {"戌", "【大门】宜开东南(巽)或南方(离)，忌开西北(乾)。东南木生火，南方火旺。\n【老人房】宜设在东北(艮)，艮位得宜，健康长寿。\n【文昌位】东方(震)，水木清华，学业有成。\n【客厅】宜宽敞，聚气生财。\n【禁忌】西北不宜开门，乾金泄火，主财运不佳。"},
            {"亥", "【大门】宜开东南(巽)或东方(震)，忌开西北(乾)。东南木旺，东方木生火。\n【儿童房】宜设在东方(震)，震位主长，聪明伶俐。\n【贵人位】南方(离)，火来暖水，贵人相助。\n【书房】宜设东南(巽)，文昌得位。\n【禁忌】西北不宜开门，乾金生水过盛，主寒湿。"},
            {"壬", "【大门】宜开南方(离)，忌开北方(坎)。南方火既济，水火调和。\n【文昌位】东南(巽)，利于学业，金榜题名。\n【财位】西方(兑)，金白水清，财运亨通。\n【卧室】宜设东方(震)，阳气充足。\n【禁忌】北方不宜开门，阳水过盛，主身体欠佳。"},
            {"癸", "【大门】宜开南方(离)，忌开北方(坎)。南方火既济，温暖阴水。\n【财位】西南(坤)，坤方得地，财源广进。\n【桃花位】南方(离)，人缘旺盛，婚姻美满。\n【卧室】宜设南方(离)，阳光温暖。\n【禁忌】北方不宜开门，阴水过盛，主忧郁。"},
            {"甲", "【大门】宜开南方(离)，忌开西方(兑)。南方火生木，木火通明。\n【贵人位】北方(坎)，水来生木，贵人相助。\n【文昌位】南方(离)，木火通明，学业有成。\n【书房】宜设东南(巽)，文风昌盛。\n【禁忌】西方不宜开门，金克木，主事业受阻。"},
            {"乙", "【大门】宜开南方(离)，忌开西方(兑)。南方火生木，生机勃勃。\n【桃花位】南方(离)，人缘旺盛，家庭和睦。\n【财位】西方(兑)，金克木为财，财运亨通。\n【卧室】宜设东方(震)，紫气东来。\n【禁忌】西方不宜开门，金克木，主健康不佳。"},
            {"丙", "【大门】宜开北方(坎)，忌开南方(离)。北方水既济，水火调和。\n【事业位】东方(震)，木来生火，事业兴旺。\n【文昌位】北方(坎)，水火既济，文思泉涌。\n【书房】宜设北方(坎)，智慧增长。\n【禁忌】南方不宜开门，阳火过盛，主家宅不安。"},
            {"丁", "【大门】宜开北方(坎)，忌开南方(离)。北方水既济，温暖阴火。\n【文昌位】西方(兑)，金白水清，学业有成。\n【贵人位】东方(震)，木来生火，贵人相助。\n【卧室】宜设北方(坎)，身心安宁。\n【禁忌】南方不宜开门，阴火过盛，主失眠。"},
            {"庚", "【大门】宜开东方(震)，忌开西方(兑)。东方木克土，财星高照。\n【财位】北方(坎)，金水相生，财运亨通。\n【文昌位】南方(离)，火克金为财，事业有成。\n【书房】宜设南方(离)，智慧提升。\n【禁忌】西方不宜开门，阳金过盛，主压力大。"},
            {"辛", "【大门】宜开东方(震)，忌开西方(兑)。东方木克土，贵人扶持。\n【贵人位】南方(离)，火来炼金，步步高升。\n【桃花位】北方(坎)，金生水，人缘旺盛。\n【卧室】宜设东方(震)，身体健康。\n【禁忌】西方不宜开门，阴金过盛，主忧郁。"},
            {"艮", "【大门】宜开南方(离)，忌开东北(艮)。南方火生土，阳气充足。\n【子孙位】东方(震)，震为长男，人丁兴旺。\n【财位】西方(兑)，土生金，财运亨通。\n【老人房】宜设东北(艮)，健康长寿。\n【禁忌】东北不宜开门，艮土闭塞，主诸事不顺。"},
            {"坤", "【大门】宜开北方(坎)，忌开西南(坤)。北方水旺财，天一生水。\n【财位】北方(坎)，财源广进，富贵双全。\n【子孙位】东方(震)，木克土为财，人丁兴旺。\n【花园】宜设南方(离)，生机勃勃。\n【禁忌】西南不宜开门，坤土过重，主阻滞。"},
            {"巽", "【大门】宜开北方(坎)，忌开西北(乾)。北方水木清华，智慧增长。\n【文昌位】北方(坎)，水木清华，学业有成。\n【贵人位】南方(离)，火来暖木，贵人相助。\n【书房】宜设北方(坎)，文思泉涌。\n【禁忌】西北不宜开门，乾金克木，主事业不顺。"},
            {"乾", "【大门】宜开东南(巽)，忌开西北(乾)。东南木生火，贵人相助。\n【长辈房】宜设在西北(乾)，乾为老父，福寿安康。\n【财位】北方(坎)，金生水，财运亨通。\n【书房】宜设东南(巽)，文风昌盛。\n【禁忌】西北不宜开门，乾金过盛，主压力大。"}
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
            {"子", "【生肖】鼠 · 【五行】水\n【性格】聪明伶俐，机智灵活，善于社交，处事圆滑，富有创造力，适应能力强，直觉敏锐。\n【运势】财运亨通，贵人相助，事业顺利。但需注意防小人，避免投机。\n【婚配】宜：牛、龙、猴；忌：马、兔、羊。\n【健康】注意肾脏、泌尿系统、耳朵保养。"},
            {"丑", "【生肖】牛 · 【五行】土\n【性格】稳重踏实，勤劳肯干，诚实守信，意志坚强，耐力持久，责任心强，不善言辞。\n【运势】财运稳定，福禄寿全，晚年幸福。但需注意变通，避免固执。\n【婚配】宜：鼠、蛇、鸡；忌：龙、马、羊。\n【健康】注意脾胃、消化系统保养。"},
            {"寅", "【生肖】虎 · 【五行】木\n【性格】勇猛果敢，热情开朗，富有正义感，行动力强，领导能力出众，自信满满。\n【运势】事业有成，贵人扶持，财运旺盛。但需注意脾气，避免冲动。\n【婚配】宜：马、狗；忌：猴、蛇。\n【健康】注意肝胆、神经系统保养。"},
            {"卯", "【生肖】兔 · 【五行】木\n【性格】温柔善良，聪明机智，心思细腻，富有艺术天赋，人缘极佳，善于协调。\n【运势】福禄绵长，家庭和睦，财运稳定。但需注意决断力，避免优柔寡断。\n【婚配】宜：狗、猪；忌：鸡、鼠。\n【健康】注意肝胆、眼睛保养。"},
            {"辰", "【生肖】龙 · 【五行】土\n【性格】刚毅果断，富有魄力，自信心强，领导力出众，气度非凡，雄心勃勃。\n【运势】事业辉煌，富贵荣华，声名远播。但需注意谦虚，避免骄傲。\n【婚配】宜：鸡、鼠、猴；忌：狗、兔。\n【健康】注意脾胃、心脏保养。"},
            {"巳", "【生肖】蛇 · 【五行】火\n【性格】智慧机敏，直觉敏锐，善于思考，富有魅力，心思缜密，神秘莫测。\n【运势】财运亨通，福寿安康，事业顺利。但需注意人际关系，避免孤僻。\n【婚配】宜：牛、鸡；忌：猪、虎。\n【健康】注意心脏、小肠保养。"},
            {"午", "【生肖】马 · 【五行】火\n【性格】热情奔放，乐观开朗，行动力强，追求自由，充满活力，敢于冒险。\n【运势】事业有成，福禄寿喜，财运旺盛。但需注意稳重，避免急躁。\n【婚配】宜：虎、羊、狗；忌：鼠、牛。\n【健康】注意心脏、眼睛保养。"},
            {"未", "【生肖】羊 · 【五行】土\n【性格】温和善良，富有同情心，艺术天赋高，家庭观念强，性格柔顺，善解人意。\n【运势】福禄双全，家庭美满，财运稳定。但需注意自信心，避免依赖。\n【婚配】宜：兔、马、猪；忌：牛、狗。\n【健康】注意脾胃、消化系统保养。"},
            {"申", "【生肖】猴 · 【五行】金\n【性格】聪明伶俐，反应敏捷，善于变通，富有创新精神，幽默风趣，多才多艺。\n【运势】财运旺盛，事业顺利，贵人相助。但需注意踏实，避免浮躁。\n【婚配】宜：鼠、龙；忌：虎、猪。\n【健康】注意肺、大肠、皮肤保养。"},
            {"酉", "【生肖】鸡 · 【五行】金\n【性格】勤奋努力，精明能干，追求完美，口才出众，组织能力强，注重细节。\n【运势】事业成功，财运亨通，家庭和睦。但需注意人际关系，避免挑剔。\n【婚配】宜：牛、龙、蛇；忌：兔、狗。\n【健康】注意肺、大肠保养。"},
            {"戌", "【生肖】狗 · 【五行】土\n【性格】忠诚正直，勇敢正义，责任心强，重情重义，诚实守信，善于交友。\n【运势】福禄寿全，家庭美满，财运稳定。但需注意变通，避免固执。\n【婚配】宜：虎、兔、马；忌：龙、鸡。\n【健康】注意脾胃、消化系统保养。"},
            {"亥", "【生肖】猪 · 【五行】水\n【性格】善良淳朴，乐观豁达，待人真诚，心胸宽广，知足常乐，富有同情心。\n【运势】财运亨通，福寿绵长，家庭幸福。但需注意规划，避免懒散。\n【婚配】宜：兔、羊；忌：蛇、猴。\n【健康】注意肾脏、泌尿系统保养。"},
            {"壬", "【天干】阳水 · 【五行】水\n【性格】聪明睿智，思维敏捷，善于谋划，雄心勃勃，领导力强，富有魄力。\n【运势】财运亨通，事业有成，贵人相助。适合经商、管理、策划。\n【特质】主智慧、流动、变化，具有开创性。\n【健康】注意肾脏、泌尿系统保养。"},
            {"癸", "【天干】阴水 · 【五行】水\n【性格】温柔细腻，富有智慧，善于思考，艺术天赋高，直觉敏锐，善于变通。\n【运势】福寿安康，家庭美满，财运稳定。适合艺术、学术、策划。\n【特质】主智慧、滋润、内敛，具有包容性。\n【健康】注意肾脏、耳朵保养。"},
            {"甲", "【天干】阳木 · 【五行】木\n【性格】刚毅果断，行动力强，领导能力出众，充满活力，敢于担当，富有魄力。\n【运势】事业辉煌，贵人扶持，财运旺盛。适合创业、管理、开拓。\n【特质】主生长、条达、开创，具有进取心。\n【健康】注意肝胆、眼睛保养。"},
            {"乙", "【天干】阴木 · 【五行】木\n【性格】温柔善良，富有艺术天赋，心思细腻，人缘极佳，善于协调，适应性强。\n【运势】福禄绵长，家庭和睦，事业顺利。适合艺术、教育、协调。\n【特质】主生长、柔顺、协调，具有包容性。\n【健康】注意肝胆、神经系统保养。"},
            {"丙", "【天干】阳火 · 【五行】火\n【性格】热情奔放，自信心强，富有魄力，充满活力，善于表达，敢于创新。\n【运势】事业有成，财运亨通，声名远播。适合演讲、表演、创业。\n【特质】主明亮、热情、向上，具有感染力。\n【健康】注意心脏、小肠保养。"},
            {"丁", "【天干】阴火 · 【五行】火\n【性格】温柔体贴，富有智慧，艺术天赋高，善于思考，直觉敏锐，富有魅力。\n【运势】福禄双全，家庭美满，事业顺利。适合艺术、学术、策划。\n【特质】主温暖、光明、内敛，具有创造力。\n【健康】注意心脏、眼睛保养。"},
            {"庚", "【天干】阳金 · 【五行】金\n【性格】刚毅果断，正义感强，事业成功，财运旺盛，执行力强，注重实际。\n【运势】财运亨通，事业顺利，贵人相助。适合金融、法律、管理。\n【特质】主收敛、果断、变革，具有决断力。\n【健康】注意肺、大肠保养。"},
            {"辛", "【天干】阴金 · 【五行】金\n【性格】温柔善良，精明能干，追求完美，口才出众，心思细腻，善于分析。\n【运势】福禄寿全，家庭美满，财运稳定。适合金融、艺术、分析。\n【特质】主收敛、细腻、精致，具有洞察力。\n【健康】注意肺、皮肤保养。"},
            {"艮", "【卦象】艮卦 · 【五行】土\n【性格】稳重踏实，勤劳肯干，意志坚强，保守稳重，注重实际，善于积累。\n【运势】福禄绵长，根基稳固，财运稳定。适合置业、投资、积累。\n【特质】主静止、稳定、积累，具有持久性。\n【健康】注意脾胃、消化系统保养。"},
            {"坤", "【卦象】坤卦 · 【五行】土\n【性格】温柔善良，富有同情心，家庭观念强，心胸宽广，善于包容，厚德载物。\n【运势】福寿安康，家庭美满，财运稳定。适合家庭、教育、服务。\n【特质】主包容、柔顺、承载，具有包容性。\n【健康】注意脾胃、消化系统保养。"},
            {"巽", "【卦象】巽卦 · 【五行】木\n【性格】智慧机敏，善于变通，富有创新精神，思维活跃，善于交际，适应力强。\n【运势】事业有成，贵人相助，财运亨通。适合创新、交流、策划。\n【特质】主风、变动、通达，具有灵活性。\n【健康】注意肝胆、呼吸系统保养。"},
            {"乾", "【卦象】乾卦 · 【五行】金\n【性格】刚毅果断，自信心强，领导力出众，气度非凡，富有魄力，志向远大。\n【运势】富贵荣华，事业辉煌，声名远播。适合领导、创业、开拓。\n【特质】主健、刚、动，具有权威性。\n【健康】注意肺、大肠保养。"}
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
            {"坎", "【大门】宜开东南(巽)或东方(震)，纳生气之旺气。\n【主卧】宜设东方(震)或北方(坎)，东方主长子，北方主智慧。\n【书房】宜设东南(巽)，文昌星位，利于学业。\n【厨房】宜设东南(巽)或南方(离)，木火相生。\n【禁忌】北方不宜开门，坎水泄气。"},
            {"离", "【大门】宜开西北(乾)或西方(兑)，纳金气之旺气。\n【神位】宜设北方(坎)，敬天祭祖，水火既济。\n【文昌位】西方(兑)，金白水清，学业有成。\n【财位】东方(震)，木火通明，财源广进。\n【禁忌】南方不宜开门，离火过旺。"},
            {"震", "【大门】宜开南方(离)或西方(兑)，纳火金之旺气。\n【厨房】宜设东南(巽)，火生土旺，家庭和睦。\n【桃花位】南方(离)，人缘旺盛，婚姻美满。\n【财位】北方(坎)，水生木旺，财运亨通。\n【禁忌】东方不宜开门，震木过盛。"},
            {"兑", "【大门】宜开东北(艮)或北方(坎)，纳土水之旺气。\n【财位】宜设南方(离)，火生土旺，财源广进。\n【贵人位】东方(震)，木来克土，贵人扶持。\n【文昌位】东南(巽)，文风昌盛，学业有成。\n【禁忌】西方不宜开门，兑金过盛。"},
            {"巽", "【大门】宜开北方(坎)或东北(艮)，纳水土之旺气。\n【财位】宜设北方(坎)，水木清华，财源广进。\n【文昌位】北方(坎)，智慧增长，学业有成。\n【贵人位】南方(离)，火来暖木，贵人相助。\n【禁忌】东南不宜开门，巽风过盛。"},
            {"艮", "【大门】宜开西方(兑)或东南(巽)，纳金木之旺气。\n【子孙位】东方(震)，震为长男，人丁兴旺。\n【财位】西方(兑)，土生金，财运亨通。\n【老人房】宜设东北(艮)，健康长寿。\n【禁忌】东北不宜开门，艮土闭塞。"},
            {"坤", "【大门】宜开东方(震)或南方(离)，纳木火之旺气。\n【财位】宜设北方(坎)，天一生水，财源广进。\n【子孙位】东方(震)，木克土为财，人丁兴旺。\n【花园】宜设南方(离)，生机勃勃。\n【禁忌】西南不宜开门，坤土过重。"},
            {"乾", "【大门】宜开南方(离)或东南(巽)，纳火木之旺气。\n【长辈房】宜设西北(乾)，乾为老父，福寿安康。\n【财位】北方(坎)，金生水，财运亨通。\n【书房】宜设东南(巽)，文风昌盛。\n【禁忌】西北不宜开门，乾金过盛。"}
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
            {"坎", "【砂水】北方宜有水景，南方宜有山峦。\n【明堂】宜开阔明亮，纳财聚气。\n【靠山】北方宜有高楼，玄武得位。\n【龙虎】东方青龙宜高耸，西方白虎宜柔顺。"},
            {"离", "【砂水】南方宜开阔，北方宜靠山。\n【明堂】宜宽敞明亮，阳光充足。\n【靠山】北方宜有山峦，玄武得位。\n【朱雀】南方宜开阔，朱雀展翅。"},
            {"震", "【砂水】东方宜流水，西方宜靠山。\n【明堂】宜宽敞，紫气东来。\n【青龙】东方宜高耸，青龙得位。\n【白虎】西方宜柔顺，白虎得宜。"},
            {"兑", "【砂水】西方宜水景，东方宜靠山。\n【明堂】宜方正，聚气生财。\n【白虎】西方宜开阔，白虎得位。\n【青龙】东方宜高耸，青龙得宜。"},
            {"巽", "【砂水】东南宜树林，西北宜水景。\n【明堂】宜明亮，文昌得位。\n【风路】东南宜通畅，纳清新之气。\n【靠山】西北宜有高楼，乾位得宜。"},
            {"艮", "【砂水】东北宜山峦，西南宜水景。\n【明堂】宜方正，聚气生财。\n【靠山】东北宜有山峦，艮位得宜。\n【水口】西南宜紧锁，财源不尽。"},
            {"坤", "【砂水】西南宜厚土，东北宜水景。\n【明堂】宜宽敞，厚德载物。\n【靠山】西南宜有山峦，坤位得宜。\n【水口】东北宜紧锁，财源广进。"},
            {"乾", "【砂水】西北宜高楼，东南宜水景。\n【明堂】宜开阔，贵人提携。\n【靠山】西北宜高耸，乾位得宜。\n【风路】东南宜通畅，纳清新之气。"}
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
            {"坎", "【五行】水旺主智，思维敏捷，智慧出众。\n【事业】财运亨通，宜经商、策划、金融。\n【学业】文昌得位，金榜题名，学业有成。\n【健康】注意肾脏、泌尿系统保养。"},
            {"离", "【五行】火旺主礼，热情开朗，善于交际。\n【事业】事业兴旺，宜仕途、演讲、演艺。\n【学业】金白水清，文思泉涌，才华横溢。\n【健康】注意心脏、眼睛保养。"},
            {"震", "【五行】木旺主仁，仁爱善良，人丁兴旺。\n【事业】贵人相助，宜创业、教育、文化。\n【学业】木火通明，学业顺利，步步高升。\n【健康】注意肝胆、神经系统保养。"},
            {"兑", "【五行】金旺主义，正义感强，果断干练。\n【事业】财运旺盛，宜理财、金融、法律。\n【学业】金白水清，智慧增长，学有所成。\n【健康】注意肺、大肠保养。"},
            {"巽", "【五行】木旺主风，思维活跃，善于变通。\n【事业】贵人相助，宜创新、交流、策划。\n【学业】水木清华，智慧出众，学业有成。\n【健康】注意肝胆、呼吸系统保养。"},
            {"艮", "【五行】土旺主信，稳重踏实，根基稳固。\n【事业】宜置业、投资、积累，财运稳定。\n【学业】土生金，智慧增长，学有所成。\n【健康】注意脾胃、消化系统保养。"},
            {"坤", "【五行】土旺主顺，包容宽厚，家宅康宁。\n【事业】宜安居、教育、服务，家庭美满。\n【学业】土生金，智慧增长，学业顺利。\n【健康】注意脾胃、消化系统保养。"},
            {"乾", "【五行】金旺主健，刚毅果断，领导力强。\n【事业】贵人提携，宜创业、领导、开拓。\n【学业】金白水清，智慧出众，金榜题名。\n【健康】注意肺、大肠保养。"}
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
        sb.append(wuxing).append("行得位，").append(bagua).append("卦得气，").append(nayin).append("纳音。");
        sb.append("\n\n");
        sb.append(fortuneText);
        sb.append("\n\n");
        sb.append("【重要提示】\n");
        sb.append("• 吉位(生气/天医/延年)宜开大门、设卧室、做书房、设财位；\n");
        sb.append("• 凶位(五鬼/绝命/祸害/六煞)宜作厨房、卫生间压煞；\n");
        sb.append("• 装修宜选与坐山五行相生之颜色，忌相克之色；\n");
        sb.append("• 以上仅供参考，实际布局请结合周边环境和专业堪舆。\n");
        
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
                        if (hasCompass) {
                            autoMode = false;
                            sensorManager.unregisterListener(LuoPanActivity.this);
                        }
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
                        if (hasCompass) {
                            autoMode = true;
                            sensorManager.registerListener(LuoPanActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                            sensorManager.registerListener(LuoPanActivity.this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
                        }
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