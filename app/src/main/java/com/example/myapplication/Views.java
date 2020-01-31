package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class Views extends AppCompatActivity {

    Button signout, ocrscanner;
    ImageButton driver, pedestrian;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views);

        driver = findViewById(R.id.buttonDriver);
        signout = findViewById(R.id.signOutBtn);
        ocrscanner = findViewById(R.id.tryOCR);

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDrive();
            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        ocrscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goScanner();
            }
        });


    }

    private void goDrive(){
        Intent intent = new Intent(Views.this, DriverDetails.class);
        startActivity(intent);
    }


    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent3 = new Intent(Views.this, MainActivity.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent3);
        //try
    }

    private void goScanner(){
        Intent intent4 = new Intent(Views.this, ticket.class);
        startActivity(intent4);
    }
}
