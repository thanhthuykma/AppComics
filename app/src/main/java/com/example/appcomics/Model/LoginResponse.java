package com.example.appcomics.Model;

public class LoginResponse {
    private boolean success;
    private String message;
    private String accessToken; // Thêm trường accessToken
    private String refreshToken; // Thêm trường refreshToken

    // Getter và Setter cho success
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Getter và Setter cho message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter và Setter cho accessToken
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter và Setter cho refreshToken
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
