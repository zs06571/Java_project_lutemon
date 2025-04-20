package com.example.java_project_lutemon.core.battle;

import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.skill.Skill;

import java.util.ArrayList;
import java.util.List;

public class BattleManager {

    // List of actions to reset temporary effects after each turn
    private static final List<Runnable> pendingResets = new ArrayList<>();

    // Left and right Lutemons participating in the battle
    private Lutemon leftLutemon;
    private Lutemon rightLutemon;
    // Flag to track which side's turn it is (true = left, false = right)
    private boolean isLeftTurn = true;

    // Flag to mark if the battle has ended
    private boolean battleOver = false;

    // Singleton instance
    private static BattleManager instance;

    // Private constructor for singleton
    private BattleManager() {}

    // Initializes a new battle between two Lutemons
    public void startBattle(Lutemon left, Lutemon right) {
        leftLutemon = left;
        rightLutemon = right;
        isLeftTurn = true;
        battleOver = false;
    }

    // Executes one turn using the specified skill (by ID)
    public BattleResult executeTurn(int skillId) {
        if (battleOver) return null;// If battle is already over, skip turn


        Lutemon attacker = isLeftTurn ? leftLutemon : rightLutemon; // Determine attacker
        Lutemon defender = isLeftTurn ? rightLutemon : leftLutemon; // Determine defender

        int damage = calculateDamage(attacker, defender, skillId);
        defender.takeDamage(damage);

        if (defender.getCurrentHp() <= 0) {
            defender.setHp(0);
            battleOver = true;
        }

        for (Skill skill : attacker.getSkillList()) {
            skill.nextTurn();
        }
        for (Skill skill : defender.getSkillList()) {
            skill.nextTurn();
        }

        String log = buildBattleLog(attacker, defender, damage);
        isLeftTurn = !isLeftTurn;

        boolean attackerWins = defender.getCurrentHp() <= 0;
        return new BattleResult(attackerWins, damage, log,
                attackerWins ? attacker : defender, attackerWins ? defender : attacker);
    }

    private int calculateDamage(Lutemon attacker, Lutemon defender, int skillId) {
        if (skillId >= 0 && skillId < attacker.getSkillList().size()) {
            Skill skill = attacker.getSkillList().get(skillId);
            if (skill.isReady()) {
                skill.apply(attacker, defender);
                skill.use();
                if (skill.shouldSkipDamageCalculation()) {
                    return 0;
                }
            }
        }
        return Math.max(0, attacker.getAttack() - defender.getDefense());
    }

    private String buildBattleLog(Lutemon attacker, Lutemon defender, int damage) {
        return String.format("%s → %s Damage: %d Remaining HP: %d/%d",
                attacker.getName(),
                defender.getName(),
                damage,
                defender.getCurrentHp(),
                defender.getMaxHp()
        );
    }

    public static void scheduleReset(Runnable resetAction, int delayTurns) {
        pendingResets.add(() -> resetAction.run());
    }

    public static void onTurnEnd() {
        for (Runnable action : pendingResets) {
            action.run();
        }
        pendingResets.clear();
    }

    public static synchronized BattleManager getInstance() {
        if (instance == null) {
            instance = new BattleManager();
        }
        return instance;
    }

    public Lutemon getLeftLutemon() {
        return leftLutemon;
    }

    public Lutemon getRightLutemon() {
        return rightLutemon;
    }

    public void performAttack() {
        executeTurn(-1);
    }

    public boolean useSkill() {
        Skill skill = getCurrentSkill();
        if (skill != null && skill.isReady()) {
            executeTurn(0);
            return true;
        }
        return false;
    }

    public Skill getCurrentSkill() {
        Lutemon attacker = getCurrentAttacker();
        if (attacker != null && attacker.getSkillList() != null && !attacker.getSkillList().isEmpty()) {
            return attacker.getSkillList().get(0);
        }
        return null;
    }
    public Lutemon getCurrentAttacker() {
        if (leftLutemon == null || rightLutemon == null) return null;
        return isLeftTurn ? leftLutemon : rightLutemon;
    }
    public Lutemon getCurrentDefender() {
        return isLeftTurn ? rightLutemon : leftLutemon;
    }
    public void autoEnemyTurn() {
        if (!isLeftTurn && !battleOver) {
            performAttack();
        }
    }
    public boolean isBattleOver() {
        return battleOver;
    }
}
