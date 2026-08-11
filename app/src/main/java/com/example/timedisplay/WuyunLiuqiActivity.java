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
    private TextView wuyunInfo, wuyunSummary;
    private TextView liuqiInfo;
    private TextView liuqiDetail;
    private TextView shichenDetail;
    private TextView wuyunYearGanZhi;
    private TextView wuyunJieqi;
    private TextView wuyunMonthDay;
    private TextView wuyunShichen;
    private TextView wuyunLiuqiDetail;
    private TextView wuyunLiuqiYunshi;
    private TextView yunqiShichenRelation;
    
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
        "宜：安睡养阳、打坐冥想\n忌：熬夜、饮酒、剧烈运动",
        "宜：深睡养肝、放松肌肉\n忌：饮酒、熬夜、情绪激动",
        "宜：熟睡静卧、深呼吸\n忌：剧烈运动、大声喧哗",
        "宜：排便、饮温水、伸展\n忌：忍便、空腹出门",
        "宜：早餐营养、从容工作\n忌：空腹、不吃早餐",
        "宜：专注工作、学习思考\n忌：懒惰、分心、久坐",
        "宜：午休小憩、养心安神\n忌：劳累过度、情绪激动",
        "宜：放松休息、缓慢活动\n忌：剧烈运动、暴饮暴食",
        "宜：运动锻炼、多饮水\n忌：憋尿、久坐",
        "宜：静养收藏、泡脚补肾\n忌：操劳过度、剧烈运动",
        "宜：心情愉悦、轻松休闲\n忌：忧愁焦虑、过度思考",
        "宜：泡脚安眠、放松身心\n忌：多虑、兴奋、熬夜"
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

    private static final String[] LIUYI_NAMES = {
        "厥阴风木", "少阴君火", "少阳相火", "太阴湿土", "阳明燥金", "太阳寒水"
    };

    private static final String COLOR_GOLD = "#E6C46A";
    private static final String COLOR_LIGHT_BLUE = "#98D8F0";
    private static final String COLOR_GRAY = "#7C8C9C";
    private static final String COLOR_JI = "#3FA34D";
    private static final String COLOR_XIONG = "#E0593B";
    private static final String COLOR_PING = "#E6C46A";
    private static final String COLOR_EARTH = "#D9A441";
    private static final String COLOR_METAL = "#9AA7B8";
    private static final String COLOR_WATER = "#3E87C2";
    private static final String COLOR_PERIOD = "#7C8C9C";
    private static final String COLOR_MUTED = "#8A93A3";

    private static class WuxingInfo {
        final String element;
        final String organ;
        final String season;
        final String foodColor;
        final String htmlColor;
        final String advice;
        final String avoid;
        final String adviceSuffix;
        WuxingInfo(String element, String organ, String season, String foodColor,
                   String htmlColor, String advice, String avoid, String adviceSuffix) {
            this.element = element; this.organ = organ; this.season = season;
            this.foodColor = foodColor; this.htmlColor = htmlColor;
            this.advice = advice; this.avoid = avoid; this.adviceSuffix = adviceSuffix;
        }
    }

    private static final WuxingInfo[] WUXING_INFO = {
        new WuxingInfo("木", "肝胆", "春", "青色", "#3FA34D", "疏肝理气", "忌大怒、熬夜、酸味过度", "保持心情舒畅"),
        new WuxingInfo("火", "心小肠", "夏", "红色", "#E0593B", "清心降火", "忌烦躁、贪凉、辛辣过度", "静心安神"),
        new WuxingInfo("土", "脾胃", "长夏", "黄色", "#D9A441", "健脾养胃", "忌思虑过度、生冷油腻", "规律饮食"),
        new WuxingInfo("金", "肺大肠", "秋", "白色", "#9AA7B8", "润肺生津", "忌悲伤、过度劳累、辛辣", "保持室内湿润"),
        new WuxingInfo("水", "肾膀胱", "冬", "黑色", "#3E87C2", "补肾温阳", "忌恐惧、寒凉、过度劳累", "注意保暖")
    };

    private static final String STATIC_TEACHING =
        "<font color='#E6C46A'><b>五运</b></font> <font color='#7C8C9C'>（年干定运，阳干太过·阴干不及）：</font><br/>" +
        "<font color='#3FA34D'>木</font><font color='#7C8C9C'>生发·肝胆</font> · " +
        "<font color='#E0593B'>火</font><font color='#7C8C9C'>炎热·心小肠</font> · " +
        "<font color='#D9A441'>土</font><font color='#7C8C9C'>承载·脾胃</font> · " +
        "<font color='#9AA7B8'>金</font><font color='#7C8C9C'>收敛·肺大肠</font> · " +
        "<font color='#3E87C2'>水</font><font color='#7C8C9C'>封藏·肾膀胱</font><br/>" +
        "<font color='#7C8C9C'>口诀：甲己土、乙庚金、丙辛水、丁壬木、戊癸火</font><br/><br/>" +
        "<font color='#E6C46A'><b>六气</b></font> <font color='#7C8C9C'>（年支定气）：</font><br/>" +
        "<font color='#3FA34D'>厥阴风木</font><font color='#7C8C9C'>大寒-春分</font> · " +
        "<font color='#E0593B'>少阴君火</font><font color='#7C8C9C'>春分-小满</font> · " +
        "<font color='#F3BA66'>少阳相火</font><font color='#7C8C9C'>小满-大暑</font><br/>" +
        "<font color='#D9A441'>太阴湿土</font><font color='#7C8C9C'>大暑-秋分</font> · " +
        "<font color='#9AA7B8'>阳明燥金</font><font color='#7C8C9C'>秋分-小雪</font> · " +
        "<font color='#3E87C2'>太阳寒水</font><font color='#7C8C9C'>小雪-大寒</font><br/>" +
        "<font color='#7C8C9C'>口诀：子午君火、丑未湿土、寅申相火、卯酉燥金、辰戌寒水、巳亥风木</font><br/><br/>";

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
        wuyunSummary = findViewById(R.id.wuyunSummary);

        liuqiInfo = findViewById(R.id.liuqiInfo);
        liuqiDetail = findViewById(R.id.liuqiDetail);
        shichenDetail = findViewById(R.id.shichenDetail);
        wuyunYearGanZhi = findViewById(R.id.wuyunYearGanZhi);
        wuyunJieqi = findViewById(R.id.wuyunJieqi);
        wuyunMonthDay = findViewById(R.id.wuyunMonthDay);
        wuyunShichen = findViewById(R.id.wuyunShichen);
        wuyunLiuqiDetail = findViewById(R.id.wuyunLiuqiDetail);
        wuyunLiuqiYunshi = findViewById(R.id.wuyunLiuqiYunshi);
        yunqiShichenRelation = findViewById(R.id.yunqiShichenRelation);
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
            
            int calcYear = year;
            int calcMonth = month;
            int calcDay = day;
            if (hour >= 23) {
                calcDay++;
                if (calcDay > getDaysInMonth(calcYear, calcMonth)) {
                    calcDay = 1;
                    calcMonth++;
                    if (calcMonth > 12) {
                        calcMonth = 1;
                        calcYear++;
                    }
                }
            }
            dayGanZhi = calculateDayGanZhi(calcYear, calcMonth, calcDay);
            timeGanZhi = calculateTimeGanZhi(dayGanZhi.substring(0, 1), hour, minute);
        }
        
        updateHeaderDisplay(yearGanZhi, monthGanZhi, dayGanZhi, timeGanZhi, year, month, day);
        
        int currentShichenIndex = getCurrentShichenIndex(hour, minute);
        
        wuyunLiuqiView.setCurrentShichen(currentShichenIndex);
        updateShichenInfo(currentShichenIndex);
        updateWuyunDisplay(yearGanZhi);
        updateLiuqiDisplay(year, month, day);
        updateWuyunLiuqiDetail(yearGanZhi);
        updateWuyunLiuqiYunshi(yearGanZhi);
    }
    
    private void updateHeaderDisplay(String yearGanZhi, String monthGanZhi, String dayGanZhi, String timeGanZhi, int year, int month, int day) {
        if (wuyunYearGanZhi != null) {
            wuyunYearGanZhi.setText(yearGanZhi);
        }
        
        if (wuyunJieqi != null) {
            wuyunJieqi.setText(JieqiData.getJieqi(year, month, day));
        }
        
        if (wuyunMonthDay != null) {
            wuyunMonthDay.setText(monthGanZhi + "·" + dayGanZhi);
        }
        
        if (wuyunShichen != null) {
            wuyunShichen.setText(timeGanZhi);
        }
    }
    
    private String calculateMonthGanZhi(int year, int month) {
        int calcYear = year;
        int calcMonth = month;
        
        if (month < 2 || (month == 2 && getDayOfYear(year, month, 1) < getJieqiDayOfYear(year, 0))) {
            calcYear = year - 1;
        }
        
        String yearPillar = calculateYearGanZhi(calcYear);
        String yearGan = yearPillar.substring(0, 1);
        
        java.util.HashMap<String, String> wuhudun = new java.util.HashMap<>();
        wuhudun.put("甲", "丙"); wuhudun.put("己", "丙");
        wuhudun.put("乙", "戊"); wuhudun.put("庚", "戊");
        wuhudun.put("丙", "庚"); wuhudun.put("辛", "庚");
        wuhudun.put("丁", "壬"); wuhudun.put("壬", "壬");
        wuhudun.put("戊", "甲"); wuhudun.put("癸", "甲");
        
        String yinMonthGan = wuhudun.get(yearGan);
        if (yinMonthGan == null) yinMonthGan = "丙";
        int yinGanIndex = java.util.Arrays.asList(TIANGAN).indexOf(yinMonthGan);
        
        String monthZhi = getMonthZhiForYear(year, month, 1);
        String[] monthZhiList = {"寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑"};
        int monthZhiIndex = java.util.Arrays.asList(monthZhiList).indexOf(monthZhi);
        int monthGanIndex = (yinGanIndex + monthZhiIndex) % 10;
        
        return TIANGAN[monthGanIndex] + monthZhi;
    }
    
    private String getMonthZhiForYear(int year, int month, int day) {
        if (month == 1 && day < 6) return "子";
        if (month == 1 && day >= 6) return "丑";
        if (month == 2 && day < 4) return "丑";
        if (month == 2 && day >= 4) return "寅";
        
        String[] monthZhiList = {"寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑"};
        return monthZhiList[(month - 1) % 12];
    }
    
    private int getDayOfYear(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }
    
    private int getJieqiDayOfYear(int year, int jieqiIndex) {
        int[] jieqiDays = {4, 19, 6, 21, 5, 21, 6, 22, 8, 23, 8, 23, 8, 23, 8, 23, 8, 24, 7, 23, 7, 22, 5, 20};
        int dayOfYear = 0;
        for (int i = 0; i < jieqiIndex; i++) {
            if (i < 11) {
                dayOfYear += (i % 2 == 0) ? 31 : 28;
            } else {
                int m = (i / 2) + 6;
                int daysInMonth = (m == 7 || m == 8 || m == 10 || m == 12) ? 31 :
                                  (m == 9 || m == 11) ? 30 : 28;
                dayOfYear += daysInMonth;
            }
        }
        return dayOfYear + jieqiDays[jieqiIndex];
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
        
        int shichenIndex = getCurrentShichenIndex(hour, minute);
        
        String[] wuShuDun = {"甲", "丙", "戊", "庚", "壬"};
        int baseIndex;
        switch (dayGan) {
            case "甲": case "己": baseIndex = 0; break;
            case "乙": case "庚": baseIndex = 1; break;
            case "丙": case "辛": baseIndex = 2; break;
            case "丁": case "壬": baseIndex = 3; break;
            case "戊": case "癸": baseIndex = 4; break;
            default: baseIndex = 0;
        }
        
        int ganIndex = (baseIndex + shichenIndex * 2) % 10;
        int zhiIndex = shichenIndex % 12;
        return TIANGAN[ganIndex] + DIZHI[zhiIndex];
    }
    
    private void updateShichenInfo(int index) {
        StringBuilder info = new StringBuilder();
        info.append("<font color='" + COLOR_GOLD + "' size='+1'><b>").append(SHICHEN_NAMES[index]).append("</b></font> ");
        info.append("<font color='" + COLOR_LIGHT_BLUE + "'>(").append(SHICHEN_TIMES[index]).append(")</font><br/>");
        info.append("<font color='" + COLOR_WATER + "'>古名：").append(SHICHEN_QUOTES[index]).append("</font> · ");
        info.append("<font color='#FFA500'>五行：").append(WUXING_ZODIAC[index]).append("</font><br/>");
        info.append("<font color='" + COLOR_JI + "'>经络：").append(SHICHEN_ZANGFU[index]).append("经").append("</font> · ");
        info.append("<font color='#DDA0DD'>方位：").append(SHICHEN_FANGWEI[index]).append("</font><br/>");
        info.append("<font color='#F3BA66'>纳音：").append(WUXING_SHEJI[index]).append("</font> · ");
        info.append("<font color='#FFB84D'>物候：").append(SHICHEN_WUYIN[index]).append("</font>");
        if (currentShichenInfo != null) {
            currentShichenInfo.setText(android.text.Html.fromHtml(info.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }

        StringBuilder yiJi = new StringBuilder();
        String[] parts = SHICHEN_YIJI[index].split("\\n");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.startsWith("宜：")) {
                yiJi.append("<br/><font color='" + COLOR_JI + "'>").append(part).append("</font>");
            } else if (part.startsWith("忌：")) {
                yiJi.append("<br/><font color='" + COLOR_XIONG + "'>").append(part).append("</font>");
            } else {
                yiJi.append("<br/><font color='" + COLOR_GOLD + "'>").append(part).append("</font>");
            }
        }
        if (shichenDetail != null) {
            shichenDetail.setText(android.text.Html.fromHtml(yiJi.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
    }
    
    private void updateWuyunDisplay(String yearGanZhi) {
        Calendar calendar = Calendar.getInstance();
        if (customCalendar != null) {
            calendar = (Calendar) customCalendar.clone();
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        String yearGan = yearGanZhi.substring(0, 1);
        String yearZhi = yearGanZhi.substring(1, 2);
        String zhongYun = getZhongYunShort(yearGan);
        String[] sijiYun = getSijiYun(yearGan);
        
        String[][] yunDetails = {
            {"木", "主生发条达，对应肝胆，宜疏肝理气", COLOR_JI, "春季", "大寒-春分"},
            {"火", "主炎热向上，对应心小肠，宜清心降火", COLOR_XIONG, "夏季", "春分-芒种"},
            {"土", "主承载化生，对应脾胃，宜健脾养胃", COLOR_EARTH, "长夏", "芒种-处暑"},
            {"金", "主收敛肃杀，对应肺大肠，宜润肺生津", COLOR_METAL, "秋季", "处暑-立冬"},
            {"水", "主寒冷封藏，对应肾膀胱，宜温补肾阳", COLOR_WATER, "冬季", "立冬-大寒"}
        };
        
        String[][] wuyunYangsheng = {
            {"木运", "宜：疏肝理气、绿菜、散步\n忌：大怒、熬夜、酸食"},
            {"火运", "宜：清心降火、红食、静心\n忌：烦躁、贪凉、辛辣"},
            {"土运", "宜：健脾养胃、黄食、规律\n忌：思虑、生冷、油腻"},
            {"金运", "宜：润肺生津、白食、有氧\n忌：悲伤、劳累、辛辣"},
            {"水运", "宜：温补肾阳、黑食、早睡\n忌：恐惧、寒凉、劳累"}
        };
        
        int currentYunIndex = getCurrentYunIndex(year, month, day);
        String currentYunElement = sijiYun[currentYunIndex];
        int detailIndex = getWuXingIndex(currentYunElement);
        
        StringBuilder info = new StringBuilder();
        info.append("<font color='" + COLOR_GOLD + "' size='+1'><b>").append(yearGanZhi).append("年</b></font><br/>");
        info.append("<font color='" + COLOR_WATER + "'>年干：").append(yearGan).append(" · ").append(getWuXing(yearGan)).append("</font> | ");
        info.append("<font color='#FFA500'>年支：").append(yearZhi).append(" · ").append(getWuXing(yearZhi)).append("</font><br/>");
        info.append("<font color='" + COLOR_JI + "'>中运：").append(zhongYun).append("</font><br/><br/>");

        info.append("<font color='" + COLOR_LIGHT_BLUE + "'><b>五运分布：</b></font><br/>");
        String[] labels = {"初运", "二运", "三运", "四运", "终运"};
        String[] periods = {"大寒-春分", "春分-芒种", "芒种-处暑", "处暑-立冬", "立冬-大寒"};
        for (int i = 0; i < 5; i++) {
            int elemIndex = getWuXingIndex(sijiYun[i]);
            if (i == currentYunIndex) {
                info.append("  <font color='" + COLOR_GOLD + "'>▶ ").append(labels[i]).append("</font> ");
                info.append("<font color='").append(yunDetails[elemIndex][2]).append("'><b>").append(sijiYun[i]).append("运</b></font> ");
                info.append("<font color='" + COLOR_PERIOD + "'>(").append(periods[i]).append(" · ").append(yunDetails[elemIndex][3]).append(")");
                info.append(" - <font color='").append(yunDetails[elemIndex][2]).append("'>").append(yunDetails[elemIndex][1]).append("</font>");
            } else {
                info.append("  <font color='" + COLOR_MUTED + "'>").append(labels[i]).append("</font> ");
                info.append("<font color='" + COLOR_MUTED + "'>").append(sijiYun[i]).append("运").append("</font>");
                info.append("<font color='" + COLOR_MUTED + "'>(").append(periods[i]).append(")</font>");
            }
            if (i < 4) info.append("<br/>");
        }
        info.append("<br/><br/><font color='" + COLOR_GOLD + "'><b>当前").append(yunDetails[detailIndex][3]).append(sijiYun[currentYunIndex]).append("运养生要点：</b></font><br/>");
        info.append("<font color='").append(yunDetails[detailIndex][2]).append("'>").append(wuyunYangsheng[detailIndex][1]).append("</font>");
        if (wuyunInfo != null) {
            wuyunInfo.setText(android.text.Html.fromHtml(info.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        
        if (wuyunSummary != null && yearGanZhi != null) {
            wuyunSummary.setText(getWuyunLiuqiSummary(yearGanZhi));
        }
        
        updateYunqiShichenRelation(yearGanZhi, year, month, day);
    }
    
    private int getCurrentYunIndex(int year, int month, int day) {
        String jieqi = JieqiData.getJieqi(year, month, day);
        int jieqiIndex = JieqiData.getJieqiIndex(jieqi);
        
        if (jieqiIndex == 23 || jieqiIndex <= 2) return 0;
        else if (jieqiIndex <= 7) return 1;
        else if (jieqiIndex <= 12) return 2;
        else if (jieqiIndex <= 17) return 3;
        else return 4;
    }
    
    private void updateYunqiShichenRelation(String yearGanZhi, int year, int month, int day) {
        if (yunqiShichenRelation == null) return;
        
        String yearGan = yearGanZhi.substring(0, 1);
        String yearZhi = yearGanZhi.substring(1, 2);
        
        String zhongYun = getZhongYunShort(yearGan);
        int siTianIndex = getSiTianIndex(yearZhi);
        String siTian = LIUYI_NAMES[siTianIndex];
        
        int currentQiIndex = getCurrentQiIndex(year, month, day);
        String currentQi = LIUYI_NAMES[currentQiIndex];
        
        StringBuilder relation = new StringBuilder();
        relation.append("<font color='" + COLOR_GOLD + "'><b>").append(yearGanZhi).append("年 · ").append(zhongYun).append(" · ").append(siTian).append("</b></font><br/><br/>");

        relation.append("<font color='" + COLOR_LIGHT_BLUE + "'><b>当前六气：").append(currentQi).append("</b></font><br/><br/>");

        relation.append("<font color='" + COLOR_LIGHT_BLUE + "'><b>运气与时辰关系：</b></font><br/>");
        
        String[][] yunqiShichen = {
            {"木运", "厥阴风木", "厥阴风木（丑、戌时）：丑时肝经排毒，戌时心包护心", COLOR_JI},
            {"火运", "少阴君火", "少阴君火（午、酉时）：午时心经宜午休，酉时肾经宜静养", COLOR_XIONG},
            {"火运", "少阳相火", "少阳相火（子、亥时）：子时胆经主决断，亥时三焦通百脉宜眠", "#F3BA66"},
            {"土运", "太阴湿土", "太阴湿土（寅、巳时）：寅时肺经宜呼吸，巳时脾经主运化", COLOR_EARTH},
            {"金运", "阳明燥金", "阳明燥金（卯、辰时）：卯时大肠宜排便，辰时胃经宜早餐", COLOR_METAL},
            {"水运", "太阳寒水", "太阳寒水（未、申时）：未时小肠宜休息，申时膀胱宜运动", COLOR_WATER}
        };
        
        for (String[] item : yunqiShichen) {
            if (zhongYun.contains(item[0]) || siTian.contains(item[1]) || currentQi.contains(item[1])) {
                relation.append("<font color='").append(item[3]).append("'>").append(item[2]).append("</font><br/>");
            }
        }
        
        relation.append("<br/><font color='" + COLOR_GOLD + "'><b>当前").append(getSeasonName(month)).append("养生策略：</b></font><br/>");
        relation.append("<font color='" + COLOR_GRAY + "'>调养：</font><br/>");
        WuxingInfo strategyInfo = getWuxingInfo(zhongYun);
        relation.append("<font color='" + COLOR_JI + "'>顺应时令：").append(zhongYun).append("之年，").append(currentQi).append("主令</font><br/>");
        relation.append(formatYiJi(strategyInfo.advice + "，" + strategyInfo.adviceSuffix, strategyInfo.avoid.substring(1), strategyInfo.foodColor + "食物")).append("<br/>");
        relation.append("<font color='" + COLOR_GOLD + "'>按时而养：</font><font color='" + COLOR_GRAY + "'>十二时辰各有经络当令，循时而作、按时作息即合天时。</font>");
        
        yunqiShichenRelation.setText(android.text.Html.fromHtml(relation.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
    }
    
    private String getSeasonName(int month) {
        if (month >= 3 && month <= 5) return "春季";
        if (month >= 6 && month <= 8) return "夏季";
        if (month >= 9 && month <= 11) return "秋季";
        return "冬季";
    }
    
    private String formatYiJi(String yi, String ji, String food) {
        return "<font color='" + COLOR_JI + "'>宜：</font>" + yi + "、多食" + food + "<br/>" +
               "<font color='" + COLOR_XIONG + "'>忌：</font>" + ji;
    }

    private WuxingInfo getWuxingInfo(String wuxingStr) {
        for (WuxingInfo info : WUXING_INFO) {
            if (wuxingStr.contains(info.element)) return info;
        }
        return null;
    }
    
    private void updateLiuqiDisplay(int year, int month, int day) {
        String[] liuqi = getLiuqiForDate(year, month, day);
        int currentQiIndex = getCurrentQiIndex(year, month, day);
        
        String[] qiNames = {"初气", "二气", "三气", "四气", "五气", "终气"};
        String[] qiColors = {COLOR_JI, COLOR_XIONG, "#F3BA66", COLOR_EARTH, COLOR_METAL, COLOR_WATER};
        String[] qiPeriods = {"大寒-春分", "春分-小满", "小满-大暑", "大暑-秋分", "秋分-小雪", "小雪-大寒"};
        String[] qiEarthlyBranches = {"寅卯", "巳午", "巳午", "申酉", "申酉", "亥子"};
        String[] qiHeavenlyStems = {"丁壬", "戊癸", "戊癸", "甲己", "甲己", "丙辛"};
        
        StringBuilder qiInfo = new StringBuilder();
        qiInfo.append("<font color='" + COLOR_LIGHT_BLUE + "'><b>六气分布：</b></font><br/>");
        for (int i = 0; i < 6; i++) {
            if (i == currentQiIndex) {
                qiInfo.append("<font color='" + COLOR_GOLD + "'>▶ ").append(qiNames[i]).append("</font> ");
                qiInfo.append("<font color='").append(qiColors[i]).append("'><b>").append(liuqi[i]).append("</b></font>");
                qiInfo.append("<font color='" + COLOR_PERIOD + "'>(").append(qiPeriods[i]).append(")</font>");
            } else {
                qiInfo.append("<font color='" + COLOR_MUTED + "'>").append(qiNames[i]).append("</font> ");
                qiInfo.append("<font color='" + COLOR_MUTED + "'>").append(liuqi[i]).append("</font>");
            }
            if (i < 5) qiInfo.append("<br/>");
        }
        if (liuqiInfo != null) {
            liuqiInfo.setText(android.text.Html.fromHtml(qiInfo.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        
        StringBuilder qiDetail = new StringBuilder();
        String[][] qiDetails = {
            {"厥阴风木", "风气主令，生发疏泄（大寒-春分，寅卯，丁壬）。<br/>易头痛眩晕、关节痛、肝郁。<br/>防风疏肝，多散步；食青色，少酸味", COLOR_JI},
            {"少阴君火", "热气主令，温热明亮（春分-小满，巳午，戊癸）。<br/>易发热心烦、口疮、失眠。<br/>清热降火，宜午休；食红色，忌热性", COLOR_XIONG},
            {"少阳相火", "火气主令，炎热躁动（小满-大暑，巳午，戊癸）。<br/>易目赤咽痛、疮疡烦躁。<br/>清泻相火，忌熬夜；食苦味泄火", "#F3BA66"},
            {"太阴湿土", "湿气主令，湿润黏滞（大暑-秋分，申酉，甲己）。<br/>易腹胀泻痢、水肿湿疹。<br/>健脾祛湿，宜运动；食黄色，忌生冷", COLOR_EARTH},
            {"阳明燥金", "燥气主令，干燥收敛（秋分-小雪，申酉，甲己）。<br/>易干咳、肤燥便秘咽干。<br/>润肺生津，保湿；食白色，忌辛辣", COLOR_METAL},
            {"太阳寒水", "寒气主令，寒冷凝滞（小雪-大寒，亥子，丙辛）。<br/>易感冒风寒、关节冷痛畏寒。<br/>温阳散寒，早睡保暖；食黑色温性，忌寒凉", COLOR_WATER}
        };
        
        qiDetail.append("<font color='" + COLOR_GOLD + "'><b>当前六气详解：</b></font><br/><br/>");
        qiDetail.append("<font color='").append(qiDetails[currentQiIndex][2]).append("' size='+1'><b>").append(qiDetails[currentQiIndex][0]).append("</b></font>");
        qiDetail.append("<font color='" + COLOR_PERIOD + "'>").append(qiDetails[currentQiIndex][1]).append("</font>");
        
        qiDetail.append("<br/><br/><font color='" + COLOR_MUTED + "'><b>其他六气（非当前时段）：</b></font><br/>");
        for (int i = 0; i < 6; i++) {
            if (i != currentQiIndex) {
                qiDetail.append("<font color='" + COLOR_MUTED + "'>").append(qiDetails[i][0]).append("</font>");
                if (i < 5 && i != currentQiIndex - 1) qiDetail.append(" · ");
            }
        }
        if (liuqiDetail != null) {
            liuqiDetail.setText(android.text.Html.fromHtml(qiDetail.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
    }
    
    private void updateWuyunLiuqiDetail(String yearGanZhi) {
        String yearGan = yearGanZhi.substring(0, 1);
        String yearZhi = yearGanZhi.substring(1, 2);

        String zhongYun = getZhongYunShort(yearGan);
        int siTianIndex = getSiTianIndex(yearZhi);

        StringBuilder detail = new StringBuilder();
        detail.append(STATIC_TEACHING);

        detail.append("<font color='" + COLOR_GOLD + "'><b>").append(yearGanZhi).append("年运气特点</b></font><br/>");
        detail.append("<font color='" + COLOR_LIGHT_BLUE + "'>年干").append(yearGan).append("：").append(zhongYun).append("</font><br/>");
        detail.append("<font color='" + COLOR_LIGHT_BLUE + "'>年支").append(yearZhi).append("：").append(LIUYI_NAMES[siTianIndex]).append("司天</font><br/>");

        detail.append("<br/><font color='" + COLOR_GOLD + "'><b>养生原则</b></font><br/>");
        detail.append("<font color='" + COLOR_GRAY + "'>春养肝、夏养心、长夏养脾、秋养肺、冬养肾，顺时调摄以合天人。当年运气太过不及，尤须据中运司天偏性制宜。</font>");

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
    
    private int getWuXingIndex(String wuxing) {
        switch (wuxing) {
            case "木": return 0;
            case "火": return 1;
            case "土": return 2;
            case "金": return 3;
            case "水": return 4;
            default: return 0;
        }
    }

    private int getSiTianIndex(String yearZhi) {
        switch (yearZhi) {
            case "子": case "午": return 1;
            case "丑": case "未": return 3;
            case "寅": case "申": return 2;
            case "卯": case "酉": return 4;
            case "辰": case "戌": return 5;
            case "巳": case "亥": return 0;
            default: return 0;
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
            result[i] = yuns[(centerIndex - 1 + i + 5) % 5];
        }
        return result;
    }
    
    private String[] getLiuqiForDate(int year, int month, int day) {
        String yearGanZhi = calculateYearGanZhi(year);
        String yearZhi = yearGanZhi.substring(1, 2);

        int siTianIndex = getSiTianIndex(yearZhi);

        String[] result = new String[6];
        int zaiQuanIndex = (siTianIndex + 3) % 6;

        result[2] = LIUYI_NAMES[siTianIndex];
        result[5] = LIUYI_NAMES[zaiQuanIndex];

        int chuQiIndex = (siTianIndex - 2 + 6) % 6;
        result[0] = LIUYI_NAMES[chuQiIndex];
        result[1] = LIUYI_NAMES[(chuQiIndex + 1) % 6];
        result[3] = LIUYI_NAMES[(siTianIndex + 1) % 6];
        result[4] = LIUYI_NAMES[(siTianIndex + 2) % 6];
        
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
        if (hour >= 23 || hour < 1) return 0;
        else if (hour >= 1 && hour < 3) return 1;
        else if (hour >= 3 && hour < 5) return 2;
        else if (hour >= 5 && hour < 7) return 3;
        else if (hour >= 7 && hour < 9) return 4;
        else if (hour >= 9 && hour < 11) return 5;
        else if (hour >= 11 && hour < 13) return 6;
        else if (hour >= 13 && hour < 15) return 7;
        else if (hour >= 15 && hour < 17) return 8;
        else if (hour >= 17 && hour < 19) return 9;
        else if (hour >= 19 && hour < 21) return 10;
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

    private void updateWuyunLiuqiYunshi(String yearGanZhi) {
        if (wuyunLiuqiYunshi == null) return;

        String yearGan = yearGanZhi.substring(0, 1);
        String yearZhi = yearGanZhi.substring(1, 2);

        StringBuilder yunshi = new StringBuilder();

        String zhongYun = getZhongYunShort(yearGan);
        int siTianIndex = getSiTianIndex(yearZhi);
        String siTian = LIUYI_NAMES[siTianIndex];
        int zaiQuanIndex = (siTianIndex + 3) % 6;
        String zaiQuan = LIUYI_NAMES[zaiQuanIndex];

        yunshi.append("<font color='" + COLOR_GOLD + "'><b>").append(yearGanZhi).append("年运势研判</b></font><br/><br/>");

        yunshi.append("<font color='" + COLOR_LIGHT_BLUE + "'><b>【运气组合】</b></font><br/>");
        yunshi.append("<font color='" + COLOR_JI + "'>中运：").append(zhongYun).append("</font><br/>");
        yunshi.append("<font color='" + COLOR_XIONG + "'>司天：").append(siTian).append("</font><br/>");
        yunshi.append("<font color='" + COLOR_WATER + "'>在泉：").append(zaiQuan).append("</font><br/><br/>");

        yunshi.append("<font color='" + COLOR_GOLD + "'><b>【综合运势】</b></font><br/>");
        yunshi.append(getWuyunDetailedAnalysis(zhongYun)).append("<br/>");
        yunshi.append("司天 ").append(siTian).append("：").append(getLiuyiDetailedAnalysis(siTian)).append("<br/>");
        yunshi.append("在泉 ").append(zaiQuan).append("：").append(getLiuyiDetailedAnalysis(zaiQuan)).append("<br/><br/>");

        String[][] yunshiAnalysis = {
            {"木", "木运之年，风气偏盛，肝气易旺。<br/>财气波动，乘势进取为宜。<br/>防肝胆、头风，多青色疏肝理气。<br/>人缘舒展，利交际结友。<br/><br/>", COLOR_JI},
            {"火", "火运之年，热气偏盛，心气易旺。<br/>财气宣通，理财须审慎。<br/>防心系、口疮，多红色清心降火。<br/>情意畅达，利盟定姻好。<br/><br/>", COLOR_XIONG},
            {"土", "土运之年，湿气偏盛，脾气易旺。<br/>财气平顺，稳健守成为宜。<br/>防脾胃不调，多黄色健脾化湿。<br/>情意安固，利家室之成。<br/><br/>", COLOR_EARTH},
            {"金", "金运之年，燥气偏盛，肺气易旺。<br/>财气收敛，宜纳获之功。<br/>防肺燥、肤干，多白色润肺生津。<br/>情意内敛，宜理性相与。<br/><br/>", COLOR_METAL},
            {"水", "水运之年，寒气偏盛，肾气易旺。<br/>财气潜藏，蓄势守成为宜。<br/>防肾系、寒痹，多黑色温阳补肾。<br/>情意沉静，宜诚悫相待。<br/><br/>", COLOR_WATER}
        };

        String wuxing = getWuXing(yearGan);
        for (String[] analysis : yunshiAnalysis) {
            if (analysis[0].equals(wuxing)) {
                yunshi.append("<font color='").append(analysis[2]).append("'>").append(analysis[1]).append("</font>");
                break;
            }
        }

        yunshi.append("<font color='" + COLOR_GOLD + "'><b>年度建议</b></font><br/>");
        yunshi.append(getYunQiCombinationAnalysis(zhongYun, siTian, zaiQuan)).append("<br/>");
        yunshi.append("<font color='" + COLOR_GRAY + "'>").append(getYearAdvice(yearGan, yearZhi)).append("</font>");

        wuyunLiuqiYunshi.setText(android.text.Html.fromHtml(yunshi.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
    }
    
    private String getWuyunDetailedAnalysis(String zhongYun) {
        String wuxing = zhongYun.substring(0, 1);

        switch (wuxing) {
            case "木":
                return "<font color='" + COLOR_JI + "'>木运属风，主生发，对应肝胆。" +
                       "<br/>太过：风盛，易眩晕抽搐；" +
                       "<br/>不及：风弱，易肝郁拘挛。</font>";
            case "火":
                return "<font color='" + COLOR_XIONG + "'>火运属热，主炎上，对应心小肠。" +
                       "<br/>太过：热盛，易烦热出血；" +
                       "<br/>不及：热弱，易寒悸失眠。</font>";
            case "土":
                return "<font color='" + COLOR_EARTH + "'>土运属湿，主承载，对应脾胃。" +
                       "<br/>太过：湿盛，易胀满水肿；" +
                       "<br/>不及：湿弱，易燥瘦乏力。</font>";
            case "金":
                return "<font color='" + COLOR_METAL + "'>金运属燥，主收敛，对应肺大肠。" +
                       "<br/>太过：燥盛，易咳嗽便秘；" +
                       "<br/>不及：燥弱，易喘泻湿证。</font>";
            case "水":
                return "<font color='" + COLOR_WATER + "'>水运属寒，主封藏，对应肾膀胱。" +
                       "<br/>太过：寒盛，易关节痛畏寒；" +
                       "<br/>不及：寒弱，易热尿频。</font>";
            default:
                return "未知运气";
        }
    }
    
    private String getLiuyiDetailedAnalysis(String liuyi) {
        if (liuyi.contains("木")) {
            return "<font color='" + COLOR_JI + "'>厥阴风木：主风邪生发。易患肝胆病、神经系统病、关节病。</font>";
        } else if (liuyi.contains("君火")) {
            return "<font color='" + COLOR_XIONG + "'>少阴君火：主热气光明。易患心血管病、热病、神志病。</font>";
        } else if (liuyi.contains("相火")) {
            return "<font color='#F3BA66'>少阳相火：主热气蒸腾。易患肝胆火旺、热病、炎症。</font>";
        } else if (liuyi.contains("湿土")) {
            return "<font color='" + COLOR_EARTH + "'>太阴湿土：主湿气运化。易患脾胃病、消化病、水肿。</font>";
        } else if (liuyi.contains("燥金")) {
            return "<font color='" + COLOR_METAL + "'>阳明燥金：主燥气收敛。易患呼吸病、皮肤病、便秘。</font>";
        } else if (liuyi.contains("寒水")) {
            return "<font color='" + COLOR_WATER + "'>太阳寒水：主寒气封藏。易患肾病、关节病、畏寒。</font>";
        }
        return "未知六气";
    }
    
    private String getYunQiCombinationAnalysis(String zhongYun, String siTian, String zaiQuan) {
        String yunWuXing = zhongYun.substring(0, 1);
        String siTianWuXing = extractWuxing(siTian);
        String zaiQuanWuXing = extractWuxing(zaiQuan);

        return getCombinationAnalysis(yunWuXing, siTianWuXing, "司天") + "<br/>" +
               getCombinationAnalysis(yunWuXing, zaiQuanWuXing, "在泉");
    }

    private String getCombinationAnalysis(String yunWuXing, String otherWuXing, String otherName) {
        StringBuilder sb = new StringBuilder();
        sb.append("中运 ").append(yunWuXing).append("运与").append(otherName).append(" ").append(otherWuXing).append("气：");
        if (yunWuXing.equals(otherWuXing)) {
            sb.append("<font color='" + COLOR_GOLD + "'>运气同气，本气过盛，需防本脏疾病。</font>");
        } else if (isSheng(yunWuXing, otherWuXing)) {
            sb.append("<font color='" + COLOR_JI + "'>运生气，气运相生，气候平和。</font>");
        } else if (isSheng(otherWuXing, yunWuXing)) {
            sb.append("<font color='" + COLOR_WATER + "'>气生运，气运相生，气候适宜。</font>");
        } else if (isKe(yunWuXing, otherWuXing)) {
            sb.append("<font color='" + COLOR_XIONG + "'>运克气，气运相克，气候异常。</font>");
        } else {
            sb.append("<font color='" + COLOR_EARTH + "'>气克运，气运相克，气候失调。</font>");
        }
        return sb.toString();
    }

    private String extractWuxing(String text) {
        String[] elements = {"木", "火", "土", "金", "水"};
        for (String e : elements) {
            if (text.contains(e)) return e;
        }
        return "";
    }
    
    private boolean isSheng(String a, String b) {
        java.util.Map<String, String> shengMap = new java.util.HashMap<>();
        shengMap.put("木", "火"); shengMap.put("火", "土");
        shengMap.put("土", "金"); shengMap.put("金", "水"); shengMap.put("水", "木");
        return shengMap.get(a) != null && shengMap.get(a).equals(b);
    }
    
    private boolean isKe(String a, String b) {
        java.util.Map<String, String> keMap = new java.util.HashMap<>();
        keMap.put("木", "土"); keMap.put("火", "金");
        keMap.put("土", "水"); keMap.put("金", "木"); keMap.put("水", "火");
        return keMap.get(a) != null && keMap.get(a).equals(b);
    }

    private String getYearAdvice(String yearGan, String yearZhi) {
        String wuxing = getWuXing(yearGan);
        String[] zodiac = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
        String[] zodiacMap = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        String zodiacName = "";
        for (int i = 0; i < zodiacMap.length; i++) {
            if (zodiacMap[i].equals(yearZhi)) {
                zodiacName = zodiac[i];
                break;
            }
        }

        String[][] adviceMap = {
            {"木", "宜：进取修学、舒展条达<br/>忌：躁急妄动、劳倦伤肝<br/>乘势生发，立业有为"},
            {"火", "宜：显才静养、明心戒躁<br/>忌：情绪激动、贪进冒功<br/>火明则名，谦退乃昌"},
            {"土", "宜：稳步诚信、厚积深耕<br/>忌：优柔怠惰、贪安废事<br/>守中致用，厚载有成"},
            {"金", "宜：果决变革、肃敛清源<br/>忌：刚愎自用、侈靡伤财<br/>金肃则利，应时乃功"},
            {"水", "宜：谋定养晦、沉静涵泳<br/>忌：盲从轻信、浮泛无根<br/>渊静藏用，待时而发"}
        };

        for (String[] advice : adviceMap) {
            if (advice[0].equals(wuxing)) {
                return advice[1];
            }
        }
        return "宜：顺势而动、刚毅有为<br/>忌：妄进执拗、怠惰无成";
    }
    
    private String getWuyunLiuqiSummary(String yearGanZhi) {
        String yearGan = yearGanZhi.substring(0, 1);
        String yearZhi = yearGanZhi.substring(1, 2);
        
        String wuxing = getWuXing(yearGan);
        String zhongYun = getZhongYunShort(yearGan);

        int siTianIndex = getSiTianIndex(yearZhi);
        String siTian = LIUYI_NAMES[siTianIndex];

        return yearGanZhi + "年" + zhongYun + "，司天" + siTian + "，";
    }
    
    private int getDaysInMonth(int year, int month) {
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = daysInMonth[month - 1];
        if (month == 2 && isLeapYear(year)) {
            days = 29;
        }
        return days;
    }
    
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
