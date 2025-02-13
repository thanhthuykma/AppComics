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
import com.example.appcomics.Model.History;
import com.example.appcomics.R;

import java.util.List;
import java.util.Map;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<History> historyList;
    private Map<Integer, Integer> chaptersMap;
    private Map<Integer, Integer> viewsMap;
    private Context context;

    public HistoryAdapter(List<History> historyList, Map<Integer, Integer> chaptersMap, Map<Integer, Integer> viewsMap,Context context) {
        this.historyList = historyList;
        this.chaptersMap = chaptersMap;
        this.viewsMap = viewsMap;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.title.setText(history.getManganame());

        int mangaid = history.getMangaid();
        Integer chapters = chaptersMap.get(mangaid);
        Integer views = viewsMap.get(mangaid);

        holder.chapters.setText("Số chương: " + (chapters != null ? chapters : "N/A"));
        holder.views.setText("Lượt xem: " + (views != null ? views : "N/A"));

        Glide.with(holder.itemView.getContext())
                .load(history.getUrl())
                .into(holder.mangaImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChapterActivity.class);
                intent.putExtra("mangaidhis",history.getMangaid());
                intent.putExtra("namehis",history.getManganame());
                intent.putExtra("usernamehis",history.getUsername());
                intent.putExtra("imagehistoryhis",history.getUrl());
                intent.putExtra("source","history");
                context.startActivity(intent);
                //((Activity)context).startActivityForResult(intent, REQUEST_CODE_HISTORY);

            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
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