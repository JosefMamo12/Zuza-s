package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private TextView fullName, age, email;
    private ImageView logInImg, accountImg, logOutImg, menuFoodImage, arrow, homeImg;
    private FirebaseAuth mAuth;
    private FirebaseDatabase userDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        navBarInitializer();
        fillTheMissingDetails();

    }

    private void fillTheMissingDetails() {
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = userDatabase.getReference("Users");
        DatabaseReference uidRef = userRef.child(uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (user.getFullName() != null) {
                    fullName.setText(user.getFullName());
                }
                if (user.getAge() != null) {
                    age.setText(user.getAge());
                }
                if (user.getEmail() != null) {
                    email.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, error.getCode(), Toast.LENGTH_SHORT).show();

            }
        };
        uidRef.addListenerForSingleValueEvent(eventListener);
    }

    public void navBarInitializer() {
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance();

        fullName = (TextView) findViewById(R.id.profile_full_name);
        age = (TextView) findViewById(R.id.profile_age);
        email = (TextView) findViewById(R.id.profile_mail);


        /* Image views of navBar*/
        homeImg = (ImageView) findViewById(R.id.homeImg);
        logInImg = (ImageView) findViewById(R.id.logInImg);
        accountImg = (ImageView) findViewById(R.id.accountImg);
        arrow = (ImageView) findViewById(R.id.arrow);
        ImageView logOutImg = (ImageView) findViewById(R.id.logOutImg);
        ImageView menuFoodImg = (ImageView) findViewById(R.id.menuFoodImg);



        /* OnClick Listeners  */
        homeImg.setOnClickListener(this);
        logInImg.setOnClickListener(this);
        accountImg.setOnClickListener(this);
        logOutImg.setOnClickListener(this);
        menuFoodImg.setOnClickListener(this);
        arrow.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.arrow:
                finish();
                break;
            case R.id.menuFoodImg:
                startActivity(new Intent(this, MenuPage.class));
                break;
            case R.id.logOutImg:
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                    checkIfConnected();
                    Toast.makeText(this, "User successfully logged out", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please login before logout", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.homeImg:
                startActivity(new Intent(getApplicationContext(), HomePage.class));

        }

    }
}
