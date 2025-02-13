package com.example.appcomics.Controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.appcomics.Adapter.ChapterAdapter;
import com.example.appcomics.Adapter.LinkAdapter;
import com.example.appcomics.Model.Chapter;
import com.example.appcomics.Model.Links;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Spinner;

public class DetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private LinkAdapter linkAdapter;
    private IComicAPI iComicAPI;
    private ImageView back_icon;
    private ImageView next_icon;
    private Toolbar toolbar;
    //private TextView toolbar_title;
    private int mangaid;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList;
    private int currentPage = 0; // Trang hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        int chapterid = intent.getIntExtra("chapterid",0);
        String tenchap = intent.getStringExtra("tenchap");
        mangaid = intent.getIntExtra("mangaid",0);


        viewPager2 = findViewById(R.id.view_pager_detail);
        //toolbar_title = findViewById(R.id.toolbar_title);
        next_icon = findViewById(R.id.next_icon);
        back_icon = findViewById(R.id.back_icon);
        toolbar = findViewById(R.id.toolbar_details);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);  // Disable default title
        //toolbar_title.setText(tenchap);

        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);
        fetchimage(chapterid);
        fetchChaptersByMangaId(mangaid);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == 0) {
                    // Quay lại màn hình hiển thị tất cả các chương
                                      finish();
                } else {
                    // Quay lại trang trước đó
                    viewPager2.setCurrentItem(currentPage - 1);
                }
            }
        });

        next_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < linkAdapter.getItemCount() - 1) {
                    viewPager2.setCurrentItem(currentPage + 1);
                } else {
                    Toast.makeText(DetailActivity.this, "Đã ở trang cuối cùng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
            }
        });
    }
    //Thêm chapter vào spinner
    private void fetchChaptersByMangaId(int mangaid) {
        iComicAPI.getChaptersByMangaId(mangaid).enqueue(new Callback<List<Chapter>>() {
            @Override
            public void onResponse(Call<List<Chapter>> call, Response<List<Chapter>> response) {
                if (response.isSuccessful()) {
                    chapterList = response.body();
                    setupSpinner(chapterList);
                }
                else {
                    Toast.makeText(DetailActivity.this, "Failed to load chapters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Chapter>> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Truyện chưa được cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Setup Spinner với dữ liệu chapter
    private void setupSpinner(List<Chapter> chapters) {
        List<String> chapterTitles = new ArrayList<>();
        for (Chapter chapter : chapters) {
            chapterTitles.add(chapter.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(DetailActivity.this,
                android.R.layout.simple_spinner_item, chapterTitles);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.chapter_spinner);
        spinner.setAdapter(adapter);

        // Xử lý spinner được chọn
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected chapter
                Chapter selectedChapter = chapters.get(position);
                fetchimage(selectedChapter.getID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no item is selected (optional)
            }
        });
    }


    private void fetchimage(int chapterid) {
        iComicAPI.getLinks(chapterid).enqueue(new Callback<List<Links>>() {
            @Override
            public void onResponse(Call<List<Links>> call, Response<List<Links>> response) {
                if (response.isSuccessful()) {
                    List<Links> links = response.body();
                    List<String> imageUrls = new ArrayList<>();

                    for (Links link : links) {
                        imageUrls.add(link.getLink());
                    }
                    // Chắc chắn không gán adapter nếu không có hình ảnh nào
                    if (!imageUrls.isEmpty()) {
                        linkAdapter = new LinkAdapter(imageUrls);
                        viewPager2.setAdapter(linkAdapter);
                    } else {
                        Toast.makeText(DetailActivity.this, "Không có hình ảnh để hiển thị", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Links>> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Lưu vị trí trang hiện tại vào SharedPreferences
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("currentPage", currentPage);  // Lưu trang hiện tại
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Lưu vị trí trang khi ứng dụng dừng lại
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("currentPage", currentPage);  // Lưu trang hiện tại
        editor.apply();
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra SharedPreferences để lấy vị trí trang đã lưu
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int savedPage = preferences.getInt("currentPage", -1);  // Nếu không có, giá trị mặc định là -1

        if (savedPage != -1) {
            // Nếu có trang đã lưu, hiển thị Dialog để hỏi người dùng
            showResumeDialog(savedPage);
        }
    }

    private void showResumeDialog(int savedPage) {
        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bạn đang đọc ở trang " + (savedPage + 1))
                .setMessage("Bạn muốn tiếp tục đọc từ trang này hoặc đọc lại từ đầu?")
                .setPositiveButton("Đọc tiếp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đọc tiếp từ trang đã lưu
                        viewPager2.setCurrentItem(savedPage);
                    }
                })
                .setNegativeButton("Đọc lại từ đầu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Quay lại trang đầu
                        viewPager2.setCurrentItem(0);
                    }
                })
                .setCancelable(false)
                .show();
    }


}
