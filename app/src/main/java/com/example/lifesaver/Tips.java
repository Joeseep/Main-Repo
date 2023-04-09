package com.example.lifesaver;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Tips extends AppCompatActivity {
    DatabaseReference databaseReference;
    TextView des, title;
    ImageView image;
    VideoView videotip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tips");

        des = findViewById(R.id.tip_des);
        title = findViewById(R.id.tip_title);
        image = findViewById(R.id.tip_image);
        videotip = findViewById(R.id.videotip);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            des.setText(bundle.getString("Description"));
            String destext = bundle.getString("Description");
            String[] causeArray = destext.split("\\*"); // split at each asterisk
            destext = TextUtils.join("\n\n\u2022", causeArray).trim(); // join with newline character
            des.setText(destext);

            title.setText(bundle.getString("Title"));
            Glide.with(this).load(bundle.getString("Image")).into(image);

            String videoTIP = bundle.getString("TipVideo");
            if(!TextUtils.isEmpty(videoTIP)){
                videotip.setVideoURI(Uri.parse(videoTIP));
                videotip.setMediaController(null);
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videotip);
                videotip.setMediaController(mediaController);

                videotip.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaController.hide();
                    }
                });

            }
        }
    }
}