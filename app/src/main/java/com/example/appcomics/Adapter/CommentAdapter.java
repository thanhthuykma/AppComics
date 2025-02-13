/*package com.example.appcomics.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcomics.Model.Comment;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private IComicAPI iComicAPI;
    Context context;

    public CommentAdapter(List<Comment> commentList,Context context) {
        this.context =context;
        this.commentList = commentList;
        this.iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        final Comment comment = commentList.get(position);

        // Hiển thị dữ liệu bình luận
        holder.tvUserName.setText("User ID: " + comment.getUserId());  // Có thể thay bằng tên người dùng thực tế
        holder.tvCommentContent.setText(comment.getContent());
        holder.tvLikeCount.setText(String.valueOf(comment.getLikes()));

        // Cập nhật hình ảnh nút "Thích" tùy thuộc vào số lượt thích
        if (comment.getLikes() > 0) {
            holder.btnLike.setImageResource(R.drawable.baseline_thumb_up_24); // Nếu có lượt thích, hiển thị hình ảnh filled
        } else {
            holder.btnLike.setImageResource(R.drawable.baseline_thumb_up_off_alt_24); // Nếu chưa có lượt thích, hiển thị hình ảnh outline
        }

        holder.btnLike.setOnClickListener(view -> {
            likeComment(comment.getId(), holder, comment); // Thực hiện thích bình luận và thay đổi trạng thái
        });
    }

    private void likeComment(int commentId, CommentViewHolder holder, Comment comment) {
        // Thực hiện API yêu cầu thích bình luận
        iComicAPI.likeComment(commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Cập nhật lại số lượt thích
                    int likeCount = comment.getLikes() + 1;
                    comment.setLikes(likeCount);
                    holder.tvLikeCount.setText(String.valueOf(likeCount));

                    // Cập nhật hình ảnh nút thành filled
                    holder.btnLike.setImageResource(R.drawable.baseline_thumb_up_24);
                } else {
                    // Xử lý khi API không thành công
                    // Có thể hiển thị thông báo lỗi cho người dùng
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Xử lý lỗi khi không kết nối được với API
                // Có thể hiển thị thông báo lỗi cho người dùng
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserName, tvCommentContent, tvLikeCount;
        public ImageButton btnLike;

        public CommentViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvCommentContent = itemView.findViewById(R.id.tv_content);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            btnLike = itemView.findViewById(R.id.btn_like);
        }
    }
}
*/