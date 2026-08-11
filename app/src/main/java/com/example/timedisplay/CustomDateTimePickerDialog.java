package com.example.timedisplay;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;

public class CustomDateTimePickerDialog extends Dialog {

    private EditText yearEdit;
    private TextView yearMinus, yearPlus;
    private NumberPicker monthPicker, dayPicker, hourPicker, minutePicker;
    private TextView cancelButton, confirmButton;
    private TextView tvYiJi;
    private TextView tvDateGap;
    private OnDateTimeSetListener listener;

    public interface OnDateTimeSetListener {
        void onDateTimeSet(int year, int month, int day, int hour, int minute);
    }

    public CustomDateTimePickerDialog(Context context, Calendar currentCalendar, OnDateTimeSetListener listener) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.listener = listener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_datetime_picker);

        // Make dialog background transparent
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        initPickers(currentCalendar);
        setupButtons();
        setupYiJi();
    }

    private void setupYiJi() {
        tvYiJi = findViewById(R.id.tvYiJi);
        tvDateGap = findViewById(R.id.tvDateGap);
        if (tvYiJi != null) {
            updateYiJi();
        }
        updateDateGap();

        // 年/月/日变化时同时更新日期范围、宜忌及与今日差距
        NumberPicker.OnValueChangeListener dayAndYiJiUpdater = (picker, oldVal, newVal) -> {
            updateDayRange();
            updateYiJi();
            updateDateGap();
        };
        monthPicker.setOnValueChangedListener(dayAndYiJiUpdater);

        // 日变化时更新宜忌及差距
        dayPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            updateYiJi();
            updateDateGap();
        });
    }

    private void updateDateGap() {
        if (tvDateGap == null) return;
        try {
            int y = getYearValue();
            int m = monthPicker.getValue();
            int d = dayPicker.getValue();

            Calendar now = Calendar.getInstance();
            now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            now.set(Calendar.MILLISECOND, 0);

            Calendar target = Calendar.getInstance();
            target.set(y, m - 1, d, 0, 0, 0);
            target.set(Calendar.MILLISECOND, 0);

            long diffDays = (target.getTimeInMillis() - now.getTimeInMillis()) / (1000L * 60 * 60 * 24);

            if (diffDays == 0) {
                tvDateGap.setText("与今日：即今日（0天）");
                return;
            }

            boolean future = diffDays > 0;
            int sign = future ? 1 : -1;

            int yDiff = y - now.get(Calendar.YEAR);
            int mDiff = m - (now.get(Calendar.MONTH) + 1);
            int dDiff = d - now.get(Calendar.DAY_OF_MONTH);

            // 日借位
            if (dDiff * sign < 0) {
                mDiff -= sign;
                Calendar prev = (Calendar) target.clone();
                prev.add(Calendar.DATE, -sign);
                dDiff += sign * prev.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            // 月借位
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
            int year = getYearValue();
            int month = monthPicker.getValue();
            int day = dayPicker.getValue();
            String html = DestinyCalculator.getDailyYiJi(year, month, day);
            tvYiJi.setText(Html.fromHtml(html));
        } catch (Exception e) {
            tvYiJi.setText("宜忌加载中...");
        }
    }

    private void initPickers(Calendar calendar) {
        yearEdit = findViewById(R.id.yearEdit);
        yearMinus = findViewById(R.id.yearMinus);
        yearPlus = findViewById(R.id.yearPlus);
        monthPicker = findViewById(R.id.monthPicker);
        dayPicker = findViewById(R.id.dayPicker);
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);

        int currentYear = calendar.get(Calendar.YEAR);

        // Year: 可点击输入 + 加减按钮，避免滚轮滚动上百年的麻烦
        yearEdit.setText(String.valueOf(currentYear));
        yearMinus.setOnClickListener(v -> adjustYear(-1));
        yearPlus.setOnClickListener(v -> adjustYear(1));
        yearEdit.setOnEditorActionListener((v, actionId, event) -> {
            syncYearFromInput();
            yearEdit.clearFocus();
            return true;
        });
        yearEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) syncYearFromInput();
        });
        yearEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                updateDateGap();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // Month: 1-12，显示为 "01月" 格式
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        monthPicker.setFormatter(i -> String.format("%02d月", i));

        // Day: 1-31 (will update based on month)，显示为 "01日" 格式
        updateDayRange();
        dayPicker.setValue(calendar.get(Calendar.DAY_OF_MONTH));
        dayPicker.setFormatter(i -> String.format("%02d日", i));

        // Hour: 0-23，显示为 "00时" 格式
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        hourPicker.setFormatter(i -> String.format("%02d时", i));

        // Minute: 0-59，显示为 "00分" 格式
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(calendar.get(Calendar.MINUTE));
        minutePicker.setFormatter(i -> String.format("%02d分", i));

        // Update day when month changes
        NumberPicker.OnValueChangeListener dayUpdater = (picker, oldVal, newVal) -> updateDayRange();
        monthPicker.setOnValueChangedListener(dayUpdater);

        // Style the pickers
        styleNumberPicker(monthPicker);
        styleNumberPicker(dayPicker);
        styleNumberPicker(hourPicker);
        styleNumberPicker(minutePicker);
    }

    private int getYearValue() {
        try {
            String s = yearEdit.getText().toString().trim();
            if (s.isEmpty()) return Calendar.getInstance().get(Calendar.YEAR);
            return Integer.parseInt(s);
        } catch (Exception e) {
            return Calendar.getInstance().get(Calendar.YEAR);
        }
    }

    private void syncYearFromInput() {
        int y = getYearValue();
        yearEdit.setText(String.valueOf(y));
        updateDayRange();
    }

    private void adjustYear(int delta) {
        int y = getYearValue() + delta;
        yearEdit.setText(String.valueOf(y));
        updateDayRange();
    }

    private void updateDayRange() {
        int year = getYearValue();
        int month = monthPicker.getValue();
        int maxDay = getMaxDayOfMonth(year, month);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(maxDay);
        if (dayPicker.getValue() > maxDay) {
            dayPicker.setValue(maxDay);
        }
    }

    private int getMaxDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void styleNumberPicker(NumberPicker picker) {
        // Disable manual keyboard input - scroll only
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // 设置分割线颜色为半透明金色
        try {
            java.lang.reflect.Field[] fields = picker.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getName().equals("mSelectionDivider")) {
                    field.setAccessible(true);
                    java.lang.reflect.Field colorField = java.lang.reflect.Field.class.getDeclaredField("accessFlags");
                    colorField.setAccessible(true);
                    colorField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
                    field.set(picker, null);
                }
                if (field.getName().equals("mSelectionDividerHeight")) {
                    field.setAccessible(true);
                    field.set(picker, 2);
                }
            }
        } catch (Exception ignored) {}

        // 设置滚轮文字颜色为金色、放大
        try {
            java.lang.reflect.Field paintField = picker.getClass().getDeclaredField("mSelectorWheelPaint");
            paintField.setAccessible(true);
            android.text.TextPaint paint = (android.text.TextPaint) paintField.get(picker);
            paint.setColor(Color.parseColor("#CCB866"));
            paint.setTextSize(42f);
            paint.setAntiAlias(true);
            paint.setFakeBoldText(true);
            picker.invalidate();
        } catch (Exception ignored) {}

        // 也尝试设置内部 EditText 的文字颜色
        try {
            int childCount = picker.getChildCount();
            for (int i = 0; i < childCount; i++) {
                android.view.View child = picker.getChildAt(i);
                if (child instanceof android.widget.EditText) {
                    android.widget.EditText editText = (android.widget.EditText) child;
                    editText.setTextColor(Color.parseColor("#CCB866"));
                    editText.setTextSize(24f);
                }
            }
        } catch (Exception ignored) {}
    }

    private void setupButtons() {
        cancelButton = findViewById(R.id.cancelButton);
        confirmButton = findViewById(R.id.confirmButton);

        cancelButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateTimeSet(
                    getYearValue(),
                    monthPicker.getValue(),
                    dayPicker.getValue(),
                    hourPicker.getValue(),
                    minutePicker.getValue()
                );
            }
            dismiss();
        });
    }
}
