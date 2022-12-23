package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements View.OnClickListener {


    private ImageSlider imageSlider;
    private ImageView logInImg, accountImg, logOutImg, menuFoodImg, imageView;
    private Button btnSelect, btnUpload;
    private ArrayList<SlideModel> images;
    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 22;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        navBarInitializer();

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0F9D58"));

        btnSelect = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.imgView);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


        imageSlider = (ImageSlider) findViewById(R.id.image_slider);
        images = new ArrayList<>();

        images.add(new SlideModel(R.drawable.a1, null));
        images.add(new SlideModel(R.drawable.a2, null));
        images.add(new SlideModel(R.drawable.a3, null));
        images.add(new SlideModel(R.drawable.a4, null));
        images.add(new SlideModel(R.drawable.a5, null));
        images.add(new SlideModel(R.drawable.a6, null));


        imageSlider.setImageList(images, ScaleTypes.FIT);
    }

    private void uploadImage() {
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("IMAGE/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here ..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void navBarInitializer() {
        mAuth = FirebaseAuth.getInstance();
        /* Image views of navBar*/
        logInImg = (ImageView) findViewById(R.id.logInImg);
        accountImg = (ImageView) findViewById(R.id.accountImg);
        logOutImg = (ImageView) findViewById(R.id.logOutImg);
        menuFoodImg = (ImageView) findViewById(R.id.menuFoodImg);


        /* OnClick Listeners  */
        logInImg.setOnClickListener(this);
        accountImg.setOnClickListener(this);
        logOutImg.setOnClickListener(this);
        menuFoodImg.setOnClickListener(this);

        checkIfConnected();
    }

    private void checkIfConnected() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            logInImg.setVisibility(View.INVISIBLE);
            accountImg.setVisibility(View.VISIBLE);
        } else {
            logInImg.setVisibility(View.VISIBLE);
            accountImg.setVisibility(View.INVISIBLE);
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuFoodImg:
                finish();
                startActivity(new Intent(this, MenuPage.class));
                break;
            case R.id.logOutImg:
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    checkIfConnected();
                    Toast.makeText(this, "User successfully logged out", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please login before logout", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.logInImg:
                finish();
                startActivity(new Intent(this, Login.class));
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void openLocation(View view) {
        double latitude = 32.71438650765835, longitude = 35.56366019755522;
        String name = "ZUZA";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=" + latitude + "," + longitude + "(" + name + ")"));
        startActivity(intent);
    }
}
//32.71438650765835, 35.56366019755522