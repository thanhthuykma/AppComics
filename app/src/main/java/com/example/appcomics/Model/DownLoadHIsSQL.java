package com.example.appcomics.Model;

public class DownLoadHIsSQL {
    private int mangaid;
    private String manganame;
    private int views;
    private String image;
    private String tacgia;

    public DownLoadHIsSQL(int mangaid, String manganame, int views, String image,String tacgia) {
        this.mangaid = mangaid;
        this.manganame = manganame;
        this.views = views;
        this.image = image;
        this.tacgia = tacgia;
    }

    public int getMangaid() {
        return mangaid;
    }

    public void setMangaid(int mangaid) {
        this.mangaid = mangaid;
    }

    public String getManganame() {
        return manganame;
    }

    public void setManganame(String manganame) {
        this.manganame = manganame;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTacgia() {
        return tacgia;
    }

    public void setTacgia(String tacgia) {
        this.tacgia = tacgia;
    }
}
