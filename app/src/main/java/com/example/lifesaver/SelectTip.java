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

public class SelectTip extends AppCompatActivity {
    RecyclerView recyclerView;
    List<TipClass> TipClassList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tip);

        recyclerView = findViewById(R.id.tiprecycleview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SelectTip.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        TipClassList = new ArrayList<>();

        TipAdapter adapter = new TipAdapter(SelectTip.this, TipClassList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Tips");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TipClassList.clear();
                for(DataSnapshot itemsnapshot: snapshot.getChildren()){
                    TipClass tipClass = itemsnapshot.getValue(TipClass.class);
                    TipClassList.add(tipClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}