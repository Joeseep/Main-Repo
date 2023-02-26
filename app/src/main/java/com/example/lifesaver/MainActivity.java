package com.example.lifesaver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

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



    private ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = new Intent(MainActivity.this, MainPage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mEmailInput = findViewById(R.id.email_input);
        mPasswordInput = findViewById(R.id.password_input);
        mLoginButton = findViewById(R.id.login_button);
        mCreateAccountText = findViewById(R.id.create_account_text);
        mForgotPasswordText = findViewById(R.id.forgot_password);
        mGoogleLoginButton = findViewById(R.id.google_login_button);
        mFacebookLoginButton = findViewById(R.id.facebook_login_button);
        mCallbackManager = CallbackManager.Factory.create();


        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailInput.getText().toString();
                String password = mPasswordInput.getText().toString();

                if (email.isEmpty()) {
                    mEmailInput.setError("Email is required");
                    mEmailInput.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    mPasswordInput.setError("Password is required");
                    mPasswordInput.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, Homepage.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                                Log.d("TAG", errorMessage);
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

        // Set click listener on the Google Sign-In button
        mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                mLauncher.launch(signInIntent);

            }
        });
        // Initialize the Facebook login button
        mFacebookLoginButton = findViewById(R.id.facebook_login_button);
        mFacebookLoginButton.setPermissions(Arrays.asList("public_profile","email"));

// Initialize the CallbackManager
        mCallbackManager = CallbackManager.Factory.create();

// Register a callback for Facebook login
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // Get the access token and make a Graph API request
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            // Get the user's name and ID from the response
                            String name = object.getString("name");
                            String id = object.getString("id");
                            Log.d(TAG, "Name: " + name);
                            Log.d(TAG, "ID: " + id);

                            // Start the main activity
                            Intent intent = new Intent(MainActivity.this, MainPage.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Set the parameters for the Graph API request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name");
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
            protected void onActivityResult ( int requestCode, int resultCode, Intent data){
                onActivityResult(requestCode, resultCode, data);
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            }

        });

    }
}



