package com.example.appcomics.Fragment;

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
import com.example.appcomics.Model.History;
import com.example.appcomics.Model.ViewsResponse;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

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

        //Tạo api
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);

        if (getArguments() != null) {
            username = getArguments().getString("USERNAME");
        }

        //Lấy dữ liệu từ bảng history
        getDownload(username);

        return view;
    }

    public void getDownload(String username) {
        iComicAPI.getDownload(username).enqueue(new Callback<List<DownLoadHIis>>() {
            @Override
            public void onResponse(Call<List<DownLoadHIis>> call, Response<List<DownLoadHIis>> response) {
                if (response.isSuccessful() && response.body() != null) {
                   List<DownLoadHIis> downloads = response.body();
                    // In ra dữ liệu trả về để kiểm tra
                    for (DownLoadHIis comic : downloads) {
                        Log.d("Download", "Title: " + comic.getManganame() + ", Views: " + comic.getViews());
                    }
                    DownLoadAdapter downLoadAdapter = new DownLoadAdapter(getContext(),downloads);
                    recyclerView.setAdapter(downLoadAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<DownLoadHIis>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
