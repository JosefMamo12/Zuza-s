package com.example.myapplication.EditProfile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.HomePage;
import com.example.myapplication.MenuPage;
import com.example.myapplication.Profile;
import com.example.myapplication.R;
import com.example.myapplication.User;
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
import java.util.HashMap;
import java.util.UUID;

public class EditProfile extends AppCompatActivity implements OnClickListener {


    FirebaseAuth mAuth;
    FirebaseStorage firebaseStorage;

    EditText fullNameEditText;
    EditText ageEditText;
    EditText phoneNumberEditText;
    TextView changeProfilePic;
    ImageView logInImg, accountImg, arrow;
    CircularImageView profilePic;
    private TextView updateChanges;
    private ImageView shoppingCart;
    private ImageView managerReport;

    private FirebaseDatabase userDatabase;
    HashMap<Integer, String> myHash;
    String pName, pAge, pPhone;


    private Uri filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);
        myHash = new HashMap<>();
        firebaseStorage = FirebaseStorage.getInstance();
        navBarInitializer();
        editProfileInitializer();
        checkIfAdminConnected();
        MyListener listener = new MyListener() {
            @Override
            public void onDataLoaded() {
                fillViewOptions();
            }

            @Override
            public void onError(String message) {
                Log.d(message, message);
            }
        };
        EditProfileController.beforeUpdating(listener, myHash);
    }


    protected void fillViewOptions() {
        if (myHash.containsKey(0)) {
            fullNameEditText.setText(myHash.get(0));
            pName = myHash.get(0);
        }
        if (myHash.containsKey(1)) {
            ageEditText.setText(myHash.get(1));
            pAge = myHash.get(1);
        }
        if (myHash.containsKey(2)) {
            phoneNumberEditText.setText(myHash.get(2));
            pPhone = myHash.get(2);
        }
        if (myHash.containsKey(3)) {
            retrievePhotoFromStorage(myHash.get(3));
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_arrow:
            case R.id.accountImg:
                startActivity(new Intent(this, Profile.class));
                break;
            case R.id.menuFoodImg:
                startActivity(new Intent(this, MenuPage.class));
                break;
            case R.id.logOutImg:
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
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
            case R.id.update_changes:
                String fullName = fullNameEditText.getText().toString();
                String age = ageEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                if (EditProfileController.checkBeforeAfterProfile(pName, pAge, pPhone, fullName, age, phoneNumber)) {
                    Toast.makeText(this, "Nothing has been changed!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int returnedError = EditProfileController.updateProfile(fullName, age, phoneNumber);
                switch (returnedError) {
                    case 0:
                        if (filePath != null) {
                            uploadImage();
                            System.out.println("Upload image");
                        }
                        Toast.makeText(getApplicationContext(), "Private profile updated has been changed successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        fullNameEditText.setError("Full Name is required");
                        fullNameEditText.requestFocus();
                        break;
                    case 2:
                        ageEditText.setError("Not number exception");
                        ageEditText.requestFocus();
                        break;
                    case 3:
                        ageEditText.setError("Illegal number");
                        ageEditText.requestFocus();
                        break;
                    case 4:
                        ageEditText.setError("Age is required");
                        ageEditText.requestFocus();
                        break;
                    case 5:
                        phoneNumberEditText.setError("Phone number is required");
                        phoneNumberEditText.requestFocus();
                        break;
                    case 6:
                        phoneNumberEditText.setError("Only numbers required");
                        phoneNumberEditText.requestFocus();
                        break;
                    case 7:
                        phoneNumberEditText.setError("Phone number should be 10 digits");
                        phoneNumberEditText.requestFocus();
                        break;
                }
                break;
            case R.id.change_profile_picture:
                selectImage();
                break;
        }


    }

    public void navBarInitializer() {
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance();

        /* Image views of navBar*/
        ImageView homeImg = findViewById(R.id.homeImg);
        logInImg = findViewById(R.id.logInImg);
        accountImg = findViewById(R.id.accountImg);
        shoppingCart = findViewById(R.id.shop_cart);
        managerReport = findViewById(R.id.report);
        ImageView logOutImg = findViewById(R.id.logOutImg);
        ImageView menuFoodImg = findViewById(R.id.menuFoodImg);

        /* OnClick Listeners  */
        homeImg.setOnClickListener(this);
        logInImg.setOnClickListener(this);
        accountImg.setOnClickListener(this);
        logOutImg.setOnClickListener(this);
        menuFoodImg.setOnClickListener(this);
        managerReport.setOnClickListener(this);
        shoppingCart.setOnClickListener(this);

        checkIfConnected();
    }

    public void editProfileInitializer() {
        fullNameEditText = findViewById(R.id.edit_profile_f_name);
        ageEditText = findViewById(R.id.edit_profile_age);
        updateChanges = findViewById(R.id.update_changes);
        phoneNumberEditText = findViewById(R.id.edit_phone_number);
        profilePic = findViewById(R.id.edit_profile_photo);
        changeProfilePic = findViewById(R.id.change_profile_picture);
        arrow = findViewById(R.id.edit_arrow);

        updateChanges.setOnClickListener(this);
        arrow.setOnClickListener(this);
        changeProfilePic.setOnClickListener(this);
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

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        int PICK_IMAGE_REQUEST = 22;
        startActivityForResult(Intent.createChooser(intent, "Select Image from here ..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int PICK_IMAGE_REQUEST = 22;
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
    }

    private void retrievePhotoFromStorage(String name) {
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePic);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String name = "images/" + UUID.randomUUID().toString();
            StorageReference ref = firebaseStorage.getReference();
            StorageReference imageRef = ref.child(name);

            imageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference userRef = userDatabase.getReference("Users");
                    userRef.child(uid).child("url").setValue(name);
                    Toast.makeText(getApplicationContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


}




