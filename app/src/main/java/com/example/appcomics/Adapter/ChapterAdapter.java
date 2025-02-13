package com.example.appcomics.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcomics.Controller.DetailActivity;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
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
        public TextView chapterName;
        public ChapterViewHolder(@NonNull View itemView){
            super(itemView);
            chapterName = itemView.findViewById(R.id.chapter_name);
        }
    }
}
