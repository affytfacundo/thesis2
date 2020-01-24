package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.example.myapplication.BaseActivity.SELECT_PHOTO;
import static com.example.myapplication.BaseActivity.WRITE_STORAGE;

public class ticket extends BaseActivity {

    private Button captureImageBtn, detectTextBtn;
    private ImageView imageView;
    private TextView textView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        captureImageBtn = findViewById(R.id.capture_image);
        detectTextBtn = findViewById(R.id.detect_text);
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

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);


            //Get full image instead of thumbnail
        /*if(requestCode == RESULT_OK){
            switch (requestCode){
                case WRITE_STORAGE:
                    checkPermission(requestCode);
                    break;

                case SELECT_PHOTO:
                    Uri dataUri = data.getData();
                    String path = MyHelper.getPath(ticket.this, dataUri);

                    if (path == null){
                        imageBitmap = MyHelper.resizePhoto(photo, ticket.this, dataUri, imageView);
                    }else {
                        imageBitmap = MyHelper.resizePhoto(photo, path, imageView);
                    }
                    if(imageBitmap != null) {
                        textView.setText(null);
                        imageView.setImageBitmap(imageBitmap);
                    }
                    break;
            }
        }*/
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
            List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();
            if (blocks.size() == 0) {
                Toast.makeText(ticket.this, "No text", Toast.LENGTH_SHORT).show();
                return;
            }
            for (FirebaseVisionText.TextBlock block : text.getTextBlocks()) {
                String txt = block.getText();
                textView.setText(txt);
            }


            //2nd
        /*textView.setText(null);
        if(text.getTextBlocks().size() == 0){
            Toast.makeText(ticket.this, "No text", Toast.LENGTH_SHORT).show();
            return;
        }
        for(FirebaseVisionText.TextBlock block : text.getTextBlocks()){
            textView.append(block.getText());*/
        }

    }




