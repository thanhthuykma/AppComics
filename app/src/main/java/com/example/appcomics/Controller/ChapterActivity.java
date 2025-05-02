package com.example.appcomics.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appcomics.Adapter.ChapterAdapter;
import com.example.appcomics.Model.ChapContent;
import com.example.appcomics.Model.Chapter;
import com.example.appcomics.Model.Download;
import com.example.appcomics.Model.Favourite;
import com.example.appcomics.Model.History;
import com.example.appcomics.Model.LinkResponse;
import com.example.appcomics.Model.PdfCreator;
import com.example.appcomics.R;
import com.example.appcomics.SQLite.DatabaseHelper;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.appcomics.Model.ChapterCountResponse;

public class ChapterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Chapter> chapterList;
    private ChapterAdapter chapterAdapter;
    private IComicAPI iComicAPI;
    private int mangaid;
    private String username;
    private String name;
    private String historyimage;
    private String tacgia;
    private int views;
    private ImageButton buttondownload;
    private ImageButton buttonfav;
    private ImageButton btncomment;
    private TextView text_chapter;
    private List<String> linkDownload = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private static final int REQUEST_CODE_PERMISSION = 100;//người dùng đồng ý truy cập bộ nhớ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        if (source.equals("comic")) {
            mangaid = intent.getIntExtra("mangaid", 0);
            name = intent.getStringExtra("name");
            username = intent.getStringExtra("username");
            historyimage = intent.getStringExtra("imagehistory");
            tacgia = intent.getStringExtra("tacgia");
            views = intent.getIntExtra("views",0);
        } else if (source.equals("history")) {
            mangaid = intent.getIntExtra("mangaidhis", 0);
            name = intent.getStringExtra("namehis");
            username = intent.getStringExtra("usernamehis");
            historyimage = intent.getStringExtra("imagehistoryhis");
        } else if (source.equals("fav")) {
            mangaid = intent.getIntExtra("mangaidfav", 0);
            name = intent.getStringExtra("namefav");
            username = intent.getStringExtra("usernamefav");
            historyimage = intent.getStringExtra("imagehistoryfav");
        }

        // Image button
        buttondownload = findViewById(R.id.download);
        buttonfav = findViewById(R.id.btn_favorite);
        btncomment = findViewById(R.id.btn_comment);

        //Comment
        btncomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ChapterActivity.this,CommentActivity.class);
                intent.putExtra("mangaid",mangaid);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

        //Dowload truyện
        buttondownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadChapter(mangaid);
                insertDownload(username,name,mangaid,historyimage,views,tacgia);
            }
        });

        // Cập nhật trạng thái nút yêu thích khi activity được tạo
        updateFavouriteButtonState();

        // Nút yêu thích
        buttonfav.setOnClickListener(new View.OnClickListener() {
            boolean ischecked = loadFavouriteState(); // Tải trạng thái từ SharedPreferences

            @Override
            public void onClick(View v) {
                ischecked = !ischecked; // Đảo ngược trạng thái
                buttonfav.setSelected(ischecked);
                buttonfav.setImageResource(ischecked ? R.drawable.filled_heart : R.drawable.heart_outline);
                saveFavouriteState(ischecked); // Lưu trạng thái mới

                if (ischecked) {
                    insertFavourite(mangaid, username, name, historyimage);
                } else {
                    deleteFavourite(mangaid);
                }
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Cấu hình Recycler
        recyclerView = findViewById(R.id.recycle_chapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Gọi API lấy danh sách chương
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);
        fetchChaptersByMangaId(mangaid);
        //api thêm thông tin vào bảng history
        insertHistory(mangaid, username, name, historyimage);
        //Lấy số chap
        text_chapter = findViewById(R.id.text_chapter);
        getChapsize(mangaid);
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
    //API tải các chapter
    private void downloadChapter(int mangaid) {
        iComicAPI.getLinkDownload(mangaid).enqueue(new Callback<List<ChapContent>>() {
            @Override
            public void onResponse(Call<List<ChapContent>> call, Response<List<ChapContent>> response) {
                if (response.isSuccessful()) {
                    List<ChapContent> chapters = response.body();
                    if (chapters != null && !chapters.isEmpty()) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        for (ChapContent chapter : chapters) {
                            final int chapterid = chapter.getChapterid();
                            final String chapterTitle = chapter.getChapter_title();
                            final String content = chapter.getContent();

                            // Khai báo các biến cần thiết final để dùng trong async
                            final String finalTacgia = tacgia;
                            final int finalViews = views;
                            final String finalHistoryImage = historyimage;

                            String checkQuery = "SELECT * FROM " + DatabaseHelper.TABLE_DOWNLOADS +
                                    " WHERE " + DatabaseHelper.COLUMN_MANGAID + " = ? AND " +
                                    DatabaseHelper.COLUMN_CHAPTERID + " = ?";
                            Cursor cursor = db.rawQuery(checkQuery, new String[]{String.valueOf(mangaid), String.valueOf(chapterid)});

                            if (cursor.getCount() == 0) {
                                cursor.close();

                                iComicAPI.getAudio(chapterid).enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            try {
                                                byte[] audioData = response.body().bytes();

                                                // Tạo đường dẫn file để lưu âm thanh
                                                File audioFile = new File(getFilesDir(), "audio_" + chapterid + ".mp3");
                                                FileOutputStream fos = new FileOutputStream(audioFile);
                                                fos.write(audioData);
                                                fos.close();

                                                Log.d("AUDIO", "File âm thanh đã lưu tại: " + audioFile.getAbsolutePath());

                                                // Lưu đường dẫn file vào SharedPreferences
                                                SharedPreferences prefs = getSharedPreferences("AudioPrefs", MODE_PRIVATE);
                                                prefs.edit().putString("audio_path_chap_" + chapterid, audioFile.getAbsolutePath()).apply();

                                                // Lưu các thông tin chương vào SQLite như cũ (ngoại trừ audioData)
                                                ContentValues values = new ContentValues();
                                                values.put(DatabaseHelper.COLUMN_MANGAID, mangaid);
                                                values.put(DatabaseHelper.COLUMN_CHAPTERID, chapterid);
                                                values.put(DatabaseHelper.COLUMN_CHAPTER_TITLE, chapterTitle);
                                                values.put(DatabaseHelper.COLUMN_CONTENT, content);
                                                values.put(DatabaseHelper.COLUMN_TAC_GIA, finalTacgia);
                                                values.put(DatabaseHelper.COLUMN_VIEWS, finalViews);
                                                values.put(DatabaseHelper.COLUMN_IMAGES, finalHistoryImage);

                                                long result = db.insert(DatabaseHelper.TABLE_DOWNLOADS, null, values);
                                                if (result != -1) {
                                                    Log.d("SQLite", "Đã lưu chương: " + chapterTitle);
                                                } else {
                                                    Log.e("SQLite", "Lỗi khi lưu chương: " + chapterTitle);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                Log.e("AUDIO", "Lỗi khi đọc dữ liệu âm thanh.");
                                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Lỗi khi đọc dữ liệu âm thanh", Toast.LENGTH_SHORT).show());
                                            }
                                        } else {
                                            Log.e("AUDIO", "Không nhận được dữ liệu âm thanh từ API.");
                                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Không thể tải âm thanh cho chương: " + chapterTitle, Toast.LENGTH_SHORT).show());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        t.printStackTrace();
                                        Log.e("AUDIO", "Lỗi khi gọi API audio: " + t.getMessage());
                                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Lỗi mạng khi tải âm thanh", Toast.LENGTH_SHORT).show());
                                    }
                                });


                            } else {
                                cursor.close();
                                Log.d("SQLite", "Chương " + chapterTitle + " đã tồn tại, không lưu lại.");
                            }
                        }

                        runOnUiThread(() -> Toast.makeText(ChapterActivity.this, "Đang tải các chương...", Toast.LENGTH_SHORT).show());
                        runOnUiThread(() -> Toast.makeText(ChapterActivity.this, "Tải về thành công", Toast.LENGTH_SHORT).show());


                    } else {
                        Toast.makeText(ChapterActivity.this, "Không có chương nào để tải", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChapterActivity.this, "Không thể lấy danh sách chương", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChapContent>> call, Throwable t) {
                Toast.makeText(ChapterActivity.this, "Lỗi tải danh sách chương: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





    //Gọi API đếm số chương
    private void getChapsize(int mangaid) {
        iComicAPI.getChapsize(mangaid).enqueue(new Callback<List<ChapterCountResponse>>() {
            @Override
            public void onResponse(Call<List<ChapterCountResponse>> call, Response<List<ChapterCountResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    int count = response.body().get(0).getCount(); // Lấy giá trị "COUNT(ID)" từ đối tượng đầu tiên
                    text_chapter.setText("CHAPTER " + "(" + String.valueOf(count) + ")" + "  "); // Cập nhật TextView
                    Log.d("Chapter Count", "Number of chapters: " + count);
                } else {
                    Log.e("API Error", "Response is not successful or body is empty");
                }
            }

            @Override
            public void onFailure(Call<List<ChapterCountResponse>> call, Throwable t) {
                Log.e("API Error", "Failed to get chapter size: " + t.getMessage());
            }
        });
    }

    //Thêm vào bảng history
    private void insertDownload(String username,String manganame, int mangaid,String url,int views, String tacgia){
        Download download = new Download(username,manganame,mangaid,url,views,tacgia);
        iComicAPI.insertDownload(download).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("Download", "inserted in to download");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    // Gọi API để lấy danh sách chapter
    private void fetchChaptersByMangaId(int mangaid) {
        iComicAPI.getChaptersByMangaId(mangaid).enqueue(new Callback<List<Chapter>>() {
            @Override
            public void onResponse(Call<List<Chapter>> call, Response<List<Chapter>> response) {
                if (response.isSuccessful()) {
                    chapterList = response.body();
                    chapterAdapter = new ChapterAdapter(ChapterActivity.this, chapterList);
                    recyclerView.setAdapter(chapterAdapter);
                    // Update lượt xem
                    fetchView(mangaid);
                }
            }

            @Override
            public void onFailure(Call<List<Chapter>> call, Throwable t) {
                Toast.makeText(ChapterActivity.this, "Truyện chưa được cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //API thêm vào bảng lịch sử
    private void insertHistory(int mangaid , String username, String name, String historyimage){
        History history = new History(mangaid,username,name,historyimage);
        iComicAPI.insertHistory(history).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(ChapterActivity.this, "Đã thêm vào lịch sử", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChapterActivity.this, "Lỗi : "+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update lượt xem
    private void fetchView(int mangaid) {
        iComicAPI.updateViews(mangaid).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ViewUpdate", "Views updated successfully for manga ID: " + mangaid);
                    // Gửi broadcast sau khi cập nhật lượt xem
                    Intent intent = new Intent("VIEW_UPDATE_ACTION");
                    LocalBroadcastManager.getInstance(ChapterActivity.this).sendBroadcast(intent);
                } else {
                    Log.e("ViewUpdate", "Failed to update views: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ViewUpdate", "Error updating views", t);
            }
        });
    }

    // Thêm vào bảng yêu thích
    private void insertFavourite(int mangaid, String username, String manganame,String url) {
        Favourite favourites = new Favourite(mangaid, username, manganame , url);
        iComicAPI.insertFavourites(favourites).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChapterActivity.this, "Đã thêm vào mục yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChapterActivity.this, "Thêm mục yêu thích thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChapterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xóa khỏi mục yêu thích
    private void deleteFavourite(int mangaid) {
        Log.d("DeleteFavourite", "Deleting mangaid: " + mangaid);
        iComicAPI.deleteFav(mangaid).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChapterActivity.this, "Bỏ thích!", Toast.LENGTH_SHORT).show();
                } else {
                    // Hiển thị thông báo lỗi chi tiết
                    String errorMessage = "Lỗi bỏ thích: " + response.code() + " " + response.message();
                    Toast.makeText(ChapterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("DeleteFavourite", "Response error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChapterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DeleteFavourite", "Failure: " + t.getMessage(), t);
            }
        });
    }


    // Lưu trạng thái yêu thích vào SharedPreferences
    private void saveFavouriteState(boolean isChecked) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFavourite_" + mangaid, isChecked); // Lưu trạng thái với ID của manga
        editor.apply();
    }

    // Tải trạng thái yêu thích từ SharedPreferences
    private boolean loadFavouriteState() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isFavourite_" + mangaid, false); // Mặc định là false nếu không có trạng thái lưu
    }

    // Cập nhật trạng thái của nút yêu thích khi activity được tạo
    private void updateFavouriteButtonState() {
        boolean isChecked = loadFavouriteState();
        buttonfav.setSelected(isChecked);
        buttonfav.setImageResource(isChecked ? R.drawable.filled_heart : R.drawable.heart_outline);
    }

}
