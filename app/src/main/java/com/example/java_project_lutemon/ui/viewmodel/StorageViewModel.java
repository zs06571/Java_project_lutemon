package com.example.java_project_lutemon.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.java_project_lutemon.core.state.GameState;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.repository.LutemonRepository;

import java.util.ArrayList;
import java.util.List;

public class StorageViewModel extends ViewModel {

    private final LutemonRepository repository = LutemonRepository.getInstance();
    private final MutableLiveData<Boolean> refreshTrigger = new MutableLiveData<>(false);

    public void moveToStorage(int id) {
        Lutemon l = repository.getLutemonById(id);
        if (l != null) {
            l.setState(GameState.STORAGE);
            repository.updateLutemon(l);
        }
    }

    public void restoreToHome(int id) {
        Lutemon l = LutemonRepository.getInstance().getLutemonById(id);
        if (l != null) {
            l.setState(GameState.REST);
            repository.updateLutemon(l);
            refreshTrigger.setValue(!refreshTrigger.getValue());
        }
    }

    public LiveData<List<Lutemon>> getStoredLutemons() {
        return Transformations.switchMap(refreshTrigger, trigger -> {
            return Transformations.map(
                    LutemonRepository.getInstance().getAllLutemonsLiveData(),
                    list -> {
                        List<Lutemon> result = new ArrayList<>();
                        for (Lutemon l : list) {
                            if (l.getState() == GameState.STORAGE) {
                                result.add(l);
                            }
                        }
                        return result;
                    });
        });
    }

    public LiveData<List<Lutemon>> getRestingLutemons() {
        return Transformations.map(
                LutemonRepository.getInstance().getAllLutemonsLiveData(),
                list -> {
                    List<Lutemon> result = new ArrayList<>();
                    for (Lutemon l : list) {
                        if (l.getState() == GameState.REST) {
                            result.add(l);
                        }
                    }
                    return result;
                });
    }

    public void removeLutemon(int id) {
        LutemonRepository.getInstance().deleteLutemon(id);
    }
}
