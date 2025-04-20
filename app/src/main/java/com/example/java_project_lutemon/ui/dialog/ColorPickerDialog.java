package com.example.java_project_lutemon.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.ui.adapter.ColorGridAdapter;

public class ColorPickerDialog extends DialogFragment {
    private OnColorSelectedListener listener;

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_color_picker, null);

        GridView grid = view.findViewById(R.id.color_grid);
        grid.setAdapter(new ColorGridAdapter(getContext()));

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Select Color")
                .create();
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }
}

