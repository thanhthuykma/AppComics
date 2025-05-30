package com.example.appcomics.Controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

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
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appcomics.Model.ChapContent;
import com.example.appcomics.Model.Chapter;
import com.example.appcomics.R;
import com.example.appcomics.SQLite.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;

public class ReadStoryDownActivity extends AppCompatActivity {
    private int chapterid;
    private String content,title;
    private TextView txtcon,txtchapnumber;
    private ImageButton btnback,btnnext;
    private Toolbar toolbar;
    private ArrayList<ChapContent> chapContentList;
    private int currentPosition;
    private Switch themeSwitch;
    private boolean isDarkMode = false;
    private SeekBar fontSizeSeekBar;
    private float textSize;
    private ScrollView scrollView;
    private ImageButton btn_tts;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_story_down);
        chapterid = getIntent().getIntExtra("chapterid", 0);
        content = getIntent().getStringExtra("content");
        title = getIntent().getStringExtra("title");
        txtcon = findViewById(R.id.story_content);
        txtcon.setText(content);
        scrollView = findViewById(R.id.scrollView);
        txtchapnumber = findViewById(R.id.chapter_number);
        txtchapnumber.setText(title);
        int savedPosition = getSharedPreferences("ReadingPrefs", MODE_PRIVATE)
                .getInt("scroll_position_" + chapterid, 0);

        if (savedPosition > 0) {
            new AlertDialog.Builder(ReadStoryDownActivity.this)
                    .setTitle("Tiếp tục đọc?")
                    .setMessage("Bạn có muốn tiếp tục đọc từ vị trí trước đó không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        txtcon.post(() -> scrollView.scrollTo(0, savedPosition));
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
        btnback = findViewById(R.id.btn_previous);
        btnnext = findViewById(R.id.btn_next);
        toolbar = findViewById(R.id.toolbar_read);
        chapContentList = getIntent().getParcelableArrayListExtra("chapterList");
        currentPosition = getIntent().getIntExtra("position", 0);
        //spinner
        setupSpinner(chapContentList);
        //che do toi sang
        themeSwitch = findViewById(R.id.theme_switch);

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

        //Chỉnh cỡ chữ
        fontSizeSeekBar = findViewById(R.id.font_size_seekbar);
        // Lấy cỡ chữ đã lưu
        textSize = getSharedPreferences("ReadingPrefs", MODE_PRIVATE).getFloat("text_size", 18);
        txtcon.setTextSize(textSize);
        // Cập nhật khi SeekBar thay đổi
        fontSizeSeekBar.setProgress((int) textSize);
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 12) progress = 12; // Giới hạn cỡ chữ tối thiểu
                txtcon.setTextSize(progress);
                textSize = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getSharedPreferences("ReadingPrefs", MODE_PRIVATE).edit().putFloat("text_size", textSize).apply();
            }
        });


        btnnext.setOnClickListener(v -> {
            if (currentPosition < chapContentList.size() - 1) {
                currentPosition++;
                displayChapter();
            }
        });

        btnback.setOnClickListener(v -> {
            if (currentPosition > 0) {
                currentPosition--;
                displayChapter();
            }
        });
        btn_tts = findViewById(R.id.btn_tts);
        btn_tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
playAudioFromId(chapterid);
            }
        });
    }
    private void displayChapter() {
        if (chapContentList != null && !chapContentList.isEmpty()) {
            ChapContent currentChapter = chapContentList.get(currentPosition);
            txtchapnumber.setText(currentChapter.getChapter_title());
            txtcon.setText(currentChapter.getContent());
        }
    }
    private void applyTheme() {
        if (isDarkMode) {
            scrollView.setBackgroundColor(Color.BLACK);
            txtcon.setTextColor(Color.WHITE);
        } else {
            scrollView.setBackgroundColor(Color.WHITE);
            txtcon.setTextColor(Color.BLACK);
        }
    }
    private void setupSpinner(List<ChapContent> chapters) {
        List<String> chapterTitles = new ArrayList<>();
        for (ChapContent chapter : chapters) {
            chapterTitles.add(chapter.getChapter_title()); // Lấy tiêu đề chương
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, chapterTitles);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.chapter_spinner);
        spinner.setAdapter(adapter);

        // Xác định vị trí của chương hiện tại trong danh sách
        int defaultIndex = 0;
        for (int i = 0; i < chapters.size(); i++) {
            if (chapters.get(i).getChapterid() == chapterid) {
                defaultIndex = i;
                break;
            }
        }
        spinner.setSelection(defaultIndex);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != currentPosition) { // Tránh load lại nếu chọn lại chương hiện tại
                    currentPosition = position;
                    displayChapter(); // Hiển thị nội dung chương mới
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void playAudioFromId(int chapterid) {
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
        // Lấy đường dẫn của file âm thanh từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AudioPrefs", MODE_PRIVATE);
        String audioPath = prefs.getString("audio_path_chap_" + chapterid, null);

        if (audioPath != null) {
            File audioFile = new File(audioPath);
            if (audioFile.exists()) {
                try {
                    // Sử dụng MediaPlayer để phát âm thanh từ file
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    Log.d("AUDIO", "Đang phát âm thanh từ: " + audioFile.getAbsolutePath());

                    // Hiển thị MediaController
                    runOnUiThread(() -> {
                        FrameLayout anchor = findViewById(R.id.audioLayout); // layout chứa MediaController
                        mediaController = new MediaController(ReadStoryDownActivity.this);

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

                    // Giải phóng MediaPlayer khi hoàn thành
                    mediaPlayer.setOnCompletionListener(mp -> mp.release());

                } catch (IOException e) {
                    Log.e("AUDIO", "Lỗi khi phát âm thanh: " + e.getMessage());
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Lỗi khi phát âm thanh", Toast.LENGTH_SHORT).show());
                }
            } else {
                Log.e("AUDIO", "File âm thanh không tồn tại: " + audioFile.getAbsolutePath());
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "File âm thanh không tồn tại", Toast.LENGTH_SHORT).show());
            }
        } else {
            Log.e("AUDIO", "Không tìm thấy đường dẫn âm thanh cho chương: " + chapterid);
            runOnUiThread(() ->
                    Toast.makeText(getApplicationContext(), "Không tìm thấy âm thanh cho chương", Toast.LENGTH_SHORT).show());
        }
    }




}

