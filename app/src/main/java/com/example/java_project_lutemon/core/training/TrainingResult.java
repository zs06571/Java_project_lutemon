package com.example.java_project_lutemon.core.training;

public class TrainingResult {
    private final int gainedExp;
    private final int statIncrease;
    private final int hpIncrease;

    public TrainingResult(int gainedExp, int statIncrease, int hpIncrease) {
        this.gainedExp = gainedExp;
        this.statIncrease = statIncrease;
        this.hpIncrease = hpIncrease;
    }

    // Getters
    public int getGainedExp() { return gainedExp; }
    public int getStatIncrease() { return statIncrease; }
    public int getHpIncrease() { return hpIncrease; }
}
