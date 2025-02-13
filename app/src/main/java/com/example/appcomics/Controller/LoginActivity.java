package com.example.appcomics.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.appcomics.Model.ForgotPasswordRequest;
import com.example.appcomics.Model.LoginResponse;
import com.example.appcomics.Model.VerifyCodeRequest;
import com.example.appcomics.R;
import com.example.appcomics.retrofit.IComicAPI;
import com.example.appcomics.retrofit.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;


import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private Button loginbutton ;
    private Button regbutton;
    private IComicAPI iComicAPI;
    private EditText username;
    private EditText password;
    private TextView forgetpass;
    private ImageButton btnvantay;
    private int counter = 0; // Biến đếm số lần đăng nhập thất bại
    private final int MAX_ATTEMPTS = 3; // Số lần thử tối đa
    private boolean verify =false;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginbutton = findViewById(R.id.btnDangNhap);
        username = findViewById(R.id.txtTaikhoan);
        password = findViewById(R.id.txtMatKhau);
        forgetpass = findViewById(R.id.tvForgotPassword);
        btnvantay = findViewById(R.id.btnFingerprint);

        // Kiểm tra xem thiết bị có hỗ trợ vân tay không
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                //Toast.makeText(this, "Thiết bị hỗ trợ vân tay", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Thiết bị không hỗ trợ vân tay", Toast.LENGTH_SHORT).show();
                return;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Vân tay hiện không khả dụng", Toast.LENGTH_SHORT).show();
                return;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Chưa đăng ký vân tay", Toast.LENGTH_SHORT).show();
                return;
        }

        // Thực hiện khi người dùng nhấn nút
        btnvantay.setOnClickListener(v -> authenticateWithFingerprint());

        //Quên mật khẩu
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot,null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userEmail = emailBox.getText().toString();
                       //ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(userEmail);

                        //Gửi mã xác thực tới email
                        sendEmail(userEmail);
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

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()//yêu cầu quyền truy cập email của người dùng.
                .build();

        // Lấy GoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Gán sự kiện cho nút Google Sign-In
        findViewById(R.id.btnGoogleSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        iComicAPI = RetrofitClient.getClient().create(IComicAPI.class);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logincheck();
            }
        });

        //Đăng ký tài khoản
        regbutton = findViewById(R.id.btnDangKy);
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    // Hàm xác thực vân tay

    //Gửi email mã xác thực
    private void sendEmail(String email){
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(email);
        iComicAPI.forgotPassword(forgotPasswordRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Check if the response was successful
                if (response.isSuccessful()) {
                    // Extract the response body if needed (for displaying messages)
                    try {
                        String responseMessage = response.body().string();
                        Toast.makeText(getApplicationContext(), "Đã gửi mã xác thực khôi phục. Vui lòng kiểm tra hộp thư!", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        View dialogView = getLayoutInflater().inflate(R.layout.dialog_verify,null);
                        EditText verify = dialogView.findViewById(R.id.verifyBox);
                        EditText newpass = dialogView.findViewById(R.id.passBox);
                        EditText repass = dialogView.findViewById(R.id.repassbox);

                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();
                        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String vericode = verify.getText().toString();
                                String pass = newpass.getText().toString();
                                String confirm = repass.getText().toString();
                                // kiểm tra mã xác thực và mật khẩu
                                checkVerification(email,vericode,pass,confirm);

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


                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to read response.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle specific error responses such as 404 for email not found
                    if (response.code() == 404) {
                        Toast.makeText(getApplicationContext(), "Email không tồn tại trong hệ thống.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Gửi mã xác thc thất bại. Hãy thử lại", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle any failure during the API call (e.g., no internet connection)
                Toast.makeText(getApplicationContext(), "Failed to connect to the server. Please try again.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    //Kiểm tra mã xác thực
    private void checkVerification(String email, String verify, String newPass, String confirm) {
        // Kiểm tra mật khẩu mới và mật khẩu xác nhận có trùng khớp không
        if (!newPass.equals(confirm)) {
            Toast.makeText(getApplicationContext(), "Mật khẩu mới và mật khẩu xác nhận không trùng nhau. Vui lòng kiểm tra lại.", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục gọi API nếu mật khẩu không trùng khớp
        }
        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest(email, verify, newPass, confirm);

        iComicAPI.verifyCode(verifyCodeRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseMessage = response.body().string();
                        Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to read server response.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        // Handle errors such as mismatched passwords or invalid verification code
                        String errorMessage = response.errorBody().string();
                        if (response.code() == 400) {
                            // Display the error message sent by the server (e.g., invalid code, password mismatch)
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Đổi mật khẩu thất bại. Hãy thử lại", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to read error response.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network or server connection failures
                Toast.makeText(getApplicationContext(), "Connection failed. Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
    public void saveTokenWithExpiration(String accessToken) {
        try {
            // Giải mã JWT token
            DecodedJWT decodedJWT = JWT.decode(accessToken);

            // Lấy thời gian hết hạn từ token (trong Unix time, tính bằng mili giây)
            Date expirationDate = decodedJWT.getExpiresAt();

            if (expirationDate != null) {
                long tokenExpireAt = expirationDate.getTime(); // Thời gian hết hạn trong mili giây

                // Lưu thời gian hết hạn vào SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("Session", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("AccessToken", accessToken);
                editor.putLong("TokenExpireAt", tokenExpireAt);
                editor.apply();

                // In ra thời gian hết hạn để kiểm tra
                Log.d("TokenExpireAt", "Token Expire At: " + tokenExpireAt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logincheck() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (counter < MAX_ATTEMPTS) {
            iComicAPI.login(user, pass).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse != null && loginResponse.isSuccess()) {
                            String accessToken = loginResponse.getAccessToken();
                            String refreshToken = loginResponse.getRefreshToken();

                            // Lưu token và thời gian hết hạn vào SharedPreferences
                            saveTokenWithExpiration(accessToken);

                            // Lưu refresh token vào SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("Session", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("RefreshToken", refreshToken);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        counter++;
                        int remainingAttempts = MAX_ATTEMPTS - counter;
                        if (remainingAttempts > 0) {
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu chưa đúng. Bạn còn " + remainingAttempts + " lần thử.", Toast.LENGTH_SHORT).show();
                        } else {
                           //loginbutton.setEnabled(false);
                            showLockoutDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            loginbutton.setEnabled(false);
            showLockoutDialog();
        }
    }

    private void showLockoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Tài khoản bị khóa");
        builder.setMessage("Bạn đã nhập sai quá số lần cho phép. Vui lòng đổi mật khẩu để tiếp tục sử dụng.");

        builder.setPositiveButton("Đổi mật khẩu", (dialogInterface, i) -> {
            showForgotPasswordDialog();
        });

        builder.setNegativeButton("Hủy", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
        EditText emailBox = dialogView.findViewById(R.id.emailBox);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnReset).setOnClickListener(v -> {
            String userEmail = emailBox.getText().toString();
            // Gửi mã xác thực tới email
            sendEmail(userEmail);
        });

        dialogView.findViewById(R.id.btnhuy).setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
    }


    // Phương thức khởi tạo Google Sign-In
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Xử lý kết quả sau khi đăng nhập
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    // Xử lý kết quả đăng nhập
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Đăng nhập thành công
            Log.d("GoogleSignIn", "signInResult: success, Account: " + account.getEmail());
            // Chuyển đến MainActivity và gửi username là địa chỉ email
            SharedPreferences sharedPreferences = getSharedPreferences("Session", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("TokenExpireAt", System.currentTimeMillis() + 10 * 1000);
            editor.apply();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", account.getEmail()); // Lưu tên Gmail
            startActivity(intent);
            finish(); // Kết thúc LoginActivity
        } catch (ApiException e) {
            // Đăng nhập thất bại
            Log.w("GoogleSignIn", "signInResult: failed code=" + e.getStatusCode());
            //Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }
    private void authenticateWithFingerprint() {
        Executor executor = ContextCompat.getMainExecutor(this);
        String user = username.getText().toString().trim();

        if(user.isEmpty()){
            Toast.makeText(LoginActivity.this,"Mời bạn nhập tên tài khoản",Toast.LENGTH_SHORT).show();;
        }else {
            iComicAPI.usernameCheck(user).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse != null && loginResponse.isSuccess()) {
                            verify = true;
                            //Toast.makeText(LoginActivity.this, loginResponse.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            verify = false;
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {

                }
            });
        }

        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {

                            if(verify){
                                super.onAuthenticationSucceeded(result);
                                SharedPreferences sharedPreferences = getSharedPreferences("Session", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putLong("TokenExpireAt", System.currentTimeMillis() + 10 * 1000);
                                editor.apply();
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("username", user);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this,"Tài khoản chưa đúng",Toast.LENGTH_SHORT).show();
                            }

                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        //Toast.makeText(LoginActivity.this, "Lỗi: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(LoginActivity.this, "Xác thực thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });

        // Hiển thị hộp thoại xác thực vân tay
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng nhập bằng vân tay")
                .setSubtitle("Chạm vào cảm biến vân tay để tiếp tục")
                .setNegativeButtonText("Hủy")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

}
