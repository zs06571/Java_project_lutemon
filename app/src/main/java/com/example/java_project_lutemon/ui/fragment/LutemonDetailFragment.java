package com.example.java_project_lutemon.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.model.LutemonFactory;
import com.example.java_project_lutemon.core.model.LutemonType;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.ui.viewmodel.LutemonViewModel;

import java.util.List;

public class LutemonDetailFragment extends Fragment {

    private LinearLayout container;
    private LutemonViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lutemon_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar_detail);
        toolbar.setTitle("Lutemon Detail");
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        container = view.findViewById(R.id.lutemon_detail_container);
        viewModel = new ViewModelProvider(this).get(LutemonViewModel.class);

        int lutemonId = getArguments() != null ? getArguments().getInt("lutemonId", -1) : -1;

        if (lutemonId != -1) {
            Lutemon l = LutemonRepository.getInstance().getLutemonById(lutemonId);
            if (l != null) {
                displayLutemonCard(l);
                return;
            } else {
                displayDefaultFive();
            }
        } else {
            displayDefaultFive();
        }
    }

    private void displayLutemonCard(Lutemon l) {
        View card = LayoutInflater.from(getContext())
                .inflate(R.layout.item_lutemon_detail_card, container, false);

        ((TextView) card.findViewById(R.id.txt_name)).setText(l.getName());
        ((TextView) card.findViewById(R.id.txt_type)).setText(l.getType().getDisplayName());
        ((TextView) card.findViewById(R.id.tv_skill_level)).setText("Lv." + l.getLevel());

        ((TextView) card.findViewById(R.id.txt_atk)).setText("ATK: " + l.getAttack());
        ((TextView) card.findViewById(R.id.txt_def)).setText("DEF: " + l.getDefense());
        ((TextView) card.findViewById(R.id.txt_hp)).setText("HP: " + l.getMaxHp());

        ((TextView) card.findViewById(R.id.tvBattles)).setText("Battles: " + l.getBattleCount());
        ((TextView) card.findViewById(R.id.tvTrainings)).setText("Trainings: " + l.getTrainingCount());
        ((TextView) card.findViewById(R.id.tvWins)).setText("Wins: " + l.getWinCount());

        ProgressBar progressExp = card.findViewById(R.id.progress_exp);
        TextView txtExp = card.findViewById(R.id.txt_exp);
        progressExp.setMax(l.getMaxExp());
        progressExp.setProgress(l.getExp());
        txtExp.setText("EXP: " + l.getExp() + "/" + l.getMaxExp());
        txtExp.setVisibility(View.GONE);
        progressExp.setOnClickListener(v -> {
            boolean show = txtExp.getVisibility() == View.GONE;
            txtExp.setVisibility(show ? View.VISIBLE : View.GONE);
        });

        ((ImageView) card.findViewById(R.id.img_avatar)).setImageResource(l.getImageResId());
        List<com.example.java_project_lutemon.core.skill.Skill> skillList = l.getSkills();
        TextView txtSkill = card.findViewById(R.id.txt_skill_description);

        if (skillList != null && !skillList.isEmpty()) {
            txtSkill.setText("Skill: " + skillList.get(0).getDescription());
        } else {
            txtSkill.setText("Skill: None");
        }
        container.addView(card);
    }

    private void displayDefaultFive() {
        for (LutemonType type : LutemonType.values()) {
            Lutemon sample = LutemonFactory.create(type, type.getDisplayName(), 0);
            displayLutemonCard(sample);
        }
    }

}
