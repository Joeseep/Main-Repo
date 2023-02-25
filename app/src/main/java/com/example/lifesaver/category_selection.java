package com.example.lifesaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class category_selection extends AppCompatActivity {
    LinearLayout minorlayout;
    LinearLayout majorlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);
        minorlayout = findViewById(R.id.minorlayout);
        majorlayout = findViewById(R.id.majorlayout);

        majorlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(category_selection.this,Majordiseases.class);
                startActivity(intent);
            }
        });

        minorlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(category_selection.this,Minordiseases.class);
                startActivity(intent);
            }
        });
    }
}