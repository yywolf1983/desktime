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
        "宜：安睡养阳、打坐冥想\n忌：熬夜、饮酒、剧烈运动\n💡 子时阴气最盛，阳气初生，熟睡可养胆气，熬夜伤胆最甚",
        "宜：深睡养肝、放松肌肉\n忌：饮酒、熬夜、情绪激动\n💡 丑时肝经当令，肝脏排毒解毒，熟睡则肝血归藏，面色红润",
        "宜：熟睡静卧、深呼吸\n忌：剧烈运动、大声喧哗\n💡 寅时肺经当令，气血由静转动，深度呼吸助肺宣发肃降",
        "宜：排便、饮温水、伸展\n忌：忍便、空腹出门\n💡 卯时大肠经旺，起床饮温水促进排便，排出毒素一身轻",
        "宜：早餐营养、从容工作\n忌：空腹、不吃早餐\n💡 辰时胃经最旺，消化吸收力最强，早餐宜丰盛但不过饱",
        "宜：专注工作、学习思考\n忌：懒惰、分心、久坐不动\n💡 巳时脾经当令，脾主运化，此时学习工作效率最高",
        "宜：午休小憩、养心安神\n忌：劳累过度、情绪激动\n💡 午时心经当令，阳气最盛阴气初生，小睡15-30分钟养心最佳",
        "宜：放松休息、缓慢活动\n忌：剧烈运动、暴饮暴食\n💡 未时小肠经旺，分清泌浊吸收营养，宜慢节奏工作",
        "宜：运动锻炼、多饮水\n忌：憋尿、久坐不动\n💡 申时膀胱经最旺，适合运动排汗，多饮水助代谢排毒",
        "宜：静养收藏、泡脚补肾\n忌：操劳过度、剧烈运动\n💡 酉时肾经当令，肾藏精，宜静养收敛，泡脚助肾气升发",
        "宜：心情愉悦、轻松休闲\n忌：忧愁焦虑、过度思考\n💡 戌时心包经旺，心包护心，保持愉悦心情助心血管健康",
        "宜：泡脚安眠、放松身心\n忌：多虑、兴奋、熬夜\n💡 亥时三焦通百脉，全身气血归藏，准备入睡养精蓄锐"
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
            yearGanZhi = calculateYearGanZhi(year) + "年";
            monthGanZhi = calculateMonthGanZhi(year, month) + "月";
            dayGanZhi = calculateDayGanZhi(year, month, day) + "日";
            timeGanZhi = calculateTimeGanZhi(dayGanZhi.substring(0, 1), hour, minute) + "时";
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
            wuyunYearGanZhi.setText(yearGanZhi);
        }
        
        if (wuyunJieqi != null) {
            wuyunJieqi.setText(getJieqi(year, month, day));
        }
        
        if (wuyunMonthDay != null) {
            wuyunMonthDay.setText(monthGanZhi + "·" + dayGanZhi);
        }
        
        if (wuyunShichen != null) {
            wuyunShichen.setText(timeGanZhi);
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
            {"木", "生发生长、条达舒畅，主肝胆系统，宜疏肝理气", "#90EE90", "春季"},
            {"火", "炎热繁荣、向上明亮，主心小肠系统，宜清心降火", "#FF6B6B", "夏季"},
            {"土", "孕育稳定、敦厚承载，主脾胃系统，宜健脾养胃", "#DEB887", "长夏"},
            {"金", "收敛收获、肃杀刚强，主肺大肠系统，宜润肺生津", "#C0C0C0", "秋季"},
            {"水", "潜藏储备、流动滋润，主肾膀胱系统，宜温补肾阳", "#87CEEB", "冬季"}
        };
        
        String[][] wuyunYangsheng = {
            {"木运", "宜：疏肝理气、多食绿色蔬菜、户外散步\n忌：大怒、熬夜、酸味过度\n💡 木运之年肝气偏旺，春季宜早起散步、赏花踏青，舒畅情志"},
            {"火运", "宜：清心降火、多食红色食物、静心养神\n忌：烦躁、贪凉、辛辣过度\n💡 火运之年心气偏旺，夏季宜午休养心、清淡饮食，避免情绪过激"},
            {"土运", "宜：健脾养胃、多食黄色食物、规律饮食\n忌：思虑过度、生冷油腻\n💡 土运之年脾气偏旺，长夏宜喝粥慢走、细嚼慢咽，养护脾胃"},
            {"金运", "宜：润肺生津、多食白色食物、适度有氧\n忌：悲伤、过度劳累、辛辣\n💡 金运之年肺气偏旺，秋季宜登高远眺、深呼吸养肺，保持乐观"},
            {"水运", "宜：温补肾阳、多食黑色食物、早睡晚起\n忌：恐惧、寒凉、过度劳累\n💡 水运之年肾气偏旺，冬季宜早睡保暖、泡脚养肾，避免惊恐"}
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
        int currentQiIndex = getCurrentQiIndex(year, month, day);
        
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
            {"厥阴风木", "风气主令，万物生发疏泄，如春风化雨。<br/>对应时段：大寒至春分 · 地支寅卯 · 天干丁壬<br/><br/>⚠️ 易患：头痛眩晕、关节游走痛、肝气郁结、皮肤瘙痒<br/>✅ 养生：宜防风邪侵袭，疏肝理气，保持情绪舒畅，多户外散步<br/>🍃 饮食：多食芹菜、菠菜、茼蒿、薄荷等青色食物，少食酸味", "#90EE90"},
            {"少阴君火", "热气主令，气温回升温热明亮，万物繁茂。<br/>对应时段：春分至小满 · 地支巳午 · 天干戊癸<br/><br/>⚠️ 易患：发热心烦、口舌生疮、失眠多梦、心脏不适<br/>✅ 养生：宜清热降火，静心安神，少食辛辣，适当午休<br/>🍅 饮食：多食番茄、西瓜、绿豆、莲子等红色食物，忌过食热性食物", "#FF6B6B"},
            {"少阳相火", "火气主令，炎热躁动，暑气渐盛。<br/>对应时段：小满至大暑 · 地支巳午 · 天干戊癸<br/><br/>⚠️ 易患：目赤肿痛、咽喉肿痛、疮疡疖肿、情绪烦躁<br/>✅ 养生：宜清泻相火，饮食清淡，避免熬夜，保持心境平和<br/>🌶️ 饮食：多食苦瓜、苦菜、莲子心、绿茶等苦味食物，以苦泄火", "#FF8C00"},
            {"太阴湿土", "湿气主令，雨水充沛，湿润黏滞。<br/>对应时段：大暑至秋分 · 地支申酉 · 天干甲己<br/><br/>⚠️ 易患：腹胀腹泻、水肿困重、脾胃不适、皮肤湿疹<br/>✅ 养生：宜健脾祛湿，多食薏米山药，适度运动出汗排湿<br/>🌾 饮食：多食小米、南瓜、土豆、薏仁等黄色食物，忌生冷油腻", "#DEB887"},
            {"阳明燥金", "燥气主令，天气干燥，收敛肃降。<br/>对应时段：秋分至小雪 · 地支申酉 · 天干甲己<br/><br/>⚠️ 易患：干咳少痰、皮肤干燥、便秘、咽干口燥、呼吸道疾病<br/>✅ 养生：宜润肺生津，多食梨百合，保持室内湿润，早睡早起<br/>🍐 饮食：多食梨、白萝卜、银耳、蜂蜜等白色食物，忌辛辣烧烤", "#C0C0C0"},
            {"太阳寒水", "寒气主令，天寒地冻，寒冷凝滞。<br/>对应时段：小雪至大寒 · 地支亥子 · 天干丙辛<br/><br/>⚠️ 易患：感冒风寒、关节冷痛、畏寒肢冷、腰膝酸软<br/>✅ 养生：宜温阳散寒，多食温热食物，注意保暖，早睡晚起<br/>🫘 饮食：多食黑豆、核桃、羊肉、生姜等黑色温性食物，忌寒凉生冷", "#87CEEB"}
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
        detail.append("<font color='#90EE90'>木运</font> <font color='#8899AA'>— 主生发条达，对应春季，影响肝胆系统，宜疏肝理气</font><br/>");
        detail.append("<font color='#FF6B6B'>火运</font> <font color='#8899AA'>— 主炎热向上，对应夏季，影响心小肠系统，宜清心降火</font><br/>");
        detail.append("<font color='#DEB887'>土运</font> <font color='#8899AA'>— 主孕育承载，对应长夏，影响脾胃系统，宜健脾祛湿</font><br/>");
        detail.append("<font color='#C0C0C0'>金运</font> <font color='#8899AA'>— 主收敛肃降，对应秋季，影响肺大肠系统，宜润肺生津</font><br/>");
        detail.append("<font color='#87CEEB'>水运</font> <font color='#8899AA'>— 主潜藏滋润，对应冬季，影响肾膀胱系统，宜温补肾阳</font><br/><br/>");
        
        detail.append("<font color='#FFD700'><b>五运推算方法</b></font><br/>");
        detail.append("<font color='#8899AA'>年干决定五运：甲己化土，乙庚化金，丙辛化水，丁壬化木，戊癸化火。</font><br/>");
        detail.append("<font color='#8899AA'>阳干为太过（运气旺盛），阴干为不及（运气衰弱）。</font><br/><br/>");
        
        detail.append("<font color='#FFD700'><b>什么是六气？</b></font><br/>");
        detail.append("<font color='#90EE90'>厥阴风木</font> <font color='#8899AA'>— 风气主令，大寒至春分，万物生发</font><br/>");
        detail.append("<font color='#FF6B6B'>少阴君火</font> <font color='#8899AA'>— 热气主令，春分至小满，温热繁茂</font><br/>");
        detail.append("<font color='#FF8C00'>少阳相火</font> <font color='#8899AA'>— 火气主令，小满至大暑，暑热炎盛</font><br/>");
        detail.append("<font color='#DEB887'>太阴湿土</font> <font color='#8899AA'>— 湿气主令，大暑至秋分，湿润多雨</font><br/>");
        detail.append("<font color='#C0C0C0'>阳明燥金</font> <font color='#8899AA'>— 燥气主令，秋分至小雪，干燥收敛</font><br/>");
        detail.append("<font color='#87CEEB'>太阳寒水</font> <font color='#8899AA'>— 寒气主令，小雪至大寒，寒冷封藏</font><br/><br/>");
        
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
        detail.append("<font color='#8899AA'>五运六气揭示年度气候规律，人体应顺应自然变化：春养肝、夏养心、长夏养脾、秋养肺、冬养肾。根据当年运气特点，调整饮食起居，达到“天人合一”的养生境界。</font>");
        
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
    
    private int getCurrentQiIndex(int year, int month, int day) {
        String jieqi = JieqiData.getJieqi(year, month, day);
        int jieqiIndex = JieqiData.getJieqiIndex(jieqi);
        
        if (jieqiIndex == 23 || jieqiIndex == 0 || jieqiIndex == 1 || jieqiIndex == 2 || (jieqiIndex == 3 && !jieqi.equals("春分"))) {
            return 0;
        } else if (jieqiIndex == 3 || jieqiIndex == 4 || (jieqiIndex == 5 && !jieqi.equals("小满"))) {
            return 1;
        } else if (jieqiIndex == 5 || jieqiIndex == 6 || jieqiIndex == 7 || (jieqiIndex == 8 && !jieqi.equals("大暑"))) {
            return 2;
        } else if (jieqiIndex == 8 || jieqiIndex == 9 || jieqiIndex == 10 || jieqiIndex == 11 || (jieqiIndex == 12 && !jieqi.equals("秋分"))) {
            return 3;
        } else if (jieqiIndex == 12 || jieqiIndex == 13 || jieqiIndex == 14 || jieqiIndex == 15 || (jieqiIndex == 16 && !jieqi.equals("小雪"))) {
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
