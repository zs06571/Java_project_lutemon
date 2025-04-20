package com.example.java_project_lutemon.core.training;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.state.GameState;

import java.util.HashMap;
import java.util.Map;

public class TrainingManager {
    private boolean isTraining = false;
    private Thread trainingThread;
    private TrainingCallback currentCallback;

    public TrainingResult train(Lutemon lutemon, TrainingType type) {
        int exp = 0;
        int stat = 0;
        int hpIncrease = 0;

        switch (type) {
            case ATTACK:
                stat = (int) (lutemon.getAttack() * 0.1);
                lutemon.setAttack(lutemon.getAttack() + stat);
                exp = 15;
                break;
            case DEFENSE:
                stat = (int) (lutemon.getDefense() * 0.1);
                lutemon.setDefense(lutemon.getDefense() + stat);
                exp = 10;
                break;
            case STAMINA:
                stat = (int) (lutemon.getMaxHp() * 0.1);
                hpIncrease = stat;
                lutemon.setMaxHp(lutemon.getMaxHp() + hpIncrease);
                exp = 10;
                break;
        }

        lutemon.heal(hpIncrease);
        lutemon.gainExp(exp);

        return new TrainingResult(exp, stat, hpIncrease);
    }

    private static final Map<TrainingType, Long> TRAINING_DURATIONS = new HashMap<>() {{
        put(TrainingType.ATTACK, 40_000L);
        put(TrainingType.DEFENSE, 60_000L);
        put(TrainingType.SKILL, 90_000L);
    }};

    private static final Map<Integer, CountDownTimer> activeTimers = new HashMap<>();

    public static void startTraining(Lutemon lutemon, TrainingType type, TrainingCallback callback) {
        if (activeTimers.containsKey(lutemon.getId())) {
            Log.w("DEBUG_TIMER", "Replacing existing timer for " + lutemon.getName());
            activeTimers.get(lutemon.getId()).cancel();
            activeTimers.remove(lutemon.getId());
        }

        long totalDuration = TRAINING_DURATIONS.getOrDefault(type, 60_000L);
        long remaining = lutemon.getTrainingRemaining() > 0 ? lutemon.getTrainingRemaining() : totalDuration;

        lutemon.setTraining(true);
        lutemon.setCurrentTrainingType(type);
        lutemon.setTrainingRemaining(remaining);


        CountDownTimer timer = new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                lutemon.setTrainingRemaining(millisUntilFinished);
                if (callback != null) {
                    callback.onTick(lutemon, millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {

                switch (type) {
                    case ATTACK:
                        lutemon.setAttack(lutemon.getAttack() + 1);
                        break;
                    case DEFENSE:
                        lutemon.setDefense(lutemon.getDefense() + 1);
                        break;
                    case SKILL:
                        if (!lutemon.getSkills().isEmpty()) {
                            lutemon.getSkills().get(0).upgrade();
                        }
                        break;
                }
                lutemon.setHp(lutemon.getHp() + 3);
                lutemon.setTraining(false);
                lutemon.setTrainingRemaining(0);
                lutemon.setState(GameState.REST);
                lutemon.incrementTotalTraining();
                if (callback != null) {
                    android.util.Log.d("TRAINING_TIMER", "Finished for Lutemon ID: " + lutemon.getId());
                    callback.onFinish(lutemon);
                }
                activeTimers.remove(lutemon.getId());
            }
        };
        activeTimers.put(lutemon.getId(), timer);
        timer.start();
        android.util.Log.d("TRAINING_TIMER", "Timer started for Lutemon ID: " + lutemon.getId());
        activeTimers.put(lutemon.getId(), timer);
    }

    public static boolean startTrainingIfPossible(Context context, Lutemon lutemon, TrainingType type, TrainingCallback callback) {
        if (getActiveTrainingCount() >= 3) {
            new AlertDialog.Builder(context)
                    .setTitle("Training Full")
                    .setMessage("Maximum 3 Lutemons can train at the same time.")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }

        startTraining(lutemon, type, callback);
        return true;
    }

    public static void cancelTraining(int lutemonId) {
        if (activeTimers.containsKey(lutemonId)) {
            activeTimers.get(lutemonId).cancel();
            activeTimers.remove(lutemonId);
        }
    }
    public static long getTrainingDuration(TrainingType type) {
        switch (type) {
            case ATTACK:
                return 40000L;
            case DEFENSE:
                return 60000L;
            case SKILL:
                return 90000L;
            default:
                return 60000L;
        }
    }

    public static int getActiveTrainingCount() {
        return activeTimers.size();
    }
}