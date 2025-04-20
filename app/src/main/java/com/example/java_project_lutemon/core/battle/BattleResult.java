package com.example.java_project_lutemon.core.battle;

import com.example.java_project_lutemon.core.model.Lutemon;

public class BattleResult {
    private final boolean isWin;
    private final int damageDealt;
    private final String logMessage;
    private final Lutemon winner;
    private final Lutemon loser;

    public BattleResult(boolean isWin, int damageDealt, String logMessage, Lutemon winner, Lutemon loser) {
        this.isWin = isWin;
        this.damageDealt = damageDealt;
        this.logMessage = logMessage;
        this.winner = winner;
        this.loser = loser;
    }

    public Lutemon getLoser() {
        return loser;
    }

    // Getters
    public boolean isWin() { return isWin; }
    public int getDamageDealt() { return damageDealt; }
    public String getLogMessage() { return logMessage; }
    public com.example.java_project_lutemon.core.model.Lutemon getWinner() { return winner; }
}