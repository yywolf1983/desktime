package com.example.timedisplay;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;

public class CustomDateTimePickerDialog extends Dialog {

    private NumberPicker yearPicker, monthPicker, dayPicker, hourPicker, minutePicker;
    private TextView cancelButton, confirmButton;
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
    }

    private void initPickers(Calendar calendar) {
        yearPicker = findViewById(R.id.yearPicker);
        monthPicker = findViewById(R.id.monthPicker);
        dayPicker = findViewById(R.id.dayPicker);
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);

        int currentYear = calendar.get(Calendar.YEAR);

        // Year: 当前年份±60年，缩小范围方便快速选择
        yearPicker.setMinValue(currentYear - 60);
        yearPicker.setMaxValue(currentYear + 60);
        yearPicker.setValue(currentYear);
        yearPicker.setWrapSelectorWheel(false);

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

        // Update day when month/year changes
        NumberPicker.OnValueChangeListener dayUpdater = (picker, oldVal, newVal) -> updateDayRange();
        yearPicker.setOnValueChangedListener(dayUpdater);
        monthPicker.setOnValueChangedListener(dayUpdater);

        // Style the pickers
        styleNumberPicker(yearPicker);
        styleNumberPicker(monthPicker);
        styleNumberPicker(dayPicker);
        styleNumberPicker(hourPicker);
        styleNumberPicker(minutePicker);
    }

    private void updateDayRange() {
        int year = yearPicker.getValue();
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
                    yearPicker.getValue(),
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
