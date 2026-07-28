package com.example.timedisplay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class JieqiActivity extends Activity {

    private static final String COLOR_GOLD = "#E6C46A";
    private static final String COLOR_GRAY = "#999999";
    private static final String COLOR_SEASON = "#FF8FCDEB";
    private static final String COLOR_PAST = "#FF9AA7B8";
    private static final String HOU_PREFIX_1 = "初候 · ";
    private static final String HOU_PREFIX_2 = "二候 · ";
    private static final String HOU_PREFIX_3 = "三候 · ";
    private static final String[] DAY_MARKS = {"一", "丁", "上", "止", "正"};
    private static final int COLS = 4;
    private static final int ROWS = 6;

    private String currentJieqi;
    private TextView jieqiName;
    private TextView jieqiEnglish;
    private TextView jieqiPhenomenon;
    private TextView hou1, hou2, hou3;
    private TextView hou1Desc, hou2Desc, hou3Desc;
    private TextView hou1Days, hou2Days, hou3Days;
    private TextView tradition;
    private TextView prevJieqi, nextJieqi;
    private TextView jieqiDate;
    private TextView daysToNext;
    private LinearLayout jieqiListLayout;

    private final View[] jieqiBoxes = new View[24];
    private final TextView[] jieqiBoxNames = new TextView[24];
    private final TextView[] jieqiBoxDates = new TextView[24];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isRotationLocked = prefs.getBoolean("rotationLocked", false);
        int lockedOrientation = prefs.getInt("lockedOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (isRotationLocked && lockedOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(lockedOrientation);
        }

        setContentView(R.layout.activity_jieqi);

        jieqiName = findViewById(R.id.jieqiName);
        jieqiEnglish = findViewById(R.id.jieqiEnglish);
        jieqiPhenomenon = findViewById(R.id.jieqiPhenomenon);
        hou1 = findViewById(R.id.hou1);
        hou2 = findViewById(R.id.hou2);
        hou3 = findViewById(R.id.hou3);
        hou1Desc = findViewById(R.id.hou1Desc);
        hou2Desc = findViewById(R.id.hou2Desc);
        hou3Desc = findViewById(R.id.hou3Desc);
        hou1Days = findViewById(R.id.hou1Days);
        hou2Days = findViewById(R.id.hou2Days);
        hou3Days = findViewById(R.id.hou3Days);
        tradition = findViewById(R.id.tradition);
        prevJieqi = findViewById(R.id.prevJieqi);
        nextJieqi = findViewById(R.id.nextJieqi);
        jieqiDate = findViewById(R.id.jieqiDate);
        daysToNext = findViewById(R.id.daysToNext);
        jieqiListLayout = findViewById(R.id.jieqiListLayout);

        Calendar cal = Calendar.getInstance();
        currentJieqi = getCurrentJieqiInfo(getIntent().getStringExtra("jieqi"), cal);
        refreshViews(cal);

        prevJieqi.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            currentJieqi = JieqiData.getPrevJieqi(currentJieqi);
            refreshViews(c);
        });

        nextJieqi.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            currentJieqi = JieqiData.getNextJieqi(currentJieqi);
            refreshViews(c);
        });
    }

    private void refreshViews(Calendar cal) {
        displayJieqiInfo(currentJieqi, cal);
        displayJieqiList(cal);
    }

    private String getCurrentJieqiInfo(String jieqi, Calendar cal) {
        if (jieqi == null || jieqi.isEmpty()) {
            return JieqiData.getCurrentJieqi(cal);
        }
        return jieqi;
    }

    private void displayJieqiInfo(String jieqi, Calendar cal) {
        jieqi = getCurrentJieqiInfo(jieqi, cal);

        JieqiData.JieqiInfo info = JieqiData.getJieqiInfo(jieqi);
        if (info == null) {
            jieqiName.setText(jieqi);
            clearTextViews(jieqiEnglish, jieqiPhenomenon, hou1Desc, hou2Desc, hou3Desc, tradition);
            hou1.setText(HOU_PREFIX_1);
            hou2.setText(HOU_PREFIX_2);
            hou3.setText(HOU_PREFIX_3);
            return;
        }

        jieqiName.setText(info.name);
        jieqiEnglish.setText(info.english);
        jieqiPhenomenon.setText(info.phenomenon);
        hou1.setText(HOU_PREFIX_1 + info.hou1);
        hou2.setText(HOU_PREFIX_2 + info.hou2);
        hou3.setText(HOU_PREFIX_3 + info.hou3);
        if (hou1Desc != null) hou1Desc.setText(info.hou1Desc);
        if (hou2Desc != null) hou2Desc.setText(info.hou2Desc);
        if (hou3Desc != null) hou3Desc.setText(info.hou3Desc);
        tradition.setText(info.tradition);

        displayHouDays(cal);

        int index = JieqiData.getJieqiIndex(jieqi);
        prevJieqi.setText(JieqiData.getPrevJieqi(jieqi));
        nextJieqi.setText(JieqiData.getNextJieqi(jieqi));

        int[] date = JieqiData.getJieqiDate(cal.get(Calendar.YEAR), index);
        jieqiDate.setText(date[0] + "年" + date[1] + "月" + date[2] + "日");

        int days = JieqiData.calculateDaysToNextJieqi(cal);
        daysToNext.setText("距离下一个节气还有 " + days + " 天");
    }

    private void clearTextViews(TextView... views) {
        for (TextView v : views) {
            if (v != null) v.setText("");
        }
    }

    private void displayHouDays(Calendar cal) {
        int daysIntoJieqi = calculateDaysIntoJieqi(cal);
        displayHouDaysLayout(hou1Days, daysIntoJieqi, 0);
        displayHouDaysLayout(hou2Days, daysIntoJieqi, 5);
        displayHouDaysLayout(hou3Days, daysIntoJieqi, 10);
    }

    private int calculateDaysIntoJieqi(Calendar cal) {
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        int index = JieqiData.getJieqiIndex(currentJieqi);
        int[] date = JieqiData.getJieqiDate(currentYear, index);
        int jieqiYear = date[0], jieqiMonth = date[1], jieqiDay = date[2];

        if (compareJieqiDate(jieqiYear, jieqiMonth, jieqiDay, currentYear, currentMonth, currentDay) > 0) {
            return -1;
        }

        Calendar jieqiCalendar = Calendar.getInstance();
        jieqiCalendar.set(jieqiYear, jieqiMonth - 1, jieqiDay, 0, 0, 0);
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(currentYear, currentMonth - 1, currentDay, 0, 0, 0);

        long diff = todayStart.getTimeInMillis() - jieqiCalendar.getTimeInMillis();
        int days = (int) (diff / (1000 * 60 * 60 * 24));
        return Math.max(0, Math.min(days, 14));
    }

    private int compareJieqiDate(int jieqiYear, int jieqiMonth, int jieqiDay,
                                 int currentYear, int currentMonth, int currentDay) {
        if (jieqiYear != currentYear) return Integer.compare(jieqiYear, currentYear);
        if (jieqiMonth != currentMonth) return Integer.compare(jieqiMonth, currentMonth);
        return Integer.compare(jieqiDay, currentDay);
    }

    private boolean isJieqiPast(int jieqiYear, int jieqiMonth, int jieqiDay,
                                int currentYear, int currentMonth, int currentDay) {
        return compareJieqiDate(jieqiYear, jieqiMonth, jieqiDay, currentYear, currentMonth, currentDay) < 0;
    }

    private void displayHouDaysLayout(TextView textView, int daysIntoJieqi, int houOffset) {
        if (daysIntoJieqi >= houOffset && daysIntoJieqi < houOffset + 5) {
            textView.setText(DAY_MARKS[daysIntoJieqi - houOffset]);
            textView.setTextColor(Color.parseColor(COLOR_GOLD));
        } else if (daysIntoJieqi >= houOffset + 5) {
            textView.setText("正");
            textView.setTextColor(Color.parseColor(COLOR_GOLD));
        } else {
            textView.setText("○");
            textView.setTextColor(Color.parseColor(COLOR_GRAY));
        }
    }

    private void displayJieqiList(Calendar cal) {
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        int currentJieqiIndex = JieqiData.getJieqiIndex(JieqiData.getCurrentJieqi(cal));

        if (jieqiBoxes[0] == null) {
            for (int row = 0; row < ROWS; row++) {
                jieqiListLayout.addView(buildJieqiRow(row, currentYear));
            }
        }
        for (int index = 0; index < JieqiData.SOLAR_TERMS.length; index++) {
            int[] date = JieqiData.getJieqiDate(currentYear, index);
            boolean isPast = isJieqiPast(date[0], date[1], date[2], currentYear, currentMonth, currentDay);
            applyJieqiBoxState(index,
                JieqiData.SOLAR_TERMS[index].equals(currentJieqi),
                index == currentJieqiIndex,
                isPast);
        }
    }

    private LinearLayout buildJieqiRow(int rowIndex, int currentYear) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rowLayout.setGravity(android.view.Gravity.CENTER);
        for (int col = 0; col < COLS; col++) {
            int index = rowIndex * COLS + col;
            if (index >= JieqiData.SOLAR_TERMS.length) break;
            String jieqi = JieqiData.SOLAR_TERMS[index];
            int[] date = JieqiData.getJieqiDate(currentYear, index);
            rowLayout.addView(createJieqiBox(index, jieqi, date[1] + "/" + date[2]));
        }
        return rowLayout;
    }

    private View createJieqiBox(int index, String name, String dateStr) {
        float density = getResources().getDisplayMetrics().density;
        LinearLayout jieqiBox = new LinearLayout(this);
        jieqiBox.setOrientation(LinearLayout.VERTICAL);
        jieqiBox.setGravity(android.view.Gravity.CENTER);
        jieqiBox.setClickable(true);
        jieqiBox.setBackgroundColor(Color.parseColor("#301a1a2e"));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        int m = (int) (4 * density);
        p.setMargins(m, m, m, m);
        p.height = (int) (60 * density);
        jieqiBox.setLayoutParams(p);

        TextView boxName = new TextView(this);
        boxName.setText(name);
        boxName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16);
        boxName.setGravity(android.view.Gravity.CENTER);
        TextView boxDate = new TextView(this);
        boxDate.setText(dateStr);
        boxDate.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15);
        boxDate.setGravity(android.view.Gravity.CENTER);
        boxDate.setPadding(0, (int) (2 * density), 0, 0);

        jieqiBox.addView(boxName);
        jieqiBox.addView(boxDate);
        jieqiBoxes[index] = jieqiBox;
        jieqiBoxNames[index] = boxName;
        jieqiBoxDates[index] = boxDate;
        jieqiBox.setOnClickListener(v -> {
            currentJieqi = name;
            refreshViews(Calendar.getInstance());
        });
        return jieqiBox;
    }

    private void applyJieqiBoxState(int index, boolean isCurrent, boolean isSeason, boolean isPast) {
        View box = jieqiBoxes[index];
        TextView boxName = jieqiBoxNames[index];
        TextView boxDate = jieqiBoxDates[index];

        boxName.setText(isCurrent ? "" : JieqiData.SOLAR_TERMS[index]);
        boxName.setTypeface(null, (isCurrent || isSeason)
            ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        box.setBackgroundColor(Color.parseColor(isCurrent ? "#502a2a4e" : "#301a1a2e"));
        int color = Color.parseColor(isCurrent ? COLOR_GOLD
            : isSeason ? COLOR_SEASON : isPast ? COLOR_GRAY : COLOR_PAST);
        boxName.setTextColor(color);
        boxDate.setTextColor(color);
    }

    public void goBack(View view) {
        finish();
    }
}
