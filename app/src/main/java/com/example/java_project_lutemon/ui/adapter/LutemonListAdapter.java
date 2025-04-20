package com.example.java_project_lutemon.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import java.util.List;

public class LutemonListAdapter extends RecyclerView.Adapter<LutemonListAdapter.ViewHolder> {
    private Context context;
    private List<Lutemon> lutemons;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(Lutemon lutemon);
    }

    public int getSelectedLutemonId() {
        if (selectedPosition >= 0 && selectedPosition < lutemons.size()) {
            return lutemons.get(selectedPosition).getId();
        }
        return -1;
    }


    public LutemonListAdapter(Context context, List<Lutemon> lutemons) {
        this.context = context;
        this.lutemons = lutemons;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLutemons(List<Lutemon> newLutemons) {
        this.lutemons = newLutemons;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lutemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lutemon lutemon = lutemons.get(position);
        holder.bind(lutemon);
    }

    @Override
    public int getItemCount() {
        return lutemons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textName;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.tv_lutemons_name);
            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });
        }

        public void bind(Lutemon lutemon) {
            textName.setText(lutemon.getName());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(lutemon);
                }
            });
        }
    }

}