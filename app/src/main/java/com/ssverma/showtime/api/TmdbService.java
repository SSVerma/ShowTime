package com.ssverma.showtime.api;

import com.ssverma.showtime.BuildConfig;
import com.ssverma.showtime.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TmdbService {
    @GET("movie/popular?api_key=" + BuildConfig.TmdbApiKey)
    Call<MovieResponse> getPopularMovies(@Query("page") int pageNumber);

    @GET("movie/top_rated?api_key=" + BuildConfig.TmdbApiKey)
    Call<MovieResponse> getTopRatedMovies(@Query("page") int pageNumber);
}
