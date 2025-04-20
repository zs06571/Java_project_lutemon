package com.example.java_project_lutemon.core.skill;

import com.example.java_project_lutemon.core.model.Lutemon;

public class HealSkill extends Skill {

    public HealSkill() {
        super("Petal Heal", 3, 0, 0, 1);
    }

    public HealSkill(int level) {
        super("Petal Heal", 3, 0, 0, level);
    }

    @Override
    public void apply(Lutemon caster, Lutemon target) {
        if (isReady()) {
            int healAmount = (int)(caster.getMaxHp() * (0.3 + 0.05 * (level - 1)));
            caster.setHp(Math.min(caster.getHp() + healAmount, caster.getMaxHp()));
            use();
        }
    }
    @Override
    public String getDescription() {
        return "Petal Heal: Restore " + (30 + (level - 1) * 5) + "% max HP.";
    }
    @Override
    public boolean shouldSkipDamageCalculation() {
        return true;
    }

}
