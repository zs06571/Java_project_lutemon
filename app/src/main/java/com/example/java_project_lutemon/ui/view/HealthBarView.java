package com.example.java_project_lutemon.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.graphics.Paint;


public class HealthBarView extends View {
    private int maxHealth = 100;
    private int currentHealth = 100;
    private final Paint bgPaint = new Paint();
    private final Paint fgPaint = new Paint();

    public HealthBarView(Context context) {
        super(context);
        initPaints();
    }

    private void initPaints() {
        bgPaint.setColor(Color.GRAY);
        fgPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = getWidth() * ((float)currentHealth / maxHealth);
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
        canvas.drawRect(0, 0, width, getHeight(), fgPaint);
    }

    public void setMaxHealth(int max) {
        this.maxHealth = max;
        invalidate();
    }

    public void setCurrentHealth(int current) {
        this.currentHealth = current;
        invalidate();
    }
}