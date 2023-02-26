package com.example.lifesaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout myDrawer;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDrawer = (DrawerLayout) findViewById(R.id.mainpagedrawer);
        NavigationView navigationView = findViewById(R.id.naviview);
        navigationView.setNavigationItemSelectedListener(this);




        myDrawer.addDrawerListener(toggle);


        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new Diseasefragment()).commit();
            navigationView.setCheckedItem(R.id.disease);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.disease:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new Diseasefragment()).commit();
                break;
            case R.id.signout:
                Toast.makeText(this,"Logout!",Toast.LENGTH_SHORT).show();
        }
        myDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(myDrawer.isDrawerOpen(GravityCompat.START)){
            myDrawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}