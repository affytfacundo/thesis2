package com.example.myapplication;


import android.os.Bundle;
import android.telephony.SmsManager;
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
import java.sql.Driver;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DriverSummary extends AppCompatActivity {

    private static final String KEY_NAME = "name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_LICENSE = "licenseNumber";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_VIOLATIONS = "violations";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_DATE = "dateAndTime";
    private static final String KEY_CONTROL = "controlNumber";
    private static final String KEY_PLATE = "plateNumber";
    private static final String KEY_MAKE = "make";
    private static final String KEY_ENFORCER = "enforcer";
    private final static int SEND_SMS_PERMISSION_REQUEST_CODE = 143;


    TextView phoneNumb;
    TextView name, gender, license, address, longi, lati, date, violations, control, make, plate, enforcercheck;
    String Stringname, Stringgender, Stringlicense, Stringaddress, Stringlongi, Stringlati, Stringdate, Stringviolations, Stringcontrol, Stringmake, Stringplate, Stringenforcer;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_summary);


        db = FirebaseFirestore.getInstance();

        phoneNumb = findViewById(R.id.phoneNum);


        //SMS


        //SMS

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd-yyyy-hh:mm:ss");
        String datetime = simpleDateFormat.format(calendar.getTime());


        enforcercheck = findViewById(R.id.enforcecheck);
        name = findViewById(R.id.nameSum);
        gender = findViewById(R.id.genderSum);
        license = findViewById(R.id.licenseSum);
        address = findViewById(R.id.addressSum);
        longi = findViewById(R.id.longitudeSum);
        lati = findViewById(R.id.latitudeSum);
        date = findViewById(R.id.datetimeSum);
        violations = findViewById(R.id.violationSum);
        control = findViewById(R.id.ctrlSum);
        make = findViewById(R.id.makeSum);
        plate = findViewById(R.id.plateSum);




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
        Stringmake = getIntent().getExtras().getString("Make");
        make.setText(Stringmake);
        Stringplate = getIntent().getExtras().getString("Plate No.");
        plate.setText(Stringplate);
        Stringenforcer = getIntent().getExtras().getString("Enforcer");
        enforcercheck.setText(Stringenforcer);



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
        note.put(KEY_PLATE, Stringplate);
        note.put(KEY_MAKE, Stringmake);
        note.put(KEY_ENFORCER, Stringenforcer);

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
        sendTextViolations();
        sendTextControl();

    }

    public void sendTextViolations() {
        String number = phoneNumb.getText().toString();
        String sms = "You have been apprehended for the following:" + " " + Stringviolations;


        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, sms, null, null);
            Toast.makeText(DriverSummary.this, "Sent", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(DriverSummary.this, "Failed", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendTextControl() {
        String number = phoneNumb.getText().toString();
        String sms = "Your Control Number: " + Stringcontrol + "\n " + "Visit bit.ly/eCTTMO to review your violation.";


        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, sms, null, null);
            Toast.makeText(DriverSummary.this, "Sent", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(DriverSummary.this, "Failed", Toast.LENGTH_SHORT).show();
        }

    }
    }
