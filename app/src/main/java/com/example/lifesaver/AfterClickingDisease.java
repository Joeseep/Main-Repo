package com.example.lifesaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AfterClickingDisease extends AppCompatActivity {
    DatabaseReference databaseReference;
    TextView descript, minortitle, cause, symptoms, firstaid;
    ImageView minorimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_clicking_disease);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Major");

        descript = findViewById(R.id.descript);
        minortitle = findViewById(R.id.minortitle);
        minorimage = findViewById(R.id.minorimage);
        cause = findViewById(R.id.cause);
        symptoms = findViewById(R.id.symptoms);
        firstaid = findViewById(R.id.firstaid);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            descript.setText(bundle.getString("Description"));
            minortitle.setText(bundle.getString("Name"));
            //causes
            String causeText = bundle.getString("Cause");
            String[] causeArray = causeText.split("\\*"); // split at each asterisk
            causeText = TextUtils.join("\n\n\u2022", causeArray).trim(); // join with newline character
            cause.setText(causeText);
            //symptoms
            String symptomText = bundle.getString("Symptom");
            String[] symptomArray = symptomText.split("\\*");
            symptomText = TextUtils.join("\n\n\u2022", symptomArray).trim();
            symptoms.setText(symptomText);
            //fistaid
            String firstaidText = bundle.getString("Firstaid");
            String[] firstaidArray = firstaidText.split("\\*");
            firstaidText = TextUtils.join("\n\n\u2022", firstaidArray).trim();
            firstaid.setText(firstaidText);
            Glide.with(this).load(bundle.getString("Image")).into(minorimage);
        }
    }
}