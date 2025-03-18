package com.example.appcomics.Model;

public class ChapContent {
    private int chapterid;
    private String chapter_title;
    private String content;

    public int getChapterid() {
        return chapterid;
    }

    public void setChapterid(int chapterid) {
        this.chapterid = chapterid;
    }

    public String getChapter_title() {
        return chapter_title;
    }

    public void setChapter_title(String chapter_title) {
        this.chapter_title = chapter_title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChapContent(int chapterid, String chapter_title, String content) {
        this.chapterid = chapterid;
        this.chapter_title = chapter_title;
        this.content = content;
    }
}
