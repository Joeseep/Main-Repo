package com.example.lifesaver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class FoundationAdapter extends RecyclerView.Adapter<FoundationAdapter.ViewHolder> {

    private List<Foundation> foundationClassList;
    private Context context;

    public FoundationAdapter(Context context, List<Foundation> foundationClassList) {
        this.context = context;
        this.foundationClassList = foundationClassList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foundation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Foundation foundationClass = foundationClassList.get(position);
        if (TextUtils.isEmpty(foundationClass.getLogo())) {
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/athena-688cb.appspot.com/o/Foundation%20Logo%2Ffoundation_logo_placeholder.png?alt=media&token=e02b763f-19fc-4d94-838a-0cf4ca6eba50")
                    .into(holder.foundationImage);
        } else {
            Glide.with(context).load(foundationClass.getLogo()).into(holder.foundationImage);
        }

        holder.foundationName.setText(foundationClass.getName());
        holder.foundationGoal.setText(foundationClass.getGoal());
        holder.foundationContact.setText(foundationClass.getContact());
        holder.foundationEmail.setText(foundationClass.getEmail());
        holder.foundationAddress.setText(foundationClass.getAddress());
        holder.foundationWebsite.setText(foundationClass.getWebsite());

        holder.foundationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FoundationPage.class);
                intent.putExtra("logo", foundationClass.getLogo());
                intent.putExtra("name", foundationClass.getName());
                intent.putExtra("goal", foundationClass.getGoal());
                intent.putExtra("contact", foundationClass.getContact());
                intent.putExtra("email", foundationClass.getEmail());
                intent.putExtra("website", foundationClass.getWebsite());
                intent.putExtra("address", foundationClass.getAddress());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foundationClassList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView foundationImage;
        TextView foundationName, foundationGoal, foundationContact, foundationEmail, foundationWebsite, foundationAddress;
        CardView foundationItem;

        public ViewHolder(View itemView) {
            super(itemView);

            foundationImage = itemView.findViewById(R.id.foundation_image);
            foundationName = itemView.findViewById(R.id.foundation_name);
            foundationGoal = itemView.findViewById(R.id.foundation_goal);
            foundationContact = itemView.findViewById(R.id.foundation_contact);
            foundationEmail = itemView.findViewById(R.id.foundation_email);
            foundationWebsite = itemView.findViewById(R.id.foundation_website);
            foundationAddress = itemView.findViewById(R.id.foundation_address);
            foundationItem = itemView.findViewById(R.id.foundation_item);

        }
    }
}
