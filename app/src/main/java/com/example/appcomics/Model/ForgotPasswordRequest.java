package com.example.appcomics.Model;

public class ForgotPasswordRequest {
    private String email;


    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
