package com.example.weather;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SimpleDataFragment extends Fragment {

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Zapisz dane, które chcesz zachować podczas obracania ekranu
        outState.putBoolean("fieldsVisible", fieldsVisible);
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Przywróć zapisane dane po obróceniu ekranu
        if (savedInstanceState != null) {
            fieldsVisible = savedInstanceState.getBoolean("fieldsVisible", false);
            formDataList = readCityListFromJson();
            // Aktualizuj UI w zależności od przywróconych danych
            updateInputFieldsVisibility();
            setupCitySpinner(extractCityNames(formDataList));

            // Jeśli istnieje wybrane miasto, wczytaj dane pogodowe
            String selectedCity = citySpinner.getSelectedItem().toString();
            if (!selectedCity.equals("Brak miast")) {
                loadWeatherDataForCity(selectedCity);
            }
        }
    }
    SendMessage SM;
    private LinearLayout inputFieldsLayout;
    private Spinner citySpinner;
    private Button addButton;
    private ImageView showFieldsButton, removeCityButton,fetchDataButton;
    private EditText editCityName;
    private TextView cityNameTextView,coordinatesTextView, timeTextView, temperatureTextView, pressureTextView, descriptionTextView,header4TextView;
    private boolean fieldsVisible = false;
    private List<String> formDataList = new ArrayList<>();

    static boolean refreshFlag = false;



    public SimpleDataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.simpledatafragment_layout, container, false);

        inputFieldsLayout = rootView.findViewById(R.id.inputFieldsLayout);
        citySpinner = rootView.findViewById(R.id.citySpinner);
        editCityName = rootView.findViewById(R.id.editCityName);
        showFieldsButton = rootView.findViewById(R.id.showFieldsButton);
        addButton = rootView.findViewById(R.id.addButton);
        removeCityButton = rootView.findViewById(R.id.removeCityButton);
        cityNameTextView = rootView.findViewById(R.id.cityName);
        coordinatesTextView = rootView.findViewById(R.id.coordinates);
        timeTextView = rootView.findViewById(R.id.time);
        temperatureTextView = rootView.findViewById(R.id.temperature);
        pressureTextView = rootView.findViewById(R.id.pressure);
        descriptionTextView = rootView.findViewById(R.id.description);
        header4TextView = rootView.findViewById(R.id.header4);
        fetchDataButton = rootView.findViewById(R.id.fetchDataButton);

        List<String> savedCityList = readCityListFromJson();
        formDataList.addAll(savedCityList);
        setupCitySpinner(extractCityNames(formDataList));
        changeNetworkHeader(WeatherApiTask.isNetworkAvailable);


        showFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldsVisible = !fieldsVisible;
                updateInputFieldsVisibility();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobranie danych z formularza
                String cityName = editCityName.getText().toString();

                // Sprawdzenie, czy nazwa miasta nie jest pusta
                if (!cityName.isEmpty()) {
                    // Usuń "Brak miast" z listy, jeśli istnieje
                    removeNoCities();

                    // Sprawdź czy miasto już istnieje w liście
                    if (!formDataList.contains(cityName)) {
                        // Dodanie nazwy miasta do Spinnera
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) citySpinner.getAdapter();
                        adapter.add(cityName);
                        adapter.notifyDataSetChanged();

                        // Dodanie danych do tablicy
                        String formData = "Miasto: " + cityName;
                        formDataList.add(formData);

                        // Opcjonalnie można wyczyścić pola tekstowe po dodaniu miasta
                        editCityName.getText().clear();

                        // Aktualizacja Spinnera na podstawie formDataList
                        setupCitySpinner(extractCityNames(formDataList));

                        int position = adapter.getPosition(cityName);
                        if (position != -1) {
                            citySpinner.setSelection(position);
                        }
                    }
                    loadWeatherDataForCity(cityName);
                    saveCityListToJson();
                }
                fieldsVisible = false;
                updateInputFieldsVisibility();
            }
        });
        removeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedCity();
                saveCityListToJson();
            }

        });
        fetchDataButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(NetworkUtils.isNetworkAvailable(getContext())){
                    // Wymuś pobranie danych z serwera dla wszystkich miast
                    refreshFlag = true;
                    fetchWeatherDataForAllCities();
                    refreshFlag = false;
                }
                else {
                    Toast.makeText(getContext(), "Brak dostępu do internetu. Dane są czytane z pliku.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Pobierz aktualnie wybrane miasto
                String selectedCity = citySpinner.getSelectedItem().toString();
                loadWeatherDataForCity(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        setupCitySpinner(extractCityNames(formDataList));
        saveCityListToJson();
        return rootView;
    }

    private void setupCitySpinner(List<String> cityNames) {
        if (cityNames.isEmpty()) {
            cityNames.add("Brak miast");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cityNames
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        citySpinner.setAdapter(adapter);
    }

    private void removeNoCities() {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) citySpinner.getAdapter();
        int position = adapter.getPosition("Brak miast");
        if (position != -1) {
            adapter.remove("Brak miast");
            adapter.notifyDataSetChanged();
        }
    }

    private List<String> extractCityNames(List<String> formDataList) {
        List<String> cityNames = new ArrayList<>();
        for (String formData : formDataList) {
            String[] parts = formData.split(", ");
            for (String part : parts) {
                if (part.startsWith("Miasto: ")) {
                    cityNames.add(part.substring(8));
                    break;
                }
            }
        }
        return cityNames;
    }

    private void removeSelectedCity() {
        int selectedPosition = citySpinner.getSelectedItemPosition();
        if (selectedPosition != AdapterView.INVALID_POSITION) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) citySpinner.getAdapter();
            String selectedCity = adapter.getItem(selectedPosition);

            // Remove the selected city from the Spinner
            adapter.remove(selectedCity);
            adapter.notifyDataSetChanged();

            // Remove the corresponding entry from the formDataList
            removeCityFromFormDataList(selectedCity);

            if (adapter.getCount() == 0) {
                adapter.add("Brak miast");
            }
        }
    }

    private void removeCityFromFormDataList(String selectedCity) {
        // Find and remove the corresponding entry from formDataList
        for (String formData : formDataList) {
            if (formData.contains("Miasto: " + selectedCity)) {
                formDataList.remove(formData);
                break;
            }
        }
    }

    private void printCityList(ArrayAdapter<String> adapter) {
        List<String> cityList = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            cityList.add(adapter.getItem(i));
        }

        Log.d("CityList", cityList.toString());
    }
    private void updateCityNameTextView(String cityName) {
        cityNameTextView.setText("Miejscowość: " + cityName);
        cityNameTextView.setVisibility(View.VISIBLE);
    }
    private void setWeatherIcon(String iconCode) {
        ImageView weatherIconImageView = getView().findViewById(R.id.weatherIcon);

        // Zakładając, że pliki ikon są w formacie "iconCode.png"
        String iconName = "w" + iconCode;

        // Pobierz identyfikator zasobu na podstawie nazwy pliku
        int iconResourceId = getResources().getIdentifier(iconName, "drawable", requireContext().getPackageName());

        // Ustaw ikonę w ImageView
        if (iconResourceId != 0) {
            weatherIconImageView.setImageResource(iconResourceId);
        } else {
            // Jeżeli nie znaleziono zasobu, ustaw domyślną ikonę
            weatherIconImageView.setImageResource(R.drawable.question);
        }
    }
    private void updateInputFieldsVisibility() {
        inputFieldsLayout.setVisibility(fieldsVisible ? View.VISIBLE : View.GONE);
    }
    private void loadWeatherDataForCity(String cityName) {
        // Wywołaj pobieranie danych o pogodzie dla wybranego miasta
        new WeatherApiTask(getContext(), new WeatherApiTask.WeatherListener() {
            @Override
            public void onWeatherUpdate(String city,double temperature, double pressure, int humidity, String iconCode, double windSpeed, double windDeg, int visibility, String formattedDate, String formattedCoords, String weatherDesc) {
                cityNameTextView.setText("Miejscowość: " + city);
                coordinatesTextView.setText("Współrzędne: " + formattedCoords);
                timeTextView.setText("Czas: " + formattedDate);
                temperatureTextView.setText("Temperatura: " + temperature +" \u2103"); //\u2109
                pressureTextView.setText("Ciśnienie: " + pressure+" hPa");
                descriptionTextView.setText("Opis: " + weatherDesc);
                setWeatherIcon(iconCode);
                SM.sendData(windSpeed, windDeg, visibility, humidity);
            }
        }, "metric").execute(cityName);
        changeNetworkHeader(WeatherApiTask.isNetworkAvailable);
    }
    private void changeNetworkHeader(boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            header4TextView.setVisibility(View.GONE);
        } else {
            header4TextView.setText("Brak dostępu do internetu dane są czytane z pliku");
            header4TextView.setVisibility(View.VISIBLE);
        }
    }
    // Metoda do zapisywania listy miast do pliku JSON
    private void saveCityListToJson() {
        JSONArray cityArray = new JSONArray();

        for (String city : formDataList) {
            JSONObject cityObject = new JSONObject();
            try {
                // Sprawdź, czy miasto już istnieje na liście
                if (!isCityAlreadyAdded(cityArray, city)) {
                    cityObject.put("cityName", city);

                    cityArray.put(cityObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Zapisz JSONArray do pliku JSON
        try (FileOutputStream fos = requireContext().openFileOutput("cityList.json", Context.MODE_PRIVATE)) {
            fos.write(cityArray.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda do odczytu listy miast z pliku JSON
    private List<String> readCityListFromJson() {
        List<String> cityList = new ArrayList<>();

        try {
            FileInputStream fis = requireContext().openFileInput("cityList.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            // Odczytaj JSONArray z pliku
            JSONArray cityArray = new JSONArray(sb.toString());

            // Przekształć JSONArray na listę miast
            for (int i = 0; i < cityArray.length(); i++) {
                JSONObject cityObject = cityArray.getJSONObject(i);
                String cityName = cityObject.getString("cityName");

                cityList.add(cityName);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return cityList;
    }
    private void fetchWeatherDataForAllCities() {
        // Iteruj przez listę miast i wywołaj pobieranie danych dla każdego z nich
        for (String city : extractCityNames(formDataList)) {
            new WeatherApiTask(getContext(), new WeatherApiTask.WeatherListener() {
                @Override
                public void onWeatherUpdate(String city,double temperature, double pressure, int humidity, String iconCode, double windSpeed, double windDeg, int visibility, String formattedDate, String formattedCoords, String weatherDesc) {
                    String selectedCity = citySpinner.getSelectedItem().toString();
                    loadWeatherDataForCity(selectedCity);
                }
            }, "metric").execute(city);
        }
    }
    interface SendMessage {
        void sendData(double windSpeed, double windDeg, int visibility, int humidity);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }
    private boolean isCityAlreadyAdded(JSONArray cityArray, String cityName) {
        for (int i = 0; i < cityArray.length(); i++) {
            try {
                JSONObject cityObject = cityArray.getJSONObject(i);
                String existingCityName = cityObject.getString("cityName");
                if (existingCityName.equals(cityName)) {
                    return true; // Miasto już istnieje na liście
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false; // Miasto nie istnieje na liście
    }
}


