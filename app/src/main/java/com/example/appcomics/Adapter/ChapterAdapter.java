package com.example.appcomics.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcomics.Controller.DetailActivity;
import com.example.appcomics.Controller.ReadStoryActivity;
import com.example.appcomics.Model.Chapter;
import com.example.appcomics.R;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private List<Chapter> chapterList;
    private Context context;
    public ChapterAdapter(Context context,List<Chapter> chapterList){
        this.context = context;
        this.chapterList = chapterList;
    }
    @NonNull
    @Override
    public ChapterAdapter.ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter,parent,false);

        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterAdapter.ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.chapterName.setText(chapter.getName());
        // Lấy phần trăm từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("ReadingPrefs", Context.MODE_PRIVATE);
        int percentRead = prefs.getInt("read_percent_" + chapter.getID(), 0);

        // Cập nhật vòng tròn tiến trình
        holder.progressCircle.setProgress(percentRead);
        holder.progressper.setText(String.valueOf(percentRead)+"%");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadStoryActivity.class);
                intent.putExtra("chapterid",chapter.getID());
                intent.putExtra("tenchap",chapter.getName());
                intent.putExtra("mangaid",chapter.getMangaId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    //Viewholder để tối ưu hiển thị
    public static class ChapterViewHolder extends RecyclerView.ViewHolder{
        public TextView chapterName,progressper;
        public CircularProgressIndicator progressCircle;
        public ChapterViewHolder(@NonNull View itemView){
            super(itemView);
            chapterName = itemView.findViewById(R.id.chapter_name);
            progressCircle = itemView.findViewById(R.id.progress_circle);
            progressper = itemView.findViewById(R.id.progress_percentage);
        }
    }
}
