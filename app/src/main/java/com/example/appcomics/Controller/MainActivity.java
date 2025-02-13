package com.example.appcomics.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.appcomics.Fragment.DownloadFragment;
import com.example.appcomics.Fragment.FavouriteFragment;
import com.example.appcomics.Fragment.HistoryFragment;
import com.example.appcomics.Fragment.HomeFragment;
import com.example.appcomics.Fragment.UserFragment;
import com.example.appcomics.Model.RefreshTokenResponse;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String username;
    private IComicAPI iComicAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Lấy username từ Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);

        // Thiết lập BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Thiết lập Fragment mặc định là HomeFragment khi MainActivity được khởi chạy lần đầu
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home); // Đặt mục "home" được chọn mặc định
            loadFragment(new HomeFragment(), username); // Hiển thị HomeFragment với username
        }

        // Xử lý sự kiện khi người dùng chọn mục trong BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String fragmentUsername = username;

                if (item.getItemId() == R.id.home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.favourite) {
                    selectedFragment = new FavouriteFragment();
                }
                 else if (item.getItemId() == R.id.history) {
                     selectedFragment = new HistoryFragment();
                } else if (item.getItemId() == R.id.download) {
                    selectedFragment = new DownloadFragment();
                 }
                else if (item.getItemId() == R.id.account) {
                    selectedFragment = new UserFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment, fragmentUsername);
                }

                return true;
            }
        });
    }

    // Phương thức để thay thế Fragment hiện tại bằng Fragment mới và truyền username
    private void loadFragment(Fragment fragment, String username) {
        if (fragment instanceof HomeFragment || fragment instanceof FavouriteFragment || fragment instanceof HistoryFragment || fragment instanceof DownloadFragment ||
        fragment instanceof UserFragment) {
            Bundle args = new Bundle();
            args.putString("USERNAME", username);
            fragment.setArguments(args);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
    /*private void checkSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("Session", MODE_PRIVATE);
        long tokenExpireAt = sharedPreferences.getLong("TokenExpireAt", 0);
        String refreshToken = sharedPreferences.getString("RefreshToken", null);

        // Kiểm tra xem token đã hết hạn chưa
        if (System.currentTimeMillis() > tokenExpireAt) {
                // Nếu không có refresh token, yêu cầu người dùng đăng nhập lại
                Toast.makeText(this, "Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSession();
    }*/

}
