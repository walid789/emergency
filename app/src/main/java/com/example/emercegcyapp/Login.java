package com.example.emercegcyapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emercegcyapp.DBHandler;
import com.example.emercegcyapp.R;
import com.example.emercegcyapp.SinUp;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    TextView buttonSinUp;
    private Button buttonLogin;
    private DBHandler dbHandler;
    private EditText user_nameEdt, passwordEdt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        buttonSinUp=findViewById(R.id.buttonSignup);
        buttonLogin=findViewById(R.id.buttonLogin);
        user_nameEdt =findViewById(R.id.editTextUsername);
        passwordEdt =findViewById(R.id.editTextPassword);
        dbHandler = new DBHandler(Login.this);



        buttonSinUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToSignUp();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authetification();
            }
        });
    }


    public void Authetification(){
        String user_name=user_nameEdt.getText().toString();
        String password=passwordEdt.getText().toString();
        int test= dbHandler.Auth(user_name,password);
        Log.d(TAG, "Authetification: " +test);
        if(test>0){

            Toast.makeText(Login.this, "Auth success for "+user_name, Toast.LENGTH_SHORT).show();

           Intent intent=new Intent(this,MainActivity.class);
           intent.putExtra("id_user",test);
           intent.putExtra("user_name",user_name);
            startActivity(intent);
        }
        if(test==-1){
            Intent intent=new Intent(this,AdminPanel.class);
            startActivity(intent);
        }
        if(test==0){
            Toast.makeText(Login.this, "password or username invalid", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendToSignUp(){
        Intent intent=new Intent(this, SinUp.class);
        startActivity(intent);
    }
}