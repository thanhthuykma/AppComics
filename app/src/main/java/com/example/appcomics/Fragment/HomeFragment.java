package com.example.appcomics.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.appcomics.Adapter.ComicAdapter;
import com.example.appcomics.Adapter.SliderAdapter;
import com.example.appcomics.Model.Banner;
import com.example.appcomics.Model.Comic;
import com.example.appcomics.Model.ComicCountResponse;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

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
                Button ok = dialogView.findViewById(R.id.btnReset);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

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


        // Fetch banner and comics
        fetchBanner();
        fetchComics();
        //Đếm số truyệntranh
        text_comic = view.findViewById(R.id.text_comic);
        getComicsize();

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
    //Lây số truyện tranh
    private void getComicsize() {
        iComicAPI.getComicsize().enqueue(new Callback<List<ComicCountResponse>>() {
            @Override
            public void onResponse(Call<List<ComicCountResponse>> call, Response<List<ComicCountResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    int count = response.body().get(0).getCount();
                    text_comic.setText("NEW COMIC (" + count + ")");
                } else {
                    text_comic.setText("NEW COMIC (0)"); // Hiển thị 0 nếu không có dữ liệu
                }
            }

            @Override
            public void onFailure(Call<List<ComicCountResponse>> call, Throwable t) {
                text_comic.setText("NEW COMIC (0)"); // Hiển thị 0 trong trường hợp lỗi
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
