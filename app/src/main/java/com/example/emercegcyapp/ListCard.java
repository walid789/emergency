package com.example.emercegcyapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class ListCard extends AppCompatActivity  implements  LocationListener{
   private  ImageView v1,v2,v3,v4;
    private DBHandler dbHandler;
    private LocationManager lm;
    private static final int PERMISSION_GPS = 100;
    private static final long LOCATION_UPDATE_INTERVAL = 5000; // Update location every 5 seconds
    private static final String HOSPITAL_KEYWORD = "hospital";
    private float initialLatitude;
    private float initialLongitude;
    private ImageView b1;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_card);
        getSupportActionBar().hide();
        v1=findViewById(R.id.accident);
        v2=findViewById(R.id.chest);
        v3=findViewById(R.id.Breath);
        v4=findViewById(R.id.danger);
        handler = new Handler();
        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates("An accident");
            }
        });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates("Chest Pain");

            }
        });
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates("Breath lessness");

            }
        });
        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates("Unconcsiousness");

            }
        });
    }
    private void startLocationUpdates(String cas) {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS);
        } else {
            // If permission is already granted, request location updates
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, 0, this);
            Location initialLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (initialLocation != null) {
                dbHandler = new DBHandler(ListCard.this);
                initialLatitude = (float) initialLocation.getLatitude();
                initialLongitude = (float) initialLocation.getLongitude();
                int id_user= getIntent().getIntExtra("id_user",0);
                Date currentDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String formattedDate = sdf.format(currentDate);
                //String cas = getIntent().getStringExtra("cas");
                Log.d(TAG, "startLocationUpdates: "+cas);
                Emergcy em =new Emergcy( 0,id_user,formattedDate,initialLatitude,initialLongitude,cas);
                dbHandler.saveEmergcy(em);
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
                startLocationUpdates(" ");
            } else {
                Toast.makeText(this, "Permission refusée !", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void onLocationChanged(Location location) {
        // Handle the updated location here, if needed
    }

    // Other methods for interface implementation

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    public void onProviderEnabled(String provider) {
    }


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
    /*public void sendToMain(String cas ){
        Intent intent = new Intent(ListCard.this, MainActivity.class);
        int id_user= getIntent().getIntExtra("id_user",0);
        intent.putExtra("cas",cas);
        intent.putExtra("id_user",id_user);
        startActivity(intent);
    }*/
}