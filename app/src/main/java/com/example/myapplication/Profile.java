package com.example.myapplication;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.myapplication.EditProfile.EditProfile;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

/**
 * Profile class,
 * content layout - profile_activity
 * this page created to see end user details.
 * the user can change his profile details, and change his profile picture.
 */

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;

    private TextView fullName, age, email, editProfile;
    private ImageView logInImg;
    private ImageView accountImg;
    private ImageView logOutImg;
    private ImageView menuFoodImage;
    private TextView phoneNumber;
    private ImageView managerReport;
    private ImageView shoppingCart;
    private FirebaseAuth mAuth;
    private CircularImageView profilePic;
    private FirebaseDatabase userDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        navBarInitializer();
        profilePageInitializer();
        checkIfAdminConnected();
        fillTheMissingDetails();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }

    private void checkIfAdminConnected() {
        if (mAuth.getCurrentUser() == null) {
            managerReport.setVisibility(View.INVISIBLE);
            shoppingCart.setVisibility(View.VISIBLE);
            return;
        }
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = userDatabase.getReference("Users");
        DatabaseReference childRef = userRef.child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (user.isAdmin()) {
                    managerReport.setVisibility(View.VISIBLE);
                    shoppingCart.setVisibility(View.INVISIBLE);
                } else {
                    managerReport.setVisibility(View.INVISIBLE);
                    shoppingCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        };
        childRef.addListenerForSingleValueEvent(valueEventListener);
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
                if (user.getPhoneNumber() != null) {
                    phoneNumber.setText(user.getPhoneNumber());
                }
                if (user.getUrl() != null) {
                    findViewById(R.id.add_profile_picture).setVisibility(View.INVISIBLE);
                    retrievePhotoFromStorage(user.getUrl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, error.getCode(), Toast.LENGTH_SHORT).show();

            }
        };
        uidRef.addListenerForSingleValueEvent(eventListener);
    }

    private void profilePageInitializer() {
        fullName = (TextView) findViewById(R.id.profile_full_name);
        age = (TextView) findViewById(R.id.profile_age);
        email = (TextView) findViewById(R.id.profile_mail);
        ImageView addImage = (ImageView) findViewById(R.id.add_profile_picture);
        ImageView arrow = (ImageView) findViewById(R.id.arrow);
        ImageView imageView = findViewById(R.id.imgView);
        profilePic = (CircularImageView) findViewById(R.id.profile_photo);
        editProfile = (TextView) findViewById(R.id.edit_profile);
        managerReport = (ImageView) findViewById(R.id.report);
        shoppingCart = (ImageView) findViewById(R.id.shop_cart);
        phoneNumber = (TextView) findViewById(R.id.phone_number);

        managerReport.setOnClickListener(this);
        shoppingCart.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        addImage.setOnClickListener(this);
        arrow.setOnClickListener(this);
    }

    public void navBarInitializer() {
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance();


        /* Image views of navBar*/
        ImageView homeImg = (ImageView) findViewById(R.id.homeImg);
        logInImg = (ImageView) findViewById(R.id.logInImg);
        accountImg = (ImageView) findViewById(R.id.accountImg);

        ImageView logOutImg = (ImageView) findViewById(R.id.logOutImg);
        ImageView menuFoodImg = (ImageView) findViewById(R.id.menuFoodImg);



        /* OnClick Listeners  */
        homeImg.setOnClickListener(this);
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
            case R.id.edit_profile:
                startActivity(new Intent(getApplicationContext(), EditProfile.class));
                break;
            case R.id.homeImg:
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                break;
            case R.id.add_profile_picture:
                selectImage();
                break;
        }

    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
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
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uploadImage();
    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String name = "images/" + UUID.randomUUID().toString();

            StorageReference ref = storageReference.child(name);

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference userRef = userDatabase.getReference("Users");
                    userRef.child(uid).child("url").setValue(name);
                    Toast.makeText(Profile.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Profile.this, "Falied: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Upload " + (int) progress + "%");
                }
            });
        }
    }

    private void retrievePhotoFromStorage(String name) {
        if (name == null || name.isEmpty())
            return;

        storageReference.child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePic);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
