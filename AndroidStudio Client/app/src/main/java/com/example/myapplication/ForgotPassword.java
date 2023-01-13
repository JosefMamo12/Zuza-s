package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ForgotPassword class
 * Content layout - activity_forgot_password
 * If end user forgot his password - easy send to email method for easy password reset.
 */
public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    private EditText emailEditText;
    private ProgressBar progressBar;
    Retrofit retrofit;
    ServerAPI api;

    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailEditText = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar);
        retrofit = new Retrofit.Builder().baseUrl(ServerAPI.baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(ServerAPI.class);
        TextView banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        Button resetPasswordButton = findViewById(R.id.resetPassword);
        resetPasswordButton.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.resetPassword:
                resetPassword();
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));

        }

    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide valid email!!!");
            emailEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Call<Void> call =  api.forgotPassword(email);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.w("Forgot Password", "Successful");
                    Toast.makeText(ForgotPassword.this, "Please check your email", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Log.w("Forgot Password", "Failed");
                    Toast.makeText(ForgotPassword.this, "Try again please", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.w("Forgot Password", "Bad is it good that we are in here?");
            }
        });
    }
}
//        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(ForgotPassword.this, "Check your email to reset your password", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                    finish();
//                } else {
//                    Toast.makeText(ForgotPassword.this, "Try again! Something wrong happened", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
//
//
//    }
//}
