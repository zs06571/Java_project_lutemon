package com.example.java_project_lutemon.core.training;

import com.example.java_project_lutemon.core.model.Lutemon;

public interface TrainingCallback {
    void onTick(Lutemon lutemon, long millisUntilFinished);
    void onFinish(Lutemon lutemon);
}