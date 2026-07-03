package com.example.timedisplay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LuoPanActivity extends android.app.Activity {

    private float azimuth = 0f;

    private TextView directionInfo, mountainInfo, wuxingInfo, shierZhixingInfo, tianganInfo;
    private TextView baguaInfo, chaoXiangInfo, nayinInfo, shenshaInfo, jixiongInfo, diguiInfo;
    private TextView eastZhaoxiang, eastShuisha, eastLayout,
                     southZhaoxiang, southShuisha, southLayout,
                     westZhaoxiang, westShuisha, westLayout,
                     northZhaoxiang, northShuisha, northLayout,
                     southeastZhaoxiang, southeastShuisha, southeastLayout,
                     southwestZhaoxiang, southwestShuisha, southwestLayout,
                     northwestZhaoxiang, northwestShuisha, northwestLayout,
                     northeastZhaoxiang, northeastShuisha, northeastLayout;
    private TextView bazhaiDesc, jiuxingDesc, bamenDesc, luopanSummary, shanshuiFengshui, yinzhaiFengshui, yinzhaiArrow;
    private LinearLayout yinzhaiToggle, yinzhaiContent;
    private boolean yinzhaiExpanded = false;

    private final String[] baguaNames = {"坎", "艮", "震", "巽", "中", "乾", "兑", "坤", "离"};
    private final String[] mountainNames = {"子", "癸", "丑", "艮", "寅", "甲", "卯", "乙", "辰",
            "巽", "巳", "丙", "午", "丁", "未", "坤", "申", "庚", "酉", "辛", "戌", "乾", "亥", "壬"};
    private final String[] tianganNames = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private final String[] wuxingNames = {"水", "土", "木", "木", "土", "木", "木", "土", "火", "火",
            "土", "火", "火", "土", "金", "金", "土", "金", "金", "土", "水", "土", "水", "水"};
    private final String[] nayinMap = {
            "桑柘木", "桑柘木", "壁上土", "壁上土", "松柏木", "松柏木",
            "炉中火", "炉中火", "大林木", "大林木", "白蜡金", "白蜡金",
            "杨柳木", "杨柳木", "泉中水", "泉中水", "屋上土", "屋上土",
            "霹雳火", "霹雳火", "松柏木", "松柏木", "长流水", "长流水"
    };
    private final String[] shenshaMap = {
            "天德", "玉堂", "天牢", "玄武", "司命", "青龙",
            "明堂", "金匮", "天德", "玉堂", "天牢", "玄武",
            "司命", "青龙", "明堂", "金匮", "天德", "玉堂",
            "天牢", "玄武", "司命", "青龙", "明堂", "金匮"
    };

    // 八门生活应用建议：{卦名, baguaNumber, 休门位, 生门位, 开门位, 求财方位, 求职方位, 谈判方位, 忌走方位}
    private static final String[][] BAMEN_LIFE_ADVICE = {
            {"坎", "1", "北方（本位）", "东方", "东南方", "东方生门方", "东南开门方", "北方休门方", "南方死门方、西南惊门方"},
            {"艮", "8", "东北方", "东南方", "南方", "东南生门方", "南方开门方", "东北休门方", "西方惊门方、西北死门方"},
            {"震", "3", "东方", "南方", "西北方", "南方生门方", "西北开门方", "东方休门方", "北方死门方、东北惊门方"},
            {"巽", "4", "东南方", "西北方", "北方", "西北生门方", "北方开门方", "东南休门方", "西方死门方、西南惊门方"},
            {"乾", "6", "西北方", "西方", "东北方", "西方生门方", "东北开门方", "西北休门方", "南方死门方、东南惊门方"},
            {"兑", "7", "西方", "西南方", "东南方", "西南生门方", "东南开门方", "西方休门方", "东方惊门方、东北死门方"},
            {"坤", "2", "西南方", "北方", "东方", "北方生门方", "东方开门方", "西南休门方", "东南死门方、南方惊门方"},
            {"离", "9", "南方", "东方", "西北方", "东方生门方", "西北开门方", "南方休门方", "东北死门方、北方惊门方"}
    };

    // 八宅吉凶方释义口诀
    private static final String BAZHAI_KOUJUE =
            "八宅方位吉凶：<br/>"
            + "• <font color='#00CC00'><b>生气方</b></font>（最吉）：主丁财两旺，宜主卧、大门；<br/>"
            + "• <font color='#00CC00'><b>天医方</b></font>（次吉）：主祛病延年，宜体弱居住；<br/>"
            + "• <font color='#00CC00'><b>延年方</b></font>（次吉）：主婚姻美满、健康长寿，宜夫妻房；<br/>"
            + "• <font color='#FFAA00'><b>伏位方</b></font>（平）：主平稳守成，宜静养读书；<br/>"
            + "• <font color='#FF4444'><b>祸害方</b></font>（凶）：主口舌是非、胃病，宜作厕所；<br/>"
            + "• <font color='#FF4444'><b>六煞方</b></font>（凶）：主感情纠纷、失眠，宜作储物间；<br/>"
            + "• <font color='#FF4444'><b>五鬼方</b></font>（凶）：主官非破财、火灾，宜作厨房；<br/>"
            + "• <font color='#FF4444'><b>绝命方</b></font>（大凶）：主伤残、绝嗣，宜作厕所仓库。<br/><br/>";

    // 九星吉凶口诀
    private static final String JIUXING_KOUJUE =
            "九星吉凶：<br/>"
            + "• <font color='#00CC00'><b>一白</b></font>（水）→ 文昌官贵，利考试求职；<br/>"
            + "• <font color='#FF4444'><b>二黑</b></font>（土）→ 病符，主脾胃病灾；<br/>"
            + "• <font color='#FF4444'><b>三碧</b></font>（木）→ 是非，主口舌争斗；<br/>"
            + "• <font color='#00CC00'><b>四绿</b></font>（木）→ 文曲，利读书创作；<br/>"
            + "• <font color='#FF4444'><b>五黄</b></font>（土）→ 大煞，主灾祸破财；<br/>"
            + "• <font color='#00CC00'><b>六白</b></font>（金）→ 武曲，利升职；<br/>"
            + "• <font color='#FF4444'><b>七赤</b></font>（金）→ 破军，主贼盗官非；<br/>"
            + "• <font color='#00CC00'><b>八白</b></font>（土）→ 旺财，利置业投资；<br/>"
            + "• <font color='#00CC00'><b>九紫</b></font>（火）→ 喜庆，利婚嫁添丁。<br/><br/>";

    // 八门性格与用途口诀
    private static final String BAMEN_KOUJUE =
            "八门吉凶与用途：<br/>"
            + "• <font color='#00CC00'><b>开门</b></font>（吉）：通达顺利，宜开业、求职、谈判；<br/>"
            + "• <font color='#00CC00'><b>休门</b></font>（吉）：百事皆宜，宜婚嫁、求财、休养；<br/>"
            + "• <font color='#00CC00'><b>生门</b></font>（吉）：财源广进，宜求财、交易、建造；<br/>"
            + "• <font color='#FFAA00'><b>杜门</b></font>（平）：闭塞隐藏，宜躲避、保密；<br/>"
            + "• <font color='#FFAA00'><b>景门</b></font>（平）：文书吉庆，宜考试、诉讼；<br/>"
            + "• <font color='#FF4444'><b>伤门</b></font>（凶）：损伤争斗，宜追债、擒贼；<br/>"
            + "• <font color='#FF4444'><b>死门</b></font>（凶）：衰败丧事，宜安葬；<br/>"
            + "• <font color='#FF4444'><b>惊门</b></font>（凶）：惊恐怪异，宜诉讼、捕盗。<br/><br/>";

    private LuoPanView luoPanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 必须在 setContentView 之前应用方向锁定，避免进入后再切换方向
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isRotationLocked = prefs.getBoolean("rotationLocked", false);
        int lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(lockedOrientation);
        }

        setContentView(R.layout.activity_luo_pan);

        initViews();
        setupLuoPanRotation();
        updateInfo();
    }

    private void initViews() {
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
        eastZhaoxiang = findViewById(R.id.eastZhaoxiang);
        eastShuisha = findViewById(R.id.eastShuisha);
        eastLayout = findViewById(R.id.eastLayout);
        southZhaoxiang = findViewById(R.id.southZhaoxiang);
        southShuisha = findViewById(R.id.southShuisha);
        southLayout = findViewById(R.id.southLayout);
        westZhaoxiang = findViewById(R.id.westZhaoxiang);
        westShuisha = findViewById(R.id.westShuisha);
        westLayout = findViewById(R.id.westLayout);
        northZhaoxiang = findViewById(R.id.northZhaoxiang);
        northShuisha = findViewById(R.id.northShuisha);
        northLayout = findViewById(R.id.northLayout);
        southeastZhaoxiang = findViewById(R.id.southeastZhaoxiang);
        southeastShuisha = findViewById(R.id.southeastShuisha);
        southeastLayout = findViewById(R.id.southeastLayout);
        southwestZhaoxiang = findViewById(R.id.southwestZhaoxiang);
        southwestShuisha = findViewById(R.id.southwestShuisha);
        southwestLayout = findViewById(R.id.southwestLayout);
        northwestZhaoxiang = findViewById(R.id.northwestZhaoxiang);
        northwestShuisha = findViewById(R.id.northwestShuisha);
        northwestLayout = findViewById(R.id.northwestLayout);
        northeastZhaoxiang = findViewById(R.id.northeastZhaoxiang);
        northeastShuisha = findViewById(R.id.northeastShuisha);
        northeastLayout = findViewById(R.id.northeastLayout);
        bazhaiDesc = findViewById(R.id.bazhaiDesc);
        jiuxingDesc = findViewById(R.id.jiuxingDesc);
        bamenDesc = findViewById(R.id.bamenDesc);
        luopanSummary = findViewById(R.id.luopanSummary);
        shanshuiFengshui = findViewById(R.id.shanshuiFengshui);
        yinzhaiFengshui = findViewById(R.id.yinzhaiFengshui);
        yinzhaiArrow = findViewById(R.id.yinzhaiArrow);
        yinzhaiToggle = findViewById(R.id.yinzhaiToggle);
        yinzhaiContent = findViewById(R.id.yinzhaiContent);

        if (yinzhaiToggle != null && yinzhaiArrow != null && yinzhaiContent != null) {
            yinzhaiToggle.setOnClickListener(v -> {
                yinzhaiExpanded = !yinzhaiExpanded;
                if (yinzhaiExpanded) {
                    yinzhaiArrow.setText("▼");
                    yinzhaiContent.setVisibility(View.VISIBLE);
                    yinzhaiContent.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                } else {
                    yinzhaiArrow.setText("▶");
                    yinzhaiContent.setVisibility(View.GONE);
                    yinzhaiContent.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0
                    ));
                }
            });
        }

        luoPanView = findViewById(R.id.luoPanView);
    }

    private void setupLuoPanRotation() {
        luoPanView.setOnRotationChangeListener(rotation -> {
            azimuth = rotation;
            updateInfo();
        });
    }

    private void updateInfo() {
        float normalizedAzimuth = ((-azimuth % 360) + 360) % 360;
        int mountainIndex = (int) (normalizedAzimuth / 15);
        int baguaIndex = (int) (normalizedAzimuth / 45);

        String mountain = mountainNames[mountainIndex];
        String bagua = baguaNames[baguaIndex];
        String wuxing = wuxingNames[mountainIndex];
        String nayin = nayinMap[mountainIndex];
        String shensha = shenshaMap[mountainIndex];

        String chaoXiang = mountainNames[(mountainIndex + 12) % 24];
        int tianganIndex = mountainIndex % 10;
        String tiangan = tianganNames[tianganIndex];

        if (directionInfo != null) directionInfo.setText(bagua + "(" + getDirectionString(normalizedAzimuth) + ")");
        if (mountainInfo != null) mountainInfo.setText("坐山: " + mountain);
        if (wuxingInfo != null) wuxingInfo.setText("五行: " + wuxing);
        if (shierZhixingInfo != null) shierZhixingInfo.setText("地支: " + getDizhiForMountain(mountain));
        if (tianganInfo != null) tianganInfo.setText("天干: " + tiangan);
        if (baguaInfo != null) baguaInfo.setText("八卦: " + bagua);
        if (chaoXiangInfo != null) chaoXiangInfo.setText("朝向: " + chaoXiang);
        if (nayinInfo != null) nayinInfo.setText("纳音: " + nayin);
        if (shenshaInfo != null) shenshaInfo.setText("神煞: " + shensha);
        if (jixiongInfo != null) jixiongInfo.setText("吉凶: " + getJiXiong(mountain, wuxing));
        if (diguiInfo != null) diguiInfo.setText("归藏: " + getGuiZang(wuxing));

        updateBafangInfo(mountain, chaoXiang, wuxing, bagua);

        setupBazhaiNinePalace(bagua, normalizedAzimuth);
        setupJiuxingNinePalace(bagua, normalizedAzimuth);
        setupBamenNinePalace(bagua, normalizedAzimuth);

        if (luopanSummary != null) {
            luopanSummary.setText(getLuoPanSummary(mountain, chaoXiang, wuxing));
        }
        
        if (shanshuiFengshui != null) {
            shanshuiFengshui.setText(Html.fromHtml(getShanshuiFengshui(mountain, chaoXiang, wuxing, bagua)));
        }
        if (yinzhaiFengshui != null) {
            yinzhaiFengshui.setText(Html.fromHtml(getYinzhaiFengshui(mountain, chaoXiang, wuxing, bagua)));
        }
    }

    private void updateBafangInfo(String mountain, String chaoXiang, String wuxing, String bagua) {
        String[] directions = {"东方", "南方", "西方", "北方", "东南", "西南", "西北", "东北"};
        String[] baguas = {"震", "离", "兑", "坎", "巽", "坤", "乾", "艮"};
        String[] wuxings = {"木", "火", "金", "水", "木", "土", "金", "土"};
        String[] beasts = {"青龙", "朱雀", "白虎", "玄武", "青龙", "白虎", "玄武", "青龙"};
        
        String[][] shuiShaJiInfo = {
            {"青龙高耸，主文昌鼎盛、贵人提携，山峰尖秀出文人", "东方属木宜挺拔，青龙昂首阳气足，长子有出息"},
            {"朱雀翔舞，主名声远播、事业昌隆，明堂开阔纳祥瑞", "南方属火宜秀丽，朱雀展翅文运通，中女有作为"},
            {"白虎驯服，主财源广进、家宅安宁，山势柔顺聚财气", "西方属金宜低伏，白虎卧伏家宅安，少女有福泽"},
            {"玄武厚重，主根基稳固、子孙繁盛，山环水绕福寿全", "北方属水宜厚实，玄武垂头靠山稳，晚运亨通"},
            {"巽峰高耸，主文星高照、贵人临门，溪流婉转财源广", "东南属木宜秀丽，巽位得位文运盛，长女聪慧"},
            {"坤砂端方，主家庭和睦、人丁兴旺，山圆土厚纳吉祥", "西南属土宜平坦，坤位安宁老母康，财运稳定"},
            {"乾峰巍峨，主权威显赫、事业发达，山环水抱财禄聚", "西北属金宜雄伟，乾位得势官运通，老父康健"},
            {"艮砂敦厚，主家宅平安、子孙昌盛，靠山稳固基业长", "东北属土宜厚实，艮位安宁少男健，基础牢固"}
        };
        
        String[][] shuiShaXiongInfo = {
            {"青龙破碎，主官非口舌、家道衰败，山峰尖射损人丁", "东方忌破碎，青龙低头长子灾，尖角冲射主官非"},
            {"朱雀阴暗，主疾病缠身、横祸不断，尖射直冲血光灾", "南方忌冲射，朱雀开口口舌多，阴暗主心病"},
            {"白虎昂头，主争斗凶灾、家宅不宁，高大雄健杀气重", "西方忌高昂，白虎抬头少女殃，道路直冲损丁财"},
            {"玄武空缺，主破财败家、根基动摇，水流直泄家业败", "北方忌空缺，玄武无靠晚景凉，低洼主肾病"},
            {"巽位堵塞，主困顿阻滞、病灾频仍，污秽堆积招瘟疫", "东南忌堵塞，巽位闭塞文运暗，长女不利"},
            {"坤砂陡峭，主灾祸连绵、老母有殃，地势倾斜根基危", "西南忌陡峭，坤砂破碎老母灾，不平主脾胃"},
            {"乾位低洼，主财气散尽、贵人远离，破碎不堪招灾祸", "西北忌低洼，乾位空缺贵人失，潮湿主肺疾"},
            {"艮砂崩塌，主横灾祸事、人丁损伤，道路直冲犯煞重", "东北忌崩塌，艮砂缺损少男伤，冲射主手足"}
        };
        
        String[][] shuiShaPingInfo = {
            {"山势平缓，主家道平顺、生活安稳，草木茂盛生机足", "东方木气平和，青龙低伏长子稳，平淡见真福"},
            {"明堂开阔，主身心安康、家庭和睦，阳光柔和无病灾", "南方火气平和，朱雀敛翼中女顺，知足常乐"},
            {"白虎伏卧，主家宅安宁、无是无非，地势平坦人心定", "西方金气平和，白虎驯服少女安，和气生财"},
            {"玄武平缓，主根基稳健、福寿安康，靠山适中享太平", "北方水气平和，玄武藏头晚景稳，细水长流"},
            {"巽位舒展，主文运平稳、家道日兴，通风明亮福气来", "东南木气平和，巽位适中长女稳，稳步兴旺"},
            {"坤砂圆润，主福寿安康、家庭和睦，地势平坦享天年", "西南土气平和，坤位安宁老母健，家和万事兴"},
            {"乾位平正，主贵人相助、事业平稳，山水平和财运亨", "西北金气平和，乾位适中老父康，稳中有进"},
            {"艮砂厚实，主家宅稳固、子孙荣昌，山势平缓无冲煞", "东北土气平和，艮位安宁少男健，根深叶茂"}
        };
        
        String[][] layoutJiInfo = {
            {"宜开正门迎紫气，设书房助文昌，栽花草养木气", "门朝东日出入宅，书房设此方，学业事业皆成"},
            {"宜开南门迎火气，设客厅接宾客，修明堂纳祥瑞", "门朝南阳光足，客厅设此方，名声远播"},
            {"宜设厨房旺火气，建库房聚财物，修道路通财气", "西方宜静，厨房库房设此，财运稳定"},
            {"宜建水池蓄财气，设后花园养生机，修靠山固根基", "北方宜有靠，水池设此方，晚年清福"},
            {"宜开侧门纳秀气，设书房办公务，种花木养性情", "巽位主文昌，书房设此方，贵人相助"},
            {"宜设主卧养身心，建储藏室聚财物，修平台纳福气", "坤位主母仪，主卧设此方，家人长寿"},
            {"宜设主位显尊贵，建书房增智慧，修高台望远景", "乾位主天贵，书房设此方，官运旺"},
            {"宜建大门固根基，设祠堂祭先祖，修厚墙保安宁", "艮位主子孙，大门设此方，子孙兴旺"}
        };
        
        String[][] layoutXiongInfo = {
            {"忌开凶门纳煞气，忌建厕所污木气，忌堆杂物阻生机", "此方开门长子灾，设厕所主肝胆病，家运衰败"},
            {"忌阴暗潮湿伤火气，忌建厨房火太旺，忌污水冲损丁财", "此方阴暗主心病，设厨房口舌多，家庭不宁"},
            {"忌高大建筑挡金气，忌建厕所污金气，忌喧噪声烦心神", "此方高物挡气，设厕所主肺病，破财招灾"},
            {"忌开门泄气散财气，忌高楼挡靠山，忌低洼潮湿伤肾气", "此方开门财气泄，低洼主肾病，根基动摇"},
            {"忌堵塞压迫阻气机，忌堆杂物挡通路，忌污秽不堪招病灾", "巽位堵塞文运暗，污秽主肝胆病，长女不利"},
            {"忌空旷无靠失根基，忌建厕所污土气，忌尖角冲射招是非", "坤位虚空老母病，设厕所主脾胃，财运破败"},
            {"忌低洼潮湿损金气，忌建厨房火克金，忌破碎不堪招灾祸", "乾位低洼老父病，设厨房主头痛，贵人远去"},
            {"忌道路直冲犯凶煞，忌建厕所污土气，忌崩塌破碎损人丁", "艮位冲射少男伤，崩塌主手足疾，家宅危"}
        };
        
        String[][] layoutPingInfo = {
            {"宜建杂物间储物，保持整洁有条理，适度通风养木气", "东方平位宜静，杂物间设此，安稳度日"},
            {"宜建储藏室储物，保持通风又干燥，适度采光养火气", "南方平位宜守，储藏室设此，平淡安康"},
            {"宜建厨房或储物，保持干净又卫生，适度使用养金气", "西方平位宜和，厨房设此，和气生财"},
            {"宜建花园养绿地，保持湿润有生机，适度运动养水气", "北方平位宜缓，花园设此，福寿绵长"},
            {"宜设书房或办公，保持通风又明亮，适度学习养秀气", "东南平位宜学，书房设此，稳步提升"},
            {"宜设卧室或储藏，保持舒适又安稳，适度休息养土气", "西南平位宜养，卧室设此，身心安康"},
            {"宜设书房或储物，保持整洁又有序，适度思考养金气", "西北平位宜思，书房设此，贵人相助"},
            {"宜建花园或绿地，保持生机又盎然，适度劳作养土气", "东北平位宜实，花园设此，子孙贤"}
        };

        TextView[] zhaoxiangViews = {eastZhaoxiang, southZhaoxiang, westZhaoxiang, northZhaoxiang,
            southeastZhaoxiang, southwestZhaoxiang, northwestZhaoxiang, northeastZhaoxiang};
        TextView[] shuishaViews = {eastShuisha, southShuisha, westShuisha, northShuisha,
            southeastShuisha, southwestShuisha, northwestShuisha, northeastShuisha};
        TextView[] layoutViews = {eastLayout, southLayout, westLayout, northLayout,
            southeastLayout, southwestLayout, northwestLayout, northeastLayout};

        for (int i = 0; i < 8; i++) {
            String direction = directions[i];
            String bfBagua = baguas[i];
            String bfWuxing = wuxings[i];
            String beast = beasts[i];
            String relation = getDirectionRelation(mountain, chaoXiang, direction);

            String wuxingRelation = getWuxingRelation(wuxing, bfWuxing);
            String jiXiong = getLuckFromWuxingRelation(wuxingRelation);
            String luckColor = jiXiong.equals("吉") ? "#00CC00" : jiXiong.equals("凶") ? "#FF4444" : "#FFAA00";

            String zhaoxiangDetail = getZhaoxiangDetail(bfBagua, bfWuxing, beast, jiXiong);
            String zuoXiangDetail = getZuoXiangDetail(direction, mountain, chaoXiang, relation);

            if (zhaoxiangViews[i] != null) {
                String zhaoxiangHtml = "<b>格局：</b>" + bfBagua + "卦·" + bfWuxing + "·" + beast +
                    " " + relation + wuxingRelation +
                    "<font color='" + luckColor + "'>【" + jiXiong + "】</font>" +
                    "<br/><font color='#8899AA'>　" + zhaoxiangDetail + "</font>" +
                    "<br/><b>坐向：</b>" + zuoXiangDetail;
                zhaoxiangViews[i].setText(Html.fromHtml(zhaoxiangHtml));
            }
            if (shuishaViews[i] != null) {
                String[] currentShuiSha = jiXiong.equals("吉") ? shuiShaJiInfo[i] :
                                         jiXiong.equals("凶") ? shuiShaXiongInfo[i] : shuiShaPingInfo[i];
                String shuishaHtml = "<b>砂水：</b>" + currentShuiSha[0] +
                    "<br/><font color='#8899AA'>　" + currentShuiSha[1] + "</font>";
                shuishaViews[i].setText(Html.fromHtml(shuishaHtml));
            }
            if (layoutViews[i] != null) {
                String[] currentLayout = jiXiong.equals("吉") ? layoutJiInfo[i] :
                                         jiXiong.equals("凶") ? layoutXiongInfo[i] : layoutPingInfo[i];
                String layoutHtml = "<b>布局：</b>" + currentLayout[0] +
                    "<br/><font color='#8899AA'>　" + currentLayout[1] + "</font>";
                layoutViews[i].setText(Html.fromHtml(layoutHtml));
            }
        }
    }
    
    private String getLuckFromWuxingRelation(String relation) {
        if (relation.equals("【比和】") || relation.equals("【生我】")) return "吉";
        if (relation.equals("【我生】") || relation.equals("【我克】")) return "平";
        if (relation.equals("【克我】")) return "凶";
        return "平";
    }

    private String getWuxingRelation(String mainWuxing, String otherWuxing) {
        if (mainWuxing.equals(otherWuxing)) return "【比和】";
        if ((mainWuxing.equals("木") && otherWuxing.equals("火")) ||
            (mainWuxing.equals("火") && otherWuxing.equals("土")) ||
            (mainWuxing.equals("土") && otherWuxing.equals("金")) ||
            (mainWuxing.equals("金") && otherWuxing.equals("水")) ||
            (mainWuxing.equals("水") && otherWuxing.equals("木"))) {
            return "【生我】";
        }
        if ((mainWuxing.equals("火") && otherWuxing.equals("木")) ||
            (mainWuxing.equals("土") && otherWuxing.equals("火")) ||
            (mainWuxing.equals("金") && otherWuxing.equals("土")) ||
            (mainWuxing.equals("水") && otherWuxing.equals("金")) ||
            (mainWuxing.equals("木") && otherWuxing.equals("水"))) {
            return "【我生】";
        }
        if ((mainWuxing.equals("木") && otherWuxing.equals("金")) ||
            (mainWuxing.equals("火") && otherWuxing.equals("水")) ||
            (mainWuxing.equals("土") && otherWuxing.equals("木")) ||
            (mainWuxing.equals("金") && otherWuxing.equals("火")) ||
            (mainWuxing.equals("水") && otherWuxing.equals("土"))) {
            return "【克我】";
        }
        return "【我克】";
    }

    private String getDirectionString(float azimuth) {
        String[] directions = {"北", "东北", "东", "东南", "南", "西南", "西", "西北"};
        int index = (int) ((azimuth + 22.5) / 45) % 8;
        return directions[index];
    }

    
    
    private String getZhaoxiangDetail(String bagua, String wuxing, String beast, String jiXiong) {
        switch (bagua) {
            case "震":
                return "震卦主动，属木为青龙，象长子。震为雷，主奋发有为、积极进取，东方日出之地，阳气始生";
            case "离":
                return "离卦主明，属火为朱雀，象中女。离为火，主光明磊落、文采飞扬，南方正午之地，阳气最盛";
            case "兑":
                return "兑卦主悦，属金为白虎，象少女。兑为泽，主喜悦和顺、口舌生财，西方日落之地，阳气收敛";
            case "坎":
                return "坎卦主险，属水为玄武，象中男。坎为水，主智慧深沉、根基稳固，北方子夜之地，阴气最盛";
            case "巽":
                return "巽卦主入，属木为青龙辅，象长女。巽为风，主文书学业、贵人相助，东南春夏之交，气机舒展";
            case "坤":
                return "坤卦主顺，属土为白虎辅，象老母。坤为地，主厚德载物、包容养育，西南夏秋之交，土气厚重";
            case "乾":
                return "乾卦主健，属金为玄武辅，象老父。乾为天，主刚健中正、权威显赫，西北秋冬之交，金气肃杀";
            case "艮":
                return "艮卦主止，属土为青龙辅，象少男。艮为山，主静止稳固、积蓄力量，东北冬春之交，土气收藏";
            default:
                return "";
        }
    }

    private String getZuoXiangDetail(String direction, String mountain, String chaoXiang, String relation) {
        String mountainDirection = getMountainDirection(mountain);
        String chaoXiangDirection = getChaoXiangDirection(chaoXiang);
        if (mountainDirection.equals(direction)) {
            return "坐山" + mountain + "，背靠此方，如人坐椅，宜有高大山峦或建筑为靠，主根基稳固、贵人扶持";
        } else if (chaoXiangDirection.equals(direction)) {
            return "朝向" + chaoXiang + "，面朝此方，如人望前，宜开阔明亮有水环抱，主前程光明、财运亨通";
        } else if (relation.equals("为青龙方")) {
            return "青龙位，主长子贵人，宜高耸秀丽、草木繁茂，青龙昂首则家道兴旺、贵人提携";
        } else if (relation.equals("为白虎方")) {
            return "白虎位，主少女财帛，宜低伏驯服、地势平坦，白虎抬头则招是非、家宅不宁";
        }
        return "辅弼位，主辅助呼应，宜平整无缺、与主位协调，辅弼有情则福禄绵长、家宅安宁";
    }

    private String getDirectionRelation(String mountain, String chaoXiang, String direction) {
        String mountainDirection = getMountainDirection(mountain);
        String chaoXiangDirection = getChaoXiangDirection(chaoXiang);
        
        if (mountainDirection.equals(direction)) {
            return "为坐山";
        } else if (chaoXiangDirection.equals(direction)) {
            return "为朝向";
        } else if (isLeftSide(mountainDirection, direction)) {
            return "为青龙方";
        } else if (isRightSide(mountainDirection, direction)) {
            return "为白虎方";
        }
        return "为辅弼方";
    }
    
    private String getMountainDirection(String mountain) {
        String[] mountains = {"子", "癸", "丑", "艮", "寅", "甲", "卯", "乙", "辰", "巽", "巳", "丙", "午", "丁", "未", "坤", "申", "庚", "酉", "辛", "戌", "乾", "亥", "壬"};
        String[] directions = {"北方", "北方", "东北", "东北", "东方", "东方", "东方", "东方", "东南", "东南", "南方", "南方", "南方", "南方", "西南", "西南", "西方", "西方", "西方", "西方", "西北", "西北", "北方", "北方"};
        for (int i = 0; i < mountains.length; i++) {
            if (mountains[i].equals(mountain)) {
                return directions[i];
            }
        }
        return "北方";
    }
    
    private String getChaoXiangDirection(String chaoXiang) {
        // chaoXiang 是山名（如"午"），基于山名索引返回对应方向
        return getMountainDirection(chaoXiang);
    }
    
    private boolean isLeftSide(String mountainDirection, String direction) {
        String[] order = {"北方", "东北", "东方", "东南", "南方", "西南", "西方", "西北"};
        int mountainIndex = -1, dirIndex = -1;
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(mountainDirection)) mountainIndex = i;
            if (order[i].equals(direction)) dirIndex = i;
        }
        if (mountainIndex == -1 || dirIndex == -1) return false;
        int diff = (dirIndex - mountainIndex + 8) % 8;
        return diff >= 1 && diff <= 3;
    }
    
    private boolean isRightSide(String mountainDirection, String direction) {
        String[] order = {"北方", "东北", "东方", "东南", "南方", "西南", "西方", "西北"};
        int mountainIndex = -1, dirIndex = -1;
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(mountainDirection)) mountainIndex = i;
            if (order[i].equals(direction)) dirIndex = i;
        }
        if (mountainIndex == -1 || dirIndex == -1) return false;
        int diff = (dirIndex - mountainIndex + 8) % 8;
        return diff >= 5 && diff <= 7;
    }

    private String getDizhiForMountain(String mountain) {
        String[] dizhiMap = {"子", "癸", "丑", "艮", "寅", "甲", "卯", "乙", "辰",
                "巽", "巳", "丙", "午", "丁", "未", "坤", "申", "庚", "酉", "辛", "戌", "乾", "亥", "壬"};
        String[] dizhiOnly = {"子", "", "丑", "", "寅", "", "卯", "", "辰", "", "巳", "",
                "午", "", "未", "", "申", "", "酉", "", "戌", "", "亥", ""};
        for (int i = 0; i < dizhiMap.length; i++) {
            if (dizhiMap[i].equals(mountain)) {
                return dizhiOnly[i].isEmpty() ? mountain : dizhiOnly[i];
            }
        }
        return mountain;
    }

    private String getJiXiong(String mountain, String wuxing) {
        String[] luckyMountains = {"子", "午", "卯", "酉", "艮", "坤", "巽", "乾"};
        for (String lm : luckyMountains) {
            if (lm.equals(mountain)) return "吉";
        }
        return "中";
    }

    private String getGuiZang(String wuxing) {
        switch (wuxing) {
            case "木": return "归藏震卦·动";
            case "火": return "归藏离卦·丽";
            case "土": return "归藏坤卦·藏";
            case "金": return "归藏兑卦·说";
            case "水": return "归藏坎卦·陷";
            default: return wuxing;
        }
    }

    private void setupBazhaiNinePalace(String bagua, float rotation) {
        String[] baguaList = {"坎", "艮", "震", "巽", "中", "乾", "兑", "坤", "离"};
        String[] directions = {"北方", "东北", "东方", "东南", "", "西北", "西方", "西南", "南方"};
        String[] wuxingList = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
        
        int baguaIndex = -1;
        for (int i = 0; i < baguaList.length; i++) {
            if (baguaList[i].equals(bagua)) {
                baguaIndex = i;
                break;
            }
        }
        
        int[][] bazhaiFlying = {
            {6,3,1,2,5,4,7,0},
            {5,7,4,1,2,3,6,0},
            {2,1,4,7,6,3,5,0},
            {3,6,5,4,1,7,2,0},
            {5,6,7,2,4,1,3,0},
            {3,2,7,1,4,6,5,0},
            {1,4,2,7,5,6,3,0},
            {5,3,6,4,7,2,1,0}
        };
        
        String[] bazhaiNames = {"伏位", "生气", "延年", "天医", "祸害", "六煞", "五鬼", "绝命"};
        String[] bazhaiLuck = {"平", "吉", "吉", "吉", "凶", "凶", "凶", "凶"};
        String[] bazhaiMeaning = {
            "平稳守成，宜静不宜动",
            "旺丁旺财，生机勃勃",
            "健康长寿，夫妻和睦",
            "疾病痊愈，贵人相助",
            "口舌是非，家宅不安",
            "桃花纠纷，感情困扰",
            "官非灾祸，破财伤身",
            "大凶之位，百事不宜"
        };
        
        StringBuilder sb = new StringBuilder();
        sb.append("<b>八宅（").append(bagua).append("宅）：</b><br/>");

        if (baguaIndex >= 0 && baguaIndex <= 7) {
            int[] flying = bazhaiFlying[baguaIndex];
            for (int i = 0; i < 8; i++) {
                int starIndex = flying[i];
                int dirIndex = i;
                if (dirIndex >= 4) dirIndex++;

                String luckColor = bazhaiLuck[starIndex].equals("吉") ? "#00CC00" :
                                   bazhaiLuck[starIndex].equals("凶") ? "#FF4444" : "#FFAA00";
                sb.append(bazhaiNames[starIndex]);
                sb.append("<font color='").append(luckColor).append("'>【").append(bazhaiLuck[starIndex]).append("】</font>");
                sb.append(directions[dirIndex]).append("·").append(baguaList[dirIndex]).append("卦·").append(wuxingList[dirIndex]);
                sb.append(" ").append(bazhaiMeaning[starIndex]).append("<br/>");
            }
        }

        // 通俗原因解读
        sb.append("<br/><font color='#CCB866'><b>💡 通俗解读：</b></font><br/>");
        sb.append(getBazhaiPlainExplanation(bagua));
        bazhaiDesc.setText(Html.fromHtml(sb.toString()));
    }

    /**
     * 八宅风水的通俗解读：让普通人也能看懂为什么这样判断
     */
    private String getBazhaiPlainExplanation(String bagua) {
        // 判断东四宅/西四宅
        boolean isEast = bagua.equals("坎") || bagua.equals("离") || bagua.equals("震") || bagua.equals("巽");
        String group = isEast ? "东四宅" : "西四宅";
        String groupGua = isEast ? "坎、离、震、巽" : "乾、坤、艮、兑";
        String groupElement = isEast ? "水、火、木、木" : "金、土、土、金";
        String groupFeature = isEast ? "木火相生、水木相润，主生发向上、朝气蓬勃"
                                       : "金土相生、厚土载物，主稳固厚重、藏风聚气";

        StringBuilder sb = new StringBuilder();
        sb.append("为何这样排？简单说就是「<b>物以类聚</b>」的道理。<br/>");
        sb.append("「").append(bagua).append("」属").append(group).append("（")
          .append(groupGua).append("，五行").append(groupElement).append("），");
        sb.append("其特点是").append(groupFeature).append("。<br/><br/>");

        sb.append(BAZHAI_KOUJUE);

        sb.append("<font color='#CCB866'>一句话总结：</font>");
        sb.append("吉方放重要房间（卧室、客厅、大门），凶方放次要房间（厕所、储物间），");
        sb.append("这就叫「<b>趋吉避凶</b>」，是八宅风水的核心智慧。");

        return sb.toString();
    }

    private void setupJiuxingNinePalace(String bagua, float rotation) {
        String[] baguaList = {"坎", "艮", "震", "巽", "中", "乾", "兑", "坤", "离"};
        String[] directions = {"北方", "东北", "东方", "东南", "", "西北", "西方", "西南", "南方"};
        String[] wuxingList = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
        
        int[] baguaNumbers = {1, 8, 3, 4, 5, 6, 7, 2, 9};
        int baguaNumber = 5;
        for (int i = 0; i < baguaList.length; i++) {
            if (baguaList[i].equals(bagua)) {
                baguaNumber = baguaNumbers[i];
                break;
            }
        }
        
        String[] jiuxingNames = {"一白贪狼", "二黑巨门", "三碧禄存", "四绿文曲", "五黄廉贞", "六白武曲", "七赤破军", "八白左辅", "九紫右弼"};
        String[] jiuxingWuxing = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
        String[] jiuxingLuck = {"吉", "凶", "凶", "吉", "凶", "吉", "凶", "吉", "吉"};
        String[] jiuxingMeaning = {
            "官贵显达，文昌学业",
            "病符缠身，健康不利",
            "口舌是非，争斗不和",
            "文昌智慧，学业有成",
            "大凶之位，灾祸连连",
            "财权双收，贵人相助",
            "盗贼破财，是非口角",
            "财运亨通，田产丰隆",
            "喜庆吉祥，婚姻美满"
        };
        
        int[] luoShuOrder = {0, 7, 3, 4, 5, 2, 6, 1, 8};
        StringBuilder sb = new StringBuilder();
        sb.append("<b>九星飞宫（").append(bagua).append("卦入中·").append(jiuxingNames[baguaNumber - 1]).append("星）：</b><br/>");
        
        for (int i = 0; i < 9; i++) {
            int starNum = (baguaNumber + luoShuOrder[i]) % 9;
            if (starNum == 0) starNum = 9;
            int starIndex = starNum - 1;
            
            if (i != 4) {
                String luckColor = jiuxingLuck[starIndex].equals("吉") ? "#00CC00" : 
                                   jiuxingLuck[starIndex].equals("凶") ? "#FF4444" : "#FFAA00";
                sb.append(jiuxingNames[starIndex]);
                sb.append("<font color='").append(luckColor).append("'>【").append(jiuxingLuck[starIndex]).append("】</font>");
                sb.append(directions[i]).append("·").append(baguaList[i]).append("卦·").append(jiuxingWuxing[starIndex]);
                sb.append(" ").append(jiuxingMeaning[starIndex]).append("<br/>");
            }
        }

        // 通俗原因解读
        sb.append("<br/><font color='#CCB866'><b>💡 通俗解读：</b></font><br/>");
        sb.append(getJiuxingPlainExplanation(bagua, baguaNumber));
        jiuxingDesc.setText(Html.fromHtml(sb.toString()));
    }

    /**
     * 九星飞宫的通俗解读：让普通人也能看懂为什么这样判断
     */
    private String getJiuxingPlainExplanation(String bagua, int baguaNumber) {
        String[] starNames = {"一白贪狼", "二黑巨门", "三碧禄存", "四绿文曲", "五黄廉贞",
                              "六白武曲", "七赤破军", "八白左辅", "九紫右弼"};
        String inCenterStar = starNames[baguaNumber - 1];

        StringBuilder sb = new StringBuilder();
        sb.append("九星飞宫，简单说就是「<b>轮流值班</b>」的星象系统。<br/>");
        sb.append("古人把九颗虚拟星按洛书顺序排布，每颗星管一种运势，");
        sb.append("中宫之星为「<b>当值星</b>」，决定全局气场基调。<br/><br/>");

        sb.append("当前坐山<b>").append(bagua).append("</b>卦入中，当值星为「<b>")
          .append(inCenterStar).append("</b>」，其影响如下：<br/>");

        // 根据入中星给出本局基调解释
        switch (baguaNumber) {
            case 1:
                sb.append("一白贪狼星（水）入中，水主智、主流通，本局利于学业、考试、远行，整体气场温润柔和，适合读书进修、谋划未来。");
                break;
            case 2:
                sb.append("二黑巨门星（土）入中，土主病符，本局气场沉闷易病，需注意脾胃健康，不宜动土兴工，宜静养、忌躁动。");
                break;
            case 3:
                sb.append("三碧禄存星（木）入中，木主争斗，本局气场躁动易起口舌，须防小人是非、家庭争吵，宜绿色植物化解、忌动怒。");
                break;
            case 4:
                sb.append("四绿文曲星（木）入中，木主文昌，本局利读书考试、文艺创作，是文职人士、学生的吉时，宜读书学习、签约谈事。");
                break;
            case 5:
                sb.append("五黄廉贞星（土）入中，土为大凶煞，本局灾祸连连、疾病破财，宜安静、忌动土搬迁，须用金属风铃或铜器化解。");
                break;
            case 6:
                sb.append("六白武曲星（金）入中，金主权势，本局利升职、求职、武职行业，是事业发展、贵人相助的好时机，宜进取有为。");
                break;
            case 7:
                sb.append("七赤破军星（金）入中，金主肃杀，本局易破财招盗、口角争斗，须防小人暗害、官非纠纷，宜守不宜攻、忌投机。");
                break;
            case 8:
                sb.append("八白左辅星（土）入中，土主财运，本局为当下最吉的旺财星，利置业、投资、求财，是积累财富的好时机。");
                break;
            case 9:
                sb.append("九紫右弼星（火）入中，火主喜庆，本局利婚嫁、添丁、庆祝之事，整体气氛欢快热烈，适合办喜事、谈合作。");
                break;
        }
        sb.append("<br/><br/>");

        sb.append(JIUXING_KOUJUE);

        sb.append("<font color='#CCB866'>一句话总结：</font>");
        sb.append("九星随中宫流转，吉星方位宜活动、办公、休息；凶星方位宜安放金属、植物等物品化解，");
        sb.append("这就是「<b>星随宫转，吉凶有方</b>」的道理。");

        return sb.toString();
    }

    private void setupBamenNinePalace(String bagua, float rotation) {
        String[] baguaList = {"坎", "艮", "震", "巽", "中", "乾", "兑", "坤", "离"};
        String[] directions = {"北方", "东北", "东方", "东南", "", "西北", "西方", "西南", "南方"};
        
        int[] baguaNumbers = {1, 8, 3, 4, 5, 6, 7, 2, 9};
        int baguaNumber = 5;
        for (int i = 0; i < baguaList.length; i++) {
            if (baguaList[i].equals(bagua)) {
                baguaNumber = baguaNumbers[i];
                break;
            }
        }
        
        String[] bamenNames = {"休门", "生门", "伤门", "杜门", "景门", "死门", "惊门", "开门"};
        String[] bamenLuck = {"吉", "吉", "凶", "平", "平", "凶", "凶", "吉"};
        String[] bamenMeaning = {
            "休息养生，百事皆宜",
            "财源广进，最吉之门",
            "损伤争斗，出行不利",
            "闭塞隐藏，宜守不宜攻",
            "名声文书，吉凶参半",
            "衰败丧事，百事不宜",
            "惊恐怪异，口舌是非",
            "通达顺利，利开业求职"
        };
        
        int[] validPositions = {0, 1, 2, 3, 5, 6, 7, 8};
        StringBuilder sb = new StringBuilder();
        sb.append("<b>八门遁法（").append(bagua).append("卦起休门）：</b><br/>");
        
        for (int i = 0; i < bamenNames.length; i++) {
            int posIndex = (i + baguaNumber - 1 + 8) % 8;
            int adjustedPos = validPositions[posIndex];
            
            String luckColor = bamenLuck[i].equals("吉") ? "#00CC00" : 
                               bamenLuck[i].equals("凶") ? "#FF4444" : "#FFAA00";
            sb.append(bamenNames[i]);
            sb.append("<font color='").append(luckColor).append("'>【").append(bamenLuck[i]).append("】</font>");
            sb.append(directions[adjustedPos]).append("·").append(baguaList[adjustedPos]).append("卦");
            sb.append(" ").append(bamenMeaning[i]).append("<br/>");
        }

        // 通俗原因解读
        sb.append("<br/><font color='#CCB866'><b>💡 通俗解读：</b></font><br/>");
        sb.append(getBamenPlainExplanation(bagua, baguaNumber));
        bamenDesc.setText(Html.fromHtml(sb.toString()));
    }

    /**
     * 八门遁法的通俗解读：让普通人也能看懂为什么这样判断
     */
    private String getBamenPlainExplanation(String bagua, int baguaNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("八门，可以理解为「<b>八扇运势之门</b>」，每扇门后面藏着不同的能量。<br/>");
        sb.append("古人行军打仗、出行办事，都要看走哪扇门「<b>吉凶不同</b>」。<br/><br/>");

        sb.append("其原理是：以坐山卦为起点，把八门按固定顺序排到八方，每方一门。<br/>");
        sb.append("当前坐山为<b>").append(bagua).append("</b>卦，从此方起休门，依次顺排。<br/><br/>");

        sb.append(BAMEN_KOUJUE);

        // 根据当前坐山卦，给出实战建议
        sb.append("<font color='#CCB866'>生活应用建议：</font><br/>");
        sb.append(getBamenLifeAdvice(baguaNumber));
        sb.append("<br/>");

        sb.append("<font color='#CCB866'>一句话总结：</font>");
        sb.append("出门办事、做生意、谈合作，先看走的是「吉门」还是「凶门」方位，");
        sb.append("吉门方行事顺利，凶门方易生波折，这就是「<b>择门而行</b>」的智慧。");

        return sb.toString();
    }

    /**
     * 根据当前坐山给出八门生活应用建议
     */
    private String getBamenLifeAdvice(int baguaNumber) {
        String key = String.valueOf(baguaNumber);
        for (String[] a : BAMEN_LIFE_ADVICE) {
            if (a[1].equals(key)) {
                return a[0] + "卦起休门：休门在" + a[2] + "，生门在" + a[3] + "，开门在" + a[4] + "。<br/>"
                     + "• 求财交易 → 走" + a[5] + "；<br/>"
                     + "• 求职开业 → 走" + a[6] + "；<br/>"
                     + "• 谈判婚嫁 → 走" + a[7] + "；<br/>"
                     + "• 忌走" + a[8] + "办事。";
            }
        }
        return "请根据坐山方位选择对应的吉门行事。";
    }

    private String getShanshuiFengshui(String mountain, String chaoXiang, String wuxing, String bagua) {
        StringBuilder sb = new StringBuilder();

        sb.append("<b>山水格局：").append(mountain).append("山").append(chaoXiang).append("向</b><br/>");
        sb.append("<font color='#8899AA'>山管人丁水管财，山环水抱富贵来</font><br/><br/>");

        // 四兽格局总览
        sb.append("<b>【四兽总览】</b><br/>");
        sb.append(getFourBeastsDetail(mountain, wuxing, false));
        sb.append("<br/>");

        // 水法详解
        sb.append("<b>【水法详解】</b><br/>");
        sb.append("水为财源，山为人根；天门宜开，地户宜闭。<br/>");
        sb.append("<font color='#00CC00'>【吉水】</font>环抱水·九曲水·朝海水<br/>");
        sb.append("<font color='#FF4444'>【凶水】</font>直冲水·反弓水·割脚水<br/>");
        sb.append("　水口：").append(getShuikouDirection(mountain)).append("方，宜有关拦<br/>");
        sb.append("　来水：").append(getLaishuiDirection(mountain)).append("方，宜开阔<br/>");

        // 坐山向水吉凶
        sb.append("<br/><b>【坐山向水】</b><br/>");
        sb.append(getShanShuiJiXiong(mountain, chaoXiang, wuxing));

        return sb.toString();
    }

    private String getFourBeastsDetail(String mountain, String wuxing, boolean forYinzhai) {
        StringBuilder sb = new StringBuilder();

        String[] beastNames = {"青龙（左）", "朱雀（前）", "白虎（右）", "玄武（后）"};
        String[] beastWuxings = {"木", "火", "金", "水"};
        String[] yangzhaiJi = {
            "高耸秀丽，主贵人提携",
            "开阔明亮，主名声远播",
            "低伏驯服，主财源广进",
            "厚实稳重，主根基深厚"
        };
        String[] yangzhaiXiong = {
            "低陷破碎，主小人暗算",
            "阴暗闭塞，主口舌是非",
            "高大雄健，主争斗凶灾",
            "空虚塌陷，主根基动摇"
        };
        String[] yinzhaiJi = {
            "宜高耸环抱，主出文贵",
            "宜端正开阔，主有名望",
            "宜低伏驯服，主多财富",
            "宜厚重稳固，主多福寿"
        };
        String[] yinzhaiXiong = {
            "忌高昂反背，主争斗官非",
            "忌尖射逼压，主口舌是非",
            "忌雄健昂头，主凶灾败财",
            "忌空缺塌陷，主根基动摇"
        };

        for (int i = 0; i < 4; i++) {
            String relation = getWuxingRelation(wuxing, beastWuxings[i]);
            String jiXiong = getLuckFromWuxingRelation(relation);
            String color = jiXiong.equals("吉") ? "#00CC00" : jiXiong.equals("凶") ? "#FF4444" : "#FFAA00";
            sb.append("　<font color='").append(color).append("'>【").append(jiXiong).append("】</font>");
            sb.append(beastNames[i]).append("：");
            if (forYinzhai) {
                sb.append(jiXiong.equals("凶") ? yinzhaiXiong[i] : yinzhaiJi[i]);
            } else {
                sb.append(jiXiong.equals("吉") ? yangzhaiJi[i] : yangzhaiXiong[i]);
            }
            sb.append("<br/>");
        }

        return sb.toString();
    }

    private String getLaishuiDirection(String mountain) {
        String[] laishuiMap = {
            "西北", "西北", "西南", "西南", "东南", "东南",
            "东方", "东方", "南方", "南方", "北方", "北方",
            "东南", "东南", "东北", "东北", "西南", "西南",
            "西北", "西北", "东北", "东北", "东方", "东方"
        };
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return laishuiMap[i];
            }
        }
        return "东南";
    }

    private String getShanShuiJiXiong(String mountain, String chaoXiang, String wuxing) {
        StringBuilder sb = new StringBuilder();
        String mountainDir = getMountainDirection(mountain);
        String chaoXiangDir = getChaoXiangDirection(chaoXiang);

        sb.append("坐").append(mountain).append("向").append(chaoXiang).append("，背山面水为上格。<br/>");
        sb.append("　坐山宜有高山，玄武垂头人丁旺<br/>");
        sb.append("　朝向宜有流水，朱雀翔舞财源广<br/>");

        // 判断山水格局类型
        String pattern = getShanshuiPattern(mountain, wuxing);
        sb.append("　格局判定：<font color='#FFD700'><b>").append(pattern).append("</b></font><br/>");

        return sb.toString();
    }

    private String getShanshuiPattern(String mountain, String wuxing) {
        String mountainDir = getMountainDirection(mountain);
        String mountainWuxing = "";
        switch (mountainDir) {
            case "东方": case "东南": mountainWuxing = "木"; break;
            case "南方": mountainWuxing = "火"; break;
            case "西方": case "西北": mountainWuxing = "金"; break;
            case "北方": mountainWuxing = "水"; break;
            case "西南": case "东北": mountainWuxing = "土"; break;
        }

        String relation = getWuxingRelation(wuxing, mountainWuxing);
        if (relation.equals("【比和】")) {
            return "山命比和，根基深厚";
        } else if (relation.equals("【生我】")) {
            return "山生我命，贵人扶持";
        } else if (relation.equals("【我生】")) {
            return "我命生山，先苦后甜";
        } else if (relation.equals("【克我】")) {
            return "山克我命，压力较大";
        } else {
            return "我命克山，驾驭有方";
        }
    }
    
    private String getShuikouDirection(String mountain) {
        String[] shuikouMap = {
            "东南", "东南", "东北", "东北", "西南", "西南", 
            "西方", "西方", "北方", "北方", "东方", "东方",
            "西北", "西北", "南方", "南方", "东北", "东北",
            "东南", "东南", "西南", "西南", "西方", "西方"
        };
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return shuikouMap[i];
            }
        }
        return "东南";
    }
    
    private String getYinzhaiFengshui(String mountain, String chaoXiang, String wuxing, String bagua) {
        StringBuilder sb = new StringBuilder();

        sb.append("<b>寻龙点穴，龙穴砂水向五吉</b><br/>");
        sb.append("<font color='#8899AA'>阴宅重藏风聚气，先人安则后代宁</font><br/><br/>");

        // 龙穴砂水向 五要素
        sb.append("<b>【地理五诀】</b><br/>");
        sb.append(getDiLiWuJue(mountain, chaoXiang, wuxing));
        sb.append("<br/>");

        // 寻龙要点（扩充）
        sb.append("<b>【寻龙点穴】</b><br/>");
        sb.append("<font color='#00CC00'>【龙】</font>龙要真：起伏有气势，有祖宗<br/>");
        sb.append("<font color='#00CC00'>【龙】</font>龙要活：蜿蜒有生气，草木茂<br/>");
        sb.append("<font color='#00CC00'>【龙】</font>龙要旺：山形饱满，生气旺<br/>");
        sb.append("<font color='#00CC00'>【龙】</font>龙要止：到头有结作，有水护<br/>");
        sb.append("<font color='#FF4444'>【忌】</font>龙怕断：气不连，后代贫病<br/>");
        sb.append("<font color='#FF4444'>【忌】</font>龙怕硬：僵硬无生气，人丁稀<br/><br/>");

        // 点穴要领（扩充）
        sb.append("<b>【点穴秘法】</b><br/>");
        sb.append("<font color='#00CC00'>【穴】</font>穴要的：藏风聚气，差之千里<br/>");
        sb.append("<font color='#00CC00'>【穴】</font>穴要暖：土质温润，人多福寿<br/>");
        sb.append("<font color='#00CC00'>【穴】</font>穴要稳：背靠主山，根基固<br/>");
        sb.append("<font color='#00CC00'>【穴】</font>穴要净：清净无恶石，福绵长<br/>");
        sb.append("<font color='#FF4444'>【忌】</font>穴怕风：气散，后代贫寒<br/>");
        sb.append("<font color='#FF4444'>【忌】</font>穴怕水：气散，败财损丁<br/><br/>");

        // 砂水环抱（扩充，随坐山变化）
        sb.append("<b>【砂水环抱】</b><br/>");
        sb.append(getFourBeastsDetail(mountain, wuxing, true));
        sb.append("<br/>");

        // 水口详解
        sb.append("<b>【水口关拦】</b><br/>");
        sb.append("水口为财库门户。<br/>");
        sb.append("<font color='#00CC00'>【吉】</font>紧锁有关拦，财气不散<br/>");
        sb.append("<font color='#FF4444'>【凶】</font>直泄无遮拦，财去人散<br/>");
        sb.append("　水口方位：").append(getShuikouDirection(mountain)).append("方，宜有山峦洲渚关拦<br/>");
        sb.append("　天门开：来水之方宜开阔，水有源则财无尽<br/>");
        sb.append("　地户闭：去水之方宜紧闭，水有拦则财常聚<br/><br/>");

        // 明堂
        sb.append("<b>【明堂格局】</b><br/>");
        sb.append(getMingTangDetail(mountain, chaoXiang));
        sb.append("<br/>");

        // 坐山向具体建议（保留并丰富）
        sb.append("<b>【").append(mountain).append("山").append(chaoXiang).append("向】</b><br/>");
        sb.append(getYinzhaiSpecificAdvice(mountain));
        sb.append("<br/>");
        sb.append("　来龙方位：").append(getLaiLongDirection(mountain)).append("方来龙为上，山势宜起伏有力<br/>");
        sb.append("　格局评定：").append(getYinzhaiPattern(mountain, wuxing)).append("<br/>");

        return sb.toString();
    }

    private String getDiLiWuJue(String mountain, String chaoXiang, String wuxing) {
        StringBuilder sb = new StringBuilder();
        String mountainDir = getMountainDirection(mountain);
        String chaoXiangDir = getChaoXiangDirection(chaoXiang);

        sb.append("　<font color='#90EE90'><b>龙</b></font>：主贵贱，宜有来龙<br/>");
        sb.append("　<font color='#FFD700'><b>穴</b></font>：主吉凶，藏风聚气<br/>");
        sb.append("　<font color='#FFA500'><b>砂</b></font>：主贤愚，四兽齐备<br/>");
        sb.append("　<font color='#87CEEB'><b>水</b></font>：主财运，环抱为佳<br/>");
        sb.append("　<font color='#FF6B6B'><b>向</b></font>：主兴衰，向法合度<br/>");

        return sb.toString();
    }

    private String getMingTangDetail(String mountain, String chaoXiang) {
        StringBuilder sb = new StringBuilder();
        sb.append("明堂：穴前水聚之处。<br/>");
        sb.append("　内明堂：宜平整，主初兴<br/>");
        sb.append("　中明堂：宜明亮，主发福<br/>");
        sb.append("　外明堂：宜广阔，主荣昌<br/>");
        sb.append("　<font color='#00CC00'>【吉】</font>四周高中间低，水聚天心，富贵双全<br/>");
        sb.append("　<font color='#FF4444'>【凶】</font>水去直泄无遮拦，财散人离，家道败落<br/>");
        sb.append("　").append(chaoXiang).append("向之明堂：宜开阔平整有朝山，案山近则速发，朝山远则福长");

        return sb.toString();
    }

    private String getLaiLongDirection(String mountain) {
        String[] lailongMap = {
            "西北", "正北", "西北", "正北", "东北", "正东", "东北", "正东",
            "东南", "正南", "东南", "正南", "正南", "正南", "西南", "西南",
            "正西", "正西", "正西", "西北", "西北", "西北", "正北", "东北"
        };
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return lailongMap[i];
            }
        }
        return "西北";
    }

    private String getYinzhaiPattern(String mountain, String wuxing) {
        String mountainDir = getMountainDirection(mountain);
        String mountainWuxing = "";
        switch (mountainDir) {
            case "东方": case "东南": mountainWuxing = "木"; break;
            case "南方": mountainWuxing = "火"; break;
            case "西方": case "西北": mountainWuxing = "金"; break;
            case "北方": mountainWuxing = "水"; break;
            case "西南": case "东北": mountainWuxing = "土"; break;
        }

        String relation = getWuxingRelation(wuxing, mountainWuxing);
        if (relation.equals("【比和】")) {
            return "<font color='#FFD700'><b>山命同气</b></font> — 坐山与命主五行比和，阴阳调和，福泽绵长，属于上吉之格";
        } else if (relation.equals("【生我】")) {
            return "<font color='#00CC00'><b>山生命主</b></font> — 坐山生助命主，先灵安则后人福，属于大吉之格，子孙昌盛";
        } else if (relation.equals("【我生】")) {
            return "<font color='#FFAA00'><b>命主生山</b></font> — 命主生扶坐山，泄秀之象，虽有耗损但文采秀发，后代出文人";
        } else if (relation.equals("【克我】")) {
            return "<font color='#FF4444'><b>山克命主</b></font> — 坐山克制命主，阴克阳之象，需择吉日良辰安葬，可化凶为吉";
        } else {
            return "<font color='#FF6B6B'><b>命主克山</b></font> — 命主克制坐山，阳克阴之象，占之不利，宜另择吉地或用法化解";
        }
    }
    
    private String getYinzhaiSpecificAdvice(String mountain) {
        String[] advice = {
            "子山午向：宜选北方有山玄武厚实，南方有水朱雀开阔之地。山势宜从西北方来龙，东南方出水为吉。",
            "癸山丁向：宜选东北方有山，西南方有水之地。山势宜从北方来龙，东南方出水为佳。",
            "丑山未向：宜选东北方有山守护，南方有水开阔之地。山势宜从西北方来龙，西南方出水为吉。",
            "艮山坤向：宜选东北方有高大山峰，西南方有水旺财之地。山势宜从东北方来龙，南方出水为佳。",
            "寅山申向：宜选东方有山青龙高耸，西方有水白虎驯服之地。山势宜从东北方来龙，南方出水为吉。",
            "甲山庚向：宜选东方有山，北方有水之地。山势宜从东方来龙，西方出水为佳。",
            "卯山酉向：宜选东方有山青龙高耸，西方有水朱雀开阔之地。山势宜从东南方来龙，西方出水为吉。",
            "乙山辛向：宜选东南方有山，西方有水之地。山势宜从东方来龙，西北方出水为佳。",
            "辰山戌向：宜选东南方有山，西北方有水之地。山势宜从南方来龙，北方出水为吉。",
            "巽山乾向：宜选东南方有水旺财，西北方有山贵人之地。山势宜从东南方来龙，西北方出水为佳。",
            "巳山亥向：宜选南方有山，北方有水之地。山势宜从东南方来龙，西北方出水为吉。",
            "丙山壬向：宜选南方有山朱雀高耸，北方有水玄武厚实之地。山势宜从南方来龙，北方出水为佳。",
            "午山子向：宜选南方有山，北方有水之地。此为水火既济格局，山势宜从南方来龙，北方出水为吉。",
            "丁山癸向：宜选西南方有山，北方有水之地。山势宜从南方来龙，东北方出水为佳。",
            "未山丑向：宜选西南方有山，东北方有水之地。山势宜从西南方来龙，东北方出水为吉。",
            "坤山艮向：宜选西南方有山靠山稳固，东北方有水旺财之地。山势宜从西南方来龙，东北方出水为佳。",
            "申山寅向：宜选西方有山白虎驯服，东方有水青龙高耸之地。山势宜从西北方来龙，东方出水为吉。",
            "庚山甲向：宜选西方有山，东方有水之地。山势宜从西方来龙，东方出水为佳。",
            "酉山卯向：宜选西方有山，东方有水之地。山势宜从西北方来龙，东方出水为吉。",
            "辛山乙向：宜选西北方有山，东方有水之地。山势宜从西方来龙，东南方出水为佳。",
            "戌山辰向：宜选西北方有山，东南方有水之地。山势宜从西北方来龙，东南方出水为吉。",
            "乾山巽向：宜选西北方有高大山峰贵人相助，东南方有水旺财之地。山势宜从西北方来龙，东南方出水为佳。",
            "亥山巳向：宜选北方有水旺财，东南方有山之地。山势宜从北方来龙，东南方出水为吉。",
            "壬山丙向：宜选北方有山玄武厚实，南方有水朱雀开阔之地。山势宜从北方来龙，南方出水为佳。"
        };
        
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return advice[i];
            }
        }
        return "宜选靠山稳固、砂水环绕、明堂开阔之地，山势宜蜿蜒起伏，水势宜环抱有情。";
    }
    
    private String getLuoPanSummary(String mountain, String chaoXiang, String wuxing) {
        String jixiong = getJiXiong(mountain, wuxing);
        String[] luckyDirs = {"南方", "东南方", "东方", "东北方", "西方", "西南方", "西北方", "北方"};
        
        int mountainIndex = 0;
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                mountainIndex = i;
                break;
            }
        }
        String luckyDir = luckyDirs[mountainIndex % 8];

        return "坐" + mountain + "向" + chaoXiang + "，五行属" + wuxing + "，" + jixiong + "。吉利方位：" + luckyDir;
    }
}