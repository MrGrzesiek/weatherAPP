package com.example.weather;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

public class WeatherApiTask extends AsyncTask<String, Void, String> {

    private static final String API_KEY = "93f7136268c95738b07e1d7ee08cf74a";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    private WeatherListener weatherListener;
    private String units;  // Dodaj zmienną globalną dla jednostek
    private Context context;  // Dodaj kontekst aplikacji

    public WeatherApiTask(Context context, WeatherListener weatherListener, String units) {
        this.context = context;
        this.weatherListener = weatherListener;
        this.units = units;
    }

    @Override
    protected String doInBackground(String... params) {
        String city = params[0];
        Log.d("WeatherApiTask", "City Name: " + city);

        try {
            URL url = new URL(API_URL + "?q=" + city + "&appid=" + API_KEY + "&units=" + units + "&lang=pl");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Zapisz JSON do pliku
            saveJsonToFile(response.toString(), city);

            return response.toString();

        } catch (Exception e) {
            Log.e("WeatherApiTask", "Error while fetching weather data", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null) {
            Log.d("WeatherApiTask", "Received JSON response: " + result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                // Wyciągnij dane z podklucza "dt"
                long dt = jsonObject.getLong("dt");

                // Wyciągnij dane z podklucza "coord"
                JSONObject coordObject = jsonObject.getJSONObject("coord");
                double latitude = coordObject.getDouble("lat");
                double longitude = coordObject.getDouble("lon");

                // Wyciągnij dane z podklucza "main"
                JSONObject mainObject = jsonObject.getJSONObject("main");
                double temperature = mainObject.getDouble("temp");
                double pressure = mainObject.getDouble("pressure");
                int humidity = mainObject.getInt("humidity");

                // Wyciągnij dane z podklucza "weather"
                JSONObject weatherObject = jsonObject.getJSONArray("weather").getJSONObject(0);
                String iconCode = weatherObject.getString("icon");

                // Wyciągnij dane z podklucza "wind"
                JSONObject windObject = jsonObject.getJSONObject("wind");
                double windSpeed = windObject.getDouble("speed");
                double windDeg = windObject.getDouble("deg");

                // Wyciągnij dane z podklucza "visibility"
                int visibility = jsonObject.getInt("visibility");

                // Konwertuj dt na format daty i czasu
                String formattedDate = convertTimestampToDate(dt);

                // Konwertuj szerokość i długość geograficzną na string z kierunkami
                String formattedCoords = formatCoordinates(latitude, longitude);

                String weatherDesc = weatherObject.getString("description");

                // Przekazanie danych do słuchacza
                weatherListener.onWeatherUpdate(temperature, pressure, humidity, iconCode, windSpeed,windDeg, visibility, formattedDate, formattedCoords,weatherDesc);

            } catch (JSONException e) {
                Log.e("WeatherApiTask", "Error parsing JSON response", e);
            }
        } else {
            Log.e("WeatherApiTask", "Empty or null response");
        }
    }

    // Interfejs do przekazywania danych o pogodzie do klasy wywołującej
    public interface WeatherListener {
        void onWeatherUpdate(double temperature, double pressure, int humidity, String iconCode, double windSpeed,double windDeg, int visibility, String formattedDate, String formattedCoords,String weatherDesc);

    }

    // Funkcja do zapisywania JSON do pliku
    private void saveJsonToFile(String json, String fileName) {
        try {
            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "WeatherData");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName + ".json");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("WeatherApiTask", "Error saving JSON to file", e);
        }
    }

    // Konwersja timestamp na format daty i czasu
    private String convertTimestampToDate(long timestamp) {
        try {
            Date date = new Date(timestamp * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            Log.e("WeatherApiTask", "Error converting timestamp to date", e);
            return "";
        }
    }
    private String formatCoordinates(double latitude, double longitude) {
        String latDirection = (latitude >= 0) ? "N" : "S";
        String lonDirection = (longitude >= 0) ? "E" : "W";

        return String.format(Locale.getDefault(), "%.2f°%s, %.2f°%s", Math.abs(latitude), latDirection, Math.abs(longitude), lonDirection);
    }
}
