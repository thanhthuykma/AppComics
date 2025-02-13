package com.example.appcomics.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.appcomics.Adapter.CatergoryAdapter;
import com.example.appcomics.Adapter.ComicAdapter;
import com.example.appcomics.Model.Catergory;
import com.example.appcomics.Model.Comic;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterFragment extends Fragment {
    private ComicAdapter comicAdapter;
    private List<Comic> allComics = new ArrayList<>();
    private String username;
    private IComicAPI iComicAPI;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private TextView huy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        // Khởi tạo Toolbar

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true); // Để Fragment có menu

        // Hiển thị nút "Back" trên Toolbar
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        }

        // RecyclerView
        recyclerView = view.findViewById(R.id.recycle_filter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);

        // Fetch the manga IDs passed as arguments
        if (getArguments() != null) {
            username = getArguments().getString("USERNAME");
            ArrayList<Integer> mangaIds = getArguments().getIntegerArrayList("MANGA_IDS");
            if (mangaIds != null && !mangaIds.isEmpty()) {
                comicAdapter = new ComicAdapter(getContext(), allComics, username);
                recyclerView.setAdapter(comicAdapter); // Khởi tạo adapter chỉ một lần

                for (Integer mangaid : mangaIds) {
                    int mangaidInt = mangaid != null ? mangaid.intValue() : 0;
                    fetchComics(mangaidInt);
                }
            } else {
                Toast.makeText(getContext(), "No comics found.", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    private void fetchComics(int mangaid) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();

        iComicAPI.getComicsbyId(mangaid).enqueue(new Callback<List<Comic>>() {
            @Override
            public void onResponse(Call<List<Comic>> call, Response<List<Comic>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    List<Comic> comics = response.body();
                    if (comics != null) {
                        allComics.addAll(comics); // Thêm tất cả comics mới vào danh sách
                        comicAdapter.notifyDataSetChanged(); // Cập nhật adapter
                    }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // ID của nút "Back"
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack(); // Quay lại fragment trước đó
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
