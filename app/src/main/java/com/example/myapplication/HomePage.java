package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private ImageView logInImg;
    private ImageView accountImg;
    private ImageView shoppingCart;
    private ImageView managerReport;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        checkIfAdminConnected();
        checkIfConnected();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Download and cache all the data from menu items. (Shouldn't weigh too much)
        FirebaseDatabase.getInstance().getReference("menuItems").keepSynced(true);


        navBarInitializer();
        sliderIntializer();

    }

    private void sliderIntializer() {

        ImageSlider imageSlider = (ImageSlider) findViewById(R.id.image_slider);
        ArrayList<SlideModel> images = new ArrayList<>();

        images.add(new SlideModel(R.drawable.a1, null));
        images.add(new SlideModel(R.drawable.a2, null));
        images.add(new SlideModel(R.drawable.a3, null));
        images.add(new SlideModel(R.drawable.a4, null));
        images.add(new SlideModel(R.drawable.a5, null));
        images.add(new SlideModel(R.drawable.a6, null));

        imageSlider.setImageList(images, ScaleTypes.FIT);
    }


    private void checkIfAdminConnected() {

        if (mAuth.getCurrentUser() == null) {
            managerReport.setVisibility(View.INVISIBLE);
            shoppingCart.setVisibility(View.VISIBLE);
            return;
        }
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("Users");
        DatabaseReference childRef = userRef.child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user == null) return;
                if (user.getUid().equals(uid) && user.isAdmin()) {
                    managerReport.setVisibility(View.VISIBLE);
                    shoppingCart.setVisibility(View.INVISIBLE);
                } else {
                    managerReport.setVisibility(View.INVISIBLE);
                    shoppingCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        };
        childRef.addListenerForSingleValueEvent(valueEventListener);
    }


    public void navBarInitializer() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        /* Image views of navBar*/
        shoppingCart = (ImageView) findViewById(R.id.shop_cart);
        managerReport = (ImageView) findViewById(R.id.report);
        logInImg = (ImageView) findViewById(R.id.logInImg);
        accountImg = (ImageView) findViewById(R.id.accountImg);
        ImageView logOutImg = (ImageView) findViewById(R.id.logOutImg);
        ImageView menuFoodImg = (ImageView) findViewById(R.id.menuFoodImg);


        /* OnClick Listeners  */
        logInImg.setOnClickListener(this);
        accountImg.setOnClickListener(this);
        logOutImg.setOnClickListener(this);
        menuFoodImg.setOnClickListener(this);
        shoppingCart.setOnClickListener(this);
        managerReport.setOnClickListener(this);


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
            case R.id.shop_cart:
                if (mAuth.getCurrentUser() == null)
                {
                    Toast.makeText(this, "Please login to make a cart", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    finish();
                    startActivity(new Intent(this, CartPage.class));
                }
                break;
            case R.id.accountImg:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                break;
            case R.id.shop_cart:
                startActivity(new Intent(getApplicationContext(), ShoppingCartView.class));
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