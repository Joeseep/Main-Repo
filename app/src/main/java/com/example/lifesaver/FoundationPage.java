package com.example.lifesaver;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoundationPage extends AppCompatActivity {

    private TextView foundationName;
    private ImageView foundationLogo;
    private TextView foundationAddress;
    private TextView foundationEmail;
    private TextView foundationContactNumber;
    private TextView foundationGoalContent;
    private TextView foundationWebsite;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(androidx.constraintlayout.widget.R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_foundation_page);

        // Initialize views
        foundationName = findViewById(R.id.foundation_name);
        foundationLogo = findViewById(R.id.foundation_logo);
        foundationAddress = findViewById(R.id.foundation_address);
        foundationEmail = findViewById(R.id.foundation_email);
        foundationContactNumber = findViewById(R.id.foundation_contact_number);
        foundationGoalContent = findViewById(R.id.foundation_goal_content);
        foundationWebsite = findViewById(R.id.foundation_website);

        // Get foundation name from intent
        String foundationNameString = getIntent().getStringExtra("name");

        // Set title as foundation name
        setTitle(foundationNameString);

        // Get database reference to volunteer node child
        databaseRef = FirebaseDatabase.getInstance().getReference().child("volunteer");

        // Query for the foundation with the given name
        Query foundationQuery = databaseRef.orderByChild("name").equalTo(foundationNameString);

        // Attach a value event listener to the query
        foundationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Loop through the results (should be only one)
                for (DataSnapshot foundationSnapshot : dataSnapshot.getChildren()) {
                    // Get foundation data
                    String foundationAddressString = foundationSnapshot.child("address").getValue(String.class);
                    String foundationEmailString = foundationSnapshot.child("email").getValue(String.class);
                    String foundationContactNumberString = foundationSnapshot.child("contact").getValue(String.class);
                    String foundationLogoString = foundationSnapshot.child("logo").getValue(String.class);
                    String foundationGoalContentString = foundationSnapshot.child("goal").getValue(String.class);
                    String foundationWebsiteString = foundationSnapshot.child("website").getValue(String.class);

                    // Set foundation data to views
                    foundationName.setText(foundationNameString);
                    foundationAddress.setText(foundationAddressString);
                    foundationEmail.setText(foundationEmailString);
                    foundationContactNumber.setText(foundationContactNumberString);
                    foundationGoalContent.setText(foundationGoalContentString);
                    foundationWebsite.setText(foundationWebsiteString);

                    // Load foundation logo from URL using Picasso library
                    if (foundationLogoString != null && !foundationLogoString.isEmpty()) {
                        Picasso.get().load(foundationLogoString).into(foundationLogo);
                    } else {
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/athena-688cb.appspot.com/o/Foundation%20Logo%2Ffoundation_logo_placeholder.png?alt=media&token=e02b763f-19fc-4d94-838a-0cf4ca6eba50").into(foundationLogo);
                    }

                    // Set click listeners for email, contact number, and website TextViews
                    foundationEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Open email app to compose email
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:" + foundationEmailString));
                            startActivity(Intent.createChooser(intent, "Send email"));
                        }
                    });
                    foundationAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create a Uri from the foundation's address
                            String mapQuery = "geo:0,0?q=" + Uri.encode(foundationAddressString);
                            Uri mapUri = Uri.parse(mapQuery);

                            // Create an Intent to open Google Maps
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });



                    foundationContactNumber.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Open phone app to call contact number
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + foundationContactNumberString));
                            startActivity(intent);
                        }
                    });

                    foundationWebsite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = foundationWebsiteString;
                            if (url.startsWith("https://") || url.startsWith("http://")) {
                                Uri uri = Uri.parse(url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }else{
                                Toast.makeText(FoundationPage.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FoundationPage", "loadFoundation:onCancelled", databaseError.toException());
            }
        });
    }
}
