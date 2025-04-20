package com.example.java_project_lutemon.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LutemonRepository.getInstance().loadFromFile(this);

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNav, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            if (destId == R.id.splashFragment || destId == R.id.loginFragment) {
                bottomNav.setVisibility(View.GONE);
            } else {
                bottomNav.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LutemonRepository.getInstance().saveToFile(this);
    }

}
