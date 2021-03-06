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
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SingleSpinner;
import com.androidbuts.multispinnerfilter.SingleSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public class DriverDetails extends BaseActivity implements LocationListener {

    EditText name, gender, license, addressText, timedate, vioTxt, plateNum;
    Spinner makeText;
    Button upload, chooseViolations, resetinput;
    TextView longitude, latitude, enforcer;
    private LocationManager locationManager;
    private String provider;
    String[] listViolations;
    boolean[] checkedViolations;
    ArrayList<Integer> driveViolation = new ArrayList<>();

    RadioGroup radioGroup;
    RadioButton radioButtonMale, radioButtonFemale;

    private ImageView myImageView;
    private TextView myTextView;
    private Button captureImageBtn, detectTextBtn;
    private Bitmap myBitmap;
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    String get = "";



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
        enforcer = findViewById(R.id.enforcerTxt);

        String enforcergrab = getIntent().getExtras().getString("Enforcer");
        enforcer.setText(enforcergrab);


        name = findViewById(R.id.nametxt);
        name.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        gender = findViewById(R.id.gendertxt);
        gender.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        license = findViewById(R.id.licensetxt);
        license.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        addressText = findViewById(R.id.addresstxt);
        addressText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        plateNum = findViewById(R.id.plateNumTxt);
        plateNum.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        makeText = findViewById(R.id.makeTxt);
        upload = findViewById(R.id.uploadBtn);
        timedate = findViewById(R.id.timeText);
        resetinput = findViewById(R.id.resetFieldsBtn);



        radioButtonMale = findViewById(R.id.maleRadio);
        radioButtonFemale = findViewById(R.id.femaleRadio);
        radioGroup = findViewById(R.id.radioGrp);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.maleRadio:
                        gender.setText("MALE");
                        break;

                    case R.id.femaleRadio:
                        gender.setText("FEMALE");
                        break;
                }

            }
        });


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
                addressText.setText("");
                plateNum.setText("");
                vioTxt.setText("");
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
                             vioTxt.setText(item.toUpperCase());
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources()
        .getStringArray(R.array.makeCars));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        makeText.setAdapter(adapter);


        /*final List<String> list2 = Arrays.asList(getResources().getStringArray(R.array.violations));
        final List<KeyPairBoolData> listArray0 = new ArrayList<>();

        for (int i = 0; i < list2.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list2.get(i));
            h.setSelected(false);
            listArray0.add(h);
        }

        final List<KeyPairBoolData> listArray1 = new ArrayList<>();

        for (int i = 0; i < list2.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list2.get(i));
            h.setSelected(false);
            listArray1.add(h);
        }

        final List<KeyPairBoolData> listArray2 = new ArrayList<>();

        for (int i = 0; i < list2.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list2.get(i));
            h.setSelected(false);
            listArray2.add(h);
        }
        final List<KeyPairBoolData> listArray3 = new ArrayList<>();

        for (int i = 0; i < list2.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list2.get(i));
            h.setSelected(false);
            listArray3.add(h);
        }

        MultiSpinnerSearch searchMultiSpinnerUnlimited = (MultiSpinnerSearch) findViewById(R.id.searchMultiSpinner);
        //MultiSpinnerSearch searchMultiSpinnerLimit = (MultiSpinnerSearch) findViewById(R.id.searchMultiSpinnerLimit);
       // SingleSpinnerSearch searchSingleSpinner = (SingleSpinnerSearch) findViewById(R.id.searchSingleSpinner);
        //SingleSpinner singleSpinner = (SingleSpinner) findViewById(R.id.singleSpinner);

        searchMultiSpinnerUnlimited.setEmptyTitle("No Data Found!");
        searchMultiSpinnerUnlimited.setSearchHint("Find Data");

        searchMultiSpinnerUnlimited.setItems(listArray0, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.e("wow", "" + items.get(i).getName() + " : " + items.get(i).isSelected());
                        vioTxt.append(items.get(i).getName().toUpperCase() + ", ");

                    }
                }
            }
        });*/





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
            Toast.makeText(DriverDetails.this,
                    "Location not available", Toast.LENGTH_LONG).show();
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

    private void processTextExtract(FirebaseVisionText firebaseVisionText) {
        String inputOCR = null;
        name.setText(null);
        license.setText(null);
        addressText.setText(null);
     //   gender.setText(null);
     //   plateNum.setText(null);

        myTextView.setText(null);
        if (firebaseVisionText.getTextBlocks().size() == 0) {
            myTextView.setText(R.string.no_text);
            return;
        }
        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
            myTextView.append(block.getText());
        }

        inputOCR = myTextView.getText().toString();
        String[] list = inputOCR.split("(?=\\p{Space})");


//CODE FOR EXTRACTING ADDRESS
      int addLoc = 0;
      int licLoc = 1;
      for(int i = 0; i < list.length; i++){
          if(list[i].contains("Address"))
              addLoc = i;
          else if(addLoc != 0 && list[i].contains("License")){
              licLoc = i;
              break;
          }
      }
      String[] result = new String[licLoc - addLoc - 1];
      for (int i = addLoc+1; i < licLoc; i++)
          result[i - addLoc - 1] = list[i];

      for(int i = 0; i < result.length; i++){
          addressText.append(result[i]);
      }

//END FOR ADDRESS

        //try for changing name code
        int midLoc = 0;
        int nationalityLoc = 1;
      for(int i = 0; i < list.length; i++){
          if (list[i].contains("Middle") || list[i].contains("Middie") || list[i].contains("Middile") || list[i].contains("Maddle") || list[i].contains("Hiddle") || list[i].contains("Midde"))
                  midLoc = i;

              else if (midLoc != 0 && list[i].contains("Nationality")) {
                  nationalityLoc = i;
                  break;
              }
      }

      String[] nameNew = new String[nationalityLoc - midLoc - 1];
      for(int i = midLoc + 1; i < nationalityLoc; i++)
          nameNew[i - midLoc - 1] = list[i];


      String replace2 = "";

      for(int i = 1; i < nameNew.length; i++){
          String tempName = null;
          tempName = nameNew[i];
          replace2 = replace2 + tempName;
      }


      replace2 = replace2.replaceAll("\\B0|0\\B", "O");
      name.setText(replace2.toUpperCase());

            //end try

//OLD CODE FOR NAME EXTRACT
 /*       for (int i = 0; i < list.length; i++) {
            if (list[i].contains("Middle") || list[i].contains("Middie") || list[i].contains("Middile") || list[i].contains("Maddle") || list[i].contains("Hiddle") || list[i].contains("Midde")) {
                if (list[i + 1].contains("Name") || list[i + 1].contains("Mame") || list[i + 1].contains("Namne")) {
                    String lastname = list[i + 2];
                    String firstname = list[i + 3];
                    String midname = list[i + 4];
                    String allname = lastname + firstname + midname;
                    allname = allname.replaceAll("\\B0|0\\B", "O");
                    name.setText(allname);
                } else {
                    String lastname = list[i + 1];
                    lastname.substring(3);

                    String firstname = list[i + 2];
                    String midname = list[i + 3];
                    name.setText(lastname + firstname + midname);
                }
            }
        }*/

// letter O to number zero : not sure if working ;; yup not working yet.
        for (int i = 0; i < list.length; i++) {
            if (list[i].contains("No.")) {
                String licenseNum = list[i + 1];
                licenseNum = licenseNum.replace('O', '0');
                licenseNum = licenseNum.replace('o', '0');
                license.setText(getSafeSubstring(licenseNum, 14));
            }
            if (list[i].contains("No")) {
                String licenseNum = list[i + 1];
                licenseNum = licenseNum.replace('O', '0');
                licenseNum = licenseNum.replace('o', '0');
                license.setText(getSafeSubstring(licenseNum, 14));
            }
        }

        Log.e("char","" + myTextView.getText().toString());

    }

    public String getSafeSubstring(String s, int maxLength){
        if(!TextUtils.isEmpty(s)){
            if(s.length() >= maxLength){
                return s.substring(0, maxLength);
            }
        }
        return s;
    }


    public void summarize(View v){

        Intent i = new Intent(DriverDetails.this, DriverSummary.class);
        String nameStr = name.getText().toString();
        String genderStr = gender.getText().toString();
        String licenseStr = license.getText().toString();
        String addStr = addressText.getText().toString();
        String huli = vioTxt.getText().toString();
        String longi = longitude.getText().toString();
        String lati = latitude.getText().toString();
        String plato = plateNum.getText().toString().toUpperCase();
        String mk = makeText.getSelectedItem().toString().toUpperCase();
        String enforcergrab2 = enforcer.getText().toString();

        i.putExtra("Name", nameStr);
        i.putExtra("Gender", genderStr);
        i.putExtra("License No.", licenseStr);
        i.putExtra("Address", addStr);
        i.putExtra("Violations", huli);
        i.putExtra("Latitude", lati);
        i.putExtra("Longitude", longi);
        i.putExtra("Plate No.", plato);
        i.putExtra("Make", mk);
        i.putExtra("Enforcer", enforcergrab2);
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

