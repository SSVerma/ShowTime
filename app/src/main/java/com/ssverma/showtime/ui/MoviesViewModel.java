package com.ssverma.showtime.ui;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.ssverma.showtime.common.LoadingState;
import com.ssverma.showtime.common.Resource;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.VideosResponse;
import com.ssverma.showtime.repository.MoviesRepository;

public class MoviesViewModel extends ViewModel {
    private final MoviesRepository repository;
    private LiveData<Resource<VideosResponse>> videoLiveData;
    private LiveData<PagedList<Review>> reviewsLiveData;
    private MutableLiveData<Integer> movieIdLiveData = new MutableLiveData<>();

    public MoviesViewModel() {
        repository = new MoviesRepository();
        videoLiveData = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<Resource<VideosResponse>>>() {
            @Override
            public LiveData<Resource<VideosResponse>> apply(Integer movieId) {
                return repository.getVideos(movieId);
            }
        });

        reviewsLiveData = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<PagedList<Review>>>() {
            @Override
            public LiveData<PagedList<Review>> apply(Integer movieId) {
                return repository.getReviews(movieId);
            }
        });

    }

    public void setMovieId(int movieId) {
        movieIdLiveData.setValue(movieId);
    }

    public LiveData<Resource<VideosResponse>> getVideos() {
        return videoLiveData;
    }

    public LiveData<PagedList<Review>> getReviews() {
        return reviewsLiveData;
    }

    public LiveData<LoadingState> getReviewLoadingState() {
        return repository.getReviewsLoadingState();
    }

}
