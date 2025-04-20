package com.example.java_project_lutemon.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.model.LutemonType;
import com.example.java_project_lutemon.core.model.ChartStats;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<List<Lutemon>> allLutemons = new MutableLiveData<>();
    private final MutableLiveData<ChartStats> lutemonStats = new MutableLiveData<>();

    public void setLutemons(List<Lutemon> list) {
        allLutemons.setValue(list);
    }

    public LiveData<List<Lutemon>> getLutemons() {
        return allLutemons;
    }

    public LiveData<ChartStats> getLutemonStats() {
        return lutemonStats;
    }

    public void loadStats() {
        BarData barData = generateBarData();
        PieData pieData = generatePieData();
        lutemonStats.setValue(new ChartStats(barData, pieData));
    }

    private BarData generateBarData() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (LutemonType type : LutemonType.values()) {
            int totalTrain = 0;
            int totalBattle = 0;

            if (allLutemons.getValue() != null) {
                for (Lutemon l : allLutemons.getValue()) {
                    if (l.getType() == type) {
                        totalTrain += l.getTrainingCount();
                        totalBattle += l.getWinCount();
                    }
                }
            }

            entries.add(new BarEntry(index, totalTrain));
            labels.add(type.name() + " Train");
            index++;

            entries.add(new BarEntry(index, totalBattle));
            labels.add(type.name() + " Battle");
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Train/Battle Counts by Type");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        return barData;
    }

    private PieData generatePieData() {
        List<PieEntry> entries = new ArrayList<>();
        if (allLutemons.getValue() != null) {
            for (LutemonType type : LutemonType.values()) {
                long count = allLutemons.getValue().stream()
                        .filter(l -> l.getType() == type)
                        .count();
                if (count > 0) {
                    entries.add(new PieEntry(count, type.name()));
                }
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Lutemon Types");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        return new PieData(dataSet);
    }
} 