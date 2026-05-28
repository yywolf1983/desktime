package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class WuyunLiuqiView extends View {
    private Paint paint;
    private Paint textPaint;
    private Paint highlightPaint;
    
    private int currentShichenIndex = 0;
    private String[] shichenNames = {
        "子", "丑", "寅", "卯", "辰", "巳",
        "午", "未", "申", "酉", "戌", "亥"
    };
    
    private String[] shichenTimes = {
        "23:00-01:00", "01:00-03:00", "03:00-05:00",
        "05:00-07:00", "07:00-09:00", "09:00-11:00",
        "11:00-13:00", "13:00-15:00", "15:00-17:00",
        "17:00-19:00", "19:00-21:00", "21:00-23:00"
    };
    
    private String[] shichenQuotes = {
        "夜半", "鸡鸣", "平旦", "日出", "食时", "隅中",
        "日中", "日昳", "晡时", "日入", "黄昏", "人定"
    };
    
    private String[] wuxingLabels = {
        "水·鼠", "土·牛", "木·虎", "木·兔", "土·龙", "火·蛇",
        "火·马", "土·羊", "金·猴", "金·鸡", "土·狗", "水·猪"
    };

    public WuyunLiuqiView(Context context) {
        super(context);
        init();
    }

    public WuyunLiuqiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WuyunLiuqiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        textPaint.setColor(Color.parseColor("#87CEEB"));
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        highlightPaint.setColor(Color.parseColor("#FFD700"));
        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setStrokeWidth(4);
    }

    public void setCurrentShichen(int index) {
        this.currentShichenIndex = index;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        int padding = 12;
        
        paint.setColor(Color.parseColor("#0A0A14"));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, paint);
        
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        String timeStr = String.format("%02d:%02d:%02d", hour, minute, second);
        String currentShichen = shichenNames[currentShichenIndex] + "时 · " + shichenQuotes[currentShichenIndex];
        
        int gridTop = 130;
        int gridHeight = height - gridTop - padding;
        int gridWidth = width - padding * 2;
        
        float cellWidth = gridWidth / 4.0f;
        float cellHeight = gridHeight / 3.0f;
        float minCell = Math.min(cellWidth, cellHeight);
        
        // 基于cell大小设置文字
        textPaint.setTextSize(minCell * 0.45f);
        textPaint.setColor(Color.parseColor("#FFD700"));
        canvas.drawText(timeStr, width / 2f, 70, textPaint);
        
        textPaint.setTextSize(minCell * 0.27f);
        textPaint.setColor(Color.parseColor("#87CEEB"));
        canvas.drawText(currentShichen, width / 2f, 115, textPaint);
        
        float cornerRadius = 12;
        float cellPadding = 4;
        
        for (int i = 0; i < 12; i++) {
            int row = i / 4;
            int col = i % 4;
            
            float left = padding + col * cellWidth;
            float top = gridTop + row * cellHeight;
            float right = left + cellWidth;
            float bottom = top + cellHeight;
            
            boolean isCurrent = i == currentShichenIndex;
            if (isCurrent) {
                paint.setColor(Color.parseColor("#FFD700"));
                paint.setAlpha(180);
            } else {
                paint.setColor(Color.parseColor("#1a1a2e"));
                paint.setAlpha(220);
            }
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(left + cellPadding, top + cellPadding, right - cellPadding, bottom - cellPadding, cornerRadius, cornerRadius, paint);
            
            if (isCurrent) {
                canvas.drawRoundRect(left + cellPadding, top + cellPadding, right - cellPadding, bottom - cellPadding, cornerRadius, cornerRadius, highlightPaint);
            }
            
            float centerX = (left + right) / 2;
            float centerY = (top + bottom) / 2;
            
            textPaint.setTextSize(minCell * 0.35f);
            textPaint.setColor(isCurrent ? Color.parseColor("#0A0A14") : Color.parseColor("#FFD700"));
            canvas.drawText(shichenNames[i], centerX, centerY - minCell * 0.15f, textPaint);
            
            textPaint.setTextSize(minCell * 0.17f);
            textPaint.setColor(isCurrent ? Color.parseColor("#1a1a2e") : Color.parseColor("#87CEEB"));
            canvas.drawText(shichenTimes[i], centerX, centerY + minCell * 0.06f, textPaint);
            
            textPaint.setTextSize(minCell * 0.15f);
            textPaint.setColor(isCurrent ? Color.parseColor("#2a2a3e") : Color.parseColor("#ADD8E6"));
            canvas.drawText(wuxingLabels[i], centerX, centerY + minCell * 0.27f, textPaint);
        }
    }
}
