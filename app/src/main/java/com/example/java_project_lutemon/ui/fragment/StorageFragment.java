package com.example.java_project_lutemon.ui.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.ui.adapter.LutemonCardAdapter;
import com.example.java_project_lutemon.ui.viewmodel.StorageViewModel;

import java.util.ArrayList;
import java.util.List;

public class StorageFragment extends Fragment {

    private RecyclerView recyclerView;
    private LutemonCardAdapter adapter;
    private StorageViewModel viewModel;
    private List<Lutemon> storageList = new ArrayList<>();
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_storage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.storage_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Storage");
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_storage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LutemonCardAdapter(this, requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(StorageViewModel.class);
        viewModel.getStoredLutemons().observe(getViewLifecycleOwner(), list -> {
            storageList.clear();
            storageList.addAll(list);
            adapter.setLutemons(list);
        });

        adapter.setOnItemClickListener(lutemon -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Move to Home")
                    .setMessage("Do you want to move " + lutemon.getName() + " back to Home?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        viewModel.restoreToHome(lutemon.getId());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        adapter.setOnItemLongClickListener((lutemon, anchorView) -> {
            PopupMenu popup = new PopupMenu(requireContext(), anchorView);

            popup.getMenu().add(Menu.NONE, 1, 1, "View Details");
            popup.getMenu().add(Menu.NONE, 2, 2, "Release");

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == 1) {
                    Bundle args = new Bundle();
                    args.putInt("lutemonId", lutemon.getId());
                    NavHostFragment.findNavController(StorageFragment.this)
                            .navigate(R.id.action_storage_to_detail, args);
                } else if (id == 2) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Release Lutemon")
                            .setMessage("Are you sure you want to release " + lutemon.getName() + "?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                viewModel.removeLutemon(lutemon.getId());
                                Toast.makeText(getContext(), lutemon.getName() + " released", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
                return true;
            });
            popup.show();
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.storage_menu, menu);
                MenuItem searchItem = menu.findItem(R.id.menu_search);
                androidx.appcompat.widget.SearchView searchView =
                        (androidx.appcompat.widget.SearchView) searchItem.getActionView();
                searchView.setQueryHint("Search");

                View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
                if (searchPlate != null) {
                    searchPlate.setBackgroundColor(Color.TRANSPARENT);
                }
                EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
                if (searchText != null) {
                    searchText.setTextColor(Color.WHITE);
                    searchText.setHintTextColor(Color.LTGRAY);
                    searchText.setTextSize(18);
                    searchText.setTypeface(Typeface.DEFAULT_BOLD);
                }

                ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
                if (searchIcon != null) {
                    searchIcon.setVisibility(View.GONE);
                }
                searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        adapter.filter(query, currentFilter);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.filter(newText, currentFilter);
                        return true;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                NavController navController = NavHostFragment.findNavController(StorageFragment.this);

                if (menuItem.getItemId() == R.id.item_lutemon_list) {
                    navController.navigate(R.id.action_storage_to_list);
                    return true;

                } else if (menuItem.getItemId() == R.id.item_statistics) {
                    navController.navigate(R.id.action_storage_to_statistics);
                    return true;
                }
                return false;
            }

        }, getViewLifecycleOwner());
    }
}
