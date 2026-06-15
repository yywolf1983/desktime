package com.example.timedisplay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class WuyunLiuqiActivity extends Activity {

    private WuyunLiuqiView wuyunLiuqiView;
    private TextView currentShichenInfo;
    private TextView wuyunInfo;
    private TextView liuqiInfo;
    private TextView liuqiDetail;
    private TextView shichenDetail;
    
    private Handler updateHandler;
    private Runnable updateRunnable;
    
    private static final String[] TIANGAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] DIZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    
    private static final String[] SHICHEN_NAMES = {
        "子时", "丑时", "寅时", "卯时", "辰时", "巳时",
        "午时", "未时", "申时", "酉时", "戌时", "亥时"
    };
    
    private static final String[] SHICHEN_TIMES = {
        "23:00-01:00", "01:00-03:00", "03:00-05:00",
        "05:00-07:00", "07:00-09:00", "09:00-11:00",
        "11:00-13:00", "13:00-15:00", "15:00-17:00",
        "17:00-19:00", "19:00-21:00", "21:00-23:00"
    };
    
    private static final String[] SHICHEN_QUOTES = {
        "夜半", "鸡鸣", "平旦", "日出", "食时", "隅中",
        "日中", "日昳", "晡时", "日入", "黄昏", "人定"
    };
    
    private static final String[] SHICHEN_YANGSHENG = {
        "胆经·宜熟睡", "肝经·宜熟睡", "肺经·宜深睡",
        "大肠经·宜排便", "胃经·宜进食", "脾经·宜工作",
        "心经·宜午休", "小肠经·宜休息", "膀胱经·宜运动",
        "肾经·宜静养", "心包经·宜放松", "三焦经·宜安眠"
    };
    
    private static final String[] SHICHEN_YIJI = {
        "宜：安睡、打坐\n忌：熬夜\n💡 建议：子时是阴气最盛的时候",
        "宜：深睡、养肝\n忌：饮酒\n💡 建议：丑时是肝脏排毒时间",
        "宜：熟睡、静卧\n忌：剧烈\n💡 建议：寅时肺经当令，宜深度呼吸",
        "宜：排便、饮水\n忌：忍便\n💡 建议：卯时起床喝温水促进排便",
        "宜：早餐、进食\n忌：空腹\n💡 建议：辰时是吃早餐的黄金时间",
        "宜：思考、工作\n忌：懒惰\n💡 建议：巳时脾经旺，适合学习工作",
        "宜：午休、休憩\n忌：劳累\n💡 建议：午时心经当令，小睡片刻养心",
        "宜：休息、放松\n忌：剧烈\n💡 建议：未时小肠吸收营养",
        "宜：运动、饮水\n忌：憋尿\n💡 建议：申时膀胱经旺，适合运动",
        "宜：静养、休息\n忌：操劳\n💡 建议：酉时肾经当令，宜收藏精气",
        "宜：愉悦、休闲\n忌：忧愁\n💡 建议：戌时心包经旺，保持心情愉悦",
        "宜：安眠、泡脚\n忌：多虑\n💡 建议：亥时三焦通百脉，准备入睡"
    };
    
    private static final String[] SHICHEN_ZANGFU = {
        "胆", "肝", "肺", "大肠", "胃", "脾", "心", "小肠", "膀胱", "肾", "心包", "三焦"
    };
    
    private static final String[] WUXING_ZODIAC = {
        "水·鼠", "土·牛", "木·虎", "木·兔", "土·龙", "火·蛇",
        "火·马", "土·羊", "金·猴", "金·鸡", "土·狗", "水·猪"
    };
    
    private static final String[] WUXING_SHEJI = {
        "水·智", "土·信", "木·仁", "木·仁",
        "土·信", "火·礼", "火·礼", "土·信",
        "金·义", "金·义", "土·信", "水·智"
    };
    
    private static final String[] SHICHEN_WUYIN = {
        "羽", "宫", "角", "角",
        "宫", "徵", "徵", "宫",
        "商", "商", "宫", "羽"
    };
    
    private static final String[] SHICHEN_FANGWEI = {
        "北方", "中央", "东方", "东方",
        "中央", "南方", "南方", "中央",
        "西方", "西方", "中央", "北方"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isRotationLocked = prefs.getBoolean("rotationLocked", false);
        int lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(lockedOrientation);
        }
        
        setContentView(R.layout.activity_wuyun_liuqi);
        
        initViews();
        setupUpdateHandler();
    }
    
    private void initViews() {
        wuyunLiuqiView = findViewById(R.id.wuyunLiuqiView);
        currentShichenInfo = findViewById(R.id.currentShichenInfo);
        wuyunInfo = findViewById(R.id.wuyunInfo);
        liuqiInfo = findViewById(R.id.liuqiInfo);
        liuqiDetail = findViewById(R.id.liuqiDetail);
        shichenDetail = findViewById(R.id.shichenDetail);
    }
    
    private void setupUpdateHandler() {
        updateHandler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateWuyunLiuqiInfo();
                long nextMinute = ((SystemClock.uptimeMillis() / 60000) + 1) * 60000;
                updateHandler.postAtTime(this, nextMinute);
            }
        };
        
        updateWuyunLiuqiInfo();
        long nextMinute = ((SystemClock.uptimeMillis() / 60000) + 1) * 60000;
        updateHandler.postAtTime(updateRunnable, nextMinute);
    }
    
    private void updateWuyunLiuqiInfo() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        String yearGanZhi = calculateYearGanZhi(year);
        int currentShichenIndex = getCurrentShichenIndex(hour, minute);
        
        wuyunLiuqiView.setCurrentShichen(currentShichenIndex);
        updateShichenInfo(currentShichenIndex);
        updateWuyunDisplay(yearGanZhi);
        updateLiuqiDisplay(year, month, day);
    }
    
    private void updateShichenInfo(int index) {
        StringBuilder info = new StringBuilder();
        info.append("<font color='#FFD700'>").append(SHICHEN_NAMES[index]).append("时</font> ");
        info.append("<font color='#98D8F0'>").append(SHICHEN_TIMES[index]).append("</font><br/>");
        info.append("<font color='#87CEEB'>").append(SHICHEN_QUOTES[index]).append("</font> ");
        info.append("<font color='#FFA500'>").append(WUXING_ZODIAC[index]).append("</font><br/>");
        info.append("<font color='#90EE90'>").append(SHICHEN_ZANGFU[index]).append("经</font> ");
        info.append("<font color='#DDA0DD'>").append(WUXING_SHEJI[index]).append("</font><br/>");
        info.append("<font color='#FF8C00'>").append(SHICHEN_WUYIN[index]).append("</font> ");
        info.append("<font color='#87CEEB'>").append(SHICHEN_FANGWEI[index]).append("</font>");
        currentShichenInfo.setText(android.text.Html.fromHtml(info.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        StringBuilder yiJi = new StringBuilder();
        String[] parts = SHICHEN_YIJI[index].split("\\n");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.startsWith("宜：")) {
                yiJi.append("<font color='#90EE90'>宜：").append(part.substring(2)).append("</font>");
            } else if (part.startsWith("忌：")) {
                yiJi.append("<font color='#FF6B6B'>忌：").append(part.substring(2)).append("</font>");
            } else if (part.startsWith("💡")) {
                yiJi.append("<font color='#FFD700'>").append(part).append("</font>");
            } else {
                yiJi.append(part);
            }
            if (i < parts.length - 1) yiJi.append("<br/>");
        }
        shichenDetail.setText(android.text.Html.fromHtml(yiJi.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
    }
    
    private void updateWuyunDisplay(String yearGanZhi) {
        String yearGan = yearGanZhi.substring(0, 1);
        String zhongYun = getZhongYunShort(yearGan);
        String[] sijiYun = getSijiYun(yearGan);
        
        String[][] yunDetails = {
            {"木", "主生发、生长", "#90EE90"},
            {"火", "主炎热、繁荣", "#FF6B6B"},
            {"土", "主孕育、稳定", "#DEB887"},
            {"金", "主收敛、收获", "#C0C0C0"},
            {"水", "主潜藏、储备", "#87CEEB"}
        };
        
        StringBuilder info = new StringBuilder();
        info.append("<font color='#FFD700'>").append(yearGanZhi).append("年</font><br/>");
        info.append("<font color='#87CEEB'>中运：").append(zhongYun).append("</font><br/><br/>");
        
        String[] labels = {"初运", "二运", "三运", "四运", "终运"};
        for (int i = 0; i < 5; i++) {
            info.append("<font color='#98D8F0'>").append(labels[i]).append("</font> ");
            info.append("<font color='").append(yunDetails[i][2]).append("'>").append(sijiYun[i]).append("运</font> ");
            info.append("<font color='#6B7B8A'>(").append(yunDetails[i][0]).append("·").append(yunDetails[i][1]).append(")</font>");
            if (i < 4) info.append("<br/>");
        }
        wuyunInfo.setText(android.text.Html.fromHtml(info.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
    }
    
    private void updateLiuqiDisplay(int year, int month, int day) {
        String[] liuqi = getLiuqiForDate(year, month, day);
        int currentQiIndex = getCurrentQiIndex(month, day);
        
        String[] qiNames = {"初气", "二气", "三气", "四气", "五气", "终气"};
        String[] qiColors = {"#90EE90", "#FF6B6B", "#FF8C00", "#DEB887", "#C0C0C0", "#87CEEB"};
        
        StringBuilder qiInfo = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (i == currentQiIndex) {
                qiInfo.append("<font color='#FFD700'>▶ ").append(qiNames[i]).append("</font> ");
                qiInfo.append("<font color='#90EE90'><b>").append(liuqi[i]).append("</b></font>");
            } else {
                qiInfo.append("<font color='#6B7B8A'>▫️ ").append(qiNames[i]).append("</font> ");
                qiInfo.append("<font color='").append(qiColors[i]).append("'>").append(liuqi[i]).append("</font>");
            }
            if (i < 5) qiInfo.append("<br/>");
        }
        liuqiInfo.setText(android.text.Html.fromHtml(qiInfo.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        StringBuilder qiDetail = new StringBuilder();
        String[][] qiDetails = {
            {"厥阴风木", "风气为主，主生发、疾病多风证", "#90EE90"},
            {"少阴君火", "热气为主，主温热、疾病多热证", "#FF6B6B"},
            {"少阳相火", "火气为主，主炎热、疾病多火证", "#FF8C00"},
            {"太阴湿土", "湿气为主，主湿润、疾病多湿证", "#DEB887"},
            {"阳明燥金", "燥气为主，主干燥、疾病多燥证", "#C0C0C0"},
            {"太阳寒水", "寒气为主，主寒冷、疾病多寒证", "#87CEEB"}
        };
        
        for (int i = 0; i < 6; i++) {
            qiDetail.append("<font color='").append(qiDetails[i][2]).append("'>").append(qiDetails[i][0]).append("</font>");
            qiDetail.append("<font color='#6B7B8A'>：").append(qiDetails[i][1]).append("</font>");
            if (i < 5) qiDetail.append("<br/>");
        }
        liuqiDetail.setText(android.text.Html.fromHtml(qiDetail.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
    }
    
    
    
    private String calculateYearGanZhi(int year) {
        int baseYear = 1900;
        int baseGanIndex = 6;
        int baseZhiIndex = 0;
        
        int yearDiff = year - baseYear;
        int ganIndex = (baseGanIndex + yearDiff) % 10;
        if (ganIndex < 0) ganIndex += 10;
        int zhiIndex = (baseZhiIndex + yearDiff) % 12;
        if (zhiIndex < 0) zhiIndex += 12;
        
        return TIANGAN[ganIndex] + DIZHI[zhiIndex];
    }
    
    private String getZhongYunShort(String yearGan) {
        switch (yearGan) {
            case "甲": case "己": return "土太过";
            case "乙": case "庚": return "金不及";
            case "丙": case "辛": return "水太过";
            case "丁": case "壬": return "木不及";
            case "戊": case "癸": return "火太过";
            default: return "土";
        }
    }
    
    private String[] getSijiYun(String yearGan) {
        String[] yuns = {"木", "火", "土", "金", "水"};
        int startIndex;
        
        switch (yearGan) {
            case "甲": case "己": startIndex = 2; break;
            case "乙": case "庚": startIndex = 3; break;
            case "丙": case "辛": startIndex = 4; break;
            case "丁": case "壬": startIndex = 0; break;
            case "戊": case "癸": startIndex = 1; break;
            default: startIndex = 2;
        }
        
        String[] result = new String[5];
        for (int i = 0; i < 5; i++) {
            result[i] = yuns[(startIndex + i) % 5];
        }
        return result;
    }
    
    private String[] getLiuqiForDate(int year, int month, int day) {
        String yearGanZhi = calculateYearGanZhi(year);
        String yearZhi = yearGanZhi.substring(1, 2);
        
        String[][] sanyangQi = {
            {"厥阴风木", "少阴君火", "少阳相火", "太阴湿土", "阳明燥金", "太阳寒水"},
            {"少阴君火", "太阴湿土", "少阳相火", "阳明燥金", "太阳寒水", "厥阴风木"},
            {"太阴湿土", "少阳相火", "阳明燥金", "太阳寒水", "厥阴风木", "少阴君火"},
            {"少阳相火", "阳明燥金", "太阳寒水", "厥阴风木", "少阴君火", "太阴湿土"},
            {"阳明燥金", "太阳寒水", "厥阴风木", "少阴君火", "太阴湿土", "少阳相火"},
            {"太阳寒水", "厥阴风木", "少阴君火", "太阴湿土", "少阳相火", "阳明燥金"}
        };
        
        int zhiIndex = java.util.Arrays.asList(DIZHI).indexOf(yearZhi);
        int qiIndex = (zhiIndex + 2) % 6;
        
        return sanyangQi[qiIndex];
    }
    
    private int getCurrentQiIndex(int month, int day) {
        if ((month == 1 && day >= 20) || (month == 2 && day <= 19)) {
            return 0;
        } else if ((month == 2 && day >= 20) || (month == 3 && day <= 20)) {
            return 1;
        } else if ((month == 3 && day >= 21) || (month == 4 && day <= 20)) {
            return 2;
        } else if ((month == 4 && day >= 21) || (month == 6 && day <= 21)) {
            return 3;
        } else if ((month == 6 && day >= 22) || (month == 8 && day <= 22)) {
            return 4;
        } else {
            return 5;
        }
    }
    
    private int getCurrentShichenIndex(int hour, int minute) {
        int adjustedHour = hour;
        if (minute >= 45) {
            adjustedHour = (hour + 1) % 24;
        }
        
        if (adjustedHour >= 23 || adjustedHour < 1) return 0;
        else if (adjustedHour >= 1 && adjustedHour < 3) return 1;
        else if (adjustedHour >= 3 && adjustedHour < 5) return 2;
        else if (adjustedHour >= 5 && adjustedHour < 7) return 3;
        else if (adjustedHour >= 7 && adjustedHour < 9) return 4;
        else if (adjustedHour >= 9 && adjustedHour < 11) return 5;
        else if (adjustedHour >= 11 && adjustedHour < 13) return 6;
        else if (adjustedHour >= 13 && adjustedHour < 15) return 7;
        else if (adjustedHour >= 15 && adjustedHour < 17) return 8;
        else if (adjustedHour >= 17 && adjustedHour < 19) return 9;
        else if (adjustedHour >= 19 && adjustedHour < 21) return 10;
        else return 11;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateHandler.removeCallbacks(updateRunnable);
        updateWuyunLiuqiInfo();
        long nextMinute = ((SystemClock.uptimeMillis() / 60000) + 1) * 60000;
        updateHandler.postAtTime(updateRunnable, nextMinute);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(updateRunnable);
    }
}
