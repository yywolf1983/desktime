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
            + "• <font color='#3FA34D'><b>生气方</b></font>（最吉）：主丁财两旺，宜主卧、大门；<br/>"
            + "• <font color='#3FA34D'><b>天医方</b></font>（次吉）：主祛病延年，宜体弱居住；<br/>"
            + "• <font color='#3FA34D'><b>延年方</b></font>（次吉）：主婚姻美满、健康长寿，宜夫妻房；<br/>"
            + "• <font color='#E6C46A'><b>伏位方</b></font>（平）：主平稳守成，宜静养读书；<br/>"
            + "• <font color='#E0593B'><b>祸害方</b></font>（凶）：主口舌是非、胃病，宜作厕所；<br/>"
            + "• <font color='#E0593B'><b>六煞方</b></font>（凶）：主感情纠纷、失眠，宜作储物间；<br/>"
            + "• <font color='#E0593B'><b>五鬼方</b></font>（凶）：主官非破财、火灾，宜作厨房；<br/>"
            + "• <font color='#E0593B'><b>绝命方</b></font>（大凶）：主伤残、绝嗣，宜作厕所仓库。<br/><br/>";

    // 九星吉凶口诀
    private static final String JIUXING_KOUJUE =
            "九星吉凶：<br/>"
            + "• <font color='#3FA34D'><b>一白</b></font>（水）→ 文昌官贵，利考试求职；<br/>"
            + "• <font color='#E0593B'><b>二黑</b></font>（土）→ 病符，主脾胃病灾；<br/>"
            + "• <font color='#E0593B'><b>三碧</b></font>（木）→ 是非，主口舌争斗；<br/>"
            + "• <font color='#3FA34D'><b>四绿</b></font>（木）→ 文曲，利读书创作；<br/>"
            + "• <font color='#E0593B'><b>五黄</b></font>（土）→ 大煞，主灾祸破财；<br/>"
            + "• <font color='#3FA34D'><b>六白</b></font>（金）→ 武曲，利升职；<br/>"
            + "• <font color='#E0593B'><b>七赤</b></font>（金）→ 破军，主贼盗官非；<br/>"
            + "• <font color='#3FA34D'><b>八白</b></font>（土）→ 旺财，利置业投资；<br/>"
            + "• <font color='#3FA34D'><b>九紫</b></font>（火）→ 喜庆，利婚嫁添丁。<br/><br/>";

    // 八门性格与用途口诀
    private static final String BAMEN_KOUJUE =
            "八门吉凶与用途：<br/>"
            + "• <font color='#3FA34D'><b>开门</b></font>（吉）：通达顺利，宜开业、求职、谈判；<br/>"
            + "• <font color='#3FA34D'><b>休门</b></font>（吉）：百事皆宜，宜婚嫁、求财、休养；<br/>"
            + "• <font color='#3FA34D'><b>生门</b></font>（吉）：财源广进，宜求财、交易、建造；<br/>"
            + "• <font color='#E6C46A'><b>杜门</b></font>（平）：闭塞隐藏，宜躲避、保密；<br/>"
            + "• <font color='#E6C46A'><b>景门</b></font>（平）：文书吉庆，宜考试、诉讼；<br/>"
            + "• <font color='#E0593B'><b>伤门</b></font>（凶）：损伤争斗，宜追债、擒贼；<br/>"
            + "• <font color='#E0593B'><b>死门</b></font>（凶）：衰败丧事，宜安葬；<br/>"
            + "• <font color='#E0593B'><b>惊门</b></font>（凶）：惊恐怪异，宜诉讼、捕盗。<br/><br/>";

    private LuoPanView luoPanView;
    private String currentMonthWuxing = "木";

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
        currentMonthWuxing = getMonthWuxingByJieqi(JieqiData.getCurrentJieqi(java.util.Calendar.getInstance()));
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
            {"青龙高耸，主文昌贵人，峰秀出文人", "东方木宜挺拔，长子出息"},
            {"朱雀翔舞，主名声事业，明堂纳祥", "南方火宜秀丽，中女有为"},
            {"白虎驯服，主财源家安，山柔聚财", "西方金宜低伏，少女有福"},
            {"玄武厚重，主根基子孙，山环水绕", "北方水宜厚实，晚运亨通"},
            {"巽峰高耸，主文星贵人，溪婉财广", "东南木宜秀丽，长女聪慧"},
            {"坤砂端方，主家和人丁，山圆土厚", "西南土宜平坦，老母康健"},
            {"乾峰巍峨，主权威事业，山环水抱", "西北金宜雄伟，老父康健"},
            {"艮砂敦厚，主家安子孙，靠山稳固", "东北土宜厚实，少男健壮"}
        };
        
        String[][] shuiShaXiongInfo = {
            {"青龙破碎，主官非家败，尖射损丁", "东方忌破碎，尖角主官非"},
            {"朱雀阴暗，主疾病横祸，尖射血光", "南方忌冲射，阴暗主心病"},
            {"白虎昂头，主争斗凶灾，杀气重", "西方忌高昂，直冲损丁财"},
            {"玄武空缺，主破财根摇，水泄家败", "北方忌空缺，低洼主肾病"},
            {"巽位堵塞，主困顿病灾，污秽招疫", "东南忌堵塞，文运暗"},
            {"坤砂陡峭，主灾祸母殃，势斜根危", "西南忌陡峭，主脾胃病"},
            {"乾位低洼，主财散贵离，破碎招灾", "西北忌低洼，主肺疾"},
            {"艮砂崩塌，主横祸丁伤，直冲犯煞", "东北忌崩塌，主手足疾"}
        };
        
        String[][] shuiShaPingInfo = {
            {"山势平缓，主家道平顺，草木茂盛", "东方木气平和，安稳度日"},
            {"明堂开阔，主身心安康，阳光柔和无灾", "南方火气平和，知足常乐"},
            {"白虎伏卧，主家宅安宁，地势平坦", "西方金气平和，和气生财"},
            {"玄武平缓，主根基稳健，靠山适中", "北方水气平和，细水长流"},
            {"巽位舒展，主文运平稳，通风明亮", "东南木气平和，稳步兴旺"},
            {"坤砂圆润，主福寿安康，地势平坦", "西南土气平和，家和万事兴"},
            {"乾位平正，主贵人相助，山水平财亨", "西北金气平和，稳中有进"},
            {"艮砂厚实，主家宅稳固，山势平缓", "东北土气平和，根深叶茂"}
        };
        
        String[][] layoutJiInfo = {
            {"宜开正门、书房、花木", "朝东利学业事业"},
            {"宜开南门、客厅、明堂", "朝南利名声远播"},
            {"宜设厨仓、修路", "西宜静利财运"},
            {"宜水池、花园、靠山", "北有靠利晚年"},
            {"宜侧门、书房、花木", "巽文昌利贵人"},
            {"宜主卧、储藏、平台", "坤主母利长寿"},
            {"宜主位、书房、高台", "乾天贵利官运"},
            {"宜大门、祠堂、厚墙", "艮子孙利兴旺"}
        };
        
        String[][] layoutXiongInfo = {
            {"忌凶门、厕、杂物", "东开门长子灾"},
            {"忌阴暗、厨、污水", "南阴暗主心病"},
            {"忌高物挡、厕、喧声", "西挡气主肺病"},
            {"忌开门泄、楼挡、低洼", "北开门财气泄"},
            {"忌堵、杂物、污秽", "巽堵塞文运暗"},
            {"忌空旷无靠、厕、尖角", "坤虚空老母病"},
            {"忌低洼潮、厨、破碎", "乾低洼老父病"},
            {"忌道冲、厕、崩塌", "艮冲射少男伤"}
        };
        
        String[][] layoutPingInfo = {
            {"宜建杂物间，整洁通风", "东方平位宜静"},
            {"宜建储藏室，通风干燥", "南方平位宜守"},
            {"宜建厨房或储物，干净卫生", "西方平位宜和"},
            {"宜建花园绿地，湿润有生机", "北方平位宜缓"},
            {"宜设书房办公，通风明亮", "东南平位宜学"},
            {"宜设卧室储藏，舒适安稳", "西南平位宜养"},
            {"宜设书房储物，整洁有序", "西北平位宜思"},
            {"宜建花园绿地，生机盎然", "东北平位宜实"}
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
            String luckColor = jiXiong.equals("吉") ? "#3FA34D" : jiXiong.equals("凶") ? "#E0593B" : "#E6C46A";

            String zhaoxiangDetail = getZhaoxiangDetail(bfBagua, bfWuxing, beast, jiXiong);
            String zuoXiangDetail = getZuoXiangDetail(direction, mountain, chaoXiang, relation);

            if (zhaoxiangViews[i] != null) {
                String zhaoxiangHtml = "<b>格局：</b>" + bfBagua + "卦·" + bfWuxing + "·" + beast +
                    " " + relation + wuxingRelation +
                    "<font color='" + luckColor + "'>【" + jiXiong + "】</font>" +
                    "<br/><font color='#7C8C9C'>　" + zhaoxiangDetail + "</font>" +
                    "<br/><b>坐向：</b>" + zuoXiangDetail;
                zhaoxiangViews[i].setText(Html.fromHtml(zhaoxiangHtml));
            }
            if (shuishaViews[i] != null) {
                String[] currentShuiSha = jiXiong.equals("吉") ? shuiShaJiInfo[i] :
                                         jiXiong.equals("凶") ? shuiShaXiongInfo[i] : shuiShaPingInfo[i];
                String shuishaHtml = "<b>砂水：</b>" + currentShuiSha[0] +
                    "<br/><font color='#7C8C9C'>　" + currentShuiSha[1] + "</font>";
                shuishaViews[i].setText(Html.fromHtml(shuishaHtml));
            }
            if (layoutViews[i] != null) {
                String[] currentLayout = jiXiong.equals("吉") ? layoutJiInfo[i] :
                                         jiXiong.equals("凶") ? layoutXiongInfo[i] : layoutPingInfo[i];
                String layoutHtml = "<b>布局：</b>" + currentLayout[0] +
                    "<br/><font color='#7C8C9C'>　" + currentLayout[1] + "</font>";
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
                return "震卦·木·青龙，主长男，主动，利进取";
            case "离":
                return "离卦·火·朱雀，主中女，主明，利文采";
            case "兑":
                return "兑卦·金·白虎，主少女，主悦，利辞令";
            case "坎":
                return "坎卦·水·玄武，主中男，主险，利智虑";
            case "巽":
                return "巽卦·木·青龙辅，主长女，主入，利学业贵人";
            case "坤":
                return "坤卦·土·白虎辅，主老母，主顺，利含弘";
            case "乾":
                return "乾卦·金·玄武辅，主老父，主健，利威权";
            case "艮":
                return "艮卦·土·青龙辅，主少男，主止，利安固";
            default:
                return "";
        }
    }

    private String getZuoXiangDetail(String direction, String mountain, String chaoXiang, String relation) {
        String mountainDirection = getMountainDirection(mountain);
        String chaoXiangDirection = getChaoXiangDirection(chaoXiang);
        if (mountainDirection.equals(direction)) {
            return "坐山" + mountain + "：宜有倚托，主根基固、得贵人";
        } else if (chaoXiangDirection.equals(direction)) {
            return "朝向" + chaoXiang + "：宜开阔临水，主财源茂、前程朗";
        } else if (relation.equals("为青龙方")) {
            return "青龙位：宜轩昂，主长男兴、贵人来";
        } else if (relation.equals("为白虎方")) {
            return "白虎位：宜低伏，主少女安、财运稳";
        }
        return "辅弼位：宜平正，主佐助有方、福禄绵长";
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
        // 以坐山五行与当前月令（节气）五行生克判定旺衰：得令生扶为吉，失令受克为凶，其余为平
        String rel = getWuxingRelation(currentMonthWuxing, wuxing);
        if (rel.equals("【比和】") || rel.equals("【生我】")) return "吉";
        if (rel.equals("【克我】")) return "凶";
        return "平";
    }

    // 节气名 → 节月地支五行（用于罗盘坐山旺衰）
    private static String getMonthWuxingByJieqi(String jieqi) {
        switch (jieqi) {
            case "立春": case "雨水": return "木";
            case "惊蛰": case "春分": return "木";
            case "清明": case "谷雨": return "土";
            case "立夏": case "小满": return "火";
            case "芒种": case "夏至": return "火";
            case "小暑": case "大暑": return "土";
            case "立秋": case "处暑": return "金";
            case "白露": case "秋分": return "金";
            case "寒露": case "霜降": return "土";
            case "立冬": case "小雪": return "水";
            case "大雪": case "冬至": return "水";
            case "小寒": case "大寒": return "土";
            default: return "木";
        }
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
            "平守敦安，宜静不宜动",
            "丁财俱旺，生气方隆",
            "康宁攸永，琴瑟和鸣",
            "疴疾得瘳，贵人相携",
            "口舌纷然，家室不宁",
            "桃夭致讼，情好见困",
            "官灾频仍，破财伤身",
            "至凶之方，百事俱忌"
        };
        
        StringBuilder sb = new StringBuilder();
        sb.append("<b>八宅（").append(bagua).append("宅）：</b><br/>");

        if (baguaIndex >= 0 && baguaIndex <= 7) {
            int[] flying = bazhaiFlying[baguaIndex];
            for (int i = 0; i < 8; i++) {
                int starIndex = flying[i];
                int dirIndex = i;
                if (dirIndex >= 4) dirIndex++;

                String luckColor = bazhaiLuck[starIndex].equals("吉") ? "#3FA34D" :
                                   bazhaiLuck[starIndex].equals("凶") ? "#E0593B" : "#E6C46A";
                sb.append(bazhaiNames[starIndex]);
                sb.append("<font color='").append(luckColor).append("'>【").append(bazhaiLuck[starIndex]).append("】</font>");
                sb.append(directions[dirIndex]).append("·").append(baguaList[dirIndex]).append("卦·").append(wuxingList[dirIndex]);
                sb.append(" ").append(bazhaiMeaning[starIndex]).append("<br/>");
            }
        }

        // 原因解读
        sb.append("<br/><font color='#D6BE86'><b>解读：</b></font><br/>");
        sb.append(getBazhaiPlainExplanation(bagua));
        bazhaiDesc.setText(Html.fromHtml(sb.toString()));
    }

    /**
     * 八宅风水解读：说明卦气归属与吉凶方位之理
     */
    private String getBazhaiPlainExplanation(String bagua) {
        // 判断东四宅/西四宅
        boolean isEast = bagua.equals("坎") || bagua.equals("离") || bagua.equals("震") || bagua.equals("巽");
        String group = isEast ? "东四宅" : "西四宅";
        String groupGua = isEast ? "坎离震巽" : "乾坤艮兑";

        StringBuilder sb = new StringBuilder();
        sb.append("「").append(bagua).append("」宅属<b>").append(group).append("</b>（")
          .append(groupGua).append("），同组卦气相通、游年顺飞而定八方吉凶。<br/><br/>");
        sb.append(BAZHAI_KOUJUE);
        sb.append("<br/><font color='#D6BE86'>要诀：</font>吉方布寝堂门户，凶方布溷湢储畜，趋吉而避凶。");

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
            "官贵显达，文昌利学",
            "病符缠身，摄生不宜",
            "口舌纷纭，争竞不和",
            "文昌启慧，学业有成",
            "至凶之方，灾眚频仍",
            "财权并至，贵人相携",
            "盗贼破财，是非口角",
            "财源畅茂，田宅丰盈",
            "喜气骈集，婚好谐睦"
        };
        
        int[] luoShuOrder = {0, 7, 3, 4, 5, 2, 6, 1, 8};
        StringBuilder sb = new StringBuilder();
        sb.append("<b>九星飞宫（").append(bagua).append("卦入中·").append(jiuxingNames[baguaNumber - 1]).append("星）：</b><br/>");
        
        for (int i = 0; i < 9; i++) {
            int starNum = (baguaNumber + luoShuOrder[i]) % 9;
            if (starNum == 0) starNum = 9;
            int starIndex = starNum - 1;
            
            if (i != 4) {
                String luckColor = jiuxingLuck[starIndex].equals("吉") ? "#3FA34D" : 
                                   jiuxingLuck[starIndex].equals("凶") ? "#E0593B" : "#E6C46A";
                sb.append(jiuxingNames[starIndex]);
                sb.append("<font color='").append(luckColor).append("'>【").append(jiuxingLuck[starIndex]).append("】</font>");
                sb.append(directions[i]).append("·").append(baguaList[i]).append("卦·").append(jiuxingWuxing[starIndex]);
                sb.append(" ").append(jiuxingMeaning[starIndex]).append("<br/>");
            }
        }

        // 原因解读
        sb.append("<br/><font color='#D6BE86'><b>解读：</b></font><br/>");
        sb.append(getJiuxingPlainExplanation(bagua, baguaNumber));
        jiuxingDesc.setText(Html.fromHtml(sb.toString()));
    }

    /**
     * 九星飞宫解读：说明当值星气场基调与吉凶方之理
     */
    private String getJiuxingPlainExplanation(String bagua, int baguaNumber) {
        String[] starNames = {"一白贪狼", "二黑巨门", "三碧禄存", "四绿文曲", "五黄廉贞",
                              "六白武曲", "七赤破军", "八白左辅", "九紫右弼"};
        String inCenterStar = starNames[baguaNumber - 1];

        StringBuilder sb = new StringBuilder();
        sb.append("坐山<b>").append(bagua).append("</b>卦入中，当值星为「<b>")
          .append(inCenterStar).append("</b>」，定全局气场基调：<br/>");

        // 根据入中星给出本局基调解释
        switch (baguaNumber) {
            case 1:
                sb.append("一白水入中，智用流通，利修学远行，宜进修谋画。");
                break;
            case 2:
                sb.append("二黑土入中，病符沉滞易疴，护脾胃，宜静忌动土。");
                break;
            case 3:
                sb.append("三碧木入中，主争竞口舌，防小人，戒怒，宜植青木。");
                break;
            case 4:
                sb.append("四绿木入中，主文昌，利读习缔约，宜学宜谈。");
                break;
            case 5:
                sb.append("五黄土入中，至凶，灾病破财，宜静忌动土，铜器化之。");
                break;
            case 6:
                sb.append("六白金入中，主权势，利迁擢求职，得贵助，宜进取。");
                break;
            case 7:
                sb.append("七赤金入中，主肃杀，易破财招盗，防官非，宜守忌投机。");
                break;
            case 8:
                sb.append("八白土入中，旺财之曜，利置业求财，宜积累。");
                break;
            case 9:
                sb.append("九紫火入中，主喜庆，利婚嫁添丁，宜嘉礼图合作。");
                break;
        }
        sb.append("<br/><br/>");

        sb.append(JIUXING_KOUJUE);

        sb.append("<br/><font color='#D6BE86'>要诀：</font>吉星方宜活动办公，凶星方以金属绿植化解，星随宫转、吉凶有方。");

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
            
            String luckColor = bamenLuck[i].equals("吉") ? "#3FA34D" : 
                               bamenLuck[i].equals("凶") ? "#E0593B" : "#E6C46A";
            sb.append(bamenNames[i]);
            sb.append("<font color='").append(luckColor).append("'>【").append(bamenLuck[i]).append("】</font>");
            sb.append(directions[adjustedPos]).append("·").append(baguaList[adjustedPos]).append("卦");
            sb.append(" ").append(bamenMeaning[i]).append("<br/>");
        }

        // 原因解读
        sb.append("<br/><font color='#D6BE86'><b>解读：</b></font><br/>");
        sb.append(getBamenPlainExplanation(bagua, baguaNumber));
        bamenDesc.setText(Html.fromHtml(sb.toString()));
    }

    /**
     * 八门遁法解读：说明门气吉凶与行事择门之理
     */
    private String getBamenPlainExplanation(String bagua, int baguaNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("坐山<b>").append(bagua).append("</b>卦起休门顺排八方，门各有气：<br/><br/>");
        sb.append(BAMEN_KOUJUE);

        // 根据当前坐山卦，给出实战建议
        sb.append("<br/><font color='#D6BE86'>应用：</font><br/>");
        sb.append(getBamenLifeAdvice(baguaNumber));
        sb.append("<br/><br/><font color='#D6BE86'>要诀：</font>办事择吉门（休生开）而行，凶门（死惊伤）波折，择门定吉凶。");

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
        sb.append("<font color='#7C8C9C'>山管人丁水管财，山环水抱富贵来</font><br/><br/>");

        // 四兽格局总览
        sb.append("<b>【四兽总览】</b><br/>");
        sb.append(getFourBeastsDetail(mountain, wuxing, false));
        sb.append("<br/>");

        // 水法详解
        sb.append("<b>【水法】</b><br/>");
        sb.append("<font color='#3FA34D'>吉水</font>：环抱·九曲·朝海　<font color='#E0593B'>凶水</font>：直冲·反弓·割脚<br/>");
        sb.append("水口：").append(getShuikouDirection(mountain)).append("方宜关拦　来水：").append(getLaishuiDirection(mountain)).append("方宜开阔<br/>");

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
            "高耸端丽，主贵人提携",
            "开阔朗明，主声名远播",
            "低伏驯顺，主财源广进",
            "厚实凝重，主根基深厚"
        };
        String[] yangzhaiXiong = {
            "低陷破碎，主小人暗算",
            "阴暗壅塞，主口舌是非",
            "高亢雄肆，主争斗凶灾",
            "空虚坍陷，主根基动摇"
        };
        String[] yinzhaiJi = {
            "宜高耸环抱，主出文贵",
            "宜端正方阔，主有声望",
            "宜低伏驯顺，主多财富",
            "宜厚重稳固，主多福寿"
        };
        String[] yinzhaiXiong = {
            "忌高昂反背，主争斗官非",
            "忌尖射逼压，主口舌是非",
            "忌雄健昂头，主凶灾败财",
            "忌空缺坍陷，主根基动摇"
        };

        for (int i = 0; i < 4; i++) {
            String relation = getWuxingRelation(wuxing, beastWuxings[i]);
            String jiXiong = getLuckFromWuxingRelation(relation);
            String color = jiXiong.equals("吉") ? "#3FA34D" : jiXiong.equals("凶") ? "#E0593B" : "#E6C46A";
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

        sb.append("坐").append(mountain).append("向").append(chaoXiang).append("，背山面水乃上格。<br/>");
        sb.append("　坐山宜有崇冈，玄武垂头则人丁旺<br/>");
        sb.append("　朝向宜有流水，朱雀翔舞则财源广<br/>");

        // 判断山水格局类型
        String pattern = getShanshuiPattern(mountain, wuxing);
        sb.append("　格局判定：<font color='#E6C46A'><b>").append(pattern).append("</b></font><br/>");

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
            return "山生我命，得贵人扶";
        } else if (relation.equals("【我生】")) {
            return "我命生山，先劳后逸";
        } else if (relation.equals("【克我】")) {
            return "山克我命，所承压重";
        } else {
            return "我命克山，御之有方";
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
        sb.append("<font color='#7C8C9C'>阴宅重藏风聚气，先人安则后代宁</font><br/><br/>");

        // 龙穴砂水向 五要素
        sb.append("<b>【地理五诀】</b><br/>");
        sb.append(getDiLiWuJue(mountain, chaoXiang, wuxing));
        sb.append("<br/>");

        // 寻龙要点（扩充）
        sb.append("<b>【寻龙点穴】</b><br/>");
        sb.append("<font color='#3FA34D'>【龙】</font>龙要真：起伏有气势，有祖宗<br/>");
        sb.append("<font color='#3FA34D'>【龙】</font>龙要活：蜿蜒有生气，草木茂<br/>");
        sb.append("<font color='#3FA34D'>【龙】</font>龙要旺：山形饱满，生气旺<br/>");
        sb.append("<font color='#3FA34D'>【龙】</font>龙要止：到头有结作，有水护<br/>");
        sb.append("<font color='#E0593B'>【忌】</font>龙怕断：气不连，后代贫病<br/>");
        sb.append("<font color='#E0593B'>【忌】</font>龙怕硬：僵硬无生气，人丁稀<br/><br/>");

        // 点穴要领（扩充）
        sb.append("<b>【点穴秘法】</b><br/>");
        sb.append("<font color='#3FA34D'>【穴】</font>穴要的：藏风聚气，差之千里<br/>");
        sb.append("<font color='#3FA34D'>【穴】</font>穴要暖：土质温润，人多福寿<br/>");
        sb.append("<font color='#3FA34D'>【穴】</font>穴要稳：背靠主山，根基固<br/>");
        sb.append("<font color='#3FA34D'>【穴】</font>穴要净：清净无恶石，福绵长<br/>");
        sb.append("<font color='#E0593B'>【忌】</font>穴怕风：气散，后代贫寒<br/>");
        sb.append("<font color='#E0593B'>【忌】</font>穴怕水：气散，败财损丁<br/><br/>");

        // 砂水环抱（扩充，随坐山变化）
        sb.append("<b>【砂水环抱】</b><br/>");
        sb.append(getFourBeastsDetail(mountain, wuxing, true));
        sb.append("<br/>");

        // 水口详解
        sb.append("<b>【水口关拦】</b><br/>");
        sb.append("水口者，财库之门户也。<br/>");
        sb.append("<font color='#3FA34D'>【吉】</font>紧锁有关拦，财气不散<br/>");
        sb.append("<font color='#E0593B'>【凶】</font>直泄无遮拦，财去人散<br/>");
        sb.append("　水口方位：").append(getShuikouDirection(mountain)).append("方，宜有山峦洲渚以关拦<br/>");
        sb.append("　天门开：来水之方宜开阔，水有源则财用不竭<br/>");
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

        sb.append("　<font color='#3FA34D'><b>龙</b></font>：主贵贱，宜有来龙<br/>");
        sb.append("　<font color='#E6C46A'><b>穴</b></font>：主吉凶，藏风聚气<br/>");
        sb.append("　<font color='#F3BA66'><b>砂</b></font>：主贤愚，四兽齐备<br/>");
        sb.append("　<font color='#3E87C2'><b>水</b></font>：主财运，环抱为佳<br/>");
        sb.append("　<font color='#E0593B'><b>向</b></font>：主兴衰，向法合度<br/>");

        return sb.toString();
    }

    private String getMingTangDetail(String mountain, String chaoXiang) {
        StringBuilder sb = new StringBuilder();
        sb.append("明堂：穴前水聚之所。<br/>");
        sb.append("　内明堂：宜平正，主初兴<br/>");
        sb.append("　中明堂：宜朗明，主发福<br/>");
        sb.append("　外明堂：宜广阔，主荣昌<br/>");
        sb.append("　<font color='#3FA34D'>【吉】</font>四围高而中洼，水聚天心，富贵兼全<br/>");
        sb.append("　<font color='#E0593B'>【凶】</font>水去直泄无遮拦，财散人离，家道中落<br/>");
        sb.append("　").append(chaoXiang).append("向之明堂：宜开阔平正有朝山，案山近则速发，朝山远则福长");

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
            return "<font color='#E6C46A'><b>山命同气</b></font> — 坐山与命比和，阴阳调和，上吉之格";
        } else if (relation.equals("【生我】")) {
            return "<font color='#3FA34D'><b>山生命主</b></font> — 坐山生我，先灵安而后人福，大吉，子孙蕃昌";
        } else if (relation.equals("【我生】")) {
            return "<font color='#E6C46A'><b>命主生山</b></font> — 我生山，泄秀，后代多出文人";
        } else if (relation.equals("【克我】")) {
            return "<font color='#E0593B'><b>山克命主</b></font> — 山克我，宜择吉而葬以化其凶";
        } else {
            return "<font color='#E0593B'><b>命主克山</b></font> — 我克山，占之不吉，宜别择吉壤";
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
        // 吉利方位：坐山五行所「喜」（生我、比和）之方位
        String[] dirNames = {"东方", "东南方", "南方", "西南方", "西方", "西北方", "北方", "东北方"};
        String[] dirWuxing = {"木", "木", "火", "土", "金", "金", "水", "土"};
        StringBuilder lucky = new StringBuilder();
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < dirNames.length; i++) {
                String rel = getWuxingRelation(dirWuxing[i], wuxing);
                if (pass == 0 && rel.equals("【生我】")) {
                    if (lucky.length() > 0) lucky.append("、");
                    lucky.append(dirNames[i]).append("(生方)");
                } else if (pass == 1 && rel.equals("【比和】")) {
                    if (lucky.length() > 0) lucky.append("、");
                    lucky.append(dirNames[i]).append("(旺方)");
                }
            }
        }
        String luckyDir = lucky.length() > 0 ? lucky.toString() : "无特别方位";
        return "坐" + mountain + "向" + chaoXiang + "，五行属" + wuxing + "，" + jixiong + "。吉利方位：" + luckyDir;
    }
}