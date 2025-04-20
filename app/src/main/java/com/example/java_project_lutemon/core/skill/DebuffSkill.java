package com.example.java_project_lutemon.core.skill;

import com.example.java_project_lutemon.core.battle.BattleManager;
import com.example.java_project_lutemon.core.model.Lutemon;

public class DebuffSkill extends Skill {

    public DebuffSkill() {
        super("Shadow Seal", 2, 0, 2, 1);
    }

    public DebuffSkill(int level) {
        super("Shadow Seal", 2, 0, 2, level);
    }

    @Override
    public void apply(Lutemon caster, Lutemon target) {
        if (isReady()) {
            int atk = target.getAttack();
            int def = target.getDefense();
            float factor = 1 - (0.2f + 0.05f * (level - 1));

            target.setAttack((int)(atk * factor));
            target.setDefense((int)(def * factor));

            BattleManager.scheduleReset(() -> {
                target.setAttack(atk);
                target.setDefense(def);
            }, duration);

            use();
        }
    }

    @Override
    public String getDescription() {
        return "Shadow Seal: Reduce enemy ATK/DEF by " + (20 + (level - 1) * 5) + "% for 2 turns.";
    }
}
