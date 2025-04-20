package com.example.java_project_lutemon.core.model;

import com.example.java_project_lutemon.core.skill.HealSkill;

public class White extends Lutemon {
    public White(String name, int color, int id) {
        super(name, color, 20, 4, 5, id);
        addSkill(new HealSkill(1));
        this.type = LutemonType.WHITE;
    }

    @Override
    public LutemonType getType() {
        return LutemonType.WHITE;
    }

    public White() {
        super("Default", LutemonType.WHITE.getColorResId(), 20, 5, 4, 0);
        addSkill(new HealSkill(1));
        this.type = LutemonType.WHITE;
    }
}