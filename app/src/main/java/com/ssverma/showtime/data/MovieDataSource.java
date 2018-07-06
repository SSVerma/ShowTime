package com.ssverma.showtime.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.ssverma.showtime.api.ApiUtils;
import com.ssverma.showtime.api.TmdbService;
import com.ssverma.showtime.common.LoadingState;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.MovieResponse;

import java.io.IOException;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

public class MovieDataSource extends PageKeyedDataSource<Integer, Movie> {

    private final TmdbService tmdbService;
    private final MutableLiveData<LoadingState> loadingStates;
    private final MutableLiveData<LoadingState> initialLoadingStates;
    private final String filter;

    public MovieDataSource(String filter) {
        this.filter = filter;
        this.tmdbService = ApiUtils.getTmdbService();
        this.loadingStates = new MutableLiveData<>();
        initialLoadingStates = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Movie> callback) {
        loadingStates.postValue(LoadingState.LOADING);
        initialLoadingStates.postValue(LoadingState.LOADING);

        Call<MovieResponse> request = tmdbService.getMovies(filter, 1);

        try {
            Response<MovieResponse> response = request.execute();
            loadingStates.postValue(LoadingState.LOADED);
            initialLoadingStates.postValue(LoadingState.LOADED);

            if (response == null) {
                callback.onResult(Collections.<Movie>emptyList(), null, 1);
                return;
            }

            if (response.body() == null) {
                callback.onResult(Collections.<Movie>emptyList(), null, 1);
                return;
            }

            if (response.body().getMoviesList() == null || response.body().getMoviesList().isEmpty()) {
                callback.onResult(Collections.<Movie>emptyList(), null, 1);
                return;
            }

            callback.onResult(response.body().getMoviesList(), null, 1);

        } catch (IOException e) {
            e.printStackTrace();
            loadingStates.postValue(LoadingState.FAILED);
            initialLoadingStates.postValue(LoadingState.FAILED);
        }

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movie> callback) {
        //
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movie> callback) {
        loadingStates.postValue(LoadingState.LOADING);

        Call<MovieResponse> request = tmdbService.getMovies(filter, params.key + 1);

        try {
            Response<MovieResponse> response = request.execute();
            loadingStates.postValue(LoadingState.LOADED);

            if (response == null) {
                callback.onResult(Collections.<Movie>emptyList(), params.key + 1);
                return;
            }

            if (response.body() == null) {
                callback.onResult(Collections.<Movie>emptyList(), params.key + 1);
                return;
            }

            if (response.body().getMoviesList() == null || response.body().getMoviesList().isEmpty()) {
                callback.onResult(Collections.<Movie>emptyList(), params.key + 1);
                return;
            }

            callback.onResult(response.body().getMoviesList(), params.key + 1);

        } catch (IOException e) {
            e.printStackTrace();
            loadingStates.postValue(LoadingState.FAILED);
        }
    }

    public MutableLiveData<LoadingState> getLoadingStates() {
        return loadingStates;
    }

    public MutableLiveData<LoadingState> getInitialLoadingStates() {
        return initialLoadingStates;
    }
}
