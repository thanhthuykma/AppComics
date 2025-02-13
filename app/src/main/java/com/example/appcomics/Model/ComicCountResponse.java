package com.example.appcomics.Model;

import com.google.gson.annotations.SerializedName;

public class ComicCountResponse {
    @SerializedName("COUNT(*)")
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
