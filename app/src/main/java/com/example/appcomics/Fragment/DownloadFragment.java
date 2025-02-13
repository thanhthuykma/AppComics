package com.example.appcomics.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.appcomics.Adapter.HistoryAdapter;
import com.example.appcomics.Model.ChapterCountResponse;
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
    private List<History> histories;
    private int pendingRequests;

    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        recyclerView = view.findViewById(R.id.recycler_favourite);
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
        iComicAPI.getDownload(username).enqueue(new Callback<List<History>>() {
            @Override
            public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    histories = response.body();
                    pendingRequests = histories.size(); // Đặt số lượng yêu cầu API còn lại

                    // Nếu danh sách không rỗng thì lấy views và chapter count
                    for (History history : histories) {
                        int mangaid = history.getMangaid();
                        getViewsAndChapsize(mangaid);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<History>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getViewsAndChapsize(int mangaid) {
        // Lấy lượt view
        iComicAPI.getViews(mangaid).enqueue(new Callback<List<ViewsResponse>>() {
            @Override
            public void onResponse(Call<List<ViewsResponse>> call, Response<List<ViewsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ViewsResponse> viewsResponses = response.body();
                    int views = viewsResponses.get(0).getViews();
                    viewsMap.put(mangaid, views);  // Lưu số views cho mangaid

                    // Sau khi có views, lấy số chapter
                    iComicAPI.getChapsize(mangaid).enqueue(new Callback<List<ChapterCountResponse>>() {
                        @Override
                        public void onResponse(Call<List<ChapterCountResponse>> call, Response<List<ChapterCountResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<ChapterCountResponse> chapterCountResponses = response.body();
                                int chapters = chapterCountResponses.get(0).getCount();
                                chaptersMap.put(mangaid, chapters);  // Lưu số chapters cho mangaid

                                // Kiểm tra xem tất cả các yêu cầu đã hoàn tất
                                pendingRequests--;
                                if (pendingRequests == 0) {
                                    historyAdapter = new HistoryAdapter(histories, chaptersMap, viewsMap, getContext());
                                    recyclerView.setAdapter(historyAdapter);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<ChapterCountResponse>> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi lấy số chapter: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ViewsResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi lấy lượt view: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
