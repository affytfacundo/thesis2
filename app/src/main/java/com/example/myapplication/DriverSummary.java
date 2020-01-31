package com.example.myapplication;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DriverSummary extends AppCompatActivity {

    private static final String KEY_NAME = "Name";
    private static final String KEY_GENDER = "Gender";
    private static final String KEY_LICENSE = "License";
    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_VIOLATIONS = "Violations";
    private static final String KEY_LATITUDE = "Latitude";
    private static final String KEY_LONGITUDE = "Longitude";
    private static final String KEY_DATE = "Date & Time";
    private static final String KEY_CONTROL = "Control Number";


    TextView name, gender, license, address, longi, lati, date, violations, control;
    String Stringname, Stringgender, Stringlicense, Stringaddress, Stringlongi, Stringlati, Stringdate, Stringviolations, Stringcontrol;
   /* long maxid=0;
    DatabaseReference reff;
    Member member;*/

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_summary);


        db = FirebaseFirestore.getInstance();

        //SMS


        //SMS

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd-yyyy-hh:mm:ss");
        String datetime = simpleDateFormat.format(calendar.getTime());




        //MMM-dd-yyyy-hh:mm:ss

        //yyyyMMddhhmmss control number

        name = findViewById(R.id.nameSum);
        gender = findViewById(R.id.genderSum);
        license = findViewById(R.id.licenseSum);
        address = findViewById(R.id.addressSum);
        longi = findViewById(R.id.longitudeSum);
        lati = findViewById(R.id.latitudeSum);
        date = findViewById(R.id.datetimeSum);
        violations = findViewById(R.id.violationSum);
        control = findViewById(R.id.ctrlSum);




        Stringname = getIntent().getExtras().getString("Name");
        name.setText(Stringname);
        Stringgender=getIntent().getExtras().getString("Gender");
        gender.setText(Stringgender);
        Stringlicense=getIntent().getExtras().getString("License No.");
        license.setText(Stringlicense);
        Stringaddress=getIntent().getExtras().getString("Address");
        address.setText(Stringaddress);
        Stringlati=getIntent().getExtras().getString("Latitude");
        lati.setText(Stringlati);
        Stringlongi=getIntent().getExtras().getString("Longitude");
        longi.setText(Stringlongi);
        Stringviolations=getIntent().getExtras().getString("Violations");
        violations.setText(Stringviolations);
        date.setText(datetime);
        Stringdate = date.getText().toString();


        String input = Stringlicense;     //input string
        String controlNo = Stringlicense;     //substring containing last 4 characters

        if (input.length() > 6)
        {
            controlNo = input.substring(input.length() - 6);
        }
        else
        {
            controlNo = input;
        }

        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddhhmm");
        String controlnumber = simpleDateFormat1.format(calendar.getTime()) + "-" + controlNo;

        control.setText(controlnumber);
        Stringcontrol = control.getText().toString();



    }
    public void upload(View v){


        Map<String, Object> note = new HashMap<>();
        note.put(KEY_NAME, Stringname);
        note.put(KEY_GENDER, Stringgender);
        note.put(KEY_LICENSE, Stringlicense);
        note.put(KEY_ADDRESS, Stringaddress);
        note.put(KEY_VIOLATIONS, Stringviolations);
        note.put(KEY_LATITUDE, Stringlati);
        note.put(KEY_LONGITUDE, Stringlongi);
        note.put(KEY_DATE, Stringdate);
        note.put(KEY_CONTROL, Stringcontrol);

       // reff.child(String.valueOf(maxid+1)).setValue(note);



        db.collection("drivers").document(    ).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DriverSummary.this, "Uploaded", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DriverSummary.this, "Error!", Toast.LENGTH_SHORT).show();

                    }
                });


    }

}
