package com.example.lifesaver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button mLoginButton;
    private TextView mCreateAccountText;
    private TextView mForgotPasswordText;
    private SignInButton mGoogleLoginButton;
    private LoginButton mFacebookLoginButton;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private static final String TAG = "MainActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String name = user.getDisplayName();

                            // Use the default profile picture URL
                            String profilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/athena-688cb.appspot.com/o/User%20profiles%2Fno_profile.jpg?alt=media&token=2a443f47-ec72-42ef-9c2d-da8cfcf2563d";

                            // Create a new User object with the retrieved information
                            User userObj = new User(name, email , " ", "", profilePictureUrl);

                            // Save the user object to Firebase Database
                            mDatabase.child("users").child(user.getUid()).setValue(userObj)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "User profile is created successfully.");
                                            // Start the homepage activity
                                            Intent intent = new Intent(MainActivity.this, Homepage.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Failed to create user profile.", e);
                                        }
                                    });
                        } else {
                            Log.e(TAG, "Failed to authenticate with Google.", task.getException());
                        }
                    }
                });
    }


    private final ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
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
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

        mAuth = FirebaseAuth.getInstance();
        mEmailInput = findViewById(R.id.email_input);
        mPasswordInput = findViewById(R.id.password_input);
        mLoginButton = findViewById(R.id.login_button);
        mCreateAccountText = findViewById(R.id.create_account_text);
        mForgotPasswordText = findViewById(R.id.forgot_password);
        mGoogleLoginButton = findViewById(R.id.google_login_button);
        mFacebookLoginButton = findViewById(R.id.facebook_login_button);
        mCallbackManager = CallbackManager.Factory.create();

        // Check if user is already logged in
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // User is already logged in, redirect to Home page
                Intent intent = new Intent(MainActivity.this, Homepage.class);
                startActivity(intent);
                finish();
            }


        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("62391194844-9joeem4ca9k647imbp7hc1dq2hjlcvd7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set click listener on the Google Sign-In button
        mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                mLauncher.launch(signInIntent);

            }
        });

        mFacebookLoginButton.setPermissions(Arrays.asList("public_profile","email"));
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Get the access token and make a Graph API request
                AccessToken accessToken = loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (object != null) {
                                // Get the user's name, email, and ID from the response
                                String fullName = object.getString("name");
                                String email = object.getString("email");
                                String id = object.getString("id");
                                Log.d(TAG, "Name: " + fullName);
                                Log.d(TAG, "Email: " + email);
                                Log.d(TAG, "ID: " + id);

                                // Add the user to the list of users in the database
                                User newUser = new User(fullName, email, "", "", "https://firebasestorage.googleapis.com/v0/b/athena-688cb.appspot.com/o/User%20profiles%2Fno_profile.jpg?alt=media&token=2a443f47-ec72-42ef-9c2d-da8cfcf2563d");
                                mDatabase.child("users").child(id).setValue(newUser);

                                // Start the Homepage activity
                                Intent intent = new Intent(MainActivity.this, Homepage.class);
                                startActivity(intent);

                            } else {
                                Log.d(TAG, "JSONObject is null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });

                // Set the parameters for the Graph API request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);

                // Execute the Graph API request asynchronously
                request.executeAsync();
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook login error: " + error.getMessage());
            }

        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(MainActivity.this, Homepage.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        mCreateAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });

        mForgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Recover.class);
                startActivity(intent);
            }
        });

    }
}

