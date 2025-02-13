package com.example.appcomics.Model;

public class Chapter {
    private int ID;
    private String Name;
    private int MangaID;

    public Chapter() {
    }

    public Chapter(int ID, String name, int mangaId) {
        this.ID = ID;
        Name = name;
        MangaID = mangaId;
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

    public int getMangaId() {
        return MangaID;
    }

    public void setMangaId(int mangaId) {
        MangaID = mangaId;
    }
}
