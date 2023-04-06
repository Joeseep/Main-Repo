package com.example.lifesaver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.api.client.util.ExponentialBackOff;



import java.net.PasswordAuthentication;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Report extends AppCompatActivity implements View.OnClickListener {

    private Button btnReport, btnComment, btnSuggestion, btnSubmit;
    private EditText etMessage;
    private String category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_report:
                category = "Report";
                btnReport.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                btnComment.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                btnSuggestion.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                break;
            case R.id.btn_comment:
                category = "Comment";
                btnReport.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                btnComment.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                btnSuggestion.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                break;
            case R.id.btn_suggestion:
                category = "Suggestion";
                btnReport.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                btnComment.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                btnSuggestion.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                break;
            case R.id.btn_submit:
                String message = etMessage.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(this, "Please enter your message", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (category.isEmpty()) {
                    Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }
                String fromEmail = "infin8.lifesaver@gmail.com";
                sendEmail(fromEmail,category, message);
                etMessage.setText("");
                category = "";
                btnReport.setBackgroundColor(getResources().getColor(R.color.gray));
                btnComment.setBackgroundColor(getResources().getColor(R.color.gray));
                btnSuggestion.setBackgroundColor(getResources().getColor(R.color.gray));
                Toast.makeText(this, "Thank you for your message. It will be treated with importance and confidentiality.", Toast.LENGTH_LONG).show();
                break;

        }
    }



    private void sendEmail(String fromEmailAddress, String category, String message) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long lastSentTime = sharedPreferences.getLong(fromEmailAddress + "_last_sent_time", 0);
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastSentTime;
        long weekInMillis = 7 * 24 * 60 * 60 * 1000;

        if (timeDiff < weekInMillis) {
            long remainingTime = weekInMillis - timeDiff;
            long days = TimeUnit.MILLISECONDS.toDays(remainingTime);
            long hours = TimeUnit.MILLISECONDS.toHours(remainingTime) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60;

            String remainingTimeString = String.format(Locale.getDefault(), "%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);

            Toast.makeText(this, "You can only send one email per week. Please try again in " + remainingTimeString, Toast.LENGTH_LONG).show();
            return;
        }

        // If the user is allowed to send an email, update the last sent time
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(fromEmailAddress + "_last_sent_time", currentTime);
        editor.apply();

        new Thread(() -> {
            try {
                String host = "smtp.gmail.com";
                String username = "infin8.lifesaver@gmail.com";
                String password = "inifin8LS.com";
                int port = 465;

                Properties properties = new Properties();
                properties.put("mail.smtp.host", host);
                properties.put("mail.smtp.port", port);
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.socketFactory.port", port);
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });

                Message email = new MimeMessage(session);
                email.setFrom(new InternetAddress(fromEmailAddress));
                email.setRecipients(Message.RecipientType.TO, InternetAddress.parse("infin8.lifesaver@gmail.com"));
                email.setSubject(category);
                email.setText(message);

                Transport.send(email);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }



}
