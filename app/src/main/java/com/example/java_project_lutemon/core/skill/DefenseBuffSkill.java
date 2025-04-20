package com.example.java_project_lutemon.core.skill;

import com.example.java_project_lutemon.core.battle.BattleManager;
import com.example.java_project_lutemon.core.model.Lutemon;

public class DefenseBuffSkill extends Skill {

    public DefenseBuffSkill() {
        super("Bark Shield", 2, 0, 2, 1);
    }

    public DefenseBuffSkill(int level) {
        super("Bark Shield", 2, 0, 2, level);
    }

    @Override
    public void apply(Lutemon caster, Lutemon target) {
        if (isReady()) {
            int originalDef = caster.getDefense();
            int boosted = (int)(originalDef * (1 + 0.25 + 0.05 * (level - 1)));
            caster.setDefense(boosted);
            BattleManager.scheduleReset(() -> caster.setDefense(originalDef), duration);
            use();
        }
    }

    @Override
    public String getDescription() {
        return "Bark Shield: DEF +" + (25 + (level - 1) * 5) + "% for 2 turns.";
    }
}
