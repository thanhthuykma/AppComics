package com.example.appcomics.Model;

public class Comic1 {
    private int ID;
    private String Name;
    private String Image;
    private int views;
    private String tacgia;
    private String thongtin;
    private int favoritecount;

    public Comic1(int ID, String name, String image, int views, String tacgia, String thongtin, int favoritecount) {
        this.ID = ID;
        Name = name;
        Image = image;
        this.views = views;
        this.tacgia = tacgia;
        this.thongtin = thongtin;
        this.favoritecount = favoritecount;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
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

    public String getThongtin() {
        return thongtin;
    }

    public void setThongtin(String thongtin) {
        this.thongtin = thongtin;
    }

    public int getFavoritecount() {
        return favoritecount;
    }

    public void setFavoritecount(int favoritecount) {
        this.favoritecount = favoritecount;
    }
}
