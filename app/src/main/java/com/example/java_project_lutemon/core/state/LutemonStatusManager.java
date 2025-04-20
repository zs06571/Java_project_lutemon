package com.example.java_project_lutemon.core.state;

import com.example.java_project_lutemon.core.model.Lutemon;

public class LutemonStatusManager {
    public boolean canChangeState(Lutemon lutemon, GameState newState) {
        if (lutemon.getState() == GameState.DEAD) return false;

        switch (newState) {
            case BATTLE:
                return lutemon.getState() != GameState.TRAINING;
            case TRAINING:
                return lutemon.getState() != GameState.BATTLE;
            default:
                return true;
        }
    }

    public void setState(Lutemon lutemon, GameState state) {
        if (canChangeState(lutemon, state)) {
            lutemon.setState(state);
        }
    }
}