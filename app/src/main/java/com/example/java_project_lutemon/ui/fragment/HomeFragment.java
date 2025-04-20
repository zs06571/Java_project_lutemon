package com.example.java_project_lutemon.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.ui.adapter.LutemonCardAdapter;
import com.example.java_project_lutemon.ui.viewmodel.StorageViewModel;
import com.example.java_project_lutemon.core.state.GameState;
import com.example.java_project_lutemon.core.training.TrainingManager;
import com.example.java_project_lutemon.core.training.TrainingType;
import com.example.java_project_lutemon.core.training.TrainingCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView rvLutemons;
    private LutemonCardAdapter adapter;
    private List<Lutemon> lutemonList = new ArrayList<>();
    private StorageViewModel storageViewModel;
    private Button btnCreateLutemon, btnMoveToStorage, btnRelease;
    private final List<Lutemon> selectedBattleLutemons = new ArrayList<>();
    private EditText searchInput;
    private Spinner filterSpinner;
    private String currentFilter = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnCreateLutemon = view.findViewById(R.id.btn_create_lutemon);
        btnMoveToStorage = view.findViewById(R.id.btn_move_to_storage);
        btnRelease = view.findViewById(R.id.btn_release);

        searchInput = view.findViewById(R.id.search_input);
        filterSpinner = view.findViewById(R.id.spinner_filter);

        rvLutemons = view.findViewById(R.id.rv_lutemons);
        rvLutemons.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LutemonCardAdapter(this, requireContext(), lutemonList);
        adapter.notifyDataSetChanged();
        rvLutemons.setAdapter(adapter);

        adapter.setOnItemClickListener(lutemon -> {
            if (selectedBattleLutemons.contains(lutemon)) {
                selectedBattleLutemons.remove(lutemon);
            } else {
                if (selectedBattleLutemons.size() == 2) {
                    selectedBattleLutemons.remove(0);
                }
                selectedBattleLutemons.add(lutemon);
            }
            adapter.setSelectedLutemons(selectedBattleLutemons);
        });

        view.findViewById(R.id.btn_start_battle).setOnClickListener(v -> {
            if (selectedBattleLutemons.size() == 2) {
                Bundle bundle = new Bundle();
                bundle.putInt("leftLutemonId", selectedBattleLutemons.get(0).getId());
                bundle.putInt("rightLutemonId", selectedBattleLutemons.get(1).getId());

                NavHostFragment.findNavController(this)
                        .navigate(R.id.battleFragment, bundle);
            } else {
                Toast.makeText(getContext(), "Please choose two lutemons to battle!", Toast.LENGTH_SHORT).show();
            }
        });

        storageViewModel = new ViewModelProvider(this).get(StorageViewModel.class);
        storageViewModel.getRestingLutemons().observe(getViewLifecycleOwner(), list -> {
            Log.d("DEBUG_HOME", "Resting Lutemons count = " + list.size());
            for (Lutemon l : list) {
                Log.d("DEBUG_HOME", "Found Lutemon: " + l.getName() + ", state=" + l.getState() + ", type=" + l.getType());
            }
            lutemonList.clear();
            lutemonList.addAll(list);
            adapter.setLutemons(list);
            Log.d("DEBUG_LIST", "setLutemons: " + list.size());
            adapter.notifyDataSetChanged();
            adapter.filter(searchInput.getText().toString(), currentFilter);

            searchInput.setText("");
            filterSpinner.setSelection(0);
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString(), currentFilter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.lutemon_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setSelection(0);
        currentFilter = "all";

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFilter = parent.getItemAtPosition(position).toString().toLowerCase();
                adapter.filter(searchInput.getText().toString(), currentFilter);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnCreateLutemon.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_home_to_create));

        btnMoveToStorage.setOnClickListener(v -> {
            int pos = adapter.getSelectedPosition();
            if (pos != -1) {
                Lutemon selected = lutemonList.get(pos);
                selected.setState(GameState.STORAGE);
                LutemonRepository.getInstance().updateLutemon(selected);
                storageViewModel.moveToStorage(selected.getId());
                Toast.makeText(getContext(), selected.getName() + " moved to Storage", Toast.LENGTH_SHORT).show();
                lutemonList.remove(pos);
                adapter.notifyItemRemoved(pos);
            } else {
                Toast.makeText(getContext(), "Please select a Lutemon first", Toast.LENGTH_SHORT).show();
            }
        });

        btnRelease.setOnClickListener(v -> {
            int pos = adapter.getSelectedPosition();
            if (pos >= 0 && pos < lutemonList.size()) {
                Lutemon selected = lutemonList.get(pos);
                new AlertDialog.Builder(requireContext())
                        .setTitle("Release Lutemon")
                        .setMessage("Are you sure you want to release " + selected.getName() + "? This action cannot be undone.")
                        .setPositiveButton("Release", (dialog, which) -> {
                            LutemonRepository.getInstance().deleteLutemon(selected.getId());

                            if (pos < lutemonList.size()) {
                                lutemonList.remove(pos);
                                adapter.setLutemons(lutemonList);
                                adapter.notifyDataSetChanged();
                            }

                            Toast.makeText(getContext(), selected.getName() + " released", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                Toast.makeText(getContext(), "Please select a Lutemon first", Toast.LENGTH_SHORT).show();
            }
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                menu.clear();
                inflater.inflate(R.menu.top_toolbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_lutemon_list) {
                    NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_home_to_list);
                    return true;
                } else if (id == R.id.menu_lutemon_details) {
                    NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_home_to_detail);
                    return true;
                } else if (id == R.id.menu_statistics) {
                    NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_home_to_stats);
                    return true;
                } else if (id == R.id.menu_logout) {
                    Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();

                    LutemonRepository.getInstance().saveToFile(requireContext());

                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_home_to_login);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private class LutemonAdapter extends RecyclerView.Adapter<LutemonViewHolder> {
        private final List<Lutemon> lutemons;
        private int selectedPosition = -1;

        LutemonAdapter(List<Lutemon> lutemons) {
            this.lutemons = lutemons;
        }

        @NonNull
        @Override
        public LutemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_lutemon_card, parent, false);
            return new LutemonViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LutemonViewHolder holder, int position) {
            Lutemon lutemon = lutemons.get(position);
            holder.bind(lutemon);
            holder.itemView.setBackgroundColor(position == selectedPosition
                    ? getResources().getColor(R.color.background, null)
                    : getResources().getColor(android.R.color.transparent, null));
            holder.itemView.setOnClickListener(v -> {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return lutemons.size();
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }
    }

    private class LutemonViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName, txtType, tvSkillLevel, txtExp;
        private final TextView txtAtk, txtDef, txtHp;
        private final ImageView imgAvatar;
        private final ProgressBar progressExp;
        private final CardView cardView;

        public LutemonViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            txtName = itemView.findViewById(R.id.txt_name);
            txtType = itemView.findViewById(R.id.txt_type);
            tvSkillLevel = itemView.findViewById(R.id.tv_skill_level);
            progressExp = itemView.findViewById(R.id.progress_exp);
            txtExp = itemView.findViewById(R.id.txt_exp);
            cardView = itemView.findViewById(R.id.card_lutemon);
            txtAtk = itemView.findViewById(R.id.txt_atk);
            txtDef = itemView.findViewById(R.id.txt_def);
            txtHp = itemView.findViewById(R.id.txt_hp);
        }

        public void bind(Lutemon lutemon) {
            txtName.setText(lutemon.getName());
            txtType.setText(lutemon.getType().name());
            tvSkillLevel.setText("Lv." + lutemon.getLevel());
            txtAtk.setText("ATK: " + lutemon.getAttack());
            txtDef.setText("DEF: " + lutemon.getDefense());
            txtHp.setText("HP: " + lutemon.getMaxHp());
            progressExp.setMax(lutemon.getExpToLevelUp());
            progressExp.setProgress(lutemon.getExperience());
            txtExp.setVisibility(View.GONE);
            progressExp.setOnClickListener(v -> {
                boolean show = txtExp.getVisibility() == View.GONE;
                txtExp.setVisibility(show ? View.VISIBLE : View.GONE);
            });
            imgAvatar.setImageResource(lutemon.getImageResId());
            cardView.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), lutemon.getType().getColorResId()));
        }
    }
}
