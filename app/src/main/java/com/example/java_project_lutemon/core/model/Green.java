package com.example.java_project_lutemon.core.model;

import com.example.java_project_lutemon.core.skill.BalancedBuffSkill;

public class Green extends Lutemon {
    public Green(String name, int color, int id) {
        super(name, color, 19, 6, 3, id); // HP:19, Attack:6, Defense:3
        addSkill(new BalancedBuffSkill(1));
        this.type = LutemonType.GREEN;
    }

    @Override
    public LutemonType getType() {
        return LutemonType.GREEN;
    }

    public Green() {
        super("Default", LutemonType.GREEN.getColorResId(), 19, 6, 3, 0);
        addSkill(new BalancedBuffSkill(1));
        this.type = LutemonType.GREEN;
    }
}
