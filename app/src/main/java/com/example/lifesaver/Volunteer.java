package com.example.lifesaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Volunteer extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Foundation> foundationList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    SearchView searchView;
    FoundationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.foundation_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Volunteer.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        foundationList = new ArrayList<>();

        adapter = new FoundationAdapter(Volunteer.this, foundationList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("volunteer");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foundationList.clear();
                for(DataSnapshot itemsnapshot: snapshot.getChildren()){
                    Foundation foundationClass = itemsnapshot.getValue(Foundation.class);
                    foundationList.add(foundationClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filter(String text) {
        List<Foundation> filteredList = new ArrayList<>();
        for(Foundation foundation : foundationList){
            if (foundation.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(foundation);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this, "Foundation not found", Toast.LENGTH_SHORT).show();
        }else{
            adapter = new FoundationAdapter(Volunteer.this, filteredList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(eventListener);
    }
}
