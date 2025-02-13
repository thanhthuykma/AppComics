package com.example.appcomics.Model;

import com.google.gson.annotations.SerializedName;

public class LinkResponse {
    @SerializedName("Link")
    private String Link;

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
