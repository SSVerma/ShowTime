package com.ssverma.showtime.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideosResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Video> videos;

    public int getId() {
        return id;
    }

    public List<Video> getVideos() {
        return videos;
    }
}
