package com.example.appcomics.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.appcomics.Adapter.DownLoadAdapter;
import com.example.appcomics.Adapter.HistoryAdapter;
import com.example.appcomics.Model.ChapterCountResponse;
import com.example.appcomics.Model.Comic1;
import com.example.appcomics.Model.DownLoadHIis;
import com.example.appcomics.Model.DownLoadHIsSQL;
import com.example.appcomics.Model.History;
import com.example.appcomics.Model.ViewsResponse;
import com.example.appcomics.R;
import com.example.appcomics.SQLite.DatabaseHelper;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DownloadFragment extends Fragment {
    private IComicAPI iComicAPI;
    private HistoryAdapter historyAdapter;
    private RecyclerView recyclerView;
    private String username;
    private Map<Integer, Integer> chaptersMap = new HashMap<>();
    private Map<Integer, Integer> viewsMap = new HashMap<>();
    private List<Comic1> download;
    private int pendingRequests;
    private DatabaseHelper dbHelper;

    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        recyclerView = view.findViewById(R.id.recycler_download);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbHelper = new DatabaseHelper(getContext());

        //Tạo api
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);

        if (getArguments() != null) {
            username = getArguments().getString("USERNAME");
        }

        //Lấy dữ liệu từ bảng history
        getDownload();

        return view;
    }

    public void getDownload() {
        List<DownLoadHIsSQL> downloads = new ArrayList<>();

        // Mở kết nối tới database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Truy vấn dữ liệu từ bảng downloads với điều kiện username nếu cần (hoặc bỏ điều kiện nếu không lưu username trong bảng)
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_DOWNLOADS;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int mangaid = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MANGAID));
                String manganame = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHAPTER_TITLE));
                int views = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VIEWS));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGES));
                String tacgia = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TAC_GIA));

                DownLoadHIsSQL comic = new DownLoadHIsSQL(mangaid, manganame, views, image,tacgia);
                downloads.add(comic);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        // In ra dữ liệu kiểm tra
        for (DownLoadHIsSQL comic : downloads) {
            Log.d("Download-SQLite", "Title: " + comic.getManganame() + ", Views: " + comic.getViews());
        }

        // Cập nhật RecyclerView
        DownLoadAdapter downLoadAdapter = new DownLoadAdapter(getContext(), downloads);
        recyclerView.setAdapter(downLoadAdapter);


    }
}
