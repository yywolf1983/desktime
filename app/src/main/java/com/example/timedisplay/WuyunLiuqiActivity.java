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
    private TextView wuyunYearGanZhi;
    private TextView wuyunJieqi;
    private TextView wuyunMonthDay;
    private TextView wuyunShichen;
    private TextView wuyunLiuqiDetail;
    
    private Calendar customCalendar = null;
    private Handler updateHandler;
    private Runnable updateRunnable;
    
    private String yearPillar = null;
    private String monthPillar = null;
    private String dayPillar = null;
    private String timePillar = null;
    
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
        
        loadCustomTime();
        initViews();
        setupUpdateHandler();
    }
    
    private void loadCustomTime() {
        if (getIntent().hasExtra("custom_year")) {
            customCalendar = Calendar.getInstance();
            customCalendar.set(Calendar.YEAR, getIntent().getIntExtra("custom_year", 0));
            customCalendar.set(Calendar.MONTH, getIntent().getIntExtra("custom_month", 1) - 1);
            customCalendar.set(Calendar.DAY_OF_MONTH, getIntent().getIntExtra("custom_day", 1));
            customCalendar.set(Calendar.HOUR_OF_DAY, getIntent().getIntExtra("custom_hour", 0));
            customCalendar.set(Calendar.MINUTE, getIntent().getIntExtra("custom_minute", 0));
            customCalendar.set(Calendar.SECOND, 0);
            customCalendar.set(Calendar.MILLISECOND, 0);
        }
        
        yearPillar = getIntent().getStringExtra("year_pillar");
        monthPillar = getIntent().getStringExtra("month_pillar");
        dayPillar = getIntent().getStringExtra("day_pillar");
        timePillar = getIntent().getStringExtra("time_pillar");
    }
    
    private void initViews() {
        wuyunLiuqiView = findViewById(R.id.wuyunLiuqiView);
        currentShichenInfo = findViewById(R.id.currentShichenInfo);
        wuyunInfo = findViewById(R.id.wuyunInfo);
        liuqiInfo = findViewById(R.id.liuqiInfo);
        liuqiDetail = findViewById(R.id.liuqiDetail);
        shichenDetail = findViewById(R.id.shichenDetail);
        wuyunYearGanZhi = findViewById(R.id.wuyunYearGanZhi);
        wuyunJieqi = findViewById(R.id.wuyunJieqi);
        wuyunMonthDay = findViewById(R.id.wuyunMonthDay);
        wuyunShichen = findViewById(R.id.wuyunShichen);
        wuyunLiuqiDetail = findViewById(R.id.wuyunLiuqiDetail);
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
        Calendar calendar = Calendar.getInstance();
        
        if (customCalendar != null) {
            calendar = (Calendar) customCalendar.clone();
        } else {
            calendar.setTime(new Date());
        }
        
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        String yearGanZhi = yearPillar;
        String monthGanZhi = monthPillar;
        String dayGanZhi = dayPillar;
        String timeGanZhi = timePillar;
        
        if (yearGanZhi == null || monthGanZhi == null || dayGanZhi == null || timeGanZhi == null) {
            yearGanZhi = calculateYearGanZhi(year);
            monthGanZhi = calculateMonthGanZhi(year, month);
            dayGanZhi = calculateDayGanZhi(year, month, day);
            timeGanZhi = calculateTimeGanZhi(dayGanZhi, hour, minute);
        }
        
        updateHeaderDisplay(yearGanZhi, monthGanZhi, dayGanZhi, timeGanZhi, year, month, day);
        
        int currentShichenIndex = getCurrentShichenIndex(hour, minute);
        
        wuyunLiuqiView.setCurrentShichen(currentShichenIndex);
        updateShichenInfo(currentShichenIndex);
        updateWuyunDisplay(yearGanZhi);
        updateLiuqiDisplay(year, month, day);
        updateWuyunLiuqiDetail(yearGanZhi);
    }
    
    private void updateHeaderDisplay(String yearGanZhi, String monthGanZhi, String dayGanZhi, String timeGanZhi, int year, int month, int day) {
        if (wuyunYearGanZhi != null) {
            wuyunYearGanZhi.setText(yearGanZhi + "年");
        }
        
        if (wuyunJieqi != null) {
            wuyunJieqi.setText(getJieqi(year, month, day));
        }
        
        if (wuyunMonthDay != null) {
            wuyunMonthDay.setText(monthGanZhi + "月·" + dayGanZhi + "日");
        }
        
        if (wuyunShichen != null) {
            wuyunShichen.setText(timeGanZhi + "时");
        }
    }
    
    private String getJieqi(int year, int month, int day) {
        int[] jieqiDays = {0, 20, 44, 62, 81, 101, 121, 141, 162, 182, 203, 223, 244, 264, 285, 305, 325, 345};
        String[] jieqiNames = {"小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", 
                               "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};
        
        int dayOfYear = getDayOfYear(year, month, day);
        
        for (int i = 0; i < jieqiDays.length; i++) {
            if (dayOfYear < jieqiDays[i]) {
                if (i == 0) return jieqiNames[jieqiNames.length - 1];
                return jieqiNames[i - 1];
            }
        }
        return jieqiNames[jieqiNames.length - 1];
    }
    
    private int getDayOfYear(int year, int month, int day) {
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
            daysInMonth[1] = 29;
        }
        
        int dayOfYear = day;
        for (int i = 0; i < month - 1; i++) {
            dayOfYear += daysInMonth[i];
        }
        return dayOfYear;
    }
    
    private String calculateMonthGanZhi(int year, int month) {
        int ganIndex = (year - 1900 + 6 + (month - 1) * 2) % 10;
        if (ganIndex < 0) ganIndex += 10;
        int zhiIndex = (month - 1) % 12;
        if (zhiIndex < 0) zhiIndex += 12;
        return TIANGAN[ganIndex] + DIZHI[zhiIndex];
    }
    
    private String calculateDayGanZhi(int year, int month, int day) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day);
            
            Calendar baseCalendar = Calendar.getInstance();
            baseCalendar.set(1900, 0, 1);
            
            long diff = calendar.getTimeInMillis() - baseCalendar.getTimeInMillis();
            diff = diff / (1000 * 60 * 60 * 24);
            
            int ganIndex = (6 + (int) diff) % 10;
            int zhiIndex = (0 + (int) diff) % 12;
            return TIANGAN[ganIndex] + DIZHI[zhiIndex];
        } catch (Exception e) {
            return "甲子";
        }
    }
    
    private String calculateTimeGanZhi(String dayGanZhi, int hour, int minute) {
        String dayGan = dayGanZhi.substring(0, 1);
        int baseIndex = java.util.Arrays.asList(TIANGAN).indexOf(dayGan);
        int shichenIndex = getCurrentShichenIndex(hour, minute);
        int ganIndex = (baseIndex * 2 + shichenIndex) % 10;
        int zhiIndex = shichenIndex % 12;
        return TIANGAN[ganIndex] + DIZHI[zhiIndex];
    }
    
    private void updateShichenInfo(int index) {
        StringBuilder info = new StringBuilder();
        info.append("<font color='#FFD700' size='+1'><b>").append(SHICHEN_NAMES[index]).append("时</b></font> ");
        info.append("<font color='#98D8F0'>(").append(SHICHEN_TIMES[index]).append(")</font><br/>");
        info.append("<font color='#87CEEB'>古名：").append(SHICHEN_QUOTES[index]).append("</font> · ");
        info.append("<font color='#FFA500'>五行：").append(WUXING_ZODIAC[index]).append("</font><br/>");
        info.append("<font color='#90EE90'>经络：").append(SHICHEN_ZANGFU[index]).append("经").append("</font> · ");
        info.append("<font color='#DDA0DD'>方位：").append(SHICHEN_FANGWEI[index]).append("</font><br/>");
        info.append("<font color='#FF8C00'>纳音：").append(WUXING_SHEJI[index]).append("</font> · ");
        info.append("<font color='#FFB84D'>物候：").append(SHICHEN_WUYIN[index]).append("</font>");
        if (currentShichenInfo != null) {
            currentShichenInfo.setText(android.text.Html.fromHtml(info.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        
        StringBuilder yiJi = new StringBuilder();
        String[] parts = SHICHEN_YIJI[index].split("\\n");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.startsWith("宜：")) {
                yiJi.append("<br/><font color='#90EE90'>✅ ").append(part).append("</font>");
            } else if (part.startsWith("忌：")) {
                yiJi.append("<br/><font color='#FF6B6B'>❌ ").append(part).append("</font>");
            } else if (part.startsWith("💡")) {
                yiJi.append("<br/><font color='#FFD700'>").append(part).append("</font>");
            } else {
                yiJi.append("<br/>").append(part);
            }
        }
        if (shichenDetail != null) {
            shichenDetail.setText(android.text.Html.fromHtml(yiJi.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
    }
    
    private void updateWuyunDisplay(String yearGanZhi) {
        String yearGan = yearGanZhi.substring(0, 1);
        String yearZhi = yearGanZhi.substring(1, 2);
        String zhongYun = getZhongYunShort(yearGan);
        String[] sijiYun = getSijiYun(yearGan);
        
        String[][] yunDetails = {
            {"木", "生发生长、条达舒畅、主肝胆", "#90EE90", "春季"},
            {"火", "炎热繁荣、向上明亮、主心小肠", "#FF6B6B", "夏季"},
            {"土", "孕育稳定、敦厚承载、主脾胃", "#DEB887", "长夏"},
            {"金", "收敛收获、肃杀刚强、主肺大肠", "#C0C0C0", "秋季"},
            {"水", "潜藏储备、流动滋润、主肾膀胱", "#87CEEB", "冬季"}
        };
        
        String[][] wuyunYangsheng = {
            {"木运", "宜：疏肝理气、多食绿色蔬菜\n忌：大怒、熬夜\n💡 建议：春季宜散步、赏花"},
            {"火运", "宜：清心降火、多食红色食物\n忌：烦躁、贪凉\n💡 建议：夏季宜午休、静心"},
            {"土运", "宜：健脾养胃、多食黄色食物\n忌：思虑过度、生冷\n💡 建议：长夏宜喝粥、慢走"},
            {"金运", "宜：润肺生津、多食白色食物\n忌：悲伤、过度劳累\n💡 建议：秋季宜登高、润肺"},
            {"水运", "宜：温补肾阳、多食黑色食物\n忌：恐惧、寒凉\n💡 建议：冬季宜早睡、保暖"}
        };
        
        StringBuilder info = new StringBuilder();
        info.append("<font color='#FFD700' size='+1'><b>").append(yearGanZhi).append("年</b></font><br/>");
        info.append("<font color='#87CEEB'>年干：").append(yearGan).append(" · ").append(getWuXing(yearGan)).append("</font> | ");
        info.append("<font color='#FFA500'>年支：").append(yearZhi).append(" · ").append(getWuXing(yearZhi)).append("</font><br/>");
        info.append("<font color='#90EE90'>中运：").append(zhongYun).append("</font><br/><br/>");
        
        info.append("<font color='#98D8F0'><b>五运分布：</b></font><br/>");
        String[] labels = {"初运", "二运", "三运", "四运", "终运"};
        String[] periods = {"1-2月", "3-4月", "5-6月", "7-8月", "9-12月"};
        for (int i = 0; i < 5; i++) {
            info.append("  <font color='#FFB84D'>").append(labels[i]).append("</font> ");
            info.append("<font color='").append(yunDetails[i][2]).append("'><b>").append(sijiYun[i]).append("运</b></font> ");
            info.append("<font color='#6B7B8A'>(").append(periods[i]).append(" · ").append(yunDetails[i][3]).append(")");
            info.append(" - ").append(yunDetails[i][1]).append("</font>");
            if (i < 4) info.append("<br/>");
        }
        info.append("<br/><br/><font color='#FFD700'><b>💡 养生要点：</b></font><br/>");
        info.append("<font color='").append(yunDetails[0][2]).append("'>").append(wuyunYangsheng[0][1]).append("</font>");
        if (wuyunInfo != null) {
            wuyunInfo.setText(android.text.Html.fromHtml(info.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
    }
    
    private void updateLiuqiDisplay(int year, int month, int day) {
        String[] liuqi = getLiuqiForDate(year, month, day);
        int currentQiIndex = getCurrentQiIndex(month, day);
        
        String[] qiNames = {"初气", "二气", "三气", "四气", "五气", "终气"};
        String[] qiColors = {"#90EE90", "#FF6B6B", "#FF8C00", "#DEB887", "#C0C0C0", "#87CEEB"};
        String[] qiPeriods = {"大寒-春分", "春分-小满", "小满-大暑", "大暑-秋分", "秋分-小雪", "小雪-大寒"};
        String[] qiEarthlyBranches = {"寅卯", "巳午", "巳午", "申酉", "申酉", "亥子"};
        String[] qiHeavenlyStems = {"丁壬", "戊癸", "戊癸", "甲己", "甲己", "丙辛"};
        
        StringBuilder qiInfo = new StringBuilder();
        qiInfo.append("<font color='#98D8F0'><b>六气分布：</b></font><br/>");
        for (int i = 0; i < 6; i++) {
            if (i == currentQiIndex) {
                qiInfo.append("<font color='#FFD700'>▶ ").append(qiNames[i]).append("</font> ");
                qiInfo.append("<font color='#90EE90'><b>").append(liuqi[i]).append("</b></font>");
                qiInfo.append("<font color='#6B7B8A'>(").append(qiPeriods[i]).append(")</font>");
            } else {
                qiInfo.append("<font color='#6B7B8A'>▫️ ").append(qiNames[i]).append("</font> ");
                qiInfo.append("<font color='").append(qiColors[i]).append("'>").append(liuqi[i]).append("</font>");
            }
            if (i < 5) qiInfo.append("<br/>");
        }
        if (liuqiInfo != null) {
            liuqiInfo.setText(android.text.Html.fromHtml(qiInfo.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        
        StringBuilder qiDetail = new StringBuilder();
        String[][] qiDetails = {
            {"厥阴风木", "风气为主，主生发疏泄。<br/>对应地支：寅卯 · 天干：丁壬<br/><br/>⚠️ 易患：头痛、眩晕、关节痛、肝胆疾病<br/>✅ 养生：宜防风邪，疏肝理气，保持情绪舒畅<br/>🍃 饮食：多食芹菜、菠菜、茼蒿等青色食物", "#90EE90"},
            {"少阴君火", "热气为主，主温热明亮。<br/>对应地支：巳午 · 天干：戊癸<br/><br/>⚠️ 易患：发热、心烦、口舌生疮、心脏疾病<br/>✅ 养生：宜清热降火，静心安神，少食辛辣<br/>🍅 饮食：多食番茄、西瓜、绿豆等红色食物", "#FF6B6B"},
            {"少阳相火", "火气为主，主炎热躁动。<br/>对应地支：巳午 · 天干：戊癸<br/><br/>⚠️ 易患：目赤肿痛、咽喉肿痛、疮疡、神志异常<br/>✅ 养生：宜清泻相火，饮食清淡，避免熬夜<br/>🌶️ 饮食：多食苦瓜、苦菜、莲子等苦味食物", "#FF8C00"},
            {"太阴湿土", "湿气为主，主湿润黏滞。<br/>对应地支：申酉 · 天干：甲己<br/><br/>⚠️ 易患：腹胀、腹泻、水肿、脾胃疾病、皮肤病<br/>✅ 养生：宜健脾祛湿，多食薏米、山药，适度运动<br/>🌾 饮食：多食小米、南瓜、土豆等黄色食物", "#DEB887"},
            {"阳明燥金", "燥气为主，主干燥收敛。<br/>对应地支：申酉 · 天干：甲己<br/><br/>⚠️ 易患：咳嗽、气喘、皮肤干燥、便秘、呼吸系统疾病<br/>✅ 养生：宜润肺生津，多食梨、百合，保持室内湿润<br/>🍐 饮食：多食梨、白萝卜、银耳等白色食物", "#C0C0C0"},
            {"太阳寒水", "寒气为主，主寒冷凝滞。<br/>对应地支：亥子 · 天干：丙辛<br/><br/>⚠️ 易患：感冒、关节冷痛、畏寒、肾脏疾病<br/>✅ 养生：宜温阳散寒，多食温热食物，注意保暖<br/>🫘 饮食：多食黑豆、核桃、羊肉等黑色食物", "#87CEEB"}
        };
        
        for (int i = 0; i < 6; i++) {
            qiDetail.append("<font color='").append(qiDetails[i][2]).append("' size='+1'><b>").append(qiDetails[i][0]).append("</b></font>");
            qiDetail.append("<font color='#6B7B8A'>").append(qiDetails[i][1]).append("</font>");
            if (i < 5) qiDetail.append("<br/><br/>");
        }
        if (liuqiDetail != null) {
            liuqiDetail.setText(android.text.Html.fromHtml(qiDetail.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
    }
    
    private void updateWuyunLiuqiDetail(String yearGanZhi) {
        String yearGan = yearGanZhi.substring(0, 1);
        String yearZhi = yearGanZhi.substring(1, 2);
        
        StringBuilder detail = new StringBuilder();
        
        detail.append("<font color='#FFD700'><b>五运六气概述</b></font><br/>");
        detail.append("<font color='#8899AA'>五运六气是中医运气学说的核心，以天干地支为基础，推演年度气候变化和人体健康影响。</font><br/><br/>");
        
        detail.append("<font color='#FFD700'><b>什么是五运？</b></font><br/>");
        detail.append("<font color='#90EE90'>木运</font> <font color='#8899AA'>— 主生发，对应春季，影响肝胆系统</font><br/>");
        detail.append("<font color='#FF6B6B'>火运</font> <font color='#8899AA'>— 主炎热，对应夏季，影响心小肠系统</font><br/>");
        detail.append("<font color='#DEB887'>土运</font> <font color='#8899AA'>— 主孕育，对应长夏，影响脾胃系统</font><br/>");
        detail.append("<font color='#C0C0C0'>金运</font> <font color='#8899AA'>— 主收敛，对应秋季，影响肺大肠系统</font><br/>");
        detail.append("<font color='#87CEEB'>水运</font> <font color='#8899AA'>— 主潜藏，对应冬季，影响肾膀胱系统</font><br/><br/>");
        
        detail.append("<font color='#FFD700'><b>五运推算方法</b></font><br/>");
        detail.append("<font color='#8899AA'>年干决定五运：甲己化土，乙庚化金，丙辛化水，丁壬化木，戊癸化火。</font><br/>");
        detail.append("<font color='#8899AA'>阳干为太过（运气旺盛），阴干为不及（运气衰弱）。</font><br/><br/>");
        
        detail.append("<font color='#FFD700'><b>什么是六气？</b></font><br/>");
        detail.append("<font color='#90EE90'>厥阴风木</font> <font color='#8899AA'>— 风气主令，大寒-春分</font><br/>");
        detail.append("<font color='#FF6B6B'>少阴君火</font> <font color='#8899AA'>— 热气主令，春分-小满</font><br/>");
        detail.append("<font color='#FF8C00'>少阳相火</font> <font color='#8899AA'>— 火气主令，小满-大暑</font><br/>");
        detail.append("<font color='#DEB887'>太阴湿土</font> <font color='#8899AA'>— 湿气主令，大暑-秋分</font><br/>");
        detail.append("<font color='#C0C0C0'>阳明燥金</font> <font color='#8899AA'>— 燥气主令，秋分-小雪</font><br/>");
        detail.append("<font color='#87CEEB'>太阳寒水</font> <font color='#8899AA'>— 寒气主令，小雪-大寒</font><br/><br/>");
        
        detail.append("<font color='#FFD700'><b>六气推算方法</b></font><br/>");
        detail.append("<font color='#8899AA'>年支决定六气：子午少阴君火，丑未太阴湿土，寅申少阳相火，卯酉阳明燥金，辰戌太阳寒水，巳亥厥阴风木。</font><br/><br/>");
        
        detail.append("<font color='#FFD700'><b>").append(yearGanZhi).append("年运气特点</b></font><br/>");
        String zhongYun = getZhongYunShort(yearGan);
        detail.append("<font color='#98D8F0'>年干").append(yearGan).append("：").append(zhongYun).append("</font><br/>");
        
        String[] liuyiNames = {"少阴君火", "太阴湿土", "少阳相火", "阳明燥金", "太阳寒水", "厥阴风木"};
        String[] liuyiYears = {"子", "丑", "寅", "申", "辰", "戌", "巳", "亥", "卯", "酉", "午", "未"};
        int liuyiIndex = -1;
        for (int i = 0; i < liuyiYears.length; i++) {
            if (liuyiYears[i].equals(yearZhi)) {
                liuyiIndex = i / 2;
                break;
            }
        }
        if (liuyiIndex >= 0) {
            detail.append("<font color='#98D8F0'>年支").append(yearZhi).append("：").append(liuyiNames[liuyiIndex]).append("司天</font><br/>");
        }
        
        detail.append("<br/><font color='#FFD700'><b>💡 养生原则</b></font><br/>");
        detail.append("<font color='#8899AA'>根据五运六气变化，调整饮食起居，顺应自然规律，达到防病养生的目的。</font>");
        
        if (wuyunLiuqiDetail != null) {
            wuyunLiuqiDetail.setText(android.text.Html.fromHtml(detail.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
    }
    
    
    private String calculateYearGanZhi(int year) {
        int baseYear = 1900;
        int baseIndex = 36;
        
        int yearDiff = year - baseYear;
        int yearIndex = (baseIndex + yearDiff) % 60;
        
        int ganIndex = yearIndex % 10;
        int zhiIndex = yearIndex % 12;
        
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
    
    private String getWuXing(String gan) {
        switch (gan) {
            case "甲": case "乙": return "木";
            case "丙": case "丁": return "火";
            case "戊": case "己": return "土";
            case "庚": case "辛": return "金";
            case "壬": case "癸": return "水";
            case "寅": case "卯": return "木";
            case "午": case "巳": return "火";
            case "辰": case "戌": case "丑": case "未": return "土";
            case "申": case "酉": return "金";
            case "子": case "亥": return "水";
            default: return "土";
        }
    }
    
    private String[] getSijiYun(String yearGan) {
        String[] yuns = {"木", "火", "土", "金", "水"};
        int centerIndex;
        
        switch (yearGan) {
            case "甲": case "己": centerIndex = 2; break;
            case "乙": case "庚": centerIndex = 3; break;
            case "丙": case "辛": centerIndex = 4; break;
            case "丁": case "壬": centerIndex = 0; break;
            case "戊": case "癸": centerIndex = 1; break;
            default: centerIndex = 2;
        }
        
        String[] result = new String[5];
        for (int i = 0; i < 5; i++) {
            result[i] = yuns[(centerIndex - 2 + i + 5) % 5];
        }
        return result;
    }
    
    private String[] getLiuqiForDate(int year, int month, int day) {
        String yearGanZhi = calculateYearGanZhi(year);
        String yearZhi = yearGanZhi.substring(1, 2);
        
        String[] qiOrder = {"厥阴风木", "少阴君火", "少阳相火", "太阴湿土", "阳明燥金", "太阳寒水"};
        
        int siTianIndex;
        switch (yearZhi) {
            case "子": case "午": siTianIndex = 1; break;
            case "丑": case "未": siTianIndex = 3; break;
            case "寅": case "申": siTianIndex = 2; break;
            case "卯": case "酉": siTianIndex = 4; break;
            case "辰": case "戌": siTianIndex = 5; break;
            case "巳": case "亥": siTianIndex = 0; break;
            default: siTianIndex = 0;
        }
        
        String[] result = new String[6];
        int zaiQuanIndex = (siTianIndex + 3) % 6;
        
        result[2] = qiOrder[siTianIndex];
        result[5] = qiOrder[zaiQuanIndex];
        
        int chuQiIndex = (siTianIndex - 2 + 6) % 6;
        result[0] = qiOrder[chuQiIndex];
        result[1] = qiOrder[(chuQiIndex + 1) % 6];
        result[3] = qiOrder[(siTianIndex + 1) % 6];
        result[4] = qiOrder[(siTianIndex + 2) % 6];
        
        return result;
    }
    
    private int getCurrentQiIndex(int month, int day) {
        if ((month == 1 && day >= 20) || (month == 2) || (month == 3 && day <= 20)) {
            return 0;
        } else if ((month == 3 && day >= 21) || (month == 4) || (month == 5 && day <= 20)) {
            return 1;
        } else if ((month == 5 && day >= 21) || (month == 6) || (month == 7 && day <= 22)) {
            return 2;
        } else if ((month == 7 && day >= 23) || (month == 8) || (month == 9 && day <= 22)) {
            return 3;
        } else if ((month == 9 && day >= 23) || (month == 10) || (month == 11 && day <= 21)) {
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
