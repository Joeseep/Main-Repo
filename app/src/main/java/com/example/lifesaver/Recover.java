package com.example.lifesaver;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Recover extends AppCompatActivity {

    private EditText mEmailInput;
    private EditText mCodeInput;
    private EditText mNewPasswordInput;
    private Button mSendCodeButton;
    private Button mResetButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String mCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);

        mEmailInput = findViewById(R.id.email_input);
        mSendCodeButton = findViewById(R.id.send_code_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmailInput.getText().toString();

                mDatabase.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Generate a 6-digit code and send it to the user's email
                            mCode = generateCode();
                            sendCode(email, mCode);

                            Toast.makeText(Recover.this, "Code sent to " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Recover.this, "Email not found in database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Recover.this, "Database error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mCodeInput.getText().toString();
                String newPassword = mNewPasswordInput.getText().toString();

                if (code.equals(mCode)) {
                    // Reset the user's password
                    mAuth.confirmPasswordReset(code, newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Recover.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(Recover.this, "Password reset failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Recover.this, "Code is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String generateCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return Integer.toString(code);
    }

    private void sendCode(String email, String mCode) {
        // Generate a 6-digit random code
        String code = String.format("%06d", new Random().nextInt(999999));

        // Send password reset email with the code
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Password reset email sent successfully, show a toast message
                        Toast.makeText(Recover.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error sending password reset email, show error message
                        Toast.makeText(Recover.this, "Error sending password reset email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }}


