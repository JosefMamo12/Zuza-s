package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements View.OnClickListener {


    private ImageSlider imageSlider;
    private ImageView logInImg, accountImg, logOutImg, menuFoodImg;
    FirebaseDatabase database;
    FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        navBarInitializer();


        imageSlider = (ImageSlider) findViewById(R.id.image_slider);
        ArrayList<SlideModel> images = new ArrayList<>();

        images.add(new SlideModel(R.drawable.a1, null));
        images.add(new SlideModel(R.drawable.a2, null));
        images.add(new SlideModel(R.drawable.a3, null));
        images.add(new SlideModel(R.drawable.a4, null));
        images.add(new SlideModel(R.drawable.a5, null));
        images.add(new SlideModel(R.drawable.a6, null));

        imageSlider.setImageList(images, ScaleTypes.FIT);
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
                startActivity(new Intent(this, MenuPage.class));
                break;
            case R.id.logOutImg:
                mAuth.signOut();
                checkIfConnected();
                Toast.makeText(this, "User successfully logged out", Toast.LENGTH_SHORT).show();
                System.out.println("Log out?");
                break;
            case R.id.logInImg:
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