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
import com.example.appcomics.Controller.StoryIntroActivity;
import com.example.appcomics.Model.Comic;
import com.example.appcomics.R;

import java.util.List;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ComicViewHolder> {

    private List<Comic> comicList;
    private Context context;
    private String username;
    public static final int REQUEST_CODE_HOME = 1001;
    public ComicAdapter(Context context,List<Comic> comicList,String username) {

        this.context = context;
        this.comicList = comicList;
        this.username = username;
    }

    @NonNull
    @Override
    public ComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comic_item, parent, false);
        return new ComicViewHolder(view);
    }

    @NonNull

    @Override
    public void onBindViewHolder(@NonNull ComicViewHolder holder, int position) {
        Comic comic = comicList.get(position);
        holder.textView.setText(comic.getName());

        Glide.with(holder.itemView.getContext())
                .load(comic.getImage())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StoryIntroActivity.class);
                intent.putExtra("mangaid",comic.getID());
                intent.putExtra("name",comic.getName());
                intent.putExtra("username",username);
                intent.putExtra("imagehistory",comic.getImage());
                intent.putExtra("views",comic.getViews());
                intent.putExtra("source","comic");
                context.startActivity(intent);
            }
        });
        holder.viewsText.setText("Lượt xem:"+comic.getViews());

    }

    // Method to add new comics and notify the adapter
    public void addComics(List<Comic> comics) {
        comicList.addAll(comics);  // Add new comics to the existing list
        notifyDataSetChanged();    // Notify the adapter that the data has changed
    }


    @Override
    public int getItemCount() {
        return comicList.size();
    }

    public static class ComicViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView viewsText;

        public ComicViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.manga_name);
            viewsText = itemView.findViewById(R.id.views);
        }
    }
}