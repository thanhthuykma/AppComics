package com.example.appcomics.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcomics.Model.Comment;
import com.example.appcomics.Model.ReplyComment;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReplyComAdapter extends RecyclerView.Adapter<ReplyComAdapter.ReplyComViewHolder> {
    private List<ReplyComment> commentList;
    private Context context;
    private SharedPreferences sharedPreferences;
    private IComicAPI api;
    private String username;

    public ReplyComAdapter(List<ReplyComment> commentList, Context context, String username) {
        this.commentList = commentList;
        this.context = context;
        this.api = RetrofitClient.getClient().create(IComicAPI.class);
        this.sharedPreferences = context.getSharedPreferences("RepLikePrefs",Context.MODE_PRIVATE);
        this.username = username;
    }
    @NonNull
    @Override
    public ReplyComAdapter.ReplyComViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment1, parent, false);
        return new ReplyComAdapter.ReplyComViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ReplyComAdapter.ReplyComViewHolder holder, int position) {
        final ReplyComment replyComment = commentList.get(position);

        holder.tvUserName.setText(replyComment.getReply_user());
        holder.tvCommentContent.setText(replyComment.getReply_comment());
        holder.tvLikeCount.setText(String.valueOf(replyComment.getReply_like()));

        boolean isLiked = sharedPreferences.getBoolean("liked_" + replyComment.getReply_id(), false);
        updateLikeButton(holder.btnLike, isLiked);

        holder.btnLike.setOnClickListener(v -> {
            boolean newState = !sharedPreferences.getBoolean("liked_" + replyComment.getReply_id(), false);
            sharedPreferences.edit().putBoolean("liked_" + replyComment.getReply_id(), newState).apply();
            updateLikeButton(holder.btnLike, newState);

            int likeCount = replyComment.getReply_like();
            if (newState) {
                likeCount++;
                updateLikeAPI(replyComment.getReply_id(), true);
            } else {
                likeCount--;
                updateLikeAPI(replyComment.getReply_id(), false);
            }
            replyComment.setReply_like(likeCount);
            holder.tvLikeCount.setText(String.valueOf(likeCount));
        });
        //Xóa bình luận
        if(replyComment.getReply_user().equals(username)){
            holder.btndelete.setVisibility(View.VISIBLE);
            holder.btndelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Xóa bình luận")
                            .setMessage("Bạn muốn xóa bình luận ?")
                            .setPositiveButton("OK",(dialog, which) -> {
                                int position = holder.getAdapterPosition();
                                if(position==RecyclerView.NO_POSITION) return;
                                ReplyComment commentDelete = commentList.get(position);
                                api.deleteComment(commentDelete.getReply_id()).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Đã xóa bình luận!", Toast.LENGTH_SHORT).show();
                                            // Xóa bình luận khỏi danh sách và cập nhật RecyclerView
                                            commentList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, commentList.size());
                                        } else {
                                            Toast.makeText(context, "Lỗi khi xóa bình luận!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .setNegativeButton("Hủy",(dialog, which) -> dialog.dismiss())
                            .show();
                }
            });
        }
        //tra loi
        holder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ll_reply_input.getVisibility() == View.GONE){
                    holder.ll_reply_input.setVisibility(View.VISIBLE);
                    //buttonn submit reply
                    holder.btnsubmitreply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String repcom = holder.et_reply_input.getText().toString();
                            Comment newrepcom = new Comment(replyComment.getManga_id(),username, repcom, replyComment.getReply_id() );
                            api.addComment(newrepcom).enqueue(new Callback<Comment>() {
                                @Override
                                public void onResponse(Call<Comment> call, Response<Comment> response) {
                                    if(response.isSuccessful()){
                                        ReplyComment newReply = new ReplyComment(
                                                replyComment.getManga_id(),  // Manga ID của bình luận gốc
                                                username,                    // Người dùng đang phản hồi
                                                repcom,                       // Nội dung phản hồi
                                                replyComment.getReply_id(),   // Parent ID (ID của bình luận cha)
                                                0                             // Số lượt thích ban đầu = 0
                                        );

                                        // Thêm phản hồi mới vào danh sách và cập nhật giao diện
                                        commentList.add(newReply);
                                        notifyDataSetChanged();
                                        holder.et_reply_input.setText("");
                                    }else {
                                        try {
                                            Log.e("APIrep", "Failed to add comment: " + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Comment> call, Throwable t) {

                                }
                            });
                        }
                    });
                }else{
                    holder.ll_reply_input.setVisibility(View.GONE);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }
    private void updateLikeButton(ImageButton btnLike, boolean isLiked) {
        if (isLiked) {
            btnLike.setImageResource(R.drawable.baseline_thumb_up_24);
        } else {
            btnLike.setImageResource(R.drawable.baseline_thumb_up_off_alt_24);
        }
    }
    private void updateLikeAPI(int commentId, boolean isLike) {
        Call<Void> call = isLike ? api.likeComment(commentId) : api.dislikeComment(commentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Like updated for comment ID: " + commentId);
                } else {
                    Log.e("API", "Failed to update like for comment ID: " + commentId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    public static class ReplyComViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserName, tvCommentContent, tvLikeCount, tv_xemreply;
        public ImageButton btnLike,btn_reply,btndelete;
        public LinearLayout ll_reply_input;
        private Button btnsubmitreply;
        private EditText et_reply_input;

        public ReplyComViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvCommentContent = itemView.findViewById(R.id.tv_content);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tv_xemreply = itemView.findViewById(R.id.tv_xemreply);
            btnLike = itemView.findViewById(R.id.btn_like);
            btn_reply= itemView.findViewById(R.id.btn_reply);
            ll_reply_input = itemView.findViewById(R.id.ll_reply_input);
            btnsubmitreply = itemView.findViewById(R.id.btn_submit_reply);
            et_reply_input = itemView.findViewById(R.id.et_reply_input);
            btndelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
