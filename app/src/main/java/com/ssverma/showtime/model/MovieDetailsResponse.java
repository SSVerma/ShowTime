package com.ssverma.showtime.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetailsResponse {

    @SerializedName("adult")
    private boolean isAdult;

    @SerializedName("budget")
    private int budget;

    @SerializedName("original_language")
    private String language;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("revenue")
    private int revenue;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("status")
    private String status;

    @SerializedName("tagline")
    private String tagLine;

    @SerializedName("vote_average")
    private float voteAvg;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("genres")
    private List<Genre> genres;

    public boolean isAdult() {
        return isAdult;
    }

    public int getBudget() {
        return budget;
    }

    public String getLanguage() {
        return language;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getRevenue() {
        return revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getStatus() {
        return status;
    }

    public String getTagLine() {
        return tagLine;
    }

    public float getVoteAvg() {
        return voteAvg;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public List<Genre> getGenres() {
        return genres;
    }
}
