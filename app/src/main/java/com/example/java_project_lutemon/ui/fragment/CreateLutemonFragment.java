package com.example.java_project_lutemon.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.appcompat.widget.Toolbar;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.model.LutemonFactory;
import com.example.java_project_lutemon.core.model.LutemonType;
import com.example.java_project_lutemon.core.skill.Skill;
import com.example.java_project_lutemon.ui.viewmodel.CreateLutemonViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;

public class CreateLutemonFragment extends Fragment {

    private EditText editTextName;
    private MaterialAutoCompleteTextView dropdownLutemonType;
    private TextInputLayout nameInputLayout, typeInputLayout;
    private ImageView imageTypePreview;
    private TextView textStats;
    private TextView textSkillInfo;
    private MaterialButton buttonSave;
    private MaterialButton buttonCancel;
    private CreateLutemonViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_lutemon, container, false);
        initViews(view);
        viewModel = new ViewModelProvider(this).get(CreateLutemonViewModel.class);
        setupTypeSpinner();
        setupListeners();
        setupObservers();
        return view;
    }

    private void initViews(View view) {
        editTextName = view.findViewById(R.id.editTextName);
        dropdownLutemonType = view.findViewById(R.id.dropdownLutemonType);
        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        typeInputLayout = view.findViewById(R.id.typeInputLayout);
        imageTypePreview = view.findViewById(R.id.imageTypePreview);
        textStats = view.findViewById(R.id.textStats);
        textSkillInfo = view.findViewById(R.id.textSkillInfo);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancel = view.findViewById(R.id.buttonCancel);
    }

    private void setupTypeSpinner() {
        String[] typeNames = Arrays.stream(LutemonType.values())
                .map(LutemonType::getDisplayName)
                .toArray(String[]::new);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                typeNames
        );
        dropdownLutemonType.setAdapter(adapter);
        dropdownLutemonType.setKeyListener(null);
        dropdownLutemonType.setOnClickListener(v -> dropdownLutemonType.showDropDown());
        dropdownLutemonType.setOnItemClickListener((parent, view, position, id) -> {
            LutemonType selected = LutemonType.values()[position];
            viewModel.onTypeSelected(selected);
            updateUIForSelectedType(selected);
        });
    }

    private void updateUIForSelectedType(LutemonType selected) {
        if (selected != null) {
            typeInputLayout.setHint(selected.getDisplayName());
            dropdownLutemonType.setContentDescription("Current type: " + selected.getDisplayName());
            dropdownLutemonType.announceForAccessibility("Current type: " + selected.getDisplayName());
            imageTypePreview.setImageResource(selected.getImageResId());
            imageTypePreview.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), selected.getColorResId()));

            Lutemon sample = LutemonFactory.create(selected, "Sample", 0);
            String stats = String.format(getString(R.string.lutemon_stats_format),
                    sample.getAttack(),
                    sample.getDefense(),
                    sample.getMaxHp());
            textStats.setText(stats);

            if (sample.getSkillList() == null || sample.getSkillList().isEmpty()) {
                textSkillInfo.setVisibility(View.GONE);
            } else {
                textSkillInfo.setVisibility(View.VISIBLE);
                StringBuilder skillDesc = new StringBuilder("Skills:\n");
                for (Skill skill : sample.getSkillList()) {
                    skillDesc.append("- ").append(skill.getDescription()).append("\n");
                }
                textSkillInfo.setText(skillDesc.toString().trim());
            }
        }
    }

    private void setupListeners() {
        buttonSave.setOnClickListener(v -> validateAndSaveLutemon());

        buttonCancel.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(
                    R.id.action_create_to_home,
                    null,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, false)
                            .setLaunchSingleTop(true)
                            .build()
            );
        });
    }

    private void validateAndSaveLutemon() {
        String name = editTextName.getText().toString().trim();
        LutemonType type = viewModel.getSelectedType().getValue();

        if (name.isEmpty()) {
            nameInputLayout.setError(getString(R.string.error_empty_name));
            editTextName.requestFocus();
            return;
        }

        if (type == null) {
            Toast.makeText(requireContext(), R.string.error_select_type, Toast.LENGTH_SHORT).show();
            dropdownLutemonType.announceForAccessibility(getString(R.string.error_select_type));
            return;
        }

        nameInputLayout.setError(null);
        viewModel.createLutemon(name, type);
    }

    private void setupObservers() {
        viewModel.getCreationResult().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                new android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Lutemon Created")
                        .setMessage("Your new Lutemon has been successfully created!")
                        .setPositiveButton("OK", (dialog, which) -> {
                            NavHostFragment.findNavController(this).navigate(
                                    R.id.action_create_to_home,
                                    null,
                                    new NavOptions.Builder()
                                            .setPopUpTo(R.id.homeFragment, false)
                                            .setLaunchSingleTop(true)
                                            .build()
                            );
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        viewModel.getSelectedType().observe(getViewLifecycleOwner(), this::updateUIForSelectedType);

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.create_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Create Lutemon Page");
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.create_lutemon_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_options) {
                    View menuItemView = toolbar.findViewById(item.getItemId());
                    PopupMenu popupMenu = new PopupMenu(requireContext(), menuItemView, Gravity.END);
                    popupMenu.getMenu().add(Menu.NONE, 1, 1, "Lutemon List");
                    popupMenu.getMenu().add(Menu.NONE, 2, 2, "Lutemon Detail");
                    popupMenu.setOnMenuItemClickListener(subItem -> {
                        if (subItem.getItemId() == 1) {
                            NavHostFragment.findNavController(CreateLutemonFragment.this)
                                    .navigate(R.id.action_to_lutemon_list);
                        } else if (subItem.getItemId() == 2) {
                            NavHostFragment.findNavController(CreateLutemonFragment.this)
                                    .navigate(R.id.action_to_lutemon_detail);
                        }
                        return true;
                    });
                    popupMenu.show();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editTextName = null;
        dropdownLutemonType = null;
        nameInputLayout = null;
        typeInputLayout = null;
        imageTypePreview = null;
        textStats = null;
        textSkillInfo = null;
        buttonSave = null;
        buttonCancel = null;
    }
}