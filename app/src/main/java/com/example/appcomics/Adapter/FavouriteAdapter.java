package com.example.appcomics.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appcomics.Controller.ChapterActivity;
import com.example.appcomics.Model.Favourite;
import com.example.appcomics.R;

import java.util.List;
import java.util.Map;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    private List<Favourite> favourites;
    private Context context;
    private Map<Integer,Integer> viewsMap;
    private Map<Integer,Integer> chapsMap;
    public FavouriteAdapter(List<Favourite> favourites,Context context,Map<Integer,Integer> viewsMap,Map<Integer,Integer> chapsMap) {
        this.favourites = favourites;
        this.context = context;
        this.viewsMap = viewsMap;
        this.chapsMap = chapsMap;
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.ViewHolder holder, int position) {
        Favourite favourite = favourites.get(position);
        holder.title.setText(favourite.getManganame());

        int mangaid = favourite.getMangaid();
        Integer views = viewsMap.get(mangaid);
        Integer chaps = chapsMap.get(mangaid);

        holder.chapters.setText("Số chương: " + (chaps != null ? chaps : "N/A"));
        holder.views.setText("Lượt xem: " + (views != null ? views : "N/A"));
        Glide.with(holder.itemView.getContext())
                .load(favourite.getUrl())
                .into(holder.mangaImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChapterActivity.class);
                intent.putExtra("mangaidfav",favourite.getMangaid());
                intent.putExtra("namefav",favourite.getManganame());
                intent.putExtra("usernamefav",favourite.getUsername());
                intent.putExtra("imagehistoryfav",favourite.getUrl());
                intent.putExtra("source","fav");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mangaImage;
        TextView title;
        TextView chapters;
        TextView views;

        public ViewHolder(View itemView) {
            super(itemView);
            mangaImage = itemView.findViewById(R.id.comic_image);
            title = itemView.findViewById(R.id.comic_title);
            chapters = itemView.findViewById(R.id.comic_chapters);
            views = itemView.findViewById(R.id.comic_views);
        }
    }
}
