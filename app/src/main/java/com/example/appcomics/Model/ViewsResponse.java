package com.example.appcomics.Model;

import com.google.gson.annotations.SerializedName;

public class ViewsResponse {
    @SerializedName("views")
    int views;

    public Integer getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}
