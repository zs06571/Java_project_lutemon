package com.example.java_project_lutemon.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.ChartStats;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;

public class ChartStatsAdapter extends RecyclerView.Adapter<ChartStatsAdapter.ChartViewHolder> {

    private ChartStats chartStats;

    public void setChartStats(ChartStats chartStats) {
        this.chartStats = chartStats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chart, parent, false);
        return new ChartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartViewHolder holder, int position) {
        if (chartStats == null || chartStats.getBarData() == null) return;

        BarChart chart = holder.barChart;
        chart.setData(chartStats.getBarData());

        Description description = new Description();
        description.setText("Training & Battle Counts by Type");
        chart.setDescription(description);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);

        chart.invalidate(); // Refresh chart
    }

    @Override
    public int getItemCount() {
        return chartStats == null ? 0 : 1;
    }

    static class ChartViewHolder extends RecyclerView.ViewHolder {
        BarChart barChart;

        public ChartViewHolder(@NonNull View itemView) {
            super(itemView);
            barChart = itemView.findViewById(R.id.bar_chart);
        }
    }
}