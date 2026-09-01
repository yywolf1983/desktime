package com.example.timedisplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 吉日查询页：以首页当前日期为基准，向后推 180 天，基于四柱（日柱干支 + 月支）
 * 推算建除十二神与每日宜忌，列出各类事项（嫁娶/开市/移徙等）的吉日；
 * 用户选定日期后回传首页，改写首页当前日期。
 */
public class AuspiciousDayActivity extends Activity {

    private static final int RANGE_DAYS = 180;

    // 事项类别：name + 宜此事的建除神(good) + 忌此事的建除神(bad)
    private static class Category {
        String name;
        String[] good;
        String[] bad;

        Category(String name, String[] good, String[] bad) {
            this.name = name;
            this.good = good;
            this.bad = bad;
        }
    }

    private final List<Category> categories = new ArrayList<>();

    // 单日黄历数据
    private static class AlmanacDay {
        Calendar cal;
        String dateStr;
        String weekday;
        String dayPillar;
        String jianChu;
        String yi;
        String ji;
        String level; // 吉 / 平
    }

    private LinearLayout categoryContainer;
    private ListView resultList;
    private TextView resultTitle;
    private TextView rangeText;
    private Category currentCategory;
    private Calendar baseCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auspicious_day);

        categoryContainer = findViewById(R.id.categoryContainer);
        resultList = findViewById(R.id.resultList);
        resultTitle = findViewById(R.id.resultTitle);
        rangeText = findViewById(R.id.rangeText);

        // 基准日：来自首页（默认今天）
        int by = getIntent().getIntExtra("base_year", -1);
        int bm = getIntent().getIntExtra("base_month", -1);
        int bd = getIntent().getIntExtra("base_day", -1);
        if (by > 0 && bm > 0 && bd > 0) {
            baseCal = Calendar.getInstance();
            baseCal.set(by, bm - 1, bd, 12, 0, 0);
        } else {
            baseCal = Calendar.getInstance();
        }
        baseCal.set(Calendar.MILLISECOND, 0);

        initCategories();
        updateRangeText();
        buildCategoryButtons();

        TextView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initCategories() {
        categories.add(new Category("嫁娶", new String[]{"成", "开", "定", "危", "平"}, new String[]{"破", "收", "闭", "建"}));
        categories.add(new Category("开市开业", new String[]{"开", "成", "满", "定"}, new String[]{"破", "闭", "建", "收"}));
        categories.add(new Category("移徙搬家", new String[]{"成", "开", "定", "平"}, new String[]{"破", "危", "收", "闭"}));
        categories.add(new Category("出行", new String[]{"建", "除", "满", "平", "定", "成", "开"}, new String[]{"破", "收", "闭", "危"}));
        categories.add(new Category("动土修造", new String[]{"定", "执", "成", "开", "建"}, new String[]{"破", "闭"}));
        categories.add(new Category("祭祀祈福", new String[]{"除", "定", "执", "成", "开", "平"}, new String[]{"破"}));
        categories.add(new Category("安葬", new String[]{"收", "成", "开", "定", "闭"}, new String[]{"破", "建"}));
        categories.add(new Category("入宅", new String[]{"成", "开", "定", "平"}, new String[]{"破", "危", "收", "闭"}));
        categories.add(new Category("订盟纳采", new String[]{"定", "成", "开", "执"}, new String[]{"破", "闭"}));
        categories.add(new Category("求医治病", new String[]{"除", "破", "收"}, new String[]{}));
        categories.add(new Category("纳财", new String[]{"收", "满", "开", "成"}, new String[]{"破", "闭"}));
        categories.add(new Category("赴任求官", new String[]{"开", "成", "定"}, new String[]{"除", "破"}));
    }

    private void updateRangeText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Calendar end = (Calendar) baseCal.clone();
        end.add(Calendar.DAY_OF_MONTH, RANGE_DAYS);
        String base = sdf.format(baseCal.getTime());
        String endStr = sdf.format(end.getTime());
        rangeText.setText("自 " + base + " 起 " + RANGE_DAYS + " 天内（至 " + endStr + "）");
    }

    private void buildCategoryButtons() {
        categoryContainer.removeAllViews();
        LinearLayout row = null;
        for (int i = 0; i < categories.size(); i++) {
            if (i % 3 == 0) {
                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setPadding(0, 0, 0, dpToPx(8));
                categoryContainer.addView(row);
            }
            final Category cat = categories.get(i);
            TextView btn = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            int margin = dpToPx(4);
            if (i % 3 == 1 || i % 3 == 2) lp.leftMargin = margin;
            btn.setLayoutParams(lp);
            btn.setText(cat.name);
            btn.setTextSize(13);
            btn.setTextColor(getResources().getColor(R.color.gold_faint));
            btn.setGravity(Gravity.CENTER);
            btn.setPadding(dpToPx(6), dpToPx(10), dpToPx(6), dpToPx(10));
            btn.setBackgroundResource(R.drawable.btn_round_transparent);
            btn.setClickable(true);
            btn.setFocusable(true);
            btn.setOnClickListener(v -> selectCategory(cat, btn));
            if (row != null) row.addView(btn);
        }
    }

    private void selectCategory(Category cat, TextView selectedBtn) {
        // 重置所有按钮文字色
        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View child = categoryContainer.getChildAt(i);
            if (child instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) child;
                for (int j = 0; j < g.getChildCount(); j++) {
                    View b = g.getChildAt(j);
                    if (b instanceof TextView) {
                        ((TextView) b).setTextColor(getResources().getColor(R.color.gold_faint));
                    }
                }
            }
        }
        selectedBtn.setTextColor(getResources().getColor(R.color.gold));
        currentCategory = cat;
        computeAndShow(cat);
    }

    private void computeAndShow(Category cat) {
        List<AlmanacDay> days = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        SimpleDateFormat wdf = new SimpleDateFormat("EEEE", Locale.CHINA);
        Calendar cal = (Calendar) baseCal.clone();

        for (int i = 0; i <= RANGE_DAYS; i++) {
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH) + 1;
            int d = cal.get(Calendar.DAY_OF_MONTH);

            String dayPillar = HuangLi.getDayPillar(y, m, d);
            Calendar c = Calendar.getInstance();
            c.set(y, m - 1, d, 12, 0, 0);
            c.set(Calendar.MILLISECOND, 0);
            String monthZhi = HuangLi.getMonthZhi(c);
            String jianChu = HuangLi.getJianChu(dayPillar, monthZhi);

            String level = judgeLevel(jianChu, cat);
            if ("凶".equals(level)) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                continue;
            }

            AlmanacDay ad = new AlmanacDay();
            ad.cal = (Calendar) cal.clone();
            ad.dateStr = sdf.format(cal.getTime());
            ad.weekday = wdf.format(cal.getTime());
            ad.dayPillar = dayPillar;
            ad.jianChu = jianChu;
            ad.yi = HuangLi.formatYiJi(HuangLi.getYi(jianChu), "宜：");
            ad.ji = HuangLi.formatYiJi(HuangLi.getJi(jianChu), "忌：");
            ad.level = level;
            days.add(ad);

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        resultTitle.setText(cat.name + " · 吉日（共 " + days.size() + " 个，已排除凶日）");
        resultList.setAdapter(new DayAdapter(days));
        resultList.setOnItemClickListener((parent, view, position, id) -> {
            AlmanacDay ad = (AlmanacDay) parent.getItemAtPosition(position);
            onDaySelected(ad);
        });
    }

    private String judgeLevel(String jianChu, Category cat) {
        for (String g : cat.good) {
            if (g.equals(jianChu)) return "吉";
        }
        for (String b : cat.bad) {
            if (b.equals(jianChu)) return "凶";
        }
        return "平";
    }

    private class DayAdapter extends BaseAdapter {
        private final List<AlmanacDay> data;

        DayAdapter(List<AlmanacDay> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_auspicious, parent, false);
            }
            AlmanacDay ad = data.get(position);
            TextView itemDate = convertView.findViewById(R.id.itemDate);
            TextView itemLevel = convertView.findViewById(R.id.itemLevel);
            TextView itemPillar = convertView.findViewById(R.id.itemPillar);
            TextView itemYi = convertView.findViewById(R.id.itemYi);
            TextView itemJi = convertView.findViewById(R.id.itemJi);

            itemDate.setText(ad.dateStr + "  " + ad.weekday);
            itemPillar.setText("日柱 " + ad.dayPillar + "　" + ad.jianChu + "日");
            itemYi.setText(ad.yi);
            itemJi.setText(ad.ji);

            if ("吉".equals(ad.level)) {
                itemLevel.setText("吉日");
                itemLevel.setTextColor(0xFF37C871);
            } else {
                itemLevel.setText("平");
                itemLevel.setTextColor(0xFFC9A86A);
            }

            return convertView;
        }
    }

    // 列表项点击：弹确认框，确认后将日期回传首页
    private void onDaySelected(AlmanacDay ad) {
        new AlertDialog.Builder(this)
                .setTitle("设为当前日期")
                .setMessage("将首页日期切换为 " + ad.dateStr + " 并返回？\n（该日为 " + ad.dayPillar + " " + ad.jianChu + "日）")
                .setPositiveButton("确定", (dialog, which) -> {
                    Intent result = new Intent();
                    result.putExtra("sel_year", ad.cal.get(Calendar.YEAR));
                    result.putExtra("sel_month", ad.cal.get(Calendar.MONTH) + 1);
                    result.putExtra("sel_day", ad.cal.get(Calendar.DAY_OF_MONTH));
                    setResult(RESULT_OK, result);
                    finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

}
