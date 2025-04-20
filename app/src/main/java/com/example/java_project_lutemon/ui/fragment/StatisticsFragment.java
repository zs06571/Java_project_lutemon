package com.example.java_project_lutemon.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.model.LutemonType;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.ui.adapter.ChartStatsAdapter;
import com.example.java_project_lutemon.ui.viewmodel.SharedViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerStats;
    private ChartStatsAdapter chartAdapter;
    private TextView textCreated, textCurrent, textBattles, textTraining;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.statics_toolbar);

        if (requireActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Statistics");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        textCurrent = view.findViewById(R.id.text_current_lutemon);
        textBattles = view.findViewById(R.id.text_total_battles);
        textTraining = view.findViewById(R.id.text_total_training);

        recyclerStats = view.findViewById(R.id.recycler_statistics);
        recyclerStats.setLayoutManager(new LinearLayoutManager(getContext()));
        chartAdapter = new ChartStatsAdapter();
        recyclerStats.setAdapter(chartAdapter);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        List<Lutemon> lutemons = LutemonRepository.getInstance().getAllLutemons();
        sharedViewModel.setLutemons(lutemons);
        sharedViewModel.loadStats();

        int total = lutemons.size();
        int totalTraining = lutemons.stream().mapToInt(Lutemon::getTotalTraining).sum();
        int totalBattles = lutemons.stream().mapToInt(Lutemon::getTotalBattles).sum();

        textCurrent.setText("Current Count: " + total);
        textBattles.setText("Total Battles: " + totalBattles);
        textTraining.setText("Total Training: " + totalTraining);

        BarChart chart = view.findViewById(R.id.chart_stats_by_type);

        Map<String, int[]> statsByType = new LinkedHashMap<>();
        for (LutemonType type : LutemonType.values()) {
            statsByType.put(type.name(), new int[]{0, 0, 0});
        }

        for (Lutemon l : lutemons) {
            String type = l.getType().name();
            int[] stat = statsByType.get(type);
            stat[0] += l.getTotalTraining();
            stat[1] += l.getTotalBattles();
            stat[2] += l.getTotalWins();
        }

        List<BarEntry> trainingEntries = new ArrayList<>();
        List<BarEntry> battleEntries = new ArrayList<>();
        List<BarEntry> winEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, int[]> entry : statsByType.entrySet()) {
            labels.add(entry.getKey());
            int[] values = entry.getValue();
            trainingEntries.add(new BarEntry(index, values[0]));
            battleEntries.add(new BarEntry(index, values[1]));
            winEntries.add(new BarEntry(index, values[2]));
            index++;
        }

        BarDataSet set1 = new BarDataSet(trainingEntries, "Trainings");
        set1.setColor(Color.BLUE);
        BarDataSet set2 = new BarDataSet(battleEntries, "Battles");
        set2.setColor(Color.RED);
        BarDataSet set3 = new BarDataSet(winEntries, "Wins");
        set3.setColor(Color.GREEN);

        BarData barData = new BarData(set1, set2, set3);
        float groupSpace = 0.2f;
        float barSpace = 0.05f;
        float barWidth = 0.20f;

        barData.setBarWidth(barWidth);
        chart.setData(barData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        float groupWidth = barData.getGroupWidth(groupSpace, barSpace);
        float startX = 0f;

        chart.getXAxis().setAxisMinimum(startX);
        chart.getXAxis().setAxisMaximum(startX + groupWidth * labels.size());
        chart.groupBars(startX, groupSpace, barSpace);

        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.setDragEnabled(true);
        chart.setVisibleXRangeMaximum(5);
        chart.groupBars(0f, groupSpace, barSpace);
        chart.invalidate();
    }
}
