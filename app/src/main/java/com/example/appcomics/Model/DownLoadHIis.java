package com.example.appcomics.Model;

public class DownLoadHIis {
    private String username;
    private String manganame;
    private int mangaid;
    private String url;
    private int views;
    private String tacgia;


    public DownLoadHIis(String username, String manganame, int mangaid, String url, int views, String tacgia) {
        this.username = username;
        this.manganame = manganame;
        this.mangaid = mangaid;
        this.url = url;
        this.views = views;
        this.tacgia = tacgia;

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

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getTacgia() {
        return tacgia;
    }

    public void setTacgia(String tacgia) {
        this.tacgia = tacgia;
    }

}
