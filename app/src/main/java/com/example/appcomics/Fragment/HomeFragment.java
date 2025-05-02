package com.example.appcomics.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.appcomics.Adapter.ComicAdapter;
import com.example.appcomics.Adapter.SearchAdapter;
import com.example.appcomics.Adapter.SliderAdapter;
import com.example.appcomics.Model.Banner;
import com.example.appcomics.Model.Comic;
import com.example.appcomics.Model.Comic1;
import com.example.appcomics.Model.ComicCountResponse;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;
import com.google.android.material.tabs.TabLayout;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Handler;

public class HomeFragment extends Fragment {

    private IComicAPI iComicAPI;
    private ProgressDialog progressDialog;
    private ViewPager2 viewPager2;
    private SliderAdapter sliderAdapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewRe;
    private ComicAdapter comicAdapter;
    private DotsIndicator dotsIndicator;
    private String username;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int currentPage = 0;
    private TextView text_comic;
    private AppCompatImageButton search;
    private AppCompatImageButton filter;
    private CardView layout_search;
    private EditText searchtext;
    private String timkiem;
    private TextView timkiemcomic;
    private TextView huy;
    private RecyclerView searchrecycler;
    private AlertDialog dialog;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        viewPager2 = view.findViewById(R.id.view_pager);
        dotsIndicator = view.findViewById(R.id.dots_indicator);
        recyclerView = view.findViewById(R.id.recycle_comic);
        recyclerViewRe = view.findViewById(R.id.recycle_dexuat);
        recyclerViewRe.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        text_comic = view.findViewById(R.id.text_comic);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        //Thêm hai tab và đặt tab "Truyện mới" làm mặc định
        tabLayout.addTab(tabLayout.newTab().setText("Truyện mới").setTag("NEW"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Đề xuất").setTag("RECOMMEND"));

        // Get username from arguments
        if (getArguments() != null) {
            username = getArguments().getString("USERNAME");
        }

        //Nútvtìm kiếm
        search = view.findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_timkiem,null);
                EditText timkiem = dialogView.findViewById(R.id.comicBox);
                searchrecycler = dialogView.findViewById(R.id.recyclerViewSearchResults);
                searchrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                Button ok = dialogView.findViewById(R.id.btnReset);
                builder.setView(dialogView);
                dialog = builder.create();

                timkiem.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String query = s.toString().trim();
                        if (!query.isEmpty()) {
                            searchComics1(query);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ten = timkiem.getText().toString();
                        searhComics(ten);
                        dialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.btnhuy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow()!= null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();

            }
        });

        //Lọc thể loại
        filter= view.findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment searchFragment = new SearchFragment();
                getParentFragmentManager().beginTransaction()
                        .add(R.id.fragment_container2, searchFragment)  // Add the SearchFragment over HomeFragment
                        .addToBackStack(null)  // Add this transaction to the back stack
                        .commit();
                Bundle args = new Bundle();
                args.putString("USERNAME", username);
                searchFragment.setArguments(args);
            }
        });

        // Initialize API
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);
        fetchBanner();
        // Mặc định hiển thị RecyclerView truyện mới
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewRe.setVisibility(View.GONE);
        fetchComics();
        getComicsize();
        // Lắng nghe sự kiện chọn tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tag = tab.getTag().toString();
                if (tag.equals("NEW")) {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerViewRe.setVisibility(View.GONE);
                    fetchComics();
                    //Đếm số truyệntranh
                    getComicsize();
                } else if (tag.equals("RECOMMEND")) {
                    recyclerView.setVisibility(View.GONE);
                    recyclerViewRe.setVisibility(View.VISIBLE);
                    fetchReComics();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }
    //Tìm kiếm truyện
    private void searhComics(String timkiem){
        iComicAPI.searchComic(timkiem).enqueue(new Callback<List<Comic>>() {
            @Override
            public void onResponse(Call<List<Comic>> call, Response<List<Comic>> response) {
                if (response.isSuccessful() && response!=null){
                    List<Comic> comics = response.body();
                    // Gửi yêu cầu lọc khi nhấn nút
                    Fragment filterFragment = new FilterFragment();
                    ArrayList<Integer> mangaIds = new ArrayList<>();

                    for (Comic comic : comics) {
                        mangaIds.add(comic.getID());
                    }

                    Bundle args = new Bundle();

                    args.putIntegerArrayList("MANGA_IDS", mangaIds);
                    args.putString("USERNAME", username);
                    filterFragment.setArguments(args);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, filterFragment)
                            .addToBackStack(null) // Thêm FilterFragment vào back stack nếu cần quay lại
                            .commit();
                    Toast.makeText(getContext(), "Tìm thấy " + comics.size() + " truyện", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy truyện", Toast.LENGTH_SHORT).show();
                }
                }


            @Override
            public void onFailure(Call<List<Comic>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //search
    public void searchComics1(String charac){
        iComicAPI.searchComic1(charac).enqueue(new Callback<List<Comic1>>() {
            @Override
            public void onResponse(Call<List<Comic1>> call, Response<List<Comic1>> response) {
                if (response.isSuccessful()){
                    List<Comic1> comic1List = response.body();
                    SearchAdapter searchAdapter = new SearchAdapter(dialog.getContext(),comic1List);
                    searchrecycler.setAdapter(searchAdapter);
                    Log.d("API_RESPONSE", "Số lượng truyện: " + comic1List.size());

                } else {
                    Log.e("API_ERROR", "Phản hồi không hợp lệ: " + response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<List<Comic1>> call, Throwable t) {

            }

        });
    }

    private void fetchBanner() {
        iComicAPI.getBannerList().enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                if (response.isSuccessful()) {
                    List<Banner> banners = response.body();
                    List<String> imageUrls = new ArrayList<>();
                    for (Banner banner : banners) {
                        imageUrls.add(banner.getLink());
                    }
                    sliderAdapter = new SliderAdapter(imageUrls);
                    viewPager2.setAdapter(sliderAdapter);
                    dotsIndicator.setViewPager2(viewPager2);
                    startAutoScroll(); // Start auto-scroll after setting adapter
                } else {
                    Toast.makeText(getContext(), "Failed to load banners", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchComics() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();

        iComicAPI.getComicList().enqueue(new Callback<List<Comic>>() {
            @Override
            public void onResponse(Call<List<Comic>> call, Response<List<Comic>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    List<Comic> comics = response.body();
                    comicAdapter = new ComicAdapter(getContext(), comics, username);
                    recyclerView.setAdapter(comicAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load comics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comic>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchReComics() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();

        iComicAPI.getRecommendations(username).enqueue(new Callback<List<Comic>>() {
            @Override
            public void onResponse(Call<List<Comic>> call, Response<List<Comic>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    List<Comic> comics = response.body();
                    comicAdapter = new ComicAdapter(getContext(), comics, username);
                    recyclerViewRe.setAdapter(comicAdapter);
                    text_comic.setText("TRUYỆN ĐỀ XUẤT ("+ comics.size() + ")");
                } else {
                    Toast.makeText(getContext(), "Failed to load comics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comic>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Lây số truyện tranh
    private void getComicsize() {
        iComicAPI.getComicsize().enqueue(new Callback<List<ComicCountResponse>>() {
            @Override
            public void onResponse(Call<List<ComicCountResponse>> call, Response<List<ComicCountResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    int count = response.body().get(0).getCount();
                    text_comic.setText("TRUYỆN MỚI (" + count + ")");
                } else {
                    text_comic.setText("TRUYỆN MỚI (0)"); // Hiển thị 0 nếu không có dữ liệu
                }
            }

            @Override
            public void onFailure(Call<List<ComicCountResponse>> call, Throwable t) {
                text_comic.setText("TRUYỆN MỚI (0)"); // Hiển thị 0 trong trường hợp lỗi
                Toast.makeText(getContext(), "Failed to get comic count: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startAutoScroll() {
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager2.getAdapter() != null) {
                    int itemCount = viewPager2.getAdapter().getItemCount();
                    if (itemCount > 0) {
                        currentPage = (currentPage + 1) % itemCount;
                        viewPager2.setCurrentItem(currentPage, true);
                        handler.postDelayed(this, 3000); // Auto-scroll every 3 seconds
                    }
                }
            }
        };
        handler.postDelayed(runnable, 3000); // Start auto-scroll after 3 seconds delay
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Stop auto-scroll when Fragment is paused
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoScroll();
        fetchComics();// Restart auto-scroll when Fragment is resumed
    }
}
