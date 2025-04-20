package com.example.java_project_lutemon.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.core.state.GameState;

import java.util.List;

public class LutemonListViewModel extends ViewModel {
    private final LutemonRepository repository;
    private final LiveData<List<Lutemon>> allLutemons;

    public LutemonListViewModel() {
        repository = LutemonRepository.getInstance();
        allLutemons = repository.getAllLutemonsLiveData();
    }

    public List<Lutemon> getAllLutemons() {
        return repository.getAllLutemons();
    }

    public List<Lutemon> getAvailableLutemons() {
        return repository.getAvailableLutemons();
    }

    public void deleteLutemon(int id) {
        repository.deleteLutemon(id);
    }

    public void changeLutemonState(int id, GameState newState) {
        repository.updateLutemonState(id, newState);
    }
}

