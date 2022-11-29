package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    private TextView register, forgotPassword;
    private EditText editTextUserName, editTextPassword;
    private ImageView fbBtn, twiterBtn, googleBtn;
    private Button login;
    private ProgressBar progressBar;

    CallbackManager callbackManager;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startActivity(new Intent(Login.this, HomePage.class));
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        firestore = FirebaseFirestore.getInstance();

        editTextUserName = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.pass_login);

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.forgotPass);
        forgotPassword.setOnClickListener(this);

        fbBtn = (ImageView) findViewById(R.id.facebook);
        fbBtn.setOnClickListener(this);

        googleBtn = (ImageView) findViewById(R.id.google);
        googleBtn.setOnClickListener(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.forgotPass:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
            case R.id.facebook:
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile"));
                break;
            case R.id.google:
                signIn();
                break;
            case R.id.login:
                signInWithMail();
                break;

        }

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    public void signInWithMail() {

        String mail = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (mail.isEmpty()) {
            editTextUserName.setError("Email is required");
            editTextUserName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            editTextUserName.setError("Please provide valid email");
            editTextUserName.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, HomePage.class));
                        Toast.makeText(Login.this, "Authentication Succeed.",Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(Login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }

                });
                progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(Login.this, HomePage.class);
        startActivity(intent);
    }
}