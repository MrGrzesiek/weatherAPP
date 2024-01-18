package com.example.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class MoreDataFragment extends Fragment {

    private TextView windStrengthTextView,windDirectionTextView, humidityTextView, visibilityTextView;

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

        // Inflate the layout for this fragment
        return rootView;
    }
    public void updateWeatherData(double windSpeed, double windDeg, int visibility, int humidity) {
        windStrengthTextView.setText("Siła wiatru: " + windSpeed);
        windDirectionTextView.setText("Kierunek wiatru: " + windDeg);
        humidityTextView.setText("Wilgotność: " + visibility);
        visibilityTextView.setText("Widoczność: " + humidity);
    }
}