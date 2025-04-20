package com.example.java_project_lutemon.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.core.training.TrainingManager;
import com.example.java_project_lutemon.ui.fragment.TrainingFragment;

import java.util.ArrayList;
import java.util.List;

public class LutemonCardAdapter extends RecyclerView.Adapter<LutemonCardAdapter.ViewHolder> {

    private final Context context;
    private final Fragment fragment;
    private final List<Lutemon> lutemons;
    private final List<Lutemon> allLutemons = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private int selectedId = -1;
    private int selectedPosition = -1;
    private String currentKeyword = "";
    private String currentTypeFilter = "ALL";
    private final List<Lutemon> selectedLutemons = new ArrayList<>();
    private final int layoutResId;

    public LutemonCardAdapter(Fragment fragment, Context context, List<Lutemon> lutemons, int layoutResId) {
        this.context = context;
        this.fragment = fragment;
        this.lutemons = lutemons;
        this.allLutemons.addAll(lutemons);
        this.layoutResId = layoutResId;
    }

    public LutemonCardAdapter(Fragment fragment, Context context, List<Lutemon> lutemons) {
        this(fragment, context, lutemons, R.layout.item_lutemon_card);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public interface OnItemClickListener {
        void onItemClick(Lutemon lutemon);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Lutemon lutemon, View anchor);
    }

    public void setSelectedLutemons(List<Lutemon> selected) {
        selectedLutemons.clear();
        selectedLutemons.addAll(selected);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setSelectedId(int id) {
        this.selectedId = id;
        notifyDataSetChanged();
    }

    public void setLutemons(List<Lutemon> newList) {
        this.allLutemons.clear();
        this.allLutemons.addAll(newList);

        this.lutemons.clear();
        this.lutemons.addAll(newList);

        filter(currentKeyword, currentTypeFilter);
    }

    public void filter(String keyword, String typeFilter) {
        currentKeyword = keyword != null ? keyword.toLowerCase().trim() : "";
        currentTypeFilter = typeFilter != null ? typeFilter.trim().toUpperCase() : "ALL";

        lutemons.clear();

        for (Lutemon l : allLutemons) {
            boolean matchKeyword = currentKeyword.isEmpty()
                    || l.getName().toLowerCase().contains(currentKeyword)
                    || l.getType().getDisplayName().toLowerCase().contains(currentKeyword);

            boolean matchType = currentTypeFilter.equals("ALL")
                    || l.getType().name().equalsIgnoreCase(currentTypeFilter);

            if (matchKeyword && matchType) {
                lutemons.add(l);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lutemon lutemon = lutemons.get(position);
        holder.bind(lutemon);

        if (fragment instanceof TrainingFragment) {
            if (lutemon.getId() == selectedId) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.lutemon_card_selected));
            } else {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.lutemon_card_normal));
            }
        } else {
            if (selectedLutemons.contains(lutemon)) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.lutemon_card_selected));
            } else {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.lutemon_card_normal));
            }
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                selectedPosition = pos;
                notifyDataSetChanged();
                if (clickListener != null) {
                    clickListener.onItemClick(lutemons.get(pos));
                }
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(lutemon, v);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return lutemons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgAvatar, imgStatus;
        private final TextView txtName, txtType, tvSkillLevel, txtExp, txtAtk, txtDef, txtHp, timer;
        private final ProgressBar progressExp, trainingProgress;
        private final Button cancelButton;
        private final CardView cardView;
        private final LinearLayout trainingProgressContainer;
        private final TextView tvBattles, tvWins, tvTrainings;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            imgStatus = itemView.findViewById(R.id.img_status);
            txtName = itemView.findViewById(R.id.txt_name);
            txtType = itemView.findViewById(R.id.txt_type);
            tvSkillLevel = itemView.findViewById(R.id.tv_skill_level);
            txtExp = itemView.findViewById(R.id.txt_exp);
            txtAtk = itemView.findViewById(R.id.txt_atk);
            txtDef = itemView.findViewById(R.id.txt_def);
            txtHp = itemView.findViewById(R.id.txt_hp);
            progressExp = itemView.findViewById(R.id.progress_exp);
            trainingProgress = itemView.findViewById(R.id.progress_training);
            timer = itemView.findViewById(R.id.label_timer);
            cancelButton = itemView.findViewById(R.id.btn_cancel_training);
            cardView = itemView.findViewById(R.id.card_lutemon) != null
                    ? itemView.findViewById(R.id.card_lutemon)
                    : itemView.findViewById(R.id.card_lutemon_detail);
            trainingProgressContainer = itemView.findViewById(R.id.training_progress_container);
            tvBattles = itemView.findViewById(R.id.tvBattles);
            tvWins = itemView.findViewById(R.id.tvWins);
            tvTrainings = itemView.findViewById(R.id.tvTrainings);
        }

        public void bind(Lutemon l) {
            Lutemon latest = LutemonRepository.getInstance().getLutemonById(l.getId());
            final Lutemon lutemon = (latest != null) ? latest : l;
            Log.d("DEBUG_BIND_CALL", "bind() called for: " + lutemon.getName());
            Log.d("CARD_BIND", l.getName() + " - Training: " + l.getTotalTraining() +
                    ", Battles: " + l.getTotalBattles() + ", Wins: " + l.getTotalWins());

            if (txtName != null) txtName.setText(lutemon.getName());
            if (txtType != null) txtType.setText(lutemon.getType().getDisplayName());
            if (tvSkillLevel != null) tvSkillLevel.setText("Lv." + lutemon.getLevel());
            if (txtAtk != null) txtAtk.setText("ATK: " + lutemon.getAttack());
            if (txtDef != null) txtDef.setText("DEF: " + lutemon.getDefense());
            if (txtHp != null) txtHp.setText("HP: " + lutemon.getMaxHp());

            if (tvBattles != null) tvBattles.setText("Battles: " + lutemon.getTotalBattles());
            if (tvWins != null) tvWins.setText("Wins: " + lutemon.getTotalWins());
            if (tvTrainings != null) tvTrainings.setText("Trainings: " + lutemon.getTotalTraining());

            if (progressExp != null) {
                progressExp.setMax(lutemon.getMaxExp());
                progressExp.setProgress(lutemon.getExperience());
            }

            if (txtExp != null) {
                txtExp.setText("EXP: " + lutemon.getExperience() + "/" + lutemon.getMaxExp());
                txtExp.setVisibility(View.GONE);
                progressExp.setOnClickListener(v -> {
                    boolean show = txtExp.getVisibility() == View.GONE;
                    txtExp.setVisibility(show ? View.VISIBLE : View.GONE);
                });
            }

            if (imgAvatar != null) imgAvatar.setImageResource(lutemon.getImageResId());
            if (imgStatus != null) {
                if (lutemon.isTraining()) {
                    imgStatus.setImageResource(R.drawable.ic_state_training);
                } else if (fragment.getClass().getSimpleName().equals("TrainingFragment")) {
                    if (lutemon.getState().name().equals("REST")) {
                        imgStatus.setImageResource(R.drawable.ic_state_rest);
                    } else if (lutemon.getState().name().equals("STORAGE")) {
                        imgStatus.setImageResource(R.drawable.ic_storage);
                    } else {
                        imgStatus.setImageResource(android.R.color.transparent);
                    }
                } else if (fragment.getClass().getSimpleName().equals("HomeFragment")) {
                    imgStatus.setImageResource(R.drawable.ic_state_rest);
                } else if (fragment.getClass().getSimpleName().equals("StorageFragment")) {
                    imgStatus.setImageResource(R.drawable.ic_storage);
                } else if (fragment.getClass().getSimpleName().equals("BattleFragment")) {
                    imgStatus.setImageResource(R.drawable.ic_state_battle);
                } else {
                    imgStatus.setImageResource(android.R.color.transparent);
                }
            }

            // Training
            if (lutemon.isTraining() && trainingProgressContainer != null) {
                trainingProgressContainer.setVisibility(View.VISIBLE);

                long total = lutemon.getCurrentTrainingType() != null
                        ? TrainingManager.getTrainingDuration(l.getCurrentTrainingType())
                        : 60000;

                if (trainingProgress != null) {
                    trainingProgress.setMax((int) total);
                    trainingProgress.setProgress((int) (total - lutemon.getTrainingRemaining()));
                    trainingProgress.setVisibility(View.VISIBLE);
                }

                if (timer != null) {
                    timer.setText((lutemon.getTrainingRemaining() / 1000) + "s");
                    timer.setVisibility(View.VISIBLE);
                }

                if (cancelButton != null) {
                    cancelButton.setVisibility(View.VISIBLE);
                    cancelButton.setOnClickListener(v -> {
                        if (fragment instanceof TrainingFragment) {
                            ((TrainingFragment) fragment).onCancelTraining(lutemon.getId());
                        }
                    });
                }

                if (fragment instanceof TrainingFragment) {
                    ((TrainingFragment) fragment).registerTrainingUI(lutemon.getId(), trainingProgress, timer, cancelButton);
                }

            } else {
                if (trainingProgress != null) trainingProgress.setVisibility(View.GONE);
                if (timer != null) timer.setVisibility(View.GONE);
                if (cancelButton != null) cancelButton.setVisibility(View.GONE);
            }
        }
    }
}