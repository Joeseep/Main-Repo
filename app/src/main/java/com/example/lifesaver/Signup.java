package com.example.lifesaver;

import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

                    // Check if email already exists in the Firebase Database
                    mDatabase.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                mEmail.setError("Email already exists");
                                // Show a dialog to the user to reset or add a password
                                AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                                builder.setMessage("An account with this email already exists. Would you like to reset or add a password?")
                                        .setCancelable(false)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // Send password reset email and show toast
                                                mAuth.sendPasswordResetEmail(email)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(Signup.this, "A password reset link has been sent to your email address", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(Signup.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                                                                }
                                                                // Return to main activity
                                                                Intent intent = new Intent(Signup.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // Do nothing and return to signup activity
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            else {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    if (user != null) {
                                                        // Send email verification
                                                        user.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(Signup.this, "A verification email has been sent to your email. Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                                                                            // Redirect user to login activity
                                                                            startActivity(new Intent(Signup.this, MainActivity.class));
                                                                            finish();
                                                                        } else {
                                                                            // If sending email verification fails, display a message to the user.
                                                                            Toast.makeText(Signup.this, "Failed to send email verification. Please try again later.", Toast.LENGTH_SHORT).show();
                                                                            Log.d("TAG", task.getException().getMessage());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Toast.makeText(Signup.this, "Failed to create account. Please try again later.", Toast.LENGTH_SHORT).show();
                                                    Log.d("TAG", task.getException().getMessage());
                                                }
                                            }

                                        });
                                mAuth.signOut();
                                // Redirect user to login activity
                                startActivity(new Intent(Signup.this, MainActivity.class));
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database read error
                            Toast.makeText(Signup.this, "Error: " +error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
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

