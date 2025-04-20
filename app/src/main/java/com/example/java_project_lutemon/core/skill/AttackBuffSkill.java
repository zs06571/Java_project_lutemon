package com.example.java_project_lutemon.core.skill;

import com.example.java_project_lutemon.core.model.Lutemon;

public class AttackBuffSkill extends Skill {

    public AttackBuffSkill() {
        super("Focus Beam", 3, 2, 0, 1);
    }
    public AttackBuffSkill(int level) {
        super("Focus Beam", 3, 2, 0, level);
    }

    @Override
    public void apply(Lutemon caster, Lutemon target) {
        if (isReady()) {
            int baseAttack = caster.getAttack();
            int boost = (int)(baseAttack * (1.1 + 0.1 * (level - 1)));
            target.setAttack(boost);
            use();
        }
    }

    @Override
    public String getDescription() {
        return "Focus Beam: Deal " + (110 + (level - 1) * 10) + "% ATK damage.";
    }
}
