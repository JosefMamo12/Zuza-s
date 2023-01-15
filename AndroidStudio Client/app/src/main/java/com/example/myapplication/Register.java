package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Register class
 * for manually register with 4 user feature
 * Full Name, Age, Email and password.
 * this class feed the firebase authentication and the realtime Users database.
 */
public class Register extends AppCompatActivity implements View.OnClickListener {

    Retrofit retrofit;
    ServerAPI api;
    private FirebaseAuth mAuth;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        TextView banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        TextView registerUser = findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFullName = findViewById(R.id.fullName);
        editTextAge = findViewById(R.id.age);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressBar);
        retrofit = new Retrofit.Builder().baseUrl(ServerAPI.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(ServerAPI.class);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.registerUser:
                boolean isValid = userValidation();
                if (isValid) {
                    registerUserApi();
                    finish();
                }
//                registerUser();
                break;
        }

    }

    private void registerUserApi() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        MyRegisterRequest myRegisterRequest = new MyRegisterRequest(email, age, fullName, password);
        Call<Void> call = api.register(myRegisterRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Register.this, "Register succeed", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.w("Register", "Register Failed");
                    Toast.makeText(Register.this, "Register failed", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Register.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });

    }


//    private void registerUser() {
//        String email = editTextEmail.getText().toString().trim();
//        String password = editTextPassword.getText().toString().trim();
//        String age = editTextAge.getText().toString().trim();
//        String fullName = editTextFullName.getText().toString().trim();
//        boolean isValid = userValidation(email, password, age, fullName);
//        if (isValid) {
//            progressBar.setVisibility(View.VISIBLE);
//            mAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                User user = new User(fullName, age, email, mAuth.getCurrentUser().getUid());
//                                FirebaseDatabase.getInstance().getReference("Users")
//                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                        .setValue(user).addOnCompleteListener(task1 -> {
//                                            if (task1.isSuccessful()) {
//                                                Toast.makeText(Register.this, "User has been registered successfully", Toast.LENGTH_SHORT).show();
//                                                startActivity(new Intent(getApplicationContext(), HomePage.class));
//                                                progressBar.setVisibility(View.GONE);
//                                            } else {
//                                                Toast.makeText(Register.this, "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
//                                                progressBar.setVisibility(View.GONE);
//                                            }
//
//                                        });
//                            } else {
//                                Toast.makeText(Register.this, "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        }
//                    });
//        }
//    }

    private boolean userValidation() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();

        if (fullName.isEmpty()) {
            editTextFullName.setError("Full Name is required");
            editTextFullName.requestFocus();
            return false;
        }
        if (age.isEmpty()) {
            editTextAge.setError("Age is required");
            editTextAge.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return false;
        }
        try {
            if (Integer.parseInt(age) < 0) {
                editTextAge.setError("negative number is not an age");
                editTextAge.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            editTextAge.setError("an integer is required");
            editTextAge.requestFocus();
            return false;
        }

        return true;
    }
}