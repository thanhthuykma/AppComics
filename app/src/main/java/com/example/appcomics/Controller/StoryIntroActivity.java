package com.example.appcomics.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.appcomics.Model.Author;
import com.example.appcomics.Model.CategoryResponse;
import com.example.appcomics.Model.ThongTin;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryIntroActivity extends AppCompatActivity {

    private TextView tvStoryName, tvStoryAuthor, tvStoryGenre, tvStoryContent;
    private ImageView imgStoryCover;
    private ImageButton btnBack; // Thêm biến cho nút Back
    private Toolbar toolbar;
    private IComicAPI iComicAPI;
    private int mangaid;
    private Button btndoc;
    private String tacgia;
    private String username,name;
    private String imagehistory;
    private int views;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_intro); // Đảm bảo layout tương ứng đã tạo

        // Khởi tạo các Views
        tvStoryName = findViewById(R.id.tv_story_name);
        tvStoryAuthor = findViewById(R.id.tv_story_author);
        tvStoryGenre = findViewById(R.id.tv_story_genre);
        tvStoryContent = findViewById(R.id.tv_story_content);
        imgStoryCover = findViewById(R.id.img_story_cover);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            mangaid = intent.getIntExtra("mangaid",1);
            username = intent.getStringExtra("username");
            imagehistory = intent.getStringExtra("imagehistory");
            views = intent.getIntExtra("views",0);

            iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);
            tacgia(mangaid);
            thongtin(mangaid);
            theloai(mangaid);

            // Cập nhật các view với dữ liệu
            tvStoryName.setText(name);

            // Tải ảnh bìa truyện từ URL (sử dụng Glide)
            String imageUrl = intent.getStringExtra("imagehistory");

            Glide.with(this)
                    .load(imageUrl)
                    .into(imgStoryCover);
        }
        btndoc= findViewById(R.id.btn_doctruyen);
        btndoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(StoryIntroActivity.this, ChapterActivity.class );
                intent1.putExtra("mangaid",mangaid);
                intent1.putExtra("name",name);
                intent1.putExtra("username",username);
                intent1.putExtra("imagehistory",imagehistory);
                intent1.putExtra("views",views);
                intent1.putExtra("tacgia",tacgia);
                intent1.putExtra("source","comic");
                startActivity(intent1);
            }
        });
    }
    // Xử lý nút back trên toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Kết thúc activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void tacgia(int mangaid){
        iComicAPI.tacgia(mangaid).enqueue(new Callback<Author>() {
            @Override
            public void onResponse(Call<Author> call, Response<Author> response) {
                if ((response.isSuccessful())){
                    Author author = response.body();
                    tacgia = author.getTacgia();
                    tvStoryAuthor.setText("Tác giả: "+tacgia);
                }
            }

            @Override
            public void onFailure(Call<Author> call, Throwable t) {

            }
        });
    }
    public void thongtin(int mangaid){
        iComicAPI.thongtin(mangaid).enqueue(new Callback<ThongTin>() {
            @Override
            public void onResponse(Call<ThongTin> call, Response<ThongTin> response) {
                if(response.isSuccessful()){
                    ThongTin thongtin = response.body();
                    String content= thongtin.getThongtin();
                    tvStoryContent.setText(content);
                }
            }

            @Override
            public void onFailure(Call<ThongTin> call, Throwable t) {

            }
        });
    }
    public void theloai(int mangaid){
        iComicAPI.getTheLoai(mangaid).enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if(response.isSuccessful()){
                    List<String> categories = response.body().getCategories();
                    String result = TextUtils.join(", ",categories);
                    tvStoryGenre.setText("Thể loại: " + result);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(StoryIntroActivity.this, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
            }

        });
    }
}

