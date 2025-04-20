package com.example.java_project_lutemon.core.skill;

import com.example.java_project_lutemon.core.model.Lutemon;

public abstract class Skill {
    public static final int MAX_LEVEL = 7;

    protected String name;
    protected int maxUses;
    protected int cooldown;
    protected int duration;
    protected int currentDuration;
    protected int level;
    protected int usesLeft;
    protected int currentCooldown;

    public Skill(String name, int maxUses, int cooldown, int duration, int level) {
        this.name = name;
        this.maxUses = maxUses;
        this.cooldown = cooldown;
        this.duration = duration;
        this.level = level;
        this.usesLeft = maxUses;
        this.currentCooldown = 0;
    }

    public boolean isReady() {
        return usesLeft > 0 && currentCooldown <= 0;
    }

    public void nextTurn() {
        if (currentCooldown > 0) currentCooldown--;
        if (currentDuration > 0) currentDuration--;
    }

    public void use() {
        if (isReady()) {
            currentCooldown = cooldown;
            usesLeft--;
            currentDuration = duration;
        }
    }

    public float getCooldownPercentage() {
        return cooldown == 0 ? 0f : (float) currentCooldown / cooldown;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentCooldown() {
        return cooldown;
    }

    public int getDuration() {
        return duration;
    }

    public abstract void apply(Lutemon caster, Lutemon target);

    public abstract String getDescription();

    public boolean upgrade() {
        if (level < MAX_LEVEL) {
            level++;
            return true;
        } else {
            return false;
        }
    }
    public boolean shouldSkipDamageCalculation() {
        return false;
    }
    public int getUsesLeft() {
        return usesLeft;
    }

    public boolean isActive() {
        return currentDuration > 0;
    }
}
