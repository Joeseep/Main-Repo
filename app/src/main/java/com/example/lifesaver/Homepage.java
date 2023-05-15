package com.example.lifesaver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

public class Homepage extends AppCompatActivity {
    LinearLayout diseaselayout, homepagelogout, tipslayout, directorylayout, profile, glossary, volunteer, quiz;


    @Override
    public void onBackPressed() {
        recreate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        homepagelogout = findViewById(R.id.homepagelogout);
        diseaselayout = findViewById(R.id.diseaselayout);
        tipslayout = findViewById(R.id.tipslayout);
        directorylayout = findViewById(R.id.directorylayout);
        profile = findViewById(R.id.profile);
        glossary = findViewById(R.id.glossary);
        volunteer = findViewById(R.id.volunteer);
        quiz = findViewById(R.id.quiz);



        //para padung sa mga disease
        diseaselayout.setOnClickListener(view -> {
            Intent intent = new Intent(Homepage.this, category_selection.class);
            startActivity(intent);
        });
        //para sa logout
        homepagelogout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);
            builder.setMessage("Are you sure you want to log out?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                GoogleSignIn.getClient(Homepage.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
                Intent intent = new Intent(Homepage.this, MainActivity.class);
                startActivity(intent);
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        tipslayout.setOnClickListener(view -> {
            Intent intent = new Intent(Homepage.this, SelectTip.class);
            startActivity(intent);
        });
        directorylayout.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, Directory.class);
            startActivity(intent);
        });
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, Profile.class);
            startActivity(intent);
        });
        glossary.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, Glossary.class);
            startActivity(intent);
        });
        volunteer.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, Volunteer.class);
            startActivity(intent);
        });

        quiz.setOnClickListener(view -> {
            Intent intent = new Intent(Homepage.this, Quizselection.class);
            startActivity(intent);
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, AboutPage.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.privacy) {
            String url = "https://www.termsfeed.com/live/becbd1a4-ed32-4158-87ce-462edcc2182d";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.report) {
            Intent intent = new Intent(this, Report.class);
            startActivity(intent);
            return true;
        }else if (item.getItemId() == R.id.quick_location) {
            showLocationSharingDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Show the location sharing dialog
    private void showLocationSharingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share Location");
        builder.setMessage("Select the message you want to send:");
        builder.setPositiveButton("I need help! I am in an emergency.", (dialog, which) -> {
            // Share the location with the "I need help" message
            shareLocation("I need help! I am in an emergency.");
        });
        builder.setNegativeButton("Send help! there is an emergency in my location", (dialog, which) -> {
            // Share the location with the "Send help" message
            shareLocation("Send help! there is an emergency in my location");
        });
        builder.setNeutralButton("Cancel", (dialog,which)->{
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void shareLocation(String message) {
        // Get the user's current location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request location permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

            return;
        }


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Build the message with the user's location
                        String url = "https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude();
                        String shareMessage = message + "\n" + url;

                        // Share the message using a share intent
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "Share your location"));

                    } else {
                        // Location is null, handle the error
                        Toast.makeText(this, "Unable to get current location. Make sure location is turned on.", Toast.LENGTH_SHORT).show();
                    }
                });



    }


    // Handle the result of the location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, try to get the user's location again
                shareLocation("I need help. I am in an emergency.");
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }





}