package com.example.appcomics.Model;

public class Download {
    private String username;
    private String manganame;
    private int mangaid;
    private String url;

    public Download() {
    }

    public Download(String username, String manganame, int mangaid,String url) {
        this.username = username;
        this.manganame = manganame;
        this.mangaid = mangaid;
        this.url = url;
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

    public int getMangaid() {
        return mangaid;
    }

    public void setMangaid(int mangaid) {
        this.mangaid = mangaid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
