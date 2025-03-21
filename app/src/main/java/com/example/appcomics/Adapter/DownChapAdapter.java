package com.example.appcomics.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcomics.Controller.ReadStoryDownActivity;
import com.example.appcomics.Model.ChapContent;
import com.example.appcomics.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class DownChapAdapter extends RecyclerView.Adapter<DownChapAdapter.DownChapViewHolder> {
    private List<ChapContent> chapContentList;
    private Context context;
    public DownChapAdapter(Context context, List<ChapContent> chapContentList){
        this.context = context;
        this.chapContentList = chapContentList;
    }
    @NonNull
    @Override
    public DownChapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_downchap,parent,false);

        return new DownChapAdapter.DownChapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownChapViewHolder holder, int position) {
        ChapContent chapContent = chapContentList.get(position);
        holder.chapterName.setText(chapContent.getChapter_title());
        // Lấy phần trăm từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("ReadingPrefs", Context.MODE_PRIVATE);
        int percentRead = prefs.getInt("read_percent_" + chapContent.getChapterid(), 0);
        // Cập nhật vòng tròn tiến trình
        holder.progressCircle.setProgress(percentRead);
        holder.progressper.setText(String.valueOf(percentRead)+"%");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadStoryDownActivity.class);
                intent.putExtra("chapterid",chapContent.getChapterid());
                intent.putExtra("content",chapContent.getContent());
                intent.putExtra("title",chapContent.getChapter_title());
                intent.putParcelableArrayListExtra("chapterList", new ArrayList<>(chapContentList)); // Gửi danh sách chương
                intent.putExtra("position", holder.getAdapterPosition()); // Gửi vị trí hiện tại
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapContentList.size();
    }

    public static class DownChapViewHolder extends RecyclerView.ViewHolder{
        public TextView chapterName,progressper;
        public CircularProgressIndicator progressCircle;
        public DownChapViewHolder(@NonNull View itemView){
            super(itemView);
            chapterName = itemView.findViewById(R.id.chapter_name);
            progressCircle = itemView.findViewById(R.id.progress_circle);
            progressper = itemView.findViewById(R.id.progress_percentage);
        }
    }

}
