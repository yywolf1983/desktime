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
            "古人把方位与人体对应起来：<br/>"
            + "• <b>生气方</b>（最吉）：木气生火，如春风化雨，象征生命力最旺盛，主丁财两旺，适合做主卧、大门；<br/>"
            + "• <b>天医方</b>（次吉）：土生金，如良医诊治，主祛病延年，适合体弱者居住或设药柜；<br/>"
            + "• <b>延年方</b>（次吉）：金水相生，如夫妻和谐，主婚姻美满、健康长寿，适合夫妻房；<br/>"
            + "• <b>伏位方</b>（平）：本命位，如人守本分，主平稳但无大起色，适合静养、读书；<br/>"
            + "• <b>祸害方</b>（凶）：土克水，如暗箭伤人，主口舌是非、胃病，宜作厕所压煞；<br/>"
            + "• <b>六煞方</b>（凶）：水多木浮，如桃花纠缠，主感情纠纷、失眠，宜作储物间；<br/>"
            + "• <b>五鬼方</b>（凶）：火克金，如烈火炼金，主官非破财、火灾，宜作厨房以火制火；<br/>"
            + "• <b>绝命方</b>（大凶）：金克木，如刀斧伐木，主伤残、绝嗣，宜作厕所或仓库镇压。<br/><br/>";

    // 九星吉凶口诀
    private static final String JIUXING_KOUJUE =
            "九星吉凶口诀：<br/>"
            + "• <font color='#00CC00'><b>一白</b></font>（水）→ 文昌官贵，利考试求职；<br/>"
            + "• <font color='#FF4444'><b>二黑</b></font>（土）→ 病符缠身，主脾胃病灾；<br/>"
            + "• <font color='#FF4444'><b>三碧</b></font>（木）→ 蚩尤是非，主口舌争斗；<br/>"
            + "• <font color='#00CC00'><b>四绿</b></font>（木）→ 文曲智慧，利读书创作；<br/>"
            + "• <font color='#FF4444'><b>五黄</b></font>（土）→ 廉贞大煞，主灾祸破财；<br/>"
            + "• <font color='#00CC00'><b>六白</b></font>（金）→ 武曲权威，利升职武职；<br/>"
            + "• <font color='#FF4444'><b>七赤</b></font>（金）→ 破军肃杀，主贼盗官非；<br/>"
            + "• <font color='#00CC00'><b>八白</b></font>（土）→ 左辅旺财，利置业投资；<br/>"
            + "• <font color='#00CC00'><b>九紫</b></font>（火）→ 右弼喜庆，利婚嫁添丁。<br/><br/>";

    // 八门性格与用途口诀
    private static final String BAMEN_KOUJUE =
            "八门的「性格」与「用途」：<br/>"
            + "• <font color='#00CC00'><b>开门</b></font>（吉）：如大门敞开，通达顺利，宜开业、求职、远行、谈判，是「万事开头顺利」之门；<br/>"
            + "• <font color='#00CC00'><b>休门</b></font>（吉）：如休息调养，百事皆宜，宜婚嫁、求财、安葬、求职，是「养精蓄锐」之门；<br/>"
            + "• <font color='#00CC00'><b>生门</b></font>（吉）：如生机萌发，财源广进，宜求财、交易、建造、播种，是「最吉之门」主财运；<br/>"
            + "• <font color='#FFAA00'><b>杜门</b></font>（平）：如门户关闭，闭塞隐藏，宜躲避、隐藏、保密之事，忌求职、谈事；<br/>"
            + "• <font color='#FFAA00'><b>景门</b></font>（平）：如风景在望，文书吉庆，宜考试、诉讼、文书之事，但略带血光；<br/>"
            + "• <font color='#FF4444'><b>伤门</b></font>（凶）：如刀刃伤人，损伤争斗，宜追债、打猎、擒贼，忌婚嫁、出行、搬迁；<br/>"
            + "• <font color='#FF4444'><b>死门</b></font>（凶）：如死气沉沉，衰败丧事，宜安葬、行刑、狩猎，百事不宜，主大凶；<br/>"
            + "• <font color='#FF4444'><b>惊门</b></font>（凶）：如惊雷突至，惊恐怪异，宜诉讼、捕盗，忌求财、谈判，主口舌是非。<br/><br/>";

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
            {"青龙高耸苍翠，如林木参天，主文昌鼎盛、贵人提携。山峰尖秀主科举及第，山脉连绵主子孙富贵", "东方属木为青龙，宜山峰挺拔、树木葱郁。青龙昂首则阳气充足，家道兴隆，长子有出息"},
            {"朱雀展翅翔舞，如鸾凤飞鸣，主名声远播、事业昌隆。明堂开阔则纳气充足，阳光普照则福泽绵长", "南方属火为朱雀，宜山峦秀丽、水体环绕。朱雀翔舞则文运亨通，事业兴旺，中女有作为"},
            {"白虎驯服卧伏，如猛虎归山，主财源广进、家宅安宁。山势柔顺则无凶煞，水流环抱则聚财气", "西方属金为白虎，宜山势低伏、道路平坦。白虎驯服则家宅平安，财运亨通，少女有福泽"},
            {"玄武厚重端坐，如巨龟伏眠，主根基稳固、子孙繁盛。山环水绕则藏风聚气，土厚水深则福寿双全", "北方属水为玄武，宜山形厚实、水流曲折。玄武垂头则靠山有力，根基深厚，晚运亨通"},
            {"巽峰高耸入云，如文笔插天，主文星高照、贵人临门。溪流婉转则财源广进，花木繁茂则喜气盈门", "东南属木为青龙辅，宜山峰秀丽、水清沙明。巽位得位则文运昌盛，家出读书人，长女聪慧"},
            {"坤砂端方厚重，如大地承载，主家庭和睦、人丁兴旺。山圆土厚则多福多寿，明堂宽广则纳吉祥", "西南属土为白虎辅，宜地势平坦、山形圆润。坤位安宁则老母康健，家宅和睦，财运稳定"},
            {"乾峰巍峨挺拔，如天柱高耸，主权威显赫、事业发达。山环水抱则财禄汇聚，地势高昂则权势尊崇", "西北属金为玄武辅，宜山势雄伟、金气充足。乾位得势则官运亨通，老父康健，家中出贵人"},
            {"艮砂敦厚稳重，如磐石坐镇，主家宅平安、子孙昌盛。土质温润则福寿绵长，靠山稳固则基业长青", "东北属土为青龙辅，宜山形敦厚、土质肥沃。艮位安宁则少男健壮，子孙兴旺，基础牢固"}
        };
        
        String[][] shuiShaXiongInfo = {
            {"青龙残缺破碎，如枯木凋零，主官非口舌、家道衰败。山峰尖射则凶祸临门，树木枯槁则人丁损伤", "东方属木忌破碎，青龙低头则长子多难。尖角冲射主车祸官非，凹陷空缺主肝胆疾病"},
            {"朱雀阴暗污浊，如邪火焚身，主疾病缠身、横祸不断。尖射直冲则血光灾，污水逆流则损丁财", "南方属火忌冲射，朱雀开口则口舌是非。阴暗潮湿主心病眼疾，污水直泄主财运大败"},
            {"白虎昂头怒吼，如猛虎出柙，主争斗凶灾、家宅不宁。高大雄健则杀气重，道路直冲则人丁损", "西方属金忌高昂，白虎抬头则少女遭殃。道路喧噪声烦，主肺疾呼吸系统问题，破财招灾"},
            {"玄武空缺凹陷，如破龟翻身，主破财败家、根基动摇。开门纳气则财气散，水流直泄则家业败", "北方属水忌空缺，玄武无靠则晚景凄凉。低洼潮湿主肾病耳疾，水流直去主财源断绝"},
            {"巽位堵塞压迫，如乌云蔽日，主困顿阻滞、病灾频仍。污秽堆积则瘟疫病，闭塞不通则运势衰", "东南属木忌堵塞，巽位闭塞则文运暗弱。污秽堆积主肝胆疾病，长女不利，学业受阻"},
            {"坤砂陡峭破碎，如崩土坍塌，主灾祸连绵、老母有殃。地势倾斜则家宅乱，空旷无靠则根基危", "西南属土忌陡峭，坤砂破碎则老母灾病。地势不平主脾胃损伤，家宅不安，财运破败"},
            {"乾位低洼潮湿，如金沉水底，主财气散尽、贵人远离。破碎不堪则招灾祸，金气不足则事业衰", "西北属金忌低洼，乾位空缺则贵人失助。潮湿低洼主肺疾头痛，老父不利，官运受挫"},
            {"艮砂崩塌碎裂，如山崩地裂，主横灾祸事、人丁损伤。道路直冲则犯煞重，地基不稳则家宅危", "东北属土忌崩塌，艮砂缺损则少男遭殃。道路直冲主手足损伤，子孙不利，根基动摇"}
        };
        
        String[][] shuiShaPingInfo = {
            {"山势平缓起伏，如春木生发，主家道平顺、生活安稳。草木茂盛则生机足，砂水平和则无大患", "东方木气平和，青龙低伏则长子安稳。无大起大落，平淡中见真福，稳扎稳打可保长远"},
            {"明堂开阔平整，如暖阳普照，主身心安康、家庭和睦。阳光柔和则无病灾，朱雀适中则万事顺", "南方火气平和，朱雀敛翼则中女安顺。无大灾大难，平常日子也安康，知足常乐是正道"},
            {"白虎伏卧安分，如金藏于匣，主家宅安宁、无是无非。地势平坦则人心定，砂水平和则度日安", "西方金气平和，白虎驯服则少女无恙。不争不斗，安安稳稳过日子，和气生财福自至"},
            {"玄武平缓绵长，如细水长流，主根基稳健、福寿安康。靠山适中则无大忧，聚气藏风则享太平", "北方水气平和，玄武藏头则晚景安稳。不疾不徐，平平安安度一生，细水长流福泽远"},
            {"巽位平缓舒展，如微风拂面，主文运平稳、家道日兴。无冲无煞则运势顺，通风明亮则福气来", "东南木气平和，巽位适中则长女安稳。平顺发展，积少成多终有成，稳扎稳打渐兴旺"},
            {"坤砂平缓圆润，如厚土载物，主福寿安康、家庭和睦。地势平坦则人心定，砂水调和则享天年", "西南土气平和，坤位安宁则老母康健。平稳度日，无病无灾即是福，家和万事兴"},
            {"乾位平正端方，如金钟稳悬，主贵人相助、事业平稳。不高不低则权势宜，山水平和则财运亨", "西北金气平和，乾位适中则老父安康。稳中有进，贵人相助事业顺，不骄不躁福禄长"},
            {"艮砂平缓厚实，如土生万物，主家宅稳固、子孙荣昌。山势平缓则无冲煞，根基扎实则家业兴", "东北土气平和，艮位安宁则少男健壮。稳步发展，根深叶茂子孙贤，厚积薄发终有成"}
        };
        
        String[][] layoutJiInfo = {
            {"宜开正门迎紫气，设书房助文昌，栽花草养木气。东方属木主生发，开门纳气则家业兴旺", "门朝东，日出之气入宅，主生机勃勃。书房设此方，读书事半功倍，学业事业皆有成"},
            {"宜开南门迎火气，设客厅接宾客，修明堂纳祥瑞。南方属火主礼仪，明堂开阔则人缘广纳", "门朝南，阳光充足人丁旺。客厅设此方，待客热情有礼，事业名声远播四方"},
            {"宜设厨房旺火气，建库房聚财物，修道路通财气。西方属金主收敛，布局得当则财源广进", "西方宜静不宜动，厨房设此以火炼金。库房设此方，财物积聚不散，财运稳定增长"},
            {"宜建水池蓄财气，设后花园养生机，修靠山固根基。北方属水主藏蓄，有水环绕则财气凝聚", "北方宜有靠，山水相依福寿长。水池设此方，财源滚滚而来，晚年安享清福"},
            {"宜开侧门纳秀气，设书房办公务，种花木养性情。东南属木主文昌，通风明亮则文运亨通", "巽位主文昌，开门纳秀气入宅。书房设此方，读书写作灵感佳，贵人相助事业顺"},
            {"宜设主卧养身心，建储藏室聚财物，修平台纳福气。西南属土主藏养，稳重厚实则家宅安宁", "坤位主母仪，设主卧则家庭和睦。储藏室设此方，财物丰厚有余，家人健康长寿"},
            {"宜设主位显尊贵，建书房增智慧，修高台望远景。西北属金主权威，布局高大则事业有成", "乾位主天贵，设主卧则主人尊贵。书房设此方，谋略深远智慧高，贵人扶持官运旺"},
            {"宜建大门固根基，设祠堂祭先祖，修厚墙保安宁。东北属土主稳固，根基扎实则家业长青", "艮位主子孙，设大门则根基稳固。祠堂设此方，先祖庇佑后人，子孙兴旺代代传"}
        };
        
        String[][] layoutXiongInfo = {
            {"忌开凶门纳煞气，忌建厕所污木气，忌堆杂物阻生机。东方属木贵畅达，闭塞不通则灾病生", "此方开门若逢凶煞，长子多灾。设厕所则污木气，主肝胆疾病，家运衰败是非多"},
            {"忌阴暗潮湿伤火气，忌建厨房火太旺，忌污水冲损丁财。南方属火贵明达，污浊阴暗则灾祸临", "此方阴暗主心病眼疾，中女不利。设厨房则火过旺，主口舌是非，家庭不宁"},
            {"忌高大建筑挡金气，忌建厕所污金气，忌喧噪声烦心神。西方属金贵宁静，喧嚣杂乱则争斗起", "此方位高物挡气，少女多灾。设厕所则金气污，主肺病呼吸疾，破财招灾"},
            {"忌开门泄气散财气，忌高楼挡靠山，忌低洼潮湿伤肾气。北方属水贵收藏，开门直泄则家业败", "此方开门则财气直泄，晚景凄凉。低洼潮湿主肾病耳疾，根基动摇家道衰"},
            {"忌堵塞压迫阻气机，忌堆杂物挡通路，忌污秽不堪招病灾。东南属木贵通达，闭塞不通则运不济", "巽位堵塞文运暗，学业事业受阻。污秽堆积主肝胆病，长女不利家运衰"},
            {"忌空旷无靠失根基，忌建厕所污土气，忌尖角冲射招是非。西南属土贵厚重，倾斜不稳则灾祸来", "坤位虚空老母病，家宅不安。设厕所则土气污，主脾胃疾病，财运破败"},
            {"忌低洼潮湿损金气，忌建厨房火克金，忌破碎不堪招灾祸。西北属金贵高洁，低洼破碎则贵人离", "乾位低洼老父病，官运受挫。设厨房则火炼金，主头痛肺疾，贵人远去事业衰"},
            {"忌道路直冲犯凶煞，忌建厕所污土气，忌崩塌破碎损人丁。东北属土贵稳固，动摇不稳则祸事临", "艮位冲射少男伤，子孙不利。崩塌破碎主手足疾，根基动摇家宅危"}
        };
        
        String[][] layoutPingInfo = {
            {"宜建杂物间储物，保持整洁有条理，适度通风养木气。布局平和无大碍，安安稳稳度日", "东方平位宜静不宜动，杂物间设此无妨。保持整洁则气顺，不贪多不求大，安稳度日即是福"},
            {"宜建储藏室储物，保持通风又干燥，适度采光养火气。布局适中无大过，平平安安度日", "南方平位宜守不宜攻，储藏室设此无妨。通风干燥则物存，知足常乐，平淡日子也安康"},
            {"宜建厨房或储物，保持干净又卫生，适度使用养金气。布局平和无争斗，安安稳稳度日", "西方平位宜和不宜争，厨房设此无妨。清洁卫生则气顺，和和气气，日子越过越好"},
            {"宜建花园养绿地，保持湿润有生机，适度运动养水气。布局平缓多安稳，健健康康度日", "北方平位宜缓不宜急，花园设此养身。适度运动气血活，细水长流，福寿绵长享太平"},
            {"宜设书房或办公，保持通风又明亮，适度学习养秀气。布局平和文星显，顺顺当当度日", "东南平位宜学不宜闲，书房设此长智慧。日有所学日有所进，稳步提升终有成"},
            {"宜设卧室或储藏，保持舒适又安稳，适度休息养土气。布局平缓多福寿，舒舒服服度日", "西南平位宜养不宜劳，卧室设此养身心。劳逸结合身体健，家和人兴福自到"},
            {"宜设书房或储物，保持整洁又有序，适度思考养金气。布局平和贵人助，稳稳当当度日", "西北平位宜思不宜躁，书房设此增智慧。深思熟虑行则必果，贵人相助事业顺"},
            {"宜建花园或绿地，保持生机又盎然，适度劳作养土气。布局平缓根基稳，踏踏实实度日", "东北平位宜实不宜虚，花园设此固根基。脚踏实地稳步前行，根深叶茂子孙贤"}
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
        sb.append("<font color='#8899AA'>山管人丁水管财，山环水抱富贵来。四兽守护八方安，砂水有情福泽长。</font><br/><br/>");

        // 四兽格局总览
        sb.append("<b>【四兽总览】</b><br/>");
        sb.append(getFourBeastsDetail(mountain, wuxing, false));
        sb.append("<br/>");

        // 水法详解
        sb.append("<b>【水法详解】</b><br/>");
        sb.append("水为财之源，山为丁之根。水来之方为天门，宜开敞；水去之方为地户，宜紧闭。<br/>");
        sb.append("<font color='#00CC00'>【吉水】</font>环抱水（水绕宅如带）、九曲水（水来弯曲有情）、朝海水（水聚明堂如海）<br/>");
        sb.append("<font color='#FF4444'>【凶水】</font>直冲水（水直冲宅）、反弓水（水背宅反向）、割脚水（水贴宅基过）<br/>");
        sb.append("　水口方位：").append(getShuikouDirection(mountain)).append("方为佳，宜有山峦关拦，紧锁则财气不散<br/>");
        sb.append("　来水方位：").append(getLaishuiDirection(mountain)).append("方为佳，宜开阔明朗，水源长远则财运绵长<br/>");

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
            "高耸秀丽、草木繁茂，主贵人提携、长子有为",
            "开阔明亮、水绕砂环，主名声远播、事业昌隆",
            "低伏驯服、地势平坦，主财源广进、家庭和睦",
            "厚实稳重、山峦重叠，主根基深厚、子孙繁盛"
        };
        String[] yangzhaiXiong = {
            "低陷破碎、草木枯槁，主小人暗算、长子多难",
            "阴暗闭塞、污水直冲，主口舌是非、事业受阻",
            "高大雄健、喧噪声烦，主争斗凶灾、少女遭殃",
            "空虚塌陷、水流直去，主根基动摇、晚景凄凉"
        };
        String[] yinzhaiJi = {
            "宜高耸秀丽、环抱有情，如侍卫护主，主出文贵之人，后代有官贵",
            "宜端正方圆、明塘开阔，如宾主相对，主名声远播，后代有名望",
            "宜低伏驯服、俯首向内，如仆人听命，主财富丰足，后代多财产",
            "宜厚重稳实、连绵不绝，如靠山稳固，主根基深厚，后代多福寿"
        };
        String[] yinzhaiXiong = {
            "忌高昂抬头、反背无情，主争斗官非，后代出逆子",
            "忌尖射冲穴、阴暗逼压，主口舌是非，后代多灾病",
            "忌雄健昂头、张口露齿，主凶灾横祸，后代多败财",
            "忌空缺塌陷、风吹气散，主根基动摇，后代多贫寒"
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
        sb.append("　坐山方（").append(mountainDir).append("）宜有高山厚土，玄武垂头则根基稳固，人丁兴旺<br/>");
        sb.append("　朝向方（").append(chaoXiangDir).append("）宜有明塘流水，朱雀翔舞则财源广进，事业有成<br/>");

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
            return "山命比和 — 山与人同气相应，根基深厚，稳扎稳打可成大事";
        } else if (relation.equals("【生我】")) {
            return "山生我命 — 坐山生助命主，贵人扶持，事业顺利有如神助";
        } else if (relation.equals("【我生】")) {
            return "我命生山 — 命主生扶坐山，付出较多，先苦后甜终有所成";
        } else if (relation.equals("【克我】")) {
            return "山克我命 — 坐山克制命主，压力较大，需化煞解厄方保平安";
        } else {
            return "我命克山 — 命主克制坐山，驾驭有方，但须防物极必反";
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

        sb.append("<b>寻龙点穴，阴宅选址。</b>龙穴砂水向，五者皆为吉，子孙后代昌。<br/>");
        sb.append("<font color='#8899AA'>阴宅风水重在藏风聚气，山环水抱为上。先人安则后代宁，地灵则人杰。</font><br/><br/>");

        // 龙穴砂水向 五要素
        sb.append("<b>【地理五诀】</b><br/>");
        sb.append(getDiLiWuJue(mountain, chaoXiang, wuxing));
        sb.append("<br/>");

        // 寻龙要点（扩充）
        sb.append("<b>【寻龙点穴】</b><br/>");
        sb.append("<font color='#00CC00'>【龙】</font>龙要真：山脉起伏有气势，有来有去有祖宗，起伏曲折为活龙<br/>");
        sb.append("<font color='#00CC00'>【龙】</font>龙要活：山势蜿蜒如蛇行，生动活泼非僵硬，草木繁茂是真龙<br/>");
        sb.append("<font color='#00CC00'>【龙】</font>龙要旺：山形饱满土质润，草木青葱水清澈，生气旺盛福泽长<br/>");
        sb.append("<font color='#00CC00'>【龙】</font>龙要止：山到尽头有结作，有水环绕砂护卫，止息之处是真穴<br/>");
        sb.append("<font color='#FF4444'>【忌】</font>龙怕断：山脉中断气不连，如人伤筋又动骨，后代贫病灾祸连<br/>");
        sb.append("<font color='#FF4444'>【忌】</font>龙怕硬：山形僵硬无曲折，如僵尸卧无生气，人丁稀少家业败<br/><br/>");

        // 点穴要领（扩充）
        sb.append("<b>【点穴秘法】</b><br/>");
        sb.append("<font color='#00CC00'>【穴】</font>穴要的：取穴如针灸，差之毫厘谬千里，藏风聚气是真的<br/>");
        sb.append("<font color='#00CC00'>【穴】</font>穴要暖：土质温润色红黄，不燥不湿四季温，暖地生人多福寿<br/>");
        sb.append("<font color='#00CC00'>【穴】</font>穴要稳：背靠主山左右护，前有案山远朝迎，稳如磐石根基固<br/>");
        sb.append("<font color='#00CC00'>【穴】</font>穴要净：周围清净无恶石，不冲不射不反弓，清净之地福绵长<br/>");
        sb.append("<font color='#FF4444'>【忌】</font>穴怕风：八方风吹气飘散，如人露宿受风寒，后代贫寒人丁稀<br/>");
        sb.append("<font color='#FF4444'>【忌】</font>穴怕水：水冲穴前气亦散，如人溺水难存活，败财损丁祸连绵<br/><br/>");

        // 砂水环抱（扩充，随坐山变化）
        sb.append("<b>【砂水环抱】</b><br/>");
        sb.append(getFourBeastsDetail(mountain, wuxing, true));
        sb.append("<br/>");

        // 水口详解
        sb.append("<b>【水口关拦】</b><br/>");
        sb.append("水口者，众水汇聚出口处，为财库之门户。<br/>");
        sb.append("<font color='#00CC00'>【吉】</font>水口紧锁，有关有拦，两山对峙如门卫，则财气凝聚不散<br/>");
        sb.append("<font color='#FF4444'>【凶】</font>水口直泄，一泻千里，无遮无挡如旷野，则财去人散家道败<br/>");
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

        sb.append("　<font color='#90EE90'><b>龙</b></font>：山脉为龙，主后人人丁贵贱。坐山").append(mountainDir).append("宜有来龙，起伏曲折为贵<br/>");
        sb.append("　<font color='#FFD700'><b>穴</b></font>：结穴之处，主葬事的当否。藏风聚气为上，山水交会处是真穴<br/>");
        sb.append("　<font color='#FFA500'><b>砂</b></font>：周围山为砂，主子孙贤愚。青龙白虎朱雀玄武，四兽齐备为吉<br/>");
        sb.append("　<font color='#87CEEB'><b>水</b></font>：水流为水，主家族财运。朝").append(chaoXiangDir).append("有水环抱，九曲入明堂为上<br/>");
        sb.append("　<font color='#FF6B6B'><b>向</b></font>：坐向为向，主气运兴衰。").append(mountain).append("山").append(chaoXiang).append("向，向法合度则福泽长<br/>");

        return sb.toString();
    }

    private String getMingTangDetail(String mountain, String chaoXiang) {
        StringBuilder sb = new StringBuilder();
        sb.append("明堂者，穴前水聚之处，如官吏之朝堂，故名明堂。<br/>");
        sb.append("　内明堂：近穴之小水聚处，宜方圆平整，主家道初兴<br/>");
        sb.append("　中明堂：龙虎之间的开阔地，宜宽畅明亮，主中年发福<br/>");
        sb.append("　外明堂：远朝与案山之间，宜广阔深邃，主世代荣昌<br/>");
        sb.append("　<font color='#00CC00'>【吉】</font>如掌心凹陷，四周高中间低，水聚天心，富贵双全<br/>");
        sb.append("　<font color='#FF4444'>【凶】</font>如簸箕张开，水去直泄无遮拦，财散人离，家道败落<br/>");
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