package com.example.lifesaver;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
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
        Button mSignupButton = findViewById(R.id.login_button);

        mAuth.getCurrentUser();


        // Set up click listener for Signup button
        mSignupButton.setOnClickListener(view -> {
            if (mTerms.isChecked()) {
                String fullName = mFullName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();
                String address = mAddress.getText().toString().trim();
                String phoneNumber = mPhoneNumber.getText().toString().trim();
                String profilePictureURL = "https://firebasestorage.googleapis.com/v0/b/athena-688cb.appspot.com/o/User%20profiles%2Fno_profile.jpg?alt=media&token=2a443f47-ec72-42ef-9c2d-da8cfcf2563d";

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
                // Check if email is already registered
                mDatabase.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Email already exists in database
                            AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                            builder.setTitle("User already exists!");
                            builder.setIcon(android.R.drawable.ic_dialog_alert);
                            builder.setMessage("Do you want to reset or add a password?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Send reset password link to the user's email
                                    mAuth.sendPasswordResetEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Signup.this, "Reset password link has been sent to your email. Please check your email.", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(Signup.this, "Failed to send reset password link. Please try again later.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Do nothing and close the dialog box
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            // Create account and send email verification
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                // Send email verification
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                // Email verification sent successfully
                                                                Toast.makeText(Signup.this, "A verification email has been sent to your email. Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                                                                User userobj = new User(fullName, email, address, phoneNumber, profilePictureURL);
                                                                // Save user data to database
                                                                mDatabase.child("users").child(user.getUid()).setValue(userobj);
                                                                Intent intent = new Intent(Signup.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(Signup.this, "Failed to send email verification. Please try again later.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(Signup.this, "Failed to create account. Please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                        Toast.makeText(Signup.this, "Failed to check email availability. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });


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


