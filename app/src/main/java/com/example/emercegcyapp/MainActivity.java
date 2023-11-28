package com.example.emercegcyapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private DBHandler dbHandler;
    private LocationManager lm;
    private static final int PERMISSION_GPS = 100;
    private static final long LOCATION_UPDATE_INTERVAL = 5000; // Update location every 5 seconds
    private static final String HOSPITAL_KEYWORD = "hospital";
    private float initialLatitude;
    private float initialLongitude;
    private ImageView b1;
    private Handler handler;
    private Button buttoncancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF8381")));

        buttoncancel=findViewById(R.id.buttonCancel);
        buttoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });


        b1 = findViewById(R.id.imageButton);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startLocationUpdates();
                SendToList();
            }
        });

        handler = new Handler();
    }

    private void startLocationUpdates() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS);
        } else {
            // If permission is already granted, request location updates
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, 0, this);
            Location initialLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (initialLocation != null) {
                dbHandler = new DBHandler(MainActivity.this);
                initialLatitude = (float) initialLocation.getLatitude();
                initialLongitude = (float) initialLocation.getLongitude();
                int id_user= getIntent().getIntExtra("id_user",0);
                Date currentDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String formattedDate = sdf.format(currentDate);
                String cas = getIntent().getStringExtra("cas");
                Log.d(TAG, "startLocationUpdates: "+cas);
                Emergcy em =new Emergcy( 0,id_user,formattedDate,initialLatitude,initialLongitude,cas);
                //Emergcy em =new Emergcy( 0,5,"12/50/2000",1,5,"emergcy");

                dbHandler.saveEmergcy(em);
                // You now have the initial latitude and longitude stored in initialLatitude and initialLongitude variables.
            }
            // Schedule the handler to periodically check for nearby hospitals and show the route
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRouteToNearestHospital();
                    handler.postDelayed(this, LOCATION_UPDATE_INTERVAL);
                }
            }, LOCATION_UPDATE_INTERVAL);
        }
    }
    private void showRouteToNearestHospital() {
        // Get the last known location
        Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastLocation != null) {
            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();

            // Find the nearest hospital
            LatLng nearestHospital = findNearestHospital(new LatLng(latitude, longitude));
            if (nearestHospital != null) {
                // Create a Uri with the coordinates for Google Maps
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + nearestHospital.latitude + "," + nearestHospital.longitude);
                // Create an Intent with the action view and the Uri
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                // Check if there is an app to handle the Intent before starting it
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        }
    }
    private LatLng findNearestHospital(LatLng currentLocation) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                String locality = addresses.get(0).getLocality();

                // Search for the nearest hospital in the current locality
                String hospitalQuery = HOSPITAL_KEYWORD + " in " + locality;
                List<Address> hospitals = geocoder.getFromLocationName(hospitalQuery, 1);

                if (hospitals != null && hospitals.size() > 0) {
                    return new LatLng(hospitals.get(0).getLatitude(), hospitals.get(0).getLongitude());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_GPS) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission refusée !", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Handle the updated location here, if needed
    }

    // Other methods for interface implementation
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove location updates and stop the handler when the activity is destroyed
        if (lm != null) {
            lm.removeUpdates(this);
        }
        handler.removeCallbacksAndMessages(null);
    }

    public void LogOut(){
        Intent intent = new Intent(MainActivity.this, Login.class);

        startActivity(intent);
    }
    public void SendToList(){
        Intent intent = new Intent(MainActivity.this, ListCard.class);
        int id_user= getIntent().getIntExtra("id_user",0);
        intent.putExtra("id_user",id_user);
        startActivity(intent);
    }
}
