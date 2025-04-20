package com.example.java_project_lutemon.core.model;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.skill.Skill;
import com.example.java_project_lutemon.core.state.GameState;
import com.example.java_project_lutemon.core.training.TrainingType;

import java.util.ArrayList;
import java.util.List;

public abstract class Lutemon {
    private final String name;
    private final int id;
    private final int color;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int experience = 0;
    private int level = 1;
    private int winCount = 0;
    private int hp;
    private int exp;
    private int maxExp = 50;
    private int baseAttack;
    private int baseDefense;
    private String skill;
    private boolean isTraining = false;
    private long trainingRemaining = 0;
    private TrainingType currentTrainingType;
    private List<Skill> skills = new ArrayList<>();
    private GameState state = GameState.REST;
    LutemonType type;
    private int trainingCount = 0;
    private int battleCount = 0;
    private int totalWins = 0;
    private int totalBattles = 0;
    private int totalTraining = 0;


    public Lutemon(String name, int color, int maxHp, int attack, int defense, int id) {
        this.name = name;
        this.color = color;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.id = id;
        this.baseAttack = attack;
        this.baseDefense = defense;
    }

    public void resetAfterBattle() {
        this.currentHp = this.maxHp;
        this.hp = this.maxHp;

        this.state = GameState.REST;

        this.isTraining = false;
        this.trainingRemaining = 0;
        this.currentTrainingType = null;
    }


    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public String getName() { return name; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public List<Skill> getSkillList() { return skills; }

    public int getColor() { return color; }

    public GameState getState() { return state; }
    public int getId() { return id; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public int getWinCount() { return winCount; }
    public List<Skill> getSkills() { return skills; }
    public int getExp() { return exp; }
    public int getMaxExp() { return maxExp; }
    public int getExpToLevelUp() {
        return 50 + level; //
    }
    public int getHp() {
        return hp;
    }
    // Setters
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }

    public void setState(GameState state) { this.state = state; }
    public void setType(LutemonType type) {
        this.type = type;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        this.currentHp = Math.min(this.currentHp, maxHp);
    }
    public void gainExp(int exp) {
        this.experience += exp;
        if (experience >= level * 20) {
            experience = 0;
            level++;
            maxHp += 5;
            attack += 2;
            defense += 1;
            currentHp = maxHp;
        }
    }
    public void resetExp() {
        this.experience = 0;
    }

    public abstract LutemonType getType();
    public void incrementWinCount() { winCount++; }
    public int getImageResId() {
        if (type == null) {
            return R.drawable.ic_lutemon_placeholder;
        }
        return type.getImageResId();
    }
    public int getColorResId() {
        return type.getColorResId();
    }
    public void setHp(int hp) {
        if (hp < 0) {
            this.hp = 0;
        } else if (hp > maxHp) {
            this.hp = maxHp;
        } else {
            this.hp = hp;
        }
    }
    public int getBaseAttack() {
        return baseAttack;
    }
    public int getBaseDefense() {
        return baseDefense;
    }

    public boolean isTraining() { return isTraining; }
    public void setTraining(boolean training) { this.isTraining = training; }

    public long getTrainingRemaining() { return trainingRemaining; }
    public void setTrainingRemaining(long ms) { this.trainingRemaining = ms; }

    public TrainingType getCurrentTrainingType() { return currentTrainingType; }
    public void setCurrentTrainingType(TrainingType type) { this.currentTrainingType = type; }

    public int getTrainingCount() {
        return trainingCount;
    }
    public int getBattleCount() { return battleCount; }
    public void incrementBattleCount() { battleCount++; }

    public int getTotalBattles() { return totalBattles; }
    public void incrementTotalBattles() { totalBattles++; }
    public int getTotalWins() { return totalWins; }
    public void incrementTotalWins() { totalWins++; }

    public int getTotalTraining() { return totalTraining; }
    public void incrementTotalTraining() { totalTraining++; }

}