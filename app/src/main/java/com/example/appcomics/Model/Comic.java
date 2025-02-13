package com.example.appcomics.Model;

public class Comic {
    private int ID;
    private String Name;
    private String Image;
    private int views;

    public Comic() {
    }


    public Comic(int ID, String name, String image, int views) {
        this.ID = ID;
        Name = name;
        Image = image;
        this.views = views;
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
}
