package com.example.java_project_lutemon;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.example.java_project_lutemon.core.repository.LutemonRepository;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();

        LutemonRepository.getInstance().loadFromFile(this);

        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LutemonRepository.getInstance().saveToFile(activity.getApplicationContext());
    }

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override public void onActivityStarted(Activity activity) {}
    @Override public void onActivityResumed(Activity activity) {}
    @Override public void onActivityStopped(Activity activity) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override public void onActivityDestroyed(Activity activity) {}
}
