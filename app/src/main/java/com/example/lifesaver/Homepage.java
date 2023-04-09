package com.example.lifesaver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class Homepage extends AppCompatActivity {
    LinearLayout diseaselayout, homepagelogout, tipslayout, directorylayout, profile, glossary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        homepagelogout = findViewById(R.id.homepagelogout);
        diseaselayout = findViewById(R.id.diseaselayout);
        tipslayout = findViewById(R.id.tipslayout);
        directorylayout = findViewById(R.id.directorylayout);
        profile = findViewById(R.id.profile);
        glossary = findViewById(R.id.glossary);


        //para padung sa mga disease
        diseaselayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Homepage.this, category_selection.class);
                startActivity(intent);
            }
        });
        //para sa logout
        homepagelogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                GoogleSignIn.getClient(Homepage.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
                Intent intent = new Intent(Homepage.this, MainActivity.class);
                startActivity(intent);
            }
        });
        tipslayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Homepage.this, SelectTip.class);
                startActivity(intent);
            }
        });
        directorylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, Directory.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, Profile.class);
                startActivity(intent);
            }
        });
        glossary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, Glossary.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, AboutPage.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.privacy) {
            String url = "https://www.termsfeed.com/live/becbd1a4-ed32-4158-87ce-462edcc2182d";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.report){
            Intent intent = new Intent(this, Report.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}