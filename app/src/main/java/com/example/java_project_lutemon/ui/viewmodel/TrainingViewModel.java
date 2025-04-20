
package com.example.java_project_lutemon.ui.viewmodel;

import static com.example.java_project_lutemon.core.training.TrainingManager.getTrainingDuration;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.state.GameState;
import com.example.java_project_lutemon.core.training.TrainingManager;
import com.example.java_project_lutemon.core.training.TrainingResult;
import com.example.java_project_lutemon.core.training.TrainingType;
import com.example.java_project_lutemon.core.training.TrainingCallback;
import com.example.java_project_lutemon.core.repository.LutemonRepository;

import java.util.List;
import java.util.ArrayList;

public class TrainingViewModel extends ViewModel {

    private final TrainingManager trainingManager;
    private final LutemonRepository repository = LutemonRepository.getInstance();
    private final MutableLiveData<Integer> trainingProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Lutemon> currentLutemon = new MutableLiveData<>();
    private final MutableLiveData<TrainingResult> trainingResult = new MutableLiveData<>();
    private final MutableLiveData<List<Lutemon>> liveTrainingLutemons = new MutableLiveData<>();
    private final MutableLiveData<List<Lutemon>> liveRestLutemons = new MutableLiveData<>();

    private TrainingCallback callbackContext;

    public TrainingViewModel() {
        trainingManager = new TrainingManager();
    }

    public void setCallback(TrainingCallback callback) {
        this.callbackContext = callback;
        Log.d("DEBUG_TRAINING", "Callback registered: " + (callback != null));
    }

    public void init(int lutemonId) {
        Lutemon lutemon = repository.getLutemonById(lutemonId);
        currentLutemon.setValue(lutemon);
        trainingProgress.setValue(0);
    }

    public void startTraining(Lutemon lutemon, TrainingType type) {
        Log.d("DEBUG_TIMER", "ViewModel.startTraining CALLED FOR: " + lutemon.getName());
        if (currentLutemon.getValue() == null) return;

        lutemon.setTraining(true);
        lutemon.setTrainingRemaining(TrainingManager.getTrainingDuration(type));
        lutemon.setCurrentTrainingType(type);
        lutemon.setState(GameState.TRAINING);

        if (callbackContext != null) {
            TrainingManager.startTraining(lutemon, type, callbackContext);
        }

        repository.updateLutemon(lutemon);
        updateLiveData();
    }

    public void cancelTraining(int lutemonId) {
        trainingManager.cancelTraining(lutemonId);

        Lutemon l = repository.getLutemonById(lutemonId);
        if (l != null) {
            l.setTraining(false);
            l.setTrainingRemaining(0);
            l.setState(GameState.REST);
            repository.updateLutemon(l);
        }

        updateLiveData();
    }

    public List<Lutemon> getTrainingLutemons() {
        List<Lutemon> all = repository.getAllLutemons();
        List<Lutemon> result = new ArrayList<>();
        for (Lutemon l : all) {
            if (l.isTraining()) result.add(l);
        }
        return result;
    }

    public List<Lutemon> getRestLutemons() {
        List<Lutemon> all = repository.getAllLutemons();
        List<Lutemon> result = new ArrayList<>();
        for (Lutemon l : all) {
            if (!l.isTraining()) result.add(l);
        }
        return result;
    }

    public LiveData<TrainingResult> getTrainingResult() {
        return trainingResult;
    }

    public LiveData<List<Lutemon>> getLiveTrainingLutemons() {
        return liveTrainingLutemons;
    }

    public LiveData<List<Lutemon>> getLiveRestLutemons() {
        return liveRestLutemons;
    }

    public void updateLiveData() {
        liveTrainingLutemons.postValue(getTrainingLutemons());
        liveRestLutemons.postValue(getRestLutemons());
    }

    private void updateLutemon(Lutemon l) {
        repository.updateLutemon(l);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void resumeAllTimersIfNeeded(TrainingCallback callback) {
        for (Lutemon l : getTrainingLutemons()) {
            if (l.getTrainingRemaining() > 0) {
                TrainingManager.startTraining(l, l.getCurrentTrainingType(), callback);
                repository.updateLutemon(l);
            }
        }
    }
}
