package com.example.java_project_lutemon.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.ui.view.ColorPickerView;

public class CreateLutemonDialog extends DialogFragment {
    private EditText editName;
    private ColorPickerView colorPicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_lutemon, null);

        initViews(dialogView);

        builder.setView(dialogView)
                .setTitle("Create New Lutemon")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String name = editName.getText().toString();
                    int color = colorPicker.getSelectedColor();
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    private void initViews(View rootView) {
        editName = rootView.findViewById(R.id.edit_name);
        colorPicker = rootView.findViewById(R.id.color_picker);

        if (editName == null || colorPicker == null) {
            throw new IllegalStateException("Required views not found in dialog layout");
        }
    }
}