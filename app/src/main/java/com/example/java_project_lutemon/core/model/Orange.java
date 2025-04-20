package com.example.java_project_lutemon.core.model;

import com.example.java_project_lutemon.core.skill.AttackBuffSkill;

public class Orange extends Lutemon {
    public Orange(String name, int color, int id) {
        super(name, color, 17, 8, 1, id); // HP:17, Attack:8, Defense:1
        addSkill(new AttackBuffSkill(1));
        this.type = LutemonType.ORANGE;
    }

    @Override
    public LutemonType getType() {
        return LutemonType.ORANGE;
    }

    public Orange() {
        super("Default", LutemonType.ORANGE.getColorResId(), 17, 8, 1, 0);
        addSkill(new AttackBuffSkill(1));
        this.type = LutemonType.ORANGE;
    }
}