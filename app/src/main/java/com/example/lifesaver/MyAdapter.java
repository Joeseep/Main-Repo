package com.example.lifesaver;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<DiseaseClass> diseaseClassList;

    public MyAdapter(Context context, List<DiseaseClass> diseaseClassList) {
        this.context = context;
        this.diseaseClassList = diseaseClassList;
    }

    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent).getContext()).inflate(R.layout.minoritems, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(diseaseClassList.get(position).getImageURL()).into(holder.imageid);
        holder.minorname.setText(diseaseClassList.get(position).getName());
        holder.desc.setText(diseaseClassList.get(position).getDescription());

        holder.minoritems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AfterClickingDisease.class);
                intent.putExtra("Image", diseaseClassList.get(holder.getAdapterPosition()).getImageURL());
                intent.putExtra("Description", diseaseClassList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Name", diseaseClassList.get(holder.getAdapterPosition()).getName());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diseaseClassList.size();
    }
}
class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView imageid;
    TextView minorname, desc;
    CardView minoritems;

    public MyViewHolder(@NonNull View itemView){
        super(itemView);

        desc = itemView.findViewById(R.id.desc);
        imageid = itemView.findViewById(R.id.imageid);
        minorname = itemView.findViewById(R.id.minorname);
        minoritems = itemView.findViewById(R.id.minoritems);


    }
}