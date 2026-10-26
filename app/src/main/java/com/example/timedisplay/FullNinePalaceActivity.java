package com.example.timedisplay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FullNinePalaceActivity extends Activity {

    private FullNinePalacePanel fullNinePalacePanel;
    private TextView fullPageTitle;
    
    private TextView expBasic;

    private TextView expZhifuZhishi;
    private TextView expGan;
    private TextView expSummary;
    private TextView expDirection;
    private TextView expYiJi;
    private TextView expLife;
    private TextView expTime;
    private TextView expOverall;

    private TextView expTianDiPanDesc;
    private TextView expPalacesDesc;
    private TextView expPaipanYiju;
    private TextView expGejuKeying;

    // 九星/八门/八神 分栏详细解释（横竖屏一致）
    private TextView expNineStarsDesc;
    private TextView expEightDoorsDesc;
    private TextView expGodsDesc;

    private static final long UPDATE_INTERVAL = 60000;
    private Handler updateHandler;
    private Runnable updateRunnable;

    private static final String[] TIANGAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] DIZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static final String[] LIUJIAZI = {
        "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉",
        "甲戌", "乙亥", "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未",
        "甲申", "乙酉", "丙戌", "丁亥", "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳",
        "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥", "庚子", "辛丑", "壬寅", "癸卯",
        "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥", "壬子", "癸丑",
        "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉", "壬戌", "癸亥"
    };
    private static final String[] MONTH_ZHI_LIST = {"寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑"};


    private static final java.util.HashMap<String, String> WUHUDUN = new java.util.HashMap<String, String>();
    private static final java.util.HashMap<String, String> WUSHUDUN_MAP = new java.util.HashMap<String, String>();

    static {
        WUHUDUN.put("甲", "丙");
        WUHUDUN.put("己", "丙");
        WUHUDUN.put("乙", "戊");
        WUHUDUN.put("庚", "戊");
        WUHUDUN.put("丙", "庚");
        WUHUDUN.put("辛", "庚");
        WUHUDUN.put("丁", "壬");
        WUHUDUN.put("壬", "壬");
        WUHUDUN.put("戊", "甲");
        WUHUDUN.put("癸", "甲");

        WUSHUDUN_MAP.put("甲", "甲");
        WUSHUDUN_MAP.put("己", "甲");
        WUSHUDUN_MAP.put("乙", "丙");
        WUSHUDUN_MAP.put("庚", "丙");
        WUSHUDUN_MAP.put("丙", "戊");
        WUSHUDUN_MAP.put("辛", "戊");
        WUSHUDUN_MAP.put("丁", "庚");
        WUSHUDUN_MAP.put("壬", "庚");
        WUSHUDUN_MAP.put("戊", "壬");
        WUSHUDUN_MAP.put("癸", "壬");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 始终按保存的方向固定屏幕（不跟随系统），由旋转按钮手动切换
        RotationLockUtil.apply(this);
        
        setContentView(R.layout.activity_full_nine_palace);

        fullNinePalacePanel = (FullNinePalacePanel) findViewById(R.id.fullNinePalacePanel);
        fullPageTitle = (TextView) findViewById(R.id.fullPageTitle);
        
        expBasic = (TextView) findViewById(R.id.expBasic);
        expZhifuZhishi = (TextView) findViewById(R.id.expZhifuZhishi);
        expGan = (TextView) findViewById(R.id.expGan);
        expSummary = (TextView) findViewById(R.id.expSummary);
        expDirection = (TextView) findViewById(R.id.expDirection);
        expYiJi = (TextView) findViewById(R.id.expYiJi);
        expLife = (TextView) findViewById(R.id.expLife);
        expTime = (TextView) findViewById(R.id.expTime);
        expOverall = (TextView) findViewById(R.id.expOverall);
        expTianDiPanDesc = (TextView) findViewById(R.id.expTianDiPanDesc);
        expPalacesDesc = (TextView) findViewById(R.id.expPalacesDesc);
        expPaipanYiju = (TextView) findViewById(R.id.expPaipanYiju);
        expGejuKeying = (TextView) findViewById(R.id.expGejuKeying);

        expNineStarsDesc = (TextView) findViewById(R.id.expNineStarsDesc);
        expEightDoorsDesc = (TextView) findViewById(R.id.expEightDoorsDesc);
        expGodsDesc = (TextView) findViewById(R.id.expGodsDesc);
        
        updateHandler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateFullNinePalace();
                long nextUpdate = ((SystemClock.uptimeMillis() / UPDATE_INTERVAL) + 1) * UPDATE_INTERVAL;
                updateHandler.postAtTime(this, nextUpdate);
            }
        };

        updateFullNinePalace();
        long first = ((SystemClock.uptimeMillis() / UPDATE_INTERVAL) + 1) * UPDATE_INTERVAL;
        updateHandler.postAtTime(updateRunnable, first);

        int orientation = getResources().getConfiguration().orientation;
        if (android.content.res.Configuration.ORIENTATION_LANDSCAPE == orientation) {
            // 横屏：以「综合研判」为界，之前放左栏、之后（含综合研判）放右栏，且左右独立滚动
            arrangeLandscapeColumns();
        } else {
            // 竖屏：九宫尺寸随屏幕自适应（横屏由 layout-land 的 match_parent 自动贴合可用高度，无需干预）
            if (fullNinePalacePanel != null) {
                fullNinePalacePanel.post(() -> applyNinePalaceResponsiveSize());
            }
        }
    }

    // 获取自定义时间（如果有）
    private Calendar getDisplayCalendar() {
        android.content.Intent intent = getIntent();
        if (intent != null && intent.hasExtra("custom_year")) {
            Calendar cal = Calendar.getInstance();
            cal.set(
                intent.getIntExtra("custom_year", cal.get(Calendar.YEAR)),
                intent.getIntExtra("custom_month", cal.get(Calendar.MONTH) + 1) - 1,
                intent.getIntExtra("custom_day", cal.get(Calendar.DAY_OF_MONTH)),
                intent.getIntExtra("custom_hour", cal.get(Calendar.HOUR_OF_DAY)),
                intent.getIntExtra("custom_minute", cal.get(Calendar.MINUTE)),
                0
            );
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        }
        return Calendar.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHandler.removeCallbacks(updateRunnable);
        updateFullNinePalace();
        long next = ((SystemClock.uptimeMillis() / UPDATE_INTERVAL) + 1) * UPDATE_INTERVAL;
        updateHandler.postAtTime(updateRunnable, next);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(updateRunnable);
    }

    // 横屏布局：整体分为左右两栏，且左右两栏各自独立滚动。
    // 左栏 = 「综合研判」之前的所有内容；右栏 = 从「综合研判」开始（含）之后的全部内容。
    // 仅改变父容器，视图对象本身不变，因此横竖屏信息完全一致。
    private void arrangeLandscapeColumns() {
        android.widget.LinearLayout content = (android.widget.LinearLayout) findViewById(R.id.fullNinePalaceLayout);
        android.view.View start = findViewById(R.id.zhkyGroup);
        if (content == null || start == null) return;
        int idx = content.indexOfChild(start);
        // 横屏分栏已由 res/layout-land/activity_full_nine_palace.xml 完成：
        // 九宫是 fullNinePalaceLayout 之外的独立左列，因此 zhkyGroup 是该容器的第一个子视图（idx == 0），
        // 「zhkyGroup 之前」没有任何内容。此时若继续重建两栏，会凭空生成一个占 50% 宽度的空列，
        // 把所有文字挤到右栏的 50%（表现为文字只占右半区的一半、中间大片空白）。
        // 故 idx == 0（无左栏内容）时直接返回，保持 XML 的左九宫 / 右文字结构不变。
        if (idx <= 0) return;

        android.view.ViewGroup parent = (android.view.ViewGroup) content.getParent();
        if (parent instanceof android.widget.ScrollView) {
            ((android.widget.ScrollView) parent).setFillViewport(true);
        }
        // 仅原地修改现有布局参数（fullNinePalaceLayout 是 ScrollView(FrameLayout) 的直接子视图，
        // 其参数实际为 FrameLayout.LayoutParams；若整体替换为基类会触发 ClassCastException）
        android.view.ViewGroup.LayoutParams clp = content.getLayoutParams();
        clp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        clp.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

        // 取出 start 及其后的所有兄弟视图（保持原顺序）-> 右栏
        java.util.ArrayList<android.view.View> right = new java.util.ArrayList<>();
        for (int i = content.getChildCount() - 1; i >= idx; i--) {
            android.view.View v = content.getChildAt(i);
            content.removeViewAt(i);
            right.add(0, v);
        }
        // 剩余 [0, idx) 为左栏
        java.util.ArrayList<android.view.View> left = new java.util.ArrayList<>();
        for (int i = content.getChildCount() - 1; i >= 0; i--) {
            android.view.View v = content.getChildAt(i);
            content.removeViewAt(i);
            left.add(0, v);
        }

        // 左右两列容器（横向，占满高度）
        android.widget.LinearLayout twoCol = new android.widget.LinearLayout(this);
        twoCol.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        twoCol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.MATCH_PARENT));

        // 左栏：独立滚动
        android.widget.LinearLayout leftCol = new android.widget.LinearLayout(this);
        leftCol.setOrientation(android.widget.LinearLayout.VERTICAL);
        leftCol.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT, android.widget.FrameLayout.LayoutParams.WRAP_CONTENT));
        for (android.view.View v : left) leftCol.addView(v);

        android.widget.ScrollView leftScroll = new android.widget.ScrollView(this);
        leftScroll.setFillViewport(true);
        android.widget.LinearLayout.LayoutParams lpL = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lpL.setMargins(0, 0, 4, 0);
        leftScroll.setLayoutParams(lpL);
        leftScroll.addView(leftCol);

        // 右栏：独立滚动
        android.widget.LinearLayout rightCol = new android.widget.LinearLayout(this);
        rightCol.setOrientation(android.widget.LinearLayout.VERTICAL);
        rightCol.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT, android.widget.FrameLayout.LayoutParams.WRAP_CONTENT));
        for (android.view.View v : right) rightCol.addView(v);

        android.widget.ScrollView rightScroll = new android.widget.ScrollView(this);
        rightScroll.setFillViewport(true);
        android.widget.LinearLayout.LayoutParams lpR = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lpR.setMargins(4, 0, 0, 0);
        rightScroll.setLayoutParams(lpR);
        rightScroll.addView(rightCol);

        twoCol.addView(leftScroll);
        twoCol.addView(rightScroll);
        content.addView(twoCol);

        // 外层 ScrollView 以 UNSPECIFIED 高度测量子级，内层 ScrollView 无法自动获得视口高度，
        // 因此布局完成后把两栏高度固定为可视区域高度，使左右两栏真正独立滚动。
        if (parent instanceof android.widget.ScrollView) {
            final android.widget.ScrollView sv = (android.widget.ScrollView) parent;
            sv.post(() -> {
                int h = sv.getHeight();
                int pad = content.getPaddingTop() + content.getPaddingBottom();
                int colH = h - pad;
                if (colH > 0) {
                    android.view.ViewGroup.LayoutParams p = twoCol.getLayoutParams();
                    p.height = colH;
                    twoCol.setLayoutParams(p);
                }
            });
        }
    }

    // 竖屏：九宫尺寸随屏幕自适应，避免小屏过大、大屏过小。
    // 取「屏宽×0.86」与「屏高×0.50」的较小值，再夹到 [200,460]dp，并至少左右各留 12dp。
    private void applyNinePalaceResponsiveSize() {
        if (fullNinePalacePanel == null) return;
        android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        if (density <= 0f) return;
        int wdp = (int) (dm.widthPixels / density);
        int hdp = (int) (dm.heightPixels / density);
        int size = (int) (Math.min(wdp * 0.86f, hdp * 0.50f));
        size = Math.min(size, wdp - 24);           // 左右至少各留 12dp
        size = Math.max(200, Math.min(460, size)); // 下限防小屏过大，上限防大屏过小
        android.view.ViewGroup.LayoutParams lp = fullNinePalacePanel.getLayoutParams();
        if (lp == null) return;
        lp.width = (int) (size * density + 0.5f);
        lp.height = (int) (size * density + 0.5f);
        fullNinePalacePanel.setLayoutParams(lp);
    }

    private void updateFullNinePalace() {
        Calendar calendar = getDisplayCalendar();
        Date now = calendar.getTime();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String yearPillar = calculateYearPillar(year, month, day);
        String monthPillar = calculateMonthPillar(year, month, day, yearPillar.substring(0, 1));
        
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
        String dayPillar = calculateDayPillar(calcYear, calcMonth, calcDay);
        String timePillar = calculateTimePillar(hour, minute, dayPillar.substring(0, 1));

        // 节气与节气内第几天统一使用 JieqiData（与首页显示一致），避免两页节气判断分歧
        String jieqi = JieqiData.getCurrentJieqi(calendar);
        int dayInJieqi = JieqiData.getDaysIntoJieqi(calendar, jieqi) + 1; // 转 1 基（1..15）

        // 计算落宫信息（统一调用 QiMenCalculator 单一算法）
        calculateAndSetPalaceData(yearPillar, monthPillar, dayPillar, timePillar, jieqi, dayInJieqi);

        // 更新各解读区域
        updateExplanation(yearPillar, monthPillar, dayPillar, timePillar, jieqi, dayInJieqi);
    }

    // 获取空亡
    private String getKongWang(String dayPillar) {
        String[] xunshouList = {"甲子", "甲戌", "甲申", "甲午", "甲辰", "甲寅"};
        java.util.Map<String, String> kongwangMap = new java.util.HashMap<>();
        kongwangMap.put("甲子", "戌亥");
        kongwangMap.put("甲戌", "申酉");
        kongwangMap.put("甲申", "午未");
        kongwangMap.put("甲午", "辰巳");
        kongwangMap.put("甲辰", "寅卯");
        kongwangMap.put("甲寅", "子丑");
        
        String dayGan = dayPillar.substring(0, 1);
        String dayZhi = dayPillar.substring(1, 2);
        String shiGanzhi = dayGan + dayZhi;
        int shiIndex = -1;
        for (int i = 0; i < LIUJIAZI.length; i++) {
            if (LIUJIAZI[i].equals(shiGanzhi)) {
                shiIndex = i;
                break;
            }
        }
        if (shiIndex == -1) shiIndex = 0;
        int xunIndex = shiIndex / 10;
        String xunshou = xunshouList[xunIndex];
        
        return kongwangMap.get(xunshou) != null ? kongwangMap.get(xunshou) : "--";
    }

    // 获取马星
    private String getMaXing(String dayPillar) {
        // 马星（三合局）：申子辰→寅；寅午戌→申；巳酉丑→亥；亥卯未→巳
        java.util.Map<String, String> maXingMap = new java.util.HashMap<>();
        maXingMap.put("申", "寅"); maXingMap.put("子", "寅"); maXingMap.put("辰", "寅");
        maXingMap.put("寅", "申"); maXingMap.put("午", "申"); maXingMap.put("戌", "申");
        maXingMap.put("巳", "亥"); maXingMap.put("酉", "亥"); maXingMap.put("丑", "亥");
        maXingMap.put("亥", "巳"); maXingMap.put("卯", "巳"); maXingMap.put("未", "巳");
        String dayZhi = dayPillar.substring(1, 2);
        return maXingMap.get(dayZhi) != null ? maXingMap.get(dayZhi) : "--";
    }

    private String calculateYearPillar(int year, int month, int day) {
        // 年柱以立春为界：立春之前（小寒、大寒）仍属上一年，用节气精确判定
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day, 12, 0, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        String jieqi = JieqiData.getCurrentJieqi(cal);
        if ("小寒".equals(jieqi) || "大寒".equals(jieqi)) {
            year = year - 1;
        }

        int baseYear = 1900;
        int baseIndex = 36;

        int yearDiff = year - baseYear;
        int yearIndex = (baseIndex + yearDiff) % 60;

        int yearGanIndex = yearIndex % 10;
        int yearZhiIndex = yearIndex % 12;

        String yearGan = TIANGAN[yearGanIndex];
        String yearZhi = DIZHI[yearZhiIndex];

        return yearGan + yearZhi;
    }

    // 获取月支（节月：以二十四节气为分界，与首页 MainActivity 一致）
    private String getMonthZhi(int year, int month, int day) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day, 12, 0, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        String jieqi = JieqiData.getCurrentJieqi(cal);
        switch (jieqi) {
            case "立春":
            case "雨水":  return "寅";
            case "惊蛰":
            case "春分":  return "卯";
            case "清明":
            case "谷雨":  return "辰";
            case "立夏":
            case "小满":  return "巳";
            case "芒种":
            case "夏至":  return "午";
            case "小暑":
            case "大暑":  return "未";
            case "立秋":
            case "处暑":  return "申";
            case "白露":
            case "秋分":  return "酉";
            case "寒露":
            case "霜降":  return "戌";
            case "立冬":
            case "小雪":  return "亥";
            case "大雪":
            case "冬至":  return "子";
            case "小寒":
            case "大寒":  return "丑";
            default:       return "寅";
        }
    }

    private String calculateMonthPillar(int year, int month, int day, String yearGan) {
        String monthZhi = getMonthZhi(year, month, day);
        String yinMonthGan = WUHUDUN.get(yearGan);
        if (yinMonthGan == null) {
            yinMonthGan = "丙";
        }
        int yinGanIndex = java.util.Arrays.asList(TIANGAN).indexOf(yinMonthGan);
        int monthZhiIndex = java.util.Arrays.asList(MONTH_ZHI_LIST).indexOf(monthZhi);
        int monthGanIndex = (yinGanIndex + monthZhiIndex) % 10;
        String monthGan = TIANGAN[monthGanIndex];
        return monthGan + monthZhi;
    }

    // 计算日柱：采用整数儒略日算法求两日期之间的纯日历天数差，避免毫秒差在夏令时
    // 切换日（1986-1991 中国曾实行夏时制）产生 ±1 天的误差，确保与首页 MainActivity 一致、日柱准确。
    private String calculateDayPillar(int year, int month, int day) {
        try {
            int daysDiff = julianDay(year, month, day) - julianDay(1900, 1, 1);
            int baseGanzhiIndex = 10; // 1900年1月1日为甲戌日
            int ganzhiIndex = (baseGanzhiIndex + daysDiff) % 60;
            if (ganzhiIndex < 0) ganzhiIndex += 60;
            return LIUJIAZI[ganzhiIndex];
        } catch (Exception e) {
            e.printStackTrace();
            return "甲午"; // 默认值
        }
    }

    // 儒略日数（整数部分，用于求两日期之间的整数天数差）；与 MainActivity 同源
    private static int julianDay(int y, int m, int d) {
        if (m <= 2) { y -= 1; m += 12; }
        int a = y / 100;
        int b = 2 - a + a / 4;
        return (int) (365.25 * (y + 4716)) + (int) (30.6001 * (m + 1)) + d + b - 1524;
    }

    private String calculateTimePillar(int hour, int minute, String dayGan) {
        String hourZhi = "子";
        int hourZhiIndex = 0;
        
        if (hour >= 23 || hour < 1) {
            hourZhi = "子";
            hourZhiIndex = 0;
        } else if (hour >= 1 && hour < 3) {
            hourZhi = "丑";
            hourZhiIndex = 1;
        } else if (hour >= 3 && hour < 5) {
            hourZhi = "寅";
            hourZhiIndex = 2;
        } else if (hour >= 5 && hour < 7) {
            hourZhi = "卯";
            hourZhiIndex = 3;
        } else if (hour >= 7 && hour < 9) {
            hourZhi = "辰";
            hourZhiIndex = 4;
        } else if (hour >= 9 && hour < 11) {
            hourZhi = "巳";
            hourZhiIndex = 5;
        } else if (hour >= 11 && hour < 13) {
            hourZhi = "午";
            hourZhiIndex = 6;
        } else if (hour >= 13 && hour < 15) {
            hourZhi = "未";
            hourZhiIndex = 7;
        } else if (hour >= 15 && hour < 17) {
            hourZhi = "申";
            hourZhiIndex = 8;
        } else if (hour >= 17 && hour < 19) {
            hourZhi = "酉";
            hourZhiIndex = 9;
        } else if (hour >= 19 && hour < 21) {
            hourZhi = "戌";
            hourZhiIndex = 10;
        } else {
            hourZhi = "亥";
            hourZhiIndex = 11;
        }

        String startGan = WUSHUDUN_MAP.get(dayGan);
        if (startGan == null) {
            startGan = "甲";
        }
        int startGanIndex = java.util.Arrays.asList(TIANGAN).indexOf(startGan);
        int hourGanIndex = (startGanIndex + hourZhiIndex) % 10;
        String hourGan = TIANGAN[hourGanIndex];
        return hourGan + hourZhi;
    }

    private String[] calculateAndSetPalaceData(String yearPillar, String monthPillar, String dayPillar, String timePillar, String jieqi, int dayInJieqi) {
        // 统一调用 QiMenCalculator（全 app 唯一的排盘算法实现）
        QiMenCalculator.Result r = QiMenCalculator.calculate(yearPillar, monthPillar, dayPillar, timePillar, jieqi, dayInJieqi);

        String[] PALACE_NAMES = {"坎一", "坤二", "震三", "巽四", "中五", "乾六", "兑七", "艮八", "离九"};

        String[][] palaceData = new String[9][4];
        String[] luckData = new String[9];
        for (int i = 0; i < 9; i++) {
            String star = r.nineStars[i];
            String door = r.eightDoors[i];
            String god = r.eightGods[i];
            String tianGan = r.tianPanTianGan[i];
            String diGan = r.diPanTianGan[i];

            // 宫内四行：宫名+干支 / 九星 / 八门 / 八神+旺衰
            palaceData[i][0] = PALACE_NAMES[i] + " " + tianGan + "/" + diGan;
            palaceData[i][1] = star + "星";
            palaceData[i][2] = (door != null && !door.isEmpty() ? door : " ") + "门";
            palaceData[i][3] = god + " " + r.wangCui[i];

            luckData[i] = r.luck[i];
        }

        fullNinePalacePanel.setPalaceData(palaceData);
        fullNinePalacePanel.setLuckData(luckData);
        // 亮度需与首页九宫（MainActivity）保持一致，避免两处九宫明暗不同
        fullNinePalacePanel.setBrightness(0.8f);

        // 返回落宫信息：[值符落宫, 值使落宫, 日干落宫, 时干落宫]
        String[] palaceInfo = new String[4];
        palaceInfo[0] = PALACE_NAMES[r.zhiFuPalace];
        palaceInfo[1] = PALACE_NAMES[r.zhiShiPalace];
        palaceInfo[2] = r.riGanPalace >= 0 ? PALACE_NAMES[r.riGanPalace] : "--";
        palaceInfo[3] = r.shiGanPalace >= 0 ? PALACE_NAMES[r.shiGanPalace] : "--";

        return palaceInfo;
    }

    // ==================== 排盘算法已统一到 QiMenCalculator（单一实现） ====================
    

    

    

    



    
    /** 九星吉凶配色，依《三元经》品级：上吉绿、次吉浅绿、小凶黄、大凶红。 */
    private String getStarLuckColor(String star) {
        if (star == null) return "#E6C46A";
        switch (star) {
            case "天辅": case "天禽": case "天心": return "#3FA34D"; // 上吉
            case "天冲": case "天任": return "#7FBF5F";             // 次吉
            case "天柱": case "天英": return "#E6C46A";             // 小凶
            case "天蓬": case "天芮": return "#E0593B";             // 大凶
            default: return "#E6C46A";
        }
    }
    
    private String getDoorLuckColor(String door) {
        if (door == null) return "#E6C46A";
        switch (door) {
            case "开": return "#3FA34D";
            case "休": return "#3FA34D";
            case "生": return "#3FA34D";
            case "伤": return "#E0593B";
            case "杜": return "#E6C46A";
            case "景": return "#E6C46A";
            case "死": return "#E0593B";
            case "惊": return "#E0593B";
            default: return "#E6C46A";
        }
    }

    // 八门吉凶等级（用于八门排布面板副文本）
    private String getDoorLuckLevel(String door) {
        if (door == null || door.isEmpty()) return "";
        switch (door) {
            case "开": return "吉门·谒贵求谋";
            case "休": return "吉门·赴官迁徙";
            case "生": return "吉门·求财造作";
            case "伤": return "凶门·损伤争斗";
            case "杜": return "平门·藏形避难";
            case "景": return "平门·文书求名";
            case "死": return "凶门·衰败收敛";
            case "惊": return "凶门·惊疑词讼";
            default: return "";
        }
    }

    // 八神简要说明（用于八神排布面板副文本）
    private String getGodBrief(String god) {
        if (god == null || god.isEmpty()) return "";
        switch (god) {
            case "值符": return "百恶消散";
            case "螣蛇": return "惊恐怪异";
            case "太阴": return "阴匿暗助";
            case "六合": return "婚姻和合";
            case "白虎": return "兵戈死丧";
            case "玄武": return "盗贼逃亡";
            case "九地": return "柔顺潜藏";
            case "九天": return "扬兵显达";
            default: return "";
        }
    }
    
    private String getRiShiRelationColor(String relation) {
        if (relation == null) return "#E6C46A";
        if (relation.contains("生") || relation.contains("合") || relation.contains("比")) {
            return "#3FA34D";
        } else if (relation.contains("克") || relation.contains("冲")) {
            return "#E0593B";
        } else {
            return "#E6C46A";
        }
    }

    private void updateExplanation(String yearPillar, String monthPillar, String dayPillar, String timePillar, String jieqi, int dayInJieqi) {
        QiMenCalculator.Result r = QiMenCalculator.calculate(yearPillar, monthPillar, dayPillar, timePillar, jieqi, dayInJieqi);
        boolean isYangDun = r.isYangDun;
        int ju = r.ju;

        String timeGan = timePillar != null && timePillar.length() >= 1 ? timePillar.substring(0, 1) : "甲";
        String timeZhi = timePillar != null && timePillar.length() >= 2 ? timePillar.substring(1, 2) : "子";
        String dayGan = dayPillar != null && dayPillar.length() >= 1 ? dayPillar.substring(0, 1) : "甲";

        String[] nineStars = r.nineStars;
        String[] eightDoors = r.eightDoors;
        String[] eightGods = r.eightGods;
        String[] wangCui = r.wangCui;
        String xunShou = r.xunShou;
        String zhiFuStar = r.zhiFuStar;
        String zhiShiDoor = r.zhiShiDoor;
        int zhiFuPalace = r.zhiFuPalace;
        int zhiShiPalace = r.zhiShiPalace;
        int riGanPalace = r.riGanPalace;
        int shiGanPalace = r.shiGanPalace;
        String[] tianPan = r.tianPanTianGan;
        String[] diPanTianGan = r.diPanTianGan;
        String zhiFuGod = eightGods[zhiFuPalace];

        String[] DIRECTIONS = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};
        String[] PALACE_NAMES = {"坎一", "坤二", "震三", "巽四", "中五", "乾六", "兑七", "艮八", "离九"};

        String kongWang = getKongWang(dayPillar);
        String maXing = getMaXing(dayPillar);
        String riGanWuXing = QiMenCalculator.getWuXing(dayGan);
        String shiGanWuXing = QiMenCalculator.getWuXing(timeGan);
        String dayZhi = dayPillar.substring(1, 2);
        
        StringBuilder sbBasic = new StringBuilder();
        sbBasic.append("<font color='#CCB866'><b>").append(jieqi).append(" · ").append(isYangDun ? "阳遁" : "阴遁").append(ju).append("局</b></font><br/>");
        
        sbBasic.append("　《秘笈》曰：<font color='#7C8C9C'>「六甲为时首，而应局斯灵。」</font><br/><br/>");

        sbBasic.append("📆 旬首 <font color='#E6C46A'><b>").append(xunShou).append("</b></font>　空亡 <font color='#F3BA66'><b>").append(kongWang).append("</b></font><br/>");
        sbBasic.append("<font color='#7C8C9C'>").append(getKongWangExplanation(kongWang)).append("</font><br/><br/>");
        
        sbBasic.append("🐴 马星 <font color='#E6C46A'><b>").append(maXing).append("</b></font><br/>");
        sbBasic.append("<font color='#7C8C9C'>").append(getMaXingExplanation(maXing)).append("</font><br/><br/>");
        
        sbBasic.append("<font color='#CCB866'><b>旺相休囚</b></font><br/>");
        sbBasic.append("<font color='#7C8C9C'>").append(getWangCuiDescription(wangCui, riGanPalace)).append("</font><br/>");
        sbBasic.append("<font color='#7C8C9C'>　《烟波钓叟歌》：「与我同行即为相，我生之月诚为旺，废于父母休于财，囚于官鬼真不妄。」</font>");
        expBasic.setText(android.text.Html.fromHtml(sbBasic.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        if (expSummary != null) {
            expSummary.setText(getQiMenSummary(yearPillar, monthPillar, dayPillar, timePillar, zhiFuStar, zhiShiDoor, isYangDun, ju));
        }
        
        StringBuilder sbZhifuZhishi = new StringBuilder();
        String zhiFuColor = getStarLuckColor(zhiFuStar);
        String zhiShiColor = getDoorLuckColor(zhiShiDoor);
        sbZhifuZhishi.append("值符：<font color='").append(zhiFuColor).append("'>").append(zhiFuStar).append("星</font>")
          .append("（").append(getStarGrade(zhiFuStar)).append("）落").append(PALACE_NAMES[zhiFuPalace])
          .append("<font color='#7C8C9C'>　九星之领袖，所到之处百恶消散</font><br/>");
        sbZhifuZhishi.append("值使：<font color='").append(zhiShiColor).append("'>").append(zhiShiDoor).append("门</font>")
          .append("（").append(getDoorGrade(zhiShiDoor)).append("）落").append(PALACE_NAMES[zhiShiPalace])
          .append("<font color='#7C8C9C'>　八门之枢机，用事之要</font>");
        expZhifuZhishi.setText(android.text.Html.fromHtml(sbZhifuZhishi.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        StringBuilder sbGan = new StringBuilder();
        sbGan.append("<font color='#7C8C9C'>日干为人，时干为事，观其落宫与生克，以定主客。</font><br/>");
        sbGan.append("日干 ").append(dayGan).append(dayZhi).append("(").append(riGanWuXing).append(")").append("落").append(riGanPalace >= 0 ? PALACE_NAMES[riGanPalace] : "--").append("<br/>");
        sbGan.append("时干 ").append(timeGan).append(timeZhi).append("(").append(shiGanWuXing).append(")").append("落").append(shiGanPalace >= 0 ? PALACE_NAMES[shiGanPalace] : "--").append("<br/>");
        String riShiRelation = getRiShiRelationship(dayGan, timeGan);
        String riShiColor = getRiShiRelationColor(riShiRelation);
        sbGan.append("日时关系：<font color='").append(riShiColor).append("'>").append(riShiRelation).append("</font>");
        expGan.setText(android.text.Html.fromHtml(sbGan.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        String[] luckData = new String[9];
        
        for (int i = 0; i < 9; i++) {
            String door = eightDoors[i] != null && !eightDoors[i].isEmpty() ? eightDoors[i] : "";
            String god = eightGods[i] != null && !eightGods[i].isEmpty() ? eightGods[i] : "";
            
            luckData[i] = QiMenCalculator.getLuckSymbol(nineStars[i], door, god, wangCui[i]);
        }
        
        StringBuilder sbDirection = new StringBuilder();
        StringBuilder luckyDirections = new StringBuilder();
        StringBuilder unluckyDirections = new StringBuilder();
        StringBuilder neutralDirections = new StringBuilder();
        StringBuilder wangPositions = new StringBuilder();
        StringBuilder xiuPositions = new StringBuilder();
        StringBuilder qiuPositions = new StringBuilder();
        StringBuilder siPositions = new StringBuilder();
        StringBuilder xiangPositions = new StringBuilder();
        
        for (int i = 0; i < 9; i++) {
            String door = eightDoors[i];
            if (door == null || door.isEmpty()) continue;
            if (door.equals("开") || door.equals("休") || door.equals("生")) {
                if (luckyDirections.length() > 0) luckyDirections.append("、");
                luckyDirections.append(DIRECTIONS[i]);
            } else if (door.equals("死") || door.equals("惊") || door.equals("伤")) {
                if (unluckyDirections.length() > 0) unluckyDirections.append("、");
                unluckyDirections.append(DIRECTIONS[i]);
            } else {
                if (neutralDirections.length() > 0) neutralDirections.append("、");
                neutralDirections.append(DIRECTIONS[i]);
            }
            if (i != 4) {
                String wc = wangCui[i];
                if (wc.equals("旺")) {
                    if (wangPositions.length() > 0) wangPositions.append("、");
                    wangPositions.append(DIRECTIONS[i]);
                } else if (wc.equals("相")) {
                    if (xiangPositions.length() > 0) xiangPositions.append("、");
                    xiangPositions.append(DIRECTIONS[i]);
                } else if (wc.equals("休")) {
                    if (xiuPositions.length() > 0) xiuPositions.append("、");
                    xiuPositions.append(DIRECTIONS[i]);
                } else if (wc.equals("囚")) {
                    if (qiuPositions.length() > 0) qiuPositions.append("、");
                    qiuPositions.append(DIRECTIONS[i]);
                } else if (wc.equals("死")) {
                    if (siPositions.length() > 0) siPositions.append("、");
                    siPositions.append(DIRECTIONS[i]);
                }
            }
        }
        
        sbDirection.append("<font color='#7C8C9C'>《秘笈》曰：「九天之上好扬兵，九地潜藏可立营，伏兵但向太阴位，若逢六合利逃形。」")
          .append("三吉门所在之方，为出行谋事之所宜。</font><br/><br/>");
        if (luckyDirections.length() > 0) sbDirection.append("<font color='#3FA34D'>吉方</font>：").append(luckyDirections).append("（开休生三吉门）<br/>");
        if (neutralDirections.length() > 0) sbDirection.append("⚪ <font color='#E6C46A'>平方</font>：").append(neutralDirections).append("（杜景二门）<br/>");
        if (unluckyDirections.length() > 0) sbDirection.append("<font color='#E0593B'>凶方</font>：").append(unluckyDirections).append("（死惊伤三凶门）<br/>");

        sbDirection.append("<br/>");
        sbDirection.append("<b>旺相休囚死分布：</b><br/>");
        sbDirection.append("<font color='#7C8C9C'>「吉宿更能逢旺相，万举万全必成功；若遇休囚并废没，劝君不必进前程。」</font><br/>");
        if (wangPositions.length() > 0) sbDirection.append("<font color='#3FA34D'>旺</font>：").append(wangPositions).append("（得时·百事顺）<br/>");
        if (xiangPositions.length() > 0) sbDirection.append("<font color='#3FA34D'>相</font>：").append(xiangPositions).append("（得生·次吉）<br/>");
        if (xiuPositions.length() > 0) sbDirection.append("<font color='#E6C46A'>休</font>：").append(xiuPositions).append("（退藏·静养）<br/>");
        if (qiuPositions.length() > 0) sbDirection.append("<font color='#F3BA66'>囚</font>：").append(qiuPositions).append("（受克·不利）<br/>");
        if (siPositions.length() > 0) sbDirection.append("<font color='#E0593B'>死</font>：").append(siPositions).append("（处死·衰败）</font>");
        expDirection.setText(android.text.Html.fromHtml(sbDirection.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        StringBuilder sbYiJi = new StringBuilder();
        sbYiJi.append("《秘笈》曰：").append(getShiGanZhuShi(timeGan)).append("\n");
        sbYiJi.append("宜：");
        String[] yiItems = getYiActivitiesDetailed(zhiShiDoor, timeGan);
        for (int i = 0; i < Math.min(yiItems.length, 5); i++) {
            if (i > 0) {
                sbYiJi.append("、");
            }
            sbYiJi.append(yiItems[i]);
        }
        sbYiJi.append("\n");
        sbYiJi.append("忌：");
        String[] jiItems = getJiActivitiesDetailed(zhiShiDoor, timeGan);
        for (int i = 0; i < Math.min(jiItems.length, 5); i++) {
            if (i > 0) {
                sbYiJi.append("、");
            }
            sbYiJi.append(jiItems[i]);
        }
        expYiJi.setText(sbYiJi.toString());
        
        String riGanLuck = riGanPalace >= 0 ? luckData[riGanPalace] : "未知";
        String shiGanLuck = shiGanPalace >= 0 ? luckData[shiGanPalace] : "未知";
        
        StringBuilder sbLife = new StringBuilder();
        sbLife.append("当前宜以").append(getMindAdvice(zhiShiDoor, zhiFuStar)).append("；事业学业")
              .append(getCareerAdvice(zhiShiDoor, zhiFuStar)).append("，财运").append(getWealthAdvice(zhiShiDoor, zhiFuStar))
              .append("，健康饮食").append(getHealthAdvice(zhiShiDoor, zhiFuStar)).append("，感情人际")
              .append(getRelationshipAdvice(zhiShiDoor, zhiFuStar)).append("，出行").append(getTravelAdvice(zhiShiDoor, zhiFuStar)).append("。");
        expLife.setText(android.text.Html.fromHtml(sbLife.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        
        StringBuilder sbTime = new StringBuilder();
        sbTime.append(getShichenName(timeZhi)).append(" ").append(timeGan).append(timeZhi).append("(").append(shiGanWuXing).append(")\n");
        sbTime.append(getTimeFortune(timeZhi, zhiShiDoor, zhiFuStar));
        expTime.setText(sbTime.toString());
        
        expOverall.setText(getOverallAdviceSimple(isYangDun, ju, zhiFuStar, zhiShiDoor));
        
        if (expTianDiPanDesc != null) expTianDiPanDesc.setText(android.text.Html.fromHtml(getTianDiPanDesc(PALACE_NAMES, nineStars, PALACE_NAMES), android.text.Html.FROM_HTML_MODE_LEGACY));
        if (expPalacesDesc != null) expPalacesDesc.setText(android.text.Html.fromHtml(getPalacesDesc(PALACE_NAMES, nineStars, eightDoors, eightGods, luckData, tianPan, diPanTianGan, wangCui, DIRECTIONS), android.text.Html.FROM_HTML_MODE_LEGACY));

        // 九星 / 八门 / 八神 详细解释（已合并进主九宫，此处给出含义详解）
        if (expNineStarsDesc != null) {
            expNineStarsDesc.setText(android.text.Html.fromHtml(
                buildNineStarsDetail(nineStars, wangCui, PALACE_NAMES, DIRECTIONS, zhiFuStar, zhiFuPalace),
                android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        if (expEightDoorsDesc != null) {
            expEightDoorsDesc.setText(android.text.Html.fromHtml(
                buildEightDoorsDetail(eightDoors, wangCui, PALACE_NAMES, DIRECTIONS, zhiShiDoor, zhiShiPalace),
                android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        if (expGodsDesc != null) {
            expGodsDesc.setText(android.text.Html.fromHtml(
                buildEightGodsDetail(eightGods, PALACE_NAMES, DIRECTIONS),
                android.text.Html.FROM_HTML_MODE_LEGACY));
        }
        
        // 格局克应：十干克应八十一格 + 经典格局（三奇得使、玉女守门、天网四张等）+ 忌例
        if (expGejuKeying != null) {
            expGejuKeying.setText(android.text.Html.fromHtml(
                buildGeJuKeying(tianPan, diPanTianGan, eightDoors, eightGods, nineStars,
                                PALACE_NAMES, DIRECTIONS, dayGan, timeGan, timePillar,
                                zhiShiPalace, xunShou),
                android.text.Html.FROM_HTML_MODE_LEGACY));
        }

        if (expPaipanYiju != null) {
            expPaipanYiju.setText(android.text.Html.fromHtml(getPaipanYiju(yearPillar, monthPillar, dayPillar, timePillar, jieqi, isYangDun, ju, xunShou, zhiFuStar, zhiShiDoor, zhiFuPalace, zhiShiPalace), android.text.Html.FROM_HTML_MODE_LEGACY));
        }
    }
    
    private String getTianDiPanDesc(String[] palaces, String[] nineStars, String[] palaceNames) {
        StringBuilder desc = new StringBuilder();

        String[] directions = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};

        // 九宫：卦名、五行、方位、九星本位（《秘笈》：惟天禽无定位，寄西南而属中宫）
        String[][] palaceDesc = {
            {"坎", "水", "正北", "天蓬本位，主智主险"},
            {"坤", "土", "西南", "天芮本位，天禽寄此"},
            {"震", "木", "正东", "天冲本位，主动主长"},
            {"巽", "木", "东南", "天辅本位，主风主变"},
            {"中", "土", "中央", "天禽本位，寄于坤二"},
            {"乾", "金", "西北", "天心本位，主健主尊"},
            {"兑", "金", "正西", "天柱本位，主悦主泽"},
            {"艮", "土", "东北", "天任本位，主止主蓄"},
            {"离", "火", "正南", "天英本位，主明主礼"}
        };

        desc.append("<font color='#F3BA66'><b>天盘</b></font>");
        desc.append("<font color='#7C8C9C'>　九星、八门、八神与三奇六仪随时而转，附于地盘之上者。</font><br/>");
        desc.append("<font color='#F3BA66'><b>地盘（九宫）</b></font>");
        desc.append("<font color='#7C8C9C'>　《秘笈》曰：「夫星有阴阳，门有开阖，惟天禽则无定位，寄西南而属中宫。」</font><br/>");
        for (int i = 0; i < 9; i++) {
            if (!palaceNames[i].isEmpty() && !palaceNames[i].equals("--")) {
                String palace = palaceNames[i].substring(0, 1);
                desc.append("<font color='#F3BA66'>").append(palace).append("宫</font> ");
                desc.append("<font color='#98D8F0'>(").append(palaceDesc[i][1]).append("·").append(palaceDesc[i][2]).append(")</font> ");
                desc.append("<font color='#7C8C9C'>").append(palaceDesc[i][3]).append("</font>");
                if (i < 8) desc.append("<br/>");
            }
        }

        return desc.toString();
    }
    
    // 九星详细解释：依《奇门遁甲秘笈大全》卷二十二「九星吉凶诗诀」原文，辅以疏义。
    // 吉凶等级依《三元经》："辅禽心星为上吉，冲任次吉理须明；大凶天蓬与天芮，小凶天柱及天英。"
    private String buildNineStarsDetail(String[] nineStars, String[] wangCui,
            String[] PALACE_NAMES, String[] DIRECTIONS, String zhiFuStar, int zhiFuPalace) {
        // {星名, 五行, 三元经品级, 诗诀原文(前四句), 疏义}
        String[][] info = {
            {"天蓬", "水", "大凶", "天蓬主事异秋冬，用讼安边春夏功，嫁娶俱亡移徙火，入官险道门逢凶。",
             "蓬星属水，主讼争险陷。惟春夏用事、安边固守为功；贾商埋葬居行皆否，纵逢奇门亦减福。"},
            {"天芮", "土", "大凶", "芮星授道结交宜，作事征行不必为，盗贼忧惊伤小口，灾刑囚事被官羁。",
             "芮星属土，宜授道结交、求医问药；征行嫁娶营葬皆忌，大凶春夏、秋冬稍吉，纵得奇门福亦亏。"},
            {"天冲", "木", "次吉", "天冲报怨趁春温，万里威风胆气雄，不利秋冬春夏胜，贾商行徙入官迍。",
             "冲星属木，主报怨雪耻、出师振威。宜春夏而忌秋冬；造葬修方、嫁娶产育多迍。"},
            {"天辅", "木", "上吉", "天辅修身利造营，征赢春夏地开平，罪刑此出逢天赦，远出居官功亦成。",
             "辅星属木，主文教修身。营造征行、远出居官、嫁娶多儿皆成，惟谒求迁移稍逊。"},
            {"天禽", "土", "上吉", "天禽中主四时通，硬冲坚大有奇功，宜用智谋机活伏，祭神感应上官亨。",
             "禽星属土，居中无定位而寄坤，四时皆通。贾商嫁娶、行修造作，奇门加之尽亨通。"},
            {"天心", "金", "上吉", "心宿星机神道辉，求仙合药百为宜，入官嫁娶及移徙，造葬征行祭祀时。",
             "心星属金，主机谋神道。求仙合药、入官嫁娶、造葬征行皆宜；泰在秋冬，春夏则利君子而危小人。"},
            {"天柱", "金", "小凶", "天柱山方修造良，祀神嫁娶亦生光，藏形谨守斯为美，移徙征行却受殃。",
             "柱星属金，宜藏形谨守、山方修造；移徙征行受殃，营谋轻动、妄行相交主中伤。"},
            {"天任", "土", "次吉", "天任之宿属星仪，百事求谋利四时，造葬入官并请谒，行商娶祀吉迁移。",
             "任星属土，百事求谋四时皆利。造葬入官、请谒行商、嫁娶祭祀迁移俱吉，更喜气神旺相。"},
            {"天英", "火", "小凶", "天英之宿是天衢，远行饮宴乐愉愉，出入葬埋宜嫁娶，徙宫筑室祀商违。",
             "英星属火，主远行饮宴、出入葬埋；徙宫筑室、祭祀营商相违，主令慎勿轻加于客。"}
        };
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#CCB866'><b>九星</b></font>：随值符飞布九宫，各有所司。");
        sb.append("《三元经》曰：<font color='#7C8C9C'>「辅禽心星为上吉，冲任次吉理须明；")
          .append("大凶天蓬与天芮，小凶天柱及天英。」</font>");
        sb.append("本盘<b>值符</b>为 <font color='#3FA34D'><b>").append(zhiFuStar).append("星</b></font>，落<b>")
          .append(PALACE_NAMES[zhiFuPalace]).append("</b>宫，为九星之领袖。<br/><br/>");
        for (int i = 0; i < 9; i++) {
            String star = nineStars[i];
            String[] detail = null;
            for (String[] it : info) { if (it[0].equals(star)) { detail = it; break; } }
            if (detail == null) continue;
            String level = detail[2];
            String luckColor = "上吉".equals(level) ? "#3FA34D"
                    : ("次吉".equals(level) ? "#7FBF5F"
                    : ("小凶".equals(level) ? "#E6C46A" : "#E0593B"));
            boolean isZhiFu = star.equals(zhiFuStar);
            sb.append("<font color='#98D8F0'>").append(PALACE_NAMES[i]).append("·").append(DIRECTIONS[i]).append("</font>　");
            sb.append("<font color='").append(luckColor).append("'><b>").append(star).append("星</b></font>");
            sb.append("（").append(detail[1]).append("·").append(level).append("）");
            if (isZhiFu) sb.append(" <font color='#3FA34D'>★值符</font>");
            sb.append("<br/>");
            sb.append("<font color='#B9A05C'>").append(detail[3]).append("</font><br/>");
            sb.append("<font color='#7C8C9C'>疏：").append(detail[4]).append("</font><br/><br/>");
        }
        return sb.toString();
    }

    // 八门详细解释：依《奇门遁甲秘笈大全》卷二十二「八门吉凶诗诀」与卷五「八门克应」衢仙断语。
    // 吉凶以开、休、生为三吉门，死、惊、伤为凶门，杜、景为中平，本《统宗大全》八门主事歌之义。
    private String buildEightDoorsDetail(String[] eightDoors, String[] wangCui,
            String[] PALACE_NAMES, String[] DIRECTIONS, String zhiShiDoor, int zhiShiPalace) {
        // {门名, 五行, 吉凶, 诗诀原文(前四句), 疏义}
        String[][] info = {
            {"休", "水", "吉", "休门一一气盈室，富贵子孙田土吉，祭祀修营入宅基，赴官迁徙事周悉。",
             "休门属水，主休养谒贵。赴官迁徙、祭祀入宅、婚姻产育皆宜，为三吉门之一，百事最为良。"},
            {"生", "土", "吉", "生门八八气盈星，凶煞皆降尊土精，因待女财人寄物，从兹致富子孙兴。",
             "生门属土，主生发致富。嫁娶种萌、造作求财、出入经营俱吉，凶煞皆降，为求财第一吉门。"},
            {"伤", "木", "凶", "伤门气短数三三，寅卯旺方音角间，渔猎捕征搜索债，更宜赌博追亡还。",
             "伤门属木，主损伤争斗。惟宜渔猎捕捉、索债追亡；官司口舌、血光伤身，上官出行嫁娶大凶。"},
            {"杜", "木", "平", "杜门四四星凶恶，木性时方寅卯泊，闭堤绝水事封培，追邪伐盗并勾捉。",
             "杜门属木，为藏形之方。宜躲灾避难、隐伏逃藏、追邪伐盗；用动则防官刑财散，余事不利。"},
            {"景", "火", "平", "景门九九紫气盈，巳午旺南寅戌结，遣使上书能解危，求谋修造访寻谒。",
             "景门属火，主文书声名。惟宜上书献策、奏对选拔；献策求名、举科选士得之，余者不利。"},
            {"死", "土", "凶", "死门二二凶星逆，戊己坤艮方位即，穿猎渔网刑戳宜，送丧吊死葬埋益。",
             "死门属土，主肃杀衰败。惟宜吊死送丧、葬埋捕捉畋猎；所求不利不宜行，动见败亡官落职。"},
            {"惊", "金", "凶", "惊门无化气为逆，旺在庚辛申酉地，罗网张疑立狱讼，攻门刑击一齐到。",
             "惊门属金，主争讼惊疑。惟宜词讼捕捉、设疑伏兵；市贾营修皆可忌，致讼虚惊疾疫兴。"},
            {"开", "金", "吉", "开门六六气营奇，谒贵求谋利有为，立宅扦修官职进，外来财帛马牛肥。",
             "开门属金，主通达开启。谒贵求谋、立宅赴官、商贾畜产俱宜，金上庚辛秋月旺，为三吉门之首。"}
        };
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#CCB866'><b>八门</b></font>：随值使飞布，主人事吉凶宜忌。");
        sb.append("《统宗大全》八门主事歌：<font color='#7C8C9C'>「欲求财利往生方，葬猎须知死路强；")
          .append("征战远行开门吉，休门见贵最为良；捉贼惊门无不获，杜门无事好逃藏；")
          .append("索债须知伤上去，思量饮酒景门高。」</font>");
        sb.append("本盘<b>值使</b>为 <font color='#3FA34D'><b>").append(zhiShiDoor).append("门</b></font>，落<b>")
          .append(PALACE_NAMES[zhiShiPalace]).append("</b>宫，乃用事之要。<br/><br/>");
        for (int i = 0; i < 9; i++) {
            String door = eightDoors[i];
            if (door == null || door.isEmpty()) continue;
            String[] detail = null;
            for (String[] it : info) { if (it[0].equals(door)) { detail = it; break; } }
            if (detail == null) continue;
            String luckColor = "吉".equals(detail[2]) ? "#3FA34D" : ("凶".equals(detail[2]) ? "#E0593B" : "#E6C46A");
            boolean isZhiShi = door.equals(zhiShiDoor);
            sb.append("<font color='#98D8F0'>").append(PALACE_NAMES[i]).append("·").append(DIRECTIONS[i]).append("</font>　");
            sb.append("<font color='").append(luckColor).append("'><b>").append(door).append("门</b></font>");
            sb.append("（").append(detail[1]).append("·").append(detail[2]).append("）");
            if (isZhiShi) sb.append(" <font color='#3FA34D'>★值使</font>");
            sb.append("<br/>");
            sb.append("<font color='#B9A05C'>").append(detail[3]).append("</font><br/>");
            sb.append("<font color='#7C8C9C'>疏：").append(detail[4]).append("</font><br/><br/>");
        }
        return sb.toString();
    }

    // 八神详细解释：依《奇门遁甲秘笈大全》卷六「九神主论章」原文，辅以疏义。
    private String buildEightGodsDetail(String[] eightGods,
            String[] PALACE_NAMES, String[] DIRECTIONS) {
        // {神名, 吉凶, 九神主论章原文, 疏义}
        String[][] info = {
            {"值符", "吉", "值符者，诸神之元首，九星之领袖。其神所到之处，百恶消散，诸凶寂灭，至吉之神。所畏者，太白金星，忌入墓。",
             "值符所临，百恶消散。经云「急从神兮缓从门」，有急难宜弃门而从值符方下而行。"},
            {"螣蛇", "凶", "螣蛇者，丁火之气，其实属阴土，兑纳丁，其神性柔而口毒，专司惊恐、怪异、火妖蛊之事。",
             "螣蛇主虚惊怪异、口舌纠缠，事多反复变诈；宜防不宜进，忌妄动远行。"},
            {"太阴", "吉", "太阴者，西方之阴金也。其神好阴匿、暗昧、欺蔽、妾妇之事。",
             "太阴主荫庇暗助。宜密谋潜伏、避难藏形，经云「伏兵但向太阴位」。"},
            {"六合", "吉", "六合者，甲木之化气也，东方之阴木也。其神性和平，专司婚姻、交易、媒妁、和合之事。",
             "六合主和合欢乐。宜婚嫁交易、调解结盟，经云「若逢六合利逃形」。"},
            {"白虎", "凶", "白虎者，庚金也。统辖西方之威，其神好杀，专司兵戈、杀伐、争斗、疾病、死丧、道路之事。",
             "白虎主血光刑伤、道路死丧；宜武事镇煞，忌争斗出行，防损伤官非。"},
            {"玄武", "凶", "玄武者，水之精也。统辖北方之气，其神好阴谋、贼害，专司盗贼、逃亡之事。",
             "玄武主盗骗失脱。防小人、失物、口舌，凡事宜慎密，忌托付财货。"},
            {"九地", "平", "九地者，坤土也。其性好静，所主者，乃柔顺虚恭之事，亦操生杀之权，半吉半凶之神，畏克制，忌入墓，春夏则生，秋冬则杀。",
             "九地主沉稳潜藏。宜固守静养、埋伏藏兵、迟缓之事，经云「九地潜藏可立营」。"},
            {"九天", "吉", "九天者，乾金也。其体属金，乾纳甲壬，性刚而好动。所主者，名正言顺之事，值其令而无阻，至吉之神。若得门、得奇，万福咸集，即不得奇，亦不为凶。",
             "九天主飞扬显达。宜远行扬名、进攻开拓，经云「九天之上好扬兵」。"}
        };
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='#CCB866'><b>八神</b></font>：值符统七神，环绕九宫，各主气场。");
        sb.append("《秘笈》云：<font color='#7C8C9C'>「逆顺直符统七神，地宜伏匿天扬兵，太阴六合可回避，")
          .append("蛇远勾连田土争，白虎西方凶神杀，玄武盗贼防征途。」</font>");
        sb.append("又云：<font color='#7C8C9C'>「九天之上好扬兵，九地潜藏可立营，伏兵但向太阴位，")
          .append("若逢六合利逃形。」</font>余神随阴阳遁顺逆飞布。<br/><br/>");
        for (int i = 0; i < 9; i++) {
            String god = eightGods[i];
            if (god == null || god.isEmpty()) continue;
            String[] detail = null;
            for (String[] it : info) { if (it[0].equals(god)) { detail = it; break; } }
            if (detail == null) continue;
            String luckColor = "吉".equals(detail[1]) ? "#3FA34D" : ("凶".equals(detail[1]) ? "#E0593B" : "#E6C46A");
            sb.append("<font color='#98D8F0'>").append(PALACE_NAMES[i]).append("·").append(DIRECTIONS[i]).append("</font>　");
            sb.append("<font color='").append(luckColor).append("'><b>").append(god).append("</b></font>");
            sb.append("（").append(detail[1]).append("）").append("<br/>");
            sb.append("<font color='#B9A05C'>").append(detail[2]).append("</font><br/>");
            sb.append("<font color='#7C8C9C'>疏：").append(detail[3]).append("</font><br/><br/>");
        }
        return sb.toString();
    }

    // ==================== 十干克应格局（《奇门遁甲秘笈大全》原文） ====================

    /**
     * 十干克应：天盘干加临地盘干所成之格。
     * 原文：「甲加丙为青龙反首，而动作无阻。丙加甲为飞鸟跌穴，而运用有成。
     *       乙加辛是青龙逃走，而财物废坠。辛加乙是白虎猖狂，而身体毁伤。
     *       丁加癸为朱雀投江，而讼狱所由起也。癸加丁为螣蛇夭矫，而忧惶之事必至也。
     *       天地大格，庚下加癸，天地小格，壬上加庚。庚加于己，士卒死于中途；己加于庚，将兵厄于危路。
     *       庚加于丙，是为太白入荧惑，而贼必来；丙加于庚，是为丙火入金乡，而贼必退也。」
     * 注：甲遁于六仪，故「甲加丙」即天盘戊加地盘丙；「丙加甲」即天盘丙加地盘戊。
     * {天盘干, 地盘干, 格名, 吉凶, 原文}
     */
        private static final String[][] KE_YING_GE = QiMenGeJu.KE_YING_GE;

    /** 查十干克应格；无格则返回 null。 */
    private String[] findKeYingGe(String tianGan, String diGan) {
        return QiMenGeJu.findKeYingGe(tianGan, diGan);
    }

    /**
     * 六仪击刑：天盘六仪加临所刑之宫。
     * 歌曰：「六仪击刑为太凶，甲子值符愁向东。戌刑在未申在寅，寅巳辰辰午刑午。」
     * 宫序：0坎 1坤 2震 3巽 4中 5乾 6兑 7艮 8离
     */
        private static final String[][] JI_XING = QiMenGeJu.JI_XING;
    private static final int[] JI_XING_PALACE = QiMenGeJu.JI_XING_PALACE;

    /**
     * 三奇入墓：歌曰「乙日哪堪见未时，丙丁入火火墓戌」，即乙奇入坤二（未）、丙丁奇入乾六（戌）。
     */
    private boolean isQiRuMu(String tianGan, int palace) {
        return QiMenGeJu.isQiRuMu(tianGan, palace);
    }

    /** 门迫：门五行克宫五行，谓之迫。宫五行序：水土木木土金金土火；门五行：休水 生土 伤木 杜木 景火 死土 惊金 开金。 */
    private boolean isMenPo(String door, int palace) {
        return QiMenGeJu.isMenPo(door, palace);
    }

    /** 五不遇时：时干克日干。原文「时干克日，为五不遇，而定损其明」。 */
    private boolean isWuBuYuShi(String dayGan, String timeGan) {
        return QiMenGeJu.isWuBuYuShi(dayGan, timeGan);
    }

    /** 地盘九星本位：坎天蓬 坤天芮 震天冲 巽天辅 中天禽 乾天心 兑天柱 艮天任 离天英。 */
    private static final String[] BEN_WEI_STAR = QiMenGeJu.BEN_WEI_STAR;
    /** 九宫对冲：坎↔离 坤↔艮 震↔兑 巽↔乾 中↔中。 */
    private static final int[] DUI_CHONG = QiMenGeJu.DUI_CHONG;
    /** 六甲遁仪：甲子戊 甲戌己 甲申庚 甲午辛 甲辰壬 甲寅癸。 */
        private static final String[][] LIU_JIA_YI = QiMenGeJu.LIU_JIA_YI;
    /** 时干入墓：歌曰「又有时干入墓宫，课中时下忌相逢。戊戌壬辰兼丙戌，癸未丁丑己同凶。」 */
    private static final String[] SHI_GAN_RU_MU = QiMenGeJu.SHI_GAN_RU_MU;
    /**
     * 三奇得使：《烟波钓叟歌》「乙逢犬马丙鼠猴，六丁玉女骑龙虎」。
     * 甲戌甲午乙为使，甲子甲申丙为使，甲辰甲寅丁为使。
     * {天盘奇, 地盘仪, 格名, 六甲}
     */
        private static final String[][] SAN_QI_DE_SHI = QiMenGeJu.SAN_QI_DE_SHI;
    /** 得使而带微疵者。原文「乙奇加甲午辛乃青龙逃走，丙奇加甲申庚乃荧入太白，丁奇加甲寅癸乃朱雀投江，凡此三者尚有微疵不吉。」 */
        private static final String[][] DE_SHI_WEI_CI = QiMenGeJu.DE_SHI_WEI_CI;
    /**
     * 三奇贵人升殿 / 三奇受制。
     * 升殿：乙奇临震（日出扶桑）、丙奇临离（月照端门）、丁奇临兑（贵人升殿）。
     * 受制：乙奇临乾（木入金乡）、丙奇临坎（火入水乡）。王璋曰「此谓三奇受制，万事不可举也。」
     */
        private static final String[][] SAN_QI_LIN_GONG = QiMenGeJu.SAN_QI_LIN_GONG;

    /** 由旬首（如「甲子」）取其遁仪（如「戊」）。 */
    private String liuJiaYi(String xunShou) {
        return QiMenGeJu.liuJiaYi(xunShou);
    }

    /** 吉凶配色：吉绿、凶红、平黄。 */
    private String luckColor(String luck) {
        return QiMenGeJu.luckColor(luck);
    }

    /**
     * 构建「格局克应」卡片。
     * 一、十干克应（天盘干加地盘干，凡八十一格）逐宫列之；
     * 二、经典格局：三奇得使、玉女守门、天网四张、伏宫、飞宫、悖格、伏吟、反吟、三奇升殿受制；
     * 三、忌例：六仪击刑、门迫、三奇入墓、五不遇时、时干入墓。
     */
    private String buildGeJuKeying(String[] tianPan, String[] diPan, String[] doors, String[] eightGods,
                                   String[] nineStars, String[] PALACE_NAMES, String[] DIRECTIONS,
                                   String dayGan, String timeGan, String timePillar,
                                   int zhiShiPalace, String xunShou) {
        StringBuilder sb = new StringBuilder();
        java.util.ArrayList<String> jiList = new java.util.ArrayList<>();
        java.util.ArrayList<String> xiongList = new java.util.ArrayList<>();

        sb.append("<font color='#CCB866'><b>一、十干克应</b></font><br/>");
        sb.append("<font color='#7C8C9C'>《秘笈》曰：「甲加丙为青龙反首，而动作无阻；丙加甲为飞鸟跌穴，")
          .append("而运用有成；乙加辛是青龙逃走；辛加乙是白虎猖狂；丁加癸为朱雀投江；癸加丁为螣蛇夭矫。」</font><br/><br/>");

        for (int i = 0; i < 9; i++) {
            String tp = tianPan[i];
            String dp = diPan[i];
            if (tp == null || dp == null) continue;
            String[] ge = findKeYingGe(tp, dp);
            boolean isPo = isMenPo(doors[i], i);
            boolean isMu = isQiRuMu(tp, i);

            sb.append("<font color='#98D8F0'>").append(PALACE_NAMES[i]).append("·").append(DIRECTIONS[i]).append("</font>　");
            sb.append("<font color='#E6C46A'>").append(tp).append("加").append(dp).append("</font>");
            if (ge != null) {
                sb.append("　<font color='").append(luckColor(ge[3])).append("'><b>").append(ge[2])
                  .append("</b>（").append(ge[3]).append("）</font>");
                if ("吉".equals(ge[3])) {
                    if (!jiList.contains(ge[2])) jiList.add(ge[2]);
                } else if ("凶".equals(ge[3])) {
                    if (!xiongList.contains(ge[2])) xiongList.add(ge[2]);
                }
                sb.append("<br/><font color='#7C8C9C'>　").append(ge[4]).append("</font>");
            }
            if (isMu) {
                sb.append("<br/>　<font color='#E0593B'>三奇入墓</font><font color='#7C8C9C'>　乙奇临坤、丙丁临乾，入墓则不可举兵</font>");
                if (!xiongList.contains("三奇入墓")) xiongList.add("三奇入墓");
            }
            if (isPo) {
                sb.append("<br/>　<font color='#E0593B'>门迫</font><font color='#7C8C9C'>　门制其宫为之迫，吉门被迫则吉事不成</font>");
                if (!xiongList.contains("门迫")) xiongList.add("门迫");
            }
            sb.append("<br/><br/>");
        }

        // ============ 二、经典格局 ============
        StringBuilder jd = new StringBuilder();

        // 九遁（天遁、地遁、人遁、神遁、鬼遁、风遁、云遁、龙遁、虎遁，皆为大吉）
        for (int i = 0; i < 9; i++) {
            String dun = QiMenGeJu.detectJiuDun(doors != null ? doors[i] : null, tianPan[i], diPan[i],
                                                eightGods != null ? eightGods[i] : null, i);
            if (dun == null) continue;
            String[] d = QiMenGeJu.jiuDunDetail(dun);
            jd.append("<font color='#3FA34D'><b>").append(dun).append("</b></font>　")
              .append(PALACE_NAMES[i]).append("宫<br/>");
            if (d != null) {
                jd.append("<font color='#7C8C9C'>　").append(d[1]).append("，").append(d[2]).append("。</font>");
            }
            jd.append("<br/><br/>");
            if (!jiList.contains(dun)) jiList.add(dun);
        }

        // 三奇得使
        for (int i = 0; i < 9; i++) {
            String tp = tianPan[i], dp = diPan[i];
            if (tp == null || dp == null) continue;
            for (String[] s : SAN_QI_DE_SHI) {
                if (s[0].equals(tp) && s[1].equals(dp)) {
                    jd.append("<font color='#3FA34D'><b>").append(s[2]).append("</b></font>　")
                      .append(PALACE_NAMES[i]).append("宫　").append(tp).append("加").append(dp)
                      .append("（").append(s[3]).append("）<br/>");
                    jd.append("<font color='#7C8C9C'>　歌曰：「三奇得使诚堪使，六甲遇之非小补。」</font>");
                    for (String[] w : DE_SHI_WEI_CI) {
                        if (w[0].equals(tp) && w[1].equals(dp)) {
                            jd.append("<br/><font color='#E6C46A'>　微疵：此格即").append(w[2])
                              .append("，原文谓「尚有微疵不吉，如遇本旬直符同临其上，方可用之而吉也」。</font>");
                        }
                    }
                    jd.append("<br/><br/>");
                    if (!jiList.contains(s[2])) jiList.add(s[2]);
                }
            }
        }

        // 玉女守门：地盘六丁守直使之门
        if (zhiShiPalace >= 0 && zhiShiPalace < 9 && "丁".equals(diPan[zhiShiPalace])) {
            jd.append("<font color='#3FA34D'><b>玉女守门</b></font>　值使落").append(PALACE_NAMES[zhiShiPalace])
              .append("宫，地盘为丁<br/>");
            jd.append("<font color='#7C8C9C'>　歌曰：「又有三奇游六仪，号为玉女守门扉。若作阴私和合事，请君但向此中推。」")
              .append("丁为玉女而会天乙直使之门，利谋秘密阴私、营建宴会和乐之事。</font><br/><br/>");
            if (!jiList.contains("玉女守门")) jiList.add("玉女守门");
        }

        // 天网四张：天上六癸所加，一二三四宫为低，六七八九宫为高
        for (int i = 0; i < 9; i++) {
            if (!"癸".equals(tianPan[i])) continue;
            boolean gao = (i != 4) && (i + 1) >= 6;
            jd.append("<font color='#E0593B'><b>天网四张</b></font>　").append(PALACE_NAMES[i])
              .append("宫　六癸临").append(gao ? "六七八九宫，其网高" : "一二三四宫，其网低").append("<br/>");
            jd.append("<font color='#7C8C9C'>　歌曰：「天网四张无路走，一二网低有路踪。」")
              .append(gao ? "网高则万事不宜，虽合奇门吉宿亦不可用。" : "网低尚可匍匐而出，然亦宜隐伏。")
              .append("</font><br/><br/>");
            if (!xiongList.contains("天网四张")) xiongList.add("天网四张");
        }

        // 伏宫格、飞宫格（以本旬遁仪为直符）
        String yi = liuJiaYi(xunShou);
        if (yi != null) {
            for (int i = 0; i < 9; i++) {
                String tp = tianPan[i], dp = diPan[i];
                if (tp == null || dp == null) continue;
                if ("庚".equals(tp) && yi.equals(dp)) {
                    jd.append("<font color='#E0593B'><b>伏宫格</b></font>　").append(PALACE_NAMES[i])
                      .append("宫　六庚加直符<br/>");
                    jd.append("<font color='#7C8C9C'>　歌曰：「庚加直符天乙伏，交锋多不利，为客少成功。」主客皆凶。</font><br/><br/>");
                    if (!xiongList.contains("伏宫格")) xiongList.add("伏宫格");
                }
                if (yi.equals(tp) && "庚".equals(dp)) {
                    jd.append("<font color='#E0593B'><b>飞宫格</b></font>　").append(PALACE_NAMES[i])
                      .append("宫　直符加六庚<br/>");
                    jd.append("<font color='#7C8C9C'>　歌曰：「直符加庚天乙飞，两敌不堪争，为主似不赢。」出则大将遭擒。</font><br/><br/>");
                    if (!xiongList.contains("飞宫格")) xiongList.add("飞宫格");
                }
            }
        }

        // 悖格（勃格）：丙为勃，庚为格；丙加庚、丙加丙、丙加直符皆为悖
        for (int i = 0; i < 9; i++) {
            String tp = tianPan[i], dp = diPan[i];
            if (tp == null || dp == null) continue;
            if ("丙".equals(tp) && ("庚".equals(dp) || "丙".equals(dp) || (yi != null && yi.equals(dp)))) {
                jd.append("<font color='#E0593B'><b>悖格</b></font>　").append(PALACE_NAMES[i])
                  .append("宫　天丙加地").append(dp).append("<br/>");
                jd.append("<font color='#7C8C9C'>　歌曰：「丙为勃兮庚为格，格则不通勃乱逆。」主纲纪紊乱，百事不宜。</font><br/><br/>");
                if (!xiongList.contains("悖格")) xiongList.add("悖格");
            }
        }

        // 伏吟、反吟（九星）
        StringBuilder fy = new StringBuilder(), fany = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            String star = nineStars[i];
            if (star == null || star.isEmpty()) continue;
            if (star.equals(BEN_WEI_STAR[i])) {
                if (fy.length() > 0) fy.append("、");
                fy.append(PALACE_NAMES[i]);
            }
            if (star.equals(BEN_WEI_STAR[DUI_CHONG[i]]) && DUI_CHONG[i] != i) {
                if (fany.length() > 0) fany.append("、");
                fany.append(PALACE_NAMES[i]);
            }
        }
        if (fy.length() > 0) {
            jd.append("<font color='#E0593B'><b>九星伏吟</b></font>　").append(fy).append("<br/>");
            jd.append("<font color='#7C8C9C'>　歌曰：「就中伏吟为最凶，天蓬加着地天蓬。」主孝服损人口，惟宜收敛货财。</font><br/><br/>");
            if (!xiongList.contains("九星伏吟")) xiongList.add("九星伏吟");
        }
        if (fany.length() > 0) {
            jd.append("<font color='#E0593B'><b>九星反吟</b></font>　").append(fany).append("<br/>");
            jd.append("<font color='#7C8C9C'>　歌曰：「天蓬若到天英上，须知即是反吟宫。」不利举兵动众，惟宜散恤仓库之事。</font><br/><br/>");
            if (!xiongList.contains("九星反吟")) xiongList.add("九星反吟");
        }

        // 三奇升殿 / 三奇受制
        for (int i = 0; i < 9; i++) {
            String tp = tianPan[i];
            if (tp == null) continue;
            for (String[] s : SAN_QI_LIN_GONG) {
                if (s[0].equals(tp) && Integer.parseInt(s[1]) == i) {
                    jd.append("<font color='").append(luckColor(s[3])).append("'><b>").append(s[2])
                      .append("</b></font>　").append(PALACE_NAMES[i]).append("宫　").append(tp).append("奇临此<br/>");
                    jd.append("<font color='#7C8C9C'>　").append(s[4]).append("</font><br/><br/>");
                    if ("吉".equals(s[3])) {
                        if (!jiList.contains(s[2])) jiList.add(s[2]);
                    } else {
                        if (!xiongList.contains(s[2])) xiongList.add(s[2]);
                    }
                }
            }
        }

        if (jd.length() > 0) {
            sb.append("<font color='#CCB866'><b>二、经典格局</b></font><br/><br/>").append(jd);
        }

        // ============ 三、忌例 ============
        StringBuilder ji = new StringBuilder();

        // 六仪击刑
        StringBuilder jx = new StringBuilder();
        for (int k = 0; k < JI_XING.length; k++) {
            int p = JI_XING_PALACE[k];
            if (JI_XING[k][0].equals(tianPan[p])) {
                if (jx.length() > 0) jx.append("、");
                jx.append(JI_XING[k][0]).append("加").append(JI_XING[k][1]).append("（").append(JI_XING[k][2]).append("）");
            }
        }
        if (jx.length() > 0) {
            ji.append("<font color='#E0593B'><b>六仪击刑</b></font>：").append(jx).append("<br/>");
            ji.append("<font color='#7C8C9C'>　歌曰：「六仪击刑何太凶，甲子直符愁向东。」其时极凶，不可用事。</font><br/><br/>");
            if (!xiongList.contains("六仪击刑")) xiongList.add("六仪击刑");
        }

        // 五不遇时
        if (isWuBuYuShi(dayGan, timeGan)) {
            ji.append("<font color='#E0593B'><b>五不遇时</b></font>：时干 ").append(timeGan)
              .append(" 克日干 ").append(dayGan).append("<br/>");
            ji.append("<font color='#7C8C9C'>　歌曰：「五不遇时龙不精，号为日月损光明。」纵有奇门，不可行百事。</font><br/><br/>");
            if (!xiongList.contains("五不遇时")) xiongList.add("五不遇时");
        }

        // 时干入墓
        if (timePillar != null) {
            for (String s : SHI_GAN_RU_MU) {
                if (s.equals(timePillar)) {
                    ji.append("<font color='#E0593B'><b>时干入墓</b></font>：时柱 ").append(timePillar).append("<br/>");
                    ji.append("<font color='#7C8C9C'>　歌曰：「又有时干入墓宫，课中时下忌相逢。戊戌壬辰兼丙戌，癸未丁丑己同凶。」</font><br/><br/>");
                    if (!xiongList.contains("时干入墓")) xiongList.add("时干入墓");
                    break;
                }
            }
        }

        if (ji.length() > 0) {
            sb.append("<font color='#CCB866'><b>三、忌例</b></font><br/><br/>").append(ji);
        }

        // ============ 汇总 ============
        sb.append("<font color='#CCB866'><b>四、吉凶总断</b></font><br/>");
        if (!jiList.isEmpty()) {
            sb.append("<font color='#3FA34D'>吉格</font>：")
              .append(android.text.TextUtils.join("、", jiList)).append("<br/>");
        }
        if (!xiongList.isEmpty()) {
            sb.append("<font color='#E0593B'>凶格</font>：")
              .append(android.text.TextUtils.join("、", xiongList)).append("<br/>");
        }
        if (jiList.isEmpty() && xiongList.isEmpty()) {
            sb.append("<font color='#7C8C9C'>本盘未见显著吉格凶格，可依星门神之常理而行。</font><br/>");
        }
        sb.append("<br/><font color='#7C8C9C'>《烟波钓叟歌》：「吉门仍又合三奇，大概须云百事宜；")
          .append("其间倘或多凶吉，一篇未足究精微。」又云：「更合从旁加检点，临宫不可有微疵。」</font>");
        return sb.toString();
    }

    private String getNineStarsDesc(String[] stars) {
        // 品级依《三元经》："辅禽心星为上吉，冲任次吉理须明；大凶天蓬与天芮，小凶天柱及天英。"
        String[][] starInfo = {
            {"天蓬", "#E0593B", "水·大凶，主讼争险陷"},
            {"天芮", "#E0593B", "土·大凶，主疾病，宜求医"},
            {"天冲", "#7FBF5F", "木·次吉，主报怨雪耻"},
            {"天辅", "#3FA34D", "木·上吉，主文教修身"},
            {"天英", "#E6C46A", "火·小凶，主远行饮宴"},
            {"天禽", "#3FA34D", "土·上吉，居中四时通"},
            {"天柱", "#E6C46A", "金·小凶，宜藏形谨守"},
            {"天心", "#3FA34D", "金·上吉，主机谋合药"},
            {"天任", "#7FBF5F", "土·次吉，百事求谋利"}
        };
        StringBuilder desc = new StringBuilder();
        for (String[] info : starInfo) {
            desc.append("<font color='").append(info[1]).append("'>").append(info[0]).append("</font>")
                .append("<font color='#98D8F0'> ").append(info[2]).append("</font>　");
        }
        return desc.toString().trim();
    }
    
    private String getEightDoorsDesc(String[] doors) {
        String[][] doorInfo = {
            {"休", "#3FA34D", "水·吉门，赴官迁徙"},
            {"生", "#3FA34D", "土·吉门，求财造作"},
            {"伤", "#E0593B", "木·凶门，渔猎索债"},
            {"杜", "#E6C46A", "木·平门，藏形避难"},
            {"景", "#E6C46A", "火·平门，上书求名"},
            {"死", "#E0593B", "土·凶门，葬埋捕捉"},
            {"惊", "#E0593B", "金·凶门，词讼捕捉"},
            {"开", "#3FA34D", "金·吉门，谒贵求谋"}
        };
        StringBuilder desc = new StringBuilder();
        for (String[] info : doorInfo) {
            desc.append("<font color='").append(info[1]).append("'>").append(info[0]).append("门</font>")
                .append("<font color='#98D8F0'> ").append(info[2]).append("</font>　");
        }
        return desc.toString().trim();
    }
    
    private String getEightGodsDesc(String[] gods) {
        // 依《秘笈大全》卷六「九神主论章」
        String[][] godInfo = {
            {"值符", "#3FA34D", "诸神元首，百恶消散"},
            {"螣蛇", "#E0593B", "性柔口毒，惊恐怪异"},
            {"太阴", "#3FA34D", "好阴匿，宜潜伏"},
            {"六合", "#3FA34D", "性和平，婚姻和合"},
            {"白虎", "#E0593B", "好杀，兵戈死丧"},
            {"玄武", "#E0593B", "好阴谋，盗贼逃亡"},
            {"九地", "#E6C46A", "好静，柔顺潜藏"},
            {"九天", "#3FA34D", "性刚好动，扬兵显达"}
        };
        StringBuilder desc = new StringBuilder();
        for (String[] info : godInfo) {
            desc.append("<font color='").append(info[1]).append("'>").append(info[0]).append("</font>")
                .append("<font color='#98D8F0'> ").append(info[2]).append("</font>　");
        }
        return desc.toString().trim();
    }
    
    private String getPalacesDesc(String[] palaces, String[] stars, String[] doors, String[] gods, String[] lucks,
            String[] tianPan, String[] diPanTianGan, String[] wangCui, String[] DIRECTIONS) {
        String[] palaceElement = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
        StringBuilder desc = new StringBuilder();
        desc.append("<font color='#7C8C9C'>逐宫列天盘地盘之干、星门神与旺衰吉凶宜忌，附十干克应之格。</font><br/><br/>");
        for (int i = 0; i < 9; i++) {
            String palace = palaces[i];
            String star = stars[i];
            String door = doors[i];
            String god = gods[i];
            String luck = lucks[i];
            String dir = DIRECTIONS[i];
            String element = palaceElement[i];
            String tp = tianPan[i];
            String dp = diPanTianGan[i];
            String wang = wangCui[i];

            desc.append("<font color='#98D8F0'><b>").append(palace).append("·").append(dir).append("</b></font>");
            desc.append("（属").append(element).append("）<br/>");

            desc.append("天盘 <font color='#E6C46A'>").append(tp).append("</font> ｜ 地盘 <font color='#E6C46A'>").append(dp).append("</font>");
            if (star != null && !star.isEmpty()) {
                desc.append(" ｜ <font color='#E6C46A'>").append(star).append("星</font>");
                if (wang != null && !wang.isEmpty()) desc.append("(").append(wang).append(")");
            }
            if (door != null && !door.isEmpty()) {
                desc.append(" ｜ <font color='#3FA34D'>").append(door).append("门</font>");
            }
            if (god != null && !god.isEmpty()) {
                desc.append(" ｜ <font color='#DDA0DD'>").append(god).append("</font>");
            }
            desc.append("<br/>");

            String luckColor = luck.contains("吉") ? "#3FA34D" : (luck.contains("凶") ? "#E0593B" : "#7C8C9C");
            desc.append("吉凶：<font color='").append(luckColor).append("'><b>").append(luck).append("</b></font>　");

            // 宜忌依《秘笈大全》八门诗诀与八门克应衢仙断语
            String yiji;
            if (door == null || door.isEmpty()) {
                yiji = "中宫无门，寄坤而用";
            } else if (door.equals("开")) yiji = "宜谒贵求谋、立宅赴官、商贾出行";
            else if (door.equals("休")) yiji = "宜赴官迁徙、祭祀入宅、婚姻产育";
            else if (door.equals("生")) yiji = "宜嫁娶造作、求财营商、出入经营";
            else if (door.equals("伤")) yiji = "惟宜渔猎捕捉、索债追亡，余皆不利";
            else if (door.equals("杜")) yiji = "宜躲灾避难、隐伏逃藏、追邪伐盗";
            else if (door.equals("景")) yiji = "宜上书献策、奏对选拔、求名举科";
            else if (door.equals("死")) yiji = "惟宜吊死送丧、葬埋捕捉畋猎";
            else if (door.equals("惊")) yiji = "惟宜词讼捕捉、设疑伏兵";
            else yiji = "";

            desc.append("<font color='#7C8C9C'>解曰：").append(palace).append("属").append(element);
            if (!yiji.isEmpty()) desc.append("，").append(yiji);
            desc.append("。</font>");

            String[] ge = findKeYingGe(tp, dp);
            if (ge != null) {
                String geColor = luckColor(ge[3]);
                desc.append("<br/><font color='#7C8C9C'>格曰：</font><font color='").append(geColor)
                    .append("'><b>").append(ge[2]).append("</b>（").append(ge[3]).append("）</font>")
                    .append("<font color='#7C8C9C'>　").append(ge[4]).append("</font>");
            }
            if (isMenPo(door, i)) {
                desc.append("<br/><font color='#E0593B'>门迫</font>")
                    .append("<font color='#7C8C9C'>　门克其宫，吉门被迫吉不全，凶门被迫凶更烈。</font>");
            }
            if (isQiRuMu(tp, i)) {
                desc.append("<br/><font color='#E0593B'>三奇入墓</font>")
                    .append("<font color='#7C8C9C'>　歌曰「乙日哪堪见未时，丙丁入火火墓戌」，此时诸事不须为。</font>");
            }
            desc.append("<br/><br/>");
        }
        return desc.toString();
    }
    
    /** 日时关系：《秘笈》"时干克日，为五不遇，而定损其明"。 */
    private String getRiShiRelationship(String riGan, String shiGan) {
        if (riGan == null || shiGan == null) return "日时未明";
        // 五不遇时：时干克日干，百事不宜
        if (isWuBuYuShi(riGan, shiGan)) {
            return "五不遇时（时干克日干），定损其明，百事不宜";
        }
        if (riGan.equals(shiGan)) {
            return "日时比和，主客协和，事易成";
        }
        String riWuxing = QiMenCalculator.getWuXing(riGan);
        String shiWuxing = QiMenCalculator.getWuXing(shiGan);

        if (QiMenCalculator.isSheng(shiWuxing, riWuxing)) {
            return "时生日，事生我，事半而功倍，易得外助";
        }
        if (QiMenCalculator.isSheng(riWuxing, shiWuxing)) {
            return "日生时，我生事，须奋勉乃成";
        }
        if (QiMenCalculator.isKe(riWuxing, shiWuxing)) {
            return "日克时，我克事，用力多而功少";
        }
        return "日时气数持平，循常以动";
    }
    
    private String getCareerAdvice(String door, String star) {
        if (door == null) return "谨慎行事";
        switch (door) {
            case "开": return "乘势进取，宜谋大事";
            case "生": return "把握机缘，百事可成";
            case "休": return "蓄力进修，厚积薄发";
            case "景": return "展才扬名，宜见贵人";
            case "伤": return "稳守待时，防人侵损";
            case "杜": return "静守待变，慎言笃行";
            case "死": return "守不宜攻，防失机宜";
            case "惊": return "慎言慎行，低调自处";
            default: return "按部就班";
        }
    }

    private String getWealthAdvice(String door, String star) {
        if (door == null) return "谨慎理财";
        switch (door) {
            case "生": return "求财置业，利市可期";
            case "开": return "广开财源，可大可久";
            case "休": return "稳健守财，储蓄为宜";
            case "景": return "量力而行，见好即收";
            case "伤": return "不宜投资，守财为主";
            case "杜": return "静观其变，待时而动";
            case "死": return "守财防耗，切忌妄举";
            case "惊": return "不宜借贷，谨防牵连";
            default: return "稳健理财";
        }
    }

    private String getRelationshipAdvice(String door, String star) {
        if (door == null) return "谨慎交往";
        switch (door) {
            case "休": return "和气致祥，宜通情愫";
            case "生": return "利于表白求婚";
            case "开": return "广结善缘，得贵人助";
            case "景": return "展露风采，宜于社交";
            case "惊": return "防口舌风波，冷静处之";
            case "伤": return "克制情绪，免起争端";
            case "死": return "宜反思修复，不可强求";
            case "杜": return "主动沟通，消除隔阂";
            default: return "顺其自然";
        }
    }

    private String getHealthAdvice(String door, String star) {
        if (door == null) return "注意保养";
        switch (door) {
            case "休": return "调养休息，劳逸有节";
            case "生": return "适度运动，培补元气";
            case "开": return "户外舒展，气血通畅";
            case "死": return "防病于未然，宜作检查";
            case "伤": return "防意外损伤，行事宜慎";
            case "景": return "清淡降火，忌于熬夜";
            case "杜": return "舒解郁结，宽心为要";
            case "惊": return "安神定志，静心为宜";
            default: return "保持良好习惯";
        }
    }

    private String getStudyAdvice(String door, String star) {
        if (door == null) return "勤奋学习";
        switch (door) {
            case "景": return "文思畅达，宜应科举";
            case "开": return "拓展见闻，进业修德";
            case "生": return "备考复习，技艺精进";
            case "休": return "静心温故，涵养身心";
            case "杜": return "深思力行，克服滞碍";
            case "伤": return "贵在有恒，防半途废";
            case "死": return "调整心法，另辟蹊径";
            case "惊": return "平心静气，从容应对";
            default: return "循序渐进";
        }
    }

    private String getTravelAdvice(String door, String star) {
        if (door == null) return "谨慎出行";
        switch (door) {
            case "开": return "出行顺利，宜远行游历";
            case "休": return "休闲出行，度假为宜";
            case "生": return "出行吉利，求财称意";
            case "景": return "观光游览，访古寻幽";
            case "惊": return "防延误阻滞，行宜谨慎";
            case "伤": return "防意外损伤，慎于出行";
            case "死": return "不宜远行，居家为安";
            case "杜": return "出行受阻，宜另择期";
            default: return "出行平稳，注意安全";
        }
    }

    private String getDietAdvice(String door, String star) {
        if (door == null) return "饮食清淡";
        switch (door) {
            case "生": return "进补养生，五味调和";
            case "休": return "清淡素食，调理脾胃";
            case "开": return "宾朋欢叙，饮食有节";
            case "景": return "清热降火，忌于辛辣";
            case "死": return "饮食宜慎，注意卫生";
            case "伤": return "节制饮食，忌于暴饮";
            case "惊": return "饮食有常，安神为先";
            case "杜": return "家常便饭，简单为宜";
            default: return "饮食规律，营养均衡";
        }
    }

    private String getSocialAdvice(String door, String star) {
        if (door == null) return "谨慎交友";
        switch (door) {
            case "开": return "广结善缘，宜交贵人";
            case "休": return "维系旧友，家庭欢聚";
            case "生": return "和合共赢，宜结同盟";
            case "景": return "展露风采，宜与雅集";
            case "惊": return "防口舌是非，少言多行";
            case "伤": return "人际易紧，宜守低调";
            case "死": return "减少应酬，不宜聚会";
            case "杜": return "社交受阻，宜主动沟通";
            default: return "社交平稳，顺其自然";
        }
    }

    private String getMindAdvice(String door, String star) {
        if (door == null) return "保持平和";
        switch (door) {
            case "休": return "修身养性，以静制动";
            case "生": return "持满戒盈，顺势而发";
            case "开": return "开拓胸襟，见机而作";
            case "景": return "保持热忱，文思畅达";
            case "死": return "知止不殆，另觅生机";
            case "伤": return "制怒守静，忍而后动";
            case "惊": return "安神定志，勿为所扰";
            case "杜": return "守时待变，徐图后进";
            default: return "保持平常心，顺其自然";
        }
    }
    
    private String getPalaceTip(int index, String palace, String star, String door, String god) {
        StringBuilder tip = new StringBuilder();
        
        switch (palace) {
            case "坎": tip.append("北方水"); break;
            case "坤": tip.append("西南土"); break;
            case "震": tip.append("东方木"); break;
            case "巽": tip.append("东南木"); break;
            case "中": tip.append("中央土"); break;
            case "乾": tip.append("西北金"); break;
            case "兑": tip.append("西方金"); break;
            case "艮": tip.append("东北土"); break;
            case "离": tip.append("南方火"); break;
        }
        
        tip.append("·");
        
        if (door != null && !door.isEmpty()) {
            switch (door) {
                case "开": tip.append("宜谒贵求谋"); break;
                case "生": tip.append("宜求财造作"); break;
                case "休": tip.append("宜赴官迁徙"); break;
                case "景": tip.append("宜上书求名"); break;
                case "伤": tip.append("防损伤争斗"); break;
                case "杜": tip.append("宜藏形避难"); break;
                case "死": tip.append("防衰败收敛"); break;
                case "惊": tip.append("防惊疑词讼"); break;
            }
        } else {
            tip.append("中宫无门");
        }
        
        if (star != null && !star.isEmpty()) {
            tip.append("·");
            if (star.equals("天辅") || star.equals("天心")) {
                tip.append("吉");
            } else if (star.equals("天蓬") || star.equals("天任")) {
                tip.append("中");
            } else {
                tip.append("平");
            }
        }
        
        return tip.toString();
    }
    
    private String getTimeFortune(String timeZhi, String zhiShiDoor, String zhiFuStar) {
        if (timeZhi == null) return "时辰平吉，宜顺时以动";
        
        String timeInfo = "";
        switch (timeZhi) {
            case "子": timeInfo = "子(23-1) 胆经当令"; break;
            case "丑": timeInfo = "丑(1-3) 肝经当令"; break;
            case "寅": timeInfo = "寅(3-5) 肺经当令"; break;
            case "卯": timeInfo = "卯(5-7) 大肠经当令"; break;
            case "辰": timeInfo = "辰(7-9) 胃经当令"; break;
            case "巳": timeInfo = "巳(9-11) 脾经当令"; break;
            case "午": timeInfo = "午(11-13) 心经当令"; break;
            case "未": timeInfo = "未(13-15) 小肠经当令"; break;
            case "申": timeInfo = "申(15-17) 膀胱经当令"; break;
            case "酉": timeInfo = "酉(17-19) 肾经当令"; break;
            case "戌": timeInfo = "戌(19-21) 心包经当令"; break;
            case "亥": timeInfo = "亥(21-23) 三焦经当令"; break;
            default: timeInfo = "时辰";
        }

        String[] luckyDoors = {"开", "休", "生"};
        boolean isLuckyDoor = false;
        for (String d : luckyDoors) {
            if (d.equals(zhiShiDoor)) {
                isLuckyDoor = true;
                break;
            }
        }

        if (isLuckyDoor) {
            return timeInfo + " · 值使得吉门，此时用事为宜";
        } else {
            return timeInfo + " · 值使不得吉门，宜静守待时";
        }
    }
    
    /** 时干主事，依《秘笈大全》：「六乙为天德……六癸为天网，六甲为时首，而应局斯灵。」 */
    private String getShiGanZhuShi(String gan) {
        if (gan == null) return "时临六甲，为时首，而应局斯灵。";
        switch (gan) {
            case "乙": return "时临六乙，为天德，出从天上日奇之下，可决胜于未然。";
            case "丙": return "时临六丙，为天威，不得丙火销金之令，宜防败于不测。";
            case "丁": return "时临六丁，名为玉女潜形，宜隐伏而不见其象。";
            case "戊": return "时临六戊，号曰乘龙万里，逢凶徒而自灭其踪。";
            case "己": return "时临六己，为地户，宜阴私而用偷劫。";
            case "庚": return "时临六庚，出战则咎将及于其师。";
            case "辛": return "时临六辛，行师则祸必及于其身。";
            case "壬": return "时临六壬，为天牢，而飞祸速至。";
            case "癸": return "时临六癸，为天网，审高下而利逃亡。";
            default:  return "时临六甲，为时首，而应局斯灵。";
        }
    }

    /** 宜：依《秘笈大全》八门诗诀与八门克应衢仙断语。 */
    private String[] getYiActivitiesDetailed(String door, String shiGan) {
        java.util.ArrayList<String> yiList = new java.util.ArrayList<>();

        if (door != null) {
            switch (door) {
                case "开":
                    yiList.add("谒贵求谋"); yiList.add("立宅赴官"); yiList.add("出行商贾");
                    yiList.add("开业签约");
                    break;
                case "生":
                    yiList.add("嫁娶造作"); yiList.add("求财营商"); yiList.add("置业储蓄");
                    yiList.add("出入经营");
                    break;
                case "休":
                    yiList.add("赴官迁徙"); yiList.add("祭祀入宅"); yiList.add("婚姻产育");
                    yiList.add("谒贵休养");
                    break;
                case "景":
                    yiList.add("上书献策"); yiList.add("奏对选拔"); yiList.add("求名举科");
                    yiList.add("文书印信");
                    break;
                case "杜":
                    yiList.add("躲灾避难"); yiList.add("隐伏逃藏"); yiList.add("追邪伐盗");
                    yiList.add("密谋保密");
                    break;
                case "伤":
                    yiList.add("渔猎捕捉"); yiList.add("索债追亡"); yiList.add("博弈竞技");
                    break;
                case "死":
                    yiList.add("吊死送丧"); yiList.add("葬埋捕捉"); yiList.add("畋猎刑戮");
                    yiList.add("清理守成");
                    break;
                case "惊":
                    yiList.add("词讼捕捉"); yiList.add("设疑伏兵"); yiList.add("掩捕逃亡");
                    break;
            }
        }

        yiList.add("祭神");
        yiList.add("祈福");
        yiList.add("行善");
        yiList.add("敬老");

        return yiList.toArray(new String[0]);
    }
    
    /** 忌：依《秘笈大全》八门诗诀与八门克应衢仙断语。 */
    private String[] getJiActivitiesDetailed(String door, String shiGan) {
        java.util.ArrayList<String> jiList = new java.util.ArrayList<>();

        if (door != null) {
            switch (door) {
                case "死":
                    jiList.add("所求营谋"); jiList.add("出行"); jiList.add("嫁娶");
                    jiList.add("修造"); jiList.add("上官");
                    break;
                case "惊":
                    jiList.add("市贾营修"); jiList.add("安静喜庆"); jiList.add("嫁娶宴会");
                    jiList.add("争执");
                    break;
                case "伤":
                    jiList.add("上官出行"); jiList.add("嫁娶修造"); jiList.add("商贾埋葬");
                    jiList.add("争执动土");
                    break;
                case "杜":
                    jiList.add("出行商贾"); jiList.add("谋求谒贵"); jiList.add("张扬显露");
                    break;
                case "景":
                    jiList.add("争讼口舌"); jiList.add("远行"); jiList.add("酒色娱乐");
                    break;
                case "开":
                    jiList.add("阴私隐匿"); jiList.add("闭关自守");
                    break;
                case "休":
                    jiList.add("过劳剧动"); jiList.add("冒险躁进");
                    break;
                case "生":
                    jiList.add("浪费盲投"); jiList.add("投机赌博");
                    break;
            }
        }

        if ("庚".equals(shiGan) || "辛".equals(shiGan) || "壬".equals(shiGan)) {
            jiList.add("出兵兴师");
        }
        jiList.add("赌博");
        jiList.add("诉讼");
        jiList.add("口舌");

        return jiList.toArray(new String[0]);
    }
    
    private String getShichenName(String timeZhi) {
        if (timeZhi == null) return "子时";
        switch (timeZhi) {
            case "子": return "子时 (23:00-01:00)";
            case "丑": return "丑时 (01:00-03:00)";
            case "寅": return "寅时 (03:00-05:00)";
            case "卯": return "卯时 (05:00-07:00)";
            case "辰": return "辰时 (07:00-09:00)";
            case "巳": return "巳时 (09:00-11:00)";
            case "午": return "午时 (11:00-13:00)";
            case "未": return "未时 (13:00-15:00)";
            case "申": return "申时 (15:00-17:00)";
            case "酉": return "酉时 (17:00-19:00)";
            case "戌": return "戌时 (19:00-21:00)";
            case "亥": return "亥时 (21:00-23:00)";
            default: return timeZhi + "时";
        }
    }
    
    private String getOverallAdviceSimple(boolean isYangDun, int ju, String star, String door) {
        StringBuilder sb = new StringBuilder();

        String starGrade = getStarGrade(star);
        String doorGrade = getDoorGrade(door);
        boolean isLuckyStar = "上吉".equals(starGrade) || "次吉".equals(starGrade);
        boolean isLuckyDoor = "吉门".equals(doorGrade);

        if (isLuckyStar && isLuckyDoor) {
            sb.append("整体运势：大吉（值符值使皆吉），宜乘时而进\n");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("整体运势：小吉（值符值使一吉），宜稳步而行\n");
        } else {
            sb.append("整体运势：平平（值符值使欠佳），宜慎守勿进\n");
        }

        sb.append("《秘笈》曰：「急从神兮缓从门。」有急难则从值符方而行，从容行事则择吉门而出。\n");

        if (door != null) {
            switch (door) {
                case "开": sb.append("行事：开门主开通，宜谒贵求谋、立宅赴官"); break;
                case "生": sb.append("行事：生门主生发，宜嫁娶造作、求财营商"); break;
                case "休": sb.append("行事：休门主休养，宜赴官迁徙、祭祀入宅"); break;
                case "景": sb.append("行事：景门主文书，宜上书献策、求名举科"); break;
                case "杜": sb.append("行事：杜门主闭塞，宜躲灾避难、隐伏藏形"); break;
                case "伤": sb.append("行事：伤门主损伤，惟宜渔猎捕捉、索债追亡"); break;
                case "死": sb.append("行事：死门主收敛，惟宜吊死送丧、葬埋捕捉"); break;
                case "惊": sb.append("行事：惊门主惊疑，惟宜词讼捕捉、设疑伏兵"); break;
            }
        }

        return sb.toString();
    }

    /** 空亡：奇门以旬空论，空亡之方谋望难成，吉凶减半。 */
    private String getKongWangExplanation(String kongWang) {
        if (kongWang == null || kongWang.equals("--")) return "空亡之方，事体不实，谋望难成。";
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("戌亥", "戌亥空（乾、坎之界）：天门事虚，宜守不宜进。");
        map.put("申酉", "申酉空（坤、兑之界）：金气落空，兵戈刑伤不实。");
        map.put("午未", "午未空（离、坤之界）：火土落空，声名口舌虚浮。");
        map.put("辰巳", "辰巳空（巽、巽位）：田宅财帛虚，宜审而后动。");
        map.put("寅卯", "寅卯空（艮、震之界）：文书人事不实，求谋难就。");
        map.put("子丑", "子丑空（坎、艮之界）：水土落空，谋事迟滞。");
        return map.getOrDefault(kongWang, "空亡之方，事体不实，谋望难成。");
    }
    
    /** 马星：主迁移动变，所临之方利出行求财；逢空亡则马不行。 */
    private String getMaXingExplanation(String maXing) {
        if (maXing == null || maXing.equals("--")) return "马星主奔波变动，宜出行求谋。";
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("寅", "马星在寅（东北）：主动主迁，利趋东北出行求财。");
        map.put("午", "马星在午（正南）：主动主迁，利趋正南出行求财。");
        map.put("申", "马星在申（西南）：主动主迁，利趋西南出行求财。");
        map.put("亥", "马星在亥（西北）：主动主迁，利趋西北出行求财。");
        map.put("巳", "马星在巳（东南）：主动主迁，利趋东南出行求财。");
        map.put("酉", "马星在酉（正西）：主动主迁，利趋正西出行求财。");
        return map.getOrDefault(maXing, "马星主动，奔波迁变。");
    }
    
    private String getWangCuiDescription(String[] wangCui, int riGanPalace) {
        if (riGanPalace < 0 || riGanPalace >= wangCui.length) {
            return "旺衰状态不明";
        }
        String status = wangCui[riGanPalace];
        switch (status) {
            case "旺": return "日干落宫当令而旺：气盛事通，谋为多成。";
            case "相": return "日干落宫得生而相：有助易成，进则可望。";
            case "休": return "日干落宫休废：气退不可用，宜静养守常。";
            case "囚": return "日干落宫受制被囚：滞碍难行，宜守不宜攻。";
            case "死": return "日干落宫临死地：百事无成，宜固守勿动。";
            default: return "旺衰未明";
        }
    }
    
    private String getQiMenSummary(String yearPillar, String monthPillar, String dayPillar, String timePillar, String zhiFuStar, String zhiShiDoor, boolean isYangDun, int ju) {
        String dayGan = dayPillar.substring(0, 1);
        String dayGanWuXing = QiMenCalculator.getWuXing(dayGan);

        String starGrade = getStarGrade(zhiFuStar);
        String doorGrade = getDoorGrade(zhiShiDoor);
        boolean isLuckyStar = "上吉".equals(starGrade) || "次吉".equals(starGrade);
        boolean isLuckyDoor = "吉门".equals(doorGrade);

        StringBuilder sb = new StringBuilder();
        sb.append(dayGanWuXing).append("日").append(dayGan).append(" · ");
        sb.append(isYangDun ? "阳遁" : "阴遁").append(ju).append("局\n");
        sb.append("值符 ").append(zhiFuStar).append("星（").append(starGrade).append("）");
        sb.append(" · 值使 ").append(zhiShiDoor).append("门（").append(doorGrade).append("）\n");
        sb.append("《三元经》曰：「辅禽心星为上吉，冲任次吉理须明；大凶天蓬与天芮，小凶天柱及天英。」\n");
        sb.append("整体格局：");
        if (isLuckyStar && isLuckyDoor) {
            sb.append("★★★ 大吉　值符值使皆吉，吉门又合三奇");
        } else if (isLuckyStar || isLuckyDoor) {
            sb.append("★★ 小吉　值符值使一吉，宜稳步而行");
        } else {
            sb.append("★ 平平　值符值使欠佳，宜慎守勿进");
        }
        return sb.toString();
    }

    /** 九星品级，依《三元经》："辅禽心星为上吉，冲任次吉理须明；大凶天蓬与天芮，小凶天柱及天英。" */
    private String getStarGrade(String star) {
        if (star == null) return "未明";
        switch (star) {
            case "天辅": case "天禽": case "天心": return "上吉";
            case "天冲": case "天任": return "次吉";
            case "天柱": case "天英": return "小凶";
            case "天蓬": case "天芮": return "大凶";
            default: return "未明";
        }
    }

    /** 八门品级：开、休、生为三吉门；伤、死、惊为凶门；杜、景为中平。 */
    private String getDoorGrade(String door) {
        if (door == null || door.isEmpty()) return "无门";
        switch (door) {
            case "开": case "休": case "生": return "吉门";
            case "杜": case "景": return "平门";
            case "伤": case "死": case "惊": return "凶门";
            default: return "平门";
        }
    }
    
    private String getPaipanYiju(String yearPillar, String monthPillar, String dayPillar, String timePillar,
                                  String jieqi, boolean isYangDun, int ju,
                                  String xunShou, String zhiFuStar, String zhiShiDoor,
                                  int zhiFuPalace, int zhiShiPalace) {
        String[] PALACE_NAMES = {"坎一", "坤二", "震三", "巽四", "中五", "乾六", "兑七", "艮八", "离九"};
        
        String getGoldColor = "<font color='#E6C46A'>";
        String getWoodColor = "<font color='#3FA34D'>";
        String getFireColor = "<font color='#E0593B'>";
        String getEarthColor = "<font color='#DEB887'>";
        String getMetalColor = "<font color='#9AA7B8'>";
        String getWaterColor = "<font color='#3E87C2'>";
        String getTitleColor = "<font color='#CCB866'>";
        String getClose = "</font>";
        
        String dunType = isYangDun ? "阳遁" : "阴遁";
        String dunColor = isYangDun ? getWoodColor : getMetalColor;
        String shunNi = isYangDun ? "顺" : "逆";
        
        StringBuilder sb = new StringBuilder();

        // 1. 四柱
        sb.append(getTitleColor).append("<b>【四柱】</b> ").append(getClose)
          .append(yearPillar).append(" ").append(monthPillar).append(" ").append(dayPillar).append(" ").append(timePillar)
          .append("<br/><br/>");

        // 2. 局法
        sb.append(getTitleColor).append("<b>【局法】</b> ").append(getClose)
          .append(jieqi).append("　").append(dunType).append(ju).append("局").append(shunNi).append("行")
          .append("<br/>");
        sb.append("<font color='#7C8C9C'>　《烟波钓叟歌》：「阴阳二遁分顺逆，一气三元人莫测；")
          .append("五日都来换一元，接气超神为准的。」</font><br/><br/>");

        // 3. 值符值使
        sb.append(getTitleColor).append("<b>【值符值使】</b> ").append(getClose)
          .append("时柱定旬首 ").append(xunShou).append("<br/>");
        sb.append("　值符 ").append(getFireColor).append(zhiFuStar).append(getClose).append("落").append(PALACE_NAMES[zhiFuPalace])
          .append("　值使 ").append(getWoodColor).append(zhiShiDoor).append(getClose).append("落").append(PALACE_NAMES[zhiShiPalace])
          .append("<br/>");
        sb.append("<font color='#7C8C9C'>　值符者，诸神之元首、九星之领袖；值使者，八门之枢机。</font><br/><br/>");

        // 4. 三奇六仪
        sb.append(getTitleColor).append("<b>【三奇六仪】</b> ").append(getClose)
          .append("三奇 ").append(getFireColor).append("乙丙丁").append(getClose)
          .append("　六仪 ").append(getEarthColor).append("戊己庚辛壬癸").append(getClose)
          .append("<br/>");
        sb.append("<font color='#7C8C9C'>　《秘笈》曰：「六甲为时首，而应局斯灵。」甲遁于六仪，故六仪即六甲。</font><br/><br/>");

        // 5. 九星
        sb.append(getTitleColor).append("<b>【九星】</b> ").append(getClose)
          .append("值符 ").append(getFireColor).append(zhiFuStar).append(getClose).append("自").append(PALACE_NAMES[zhiFuPalace]).append(shunNi).append("飞：蓬芮冲辅禽心柱任英")
          .append("<br/><br/>");

        // 6. 八门
        sb.append(getTitleColor).append("<b>【八门】</b> ").append(getClose)
          .append("值使 ").append(getWoodColor).append(zhiShiDoor).append(getClose).append("自").append(PALACE_NAMES[zhiShiPalace]).append(shunNi).append("飞：休生伤杜景死惊开")
          .append("<br/><br/>");

        // 7. 八神
        sb.append(getTitleColor).append("<b>【八神】</b> ").append(getClose)
          .append("自").append(PALACE_NAMES[zhiFuPalace]).append(shunNi).append("布：符蛇阴合白玄地天")
          .append("<br/><br/>");

        // 8. 旺衰
        sb.append(getTitleColor).append("<b>【旺相休囚】</b> ").append(getClose)
          .append("以宫五行为用神、月令五行为时令").append("<br/>");
        sb.append("<font color='#7C8C9C'>　《烟波钓叟歌》：「与我同行即为相，我生之月诚为旺，")
          .append("废于父母休于财，囚于官鬼真不妄。」</font><br/><br/>");

        // 9. 关键
        sb.append(getTitleColor).append("<b>【关键】</b> ").append(getClose)
          .append(dunType).append(shunNi).append("行　值符星随时干　值使门随时支").append("<br/>");
        sb.append("<font color='#7C8C9C'>　《秘笈》曰：「急从神兮缓从门。」有急难则从值符方，")
          .append("从容行事则择吉门而出。</font>");

        return sb.toString();
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

