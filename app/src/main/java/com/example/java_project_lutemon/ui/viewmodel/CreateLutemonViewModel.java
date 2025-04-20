package com.example.java_project_lutemon.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.model.LutemonFactory;
import com.example.java_project_lutemon.core.model.LutemonType;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.core.state.GameState;

public class CreateLutemonViewModel extends ViewModel {
    private final LutemonRepository repository = LutemonRepository.getInstance();
    private final MutableLiveData<Boolean> creationResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<LutemonType> selectedType = new MutableLiveData<>();

    public LutemonType[] getLutemonTypes() {
        return LutemonType.values();
    }

    public void createLutemon(String name, LutemonType type) {

        if (name == null || name.trim().isEmpty()) {
            errorMessage.setValue("Name cannot be empty");
            creationResult.setValue(false);
            return;
        }
        if (type == null) {
            errorMessage.setValue("Please select a Lutemon type");
            creationResult.setValue(false);
            return;
        }
        boolean duplicate = repository.getAllLutemons().stream()
                .anyMatch(lutemon -> lutemon.getName().equalsIgnoreCase(name));
        if (duplicate) {
            errorMessage.setValue("Lutemon name already exists");
            creationResult.setValue(false);
            return;
        }
        int newId = repository.getAllLutemons().stream()
                .mapToInt(Lutemon::getId)
                .max().orElse(0) + 1;
        try {
            Lutemon lutemon = LutemonFactory.create(type, name, newId);
            lutemon.setState(GameState.REST);
            repository.addLutemon(lutemon);
            creationResult.setValue(true);
        } catch (Exception e) {
            Log.e("CreateLutemon", "Error creating Lutemon", e);
            e.printStackTrace();
            errorMessage.setValue("Creation failed: " + e.getMessage());
            creationResult.setValue(false);
        }
    }

    public LiveData<LutemonType> getSelectedType() {
        return selectedType;
    }

    public void onTypeSelected(LutemonType type) {
        selectedType.setValue(type);
    }

    public LiveData<Boolean> getCreationResult() {
        return creationResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
