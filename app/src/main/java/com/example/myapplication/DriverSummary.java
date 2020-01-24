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
    private static final String KEY_CONTROL = "Control Number";

    TextView name, gender, license, address, longi, lati, control, violations, timedate;
    String Stringname, Stringgender, Stringlicense, Stringaddress, Stringlongi, Stringlati, Stringcontrol, Stringviolations;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String controlNumber = simpleDateFormat.format(calendar.getTime());

    //    controlNumber = Integer.parseInt(datetime);



        name = findViewById(R.id.nameSum);
        gender = findViewById(R.id.genderSum);
        license = findViewById(R.id.licenseSum);
        address = findViewById(R.id.addressSum);
        longi = findViewById(R.id.longSum);
        lati = findViewById(R.id.latSum);
        control = findViewById(R.id.datetimeSum);
        violations = findViewById(R.id.violationSum);




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
        control.setText(controlNumber);
        Stringcontrol = control.getText().toString();



    }
    public void upload(View v){

       /* NexmoClient client = new NexmoClient.Builder()
                .apiKey("e25f2504")
                .apiSecret("c4rHgoelnf27mprX")
                .build();

        String messageText = "Hello from Nexmo charaaaan";
        TextMessage message = new TextMessage("Nexmo", "639369590417", messageText);

        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

        for (SmsSubmissionResponseMessage responseMessage : response.getMessages()) {
            System.out.println(responseMessage);
        }*/

        Map<String, Object> note = new HashMap<>();
        note.put(KEY_NAME, Stringname);
        note.put(KEY_GENDER, Stringgender);
        note.put(KEY_LICENSE, Stringlicense);
        note.put(KEY_ADDRESS, Stringaddress);
        note.put(KEY_VIOLATIONS, Stringviolations);
        note.put(KEY_LATITUDE, Stringlati);
        note.put(KEY_LONGITUDE, Stringlongi);
        note.put(KEY_CONTROL, Stringcontrol);

       // reff.child(String.valueOf(maxid+1)).setValue(note);



        db.collection("violators").document(    ).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
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
