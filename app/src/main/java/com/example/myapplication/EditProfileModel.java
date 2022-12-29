package com.example.myapplication;

import static com.example.myapplication.EditProfile.myHash;

import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;


public class EditProfileModel {



    private FirebaseAuth mAuth;
    private FirebaseDatabase userDatabase;
    DatabaseReference userRef;
    String uid;




    public int updateProfile(String username, String age, String phoneNumber) {
        userDatabase = FirebaseDatabase.getInstance();
        return userValidation(username, age, phoneNumber);

    }

    public EditProfileModel() {
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance();
        userRef = userDatabase.getReference("Users");
        uid = mAuth.getCurrentUser().getUid();

    }


    private int userValidation(String fullName, String age, String phoneNumber) {
        if (fullName.isEmpty()) {
            return 1;
        }
        if (age.isEmpty()) {
            return 4;
        }
        int ageNum;
        try {
            ageNum = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 2;
        }
        if (ageNum > 120 || ageNum <= 0)
            return 3;

        if (phoneNumber.isEmpty()) {
            return 5;
        }
        try {
            Integer.parseInt(phoneNumber);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return 6;
        }
        if (phoneNumber.length() != 10)
            return 7;
        passingAllValidation(fullName, age, phoneNumber);
        return 0;
    }

    private void passingAllValidation(String fullName, String age, String phoneNumber) {
        if (fullName != null) {
            userRef.child(uid).child("fullName").setValue(fullName);
        }
        if (age != null){
            userRef.child(uid).child("age").setValue(age);
        }
        if(phoneNumber != null){
            userRef.child(uid).child("phoneNumber").setValue(phoneNumber);
        }
    }


    public void getData(MyListener listener) {
        DatabaseReference uidRef = userRef.child(uid);
        uidRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.getFullName() != null) {
                        myHash.put(0, user.getFullName());
                    }
                    if (user.getAge() != null) {
                        myHash.put(1, user.getAge());
                    }
                    if (user.getPhoneNumber() != null) {
                        myHash.put(2, user.getPhoneNumber());
                    }
                    if (user.getUrl() != null) {
                        myHash.put(3, user.getUrl());
                    }
                    listener.onDataLoaded();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }

}





