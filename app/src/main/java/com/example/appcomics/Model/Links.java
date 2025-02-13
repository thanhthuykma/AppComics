package com.example.appcomics.Model;

public class Links {
    private int ID;
    private String Link;
    private int ChapterId;

    public Links() {
    }

    public Links(int ID, String link, int chapterId) {
        this.ID = ID;
        Link = link;
        ChapterId = chapterId;
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

    public void setLinks(String link) {
        Link = link;
    }

    public int getChapterId() {
        return ChapterId;
    }

    public void setChapterId(int chapterId) {
        ChapterId = chapterId;
    }
}
