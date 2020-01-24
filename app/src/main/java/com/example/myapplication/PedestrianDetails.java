package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PedestrianDetails extends AppCompatActivity implements LocationListener {

    EditText name, gender, address, timedate, vioTxt;
    Button upload, chooseViolations;
    TextView longitude, latitude;
    private LocationManager locationManager;
    private String provider;
    String[] listViolations;
    boolean[] checkedViolations;
    ArrayList<Integer> pedestrianViolation = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedestrian_details);

        longitude = findViewById(R.id.longitudeText);
        latitude = findViewById(R.id.latitudeText);


        name = findViewById(R.id.nametxt);
        gender = findViewById(R.id.gendertxt);
        address = findViewById(R.id.addresstxt);
        upload = findViewById(R.id.uploadBtn);
        timedate = findViewById(R.id.timeText);

        chooseViolations = findViewById(R.id.violationsBtn);
        vioTxt = findViewById(R.id.violationsHide);

        listViolations = getResources().getStringArray(R.array.pedestrianviolations);
        checkedViolations = new boolean[listViolations.length];

        chooseViolations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(PedestrianDetails.this);
                mBuilder.setTitle("Pick the violations of the pedestrian");
                mBuilder.setMultiChoiceItems(listViolations, checkedViolations, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            pedestrianViolation.add(position);
                        }
                        else{
                            pedestrianViolation.remove(Integer.valueOf(position));
                        }

                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < pedestrianViolation.size(); i++){
                            item = item + listViolations[pedestrianViolation.get(i)];
                            if(i != pedestrianViolation.size() -1){
                                item = item + ", ";
                            }
                        }
                        vioTxt.setText(item);
                    }
                });

                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0 ; i < checkedViolations.length; i++){
                            checkedViolations[i] = false;
                            pedestrianViolation.clear();
                            vioTxt.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        } else {
            latitude.setText("Location not available.");
            longitude.setText("Location not available");
        }
    }

    public void summarize (View v){
        Intent j = new Intent(PedestrianDetails.this, PedestrianSummary.class);
        String nameStr = name.getText().toString();
        String genderStr = gender.getText().toString();
        String addStr = address.getText().toString();
        String huli = vioTxt.getText().toString();
        String longi = longitude.getText().toString();
        String lati = latitude.getText().toString();
        j.putExtra("Name", nameStr);
        j.putExtra("Gender", genderStr);
        j.putExtra("Address", addStr);
        j.putExtra("Violations", huli);
        j.putExtra("Latitude", lati);
        j.putExtra("Longitude", longi);
        startActivity(j);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onLocationChanged(Location location){
        latitude.setText(" " + location.getLatitude());
        longitude.setText(" " + location.getLongitude());
        latitude.setVisibility(View.INVISIBLE);
        longitude.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }


}
