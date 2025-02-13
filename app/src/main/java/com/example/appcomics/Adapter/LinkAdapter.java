package com.example.appcomics.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appcomics.Model.Links;
import com.example.appcomics.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.LinkViewHolder> {
    private List<String> imageUrls;
    public LinkAdapter(List<String> imageUrls){
        this.imageUrls = imageUrls;
    }
    @NonNull
    @Override
    public LinkAdapter.LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail,parent,false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkAdapter.LinkViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        //Glide để tải hình ảnh từ URL
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class LinkViewHolder extends  RecyclerView.ViewHolder{
        PhotoView photoView;
        LinkViewHolder(View itemView){
            super(itemView);
            photoView = itemView.findViewById(R.id.photo_view_detail);
        }
    }
}
