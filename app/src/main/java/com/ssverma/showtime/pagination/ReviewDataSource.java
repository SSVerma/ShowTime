package com.ssverma.showtime.pagination;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.ssverma.showtime.api.ApiUtils;
import com.ssverma.showtime.api.TmdbService;
import com.ssverma.showtime.common.LoadingState;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.ReviewsResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ReviewDataSource extends PageKeyedDataSource<Integer, Review> {

    private final TmdbService tmdbService;
    private final MutableLiveData<LoadingState> loadingStates;
    private final int movieId;

    public ReviewDataSource(int movieId) {
        this.movieId = movieId;
        tmdbService = ApiUtils.getTmdbService();
        loadingStates = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Review> callback) {
        loadingStates.postValue(LoadingState.LOADING);

        Call<ReviewsResponse> request = tmdbService.getReviews(movieId, 1);

        try {
            Response<ReviewsResponse> response = request.execute();

            if (response == null) {
                callback.onResult(null, null, 2);
            } else {
                callback.onResult(response.body().getReviews(), 1, 2);
            }
            loadingStates.postValue(LoadingState.LOADED);

        } catch (IOException e) {
            e.printStackTrace();
            loadingStates.postValue(LoadingState.FAILED);
        }

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Review> callback) {
        //
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Review> callback) {
        loadingStates.postValue(LoadingState.LOADING);

        Call<ReviewsResponse> request = tmdbService.getReviews(movieId, params.key);

        try {
            Response<ReviewsResponse> response = request.execute();

            if (response == null) {
                callback.onResult(null, params.key + 1);
            } else {
                callback.onResult(response.body().getReviews(), params.key + 1);
            }

            loadingStates.postValue(LoadingState.LOADED);

        } catch (IOException e) {
            e.printStackTrace();
            loadingStates.postValue(LoadingState.FAILED);
        }

    }

    public MutableLiveData<LoadingState> getLoadingStates() {
        return loadingStates;
    }
}
