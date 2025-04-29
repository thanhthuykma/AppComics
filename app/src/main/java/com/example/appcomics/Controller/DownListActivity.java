package com.example.appcomics.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appcomics.Adapter.ChapterAdapter;
import com.example.appcomics.Adapter.DownChapAdapter;
import com.example.appcomics.Model.ChapContent;
import com.example.appcomics.R;
import com.example.appcomics.SQLite.DatabaseHelper;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DownChapAdapter chapterAdapter;
    private List<ChapContent> chapters;
    private ImageButton btn_delete;
    private IComicAPI iComicAPI;
    private TextView textchapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_list);

        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.recycle_chapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Sử dụng LinearLayoutManager

        // Lấy danh sách các chương từ SQLite
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int mangaId = getIntent().getIntExtra("MANGA_ID", -1);
        int chapcount = getIntent().getIntExtra("CHAPTER_COUNT", 0);
        textchapter = findViewById(R.id.text_chapter);
        textchapter.setText("   Số chương("+ chapcount+ ")");
        chapters = dbHelper.getDownloadedChapters(mangaId); // Lấy danh sách chương đã tải về

        // Thiết lập Adapter cho RecyclerView
        chapterAdapter = new DownChapAdapter(this,chapters);
        recyclerView.setAdapter(chapterAdapter);
        btn_delete = findViewById(R.id.btn_delete);
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở cơ sở dữ liệu để xóa dữ liệu
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Xóa dữ liệu có mangaId tương ứng
                String selection = DatabaseHelper.COLUMN_MANGAID + " = ?";
                String[] selectionArgs = {String.valueOf(mangaId)};

                // Xóa các dòng có mangaId cụ thể
                int rowsDeleted = db.delete(DatabaseHelper.TABLE_DOWNLOADS, selection, selectionArgs);

                // Kiểm tra xem đã xóa thành công hay không
                if (rowsDeleted > 0) {
                    // Cập nhật lại danh sách các chương
                    chapters.clear(); // Xóa tất cả các phần tử trong danh sách
                    chapters.addAll(dbHelper.getDownloadedChapters(mangaId)); // Lấy lại danh sách chương từ SQLite

                    // Cập nhật adapter
                    chapterAdapter.notifyDataSetChanged();

                    Toast.makeText(DownListActivity.this, "Đã xóa truyện khỏi mục đã tải", Toast.LENGTH_SHORT).show();
                    deleteDownLoad(mangaId);
                } else {
                    Toast.makeText(DownListActivity.this, "Không có dữ liệu để xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteDownLoad(int mangaId) {
        iComicAPI.deleteDownload(mangaId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

}
