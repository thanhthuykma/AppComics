package com.example.appcomics.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appcomics.Controller.DownListActivity;
import com.example.appcomics.Model.Comic1;
import com.example.appcomics.Model.DownLoadHIis;
import com.example.appcomics.R;


import java.util.List;

public class DownLoadAdapter extends RecyclerView.Adapter<DownLoadAdapter.DownloadViewHolder> {

    private Context context;
    private List<DownLoadHIis> downloadList;

    // Constructor để nhận dữ liệu
    public DownLoadAdapter(Context context, List<DownLoadHIis> comicList) {
        this.context = context;
        this.downloadList = comicList;
    }

    // Tạo ra ViewHolder để liên kết layout item_download với mã Java
    @Override
    public DownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_download, parent, false); // Đảm bảo item_download là layout bạn đã tạo
        return new DownloadViewHolder(itemView);
    }

    // Gắn dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(DownloadViewHolder holder, int position) {
        DownLoadHIis download = downloadList.get(position);

        // Set dữ liệu vào các view trong item_download
        holder.comicTitle.setText(download.getManganame());
        holder.comicChapters.setText("Tác giả: " + download.getTacgia());
        holder.comicViews.setText("Views: " + download.getViews());

        // Load hình ảnh cho ImageView bằng Glide hoặc Picasso
        // Ví dụ Glide:
        Glide.with(context)
                .load(download.getUrl())
                .into(holder.comicImage);
        holder.itemView.setOnClickListener(v -> {
            // Pass the mangaid or necessary data to the next activity
            Intent intent = new Intent(context, DownListActivity.class);
            intent.putExtra("MANGA_ID", download.getMangaid()); // Assuming getMangaid() returns the ID of the manga
            context.startActivity(intent);
        });

    }

    // Trả về số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    // ViewHolder chứa các view của item_download
    public static class DownloadViewHolder extends RecyclerView.ViewHolder {

        public ImageView comicImage;
        public TextView comicTitle;
        public TextView comicChapters;
        public TextView comicViews;

        public DownloadViewHolder(View itemView) {
            super(itemView);
            comicImage = itemView.findViewById(R.id.comic_image);
            comicTitle = itemView.findViewById(R.id.comic_title);
            comicChapters = itemView.findViewById(R.id.comic_chapters);
            comicViews = itemView.findViewById(R.id.comic_views);
        }
    }
}
