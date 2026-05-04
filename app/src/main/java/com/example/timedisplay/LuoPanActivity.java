package com.example.timedisplay;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class LuoPanActivity extends Activity {
    private LuoPanView luoPanView;
    private TextView titleInfo;
    private TextView directionInfo;
    private TextView mountainInfo;
    private TextView wuxingInfo;
    private TextView shierZhixingInfo;
    private TextView tianganInfo;
    private TextView luopanTips;
    private TextView detailInfo;
    
    private float currentRotation = 0;
    private float lastAngle = 0;
    private boolean isRotating = false;
    
    private static final String KEY_ROTATION = "current_rotation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luo_pan);
        
        luoPanView = findViewById(R.id.luoPanView);
        titleInfo = findViewById(R.id.titleInfo);
        directionInfo = findViewById(R.id.directionInfo);
        mountainInfo = findViewById(R.id.mountainInfo);
        wuxingInfo = findViewById(R.id.wuxingInfo);
        shierZhixingInfo = findViewById(R.id.shierZhixingInfo);
        tianganInfo = findViewById(R.id.tianganInfo);
        luopanTips = findViewById(R.id.luopanTips);
        detailInfo = findViewById(R.id.detailInfo);
        
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
        
        directionInfo.setText("方位: " + direction);
        mountainInfo.setText("坐山: " + mountain);
        wuxingInfo.setText("五行: " + getWuxing(mountain));
        shierZhixingInfo.setText("十二支: " + getShierZhi(mountain));
        tianganInfo.setText("天干: " + getTiangan(mountain));
        luopanTips.setText(getMountainTip(mountain));
        detailInfo.setText(getDetailInfo(mountain));
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
    
    private String getDetailInfo(String mountain) {
        String[] details = {
            "【壬水】\n天时：云、雨、霜、露。\n地理：河川、湖海、泉涧。\n人物：舟子、旅人、渔夫。\n身体：耳、目、筋骨、血脉。\n性情：刚猛、智巧、多智。\n事业：水利、交通、物流。",
            "【子水】\n天时：月、夜、霜、雪。\n地理：江、河、池塘、泉水。\n人物：妇人、盗贼、术士。\n身体：肾、耳、生殖系统。\n性情：阴险、柔顺、多疑。\n事业：水产、夜场、玄学。",
            "【癸水】\n天时：雨露、霜雪、雾。\n地理：池沼、沟涧、泉井。\n人物：媒人、盗贼、乐工。\n身体：肾、精、脑、唾。\n性情：阴柔、多情、内向。\n事业：服务、艺术、中介。",
            "【丑土】\n天时：云、雾、阴湿。\n地理：桑园、坟墓、仓库。\n人物：老妇人、农夫、牧竖。\n身体：脾、腹、筋骨。\n性情：厚重、耐劳、固执。\n事业：农业、仓储、地产。",
            "【艮土】\n天时：云、雾、山岚。\n地理：山、丘陵、坟墓。\n人物：少男、僧人、道士。\n身体：手、指、背、鼻。\n性情：沉静、稳重、诚实。\n事业：宗教、建筑、矿业。",
            "【寅木】\n天时：风、雷、春。\n地理：山林、桥梁、花园。\n人物：贵人、夫婿、客商。\n身体：胆、毛发、手掌。\n性情：正直、向上、积极。\n事业：木材、教育、文化。",
            "【甲木】\n天时：雷、春、风。\n地理：森林、大路、桥梁。\n人物：贵人、首领、君子。\n身体：胆、头、发、目。\n性情：刚直、积极、向上。\n事业：领导、建筑、教育。",
            "【卯木】\n天时：春、风、雷。\n地理：园林、草地、花木。\n人物：妇人、秀才、艺人。\n身体：肝、目、爪、筋。\n性情：柔顺、温和、文雅。\n事业：艺术、园艺、美容。",
            "【乙木】\n天时：风、春、露。\n地理：果园、菜园、花园。\n人物：妇人、姑娘、工匠。\n身体：肝、肩、颈、指。\n性情：温柔、顺从、含蓄。\n事业：手工艺、设计、服务。",
            "【辰土】\n天时：云、雾、雨。\n地理：山、高岗、寺观。\n人物：僧人、道士、医师。\n身体：脾、胃、肩、背。\n性情：厚重、包容、慈善。\n事业：医疗、宗教、公益。",
            "【巽木】\n天时：风、云、烟。\n地理：树林、竹林、菜园。\n人物：长女、寡妇、商人。\n身体：胆、股、肱、目。\n性情：柔和、温和、细心。\n事业：贸易、园艺、纺织。",
            "【巳火】\n天时：夏、暑、晴。\n地理：炉冶、窑灶、闹市。\n人物：妇人、少女、文艺。\n身体：心、面、口、齿。\n性情：热情、开朗、急躁。\n事业：文化、娱乐、服务业。",
            "【丙火】\n天时：日、夏、暑。\n地理：高岗、窑灶、炉冶。\n人物：贵人、官员、文人。\n身体：小肠、眼、额、肩。\n性情：热情、积极、乐观。\n事业：政府、文化、教育。",
            "【午火】\n天时：夏、日、晴。\n地理：宫室、堂屋、街道。\n人物：贵人、妇人、美人。\n身体：心、舌、目、神。\n性情：热情、开朗、积极。\n事业：文化、娱乐、艺术。",
            "【丁火】\n天时：星、夜、秋。\n地理：灯火、烛光、香火。\n人物：妇人、文人、星士。\n身体：心、眼、血脉、精神。\n性情：温和、细心、聪明。\n事业：文化、艺术、教育。",
            "【未土】\n天时：夏、云、雾。\n地理：田园、庭院、菜园。\n人物：老妇人、厨师、农夫。\n身体：脾、腹、胃、口。\n性情：温和、稳重、包容。\n事业：农业、餐饮、食品。",
            "【坤土】\n天时：云、雾、阴。\n地理：田野、乡村、郊外。\n人物：老母、妇人、农夫。\n身体：脾、胃、腹、皮。\n性情：柔顺、包容、慈爱。\n事业：农业、地产、服务业。",
            "【申金】\n天时：秋、霜、露。\n地理：城郭、道路、祠庙。\n人物：行人、军徒、恶人。\n身体：肺、大肠、骨、筋。\n性情：威严、勇猛、果断。\n事业：军事、政法、金属。",
            "【庚金】\n天时：秋、霜、露。\n地理：道路、桥梁、矿山。\n人物：军人、武士、贵人。\n身体：肺、喉、鼻、舌。\n性情：刚健、勇敢、威严。\n事业：军事、政法、金属。",
            "【酉金】\n天时：秋、霜、月。\n地理：街巷、酒店、作坊。\n人物：妇人、少女、艺人。\n身体：肺、口、舌、齿。\n性情：温和、文雅、爱美。\n事业：艺术、娱乐、珠宝。",
            "【辛金】\n天时：秋、霜、月。\n地理：珠宝、玉器、首饰。\n人物：妇人、美人、匠人。\n身体：肺、鼻、舌、喉。\n性情：温和、爱美、细腻。\n事业：珠宝、艺术、设计。",
            "【戌土】\n天时：秋、云、雾。\n地理：山岗、高坡、寺观。\n人物：僧人、道士、善人。\n身体：胃、脾、腹、腿。\n性情：厚重、诚信、慈善。\n事业：宗教、公益、教育。",
            "【乾金】\n天时：天、日、雷。\n地理：京都、大郡、高岗。\n人物：君父、贵人、长者。\n身体：首、骨、肺、大肠。\n性情：刚健、积极、向上。\n事业：领导、政府、企业。",
            "【亥水】\n天时：雨、雪、霜。\n地理：江河、湖海、溪流。\n人物：妇人、少女、孩童。\n身体：肾、膀胱、耳、目。\n性情：聪明、灵秀、多情。\n事业：水利、物流、艺术。"
        };
        
        String[] mountains = {
            "壬", "子", "癸", "丑", "艮", "寅",
            "甲", "卯", "乙", "辰", "巽", "巳",
            "丙", "午", "丁", "未", "坤", "申",
            "庚", "酉", "辛", "戌", "乾", "亥"
        };
        
        for (int i = 0; i < mountains.length; i++) {
            if (mountains[i].equals(mountain)) {
                return details[i];
            }
        }
        return "";
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
