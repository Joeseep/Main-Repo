package com.example.lifesaver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Directory extends AppCompatActivity {
    private static final int REQUEST_PHONE_CALL = 1;

    DatabaseReference databaseReference;
    TextView agency, contact;

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            LinearLayout linearLayout = findViewById(R.id.linear_layout);
            linearLayout.removeAllViews();
            Drawable phoneDrawable = getResources().getDrawable(R.drawable.ic_phone);


            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                String contactStr = childSnapshot.child("contact").getValue(String.class);
                String agencyStr = childSnapshot.child("agency").getValue(String.class);

                TextView agencyTextView = new TextView(Directory.this);
                agencyTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                agencyTextView.setText(agencyStr);
                agencyTextView.setTextSize(22);
                agencyTextView.setTypeface(null, Typeface.BOLD);

                TextView contactTextView = new TextView(Directory.this);
                contactTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                contactTextView.setText(String.valueOf(contactStr));
                contactTextView.setTextSize(20);
                contactTextView.setTextColor(getResources().getColor(R.color.blue));
                contactTextView.setCompoundDrawablesWithIntrinsicBounds(phoneDrawable, null, null, null);

                ImageView phoneImageView = new ImageView(Directory.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.image_width),
                        getResources().getDimensionPixelSize(R.dimen.image_height)
                );
                layoutParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.margin_small));
                phoneImageView.setLayoutParams(layoutParams);
                phoneImageView.setImageResource(R.drawable.ic_phone);

                LinearLayout contactLayout = new LinearLayout(Directory.this);
                contactLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                contactLayout.setOrientation(LinearLayout.HORIZONTAL);
                contactLayout.addView(contactTextView);
                contactLayout.addView(phoneImageView);

                linearLayout.addView(agencyTextView);
                linearLayout.addView(contactLayout);

                contactTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNumber = contactTextView.getText().toString().trim();

                        if (!TextUtils.isEmpty(phoneNumber)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                                } else {
                                    makePhoneCall(phoneNumber);
                                }
                            } else {
                                makePhoneCall(phoneNumber);
                            }
                        } else {
                            Toast.makeText(Directory.this, "Phone number not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        private void makePhoneCall(String phoneNumber) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (ActivityCompat.checkSelfPermission(Directory.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("Firebase", "Failed to read value.", databaseError.toException());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);
        agency = findViewById(R.id.agency);
        contact = findViewById(R.id.contact);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Directory");
        databaseReference.addValueEventListener(valueEventListener);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }
}
