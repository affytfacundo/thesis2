package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class Views extends AppCompatActivity {

    Button signout, ocrscanner;
    ImageButton driver, pedestrian;
    TextView user;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



        driver = findViewById(R.id.buttonDriver);
        signout = findViewById(R.id.signOutBtn);
        ocrscanner = findViewById(R.id.tryOCR);
        user = findViewById(R.id.emailUser);

        user.setText(firebaseUser.getEmail());

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDrive();
            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent3 = new Intent(Views.this, MainActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent3);

            }
        });

        ocrscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goScanner();
            }
        });


    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(Views.this);
    }

    private void goDrive(){
        Intent intent = new Intent(Views.this, DriverDetails.class);
        startActivity(intent);
    }


    private void goScanner(){
       // Intent intent4 = new Intent(Views.this, ticket.class);
      //  startActivity(intent4);
    }
}
