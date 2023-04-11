package com.example.lifesaver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Recover extends AppCompatActivity {

    private EditText emailInput;
    private Button sendResetLinkButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);

        emailInput = findViewById(R.id.email_input);
        sendResetLinkButton = findViewById(R.id.send_reset_link_button);

        firebaseAuth = FirebaseAuth.getInstance();

        sendResetLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();
                if (email.isEmpty()) {
                    emailInput.setError("Please enter your email address");
                    emailInput.requestFocus();
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Recover.this, "Password reset link sent to your email", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Recover.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(Recover.this, "Failed to send password reset link. Please check your email address and try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}


