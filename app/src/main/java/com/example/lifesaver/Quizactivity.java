package com.example.lifesaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Quizactivity extends AppCompatActivity {
    TextView topicname, timer, qcount, question;
    AppCompatButton optiona, optionb, optionc, nextbutton;

    Timer quiztimer;
    int totaltimemins = 1;
    int seconds = 0;
    List<Questionclass> questionlist;
    int currentquestionposition = 0;
    String selectedoptionbyuser = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizactivity);
        timer = findViewById(R.id.timer);
        topicname = findViewById(R.id.topicname);
        qcount = findViewById(R.id.qcount);
        question = findViewById(R.id.question);

        optiona = findViewById(R.id.optiona);
        optionb = findViewById(R.id.optionb);
        optionc = findViewById(R.id.optionc);
        nextbutton = findViewById(R.id.nextbutton);

        final String gettopic = getIntent().getStringExtra("selectedtopic");
        topicname.setText(gettopic);

        questionlist = QuestionBank.getquestions(gettopic);
        timer(timer);

        qcount.setText((currentquestionposition+1)+"/"+questionlist.size());
        question.setText(questionlist.get(0).getQuestion());
        optiona.setText(questionlist.get(0).getOption1());
        optionb.setText(questionlist.get(0).getOption2());
        optionc.setText(questionlist.get(0).getOption3());

        optiona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedoptionbyuser.isEmpty()){
                    selectedoptionbyuser=optiona.getText().toString();
                    optiona.setBackgroundResource(R.drawable.red);
                    optiona.setTextColor(Color.WHITE);

                    revealanswer();

                    questionlist.get(currentquestionposition).setSelectedanswer(selectedoptionbyuser);
                }
            }
        });

        optionb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedoptionbyuser.isEmpty()){
                    selectedoptionbyuser=optionb.getText().toString();
                    optionb.setBackgroundResource(R.drawable.red);
                    optionb.setTextColor(Color.WHITE);

                    revealanswer();

                    questionlist.get(currentquestionposition).setSelectedanswer(selectedoptionbyuser);
                }
            }
        });

        optionc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedoptionbyuser.isEmpty()){
                    selectedoptionbyuser=optionc.getText().toString();
                    optionc.setBackgroundResource(R.drawable.red);
                    optionc.setTextColor(Color.WHITE);

                    revealanswer();

                    questionlist.get(currentquestionposition).setSelectedanswer(selectedoptionbyuser);
                }
            }
        });

        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedoptionbyuser.isEmpty()){
                    Toast.makeText(Quizactivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                }
                else{
                    changenextquestion();
                }
            }
        });

    }
    private void changenextquestion(){
        currentquestionposition++;
        if((currentquestionposition+1)==questionlist.size()){
            nextbutton.setText("Submit Quiz");
        }
        if(currentquestionposition<questionlist.size()){
            selectedoptionbyuser="";
            optiona.setBackgroundResource(R.drawable.whitestroke);
            optiona.setTextColor(Color.parseColor("#1F6BB8"));

            optionb.setBackgroundResource(R.drawable.whitestroke);
            optionb.setTextColor(Color.parseColor("#1F6BB8"));

            optionc.setBackgroundResource(R.drawable.whitestroke);
            optionc.setTextColor(Color.parseColor("#1F6BB8"));

            qcount.setText((currentquestionposition+1)+"/"+questionlist.size());
            question.setText(questionlist.get(currentquestionposition).getQuestion());
            optiona.setText(questionlist.get(currentquestionposition).getOption1());
            optionb.setText(questionlist.get(currentquestionposition).getOption2());
            optionc.setText(questionlist.get(currentquestionposition).getOption3());
        }
        else{
            Intent intent = new Intent(Quizactivity.this, QuizResult.class);
            intent.putExtra("correct", getcorrectanswers());
            intent.putExtra("incorrect", getincorrectanswers());
            startActivity(intent);

            finish();
        }
    }
    private void timer(TextView timer){
        quiztimer = new Timer();
        quiztimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(seconds==0){
                    totaltimemins--;
                    seconds=59;
                }
                else if(seconds==0 && totaltimemins==0){
                    quiztimer.purge();
                    quiztimer.cancel();

                    Toast.makeText(Quizactivity.this, "Time's up", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Quizactivity.this, QuizResult.class);
                    intent.putExtra("correct", getcorrectanswers());
                    intent.putExtra("incorrect", getincorrectanswers());
                    startActivity(intent);
                    finish();
                }
                else{
                    seconds--;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String finalminutes = String.valueOf(totaltimemins);
                        String finalseconds = String.valueOf(seconds);

                        if(finalminutes.length()==1){
                            finalminutes = "0"+ finalminutes;
                        }
                        if(finalseconds.length()==1){
                            finalseconds = "0"+finalseconds;
                        }
                        timer.setText(finalminutes + ":"+finalseconds);
                    }
                });
            }
        },1000, 1000);
    }

    private int getcorrectanswers(){
        int correctanswers = 0;
        for(int i=0;i<questionlist.size();i++){
            String getselectedanswer = questionlist.get(i).getSelectedanswer();
            String getanswer = questionlist.get(i).getAnswer();

            if(getselectedanswer.equals(getanswer)){
                correctanswers++;
            }
        }
        return correctanswers;
    }

    private int getincorrectanswers(){
        int correctanswers = 0;
        for(int i=0;i<questionlist.size();i++){
            String getselectedanswer = questionlist.get(i).getSelectedanswer();
            String getanswer = questionlist.get(i).getAnswer();

            if(!getselectedanswer.equals(getanswer)){
                correctanswers++;
            }
        }
        return correctanswers;
    }

    @Override
    public void onBackPressed() {
        quiztimer.purge();
        quiztimer.cancel();

        startActivity(new Intent(Quizactivity.this, Quizselection.class));
    }

    private void revealanswer(){
        final String getAnswer = questionlist.get(currentquestionposition).getAnswer();
        if(optiona.getText().toString().equals(getAnswer)){
            optiona.setBackgroundResource(R.drawable.green);
            optiona.setTextColor(Color.WHITE);
        }
        else if(optionb.getText().toString().equals(getAnswer)){
            optionb.setBackgroundResource(R.drawable.green);
            optionb.setTextColor(Color.WHITE);
        }
        else if(optionc.getText().toString().equals(getAnswer)){
            optionc.setBackgroundResource(R.drawable.green);
            optionc.setTextColor(Color.WHITE);
        }
    }
}