package com.example.lifesaver;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Tips extends AppCompatActivity {
    DatabaseReference databaseReference;
    TextView des, title;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tips");

        des = findViewById(R.id.tip_des);
        title = findViewById(R.id.tip_title);
        image = findViewById(R.id.tip_image);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            des.setText(bundle.getString("Description"));
            title.setText(bundle.getString("Name"));
            Glide.with(this).load(bundle.getString("Image")).into(image);
        }
    }
}