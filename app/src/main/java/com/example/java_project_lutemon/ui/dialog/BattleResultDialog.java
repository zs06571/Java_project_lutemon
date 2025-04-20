package com.example.java_project_lutemon.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.java_project_lutemon.R;

public class BattleResultDialog extends DialogFragment {
    private static final String ARG_IS_WIN = "is_win";
    private static final String ARG_EXP_GAINED = "exp_gained";

    private TextView textResult;
    private TextView textExp;
    private ImageView imageResult;
    private View rootLayout;

    public static BattleResultDialog newInstance(boolean isWin, int expGained) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_WIN, isWin);
        args.putInt(ARG_EXP_GAINED, expGained);

        BattleResultDialog dialog = new BattleResultDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_battle_result, null);

        initViews(view);

        boolean isWin = requireArguments().getBoolean(ARG_IS_WIN);
        int expGained = requireArguments().getInt(ARG_EXP_GAINED);

        setupUI(isWin, expGained);

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("confirm", (dialog, which) -> dismiss())
                .setCancelable(false)
                .create();
    }

    private void initViews(View rootView) {
        textResult = rootView.findViewById(R.id.text_result);
        textExp = rootView.findViewById(R.id.text_exp);
        imageResult = rootView.findViewById(R.id.image_result);
        rootLayout = rootView.findViewById(R.id.root_layout);

        if (textResult == null || textExp == null || imageResult == null || rootLayout == null) {
            throw new IllegalStateException("Required views not found in dialog layout");
        }
    }

    private void setupUI(boolean isWin, int expGained) {
        textResult.setText(isWin ? R.string.victory : R.string.defeat);
        textExp.setText(getString(R.string.exp_gained, expGained));
        imageResult.setImageResource(isWin ? R.drawable.ic_victory : R.drawable.ic_defeat);

        if (isWin) {
            rootLayout.setBackgroundResource(R.drawable.ic_victory);
        }
    }
}