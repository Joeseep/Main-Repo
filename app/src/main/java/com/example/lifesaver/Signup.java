package com.example.lifesaver;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Signup extends AppCompatActivity {

    private EditText mFullName, mEmail, mPassword, mConfirmPassword, mAddress, mPhoneNumber;
    private CheckBox mTerms;
    private Button mSignupButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Database instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get references to UI elements
        mFullName = findViewById(R.id.FullName);
        mEmail = findViewById(R.id.email_input);
        mPassword = findViewById(R.id.password_input);
        mConfirmPassword = findViewById(R.id.confirm_password_input);
        mAddress = findViewById(R.id.Address);
        mPhoneNumber = findViewById(R.id.PhoneNumber);
        mTerms = findViewById(R.id.Terms);
        mSignupButton = findViewById(R.id.login_button);

        // Set up click listener for Signup button
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTerms.isChecked()) {
                    String fullName = mFullName.getText().toString().trim();
                    String email = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    String confirmPassword = mConfirmPassword.getText().toString().trim();
                    String address = mAddress.getText().toString().trim();
                    String phoneNumber = mPhoneNumber.getText().toString().trim();

                    // Validate user input
                    if (TextUtils.isEmpty(fullName)) {
                        mFullName.setError("Full name is required");
                        return;
                    }
                    if (TextUtils.isEmpty(email)) {
                        mEmail.setError("Email is required");
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        mPassword.setError("Password is required");
                        return;
                    }
                    if (password.length() < 6) {
                        mPassword.setError("Password must be at least 6 characters");
                        return;
                    }
                    if (!password.equals(confirmPassword)) {
                        mConfirmPassword.setError("Passwords do not match");
                        return;
                    }
                    if (TextUtils.isEmpty(address)) {
                        mAddress.setError("Address is required");
                        return;
                    }
                    if (TextUtils.isEmpty(phoneNumber)) {
                        mPhoneNumber.setError("Phone number is required");
                        return;
                    }

                    // Create a new user account in Firebase Auth
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        // Save user details in Firebase Database
                                        String userId = Objects.requireNonNull(user).getUid();
                                        String defaultPicUrl = "https://firebasestorage.googleapis.com/v0/b/athena-688cb.appspot.com/o/User%20profiles%2Fno_profile.jpg?alt=media&token=2a443f47-ec72-42ef-9c2d-da8cfcf2563d";
                                        User newUser = new User(fullName, email, address, phoneNumber,defaultPicUrl);
                                        mDatabase.child("users").child(userId).child("profilePicUrl").setValue(defaultPicUrl);
                                        mDatabase.child("users").child(userId).setValue(newUser);
                                        // Redirect user to Home activity
                                        startActivity(new Intent(Signup.this, MainActivity.class));
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(Signup.this, "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        Log.d("TAG", errorMessage);
                                    }
                                }
                            });
                } else {
// Show an error message if checkbox is not checked
                    Toast.makeText(Signup.this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void openTermsAndConditionsUrl(View view) {
        String url = "https://www.termsfeed.com/live/3c2670be-853e-4f97-b70b-fa52b78ecf65";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}

