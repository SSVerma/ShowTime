package com.ssverma.showtime.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {

    @SerializedName("page")
    private int pageNumber;

    @SerializedName("results")
    private List<Movie> moviesList;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    public int getPageNumber() {
        return pageNumber;
    }

    public List<Movie> getMoviesList() {
        return moviesList;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
