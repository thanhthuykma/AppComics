package com.example.appcomics.Model;

public class VerifyCodeRequest {
    private String email;
    private String verificationCode;
    private String newPassword;
    private String confirmPassword;

    // Constructor
    public VerifyCodeRequest(String email, String verificationCode, String newPassword, String confirmPassword) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
