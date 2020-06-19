package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=TOKEN&lang=en&units=metric";

    private EditText editTextCity;
    private Button buttonShowWeather;
    private TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextTextCity);
        buttonShowWeather = findViewById(R.id.buttonShowWeather);
        textViewWeather = findViewById(R.id.textViewWeather);

    }

    public void onClickShowWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if (!city.isEmpty()) {
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(WEATHER_URL,city);
            task.execute(url);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder(); // to save the data
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection(); // open Connection
                InputStream inputStream = urlConnection.getInputStream(); // getting the stream
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // to read the data from the stream
                BufferedReader reader = new BufferedReader(inputStreamReader);  // making iSR into BR to read by lines
                String line = reader.readLine();  // reading the data
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");  // taking city name
                String temp = jsonObject.getJSONObject("main").getString("temp");  // temperature
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String weather = String.format(getString(R.string.weather_descript), city, temp, description);
                textViewWeather.setText(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
