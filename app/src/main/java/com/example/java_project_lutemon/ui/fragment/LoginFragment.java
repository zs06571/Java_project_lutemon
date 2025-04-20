package com.example.java_project_lutemon.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.repository.LutemonRepository;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnImport = view.findViewById(R.id.btn_import_data);
        Button btnNewGame = view.findViewById(R.id.btn_new_game);

        LutemonRepository repository = LutemonRepository.getInstance();

        btnImport.setOnClickListener(v -> {
            repository.loadFromFile(requireContext());
            NavHostFragment.findNavController(this).navigate(R.id.action_login_to_home);
        });

        btnNewGame.setOnClickListener(v -> {
            repository.clearAll();
            NavHostFragment.findNavController(this).navigate(R.id.action_login_to_home);
        });
    }
}