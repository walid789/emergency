package com.example.emercegcyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminPanel extends AppCompatActivity {
    public static ArrayList<Emergcy> EmergcyList = new ArrayList<Emergcy>();
    public static ArrayList<User> UserList = new ArrayList<User>();
    private DBHandler dbHandler;
    private static final String CHANNEL_ID = "My_Channel_ID";
    private static final int NOTIFICATION_ID = 123;

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        getSupportActionBar().setTitle("The Emergcy List");
        dbHandler = new DBHandler(AdminPanel.this);
        setupData();
        setupList();

    }
    private void initiatePhoneCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "123456899"));
        startActivity(intent);
    }

    private void openGoogleMap(float latitude, float longitude) {
        // Specify the latitude and longitude of the location you want to show


        // Create a Uri with the location coordinates
        Uri gmmIntentUri = Uri.parse("geo:" + Float.toString(latitude) + "," +  Float.toString(longitude));

        // Create an Intent with the Uri to open Google Maps
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Specify the package for Google Maps

        // Verify if the Google Maps app is installed on the device
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            // Start the activity to open Google Maps
            startActivity(mapIntent);
        } else {
            // If Google Maps app is not installed, handle it here (e.g., show a message or open in a browser)
            // For example, you can open the location in a web browser using a WebView:
           startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q=" +Float.toString(latitude) + "," +Float.toString(longitude))));
        }
    }
    public void supprimer(int id){
        dbHandler.deleteEmergcy(id);
        setupData();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    public void setupData(){
        EmergcyList.clear();
        EmergcyList.addAll(dbHandler.readEmergcy());

    }
    public void setupList(){
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = sdf.format(currentDate);
        //newRow.setPadding(5, 5, 5, 5);
        for (Emergcy element : EmergcyList) {
            UserList.addAll(dbHandler.readUserById(element.getId_user()));

            if(formattedDate.equals(element.getDate())){
                Toast.makeText(AdminPanel.this,UserList.get(0).getUser_name()+"  " + element.toString(), Toast.LENGTH_SHORT).show();
            }


            TableRow newRow = new TableRow(this);
            // Set background color for the row
            int backgroundColor = Color.parseColor("#F0F7F7");
            newRow.setBackgroundColor(backgroundColor);
            newRow.setPadding(20, 20, 0, 20);

            // Create TextViews for the new row
            TextView text1 = new TextView(this);
            text1.setText(UserList.get(0).getUser_name());
            text1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.75f));

            TextView text2 = new TextView(this);
            text2.setText(element.getCas());
            text2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.25f));


            TextView text3 = new TextView(this);
            text3.setText(element.getDate());
            text3.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));


            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.phone);
            imageView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.75f));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiatePhoneCall(UserList.get(0).getPhone_number());
                }
            });

            ImageView map = new ImageView(this);
            map.setImageResource(R.drawable.map);
            map.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.75f));
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGoogleMap(element.getLatitude(),element.getLongitude());
                }
            });


            ImageView sup = new ImageView(this);
            sup.setImageResource(R.drawable.delete);
            sup.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.25f));
            sup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supprimer(element.getId());
                }
            });
            // Add TextViews to the new row
            newRow.addView(text1);
            newRow.addView(text2);
            newRow.addView(text3);
            newRow.addView(map);
            newRow.addView(sup);
            newRow.addView(imageView);

            // Add the new row to the TableLayout
            tableLayout.addView(newRow);
        }
    }


}