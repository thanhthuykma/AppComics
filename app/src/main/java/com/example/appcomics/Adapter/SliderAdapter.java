package com.example.appcomics.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcomics.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<String> imageUrls;

    public SliderAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photo_view_slider);
        }
    }
}