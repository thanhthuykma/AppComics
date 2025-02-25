package com.example.appcomics.Controller;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcomics.Adapter.CommentAdapter;
import com.example.appcomics.Model.Comment;
import com.example.appcomics.Model.ReplyComment;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;  // RecyclerView để hiển thị danh sách bình luận
    private CommentAdapter commentAdapter;  // Adapter dùng để kết nối dữ liệu bình luận với RecyclerView
    private IComicAPI iComicAPI;  // Interface để giao tiếp với API comic
    private String username;
    private List<Comment> commentList;
    private List<ReplyComment> replyCommentsList;
    private int mangaid;
    private Toolbar toolbar;
    private EditText etComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comment);  // Gán layout cho Activity

        Intent intent = getIntent();
        mangaid = intent.getIntExtra("mangaid",0);
        username = intent.getStringExtra("username");
        recyclerView = findViewById(R.id.recycler_comments);  // Ánh xạ RecyclerView từ layou
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Gọi API lấy danh sách chương
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);
        // Gọi phương thức để tải danh sách bình luận cho comicId
        fetchComments(mangaid);

        // Cài đặt sự kiện khi người dùng nhấn nút gửi bình luận
        findViewById(R.id.btn_submit_comment).setOnClickListener(view -> {
            etComment = findViewById(R.id.et_comment_input);  // Lấy EditText nhập bình luận
            String commentContent = etComment.getText().toString();  // Lấy nội dung bình luận
            if (!commentContent.isEmpty()) {  // Kiểm tra nếu người dùng đã nhập bình luận
                submitComment(commentContent);  // Gửi bình luận nếu không trống
            } else {
                // Nếu bình luận trống, hiển thị thông báo
                Toast.makeText(this, "Hãy viết bình luận!", Toast.LENGTH_SHORT).show();
            }
        });
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Kết thúc activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Phương thức lấy danh sách bình luận từ API dựa trên comicId
    public void fetchComments(int comicId) {
        iComicAPI.getCommentsByComicId(comicId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    commentList= response.body();  // Lấy danh sách bình luận từ phản hồi API
                    iComicAPI.getRepCom(comicId).enqueue(new Callback<List<ReplyComment>>() {
                        @Override
                        public void onResponse(Call<List<ReplyComment>> call, Response<List<ReplyComment>> response) {
                            if(response.isSuccessful()){
                                replyCommentsList = response.body();
                                commentAdapter = new CommentAdapter(commentList,replyCommentsList,CommentActivity.this,username);  // Khởi tạo Adapter với danh sách bình luận
                                recyclerView.setAdapter(commentAdapter);  // Gán Adapter cho RecyclerView
                            }
                        }

                        @Override
                        public void onFailure(Call<List<ReplyComment>> call, Throwable t) {

                        }
                    });
                } else {
                    // Hiển thị thông báo nếu tải bình luận thất bại
                    Toast.makeText(CommentActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                // Hiển thị thông báo nếu xảy ra lỗi khi kết nối đến API
                Toast.makeText(CommentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức gửi bình luận mới lên API
    private void submitComment(String content) {
        Integer parent_id = null;
        Comment newComment = new Comment(mangaid,username, content,parent_id);  // Tạo đối tượng Comment mới (0 là số lượt thích ban đầu)

        // Gửi bình luận mới lên API
        iComicAPI.addComment(newComment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    // Hiển thị thông báo khi bình luận được thêm thành công
                   etComment.setText("");
                    fetchComments(mangaid);  // Tải lại danh sách bình luận sau khi thêm
                } else {
                    // Hiển thị thông báo nếu thêm bình luận thất bại
                    Toast.makeText(CommentActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                // Hiển thị thông báo nếu xảy ra lỗi khi gửi bình luận
                Toast.makeText(CommentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
