package com.example.lifesaver;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class TipAdapter extends RecyclerView.Adapter<TipViewHolder> {
    private final Context context;
    private List<TipClass> TipClassList;

    public TipAdapter(Context context, List<TipClass> TipClassList) {
        this.context = context;
        this.TipClassList = TipClassList;
    }

    public void setFilteredTips(List<TipClass> filteredTips){
        this.TipClassList = filteredTips;
        notifyDataSetChanged();
    }
    @androidx.annotation.NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent).getContext()).inflate(R.layout.tipitems, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull TipViewHolder holder, int position) {

        Glide.with(context).load(TipClassList.get(position).getImage()).into(holder.image);
        holder.title.setText(TipClassList.get(position).getTitle());
        holder.description.setText(TipClassList.get(position).getDescription());
        holder.tipvideo.setVideoPath(TipClassList.get(position).getVideoTIP());
        holder.tiprefer.setText(TipClassList.get(position).getTipreference());
        holder.tipvideo.start();

        holder.tipitems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Tips.class);
                intent.putExtra("Image", TipClassList.get(holder.getAdapterPosition()).getImage());
                intent.putExtra("Description", TipClassList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Title", TipClassList.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("TipVideo", TipClassList.get(holder.getAdapterPosition()).getVideoTIP());
                intent.putExtra("Tipreference", TipClassList.get(holder.getAdapterPosition()).getTipreference());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return TipClassList.size();
    }
}

class TipViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView title, description, tiprefer;
    CardView tipitems;

    VideoView tipvideo;

    public TipViewHolder(@NonNull View itemView) {
        super(itemView);

        description = itemView.findViewById(R.id.description);
        image = itemView.findViewById(R.id.image);
        title = itemView.findViewById(R.id.title);
        tipitems = itemView.findViewById(R.id.tipitems);
        tipvideo = itemView.findViewById(R.id.tipvideo);
        tiprefer = itemView.findViewById(R.id.tiprefer);

    }
}