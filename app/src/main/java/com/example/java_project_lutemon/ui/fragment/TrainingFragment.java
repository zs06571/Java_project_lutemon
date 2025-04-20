package com.example.java_project_lutemon.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.fragment.NavHostFragment;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.state.GameState;
import com.example.java_project_lutemon.core.training.TrainingCallback;
import com.example.java_project_lutemon.core.training.TrainingManager;
import com.example.java_project_lutemon.core.training.TrainingType;
import com.example.java_project_lutemon.ui.adapter.LutemonCardAdapter;
import com.example.java_project_lutemon.ui.viewmodel.TrainingViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingFragment extends Fragment implements TrainingCallback {

    private RecyclerView recyclerTraining, recyclerRest;
    private LutemonCardAdapter trainingAdapter, restAdapter;
    private TrainingViewModel viewModel;
    private TextView emptyTrainingHint, emptyRestHint;
    private Button btnTrainAttack, btnTrainDefense, btnTrainSkill;
    private Lutemon selectedLutemon;
    private List<Lutemon> trainingLutemons = new ArrayList<>();

    private final Map<Integer, ProgressBar> trainingProgressBars = new HashMap<>();
    private final Map<Integer, TextView> trainingCountdownLabels = new HashMap<>();
    private final Map<Integer, Button> cancelButtons = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TrainingViewModel.class);
        viewModel.setCallback(this);

        recyclerTraining = view.findViewById(R.id.recycler_training);
        recyclerRest = view.findViewById(R.id.recycler_rest);
        emptyTrainingHint = view.findViewById(R.id.empty_training_hint);
        emptyRestHint = view.findViewById(R.id.empty_rest_hint);

        recyclerTraining.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRest.setLayoutManager(new LinearLayoutManager(getContext()));

        trainingAdapter = new LutemonCardAdapter(this, requireContext(), new ArrayList<>());
        restAdapter = new LutemonCardAdapter(this, requireContext(), new ArrayList<>());

        recyclerTraining.setAdapter(trainingAdapter);
        recyclerRest.setAdapter(restAdapter);

        restAdapter.setOnItemClickListener(lutemon -> {
            selectedLutemon = lutemon;
            restAdapter.setSelectedId(lutemon.getId());
            Toast.makeText(getContext(), "Selected: " + lutemon.getName(), Toast.LENGTH_SHORT).show();
        });

        btnTrainAttack = view.findViewById(R.id.btn_train_attack);
        btnTrainDefense = view.findViewById(R.id.btn_train_defense);
        btnTrainSkill = view.findViewById(R.id.btn_train_skill);

        btnTrainAttack.setOnClickListener(v -> startTraining(TrainingType.ATTACK));
        btnTrainDefense.setOnClickListener(v -> startTraining(TrainingType.DEFENSE));
        btnTrainSkill.setOnClickListener(v -> startTraining(TrainingType.SKILL));

        viewModel.getLiveTrainingLutemons().observe(getViewLifecycleOwner(), list -> {
            trainingAdapter.setLutemons(list);
            emptyTrainingHint.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getLiveRestLutemons().observe(getViewLifecycleOwner(), list -> {
            restAdapter.setLutemons(list);
            emptyRestHint.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void startTraining(TrainingType type) {
        if (selectedLutemon == null) {
            Toast.makeText(getContext(), "Please select a Lutemon first", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.init(selectedLutemon.getId());
        viewModel.startTraining(selectedLutemon, type);
        selectedLutemon = null;
        restAdapter.setSelectedId(-1);
        viewModel.updateLiveData();
        refreshAdapters();
    }

    public void registerTrainingUI(int lutemonId, ProgressBar pb, TextView label, Button btn) {
        trainingProgressBars.put(lutemonId, pb);
        trainingCountdownLabels.put(lutemonId, label);
        cancelButtons.put(lutemonId, btn);
    }

    public void onCancelTraining(int lutemonId) {
        viewModel.cancelTraining(lutemonId);
        Toast.makeText(getContext(), "Training cancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setCallback(this);
        viewModel.updateLiveData();
        refreshAdapters();
        viewModel.resumeAllTimersIfNeeded(this);
    }

    private void refreshAdapters() {
        trainingProgressBars.clear();
        trainingCountdownLabels.clear();
        cancelButtons.clear();
        updateTrainingList();

        var training = viewModel.getTrainingLutemons();
        var rest = viewModel.getRestLutemons();

        trainingAdapter.setLutemons(viewModel.getTrainingLutemons());
        restAdapter.setLutemons(viewModel.getRestLutemons());

        emptyTrainingHint.setVisibility(training.isEmpty() ? View.VISIBLE : View.GONE);
        emptyRestHint.setVisibility(rest.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTick(Lutemon l, long millisUntilFinished) {
        ProgressBar pb = trainingProgressBars.get(l.getId());
        TextView label = trainingCountdownLabels.get(l.getId());

        Log.d("UI_TRAIN", "onTick: " + l.getName() + ", Remaining: " + millisUntilFinished +
                ", pb=" + (pb != null) + ", label=" + (label != null));

        if (pb != null) {
            long total = TrainingManager.getTrainingDuration(l.getCurrentTrainingType());
            pb.setMax((int) total);
            pb.setProgress((int) (total - millisUntilFinished));
        }
        if (label != null) {
            label.setText((millisUntilFinished / 1000) + "s");
        }
    }

    @Override
    public void onFinish(Lutemon l) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), l.getName() + " training finished!", Toast.LENGTH_SHORT).show();
        }
        viewModel.updateLiveData();
        refreshAdapters();
    }

    private void updateTrainingList() {
        List<Lutemon> list = viewModel.getTrainingLutemons();
        trainingAdapter.setLutemons(list);
    }
}
