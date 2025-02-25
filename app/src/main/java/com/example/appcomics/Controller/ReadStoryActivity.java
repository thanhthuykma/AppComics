package com.example.appcomics.Controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.appcomics.Model.Chapter;
import com.example.appcomics.Model.ChapterContent;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadStoryActivity extends AppCompatActivity {
    private IComicAPI iComicAPI;
    private ImageButton btnback, btnnext;
    private Toolbar toolbar;
    private int mangaid;
    private TextView story_content,tvchaptername;
    private ScrollView scrollView;
    private List<Chapter> chapterList;
    private int chapterid;
    private Switch themeSwitch;
    private boolean isDarkMode = false;
    private SeekBar fontSizeSeekBar;
    private float textSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_story);
        Intent intent = getIntent();
        chapterid = intent.getIntExtra("chapterid", 0);
        //Toast.makeText(ReadStoryActivity.this,"chapterid"+chapterid,Toast.LENGTH_SHORT).show();
        String tenchap = intent.getStringExtra("tenchap");
        mangaid = intent.getIntExtra("mangaid", 0);

        btnback = findViewById(R.id.btn_previous);
        btnnext = findViewById(R.id.btn_next);
        toolbar = findViewById(R.id.toolbar_read);
        story_content = findViewById(R.id.story_content);
        scrollView = findViewById(R.id.scrollView);
        themeSwitch = findViewById(R.id.theme_switch);
        tvchaptername = findViewById(R.id.chapter_number);
        tvchaptername.setText(tenchap);

        btnback.setOnClickListener(v -> navigateToChapter(-1));
        btnnext.setOnClickListener(v -> navigateToChapter(1));

        // Kiểm tra trạng thái đã lưu
        isDarkMode = getSharedPreferences("ReadingPrefs", MODE_PRIVATE).getBoolean("dark_mode", false);
        applyTheme();
        // Xử lý khi bật/tắt chế độ tối
        themeSwitch.setChecked(isDarkMode);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isDarkMode = isChecked;
            getSharedPreferences("ReadingPrefs", MODE_PRIVATE).edit().putBoolean("dark_mode", isDarkMode).apply();
            applyTheme();
        });
        fontSizeSeekBar = findViewById(R.id.font_size_seekbar);
        // Lấy cỡ chữ đã lưu
        textSize = getSharedPreferences("ReadingPrefs", MODE_PRIVATE).getFloat("text_size", 18);
        story_content.setTextSize(textSize);
        // Cập nhật khi SeekBar thay đổi
        fontSizeSeekBar.setProgress((int) textSize);
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 12) progress = 12; // Giới hạn cỡ chữ tối thiểu
                story_content.setTextSize(progress);
                textSize = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getSharedPreferences("ReadingPrefs", MODE_PRIVATE).edit().putFloat("text_size", textSize).apply();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);
        fetchcontent(chapterid);
        fetchChaptersByMangaId(mangaid);
    }
    //chỉnh sáng tối
    private void applyTheme() {
        if (isDarkMode) {
            scrollView.setBackgroundColor(Color.BLACK);
            story_content.setTextColor(Color.WHITE);
        } else {
            scrollView.setBackgroundColor(Color.WHITE);
            story_content.setTextColor(Color.BLACK);
        }
    }

    //thêm chapter vào spinner
    private void fetchChaptersByMangaId(int mangaid) {
        iComicAPI.getChaptersByMangaId(mangaid).enqueue(new Callback<List<Chapter>>() {
            @Override
            public void onResponse(Call<List<Chapter>> call, Response<List<Chapter>> response) {
                if (response.isSuccessful()) {
                    chapterList = response.body();
                    setupSpinner(chapterList);
                }
                else {
                    Toast.makeText(ReadStoryActivity.this, "Failed to load chapters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Chapter>> call, Throwable t) {
                Toast.makeText(ReadStoryActivity.this, "Truyện chưa được cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupSpinner(List<Chapter> chapters) {
        List<String> chapterTitles = new ArrayList<>();
        for (Chapter chapter : chapters) {
            chapterTitles.add(chapter.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(ReadStoryActivity.this,
                android.R.layout.simple_spinner_item, chapterTitles);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.chapter_spinner);
        spinner.setAdapter(adapter);

        // Đặt giá trị mặc định của Spinner theo chapterid hiện tại
        int defaultIndex = 0;
        for (int i = 0; i < chapters.size(); i++) {
            if (chapters.get(i).getID() == chapterid) {
                defaultIndex = i;
                break;
            }
        }
        spinner.setSelection(defaultIndex); // Xóa `false` để đảm bảo `onItemSelected` chạy khi chọn lại cùng chapter

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Chapter selectedChapter = chapters.get(position);
                if (selectedChapter.getID() != chapterid) {  // Chỉ tải lại nếu chapter khác
                    chapterid = selectedChapter.getID();  // Cập nhật chapterid mới
                    fetchcontent(chapterid);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    //back,next
    private void navigateToChapter(int direction) {
        if (chapterList == null || chapterList.isEmpty()) return;

        int currentIndex = -1;
        for (int i = 0; i < chapterList.size(); i++) {
            if (chapterList.get(i).getID() == chapterid) {
                currentIndex = i;
                break;
            }
        }

        int newIndex = currentIndex + direction;
        if (newIndex >= 0 && newIndex < chapterList.size()) {
            // Lưu vị trí cuộn của chương hiện tại
            int currentScrollY = scrollView.getScrollY();
            getSharedPreferences("ReadingPrefs", MODE_PRIVATE)
                    .edit()
                    .putInt("scroll_position_" + chapterid, currentScrollY)
                    .apply();

            // Chuyển sang chương mới
            Chapter newChapter = chapterList.get(newIndex);
            chapterid = newChapter.getID();
            fetchcontent(chapterid);
            tvchaptername.setText(newChapter.getName());

            // Cập nhật Spinner
            Spinner spinner = findViewById(R.id.chapter_spinner);
            spinner.setSelection(newIndex);
        } else {
            Toast.makeText(this, direction == -1 ? "Đây là chương đầu tiên" : "Đây là chương cuối cùng", Toast.LENGTH_SHORT).show();
        }
    }



    public void fetchcontent(int chapterid) {
        iComicAPI.getChapcontent(chapterid).enqueue(new Callback<ChapterContent>() {
            @Override
            public void onResponse(Call<ChapterContent> call, Response<ChapterContent> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String content = response.body().getNoidung();
                    story_content.setText(content);
                    int savedPosition = getSharedPreferences("ReadingPrefs", MODE_PRIVATE)
                            .getInt("scroll_position_" + chapterid, 0);

                    if (savedPosition > 0) {
                        new AlertDialog.Builder(ReadStoryActivity.this)
                                .setTitle("Tiếp tục đọc?")
                                .setMessage("Bạn có muốn tiếp tục đọc từ vị trí trước đó không?")
                                .setPositiveButton("Có", (dialog, which) -> {
                                    story_content.post(() -> scrollView.scrollTo(0, savedPosition));
                                })
                                .setNegativeButton("Không", (dialog, which) -> {
                                })
                                .show();
                    }

                    // Theo dõi vị trí cuộn
                    scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                        int scrollY = scrollView.getScrollY();
                        int contentHeight = scrollView.getChildAt(0).getHeight();
                        int scrollViewHeight = scrollView.getHeight();

                        // Tính phần trăm đã đọc
                        int percentRead = (int) (((float) scrollY / (contentHeight - scrollViewHeight)) * 100);
                        percentRead = Math.max(0, Math.min(percentRead, 100)); // Giới hạn từ 0 - 100%

                        // Lưu vào SharedPreferences
                        getSharedPreferences("ReadingPrefs", MODE_PRIVATE)
                                .edit()
                                .putInt("scroll_position_" + chapterid, scrollY)
                                .putInt("read_percent_" + chapterid, percentRead)
                                .apply();
                    });
                }
            }

            @Override
            public void onFailure(Call<ChapterContent> call, Throwable t) {
            }
        });
    }
}
