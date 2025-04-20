package com.example.java_project_lutemon.core.data;

import android.content.Context;
import android.widget.Toast;

public class ErrorHandler {
    public static void logError(String tag, String message) {
        System.err.println(tag + ": " + message);
    }

    public static void showUserError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}