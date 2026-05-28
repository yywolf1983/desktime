package com.example.timedisplay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class JieqiActivity extends Activity {

    private String currentJieqi;
    private TextView jieqiName;
    private TextView jieqiEnglish;
    private TextView jieqiPhenomenon;
    private TextView hou1;
    private TextView hou2;
    private TextView hou3;
    private TextView hou1Days;
    private TextView hou2Days;
    private TextView hou3Days;
    private TextView tradition;
    private TextView prevJieqi;
    private TextView nextJieqi;
    private TextView jieqiDate;
    private TextView daysToNext;
    private LinearLayout jieqiListLayout;
    private LinearLayout prevJieqiLayout;
    private LinearLayout nextJieqiLayout;

    private static final String[] DAY_MARKS = {"一", "丁", "上", "止", "正"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jieqi);

        jieqiName = findViewById(R.id.jieqiName);
        jieqiEnglish = findViewById(R.id.jieqiEnglish);
        jieqiPhenomenon = findViewById(R.id.jieqiPhenomenon);
        hou1 = findViewById(R.id.hou1);
        hou2 = findViewById(R.id.hou2);
        hou3 = findViewById(R.id.hou3);
        hou1Days = findViewById(R.id.hou1Days);
        hou2Days = findViewById(R.id.hou2Days);
        hou3Days = findViewById(R.id.hou3Days);
        tradition = findViewById(R.id.tradition);
        prevJieqi = findViewById(R.id.prevJieqi);
        nextJieqi = findViewById(R.id.nextJieqi);
        jieqiDate = findViewById(R.id.jieqiDate);
        daysToNext = findViewById(R.id.daysToNext);
        jieqiListLayout = findViewById(R.id.jieqiListLayout);
        prevJieqiLayout = findViewById(R.id.prevJieqiLayout);
        nextJieqiLayout = findViewById(R.id.nextJieqiLayout);

        currentJieqi = getIntent().getStringExtra("jieqi");
        if (currentJieqi == null || currentJieqi.isEmpty()) {
            currentJieqi = JieqiData.getCurrentJieqi(Calendar.getInstance());
        }

        displayJieqiInfo(currentJieqi);
        displayJieqiList();

        prevJieqiLayout.setOnClickListener(v -> {
            currentJieqi = JieqiData.getPrevJieqi(currentJieqi);
            displayJieqiInfo(currentJieqi);
            displayJieqiList();
        });

        nextJieqiLayout.setOnClickListener(v -> {
            currentJieqi = JieqiData.getNextJieqi(currentJieqi);
            displayJieqiInfo(currentJieqi);
            displayJieqiList();
        });
    }

    private void displayJieqiInfo(String jieqi) {
        if (jieqi == null || jieqi.isEmpty()) {
            jieqi = JieqiData.getCurrentJieqi(Calendar.getInstance());
        }
        
        JieqiData.JieqiInfo info = JieqiData.getJieqiInfo(jieqi);
        if (info == null) {
            jieqiName.setText(jieqi);
            jieqiEnglish.setText("");
            jieqiPhenomenon.setText("");
            hou1.setText("初候 · ");
            hou2.setText("二候 · ");
            hou3.setText("三候 · ");
            tradition.setText("");
            return;
        }

        jieqiName.setText(info.name);
        jieqiEnglish.setText(info.english);
        jieqiPhenomenon.setText(info.phenomenon);
        hou1.setText("初候 · " + info.hou1);
        hou2.setText("二候 · " + info.hou2);
        hou3.setText("三候 · " + info.hou3);
        tradition.setText(info.tradition);

        displayHouDays();

        int index = JieqiData.getJieqiIndex(jieqi);
        String prev = JieqiData.getPrevJieqi(jieqi);
        String next = JieqiData.getNextJieqi(jieqi);
        
        prevJieqi.setText(prev);
        nextJieqi.setText(next);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int[] date = JieqiData.getJieqiDate(year, index);
        jieqiDate.setText(date[0] + "年" + date[1] + "月" + date[2] + "日");

        int days = JieqiData.calculateDaysToNextJieqi(calendar);
        daysToNext.setText("距离下一个节气还有 " + days + " 天");
    }

    private void displayHouDays() {
        int daysIntoJieqi = calculateDaysIntoJieqi();
        
        displayHouDaysLayout(hou1Days, daysIntoJieqi, 0);
        displayHouDaysLayout(hou2Days, daysIntoJieqi, 5);
        displayHouDaysLayout(hou3Days, daysIntoJieqi, 10);
    }

    private int calculateDaysIntoJieqi() {
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1;
        int currentDay = now.get(Calendar.DAY_OF_MONTH);
        
        int index = JieqiData.getJieqiIndex(currentJieqi);
        int[] jieqiDate = JieqiData.getJieqiDate(currentYear, index);
        int jieqiYear = jieqiDate[0];
        int jieqiMonth = jieqiDate[1];
        int jieqiDay = jieqiDate[2];
        
        if (currentYear < jieqiYear || 
            (currentYear == jieqiYear && currentMonth < jieqiMonth) || 
            (currentYear == jieqiYear && currentMonth == jieqiMonth && currentDay < jieqiDay)) {
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

    private void displayHouDaysLayout(TextView textView, int daysIntoJieqi, int houOffset) {
        if (daysIntoJieqi < 0) {
            textView.setText("○");
            textView.setTextColor(Color.parseColor("#666666"));
        } else if (daysIntoJieqi >= houOffset + 5) {
            textView.setText("正");
            textView.setTextColor(Color.parseColor("#FFD700"));
        } else if (daysIntoJieqi >= houOffset && daysIntoJieqi < houOffset + 5) {
            int dayIndex = daysIntoJieqi - houOffset;
            if (dayIndex >= 0 && dayIndex < DAY_MARKS.length) {
                textView.setText(DAY_MARKS[dayIndex]);
                textView.setTextColor(Color.parseColor("#FFD700"));
            } else {
                textView.setText("○");
                textView.setTextColor(Color.parseColor("#666666"));
            }
        } else {
            textView.setText("○");
            textView.setTextColor(Color.parseColor("#666666"));
        }
    }

    private void displayJieqiList() {
        jieqiListLayout.removeAllViews();

        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1;
        int currentDay = now.get(Calendar.DAY_OF_MONTH);

        String currentSeasonJieqi = JieqiData.getCurrentJieqi(now);
        int currentJieqiIndex = JieqiData.getJieqiIndex(currentSeasonJieqi);

        int cols = 4;
        int rows = 6;
        
        for (int row = 0; row < rows; row++) {
            LinearLayout rowLayout = new LinearLayout(JieqiActivity.this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            rowLayout.setGravity(android.view.Gravity.CENTER);
            
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                if (index >= JieqiData.SOLAR_TERMS.length) break;
                
                String jieqi = JieqiData.SOLAR_TERMS[index];
                int[] date = JieqiData.getJieqiDate(currentYear, index);
                int jieqiMonth = date[1];
                int jieqiDay = date[2];

                boolean isPast = index < currentJieqiIndex;
                boolean isCurrent = jieqi.equals(currentJieqi);
                boolean isCurrentSeason = index == currentJieqiIndex;

                float density = getResources().getDisplayMetrics().density;

                LinearLayout jieqiBox = new LinearLayout(JieqiActivity.this);
                jieqiBox.setOrientation(LinearLayout.VERTICAL);
                jieqiBox.setGravity(android.view.Gravity.CENTER);
                
                LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1
                );
                int marginPx = (int) (4 * density);
                boxParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                boxParams.height = (int) (60 * density); // 60dp
                jieqiBox.setLayoutParams(boxParams);
                jieqiBox.setClickable(true);
                jieqiBox.setBackgroundColor(Color.parseColor("#1a1a2e"));

                TextView jieqiName = new TextView(JieqiActivity.this);
                jieqiName.setText(jieqi);
                jieqiName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16);
                jieqiName.setGravity(android.view.Gravity.CENTER);
                
                TextView jieqiDate = new TextView(JieqiActivity.this);
                jieqiDate.setText(jieqiMonth + "/" + jieqiDay);
                jieqiDate.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13);
                jieqiDate.setGravity(android.view.Gravity.CENTER);
                int paddingPx = (int) (2 * density);
                jieqiDate.setPadding(0, paddingPx, 0, 0);

                if (isCurrent) {
                    jieqiName.setTextColor(Color.parseColor("#FFD700"));
                    jieqiDate.setTextColor(Color.parseColor("#FFD700"));
                    jieqiName.setTypeface(null, android.graphics.Typeface.BOLD);
                    jieqiBox.setBackgroundColor(Color.parseColor("#2a2a4e"));
                } else if (isCurrentSeason) {
                    jieqiName.setTextColor(Color.parseColor("#FF87CEEB"));
                    jieqiDate.setTextColor(Color.parseColor("#FF87CEEB"));
                    jieqiName.setTypeface(null, android.graphics.Typeface.BOLD);
                } else if (isPast) {
                    jieqiName.setTextColor(Color.parseColor("#666666"));
                    jieqiDate.setTextColor(Color.parseColor("#666666"));
                } else {
                    jieqiName.setTextColor(Color.parseColor("#FFADD8E6"));
                    jieqiDate.setTextColor(Color.parseColor("#FFADD8E6"));
                }

                jieqiBox.addView(jieqiName);
                jieqiBox.addView(jieqiDate);

                final String selectedJieqi = jieqi;
                jieqiBox.setOnClickListener(v -> {
                    currentJieqi = selectedJieqi;
                    displayJieqiInfo(currentJieqi);
                    displayJieqiList();
                });

                rowLayout.addView(jieqiBox);
            }
            
            jieqiListLayout.addView(rowLayout);
        }
    }

    private boolean isDatePast(int jieqiMonth, int jieqiDay, int currentMonth, int currentDay) {
        if (jieqiMonth >= 3 && jieqiMonth < currentMonth) {
            return true;
        } else if (jieqiMonth == currentMonth) {
            return jieqiDay < currentDay;
        } else if (jieqiMonth <= 2 && currentMonth > 2) {
            return false;
        }
        return jieqiMonth < currentMonth;
    }

    public void goBack(View view) {
        finish();
    }
}
