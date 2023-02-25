package com.example.lifesaver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class AfterClickingDisease extends AppCompatActivity {

    TextView descript, minortitle;
    ImageView minorimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_clicking_disease);

        descript = findViewById(R.id.descript);
        minortitle = findViewById(R.id.minortitle);
        minorimage = findViewById(R.id.minorimage);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            descript.setText(bundle.getString("Description"));
            minortitle.setText(bundle.getString("Name"));
            Glide.with(this).load(bundle.getString("Image")).into(minorimage);

        }

    }
}