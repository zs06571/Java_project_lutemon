package com.example.java_project_lutemon.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.ui.fragment.TrainingFragment;

public class TrainingProgressDialog extends DialogFragment {
    private ProgressBar progressBar;
    private TextView textProgress;
    private int lutemonId;

    public TrainingProgressDialog() {
    }
    public static TrainingProgressDialog newInstance(int lutemonId) {
        TrainingProgressDialog dialog = new TrainingProgressDialog();
        Bundle args = new Bundle();
        args.putInt("lutemonId", lutemonId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            lutemonId = getArguments().getInt("lutemonId", -1);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_training_progress);
        dialog.setCancelable(true);

        TextView tvStatus = dialog.findViewById(R.id.tv_training_status);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_training);

        tvStatus.setText("Training in progress...");

        btnCancel.setOnClickListener(v -> {
            if (getParentFragment() instanceof TrainingFragment) {
                ((TrainingFragment) getParentFragment()).onCancelTraining(lutemonId);
            }
            dismiss();
        });

        return dialog;
    }

    public void updateProgress(int progress) {
        if (progressBar != null && textProgress != null) {
            progressBar.setProgress(progress);
            textProgress.setText(progress + "%");
        }
    }
}