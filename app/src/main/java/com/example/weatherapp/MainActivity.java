package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    ImageView weatherIcon;
    TextView temp, condition, city;
    final String APP_ID = "aa64cf4a4dc866f14c60ff86f5e0b67d";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    String location_provider = LocationManager.GPS_PROVIDER;
    LocationManager locationManager;
    LocationListener locationListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        weatherIcon = findViewById(R.id.weatherIcon);
        temp = findViewById(R.id.temperature);
        condition = findViewById(R.id.weatherCondition);
        city = findViewById(R.id.cityName);

        getWeather();


    }

    public void getWeather() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                Toast.makeText(MainActivity.this, Latitude, Toast.LENGTH_SHORT).show();
                RequestParams params = new RequestParams();
                params.put("lat", Latitude);
                params.put("lon", Longitude);
                params.put("appid", APP_ID);
                letsdoSomeNetworking(params);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
            }
        };


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        locationManager.requestLocationUpdates(location_provider, 5000, 1000, locationListener);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==101)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this,"Locationget Succesffully",Toast.LENGTH_SHORT).show();
                getWeather();
            }
            else
            {
                //user denied the permission
            }
        }
    }


    private void letsdoSomeNetworking(RequestParams params){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);

                Toast.makeText(MainActivity.this,"Data Get Success",Toast.LENGTH_SHORT).show();

                weatherData weatherD=weatherData.fromJson(response);
                updateUI(weatherD);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                condition.setText(errorResponse.toString());
            }
        });


    }

    private void updateUI(weatherData weather){

        temp.setText(weather.getmTemperature());
        city.setText(weather.getMcity());
        condition.setText(weather.getmWeatherType());
        //int resourceID=getResources().getIdentifier(weather.getMicon(),"drawable",getPackageName());
        weatherIcon.setImageResource(R.drawable.cloudy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager!=null)
        {
            locationManager.removeUpdates(locationListener);
        }
    }



}