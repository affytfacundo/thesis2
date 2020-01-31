package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PedestrianSummary extends AppCompatActivity {

    private static final String KEY_NAME = "Name";
    private static final String KEY_GENDER = "Gender";
    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_VIOLATIONS = "Violations";
    private static final String KEY_LATITUDE = "Latitude";
    private static final String KEY_LONGITUDE = "Longitude";
    private static final String KEY_DATE = "Date & Time";
    private static final String KEY_CONTROL = "Control Number";

    TextView name, gender, address, longi, lati, control, violations, timedate;
    String Stringname, Stringgender, Stringaddress, Stringdate, Stringlongi, Stringlati, Stringviolations;
    /*long maxid=0;
    DatabaseReference reff;*/

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedestrian_summary);

        db = FirebaseFirestore.getInstance();
        /*reff = FirebaseDatabase.getInstance().getReference().child("pedestrians");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                maxid=(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/




        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd-yyyy-hh:mm:ss");
        String datetime = simpleDateFormat.format(calendar.getTime());

        name = findViewById(R.id.nameSum);
        gender = findViewById(R.id.genderSum);
        address = findViewById(R.id.addressSum);
        longi = findViewById(R.id.longSum);
        lati = findViewById(R.id.latSum);
        timedate = findViewById(R.id.datetimeSum);
        violations = findViewById(R.id.violationSum);
        control = findViewById(R.id.controlText);


        Stringname = getIntent().getExtras().getString("Name");
        name.setText(Stringname);
        Stringgender=getIntent().getExtras().getString("Gender");
        gender.setText(Stringgender);
        Stringaddress=getIntent().getExtras().getString("Address");
        address.setText(Stringaddress);
        Stringlati=getIntent().getExtras().getString("Latitude");
        lati.setText(Stringlati);
        Stringlongi=getIntent().getExtras().getString("Longitude");
        longi.setText(Stringlongi);
        Stringviolations=getIntent().getExtras().getString("Violations");
        violations.setText(Stringviolations);
        timedate.setText(datetime);
        Stringdate = timedate.getText().toString();

    }

    public void upload(View v){
        Map<String, Object> note = new HashMap<>();
        note.put(KEY_NAME, Stringname);
        note.put(KEY_GENDER, Stringgender);
        note.put(KEY_ADDRESS, Stringaddress);
        note.put(KEY_VIOLATIONS, Stringviolations);
        note.put(KEY_LATITUDE, Stringlati);
        note.put(KEY_LONGITUDE, Stringlongi);
        note.put(KEY_DATE, Stringdate);
        note.put(KEY_CONTROL, control);

      //  reff.child(String.valueOf(maxid+1)).setValue("pedestrians");


        db.collection("pedestrians").document().set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PedestrianSummary.this, "Uploaded", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PedestrianSummary.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
