package com.example.java_project_lutemon.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.ui.adapter.LutemonCardAdapter;
import com.example.java_project_lutemon.ui.viewmodel.LutemonViewModel;

import java.util.ArrayList;
import java.util.List;

public class LutemonListFragment extends Fragment {

    private RecyclerView recyclerView;
    private LutemonCardAdapter adapter;
    private LutemonViewModel viewModel;

    public LutemonListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lutemon_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Toolbar toolbar = view.findViewById(R.id.list_toolbar);
        TextView title = view.findViewById(R.id.toolbar_title);
        title.setText("Lutemon List");

        if (requireActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        toolbar.setNavigationOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        recyclerView = view.findViewById(R.id.recycler_lutemon_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LutemonCardAdapter(this, requireContext(), new ArrayList<>(), R.layout.item_lutemon_detail_card);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(LutemonViewModel.class);
        viewModel.getLutemons().observe(getViewLifecycleOwner(), lutemons -> {
            if (lutemons != null) {
                adapter.setLutemons(lutemons);
            }
        });

        viewModel.refreshData();
        adapter.setLutemons(viewModel.getLutemons().getValue());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            List<Lutemon> updated = LutemonRepository.getInstance().getAllLutemons();
            adapter.setLutemons(updated);
            adapter.notifyDataSetChanged();
        }
    }
}