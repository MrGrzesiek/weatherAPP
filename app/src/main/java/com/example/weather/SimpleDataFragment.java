package com.example.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.weather.R;

import java.util.ArrayList;
import java.util.List;

public class SimpleDataFragment extends Fragment {

    private LinearLayout inputFieldsLayout;
    private Spinner citySpinner;
    private Button showFieldsButton;
    private EditText editCityName;
    private Button sendFormButton;
    private List<String> citiesList;

    private boolean fieldsVisible = false;

    public SimpleDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.simpledatafragment_layout, container, false);

        inputFieldsLayout = rootView.findViewById(R.id.inputFieldsLayout);
        citySpinner = rootView.findViewById(R.id.citySpinner);
        editCityName = rootView.findViewById(R.id.editCityName);

        setupCitySpinner();

        showFieldsButton = rootView.findViewById(R.id.showFieldsButton);
        showFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zmiana widoczności pól
                fieldsVisible = !fieldsVisible;

                // Ustawienie tekstu na podstawie stanu widoczności
                showFieldsButton.setText(fieldsVisible ? "Ukryj pola" : "Pokaż pola");

                // Zmiana widoczności pól
                inputFieldsLayout.setVisibility(fieldsVisible ? View.VISIBLE : View.GONE);
            }
        });

        Button addButton = rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobranie nazwy miasta z pola tekstowego
                String cityName = editCityName.getText().toString();

                // Sprawdzenie, czy nazwa miasta nie jest pusta
                if (!cityName.isEmpty()) {
                    // Dodanie nazwy miasta do Spinnera
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) citySpinner.getAdapter();
                    adapter.add(cityName);
                    adapter.notifyDataSetChanged();

                    // Opcjonalnie można wyczyścić pole tekstowe po dodaniu miasta
                    editCityName.getText().clear();
                }
            }
        });

        return rootView;
    }

    private void setupCitySpinner() {
        // Przykładowa lista miast
        List<String> cities = new ArrayList<>();

        // Tworzenie adaptera dla Spinnera
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cities
        );

        // Ustawianie stylu rozwijanej listy
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Ustawianie adaptera dla Spinnera
        citySpinner.setAdapter(adapter);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}