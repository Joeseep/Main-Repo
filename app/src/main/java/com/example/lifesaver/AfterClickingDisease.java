package com.example.lifesaver;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AfterClickingDisease extends AppCompatActivity {
    DatabaseReference databaseReference;
    TextView descript, minortitle, cause, symptoms, firstaid, prevention, referred;
    ImageView minorimage;

    VideoView videotips;


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
        prevention = findViewById(R.id.prevention);
        referred = findViewById(R.id.referred);
        videotips = findViewById(R.id.videotips);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            descript.setText(bundle.getString("Description"));
            minortitle.setText(bundle.getString("Name"));
            //referred.setText(bundle.getString("Reference"));

            String reference = bundle.getString("Reference");
            String[] referArray = reference.split("\\*");
            reference = TextUtils.join("\n", referArray).trim();
            referred.setText(reference);

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

            //prevention
            String preventionText = bundle.getString("Prevention");
            String[] preventionArray = preventionText.split("\\*");
            preventionText = TextUtils.join("\n\n\u2022", preventionArray).trim();
            prevention.setText(preventionText);
            Glide.with(this).load(bundle.getString("Image")).into(minorimage);

            String videoURL = bundle.getString("Video");
            if(!TextUtils.isEmpty(videoURL)){
                videotips.setVideoURI(Uri.parse(videoURL));
                //videotips.start();

                videotips.setMediaController(null);

                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videotips);
                videotips.setMediaController(mediaController);

                videotips.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaController.hide();
                    }
                });
            }
        }
    }
}