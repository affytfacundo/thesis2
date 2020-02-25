/*
package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ticket extends AppCompatActivity {

    private Button captureImageBtn, detectTextBtn, uploadImageBtn;
    private ImageView myImageView;
    private TextView myTextView;
    static final int REQUEST_TAKE_PHOTO = 1;
    Bitmap imageBitmap;
    String currentPhotoPath;
    private TextView nameocr, licenseocr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        captureImageBtn = findViewById(R.id.capture_image);
        detectTextBtn = findViewById(R.id.detect_text);
        myImageView = findViewById(R.id.image_view2);
        myTextView = findViewById(R.id.textView);

        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();
            }
        });


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
                imageFileName,
 prefix

                ".jpg",
 suffix

                storageDir
 directory

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
  if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);



            //Get full image instead of thumbnail
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
                        imageBitmap = MyHelper.resizePhoto(photo, this, dataUri, myImageView);
                    } else {
                        imageBitmap = MyHelper.resizePhoto(photo, path, myImageView);
                    }
                    if (imageBitmap != null) {
                        myTextView.setText(null);
                        myImageView.setImageBitmap(imageBitmap);
                    }
                    break;

            }
        }

        }

private void checkPermission(int requestCode) {
    }


        private void detectTextFromImage () {
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            detector.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText texts) {
                    processExtractedText(texts);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }

        private void processExtractedText (FirebaseVisionText firebaseVisionText){
            String inputOCR = null;
          //  licenseocr.setText("");
           // nameocr.setText("");
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

 for(int i = 0; i < list.length; i++){
            Log.e("Values", "" + list[i]);
        }



            for(int i = 0; i < list.length; i++){
                if (list[i].contains("Middle")){

                    Log.e("Check", "" + list[i+2]);
                    Log.e("Check", "" + list[i+3]);
                    Log.e("Check", "" + list[i+4]);
                    String lastname = list[i+2];
                    String firstname = list[i+3];
                    String midname = list[i+4];
              //      nameocr.setText(lastname + firstname + midname);
                }
            }


            for(int i = 0; i < list.length; i++){
                if (list[i].contains("No.")){

                    Log.e("Check", "" + list[i+1]);
                    String licenseNum = list[i+1];
               //     licenseocr.setText(licenseNum);
                }
            }

        }



            //3rd
List <FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();
            if (blocks.size() == 0) {
                Toast.makeText(ticket.this, "No text", Toast.LENGTH_SHORT).show();
                return;
            }
            String s="";
            for (int i = 0; i < blocks.size(); i++) {
                List lines = blocks.get(i).getLines();
                for (int j = 0; j < lines.size(); j++) {
                    List elements = lines.get(j).getElements();
                    for (int k = 0; k < elements.size(); k++) {
                        s +=elements.get(k).getText()+" ";
                    }
                }
            }
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }






            //2nd
textView.setText(null);
        if(text.getTextBlocks().size() == 0){
            Toast.makeText(ticket.this, "No text", Toast.LENGTH_SHORT).show();
            return;
        }
        for(FirebaseVisionText.TextBlock block : text.getTextBlocks()){
            textView.append(block.getText());

        }





*/
