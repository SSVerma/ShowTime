package com.ssverma.showtime.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.ssverma.showtime.api.ApiUtils;
import com.ssverma.showtime.api.TmdbService;
import com.ssverma.showtime.common.LoadingState;
import com.ssverma.showtime.common.Resource;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.ReviewsResponse;
import com.ssverma.showtime.model.VideosResponse;
import com.ssverma.showtime.pagination.ReviewDataSource;
import com.ssverma.showtime.pagination.ReviewDataSourceFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesRepository {
    private static final int PAGE_SIZE = 10;
    private final TmdbService tmdbService;
    private ReviewDataSourceFactory reviewDataSourceFactory;
    private LiveData<PagedList<Review>> reviews;

    public MoviesRepository() {
        tmdbService = ApiUtils.getTmdbService();
    }

    public LiveData<PagedList<Review>> getReviews(Integer movieId) {
        if (reviews != null) {
            return reviews;
        }

        reviewDataSourceFactory = new ReviewDataSourceFactory(movieId);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        reviews = new LivePagedListBuilder<Integer, Review>(reviewDataSourceFactory, config)
                .setInitialLoadKey(1)
                .build();

        return reviews;
    }

    public LiveData<LoadingState> getReviewsLoadingState() {
        return Transformations.switchMap(reviewDataSourceFactory.getDataSourceLiveData(), new Function<ReviewDataSource, LiveData<LoadingState>>() {
            @Override
            public LiveData<LoadingState> apply(ReviewDataSource reviewDataSource) {
                return reviewDataSource.getLoadingStates();
            }
        });
    }

    public LiveData<Resource<VideosResponse>> getVideos(int movieId) {
        final MutableLiveData<Resource<VideosResponse>> data = new MutableLiveData<>();

        tmdbService.getVideos(movieId).enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideosResponse> call, @NonNull Response<VideosResponse> response) {
                data.setValue(new Resource<VideosResponse>(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {
                data.setValue(new Resource<VideosResponse>(t));
            }
        });

        return data;
    }

    public LiveData<Resource<ReviewsResponse>> getReviews(int movieId, int pageNumber) {
        final MutableLiveData<Resource<ReviewsResponse>> data = new MutableLiveData<>();
        tmdbService.getReviews(movieId, pageNumber).enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewsResponse> call, @NonNull Response<ReviewsResponse> response) {
                data.setValue(new Resource<ReviewsResponse>(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<ReviewsResponse> call, @NonNull Throwable t) {
                data.setValue(new Resource<ReviewsResponse>(t));
            }
        });
        return data;
    }

}
