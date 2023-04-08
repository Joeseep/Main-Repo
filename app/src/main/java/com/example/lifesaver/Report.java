package com.example.lifesaver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Report extends AppCompatActivity implements View.OnClickListener {

    private Button btnReport, btnComment, btnSuggestion, btnSubmit;
    private EditText etMessage;
    private String category = "";
    private boolean hasSentMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            // Initialize views
            btnReport = findViewById(R.id.btn_report);
            btnComment = findViewById(R.id.btn_comment);
            btnSuggestion = findViewById(R.id.btn_suggestion);
            etMessage = findViewById(R.id.et_message);
            btnSubmit = findViewById(R.id.btn_submit);

            // Set click listeners
            btnReport.setOnClickListener(this);
            btnComment.setOnClickListener(this);
            btnSuggestion.setOnClickListener(this);
            btnSubmit.setOnClickListener(this);

            // Check if user has already sent a message this week
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            long lastMessageTime = preferences.getLong(userEmail + "_last_message_time", 0);
            if (lastMessageTime <= 0) {
                hasSentMessage = false;
            } else {
                long currentTime = System.currentTimeMillis();
                long weekInMillis = 7 * 24 * 60 * 60 * 1000;
                if (currentTime - lastMessageTime < weekInMillis) {
                    hasSentMessage = true;
                }
            }
        }
    }

    private void saveLastMessageTime() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            long currentTime = System.currentTimeMillis();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(userEmail + "_last_message_time", currentTime);
            editor.apply();
            hasSentMessage = true; // set hasSentMessage to true after saving the last message time
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_report) {
            category = "Report";
            btnReport.setBackgroundResource(R.color.light_green);
            btnComment.setBackgroundResource(R.color.gray);
            btnSuggestion.setBackgroundResource(R.color.gray);
        } else if (v.getId() == R.id.btn_comment) {
            category = "Comment";
            btnReport.setBackgroundResource(R.color.gray);
            btnComment.setBackgroundResource(R.color.light_green);
            btnSuggestion.setBackgroundResource(R.color.gray);
        } else if (v.getId() == R.id.btn_suggestion) {
            category = "Suggestion";
            btnReport.setBackgroundResource(R.color.gray);
            btnComment.setBackgroundResource(R.color.gray);
            btnSuggestion.setBackgroundResource(R.color.light_green);
        } else if (v.getId() == R.id.btn_submit) {
            if (!hasSentMessage) {
                String message = etMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (category.isEmpty()) {
                    Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendEmail(message);
                saveLastMessageTime();

                Toast.makeText(this, "Thank you for your " + category.toLowerCase() + "!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "You can only send one message per week", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendEmail(final String message) {
        // Set up mail properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.ssl.trust","smtp.gmail.com");

        // Set up mail session
        final Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("infin8.lifesaver@gmail.com", "ywzlbqytlaepqazn");
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();
        // Set up email message
        final String subject = category;
        final String body = "FROM: "+userEmail+"\n"+ message;
        final MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress("infin8.lifesaver@gmail.com"));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse("feedback.lifesaver@gmail.com"));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // Send email in a separate thread
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Transport.send(mimeMessage);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
        executor.shutdown();
    }




}
