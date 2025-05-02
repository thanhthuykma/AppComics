package com.example.appcomics.Controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.appcomics.Model.AudioResponse;
import com.example.appcomics.Model.Chapter;
import com.example.appcomics.Model.ChapterContent;
import com.example.appcomics.Model.ContentRequest;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;
import android.widget.Spinner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadStoryActivity extends AppCompatActivity {
    private IComicAPI iComicAPI;
    private ImageButton btnback, btnnext, btntts;
    private Toolbar toolbar;
    private int mangaid;
    private TextView story_content,tvchaptername;
    private ScrollView scrollView;
    private List<Chapter> chapterList;
    private int chapterid;
    private Switch themeSwitch;
    private String content;
    private boolean isDarkMode = false;
    private SeekBar fontSizeSeekBar;
    private float textSize;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;


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
        btntts = findViewById(R.id.btn_tts);
        toolbar = findViewById(R.id.toolbar_read);
        story_content = findViewById(R.id.story_content);
        scrollView = findViewById(R.id.scrollView);
        themeSwitch = findViewById(R.id.theme_switch);
        tvchaptername = findViewById(R.id.chapter_number);
        tvchaptername.setText(tenchap);
        //Text to speech
        // Trong Activity hoặc Fragment của bạn

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
        btntts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentRequest contentRequest = new ContentRequest(content);
                            // Gọi tiếp API phát âm thanh từ ID
                            playAudioFromId(chapterid);
            }
        });

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
    // Hàm để phát âm thanh từ URL
    private void playAudioFromId(int id) {
        IComicAPI iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);

        iComicAPI.getAudio(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] audioData = response.body().bytes();
                        // Kiểm tra nếu có MediaPlayer đang phát, dừng nó và giải phóng tài nguyên
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;

                            // Nếu có MediaController, ẩn nó
                            if (mediaController != null) {
                                mediaController.hide();
                                mediaController = null;
                            }
                        }


                        // Tạo file tạm để lưu âm thanh
                        File tempFile = File.createTempFile("audio", ".mp3", getCacheDir());
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        fos.write(audioData);
                        fos.close();

                        // Tạo MediaPlayer
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(tempFile.getAbsolutePath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        // Hiển thị MediaController
                        runOnUiThread(() -> {
                            FrameLayout anchor = findViewById(R.id.audioLayout); // layout chứa MediaController
                            mediaController = new MediaController(ReadStoryActivity.this);

                            mediaController.setMediaPlayer(new MediaController.MediaPlayerControl() {
                                @Override public void start() { mediaPlayer.start(); }
                                @Override public void pause() { mediaPlayer.pause(); }
                                @Override public int getDuration() { return mediaPlayer.getDuration(); }
                                @Override public int getCurrentPosition() { return mediaPlayer.getCurrentPosition(); }
                                @Override public void seekTo(int pos) { mediaPlayer.seekTo(pos); }
                                @Override public boolean isPlaying() { return mediaPlayer.isPlaying(); }
                                @Override public int getBufferPercentage() { return 100; }
                                @Override public boolean canPause() { return true; }
                                @Override public boolean canSeekBackward() { return true; }
                                @Override public boolean canSeekForward() { return true; }
                                @Override public int getAudioSessionId() { return mediaPlayer.getAudioSessionId(); }
                            });

                            mediaController.setAnchorView(anchor);
                            mediaController.setEnabled(true);
                            mediaController.show(0);
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(getApplicationContext(), "Lỗi khi đọc dữ liệu âm thanh", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Không thể tải âm thanh", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
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
                    content = response.body().getNoidung();
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
