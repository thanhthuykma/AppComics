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
import com.example.appcomics.Controller.StoryIntroActivity;
import com.example.appcomics.Model.Comic;
import com.example.appcomics.Model.Comic1;
import com.example.appcomics.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context context;
    private List<Comic1> comicList;


    public SearchAdapter(Context context, List<Comic1> comicList) {
        this.context = context;
        this.comicList = comicList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_results, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Comic1 comic = comicList.get(position);
        holder.bind(comic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StoryIntroActivity.class);
                intent.putExtra("mangaid",comic.getID());
                intent.putExtra("name",comic.getName());
                intent.putExtra("username",comic.getName());
                intent.putExtra("imagehistory",comic.getImage());
                intent.putExtra("views",comic.getViews());
                intent.putExtra("source","comic");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView imageComic;
        TextView textViewTitle, textViewAuthor, textViewChapters, textfavcount;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imageComic = itemView.findViewById(R.id.imageComic);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewChapters = itemView.findViewById(R.id.textViews);
            textfavcount = itemView.findViewById(R.id.textfavcount);
        }

        public void bind(Comic1 comic) {
            textViewTitle.setText(comic.getName());
            textViewAuthor.setText("Tác giả: " + comic.getTacgia());
            textViewChapters.setText("Lượt xem: " + comic.getViews());
            textfavcount.setText("Lượt thích: "+ comic.getFavoritecount());

            // Load image with Glide
            Glide.with(itemView.getContext())
                    .load(comic.getImage())
                    .into(imageComic);

        }
    }
}
