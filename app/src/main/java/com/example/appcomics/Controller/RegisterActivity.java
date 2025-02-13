package com.example.appcomics.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appcomics.Model.RegisterResponse;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;

import java.io.IOException;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText repass;
    private EditText editTextemail;
    private String user,pass,repassw,email;
    private Button reg;
    private IComicAPI iComicAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.txtMatKhau);
        repass = findViewById(R.id.txtMatKhau2);
        editTextemail = findViewById(R.id.email);
        reg = findViewById(R.id.btnDangKy);

        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);

        //Đăng ký
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();
                repassw = repass.getText().toString();
                email = editTextemail.getText().toString();
                if (user.isEmpty()||pass.isEmpty()||repassw.isEmpty()||email.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(user,email,pass,repassw);
                }

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//hiển thị nút quay lai
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_reg);

    }
    private void register(String user,String email,String pass,String repassw){
        iComicAPI.register(user,email,pass,repassw).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Thông báo thành công
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý phản hồi không thành công
                    String errorMessage;
                    if (response.errorBody() != null) {
                        try {
                            // Chuyển đổi lỗi thành chuỗi
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());

                            // Lấy thông báo lỗi từ trường "message"
                             errorMessage = jsonObject.getString("message");

                            // Hiển thị thông báo lỗi
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        } catch (IOException | JSONException e) {
                            Log.e("RegisterActivity", "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký không thành công. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}