package com.example.java_project_lutemon.core.model;

import com.example.java_project_lutemon.core.skill.DebuffSkill;

public class Black extends Lutemon {
    public Black(String name, int color, int id) {
        super(name, color, 16, 9, 0, id);
        // HP:16, Attack:9, Defense:0
        this.type = LutemonType.BLACK;
        addSkill(new DebuffSkill(1));
    }

    @Override
    public LutemonType getType() {
        return LutemonType.BLACK;
    }

    public Black() {
        super("Default", LutemonType.BLACK.getColorResId(), 16, 9, 0, 0);
        this.type = LutemonType.BLACK;
        addSkill(new DebuffSkill(1));
    }
}

