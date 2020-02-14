package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.myapplication.BaseActivity.SELECT_PHOTO;
import static com.example.myapplication.BaseActivity.WRITE_STORAGE;

public class ticket extends BaseActivity {

    private Button captureImageBtn, detectTextBtn, uploadImageBtn;
    private ImageView imageView;
    private TextView textView;
    static final int REQUEST_TAKE_PHOTO = 1;
    Bitmap imageBitmap;
    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        captureImageBtn = findViewById(R.id.capture_image);
        detectTextBtn = findViewById(R.id.detect_text);
        uploadImageBtn = findViewById(R.id.uploadimg);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_display);

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

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void dispatchTakePictureIntent() {
       /* Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/

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
                        "com.example.android.fileprovider",
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      /*  if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);*/


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
                        imageBitmap = MyHelper.resizePhoto(photo, this, dataUri, imageView);
                    } else {
                        imageBitmap = MyHelper.resizePhoto(photo, path, imageView);
                    }
                    if (imageBitmap != null) {
                        textView.setText(null);
                        imageView.setImageBitmap(imageBitmap);
                    }
                    break;

            }
        }
        }

    /*private void checkPermission(int requestCode) {
    }*/

        private void detectTextFromImage () {
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            detector.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText texts) {
                    processText(texts);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }

        private void processText (FirebaseVisionText text){

            //working first
            List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();
            if (blocks.size() == 0) {
                Toast.makeText(ticket.this, "No text", Toast.LENGTH_SHORT).show();
                return;
            }
            //working
            for (FirebaseVisionText.TextBlock block : text.getTextBlocks()) {
                String txt = block.getText();
                textView.setText(txt);
            }




        }



            //3rd
            /*List <FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();
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
        }*/





            //2nd
        /*textView.setText(null);
        if(text.getTextBlocks().size() == 0){
            Toast.makeText(ticket.this, "No text", Toast.LENGTH_SHORT).show();
            return;
        }
        for(FirebaseVisionText.TextBlock block : text.getTextBlocks()){
            textView.append(block.getText());*/
        }





