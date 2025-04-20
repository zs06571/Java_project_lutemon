package com.example.java_project_lutemon.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.core.state.GameState;

import java.util.ArrayList;
import java.util.List;

public class LutemonViewModel extends ViewModel {
    private final LutemonRepository lutemonRepository;
    private final LiveData<List<Lutemon>> lutemonsLiveData;
    private final MutableLiveData<Lutemon> currentLutemon = new MutableLiveData<>();
    private final MutableLiveData<List<Lutemon>> lutemons = new MutableLiveData<>(new ArrayList<>());

    public LutemonViewModel() {
        lutemonRepository = LutemonRepository.getInstance();
        lutemonsLiveData = lutemonRepository.getAllLutemonsLiveData();
        lutemonRepository.getAllLutemonsLiveData().getValue();
    }

    public LiveData<List<Lutemon>> getLutemons() {
        return lutemonsLiveData;
    }

    public LiveData<Lutemon> getCurrentLutemon() {
        return currentLutemon;
    }

    public void setCurrentLutemon(Lutemon lutemon) {
        currentLutemon.setValue(lutemon);
    }

    public void deleteLutemon(int selectedId) {
        lutemonRepository.deleteLutemon(selectedId);
    }
    public void refreshData() {
        List<Lutemon> current = new ArrayList<>(lutemonRepository.getAllLutemons());

        MutableLiveData<List<Lutemon>> liveData =
                (MutableLiveData<List<Lutemon>>) lutemonRepository.getAllLutemonsLiveData();
        liveData.postValue(current);
    }

    public void moveToStorage(int id) {
        List<Lutemon> lutemons = lutemonRepository.getAllLutemons();

        Lutemon target = null;
        for (Lutemon lutemon : lutemons) {
            if (lutemon.getId() == id) {
                target = lutemon;
                break;
            }
        }

        if (target != null) {
            target.setState(GameState.STORAGE);

            lutemonRepository.updateLutemon(target);

            refreshData();
        }
    }

    public LiveData<List<Lutemon>> getRestingLutemons() {
        MutableLiveData<List<Lutemon>> restLiveData = new MutableLiveData<>();
        List<Lutemon> all = LutemonRepository.getInstance().getAllLutemons();
        List<Lutemon> result = new ArrayList<>();
        for (Lutemon l : all) {
            if (l.getState() == GameState.REST) {
                result.add(l);
            }
        }
        restLiveData.setValue(result);
        return restLiveData;
    }
}

