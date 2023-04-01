package com.example.lifesaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Majordiseases extends AppCompatActivity {
    RecyclerView recyclerView;
    List<DiseaseClass> diseaseClassList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    SearchView search;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_majordiseases);

        search= findViewById(R.id.search);
        search.clearFocus();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterText(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.majorrecycleview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Majordiseases.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        diseaseClassList = new ArrayList<>();

        adapter = new MyAdapter(Majordiseases.this, diseaseClassList);
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

    private void filterText(String text) {
        List<DiseaseClass> filteredList = new ArrayList<>();
        for(DiseaseClass disease : diseaseClassList){
            if (disease.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(disease);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this, "Disease not found", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setFilteredList(filteredList);
        }
    }
}