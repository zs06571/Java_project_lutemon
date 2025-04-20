package com.example.java_project_lutemon.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.java_project_lutemon.core.skill.Skill;

public class SkillButtonView extends androidx.appcompat.widget.AppCompatButton {
    private static final int COOLDOWN_COLOR = Color.argb(150, 0, 0, 0);
    private final Paint cooldownPaint = new Paint();
    private final RectF arcRect = new RectF();
    private Skill boundSkill;
    private float sweepAngle = 0;

    public SkillButtonView(Context context) {
        super(context);
        init();
    }

    public SkillButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        cooldownPaint.setColor(COOLDOWN_COLOR);
        cooldownPaint.setStyle(Paint.Style.FILL);
        setAllCaps(false);
    }

    public void bind(Skill skill) {
        this.boundSkill = skill;
        setText(skill.getName());
        updateCooldownState();
    }

    private void updateCooldownState() {
        if (boundSkill == null) return;

        boolean isReady = boundSkill.isReady();
        setEnabled(isReady);
        sweepAngle = isReady ? 0 : 360 * (1 - boundSkill.getCooldownPercentage());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (sweepAngle > 0) {
            arcRect.set(0, 0, getWidth(), getHeight());
            canvas.drawArc(arcRect, -90, sweepAngle, true, cooldownPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        postDelayed(cooldownUpdater, 100);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(cooldownUpdater);
        super.onDetachedFromWindow();
    }

    private final Runnable cooldownUpdater = new Runnable() {
        @Override
        public void run() {
            if (boundSkill != null) {
                updateCooldownState();
                postDelayed(this, 100);
            }
        }
    };
}
