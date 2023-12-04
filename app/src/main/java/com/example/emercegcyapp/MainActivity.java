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
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity  {
    private DBHandler dbHandler;


    private ImageView b1;
    private Handler handler;
    private Button buttoncancel;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D74240")));
        String user_name=getIntent().getStringExtra("user_name");
        name=findViewById(R.id.textView2);
        name.setText(user_name);
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


    }

    public void LogOut(){
        Intent intent = new Intent(MainActivity.this, Login.class);

        startActivity(intent);
    }
    public void SendToList(){
        Intent intent = new Intent(MainActivity.this, ListCard.class);
        int id_user= getIntent().getIntExtra("id_user",0);
        String user_name=getIntent().getStringExtra("user_name");
        intent.putExtra("id_user",id_user);
        startActivity(intent);
    }
}
