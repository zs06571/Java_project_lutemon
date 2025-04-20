package com.example.java_project_lutemon.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.ui.dialog.ColorPickerDialog;

public class ColorGridAdapter extends BaseAdapter {
    private final Context context;
    private final int[] colors = {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA
    };

    public ColorGridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false);
        ImageView colorView = view.findViewById(R.id.color_item_view);
        colorView.setBackgroundColor(colors[position]);

        view.setOnClickListener(v -> {
            if (context instanceof ColorPickerDialog.OnColorSelectedListener) {
                ((ColorPickerDialog.OnColorSelectedListener) context).onColorSelected(colors[position]);
            }
        });

        return view;
    }
}

