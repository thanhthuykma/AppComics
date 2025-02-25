package com.example.appcomics.Model;

public class ChapterContent {
    private String Name;
    private String noidung;

    public ChapterContent(String name, String noidung) {
        Name = name;
        this.noidung = noidung;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }
}
