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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcomics.Controller.CommentActivity;
import com.example.appcomics.Model.Comment;
import com.example.appcomics.Model.ReplyComment;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private List<ReplyComment> replyCommentList;
    private Context context;
    private SharedPreferences sharedPreferences;
    private IComicAPI api;
    private String username;


    public CommentAdapter(List<Comment> commentList, List<ReplyComment> replyCommentList, Context context, String username) {
        this.context = context;
        this.commentList = commentList;
        this.sharedPreferences = context.getSharedPreferences("LikePrefs", Context.MODE_PRIVATE);
        this.api = RetrofitClient.getClient().create(IComicAPI.class);
        this.replyCommentList = replyCommentList;
        this.username = username;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        if (commentList == null || commentList.isEmpty()) {
            return; // Không bind dữ liệu nếu danh sách rỗng
        } else {
            Comment comment = commentList.get(position);
            ReplyComment replyComment = null;
            if (replyCommentList != null && position < replyCommentList.size()) {
                replyComment = replyCommentList.get(position);
            }
            // Lọc danh sách replyCommentList để lấy reply có reply_parentid trùng với comment id
            List<ReplyComment> filteredReplies = new ArrayList<>();
            if (replyCommentList != null) {
                for (ReplyComment reply : replyCommentList) {
                    if (reply.getReply_parentid() != null && reply.getReply_parentid().equals(comment.getId())) {
                        filteredReplies.add(reply);
                    }
                }
            }

            //Log.d("CommentAdapter", "Binding comment parentID: " + comment.getParent_id() + "comment id:" + comment.getId() + "reply_parentid" + replyComment.getReply_parentid());

            holder.tvUserName.setText(comment.getUsername());
            holder.tvCommentContent.setText(comment.getComment());
            holder.tvLikeCount.setText(String.valueOf(comment.getLike_count()));
            // Nếu có ít nhất một phản hồi thì hiển thị nút "Xem reply"
            if (!filteredReplies.isEmpty()) {
                holder.tv_xemreply.setVisibility(View.VISIBLE);
                holder.tv_xemreply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.ReplyCommentadapter.setLayoutManager(new LinearLayoutManager(context));
                        ReplyComAdapter replyComAdapter = new ReplyComAdapter(filteredReplies, context,username);
                        holder.ReplyCommentadapter.setAdapter(replyComAdapter);
                        holder.ReplyCommentadapter.setVisibility(View.VISIBLE);
                    }
                });

            } else {
                holder.tv_xemreply.setVisibility(View.GONE);

            }
            //Xóa bình luận
            if (comment.getUsername().equals(username)){
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
                                    Comment commentDelete = commentList.get(position);
                                    api.deleteComment(commentDelete.getId()).enqueue(new Callback<Void>() {
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
                                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                });
            }
            //button trả lời
            holder.btnreply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.ll_reply_input.getVisibility() == View.GONE) {
                        holder.ll_reply_input.setVisibility(View.VISIBLE);
                        if (holder.btnsubmitreply == null) {
                            Log.e("CommentAdapter", "btnsubmitreply is null!");
                        }else{
                        holder.btnsubmitreply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String repcom = holder.et_reply_input.getText().toString();
                                Comment newComment = new Comment(comment.getManga_id(), username, repcom, comment.getId());
                                Log.d("APIrep", "MangaID: " + comment.getManga_id() + ", Username: " + comment.getUsername() + ", Content: " + repcom);

                                api.addComment(newComment).enqueue(new Callback<Comment>() {
                                    @Override
                                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                                        if(response.isSuccessful()){

                                            // Tạo đối tượng ReplyComment mới từ phản hồi đã thêm
                                            ReplyComment newReply = new ReplyComment(
                                                    comment.getManga_id(),
                                                    username,
                                                    repcom,
                                                    comment.getId(),
                                                    0 // Ban đầu chưa có lượt thích
                                            );

                                            // Cập nhật danh sách phản hồi
                                            filteredReplies.add(newReply);

                                            // Thông báo cập nhật danh sách
                                            if (holder.ReplyCommentadapter.getAdapter() != null) {
                                                holder.ReplyCommentadapter.getAdapter().notifyDataSetChanged();
                                            }
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
                                        Log.e("APIrep", "Lỗi khi gửi phản hồi: " + t.getMessage());
                                    }
                                });
                            }
                        });
                    }
                    } else {
                        holder.ll_reply_input.setVisibility(View.GONE);
                    }
                }
            });


            boolean isLiked = sharedPreferences.getBoolean("liked_" + comment.getId(), false);
            updateLikeButton(holder.btnLike, isLiked);

            holder.btnLike.setOnClickListener(v -> {
                boolean newState = !sharedPreferences.getBoolean("liked_" + comment.getId(), false);
                sharedPreferences.edit().putBoolean("liked_" + comment.getId(), newState).apply();
                updateLikeButton(holder.btnLike, newState);

                int likeCount = comment.getLike_count();
                if (newState) {
                    likeCount++;
                    updateLikeAPI(comment.getId(), true);
                } else {
                    likeCount--;
                    updateLikeAPI(comment.getId(), false);
                }
                comment.setLike_count(likeCount);
                holder.tvLikeCount.setText(String.valueOf(likeCount));
            });

        }
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


    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserName, tvCommentContent, tvLikeCount, tv_xemreply;
        public ImageButton btnLike, btnreply, btndelete;
        public LinearLayout ll_reply_input;
        private RecyclerView ReplyCommentadapter;
        private Button btnsubmitreply;
        private EditText et_reply_input;

        public CommentViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvCommentContent = itemView.findViewById(R.id.tv_content);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tv_xemreply = itemView.findViewById(R.id.tv_xemreply);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnreply = itemView.findViewById(R.id.btn_reply);
            ll_reply_input = itemView.findViewById(R.id.ll_reply_input);
            btnsubmitreply = itemView.findViewById(R.id.btn_submit_reply);
            et_reply_input = itemView.findViewById(R.id.et_reply_input);
            btndelete = itemView.findViewById(R.id.btn_delete);
            ReplyCommentadapter = itemView.findViewById(R.id.recycler_replycomments);
        }
    }
}
