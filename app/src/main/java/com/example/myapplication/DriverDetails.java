package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DriverDetails extends BaseActivity implements LocationListener {

    EditText name, gender, license, address, timedate, vioTxt;
    Button upload, chooseViolations, resetinput;
    TextView longitude, latitude;
    private LocationManager locationManager;
    private String provider;
    String[] listViolations;
    boolean[] checkedViolations;
    ArrayList<Integer> driveViolation = new ArrayList<>();

    private ImageView myImageView;
    private TextView myTextView;
    private Button captureImageBtn, detectTextBtn;
    private Bitmap myBitmap;
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);


        captureImageBtn = findViewById(R.id.captureImgBtn);
        detectTextBtn = findViewById(R.id.detect_textBtn);
        myImageView = findViewById(R.id.image_view2);
        myTextView = findViewById(R.id.txt_display);



        longitude = findViewById(R.id.longitudeText);
        latitude = findViewById(R.id.latitudeText);


        name = findViewById(R.id.nametxt);
        gender = findViewById(R.id.gendertxt);
        license = findViewById(R.id.licensetxt);
        address = findViewById(R.id.addresstxt);
        upload = findViewById(R.id.uploadBtn);
        timedate = findViewById(R.id.timeText);
        resetinput = findViewById(R.id.resetFieldsBtn);


        chooseViolations = findViewById(R.id.violationsBtn);
        vioTxt = findViewById(R.id.violationsHide);

        listViolations = getResources().getStringArray(R.array.violations);
        checkedViolations = new boolean[listViolations.length];

        resetinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setText("");
                license.setText("");
                gender.setText("");
                address.setText("");
            }
        });

        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runTextRecog();
            }
        });


        chooseViolations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DriverDetails.this);
                mBuilder.setTitle("Pick the violations of the driver");
                mBuilder.setMultiChoiceItems(listViolations, checkedViolations, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            driveViolation.add(position);
                        }
                        else{
                            driveViolation.remove(Integer.valueOf(position));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                            String item = "";
                            for (int i = 0; i < driveViolation.size(); i++){
                                item = item + listViolations[driveViolation.get(i)];
                                if(i != driveViolation.size() -1){
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
                                driveViolation.clear();
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

        if (location != null) {
            onLocationChanged(location);
        } else {
            latitude.setText("Location not available.");
            longitude.setText("Location not available");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        galleryAddPic();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case WRITE_STORAGE:
                    checkPermission(requestCode);
                    break;
                case SELECT_PHOTO:
                    Uri dataUri = data.getData();
                    String path = MyHelper.getPath(this, dataUri);
                    if (path == null) {
                        myBitmap = MyHelper.resizePhoto(photo, this, dataUri, myImageView);
                    } else {
                        myBitmap = MyHelper.resizePhoto(photo, path, myImageView);
                    }
                    if (myBitmap != null) {
                        myTextView.setText(null);
                        myImageView.setImageBitmap(myBitmap);
                    }
                    break;

            }
        }
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.myapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File( Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        if(!storageDir.exists()){

            boolean s = new File(storageDir.getPath()).mkdirs();

            if(!s){
                Log.v("not", "not created");
            }
            else{
                Log.v("cr","directory created");
            }
        }
        else{
            Log.v("directory", "directory exists");
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        galleryAddPic();
        return image;

    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }




    private void runTextRecog() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(myBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText texts) {
                processTextExtract(texts);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure
                    (@NonNull Exception exception) {
                Toast.makeText(DriverDetails.this,
                        "Exception", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void processTextExtract(FirebaseVisionText firebaseVisionText){
        String inputOCR = null;
        name.setText(null);
        license.setText(null);
        myTextView.setText(null);
        if (firebaseVisionText.getTextBlocks().size() == 0) {
            myTextView.setText(R.string.no_text);
            return;
        }
        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
            myTextView.append(block.getText());
        }

        inputOCR = myTextView.getText().toString();
        //String[] list = inputOCR.split(" ");
        String[] list = inputOCR.split("(?=\\p{Space})");

       /* for(int i = 0; i < list.length; i++){
            Log.e("Values", "" + list[i]);
        }*/


        for(int i = 0; i < list.length; i++) {
            if (list[i].contains("Middle") || list[i].contains("Middie") || list[i].contains("Middile") || list[i].contains("Maddle") || list[i].contains("Hiddle") || list[i].contains("Midde")) {
                if (list[i + 1].contains("Name") || list[i + 1].contains("Mame") || list[i].contains("Namne")) {
                    String lastname = list[i + 2];
                    String firstname = list[i + 3];
                    String midname = list[i + 4];
                    String allname = lastname + firstname + midname;
                    allname = allname.replaceAll("\\B0|0\\B", "O");
                    name.setText(allname);
                } /*else {
                    String lastname = list[i + 1];
                    lastname.substring(3);

                    String firstname = list[i + 2];
                    String midname = list[i + 3];
                    name.setText(lastname + firstname + midname);
                }*/
            }

          /*  if (list[i].contains("Middie"))
                if (list[i + 1].contains("Name") || list[i+1].contains("Mame")){
                    String lastname = list[i + 2];
                    String firstname = list[i + 3];
                    String midname = list[i + 4];
                    name.setText(lastname + firstname + midname);
                }
                *//*else{
                    String trimlastname = list[i+1];
                    StringBuilder str = new StringBuilder(trimlastname);
                    str.delete(0, 4);
                    String firstname = list[i + 2];
                    String midname = list[i + 3];
                    name.setText(str + firstname + midname);


                }*//*

            if (list[i].contains("Middile"))
                if (list[i + 1].contains("Name") || list[i+1].contains("Mame")){
                    String lastname = list[i + 2];
                    String firstname = list[i + 3];
                    String midname = list[i + 4];
                    name.setText(lastname + firstname + midname);
                }
                *//*else{
                    String trimlastname = list[i+1];
                    StringBuilder str = new StringBuilder(trimlastname);
                    str.delete(0, 4);
                    String firstname = list[i + 2];
                    String midname = list[i + 3];
                    name.setText(str + firstname + midname);
                }*//*

            if (list[i].contains("Maddle"))
                if (list[i + 1].contains("Name") || list[i+1].contains("Mame")){
                    String lastname = list[i + 2];
                    String firstname = list[i + 3];
                    String midname = list[i + 4];
                    name.setText(lastname + firstname + midname);
                }
               *//* else{
                    String trimlastname = list[i+1];
                    StringBuilder str = new StringBuilder(trimlastname);
                    str.delete(0, 4);
                    String firstname = list[i + 2];
                    String midname = list[i + 3];
                    name.setText(str + firstname + midname);
                }*//*

            if (list[i].contains("Hiddle"))
                if (list[i + 1].contains("Name") || list[i+1].contains("Mame")){
                    String lastname = list[i + 2];
                    String firstname = list[i + 3];
                    String midname = list[i + 4];
                    name.setText(lastname + firstname + midname);
                }


            if (list[i].contains("Midde"))
                if (list[i + 1].contains("Name") || list[i+1].contains("Mame")){
                    String lastname = list[i + 2];
                    String firstname = list[i + 3];
                    String midname = list[i + 4];
                    name.setText(lastname + firstname + midname);
                }*/

           /* else{
                String trimlastname = list[i+1];
                StringBuilder str = new StringBuilder(trimlastname);
                str.delete(0, 4);
                String firstname = list[i + 2];
                String midname = list[i + 3];
                name.setText(str + firstname + midname);
            }*/

        }

        for(int i = 0; i < list.length; i++){
            if (list[i].contains("No.")){

                Log.e("Check", "" + list[i+1]);
                String licenseNum = list[i+1];
                   license.setText(licenseNum);
            }
            if(list[i].contains("No")){
                String licenseNum = list[i+1];
                license.setText(licenseNum);
            }
        }

/*
        for(int i =0; i < list.length; i++){
            if(list[i].contains("Address")){
                list[i]
            }
        }*/


        Log.e("char","" + myTextView.getText().toString());

    }




    public void summarize(View v){

        Intent i = new Intent(DriverDetails.this, DriverSummary.class);
        String nameStr = name.getText().toString();
        String genderStr = gender.getText().toString();
        String licenseStr = license.getText().toString();
        String addStr = address.getText().toString();
        String huli = vioTxt.getText().toString();
        String longi = longitude.getText().toString();
        String lati = latitude.getText().toString();
        i.putExtra("Name", nameStr);
        i.putExtra("Gender", genderStr);
        i.putExtra("License No.", licenseStr);
        i.putExtra("Address", addStr);
        i.putExtra("Violations", huli);
        i.putExtra("Latitude", lati);
        i.putExtra("Longitude", longi);
        startActivity(i);




    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
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
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onLocationChanged(Location location) {
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

