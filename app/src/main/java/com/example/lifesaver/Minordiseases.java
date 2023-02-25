package com.example.lifesaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Minordiseases extends AppCompatActivity {
    RecyclerView recyclerView;
    List<DiseaseClass> diseaseClassList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minordiseases);

        recyclerView = findViewById(R.id.minorrecycleview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Minordiseases.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        diseaseClassList = new ArrayList<>();

        MyAdapter adapter = new MyAdapter(Minordiseases.this, diseaseClassList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Minor");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                diseaseClassList.clear();
                for(DataSnapshot itemsnapshot: snapshot.getChildren()){
                    DiseaseClass diseaseClass = itemsnapshot.getValue(DiseaseClass.class);
                    diseaseClassList.add(diseaseClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}