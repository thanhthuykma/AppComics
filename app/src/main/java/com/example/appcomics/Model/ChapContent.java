package com.example.appcomics.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ChapContent implements Parcelable {
    private int chapterid;
    private String chapter_title;
    private String content;


    // Constructor đầy đủ
    public ChapContent(int chapterid, String chapter_title, String content) {
        this.chapterid = chapterid;
        this.chapter_title = chapter_title;
        this.content = content;
    }

    // Constructor từ Parcel
    protected ChapContent(Parcel in) {
        chapterid = in.readInt();
        chapter_title = in.readString();
        content = in.readString();
    }

    // Creator để hỗ trợ Parcelable
    public static final Creator<ChapContent> CREATOR = new Creator<ChapContent>() {
        @Override
        public ChapContent createFromParcel(Parcel in) {
            return new ChapContent(in);
        }

        @Override
        public ChapContent[] newArray(int size) {
            return new ChapContent[size];
        }
    };

    // Getter và Setter
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

    // Ghi dữ liệu vào Parcel
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(chapterid);
        dest.writeString(chapter_title);
        dest.writeString(content);
    }

    // Mô tả nội dung (không cần sửa)
    @Override
    public int describeContents() {
        return 0;
    }
}
