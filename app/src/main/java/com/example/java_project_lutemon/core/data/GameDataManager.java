package com.example.java_project_lutemon.core.data;

import com.example.java_project_lutemon.core.model.Lutemon;
import java.util.List;

public class GameDataManager {
    private static GameDataManager instance;
    private List<Lutemon> lutemonList;

    private GameDataManager() {}

    public static synchronized GameDataManager getInstance() {
        if (instance == null) {
            instance = new GameDataManager();
        }
        return instance;
    }

    public void saveLutemons(List<Lutemon> lutemons) {
        this.lutemonList = lutemons;
    }

    public List<Lutemon> loadLutemons() {
        return lutemonList;
    }
}