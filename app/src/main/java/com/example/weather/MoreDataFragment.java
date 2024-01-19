package com.example.weather;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class MoreDataFragment extends Fragment {

    private TextView windStrengthTextView, windDirectionTextView, humidityTextView, visibilityTextView;
    double windSpeedto2=SimpleDataFragment.windSpeedto2, windDegto2=SimpleDataFragment.windDegto2;
    int visibilityto2=SimpleDataFragment.visibilityto2, humidityto2=SimpleDataFragment.humidityto2;

    public MoreDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.moredatafragment_layout, container, false);

        windStrengthTextView = rootView.findViewById(R.id.windStrength);
        windDirectionTextView = rootView.findViewById(R.id.windDirection);
        humidityTextView = rootView.findViewById(R.id.humidity);
        visibilityTextView = rootView.findViewById(R.id.visibility);
        Log.d("WeatherApiTask", "humidityto2 " + humidityto2);
        Log.d("WeatherApiTask", "windSpeedto2 " + windSpeedto2);
        Log.d("WeatherApiTask", "windDegto2 " + windDegto2);
        Log.d("WeatherApiTask", "visibilityto2 " + visibilityto2);
        updateWeatherData(windSpeedto2, windDegto2, visibilityto2, humidityto2);

        return rootView;
    }

    // Metoda, która aktualizuje dane w widoku MoreDataFragment
    public void updateWeatherData(double windSpeed, double windDeg, int visibility, int humidity) {
        windStrengthTextView.setText("Siła wiatru: " + windSpeed);
        windDirectionTextView.setText("Kierunek wiatru: " + windDeg);
        humidityTextView.setText("Wilgotność: " + humidity);
        visibilityTextView.setText("Widoczność: " + visibility);
    }
}
