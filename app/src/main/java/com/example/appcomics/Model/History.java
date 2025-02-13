package com.example.appcomics.Model;

public class History {
    private int mangaid;
    private String username;
    private String manganame;
    private String url;

    public History() {
    }

    public History(int mangaid, String username, String manganame, String url) {
        this.mangaid = mangaid;
        this.username = username;
        this.manganame = manganame;
        this.url = url;
    }

    public int getMangaid() {
        return mangaid;
    }

    public void setMangaid(int mangaid) {
        this.mangaid = mangaid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getManganame() {
        return manganame;
    }

    public void setManganame(String manganame) {
        this.manganame = manganame;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
