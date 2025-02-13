package com.example.appcomics.Model;

public class Banner {
    private int ID;
    private String Link;

    public Banner(int ID, String link) {
        this.ID = ID;
        Link = link;
    }

    public Banner() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
