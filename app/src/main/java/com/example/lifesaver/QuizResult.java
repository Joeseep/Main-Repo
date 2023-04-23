package com.example.lifesaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class QuizResult extends AppCompatActivity {
    TextView score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        score = findViewById(R.id.score);

        int correctanswer = getIntent().getIntExtra("correct", 0);
        score.setText(String.valueOf(correctanswer));


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(QuizResult.this, Quizselection.class));
        finish();
    }
}