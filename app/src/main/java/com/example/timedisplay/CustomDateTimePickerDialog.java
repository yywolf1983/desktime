package com.example.timedisplay;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomDateTimePickerDialog extends Dialog {

    private GridView yearGrid, monthGrid, dayGrid, hourGrid, minuteGrid;
    private TextView ymTitle;
    private TextView tvYiJi, tvDateGap;
    private TextView cancelButton, confirmButton;
    private TextView sumYear, sumMonth, sumDay, sumHour, sumMinute;
    private OnDateTimeSetListener listener;
    private View infoBlock;

    private int selYear, selMonth, selDay, selHour, selMinute;
    private int yearPageBase;
    private View currentPanel;

    public interface OnDateTimeSetListener {
        void onDateTimeSet(int year, int month, int day, int hour, int minute);
    }

    public CustomDateTimePickerDialog(Context context, Calendar currentCalendar, OnDateTimeSetListener listener) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.listener = listener;

        selYear = currentCalendar.get(Calendar.YEAR);
        selMonth = currentCalendar.get(Calendar.MONTH) + 1;
        selDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        selHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        selMinute = currentCalendar.get(Calendar.MINUTE);
        yearPageBase = (selYear / 20) * 20;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_datetime_picker);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        initViews();
        refreshSummary();
        updateYiJi();
        updateDateGap();
    }

    private void initViews() {
        // 外层 ScrollView:关闭垂直滚动条与 overScroll(android:verticalScrollBarEnabled 不是合法 XML 属性,只能代码设置)
        View outer = findViewById(R.id.dateTimeScrollView);
        if (outer != null) {
            outer.setVerticalScrollBarEnabled(false);
            outer.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        yearGrid = findViewById(R.id.yearGrid);
        monthGrid = findViewById(R.id.monthGrid);
        dayGrid = findViewById(R.id.dayGrid);
        hourGrid = findViewById(R.id.hourGrid);
        minuteGrid = findViewById(R.id.minuteGrid);
        // 5 个 GridView:关闭垂直滚动条(android:verticalScrollBarEnabled 非合法 XML 属性)
        for (GridView gv : new GridView[]{yearGrid, monthGrid, dayGrid, hourGrid, minuteGrid}) {
            gv.setVerticalScrollBarEnabled(false);
            gv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        ymTitle = findViewById(R.id.dayTitle);
        tvYiJi = findViewById(R.id.tvYiJi);
        tvDateGap = findViewById(R.id.tvDateGap);
        infoBlock = findViewById(R.id.infoBlock);
        cancelButton = findViewById(R.id.cancelButton);
        confirmButton = findViewById(R.id.confirmButton);
        sumYear = findViewById(R.id.sumYear);
        sumMonth = findViewById(R.id.sumMonth);
        sumDay = findViewById(R.id.sumDay);
        sumHour = findViewById(R.id.sumHour);
        sumMinute = findViewById(R.id.sumMinute);

        // 顶部日期文字各部分单独点击展开对应面板（24小时制国际标准）
        bindPart(R.id.sumYear, R.id.yearPanel, R.id.yearChevron, () -> showYearPage());
        bindPart(R.id.sumMonth, R.id.monthPanel, R.id.monthChevron, () -> showMonthPage());
        bindPart(R.id.sumDay, R.id.dayPanel, R.id.dayChevron, () -> showDayPage());
        bindPart(R.id.sumHour, R.id.hourPanel, R.id.hourChevron, () -> highlightOnly(hourGrid, selHour));
        bindPart(R.id.sumMinute, R.id.minutePanel, R.id.minuteChevron, () -> highlightOnly(minuteGrid, selMinute / 5));

        // 日：网格点选
        dayGrid.setOnItemClickListener((parent, view, position, id) -> {
            selDay = position + 1;
            refreshRows();
            refreshSummary();
            updateYiJi();
            updateDateGap();
            closePanelAfterPick(R.id.dayPanel, R.id.dayChevron);
        });

        // 月：网格点选
        monthGrid.setOnItemClickListener((parent, view, position, id) -> {
            selMonth = position + 1;
            refreshRows();
            refreshSummary();
            updateYiJi();
            updateDateGap();
            closePanelAfterPick(R.id.monthPanel, R.id.monthChevron);
        });

        // 年份翻页
        findViewById(R.id.yearPrev).setOnClickListener(v -> { yearPageBase -= 20; showYearPage(); });
        findViewById(R.id.yearNext).setOnClickListener(v -> { yearPageBase += 20; showYearPage(); });

        // 日面板翻月
        findViewById(R.id.dayPrev).setOnClickListener(v -> stepMonth(-1));
        findViewById(R.id.dayNext).setOnClickListener(v -> stepMonth(1));

        // 时：24小时网格（00-23）
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) hours.add(String.format("%02d", i));
        hourGrid.setAdapter(makeAdapter(hours, selHour));
        hourGrid.setOnItemClickListener((parent, view, position, id) -> {
            selHour = position;
            highlightOnly(hourGrid, position);
            refreshRows();
            refreshSummary();
            closePanelAfterPick(R.id.hourPanel, R.id.hourChevron);
        });

        // 分：以5分段 00,05,...,55
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i < 60; i += 5) minutes.add(String.format("%02d", i));
        minuteGrid.setAdapter(makeAdapter(minutes, selMinute / 5));
        minuteGrid.setOnItemClickListener((parent, view, position, id) -> {
            selMinute = position * 5;
            highlightOnly(minuteGrid, position);
            refreshRows();
            refreshSummary();
            closePanelAfterPick(R.id.minutePanel, R.id.minuteChevron);
        });

        // 五个独立项绑定已移至上方 bindPart 调用

        cancelButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateTimeSet(selYear, selMonth, selDay, selHour, selMinute);
            }
            dismiss();
        });
    }

    private void bindPart(int valueId, int panelId, int chevronId, Runnable onOpen) {
        View panel = findViewById(panelId);
        TextView chevron = findViewById(chevronId);
        View value = findViewById(valueId);
        value.setClickable(true);
        value.setOnClickListener(v -> {
            boolean open = panel.getVisibility() != View.VISIBLE;
            if (currentPanel != null && currentPanel != panel) {
                currentPanel.setVisibility(View.GONE);
                resetChevron(currentPanel);
            }
            panel.setVisibility(open ? View.VISIBLE : View.GONE);
            chevron.setText(open ? "∨" : "›");
            if (open) {
                // 打开选择面板:临时收起底部宜忌/日期差距块,给低分辨率屏幕腾出显示空间
                if (infoBlock != null) infoBlock.setVisibility(View.GONE);
                currentPanel = panel;
                onOpen.run();
            } else {
                // 关闭面板:恢复底部信息块
                if (infoBlock != null) infoBlock.setVisibility(View.VISIBLE);
                currentPanel = null;
            }
        });
    }

    // 面板选中项后统一收尾:收起面板、复位箭头、恢复底部宜忌信息块显示
    private void closePanelAfterPick(int panelId, int chevronId) {
        View panel = findViewById(panelId);
        panel.setVisibility(View.GONE);
        ((TextView) findViewById(chevronId)).setText("›");
        currentPanel = null;
        if (infoBlock != null) infoBlock.setVisibility(View.VISIBLE);
    }

    private void resetChevron(View panel) {
        if (panel == null) return;
        int chevronId;
        if (panel.getId() == R.id.yearPanel) chevronId = R.id.yearChevron;
        else if (panel.getId() == R.id.monthPanel) chevronId = R.id.monthChevron;
        else if (panel.getId() == R.id.dayPanel) chevronId = R.id.dayChevron;
        else if (panel.getId() == R.id.hourPanel) chevronId = R.id.hourChevron;
        else if (panel.getId() == R.id.minutePanel) chevronId = R.id.minuteChevron;
        else return;
        ((TextView) findViewById(chevronId)).setText("›");
    }

    private void showYearPage() {
        List<String> years = new ArrayList<>();
        for (int i = 0; i < 20; i++) years.add(String.valueOf(yearPageBase + i));
        yearGrid.setAdapter(makeAdapter(years, selYear - yearPageBase));
        yearGrid.setOnItemClickListener((parent, view, position, id) -> {
            selYear = yearPageBase + position;
            refreshRows();
            refreshSummary();
            updateYiJi();
            updateDateGap();
            // 选中后收起年面板,并恢复底部宜忌信息块显示
            closePanelAfterPick(R.id.yearPanel, R.id.yearChevron);
        });
        TextView page = findViewById(R.id.yearPage);
        if (page != null) page.setText(String.format("%d – %d", yearPageBase, yearPageBase + 19));
    }

    private void showMonthPage() {
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) months.add(String.valueOf(i));
        monthGrid.setAdapter(makeAdapter(months, selMonth - 1));
    }

    private void showDayPage() {
        int max = maxDay(selYear, selMonth);
        List<String> days = new ArrayList<>();
        for (int i = 1; i <= max; i++) days.add(String.valueOf(i));
        dayGrid.setAdapter(makeAdapter(days, selDay - 1));
        dayGrid.setNumColumns(max > 28 ? 7 : 7);
        ymTitle.setText(String.format("%d-%02d", selYear, selMonth));
    }

    private void stepMonth(int delta) {
        selMonth += delta;
        if (selMonth < 1) { selMonth = 12; selYear--; }
        if (selMonth > 12) { selMonth = 1; selYear++; }
        int max = maxDay(selYear, selMonth);
        if (selDay > max) selDay = max;
        showDayPage();
        refreshRows();
        refreshSummary();
        updateYiJi();
        updateDateGap();
    }

    private void refreshRows() {
        setText(R.id.sumYear, String.valueOf(selYear));
        setText(R.id.sumMonth, String.format("%02d", selMonth));
        setText(R.id.sumDay, String.format("%02d", selDay));
        setText(R.id.sumHour, String.format("%02d", selHour));
        setText(R.id.sumMinute, String.format("%02d", selMinute));
    }

    private void refreshSummary() {
        refreshRows();
    }

    private void setText(int id, String s) {
        TextView tv = findViewById(id);
        if (tv != null) tv.setText(s);
    }

    private ArrayAdapter<String> makeAdapter(List<String> items, int selected) {
        final int widthDp = getContext().getResources().getConfiguration().screenWidthDp;
        // 低分辨率(<360dp)整体缩小一格,避免年选择折行;极窄屏(<320dp)进一步压缩
        final int textSizeSp;
        final int padDp;
        if (widthDp < 320) {
            textSizeSp = 12;
            padDp = 2;
        } else if (widthDp < 360) {
            textSizeSp = 14;
            padDp = 3;
        } else {
            textSizeSp = 16;
            padDp = 4;
        }
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int padPx = (int) (padDp * density + 0.5f);
        final int minHeightPx = (int) (36 * density + 0.5f);

        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setGravity(Gravity.CENTER);
                // 强制单行 + 不横向滚动:彻底解决年选择4位数字在窄屏上折行的问题
                tv.setSingleLine(true);
                tv.setMaxLines(1);
                tv.setHorizontallyScrolling(false);
                tv.setEllipsize(null);
                tv.setTextSize(textSizeSp);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setPadding(padPx, padPx, padPx, padPx);
                tv.setMinHeight(minHeightPx);
                if (position == selected) {
                    tv.setTextColor(Color.parseColor("#CCB866"));
                    tv.setBackgroundResource(R.drawable.card_background);
                } else {
                    tv.setTextColor(Color.parseColor("#E8DFC8"));
                    tv.setBackgroundColor(Color.TRANSPARENT);
                }
                // API 26+:启用自动按宽度缩放字号,窄屏字号自动再收一档
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    tv.setAutoSizeTextTypeUniformWithConfiguration(
                            Math.max(10, textSizeSp - 4), textSizeSp, 1,
                            android.util.TypedValue.COMPLEX_UNIT_SP);
                }
                return tv;
            }
        };
    }

    private void highlightOnly(GridView grid, int position) {
        for (int i = 0; i < grid.getChildCount(); i++) {
            TextView tv = (TextView) grid.getChildAt(i);
            if (tv == null) continue;
            if (i == position) {
                tv.setTextColor(Color.parseColor("#CCB866"));
                tv.setBackgroundResource(R.drawable.card_background);
            } else {
                tv.setTextColor(Color.parseColor("#E8DFC8"));
                tv.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private int maxDay(int y, int m) {
        Calendar c = Calendar.getInstance();
        c.set(y, m - 1, 1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void updateDateGap() {
        if (tvDateGap == null) return;
        try {
            Calendar now = Calendar.getInstance();
            now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            now.set(Calendar.MILLISECOND, 0);

            Calendar target = Calendar.getInstance();
            target.set(selYear, selMonth - 1, selDay, 0, 0, 0);
            target.set(Calendar.MILLISECOND, 0);

            long diffDays = (target.getTimeInMillis() - now.getTimeInMillis()) / (1000L * 60 * 60 * 24);

            if (diffDays == 0) {
                tvDateGap.setText("与今日：即今日（0天）");
                return;
            }

            boolean future = diffDays > 0;
            int sign = future ? 1 : -1;

            int yDiff = selYear - now.get(Calendar.YEAR);
            int mDiff = selMonth - (now.get(Calendar.MONTH) + 1);
            int dDiff = selDay - now.get(Calendar.DAY_OF_MONTH);

            if (dDiff * sign < 0) {
                mDiff -= sign;
                Calendar prev = (Calendar) target.clone();
                prev.add(Calendar.DATE, -sign);
                dDiff += sign * prev.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            if (mDiff * sign < 0) {
                yDiff -= sign;
                mDiff += sign * 12;
            }

            String prefix = future ? "还需" : "已过去";
            tvDateGap.setText("与今日：" + prefix + " " + yDiff + "年" + mDiff + "个月" + dDiff + "天（" + (future ? "+" : "") + diffDays + "天）");
        } catch (Exception e) {
            tvDateGap.setText("与今日：—");
        }
    }

    private void updateYiJi() {
        if (tvYiJi == null) return;
        try {
            String html = DestinyCalculator.getDailyYiJi(selYear, selMonth, selDay);
            tvYiJi.setText(Html.fromHtml(html));
        } catch (Exception e) {
            tvYiJi.setText("宜忌加载中...");
        }
    }
}
