package com.example.java_project_lutemon.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;

public class LutemonCardView extends ConstraintLayout {
    private final ImageView icon;
    private final TextView nameText;
    private final ProgressBar healthBar;

    public LutemonCardView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_lutemon_card, this);
        icon = findViewById(R.id.img_avatar);
        nameText = findViewById(R.id.txt_name);
        healthBar = findViewById(R.id.progress_bar);
    }

    public void bind(Lutemon lutemon) {
        nameText.setText(lutemon.getName());
        healthBar.setMax(lutemon.getMaxHp());
        healthBar.setProgress(lutemon.getCurrentHp());
        icon.setColorFilter(lutemon.getColor());
    }
}