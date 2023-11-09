package com.example.mobileappdevass2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobileappdevass2.databinding.FragmentFirstBinding;

import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ShareLocationView customViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //database
        DatabaseHelper customDatabaseHelper = new DatabaseHelper(getContext());
        List<Location> locationList = customDatabaseHelper.getAllLocations();

        //adapter
        Adapter customAdapter = new Adapter(getContext(), locationList);


        customViewModel = new ViewModelProvider(getActivity()).get(ShareLocationView.class);
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.locationView.setAdapter(customAdapter);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //filter
            @Override
            public boolean onQueryTextSubmit(String query) {
                customAdapter.getFilter().filter(query);
                return false;
            }

            //filter
            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return false;
            }
        });

        //navigate to the SecondFragment with a customized action
        binding.locationView.setOnItemClickListener((parent, view, position, id) -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);

            Location selectedLocation = (Location) customAdapter.getItem(position);
            customViewModel.setLocationData(selectedLocation);
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //navigate to the SecondFragment with a customized action
        binding.addLocationButton.setOnClickListener(view1 -> {

            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
            customViewModel.setLocationData(null);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
