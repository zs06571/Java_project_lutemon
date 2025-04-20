package com.example.java_project_lutemon.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ColorPickerView extends View {
    private int selectedColor = 0xFF000000;

    public ColorPickerView(Context context) {
        super(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSelectedColor(int color) {
        this.selectedColor = color;
        invalidate();
    }

    public int getSelectedColor() {
        return selectedColor;
    }
}

