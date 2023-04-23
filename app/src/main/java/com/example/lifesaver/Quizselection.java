package com.example.lifesaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Quizselection extends AppCompatActivity {
    LinearLayout diseaseselect, aidselect;
    private String selectedtopic = "";
    TextView startquiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizselection);


        diseaseselect = findViewById(R.id.diseaseselect);
        aidselect = findViewById(R.id.aidselect);
        startquiz = findViewById(R.id.startquiz);

        diseaseselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedtopic = "Disease";

                diseaseselect.setBackgroundResource(R.drawable.whitestroke);

                aidselect.setBackgroundResource(R.drawable.white10);
            }
        });

        aidselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedtopic = "First Aid";

                aidselect.setBackgroundResource(R.drawable.whitestroke);

                diseaseselect.setBackgroundResource(R.drawable.white10);
            }
        });

        startquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedtopic.isEmpty()){
                    Toast.makeText(Quizselection.this, "Please select a topic", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(Quizselection.this, Quizactivity.class);
                    intent.putExtra("selectedtopic", selectedtopic);
                    startActivity(intent);
                }

            }
        });
    }
}