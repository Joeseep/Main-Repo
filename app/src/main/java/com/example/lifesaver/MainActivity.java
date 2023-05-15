package com.example.lifesaver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1996;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private static final String TAG = "MainActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = Objects.requireNonNull(user).getEmail();
                        String name = user.getDisplayName();
                        String profilePictureURL = "https://firebasestorage.googleapis.com/v0/b/athena-688cb.appspot.com/o/User%20profiles%2Fno_profile.jpg?alt=media&token=2a443f47-ec72-42ef-9c2d-da8cfcf2563d";

                        // Check if user already exists with this email address
                        mAuth.fetchSignInMethodsForEmail(Objects.requireNonNull(email)).addOnCompleteListener(task13 -> {
                            if (task13.isSuccessful()) {
                                SignInMethodQueryResult result = task13.getResult();
                                List<String> signInMethods = result.getSignInMethods();
                                if (signInMethods != null && signInMethods.contains(GoogleAuthProvider.PROVIDER_ID)) {
                                    // User already signed up with Google, log them in and proceed to home page
                                    Log.d(TAG, "User already signed up with Google, signing them in...");
                                    startActivity(new Intent(MainActivity.this, Homepage.class));
                                    finish();
                                } else {
                                    // User is not signed up with Google, check if they are already signed up with another method
                                    mAuth.createUserWithEmailAndPassword(email, "default_password").addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // User created successfully, proceed to home page
                                            User userObj = new User(name, email, "", " ", profilePictureURL);
                                            mDatabase.child("users").child(user.getUid()).setValue(userObj)
                                                    .addOnCompleteListener(task12 -> {
                                                        if (task12.isSuccessful()) {
                                                            Log.d(TAG, "Created new user account.");
                                                            startActivity(new Intent(MainActivity.this, Homepage.class));
                                                            finish();
                                                        } else {
                                                            Log.e(TAG, "Failed to create new user account.", task12.getException());
                                                        }
                                                    });
                                        } else {
                                            // User already exists, sign them in and proceed to home page
                                            mAuth.signInWithEmailAndPassword(email, "default_password").addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    Log.d(TAG, "User signed in successfully with email and password.");
                                                    startActivity(new Intent(MainActivity.this, Homepage.class));
                                                    finish();
                                                } else {
                                                    Log.e(TAG, "Failed to sign in user with email and password.", task2.getException());
                                                }
                                            });
                                        }
                                    });
                                }
                            } else {
                                Log.e(TAG, "Failed to fetch sign-in methods for email", task13.getException());
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to authenticate with Google.", task.getException());
                    }
                });
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = Objects.requireNonNull(user).getEmail();
                        String name = user.getDisplayName();

                        mAuth.fetchSignInMethodsForEmail(Objects.requireNonNull(email)).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                SignInMethodQueryResult result = task1.getResult();
                                List<String> signInMethods = result.getSignInMethods();
                                String profilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/athena-688cb.appspot.com/o/User%20profiles%2Fno_profile.jpg?alt=media&token=2a443f47-ec72-42ef-9c2d-da8cfcf2563d";

                                if (signInMethods != null) {
                                    if (signInMethods.contains(EmailAuthProvider.PROVIDER_ID)) {
                                        // User already signed up with email/password, sign them in
                                        startActivity(new Intent(MainActivity.this, Homepage.class));
                                        finish();
                                    } else if (signInMethods.contains(GoogleAuthProvider.PROVIDER_ID)) {
                                        // User already signed up with Google, sign them in
                                        startActivity(new Intent(MainActivity.this, Homepage.class));
                                        finish();
                                    } else if (signInMethods.contains(FacebookAuthProvider.PROVIDER_ID)) {
                                        // User already signed up with Facebook, sign them in
                                        startActivity(new Intent(MainActivity.this, Homepage.class));
                                        finish();
                                    } else {
                                        // User doesn't exist, create a new user object and save to Firebase Database
                                        User userObj = new User(name, email, "", " ", profilePictureUrl);
                                        mDatabase.child("users").child(user.getUid()).setValue(userObj)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d(TAG, "User profile is created successfully.");
                                                    // Start the homepage activity
                                                    Intent intent = new Intent(MainActivity.this, Homepage.class);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> Log.w(TAG, "Error adding user to Firebase", e));
                                    }
                                }
                            } else {
                                Log.e(TAG, "Failed to fetch sign-in methods for email", task1.getException());
                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        LoginManager.getInstance().logOut();
                    }
                });
    }

    private final ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle the Google Sign-In result
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    firebaseAuthWithGoogle(task.getResult());
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "signInResult:failed code=" + result.getResultCode());
                }
            });

    // Override the onActivityResult method to handle Facebook login results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Handle Google sign-in result
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            firebaseAuthWithGoogle(task.getResult());
        } else {
            // Handle Facebook login result
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        mEmailInput = findViewById(R.id.email_input);
        mPasswordInput = findViewById(R.id.password_input);
        Button mLoginButton = findViewById(R.id.login_button);
        TextView mCreateAccountText = findViewById(R.id.create_account_text);
        TextView mForgotPasswordText = findViewById(R.id.forgot_password);
        SignInButton mGoogleLoginButton = findViewById(R.id.google_login_button);
        LoginButton mFacebookLoginButton = findViewById(R.id.facebook_login_button);
        mCallbackManager = CallbackManager.Factory.create();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedInFacebook = accessToken != null && !accessToken.isExpired();

        if (currentUser != null && currentUser.isEmailVerified() || isLoggedInFacebook) {
            // User is already logged in and email is verified or logged in with Facebook, redirect to Home page
            Intent intent = new Intent(MainActivity.this, Homepage.class);
            startActivity(intent);
            finish();
        }
        else if (currentUser != null && !currentUser.isEmailVerified()) {
            // User is logged in but email is not verified, show a message to the user to verify their email
            Toast.makeText(MainActivity.this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
        }
        else {
                // User is not logged in, show the login screen
            Log.d(TAG, "User is not logged in");
        }



        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("62391194844-9joeem4ca9k647imbp7hc1dq2hjlcvd7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set click listener on the Google Sign-In button
        mGoogleLoginButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            mLauncher.launch(signInIntent);

        });

        // Initialize Facebook SDK
        FacebookSdk.setApplicationId("594527028810577");
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

// Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

// Initialize Firebase Realtime Database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

// Configure Facebook Login
        mFacebookLoginButton.setPermissions("email, public_profile");
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });




        mLoginButton.setOnClickListener(v -> {
            String email = mEmailInput.getText().toString();
            String password = mPasswordInput.getText().toString();

            if (email.isEmpty()) {
                mEmailInput.setError("Email is required.");
                mEmailInput.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailInput.setError("Please enter a valid email address.");
                mEmailInput.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                mPasswordInput.setError("Password is required.");
                mPasswordInput.requestFocus();
                return;
            }

            if (password.length() < 6) {
                mPasswordInput.setError("Password must be at least 6 characters long.");
                mPasswordInput.requestFocus();
                return;
            }

            // Authenticate user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Check if email is verified
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (Objects.requireNonNull(user).isEmailVerified()) {
                                // Sign in success, update UI with the signed-in user's information
                                startActivity(new Intent(MainActivity.this, Homepage.class));
                                finish();
                            } else {
                                // If email is not verified, display a message to the user.
                                Toast.makeText(MainActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });



        mCreateAccountText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Signup.class);
            startActivity(intent);
        });

        mForgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Recover.class);
            startActivity(intent);
        });

    }



}



