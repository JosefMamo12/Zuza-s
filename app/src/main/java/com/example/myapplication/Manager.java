package com.example.myapplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Manager extends User
{
    public Manager(String fullName, String age, String email) {
        super(fullName, age, email);
    }

}
