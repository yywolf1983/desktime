package com.example.timedisplay;

import android.content.Intent;
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

    private LuoPanView luoPanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            {"高耸苍翠利文昌，山峰秀丽贵人来", "青龙昂首子孙旺，树木葱郁家道兴"},
            {"朱雀展翅名声扬，山水秀丽事业昌", "明堂开阔纳祥瑞，阳光充足福泽长"},
            {"白虎驯服财源广，山势柔顺家宅安", "道路平坦无冲煞，水流环抱聚财气"},
            {"玄武厚实根基稳，靠山坚固子孙兴", "山环水绕藏风聚，土厚水深福寿长"},
            {"巽峰高耸文星显，青龙得位贵人生", "溪流婉转财源进，花木繁茂喜气盈"},
            {"坤砂稳重老母安，地势平坦家宅和", "土厚山圆多福寿，明堂宽广纳吉祥"},
            {"乾峰高耸贵人助，金气旺盛事业成", "山环水抱财源聚，地势高昂权势显"},
            {"艮砂敦厚少男健，山环水绕根基牢", "土质温润多福寿，靠山稳固子孙强"}
        };
        
        String[][] shuiShaXiongInfo = {
            {"尖角冲射主官非，破碎低矮家道衰", "青龙低头灾祸至，树木凋零是非多"},
            {"阴暗潮湿病缠身，尖射冲煞招横祸", "朱雀开口口舌生，污水直冲损丁财"},
            {"白虎抬头主争斗，高大雄健招是非", "道路直冲家宅破，喧噪声烦心神乱"},
            {"开门纳气主破财，空虚塌陷根基摇", "玄武空缺无靠山，水流直泄财气散"},
            {"堵塞压迫主困顿，污秽堆积招病灾", "巽位闭塞文星暗，通风不畅运势衰"},
            {"陡峭险峻主不安，空旷无靠根基危", "坤砂破碎老母灾，地势倾斜家宅乱"},
            {"低洼潮湿财气散，破碎不堪招灾祸", "乾位空缺贵人失，金气不足事业衰"},
            {"崩塌破碎主灾祸，道路直冲损人丁", "艮砂缺损少男伤，地基不稳家宅危"}
        };
        
        String[][] shuiShaPingInfo = {
            {"山势平和多安稳，草木茂盛家道兴", "砂水适中无大患，平平淡淡是真福"},
            {"明堂平缓纳福气，阳光柔和多安康", "朱雀适中无过患，平平常常万事顺"},
            {"白虎平和无争斗，地势平坦家宅宁", "砂水平和无大碍，安安稳稳度时光"},
            {"玄武平缓根基稳，靠山适中多福寿", "山水平和聚生气，无风无险享太平"},
            {"巽位平和文星显，砂水适中家道兴", "山势平缓无冲煞，平平安安福自来"},
            {"坤砂平和老母安，地势平坦多福寿", "砂水平和无大碍，安安稳稳享天年"},
            {"乾位平和贵人助，金气适中事业稳", "山水平和聚财气，平平安安财运亨"},
            {"艮砂平和少男健，山势平缓根基牢", "砂水平和无大患，安安稳稳子孙昌"}
        };
        
        String[][] layoutJiInfo = {
            {"开门纳吉气，建书房文昌兴盛，种树绿化生机勃勃", "大门宜开此方，纳气旺丁旺财"},
            {"开门迎祥瑞，建客厅明堂开阔，种花植树喜气洋洋", "宜设大门此方，招财进宝纳福"},
            {"建厨房纳火气，储藏室聚财气，布局平和家宅安", "宜作厨灶此方，财源广进"},
            {"宜建水池蓄财，厚实稳重根基固，靠山安稳子孙兴", "宜作后花园，纳水聚财"},
            {"开门纳秀气，建书房办公利事业，绿化美化心境舒", "宜开侧门此方，贵人相助"},
            {"建卧室安宁，储藏室聚财，稳重厚实家宅和", "宜作主卧此方，福寿康宁"},
            {"建主卧贵人助，书房文昌显，高大雄伟气势宏", "宜作正门此方，权威显赫"},
            {"建大门靠山稳，敦厚稳重根基固，纳气聚福家道兴", "宜开大门此方，基业长青"}
        };
        
        String[][] layoutXiongInfo = {
            {"忌开门纳凶气，忌建厕所污秽地，忌阴暗闭塞招病灾", "此方不宜开门，凶煞易入"},
            {"忌阴暗潮湿，忌建厨房火气盛，忌污水直冲损丁财", "此方不宜动土，灾祸易生"},
            {"忌高大建筑挡气场，忌建厕所污秽地，忌喧噪声烦", "此方不宜大兴土木"},
            {"忌开门纳凶气，忌建高楼挡靠山，忌低洼潮湿招病", "此方不宜开门，凶多吉少"},
            {"忌堵塞压迫，忌建杂物堆积地，忌污秽不堪招病灾", "此方宜保持通畅"},
            {"忌空旷无靠，忌建厕所污秽地，忌尖角冲射招是非", "此方宜保持稳固"},
            {"忌低洼潮湿，忌建厨房火气盛，忌破碎不堪招灾祸", "此方不宜开门"},
            {"忌道路直冲，忌建厕所污秽地，忌崩塌破碎招横祸", "此方宜保持安宁"}
        };
        
        String[][] layoutPingInfo = {
            {"宜建杂物间储藏，保持整洁有序，布局平和无大患", "此方宜静不宜动"},
            {"宜建储藏室，保持通风干燥，布局适中无大碍", "此方宜守不宜攻"},
            {"宜建厨房或储藏，保持整洁卫生，布局平和家宅安", "此方宜静不宜喧"},
            {"宜建花园绿地，保持湿润生机，布局平缓多安稳", "此方宜静不宜闹"},
            {"宜建书房办公，保持通风明亮，布局平和文星显", "此方宜静不宜杂"},
            {"宜建卧室或储藏，保持舒适安稳，布局平缓多福寿", "此方宜静不宜动"},
            {"宜建书房或储藏，保持整洁有序，布局平和贵人助", "此方宜静不宜喧"},
            {"宜建花园或绿地，保持生机勃勃，布局平缓根基稳", "此方宜静不宜闹"}
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
            
            if (zhaoxiangViews[i] != null) {
                String zhaoxiangHtml = "<b>格局：</b>" + bfBagua + "卦·" + bfWuxing + "·" + beast +
                    " " + relation + wuxingRelation +
                    "<font color='" + luckColor + "'>【" + jiXiong + "】</font>";
                zhaoxiangViews[i].setText(Html.fromHtml(zhaoxiangHtml));
            }
            if (shuishaViews[i] != null) {
                String[] currentShuiSha = jiXiong.equals("吉") ? shuiShaJiInfo[i] : 
                                         jiXiong.equals("凶") ? shuiShaXiongInfo[i] : shuiShaPingInfo[i];
                String shuishaHtml = "<b>砂水：</b>" + currentShuiSha[0] +
                    "<font color='" + luckColor + "'>【" + jiXiong + "】</font>";
                shuishaViews[i].setText(Html.fromHtml(shuishaHtml));
            }
            if (layoutViews[i] != null) {
                String[] currentLayout = jiXiong.equals("吉") ? layoutJiInfo[i] : 
                                         jiXiong.equals("凶") ? layoutXiongInfo[i] : layoutPingInfo[i];
                String layoutHtml = "<b>布局：</b>" + currentLayout[0] +
                    "<font color='" + luckColor + "'>【" + jiXiong + "】</font>";
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
        String[] directions = {"北方", "东北", "东方", "东南", "南方", "西南", "西方", "西北"};
        String[] opposites = {"南方", "西南", "西方", "西北", "北方", "东北", "东方", "东南"};
        for (int i = 0; i < directions.length; i++) {
            if (directions[i].equals(chaoXiang)) {
                return opposites[i];
            }
        }
        return "南方";
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
        return wuxing;
    }

    private String getMountainAnalysis(String mountain, String wuxing, String bagua) {
        String[] analyses = {
                "子山：正北水位，属阳水。为阴阳交界之地，象征万物之根基与始源。子为地支之首，蕴含开创之力，主智慧谋略，财源广进。北方宜有水景，南方宜有山，东方宜种植树木，西方宜保持整洁。坎卦主险主智，适合建书房、办公室，利于文昌学业，智慧通达。",
                "癸山：东北偏北，属阴水。阴水之气深沉内敛，主神秘深邃，智慧暗藏。癸为雨露之水，润物无声，主积蓄收藏，暗中谋划。东北宜有靠山，西南宜有水景，东南宜有绿化。适合建住宅、书房，利于修行静心，学术研究。",
                "丑山：东北方，属阴土。湿土之气厚重沉稳，主积蓄与收藏，厚积薄发。丑为金库，主财库积聚，财源广进。东北宜有山，西南宜有水，东南宜保持开阔。艮卦主静止稳定，适合建仓库、商铺，利于投资理财，安居乐业。",
                "艮山：东北方，属阳土。阳土之气坚实厚重，主静止与稳定，象征山岳之稳重。艮为山，有止之意，主贵人相助，家宅安宁。东北宜有高大山峰守护，西南宜有水景。适合建住宅、祠堂，利于守成安定，家庭兴旺。",
                "寅山：东北偏东，属阳木。阳木之气旺盛生发，主进取与开创，象征草木萌芽破土而出。寅为甲木余气，主尊贵权威，事业发展。东方宜有山，西方宜有水，北方宜保持开阔。震卦主动主长，适合建办公楼、创业基地，利于开创事业。",
                "甲山：东方偏北，属阳木。甲木为参天大树，主尊贵与权威，栋梁之才。甲木参天，气势磅礴，主功名事业，步步高升。东方宜有高山，西方宜有水，南方宜有绿化。震卦主动主长，适合建办公楼、学校，利于功名事业。",
                "卯山：正东方，属阴木。阴木之气柔顺生长，主繁荣与生机，象征花草茂盛。卯木主健康发展，学业进步。东方宜种植树木，西方宜有水景，南方宜有绿化。适合建住宅、学校，利于家庭和睦，学业成长。",
                "乙山：东方偏南，属阴木。乙木为花草之木，主柔顺与仁慈，象征藤蔓缠绕。乙木虽柔，但韧性十足，主和谐发展，以柔克刚。东南宜有水，西北宜有山，东方宜种植树木。巽卦主风动传播，适合建文化场所、商铺，利于商贸交流。",
                "辰山：东南偏东，属阳土。湿土之气变化多端，主变革与转化，象征龙气潜藏。辰为水库，主智慧谋略，变革创新。东南宜有水景，西北宜有山，东北宜保持整洁。适合建办公楼、研发中心，利于变革创新，事业突破。",
                "巽山：东南方，属阴木。巽为风，主风动与传播，象征消息灵通，商贸繁荣。巽木主文昌学业，智慧通达。东南宜有水，西北宜有山，东方宜种植树木。适合建学校、写字楼，利于文化教育，商贸发展。",
                "巳山：东南偏南，属阴火。阴火之气温暖和煦，主礼仪与文明，象征蛇火之温。巳火主文化教育，艺术创作。东南宜有绿化，西北宜有水，南方宜保持整洁。适合建文化中心、艺术工作室，利于文化教育，艺术创作。",
                "丙山：南方偏东，属阳火。丙火为太阳之火，光明普照，主热情与名声。丙火旺盛，气势磅礴，主名声远播，事业辉煌。南方宜有山，北方宜有水，东方宜有绿化。离卦主明主礼，适合建写字楼、传媒中心，利于名声事业。",
                "午山：正南方，属阳火。午火为正午太阳，阳气最盛，主旺盛与显达。午为马，奔腾不息，主事业顶峰，功名成就。南方宜有山峰，北方宜有水，东方宜种植树木。离卦主明主礼，适合建办公楼、文化场所，利于事业辉煌。",
                "丁山：南方偏西，属阴火。丁火为灯烛之火，柔和温暖，主文明与才艺。丁火虽小，但能照亮黑暗，主精细品质，文明教化。南方宜有绿化，北方宜有水，西方宜保持整洁。适合建工作室、学校，利于艺术创作，文化教育。",
                "未山：西南偏南，属阴土。燥土之气成熟稳重，主收藏与收获，象征五谷成熟。未土主收获成果，安居乐业。西南宜有山，东北宜有水，南方宜有绿化。坤卦主包容厚德，适合建住宅、农场，利于家庭和睦，财富积累。",
                "坤山：西南方，属阴土。坤为地，主包容与厚德，象征大地之母孕育万物。坤土厚重，承载万物，主家庭兴旺，贵人相助。西南宜有山，东北宜有水，东南宜有绿化。适合建住宅、学校、医院，利于家庭安康，事业发展。",
                "申山：西南偏西，属阳金。阳金之气肃杀锐利，主变革与决断，象征雷电交加。申金主决断行动，突破创新。西南宜保持开阔，东北宜有水，西方宜有山。适合建办公楼、金融中心，利于突破创新，事业发展。",
                "庚山：西方偏南，属阳金。庚金为刀剑之金，锋利无比，主刚强与权威。庚金坚硬，主决断果敢，执法权威。西方宜有山，东方宜有水，北方宜保持整洁。兑卦主悦主泽，适合建办公楼、司法机构，利于权威决断。",
                "酉山：正西方，属阴金。阴金之气收敛精华，主收获与财富，象征金秋硕果。酉金主财富积累，经商致富。西方宜有水，东方宜有山，南方宜有绿化。适合建商铺、仓库，利于经商致富，财富积累。",
                "辛山：西方偏北，属阴金。辛金为首饰之金，精致秀丽，主艺术与美感。辛金虽柔，但精致典雅，主精细品质，艺术创作。西方宜有水景，东方宜有山，西北宜保持开阔。兑卦主悦主泽，适合建艺术中心、珠宝店，利于艺术创作。",
                "戌山：西北偏西，属阳土。燥土之气坚实牢固，主收藏与防备，象征狗之忠诚。戌土主守护家园，稳固事业。西北宜有山，东南宜有水，西方宜保持整洁。适合建住宅、保安室，利于守护家园，事业稳固。",
                "乾山：西北方，属阳金。乾为天，主尊贵与刚健，象征天圆地方，领导万物。乾金主权威决策，贵人相助。西北宜有高大山峰，东南宜有水，北方宜保持开阔。适合建办公楼、祠堂，利于权威决策，事业发展。",
                "亥山：西北偏北，属阴水。阴水之气流动不息，主智慧与思辨，象征天河之水。亥水主智慧思辨，学术研究。西北宜有水景，东南宜有山，北方宜保持整洁。适合建书房、科研中心，利于学术研究，智慧发展。",
                "壬山：北方偏西，属阳水。壬水为江海之水，浩瀚无垠，主博大与深远。壬水奔腾不息，主流动变化，适应能力强。北方宜有水，南方宜有山，西方宜保持整洁。坎卦主险主智，适合建办公楼、图书馆，利于智慧发展，事业成就。"
        };
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return analyses[i];
            }
        }
        return mountain + "：" + wuxing + "行之地，" + bagua + "卦之位。";
    }

    private String getZhaoxiangAnalysis(String zuoShan, String chaoXiang) {
        String[] zuoChaoPairs = {
                "子午", "丑未", "寅申", "卯酉", "辰戌", "巳亥",
                "艮坤", "巽乾", "甲庚", "乙辛", "丙壬", "丁癸"
        };
        String[] analyses = {
                "坐子向午：水火既济，坎离相交。子为北方阳水，午为南方阳火，阴阳平衡，水火相济。主富贵双全，事业蒸蒸日上，家庭和睦。南方朱雀高耸，北方玄武厚实，青龙白虎环抱有情。适合经商、从政、文化教育等行业。",
                "坐丑向未：土土比和，坤艮相对。丑为东北阴土，未为西南阴土，二土比和，稳重厚实。主积蓄财富，安居乐业，家庭美满。西南为坤卦，主母亲长女，利于家庭和睦。适合房地产、农业、金融等行业。",
                "坐寅向申：金木相战，震兑相冲。寅为东北阳木，申为西南阳金，金木相战。主奋发图强，虽有挑战但终能成功，宜主动进取。申金为白虎，主肃杀变革，利于突破创新。适合军警、法律、金融等行业。",
                "坐卯向酉：木金相克，东西相对。卯为正东阴木，酉为正西阴金，卯酉相冲。主刚柔并济，利于开拓创新，名声远播。东方青龙高耸，西方白虎驯服，主贵人相助。适合文化艺术、商贸、科技等行业。",
                "坐辰向戌：土土比和，辰戌相冲。辰为东南阳土，戌为西北阳土，二土比和。主稳定坚固，利于守成发展，基业长青。辰为水库，戌为火库，水火相济，主智慧谋略。适合建筑、矿业、仓储等行业。",
                "坐巳向亥：水火相克，巳亥相冲。巳为东南阴火，亥为西北阴水，水火相克。主智慧谋略，利于策划运筹，决胜千里。巳为蛇火之温，亥为天河之水，主神秘深邃。适合文化教育、学术研究、科技研发等行业。",
                "坐艮向坤：土土比和，艮坤相对。艮为东北阳土，坤为西南阴土，二土比和。主厚德载物，利于包容万物，家庭兴旺。艮为山止，坤为地顺，主稳定安宁。适合房地产、建筑、慈善等行业。",
                "坐巽向乾：木金相克，巽乾相对。巽为东南阴木，乾为西北阳金，木金相克。主风助火势，利于传播发展，贵人相助。巽为风动传播，乾为天贵刚健，主事业有成。适合传媒、互联网、商贸等行业。",
                "坐甲向庚：木金相克，甲庚相冲。甲为东方阳木，庚为西方阳金，金木相克。主权威决断，利于功名事业，步步高升。甲木参天，庚金锐利，主刚健果断。适合领导管理、政治、军警等行业。",
                "坐乙向辛：木金相克，乙辛相冲。乙为东方阴木，辛为西方阴金，木金相克。主秀丽才华，利于艺术创作，声名鹊起。乙木柔顺，辛金精致，主美感艺术。适合艺术设计、文化教育、珠宝等行业。",
                "坐丙向壬：火水相克，丙壬相交。丙为南方阳火，壬为北方阳水，水火既济。主光明智慧，利于学术研究，成就非凡。丙火普照，壬水浩瀚，主博大深远。适合学术科研、教育培训、文化传播等行业。",
                "坐丁向癸：火水相克，丁癸相交。丁为南方阴火，癸为北方阴水，水火相济。主文明教化，利于文化教育，桃李满门。丁火柔和，癸水滋润，主精细品质。适合艺术创作、手工艺、精密制造等行业。"
        };
        String pair = zuoShan + chaoXiang;
        for (int i = 0; i < zuoChaoPairs.length; i++) {
            if (zuoChaoPairs[i].equals(pair)) {
                return analyses[i];
            }
        }
        return "坐" + zuoShan + "向" + chaoXiang + "：阴阳相配，五行相生，主吉祥顺遂。青龙宜高耸，白虎宜柔顺，朱雀宜端庄，玄武宜厚实。";
    }

    private String getShuiShaAnalysis(String mountain, String bagua) {
        String[] baguaShuiSha = {
                "坎卦·北方：北方宜有水，主智慧财运，象征智慧源泉，财源滚滚。南方宜有山，朱雀高耸，主名声远播，事业辉煌。北方不宜有高大建筑物阻挡，南方不宜有深坑或低洼之地。青龙在左（东方）宜高耸，白虎在右（西方）宜柔顺。玄武（北方）宜厚实，朱雀（南方）宜端庄秀丽。",
                "艮卦·东北：东北宜有山，主贵人相助，象征靠山坚实，贵人扶持。西南宜有水，财源广进，象征财库充盈。东北方不宜低洼潮湿，西南方不宜有高大建筑物阻挡。艮为山，有止之意，宜静不宜动，适合建宅安身，休养生息。",
                "震卦·东方：东方宜有山，青龙高耸，主贵人相助，声名远播。西方宜有水，白虎驯服，主财源广进，家宅安宁。东方不宜有深坑或低洼，西方不宜有高大尖锐的建筑物。震为动，主动主长，适合发展事业，开拓进取。",
                "巽卦·东南：东南宜有水，主文昌学业，智慧通达，利于考试求学。西北宜有山，贵人扶持，主事业有成。东南方不宜有高大建筑物阻挡，西北方不宜低洼潮湿。巽为风，主风动传播，适合从事商贸交流、文化传播等行业。",
                "中宫·中央：中央宜平坦开阔，不宜高物遮挡，主运势平稳，家庭和睦。中央为太极之位，是整个格局的核心，宜保持整洁明亮，不宜堆放杂物。中宫宜方正，不宜缺角，象征天地和谐，万物平衡。",
                "乾卦·西北：西北宜有山，主贵人相助，象征天德护佑，贵人提携。东南宜有水，财源滚滚，象征财气汇聚。西北方不宜低洼潮湿，东南方不宜有高大建筑物阻挡。乾为天，主尊贵刚健，适合建办公场所、祠堂庙宇。",
                "兑卦·西方：西方宜有水，主财源广进，象征财库丰盈，经商致富。东方宜有山，青龙护卫，主贵人相助，家宅平安。西方不宜有高大尖锐的建筑物，东方不宜有深坑或低洼。兑为悦，主喜悦和谐，适合建商铺、娱乐场所。",
                "坤卦·西南：西南宜有山，主厚德载物，象征大地孕育，家庭兴旺。东北宜有水，积蓄丰厚，象征财库充盈。西南方不宜低洼潮湿，东北方不宜有高大建筑物阻挡。坤为地，主包容厚德，适合建住宅、学校、医院。",
                "离卦·南方：南方宜有山，朱雀展翅，主名声远播，事业辉煌。北方宜有水，智慧通达，象征智慧源泉。南方不宜有深坑或低洼，北方不宜有高大建筑物阻挡。离为火，主光明文明，适合建文化场所、写字楼。"
        };
        String[] baguaList = {"坎", "艮", "震", "巽", "中", "乾", "兑", "坤", "离"};
        for (int i = 0; i < baguaList.length; i++) {
            if (baguaList[i].equals(bagua)) {
                return baguaShuiSha[i];
            }
        }
        return "坐山" + mountain + "，" + bagua + "卦方位：青龙宜高耸，白虎宜柔顺，朱雀宜端庄，玄武宜厚实。四象完备，五行相生，主吉祥安康。";
    }

    private String getMingLiAnalysis(String mountain, String wuxing) {
        String[] baguaList = {"坎", "艮", "震", "巽", "中", "乾", "兑", "坤", "离"};
        String[] baguaBuildings = {
                "坎卦·北方：大门宜开南方或东南方，主卧宜设东北方或西北方，厨房宜设东方或东南方。北方宜有水景，南方宜有绿化，东方宜种植树木，西方宜保持整洁。书房宜设在北方或东方，利于文昌学业。",
                "艮卦·东北：大门宜开南方或西方，主卧宜设东北方或西北方，厨房宜设东方或东南方。东北宜有靠山，西南宜有水景，东南宜有绿化，西北宜保持开阔。书房宜设在东南方，利于文昌学业。",
                "震卦·东方：大门宜开南方或东南方，主卧宜设东方或东南方，厨房宜设东南方或南方。东方宜种植树木，西方宜有水景，南方宜有绿化，北方宜保持整洁。书房宜设在东方或东南方，利于文昌学业。",
                "巽卦·东南：大门宜开东方或南方，主卧宜设东南方或南方，厨房宜设东方或东南方。东南宜有水景，西北宜有靠山，东方宜种植树木，西南宜保持整洁。书房宜设在东南方，利于文昌学业。",
                "中宫·中央：大门宜开南方或东方，主卧宜设中央或北方，厨房宜设东方或南方。中央宜保持开阔，四周宜有绿化环绕，不宜有高大建筑物遮挡。书房宜设在东方或东南方，利于文昌学业。",
                "乾卦·西北：大门宜开东南方或南方，主卧宜设西北方或北方，厨房宜设东南方或南方。西北宜有靠山，东南宜有水景，南方宜有绿化，东方宜保持整洁。书房宜设在东南方或东方，利于文昌学业。",
                "兑卦·西方：大门宜开东方或南方，主卧宜设西方或北方，厨房宜设东方或南方。西方宜有水景，东方宜有靠山，南方宜有绿化，北方宜保持整洁。书房宜设在东方或东南方，利于文昌学业。",
                "坤卦·西南：大门宜开东北方或东方，主卧宜设西南方或北方，厨房宜设东方或北方。西南宜有靠山，东北宜有水景，东方宜种植树木，北方宜保持整洁。书房宜设在东方或东南方，利于文昌学业。",
                "离卦·南方：大门宜开北方或东方，主卧宜设南方或东方，厨房宜设东方或北方。南方宜有绿化，北方宜有水景，东方宜种植树木，西方宜保持整洁。书房宜设在东方或北方，利于文昌学业。"
        };
        
        String chaoXiang = getChaoXiang(mountain);
        
        StringBuilder result = new StringBuilder();
        result.append("坐").append(mountain).append("向").append(chaoXiang).append("建筑布局建议：\n\n");
        result.append("【大门方位】宜开").append(getLuckyDoorDirection(mountain)).append("\n");
        result.append("【主卧方位】宜设").append(getLuckyBedroomDirection(mountain)).append("\n");
        result.append("【厨房方位】宜设").append(getLuckyKitchenDirection(mountain)).append("\n");
        result.append("【书房方位】宜设").append(getLuckyStudyDirection(mountain)).append("\n\n");
        result.append("【庭院布局】").append(getYardLayout(mountain));
        
        return result.toString();
    }
    
    private String getChaoXiang(String mountain) {
        int mountainIndex = -1;
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                mountainIndex = i;
                break;
            }
        }
        if (mountainIndex >= 0) {
            return mountainNames[(mountainIndex + 12) % 24];
        }
        return "未知";
    }
    
    private String getLuckyDoorDirection(String mountain) {
        String[] luckyDoors = {
                "南方、东南方", "南方、东南方", "南方、西方", "北方、西方",
                "南方、东方", "南方、东南方", "南方、东南方", "南方、东方",
                "南方、东南方", "北方、东方", "南方、东方", "南方、东方",
                "北方、东方", "北方、东南方", "北方、东方", "北方、东北方",
                "北方、东南方", "北方、东方", "北方、东方", "北方、东南方",
                "北方、东方", "北方、东南方", "北方、东方", "南方、东方"
        };
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return luckyDoors[i];
            }
        }
        return "南方、东方";
    }
    
    private String getLuckyBedroomDirection(String mountain) {
        String[] luckyBedrooms = {
                "东北方、西北方", "东北方、西北方", "东北方、西北方", "东方、东南方",
                "东方、东南方", "东方、东南方", "东方、东南方", "东南方、南方",
                "东南方、南方", "东南方、南方", "南方、东方", "南方、东方",
                "北方、西方", "北方、西方", "北方、西方", "北方、西南",
                "北方、西南", "北方、西南", "北方、西北", "北方、西北",
                "北方、西北", "北方、西北", "北方、东北", "北方、东北"
        };
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return luckyBedrooms[i];
            }
        }
        return "北方、东方";
    }
    
    private String getLuckyKitchenDirection(String mountain) {
        String[] luckyKitchens = {
                "东方、东南方", "东方、东南方", "东方、东南方", "东南方、南方",
                "东南方、南方", "东南方、南方", "东南方、南方", "东方、南方",
                "东方、南方", "东方、南方", "东方、南方", "东方、南方",
                "东北方、东方", "东北方、东方", "东北方、东方", "东方、北方",
                "东方、北方", "东方、北方", "东北方、东方", "东北方、东方",
                "东北方、东方", "东北方、东方", "东方、南方", "东方、南方"
        };
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return luckyKitchens[i];
            }
        }
        return "东方、南方";
    }
    
    private String getLuckyStudyDirection(String mountain) {
        String[] luckyStudies = {
                "北方、东方", "北方、东方", "东南方、东方", "东南方、东方",
                "东南方、东方", "东南方、东方", "东南方、东方", "东南方、东方",
                "东南方、东方", "东南方、东方", "东方、北方", "东方、北方",
                "东南方、东方", "东南方、东方", "东南方、东方", "东南方、东方",
                "东南方、东方", "东南方、东方", "东方、北方", "东方、北方",
                "东方、北方", "东方、北方", "东方、东南", "东方、东南"
        };
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return luckyStudies[i];
            }
        }
        return "东方、东南方";
    }
    
    private String getYardLayout(String mountain) {
        String[] layouts = {
                "北方宜建水池或水景，南方宜种花草树木，东方宜种植高大树木作为青龙，西方宜保持平整开阔。",
                "东北方宜有山石或矮墙作为靠山，西南方宜建水池或水景，东南方宜种植花草，西北方宜保持开阔。",
                "东方宜种植高大树木作为青龙，西方宜建水池或水景，南方宜种花草树木，北方宜保持平整开阔。",
                "东南方宜建水池或水景，西北方宜有山石或高楼作为靠山，东方宜种植树木，西南方宜保持平整。",
                "中央宜保持开阔平坦，四周宜种植花草树木，不宜有高大建筑物遮挡。",
                "西北方宜有山石或高楼作为靠山，东南方宜建水池或水景，南方宜种花草树木，东方宜保持平整。",
                "西方宜建水池或水景，东方宜种植高大树木作为青龙，南方宜种花草树木，北方宜保持平整开阔。",
                "西南方宜有山石或矮墙作为靠山，东北方宜建水池或水景，东方宜种植树木，北方宜保持平整。",
                "南方宜种花草树木，北方宜建水池或水景，东方宜种植高大树木，西方宜保持平整开阔。"
        };
        return layouts[0];
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
        
        bazhaiDesc.setText(Html.fromHtml(sb.toString()));
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
        
        jiuxingDesc.setText(Html.fromHtml(sb.toString()));
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
        
        bamenDesc.setText(Html.fromHtml(sb.toString()));
    }

    private String getBuildingAdvice(String mountain, String bagua) {
        String[] buildingAdvices = {
                "子山：宜坐北朝南，大门宜开南方或东南方。北方宜有水，南方宜有山，利财利子。",
                "癸山：宜坐癸向丁，大门宜开东南方。东北方宜有水，南方宜平坦开阔。",
                "丑山：宜坐东北向西南，大门宜开西南方。东北方宜有山，西南方宜有水。",
                "艮山：宜坐东北向西南，大门宜开南方或西南方。东北方宜有高大山峰守护。",
                "寅山：宜坐东北偏东向西南偏西，大门宜开南方。东方宜有水，西方宜平坦。",
                "甲山：宜坐东向北偏西，大门宜开北方或西北方。东方宜有山，北方宜有水。",
                "卯山：宜坐东向西，大门宜开西方或西北方。东方宜有山，西方宜有水。",
                "乙山：宜坐东向南偏西，大门宜开南方或西南方。东方宜平坦，南方宜有水。",
                "辰山：宜坐东南偏东向西北偏西，大门宜开西方。东南方宜有水，西北方宜有山。",
                "巽山：宜坐东南向西北，大门宜开西北方。东南方宜有水，西北方宜有高大山峰。",
                "巳山：宜坐东南偏南向西北偏北，大门宜开北方。南方宜有山，北方宜有水。",
                "丙山：宜坐南向东偏北，大门宜开东方或东北方。南方宜有山，东方宜有水。",
                "午山：宜坐南向北，大门宜开北方或东北方。南方宜有山，北方宜有水，最吉布局。",
                "丁山：宜坐南向西偏北，大门宜开西方或西北方。南方宜平坦，西方宜有水。",
                "未山：宜坐西南偏南向东北偏北，大门宜开东北方。西南方宜有山，东北方宜有水。",
                "坤山：宜坐西南向东北，大门宜开东北方或北方。西南方宜有山，东北方宜平坦开阔。",
                "申山：宜坐西南偏西向东北偏东，大门宜开东方。西方宜有水，东方宜有山。",
                "庚山：宜坐西向东偏南，大门宜开东方或东南方。西方宜有山，东方宜有水。",
                "酉山：宜坐西向东，大门宜开东方或东南方。西方宜有山，东方宜有水，利文昌。",
                "辛山：宜坐西向北偏东，大门宜开北方或东北方。西方宜平坦，北方宜有水。",
                "戌山：宜坐西北偏西向东南偏东，大门宜开东南方。西北方宜有山，东南方宜有水。",
                "乾山：宜坐西北向东南，大门宜开东南方或南方。西北方宜有高大山峰，主贵人相助。",
                "亥山：宜坐西北偏北向东南偏南，大门宜开东南方。北方宜有水，东南方宜有山。",
                "壬山：宜坐北向西偏南，大门宜开西方或西南方。北方宜有山，西方宜有水。"
        };
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return buildingAdvices[i];
            }
        }
        return mountain + "山，" + bagua + "卦方位：宜开门纳气，藏风聚水，依山傍水为吉。";
    }

    
    
    private String getLayoutKeyPoints(String mountain, String chaoXiang, String bagua) {
        String[] keyPoints = {
            "子山午向：南方宜有山峰朱雀高耸，北方宜有水景玄武厚实，东方宜种植树木青龙旺盛，西方宜平坦开阔白虎驯服。大门宜开南方纳阳气，主卧宜设东北方纳生气。",
            "癸山丁向：东南方宜开门纳气，北方宜有水蓄财，南方宜平坦开阔见明堂。东北方宜有靠山稳根基。",
            "丑山未向：东北方宜有山守护，西南方宜有水旺财。大门宜开西南方纳坤气，利于家庭和睦。",
            "艮山坤向：东北方宜有高大山峰，主贵人相助。南方宜有水旺财，大门宜开南方或西南方。",
            "寅山申向：东方宜有水旺财，西方宜平坦开阔。南方宜有山，大门宜开南方纳阳气。",
            "甲山庚向：东方宜有山青龙高耸，北方宜有水玄武厚实。大门宜开北方或西北方纳气。",
            "卯山酉向：东方宜有山，西方宜有水旺财。大门宜开西方纳兑气，利于文昌学业。",
            "乙山辛向：东方宜平坦，南方宜有水旺财。大门宜开南方或西南方，利于人际关系。",
            "辰山戌向：东南方宜有水，西北方宜有山。大门宜开西方，利于事业发展。",
            "巽山乾向：东南方宜有水旺财，西北方宜有高大山峰贵人相助。大门宜开西北方。",
            "巳山亥向：南方宜有山，北方宜有水。大门宜开北方，利于财运亨通。",
            "丙山壬向：南方宜有山，东方宜有水。大门宜开东方或东北方，利于事业起步。",
            "午山子向：南方宜有山朱雀高耸，北方宜有水玄武厚实。此为水火既济格局，最吉布局，大门宜开北方纳阳气。",
            "丁山癸向：南方宜平坦，西方宜有水旺财。大门宜开西方或西北方，利于名声远播。",
            "未山丑向：西南方宜有山，东北方宜有水旺财。大门宜开东北方，利于家庭稳定。",
            "坤山艮向：西南方宜有山靠山稳固，东北方宜平坦开阔见明堂。大门宜开东北方。",
            "申山寅向：西方宜有水旺财，东方宜有山青龙高耸。大门宜开东方，利于事业进取。",
            "庚山甲向：西方宜有山白虎驯服，东方宜有水旺财。大门宜开东方或东南方。",
            "酉山卯向：西方宜有山，东方宜有水旺财。大门宜开东方纳震气，利文昌学业。",
            "辛山乙向：西方宜平坦，北方宜有水旺财。大门宜开北方或东北方，利于贵人相助。",
            "戌山辰向：西北方宜有山，东南方宜有水旺财。大门宜开东南方，利于事业开拓。",
            "乾山巽向：西北方宜有高大山峰贵人相助，东南方宜有水旺财。大门宜开东南方。",
            "亥山巳向：北方宜有水旺财，东南方宜有山。大门宜开东南方，利于智慧发展。",
            "壬山丙向：北方宜有山玄武厚实，西方宜有水旺财。大门宜开西方或西南方。"
        };
        
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                return keyPoints[i];
            }
        }
        return "坐" + mountain + "向" + chaoXiang + "，" + bagua + "卦方位：宜开门纳气，藏风聚水，依山傍水为吉。青龙宜高耸，白虎宜驯服，朱雀宜端庄，玄武宜厚实。";
    }
    
    private String getShanshuiFengshui(String mountain, String chaoXiang, String wuxing, String bagua) {
        StringBuilder sb = new StringBuilder();
        
        String[][] shanshuiInfo = {
            {"东方", "震卦", "木", "青龙砂", "宜高耸苍翠", "主贵人相助，人丁兴旺", "忌破碎低矮，主小人作祟"},
            {"南方", "离卦", "火", "朱雀砂", "宜端庄秀丽", "主名声远播，事业有成", "忌尖射冲煞，主口舌是非"},
            {"西方", "兑卦", "金", "白虎砂", "宜驯服柔顺", "主财源广进，家庭和睦", "忌高大雄健，主灾祸横生"},
            {"北方", "坎卦", "水", "玄武砂", "宜厚实稳重", "主根基稳固，贵人扶持", "忌空虚塌陷，主家运衰败"},
            {"东南", "巽卦", "木", "青龙砂", "宜清秀高耸", "主智慧通达，文昌兴盛", "忌堵塞压迫，主阻碍重重"},
            {"西南", "坤卦", "土", "白虎砂", "宜稳重厚实", "主家庭和睦，人丁兴旺", "忌陡峭险峻，主多灾多病"},
            {"西北", "乾卦", "金", "玄武砂", "宜高大雄伟", "主权威显赫，事业发达", "忌低洼潮湿，主财气流失"},
            {"东北", "艮卦", "土", "青龙砂", "宜敦厚稳重", "主家宅平安，子孙昌盛", "忌崩塌破碎，主根基不稳"}
        };
        
        sb.append("<b>山水格局：").append(mountain).append("山").append(chaoXiang).append("向</b><br/><br/>");
        
        for (int i = 0; i < shanshuiInfo.length; i++) {
            String direction = shanshuiInfo[i][0];
            String bfBagua = shanshuiInfo[i][1];
            String bfWuxing = shanshuiInfo[i][2];
            String shaType = shanshuiInfo[i][3];
            String suitable = shanshuiInfo[i][4];
            String benefits = shanshuiInfo[i][5];
            String avoid = shanshuiInfo[i][6];
            
            String wuxingRelation = getWuxingRelation(wuxing, bfWuxing);
            String jiXiong = getLuckFromWuxingRelation(wuxingRelation);
            String luckColor = jiXiong.equals("吉") ? "#00CC00" : jiXiong.equals("凶") ? "#FF4444" : "#FFAA00";
            
            sb.append("<font color='").append(luckColor).append("'>【").append(jiXiong).append("】</font>");
            sb.append("<b>").append(direction).append("·").append(bfBagua).append("·").append(bfWuxing).append("</b>");
            sb.append(wuxingRelation).append("<br/>");
            sb.append("  ").append(shaType).append("：").append(suitable).append("<br/>");
            sb.append("  <font color='#00CC00'>【吉应】</font>").append(benefits).append("<br/>");
            sb.append("  <font color='#FF4444'>【凶应】</font>").append(avoid).append("<br/><br/>");
        }
        
        sb.append("<b>水法要点</b><br/>");
        sb.append("  山管人丁水管财，水来之处为财源，水去之处为水口。<br/>");
        sb.append("  <font color='#00CC00'>【宜】</font>水来有情，环抱有情，九曲入明堂<br/>");
        sb.append("  <font color='#FF4444'>【忌】</font>水直冲射，反弓水，割脚水<br/>");
        sb.append("  水口方位：").append(getShuikouDirection(mountain)).append("方为佳，宜紧锁关拦<br/>");
        
        return sb.toString();
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
        
        sb.append("<b>寻龙点穴，择吉而葬。</b>龙真穴的，砂水环绕，明堂开阔，水口紧锁。<br/><br/>");
        
        sb.append("<b>寻龙要点</b><br/>");
        sb.append("  <font color='#00CC00'>【吉】</font>龙要真：山脉起伏，气势磅礴，有来龙去脉<br/>");
        sb.append("  <font color='#00CC00'>【吉】</font>龙要活：山势蜿蜒，生动活泼，非僵硬死板<br/>");
        sb.append("  <font color='#00CC00'>【吉】</font>龙要旺：草木茂盛，水土滋润，生机勃勃<br/>");
        sb.append("  <font color='#00CC00'>【吉】</font>龙要止：山脉尽头，有结穴之处，非奔腾不息<br/><br/>");
        
        sb.append("<b>点穴要领</b><br/>");
        sb.append("  <font color='#00CC00'>【吉】</font>穴要的：藏风聚气，阴阳交合，生气凝聚<br/>");
        sb.append("  <font color='#00CC00'>【吉】</font>穴要暖：土质温润，不燥不湿，四季常温<br/>");
        sb.append("  <font color='#00CC00'>【吉】</font>穴要稳：靠山稳固，左右有护，前有明堂<br/>");
        sb.append("  <font color='#00CC00'>【吉】</font>穴要净：周围清净，无冲煞之气，无污秽之物<br/><br/>");
        
        String[] sbWuxing = {"木", "金", "火", "水"};
        String[] sbJiXiong = new String[4];
        String[] sbColors = new String[4];
        
        for (int i = 0; i < 4; i++) {
            String relation = getWuxingRelation(wuxing, sbWuxing[i]);
            sbJiXiong[i] = getLuckFromWuxingRelation(relation);
            sbColors[i] = sbJiXiong[i].equals("吉") ? "#00CC00" : sbJiXiong[i].equals("凶") ? "#FF4444" : "#FFAA00";
        }
        
        sb.append("<b>砂水环抱</b><br/>");
        sb.append("  <font color='").append(sbColors[0]).append("'>【").append(sbJiXiong[0]).append("】</font>");
        sb.append("青龙砂（左）：宜高耸秀丽，主贵人相助<br/>");
        sb.append("  <font color='").append(sbColors[1]).append("'>【").append(sbJiXiong[1]).append("】</font>");
        sb.append("白虎砂（右）：宜驯服柔顺，主财源广进<br/>");
        sb.append("  <font color='").append(sbColors[2]).append("'>【").append(sbJiXiong[2]).append("】</font>");
        sb.append("朱雀砂（前）：宜端庄秀丽，主名声远播<br/>");
        sb.append("  <font color='").append(sbColors[3]).append("'>【").append(sbJiXiong[3]).append("】</font>");
        sb.append("玄武砂（后）：宜厚实稳重，主根基稳固<br/><br/>");
        
        sb.append("<b>水口紧锁</b><br/>");
        sb.append("  水口者，水之出口也，宜有关拦，不宜直泄<br/>");
        sb.append("  <font color='#00CC00'>【吉】</font>水口紧锁，则财气不散，福泽留存<br/>");
        sb.append("  <font color='#FF4444'>【凶】</font>水口直泄，则财气流失，家道中落<br/><br/>");
        
        sb.append("<b>").append(mountain).append("山").append(chaoXiang).append("向建议</b><br/>");
        sb.append(getYinzhaiSpecificAdvice(mountain));
        
        return sb.toString();
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
    
    private String getSummary(String mountain, String chaoXiang, String wuxing, String bagua) {
        String[] luckyDirs = {"南方", "东南方", "东方", "东北方", "西方", "西南方", "西北方", "北方"};
        String[] luckyColors = {"红色", "青色", "绿色", "黄色", "白色", "黄色", "白色", "黑色"};
        
        int mountainIndex = 0;
        for (int i = 0; i < mountainNames.length; i++) {
            if (mountainNames[i].equals(mountain)) {
                mountainIndex = i;
                break;
            }
        }

        String luckyDir = luckyDirs[mountainIndex % 8];
        String luckyColor = luckyColors[mountainIndex % 8];

        return "坐山：" + mountain + "，朝向：" + chaoXiang + "\n" +
                "五行：" + wuxing + "，八卦：" + bagua + "\n\n" +
                "吉利方位：" + luckyDir + "\n" +
                "吉祥颜色：" + luckyColor + "\n\n" +
                "整体格局：坐" + mountain + "向" + chaoXiang + "为" + getZhaoxiangAnalysis(mountain, chaoXiang).substring(0, 8) + "\n" +
                "建议：择吉而动，顺势而为，趋吉避凶，福禄自来。";
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
        
        return "坐" + mountain + "向" + chaoXiang + "，五行属" + wuxing + "，";
    }
}