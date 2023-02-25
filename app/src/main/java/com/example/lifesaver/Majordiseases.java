package com.example.lifesaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Majordiseases extends AppCompatActivity {
    RecyclerView recyclerView;
    List<DiseaseClass> diseaseClassList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_majordiseases);

        recyclerView = findViewById(R.id.majorrecycleview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Majordiseases.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        diseaseClassList = new ArrayList<>();

        MyAdapter adapter = new MyAdapter(Majordiseases.this, diseaseClassList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Major");

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