// LutemonType.java
package com.example.java_project_lutemon.core.model;

import com.example.java_project_lutemon.R;

public enum LutemonType {
    WHITE("White", R.color.lutemon_white_color, R.drawable.lutemon_white),
    GREEN("Green", R.color.green, R.drawable.lutemon_green),
    PINK("Pink", R.color.pink, R.drawable.lutemon_pink),
    BLACK("Black", R.color.black, R.drawable.lutemon_black),
    ORANGE("Orange", R.color.orange, R.drawable.lutemon_orange);

    private final String displayName;
    private final int colorResId;
    private final int imageResId;

    LutemonType(String displayName, int colorResId, int imageResId) {
        this.displayName = displayName;
        this.colorResId = colorResId;
        this.imageResId = imageResId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColorResId() {  // 修改方法名
        return colorResId;
    }

    public int getImageResId() {
        return imageResId;
    }
}