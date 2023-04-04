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

public class SelectTip extends AppCompatActivity {
    RecyclerView recyclerView;
    List<TipClass> TipClassList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    TipAdapter adapter;
    SearchView searchtip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tip);

        searchtip = findViewById(R.id.searchtip);
        searchtip.clearFocus();
        searchtip.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTips(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.tiprecycleview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SelectTip.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        TipClassList = new ArrayList<>();

        adapter = new TipAdapter(SelectTip.this, TipClassList);
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

    private void filterTips(String newText) {
        List<TipClass> filteredTips = new ArrayList<>();
        for(TipClass tips : TipClassList){
            if(tips.getTitle().toLowerCase().contains(newText.toLowerCase())){
                filteredTips.add(tips);
            }
        }
        if(filteredTips.isEmpty()){
            Toast.makeText(this, "Tips not found", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setFilteredTips(filteredTips);
        }
    }
}