package com.example.timedisplay;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class LuoPanActivity extends Activity {
    private LuoPanView luoPanView;
    
    private float currentRotation = 0;
    private float lastAngle = 0;
    private boolean isRotating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luo_pan);
        
        luoPanView = findViewById(R.id.luoPanView);
        
        setupTouchListener();
    }
    
    private void setupTouchListener() {
        luoPanView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float centerX = v.getWidth() / 2f;
                float centerY = v.getHeight() / 2f;
                
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastAngle = getAngle(event.getX(), event.getY(), centerX, centerY);
                        isRotating = true;
                        return true;
                        
                    case MotionEvent.ACTION_MOVE:
                        if (isRotating) {
                            float currentAngle = getAngle(event.getX(), event.getY(), centerX, centerY);
                            float deltaAngle = currentAngle - lastAngle;
                            
                            // 处理角度跨越 -180/180 的情况
                            if (deltaAngle > 180) deltaAngle -= 360;
                            if (deltaAngle < -180) deltaAngle += 360;
                            
                            rotate(deltaAngle);
                            lastAngle = currentAngle;
                        }
                        return true;
                        
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isRotating = false;
                        return true;
                }
                return false;
            }
        });
    }
    
    private float getAngle(float x, float y, float centerX, float centerY) {
        return (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
    }
    
    private void rotate(float degrees) {
        currentRotation += degrees;
        if (currentRotation >= 360) {
            currentRotation -= 360;
        } else if (currentRotation < 0) {
            currentRotation += 360;
        }
        
        luoPanView.setRotation(currentRotation);
    }
}
