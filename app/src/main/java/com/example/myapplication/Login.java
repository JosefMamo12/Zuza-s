package com.example.myapplication;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

/**
 * Login class
 * Content layout - activity_login
 * regular login activity with 4 login methods:
 * Facebook authentication, Google authentication, Twitter authentication and email + password authentication.
 * each one of the possible authentication are getting updated in our realtime Users database, in addition to firebase authentication.
 */
public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1000;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private TextView register, forgotPassword;
    private EditText editTextUserName, editTextPassword;
    private ImageView fbBtn, twiterBtn, googleBtn;
    private Button login;
    private ProgressBar progressBar;

    CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;
    LoginManager loginManager;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginIntializer();
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
                facebookSignIn();
                break;
            case R.id.google:
                signIn2();
                break;
            case R.id.twiter:
                twitterSignIn();
                break;
            case R.id.login:
                signInWithMail();
                break;

        }

    }

    private void LoginIntializer() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        callbackManager = CallbackManager.Factory.create();

        loginManager = LoginManager.getInstance();
        createRequest();

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

        twiterBtn = (ImageView) findViewById(R.id.twiter);
        twiterBtn.setOnClickListener(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    private void facebookSignIn() {
        loginManager.logInWithReadPermissions(Login.this, Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(@NonNull FacebookException exception) {
                        Toast.makeText(Login.this, "" + exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void twitterSignIn() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        provider.addCustomParameter("lang", "en");
        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(Login.this, "Authentication Succeed.", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
        } else {
            mAuth
                    .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    String uid = authResult.getUser().getUid();
                                    DatabaseReference usersReference = database.getReference("Users");
                                    DatabaseReference uidRef = usersReference.child(uid);
                                    ValueEventListener eventListener = uidRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.exists()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                User userToDatabase = new User();
                                                userToDatabase.setUid(user.getUid());
                                                userToDatabase.setEmail(user.getEmail());
                                                usersReference.child(user.getUid()).setValue(userToDatabase).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(Login.this, "Uploaded to the database", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    uidRef.addListenerForSingleValueEvent(eventListener);
                                    Toast.makeText(Login.this, "Authentication Succeed.", Toast.LENGTH_SHORT).

                                            show();

                                    navigateToSecondActivity();

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println(e.getMessage());
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
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
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Authentication Succeed.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        navigateToSecondActivity();


                    } else {
                        Toast.makeText(Login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                });


    }


    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        System.out.println(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(Login.this, "Authentication Succeed.",
                            Toast.LENGTH_SHORT).show();
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference userRef = database.getReference("Users");
                    DatabaseReference uidRef = userRef.child(uid);
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                User userToDatabase = new User();
                                userToDatabase.setUid(user.getUid());
                                userToDatabase.setEmail(user.getEmail());
                                userRef.child(user.getUid()).setValue(userToDatabase).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Login.this, "Uploaded to the database", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", error.getMessage());
                        }
                    };
                    navigateToSecondActivity();
                    uidRef.addListenerForSingleValueEvent(eventListener);
                } else {
                    Toast.makeText(Login.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount account = task.getResult();
                fireBaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void fireBaseAuthWithGoogle(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference userRef = database.getReference("Users");
                    DatabaseReference uidRef = userRef.child(uid);
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                User userToDatabase = new User();
                                userToDatabase.setUid(user.getUid());
                                userToDatabase.setEmail(user.getEmail());
                                userToDatabase.setFullName(user.getDisplayName());
                                userRef.child(user.getUid()).setValue(userToDatabase).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Login.this, "Uploaded to the database", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", error.getMessage());
                        }
                    };
                    uidRef.addListenerForSingleValueEvent(eventListener);
                }
                navigateToSecondActivity();
                Toast.makeText(Login.this, "Authentication Succeed.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void createRequest() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void signIn2() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                fireBaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            navigateToSecondActivity();
                        } else {
                            Toast.makeText(Login.this, "Sorry auth failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    private void navigateToSecondActivity() {
        Intent intent = new Intent(Login.this, HomePage.class);
        startActivity(intent);
    }
}