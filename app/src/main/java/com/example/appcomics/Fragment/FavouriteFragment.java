package com.example.appcomics.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.appcomics.Adapter.FavouriteAdapter;
import com.example.appcomics.Model.ChapterCountResponse;
import com.example.appcomics.Model.Favourite;
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

public class FavouriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private FavouriteAdapter favouriteAdapter;
    private IComicAPI iComicAPI;
    private Map<Integer,Integer> viewsMap = new HashMap<>();
    private Map<Integer,Integer> chapsMap = new HashMap<>();
    private List<Favourite> favourites;
    String username;
    private int pendingRequests;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        //Khởi tạo recyclerview
        recyclerView = view.findViewById(R.id.recycler_favourite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Tạo api
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);

        if (getArguments() != null){
            username = getArguments().getString("USERNAME");
        }

        //Lấy dữ liệu
        fetchFavourites(username);
        return view;
    }
    private void fetchFavourites(String username){
        iComicAPI.getFavourites(username).enqueue(new Callback<List<Favourite>>() {
            @Override
            public void onResponse(Call<List<Favourite>> call, Response<List<Favourite>> response) {
                if (response.isSuccessful() && response.body() != null){
                   favourites = response.body();

                    pendingRequests = favourites.size();

                    for (Favourite favourite :favourites){
                        int mangaid = favourite.getMangaid();
                        getViewsAndChapsize(mangaid);
                    }
                }
                else{
                    Toast.makeText(getContext(),"Không thể tải dữ liệu ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Favourite>> call, Throwable t) {
              Toast.makeText(getContext(),"Error: " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getViewsAndChapsize(int mangaid){
        //Lấy lượt views
        iComicAPI.getViews(mangaid).enqueue(new Callback<List<ViewsResponse>>() {
            @Override
            public void onResponse(Call<List<ViewsResponse>> call, Response<List<ViewsResponse>> response) {
                if (response.isSuccessful() && response.body() != null){
                List<ViewsResponse> viewsResponses = response.body();
                int views = viewsResponses.get(0).getViews();
                viewsMap.put(mangaid,views);

                //Lấy số chapters
                    iComicAPI.getChapsize(mangaid).enqueue(new Callback<List<ChapterCountResponse>>() {
                        @Override
                        public void onResponse(Call<List<ChapterCountResponse>> call, Response<List<ChapterCountResponse>> response) {
                            if(response.isSuccessful() && response.body() != null){
                                List<ChapterCountResponse> chapterCountResponses = response.body();
                                int chaps = chapterCountResponses.get(0).getCount();
                                chapsMap.put(mangaid,chaps);

                                pendingRequests--;
                                //Kiểm tra pending
                                if ((pendingRequests == 0)) {
                                    favouriteAdapter = new FavouriteAdapter(favourites,getContext(),viewsMap,chapsMap);
                                    recyclerView.setAdapter(favouriteAdapter);
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
