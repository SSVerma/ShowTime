package com.ssverma.showtime.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("id")
    private String id;

    @SerializedName("iso_639_1")
    private String language;

    @SerializedName("key")
    private String videoId;

    @SerializedName("title")
    private String title;

    @SerializedName("size")
    private int size;

    public String getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }
}
