package com.example.java_project_lutemon.core.model;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;

public class ChartStats {
    private final BarData barData;
    private final PieData pieData;

    public ChartStats(BarData barData, PieData pieData) {
        this.barData = barData;
        this.pieData = pieData;
    }

    public BarData getBarData() {
        return barData;
    }

    public PieData getPieData() {
        return pieData;
    }
}
