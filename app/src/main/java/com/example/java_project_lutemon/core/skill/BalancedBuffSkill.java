package com.example.java_project_lutemon.core.skill;

import com.example.java_project_lutemon.core.battle.BattleManager;
import com.example.java_project_lutemon.core.model.Lutemon;

public class BalancedBuffSkill extends Skill {

    public BalancedBuffSkill() {
        super("Fury Boost", 3, 0, 1, 1);
    }

    public BalancedBuffSkill(int level) {
        super("Fury Boost", 3, 0, 1, level);
    }

    @Override
    public void apply(Lutemon caster, Lutemon target) {
        if (isReady()) {
            int atkBoost = (int)(caster.getAttack() * (1 + 0.25 + 0.05 * (level - 1)));
            int defBoost = (int)(caster.getDefense() * (1 + 0.10 + 0.02 * (level - 1)));

            caster.setAttack(atkBoost);
            caster.setDefense(defBoost);

            BattleManager.scheduleReset(() -> {
                caster.setAttack(caster.getBaseAttack());
                caster.setDefense(caster.getBaseDefense());
            }, duration);

            use();
        }
    }

    @Override
    public String getDescription() {
        return "Fury Boost: ATK +" + (25 + (level - 1) * 5) +
                "%, DEF +" + (10 + (level - 1) * 2) + "% for 1 turn.";
    }
}
