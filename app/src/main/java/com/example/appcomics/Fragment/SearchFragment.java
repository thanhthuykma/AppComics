package com.example.appcomics.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.appcomics.Adapter.CatergoryAdapter;
import com.example.appcomics.Adapter.ComicAdapter;
import com.example.appcomics.Model.Catergory;
import com.example.appcomics.Model.Comic;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private CatergoryAdapter catergoryAdapter;
    private ComicAdapter comicAdapter;
    private List<Catergory> categoryList = new ArrayList<>();
    private TextView timkiem;
    private TextView huy;
    private IComicAPI iComicAPI;
    private String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_options);
        timkiem = view.findViewById(R.id.apply_button); // Nút để áp dụng bộ lọc
        huy = view.findViewById(R.id.cancel); // Nút hủy lọc truyện

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        catergoryAdapter = new CatergoryAdapter(getContext(), categoryList);
        recyclerView.setAdapter(catergoryAdapter);



        //lấy username
        if(getArguments() != null){
            username = getArguments().getString("USERNAME");
        }

        //Tạo api
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);

        fetchCategories();

        //Lọc truyện theo thể loại

        timkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFilterRequest();
            }
        });

        huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem có fragment nào đang được hiển thị không
                if (getFragmentManager() != null) {
                    // Pop fragment khỏi back stack
                    getFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }

    //Lọc truyện
    private void sendFilterRequest() {
        // Lấy danh sách các thể loại đã chọn từ CatergoryAdapter
        List<Catergory> selectedCategories = catergoryAdapter.getSelectedCategories();
        List<Integer> categoryIds = new ArrayList<>();
        for (Catergory category : selectedCategories) {
            categoryIds.add(category.getID());
        }

        // Chuyển đổi danh sách categoryIds thành JSON array
        String jsonArray = new Gson().toJson(categoryIds);

        // Gửi yêu cầu POST đến API /filter
        iComicAPI.filterComics(jsonArray).enqueue(new Callback<List<Comic>>() {
            @Override
            public void onResponse(Call<List<Comic>> call, Response<List<Comic>> response) {
                if (response.isSuccessful()) {
                    List<Comic> comics = response.body();
                    // Gửi yêu cầu lọc khi nhấn nút
                    Fragment filterFragment = new FilterFragment();
                    ArrayList<Integer> mangaIds = new ArrayList<>();

                    for (Comic comic : comics) {
                        mangaIds.add(comic.getID());
                    }

                    Bundle args = new Bundle();
                    args.putIntegerArrayList("MANGA_IDS", mangaIds);
                    filterFragment.setArguments(args);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, filterFragment)
                            .remove(SearchFragment.this)
                            .addToBackStack(null) // Thêm FilterFragment vào back stack nếu cần quay lại
                            .commit();
                    Toast.makeText(getContext(), "Found " + comics.size() + " comics", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comic>> call, Throwable t) {
                Toast.makeText(getContext(), "Không tìm thấy truyện nào", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCategories() {
        // Gọi API để lấy danh sách thể loại
                iComicAPI.getCategories().enqueue(new Callback<List<Catergory>>() {
                    @Override
                    public void onResponse(Call<List<Catergory>> call, Response<List<Catergory>> response) {
                        if (response.isSuccessful()) {
                            categoryList.clear();
                            categoryList.addAll(response.body());
                            catergoryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Catergory>> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
