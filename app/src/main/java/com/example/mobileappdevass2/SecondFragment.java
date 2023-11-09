package com.example.mobileappdevass2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobileappdevass2.databinding.FragmentSecondBinding;
import com.google.android.material.textfield.TextInputLayout;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private ShareLocationView viewModel;
    private Location location;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ShareLocationView.class);
        //when clicked, database will be called
        binding.sendButton.setOnClickListener(v -> {

            if (areInputsValid()) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

                if (location != null) {
                    updateLocation(databaseHelper);
                } else {
                    addLocation(databaseHelper);
                }

                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            } else {
                errorInputs();
            }
        });

        binding.deleteButton.setOnClickListener(v -> {
            //setup the database
            DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
            if (location != null) {
                deleteLocation(databaseHelper);
            } else {
                Toast.makeText(getContext(), "cannot delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean areInputsValid() {
        String latitude = binding.latitudeText.getText().toString();
        String longitude = binding.longitudeText.getText().toString();

        return isValidCoordinate(latitude) && isValidCoordinate(longitude);
    }

    private void errorInputs() {
        String latitude = binding.latitudeText.getText().toString();
        String longitude = binding.longitudeText.getText().toString();

        setErrorIfInvalid(latitude, binding.latitudeLayout, "Latitude must be from 90 to -90 degrees");
        setErrorIfInvalid(longitude, binding.longitudeLayout, "longitude must be from 180 ro -180 degres");
    }

    private void setErrorIfInvalid(String input, TextInputLayout layout, String errorMessage) {
        if (input.isEmpty() || !isValidCoordinate(input)) {
            layout.setError(errorMessage);
        } else {
            layout.setError(null);
        }
    }


    //update location in the database
    private void updateLocation(DatabaseHelper databaseHelper) {
        String latitude = binding.latitudeText.getText().toString();
        String longitude = binding.longitudeText.getText().toString();

        Location updatedLocationModel = new Location(location.getId(), latitude, longitude);
        databaseHelper.updateLocation(updatedLocationModel);
    }

    //send location to database
    private void addLocation(DatabaseHelper databaseHelper) {
        String latitude = binding.latitudeText.getText().toString();
        String longitude = binding.longitudeText.getText().toString();

        Location location = new Location(latitude, longitude);
        boolean success = databaseHelper.addLocation(location);
        System.out.println(location.toString());
    }

    //delete location
    private void deleteLocation(DatabaseHelper databaseHelper) {
        databaseHelper.deleteLocation(location.getId());

        NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment);
    }

    private double parseCoordinate(String coordinate) {
        try {
            return Double.parseDouble(coordinate);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private boolean isValidCoordinate(String coordinate) {
        double value = parseCoordinate(coordinate);
        return !Double.isNaN(value) && value >= -90 && value <= 90;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
