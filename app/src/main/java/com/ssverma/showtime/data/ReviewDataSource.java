package com.ssverma.showtime.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.ssverma.showtime.api.ApiUtils;
import com.ssverma.showtime.api.TmdbService;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.ReviewsResponse;

import java.io.IOException;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

public class ReviewDataSource extends PageKeyedDataSource<Integer, Review> {

    private final TmdbService tmdbService;
    private final MutableLiveData<NetworkState> networkState;
    private final int movieId;

    ReviewDataSource(int movieId) {
        this.movieId = movieId;
        tmdbService = ApiUtils.getTmdbService();
        networkState = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Review> callback) {
        networkState.postValue(NetworkState.LOADING);

        Call<ReviewsResponse> request = tmdbService.getReviews(movieId, 1);

        try {
            Response<ReviewsResponse> response = request.execute();
            networkState.postValue(NetworkState.LOADED);

            if (response == null) {
                callback.onResult(Collections.<Review>emptyList(), null, 2);
                return;
            }

            if (response.body() == null) {
                callback.onResult(Collections.<Review>emptyList(), null, 2);
                return;
            }

            callback.onResult(response.body().getReviews(), 1, 2);

        } catch (IOException e) {
            e.printStackTrace();
            networkState.postValue(NetworkState.error(e.getMessage()));
        }

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Review> callback) {
        //
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Review> callback) {
        networkState.postValue(NetworkState.LOADING);

        Call<ReviewsResponse> request = tmdbService.getReviews(movieId, params.key + 1);

        try {
            Response<ReviewsResponse> response = request.execute();
            networkState.postValue(NetworkState.LOADED);

            if (response == null) {
                callback.onResult(Collections.<Review>emptyList(), params.key + 1);
                return;
            }

            if (response.body() == null) {
                callback.onResult(Collections.<Review>emptyList(), params.key + 1);
                return;
            }

            callback.onResult(response.body().getReviews(), params.key + 1);

        } catch (IOException e) {
            e.printStackTrace();
            networkState.postValue(NetworkState.error(e.getMessage()));
        }

    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }
}
