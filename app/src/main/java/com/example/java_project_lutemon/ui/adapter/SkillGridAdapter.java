package com.example.java_project_lutemon.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.skill.Skill;
import java.util.List;

public class SkillGridAdapter extends RecyclerView.Adapter<SkillGridAdapter.ViewHolder> {

    public interface OnSkillSelectedListener {
        void onSkillSelected(int position);
    }

    private final List<Skill> skills;
    private final OnSkillSelectedListener listener;

    public SkillGridAdapter(List<Skill> skills, OnSkillSelectedListener listener) {
        this.skills = skills;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button buttonSkill;

        public ViewHolder(View itemView) {
            super(itemView);
            buttonSkill = itemView.findViewById(R.id.button_skill);

            if (buttonSkill == null) {
                throw new IllegalStateException("Button not found in layout");
            }
        }

        public void bind(Skill skill, OnSkillSelectedListener listener, int position) {
            buttonSkill.setText(skill.getName());
            buttonSkill.setEnabled(skill.isReady());
            buttonSkill.setOnClickListener(v -> {
                if (skill.isReady()) {
                    skill.use();
                    listener.onSkillSelected(position);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_skill_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(skills.get(position), listener, position);
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }
}