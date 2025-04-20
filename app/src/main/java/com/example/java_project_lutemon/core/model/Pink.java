package com.example.java_project_lutemon.core.model;

import com.example.java_project_lutemon.core.skill.DefenseBuffSkill;

public class Pink extends Lutemon {
    public Pink(String name, int color, int id) {
        super(name, color, 18, 7, 2, id); // HP:18, Attack:7, Defense:2
        addSkill(new DefenseBuffSkill(1));
        this.type = LutemonType.PINK;
    }

    @Override
    public LutemonType getType() {
        return LutemonType.PINK;
    }

    public Pink() {
        super("Default", LutemonType.PINK.getColorResId(), 18, 7, 2, 0);
        addSkill(new DefenseBuffSkill(1));
        this.type = LutemonType.PINK;
    }
}
